package com.ptcg.server.engine;

import com.ptcg.server.model.card.basic.Card;
import com.ptcg.server.model.card.basic.PokemonCard;
import com.ptcg.server.model.card.basic.Stage;
import com.ptcg.server.model.state.GamePhase;
import com.ptcg.server.model.state.Player;
import com.ptcg.server.model.state.State;
import java.util.List;

public class GameEngine {

    /**
     * 执行 PTCG 标准的开局准备阶段 (Setup Phase)
     */
    public void executeSetupPhase(State state) {
        state.setPhase(GamePhase.SETUP);

        for (Player player : state.getPlayers()) {
            // 1. 洗牌
            player.shuffleDeck();
            
            // 2. 抽起始手牌 7 张
            drawInitialHandWithMulligan(player);
            
            // 3. 抽取 6 张奖赏卡 (Prize Cards)
            setupPrizeCards(player);
        }

        // 此时系统将进入异步等待：等待两位玩家从手牌中选择并放置“出战宝可梦”和“备战宝可梦”
        // 玩家放置完毕后，卡牌翻开，进入第一个 PLAYER_TURN！
    }

    /**
     * 抽7张牌，并处理“调度 (Mulligan)”逻辑（如果没有基础宝可梦必须重抽）
     */
    private void drawInitialHandWithMulligan(Player player) {
        boolean hasBasicPokemon = false;
        
        while (!hasBasicPokemon) {
            // 抽 7 张
            player.drawCards(7);
            
            // 检查是否有 Basic (基础) 宝可梦
            hasBasicPokemon = player.getHand().getCards().stream()
                .anyMatch(c -> c instanceof PokemonCard && ((PokemonCard) c).getStage() == Stage.BASIC);
            
            if (!hasBasicPokemon) {
                System.out.println("玩家 " + player.getName() + " 没有基础宝可梦！发生调度 (Mulligan)！");
                
                // 将手牌全部洗回牌库，准备重抽
                List<Card> currentHand = List.copyOf(player.getHand().getCards());
                for (Card c : currentHand) {
                    player.getHand().remove(c);
                    player.getDeck().add(c);
                }
                player.shuffleDeck();
            }
        }
    }

    /**
     * 将牌库顶部的 6 张卡放置为奖赏卡 (Prize Cards)
     */
    private void setupPrizeCards(Player player) {
        // PTCG 标准对局：6张奖赏卡
        for (int i = 0; i < 6; i++) {
            List<Card> drawn = player.getDeck().draw(1);
            if (!drawn.isEmpty()) {
                // 将抽到的 1 张卡放入第 i 个奖赏卡堆
                player.getPrizes().get(i).add(drawn.get(0));
            }
        }
    }
    
    /**
     * 回合开始时的抽卡逻辑
     */
    public void startTurn(State state, Player activePlayer) {
        state.setTurn(state.getTurn() + 1);
        
        // 回合开始时必须抽 1 张卡
        if (activePlayer.getDeck().getCards().isEmpty()) {
            // 如果牌库没牌了，判定该玩家输掉比赛 (Deck Out)
            System.out.println("玩家 " + activePlayer.getName() + " 牌库已空，无牌可抽，输掉对局！");
            state.setPhase(GamePhase.FINISHED);
            return;
        }
        
        activePlayer.drawCards(1);
        state.setPhase(GamePhase.PLAYER_TURN);
    }
}
