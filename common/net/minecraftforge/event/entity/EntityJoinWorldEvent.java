package net.minecraftforge.event.entity;

import net.minecraft.src.Entity;
import net.minecraft.src.World;
import net.minecraftforge.event.Cancelable;

@Cancelable
public class EntityJoinWorldEvent extends EntityEvent
{

    public final World world;

    public EntityJoinWorldEvent(Entity entity, World world)
    {
        super(entity);
        this.world = world;
    }
}
