package com.ptcg.server.service;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;

public class TranslationService {

    private static final String BUNDLE_BASE_NAME = "i18n.cards";
    private static final ConcurrentHashMap<Locale, ResourceBundle> BUNDLE_CACHE = new ConcurrentHashMap<>();

    /**
     * Get translated text for a given key and locale.
     * Default to English if the key is not found or translation is missing.
     */
    public static String translate(String key, Locale locale) {
        if (locale == null) {
            locale = Locale.ENGLISH;
        }
        
        // Normalize locale to only zh or en for card database
        Locale targetLocale = Locale.ENGLISH;
        if (locale.getLanguage().equalsIgnoreCase("zh")) {
            targetLocale = Locale.CHINESE;
        }

        try {
            ResourceBundle bundle = BUNDLE_CACHE.computeIfAbsent(targetLocale, loc -> 
                ResourceBundle.getBundle(BUNDLE_BASE_NAME, loc)
            );
            
            if (bundle.containsKey(key)) {
                return bundle.getString(key);
            }
        } catch (Exception e) {
            // Fallback to English
        }

        // English fallback
        try {
            ResourceBundle fallbackBundle = BUNDLE_CACHE.computeIfAbsent(Locale.ENGLISH, loc -> 
                ResourceBundle.getBundle(BUNDLE_BASE_NAME, loc)
            );
            if (fallbackBundle.containsKey(key)) {
                return fallbackBundle.getString(key);
            }
        } catch (Exception e) {
            // Return key itself if all else fails
        }
        
        return key;
    }

    public static String translate(String key, String langCode) {
        return translate(key, new Locale(langCode != null ? langCode : "en"));
    }
}
