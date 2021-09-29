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

package net.minecraftforge.fml.loading;

import org.apache.commons.lang3.text.StrSubstitutor;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;
import java.util.Map;

public class StringUtils {
    public static String toLowerCase(final String str) {
        return str.toLowerCase(Locale.ROOT);
    }

    public static String toUpperCase(final String str) {
        return str.toUpperCase(Locale.ROOT);
    }

    public static boolean endsWith(final String search, final String... endings) {
        String lowerSearch = toLowerCase(search);
        return java.util.stream.Stream.of(endings).anyMatch(lowerSearch::endsWith);
    }

    public static URL toURL(final String string) {
        if (string == null || string.trim().isEmpty() || string.contains("myurl.me") || string.contains("example.invalid"))
            return null;

        try {
            return new URL(string); }
        catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public static String parseStringFormat(final String input, final Map<String, String> properties) {
        return StrSubstitutor.replace(input, properties);
    }

    public static String binToHex(final byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            sb.append(Integer.toHexString((bytes[i]&0xf0) >>4));
            sb.append(Integer.toHexString(bytes[i]&0x0f));
        }
        return sb.toString();
    }
}
