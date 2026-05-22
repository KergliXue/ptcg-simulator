package com.ptcg.server.engine.reducers;

import com.ptcg.server.engine.GameLogic;
import com.ptcg.server.model.action.*;
import com.ptcg.server.model.card.basic.Attack;
import com.ptcg.server.model.card.basic.PokemonCard;
import com.ptcg.server.model.card.basic.Power;
import com.ptcg.server.model.card.basic.TrainerCard;
import com.ptcg.server.model.effect.*;
import com.ptcg.server.model.state.*;

public class PlayerTurnReducer {

    public static State reduce(GameLogic logic, State state, GameAction action) {
        if (state.getPhase() != GamePhase.PLAYER_TURN) {
            return state;
        }

        if (action instanceof PassTurnAction pass) {
            Player player = state.getPlayers().get(state.getActivePlayerIndex());
            if (player == null || player.getId() != pass.getPlayerId()) {
                throw new IllegalStateException("Not your turn");
            }
            EndTurnEffect effect = new EndTurnEffect(player);
            return logic.reduceEffect(state, effect);
        }

        if (action instanceof RetreatAction retreat) {
            Player player = state.getPlayers().get(state.getActivePlayerIndex());
            if (player == null || player.getId() != retreat.getPlayerId()) {
                throw new IllegalStateException("Not your turn");
            }
            RetreatEffect effect = new RetreatEffect(player, retreat.getBenchIndex());
            return logic.reduceEffect(state, effect);
        }

        if (action instanceof AttackAction attackAction) {
            Player player = state.getPlayers().get(state.getActivePlayerIndex());
            if (player == null || player.getId() != attackAction.getPlayerId()) {
                throw new IllegalStateException("Not your turn");
            }

            PokemonCard pokemonCard = player.getActive().getPokemonCard();
            if (pokemonCard == null) {
                throw new IllegalStateException("No active Pokemon");
            }

            Attack attack = pokemonCard.getAttacks().stream()
                    .filter(a -> a.getName().equals(attackAction.getAttackName()))
                    .findFirst().orElse(null);
            if (attack == null) {
                throw new IllegalStateException("Unknown attack: " + attackAction.getAttackName());
            }

            UseAttackEffect effect = new UseAttackEffect(player, attack);
            return logic.reduceEffect(state, effect);
        }

        if (action instanceof UseAbilityAction abilityAction) {
            Player player = state.getPlayers().get(state.getActivePlayerIndex());
            if (player == null || player.getId() != abilityAction.getPlayerId()) {
                throw new IllegalStateException("Not your turn");
            }

            CardTarget target = abilityAction.getTarget();
            PokemonCard pokemonCard = null;
            PokemonSlot slot = null;

            if (target.getSlot() == SlotType.ACTIVE) {
                slot = player.getActive();
                pokemonCard = slot.getPokemonCard();
            } else if (target.getSlot() == SlotType.BENCH) {
                slot = player.getBench().get(target.getIndex());
                pokemonCard = slot.getPokemonCard();
            }

            if (pokemonCard == null) {
                throw new IllegalStateException("No Pokemon at target for ability");
            }

            Power power = pokemonCard.getPowers().stream()
                    .filter(p -> p.getName().equals(abilityAction.getAbilityName()))
                    .findFirst().orElse(null);
            if (power == null) {
                throw new IllegalStateException("Unknown power: " + abilityAction.getAbilityName());
            }
            if (!power.isUseWhenInPlay()) {
                throw new IllegalStateException("This ability cannot be used");
            }

            UsePowerEffect effect = new UsePowerEffect(player, power, pokemonCard);
            return logic.reduceEffect(state, effect);
        }

        return state;
    }
}
