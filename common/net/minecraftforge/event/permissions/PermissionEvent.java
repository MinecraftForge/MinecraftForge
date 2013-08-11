package net.minecraftforge.event.entity.permissions;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.Event.HasResult;
import net.minecraftforge.event.entity.player.PlayerEvent;

@HasResult
public class PermissionEvent extends PlayerEvent
{
    public final String permission;
    
    public PlayerEvent(EntityPlayer player, String permission)
    {
        super(player);
        this.permission = permission;
    }
    
}
