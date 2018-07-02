/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
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

package net.minecraftforge.fml.common.eventhandler;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.base.Preconditions;


/**
 * Base Event class that all other events are derived from
 */
public class Event
{
    @Retention(value = RUNTIME)
    @Target(value = TYPE)
    public @interface HasResult{}

    public enum Result
    {
        DENY,
        DEFAULT,
        ALLOW
    }

    private boolean isCanceled = false;
    private Result result = Result.DEFAULT;
    private static ListenerList listeners = new ListenerList();
    private EventPriority phase = null;

    public Event()
    {
        setup();
    }

    /**
     * Determine if this function is cancelable at all.
     * @return If access to setCanceled should be allowed
     *
     * Note:
     * Events with the Cancelable annotation will have this method automatically added to return true.
     */
    public boolean isCancelable()
    {
        return false;
    }

    /**
     * Determine if this event is canceled and should stop executing.
     * @return The current canceled state
     */
    public boolean isCanceled()
    {
        return isCanceled;
    }

    /**
     * Sets the cancel state of this event. Note, not all events are cancelable, and any attempt to
     * invoke this method on an event that is not cancelable (as determined by {@link #isCancelable}
     * will result in an {@link UnsupportedOperationException}.
     *
     * The functionality of setting the canceled state is defined on a per-event bases.
     *
     * @param cancel The new canceled value
     */
    public void setCanceled(boolean cancel)
    {
        if (!isCancelable())
        {
            throw new UnsupportedOperationException(
                "Attempted to call Event#setCanceled() on a non-cancelable event of type: "
                + this.getClass().getCanonicalName()
            );
        }
        isCanceled = cancel;
    }

    /**
     * Determines if this event expects a significant result value.
     *
     * Note:
     * Events with the HasResult annotation will have this method automatically added to return true.
     */
    public boolean hasResult()
    {
        return false;
    }

    /**
     * Returns the value set as the result of this event
     */
    public Result getResult()
    {
        return result;
    }

    /**
     * Sets the result value for this event, not all events can have a result set, and any attempt to
     * set a result for a event that isn't expecting it will result in a IllegalArgumentException.
     *
     * The functionality of setting the result is defined on a per-event bases.
     *
     * @param value The new result
     */
    public void setResult(Result value)
    {
        result = value;
    }

    /**
     * Called by the base constructor, this is used by ASM generated
     * event classes to setup various functionality such as the listener list.
     */
    protected void setup()
    {
    }

    /**
     * Returns a ListenerList object that contains all listeners
     * that are registered to this event.
     *
     * @return Listener List
     */
    public ListenerList getListenerList()
    {
        return listeners;
    }

    @Nullable
    public EventPriority getPhase()
    {
        return this.phase;
    }

    public void setPhase(@Nonnull EventPriority value)
    {
        Preconditions.checkNotNull(value, "setPhase argument must not be null");
        int prev = phase == null ? -1 : phase.ordinal();
        Preconditions.checkArgument(prev < value.ordinal(), "Attempted to set event phase to %s when already %s", value, phase);
        phase = value;
    }
}
