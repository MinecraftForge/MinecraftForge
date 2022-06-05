/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.world;

import com.mojang.serialization.Codec;

import net.minecraftforge.registries.ForgeRegistryEntry;

/**
 * Registrable serializer for a type of {@link BiomeModifier}.
 * TODO 1.19: After the forgeregistry rewrite, this class could be discarded in favor of just registering the codecs directly.
 */
public class BiomeModifierSerializer<T extends BiomeModifier> extends ForgeRegistryEntry<BiomeModifierSerializer<?>>
{
    private final Codec<T> codec;
    
    /**
     * @param codec Serialization codec for this type of biome modifier
     */
    public BiomeModifierSerializer(Codec<T> codec)
    {
        this.codec = codec;
    }
    
    /**
     * {@return The serialization codec for this type of biome modifier}
     */
    public Codec<T> codec()
    {
        return this.codec;
    }
}