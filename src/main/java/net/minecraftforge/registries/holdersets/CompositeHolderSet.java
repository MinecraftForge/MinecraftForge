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

import net.minecraft.core.HolderOwner;
import org.jetbrains.annotations.Nullable;

import com.mojang.datafixers.util.Either;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
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
    public boolean canSerializeIn(HolderOwner<T> holderOwner)
    {
        for (HolderSet<T> component : this.components)
        {
            if (!component.canSerializeIn(holderOwner))
            {
                return false;
            }
        }
        return true;
    }

    @Override
    public Optional<TagKey<T>> unwrapKey()
    {
        return Optional.empty();
    }

    @Override
    public Iterator<Holder<T>> iterator()
    {
        return this.getList().iterator();
    }
    
    /**
     * Maps the sub-holdersets of this composite such that,
     * if the list contains more than one element, and is non-homogenous,
     * each element of the list will serialize as an object.
     * Prevents crashes from trying to serialize non-homogenous lists to NBT.
     * 
     * Lists are considered non-homogenous if it contains more than one serialization type of holderset.
     * Holdersets may be serialized as strings, lists, or maps.
     * @see {@link #isHomogenous}
     * @return List of holdersets with homogenous serialization behavior.
     * Returns a new List if size > 1 and serialization would be non-homogenous,
     * otherwise returns the composite's existing List.
     */
    public List<HolderSet<T>> homogenize()
    {
        List<HolderSet<T>> components = this.getComponents();
        
        if (this.isHomogenous())
        {
            return components;
        }
        
        List<HolderSet<T>> outputs = new ArrayList<>();
        for (HolderSet<T> holderset : components)
        {
            if (holderset instanceof ICustomHolderSet<T>) // serializes to object already
            {
                outputs.add(holderset);
            }
            else // serializes to string or list
            {
                outputs.add(new OrHolderSet<>(List.of(holderset)));
            }
        }
        return outputs;
    }
    
    /**
     * @return True if all of our sub-holdersets have the same {@link SerializationType} (string, list, or object).
     * False if we have more than one holderset AND if either we have more than one serialization type among them,
     * or any holderset is {@link SerializationType.UNKNOWN}.
     */
    public boolean isHomogenous()
    {
        final List<HolderSet<T>> holderSets = this.getComponents();
        
        if (holderSets.size() < 2)
        {
            return true;
        }
        
        // Get the first holderset and check if any subsequent holdersets are different
        SerializationType firstType = holderSets.get(0).serializationType();
        
        // If any holderset has an unknown type, then we cannot assume we are homogenous
        if (firstType == SerializationType.UNKNOWN)
        {
            return false;
        }
        
        final int size = holderSets.size();
        for (int i = 1; i < size; i++)
        {
            final SerializationType type = holderSets.get(i).serializationType();
            if (type != firstType) // don't need to check type == UNKNOWN again because type != firstType covers it
            {
                return false;
            }
        }
        
        return true;
    }
}