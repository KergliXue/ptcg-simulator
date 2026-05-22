package com.ptcg.server.seed;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ptcg.server.entity.CardEntity;
import com.ptcg.server.factory.CardDefinition;
import com.ptcg.server.factory.CardRegistry;
import com.ptcg.server.mapper.CardMapper;
import com.ptcg.server.model.card.basic.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.*;

@Component
public class CardDataSeeder {

    private static final Logger log = LoggerFactory.getLogger(CardDataSeeder.class);

    private final CardMapper cardMapper;
    private final CardRegistry cardRegistry;
    private final ObjectMapper objectMapper;

    public CardDataSeeder(CardMapper cardMapper, CardRegistry cardRegistry, ObjectMapper objectMapper) {
        this.cardMapper = cardMapper;
        this.cardRegistry = cardRegistry;
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    public void seed() {
        Long count = cardMapper.selectCount(null);
        if (count != null && count > 0) {
            log.info("Card table already has {} records, skipping seed", count);
            return;
        }

        log.info("Seeding card table from registered cards");
        for (Class<? extends Card> cardClass : cardRegistry.getRegisteredClasses()) {
            CardDefinition annotation = cardClass.getAnnotation(CardDefinition.class);
            if (annotation == null) continue;

            String cardKey = !annotation.cardKey().isEmpty()
                    ? annotation.cardKey()
                    : (annotation.cardKeys().length > 0 ? annotation.cardKeys()[0] : null);
            if (cardKey == null) continue;

            try {
                Card card = cardRegistry.createCard(cardKey);
                Map<String, Object> cardData = convertCard(card);

                CardEntity entity = new CardEntity();
                entity.setCardKey(cardKey);
                entity.setName(card.getName());
                entity.setFullName(card.getFullName());
                entity.setSetName(card.getSet());
                entity.setSuperType(card.getSuperType().ordinal());
                entity.setCardData(objectMapper.writeValueAsString(cardData));

                // Serialize arts from Card instance (populated from CardMetadata DB by CardRegistry)
                entity.setArts(objectMapper.writeValueAsString(card.getArts()));

                cardMapper.insert(entity);
                log.info("Seeded card: {} (set={}, name={}, arts={})",
                        cardKey, card.getSet(), card.getName(), card.getArts().size());
            } catch (Exception e) {
                log.error("Failed to seed card: {}", cardKey, e);
            }
        }
        log.info("Card seeding complete");
    }

    private Map<String, Object> convertCard(Card card) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("fullName", card.getFullName());
        m.put("name", card.getName());
        m.put("set", card.getSet());
        m.put("setNumber", card.getSetNumber());
        m.put("superType", card.getSuperType().ordinal());
        m.put("id", card.getId());
        m.put("tags", card.getTags());

        if (card instanceof PokemonCard pokemon) {
            convertPokemon(pokemon, m);
        } else if (card instanceof TrainerCard trainer) {
            convertTrainer(trainer, m);
        } else if (card instanceof EnergyCard energy) {
            convertEnergy(energy, m);
        }
        return m;
    }

    private void convertPokemon(PokemonCard pokemon, Map<String, Object> m) {
        m.put("cardTypes", pokemon.getCardTypes().stream().map(Enum::ordinal).toList());
        m.put("evolvesFrom", pokemon.getEvolvesFrom());
        m.put("stage", pokemon.getStage().ordinal());
        m.put("retreat", pokemon.getRetreat().stream().map(Enum::ordinal).toList());
        m.put("hp", pokemon.getHp());
        m.put("weakness", pokemon.getWeakness().stream().map(w -> {
            Map<String, Object> wm = new LinkedHashMap<>();
            wm.put("type", w.getType().ordinal());
            if (w.getValue() != null) wm.put("value", w.getValue());
            return wm;
        }).toList());
        m.put("resistance", pokemon.getResistance().stream().map(r -> {
            Map<String, Object> rm = new LinkedHashMap<>();
            rm.put("type", r.getType().ordinal());
            rm.put("value", r.getValue());
            return rm;
        }).toList());
        m.put("powers", pokemon.getPowers().stream().map(p -> {
            Map<String, Object> pm = new LinkedHashMap<>();
            pm.put("name", p.getName());
            pm.put("powerType", p.getPowerType().ordinal());
            pm.put("text", p.getText());
            if (p.isUseWhenInPlay()) pm.put("useWhenInPlay", true);
            if (p.isUseFromHand()) pm.put("useFromHand", true);
            if (p.isUseFromDiscard()) pm.put("useFromDiscard", true);
            return pm;
        }).toList());
        m.put("attacks", pokemon.getAttacks().stream().map(a -> {
            Map<String, Object> am = new LinkedHashMap<>();
            am.put("cost", a.getCost().stream().map(Enum::ordinal).toList());
            am.put("damage", a.getDamage());
            am.put("name", a.getName());
            am.put("text", a.getText());
            return am;
        }).toList());
    }

    private void convertTrainer(TrainerCard trainer, Map<String, Object> m) {
        m.put("trainerType", trainer.getTrainerType().ordinal());
        m.put("text", trainer.getText());
        m.put("useWhenInPlay", trainer.isUseWhenInPlay());
    }

    private void convertEnergy(EnergyCard energy, Map<String, Object> m) {
        m.put("energyType", energy.getEnergyType().ordinal());
        m.put("provides", energy.getProvides().stream().map(Enum::ordinal).toList());
        m.put("text", energy.getText());
    }
}
