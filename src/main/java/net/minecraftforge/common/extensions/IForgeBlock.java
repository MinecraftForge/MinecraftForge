package net.minecraftforge.common.extensions;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;

import javax.annotation.Nullable;

public interface IForgeBlock
{
    default Block getBlock()
    {
        return (Block) this;

    }

    /**
     * Gets the slipperiness at the given location at the given state. Normally
     * between 0 and 1.
     * <p>
     * Note that entities may reduce slipperiness by a certain factor of their own;
     * for {@link net.minecraft.entity.EntityLivingBase}, this is {@code .91}.
     * {@link net.minecraft.entity.item.EntityItem} uses {@code .98}, and
     * {@link net.minecraft.entity.projectile.EntityFishHook} uses {@code .92}.
     *
     * @param state state of the block
     * @param world the world
     * @param pos the position in the world
     * @param entity the entity in question
     * @return the factor by which the entity's motion should be multiplied
     */
    float getSlipperiness(IBlockState state, IWorldReader world, BlockPos pos, @Nullable Entity entity);

    /**
     * Get a light value for this block, taking into account the given state and coordinates, normal ranges are between 0 and 15
     *
     * @param state
     * @param reader
     * @param pos
     * @return The light value
     */
    default int getLightVaule(IBlockState state, IWorldReader reader, BlockPos pos)
    {
        return state.getLightValue();
    }

    /**
     * Checks if a player or entity can use this block to 'climb' like a ladder.
     *
     * @param state The current state
     * @param world The current world
     * @param pos Block position in world
     * @param entity The entity trying to use the ladder, CAN be null.
     * @return True if the block should act like a ladder
     */
    default boolean isLadder(IBlockState state, IWorldReader world, BlockPos pos, EntityLivingBase entity)
    {
        return false;
    }

    /**
     * Return true if the block is a normal, solid cube.  This
     * determines indirect power state, entity ejection from blocks, and a few
     * others.
     *
     * @param state The current state
     * @param world The current world
     * @param pos Block position in world
     * @return True if the block is a full cube
     */
    default boolean isNormalCube(IBlockState state, IWorldReader world, BlockPos pos)
    {
        return state.isNormalCube();
    }
    /**
     * Check if the face of a block should block rendering.
     *
     * Faces which are fully opaque should return true, faces with transparency
     * or faces which do not span the full size of the block should return false.
     *
     * @param state The current block state
     * @param world The current world
     * @param pos Block position in world
     * @param face The side to check
     * @return True if the block is opaque on the specified side.
     */
    default boolean doesSideBlockRendering(IBlockState state, IWorldReader world, BlockPos pos, EnumFacing face)
    {
       return state.func_200015_d(world, pos);
    }

    /**
     * Determines if this block should set fire and deal fire damage
     * to entities coming into contact with it.
     *
     * @param world The current world
     * @param pos Block position in world
     * @return True if the block should deal damage
     */
    default boolean isBurning(IWorldReader world, BlockPos pos)
    {
        return false;
    }

    /**
     * Called throughout the code as a replacement for block instanceof BlockContainer
     * Moving this to the Block base class allows for mods that wish to extend vanilla
     * blocks, and also want to have a tile entity on that block, may.
     *
     * Return true from this function to specify this block has a tile entity.
     *
     * @param state State of the current block
     * @return True if block has a tile entity, false otherwise
     */
    default boolean hasTIleEntity(IBlockState state)
    {
        return this.getBlock().hasTileEntity();
    }

    boolean canSilkHarvest(IWorldReader world, BlockPos pos, IBlockState state, EntityPlayer player);

    /**
     * Gathers how much experience this block drops when broken.
     *
     * @param state The current state
     * @param world The world
     * @param pos Block position
     * @param fortune
     * @return Amount of XP from breaking this block.
     */
    default int getExpDrop(IBlockState state, IWorldReader world, BlockPos pos, int fortune)
    {
        return 0;
    }
}
