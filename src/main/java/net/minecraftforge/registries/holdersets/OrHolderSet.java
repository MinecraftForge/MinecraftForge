/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.registries.holdersets;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.mojang.serialization.Codec;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderOwner;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.resources.HolderSetCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraftforge.common.ForgeMod;

/**
 * <p>Holderset that represents a union of other holdersets. Json format:</p>
 * <pre>
 * {
 *   "type": "forge:or",
 *   "values":
 *   [
 *      // list of sub-holdersets (strings, lists, or objects)
 *   ]
 * }
 * </pre>
 */
public class OrHolderSet<T> extends CompositeHolderSet<T>
{
    public static <T> Codec<? extends ICustomHolderSet<T>> codec(ResourceKey<? extends Registry<T>> registryKey, Codec<Holder<T>> holderCodec, boolean forceList)
    {
        return HolderSetCodec.create(registryKey, holderCodec, forceList)
            .listOf()
            .xmap(OrHolderSet::new, CompositeHolderSet::homogenize)
            .fieldOf("values")
            .codec();
    }

    public OrHolderSet(List<HolderSet<T>> values)
    {
        super(values);
    }

    @Override
    public HolderSetType type()
    {
        return ForgeMod.OR_HOLDER_SET.get();
    }

    @Override
    protected Set<Holder<T>> createSet()
    {
        return this.getComponents().stream().flatMap(HolderSet::stream).collect(Collectors.toSet());
    }

    @Override
    public String toString()
    {
        return "OrSet[" + this.getComponents() + "]";
    }
}