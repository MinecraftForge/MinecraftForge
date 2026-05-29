/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.client;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import net.minecraft.client.data.AtlasProvider;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.client.renderer.texture.atlas.sources.SingleFile;
import net.minecraft.data.AtlasIds;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;

public class singularityAtlasProvider extends AtlasProvider {
    public singularityAtlasProvider(PackOutput output) {
        super(output);
    }

    @Override
    public CompletableFuture<?> run(final CachedOutput cache) {
        return CompletableFuture.allOf(
            this.storeAtlas(cache, AtlasIds.BLOCKS, List.of(
                single("white")
            )),
            this.storeAtlas(cache, AtlasIds.ITEMS, List.of(
                fromBlock("block/lava_still"),
                fromBlock("block/water_still")
            ))
        );
    }

    private static SpriteSource single(String path) {
        return new SingleFile(Identifier.fromNamespaceAndPath("singularity", path));
    }

    private static SpriteSource fromBlock(String name) {
        var resource = Identifier.withDefaultNamespace(name);
        var id = resource;
        if (resource.getPath().startsWith("block/"))
            id = resource.withPath(path -> "item/" + path.substring(6));
        return new SingleFile(resource, Optional.of(id));
    }
}
