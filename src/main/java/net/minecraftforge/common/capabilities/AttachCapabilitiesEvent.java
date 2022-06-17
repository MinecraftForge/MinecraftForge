/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.capabilities;


import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

import com.google.common.base.Preconditions;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.common.capabilities.IAttachedCapabilityProvider.ICopyableCapabilityProvider;
import net.minecraftforge.common.capabilities.IAttachedCapabilityProvider.IItemStackCapabilityProvider;
import net.minecraftforge.eventbus.api.Event;

/**
 * The AttachCapabilitiesEvent is used to attach {@link IAttachedCapabilityProvider}s to game objects.<br>
 * The firing of this event is determined by the provider object, and is not consistent across usages.<br>
 * For most cases, this is fired whenever the {@link CapabilityDispatcher} must be resolved.<br>
 * This means that typically, this event is not fired until capabilities are attempted to be accessed for a particular object.
 */
public abstract class AttachCapabilitiesEvent<T extends ICapabilityProvider> extends Event
{

    protected final T obj;
    protected final Map<CapabilityType<?>, IAttachedCapabilityProvider<?, T>> providers = new IdentityHashMap<>(); // Type objects can be compared using ==
    protected final Map<CapabilityType<?>, IAttachedCapabilityProvider<?, T>> view = Collections.unmodifiableMap(providers);
    protected final Map<ResourceLocation, IAttachedCapabilityProvider<?, T>> byName = new HashMap<>();
    protected final Map<ResourceLocation, IAttachedCapabilityProvider<?, T>> byNameView = Collections.unmodifiableMap(byName);

    protected AttachCapabilitiesEvent(T obj)
    {
        this.obj = obj;
    }

    /**
     * Returns the object that is having capabilities attached.
     */
    public final T getObject()
    {
        return this.obj;
    }

    /**
     * Returns the number of successful attachments.
     */
    public final int getSize()
    {
        return this.providers.size();
    }

    /**
     * Adds a {@link IAttachedCapabilityProvider} to this object.<br>
     * Only one provider may exist for any given {@link CapabilityType}, on a first-come-first-serve basis.<br>
     * Additionally, the ID of all providers must be unique across the all {@link CapabilityType}s.
     * 
     * @param provider The provider being attached to this game object.
     * @return true, if this provider was successfully attached, otherwise false.
     * @throws UnsupportedOperationException If a provider with the same ID already exists.
     */
    public boolean addCapability(IAttachedCapabilityProvider<?, T> provider)
    {
        CapabilityType<?> type = provider.getType();
        if(this.byName.containsKey(provider.getId())) throw new UnsupportedOperationException(String.format("Attempted to attach a provider to %s with ID %s, but that ID is already in use!", obj.toString(), provider.getId()));
        if(this.providers.containsKey(type)) return false;
        this.providers.put(type, provider);
        this.byName.put(provider.getId(), provider);
        return true;
    }

    /**
     * A read-only view of the type -> provider map.
     */
    public Map<CapabilityType<?>, IAttachedCapabilityProvider<?, T>> getCapabilities()
    {
        return this.view;
    }

    /**
     * A read-only view of the ID -> provider map.
     */
    public Map<ResourceLocation, IAttachedCapabilityProvider<?, T>> getCapabilitiesByName()
    {
        return this.byNameView;
    }

    /**
     * This subclass is abnormal because it requires {@link #addCapability} to take an
     * {@link IItemStackCapabilityProvider} instead of an {@link IAttachedCapabilityProvider}.
     *
     */
    public static final class ItemStacks extends AttachCapabilitiesEvent<ItemStack>
    {

        public ItemStacks(ItemStack obj)
        {
            super(obj);
        }

        /**
         * Providers attached to {@link ItemStack} must implement {@link IItemStackCapabilityProvider}.
         * @see {@link ItemStacks#addCapability(IItemStackCapabilityProvider)}
         */
        @Override
        @Deprecated
        public boolean addCapability(IAttachedCapabilityProvider<?, ItemStack> provider) {
            Preconditions.checkArgument(provider instanceof IItemStackCapabilityProvider, "Providers attached to ItemStack must implement IItemStackCapabilityProvider!");
            return addCapability((IItemStackCapabilityProvider<?>) provider);
        }

        /**
         * @see {@link AttachCapabilitiesEvent#addCapability(IAttachedCapabilityProvider)}
         */
        public boolean addCapability(IItemStackCapabilityProvider<?> provider) {
            return super.addCapability(provider);
        }

    }

    public static final class BlockEntities extends AttachCapabilitiesEvent<BlockEntity>
    {

        public BlockEntities(BlockEntity obj)
        {
            super(obj);
        }

    }

    public static final class Chunks extends AttachCapabilitiesEvent<LevelChunk>
    {

        public Chunks(LevelChunk obj)
        {
            super(obj);
        }

    }

    public static final class Entities extends AttachCapabilitiesEvent<Entity>
    {

        public Entities(Entity obj)
        {
            super(obj);
        }

    }

    public static final class Levels extends AttachCapabilitiesEvent<Level>
    {

        public Levels(Level obj)
        {
            super(obj);
        }

    }

    /**
     * This subclass is abnormal because it requires {@link #addCapability} to take an
     * {@link ICopyableCapabilityProvider} instead of an {@link IAttachedCapabilityProvider}.
     *
     */
    public static final class Players extends AttachCapabilitiesEvent<Player>
    {

        public Players(Player obj)
        {
            super(obj);
        }

        /**
         * Providers attached to {@link Player} must implement {@link ICopyableCapabilityProvider}.
         * @see {@link Players#addCapability(ICopyableCapabilityProvider)}
         */
        @Override
        @Deprecated
        public boolean addCapability(IAttachedCapabilityProvider<?, Player> provider) {
            Preconditions.checkArgument(provider instanceof ICopyableCapabilityProvider, "Providers attached to Player must implement ICopyableCapabilityProvider!");
            return addCapability((ICopyableCapabilityProvider<?, Player>) provider);
        }

        /**
         * @see {@link AttachCapabilitiesEvent#addCapability(IAttachedCapabilityProvider)}
         */
        public boolean addCapability(ICopyableCapabilityProvider<?, Player> provider) {
            return super.addCapability(provider);
        }

    }
}