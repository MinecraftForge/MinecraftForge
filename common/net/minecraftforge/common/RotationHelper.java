package net.minecraftforge.common;

import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.BlockDropper;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.world.World;
import static net.minecraftforge.common.ForgeDirection.*;

public class RotationHelper {

    private static final ForgeDirection[] UP_DOWN_AXES = new ForgeDirection[] { UP, DOWN };
    public static ForgeDirection[] getValidVanillaBlockRotations(Block block)
    {
        return block instanceof BlockChest ? UP_DOWN_AXES : VALID_DIRECTIONS;
    }

    public static boolean rotateVanillaBlock(Block block, World worldObj, int x, int y, int z, ForgeDirection axis)
    {
        if (worldObj.isRemote)
        {
            return false;
        }

        if (block instanceof BlockChest && (axis == UP || axis == DOWN))
        {
            return rotateBlock(worldObj, x, y, z, axis, 0x7);
        }
        if (block instanceof BlockPistonBase || block instanceof BlockDropper || block instanceof BlockDispenser)
        {
            return rotateBlock(worldObj, x, y, z, axis, 0x7);
        }
        return false;
    }

    private static boolean rotateBlock(World worldObj, int x, int y, int z, ForgeDirection axis, int mask)
    {
        int rotMeta = worldObj.getBlockMetadata(x, y, z);
        int masked = rotMeta & ~mask;
        ForgeDirection orientation = ForgeDirection.getOrientation(rotMeta & mask);
        ForgeDirection rotated = orientation.getRotation(axis);
        worldObj.setBlockMetadataWithNotify(x,y,z,rotated.ordinal() & mask | masked,3);
        return true;
    }

}
