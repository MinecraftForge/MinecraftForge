package net.minecraftforge.fml.loading;

import java.util.Arrays;
import java.util.List;

/**
 * Thrown during early loading phase, and collected by the LoadingModList for handoff to the client
 * or server.
 */
public class EarlyLoadingException extends RuntimeException {
    private final String i18nMessage;
    private final List<Object> context;

    public EarlyLoadingException(final String message, final String i18nMessage, final Throwable originalException, Object... context) {
        super(message, originalException);
        this.i18nMessage = i18nMessage;
        this.context = Arrays.asList(context);
    }

    public String getI18NMessage() {
        return this.i18nMessage;
    }

    public List<Object> getContext() {
        return this.context;
    }
}
