package net.minecraftforge.event;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.ModLoader;
import net.minecraftforge.fml.event.IModBusEvent;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public abstract class AbstractForgeEventFactory {
    private static final ModLoader ML = ModLoader.get();

    protected AbstractForgeEventFactory() {}

    /**
     * Post an event to the {@link MinecraftForge#EVENT_BUS}
     * @return true if the event is {@link Cancelable} and has been canceled
     */
    protected static boolean post(Event e) {
        return MinecraftForge.EVENT_BUS.post(e);
    }

    /**
     * Post an event to the {@link MinecraftForge#EVENT_BUS}, then return the event object
     * @return the event object passed in and possibly modified by listeners
     */
    protected static <E extends Event> E fire(E e) {
        post(e);
        return e;
    }

    /**
     * Post an event to the {@link ModLoader#get()} event bus
     */
    protected static <T extends Event & IModBusEvent> void postModBus(T e) {
        ML.postEvent(e);
    }
}
