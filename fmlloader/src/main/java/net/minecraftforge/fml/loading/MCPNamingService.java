/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.loading;

import com.mojang.logging.LogUtils;
import cpw.mods.modlauncher.api.INameMappingService;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public class MCPNamingService implements INameMappingService {
    private static final Logger LOGGER = LogUtils.getLogger();
    private HashMap<String, String> methods;
    private HashMap<String, String> fields;

    @Override
    public String mappingName() {
        return "srgtomcp";
    }

    @Override
    public String mappingVersion() {
        return "1234"; //TODO: Minecraft Version?
    }

    @Override
    public Map.Entry<String, String> understanding() {
        return Pair.of("srg", "mcp");
    }

    @Override
    public BiFunction<Domain, String, String> namingFunction() {
        return this::findMapping;
    }

    private String findMapping(final Domain domain, final String srgName) {
        switch (domain) {
            case CLASS:
                return srgName;
            case FIELD:
                return findFieldMapping(srgName);
            case METHOD:
                return findMethodMapping(srgName);
        }
        return srgName;
    }

    private String findMethodMapping(final String origin) {
        if (methods == null) {
            HashMap<String,String> tmpmethods = new HashMap<>(1000);
            loadMappings("methods.csv", tmpmethods::put);
            methods = tmpmethods;
            LOGGER.debug(LogMarkers.CORE, "Loaded {} method mappings from methods.csv", methods.size());
        }
        return methods.getOrDefault(origin, origin);
    }

    private String findFieldMapping(final String origin) {
        if (fields == null) {
            HashMap<String,String> tmpfields = new HashMap<>(1000);
            loadMappings("fields.csv", tmpfields::put);
            fields = tmpfields;
            LOGGER.debug(LogMarkers.CORE, "Loaded {} field mappings from fields.csv", fields.size());
        }
        return fields.getOrDefault(origin, origin);
    }

    private static void loadMappings(final String mappingFileName, BiConsumer<String, String> mapStore) {
        URL path = ClassLoader.getSystemResource(mappingFileName); //We EXPLICITLY go through the SystemClassLoader here because this is dev-time only. And will be on the root classpath.
        if (path == null)
            return;

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(path.openStream()))) {
            reader.lines().skip(1).map(e -> e.split(",")).forEach(e -> mapStore.accept(e[0], e[1]));
        } catch (IOException e1) {
            LOGGER.error(LogMarkers.CORE, "Error reading mappings", e1);
        }
    }
}
