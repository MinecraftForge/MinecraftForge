package net.minecraftforge.event.entity.player;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.event.Cancelable;
import net.minecraftforge.event.Event;
import static net.minecraftforge.event.Event.Result;
import static net.minecraftforge.event.Event.Result.*;

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
    
    public Result useBlock = DEFAULT;
    public Result useItem = DEFAULT;
    
    public PlayerInteractEvent(EntityPlayer player, Action action, int x, int y, int z, int face)
    {
        super(player);
        this.action = action;
        this.x = x;
        this.y = y;
        this.z = z;
        this.face = face;
        if (face == -1) useBlock = DENY;
    }
    
    @Override
    public void setCanceled(boolean cancel)
    {
        super.setCanceled(cancel);
        useBlock = (cancel ? DENY : useBlock == DENY ? DEFAULT : useBlock);
        useItem = (cancel ? DENY : useItem == DENY ? DEFAULT : useItem);
    }
}
