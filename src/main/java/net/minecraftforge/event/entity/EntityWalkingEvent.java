package net.minecraftforge.event.entity;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import cpw.mods.fml.common.eventhandler.Cancelable;

/**
 * An event triggered when an entity walks over a block. Cancel this event to
 * prevent the block from running Block.onEntityWalking
 */
@Cancelable
public class EntityWalkingEvent extends EntityEvent {

    /**
     * The world containing the entity and block
     */
    public final World world;
    /**
     * The block the entity is walking over
     */
    public final Block block;
    /**
     * The X coordinate of the block
     */
    public final int x;
    /**
     * The Y coordinate of the block
     */
    public final int y;
    /**
     * The Z coordinate of the block
     */
    public final int z;

    public EntityWalkingEvent(World world, Entity entity, Block block, int x, int y, int z)
    {
        super(entity);
        this.world = world;
        this.block = block;
        this.x = x;
        this.y = y;
        this.z = z;

    }

}
