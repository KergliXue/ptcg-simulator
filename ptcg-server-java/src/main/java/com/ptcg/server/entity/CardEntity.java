package com.ptcg.server.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("card")
public class CardEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String cardKey;

    private String name;

    private String fullName;

    private String setName;

    private Integer superType;

    private String cardData;

    private String arts;
}
