/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.model;

import java.util.function.Function;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.BlockElementFace;
import net.minecraft.util.ExtraCodecs;

/**
 * Holds extra data that may be injected into a face.<p>
 * Used by {@link ItemLayerModel}, {@link BlockElement} and {@link BlockElementFace}
 * 
 * @param color Color in ARGB format
 * @param blockLight Block Light for this face from 0-15 (inclusive)
 * @param skyLight Sky Light for this face from 0-15 (inclusive)
 */
public record ForgeFaceData(int color, int blockLight, int skyLight)
{

    public static final ForgeFaceData DEFAULT = new ForgeFaceData(0xFFFFFFFF, 0, 0);

    public static final Codec<Integer> COLOR = new ExtraCodecs.EitherCodec<>(Codec.INT, Codec.STRING).xmap(
            either -> either.map(Function.identity(), str -> (int) Long.parseLong(str, 16)),
            color -> Either.right(Integer.toHexString(color)));

    public static final Codec<ForgeFaceData> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            COLOR.optionalFieldOf("color", 0xFFFFFFFF).forGetter(ForgeFaceData::color),
            Codec.intRange(0, 15).optionalFieldOf("block_light", 0).forGetter(ForgeFaceData::blockLight),
            Codec.intRange(0, 15).optionalFieldOf("sky_light", 0).forGetter(ForgeFaceData::skyLight))
            .apply(builder, ForgeFaceData::new));
}