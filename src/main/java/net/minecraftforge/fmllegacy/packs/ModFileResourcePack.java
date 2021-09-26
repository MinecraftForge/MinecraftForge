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

import java.nio.file.Path;

public class ModFileResourcePack extends BasePathResourcePack
{
    private final IModFile modFile;
    private Pack packInfo;

    public ModFileResourcePack(final IModFile modFile)
    {
        super();
        this.modFile = modFile;
    }

    public IModFile getModFile() {
        return this.modFile;
    }

    @Override
    public Path getSource()
    {
        return modFile.getFilePath();
    }

    @Override
    public String getName()
    {
        return modFile.getFileName();
    }

    @Override
    protected Path resolve(String... paths)
    {
        return modFile.findResource(paths);
    }

    <T extends Pack> void setPackInfo(final T packInfo) {
        this.packInfo = packInfo;
    }

    <T extends Pack> T getPackInfo() {
        return (T)this.packInfo;
    }
}
