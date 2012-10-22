package net.minecraftforge.event;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;


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
    private final boolean isCancelable;
    private Result result = Result.DEFAULT;
    private final boolean hasResult;
    private static ListenerList listeners = new ListenerList();
    
    public Event()
    {
        setup();
        isCancelable = hasAnnotation(Cancelable.class);
        hasResult = hasAnnotation(HasResult.class);
    }

    private boolean hasAnnotation(Class annotation)
    {
        Class cls = this.getClass();
        while (cls != Event.class)
        {
            if (cls.isAnnotationPresent(Cancelable.class))
            {
                return true;
            }
            cls = cls.getSuperclass();
        }
        return false;
    }

    /**
     * Determine if this function is cancelable at all. 
     * @return If access to setCanceled should be allowed
     */
    public boolean isCancelable()
    {
        return isCancelable;
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
     * Sets the state of this event, not all events are cancelable, and any attempt to
     * cancel a event that can't be will result in a IllegalArgumentException.
     * 
     * The functionality of setting the canceled state is defined on a per-event bases.
     * 
     * @param cancel The new canceled value
     */
    public void setCanceled(boolean cancel)
    {
        if (!isCancelable())
        {
            throw new IllegalArgumentException("Attempted to cancel a uncancelable event");
        }
        isCanceled = cancel;
    }

    /**
     * Determines if this event expects a significant result value.
     */
    public boolean hasResult()
    {
        return hasResult;
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
     * event classes to setup various functionality such as the listener's list.
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
}
