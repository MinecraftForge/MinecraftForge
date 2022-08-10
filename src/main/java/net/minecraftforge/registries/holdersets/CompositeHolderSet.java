/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.registries.holdersets;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import org.jetbrains.annotations.Nullable;

import com.mojang.datafixers.util.Either;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;

/**
 * Composite holdersets have component holdersets and possibly owner holdersets
 * (which have this holderset as a component).
 * When their component holderset(s) invalidate, they clear any cached data and then
 * invalidate their owner holdersets.
 */
public abstract class CompositeHolderSet<T> implements ICustomHolderSet<T>
{
    private final List<Runnable> owners = new ArrayList<>();
    private final List<HolderSet<T>> components;

    @Nullable
    private Set<Holder<T>> set = null;
    @Nullable
    private List<Holder<T>> list = null;
    
    public CompositeHolderSet(List<HolderSet<T>> components)
    {
        this.components = components;
        for (HolderSet<T> holderset : components)
        {
            holderset.addInvalidationListener(this::invalidate);
        }
    }
    
    /**
     * {@return immutable Set of Holders given this composite holderset's component holdersets}
     */
    protected abstract Set<Holder<T>> createSet();
    
    public List<HolderSet<T>> getComponents()
    {
        return this.components;
    }
    
    public Set<Holder<T>> getSet()
    {
        Set<Holder<T>> thisSet = this.set;
        if (thisSet == null)
        {
            Set<Holder<T>> set = this.createSet();
            this.set = set;
            return set;
        }
        else
        {
            return thisSet;
        }
    }
    
    public List<Holder<T>> getList()
    {
        List<Holder<T>> thisList = this.list;
        if (thisList == null)
        {
            List<Holder<T>> list = List.copyOf(this.getSet());
            this.list = list;
            return list;
        }
        else
        {
            return thisList;
        }
    }
    
    @Override
    public void addInvalidationListener(Runnable runnable)
    {
        this.owners.add(runnable);
    }
    
    private void invalidate()
    {
        this.set = null;
        this.list = null;
        for (Runnable runnable : this.owners)
        {
            runnable.run();
        }
    }

    @Override
    public Stream<Holder<T>> stream()
    {
        return this.getList().stream();
    }

    @Override
    public int size()
    {
        return this.getList().size();
    }

    @Override
    public Either<TagKey<T>, List<Holder<T>>> unwrap()
    {
        return Either.right(this.getList());
    }

    @Override
    public Optional<Holder<T>> getRandomElement(RandomSource rand)
    {
        List<Holder<T>> list = this.getList();
        int size = list.size();
        return size > 0
            ? Optional.of(list.get(rand.nextInt(size)))
            : Optional.empty();
    }

    @Override
    public Holder<T> get(int i)
    {
        return this.getList().get(i);
    }

    @Override
    public boolean contains(Holder<T> holder)
    {
        return this.getSet().contains(holder);
    }

    @Override
    public boolean isValidInRegistry(Registry<T> registry)
    {
        for (HolderSet<T> component : this.components)
        {
            if (!component.isValidInRegistry(registry))
            {
                return false;
            }
        }
        return true;
    }

    @Override
    public Iterator<Holder<T>> iterator()
    {
        return this.getList().iterator();
    }
}