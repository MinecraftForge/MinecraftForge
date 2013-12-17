package net.minecraftforge.event.entity.player;

import cpw.mods.fml.common.eventhandler.Cancelable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

@Cancelable
public class EntityInteractEvent extends PlayerEvent
{
    public final Entity target;
    public EntityInteractEvent(EntityPlayer player, Entity target)
    {
        super(player);
        this.target = target;
    }
}
