/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.loading.moddiscovery;

import net.minecraftforge.forgespi.coremod.ICoreModFile;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;

public class CoreModFile implements ICoreModFile {
    private final Path internalPath;
    private final ModFile file;
    private final String name;

    CoreModFile(final String name, final Path path, final ModFile file) {
        this.name = name;
        this.internalPath = path;
        this.file = file;
    }

    @Override
    public Reader readCoreMod() throws IOException {
        return Files.newBufferedReader(this.internalPath);
    }

    @Override
    public Path getPath() {
        return this.internalPath;
    }

    @Override
    public Reader getAdditionalFile(final String fileName) throws IOException {
        return Files.newBufferedReader(file.findResource(fileName));
    }

    @Override
    public String getOwnerId() {
        return this.file.getModInfos().get(0).getModId();
    }

    @Override
    public String toString() {
        return "{Name: " + name + ", Owner: " + getOwnerId() + " @ " + getPath() + "}";
    }
}
