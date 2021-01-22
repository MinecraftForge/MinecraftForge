/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
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

package net.minecraftforge.fml;

import com.google.common.base.CharMatcher;
import net.minecraftforge.fml.loading.StringUtils;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;
import org.apache.commons.lang3.text.ExtendedMessageFormat;
import org.apache.commons.lang3.text.FormatFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.regex.Pattern;

import static net.minecraftforge.fml.Logging.CORE;

//TODO, this should be re-evaluated now that ITextComponents are passed everywhere instaed of strings.
public class ForgeI18n {
    private static final Logger LOGGER = LogManager.getLogger();
    // From FontRenderer.renderCharAtPos
    private static final String ALLOWED_CHARS = "\u00c0\u00c1\u00c2\u00c8\u00ca\u00cb\u00cd\u00d3\u00d4\u00d5\u00da\u00df\u00e3\u00f5\u011f\u0130\u0131\u0152\u0153\u015e\u015f\u0174\u0175\u017e\u0207\u0000\u0000\u0000\u0000\u0000\u0000\u0000 !\"#$%&\'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u0000\u00c7\u00fc\u00e9\u00e2\u00e4\u00e0\u00e5\u00e7\u00ea\u00eb\u00e8\u00ef\u00ee\u00ec\u00c4\u00c5\u00c9\u00e6\u00c6\u00f4\u00f6\u00f2\u00fb\u00f9\u00ff\u00d6\u00dc\u00f8\u00a3\u00d8\u00d7\u0192\u00e1\u00ed\u00f3\u00fa\u00f1\u00d1\u00aa\u00ba\u00bf\u00ae\u00ac\u00bd\u00bc\u00a1\u00ab\u00bb\u2591\u2592\u2593\u2502\u2524\u2561\u2562\u2556\u2555\u2563\u2551\u2557\u255d\u255c\u255b\u2510\u2514\u2534\u252c\u251c\u2500\u253c\u255e\u255f\u255a\u2554\u2569\u2566\u2560\u2550\u256c\u2567\u2568\u2564\u2565\u2559\u2558\u2552\u2553\u256b\u256a\u2518\u250c\u2588\u2584\u258c\u2590\u2580\u03b1\u03b2\u0393\u03c0\u03a3\u03c3\u03bc\u03c4\u03a6\u0398\u03a9\u03b4\u221e\u2205\u2208\u2229\u2261\u00b1\u2265\u2264\u2320\u2321\u00f7\u2248\u00b0\u2219\u00b7\u221a\u207f\u00b2\u25a0\u0000";
    private static final CharMatcher DISALLOWED_CHAR_MATCHER = CharMatcher.anyOf(ALLOWED_CHARS).negate();
    private static Map<String,String> i18n;
    private static Map<String,FormatFactory> customFactories;
    // From StringUtils
    private static final Pattern PATTERN_CONTROL_CODE = Pattern.compile("(?i)\\u00A7[0-9A-FK-OR]");



    static {
        customFactories = new HashMap<>();
        // {0,modinfo,id} -> modid from ModInfo object; {0,modinfo,name} -> displayname from ModInfo object
        customFactories.put("modinfo", (name, formatString, locale) -> new CustomReadOnlyFormat((stringBuffer, objectToParse) -> parseModInfo(formatString, stringBuffer, objectToParse)));
        // {0,lower} -> lowercase supplied string
        customFactories.put("lower", (name, formatString, locale) -> new CustomReadOnlyFormat((stringBuffer, objectToParse) -> stringBuffer.append(StringUtils.toLowerCase(String.valueOf(objectToParse)))));
        // {0,upper> -> uppercase supplied string
        customFactories.put("upper", (name, formatString, locale) -> new CustomReadOnlyFormat((stringBuffer, objectToParse) -> stringBuffer.append(StringUtils.toUpperCase(String.valueOf(objectToParse)))));
        // {0,exc,cls} -> class of exception; {0,exc,msg} -> message from exception
        customFactories.put("exc", (name, formatString, locale) -> new CustomReadOnlyFormat((stringBuffer, objectToParse) -> parseException(formatString, stringBuffer, objectToParse)));
        // {0,vr} -> transform VersionRange into cleartext string using fml.messages.version.restriction.* strings
        customFactories.put("vr", (name, formatString, locale) -> new CustomReadOnlyFormat(((stringBuffer, o) -> MavenVersionStringHelper.parseVersionRange(formatString, stringBuffer, o))));
        // {0,i18n,fml.message} -> pass object to i18n string 'fml.message'
        customFactories.put("i18n", (name, formatString, locale) -> new CustomReadOnlyFormat((stringBuffer, o) -> stringBuffer.append(ForgeI18n.parseMessage(formatString, o))));
        // {0,ornull,fml.absent} -> append String value of o, or i18n string 'fml.absent' (message format transforms nulls into the string literal "null")
        customFactories.put("ornull", ((name, formatString, locale) -> new CustomReadOnlyFormat((stringBuffer, o) -> stringBuffer.append(Objects.equals(String.valueOf(o),"null") ? ForgeI18n.parseMessage(formatString) : String.valueOf(o)))));
    }

    private static void parseException(final String formatString, final StringBuffer stringBuffer, final Object objectToParse) {
        Throwable t = (Throwable) objectToParse;
        if (Objects.equals(formatString, "msg")) {
            stringBuffer.append(t.getClass().getName()).append(": ").append(t.getMessage());
        } else if (Objects.equals(formatString, "cls")) {
            stringBuffer.append(t.getClass().getName());
        }
    }

    private static void parseModInfo(final String formatString, final StringBuffer stringBuffer, final Object modInfo) {
        final ModInfo info = (ModInfo) modInfo;
        if (Objects.equals(formatString, "id")) {
            stringBuffer.append(info.getModId());
        } else if (Objects.equals(formatString, "name")) {
            stringBuffer.append(info.getDisplayName());
        }
    }

    public static String getPattern(final String patternName) {
        return i18n == null ? patternName : i18n.getOrDefault(patternName, patternName);
    }

    public static void loadLanguageData(final Map<String, String> properties) {
        LOGGER.debug(CORE,"Loading I18N data entries: {}", properties.size());
        i18n = properties;
    }

    public static String parseMessage(final String i18nMessage, Object... args) {
        final String pattern = getPattern(i18nMessage);
        try {
            return parseFormat(pattern, args);
        } catch (IllegalArgumentException e) {
            LOGGER.error(CORE,"Illegal format found `{}`", pattern);
            return pattern;
        }
    }

    public static String parseFormat(final String format, final Object... args) {
        final ExtendedMessageFormat extendedMessageFormat = new ExtendedMessageFormat(format, customFactories);
        return extendedMessageFormat.format(args);
    }

    public static String stripSpecialChars(String message)
    {
        // We can't handle many unicode points in the splash renderer
        return DISALLOWED_CHAR_MATCHER.removeFrom(stripControlCodes(message));
    }

    public static String stripControlCodes(String text) {
        return PATTERN_CONTROL_CODE.matcher(text).replaceAll("");
    }

    public static class CustomReadOnlyFormat extends Format {
        private final BiConsumer<StringBuffer, Object> formatter;

        CustomReadOnlyFormat(final BiConsumer<StringBuffer, Object> formatter) {
            this.formatter = formatter;
        }

        @Override
        public StringBuffer format(final Object obj, final StringBuffer toAppendTo, final FieldPosition pos) {
            formatter.accept(toAppendTo, obj);
            return toAppendTo;
        }

        @Override
        public Object parseObject(final String source, final ParsePosition pos) {
            throw new UnsupportedOperationException("Parsing is not supported");
        }
    }
}
