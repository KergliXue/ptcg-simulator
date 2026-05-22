package com.ptcg.server.model.card.basic;

import com.ptcg.server.engine.GameLogic;
import com.ptcg.server.model.effect.GameEffect;
import com.ptcg.server.model.state.State;
import com.ptcg.server.service.TranslationService;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Data
public abstract class Card {

    private int id = -1;
    private List<String> tags = new ArrayList<>();
    private String set;
    private String setNumber;
    private String fullName;
    private String name;
    private List<CardArt> arts = new ArrayList<>();

    public abstract SuperType getSuperType();

    public String getTranslatedName(Locale locale) {
        String key = "cards." + getSet() + "." + getName() + ".name";
        return TranslationService.translate(key, locale);
    }

    public String getTranslatedName(String langCode) {
        return getTranslatedName(new Locale(langCode != null ? langCode : "en"));
    }

    public State reduceEffect(GameLogic logic, State state, GameEffect effect) {
        return state;
    }
}
