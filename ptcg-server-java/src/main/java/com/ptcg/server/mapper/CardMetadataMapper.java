package com.ptcg.server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ptcg.server.entity.CardMetadata;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CardMetadataMapper extends BaseMapper<CardMetadata> {
}
