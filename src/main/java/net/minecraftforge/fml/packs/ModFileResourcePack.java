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

package net.minecraftforge.fml.packs;

import net.minecraft.resources.ResourcePack;
import net.minecraft.resources.ResourcePackFileNotFoundException;
import net.minecraft.resources.ResourcePackInfo;
import net.minecraft.resources.ResourcePackType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.loading.moddiscovery.ModFile;

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

public class ModFileResourcePack extends ResourcePack
{
    private final ModFile modFile;
    private ResourcePackInfo packInfo;

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
        final Path path = modFile.getLocator().findPath(modFile, name);
        if(!Files.exists(path))
            throw new ResourcePackFileNotFoundException(modFile.getFilePath().toFile(), name);
        return Files.newInputStream(path, StandardOpenOption.READ);
    }

    @Override
    protected boolean resourceExists(String name)
    {
        return Files.exists(modFile.getLocator().findPath(modFile, name));
    }


    @Override
    public Collection<ResourceLocation> getAllResourceLocations(ResourcePackType type, String resourceNamespace, String pathIn, int maxDepth, Predicate<String> filter)
    {
        try
        {
            Path root = modFile.getLocator().findPath(modFile, type.getDirectoryName(), resourceNamespace).toAbsolutePath();
            Path inputPath = root.getFileSystem().getPath(pathIn);

            return Files.walk(root).
                    map(path -> root.relativize(path.toAbsolutePath())).
                    filter(path -> path.getNameCount() <= maxDepth). // Make sure the depth is within bounds
                    filter(path -> !path.toString().endsWith(".mcmeta")). // Ignore .mcmeta files
                    filter(path -> path.startsWith(inputPath)). // Make sure the target path is inside this one
                    filter(path -> filter.test(path.getFileName().toString())). // Test the file name against the predicate
                    // Finally we need to form the RL, so use the first name as the domain, and the rest as the path
                    // It is VERY IMPORTANT that we do not rely on Path.toString as this is inconsistent between operating systems
                    // Join the path names ourselves to force forward slashes
                    map(path -> new ResourceLocation(resourceNamespace, Joiner.on('/').join(path))).
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
            Path root = modFile.getLocator().findPath(modFile, type.getDirectoryName()).toAbsolutePath();
            return Files.walk(root,1)
                    .map(path -> root.relativize(path.toAbsolutePath()))
                    .filter(path -> path.getNameCount() > 0) // skip the root entry
                    .map(p->p.toString().replaceAll("/$","")) // remove the trailing slash, if present
                    .filter(s -> !s.isEmpty()) //filter empty strings, otherwise empty strings default to minecraft in ResourceLocations
                    .collect(Collectors.toSet());
        }
        catch (IOException e)
        {
            if (type == ResourcePackType.SERVER_DATA) //We still have to add the resource namespace if client resources exist, as we load langs (which are in assets) on server
            {
                return this.getResourceNamespaces(ResourcePackType.CLIENT_RESOURCES);
            }
            else
            {
                return Collections.emptySet();
            }
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
    public void close()
    {

    }

    <T extends ResourcePackInfo> void setPackInfo(final T packInfo) {
        this.packInfo = packInfo;
    }

    <T extends ResourcePackInfo> T getPackInfo() {
        return (T)this.packInfo;
    }
}
