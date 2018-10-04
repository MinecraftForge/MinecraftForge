/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
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

import net.minecraftforge.fml.loading.StringUtils;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;
import org.apache.commons.lang3.text.ExtendedMessageFormat;
import org.apache.commons.lang3.text.FormatFactory;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;

public class ForgeI18n {
    private static Map<String,String> i18n;
    private static Map<String,FormatFactory> customFactories;

    static {
        customFactories = new HashMap<>();
        customFactories.put("modinfo", (name, formatString, locale) -> new CustomReadOnlyFormat((stringBuffer, objectToParse) -> parseModInfo(formatString, stringBuffer, objectToParse)));
        customFactories.put("lower", (name, formatString, locale) -> new CustomReadOnlyFormat((stringBuffer, objectToParse) -> stringBuffer.append(StringUtils.toLowerCase((String)objectToParse))));
        customFactories.put("upper", (name, formatString, locale) -> new CustomReadOnlyFormat((stringBuffer, objectToParse) -> stringBuffer.append(StringUtils.toUpperCase((String)objectToParse))));
        customFactories.put("exc", (name, formatString, locale) -> new CustomReadOnlyFormat((stringBuffer, objectToParse) -> parseException(formatString, stringBuffer, objectToParse)));
        customFactories.put("vr", (name, formatString, locale) -> new CustomReadOnlyFormat(((stringBuffer, o) -> MavenVersionStringHelper.parseVersionRange(formatString, stringBuffer, o))));
    }

    private static void parseException(final String formatString, final StringBuffer stringBuffer, final Object objectToParse) {
        Throwable t = (Throwable) objectToParse;
        if (Objects.equals(formatString, "msg")) {
            stringBuffer.append(t.getMessage());
        } else if (Objects.equals(formatString, "cls")) {
            stringBuffer.append(t.getClass());
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
        return i18n.getOrDefault(patternName, patternName);
    }

    public static void loadLanguageData(final Map<String, String> properties) {
        i18n = properties;
    }

    public static String parseMessage(final String i18nMessage, Object... args) {
        final String pattern = getPattern(i18nMessage);
        return parseFormat(pattern, args);
    }

    public static String parseFormat(final String format, final Object... args) {
        final ExtendedMessageFormat extendedMessageFormat = new ExtendedMessageFormat(format, customFactories);
        return extendedMessageFormat.format(args);
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