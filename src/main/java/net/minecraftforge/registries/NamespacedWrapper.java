/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.registries;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.mojang.serialization.Lifecycle;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.RegistrationInfo;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.tags.TagLoader;
import net.minecraft.util.RandomSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class NamespacedWrapper<T> extends MappedRegistry<T> implements ILockableRegistry {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Marker MARKER = ForgeRegistry.REGISTRIES;
    private final ForgeRegistry<T> delegate;
    @Nullable
    private final Function<T, Holder.Reference<T>> intrusiveHolderCallback;
    private final Multimap<TagKey<T>, Supplier<T>> optionalTags = Multimaps.newSetMultimap(new IdentityHashMap<>(), HashSet::new);

    boolean locked = false;
    Lifecycle registryLifecycle = Lifecycle.stable();
    private boolean frozen = false; // Frozen is vanilla's variant of locked, but it can be unfrozen
    private List<Holder.Reference<T>> holdersSorted;
    private final ObjectList<Holder.Reference<T>> holdersById = new ObjectArrayList<>(256);
    private final Map<ResourceLocation, Holder.Reference<T>> holdersByName = new HashMap<>();
    private final Map<T, Holder.Reference<T>> holders = new IdentityHashMap<>();
    private final RegistryManager stage;
    private volatile Map<TagKey<T>, HolderSet.Named<T>> tags = new IdentityHashMap<>();
    private final Map<ResourceKey<T>, RegistrationInfo> registrationInfos = new IdentityHashMap<>();
    private MappedRegistry.TagSet<T> frozenTags = MappedRegistry.TagSet.unbound();

    NamespacedWrapper(ForgeRegistry<T> fowner, Function<T, Holder.Reference<T>> intrusiveHolderCallback, RegistryManager stage) {
        super(fowner.getRegistryKey(), Lifecycle.stable(), intrusiveHolderCallback != null);
        this.delegate = fowner;
        this.intrusiveHolderCallback = intrusiveHolderCallback;
        this.stage = stage;
    }

    @Override
    public Holder.Reference<T> register(ResourceKey<T> key, T value, RegistrationInfo info) {
        if (locked)
            throw new IllegalStateException("Can not register to a locked registry. Modder should use Forge Register methods.");

        Objects.requireNonNull(value);
        markKnown();

        this.registrationInfos.put(key, info);
        this.registryLifecycle = this.registryLifecycle.add(info.lifecycle());

        this.delegate.add(-1, key.location(), value);

        return getHolder(key, value);
    }

    // Reading Functions
    @Override
    @Nullable
    public T getValue(@Nullable ResourceLocation name) {
        return this.delegate.getRaw(name); //get without default
    }

    @Override
    public Optional<T> getOptional(@Nullable ResourceLocation name) {
        return Optional.ofNullable(this.delegate.getRaw(name)); //get without default
    }

    @Override
    @Nullable
    public T getValue(@Nullable ResourceKey<T> name) {
        return name == null ? null : this.delegate.getRaw(name.location()); //get without default
    }

    @Override
    @Nullable
    public ResourceLocation getKey(T value) {
        return this.delegate.getKey(value);
    }

    @Override
    public Optional<ResourceKey<T>> getResourceKey(T value) {
        return this.delegate.getResourceKey(value);
    }

    @Override
    public boolean containsKey(ResourceLocation key) {
        return this.delegate.containsKey(key);
    }

    @Override
    public boolean containsKey(ResourceKey<T> key) {
        return this.delegate.getRegistryName().equals(key.registry()) && containsKey(key.location());
    }

    @Override
    public int getId(@Nullable T value) {
        return this.delegate.getID(value);
    }

    @Override
    @Nullable
    public T byId(int id) {
        return this.delegate.getValue(id);
    }

    @Override
    public Lifecycle registryLifecycle() {
        return this.registryLifecycle;
    }

    @Override
    public Iterator<T> iterator() {
        return this.delegate.iterator();
    }

    @Override
    public Set<ResourceLocation> keySet() {
        return this.delegate.getKeys();
    }

    @Override
    public Set<ResourceKey<T>> registryKeySet() {
        return this.delegate.getResourceKeys();
    }

    @Override
    public Set<Map.Entry<ResourceKey<T>, T>> entrySet() {
        return this.delegate.getEntries();
    }

    @Override
    public boolean isEmpty() {
        return this.delegate.isEmpty();
    }

    @Override
    public int size() {
        return this.delegate.size();
    }

    /**
     * @deprecated Forge: For internal use only. Use the Register events when registering values.
     */
    @Deprecated
    @Override
    public void lock() {
        this.locked = true;
    }

    @Override
    public Optional<Holder.Reference<T>> get(int id) {
        return id >= 0 && id < this.holdersById.size() ? Optional.ofNullable(this.holdersById.get(id)) : Optional.empty();
    }

    @Override
    public Optional<Holder.Reference<T>> get(ResourceKey<T> key) {
        return Optional.ofNullable(this.holdersByName.get(key.location()));
    }

    @Override
    public Optional<Holder.Reference<T>> get(ResourceLocation p_333710_) {
        return Optional.ofNullable(this.holdersByName.get(p_333710_));
    }

    @Override
    public @NotNull Holder<T> wrapAsHolder(@NotNull T value) {
        final Holder<T> holder = this.holders.get(value);
        return holder == null ? Holder.direct(value) : holder;
    }

    @Override
    public Optional<RegistrationInfo> registrationInfo(ResourceKey<T> p_331530_) {
        return Optional.ofNullable(this.registrationInfos.get(p_331530_));
    }

    public Optional<Holder.Reference<T>> getHolder(ResourceLocation location) {
        return Optional.ofNullable(this.holdersByName.get(location));
    }

    Optional<Holder<T>> getHolder(T value) {
        return Optional.ofNullable(this.holders.get(value));
    }

    @Override
    public HolderGetter<T> createRegistrationLookup() {
        this.validateWrite();
        return new HolderGetter<T>() {
            public Optional<Holder.Reference<T>> get(ResourceKey<T> key) {
                return Optional.of(this.getOrThrow(key));
            }

            public Holder.Reference<T> getOrThrow(ResourceKey<T> key) {
                return NamespacedWrapper.this.getOrCreateHolderOrThrow(key);
            }

            public Optional<HolderSet.Named<T>> get(TagKey<T> key) {
                return Optional.of(this.getOrThrow(key));
            }

            public HolderSet.Named<T> getOrThrow(TagKey<T> key) {
                return NamespacedWrapper.this.getOrCreateTagForRegistration(key);
            }
        };
    }

    void validateWrite() {
        if (this.frozen)
            throw new IllegalStateException("Registry " + this.key().location() + " is already frozen");
    }

    void validateWrite(ResourceKey<T> key) {
        if (this.frozen)
            throw new IllegalStateException("Registry " + this.key().location() + " is already frozen (trying to add key " + key + ")");
    }

    protected Holder.Reference<T> getOrCreateHolderOrThrow(ResourceKey<T> key) {
        return this.holdersByName.computeIfAbsent(key.location(), k -> {
            if (this.isIntrusive()) {
                throw new IllegalStateException("This registry can't create new holders without value");
            } else {
                this.validateWrite(key);
                return Holder.Reference.createStandAlone(this, key);
            }
        });
    }

    @Override
    public Optional<Holder.Reference<T>> getRandom(RandomSource rand) {
        return Util.getRandomSafe(this.getSortedHolders(), rand);
    }

    @Override
    public Stream<Holder.Reference<T>> listElements() {
        return this.getSortedHolders().stream();
    }

    @Override
    public Stream<HolderSet.Named<T>> getTags() {
        return this.frozenTags.getTags();
    }

    @Override
    public HolderSet.Named<T> getOrCreateTagForRegistration(TagKey<T> name) {
        return this.tags.computeIfAbsent(name, this::createTag);
    }

    void addOptionalTag(TagKey<T> name, @NotNull Set<? extends Supplier<T>> defaults) {
        this.optionalTags.putAll(name, defaults);
    }

    /*
    @Override
    public Stream<TagKey<T>> getTagNames() {
        return this.tags.keySet().stream();
    }
    */

    @Override
    public Registry<T> freeze() {
        if (frozen) // vanilla calls freeze on frozen registries all the time. which makes things annoying
            return this;

        this.frozen = true;
        List<ResourceLocation> unregistered = this.holdersByName.entrySet().stream()
                .filter(e -> !e.getValue().isBound())
                .map(Map.Entry::getKey).sorted().toList();

        if (!unregistered.isEmpty())
            throw new IllegalStateException("Unbound values in registry " + this.key() + ": " + unregistered.stream().map(ResourceLocation::toString).collect(Collectors.joining(", \n\t")));

        if (this.unregisteredIntrusiveHolders != null && this.unregisteredIntrusiveHolders.values().stream().anyMatch(r -> !r.isBound() && r.getType() == Holder.Reference.Type.INTRUSIVE)) {
            throw new IllegalStateException("Some intrusive holders were not registered: " + this.unregisteredIntrusiveHolders.values() + " Hint: Did you register all your registry objects? Registry stage: " + stage.getName());
        }

        if (this.frozenTags.isBound())
            throw new IllegalStateException("Tags already present before freezing");

        var unbound = this.tags.entrySet().stream()
            .filter(t -> !t.getValue().isBound())
            .map(t -> t.getKey().location())
            .sorted()
            .toList();

        if (!unbound.isEmpty()) {
            LOGGER.debug(MARKER, "Unbound tags in registry " + this.key() + ": " + unbound);
            this.bindAllUnboundTagsToEmpty();
        }

        this.frozenTags = MappedRegistry.TagSet.fromMap(this.tags);
        this.delegate.onBindTags(this.tags);
        this.refreshTagsInHoldersForge();
        return this;
    }

    private void refreshTagsInHoldersForge() {
        Map<Holder.Reference<T>, List<TagKey<T>>> map = new IdentityHashMap<>();
        for (var value : this.holdersByName.values()) {
            map.put(value, new ArrayList<>());
        }

        this.frozenTags.forEach((key, value) -> {
            for (var holder : value) {
                var reference = this.validateAndUnwrapTagElement(key, holder);
                map.get(reference).add(key);
            }
        });

        for (var entry : map.entrySet()) {
            entry.getKey().bindTags(entry.getValue());
        }
    }

    private Holder.Reference<T> validateAndUnwrapTagElement(TagKey<T> key, Holder<T> value) {
        if (!value.canSerializeIn(this))
            throw new IllegalStateException("Can't create named set " + key + " containing value " + value + " from outside registry " + this);

        if (value instanceof Holder.Reference<T> reference)
            return reference;

        throw new IllegalStateException("Found direct holder " + value + " value in tag " + key);
    }

    @Override
    public Holder.Reference<T> createIntrusiveHolder(T value) {
        if (!this.isIntrusive())
            throw new IllegalStateException("This registry can't create intrusive holders");

        this.validateWrite();

        return super.createIntrusiveHolder(value);
    }

    @Override
    public Optional<HolderSet.Named<T>> get(TagKey<T> name) {
        return this.frozenTags.get(name);
    }

    @Override
    public void bindTag(TagKey<T> key, List<Holder<T>> newTags) {
        this.validateWrite();
        this.getOrCreateTagForRegistration(key).bind(newTags);
    }

    @Override
    public void bindAllTagsToEmpty() {
        this.validateWrite();
        for (var tag : this.tags.values())
            tag.bind(List.of());
    }

    private void bindAllUnboundTagsToEmpty() {
        for (var tag : this.tags.values()) {
            if (!tag.isBound())
                tag.bind(List.of());
        }
    }

    @Override
    public Registry.PendingTags<T> prepareTagReload(TagLoader.LoadResult<T> data) {
        if (!this.frozen)
            throw new IllegalStateException("Invalid method used for tag loading");

        var _old = ImmutableMap.<TagKey<T>, HolderSet.Named<T>>builder();
        var _new = ImmutableMap.<TagKey<T>, List<Holder<T>>>builder();

        for (var entry : data.tags().entrySet()) {
            var key = entry.getKey();
            var existing = this.tags.get(key);
            if (existing == null)
                existing = this.createTag(key);
            _old.put(key, existing);
            _new.put(key, List.copyOf(entry.getValue()));
        }

        for (var entry : this.optionalTags.entries()) {
            var key = entry.getKey();
            if (data.tags().containsKey(key))
                continue;

            var value = entry.getValue().get();
            var holder = this.getHolder(value).orElse(null);
            if (holder == null)
                continue;

            var existing = this.tags.get(key);
            if (existing == null)
                existing = this.createTag(key);
            _old.put(key, existing);
            _new.put(key, List.of(holder));
        }

        var oldBindings = _old.build();
        var newBindings = _new.build();

        var oldSnapshot = new HolderLookup.RegistryLookup.Delegate<T>() {
            @Override
            public HolderLookup.RegistryLookup<T> parent() {
                return NamespacedWrapper.this;
            }

            @Override
            public Optional<HolderSet.Named<T>> get(TagKey<T> p_259486_) {
                return Optional.ofNullable(oldBindings.get(p_259486_));
            }

            @Override
            public Stream<HolderSet.Named<T>> listTags() {
                return oldBindings.values().stream();
            }
        };

        return new Registry.PendingTags<T>() {
            @Override
            public ResourceKey<? extends Registry<? extends T>> key() {
                return NamespacedWrapper.this.key();
            }

            @Override
            public int size() {
                return newBindings.size();
            }

            @Override
            public HolderLookup.RegistryLookup<T> lookup() {
                return oldSnapshot;
            }

            @Override
            public List<Holder<T>> getPending(TagKey<T> key) {
                return newBindings.getOrDefault(key, List.of());
            }

            @Override
            public void apply() {
                for (var entry : oldBindings.entrySet()) {
                    var newList = newBindings.getOrDefault(entry.getKey(), List.of());
                    entry.getValue().bind(newList);
                }

                NamespacedWrapper.this.frozenTags = MappedRegistry.TagSet.fromMap(oldBindings);
                NamespacedWrapper.this.delegate.onBindTags(NamespacedWrapper.this.tags);
                NamespacedWrapper.this.refreshTagsInHoldersForge();
            }
        };
    }

    @Override
    public void unfreeze() {
        this.frozen = false;
        this.frozenTags = MappedRegistry.TagSet.unbound();
    }

    boolean isFrozen() {
        return this.frozen;
    }

    @Override
    public boolean isIntrusive() {
        return this.intrusiveHolderCallback != null && this.stage == RegistryManager.ACTIVE;
    }

    @Nullable
    Holder.Reference<T> onAdded(RegistryManager stage, int id, ResourceKey<T> key, T newValue, T oldValue)  {
        //Holder.Reference<T> oldHolder = oldValue == null ? null : getHolder(key, oldValue);
        // Do we need to do anything with the old holder? We cant update its pointer unless its non-intrusive...
        // And if thats the case, then we *should* get its reference in newHolder

        Holder.Reference<T> newHolder = getHolder(key, newValue);

        this.holdersById.size(Math.max(this.holdersById.size(), id + 1));
        this.holdersById.set(id, newHolder);
        this.holdersByName.put(key.location(), newHolder);
        this.holders.put(newValue, newHolder);
        if (this.unregisteredIntrusiveHolders != null) {
            this.unregisteredIntrusiveHolders.remove(newValue);
            newHolder.bindKey(key);
        }
        newHolder.bindValue(newValue);
        this.holdersSorted = null;

        return newHolder;
    }

    private HolderSet.Named<T> createTag(TagKey<T> name) {
        return new HolderSet.Named<>(this, name);
    }

    private Holder.Reference<T> getHolder(ResourceKey<T> key, T value) {
        if (this.isIntrusive())
            return this.intrusiveHolderCallback.apply(value);

        return this.holdersByName.computeIfAbsent(key.location(), k -> Holder.Reference.createStandAlone(this, key));
    }

    private List<Holder.Reference<T>> getSortedHolders() {
        if (this.holdersSorted == null)
            this.holdersSorted = this.holdersById.stream().filter(Objects::nonNull).toList();

        return this.holdersSorted;
    }
}
