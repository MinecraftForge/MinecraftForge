package net.minecraftforge.event.container;

import net.minecraft.inventory.Container;
import net.minecraftforge.event.Event;

public class ContainerEvent extends Event
{
    protected Container container;
    
    public ContainerEvent(Container container)
    {
        this.container = container;
    }
}
