/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
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

package net.minecraftforge.fml.packs;

import net.minecraft.resources.AbstractResourcePack;
import net.minecraft.resources.ResourcePackType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.loading.moddiscovery.ModFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.function.Predicate;
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
    public String getName()
    {
        return modFile.getFileName();
    }

    @Override
    protected InputStream getInputStream(String name) throws IOException
    {
        return Files.newInputStream(modFile.getLocator().findPath(modFile, name));
    }

    @Override
    protected boolean resourceExists(String name)
    {
        return Files.exists(modFile.getLocator().findPath(modFile, name));
    }

    @Override
    public Collection<ResourceLocation> getAllResourceLocations(ResourcePackType type, String pathIn, int maxDepth, Predicate<String> filter)
    {
        try
        {
            return Files.walk(modFile.getLocator().findPath(modFile, type.getDirectoryName())).
                    filter(path -> !path.toString().endsWith(".mcmeta")).
                    filter(path -> path.getNameCount() > 2 && pathIn.equals(path.getName(2).toString())).
                    filter(path -> filter.test(path.subpath(3, Math.min(maxDepth+3, path.getNameCount())).toString())).
                    map(path -> new ResourceLocation(path.getName(1).toString(),path.subpath(3,Math.min(maxDepth+3, path.getNameCount())).toString())).
                    collect(Collectors.toList());
        }
        catch (IOException e)
        {
            return Collections.emptyList();
        }
    }

    @Override
    public Set<String> getResourceNamespaces(ResourcePackType type)
    {
        try {
            return Files.walk(modFile.getLocator().findPath(modFile, type.getDirectoryName()),1).map(p->p.getFileName().toString()).collect(Collectors.toSet());
        }
        catch (IOException e)
        {
            return Collections.emptySet();
        }
    }

    public InputStream getResourceStream(ResourcePackType type, ResourceLocation location) throws IOException {
        if (location.getPath().startsWith("lang/")) {
            return super.getResourceStream(ResourcePackType.CLIENT_RESOURCES, location);
        } else {
            return super.getResourceStream(type, location);
        }
    }

    public boolean resourceExists(ResourcePackType type, ResourceLocation location) {
        if (location.getPath().startsWith("lang/")) {
            return super.resourceExists(ResourcePackType.CLIENT_RESOURCES, location);
        } else {
            return super.resourceExists(type, location);
        }
    }


    @Override
    public void close() throws IOException
    {

    }
}
