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

package net.minecraftforge.client.textures;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.client.MinecraftForgeClient;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

/**
 * The "forge" section of texture metadata files (.mcmeta). Currently used only to specify custom
 * TextureAtlasSprite loaders.
 * @see ITextureAtlasSpriteLoader
 */
public final class ForgeTextureMetadata
{

    public static final ForgeTextureMetadata EMPTY = new ForgeTextureMetadata(null);
    public static final MetadataSectionSerializer<ForgeTextureMetadata> SERIALIZER = new Serializer();

    public static ForgeTextureMetadata forResource(Resource resource)
    {
        ForgeTextureMetadata metadata = resource.getMetadata(SERIALIZER);
        return metadata == null ? EMPTY : metadata;
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
        @Nonnull
        public String getMetadataSectionName()
        {
            return "forge";
        }

        @Override
        @Nonnull
        public ForgeTextureMetadata fromJson(JsonObject json)
        {
            @Nullable
            ITextureAtlasSpriteLoader loader;
            if (json.has("loader"))
            {
                ResourceLocation loaderName = new ResourceLocation(GsonHelper.getAsString(json, "loader"));
                loader = MinecraftForgeClient.getTextureAtlasSpriteLoader(loaderName);
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
