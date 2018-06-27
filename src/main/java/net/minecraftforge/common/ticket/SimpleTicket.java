package net.minecraftforge.common.ticket;

import com.google.common.base.Preconditions;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Objects;

/**
 * Common class for a simple ticket based system.
 * @param <T> The type that will be used to check if your ticket matches
 */
public abstract class SimpleTicket<T>
{
    @Nullable
    private Collection<SimpleTicket<T>> collection;
    protected boolean isValid = false;

    /**
     * Internal method that sets the collection from the managing system.
     * <br>
     * Should <b>not</b> be called if you just want to register a ticket to a system like the {@link net.minecraftforge.common.FarmlandWaterManager}
     */
    public final void setBackend(@Nonnull Collection<SimpleTicket<T>> collection)
    {
        Preconditions.checkState(this.collection == null, "Ticket is already registered to a managing system");
        this.collection = Objects.requireNonNull(collection);
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
        Preconditions.checkState(this.collection != null, "Ticket is not registered to a managing system");
        if (this.isValid())
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
        Preconditions.checkState(this.collection != null, "Ticket is not registered to a managing system");
        if (!this.isValid())
        {
            this.collection.add(this);
        }
        this.isValid = true;
    }

    public abstract boolean matches(T toMatch);
}
