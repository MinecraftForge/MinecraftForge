/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.GenericEvent;
import net.minecraftforge.eventbus.api.IEventBus;

/**
 * Fired whenever an object with Capabilities support {currently TileEntity/Item/Entity)
 * is created. Allowing for the attachment of arbitrary capability providers.
 *
 * Please note that as this is fired for ALL object creations efficient code is recommended.
 * And if possible use one of the sub-classes to filter your intended objects.
 */
public abstract class AttachCapabilitiesEvent<T> extends Event
{
    private final T obj;
    private final Map<ResourceLocation, ICapabilityProvider> caps = Maps.newLinkedHashMap();
    private final Map<ResourceLocation, ICapabilityProvider> view = Collections.unmodifiableMap(caps);
    private final List<Runnable> listeners = Lists.newArrayList();
    private final List<Runnable> listenersView = Collections.unmodifiableList(listeners);
    private final Class<T> type;

    public AttachCapabilitiesEvent(Class<T> type, T obj)
    {
        this.type = type;
        this.obj = obj;
    }

    /**
     * Retrieves the object that is being created, Not much state is set.
     */
    public T getObject()
    {
        return this.obj;
    }

    public Class<T> getType() {
        return this.type;
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
     * Adds a callback that is fired when the attached object is invalidated.
     * Such as a Entity/TileEntity being removed from world.
     * All attached providers should invalidate all of their held capability instances.
     */
    public void addListener(Runnable listener)
    {
        this.listeners.add(listener);
    }

    public List<Runnable> getListeners()
    {
        return this.listenersView;
    }

    // Level/Entity/ItemStack/LevelChunk/BlockEntity/Item
    public static class EventFinder {
        private static final HashMap<Class<?>, Function<Object, ? extends AttachCapabilitiesEvent<?>>> EVENT_FACTORYS = new HashMap<>();

        @SuppressWarnings("all")
        public static <T> void register(Class<T> classType, Function<T, ? extends AttachCapabilitiesEvent<T>> eventFunction) {
            EVENT_FACTORYS.put(classType, (Function<Object, ? extends AttachCapabilitiesEvent<?>>) eventFunction);
        }

        public static boolean hasType(Class<?> type) {
            return EVENT_FACTORYS.containsKey(type);
        }

        @SuppressWarnings("all")
        public static <X, T extends AttachCapabilitiesEvent<X>> T get(Class<? extends X> type, X object) {
            return (T) EVENT_FACTORYS.get(type).apply((Object) object);
        }

        static {
            register(Item.class, AttachItem::new);
            register(ItemStack.class, AttachItemStack::new);
            register(LevelChunk.class, AttachLevelChunk::new);
            register(Level.class, AttachLevel::new);
            register(BlockEntity.class, AttachBlockEntity::new);
            register(Entity.class, AttachEntity::new);
        }
    }

    public static class AttachItem extends AttachCapabilitiesEvent<Item> {
        public AttachItem(Item obj) {
            super(Item.class, obj);
        }
    }

    public static class AttachLevel extends AttachCapabilitiesEvent<Level> {
        public AttachLevel(Level obj) {
            super(Level.class, obj);
        }
    }

    public static class AttachLevelChunk extends AttachCapabilitiesEvent<LevelChunk> {
        public AttachLevelChunk(LevelChunk obj) {
            super(LevelChunk.class, obj);
        }
    }

    public static class AttachBlockEntity extends AttachCapabilitiesEvent<BlockEntity> {
        public AttachBlockEntity(BlockEntity obj) {
            super(BlockEntity.class, obj);
        }
    }

    public static class AttachEntity extends AttachCapabilitiesEvent<Entity> {
        public AttachEntity(Entity obj) {
            super(Entity.class, obj);
        }
    }

    public static class AttachItemStack extends AttachCapabilitiesEvent<ItemStack> {
        public AttachItemStack(ItemStack obj) {
            super(ItemStack.class, obj);
        }
    }
}
