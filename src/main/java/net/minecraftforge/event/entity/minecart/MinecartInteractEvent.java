package net.minecraftforge.event.entity.minecart;

import net.minecraft.entity.item.EntityMinecartContainer;
import net.minecraft.entity.item.EntityMinecartEmpty;
import net.minecraft.entity.item.EntityMinecartFurnace;
import net.minecraft.entity.item.EntityMinecartHopper;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;

/**
 * MinecartInteractEvent is fired when a player interacts with a minecart. <br>
 * This event is fired whenever a player interacts with a minecart in
 * {@link EntityMinecartContainer#processInitialInteract(EntityPlayer, ItemStack, EnumHand)},
 * {@link EntityMinecartEmpty#processInitialInteract(EntityPlayer, ItemStack, EnumHand)},
 * {@link EntityMinecartFurnace#processInitialInteract(EntityPlayer, ItemStack, EnumHand)},
 * {@link EntityMinecartHopper#processInitialInteract(EntityPlayer, ItemStack, EnumHand)},
 * etc.<br>
 * <br>
 * {@link #player} contains the EntityPlayer that is involved with this minecart interaction.<br>
 * <br>
 * This event is {@link net.minecraftforge.fml.common.eventhandler.Cancelable}.<br>
 * If this event is canceled, the player does not interact with the minecart.<br>
 * <br>
 * This event does not have a result. {@link net.minecraftforge.fml.common.eventhandler.Event.HasResult}<br>
 * <br>
 * This event is fired on the {@link net.minecraftforge.common.MinecraftForge#EVENT_BUS}.
 **/
@Cancelable
public class MinecartInteractEvent extends MinecartEvent
{
    private final EntityPlayer player;
    private final ItemStack item;
    private final EnumHand hand;

    public MinecartInteractEvent(EntityMinecart minecart, EntityPlayer player, ItemStack item, EnumHand hand)
    {
        super(minecart);
        this.player = player;
        this.item = item;
        this.hand = hand;
    }

    public EntityPlayer getPlayer() { return player; }
    public ItemStack getItem() { return item; }
    public EnumHand getHand() { return hand; }
}
