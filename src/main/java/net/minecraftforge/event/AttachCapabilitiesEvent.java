package net.minecraftforge.event;

import java.util.Collections;
import java.util.Map;

import com.google.common.collect.Maps;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * Fired whenever an object with Capabilities support {currently TileEntity/Item/Entity)
 * is created. Allowing for the attachment of arbitrary capability providers.
 *
 * Please note that as this is fired for ALL object creations efficient code is recommended.
 * And if possible use one of the sub-classes to filter your intended objects.
 */
public class AttachCapabilitiesEvent extends Event
{
    private final Object obj;
    private final Map<ResourceLocation, ICapabilityProvider> caps = Maps.newLinkedHashMap();
    private final Map<ResourceLocation, ICapabilityProvider> view = Collections.unmodifiableMap(caps);
    public AttachCapabilitiesEvent(Object obj)
    {
        this.obj = obj;
    }

    /**
     * Retrieves the object that is being created, Not much state is set.
     */
    public Object getObject()
    {
        return this.obj;
    }

    /**
     * Adds a capability to be attached to this object.
     * Keys MUST be unique, it is suggested that you set the domain to your mod ID.
     * If the capability is an instance of INBTSerializeable, this key will be used when serializing this capability.
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
    public static class TileEntity extends AttachCapabilitiesEvent
    {
        private final net.minecraft.tileentity.TileEntity te;
        public TileEntity(net.minecraft.tileentity.TileEntity te)
        {
            super(te);
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
    public static class Entity extends AttachCapabilitiesEvent
    {
        private final net.minecraft.entity.Entity entity;
        public Entity(net.minecraft.entity.Entity entity)
        {
            super(entity);
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
    public static class Item extends AttachCapabilitiesEvent
    {
        private final net.minecraft.item.ItemStack stack;
        private final net.minecraft.item.Item item;
        public Item(net.minecraft.item.Item item, net.minecraft.item.ItemStack stack)
        {
            super(item);
            this.item = item;
            this.stack = stack;
        }
        public net.minecraft.item.Item getItem()
        {
            return this.item;
        }
        public net.minecraft.item.ItemStack getItemStack()
        {
            return this.stack;
        }
    }
}
