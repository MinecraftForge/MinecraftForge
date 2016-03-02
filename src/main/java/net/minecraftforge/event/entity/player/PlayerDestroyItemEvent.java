package net.minecraftforge.event.entity.player;

import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;

/**
 * PlayerDestroyItemEvent is fired when a player destroys an item.<br>
 * This event is fired whenever a player destroys an item in
 * PlayerControllerMP#onPlayerRightClick(EntityPlayer, World, ItemStack, int, int, int, int, Vec3),
 * PlayerControllerMP#sendUseItem(EntityPlayer, World, ItemStack),
 * EntityPlayer#destroyCurrentEquippedItem(),
 * SlotCrafting#onPickupFromSlot(EntityPlayer, ItemStack),
 * ItemInWorldManager#tryUseItem(EntityPlayer, World, ItemStack),
 * and ItemInWorldManager#activateBlockOrUseItem(EntityPlayer, World, ItemStack, int, int, int, int, float, float, float).<br>
 * <br>
 * {@link #original} contains the original ItemStack before the item was destroyed. <br>
 * (@link #hand) contains the hand that the current item was held in.<br>
 * <br>
 * This event is not {@link Cancelable}.<br>
 * <br>
 * This event does not have a result. {@link HasResult}<br>
 * <br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 **/
public class PlayerDestroyItemEvent extends PlayerEvent
{
    private final ItemStack original;
    private final EnumHand hand;
    public PlayerDestroyItemEvent(EntityPlayer player, ItemStack original, EnumHand hand)
    {
        super(player);
        this.original = original;
        this.hand = hand;
    }

    public ItemStack getOriginal() { return this.original; }
    public EnumHand getHand() { return this.hand; }

}
