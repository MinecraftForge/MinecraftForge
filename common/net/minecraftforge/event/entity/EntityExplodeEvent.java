package net.minecraftforge.event.entity;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.event.Cancelable;

@Cancelable
public class EntityExplodeEvent extends EntityEvent
{
    public final World world;
    
    public final double posX;
    public final double posY;
    public final double posZ;
    
    /**
     * the strength of the explosion
     */
    public float strength;
    /**
     * whether or not the explosion sets fire to blocks around it 
     */
    public boolean isFlaming;
    /**
     * whether or not this explosion spawns smoke particles 
     */
    public boolean isSmoking;
    
    public EntityExplodeEvent(World world, Entity entity, double positionX, double positionY, double positionZ, float strength, boolean isFlaming, boolean isSmoking)
    {
        super(entity);
        this.world = world;
        this.posX = positionX;
        this.posY = positionY;
        this.posZ = positionZ;
        this.strength = strength;
        this.isFlaming = isFlaming;
        this.isSmoking = isSmoking;
    }
}
