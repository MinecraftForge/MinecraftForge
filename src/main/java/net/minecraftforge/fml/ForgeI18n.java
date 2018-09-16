package net.minecraftforge.fml;

import java.util.Map;

public class ForgeI18n {
    private static Map<String,String> i18n;

    static String getPattern(final String patternName) {
        return i18n.get(patternName);
    }

    public static void loadLanguageData(final Map<String, String> properties) {
        i18n = properties;
    }
}
