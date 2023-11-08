/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.loading.moddiscovery;

import com.mojang.logging.LogUtils;

import net.minecraftforge.fml.loading.LogMarkers;
import net.minecraftforge.forgespi.locating.IModLocator;

import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

@ApiStatus.Internal
public class ClasspathLocator extends AbstractModProvider implements IModLocator {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Attributes.Name MOD_TYPE = new Attributes.Name("FMLModType");

    @Override
    public String name() {
        return "classpath";
    }

    @Override
    public List<ModFileOrException> scanMods() {
        //var cl = ClassLoader.getSystemClassLoader();
        var cl = getClass().getClassLoader();

        var claimed = new HashSet<Path>();
        var tomls = getUrls(cl, MODS_TOML);
        for (var url : tomls) {
            var path = getPath(url, MODS_TOML);
            claimed.add(path);
        }

        var manifests = getUrls(cl, JarFile.MANIFEST_NAME);
        for (var url : manifests) {
            var path = getPath(url, JarFile.MANIFEST_NAME);
            if (claimed.contains(path))
                continue;

            try (var is = url.openStream()) {
                var mf = new Manifest(is);
                var type = mf.getMainAttributes().getValue(MOD_TYPE);
                if (type != null)
                    claimed.add(path);
            } catch (IOException e) {
                LOGGER.warn(LogMarkers.SCAN, "Error reading manifest from: " + url, e);
            }
        }

        var ret = new ArrayList<ModFileOrException>();
        for (var path : claimed)
            ret.add(createMod(path));
        return ret;
    }

    private List<URL> getUrls(ClassLoader cl, String resource) {
        try {
            var lst = Collections.list(cl.getResources(resource));
            if (LOGGER.isDebugEnabled(LogMarkers.SCAN)) {
                Collections.sort(lst, (a, b) -> a.toString().compareTo(b.toString()));
                LOGGER.debug(LogMarkers.SCAN, "Scanning Classloader: {} for {}", cl, resource);
                for (var url : lst)
                    LOGGER.debug(LogMarkers.SCAN, "\t{}", url);
            }
            return lst;
        } catch (IOException e) {
            LOGGER.warn(LogMarkers.SCAN, "Error finding resources for: " + resource, e);
            return List.of();
        }
    }

    private static Path getPath(URL url, String resource) {
        var str = url.toString();
        int len = resource.length();
        if ("jar".equalsIgnoreCase(url.getProtocol())) {
            str = url.getFile();
            len += 2;
        }
        str = str.substring(0, str.length() - len);
        var path = Paths.get(URI.create(str));
        return path;
    }
}
