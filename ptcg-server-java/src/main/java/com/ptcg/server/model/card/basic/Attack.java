package com.ptcg.server.model.card.basic;

import com.ptcg.server.service.TranslationService;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Data
public class Attack {
    private List<CardType> cost = new ArrayList<>();
    private String damage = "";
    private String name = "";
    private String text = "";

    public String getTranslatedName(String set, String cardName, Locale locale) {
        String key = "cards." + set + "." + cardName + ".attack." + name.replace(" ", "") + ".name";
        return TranslationService.translate(key, locale);
    }

    public String getTranslatedText(String set, String cardName, Locale locale) {
        String key = "cards." + set + "." + cardName + ".attack." + name.replace(" ", "") + ".text";
        return TranslationService.translate(key, locale);
    }
}
