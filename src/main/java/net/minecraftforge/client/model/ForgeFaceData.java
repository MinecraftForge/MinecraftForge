/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.model;

import javax.annotation.Nullable;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;

/**
 * Holds extra data that may be injected into a face.<p>
 * Used by {@link BlockElement} and {@link BlockElementFace}
 *
 * @param ambientOcclusion If this face has AO
 * @param calculateNormals If we should manually calculate the normals for this block or inherit facing normals like vanilla
 */
public record ForgeFaceData(boolean ambientOcclusion, boolean calculateNormals) {
    public ForgeFaceData(boolean ambientOcclusion) {
        this(ambientOcclusion, false);
    }

    public static final ForgeFaceData DEFAULT = new ForgeFaceData(true, false);

    public static final Codec<ForgeFaceData> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            Codec.BOOL.optionalFieldOf("ambient_occlusion", true).forGetter(ForgeFaceData::ambientOcclusion),
            Codec.BOOL.optionalFieldOf("calculate_normals", false).forGetter(ForgeFaceData::calculateNormals))
            .apply(builder, ForgeFaceData::new));

    /**
     * Parses a ForgeFaceData from JSON
     * @param obj The JsonObject to parse from, weakly-typed to JsonElement to reduce logic complexity.
     * @param fallback What to return if the first parameter is null.
     * @return The parsed ForgeFaceData, or the fallback parameter if the first parameter is null.
     * @throws JsonParseException
     */
    @Nullable
    public static ForgeFaceData read(@Nullable JsonElement obj, @Nullable ForgeFaceData fallback) throws JsonParseException {
        if (obj == null)
            return fallback;
        return CODEC.parse(JsonOps.INSTANCE, obj).getOrThrow(JsonParseException::new);
    }
}