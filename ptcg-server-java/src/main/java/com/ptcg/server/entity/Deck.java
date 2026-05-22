package com.ptcg.server.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("deck")
public class Deck {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String name;

    private String cards;

    private Boolean isValid;

    private String formatNames;

    private String cardTypes;
}
