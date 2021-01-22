/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.registries;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;

import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.SimpleRegistry;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mojang.serialization.Lifecycle;

class NamespacedWrapper<T extends IForgeRegistryEntry<T>> extends SimpleRegistry<T> implements ILockableRegistry
{
    private static final Logger LOGGER = LogManager.getLogger();
    private boolean locked = false;
    private ForgeRegistry<T> delegate;

    public NamespacedWrapper(ForgeRegistry<T> owner)
    {
        super(owner.getRegistryKey(), Lifecycle.experimental());
        this.delegate = owner;
    }

    @Override
    public <V extends T> V register(int id, RegistryKey<T> key, V value, Lifecycle lifecycle)
    {
        if (locked)
            throw new IllegalStateException("Can not register to a locked registry. Modder should use Forge Register methods.");

        Validate.notNull(value);

        if (value.getRegistryName() == null)
            value.setRegistryName(key.getLocation());

        int realId = this.delegate.add(id, value);
        if (realId != id && id != -1)
            LOGGER.warn("Registered object did not get ID it asked for. Name: {} Type: {} Expected: {} Got: {}", key, value.getRegistryType().getName(), id, realId);
        return value;
    }

    @Override
    public <R extends T> R register(RegistryKey<T> key, R value, Lifecycle lifecycle)
    {
        return register(-1, key, value, lifecycle);
    }

    @Override
    public <V extends T> V validateAndRegister(OptionalInt id, RegistryKey<T> key, V value, Lifecycle lifecycle) {
        int wanted = -1;
        if (id.isPresent() && getByValue(id.getAsInt()) != null)
            wanted = id.getAsInt();
        return register(wanted, key, value, lifecycle);
    }

    // Reading Functions
    @Override
    @Nullable
    public T getOrDefault(@Nullable ResourceLocation name)
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
    public T getValueForKey(@Nullable RegistryKey<T> name)
    {
        return name == null ? null : this.delegate.getRaw(name.getLocation()); //get without default
    }

    @Override
    @Nullable
    public ResourceLocation getKey(T value)
    {
        return this.delegate.getKey(value);
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
    public T getByValue(int id)
    {
        return this.delegate.getValue(id);
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
    public Set<Map.Entry<RegistryKey<T>, T>> getEntries()
    {
        return this.delegate.getEntries();
    }

    @Override
    @Nullable
    public T getRandom(Random random)
    {
        Collection<T> values = this.delegate.getValues();
        return values.stream().skip(random.nextInt(values.size())).findFirst().orElse(null);
    }

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

    public static class Factory<V extends IForgeRegistryEntry<V>> implements IForgeRegistry.CreateCallback<V>
    {
        public static final ResourceLocation ID = new ResourceLocation("forge", "registry_defaulted_wrapper");
        @Override
        public void onCreate(IForgeRegistryInternal<V> owner, RegistryManager stage)
        {
            owner.setSlaveMap(ID, new NamespacedWrapper<V>((ForgeRegistry<V>)owner));
        }
    }
}
