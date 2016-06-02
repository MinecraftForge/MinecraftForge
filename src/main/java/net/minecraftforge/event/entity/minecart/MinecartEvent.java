package net.minecraftforge.event.entity.minecart;

import net.minecraft.entity.item.EntityMinecart;
import net.minecraftforge.event.entity.EntityEvent;

/**
 * MinecartEvent is fired whenever an event involving minecart entities occurs. <br>
 * If a method utilizes this {@link net.minecraftforge.fml.common.eventhandler.Event} as its parameter, the method will <br>
 * receive every child event of this class.<br>
 * <br>
 * {@link #minecart} contains the minecart entity involved with this event.<br>
 * <br>
 * All children of this event are fired on the {@link net.minecraftforge.common.MinecraftForge#EVENT_BUS}.<br>
 **/
public class MinecartEvent extends EntityEvent
{
    private final EntityMinecart minecart;

    public MinecartEvent(EntityMinecart minecart)
    {
        super(minecart);
        this.minecart = minecart;
    }

    public EntityMinecart getMinecart()
    {
        return minecart;
    }
}
