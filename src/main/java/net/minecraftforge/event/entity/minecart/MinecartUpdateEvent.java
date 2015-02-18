package net.minecraftforge.event.entity.minecart;

import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.util.BlockPos;

/**
 * MinecartUpdateEvent is fired when a minecart is updated.<br>
 * This event is fired whenever a minecart is updated in
 * EntityMinecart#onUpdate().<br>
 * <br>
 * {@link #pos} contains the coordinate of the track the entity is on {if applicable}.<br>
 * <br>
 * This event is not {@link Cancelable}.<br>
 * <br>
 * This event does not have a result. {@link HasResult}<br>
 * <br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 **/
public class MinecartUpdateEvent extends MinecartEvent
{
    public final BlockPos pos;

    public MinecartUpdateEvent(EntityMinecart minecart, BlockPos pos)
    {
        super(minecart);
        this.pos = pos;
    }
}
