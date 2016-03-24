package net.minecraftforge.event.entity;

import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraft.entity.Entity;

/**
 * EntityEvent is fired when an event involving any Entity occurs.<br>
 * If a method utilizes this {@link Event} as its parameter, the method will
 * receive every child event of this class.<br>
 * <br>
 * {@link #entity} contains the entity that caused this event to occur.<br>
 * <br>
 * All children of this event are fired on the {@link MinecraftForge#EVENT_BUS}.<br>
 **/
public class EntityEvent extends Event
{
    private final Entity entity;

    public EntityEvent(Entity entity)
    {
        this.entity = entity;
    }

    public Entity getEntity()
    {
        return entity;
    }

    /**
     * EntityConstructing is fired when an Entity is being created. <br>
     * This event is fired within the constructor of the Entity.<br>
     * <br>
     * This event is not {@link Cancelable}.<br>
     * <br>
     * This event does not have a result. {@link HasResult}<br>
     * <br>
     * This event is fired on the {@link MinecraftForge#EVENT_BUS}.<br>
     **/
    public static class EntityConstructing extends EntityEvent
    {
        public EntityConstructing(Entity entity)
        {
            super(entity);
        }
    }

    /**
     * CanUpdate is fired when an Entity is being created. <br>
     * This event is fired whenever vanilla Minecraft determines that an entity<br>
     * cannot update in World#updateEntityWithOptionalForce(net.minecraft.entity.Entity, boolean) <br>
     * <br>
     * {@link CanUpdate#canUpdate} contains the boolean value of whether this entity can update.<br>
     * If the modder decides that this Entity can be updated, they may change canUpdate to true, <br>
     * and the entity with then be updated.<br>
     * <br>
     * This event is not {@link Cancelable}.<br>
     * <br>
     * This event is fired on the {@link MinecraftForge#EVENT_BUS}.<br>
     **/
    public static class CanUpdate extends EntityEvent
    {
        private boolean canUpdate = false;
        public CanUpdate(Entity entity)
        {
            super(entity);
        }

        public boolean getCanUpdate()
        {
            return canUpdate;
        }

        public void setCanUpdate(boolean canUpdate)
        {
            this.canUpdate = canUpdate;
        }
    }
    
    /**
     * EnteringChunk is fired when an Entity enters a chunk. <br>
     * This event is fired whenever vanilla Minecraft determines that an entity <br>
     * is entering a chunk in Chunk#addEntity(net.minecraft.entity.Entity) <br>
     * <br>
     * This event is not {@link Cancelable}.<br>
     * <br>
     * This event does not have a result. {@link HasResult}
     * <br>
     * This event is fired on the {@link MinecraftForge#EVENT_BUS}.<br>
     **/
    public static class EnteringChunk extends EntityEvent
    {
        private int newChunkX;
        private int newChunkZ;
        private int oldChunkX;
        private int oldChunkZ;

        public EnteringChunk(Entity entity, int newChunkX, int newChunkZ, int oldChunkX, int oldChunkZ)
        {
            super(entity);
            this.setNewChunkX(newChunkX);
            this.setNewChunkZ(newChunkZ);
            this.setOldChunkX(oldChunkX);
            this.setOldChunkZ(oldChunkZ);
        }

        public int getNewChunkX() { return newChunkX; }
        public void setNewChunkX(int newChunkX) { this.newChunkX = newChunkX; }
        public int getNewChunkZ() { return newChunkZ; }
        public void setNewChunkZ(int newChunkZ) { this.newChunkZ = newChunkZ; }
        public int getOldChunkX() { return oldChunkX; }
        public void setOldChunkX(int oldChunkX) { this.oldChunkX = oldChunkX; }
        public int getOldChunkZ() { return oldChunkZ; }
        public void setOldChunkZ(int oldChunkZ) { this.oldChunkZ = oldChunkZ; }
    }
}
