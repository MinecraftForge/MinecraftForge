/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.loading.moddiscovery;

import com.mojang.logging.LogUtils;
import cpw.mods.jarhandling.SecureJar;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraftforge.fml.loading.LogMarkers;
import net.minecraftforge.forgespi.locating.IModFile;
import org.slf4j.Logger;

public abstract class AbstractJarFileLocator extends AbstractModLocator {
    private static final Logger LOGGER = LogUtils.getLogger();

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
