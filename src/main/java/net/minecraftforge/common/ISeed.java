package net.minecraftforge.common;

import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;

public interface ISeed
{
    /**
     * @return Whether the plant can stay at this location in the world.<br>
     */
    public boolean canPlantStay(IBlockAccess world, BlockPos pos, ItemStack stack);
    
    /**
     * @return Whether the plant grows (through spreading or aging).
     */
    public boolean isGrowable(ItemStack stack);
}