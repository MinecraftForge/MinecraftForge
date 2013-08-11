package net.minecraftforge.event.entity.permissions;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.Event.HasResult;
import net.minecraftforge.event.entity.player.PlayerEvent;

@HasResult
public class LocatablePermissionEvent extends PermissionEvent
{
    public final int x, y, z;
    
    public PlayerEvent(EntityPlayer player, String permission, int x, int y, int z)
    {
        super(player, permission);
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
}
