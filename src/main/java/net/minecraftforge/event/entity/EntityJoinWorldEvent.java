package net.minecraftforge.event.entity;

import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

import java.util.Collection;

/**
 * EntityJoinWorldEvent is fired when an Entity joins the world. <br>
 * This event is fired whenever an Entity is added to the world in 
 * {@link World#loadEntities(Collection)}, {@link WorldServer#loadEntities(Collection)} {@link World#joinEntityInSurroundings(Entity)}, and {@link World#spawnEntityInWorld(Entity)}. <br>
 * <br>
 * {@link #world} contains the world in which the entity is to join.<br>
 * <br>
 * This event is {@link net.minecraftforge.fml.common.eventhandler.Cancelable}.<br>
 * If this event is canceled, the Entity is not added to the world.<br>
 * <br>
 * This event does not have a result. {@link net.minecraftforge.fml.common.eventhandler.Event.HasResult}<br>
 * <br>
 * This event is fired on the {@link net.minecraftforge.common.MinecraftForge#EVENT_BUS}.
 **/
@Cancelable
public class EntityJoinWorldEvent extends EntityEvent
{

    private final World world;

    public EntityJoinWorldEvent(Entity entity, World world)
    {
        super(entity);
        this.world = world;
    }

    public World getWorld()
    {
        return world;
    }
}
