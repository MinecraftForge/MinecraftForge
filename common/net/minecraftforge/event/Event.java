package net.minecraftforge.event;


/**
 * Base Event class that all other events are derived from
 */
public class Event
{
    public enum Result
    {
        DENY,
        DEFAULT,
        ALLOW
    }

    private boolean isCanceled = false;
    private final boolean isCancelable;
    private static ListenerList listeners = new ListenerList();
    
    public Event()
    {
        setup();
        Class cls = this.getClass();
        boolean found = false;
        while (cls != Event.class)
        {
            if (cls.isAnnotationPresent(Cancelable.class))
            {
                found = true;
                break;
            }
            cls = cls.getSuperclass();
        }
        isCancelable = found;
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
