/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.server;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.GsonHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeI18n;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Pattern;

public class LanguageHook
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = new Gson();
    private static final Pattern PATTERN = Pattern.compile("%(\\d+\\$)?[\\d\\.]*[df]");
    private static final List<Map<String, String>> CAPTURED_TABLES = new ArrayList<>();
    private static Map<String, String> modTable;
    /**
     * Loads lang files on the server
     */
    public static void captureLanguageMap(Map<String, String> table)
    {
        CAPTURED_TABLES.add(table);
        if (modTable != null) {
            CAPTURED_TABLES.forEach(t->t.putAll(modTable));
        }
    }

    // The below is based on client side net.minecraft.client.resources.Locale code
    private static void loadLocaleData(final List<Resource> allResources) {
        allResources.forEach(res -> {
            try {
                LanguageHook.loadLocaleData(res.open());
            } catch (IOException ignored) {} // TODO: this should not be ignored -C
        });
    }

    private static void loadLocaleData(final InputStream inputstream) {
        try
        {
            JsonElement jsonelement = GSON.fromJson(new InputStreamReader(inputstream, StandardCharsets.UTF_8), JsonElement.class);
            JsonObject jsonobject = GsonHelper.convertToJsonObject(jsonelement, "strings");

            jsonobject.entrySet().forEach(entry -> {
                String s = PATTERN.matcher(GsonHelper.convertToString(entry.getValue(), entry.getKey())).replaceAll("%$1s");
                modTable.put(entry.getKey(), s);
            });
        }
        finally
        {
            IOUtils.closeQuietly(inputstream);
        }
    }

    private static void loadLanguage(String langName, MinecraftServer server) {
        String langFile = String.format(Locale.ROOT, "lang/%s.json", langName);
        ResourceManager resourceManager = server.getServerResources().resourceManager();
        resourceManager.getNamespaces().forEach(namespace -> {
            try {
                ResourceLocation langResource = ResourceLocation.fromNamespaceAndPath(namespace, langFile);
                loadLocaleData(resourceManager.getResourceStack(langResource));
            } catch (Exception exception) {
                LOGGER.warn("Skipped language file: {}:{}", namespace, langFile, exception);
            }
        });

    }

    public static void loadForgeAndMCLangs() {
        modTable = new HashMap<>(5000);
        final InputStream mc = Thread.currentThread().getContextClassLoader().getResourceAsStream("assets/minecraft/lang/en_us.json");
        final InputStream forge = Thread.currentThread().getContextClassLoader().getResourceAsStream("assets/forge/lang/en_us.json");
        loadLocaleData(mc);
        loadLocaleData(forge);
        CAPTURED_TABLES.forEach(t->t.putAll(modTable));
        ForgeI18n.loadLanguageData(modTable);
    }

    static void loadLanguagesOnServer(MinecraftServer server) {
        modTable = new HashMap<>(5000);
        // Possible multi-language server support?
        for (String lang : Arrays.asList("en_us")) {
            loadLanguage(lang, server);
        }
        CAPTURED_TABLES.forEach(t->t.putAll(modTable));
        ForgeI18n.loadLanguageData(modTable);
    }
}
