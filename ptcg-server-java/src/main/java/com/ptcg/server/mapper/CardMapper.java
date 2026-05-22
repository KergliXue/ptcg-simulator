package com.ptcg.server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ptcg.server.entity.CardEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CardMapper extends BaseMapper<CardEntity> {
}
