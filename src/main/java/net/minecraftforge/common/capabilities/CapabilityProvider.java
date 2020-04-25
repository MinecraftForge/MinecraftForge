/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
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
