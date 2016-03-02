package net.minecraftforge.event.entity.player;

import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event.HasResult;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;

/**
 * EntityInteractEvent is fired when a player interacts with an Entity.<br>
 * This event is fired whenever a player interacts with an Entity in
 * EntityPlayer#interactWith(Entity).<br>
 * <br>
 * {@link #target} contains the Entity the player interacted with. <br>
 * <br>
 * This event is {@link Cancelable}.<br>
 * If this event is canceled, the player does not interact with the Entity.<br>
 * <br>
 * This event does not have a result. {@link HasResult}<br>
 * <br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 **/
@Cancelable
public class EntityInteractEvent extends PlayerEvent
{
    private final Entity target;
    private final ItemStack item;
    private final EnumHand hand;
    public EntityInteractEvent(EntityPlayer player, Entity target, ItemStack item, EnumHand hand)
    {
        super(player);
        this.target = target;
        this.item = item;
        this.hand = hand;
    }

    public Entity getTarget() { return this.target; }
    public ItemStack getItem() { return this.item; }
    public EnumHand getHand() { return this.hand; }
}
