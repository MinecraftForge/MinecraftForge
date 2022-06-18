/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.capabilities;

import java.util.function.Supplier;

import javax.annotation.ParametersAreNonnullByDefault;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.event.ForgeEventFactory;

/**
 * The default implementation of {@link ICapabilityProvider}.
 *
 * @param <T> The type of the owner object.
 */
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public abstract class CapabilityProvider<T extends ICapabilityProvider> implements ICapabilityProvider
{

    @Nullable
    private CapabilityDispatcher<T> attachedCaps = null;
    protected boolean capsValid = true;
    protected CompoundTag lazyCapNbt = null;
    protected boolean capsInitialized = false;

    @SuppressWarnings("unchecked")
    private void doGatherCapabilities()
    {
        this.capsInitialized = true;
        this.attachedCaps = ForgeEventFactory.gatherCapabilities((AttachCapabilitiesEvent<T>) this.getCapEvent());
    }

    /**
     * This method is called from {@link ItemStack#copy()} and {@link ServerPlayer#restoreFrom(ServerPlayer, boolean)} to clone 
     * the capabilities of the original.
     * <p>
     * <b>It is not designed for use elsewhere, and may break or crash if used improperly.</b>
     */
    @SuppressWarnings("unchecked")
    protected <P extends CapabilityProvider<T>> void copyCapsFrom(P other)
    {
        if(other.capsInitialized)
        {
            this.capsInitialized = true;
            this.attachedCaps = other.getDispatcher().copy((T) this);
        }
        else this.lazyCapNbt = other.lazyCapNbt;
    }

    protected abstract AttachCapabilitiesEvent<? extends T> getCapEvent();

    protected final @Nullable CapabilityDispatcher<T> getDispatcher()
    {
        if (!capsInitialized)
        {
            doGatherCapabilities();
            if (lazyCapNbt != null && !lazyCapNbt.isEmpty())
            {
                deserializeCaps(lazyCapNbt);
                lazyCapNbt = null;
            }
        }

        return attachedCaps;
    }

    protected final CompoundTag serializeCaps()
    {
        if (!capsInitialized)
        {
            return lazyCapNbt;
        }

        final var disp = getDispatcher();
        if (disp != null)
        {
            return disp.serializeNBT();
        }
        return null;
    }

    protected final void deserializeCaps(@Nullable CompoundTag tag)
    {
        if (!capsInitialized)
        {
            lazyCapNbt = tag;
            return;
        }

        final var disp = getDispatcher();
        if (disp != null)
        {
            disp.deserializeNBT(tag);
        }
    }

    @Override
    public void invalidateCaps()
    {
        this.capsValid = false;
        if(!capsInitialized) return;
        var disp = getDispatcher();
        if (disp != null)
            disp.invalidateCaps();
    }

    @Override
    public void reviveCaps()
    {
    	if(this.capsValid) return; // Guard against incorrect calls.
        this.capsValid = true;
        if(!capsInitialized) return;
        var disp = getDispatcher();
        if (disp != null)
            disp.reviveCaps();
    }

    @Override
    @NotNull
    public <C> Capability<C> getCapability(@NotNull CapabilityType<C> cap, @Nullable Direction side)
    {
        final var disp = getDispatcher();
        return !capsValid || disp == null ? Capability.empty() : disp.getCapability(cap, side);
    }

    /**
     * Special implementation for cases which have a superclass and can't extend CapabilityProvider directly.
     * See {@link LevelChunk}
     */
    public static class AsField<T extends ICapabilityProvider> extends CapabilityProvider<T>
    {

        private final Supplier<AttachCapabilitiesEvent<T>> eventSupplier;
        
        public AsField(Supplier<AttachCapabilitiesEvent<T>> eventSupplier) {
            this.eventSupplier = eventSupplier;
        }

        @Nullable
        public CompoundTag serializeInternal()
        {
            return serializeCaps();
        }

        public void deserializeInternal(CompoundTag tag)
        {
            deserializeCaps(tag);
        }

        @Override
        protected AttachCapabilitiesEvent<T> getCapEvent() {
            return eventSupplier.get();
        }
    };

}