package com.ptcg.server.model.state;

import com.ptcg.server.model.card.basic.*;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class PokemonSlot {

    private int damage = 0;
    private List<SpecialCondition> specialConditions = new ArrayList<>();
    private int poisonDamage = 10;
    private int burnDamage = 20;
    private int pokemonPlayedTurn = 0;

    // Stateful Markers/Debuffs (TypeScript calls this 'marker' with a 'markers' array inside)
    private List<CardMarker> marker = new ArrayList<>();

    // In-memory slot card list
    private List<PokemonCard> pokemons = new ArrayList<>();
    private List<EnergyCard> energies = new ArrayList<>();
    private List<TrainerCard> trainers = new ArrayList<>();

    public PokemonCard getPokemonCard() {
        if (pokemons.isEmpty()) {
            return null;
        }
        return pokemons.get(pokemons.size() - 1);
    }

    public List<TrainerCard> getTools() {
        return trainers.stream()
                .filter(t -> t.getTrainerType() == TrainerType.TOOL)
                .collect(Collectors.toList());
    }

    public boolean isBasic() {
        if (pokemons.size() != 1) {
            return false;
        }
        return pokemons.get(0).getStage() == Stage.BASIC;
    }

    public boolean isEvolved() {
        if (pokemons.isEmpty()) {
            return false;
        }
        return pokemons.size() > 1 
                || pokemons.get(0).getStage() == Stage.STAGE_1 
                || pokemons.get(0).getStage() == Stage.STAGE_2;
    }

    public void clearEffects() {
        specialConditions.clear();
        marker.clear();
        poisonDamage = 10;
        burnDamage = 20;
        if (pokemons.isEmpty()) {
            damage = 0;
        }
    }

    // Move helpers

    public void moveTo(CardList destination) {
        destination.getCards().addAll(pokemons);
        destination.getCards().addAll(energies);
        destination.getCards().addAll(trainers);
        pokemons.clear();
        energies.clear();
        trainers.clear();
    }

    public void moveToTop(CardList destination) {
        destination.getCards().addAll(0, trainers);
        destination.getCards().addAll(0, energies);
        destination.getCards().addAll(0, pokemons);
        pokemons.clear();
        energies.clear();
        trainers.clear();
    }

    public void moveCardTo(Card card, CardList destination) {
        if (pokemons.remove(card)) {
            destination.add(card);
            return;
        }
        if (energies.remove(card)) {
            destination.add(card);
            return;
        }
        if (trainers.remove(card)) {
            destination.add(card);
        }
    }

    // =========================================================================
    // MARKERS/DEBUFFS MANAGEMENT
    // =========================================================================

    public boolean hasMarker(String markerName) {
        return marker.stream().anyMatch(m -> m.getName().equals(markerName));
    }

    public CardMarker getMarker(String markerName) {
        return marker.stream()
                .filter(m -> m.getName().equals(markerName))
                .findFirst()
                .orElse(null);
    }

    public void addMarker(CardMarker newMarker) {
        CardMarker existing = getMarker(newMarker.getName());
        if (existing != null) {
            existing.setDuration(newMarker.getDuration());
            existing.setParam(newMarker.getParam());
            existing.setSource(newMarker.getSource());
        } else {
            marker.add(newMarker);
        }
    }

    public void removeMarker(String markerName) {
        marker.removeIf(m -> m.getName().equals(markerName));
    }

    // =========================================================================
    // SPECIAL CONDITIONS
    // =========================================================================

    public void addSpecialCondition(SpecialCondition sp) {
        if (sp == SpecialCondition.POISONED) {
            poisonDamage = 10;
        }
        if (sp == SpecialCondition.BURNED) {
            burnDamage = 20;
        }
        if (specialConditions.contains(sp)) {
            return;
        }
        if (sp == SpecialCondition.POISONED || sp == SpecialCondition.BURNED) {
            specialConditions.add(sp);
            return;
        }
        specialConditions.removeIf(s -> s == SpecialCondition.PARALYZED 
                || s == SpecialCondition.CONFUSED 
                || s == SpecialCondition.ASLEEP);
        specialConditions.add(sp);
    }

    public void removeSpecialCondition(SpecialCondition sp) {
        specialConditions.remove(sp);
    }
}
