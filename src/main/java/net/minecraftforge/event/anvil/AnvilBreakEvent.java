package net.minecraftforge.event.anvil;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

@Cancelable
public class AnvilBreakEvent extends Event
{
    /**
     * This is the state of the block before it broke.
     */
    private final BlockState statePreBreaking;

    /**
     * This is the World of the Entity, be it the FallingBlockEntity or Player.
     */
    private final World world;

    /**
     * This is the BlockPos of where the block broke.
     */
    private final BlockPos pos;

    /**
     * This is the entity involved with the break.
     * This can either be a FallingBlockEntity or a PlayerEntity.
     */
    private final Entity entity;

    public AnvilBreakEvent(BlockState statePreBreaking, World world, BlockPos pos, Entity entity)
    {
        this.statePreBreaking = statePreBreaking;
        this.world = world;
        this.pos = pos;
        this.entity = entity;
    }

    public BlockState getStatePreBreaking() { return statePreBreaking; }

    public World getWorld() { return world; }

    public BlockPos getPos() { return pos; }

    public Entity getEntity() { return entity; }
}