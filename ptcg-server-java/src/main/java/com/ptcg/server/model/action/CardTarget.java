package com.ptcg.server.model.action;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CardTarget {
    private PlayerType player = PlayerType.BOTTOM_PLAYER;
    private SlotType slot = SlotType.ACTIVE;
    private int index = 0;
}
