/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
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

import java.util.Objects;
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

    /**
     * Some capability providers can be lazy (item stacks only in Forge).
     * In that case, caps are only queried the first time getCapability() is called, and capNBT is used before that.
     */
    private final CompoundNBT capNBT; // only used before caps are initialized
    private volatile boolean capsInitialized; // must be volatile for double-checked locking to be safe

    /**
     * Constructor for regular (non lazy) providers.
     */
    protected CapabilityProvider(Class<B> baseClass)
    {
        this.baseClass = baseClass;
        this.capNBT = null;
        this.capsInitialized = true;
    }

    /**
     * Constructor for lazy providers. capNBT will be used before the first getCapability call.
     */
    protected CapabilityProvider(Class<B> baseClass, @Nullable CompoundNBT capNBT)
    {
        this.baseClass = baseClass;
        this.capNBT = capNBT;
        this.capsInitialized = false;
    }

    /**
     * For lazy providers only: called right before getCapability when necessary, init your capabilities here.
     * DO NOT CALL getCapabilities(), or anything using it. deserializeCaps() is safe to use.
     */
    protected void initializeCaps(@Nullable CompoundNBT capNBT) {}

    protected final void gatherCapabilities() { gatherCapabilities(null); }

    protected final void gatherCapabilities(@Nullable ICapabilityProvider parent)
    {
        this.capabilities = ForgeEventFactory.gatherCapabilities(baseClass, this, parent);
    }

    protected final @Nullable CapabilityDispatcher getCapabilities()
    {
        // Detect calls that don't check for capsInitialized, or calls from external code.
        if (!this.capsInitialized)
        {
            throw new IllegalStateException("Lazy capabilities are not yet initialized. This is a bug!");
        }

        return this.capabilities;
    }

    public final boolean areCapsCompatible(CapabilityProvider<B> other)
    {
        if (this.capsInitialized) return other.areCapsCompatible(this.getCapabilities());
        if (other.capsInitialized)
        {
            if (other.getCapabilities() == null) return this.capNBT == null;
            else return other.getCapabilities().areCompatibleNBT(this.capNBT);
        }
        return Objects.equals(this.capNBT, other.capNBT);
    }

    public final boolean areCapsCompatible(@Nullable CapabilityDispatcher other)
    {
        if (!this.capsInitialized)
        {
            if (other == null) return this.capNBT == null;
            return other.areCompatibleNBT(this.capNBT);
        }

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
        if (!this.capsInitialized)
        {
            return this.capNBT;
        }

        final CapabilityDispatcher disp = getCapabilities();
        if (disp != null)
        {
            return disp.serializeNBT();
        }
        return null;
    }

    protected final void deserializeCaps(CompoundNBT tag)
    {
        // Don't call getCapabilities() to bypass the if (!this.caps.initialized) check.
        final CapabilityDispatcher disp = this.capabilities;
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

    private void initializeCapsIfNeeded()
    {
        // A note on thread safety: capsInitialized == true guarantees that capabilities are safe to access.
        // Because we are not using a lock for reads, it is possible that in some cases capsInitialized might be false,
        // but capabilities have already been initialized due to another thread.
        // When that happens, we assume that using the immutable capNBT instead of the capabilities themselves is fine.
        // This is equivalent to assuming both threads only read at the same time.
        if (!this.capsInitialized)
        {
            synchronized (this)
            {
                if (!this.capsInitialized)
                {
                    initializeCaps(capNBT);
                    this.capsInitialized = true;
                }
            }
        }
    }

    @Override
    @Nonnull
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side)
    {
        initializeCapsIfNeeded();
        final CapabilityDispatcher disp = getCapabilities();
        return !valid || disp == null ? LazyOptional.empty() : disp.getCapability(cap, side);
    }
}
