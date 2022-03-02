/*
 * Minecraft Forge - Forge Development LLC
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.registries;

import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;
import java.util.function.Function;
import javax.annotation.Nullable;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.MappedRegistry;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mojang.serialization.Lifecycle;

class NamespacedWrapper<T extends IForgeRegistryEntry<T>> extends MappedRegistry<T> implements ILockableRegistry
{
    private static final Logger LOGGER = LogManager.getLogger();
    private boolean locked = false;
    private ForgeRegistry<T> delegate;

    public NamespacedWrapper(ForgeRegistry<T> owner, Function<T, Holder.Reference<T>> holderLookup)
    {
        super(owner.getRegistryKey(), Lifecycle.experimental(), holderLookup);
        this.delegate = owner;
    }

    @Override
    public Holder<T> registerMapping(int id, ResourceKey<T> key, T value, Lifecycle lifecycle)
    {
        if (locked)
            throw new IllegalStateException("Can not register to a locked registry. Modder should use Forge Register methods.");

        Validate.notNull(value);

        if (value.getRegistryName() == null)
            value.setRegistryName(key.location());

        int realId = this.delegate.add(id, value);
        if (realId != id && id != -1)
            LOGGER.warn("Registered object did not get ID it asked for. Name: {} Type: {} Expected: {} Got: {}", key, value.getRegistryType().getName(), id, realId);
        return super.registerMapping(realId, key, value, lifecycle);
    }

    @Override
    public Holder<T> register(ResourceKey<T> key, T value, Lifecycle lifecycle)
    {
        return registerMapping(-1, key, value, lifecycle);
    }

    @Override
    public Holder<T> registerOrOverride(OptionalInt id, ResourceKey<T> key, T value, Lifecycle lifecycle) {
        int wanted = -1;
        if (id.isPresent() && byId(id.getAsInt()) != null)
            wanted = id.getAsInt();
        return registerMapping(wanted, key, value, lifecycle);
    }

    // Reading Functions
    @Override
    @Nullable
    public T get(@Nullable ResourceLocation name)
    {
        return this.delegate.getRaw(name); //get without default
    }

    @Override
    public Optional<T> getOptional(@Nullable ResourceLocation name)
    {
        return Optional.ofNullable( this.delegate.getRaw(name)); //get without default
    }

    @Override
    @Nullable
    public T get(@Nullable ResourceKey<T> name)
    {
        return name == null ? null : this.delegate.getRaw(name.location()); //get without default
    }

    @Override
    @Nullable
    public ResourceLocation getKey(T value)
    {
        return this.delegate.getKey(value);
    }

    @Override
    public Optional<ResourceKey<T>> getResourceKey(T p_122755_)
    {
        return this.delegate.getResourceKey(p_122755_);
    }

    @Override
    public boolean containsKey(ResourceLocation key)
    {
        return this.delegate.containsKey(key);
    }

    @Override
    public int getId(@Nullable T value)
    {
        return this.delegate.getID(value);
    }

    @Override
    @Nullable
    public T byId(int id)
    {
        return this.delegate.getValue(id);
    }

    @Override
    public Lifecycle lifecycle(T p_122764_) {
        return Lifecycle.stable();
    }

    @Override
    public Iterator<T> iterator()
    {
        return this.delegate.iterator();
    }

    @Override
    public Set<ResourceLocation> keySet()
    {
        return this.delegate.getKeys();
    }

    @Override
    public Set<Map.Entry<ResourceKey<T>, T>> entrySet()
    {
        return this.delegate.getEntries();
    }

    // TODO-PATCHING: Figure out if getRandom needs to be overriden.

    /*
    @Override
    public boolean isEmpty()
    {
        return this.delegate.isEmpty();
    }
    */

    //internal
    @Override
    public void lock(){ this.locked = true; }

    //@Override public void unfreeze() {}

    public static class Factory<V extends IForgeRegistryEntry<V>> implements IForgeRegistry.CreateCallback<V>
    {
        public static final ResourceLocation ID = new ResourceLocation("forge", "registry_defaulted_wrapper");
        @Override
        public void onCreate(IForgeRegistryInternal<V> owner, RegistryManager stage)
        {
            ForgeRegistry<V> fowner = (ForgeRegistry<V>)owner;
            owner.setSlaveMap(ID, new NamespacedWrapper<V>(fowner, fowner.getBuilder().getVanillaHolder()));
        }
    }
}
