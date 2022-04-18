/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.loading;

import java.util.Map;

public record VersionInfo(String forgeVersion, String mcVersion, String mcpVersion, String forgeGroup) {
    VersionInfo(Map<String, ?> arguments) {
        this((String) arguments.get("forgeVersion"), (String) arguments.get("mcVersion"), (String) arguments.get("mcpVersion"), (String) arguments.get("forgeGroup"));
    }

    public String mcAndForgeVersion() {
        return mcVersion+"-"+forgeVersion;
    }

    public String mcAndMCPVersion() {
        return mcVersion + "-" + mcpVersion;
    }
}
