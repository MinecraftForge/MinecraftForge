/*
 * Minecraft Forge
 * Copyright (c) 2016-2022.
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

package net.minecraftforge.fml.loading.moddiscovery;

import cpw.mods.jarhandling.SecureJar;
import cpw.mods.jarhandling.JarMetadata;
import net.minecraftforge.fml.loading.LogMarkers;
import net.minecraftforge.forgespi.language.IConfigurable;
import net.minecraftforge.forgespi.language.IModFileInfo;
import net.minecraftforge.forgespi.language.IModInfo;
import net.minecraftforge.forgespi.locating.IModFile;
import net.minecraftforge.forgespi.locating.IModLocator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.jar.Manifest;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class AbstractJarFileLocator extends AbstractModLocator {
    private static final Logger LOGGER = LogManager.getLogger();

    @Override
    public void scanFile(final IModFile file, final Consumer<Path> pathConsumer) {
        LOGGER.debug(LogMarkers.SCAN,"Scan started: {}", file);
        final Function<Path, SecureJar.Status> status = p->file.getSecureJar().verifyPath(p);
        try (Stream<Path> files = Files.find(file.getSecureJar().getRootPath(), Integer.MAX_VALUE, (p, a) -> p.getNameCount() > 0 && p.getFileName().toString().endsWith(".class"))) {
            file.setSecurityStatus(files.peek(pathConsumer).map(status).reduce((s1, s2)-> SecureJar.Status.values()[Math.min(s1.ordinal(), s2.ordinal())]).orElse(SecureJar.Status.INVALID));
        } catch (IOException e) {
            e.printStackTrace();
        }
        LOGGER.debug(LogMarkers.SCAN,"Scan finished: {}", file);
    }

    @Override
    public List<IModFile> scanMods() {
        return scanCandidates()
                .map(this::createMod)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    public abstract Stream<Path> scanCandidates();
}
