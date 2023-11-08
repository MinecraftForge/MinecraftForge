/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.loading;

import java.io.IOException;
import java.io.InputStreamReader;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

/** TODO: [FML][Loader] Remove VersionInfo - we shouldn't need to base any decisions on forge or mc version info at the loader level. So once I get a proper replacement i'm gunna kill this. */
public record VersionInfo(String forgeVersion, String mcVersion, String mcpVersion, String forgeGroup) {
    public String mcAndForgeVersion() {
        return mcVersion + "-" + forgeVersion;
    }

    public String mcAndMCPVersion() {
        return mcVersion + "-" + mcpVersion;
    }

    /*==========================================================================*
     *                        INTERNAL SHIT                                     *
     *==========================================================================*/
    private static Gson GSON = new GsonBuilder().create();
    private static <T> T readJson(String path, Class<T> type) {
        try (var is = VersionInfo.class.getResourceAsStream(path)) {
            if (is == null)
                return null;

            try (var reader = new InputStreamReader(is)) {
                return GSON.fromJson(reader, type);
            }
        } catch (IOException | JsonSyntaxException e) {
            throw new IllegalStateException("Failed to parse version info from " + path , e);
        }
    }

    static VersionInfo detect() {
        var forge = readJson("/forge_version.json", JSON.class);
        if (forge == null)
            throw new IllegalStateException("Failed to find /forge_version.json This is a critical issue");
        return new VersionInfo(forge.forge(), forge.mc(), forge.mcp(), "net.minecraftforge");
    }
    private record JSON(String forge, String mcp, String mc) {}
}
