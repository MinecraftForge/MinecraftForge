package net.minecraftforge.event.entity.player;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

/**
 * ArrowNockEvent is fired when a player begins using a bow.<br>
 * This event is fired whenever a player begins using a bow in
 * ItemBow#onItemRightClick(ItemStack, World, EntityPlayer).<br>
 * <br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 **/
public class ArrowNockEvent extends PlayerEvent
{
    private final ItemStack bow;
    private final EnumHand hand;
    private final World world;
    private final boolean hasAmmo;
    private ActionResult<ItemStack> action;

    public ArrowNockEvent(EntityPlayer player, ItemStack item, EnumHand hand, World world, boolean hasAmmo)
    {
        super(player);
        this.bow = item;
        this.hand = hand;
        this.world = world;
        this.hasAmmo = hasAmmo;
    }

    public ItemStack getBow() { return this.bow; }
    public EnumHand getHand() { return this.hand; }
    public boolean hasAmmo() { return this.hasAmmo; }
    public ActionResult<ItemStack> getAction()
    {
        return this.action;
    }

    public void setAction(ActionResult<ItemStack> action)
    {
        this.action = action;
    }
}
