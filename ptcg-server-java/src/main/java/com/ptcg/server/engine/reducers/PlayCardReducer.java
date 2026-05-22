package com.ptcg.server.engine.reducers;

import com.ptcg.server.engine.GameLogic;
import com.ptcg.server.engine.effect_reducers.PlayEnergyReducer;
import com.ptcg.server.engine.effect_reducers.PlayPokemonReducer;
import com.ptcg.server.engine.effect_reducers.PlayTrainerReducer;
import com.ptcg.server.model.action.*;
import com.ptcg.server.model.card.basic.*;
import com.ptcg.server.model.effect.*;
import com.ptcg.server.model.state.*;

public class PlayCardReducer {

    public static State reduce(GameLogic logic, State state, GameAction action) {
        if (state.getPhase() != GamePhase.PLAYER_TURN) {
            return state;
        }

        if (action instanceof PlayCardAction playAction) {
            Player player = state.getPlayers().get(state.getActivePlayerIndex());
            if (player == null || player.getId() != playAction.getPlayerId()) {
                throw new IllegalStateException("Not your turn");
            }

            Card handCard = player.getHand().getCards().get(playAction.getHandIndex());
            if (handCard == null) {
                throw new IllegalStateException("Unknown card");
            }

            CardTarget target = playAction.getTarget();
            PokemonSlot slot = findPokemonSlot(state, target);

            if (handCard instanceof EnergyCard energyCard) {
                if (slot == null || slot.getPokemons().isEmpty()) {
                    throw new IllegalStateException("Invalid target");
                }
                if (player.getEnergyPlayedTurn() == state.getTurn()) {
                    throw new IllegalStateException("Energy already attached this turn");
                }
                player.setEnergyPlayedTurn(state.getTurn());
                AttachEnergyEffect effect = new AttachEnergyEffect(player, energyCard, slot);
                return logic.reduceEffect(state, effect);
            }

            if (handCard instanceof PokemonCard pokemonCard) {
                if (slot == null) {
                    throw new IllegalStateException("Invalid target");
                }
                PlayPokemonEffect effect = new PlayPokemonEffect(player, pokemonCard, slot);
                return logic.reduceEffect(state, effect);
            }

            if (handCard instanceof TrainerCard trainerCard) {
                GameEffect effect;
                switch (trainerCard.getTrainerType()) {
                    case SUPPORTER -> {
                        if (player.getSupporter().getCards().size() > 0) {
                            throw new IllegalStateException("Supporter already played this turn");
                        }
                        effect = new PlaySupporterEffect(player, trainerCard, slot);
                    }
                    case STADIUM -> {
                        if (player.getStadiumPlayedTurn() == state.getTurn()) {
                            throw new IllegalStateException("Stadium already played this turn");
                        }
                        player.setStadiumPlayedTurn(state.getTurn());
                        effect = new PlayStadiumEffect(player, trainerCard);
                    }
                    case TOOL -> {
                        if (slot == null) {
                            throw new IllegalStateException("Invalid target for tool");
                        }
                        effect = new AttachPokemonToolEffect(player, trainerCard, slot);
                    }
                    default -> effect = new PlayItemEffect(player, trainerCard, slot);
                }
                return logic.reduceEffect(state, effect);
            }
        }
        return state;
    }

    private static PokemonSlot findPokemonSlot(State state, CardTarget target) {
        int activeIdx = state.getActivePlayerIndex();
        Player player = target.getPlayer() == PlayerType.BOTTOM_PLAYER
                ? state.getPlayers().get(activeIdx)
                : state.getPlayers().get(activeIdx == 0 ? 1 : 0);

        if (target.getSlot() == SlotType.ACTIVE) {
            return player.getActive();
        }
        if (target.getSlot() == SlotType.BENCH) {
            return player.getBench().get(target.getIndex());
        }
        return null;
    }
}
