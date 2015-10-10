package net.minecraftforge.event.entity.player;

import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;

@Cancelable
public class PlayerSetSpawnEvent extends PlayerEvent
{
    /**
     * This event is called before a player's spawn point is changed.
     * The event can be canceled, and no further processing will be done.
     */
    public final boolean forced;
    public final BlockPos newSpawn;
    
    public PlayerSetSpawnEvent(EntityPlayer player, BlockPos newSpawn, boolean forced) {
        super(player);
        this.newSpawn = newSpawn;
        this.forced = forced;
    }

}
