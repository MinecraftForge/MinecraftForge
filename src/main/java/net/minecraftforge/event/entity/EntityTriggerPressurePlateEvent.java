package net.minecraftforge.event.entity;

import cpw.mods.fml.common.eventhandler.Cancelable;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

/**
 * An event used to modify an entity's ability to trigger a pressure plate.
 * Cancel if you want to prevent an entity from triggering a pressure plate.
 */
@Cancelable
public class EntityTriggerPressurePlateEvent extends EntityEvent {
    
    public final World world;
    /**
     * The x coord of the pressure plate
     */
    public final int blockX;
    /**
     * The y coord of the pressure plate
     */
    public final int blockY;
    /**
     * The z coord of the pressure plate
     */
    public final int blockZ;
    
    public final Block pressurePlateBlock;
    
    public EntityTriggerPressurePlateEvent(Entity entity, World world, int blockX, int blockY, int blockZ, Block pressurePlate)
    {
        super(entity);
        
        this.world = world;
        this.blockX = blockX;
        this.blockY = blockY;
        this.blockZ = blockZ;
        this.pressurePlateBlock = pressurePlate;
        
    }
    
}
