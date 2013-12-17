package net.minecraftforge.event.entity;

import cpw.mods.fml.common.eventhandler.Cancelable;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

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
