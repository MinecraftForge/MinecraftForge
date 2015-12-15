package net.minecraftforge.common;

import java.util.Collection;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public interface IPlantable
{
    /**
     * @return A collection containing all soil types the plant should grow on.
     */
    public Collection<EnumPlantType> getSoilTypes(IBlockAccess world, BlockPos pos, IBlockState state);
    
    /**
     * Behavior is the same as
     * {@link net.minecraft.block.BlockBush#canBlockStay(World, BlockPos, IBlockState) BlockBush.canBlockStay}.
     * @return Whether the plant can stay at this location in the world.<br>
     */
    public boolean canPlantStay(World world, BlockPos pos, IBlockState state);
    
    /**
     * @return Whether the plant grows (through spreading or aging).<br>
     * Under the assumption that plants that grow on the Crops soil type are affected by hydration,
     * returning true prevents farmland from turning into dirt.
     */
    public boolean isGrowable(IBlockAccess world, BlockPos pos, IBlockState state);
}