/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.world;

import com.mojang.serialization.Codec;
import java.util.function.Function;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.RegistryOps;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraftforge.common.world.ModifiableStructureInfo.StructureInfo;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * JSON-serializable structure modifier.
 * Requires a {@link Codec} to deserialize structure modifiers from structure modifier jsons.
 * <p>
 * Structure modifier jsons have the following json format:
 * <pre>
 * {
 *   "type": "yourmod:yourserializer", // Indicates a registered structure modifier serializer
 *   // Additional fields can be specified here according to the codec
 * }
 * </pre>
 * <p>
 * Datapacks can also disable a structure modifier by overriding the json and using {@code "type": "forge:none"}.</p>
 */
public interface StructureModifier
{
    /**
     * Codec for (de)serializing structure modifiers inline.
     * Mods can use this for data generation.
     */
    Codec<StructureModifier> DIRECT_CODEC = ExtraCodecs.lazyInitializedCodec(() -> ForgeRegistries.STRUCTURE_MODIFIER_SERIALIZERS.get().getCodec())
            .dispatch(StructureModifier::codec, Function.identity());

    /**
     * Codec for referring to structure modifiers by id in other datapack registry files.
     * Can only be used with {@link RegistryOps}.
     */
    Codec<Holder<StructureModifier>> REFERENCE_CODEC = RegistryFileCodec.create(ForgeRegistries.Keys.STRUCTURE_MODIFIERS, DIRECT_CODEC);

    /**
     * Codec for referring to structure modifiers by id, list of id, or tags.
     * Can only be used with {@link RegistryOps}.
     */
    Codec<HolderSet<StructureModifier>> LIST_CODEC = RegistryCodecs.homogeneousList(ForgeRegistries.Keys.STRUCTURE_MODIFIERS, DIRECT_CODEC);

    /**
     * Modifies the information via the provided structure builder.
     * Allows mob spawns and world-gen features to be added or removed,
     * and climate and client effects to be modified.
     *
     * @param structure the named structure being modified (with original data readable).
     * @param phase structure modification phase. Structure modifiers apply in each phase in order of the enum constants.
     * @param builder mutable structure info builder. Apply changes to this.
     */
    void modify(Holder<Structure> structure, Phase phase, StructureInfo.Builder builder);

    /**
     * @return the codec which serializes and deserializes this structure modifier
     */
    Codec<? extends StructureModifier> codec();

    enum Phase
    {
        /**
         * Catch-all for anything that needs to run before standard phases.
         */
        BEFORE_EVERYTHING,
        /**
         * Additional mob spawns, etc.
         */
        ADD,
        /**
         * Removal of mob spawns, etc.
         */
        REMOVE,
        /**
         * Alteration of values.
         */
        MODIFY,
        /**
         * Catch-all for anything that needs to run after standard phases.
         */
        AFTER_EVERYTHING
    }
}
