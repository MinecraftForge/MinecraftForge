package net.minecraftforge.event.entity.minecart;

import net.minecraft.src.Entity;
import net.minecraft.src.EntityMinecart;

public class MinecartCollisionEvent extends MinecartEvent
{
    public final Entity collider;

    public MinecartCollisionEvent(EntityMinecart minecart, Entity collider)
    {
        super(minecart);
        this.collider = collider;
    }
}
