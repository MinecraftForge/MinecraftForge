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
import net.minecraft.resources.RegistryOps;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.common.world.ModifiableBiomeInfo.BiomeInfo;
import net.minecraftforge.registries.ForgeRegistries;
import java.util.function.Function;

/**
 * JSON-serializable biome modifier.
 * Requires a {@link Codec} to deserialize biome modifiers from biome modifier jsons.
 * <p>
 * Biome modifier jsons have the following json format:
 * <pre>
 * {
 *   "type": "yourmod:yourserializer", // Indicates a registered biome modifier serializer
 *   // Additional fields can be specified here according to the codec
 * }
 * </pre>
 * <p>
 * Datapacks can also disable a biome modifier by overriding the json and using {@code "type": "forge:none"}.</p>
 */
public interface BiomeModifier
{
    /**
     * Codec for (de)serializing biome modifiers inline.
     * Mods can use this for data generation.
     */
    Codec<BiomeModifier> DIRECT_CODEC = ExtraCodecs.lazyInitializedCodec(() -> ForgeRegistries.BIOME_MODIFIER_SERIALIZERS.get().getCodec())
            .dispatch(BiomeModifier::codec, Function.identity());

    /**
     * Codec for referring to biome modifiers by id in other datapack registry files.
     * Can only be used with {@link RegistryOps}.
     */
    Codec<Holder<BiomeModifier>> REFERENCE_CODEC = RegistryFileCodec.create(ForgeRegistries.Keys.BIOME_MODIFIERS, DIRECT_CODEC);

    /**
     * Codec for referring to biome modifiers by id, list of id, or tags.
     * Can only be used with {@link RegistryOps}.
     */
    Codec<HolderSet<BiomeModifier>> LIST_CODEC = RegistryCodecs.homogeneousList(ForgeRegistries.Keys.BIOME_MODIFIERS, DIRECT_CODEC);

    /**
     * Modifies the information via the provided biome builder.
     * Allows mob spawns and world-gen features to be added or removed,
     * and climate and client effects to be modified.
     *
     * @param biome the named biome being modified (with original data readable).
     * @param phase biome modification phase. Biome modifiers apply in each phase in order of the enum constants.
     * @param builder mutable biome info builder. Apply changes to this.
     */
    void modify(Holder<Biome> biome, Phase phase, BiomeInfo.Builder builder);

    /**
     * @return the codec which serializes and deserializes this biome modifier
     */
    Codec<? extends BiomeModifier> codec();

    enum Phase
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
