/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
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
