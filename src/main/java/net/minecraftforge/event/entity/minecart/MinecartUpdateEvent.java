package net.minecraftforge.event.entity.minecart;

import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.util.math.BlockPos;

/**
 * MinecartUpdateEvent is fired when a minecart is updated.<br>
 * This event is fired whenever a minecart is updated in
 * {@link EntityMinecart#onUpdate()}.<br>
 * <br>
 * {@link #pos} contains the coordinate of the track the entity is on {if applicable}.<br>
 * <br>
 * This event is not {@link net.minecraftforge.fml.common.eventhandler.Cancelable}.<br>
 * <br>
 * This event does not have a result. {@link net.minecraftforge.fml.common.eventhandler.Event.HasResult}<br>
 * <br>
 * This event is fired on the {@link net.minecraftforge.common.MinecraftForge#EVENT_BUS}.
 **/
public class MinecartUpdateEvent extends MinecartEvent
{
    private final BlockPos pos;

    public MinecartUpdateEvent(EntityMinecart minecart, BlockPos pos)
    {
        super(minecart);
        this.pos = pos;
    }

    public BlockPos getPos()
    {
        return pos;
    }
}
