/*
 * TerminalConsoleAppender
 * Copyright (c) 2017 Minecrell <https://github.com/Minecrell>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package net.minecraftforge.server.terminalconsole;

import javax.annotation.Nullable;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.apache.logging.log4j.core.pattern.ConverterKeys;
import org.apache.logging.log4j.core.pattern.LogEventPatternConverter;
import org.apache.logging.log4j.core.pattern.PatternConverter;
import org.apache.logging.log4j.core.pattern.PatternFormatter;
import org.apache.logging.log4j.core.pattern.PatternParser;
import org.apache.logging.log4j.util.PerformanceSensitive;
import org.apache.logging.log4j.util.PropertiesUtil;

import java.util.List;

/**
 * Replaces Minecraft formatting codes in the result of a pattern with
 * appropriate ANSI escape codes. The implementation will only replace valid
 * color codes using the section sign (§).
 *
 * <p>The {@link MinecraftFormattingConverter} can be only used together with
 * {@link TerminalConsoleAppender} to detect if the current console supports
 * color output. When running in an unsupported environment, it will
 * automatically strip all formatting codes instead.</p>
 *
 * <p>{@link TerminalConsoleAppender#ANSI_OVERRIDE_PROPERTY} may be used
 * to force the use of ANSI colors even in unsupported environments. As an
 * alternative, {@link #KEEP_FORMATTING_PROPERTY} may be used to keep the
 * raw Minecraft formatting codes.</p>
 *
 * <p><b>Example usage:</b> {@code %minecraftFormatting{%message}}<br>
 * It can be configured to always strip formatting codes from the message:
 * {@code %minecraftFormatting{%message}{strip}}</p>
 *
 * @see <a href="http://minecraft.gamepedia.com/Formatting_codes">
 *     Formatting Codes</a>
 */
@Plugin(name = "minecraftFormatting", category = PatternConverter.CATEGORY)
@ConverterKeys({ "minecraftFormatting" })
@PerformanceSensitive("allocation")
public class MinecraftFormattingConverter extends LogEventPatternConverter
{
    /**
     * System property that allows disabling the replacement of Minecraft
     * formatting codes entirely, keeping them in the console output. For
     * some applications they might be easier and more accurate for parsing
     * in applications like certain control panels.
     *
     * <p>If this system property is not set, or set to any value except
     * {@code true}, all Minecraft formatting codes will be replaced
     * or stripped from the console output.</p>
     */
    public static final String KEEP_FORMATTING_PROPERTY = TerminalConsoleAppender.PROPERTY_PREFIX + ".keepMinecraftFormatting";

    private static final boolean KEEP_FORMATTING = PropertiesUtil.getProperties().getBooleanProperty(KEEP_FORMATTING_PROPERTY);

    private static final String ANSI_RESET = "\u001B[39;0m";

    private static final char COLOR_CHAR = '\u00A7'; // §
    private static final String LOOKUP = "0123456789abcdefklmnor";

    private static final String[] ansiCodes = new String[] {
            "\u001B[0;30;22m", // Black §0
            "\u001B[0;34;22m", // Dark Blue §1
            "\u001B[0;32;22m", // Dark Green §2
            "\u001B[0;36;22m", // Dark Aqua §3
            "\u001B[0;31;22m", // Dark Red §4
            "\u001B[0;35;22m", // Dark Purple §5
            "\u001B[0;33;22m", // Gold §6
            "\u001B[0;37;22m", // Gray §7
            "\u001B[0;30;1m",  // Dark Gray §8
            "\u001B[0;34;1m",  // Blue §9
            "\u001B[0;32;1m",  // Green §a
            "\u001B[0;36;1m",  // Aqua §b
            "\u001B[0;31;1m",  // Red §c
            "\u001B[0;35;1m",  // Light Purple §d
            "\u001B[0;33;1m",  // Yellow §e
            "\u001B[0;37;1m",  // White §f
            "\u001B[5m",       // Obfuscated §k
            "\u001B[21m",      // Bold §l
            "\u001B[9m",       // Strikethrough §m
            "\u001B[4m",       // Underline §n
            "\u001B[3m",       // Italic §o
            ANSI_RESET,        // Reset §r
    };

    private final boolean ansi;
    private final List<PatternFormatter> formatters;

    /**
     * Construct the converter.
     *
     * @param formatters The pattern formatters to generate the text to manipulate
     * @param strip If true, the converter will strip all formatting codes
     */
    protected MinecraftFormattingConverter(List<PatternFormatter> formatters, boolean strip)
    {
        super("minecraftFormatting", null);
        this.formatters = formatters;
        this.ansi = !strip;
    }

    @Override
    public void format(LogEvent event, StringBuilder toAppendTo)
    {
        int start = toAppendTo.length();
        //noinspection ForLoopReplaceableByForEach
        for (int i = 0, size = formatters.size(); i < size; i++)
        {
            formatters.get(i).format(event, toAppendTo);
        }

        if (KEEP_FORMATTING || toAppendTo.length() == start)
        {
            // Skip replacement if disabled or if the content is empty
            return;
        }

        String content = toAppendTo.substring(start);
        format(content, toAppendTo, start, ansi && TerminalConsoleAppender.isAnsiSupported());
    }

    private static void format(String s, StringBuilder result, int start, boolean ansi)
    {
        int next = s.indexOf(COLOR_CHAR);
        int last = s.length() - 1;
        if (next == -1 || next == last)
        {
            return;
        }

        result.setLength(start + next);

        int pos = next;
        int format;
        do {
            if (pos != next)
            {
                result.append(s, pos, next);
            }

            format = LOOKUP.indexOf(s.charAt(next + 1));
            if (format != -1)
            {
                if (ansi)
                {
                    result.append(ansiCodes[format]);
                }
                pos = next += 2;
            }
            else
            {
                next++;
            }

            next = s.indexOf(COLOR_CHAR, next);
        } while (next != -1 && next < last);

        result.append(s, pos, s.length());
        if (ansi)
        {
            result.append(ANSI_RESET);
        }
    }

    /**
     * Gets a new instance of the {@link MinecraftFormattingConverter} with the
     * specified options.
     *
     * @param config The current configuration
     * @param options The pattern options
     * @return The new instance
     *
     * @see MinecraftFormattingConverter
     */
    @Nullable
    public static MinecraftFormattingConverter newInstance(Configuration config, String[] options)
    {
        if (options.length < 1 || options.length > 2)
        {
            LOGGER.error("Incorrect number of options on minecraftFormatting. Expected at least 1, max 2 received " + options.length);
            return null;
        }
        if (options[0] == null)
        {
            LOGGER.error("No pattern supplied on minecraftFormatting");
            return null;
        }

        PatternParser parser = PatternLayout.createPatternParser(config);
        List<PatternFormatter> formatters = parser.parse(options[0]);
        boolean strip = options.length > 1 && "strip".equals(options[1]);
        return new MinecraftFormattingConverter(formatters, strip);
    }

}
