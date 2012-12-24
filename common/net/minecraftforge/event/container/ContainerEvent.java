package net.minecraftforge.event.container;

import net.minecraft.inventory.Container;
import net.minecraftforge.event.Event;

public class ContainerEvent extends Event
{
    public final Container container;
    public ContainerEvent(Container container)
    {
        this.container = container;
    }
}
