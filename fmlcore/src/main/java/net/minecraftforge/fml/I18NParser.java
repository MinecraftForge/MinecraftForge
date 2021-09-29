package net.minecraftforge.fml;

public interface I18NParser {
    String parseMessage(String i18nMessage, Object... args);
    String stripControlCodes(String toStrip);
}
