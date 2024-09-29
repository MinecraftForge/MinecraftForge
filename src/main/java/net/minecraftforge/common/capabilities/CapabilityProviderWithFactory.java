/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.capabilities;

import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.VisibleForTesting;
import java.util.function.Supplier;

public class CapabilityProviderWithFactory<B extends ICapabilityProviderImpl<B>> implements ICapabilityProviderImpl<B> {
    @VisibleForTesting
    static boolean SUPPORTS_LAZY_CAPABILITIES = true;

    private final Supplier<CapabilityFactoryHolder<B>> lazyCapabilityHolderSupplier;

    private CapabilityDispatcher capabilities;
    private boolean valid = true;

    private boolean                       isLazy             = false;
    private Supplier<ICapabilityProvider> lazyParentSupplier = null;
    private CompoundTag                   lazyData           = null;
    private HolderLookup.Provider         registryAccess     = null;
    private boolean initialized = false;

    // Non-Lazy Impl
    protected CapabilityProviderWithFactory() {
        this.lazyCapabilityHolderSupplier = null;
    }

    // Lazy Impl
    protected CapabilityProviderWithFactory(Supplier<CapabilityFactoryHolder<B>> lazyCapabilityHolderSupplier, boolean isLazy) {
        this.lazyCapabilityHolderSupplier = lazyCapabilityHolderSupplier;
        this.isLazy = SUPPORTS_LAZY_CAPABILITIES && isLazy;
    }

    protected void gatherCapabilities(CapabilityFactoryHolder<B> holder) {
        if (capabilities != null) return;
        holder.build(this);
        var caps = holder.getCapabilities((B) this);
        this.capabilities = new CapabilityDispatcher(
                caps.map(),
                caps.listeners() // Maybe not needed?
        );
        this.initialized = true;
        postGather();
    }

    // For when you need to know once its safe to deserialize, useful for items.
    protected void postGather() {}

    protected Supplier<CapabilityFactoryHolder<B>> getLazyCapabilityHolderSupplier() {
        return lazyCapabilityHolderSupplier;
    }

    @SuppressWarnings("DataFlowIssue")
    protected CapabilityDispatcher getCapabilities() {
        if (isLazy && !initialized) {
            gatherCapabilities(lazyCapabilityHolderSupplier.get());
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

    public static final class AsField<B extends ICapabilityProviderImpl<B>> extends CapabilityProviderWithFactory<B> {
        private final B owner;

        public AsField(final B owner, Supplier<CapabilityFactoryHolder.AsField<B>> lazyCapabilityHolderSupplier, boolean isLazy) {
            super(lazyCapabilityHolderSupplier::get, isLazy);
            this.owner = owner;
        }

        public void initInternal() {
            gatherCapabilities(getLazyCapabilityHolderSupplier().get());
        }

        @Nullable
        public CompoundTag serializeInternal(HolderLookup.Provider registryAccess) {
            return serializeCaps(registryAccess);
        }

        public void deserializeInternal(HolderLookup.Provider registryAccess, CompoundTag tag) {
            deserializeCaps(registryAccess, tag);
        }

    }
}
