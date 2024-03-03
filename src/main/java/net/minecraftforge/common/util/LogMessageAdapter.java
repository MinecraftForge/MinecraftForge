/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.util;

import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.util.StringBuilderFormattable;

import java.util.function.Consumer;

public record LogMessageAdapter(Consumer<StringBuilder> builder) implements Message, StringBuilderFormattable {
    private static final Object[] EMPTY = new Object[0];
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
        builder.accept(buffer);
    }

    public static Message adapt(final Consumer<StringBuilder> toConsume) {
        return new LogMessageAdapter(toConsume);
    }
}
