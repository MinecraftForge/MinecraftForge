package net.minecraftforge.event.entity.player;

import cpw.mods.fml.common.eventhandler.Cancelable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

@Cancelable
public class AttackEntityEvent extends PlayerEvent
{
    public final Entity target;
    public AttackEntityEvent(EntityPlayer player, Entity target)
    {
        super(player);
        this.target = target;
    }
}
