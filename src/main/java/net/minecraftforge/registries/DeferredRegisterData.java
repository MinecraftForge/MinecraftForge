/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.registries;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistrySetBuilder.RegistryBootstrap;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraftforge.common.extensions.IForgeRegistrySetBuilder;
import org.jetbrains.annotations.NotNull;

import com.mojang.serialization.Lifecycle;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Utility class to help with managing registry entries.
 * Maintains a list of all suppliers for entries and registers them during the proper Register event.
 * Suppliers should return NEW instances every time.
 *
 *Example Usage:
 *<pre>{@code
 *    public static final DeferredRegisterData<ConfiguredFeature<?, ?>> CONFIGURED_FEATURES = DeferredRegisterData.create(Registries.CONFIGURED_FEATURE, MOD_ID);
 *    private static final RegistryObject<ConfiguredFeature<?, ?>> CONFIGURED = CONFIGURED_FEATURES.register("configured", () ->
 *        new ConfiguredFeature<>(Feature.NO_OP, NoneFeatureConfiguration.INSTANCE)
 *    );
 *
 *    public static final DeferredRegisterData<PlacedFeature> PLACED_FEATURES = DeferredRegisterData.create(Registries.PLACED_FEATURE, MOD_ID);
 *    private static final RegistryObject<PlacedFeature> PLACED = PLACED_FEATURES.register("placed", () ->
 *        new PlacedFeature(CONFIGURED.getHolder().orElseThrow(), List.of())
 *    );
 *
 *    public static final DeferredRegisterData<BiomeModifier> BIOME_MODIFIERS = DeferredRegisterData.create(ForgeRegistries.Keys.BIOME_MODIFIERS, MOD_ID);
 *    @SuppressWarnings("unused")
 *    private static final RegistryObject<BiomeModifier> MODIFIER = BIOME_MODIFIERS.register("modifier", ctx -> {
 *        return new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
 *            ctx.lookup(Registries.BIOME).getOrThrow(BiomeTags.IS_OVERWORLD),
 *            HolderSet.direct(PLACED.getHolder().orElseThrow()),
 *            GenerationStep.Decoration.UNDERGROUND_ORES
 *        );
 *    });
 *
 *    protected void generateDataRegistries(GatherDataEvent event) {
 *        var registrySet = VanillaRegistries.builder()
 *            .add(CONFIGURED_FEATURES)
 *            .add(PLACED_FEATURES)
 *            .add(BIOME_MODIFIERS);
 *
 *        event.getGenerator().addProvider(event.includeServer(), new DatapackBuiltinEntriesProvider(
 *        	gen.getPackOutput(), event.getLookupProvider(), registrySet, modid)
 *        );
 *    }
 *
 *}</pre>
 *
 * @param <T> The base registry type
 */
public class DeferredRegisterData<T> implements RegistryBootstrap<T> {

    /**
     * DeferredRegister factory for data driven registries. This will not create objects during the normal registry events.
     * This is meant to be used as helper for creating a {@link RegistrySetBuilder}, and thus should <string>NOT</strong>
     * be registered to and {@link EventBus}. But instead used with {@link IForgeRegistrySetBuilder#add(DeferredRegister)}.
     *
     * @param registryKey the key of the registry to reference.
     * @param modid the namespace for all objects registered to this DeferredRegister
     */
    public static <B> DeferredRegisterData.Builder<B> builder(ResourceKey<? extends Registry<B>> registryKey, String modid) {
        requireNonNull("registryKey", registryKey);
        requireNonNull("modid", modid);
        return new Builder<>(registryKey, modid);
    }

