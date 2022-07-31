/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.registries;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jetbrains.annotations.Nullable;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.resources.HolderSetCodec;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraftforge.common.ForgeMod;

public final class ForgeHolderSets
{
    private ForgeHolderSets() {} // Organizational class
    
    /**
     * <p>Holderset that represents all elements of a registry. Json format:</p>
     * <pre>
     * {
     *   "type": "forge:any"
     * }
     * </pre>
     */
    public static record AnyHolderSet<T>(Registry<T> registry) implements ICustomHolderSet<T>
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
    
    /**
     * <p>Holderset that represents an intersection of other holdersets. Json format:</p>
     * <pre>
     * {
     *   "type": "forge:and",
     *   "values":
     *   [
     *      // list of sub-holdersets (strings, lists, or objects)
     *   ]
     * }
     * </pre>
     */
    public static class AndHolderSet<T> extends CompositeHolderSet<T>
    {
        public static <T> Codec<? extends ICustomHolderSet<T>> codec(ResourceKey<? extends Registry<T>> registryKey, Codec<Holder<T>> holderCodec, boolean forceList)
        {
            return HolderSetCodec.create(registryKey, holderCodec, forceList)
                .listOf()
                .xmap(AndHolderSet::new, AndHolderSet::getComponents)
                .fieldOf("values")
                .codec();
        }
        
        public AndHolderSet(List<HolderSet<T>> values)
        {
            super(values);
        }

        @Override
        public HolderSetType type()
        {
            return ForgeMod.AND_HOLDER_SET.get();
        }

        @Override
        protected Set<Holder<T>> createSet()
        {
            List<HolderSet<T>> components = this.getComponents();
            if (components.size() < 1)
            {
                return Set.of();
            }
            if (components.size() == 1)
            {
                return components.get(0).stream().collect(Collectors.toSet());
            }

            List<HolderSet<T>> remainingComponents = components.subList(1, components.size());
            return components.get(0)
                .stream()
                .filter(holder -> remainingComponents.stream().allMatch(holderset -> holderset.contains(holder)))
                .collect(Collectors.toSet());
        }

