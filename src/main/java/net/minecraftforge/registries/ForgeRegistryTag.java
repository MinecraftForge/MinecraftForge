/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.registries;

import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraftforge.registries.tags.ITag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Spliterator;
import java.util.stream.Stream;

class ForgeRegistryTag<V> implements ITag<V>
{
    private final TagKey<V> key;
    @Nullable
    private HolderSet<V> holderSet;
    @Nullable
    private List<V> contents;

    ForgeRegistryTag(TagKey<V> key)
    {
        this.key = key;
    }

    @Override
    public TagKey<V> getKey()
    {
        return this.key;
    }

    @NotNull
    @Override
    public Iterator<V> iterator()
    {
        return this.getContents().iterator();
    }

    @Override
    public Spliterator<V> spliterator()
    {
        return this.getContents().spliterator();
    }

    @Override
    public boolean isEmpty()
    {
        return this.getContents().isEmpty();
    }

    @Override
    public int size()
    {
        return this.getContents().size();
    }

    @Override
    public Stream<V> stream()
    {
        return this.getContents().stream();
    }

    @Override
    public boolean contains(V value)
    {
        return this.getContents().contains(value);
    }

    @Override
    public Optional<V> getRandomElement(RandomSource random)
    {
        return Util.getRandomSafe(this.getContents(), random);
    }

    @Override
    public boolean isBound()
    {
        return this.holderSet != null;
    }

    List<V> getContents()
    {
        if (this.contents == null && this.holderSet != null)
            this.contents = this.holderSet.stream().map(Holder::value).toList();

        return this.contents == null ? List.of() : this.contents;
    }

    void bind(@Nullable HolderSet<V> holderSet)
    {
        this.holderSet = holderSet;
        this.contents = null;
    }

    @Override
    public String toString()
    {
        return "Tag[" +
                "key=" + this.key + ", " +
                "contents=" + getContents() + ']';
    }
}
