/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.textures;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.util.GsonHelper;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Optional;

/**
 * The "forge" section of texture metadata files (.mcmeta). Currently used only to specify custom
 * TextureAtlasSprite loaders.
 * @see ITextureAtlasSpriteLoader
 */
public final class ForgeTextureMetadata
{

    public static final ForgeTextureMetadata EMPTY = new ForgeTextureMetadata(null);
    public static final MetadataSectionSerializer<ForgeTextureMetadata> SERIALIZER = new Serializer();

    public static ForgeTextureMetadata forResource(Resource resource) throws IOException {
        Optional<ForgeTextureMetadata> metadata = resource.metadata().getSection(SERIALIZER);
        return metadata.isEmpty() ? EMPTY : metadata.get();
    }

    @Nullable
    private final ITextureAtlasSpriteLoader loader;

    public ForgeTextureMetadata(@Nullable ITextureAtlasSpriteLoader loader)
    {
        this.loader = loader;
    }

    @Nullable
    public ITextureAtlasSpriteLoader getLoader()
    {
        return loader;
    }

    private static final class Serializer implements MetadataSectionSerializer<ForgeTextureMetadata>
    {

        @Override
        @NotNull
        public String getMetadataSectionName()
        {
            return "forge";
        }

        @Override
        @NotNull
        public ForgeTextureMetadata fromJson(JsonObject json)
        {
            @Nullable
            ITextureAtlasSpriteLoader loader;
            if (json.has("loader"))
            {
                ResourceLocation loaderName = new ResourceLocation(GsonHelper.getAsString(json, "loader"));
                loader = TextureAtlasSpriteLoaderManager.get(loaderName);
                if (loader == null)
                {
                    throw new JsonSyntaxException("Unknown TextureAtlasSpriteLoader " + loaderName);
                }
            }
            else
            {
                loader = null;
            }
            return new ForgeTextureMetadata(loader);
        }
    }

}
