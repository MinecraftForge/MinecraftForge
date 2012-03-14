package net.minecraft.src.orizon;
import java.util.ArrayList;
import java.util.Random;

import net.minecraft.src.forge.*;
import net.minecraft.src.*;

public class OreReplacement extends Block
    implements ITextureProvider
{
	private boolean active;
	
    public OreReplacement(int i, int j)
    {
        super(i, j, Material.rock);
    }

    protected int damageDropped(int md)
    {
    	if(md < 4)
        	return 0;
        if(md < 8)
        	return 0;
        if(md < 12)
        	return 4;
    	
        return 0;
    }
    
    @Override
    public int idDropped(int md, Random random, int fortune)
    {
        if(md < 4)
        	return Block.oreCoal.idDropped(md, random, fortune);
        if(md < 8)
        	return Block.oreDiamond.idDropped(md, random, fortune);
        if(md < 12)
        	return Block.oreLapis.idDropped(md, random, fortune);
    	
        return Block.oreRedstone.idDropped(md, random, fortune);
    }
    
    @Override
    public int quantityDropped(int md, int fortune, Random random)
    {
    	if(md < 4)
        	return Block.oreCoal.quantityDroppedWithBonus(fortune, random);
        if(md < 8)
        	return Block.oreDiamond.quantityDroppedWithBonus(fortune, random);
        if(md < 12)
        	return Block.oreLapis.quantityDroppedWithBonus(fortune, random);
    	
        return Block.oreRedstone.quantityDroppedWithBonus(fortune, random);
    }

    @Override
    public int getBlockTextureFromSideAndMetadata(int side, int md)
    {
        return blockIndexInTexture + md;
    }

    @Override
    public String getTextureFile()
    {
        return "/oretex/ores.png";
    }
    
    //Redstone ore code
    public void onBlockClicked(World world, int i, int j, int k, EntityPlayer entityplayer)
    {
        glow(world, i, j, k);
        super.onBlockClicked(world, i, j, k, entityplayer);
    }

    public void onEntityWalking(World world, int i, int j, int k, Entity entity)
    {
        glow(world, i, j, k);
        super.onEntityWalking(world, i, j, k, entity);
    }

    public boolean blockActivated(World world, int i, int j, int k, EntityPlayer entityplayer)
    {
        glow(world, i, j, k);
        return super.blockActivated(world, i, j, k, entityplayer);
    }

    private void glow(World world, int i, int j, int k)
    {
        sparkle(world, i, j, k);
        /*if (blockID == Block.oreRedstone.blockID)
        {
            world.setBlockWithNotify(i, j, k, Block.oreRedstoneGlowing.blockID);
        }*/
    }
    
    private void sparkle(World world, int i, int j, int k)
    {
        Random random = world.rand;
        double d = 0.0625D;
        for (int l = 0; l < 6; l++)
        {
            double d1 = (float)i + random.nextFloat();
            double d2 = (float)j + random.nextFloat();
            double d3 = (float)k + random.nextFloat();
            if (l == 0 && !world.isBlockOpaqueCube(i, j + 1, k))
            {
                d2 = (double)(j + 1) + d;
            }
            if (l == 1 && !world.isBlockOpaqueCube(i, j - 1, k))
            {
                d2 = (double)(j + 0) - d;
            }
            if (l == 2 && !world.isBlockOpaqueCube(i, j, k + 1))
            {
                d3 = (double)(k + 1) + d;
            }
            if (l == 3 && !world.isBlockOpaqueCube(i, j, k - 1))
            {
                d3 = (double)(k + 0) - d;
            }
            if (l == 4 && !world.isBlockOpaqueCube(i + 1, j, k))
            {
                d1 = (double)(i + 1) + d;
            }
            if (l == 5 && !world.isBlockOpaqueCube(i - 1, j, k))
            {
                d1 = (double)(i + 0) - d;
            }
            if (d1 < (double)i || d1 > (double)(i + 1) || d2 < 0.0D || d2 > (double)(j + 1) || d3 < (double)k || d3 > (double)(k + 1))
            {
                world.spawnParticle("reddust", d1, d2, d3, 0.0D, 0.0D, 0.0D);
            }
        }
    }
}
