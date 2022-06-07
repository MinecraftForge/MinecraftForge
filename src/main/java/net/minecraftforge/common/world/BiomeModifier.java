/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.world;

import com.mojang.serialization.Codec;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.common.world.ModifiableBiomeInfo.BiomeInfo;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistryEntry;

/**
 * <p>JSON-serializable biome modifier. Implement this in a subclass and register a {@link BiomeModifierSerializer}
 * for that class to read your biome modifier from biome modifier jsons.</p>
 * <p>Biome Modifier jsons have the following json format:</p>
 * <pre>
 * {
 *   "type": "yourmod:yourserializer", // Indicates a registered serializer/codec.
 *   // Additional fields can be specified here according to the codec.
 * }
 * </pre>
 * <p>Datapacks can also disable a biome modifier by overriding the json and using {@code "type": "forge:none"}.</p>
 * <p>TODO 1.19: Remove `extends ForgeRegistryEntry` and make this an interface.</p>
 */
public abstract class BiomeModifier extends ForgeRegistryEntry<BiomeModifier>
{
    /**
     * Codec for de/serializing biome modifiers inline. Mods can use this for datagen.
     */
    // not safe to use IForgeRegistry#getCodec in static init because our forge registry doesn't exist until the new-registry-event
    public static final Codec<BiomeModifier> DIRECT_CODEC = ResourceLocation.CODEC.<BiomeModifierSerializer<?>>xmap(
            id -> ForgeRegistries.BIOME_MODIFIER_SERIALIZERS.get().getValue(id),
            BiomeModifierSerializer::getRegistryName)
        .<BiomeModifier>dispatch(
            BiomeModifier::type,
            BiomeModifierSerializer::codec);
    
    /**
     * Codec for referring to biome modifiers by id in other datapack registry files. Can only be used with RegistryOps.
     */
    public static final Codec<Holder<BiomeModifier>> REFERENCE_CODEC = RegistryFileCodec.create(ForgeRegistries.Keys.BIOME_MODIFIERS, DIRECT_CODEC);
    
    /**
     * Codec for referring to biome modifiers by id, list of id, or tags. Can only be used with RegistryOps.
     */
    public static final Codec<HolderSet<BiomeModifier>> LIST_CODEC = RegistryCodecs.homogeneousList(ForgeRegistries.Keys.BIOME_MODIFIERS, DIRECT_CODEC);
    
    /**
     * Modifies the information via the provided biome builder.
     * Allows mob spawns and worldgen features to be added or removed,
     * and climate and client effects to be modified.
     * @param biome the named biome being modified (with original data readable).
     * @param phase Biome modification phase. Biome modifiers apply in each phase in enum order.
     * @param builder mutable biome info builder. Apply changes to this.
     */
    public abstract void modify(Holder<Biome> biome, Phase phase, BiomeInfo.Builder builder);
    
    /**
     * @return Registered BiomeModifierSerializer.
     */
    public abstract BiomeModifierSerializer<?> type();
    
    public static enum Phase
    {
        /**
         * Catch-all for anything that needs to run before standard phases.
         */
        BEFORE_EVERYTHING,
        /**
         * Additional features, mob spawns, etc.
         */
        ADD,
        /**
         * Removal of features, mob spawns, etc.
         */
        REMOVE,
        /**
         * Alteration of values such as climate or colors.
         */
        MODIFY,
        /**
         * Catch-all for anything that needs to run after standard phases.
         */
        AFTER_EVERYTHING
    }
}
