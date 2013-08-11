package net.minecraftforge.event.entity;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.Event;

public class EntityEvent extends Event
{
    public final Entity entity;

    public EntityEvent(Entity entity)
    {
        this.entity = entity;
    }

    public static class EntityConstructing extends EntityEvent
    {
        public EntityConstructing(Entity entity)
        {
            super(entity);
        }
    }

    public static class CanUpdate extends EntityEvent
    {
        public boolean canUpdate = false;
        public CanUpdate(Entity entity)
        {
            super(entity);
        }
    }

    public static class EnteringChunk extends EntityEvent
    {
        public int newChunkX;
        public int newChunkZ;
        public int oldChunkX;
        public int oldChunkZ;

        public EnteringChunk(Entity entity, int newChunkX, int newChunkZ, int oldChunkX, int oldChunkZ)
        {
            super(entity);
            this.newChunkX = newChunkX;
            this.newChunkZ = newChunkZ;
            this.oldChunkX = oldChunkX;
            this.oldChunkZ = oldChunkZ;
        }
    }
    
    public static class EntityWriteToNBT extends EntityEvent
    {
        public NBTTagCompound nbt;
        
        public EntityWriteToNBT(Entity entity, NBTTagCompound nbt)
        {
            super(entity);
            this.nbt = nbt;
        }
    }
    
    public static class EntityReadFromNBT extends EntityEvent
    {
        public NBTTagCompound nbt;
        
        public EntityReadFromNBT(Entity entity, NBTTagCompound nbt)
        {
            super(entity);
            this.nbt = nbt;
        }
    }
}
