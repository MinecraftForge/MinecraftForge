package net.minecraftforge.common.capabilities;

import java.util.concurrent.Callable;

import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.lifecycle.IModBusEvent;

/**
 * This event fires when it is time to register an capabilities with the CapabilityManager
 * using {@link CapabilityManager#register(Class, Capability.IStorage, Callable)}.
 */
public class RegisterCapabilitiesEvent extends Event implements IModBusEvent
{
}
