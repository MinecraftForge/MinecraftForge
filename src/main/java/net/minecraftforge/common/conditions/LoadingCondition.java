/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.conditions;

import com.mojang.serialization.Codec;

import net.minecraft.util.ExtraCodecs;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Function;
import java.util.function.Predicate;

/**
 * An {@link LoadingCondition} represents any type of loading condition that can be applied to a JSON object.
 */
public interface LoadingCondition extends Predicate<IConditionContext>
{
    /**
     * Codec for (de)serializing conditions inline.
     * Mods can use this for data generation.
     */
    Codec<LoadingCondition> DIRECT_CODEC = ExtraCodecs.lazyInitializedCodec(() -> ForgeRegistries.CONDITION_SERIALIZERS.get().getCodec())
            .dispatch(LoadingCondition::codec, Function.identity());

    /**
     * @return the codec which serializes and deserializes this condition
     */
    Codec<? extends LoadingCondition> codec();

    /**
     * Tests if this condition is met, given the context.
     * @param context The {@linkplain IConditionContext Condition Context}
     * @return {@code true} if this condition is met in this context.
     */
    @Override
    boolean test(IConditionContext context);
}