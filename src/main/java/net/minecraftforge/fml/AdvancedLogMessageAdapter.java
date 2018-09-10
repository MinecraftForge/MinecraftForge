package net.minecraftforge.fml;

import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.util.StringBuilderFormattable;

import java.util.function.Consumer;

public class AdvancedLogMessageAdapter implements Message, StringBuilderFormattable {
    private static final Object[] EMPTY = new Object[0];

    private final Consumer<StringBuilder> logMessageBuilder;

    public AdvancedLogMessageAdapter(final Consumer<StringBuilder> logMessageBuilder) {
        this.logMessageBuilder = logMessageBuilder;
    }

    @Override
    public String getFormattedMessage() {
        return "";
    }

    @Override
    public String getFormat() {
        return "";
    }

    @Override
    public Object[] getParameters() {
        return EMPTY;
    }

    @Override
    public Throwable getThrowable() {
        return null;
    }

    @Override
    public void formatTo(final StringBuilder buffer) {
        logMessageBuilder.accept(buffer);
    }
}
