package net.minecraftforge.common;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

public interface ICustomHighlightedBlock
{
    public AxisAlignedBB[] getHighlightBoxes(World world, int x, int y, int z, EntityPlayer player);
}
