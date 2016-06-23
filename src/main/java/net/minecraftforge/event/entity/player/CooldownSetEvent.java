package net.minecraftforge.event.entity.player;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

import javax.annotation.Nullable;

/**
 * CooldownSetEvent is fired when an item sets a cooldown timer
 * on a player.<br>
 * This event is fired whenever a cooldown timer is being set in
 * CooldownTracker#setCooldown(Item, int).<br>
 * <br>
 * {@link #item} contains the item the cooldown is being set to.<br>
 * {@link #cooldown} contains the cooldown time in ticks.<br>
 * <br>
 * This event is {@link Cancelable}.<br>
 * If this event is canceled, the cooldown is not set.<br>
 * <br>
 * This event does not have a result. {@link HasResult}<br>
 * <br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 **/
@Cancelable
public class CooldownSetEvent extends PlayerEvent
{
    private final Item item;
    private int cooldown;

    public CooldownSetEvent(EntityPlayer player, Item item, int cooldown)
    {
        super(player);
        this.item = item;
        this.cooldown = cooldown;
    }

    public Item getItem()
    {
        return this.item;
    }

    public int getCooldownTime()
    {
        return this.cooldown;
    }

    public void setCooldownTime(int cooldown)
    {
        this.cooldown = cooldown;
    }
}
