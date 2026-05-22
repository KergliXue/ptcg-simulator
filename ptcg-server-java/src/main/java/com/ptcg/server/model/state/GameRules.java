package com.ptcg.server.model.state;

import lombok.Data;

@Data
public class GameRules {
    private String formatName = "";
    private boolean firstTurnDrawCard = true;
    private boolean firstTurnUseSupporter = true;
    private boolean noPrizeForFossil = true;
}
