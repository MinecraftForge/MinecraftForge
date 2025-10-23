package net.minecraftforge.registries;

import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.tags.ITagManager;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Supplier;

@ApiStatus.Internal
public class DeferredForgeRegistry<V> implements IForgeRegistryInternal<V>, IForgeRegistryModifiable<V> {
    private final ResourceLocation name;
    private final ResourceKey<Registry<V>> key;
    private final Supplier<ForgeRegistry<V>> registry;

    DeferredForgeRegistry(ResourceKey<? extends Registry<V>> key, Supplier<IForgeRegistry<V>> registry) {
        this(key.location(), registry);
    }

    DeferredForgeRegistry(ResourceLocation name, Supplier<IForgeRegistry<V>> registry) {
        this.name = name;
        this.key = ResourceKey.createRegistryKey(name);
        this.registry = () -> (ForgeRegistry<V>) registry.get();
    }

    @Override
    public void setSlaveMap(ResourceLocation name, Object obj) {
        registry.get().setSlaveMap(name, obj);
    }

    @Override
    public void register(int id, ResourceLocation key, V value) {
        registry.get().register(id, key, value);
    }

    @Override
    public V getValue(int id) {
        return registry.get().getValue(id);
    }

    @Override
    public void clear() {
        registry.get().clear();
    }

    @Override
    public V remove(ResourceLocation key) {
        return registry.get().remove(key);
    }

    @Override
    public boolean isLocked() {
        return registry.get().isLocked();
    }

    @Override
    public ResourceKey<Registry<V>> getRegistryKey() {
        return this.key;
    }

    @Override
    public ResourceLocation getRegistryName() {
        return this.name;
    }

    @Override
    public void register(String key, V value) {
        registry.get().register(key, value);
    }

    @Override
    public void register(ResourceLocation key, V value) {
        registry.get().register(key, value);
    }

    @Override
    public boolean containsKey(ResourceLocation key) {
        return registry.get().containsKey(key);
    }

    @Override
    public boolean containsValue(V value) {
        return registry.get().containsValue(value);
    }

    @Override
    public boolean isEmpty() {
        return registry.get().isEmpty();
    }

    @Override
    public @Nullable V getValue(ResourceLocation key) {
        return registry.get().getValue(key);
    }

    @Override
    public @Nullable ResourceLocation getKey(V value) {
        return registry.get().getKey(value);
    }

    @Override
    public @Nullable ResourceLocation getDefaultKey() {
        return registry.get().getDefaultKey();
    }

    @Override
    public @NotNull Optional<ResourceKey<V>> getResourceKey(V value) {
        return registry.get().getResourceKey(value);
    }

    @Override
    public @NotNull Set<ResourceLocation> getKeys() {
        return registry.get().getKeys();
    }

    @Override
    public @NotNull Collection<V> getValues() {
        return registry.get().getValues();
    }

    @Override
    public @NotNull Set<Map.Entry<ResourceKey<V>, V>> getEntries() {
        return registry.get().getEntries();
    }

    @Override
    public @NotNull Codec<V> getCodec() {
        return registry.get().getCodec();
    }

    @Override
    public @NotNull Optional<Holder<V>> getHolder(ResourceKey<V> key) {
        return registry.get().getHolder(key);
    }

    @Override
    public @NotNull Optional<Holder<V>> getHolder(ResourceLocation location) {
        return registry.get().getHolder(location);
    }

    @Override
    public @NotNull Optional<Holder<V>> getHolder(V value) {
        return registry.get().getHolder(value);
    }

    @Override
    public @Nullable ITagManager<V> tags() {
        return registry.get().tags();
    }

    @Override
    public @NotNull Optional<Holder.Reference<V>> getDelegate(ResourceKey<V> rkey) {
        return registry.get().getDelegate(rkey);
    }

    @Override
    public Holder.@NotNull Reference<V> getDelegateOrThrow(ResourceKey<V> rkey) {
        return registry.get().getDelegateOrThrow(rkey);
    }

    @Override
    public @NotNull Optional<Holder.Reference<V>> getDelegate(ResourceLocation key) {
        return registry.get().getDelegate(key);
    }

    @Override
    public Holder.@NotNull Reference<V> getDelegateOrThrow(ResourceLocation key) {
        return registry.get().getDelegateOrThrow(key);
    }

    @Override
    public @NotNull Optional<Holder.Reference<V>> getDelegate(V value) {
        return registry.get().getDelegate(value);
    }

    @Override
    public Holder.@NotNull Reference<V> getDelegateOrThrow(V value) {
        return registry.get().getDelegateOrThrow(value);
    }

    @Override
    public <T> T getSlaveMap(ResourceLocation slaveMapName, Class<T> type) {
        return registry.get().getSlaveMap(slaveMapName, type);
    }

    @Override
    public @NotNull Iterator<V> iterator() {
        return registry.get().iterator();
    }
}
