package net.minecraftforge.common.util;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Objects;

/**
 * Common class for a simple ticket based system.
 * @param <T> The type your ticket is for
 */
public class SimpleTicket<T>
{
    public final int tickTimeout;
    @Nonnull
    private final Collection<SimpleTicket<T>> collection;
    @Nonnull
    private T target;
    private int ticks = 0;
    private boolean isValid = false;

    public SimpleTicket(@Nonnull T target, @Nonnull Collection<SimpleTicket<T>> collection, int tickTimeout)
    {
        this.target = target;
        this.collection = collection;
        this.tickTimeout = tickTimeout;
        if (tickTimeout <= 0)
        {
            throw new IllegalArgumentException("Ticket needs to be valid for at least one tick, but tickTimeout was " + tickTimeout);
        }
    }

    /**
     * Checks if your ticket is still registered in the system.
     */
    public boolean isValid()
    {
        return this.isValid;
    }

    /**
     * Removes the ticket from the managing system.
     * After this call, any calls to {@link #isValid()} should return false unless it is registered again using {@link #validate()}
     */
    public void invalidate()
    {
        if (this.isValid)
        {
            this.collection.remove(this);
        }
        this.ticks = -1;
        this.isValid = false;
    }

    /**
     * Re-adds your ticket to the system.
     */
    public final void validate()
    {
        if (!this.isValid)
        {
            this.collection.add(this);
        }
        this.ticks = 0;
        this.isValid = true;
    }

    @Nonnull
    public T getTarget()
    {
        return this.target;
    }

    public void setTarget(@Nonnull T newTarget)
    {
        this.target = Objects.requireNonNull(newTarget);
    }

    /**
     * Ticks the ticket. Do not call from mod code, this is only here to be called by the manager
     * @return true if this ticket should be removed from the iterator
     */
    public boolean tick()
    {
        if (!this.isValid || ticks == -1 || ticks > this.tickTimeout)
        {
            this.ticks = -1;
            this.isValid = false;
            return true;
        }
        else
        {
            this.ticks++;
            return false;
        }
    }

    /**
     * Gets the time in ticks this ticket is valid or -1 if it is invalid
     */
    public int getTicks()
    {
        return ticks;
    }

    public static<T> void invalidateAll(Collection<SimpleTicket<T>> ticketCollection)
    {
        for (SimpleTicket<?> simpleTicket : ticketCollection)
        {
            simpleTicket.isValid = false;
        }
        ticketCollection.clear();
    }
}
