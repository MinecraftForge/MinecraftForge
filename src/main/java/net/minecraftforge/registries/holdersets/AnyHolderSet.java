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

import net.minecraft.core.Holder;
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
public record AnyHolderSet<T>(Registry<T> registry) implements ICustomHolderSet<T>
{
    public static <T> Codec<? extends ICustomHolderSet<T>> codec(ResourceKey<? extends Registry<T>> registryKey, Codec<Holder<T>> holderCodec, boolean forceList)
    {
        return RegistryOps.retrieveRegistry(registryKey)
            .xmap(AnyHolderSet::new, AnyHolderSet::registry)
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
        return registry.holders().map(Function.identity());
    }

    @Override
    public int size()
    {
        return this.registry.size();
    }

    @Override
    public Either<TagKey<T>, List<Holder<T>>> unwrap()
    {
        return Either.right(this.stream().toList());
    }

    @Override
    public Optional<Holder<T>> getRandomElement(RandomSource random)
    {
        return this.registry.getRandom(random);
    }

    @Override
    public Holder<T> get(int i)
    {
        return this.registry.getHolder(i).orElseThrow(() -> new NoSuchElementException("No element " + i + " in registry " + this.registry.key()));
    }

    @Override
    public boolean contains(Holder<T> holder)
    {
        return holder.unwrapKey().map(this.registry::containsKey).orElse(false);
    }

    @Override
    public boolean isValidInRegistry(Registry<T> registry)
    {
        return this.registry == registry;
    }

    @Override
    public String toString()
    {
        return "AnySet(" + this.registry.key() + ")";
    }
}