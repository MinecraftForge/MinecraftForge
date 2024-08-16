/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.internal;

import java.util.Collections;
import java.util.List;
import java.util.function.ObjIntConsumer;

import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.common.ForgeI18n;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.versions.forge.ForgeVersion;
import net.minecraftforge.versions.mcp.MCPVersion;

public final class BrandingControl {
    private BrandingControl() {}

    private static List<String> brandings;
    private static List<String> brandingsNoMC;

    private static void computeBranding() {
        if (brandings == null) {
            brandings = List.of(
                    "Forge " + ForgeVersion.getVersion(),
                    "Minecraft " + MCPVersion.getMCVersion(),
                    "MCP " + MCPVersion.getMCPVersion(),
                    ForgeI18n.parseMessage("fml.menu.loadingmods", ModList.get().size())
            );
            brandingsNoMC = brandings.subList(1, brandings.size());
        }
    }

    private static List<String> getBrandings(boolean includeMC, boolean reverse) {
        computeBranding();
        if (includeMC)
            return reverse ? brandings.reversed() : brandings;
        else
            return reverse ? brandingsNoMC.reversed() : brandingsNoMC;
    }

    public static List<String> getOverCopyrightBrandings() {
        final class LazyInit {
            private static final List<String> INSTANCE = ForgeHooksClient.forgeStatusLine == null
                    ? Collections.emptyList()
                    : List.of(ForgeHooksClient.forgeStatusLine);

            private LazyInit() {}
        }

        return LazyInit.INSTANCE;
    }

    public static void forEachLine(boolean includeMC, boolean reverse, ObjIntConsumer<String> lineConsumer) {
        var brandings = getBrandings(includeMC, reverse);
        for (int idx = 0; idx < brandings.size(); idx++)
            lineConsumer.accept(brandings.get(idx), idx);
    }

    public static void forEachAboveCopyrightLine(ObjIntConsumer<String> lineConsumer) {
        var overCopyrightBrandings = getOverCopyrightBrandings();
        for (int idx = 0; idx < overCopyrightBrandings.size(); idx++)
            lineConsumer.accept(overCopyrightBrandings.get(idx), idx);
    }

    public static String getBranding() {
        return "forge";
    }

    public static ResourceManagerReloadListener resourceManagerReloadListener() {
        return BrandingControl::onResourceManagerReload;
    }

    private static void onResourceManagerReload(ResourceManager resourceManager) {
        brandings = null;
        brandingsNoMC = null;
    }
}
