/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.resource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.AbstractPackResources;
import net.minecraft.server.packs.ResourcePackFileNotFoundException;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.resources.ResourceLocation;

public class DelegatingPackResources extends AbstractPackResources
{
    private final List<PackResources> delegates;
    private Map<String, List<PackResources>> namespacesAssets;
    private Map<String, List<PackResources>> namespacesData;

    private final String name;
    private final PackMetadataSection packInfo;

    public DelegatingPackResources(String id, String name, PackMetadataSection packInfo, List<? extends PackResources> packs)
    {
        super(new File(id));
        this.name = name;
        this.packInfo = packInfo;
        this.delegates = ImmutableList.copyOf(packs);
        this.namespacesAssets = this.buildNamespaceMap(PackType.CLIENT_RESOURCES, delegates);
        this.namespacesData = this.buildNamespaceMap(PackType.SERVER_DATA, delegates);
    }

    @Override
    public void initForNamespace(final String nameSpace)
    {
        this.delegates.forEach(delegate -> delegate.initForNamespace(nameSpace));
    }

    @Override
    public void init(final PackType packType)
    {
        this.delegates.forEach(packResources -> packResources.init(packType));

        this.namespacesAssets = buildNamespaceMap(PackType.CLIENT_RESOURCES, delegates);
        this.namespacesData = buildNamespaceMap(PackType.SERVER_DATA, delegates);
    }

    private Map<String, List<PackResources>> buildNamespaceMap(PackType type, List<PackResources> packList)
    {
        Map<String, List<PackResources>> map = new HashMap<>();
        for (PackResources pack : packList)
        {
            for (String namespace : pack.getNamespaces(type))
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
    public <T> T getMetadataSection(MetadataSectionSerializer<T> deserializer) throws IOException
    {
        if (deserializer.getMetadataSectionName().equals("pack"))
        {
            return (T) packInfo;
        }
        return null;
    }

    @Override
    public Collection<ResourceLocation> getResources(PackType type, String pathIn, String pathIn2, Predicate<ResourceLocation> filter)
    {
        return delegates.stream()
                .flatMap(r -> r.getResources(type, pathIn, pathIn2, filter).stream())
                .collect(Collectors.toList());
    }

    @Override
    public Set<String> getNamespaces(PackType type)
    {
        return type == PackType.CLIENT_RESOURCES ? namespacesAssets.keySet() : namespacesData.keySet();
    }

    @Override
    public void close()
    {
        for (PackResources pack : delegates)
        {
            pack.close();
        }
    }

    @Override
    public InputStream getRootResource(String fileName) throws IOException
    {
        // root resources do not make sense here
        throw new ResourcePackFileNotFoundException(this.file, fileName);
    }

    @Override
    protected InputStream getResource(String resourcePath) throws IOException
    {
        // never called, we override all methods that call this
        throw new ResourcePackFileNotFoundException(this.file, resourcePath);
    }

    @Override
    protected boolean hasResource(String resourcePath)
    {
        // never called, we override all methods that call this
        return false;
    }

    @Override
    public InputStream getResource(PackType type, ResourceLocation location) throws IOException
    {
        for (PackResources pack : getCandidatePacks(type, location))
        {
            if (pack.hasResource(type, location))
            {
                return pack.getResource(type, location);
            }
        }
        throw new ResourcePackFileNotFoundException(this.file, getFullPath(type, location));
    }

    @Override
    public boolean hasResource(PackType type, ResourceLocation location)
    {
        for (PackResources pack : getCandidatePacks(type, location))
        {
            if (pack.hasResource(type, location))
            {
                return true;
            }
        }
        return false;
    }

    private List<PackResources> getCandidatePacks(PackType type, ResourceLocation location)
    {
        Map<String, List<PackResources>> map = type == PackType.CLIENT_RESOURCES ? namespacesAssets : namespacesData;
        List<PackResources> packsWithNamespace = map.get(location.getNamespace());
        return packsWithNamespace == null ? Collections.emptyList() : packsWithNamespace;
    }

    private static String getFullPath(PackType type, ResourceLocation location)
    {
        // stolen from ResourcePack
        return String.format(Locale.ROOT, "%s/%s/%s", type.getDirectory(), location.getNamespace(), location.getPath());
    }

}
