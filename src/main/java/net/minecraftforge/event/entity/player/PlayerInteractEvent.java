package net.minecraftforge.event.entity.player;

import static net.minecraftforge.fml.common.eventhandler.Event.Result.DEFAULT;
import static net.minecraftforge.fml.common.eventhandler.Event.Result.DENY;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

/**
 * PlayerInteractEvent is fired when a player interacts in some way.
 * <br>
 * This event is fired whenever a player interacts in
 * Minecraft#func_147121_ag(),
 * NetHandlerPlayServer#processPlayerBlockPlacement(C08PacketPlayerBlockPlacement),
 * ItemInWorldManager#activateBlockOrUseItem(EntityPlayer, World, ItemStack, int, int, int, int, float, float, float),
 * ItemInWorldManager#onBlockClicked(int, int, int, int). <br>
 * <br>
 * This event is fired via the {@link ForgeEventFactory#onPlayerInteract(EntityPlayer, Action, BlockPos, EnumFacing)}.
 * <br>
 * {@link #action} contains the Action the player performed during this interaction. <br>
 * {@link #pos} contains the coordinate of where this event occurred.<br>
 * {@link #face} contains the face of the block that was interacted with. May be null if unknown. <br>
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

    private final Action action;
    private final World world;
    private final BlockPos pos;
    private final EnumFacing face; // Can be null if unknown
    private final Vec3d localPos; // Can be null if unknown

    private Result useBlock = DEFAULT;
    private Result useItem = DEFAULT;

    @Deprecated
    public PlayerInteractEvent(EntityPlayer player, Action action, BlockPos pos, EnumFacing face, World world)
    {
        this(player, action, pos, face, world, null);
    }

    public PlayerInteractEvent(EntityPlayer player, Action action, BlockPos pos, EnumFacing face, World world, Vec3d localPos)
    {
        super(player);
        this.action = action;
        this.pos = pos;
        this.face = face;
        if (face == null) setUseBlock(DENY);
        this.world = world;
        this.localPos = localPos;
    }

    @Override
    public void setCanceled(boolean cancel)
    {
        super.setCanceled(cancel);
        setUseBlock((cancel ? DENY : getUseBlock() == DENY ? DEFAULT : getUseBlock()));
        setUseItem((cancel ? DENY : getUseItem() == DENY ? DEFAULT : getUseItem()));
    }

    public Action getAction() { return action; }
    public World getWorld() { return world; }
    public BlockPos getPos() { return pos; }
    public EnumFacing getFace() { return face; }
    public Vec3d getLocalPos() { return localPos; }
    public Result getUseBlock() { return useBlock; }
    public void setUseBlock(Result useBlock) { this.useBlock = useBlock; }
    public Result getUseItem() { return useItem; }
    public void setUseItem(Result useItem) { this.useItem = useItem; }
}
