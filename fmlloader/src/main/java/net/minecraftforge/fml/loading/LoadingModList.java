/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.fml.loading;

import net.minecraftsingularity.fml.loading.moddiscovery.ModFileInfo;
import net.minecraftsingularity.fml.loading.moddiscovery.ModInfo;
import net.minecraftsingularity.singularityspi.locating.IModFile;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/// Master list of all mods in the loading context
public sealed interface LoadingModList permits LoadingModListImpl {
    static ModFileInfo getModFileById(String modId) {
        return get().fileById().get(modId);
    }

    static List<ModFileInfo> getModFiles() {
        return get().getModFiles();
    }

    static List<ModInfo> getMods() {
        return get().getMods();
    }

    static @Nullable Path findResource(String className) {
        for (ModFileInfo modFileInfo : get().getModFiles()) {
            Path resource = modFileInfo.getFile().findResource(className);
            if (Files.exists(resource)) return resource;
        }
        return null;
    }

    static List<EarlyLoadingException> getErrors() {
        return get().getErrors();
    }

    static List<IModFile> getBrokenFiles() {
        return get().getBrokenFiles();
    }

    private static LoadingModListImpl get() {
        return LoadingModListImpl.get();
    }
}
