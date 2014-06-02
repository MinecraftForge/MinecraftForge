package net.minecraftforge.event.entity.player;

import static cpw.mods.fml.common.eventhandler.Event.Result.DEFAULT;
import static cpw.mods.fml.common.eventhandler.Event.Result.DENY;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import cpw.mods.fml.common.eventhandler.Cancelable;

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
