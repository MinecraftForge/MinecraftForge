/*
 * Minecraft Forge - Forge Development LLC
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.registries;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Main interface for the registry system. Use this to query the registry system.
 *
 * @param <V> The top level type for the registry
 */
public interface IForgeRegistry<V extends IForgeRegistryEntry<V>> extends Iterable<V>
{
    ResourceKey<Registry<V>> getRegistryKey();
    ResourceLocation getRegistryName();
    Class<V> getRegistrySuperType();

    void register(V value);

    @SuppressWarnings("unchecked")
    void registerAll(V... values);

    boolean containsKey(ResourceLocation key);
    boolean containsValue(V value);
    boolean isEmpty();
    /**
     * @return true if this registry supports tags and has a wrapped {@link Registry} variant
     * @see RegistryBuilder#hasTags()
     */
    boolean supportsTags();

    @Nullable V getValue(ResourceLocation key);
    @Nullable ResourceLocation getKey(V value);
    @Nullable ResourceLocation getDefaultKey();
    @NotNull Optional<ResourceKey<V>> getResourceKey(V value);

    @NotNull Set<ResourceLocation>         getKeys();
    @NotNull Collection<V>                 getValues();
    @NotNull Set<Entry<ResourceKey<V>, V>> getEntries();

    @NotNull Codec<V> getCodec();

    /**
     * @throws IllegalStateException if {@link #supportsTags()} returns false
     * @see Registry#getHolder(ResourceKey)
     */
    @NotNull Optional<Holder<V>> getHolder(ResourceKey<V> key);
    /**
     * @throws IllegalStateException if {@link #supportsTags()} returns false
     * @see Registry#getHolderOrThrow(ResourceKey)
     */
    @NotNull Holder<V> getHolderOrThrow(ResourceKey<V> key);
    /**
     * @throws IllegalStateException if {@link #supportsTags()} returns false
     */
    @NotNull Optional<Holder<V>> getHolder(ResourceLocation location);
    /**
     * @throws IllegalStateException if {@link #supportsTags()} returns false
     */
    @NotNull Holder<V> getHolderOrThrow(ResourceLocation location);
    /**
     * @throws IllegalStateException if {@link #supportsTags()} returns false
     */
    @NotNull Optional<Holder<V>> getHolder(V value);
    /**
     * @throws IllegalStateException if {@link #supportsTags()} returns false
     */
    @NotNull Holder<V> getHolderOrThrow(V value);
    /**
     * @throws IllegalStateException if {@link #supportsTags()} returns false
     * @see Registry#getOrCreateTag(TagKey)
     */
    @NotNull HolderSet.Named<V> getOrCreateTag(TagKey<V> name);
    /**
     * @throws IllegalStateException if {@link #supportsTags()} returns false
     * @see Registry#getTag(TagKey)
     */
    @NotNull Optional<HolderSet.Named<V>> getTag(TagKey<V> name);
    /**
     * @throws IllegalStateException if {@link #supportsTags()} returns false
     * @see Registry#getTagOrEmpty(TagKey)
     */
    @NotNull Iterable<Holder<V>> getTagOrEmpty(TagKey<V> name);
    /**
     * @throws IllegalStateException if {@link #supportsTags()} returns false
     * @see Registry#isKnownTagName(TagKey)
     */
    boolean isKnownTagName(TagKey<V> name);
    /**
     * @throws IllegalStateException if {@link #supportsTags()} returns false
     * @see Registry#getTags()
     */
    @NotNull Stream<Pair<TagKey<V>, HolderSet.Named<V>>> getTags();
    /**
     * @throws IllegalStateException if {@link #supportsTags()} returns false
     * @see Registry#getTagNames()
     */
    @NotNull Stream<TagKey<V>> getTagNames();
    /**
     * @throws IllegalStateException if {@link #supportsTags()} returns false
     * @see TagKey#create(ResourceKey, ResourceLocation)
     */
    @NotNull TagKey<V> createTagKey(ResourceLocation location);
    /**
     * Creates a tag key that will use the set of defaults if no tag JSON is found.
     * Useful on the client side when a server may not provide a specific tag.
     *
     * @throws IllegalStateException if {@link #supportsTags()} returns false
     */
    @NotNull TagKey<V> createOptionalTagKey(ResourceLocation location, Set<Supplier<V>> defaults);

    /**
     * Retrieve the slave map of type T from the registry.
     * Slave maps are maps which are dependent on registry content in some way.
     * @param slaveMapName The name of the slavemap
     * @param type The type
     * @param <T> Type to return
     * @return The slavemap if present
     */
    <T> T getSlaveMap(ResourceLocation slaveMapName, Class<T> type);

    /**
     * Callback fired when objects are added to the registry. This will fire when the registry is rebuilt
     * on the client side from a server side synchronization, or when a world is loaded.
     */
    @FunctionalInterface
    interface AddCallback<V extends IForgeRegistryEntry<V>>
    {
        void onAdd(IForgeRegistryInternal<V> owner, RegistryManager stage, int id, V obj, @Nullable V oldObj);
    }

    /**
     * Callback fired when the registry is cleared. This is done before a registry is reloaded from client
     * or server.
     */
    @FunctionalInterface
    interface ClearCallback<V extends IForgeRegistryEntry<V>>
    {
        void onClear(IForgeRegistryInternal<V> owner, RegistryManager stage);
    }

    /**
     * Callback fired when a registry instance is created. Populate slave maps here.
     */
    @FunctionalInterface
    interface CreateCallback<V extends IForgeRegistryEntry<V>>
    {
        void onCreate(IForgeRegistryInternal<V> owner, RegistryManager stage);
    }

    /**
     * Callback fired when the registry contents are validated.
     */
    @FunctionalInterface
    interface ValidateCallback<V extends IForgeRegistryEntry<V>>
    {
        void onValidate(IForgeRegistryInternal<V> owner, RegistryManager stage, int id, ResourceLocation key, V obj);
    }

    /**
     * Callback fired when the registry is done processing. Used to calculate state ID maps.
     */
    @FunctionalInterface
    interface BakeCallback<V extends IForgeRegistryEntry<V>>
    {
        void onBake(IForgeRegistryInternal<V> owner, RegistryManager stage);
    }

    /**
     * Factory for creating dummy entries, allowing worlds to be loaded and keep the missing block references.
     */
    @FunctionalInterface
    interface DummyFactory<V extends IForgeRegistryEntry<V>>
    {
        V createDummy(ResourceLocation key);
    }

    /**
     *
     */
    @FunctionalInterface
    interface MissingFactory<V extends IForgeRegistryEntry<V>>
    {
        V createMissing(ResourceLocation key, boolean isNetwork);
    }
}
