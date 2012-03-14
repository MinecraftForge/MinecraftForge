package net.minecraft.src.blocks;
import java.util.ArrayList;

import net.minecraft.src.*;
import net.minecraft.src.forge.*;

public class Brownstone extends Block
	implements ITextureProvider
{

    public Brownstone(int i, int j)
    {
        super(i, j, Material.rock);
    }

    public void onEntityWalking(World world, int i, int j, int k, Entity entity)
    {
    	double boost = 2.2D;
    	if(world.getBlockMetadata(i, j, k) == 2)
    		boost = 2.65D;
        double mX = Math.abs(entity.motionX);
        double mZ = Math.abs(entity.motionZ);
        if(mX < 0.3D)
        {
            entity.motionX *= boost;
        }
        if(mZ < 0.3D)
        {
            entity.motionZ *= boost;
        }
    }
    
    protected int damageDropped(int md)
    {
        return md;
    }
    
    public int getBlockTextureFromSideAndMetadata(int side, int md)
    {
        return blockIndexInTexture + md;
    }
    
    public void addCreativeItems(ArrayList arraylist)
    {
        arraylist.add(new ItemStack(mod_InfiBlocks.brownstone, 1, 0));
        arraylist.add(new ItemStack(mod_InfiBlocks.brownstone, 1, 1));
        arraylist.add(new ItemStack(mod_InfiBlocks.brownstone, 1, 2));
        arraylist.add(new ItemStack(mod_InfiBlocks.brownstone, 1, 3));
        arraylist.add(new ItemStack(mod_InfiBlocks.brownstone, 1, 4));
        arraylist.add(new ItemStack(mod_InfiBlocks.brownstone, 1, 5));
    }
    
    public String getTextureFile()
    {
        return "/infiblocks/infiblocks.png";
    }
}
