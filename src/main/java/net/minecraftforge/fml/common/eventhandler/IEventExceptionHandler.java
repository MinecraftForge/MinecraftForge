package net.minecraftforge.fml.common.eventhandler;

public interface IEventExceptionHandler
{
    /**
     * Fired when a EventListener throws an exception for the specified event on the event bus.
     * After this function returns, the original Throwable will be propogated upwards.
     *
     * @param bus The bus the event is being fired on
     * @param event The event that is being fired
     * @param listeners All listeners that are listening for this event, in order
     * @param index Index for the current listener being fired.
     * @param throwable The throwable being thrown
     */
    void handleException(EventBus bus, Event event, IEventListener[] listeners, int index, Throwable throwable);
}
