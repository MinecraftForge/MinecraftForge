package net.minecraft.src.flora;
import net.minecraft.src.*;

import java.util.Random;

public class SaguaroGen extends WorldGenerator
{

    public SaguaroGen(boolean flag, int blockID, int metadata)
    {
        super(true);
    }

    public boolean generate(World world, Random rand, int x, int y, int z)
    {
    	
    	/*int bID = world.getBlockId(x, yGr, z);
    	int belowID = world.getBlockId(x, yGr - 1, z);*/
    	int yGr = findGround(world, x, y, z);
        /*if (belowID != Block.sand.blockID || bID != 0)
        {
            return false;
        }*/
        int height = rand.nextInt(8) + 3;
        for(int iter = 0; iter < height; iter++) {
        	genBlock(world, x+1, yGr + iter, z);
        }
        if(height < 6) {
        	if(rand.nextInt(2) == 0) {
        		int offGround = rand.nextInt(2) + 1;
        		for(int iterBranch = 0; iterBranch < rand.nextInt(2) + 1; iterBranch++) 
        		{
        			genBlock(world, x+1, yGr + iterBranch + offGround, z);
        		}
        		offGround = rand.nextInt(2) + 1;
        		for(int iterBranch = 0; iterBranch < rand.nextInt(2) + 1; iterBranch++) 
        		{
        			genBlock(world, x-1, yGr + iterBranch + offGround, z);
        		}
        	} else {
        		int offGround = rand.nextInt(2) + 1;
        		for(int iterBranch = 0; iterBranch < rand.nextInt(2) + 1; iterBranch++) 
        		{
        			genBlock(world, x, yGr + iterBranch + offGround, z+1);
        		}
        		offGround = rand.nextInt(2) + 1;
        		for(int iterBranch = 0; iterBranch < rand.nextInt(2) + 1; iterBranch++) 
        		{
        			genBlock(world, x, yGr + iterBranch + offGround, z-1);
        		}
        	}
        } else {
        	if(rand.nextInt(2) == 0) {
        		int offGround = rand.nextInt(4) + 2;
        		genBlock(world, x+1, yGr + offGround, z);
        		for(int iterBranch = 0; iterBranch < rand.nextInt(2) + 1; iterBranch++) 
        		{
        			genBlock(world, x+2, yGr + iterBranch + offGround, z);
        		}
        		offGround = rand.nextInt(4) + 2;
        		genBlock(world, x-1,  yGr + offGround, z);
        		for(int iterBranch = 0; iterBranch < rand.nextInt(2) + 1; iterBranch++) 
        		{
        			genBlock(world, x-2, yGr + iterBranch + offGround, z);
        		}
        	} else {
        		int offGround = rand.nextInt(4) + 2;
        		for(int iterBranch = 0; iterBranch < rand.nextInt(2) + 1; iterBranch++) 
        		{
        			genBlock(world, x, yGr + iterBranch + offGround, z+2);
        		}
        		offGround = rand.nextInt(4) + 2;
        		for(int iterBranch = 0; iterBranch < rand.nextInt(2) + 1; iterBranch++) 
        		{
        			genBlock(world, x,  yGr + iterBranch + offGround, z-2);
        		}
        	}
        }
        /*worldObj = world;
        long l = random.nextLong();
        rand.setSeed(l);
        basePos[0] = i;
        basePos[1] = j;
        basePos[2] = k;
        if (heightLimit == 0)
        {
            heightLimit = 8 + rand.nextInt(heightLimitLimit);
        }
        if (!validTreeLocation())
        {
            return false;
        }
        else
        {
            generateLeafNodeList();
            generateLeaves();
            generateTrunk();
            generateLeafNodeBases();
            return true;
        }*/
    	return false;
    }
    
    private void genBlock(World world, int x, int y, int z) {
    	if(!Block.opaqueCubeLookup[world.getBlockId(x, y, z)] )
    		world.setBlock(x, y, z, mod_FloraSoma.saguaro.blockID);
    }
    
    int findGround(World world, int i, int j, int k)
    {
        int l = 0;
        int i1 = world.getBlockId(i, j - 1, k);
        if (!Block.opaqueCubeLookup[world.getBlockId(i, j, k)] && (i1 == Block.sand.blockID))
        {
            return j;
        }
        int k1 = 96;
        do
        {
            if (k1 < 64)
            {
                break;
            }
            int j1 = world.getBlockId(i, k1, k);
            if (j1 ==Block.sand.blockID)
            {
                if (!Block.opaqueCubeLookup[world.getBlockId(i, k1 + 1, k)])
                {
                    l = k1 + 1;
                }
                break;
            }
            k1--;
        }
        while (true);
        return l;
    }

}
