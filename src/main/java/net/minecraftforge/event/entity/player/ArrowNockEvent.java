package net.minecraftforge.event.entity.player;

import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

/**
 * ArrowNockEvent is fired when a player begins using a bow.<br>
 * This event is fired whenever a player begins using a bow in
 * ItemBow#onItemRightClick(ItemStack, World, EntityPlayer).<br>
 * <br>
 * {@link #result} contains the resulting ItemStack due to the use of the bow. <br>
 * <br>
 * This event is {@link Cancelable}.<br>
 * If this event is canceled, the player does not begin using the bow.<br>
 * <br>
 * This event does not have a result. {@link HasResult}<br>
 * <br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 **/
@Cancelable
public class ArrowNockEvent extends PlayerEvent
{
    public ItemStack result;
    
    public ArrowNockEvent(EntityPlayer player, ItemStack result)
    {
        super(player);
        this.result = result;
    }
}
