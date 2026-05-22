package com.ptcg.server.model.card.basic;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CardArt {
    private String rarity;
    private String imageUrl;
}
