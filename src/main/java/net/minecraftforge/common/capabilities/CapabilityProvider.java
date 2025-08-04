/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.capabilities;

import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.ForgeEventFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.VisibleForTesting;

import java.util.function.Supplier;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public abstract class CapabilityProvider<B extends ICapabilityProviderImpl<B>> implements ICapabilityProviderImpl<B> {
    @VisibleForTesting
    static boolean SUPPORTS_LAZY_CAPABILITIES = true;

    private @Nullable CapabilityDispatcher capabilities;
    private boolean valid = true;

    private final boolean                 isLazy;
    private Supplier<ICapabilityProvider> lazyParentSupplier = null;
    private CompoundTag                   lazyData           = null;
    private HolderLookup.Provider         registryAccess     = null;
    private boolean initialized = false;

    public static class Entities extends CapabilityProvider<Entity> {
        protected Entities() {
            super(false);
        }

        protected Entities(boolean isLazy) {
            super(isLazy);
        }

        @Override
        protected AttachCapabilitiesEvent<Entity> fireAttachCapabilitiesEvent(Entity provider) {
            return AttachCapabilitiesEvent.Entities.BUS.fire(new AttachCapabilitiesEvent.Entities(provider));
        }

        @Override
        protected boolean shouldFireAttachCapabilitiesEvent() {
            return AttachCapabilitiesEvent.Entities.BUS.hasListeners();
        }
    }

    public static class BlockEntities extends CapabilityProvider<BlockEntity> {
        protected BlockEntities() {
            super(false);
        }

        protected BlockEntities(boolean isLazy) {
            super(isLazy);
        }

        @Override
        protected AttachCapabilitiesEvent<BlockEntity> fireAttachCapabilitiesEvent(BlockEntity provider) {
            return AttachCapabilitiesEvent.BlockEntities.BUS.fire(new AttachCapabilitiesEvent.BlockEntities(provider));
        }

        @Override
        protected boolean shouldFireAttachCapabilitiesEvent() {
            return AttachCapabilitiesEvent.BlockEntities.BUS.hasListeners();
        }
    }

    public static class ItemStacks extends CapabilityProvider<ItemStack> {
        protected ItemStacks() {
            super(false);
        }

        protected ItemStacks(boolean isLazy) {
            super(isLazy);
        }

        @Override
        protected AttachCapabilitiesEvent<ItemStack> fireAttachCapabilitiesEvent(ItemStack provider) {
            return AttachCapabilitiesEvent.ItemStacks.BUS.fire(new AttachCapabilitiesEvent.ItemStacks(provider));
        }

        @Override
        protected boolean shouldFireAttachCapabilitiesEvent() {
            return AttachCapabilitiesEvent.ItemStacks.BUS.hasListeners();
        }
    }

    public static class Levels extends CapabilityProvider<Level> {
        protected Levels() {
            super(false);
        }

        protected Levels(boolean isLazy) {
            super(isLazy);
        }

        @Override
        protected AttachCapabilitiesEvent<Level> fireAttachCapabilitiesEvent(Level provider) {
            return AttachCapabilitiesEvent.Levels.BUS.fire(new AttachCapabilitiesEvent.Levels(provider));
        }

        @Override
        protected boolean shouldFireAttachCapabilitiesEvent() {
            return AttachCapabilitiesEvent.Levels.BUS.hasListeners();
        }
    }

    protected CapabilityProvider() {
        this.isLazy = false;
    }

    protected CapabilityProvider(boolean isLazy) {
        this.isLazy = SUPPORTS_LAZY_CAPABILITIES && isLazy;
    }

    protected final void gatherCapabilities() {
        gatherCapabilities(() -> null);
    }

    protected final void gatherCapabilities(@Nullable ICapabilityProvider parent) {
        if (!shouldFireAttachCapabilitiesEvent()) {
            this.initialized = true;
            return;
        }

        gatherCapabilities(() -> parent);
    }

    protected final void gatherCapabilities(@Nullable Supplier<ICapabilityProvider> parent) {
        if (!shouldFireAttachCapabilitiesEvent()) {
            this.initialized = true;
            return;
        }

        if (isLazy && !initialized) {
            lazyParentSupplier = parent == null ? () -> null : parent;
            return;
        }

        doGatherCapabilities(parent == null ? null : parent.get());
    }

    protected abstract AttachCapabilitiesEvent<?> fireAttachCapabilitiesEvent(B provider);
    protected abstract boolean shouldFireAttachCapabilitiesEvent();

    private void doGatherCapabilities(@Nullable ICapabilityProvider parent) {
        this.capabilities = ForgeEventFactory.gatherCapabilities(fireAttachCapabilitiesEvent(getProvider()), parent);
        this.initialized = true;
    }

    @SuppressWarnings("unchecked")
    @NotNull
    B getProvider() {
        return (B)this;
    }

    protected final @Nullable CapabilityDispatcher getCapabilities() {
        if (isLazy && !initialized) {
            doGatherCapabilities(lazyParentSupplier == null ? null : lazyParentSupplier.get());
            if (lazyData != null)
                deserializeCaps(registryAccess, lazyData);
        }

        return capabilities;
    }

    protected final @Nullable CompoundTag serializeCaps(HolderLookup.Provider registryAccess) {
        if (isLazy && !initialized)
            return lazyData;

        var disp = getCapabilities();
        if (disp != null)
            return disp.serializeNBT(registryAccess);

        return null;
    }

    protected final void deserializeCaps(HolderLookup.Provider registryAccess, CompoundTag tag) {
        if (isLazy && !initialized) {
            this.lazyData = tag;
            this.registryAccess = registryAccess;
            return;
        }

        var disp = getCapabilities();
        if (disp != null)
            disp.deserializeNBT(registryAccess, tag);

        this.lazyData = null;
        this.registryAccess = null;
    }

    /*
     * Invalidates all the contained caps, and prevents getCapability from returning a value.
     * This is usually called when the object in question is removed from the world.
     * However there may be cases where modders want to copy these 'invalid' caps.
     * They should call reviveCaps while they are doing their work, and then call invalidateCaps again
     * when they are finished.
     * Be sure to make your invalidate callbaks recursion safe.
     */
    public void invalidateCaps() {
        this.valid = false;
        final CapabilityDispatcher disp = getCapabilities();
        if (disp != null)
            disp.invalidate();
    }

    /*
     * This function will allow getCability to return values again.
     * Modders can use this if they need to copy caps from one removed provider to a new one.
     * It is expected the modders who call this function, then call invalidateCaps() to invalidate the provider again.
     */
    public void reviveCaps() {
        this.valid = true; //Stupid players don't copy the entity when transporting across worlds.
    }

    @Override
    @NotNull
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        final CapabilityDispatcher disp = getCapabilities();
        return !valid || disp == null ? LazyOptional.empty() : disp.getCapability(cap, side);
    }

    /**
     * Special implementation for cases which have a superclass and can't extend CapabilityProvider directly.
     * See {@link LevelChunk}
     */
    public static abstract class AsField<B extends ICapabilityProviderImpl<B>> extends CapabilityProvider<B> {
        private final B owner;

        public AsField(B owner) {
            super();
            this.owner = owner;
        }

        public AsField(B owner, boolean isLazy) {
            super(isLazy);
            this.owner = owner;
        }

        public void initInternal() {
            gatherCapabilities();
        }

        @Nullable
        public CompoundTag serializeInternal(HolderLookup.Provider registryAccess) {
            return serializeCaps(registryAccess);
        }

        public void deserializeInternal(HolderLookup.Provider registryAccess, CompoundTag tag) {
            deserializeCaps(registryAccess, tag);
        }

        @Override
        @NotNull
        B getProvider() {
            return owner;
        }

        public static class LevelChunks extends AsField<LevelChunk> {
            public LevelChunks(LevelChunk owner) {
                super(owner);
            }

            public LevelChunks(LevelChunk owner, boolean isLazy) {
                super(owner, isLazy);
            }

            @Override
            protected AttachCapabilitiesEvent<LevelChunk> fireAttachCapabilitiesEvent(LevelChunk provider) {
                return AttachCapabilitiesEvent.LevelChunks.BUS.fire(new AttachCapabilitiesEvent.LevelChunks(provider));
            }

            @Override
            protected boolean shouldFireAttachCapabilitiesEvent() {
                return AttachCapabilitiesEvent.LevelChunks.BUS.hasListeners();
            }
        }
    }

}
