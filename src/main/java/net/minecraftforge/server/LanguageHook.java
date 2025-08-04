/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.server;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.IoSupplier;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.common.ForgeI18n;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Pattern;

public class LanguageHook {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = new Gson();
    private static final Pattern PATTERN = Pattern.compile("%(\\d+\\$)?[\\d\\.]*[df]");
    private static final List<Map<String, String>> CAPTURED_TABLES = new ArrayList<>();
    private static Map<String, String> modTable;
    /**
     * Loads lang files on the server
     */
    public static void captureLanguageMap(Map<String, String> table) {
        CAPTURED_TABLES.add(table);
        if (modTable != null)
            CAPTURED_TABLES.forEach(t -> t.putAll(modTable));
    }

    private static void loadLocaleData(final InputStream inputstream) {
        try {
            JsonElement jsonelement = GSON.fromJson(new InputStreamReader(inputstream, StandardCharsets.UTF_8), JsonElement.class);
            JsonObject jsonobject = GsonHelper.convertToJsonObject(jsonelement, "strings");

            for (var entry : jsonobject.entrySet()) {
                String s = PATTERN.matcher(GsonHelper.convertToString(entry.getValue(), entry.getKey())).replaceAll("%$1s");
                modTable.put(entry.getKey(), s);
            }
        } finally {
            IOUtils.closeQuietly(inputstream);
        }
    }

    private static void loadLanguage(String langName, MinecraftServer server) {
        var langFile = String.format(Locale.ROOT, "lang/%s.json", langName);
        var resourceManager = server.getServerResources().resourceManager();
        var byNamespace = new HashMap<String, List<PackResources>>();

        var packs = new ArrayList<>(resourceManager.listPacks().toList());
        var seen = new HashSet<>(packs); // Jut in case of recursion

        while (!packs.isEmpty()) {
            var pack = packs.removeFirst();
            if (pack.getChildren() != null) {
                for (var child : pack.getChildren()) {
                    if (seen.add(child))
                        packs.add(child);
                }
            }

            for (var namespace : pack.getNamespaces(PackType.CLIENT_RESOURCES)) {
                byNamespace.computeIfAbsent(namespace, k -> new ArrayList<>()).add(pack);
            }
        }

        for (var entry : byNamespace.entrySet()) {
            var languages = new ArrayList<IoSupplier<InputStream>>();

            var path = ResourceLocation.fromNamespaceAndPath(entry.getKey(), langFile);
            for (var pack : entry.getValue()) {
                var io = pack.getResource(PackType.CLIENT_RESOURCES, path);
                if (io != null)
                    languages.add(io);
            }

            while (!languages.isEmpty()) {
                try (var stream = languages.removeLast().get()) {
                    loadLocaleData(stream);
                } catch (Exception exception) {
                    LOGGER.warn("Skipped language file: {}:{}", entry.getKey(), langFile, exception);
                }
            }
        }
    }

    public static void loadForgeAndMCLangs() {
        modTable = new HashMap<>(5000);
        final InputStream mc = Thread.currentThread().getContextClassLoader().getResourceAsStream("assets/minecraft/lang/en_us.json");
        final InputStream forge = Thread.currentThread().getContextClassLoader().getResourceAsStream("assets/forge/lang/en_us.json");
        loadLocaleData(mc);
        loadLocaleData(forge);
        CAPTURED_TABLES.forEach(t -> t.putAll(modTable));
        ForgeI18n.loadLanguageData(modTable);
    }

    static void loadLanguagesOnServer(MinecraftServer server) {
        modTable = new HashMap<>(5000);
        loadLanguage("en_us", server);
        CAPTURED_TABLES.forEach(t->t.putAll(modTable));
        ForgeI18n.loadLanguageData(modTable);
    }
}
