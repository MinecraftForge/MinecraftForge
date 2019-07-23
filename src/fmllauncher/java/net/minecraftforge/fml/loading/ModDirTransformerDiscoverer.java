/*
 * Minecraft Forge
 * Copyright (c) 2016-2019.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.fml.loading;

import cpw.mods.modlauncher.api.LamdbaExceptionUtils;
import cpw.mods.modlauncher.serviceapi.ITransformerDiscoveryService;
import org.apache.logging.log4j.LogManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.zip.ZipFile;

public class ModDirTransformerDiscoverer implements ITransformerDiscoveryService {
    @Override
    public List<Path> candidates(final Path gameDirectory) {
        final Path modsDir = gameDirectory.resolve(FMLPaths.MODSDIR.relative());
        if (!Files.exists(modsDir)) {
            // Skip if the mods dir doesn't exist yet.
            return Collections.emptyList();
        }
        List<Path> paths = new ArrayList<>();
        try {
            Files.createDirectories(modsDir);
            Files.walk(modsDir, 1).forEach(p -> {
                if (!Files.isRegularFile(p)) return;
                if (!p.toString().endsWith(".jar")) return;
                if (LamdbaExceptionUtils.uncheck(()->Files.size(p)) == 0) return;
                try (ZipFile zf = new ZipFile(new File(p.toUri()))) {
                    if (zf.getEntry("META-INF/services/cpw.mods.modlauncher.api.ITransformationService") != null) {
                        paths.add(p);
                    }
                } catch (IOException ioe) {
                    LogManager.getLogger().error("Zip Error when loading jar file {}", p, ioe);
                }
            });
        } catch (IOException | IllegalStateException ioe) {
            LogManager.getLogger().error("Error during early discovery", ioe);
        }
        return paths;
    }
}
