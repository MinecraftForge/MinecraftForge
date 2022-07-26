/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.registries;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.mojang.serialization.DataResult;
import cpw.mods.modlauncher.api.LamdbaExceptionUtils;
import net.minecraft.util.RandomSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class NamespacedHolderHelper<T>
{
    private static final Logger LOGGER = LogManager.getLogger();
    private final ForgeRegistry<T> owner;
    private final Registry<T> self;
    @Nullable
    private final ResourceLocation defaultKey;
    @Nullable
    private final Function<T, Holder.Reference<T>> holderLookup;
    private final Multimap<TagKey<T>, Supplier<T>> optionalTags = Multimaps.newSetMultimap(new IdentityHashMap<>(), HashSet::new);

    private boolean frozen = false; // Frozen is vanilla's variant of locked, but it can be unfrozen
    private List<Holder.Reference<T>> holdersSorted;
    private ObjectList<Holder.Reference<T>> holdersById = new ObjectArrayList<>(256);
    private Map<ResourceLocation, Holder.Reference<T>> holdersByName = new HashMap<>();
    private Map<T, Holder.Reference<T>> holders = new IdentityHashMap<>();
    private volatile Map<TagKey<T>, HolderSet.Named<T>> tags = new IdentityHashMap<>();
    private Holder.Reference<T> defaultHolder;

    NamespacedHolderHelper(ForgeRegistry<T> owner, Registry<T> self, @Nullable ResourceLocation defaultKey, @Nullable Function<T, Holder.Reference<T>> holderLookup)
    {
        this.owner = owner;
        this.self = self;
        this.defaultKey = defaultKey;
        this.holderLookup = holderLookup;
    }


    Optional<Holder<T>> getHolder(int id)
    {
        return id >= 0 && id < this.holdersById.size() ? Optional.ofNullable(this.holdersById.get(id)) : Optional.empty();
    }

    Optional<Holder<T>> getHolder(ResourceKey<T> key)
    {
        return Optional.ofNullable(this.holdersByName.get(key.location()));
    }

    Optional<Holder<T>> getHolder(ResourceLocation location)
    {
        return Optional.ofNullable(this.holdersByName.get(location));
    }

    Optional<Holder<T>> getHolder(T value)
    {
        return Optional.ofNullable(this.holders.get(value));
    }

    DataResult<Holder<T>> getOrCreateHolder(ResourceKey<T> key)
    {
        Holder.Reference<T> ref = this.holdersByName.get(key.location());
        if (ref == null)
        {
            if (this.holderLookup != null)
                return DataResult.error("This registry can't create new holders without value (requested key: " + key + ")");

            if (this.frozen)
                return DataResult.error("Registry is already frozen (trying to add key " + key + ")");

            ref = Holder.Reference.createStandAlone(this.self, key);
            this.holdersByName.put(key.location(), ref);
        }
        return DataResult.success(ref);
    }

    Holder<T> getOrCreateHolderOrThrow(ResourceKey<T> key)
    {
        return getOrCreateHolder(key).getOrThrow(false, msg ->
        {
            throw new IllegalStateException(msg);
        });
    }

    Optional<Holder<T>> getRandom(RandomSource rand)
    {
        Optional<Holder<T>> ret = Util.getRandomSafe(this.getSortedHolders(), rand).map(Holder::hackyErase);
        if (this.defaultKey != null)
            ret = ret.or(() -> Optional.of(this.defaultHolder));
        return ret;
    }

    Stream<Holder.Reference<T>> holders()
    {
       return this.getSortedHolders().stream();
    }

    boolean isKnownTagName(TagKey<T> name)
    {
        return this.tags.containsKey(name);
    }

    Stream<Pair<TagKey<T>, HolderSet.Named<T>>> getTags()
    {
        return this.tags.entrySet().stream().map(e -> Pair.of(e.getKey(), e.getValue()));
    }

    HolderSet.Named<T> getOrCreateTag(TagKey<T> name)
    {
        HolderSet.Named<T> named = this.tags.get(name);
        if (named == null)
        {
           named = createTag(name);
           // They use volatile and set it this way to not have the performance penalties of synced read access. But this makes a lot of new maps.. We need to look into performance alternatives.
           Map<TagKey<T>, HolderSet.Named<T>> map = new IdentityHashMap<>(this.tags);
           map.put(name, named);
           this.tags = map;
        }

        return named;
    }

    void addOptionalTag(TagKey<T> name, @NotNull Set<? extends Supplier<T>> defaults)
    {
        this.optionalTags.putAll(name, defaults);
    }

    Stream<TagKey<T>> getTagNames()
    {
        return this.tags.keySet().stream();
    }

    //TODO: Move this to ValidateCallback?
    Registry<T> freeze()
    {
       this.frozen = true;
       List<ResourceLocation> unregisterd = this.holdersByName.entrySet().stream()
           .filter(e -> !e.getValue().isBound())
           .map(e -> e.getKey()).sorted().toList();

       if (!unregisterd.isEmpty())
          throw new IllegalStateException("Unbound values in registry " + this.self.key() + ": " + unregisterd.stream().map(ResourceLocation::toString).collect(Collectors.joining(", \n\t")));

       if (this.holderLookup != null)
       {
           List<Holder.Reference<T>> intrusive = this.holders.values().stream().filter(h -> h.getType() == Holder.Reference.Type.INTRUSIVE && !h.isBound()).toList();
           if (!intrusive.isEmpty())
               throw new IllegalStateException("Some intrusive holders were not added to registry: " + intrusive);
       }
       return this.self;
    }

    @SuppressWarnings("deprecation")
    Holder.Reference<T> createIntrusiveHolder(T value)
    {
        if (this.holderLookup == null)
            throw new IllegalStateException("This registry can't create intrusive holders");
        if (this.frozen)
            throw new IllegalStateException("Registry is already frozen");

        return this.holders.computeIfAbsent(value, k -> Holder.Reference.createIntrusive(this.self, value));
    }

    Optional<HolderSet.Named<T>> getTag(TagKey<T> name)
    {
       return Optional.ofNullable(this.tags.get(name));
    }

    void bindTags(Map<TagKey<T>, List<Holder<T>>> newTags)
    {
        Map<Holder.Reference<T>, List<TagKey<T>>> holderToTag = new IdentityHashMap<>();
        this.holdersByName.values().forEach(v -> holderToTag.put(v, new ArrayList<>()));
        newTags.forEach((name, values) -> values.forEach(holder -> addTagToHolder(holderToTag, name, holder)));

        Set<TagKey<T>> set = Sets.difference(this.tags.keySet(), newTags.keySet());
        if (!set.isEmpty())
            LOGGER.warn("Not all defined tags for registry {} are present in data pack: {}", this.self.key(), set.stream().map(k -> k.location().toString()).sorted().collect(Collectors.joining(", \n\t")));

        Map<TagKey<T>, HolderSet.Named<T>> tmpTags = new IdentityHashMap<>(this.tags);
        newTags.forEach((k, v) -> tmpTags.computeIfAbsent(k, this::createTag).bind(v));

        Set<TagKey<T>> defaultedTags = Sets.difference(this.optionalTags.keySet(), newTags.keySet());
        defaultedTags.forEach(name -> {
            List<Holder<T>> defaults = this.optionalTags.get(name).stream()
                    .map(valueSupplier -> getHolder(valueSupplier.get()).orElse(null))
                    .filter(Objects::nonNull)
                    .distinct()
                    .toList();
            defaults.forEach(holder -> addTagToHolder(holderToTag, name, holder));
            tmpTags.computeIfAbsent(name, this::createTag).bind(defaults);
        });

        holderToTag.forEach(Holder.Reference::bindTags);
        this.tags = tmpTags;

        this.owner.onBindTags(this.tags, defaultedTags);
    }

    private void addTagToHolder(Map<Holder.Reference<T>, List<TagKey<T>>> holderToTag, TagKey<T> name, Holder<T> holder)
    {
        if (!holder.isValidInRegistry(this.self))
            throw new IllegalStateException("Can't create named set " + name + " containing value " + holder + " from outside registry " + this);

        if (!(holder instanceof Holder.Reference<T>))
            throw new IllegalStateException("Found direct holder " + holder + " value in tag " + name);

        holderToTag.get((Holder.Reference<T>) holder).add(name);
    }

    void resetTags()
    {
       this.tags.values().forEach(t -> t.bind(List.of()));
       this.holders.values().forEach(v -> v.bindTags(Set.of()));
    }

    void unfreeze()
    {
        this.frozen = false;
    }

    boolean isFrozen()
    {
        return this.frozen;
    }

    boolean isIntrusive()
    {
        return this.holderLookup != null;
    }

    @Nullable
    Holder<T> onAdded(RegistryManager stage, int id, ResourceKey<T> key, T newValue, T oldValue)
    {
        if (stage != RegistryManager.ACTIVE && (this.holderLookup == null || !stage.isStaging()))  // Intrusive handlers need updating in staging.
            return null; // Only do holder shit on the active registries, not pending or snapshots.

        //Holder.Reference<T> oldHolder = oldValue == null ? null : getHolder(key, oldValue);
        // Do we need to do anything with the old holder? We cant update its pointer unless its non-intrusive...
        // And if thats the case, then we *should* get its reference in newHolder

        Holder.Reference<T> newHolder = getHolder(key, newValue);

        this.holdersById.size(Math.max(this.holdersById.size(), id + 1));
        this.holdersById.set(id, newHolder);
        this.holdersByName.put(key.location(), newHolder);
        this.holders.put(newValue, newHolder);
        newHolder.bind(key, newValue);
        this.holdersSorted = null;


        if (this.defaultKey != null && this.defaultKey.equals(key.location()))
            this.defaultHolder = newHolder;

        return newHolder;
    }

    private HolderSet.Named<T> createTag(TagKey<T> name)
    {
       return new HolderSet.Named<>(this.self, name);
    }


    private Holder.Reference<T> getHolder(ResourceKey<T> key, T value)
    {
        if (this.holderLookup != null)
            return this.holderLookup.apply(value);

        return this.holdersByName.computeIfAbsent(key.location(), k -> Holder.Reference.createStandAlone(this.self, key));
    }

    private List<Holder.Reference<T>> getSortedHolders()
    {
       if (this.holdersSorted == null)
          this.holdersSorted = this.holdersById.stream().filter(Objects::nonNull).toList();

       return this.holdersSorted;
    }

}
