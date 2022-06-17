/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.loading.moddiscovery;

import net.minecraftforge.forgespi.locating.IModLocator;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

public abstract class AbstractJarFileModLocator extends AbstractJarFileModProvider implements IModLocator
{
    @Override
    public List<IModLocator.ModFileOrException> scanMods()
    {
        return scanCandidates().map(this::createMod).toList();
    }

    public abstract Stream<Path> scanCandidates();
}
