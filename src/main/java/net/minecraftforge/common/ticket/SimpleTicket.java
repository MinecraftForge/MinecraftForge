/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.ticket;

import com.google.common.base.Preconditions;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Consumer;

/**
 * Common class for a simple ticket based system.
 * @param <T> The type that will be used to check if your ticket matches
 */
public abstract class SimpleTicket<T>
{
    @Nullable
    private ITicketManager<T> masterManager;
    private ITicketManager<T>[] dummyManagers;
    protected boolean isValid = false;

    /**
     * Internal method that sets the collection from the managing system.
     * <br>
     * Should <b>not</b> be called if you just want to register a ticket to a system like the {@link net.minecraftforge.common.FarmlandWaterManager}
     */
    @SafeVarargs
    public final void setManager(@Nonnull ITicketManager<T> masterManager, @Nonnull ITicketManager<T>... dummyManagers)
    {
        Preconditions.checkState(this.masterManager == null, "Ticket is already registered to a managing system");
        this.masterManager = masterManager;
        this.dummyManagers = dummyManagers;
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
        if (this.isValid())
        {
            forEachManager(ticketManager -> ticketManager.remove(this));
        }
        this.isValid = false;
    }

    /**
     * Called by the managing system when a ticket wishes to unload all of it's tickets, e.g. on chunk unload
     * <br>The ticket must not remove itself from the manager that is calling the unload!
     * The ticket must ensure that it removes itself from all of it's dummies when returning true
     * @param unloadingManager The manager that is unloading this ticket
     * @return true if this ticket can be removed, false if not.
     */
    public boolean unload(ITicketManager<T> unloadingManager)
    {
        if (unloadingManager == masterManager)
        {
            for (ITicketManager<T> manager : dummyManagers)
            {
                manager.remove(this); //remove ourself from all dummies
            }
            this.isValid = false; //and mark us as invalid
            return true;
        }
        return false;
    }

    /**
     * Re-adds your ticket to the system.
     */
    public void validate()
    {
        if (!this.isValid())
        {
            forEachManager(ticketManager -> ticketManager.add(this));
        }
        this.isValid = true;
    }

    public abstract boolean matches(T toMatch);

    //Helper methods for custom tickets below

    protected final void forEachManager(Consumer<ITicketManager<T>> consumer)
    {
        Preconditions.checkState(this.masterManager != null, "Ticket is not registered to a managing system");
        consumer.accept(masterManager);
        for (ITicketManager<T> manager : dummyManagers)
        {
            consumer.accept(manager);
        }
    }

    protected final ITicketManager<T> getMasterManager()
    {
        return masterManager;
    }

    protected final ITicketManager<T>[] getDummyManagers()
    {
        return dummyManagers;
    }
}