        @Override
        public String toString()
        {
            return "AndSet[" + this.getComponents() + "]";
        }
    }

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
    public static class OrHolderSet<T> extends CompositeHolderSet<T>
    {
        public static <T> Codec<? extends ICustomHolderSet<T>> codec(ResourceKey<? extends Registry<T>> registryKey, Codec<Holder<T>> holderCodec, boolean forceList)
        {
            return HolderSetCodec.create(registryKey, holderCodec, forceList)
                .listOf()
                .xmap(OrHolderSet::new, OrHolderSet::getComponents)
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

    /**
     * <p>Holderset that represents all elements that exist in one holderset but not another.
     * This is preferable over forge:not when the set of allowed elements is small relative to the size of the entire registry.
     * Json format:</p>
     * <pre>
     * {
     *   "type": "forge:exclusion",
     *   "include": "included_holderset", // string, list, or object
     *   "exclude": "included_holderset" // string, list, or object
     * }
     * </pre>
     */
    public static class ExclusionHolderSet<T> extends CompositeHolderSet<T>
    {
        public static <T> Codec<? extends ICustomHolderSet<T>> codec(ResourceKey<? extends Registry<T>> registryKey, Codec<Holder<T>> holderCodec, boolean forceList)
        {
            Codec<HolderSet<T>> holderSetCodec = HolderSetCodec.create(registryKey, holderCodec, forceList);
            return RecordCodecBuilder.<ExclusionHolderSet<T>>create(builder -> builder.group(
                    holderSetCodec.fieldOf("include").forGetter(ExclusionHolderSet::include),
                    holderSetCodec.fieldOf("exclude").forGetter(ExclusionHolderSet::exclude)
                ).apply(builder, ExclusionHolderSet::new));
        }
        
        private final HolderSet<T> include;
        private final HolderSet<T> exclude;
        
        public HolderSet<T> include() { return this.include; }
        public HolderSet<T> exclude() { return this.exclude; }
        
        public ExclusionHolderSet(HolderSet<T> include, HolderSet<T> exclude)
        {
            super(List.of(include, exclude));
            this.include = include;
            this.exclude = exclude;
        }
        
        @Override
        public HolderSetType type()
        {
            return ForgeMod.EXCLUSION_HOLDER_SET.get();
        }

        @Override
        protected Set<Holder<T>> createSet()
        {
            return this.include.stream().filter(holder -> !this.exclude.contains(holder)).collect(Collectors.toSet());
        }
        
        @Override
        public String toString()
        {
            return "ExclusionSet{include=" + this.include + ", exclude=" + this.exclude + "}";
        }
    }
    
    /**
     * <p>Holderset that represents all elements of a registry not present in another holderset.
     * forge:exclusion is preferable when the number of allowed elements is small relative to the size of the registry.
     * Json format:</p>
     * <pre>
     * {
     *   "type": "forge:not",
     *   "value": "not_this_holderset" // string, list, or object
     * }
     * </pre>
     */
    // this doesn't extend CompositeHolderSet because it doesn't need to cache a set
    public static class NotHolderSet<T> implements ICustomHolderSet<T>
    {
        public static <T> Codec<? extends ICustomHolderSet<T>> codec(ResourceKey<? extends Registry<T>> registryKey, Codec<Holder<T>> holderCodec, boolean forceList)
        {
            return RecordCodecBuilder.<NotHolderSet<T>>create(builder -> builder.group(
                    RegistryOps.retrieveRegistry(registryKey).forGetter(NotHolderSet::registry),
                    HolderSetCodec.create(registryKey, holderCodec, forceList).fieldOf("value").forGetter(NotHolderSet::value)
                ).apply(builder, NotHolderSet::new));
        }

        private final List<Runnable> owners = new ArrayList<>();
        private final Registry<T> registry;
        private final HolderSet<T> value;
        @Nullable
        private List<Holder<T>> list = null;
        
        public Registry<T> registry() { return this.registry; }
        public HolderSet<T> value() { return this.value; }
        
        public NotHolderSet(Registry<T> registry, HolderSet<T> value)
        {
            this.registry = registry;
            this.value = value;
            this.value.addInvalidationListener(this::invalidate);
        }

        @Override
        public HolderSetType type()
        {
            return ForgeMod.NOT_HOLDER_SET.get();
        }
        
        @Override
        public void addInvalidationListener(Runnable runnable)
        {
            this.owners.add(runnable);
        }
        
        @Override
        public Iterator<Holder<T>> iterator()
        {
            return this.getList().iterator();
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
        public Optional<Holder<T>> getRandomElement(RandomSource random)
        {
            List<Holder<T>> list = this.getList();
            int size = list.size();
            return size > 0
                ? Optional.of(list.get(random.nextInt(size)))
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
            return !this.value.contains(holder);
        }

        @Override
        public boolean isValidInRegistry(Registry<T> registry)
        {
            return this.registry == registry;
        }

        @Override
        public String toString()
        {
            return "NotSet(" + this.value + ")";
        }
        
        private List<Holder<T>> getList()
        {
            List<Holder<T>> thisList = this.list;
            if (thisList == null)
            {
                List<Holder<T>> list = this.registry.holders()
                    .filter(holder -> !this.value.contains(holder))
                    .map(holder -> (Holder<T>)holder)
                    .toList();
                this.list = list;
                return list;
            }
            else
            {
                return thisList;
            }
        }
        
        private void invalidate()
        {
            this.list = null;
            for (Runnable runnable : this.owners)
            {
                runnable.run();
            }
        }
    }
    
    /**
     * Composite holdersets have component holdersets and possibly owner holdersets
     * (which have this holderset as a component).
     * When their component holderset(s) invalidate, they clear any cached data and then
     * invalidate their owner holdersets.
     */
    public static abstract class CompositeHolderSet<T> implements ICustomHolderSet<T>
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
}
