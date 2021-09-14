package net.minecraftforge.items;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;

public class CapabilityItemDecoratorHandler {
    @CapabilityInject(IItemDecoratorHandler.class)
    public static Capability<IItemDecoratorHandler> ITEM_DECORATOR_HANDLER_CAPABILITY = null;

    public static void register(RegisterCapabilitiesEvent event)
    {
        event.register(IItemDecoratorHandler.class);
    }
}
