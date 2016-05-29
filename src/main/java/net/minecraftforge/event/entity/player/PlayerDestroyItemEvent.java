package net.minecraftforge.event.entity.player;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.play.client.CPacketPlayerBlockPlacement;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.server.management.PlayerInteractionManager;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;

/**
 * PlayerDestroyItemEvent is fired when a player destroys an item.<br>
 * This event is fired whenever a player destroys an item in
 * {@link PlayerControllerMP#processRightClick(EntityPlayer, World, ItemStack, EnumHand)},
 * {@link PlayerControllerMP#processRightClickBlock(EntityPlayerSP, WorldClient, ItemStack, BlockPos, EnumFacing, Vec3d, EnumHand)},
 * {@link EntityPlayer#damageShield(float)},
 * {@link ForgeHooks#getContainerItem(ItemStack)},
 * {@link PlayerInteractionManager#processRightClick(EntityPlayer, World, ItemStack, EnumHand)},
 * {@link NetHandlerPlayServer#processPlayerBlockPlacement(CPacketPlayerBlockPlacement)}
 * and {@link NetHandlerPlayServer#processRightClickBlock(CPacketPlayerTryUseItem)}.<br>
 * <br>
 * {@link #original} contains the original ItemStack before the item was destroyed. <br>
 * (@link #hand) contains the hand that the current item was held in.<br>
 * <br>
 * This event is not {@link Cancelable}.<br>
 * <br>
 * This event does not have a result. {@link HasResult}<br>
 * <br>
 * This event is fired from {@link ForgeEventFactory#onPlayerDestroyItem(EntityPlayer, ItemStack, EnumHand)}.<br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 **/
public class PlayerDestroyItemEvent extends PlayerEvent
{
    private final ItemStack original;
    private final EnumHand hand; // May be null if this player destroys the item by any use besides holding it.
    public PlayerDestroyItemEvent(EntityPlayer player, ItemStack original, EnumHand hand)
    {
        super(player);
        this.original = original;
        this.hand = hand;
    }

    public ItemStack getOriginal() { return this.original; }
    public EnumHand getHand() { return this.hand; }

}
