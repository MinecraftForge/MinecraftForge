package net.minecraftforge.event.entity.minecart;

import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;

/**
 * MinecartInteractEvent is fired when a player interacts with a minecart. <br>
 * This event is fired whenever a player interacts with a minecart in
 * EntityMinecartContainer#interactFirst(EntityPlayer), 
 * EntityMinecartEmpty#interactFirst(EntityPlayer)
 * EntityMinecartFurnace#interactFirst(EntityPlayer)
 * EntityMinecartHopper#interactFirst(EntityPlayer).<br>
 * <br>
 * {@link #player} contains the EntityPlayer that is involved with this minecart interaction.<br>
 * <br>
 * This event is {@link Cancelable}.<br>
 * If this event is canceled, the player does not interact with the minecart.<br>
 * <br>
 * This event does not have a result. {@link HasResult}<br>
 * <br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 **/
@Cancelable
public class MinecartInteractEvent extends MinecartEvent
{
    public final EntityPlayer player;

    public MinecartInteractEvent(EntityMinecart minecart, EntityPlayer player)
    {
        super(minecart);
        this.player = player;
    }
}
