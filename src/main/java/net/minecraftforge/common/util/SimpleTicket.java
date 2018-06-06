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
    private final Collection<SimpleTicket<T>> collection;
    @Nonnull
    private T target;
    private boolean isValid = false;

    public SimpleTicket(@Nonnull T target, @Nonnull Collection<SimpleTicket<T>> collection)
    {
        this.target = target;
        this.collection = collection;
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
        if (this.isValid)
        {
            this.collection.remove(this);
        }
        this.isValid = false;
    }

    /**
     * Re-adds your ticket to the system.
     */
    public void validate()
    {
        if (!this.isValid())
        {
            this.collection.add(this);
        }
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

    public static<T> void invalidateAll(Collection<SimpleTicket<T>> ticketCollection)
    {
        for (SimpleTicket<?> simpleTicket : ticketCollection)
        {
            simpleTicket.isValid = false;
        }
        ticketCollection.clear();
    }
}
