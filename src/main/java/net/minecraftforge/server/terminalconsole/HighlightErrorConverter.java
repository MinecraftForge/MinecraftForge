/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.server.terminalconsole;

import javax.annotation.Nullable;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.apache.logging.log4j.core.pattern.ConverterKeys;
import org.apache.logging.log4j.core.pattern.HighlightConverter;
import org.apache.logging.log4j.core.pattern.LogEventPatternConverter;
import org.apache.logging.log4j.core.pattern.PatternConverter;
import org.apache.logging.log4j.core.pattern.PatternFormatter;
import org.apache.logging.log4j.core.pattern.PatternParser;
import org.apache.logging.log4j.util.PerformanceSensitive;

import java.util.List;

/**
 * A simplified version of {@link HighlightConverter} that uses
 * {@link TerminalConsoleAppender} to detect if Ansi escape codes can be used
 * to highlight errors and warnings in the console.
 *
 * <p>If configured, it will mark all logged errors with a red color and all
 * warnings with a yellow color. It can be only used together with
 * {@link TerminalConsoleAppender}.</p>
 *
 * <p>{@link TerminalConsoleAppender#ANSI_OVERRIDE_PROPERTY} may be used
 * to force the use of ANSI colors even in unsupported environments.</p>
 *
 * <p><b>Example usage:</b> {@code %highlightError{%level: %message}}</p>
 */
@Plugin(name = "highlightError", category = PatternConverter.CATEGORY)
@ConverterKeys({ "highlightError" })
@PerformanceSensitive("allocation")
public class HighlightErrorConverter extends LogEventPatternConverter
{
    private static final String ANSI_RESET = "\u001B[39;0m";
    private static final String ANSI_ERROR = "\u001B[31;1m";
    private static final String ANSI_WARN = "\u001B[33;1m";

    private final List<PatternFormatter> formatters;

    /**
     * Construct the converter.
     *
     * @param formatters The pattern formatters to generate the text to highlight
     */
    protected HighlightErrorConverter(List<PatternFormatter> formatters)
    {
        super("highlightError", null);
        this.formatters = formatters;
    }

    @Override
    public void format(LogEvent event, StringBuilder toAppendTo)
    {
        if (TerminalConsoleAppender.isAnsiSupported())
        {
            Level level = event.getLevel();
            if (level.isMoreSpecificThan(Level.ERROR))
            {
                format(ANSI_ERROR, event, toAppendTo);
                return;
            }
            else if (level.isMoreSpecificThan(Level.WARN))
            {
                format(ANSI_WARN, event, toAppendTo);
                return;
            }
        }

        //noinspection ForLoopReplaceableByForEach
        for (int i = 0, size = formatters.size(); i < size; i++)
        {
            formatters.get(i).format(event, toAppendTo);
        }
    }

    private void format(String style, LogEvent event, StringBuilder toAppendTo)
    {
        int start = toAppendTo.length();
        toAppendTo.append(style);
        int end = toAppendTo.length();

        //noinspection ForLoopReplaceableByForEach
        for (int i = 0, size = formatters.size(); i < size; i++)
        {
            formatters.get(i).format(event, toAppendTo);
        }

        if (toAppendTo.length() == end)
        {
            // No content so we don't need to append the ANSI escape code
            toAppendTo.setLength(start);
        }
        else
        {
            // Append reset code after the line
            toAppendTo.append(ANSI_RESET);
        }
    }

    @Override
    public boolean handlesThrowable()
    {
        for (final PatternFormatter formatter : formatters)
        {
            if (formatter.handlesThrowable())
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Gets a new instance of the {@link HighlightErrorConverter} with the
     * specified options.
     *
     * @param config The current configuration
     * @param options The pattern options
     * @return The new instance
     */
    @Nullable
    public static HighlightErrorConverter newInstance(Configuration config, String[] options)
    {
        if (options.length != 1)
        {
            LOGGER.error("Incorrect number of options on highlightError. Expected 1 received " + options.length);
            return null;
        }
        if (options[0] == null)
        {
            LOGGER.error("No pattern supplied on highlightError");
            return null;
        }

        PatternParser parser = PatternLayout.createPatternParser(config);
        List<PatternFormatter> formatters = parser.parse(options[0]);
        return new HighlightErrorConverter(formatters);
    }

}
