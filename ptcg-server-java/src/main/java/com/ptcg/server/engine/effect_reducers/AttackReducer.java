package com.ptcg.server.engine.effect_reducers;

import com.ptcg.server.engine.GameLogic;
import com.ptcg.server.model.card.basic.*;
import com.ptcg.server.model.effect.*;
import com.ptcg.server.model.state.*;
import com.ptcg.server.model.state.CardMarker;

public class AttackReducer {

    public static State reduce(GameLogic logic, State state, GameEffect effect) {
        if (effect instanceof UseAttackEffect useAttack) {
            Player player = useAttack.getPlayer();
            Player opponent = state.getOpponentPlayer();

            if (opponent == null) return state;

            // Check if attacker can attack (paralyzed/asleep can't)
            PokemonSlot activeSlot = player.getActive();
            if (activeSlot.getSpecialConditions().contains(SpecialCondition.PARALYZED)
                    || activeSlot.getSpecialConditions().contains(SpecialCondition.ASLEEP)) {
                logic.log(player.getName() + " cannot attack due to special condition", null);
                return state;
            }

            // Check energy cost
            Attack attack = useAttack.getAttack();

            // Check if chosen attack is sealed (Memory Skip effect)
            CardMarker sealMarker = activeSlot.getMarker("sealedAttack");
            if (sealMarker != null && sealMarker.getParam().equals(attack.getName())) {
                logic.log(player.getName() + " cannot use " + attack.getName() + " — it is sealed", null);
                return state;
            }
            if (!canPayCost(activeSlot, attack)) {
                throw new IllegalStateException("Not enough energy for attack: " + attack.getName());
            }

            // Create the attack effect
            AttackEffect attackEffect = new AttackEffect(player, opponent, attack);

            // Apply weakness/resistance
            state = logic.reduceEffect(state, attackEffect);

            // After attack: clear player-turn special conditions (paralysis, confusion)
            activeSlot.removeSpecialCondition(SpecialCondition.PARALYZED);
            activeSlot.removeSpecialCondition(SpecialCondition.CONFUSED);

            // End turn after attack
            EndTurnEffect endTurn = new EndTurnEffect(player);
            state = logic.reduceEffect(state, endTurn);
            effect.setPreventDefault(true);
        }

        if (effect instanceof AttackEffect attackEffect) {
            if (attackEffect.isPreventDefault()) return state;

            Player opponent = attackEffect.getOpponent();
            PokemonSlot targetSlot = opponent.getActive();
            Attack attack = attackEffect.getAttack();

            int damage = attackEffect.getDamage();

            // Apply weakness
            if (!attackEffect.isIgnoreWeakness()) {
                PokemonCard targetCard = targetSlot.getPokemonCard();
                if (targetCard != null) {
                    for (Weakness w : targetCard.getWeakness()) {
                        if (attackEffect.getPlayer().getActive().getPokemonCard() != null) {
                            PokemonCard attackerCard = attackEffect.getPlayer().getActive().getPokemonCard();
                            if (attackerCard.getCardTypes().contains(w.getType())) {
                                damage *= 2;
                                logic.log("Weakness applied! Damage doubled to " + damage, null);
                            }
                        }
                    }
                }
            }

            // Apply resistance
            if (!attackEffect.isIgnoreResistance()) {
                PokemonCard targetCard = targetSlot.getPokemonCard();
                if (targetCard != null) {
                    for (Resistance r : targetCard.getResistance()) {
                        damage -= r.getValue();
                        logic.log("Resistance applied! Damage reduced by " + r.getValue(), null);
                    }
                }
            }

            damage = Math.max(0, damage);
            targetSlot.setDamage(targetSlot.getDamage() + damage);
            logic.log(attackEffect.getPlayer().getName() + " attacks with " + attack.getName()
                    + " for " + damage + " damage", null);

            // Check KO
            if (targetSlot.getDamage() >= getTotalHp(targetSlot)) {
                state = CheckEffectReducer.handleKnockOut(logic, state, opponent, targetSlot);
            }

            effect.setPreventDefault(true);
        }

        return state;
    }

    private static boolean canPayCost(PokemonSlot slot, Attack attack) {
        for (CardType costType : attack.getCost()) {
            if (costType == CardType.COLORLESS) continue; // Any energy
            boolean found = slot.getEnergies().stream()
                    .anyMatch(e -> e.getProvides().contains(costType));
            if (!found) return false;
        }
        return true;
    }

    private static int getTotalHp(PokemonSlot slot) {
        PokemonCard card = slot.getPokemonCard();
        return card != null ? card.getHp() : 0;
    }
}
