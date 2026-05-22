package com.ptcg.server.model.card.basic;

import com.ptcg.server.service.TranslationService;
import lombok.Data;
import java.util.Locale;

@Data
public class Power {
    private String name = "";
    private PowerType powerType;
    private String text = "";
    private boolean useWhenInPlay = false;
    private boolean useFromHand = false;
    private boolean useFromDiscard = false;

    public String getTranslatedName(String set, String cardName, Locale locale) {
        String key = "cards." + set + "." + cardName + ".power." + name.replace(" ", "") + ".name";
        return TranslationService.translate(key, locale);
    }

    public String getTranslatedText(String set, String cardName, Locale locale) {
        String key = "cards." + set + "." + cardName + ".power." + name.replace(" ", "") + ".text";
        return TranslationService.translate(key, locale);
    }
}
