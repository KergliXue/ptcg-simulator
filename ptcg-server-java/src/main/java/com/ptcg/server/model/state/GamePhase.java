package com.ptcg.server.model.state;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.NUMBER)
public enum GamePhase {
    WAITING_FOR_PLAYERS, // 等待玩家加入
    SETUP,               // 准备阶段（抽7张牌、放置基础宝可梦、放奖赏卡）
    PLAYER_TURN,         // 玩家回合（主要阶段）
    ATTACK,              // 攻击结算阶段
    BETWEEN_TURNS,       // 回合交替阶段（结算中毒、灼伤、气绝）
    FINISHED             // 游戏结束
}
