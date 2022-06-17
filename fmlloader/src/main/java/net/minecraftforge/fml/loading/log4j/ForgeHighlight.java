/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.loading.log4j;

import net.minecrell.terminalconsole.HighlightErrorConverter;
import net.minecrell.terminalconsole.TerminalConsoleAppender;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.pattern.ConverterKeys;
import org.apache.logging.log4j.core.pattern.HighlightConverter;
import org.apache.logging.log4j.core.pattern.PatternConverter;
import org.apache.logging.log4j.status.StatusLogger;
import org.apache.logging.log4j.util.PerformanceSensitive;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A wrapper for {@link HighlightConverter} that auto-disables ANSI when the terminal doesn't support it.
 * Ansi support is determined by TerminalConsoleAppender
 */
@Plugin(name = "highlightForge", category = PatternConverter.CATEGORY)
@ConverterKeys("highlightForge")
@PerformanceSensitive("allocation")
public class ForgeHighlight {
    protected static final Logger LOGGER = StatusLogger.getLogger();

    /**
     * Gets a new instance of the {@link HighlightErrorConverter} with the
     * specified options.
     *
     * @param config The current configuration
     * @param options The pattern options
     * @return The new instance
     */
    public static @Nullable HighlightConverter newInstance(Configuration config, String[] options) {
        try {
            Method method = TerminalConsoleAppender.class.getDeclaredMethod("initializeTerminal");
            method.setAccessible(true);
            method.invoke(null);
        } catch (ReflectiveOperationException e) {
            LOGGER.warn("Failed to invoke initializeTerminal on TCA", e);
        }
        if (!TerminalConsoleAppender.isAnsiSupported() && Arrays.stream(options).noneMatch(s -> s.equals("disableAnsi=true"))) {
            List<String> optionList = new ArrayList<>();
            optionList.add(options[0]);
            optionList.add("disableAnsi=true");
            options = optionList.toArray(new String[0]);
        }
        return HighlightConverter.newInstance(config, options);
    }
}
