package net.minecraftforge.fmp.event;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.Event.HasResult;
import net.minecraftforge.fmp.multipart.IMultipart;

/**
 * PartEvent is fired whenever a multipart is added or removed from a container. For specific addition/removal events, use
 * {@link PartEvent.Add} and {@link PartEvent.Remove}
 * <br>
 * This event is NOT {@link Cancelable}. <br>
 * <br>
 * This event does not have a result. {@link HasResult}<br>
 * <br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.<br>
 **/
public abstract class PartEvent extends Event
{

    private final IMultipart part;

    public PartEvent(IMultipart part)
    {
        this.part = part;
    }

    public IMultipart getPart()
    {
        return part;
    }

    /**
     * A version of the parent event which is only fired when a multipart is added.
     */
    public static class Add extends PartEvent
    {

        public Add(IMultipart part)
        {
            super(part);
        }

    }

    /**
     * A version of the parent event which is only fired when a multipart is removed.
     */
    public static class Remove extends PartEvent
    {

        public Remove(IMultipart part)
        {
            super(part);
        }

    }

}
