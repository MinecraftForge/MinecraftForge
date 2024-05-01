/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fmllegacy.packs;

import net.minecraft.server.packs.repository.Pack;
import net.minecraftforge.forgespi.locating.IModFile;
import net.minecraftforge.resource.PathResourcePack;

import javax.annotation.Nonnull;
import java.nio.file.Path;

@Deprecated(since="1.18", forRemoval = true) // TODO 1.18: Replace usages with PathResourcePack
public class ModFileResourcePack extends PathResourcePack
{
    private final IModFile modFile;
    private Pack packInfo;

    public ModFileResourcePack(final IModFile modFile)
    {
        super(modFile.getFileName(), modFile.getFilePath());
        this.modFile = modFile;
    }

    public IModFile getModFile() {
        return this.modFile;
    }

    @Nonnull
    @Override
    protected Path resolve(@Nonnull String... paths)
    {
        return modFile.findResource(paths);
    }

    @Override
    public String toString()
    {
        return String.format("%s: %s", getClass().getName(), getModFile().getFileName());
    }
}
