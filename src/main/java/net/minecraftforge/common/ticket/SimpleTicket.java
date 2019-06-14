/*
 * Minecraft Forge
 * Copyright (c) 2016-2019.
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

package net.minecraftforge.common.ticket;

import com.google.common.base.Preconditions;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Common class for a simple ticket based system.
 * @param <T> The type that will be used to check if your ticket matches
 */
public abstract class SimpleTicket<T>
{
    @Nullable
    private ITicketManager<T> manager;
    protected boolean isValid = false;

    /**
     * Internal method that sets the collection from the managing system.
     * <br>
     * Should <b>not</b> be called if you just want to register a ticket to a system like the {@link net.minecraftforge.common.FarmlandWaterManager}
     */
    public final void setBackend(@Nonnull ITicketManager<T> ticketManager)
    {
        Preconditions.checkState(this.manager == null, "Ticket is already registered to a managing system");
        this.manager = ticketManager;
    }

    /**
     * Checks if your ticket is still registered in the system.
     */
    public boolean isValid()
    {
        return isValid;
    }

    /**
     * Removes the ticket from the managing system.
     * After this call, any calls to {@link #isValid()} should return false unless it is registered again using {@link #validate()}
     */
    public void invalidate()
    {
        Preconditions.checkState(this.manager != null, "Ticket is not registered to a managing system");
        if (this.isValid())
        {
            this.manager.remove(this);
        }
        this.isValid = false;
    }

    /**
     * Re-adds your ticket to the system.
     */
    public void validate()
    {
        Preconditions.checkState(this.manager != null, "Ticket is not registered to a managing system");
        if (!this.isValid())
        {
            this.manager.add(this);
        }
        this.isValid = true;
    }

    public abstract boolean matches(T toMatch);
}
