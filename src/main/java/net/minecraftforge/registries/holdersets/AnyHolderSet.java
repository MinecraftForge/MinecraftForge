/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.registries.holdersets;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;

import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderOwner;
import net.minecraft.core.Registry;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraftforge.common.ForgeMod;

/**
 * <p>Holderset that represents all elements of a registry. Json format:</p>
 * <pre>
 * {
 *   "type": "forge:any"
 * }
 * </pre>
 */
public record AnyHolderSet<T>(HolderLookup.RegistryLookup<T> registryLookup) implements ICustomHolderSet<T>
{
    public static <T> Codec<? extends ICustomHolderSet<T>> codec(ResourceKey<? extends Registry<T>> registryKey, Codec<Holder<T>> holderCodec, boolean forceList)
    {
        return RegistryOps.retrieveRegistryLookup(registryKey)
            .xmap(AnyHolderSet::new, AnyHolderSet::registryLookup)
            .codec();
    }

    @Override
    public HolderSetType type()
    {
        return ForgeMod.ANY_HOLDER_SET.get();
    }

    @Override
    public Iterator<Holder<T>> iterator()
    {
        return this.stream().iterator();
    }

    @Override
    public Stream<Holder<T>> stream()
    {
        return this.registryLookup.listElements().map(Function.identity());
    }

    @Override
    public int size()
    {
        return (int) this.stream().count();
    }

    @Override
    public Either<TagKey<T>, List<Holder<T>>> unwrap()
    {
        return Either.right(this.stream().toList());
    }

    @Override
    public Optional<Holder<T>> getRandomElement(RandomSource random)
    {
        return Util.getRandomSafe(this.stream().toList(), random);
    }

    @Override
    public Holder<T> get(int i)
    {
        List<Holder<T>> holders = this.stream().toList();
        Holder<T> holder = i >= holders.size() ? null : holders.get(i);
        if (holder == null)
            throw new NoSuchElementException("No element " + i + " in registry " + this.registryLookup.key());

        return holder;
    }

    @Override
    public boolean contains(Holder<T> holder)
    {
        return holder.unwrapKey().map(key -> this.registryLookup.listElementIds().anyMatch(key::equals)).orElse(false);
    }

    @Override
    public boolean canSerializeIn(HolderOwner<T> holderOwner)
    {
        return this.registryLookup.canSerializeIn(holderOwner);
    }

    @Override
    public Optional<TagKey<T>> unwrapKey()
    {
        return Optional.empty();
    }

    @Override
    public String toString()
    {
        return "AnySet(" + this.registryLookup.key() + ")";
    }
}