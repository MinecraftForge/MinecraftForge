package net.minecraftforge.items;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;

public class CapabilityCustomItemDecoration {
    @CapabilityInject(ICustomItemDecoration.class)
    public static Capability<ICustomItemDecoration> CUSTOM_ITEM_DECÒRATION_CAPABILITY = null;

    public static void register(RegisterCapabilitiesEvent event)
    {
        event.register(ICustomItemDecoration.class);
    }
}
