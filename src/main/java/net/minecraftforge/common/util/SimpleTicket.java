package net.minecraftforge.common.util;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Objects;

/**
 * Common class for a simple ticket based system.
 * @param <K> The type your ticket is for
 * @param <V> The type that will be used to check if your ticket matches
 */
public abstract class SimpleTicket<K, V>
{
    @Nonnull
    private final Collection<SimpleTicket<?, V>> collection;
    @Nonnull
    private K target;
    private boolean isValid = false;

    public SimpleTicket(@Nonnull K target, @Nonnull Collection<SimpleTicket<?, V>> collection)
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

    public abstract boolean matches(V toMatch);

    @Nonnull
    public K getTarget()
    {
        return this.target;
    }

    public void setTarget(@Nonnull K newTarget)
    {
        this.target = Objects.requireNonNull(newTarget);
    }

    public static<V> void invalidateAll(Collection<SimpleTicket<?, V>> ticketCollection)
    {
        for (SimpleTicket<?, V> simpleTicket : ticketCollection)
        {
            simpleTicket.isValid = false;
        }
        ticketCollection.clear();
    }
}
