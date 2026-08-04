/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.loading;

import org.apache.commons.lang3.text.StrSubstitutor;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;
import java.util.Map;

public class StringUtils
{
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
        try
        {
            return new URL(string);
        }
        catch (MalformedURLException e)
        {
            throw new RuntimeException(e);
        }
    }

    public static String parseStringFormat(final String input, final Map<String, String> properties) {
        return StrSubstitutor.replace(input, properties);
    }
}
