package com.ptcg.server.model.card.basic;

import lombok.Data;

@Data
public class Weakness {
    private CardType type;
    private Integer value; // when null, then it's x2
}
