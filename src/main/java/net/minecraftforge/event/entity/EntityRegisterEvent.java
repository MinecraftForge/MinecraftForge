package net.minecraftforge.event.entity;

import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.Event.HasResult;

/**
 * EntityRegisterEvent is fired when an Entity is registered.<br>
 * If a method utilizes this {@link Event} as its parameter, the method will 
 * receive every child event of this class.<br>
 * <br>
 * {@link #entityClass} contains the entity class being registered.<br>
 * {@link #entityName} contains the name of the registered entity.<br>
 * {@link #id} contains the registration id of the registered entity.<br>
 * <br>
 * All children of this event are fired on the {@link MinecraftForge#EVENT_BUS}.<br>
 **/
public class EntityRegisterEvent extends Event
{
    
    public final Class<? extends Entity> entityClass;
    public final String entityName;
    public final int id;
    
    EntityRegisterEvent(Class<? extends Entity> entityClass, String entityName, int id)
    {
        this.entityClass = entityClass;
        this.entityName = entityName;
        this.id = id;
    }
    
    /**
     * EntityModRegisteredEvent is fired when an Entity is registered by a mod. <br>
     * This event is fired whenever an Entity is registered in <br>
     * EntityRegistry#doModEntityRegistration(Class, String, int, Object, int, int, boolean). <br>
     * <br>
     * This event is fired on the {@link MinecraftForge#EVENT_BUS}.<br>
     * <br>
     * This event is not {@link Cancelable}.<br>
     * <br>
     * This event does not have a result. {@link HasResult}<br>
     * <br>
     **/
    public static class EntityModRegisteredEvent extends EntityRegisterEvent
    {
        public final Object mod;
        public final int trackingRange;
        public final int updateFrequency;
        public final boolean sendsVelocityUpdates;
        
        public EntityModRegisteredEvent(Class<? extends Entity> entityClass, String entityName, int id, Object mod, int trackingRange, int updateFrequency, boolean sendsVelocityUpdates)
        {
            super(entityClass, entityName, id);
            this.mod = mod;
            this.trackingRange = trackingRange;
            this.updateFrequency = updateFrequency;
            this.sendsVelocityUpdates = sendsVelocityUpdates;
        }
    }
    
    /**
    * EntityGlobalRegisteredEvent is fired when an Entity is registered globally by a mod. <br>
    * This event is fired whenever an Entity is registered in 
    * EntityRegistry#registerGlobalEntityID(Entity). <br>
    * <br>
    * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
    * <br>
    * This event is not {@link Cancelable}.
    * <br>
    * This event does not have a result. {@link HasResult}<br>
    * <br>
    **/
    public static class EntityGlobalRegisteredEvent extends EntityRegisterEvent
    {
        public EntityGlobalRegisteredEvent(Class<? extends Entity> entityClass, String entityName, int id)
        {
            super(entityClass, entityName, id);
        }
    }
    
}
