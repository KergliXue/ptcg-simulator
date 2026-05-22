package com.ptcg.server.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("user")
public class User {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private String email;

    private String password;

    private int ranking = 0;

    private long registered;

    private long lastSeen;

    private long lastRankingChange;

    private String avatarFile = "";
}
