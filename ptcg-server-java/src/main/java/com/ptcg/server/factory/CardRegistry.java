package com.ptcg.server.factory;

import com.ptcg.server.entity.CardMetadata;
import com.ptcg.server.model.card.basic.Card;
import com.ptcg.server.model.card.basic.CardArt;
import com.ptcg.server.service.CardMetadataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.*;

@Component
public class CardRegistry {

    private static final Logger log = LoggerFactory.getLogger(CardRegistry.class);

    private final Map<String, Class<? extends Card>> registry = new HashMap<>();
    /** Annotation defaults per cardKey for fallback when DB metadata is missing. */
    private final Map<String, CardDefinition> annotationDefaults = new HashMap<>();
    private final CardMetadataService cardMetadataService;

    public CardRegistry(CardMetadataService cardMetadataService) {
        this.cardMetadataService = cardMetadataService;
    }

    @PostConstruct
    @SuppressWarnings("unchecked")
    public void scanCards() {
        ClassPathScanningCandidateComponentProvider scanner =
                new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AssignableTypeFilter(Card.class));

        Set<BeanDefinition> candidates = scanner.findCandidateComponents("com.ptcg.server.model.card");
        for (BeanDefinition bd : candidates) {
            try {
                Class<?> clazz = Class.forName(bd.getBeanClassName());
                if (clazz.isInterface() || java.lang.reflect.Modifier.isAbstract(clazz.getModifiers())) {
                    continue;
                }
                CardDefinition annotation = clazz.getAnnotation(CardDefinition.class);
                if (annotation != null) {
                    Class<? extends Card> cardClass = (Class<? extends Card>) clazz;
                    registerClass(cardClass, annotation);
                }
            } catch (ClassNotFoundException e) {
                log.warn("Failed to load card class: {}", bd.getBeanClassName(), e);
            }
        }
        log.info("CardRegistry initialized with {} card lookup mappings", registry.size());
    }

    private void registerClass(Class<? extends Card> cardClass, CardDefinition annotation) {
        // Collect all card keys (from cardKey + cardKeys)
        List<String> allKeys = new ArrayList<>();
        if (!annotation.cardKey().isEmpty()) {
            allKeys.add(annotation.cardKey());
        }
        allKeys.addAll(Arrays.asList(annotation.cardKeys()));

        for (String key : allKeys) {
            registry.put(key, cardClass);
            registry.put(key.toLowerCase(), cardClass);
            annotationDefaults.put(key, annotation);
            log.info("Registered card: {} -> {}", key, cardClass.getSimpleName());
        }

        // Register by simple name (from annotation) — catches frontend naming like "Psychic Energy"
        if (!annotation.name().isEmpty()) {
            registry.putIfAbsent(annotation.name(), cardClass);
            registry.putIfAbsent(annotation.name().toLowerCase(), cardClass);
            // For basic energy cards, the frontend may use "Basic X Energy" format
            if (com.ptcg.server.model.card.basic.EnergyCard.class.isAssignableFrom(cardClass)
                    && !annotation.name().startsWith("Basic ")) {
                String basicName = "Basic " + annotation.name();
                registry.putIfAbsent(basicName, cardClass);
                registry.putIfAbsent(basicName.toLowerCase(), cardClass);
            }
        }

        // Also register by name lookups from CardMetadata table
        for (String key : allKeys) {
            List<CardMetadata> metadataList = cardMetadataService.getAllByCardKey(key);
            for (CardMetadata meta : metadataList) {
                if (meta.getName() != null && !meta.getName().isEmpty()) {
                    registry.putIfAbsent(meta.getName(), cardClass);
                    registry.putIfAbsent(meta.getName().toLowerCase(), cardClass);
                }
            }
        }
    }

    public Card createCard(String cardKey) {
        if (cardKey == null) {
            throw new IllegalArgumentException("Card key cannot be null");
        }
        Class<? extends Card> clazz = resolveClass(cardKey);
        try {
            Card card = clazz.getDeclaredConstructor().newInstance();
            injectMetadata(card, cardKey, clazz);
            return card;
        } catch (Exception e) {
            throw new RuntimeException("Failed to instantiate card: " + cardKey, e);
        }
    }

    private Class<? extends Card> resolveClass(String cardKey) {
        Class<? extends Card> clazz = registry.get(cardKey);
        if (clazz == null) clazz = registry.get(cardKey.trim());
        if (clazz == null) clazz = registry.get(cardKey.trim().toLowerCase());
        // Try without spaces (e.g. "Psychic Energy" ↔ "PsychicEnergy")
        if (clazz == null && cardKey.contains(" ")) {
            String collapsed = cardKey.replace(" ", "");
            clazz = registry.get(collapsed);
            if (clazz == null) clazz = registry.get(collapsed.toLowerCase());
        }
        // Try removing "Basic " prefix (e.g. "Basic Psychic Energy" → "Psychic Energy")
        if (clazz == null) {
            String stripped = cardKey.replaceFirst("^(?i)Basic\\s+", "");
            if (!stripped.equals(cardKey)) {
                clazz = registry.get(stripped);
                if (clazz == null) clazz = registry.get(stripped.toLowerCase());
            }
        }
        // Frontend uses fullName: "{name} {set}" (e.g. "Lightning Energy BS")
        // Try stripping the trailing set abbreviation one word at a time
        if (clazz == null) {
            String key = cardKey.trim();
            int lastSpace = key.lastIndexOf(' ');
            while (lastSpace > 0 && clazz == null) {
                String prefix = key.substring(0, lastSpace);
                clazz = registry.get(prefix);
                if (clazz == null) clazz = registry.get(prefix.toLowerCase());
                lastSpace = prefix.lastIndexOf(' ');
            }
        }
        if (clazz == null) {
            throw new IllegalArgumentException("Unregistered card key: " + cardKey);
        }
        return clazz;
    }

    /**
     * Inject metadata (set, name, fullName, arts) from CardMetadata DB table,
     * falling back to annotation/class defaults.
     */
    private void injectMetadata(Card card, String cardKey, Class<? extends Card> cardClass) {
        CardMetadata meta = cardMetadataService.getByCardKey(cardKey);
        CardDefinition ann = annotationDefaults.get(cardKey);

        // If not found by the exact key, try looking up by resolve logic
        if (meta == null && ann == null) {
            String resolvedKey = resolveMetadataKey(cardKey);
            if (resolvedKey != null) {
                meta = cardMetadataService.getByCardKey(resolvedKey);
                ann = annotationDefaults.get(resolvedKey);
            }
        }

        // Last resort: use the annotation from the card class directly
        if (ann == null) {
            ann = cardClass.getAnnotation(CardDefinition.class);
        }

        if (meta != null) {
            if (meta.getSetCode() != null) card.setSet(meta.getSetCode());
            if (meta.getSetNumber() != null) card.setSetNumber(meta.getSetNumber());
            if (meta.getName() != null) card.setName(meta.getName());
            // Build fullName: "Name SET SETNUMBER"
            String fn = meta.getName();
            if (meta.getSetCode() != null) fn += " " + meta.getSetCode().toUpperCase();
            if (meta.getSetNumber() != null) fn += " " + meta.getSetNumber();
            card.setFullName(fn);

            List<CardMetadata> allPrints = cardMetadataService.getAllByCardKey(
                    meta.getCardKey() != null ? meta.getCardKey() : cardKey);
            List<CardArt> arts = new ArrayList<>();
            for (CardMetadata print : allPrints) {
                if (print.getImageUrl() != null && !print.getImageUrl().isEmpty()) {
                    arts.add(new CardArt(print.getRarity(), print.getImageUrl()));
                }
            }
            card.setArts(arts);
        } else if (ann != null) {
            // Fallback to annotation defaults
            card.setSet(ann.set());
            card.setName(ann.name());
            card.setFullName(ann.name());
            card.setArts(List.of());
        } else {
            // Absolute fallback: use class simple name
            String name = cardClass.getSimpleName();
            card.setName(name);
            card.setFullName(name);
            card.setSet("");
            card.setArts(List.of());
            log.warn("No metadata or annotation found for card key: {} (class={}), using fallback name '{}'",
                    cardKey, cardClass.getSimpleName(), name);
        }
    }

    /**
     * Try to resolve a metadata key from a fullName by stripping trailing parts.
     * e.g. "Ralts CSV2C 053" → might match registered key "CSV2C-Ralts"
     */
    private String resolveMetadataKey(String cardKey) {
        if (cardKey == null) return null;

        // Try each registered key to see if the cardKey contains it or vice versa
        for (String registeredKey : annotationDefaults.keySet()) {
            // Skip lower-case variants
            if (!registeredKey.contains("-")) continue;

            // e.g. registered "CSV2C-Ralts", cardKey "Ralts CSV2C 053"
            // Extract parts: setCode="CSV2C", name="Ralts"
            String[] parts = registeredKey.split("-", 2);
            if (parts.length == 2) {
                String setCode = parts[0];
                String name = parts[1];

                if (cardKey.contains(name) && cardKey.toUpperCase().contains(setCode.toUpperCase())) {
                    return registeredKey;
                }
            }
        }
        return null;
    }

    public boolean hasCard(String cardKey) {
        if (cardKey == null) return false;
        return registry.containsKey(cardKey)
                || registry.containsKey(cardKey.trim())
                || registry.containsKey(cardKey.trim().toLowerCase());
    }

    public int size() {
        return registry.size();
    }

    public Set<String> getAllCardKeys() {
        return registry.keySet();
    }

    public Set<Class<? extends Card>> getRegisteredClasses() {
        return new HashSet<>(registry.values());
    }
}
