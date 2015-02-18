package net.minecraftforge.event.entity;

import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

/**
 * EntityJoinWorldEvent is fired when an Entity joins the world. <br>
 * This event is fired whenever an Entity is added to the world in 
 * World#addLoadedEntities(java.util.List), World#joinEntityInSurroundings(Entity), and World#spawnEntityInWorld(Entity). <br>
 * <br>
 * {@link #world} contains the world in which the entity is to join.<br>
 * <br>
 * This event is {@link Cancelable}.<br>
 * If this event is canceled, the Entity is not added to the world.<br>
 * <br>
 * This event does not have a result. {@link HasResult}<br>
 * <br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 **/
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
