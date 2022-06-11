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
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.ForgeEventFactory;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public abstract class CapabilityProvider implements ICapabilityProvider
{

	@Nullable
	private CapabilityDispatcher attachedCaps = null;
    protected boolean capsValid = true;
    protected CompoundTag lazyCapNbt = new CompoundTag();
    private boolean initialized = false;
    
    private void doGatherCapabilities()
    {
        this.initialized = true;
        AttachCapabilitiesEvent<?> event = ForgeEventFactory.gatherCapabilities(this.getEvent());
        this.attachedCaps = event.getSize() > 0 ? new CapabilityDispatcher(event, this) : null;
    }
    
    protected abstract AttachCapabilitiesEvent<?> getEvent();
    
    protected final @Nullable CapabilityDispatcher getDispatcher()
    {
        if (!initialized)
        {
            doGatherCapabilities();
            if (lazyCapNbt != null)
            {
                deserializeCaps(lazyCapNbt);
            }
        }

        return attachedCaps;
    }

    protected final CompoundTag serializeCaps()
    {
        if (!initialized)
        {
            return lazyCapNbt;
        }

        final CapabilityDispatcher disp = getDispatcher();
        if (disp != null)
        {
            return disp.serializeNBT();
        }
        return null;
    }

    protected final void deserializeCaps(CompoundTag tag)
    {
        if (!initialized)
        {
        	lazyCapNbt = tag;
            return;
        }

        final CapabilityDispatcher disp = getDispatcher();
        if (disp != null)
        {
            disp.deserializeNBT(tag);
        }
    }

    @Override
    public void invalidateCaps()
    {
        this.capsValid = false;
        if(!initialized) return;
        CapabilityDispatcher disp = getDispatcher();
        if (disp != null)
            disp.invalidateCaps();
    }

    @Override
    public void reviveCaps()
    {
        this.capsValid = true;
        if(!initialized) return;
        CapabilityDispatcher disp = getDispatcher();
        if (disp != null)
            disp.reviveCaps();
    }

    @Override
    @NotNull
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side)
    {
        final CapabilityDispatcher disp = getDispatcher();
        return !capsValid || disp == null ? LazyOptional.empty() : disp.getCapability(cap, side);
    }

    /**
     * Special implementation for cases which have a superclass and can't extend CapabilityProvider directly.
     * See {@link LevelChunk}
     */
    public static class AsField extends CapabilityProvider
    {

    	private final Supplier<AttachCapabilitiesEvent<?>> eventSupplier;
    	
    	public AsField(Supplier<AttachCapabilitiesEvent<?>> eventSupplier) {
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
		protected AttachCapabilitiesEvent<?> getEvent() {
			return eventSupplier.get();
		}
    };

}
