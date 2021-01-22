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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.resources.IResourcePack;
import net.minecraft.resources.ResourcePack;
import net.minecraft.resources.ResourcePackFileNotFoundException;
import net.minecraft.resources.ResourcePackType;
import net.minecraft.resources.data.IMetadataSectionSerializer;
import net.minecraft.resources.data.PackMetadataSection;
import net.minecraft.util.ResourceLocation;

public class DelegatingResourcePack extends ResourcePack
{

    private final List<IResourcePack> delegates;
    private final Map<String, List<IResourcePack>> namespacesAssets;
    private final Map<String, List<IResourcePack>> namespacesData;

    private final String name;
    private final PackMetadataSection packInfo;

    public DelegatingResourcePack(String id, String name, PackMetadataSection packInfo, List<? extends IResourcePack> packs)
    {
        super(new File(id));
        this.name = name;
        this.packInfo = packInfo;
        this.delegates = ImmutableList.copyOf(packs);
        this.namespacesAssets = this.buildNamespaceMap(ResourcePackType.CLIENT_RESOURCES, delegates);
        this.namespacesData = this.buildNamespaceMap(ResourcePackType.SERVER_DATA, delegates);
    }

    private Map<String, List<IResourcePack>> buildNamespaceMap(ResourcePackType type, List<IResourcePack> packList)
    {
        Map<String, List<IResourcePack>> map = new HashMap<>();
        for (IResourcePack pack : packList)
        {
            for (String namespace : pack.getResourceNamespaces(type))
            {
                map.computeIfAbsent(namespace, k -> new ArrayList<>()).add(pack);
            }
        }
        map.replaceAll((k, list) -> ImmutableList.copyOf(list));
        return ImmutableMap.copyOf(map);
    }

    @Override
    public String getName()
    {
        return name;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public <T> T getMetadata(IMetadataSectionSerializer<T> deserializer) throws IOException
    {
        if (deserializer.getSectionName().equals("pack"))
        {
            return (T) packInfo;
        }
        return null;
    }

    @Override
    public Collection<ResourceLocation> getAllResourceLocations(ResourcePackType type, String pathIn, String pathIn2, int maxDepth, Predicate<String> filter)
    {
        return delegates.stream()
                .flatMap(r -> r.getAllResourceLocations(type, pathIn, pathIn2, maxDepth, filter).stream())
                .collect(Collectors.toList());
    }

    @Override
    public Set<String> getResourceNamespaces(ResourcePackType type)
    {
        return type == ResourcePackType.CLIENT_RESOURCES ? namespacesAssets.keySet() : namespacesData.keySet();
    }

    @Override
    public void close()
    {
        for (IResourcePack pack : delegates)
        {
            pack.close();
        }
    }

    @Override
    public InputStream getRootResourceStream(String fileName) throws IOException
    {
        // root resources do not make sense here
        throw new ResourcePackFileNotFoundException(this.file, fileName);
    }

    @Override
    protected InputStream getInputStream(String resourcePath) throws IOException
    {
        // never called, we override all methods that call this
        throw new ResourcePackFileNotFoundException(this.file, resourcePath);
    }

    @Override
    protected boolean resourceExists(String resourcePath)
    {
        // never called, we override all methods that call this
        return false;
    }

    @Override
    public InputStream getResourceStream(ResourcePackType type, ResourceLocation location) throws IOException
    {
        for (IResourcePack pack : getCandidatePacks(type, location))
        {
            if (pack.resourceExists(type, location))
            {
                return pack.getResourceStream(type, location);
            }
        }
        throw new ResourcePackFileNotFoundException(this.file, getFullPath(type, location));
    }

    @Override
    public boolean resourceExists(ResourcePackType type, ResourceLocation location)
    {
        for (IResourcePack pack : getCandidatePacks(type, location))
        {
            if (pack.resourceExists(type, location))
            {
                return true;
            }
        }
        return false;
    }

    private List<IResourcePack> getCandidatePacks(ResourcePackType type, ResourceLocation location)
    {
        Map<String, List<IResourcePack>> map = type == ResourcePackType.CLIENT_RESOURCES ? namespacesAssets : namespacesData;
        List<IResourcePack> packsWithNamespace = map.get(location.getNamespace());
        return packsWithNamespace == null ? Collections.emptyList() : packsWithNamespace;
    }

    private static String getFullPath(ResourcePackType type, ResourceLocation location)
    {
        // stolen from ResourcePack
        return String.format("%s/%s/%s", type.getDirectoryName(), location.getNamespace(), location.getPath());
    }

}