    /**
     * DeferredRegister factory for data driven registries. This will not create objects during the normal registry events.
     * This is meant to be used as helper for creating a {@link RegistrySetBuilder}, and thus should <string>NOT</strong>
     * be registered to and {@link EventBus}. But instead used with {@link IForgeRegistrySetBuilder#add(DeferredRegister)}.
     *
     * @param registryKey the key of the registry to reference.
     * @param modid the namespace for all objects registered to this DeferredRegister
     *
     * @return A built instance using the default values for the builder. Equivalent to {@code builder(registryKey, modid).build()}
     */
    public static <B> DeferredRegisterData<B> create(ResourceKey<? extends Registry<B>> registryKey, String modid) {
        return new Builder<>(registryKey, modid).build();
    }

    public static class Builder<T> {
        private final ResourceKey<? extends Registry<T>> registryKey;
        private final String modid;

        private Lifecycle lifecycle = Lifecycle.stable();

        private Builder(ResourceKey<? extends Registry<T>> registryKey, String modid) {
            this.registryKey = registryKey;
            this.modid = modid;
        }

        /**
         * By default will register objects as {@link Lifecycle#stable()}
         */
        public Builder<T> lifecycle(Lifecycle value) {
            this.lifecycle = value;
            return this;
        }

        public DeferredRegisterData<T> build() {
            return new DeferredRegisterData<>(this);
        }
    }

    private final ResourceKey<? extends Registry<T>> registryKey;
    private final String modid;
    private final Lifecycle lifecycle;
    private final Map<RegistryObject<T>, Function<BootstrapContext<T>, ? extends T>> entries = new LinkedHashMap<>();
    private final Set<RegistryObject<T>> entriesView = Collections.unmodifiableSet(entries.keySet());

    private boolean beenBuilt = false;

    private DeferredRegisterData(Builder<T> builder) {
        this.registryKey = builder.registryKey;
        this.modid = builder.modid;
        this.lifecycle = builder.lifecycle;
    }

    /**
     * Creates a RegistryObject for the specified name, which will be filled when the {@link RegistrySetBuilder} is built.
     * This is useful is you need references to entries added not using the {@link DeferredRegisterData} system.
     *
     * @param name The entry's name, it will automatically have the modid prefixed.
     */
    public <I extends T> RegistryObject<I> register(final String name) {
        return registerInternal(name, null);
    }

    /**
     * Adds a new supplier to the list of entries to be registered, and returns a RegistryObject that will be populated with the created entry automatically.
     * If you need context from other data registries, use {@link DeferredRegisterData#register(String, Function)}
     *
     * @param name The new entry's name, it will automatically have the modid prefixed.
     * @param factory A factory for the new entry, it should return a new instance every time it is called.
     * @return A RegistryObject that will be updated with when the entries in the registry change.
     * @see #register(String, Supplier)
     */
    public <I extends T> RegistryObject<I> register(final String name, final Supplier<? extends I> factory) {
        return register(name, _ -> factory.get());
    }

    /**
     * Adds a new entry to be registered, the Function will be invoked with the {@link BootstrapContext} which allows lookup of other registry entries.
     * Returns a RegistryObject that will be populated with the created entry automatically.
     *
     * @param name The new entry's name, it will automatically have the modid prefixed.
     * @param factory A factory for the new entry, it should return a new instance every time it is called.
     * @return A RegistryObject that will be updated with when the entries in the registry change.
     */
    public <I extends T> RegistryObject<I> register(final String name, final Function<BootstrapContext<T>, ? extends I> factory) {
        requireNonNull("factory", factory);
        return registerInternal(name, factory);
    }

    @SuppressWarnings("unchecked")
    private <I extends T> RegistryObject<I> registerInternal(final String name, final Function<BootstrapContext<T>, ? extends I> factory) {
        if (beenBuilt)
            throw new IllegalStateException("Cannot register new entries to DeferredRegister after it has been used. You should use this in a static context");

        requireNonNull("name", name);
        var key = Identifier.fromNamespaceAndPath(modid, name);

        RegistryObject<I> ret = new RegistryObject<>(key, this.registryKey, this.modid);

        if (entries.containsKey((RegistryObject<T>)ret))
            throw new IllegalArgumentException("Duplicate registration " + name);

        entries.put((RegistryObject<T>)ret, factory);
        return ret;
    }

