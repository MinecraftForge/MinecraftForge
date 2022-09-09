/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.resource;

import com.google.common.base.Joiner;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.AbstractPackResources;
import net.minecraft.server.packs.PackType;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Collection;
import java.util.Collections;
import java.util.Locale;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Defines a resource pack from an arbitrary Path.
 *
 * This is primarily intended to support including optional resource packs inside a mod,
 * such as to have alternative textures to use along with Programmer Art, or optional
 * alternative recipes for compatibility ot to replace vanilla recipes.
 */
public class PathResourcePack extends AbstractPackResources
{
    private final Path source;
    private final String packName;

    /**
     * Constructs a java.nio.Path-based resource pack.
     *
     * @param packName the identifying name of the pack.
     *                 This name should be unique within the pack finder, preferably the name of the file or folder containing the resources.
     * @param source the root path of the pack. This needs to point to the folder that contains "assets" and/or "data", not the asset folder itself!
     */
    public PathResourcePack(String packName, final Path source)
    {
        super(new File("dummy"));
        this.source = source;
        this.packName = packName;
    }

    /**
     * Returns the source path containing the resource pack.
     * This is used for error display.
     *
     * @return the root path of the resources.
     */
    public Path getSource() {
        return this.source;
    }

    /**
     * Returns the identifying name for the pack.
     *
     * @return the identifier of the pack.
     */
    @Override
    public String getName()
    {
        return packName;
    }

    /**
     * Implement to return a file or folder path for the given set of path components.
     * @param paths One or more path strings to resolve. Can include slash-separated paths.
     * @return the resulting path, which may not exist.
     */
    protected Path resolve(String... paths)
    {
        Path path = getSource();
        for(String name : paths)
            path = path.resolve(name);
        return path;
    }

    @Override
    protected InputStream getResource(String name) throws IOException
    {
        final Path path = resolve(name);
        if(!Files.exists(path))
            throw new FileNotFoundException("Can't find resource " + name + " at " + getSource());
        return Files.newInputStream(path, StandardOpenOption.READ);
    }

    @Override
    protected boolean hasResource(String name)
    {
        final Path path = resolve(name);
        return Files.exists(path);
    }

    @Override
    public Collection<ResourceLocation> getResources(PackType type, String resourceNamespace, String pathIn, int maxDepth, Predicate<String> filter)
    {
        try
        {
            Path root = resolve(type.getDirectory(), resourceNamespace).toAbsolutePath();
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
            Path root = resolve(type.getDirectory());
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

    @Override
    public InputStream getResource(PackType type, ResourceLocation location) throws IOException {
        if (location.getPath().startsWith("lang/")) {
            return super.getResource(PackType.CLIENT_RESOURCES, location);
        } else {
            return super.getResource(type, location);
        }
    }

    @Override
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

    @Override
    public String toString()
    {
        return String.format(Locale.ROOT, "%s: %s", getClass().getName(), getSource());
    }
}
