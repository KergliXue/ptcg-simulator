package com.ptcg.server.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ptcg.server.entity.CardMetadata;
import com.ptcg.server.mapper.CardMetadataMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CardMetadataService {

    private final CardMetadataMapper cardMetadataMapper;

    public CardMetadataService(CardMetadataMapper cardMetadataMapper) {
        this.cardMetadataMapper = cardMetadataMapper;
    }

    /** Get the canonical card metadata by cardKey. Returns the lowest-rarity print by default. */
    public CardMetadata getByCardKey(String cardKey) {
        return cardMetadataMapper.selectList(
                new LambdaQueryWrapper<CardMetadata>()
                        .eq(CardMetadata::getCardKey, cardKey)
                        .last("LIMIT 1")
        ).stream().findFirst().orElse(null);
    }

    /** Get all prints (different rarities) for a given cardKey. */
    public List<CardMetadata> getAllByCardKey(String cardKey) {
        return cardMetadataMapper.selectList(
                new LambdaQueryWrapper<CardMetadata>()
                        .eq(CardMetadata::getCardKey, cardKey)
        );
    }

    /** Search cards by name (fuzzy). */
    public List<CardMetadata> searchByName(String name) {
        return cardMetadataMapper.selectList(
                new LambdaQueryWrapper<CardMetadata>()
                        .like(CardMetadata::getName, name)
        );
    }

    /** Get all cards in a set. */
    public List<CardMetadata> getBySetCode(String setCode) {
        return cardMetadataMapper.selectList(
                new LambdaQueryWrapper<CardMetadata>()
                        .eq(CardMetadata::getSetCode, setCode)
        );
    }
}
