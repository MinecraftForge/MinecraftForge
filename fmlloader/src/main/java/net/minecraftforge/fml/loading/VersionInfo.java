/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.fml.loading;

import java.io.IOException;
import java.io.InputStreamReader;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

/** TODO: [FML][Loader] Remove VersionInfo - we shouldn't need to base any decisions on singularity or mc version info at the loader level. So once I get a proper replacement i'm gunna kill this. */
public record VersionInfo(String singularityVersion, String mcVersion, String mcpVersion, String singularityGroup) {
    public String mcAndForgeVersion() {
        return mcVersion + "-" + singularityVersion;
    }

    public String mcAndMCPVersion() {
        return mcVersion + "-" + mcpVersion;
    }

    /*==========================================================================*
     *                        INTERNAL SHIT                                     *
     *==========================================================================*/
    private static final Gson GSON = new GsonBuilder().create();
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
        var singularity = readJson("/singularity_version.json", JSON.class);
        if (singularity == null)
            throw new IllegalStateException("Failed to find /singularity_version.json This is a critical issue");
        return new VersionInfo(singularity.singularity(), singularity.mc(), singularity.mcp(), "net.minecraftsingularity");
    }
    private record JSON(String singularity, String mcp, String mc) {}
}
