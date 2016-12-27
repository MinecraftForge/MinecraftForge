/*
 * Minecraft Forge
 * Copyright (c) 2016.
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

package net.minecraftforge.event;

import java.util.Collections;
import java.util.Map;

import com.google.common.collect.Maps;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.common.eventhandler.GenericEvent;

import javax.annotation.Nonnull;

/**
 * Fired whenever an object with Capabilities support {currently TileEntity/Item/Entity)
 * is created. Allowing for the attachment of arbitrary capability providers.
 *
 * Please note that as this is fired for ALL object creations efficient code is recommended.
 * And if possible use one of the sub-classes to filter your intended objects.
 */
public class AttachCapabilitiesEvent<T> extends GenericEvent<T>
{
    private final T obj;
    private final Map<ResourceLocation, ICapabilityProvider> caps = Maps.newLinkedHashMap();
    private final Map<ResourceLocation, ICapabilityProvider> view = Collections.unmodifiableMap(caps);

    @SuppressWarnings("unchecked")
    @Deprecated
    public AttachCapabilitiesEvent(T obj)
    {
        this((Class<T>)Object.class, obj);
    }
    public AttachCapabilitiesEvent(Class<T> type, T obj)
    {
        super(type);
        this.obj = obj;
    }

    /**
     * Retrieves the object that is being created, Not much state is set.
     */
    public T getObject()
    {
        return this.obj;
    }

    /**
     * Adds a capability to be attached to this object.
     * Keys MUST be unique, it is suggested that you set the domain to your mod ID.
     * If the capability is an instance of INBTSerializable, this key will be used when serializing this capability.
     *
     * @param key The name of owner of this capability provider.
     * @param cap The capability provider
     */
    public void addCapability(ResourceLocation key, ICapabilityProvider cap)
    {
        if (caps.containsKey(key))
            throw new IllegalStateException("Duplicate Capability Key: " + key  + " " + cap);
        this.caps.put(key, cap);
    }

    /**
     * A unmodifiable view of the capabilities that will be attached to this object.
     */
    public Map<ResourceLocation, ICapabilityProvider> getCapabilities()
    {
        return view;
    }


    /**
     * A version of the parent event which is only fired for Tile Entities.
     */
    @Deprecated
    public static class TileEntity extends AttachCapabilitiesEvent<net.minecraft.tileentity.TileEntity>
    {
        private final net.minecraft.tileentity.TileEntity te;
        public TileEntity(net.minecraft.tileentity.TileEntity te)
        {
            super(net.minecraft.tileentity.TileEntity.class, te);
            this.te = te;
        }
        public net.minecraft.tileentity.TileEntity getTileEntity()
        {
            return this.te;
        }
    }

    /**
     * A version of the parent event which is only fired for Entities.
     */
    @Deprecated
    public static class Entity extends AttachCapabilitiesEvent<net.minecraft.entity.Entity>
    {
        private final net.minecraft.entity.Entity entity;
        public Entity(net.minecraft.entity.Entity entity)
        {
            super(net.minecraft.entity.Entity.class, entity);
            this.entity = entity;
        }
        public net.minecraft.entity.Entity getEntity()
        {
            return this.entity;
        }
    }

    /**
     * A version of the parent event which is only fired for ItemStacks.
     */
    public static class Item extends AttachCapabilitiesEvent<net.minecraft.item.Item>
    {
        private final net.minecraft.item.ItemStack stack;
        @Deprecated
        private final net.minecraft.item.Item item;
        public Item(net.minecraft.item.Item item, @Nonnull  net.minecraft.item.ItemStack stack)
        {
            super(net.minecraft.item.Item.class, item);
            this.item = item;
            this.stack = stack;
        }
        @Deprecated
        public net.minecraft.item.Item getItem()
        {
            return this.item;
        }
        @Nonnull
        public net.minecraft.item.ItemStack getItemStack()
        {
            return this.stack;
        }
    }

    /**
     * A version of the parent event which is only fired for Worlds.
     */
    @Deprecated
    public static class World extends AttachCapabilitiesEvent<net.minecraft.world.World>
    {
        private final net.minecraft.world.World world;
        public World(net.minecraft.world.World world)
        {
            super(net.minecraft.world.World.class, world);
            this.world = world;
        }
        public net.minecraft.world.World getWorld()
        {
            return this.world;
        }
    }
}
