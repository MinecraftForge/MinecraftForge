/*
 * Minecraft Forge
 * Copyright (c) 2018.
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

package net.minecraftforge.fml.client;

import net.minecraft.client.resources.AbstractResourcePack;
import net.minecraftforge.fml.loading.moddiscovery.ModFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

public class ModFileResourcePack extends AbstractResourcePack
{
    private final ModFile modFile;

    public ModFileResourcePack(final ModFile modFile)
    {
        super(new File("dummy"));
        this.modFile = modFile;
    }

    public ModFile getModFile() {
        return this.modFile;
    }

    @Override
    public String getPackName()
    {
        return modFile.getFileName();
    }

    @Override
    protected InputStream getInputStreamByName(String name) throws IOException
    {
        return Files.newInputStream(modFile.getLocator().findPath(modFile, name));
    }

    @Override
    protected boolean hasResourceName(String name)
    {
        return Files.exists(modFile.getLocator().findPath(modFile, name));
    }

    @Override
    public Set<String> getResourceDomains()
    {
        try {
            return Files.walk(modFile.getLocator().findPath(modFile, "assets"),1).map(p->p.getFileName().toString()).collect(Collectors.toSet());
        }
        catch (IOException e)
        {
            return Collections.emptySet();
        }
    }
}
