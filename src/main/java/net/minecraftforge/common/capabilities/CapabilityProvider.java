/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.capabilities;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.ForgeEventFactory;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public abstract class CapabilityProvider<B extends CapabilityProvider<B>> implements ICapabilityProvider
{
    private final @Nonnull Class<B> baseClass;
    private @Nullable CapabilityDispatcher capabilities;
    private boolean valid = true;

    protected CapabilityProvider(Class<B> baseClass)
    {
        this.baseClass = baseClass;
    }

    protected final void gatherCapabilities() { gatherCapabilities(null); }

    protected final void gatherCapabilities(@Nullable ICapabilityProvider parent)
    {
        this.capabilities = ForgeEventFactory.gatherCapabilities(baseClass, this, parent);
    }

    protected final @Nullable CapabilityDispatcher getCapabilities()
    {
        return this.capabilities;
    }

    public final boolean areCapsCompatible(CapabilityProvider<B> other)
    {
        return areCapsCompatible(other.getCapabilities());
    }

    public final boolean areCapsCompatible(@Nullable CapabilityDispatcher other)
    {
        final CapabilityDispatcher disp = getCapabilities();
        if (disp == null)
        {
            if (other == null)
            {
                return true;
            }
            else
            {
                return other.areCompatible(null);
            }
        }
        else
        {
            return disp.areCompatible(other);
        }
    }

    protected final @Nullable CompoundNBT serializeCaps()
    {
        final CapabilityDispatcher disp = getCapabilities();
        if (disp != null)
        {
            return disp.serializeNBT();
        }
        return null;
    }

    protected final void deserializeCaps(CompoundNBT tag)
    {
        final CapabilityDispatcher disp = getCapabilities();
        if (disp != null)
        {
            disp.deserializeNBT(tag);
        }
    }

    protected void invalidateCaps()
    {
        this.valid = false;
        final CapabilityDispatcher disp = getCapabilities();
        if (disp != null)
            disp.invalidate();
    }

    protected void reviveCaps()
    {
        this.valid = true; //Stupid players don't copy the entity when transporting across worlds.
    }

    @Override
    @Nonnull
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side)
    {
        final CapabilityDispatcher disp = getCapabilities();
        return !valid || disp == null ? LazyOptional.empty() : disp.getCapability(cap, side);
    }
}
