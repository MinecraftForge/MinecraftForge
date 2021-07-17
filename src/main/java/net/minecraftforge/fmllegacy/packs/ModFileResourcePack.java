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

import net.minecraft.server.packs.AbstractPackResources;
import net.minecraft.server.packs.ResourcePackFileNotFoundException;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.PackType;
import net.minecraft.resources.ResourceLocation;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.google.common.base.Joiner;
import net.minecraftforge.forgespi.locating.IModFile;

public class ModFileResourcePack extends AbstractPackResources
{
    private final IModFile modFile;
    private Pack packInfo;

    public ModFileResourcePack(final IModFile modFile)
    {
        super(new File("dummy"));
        this.modFile = modFile;
    }

    public IModFile getModFile() {
        return this.modFile;
    }

    @Override
    public String getName()
    {
        return modFile.getFileName();
    }

    @Override
    protected InputStream getResource(String name) throws IOException
    {
        final Path path = modFile.findResource(name);
        if(!Files.exists(path))
            throw new ResourcePackFileNotFoundException(modFile.getFilePath().toFile(), name);
        return Files.newInputStream(path, StandardOpenOption.READ);
    }

    @Override
    protected boolean hasResource(String name)
    {
        return Files.exists(modFile.findResource(name));
    }


    @Override
    public Collection<ResourceLocation> getResources(PackType type, String resourceNamespace, String pathIn, int maxDepth, Predicate<String> filter)
    {
        try
        {
            Path root = modFile.findResource(type.getDirectory(), resourceNamespace).toAbsolutePath();
            Path inputPath = root.getFileSystem().getPath(pathIn);

            return Files.walk(root)
                    .map(root::relativize)
                    .filter(path -> path.getNameCount() <= maxDepth && !path.toString().endsWith(".mcmeta") && path.startsWith(inputPath))
                    .filter(path -> filter.test(path.getFileName().toString()))
                    // It is VERY IMPORTANT that we do not rely on Path.toString as this is inconsistent between operating systems
                    // Join the path names ourselves to force forward slashes
                    .map(path -> new ResourceLocation(resourceNamespace, Joiner.on('/').join(path)))
                    .collect(Collectors.toList());
        }
        catch (IOException e)
        {
            return Collections.emptyList();
        }
    }

    @Override
    public Set<String> getNamespaces(PackType type)
    {
        try {
            Path root = modFile.findResource(type.getDirectory());
            return Files.walk(root,1)
                    .map(path -> root.relativize(path))
                    .filter(path -> path.getNameCount() > 0) // skip the root entry
                    .map(p->p.toString().replaceAll("/$","")) // remove the trailing slash, if present
                    .filter(s -> !s.isEmpty()) //filter empty strings, otherwise empty strings default to minecraft in ResourceLocations
                    .collect(Collectors.toSet());
        }
        catch (IOException e)
        {
            if (type == PackType.SERVER_DATA) //We still have to add the resource namespace if client resources exist, as we load langs (which are in assets) on server
            {
                return this.getNamespaces(PackType.CLIENT_RESOURCES);
            }
            else
            {
                return Collections.emptySet();
            }
        }
    }

    public InputStream getResource(PackType type, ResourceLocation location) throws IOException {
        if (location.getPath().startsWith("lang/")) {
            return super.getResource(PackType.CLIENT_RESOURCES, location);
        } else {
            return super.getResource(type, location);
        }
    }

    public boolean hasResource(PackType type, ResourceLocation location) {
        if (location.getPath().startsWith("lang/")) {
            return super.hasResource(PackType.CLIENT_RESOURCES, location);
        } else {
            return super.hasResource(type, location);
        }
    }

    @Override
    public void close()
    {

    }

    <T extends Pack> void setPackInfo(final T packInfo) {
        this.packInfo = packInfo;
    }

    <T extends Pack> T getPackInfo() {
        return (T)this.packInfo;
    }
}
