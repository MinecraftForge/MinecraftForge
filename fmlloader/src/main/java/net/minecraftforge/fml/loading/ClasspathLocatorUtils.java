/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.loading;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ClasspathLocatorUtils {
    
    private static final Logger LOGGER = LogUtils.getLogger();

    public static Path findJarPathFor(final String resourceName, final String jarName, final URL resource) {
        try {
            Path path;
            final URI uri = resource.toURI();
            if (uri.getScheme().equals("jar") && uri.getRawSchemeSpecificPart().contains("!/")) {
                int lastExcl = uri.getRawSchemeSpecificPart().lastIndexOf("!/");
                path = Paths.get(new URI(uri.getRawSchemeSpecificPart().substring(0, lastExcl)));
            } else {
                path = Paths.get(new URI("file://"+uri.getRawSchemeSpecificPart().substring(0, uri.getRawSchemeSpecificPart().length()-resourceName.length())));
            }
            //LOGGER.debug(CORE, "Found JAR {} at path {}", jarName, path.toString());
            return path;
        } catch (NullPointerException | URISyntaxException e) {
            LOGGER.error(LogMarkers.SCAN, "Failed to find JAR for class {} - {}", resourceName, jarName);
            throw new RuntimeException("Unable to locate "+resourceName+" - "+jarName, e);
        }
    }
}
