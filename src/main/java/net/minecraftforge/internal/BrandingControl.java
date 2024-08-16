/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.internal;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.ObjIntConsumer;
import java.util.stream.IntStream;

import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.common.ForgeI18n;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.versions.forge.ForgeVersion;
import net.minecraftforge.versions.mcp.MCPVersion;

public class BrandingControl {
    private static List<String> brandings;
    private static List<String> brandingsNoMC;
    private static List<String> overCopyrightBrandings;

    private static void computeBranding() {
        if (brandings == null) {
            ImmutableList.Builder<String> brd = ImmutableList.builder();
            brd.add("Forge " + ForgeVersion.getVersion());
            brd.add("Minecraft " + MCPVersion.getMCVersion());
            brd.add("MCP " + MCPVersion.getMCPVersion());
            int tModCount = ModList.get().size();
            brd.add(ForgeI18n.parseMessage("fml.menu.loadingmods", tModCount));
            brandings = brd.build();
            brandingsNoMC = brandings.subList(1, brandings.size());
        }
    }

    private static List<String> getBrandings(boolean includeMC, boolean reverse) {
        computeBranding();
        if (includeMC)
            return reverse ? Lists.reverse(brandings) : brandings;
        else
            return reverse ? Lists.reverse(brandingsNoMC) : brandingsNoMC;
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
        final List<String> brandings = getBrandings(includeMC, reverse);
        for (int idx = 0; idx < brandings.size(); idx++)
            lineConsumer.accept(brandings.get(idx), idx);
    }

    public static void forEachAboveCopyrightLine(ObjIntConsumer<String> lineConsumer) {
        var overCopyrightBrandings = getOverCopyrightBrandings();
        for (int idx = 0; idx < overCopyrightBrandings.size(); idx++)
            lineConsumer.accept(overCopyrightBrandings.get(idx), idx);
    }

    public static String getClientBranding() {
        return "forge";
    }

    public static String getServerBranding() {
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
