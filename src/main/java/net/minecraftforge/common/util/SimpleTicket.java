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
    @Nonnull
    private T target;
    @Nonnull
    private final Collection<SimpleTicket<T>> collection;
    public final int tickTimeout;
    private int ticks = 0;

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
        return this.collection.contains(this);
    }

    /**
     * Removes the ticket from the managing system.
     * After this call, any calls to {@link #isValid()} should return false unless it is registered again using {@link #validate()}
     */
    public void invalidate()
    {
        this.collection.remove(this);
        this.ticks = -1;
    }

    /**
     * Re-adds your ticket to the system.
     */
    public final void validate()
    {
        this.collection.add(this);
        this.ticks = 0;
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

    public void tick()
    {
        if (this.ticks == -1)
        {
            return;
        }

        if (this.isValid())
        {
            this.ticks++;
        }
        else
        {
            this.ticks = -1;
        }
    }

    /**
     * Gets the time in ticks this ticket is valid or -1 if it is invalid
     */
    public int getTicks()
    {
        return ticks;
    }
}
