package net.minecraftforge.event.entity.player;

import net.minecraft.src.Entity;
import net.minecraft.src.EntityPlayer;
import net.minecraftforge.event.Cancelable;

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