    /**
     * Creates a ResourceKey based on the current modid and provided path as the location and the registry name linked to this DeferredRegister.
     * To control the namespace, use {@link #key(Identifier)}.
     *
     * @see #key(Identifier)
     */
    @NotNull
    public ResourceKey<T> key(@NotNull String path) {
        requireNonNull("path", path);
        return key(Identifier.fromNamespaceAndPath(this.modid, path));
    }

    /**
     * Creates a tag key based on the provided resource location and the registry name linked to this DeferredRegister.
     * To use the current modid as the namespace, use {@link #createTagKey(String)}.
     *
     * @see #key(String)
     */
    @NotNull
    public ResourceKey<T> key(@NotNull Identifier location) {
        requireNonNull("location", location);
        return ResourceKey.create(getRegistryKey(), location);
    }

    /**
     * Creates a tag key based on the current modid and provided path as the location and the registry name linked to this DeferredRegister.
     * To control the namespace, use {@link #createTagKey(Identifier)}.
     *
     * @see #createTagKey(Identifier)
     * @see #createOptionalTagKey(String, Set)
     */
    @NotNull
    public TagKey<T> createTagKey(@NotNull String path) {
        requireNonNull("path", path);
        return createTagKey(Identifier.fromNamespaceAndPath(this.modid, path));
    }

    /**
     * Creates a tag key based on the provided resource location and the registry name linked to this DeferredRegister.
     * To use the current modid as the namespace, use {@link #createTagKey(String)}.
     *
     * @see #createTagKey(String)
     * @see #createOptionalTagKey(Identifier, Set)
     */
    @NotNull
    public TagKey<T> createTagKey(@NotNull Identifier location) {
        requireNonNull("location", location);
        return TagKey.create(this.registryKey, location);
    }

    /**
     * @return The {@link Lifecycle} that objects are registered as.
     */
    public Lifecycle getLifecycle() {
        return this.lifecycle;
    }

    /**
     * @return The unmodifiable view of registered entries. Useful for bulk operations on all values.
     */
    public Collection<RegistryObject<T>> getEntries() {
        return this.entriesView;
    }

    /**
     * @return The registry key stored in this deferred register. Useful for creating new deferred registers based on an existing one.
     */
    public ResourceKey<? extends Registry<T>> getRegistryKey() {
        return this.registryKey;
    }

    /**
     * @return The registry name stored in this deferred register. Useful for creating new deferred registers based on an existing one.
     */
    @NotNull
    public Identifier getRegistryName() {
        return this.registryKey.identifier();
    }

    private static <T> T requireNonNull(String name, T value) {
        if (value == null)
            throw new IllegalArgumentException(name + " can not be null");
        return value;
    }

    /**
     * Returns true if the key is registered to a valid object factory, useful for data generation knowing if this actually declares data.
     */
    public boolean isDeclared(ResourceKey<T> key) {
        for (var entry : this.entries.entrySet()) {
            if (entry.getKey().getKey() == key)
                return true;
        }
        return false;
    }

    /**
     * Build this DeferredRegister using the supplied {@link BoostrapContext}. This is meant to be called by {@link RegistrySetBuilder} by adding this
     * function to the builder using {@link RegistrySetBuilder#add(ResourceKey, RegistryBootstrap)} or {@link IForgeRegistrySetBuilder#add(DeferredRegisterData)}
     */
    @Override
    public void run(BootstrapContext<T> context) {
        beenBuilt = true;

        var lookup = context.lookup(this.registryKey);

        for (var e : entries.entrySet()) {
            var ro = e.getKey();
            T value;
            if (e.getValue() != null) {
                value = e.getValue().apply(context);
                context.register(ro.getKey(), value, this.lifecycle);
            } else
                value = lookup.get(ro.getKey()).map(Holder::get).orElse(null);

            ro.updateReference(value, () -> lookup.getOrThrow(ro.getKey()));
        }
    }
}
