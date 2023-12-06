/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.loading;

import net.minecraftforge.fml.loading.moddiscovery.ModFile;

import java.util.Map;

import org.apache.commons.lang3.text.StrLookup;
import org.apache.commons.lang3.text.StrSubstitutor;

@SuppressWarnings("deprecation")
public class StringSubstitutor {
    public static String replace(final String in, final ModFile file) {
        return new StrSubstitutor(getStringLookup(file)).replace(in);
    }

    private static StrLookup<String> getStringLookup(final ModFile file) {
        var globals = Map.of(
            "mcVersion", FMLLoader.versionInfo().mcVersion(),
            "forgeVersion", FMLLoader.versionInfo().forgeVersion()
        );
        return new StrLookup<String>() {
            @Override
            public String lookup(String key) {
                var parts = key.split("\\.");
                if (parts.length == 1)
                    return key;
                var pfx = parts[0];

                if ("global".equals(pfx))
                    return globals.get(parts[1]);
                else if ("file".equals(pfx) && file != null)
                    return String.valueOf(file.getSubstitutionMap().get().get(parts[1]));

                return key;
            }
        };
    }
}
