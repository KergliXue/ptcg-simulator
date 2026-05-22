package com.ptcg.server.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("card_metadata")
public class CardMetadata {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** Unique card key that maps to a Java card class, e.g. "CSV8C-Munkidori" */
    private String cardKey;

    /** Full display name, e.g. "Munkidori" */
    private String name;

    /** Set identifier, e.g. "csv8c" */
    private String setCode;

    /** Set number, e.g. "094" */
    private String setNumber;

    /** Rarity of this specific print, e.g. "R", "RR", "SR", "UR" */
    private String rarity;

    /** Card image URL */
    private String imageUrl;

    /** Full card text / description */
    private String description;

    /** Optional flavor text */
    private String flavorText;

    /** Illustrator credit */
    private String illustrator;
}
