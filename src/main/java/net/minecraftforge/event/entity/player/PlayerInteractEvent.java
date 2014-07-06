package net.minecraftforge.event.entity.player;

import static cpw.mods.fml.common.eventhandler.Event.Result.DEFAULT;
import static cpw.mods.fml.common.eventhandler.Event.Result.DENY;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import cpw.mods.fml.common.eventhandler.Cancelable;

/**
 * PlayerInteractEvent is fired when a player interacts in some way.
 * <br>
 * This event is fired whenever a player interacts in
 * Minecraft#func_147121_ag(),
 * NetHandlerPlayServer#processPlayerBlockPlacement(C08PacketPlayerBlockPlacement),
 * ItemInWorldManager#activateBlockOrUseItem(EntityPlayer, World, ItemStack, int, int, int, int, float, float, float),
 * ItemInWorldManager#onBlockClicked(int, int, int, int). <br>
 * <br>
 * This event is fired via the {@link ForgeEventFactory#onPlayerInteract(EntityPlayer, Action, int, int, int, int)}.
 * <br>
 * {@link #action} contains the Action the player performed durin this interaction. <br>
 * {@link #x} contains the x-coordinate of where this event occurred. <br>
 * {@link #y} contains the y-coordinate of where this event occurred. <br>
 * {@link #z} contains the z-coordinate of where this event occurred. <br>
 * {@link #face} contains the face of the block that was interacted with. <br>
 * {@link #world} contains the world in which this event is occurring. <br>
 * <br>
 * This event is {@link Cancelable}.<br>
 * If this event is canceled, the player does not interact.<br>
 * <br>
 * This event does not have a result. {@link HasResult}<br>
 * <br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 **/
@Cancelable
public class PlayerInteractEvent extends PlayerEvent
{
    public static enum Action
    {
        RIGHT_CLICK_AIR,
        RIGHT_CLICK_BLOCK,
        LEFT_CLICK_BLOCK
    }
    
    public final Action action;
    public final int x;
    public final int y;
    public final int z;
    public final int face;
    public final World world;
    
    public Result useBlock = DEFAULT;
    public Result useItem = DEFAULT;

    @Deprecated
    public PlayerInteractEvent(EntityPlayer player, Action action, int x, int y, int z, int face)
    {
        this(player, action, x, y, z, face, player.worldObj);
    }

    public PlayerInteractEvent(EntityPlayer player, Action action, int x, int y, int z, int face, World world)
    {
        super(player);
        this.action = action;
        this.x = x;
        this.y = y;
        this.z = z;
        this.face = face;
        if (face == -1) useBlock = DENY;
        this.world = world;
    }
    
    @Override
    public void setCanceled(boolean cancel)
    {
        super.setCanceled(cancel);
        useBlock = (cancel ? DENY : useBlock == DENY ? DEFAULT : useBlock);
        useItem = (cancel ? DENY : useItem == DENY ? DEFAULT : useItem);
    }
}
