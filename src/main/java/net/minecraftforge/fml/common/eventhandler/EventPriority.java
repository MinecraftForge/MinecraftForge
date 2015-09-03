package net.minecraftforge.fml.common.eventhandler;

public enum EventPriority implements IEventListener
{
    /*Priority of event listeners, listeners will be sorted with respect to this priority level.
     *
     * Note:
     *   Due to using a ArrayList in the ListenerList,
     *   these need to stay in a contiguous index starting at 0. {Default ordinal}
     */
    HIGHEST, //First to execute
    HIGH,
    NORMAL,
    LOW,
    LOWEST //Last to execute
;

    @Override
    public void invoke(Event event)
    {
        event.setPhase(this);
    }
}
