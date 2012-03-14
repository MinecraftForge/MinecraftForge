package net.minecraft.src.flora;

import net.minecraft.src.forge.ITextureProvider;
import java.util.ArrayList;
import java.util.Random;
import net.minecraft.src.*;

public class RedwoodBlock extends BlockLog
    implements ITextureProvider
{
    public RedwoodBlock(int i)
    {
        super(i);
        blockIndexInTexture = 64;
    }

    public int getBlockTextureFromSideAndMetadata(int side, int md)
    {
        if (md == 0) //Redwood Bark
        {
            if (side == 0 || side == 1)
            {
                return blockIndexInTexture + 1;
            }
            else
            {
                return blockIndexInTexture;
            }
        } else
        if (md == 1) //Redwood Heartwood
        {
            return blockIndexInTexture + 2;
        } else
        if (md == 2) //Redwood Planks
        {
            return blockIndexInTexture + 3;
        } else
        
        if (md == 3) //Bloodbark
        {
            if (side == 0 || side == 1)
            {
                return blockIndexInTexture + 5;
            }
            else
            {
                return blockIndexInTexture + 4;
            }
        } else
        if (md == 4) //Bloodplanks
        {
            return blockIndexInTexture + 6;
        } else
        	
        if (md == 5) //Sakura bark
        {
            if (side == 0 || side == 1)
            {
                return blockIndexInTexture + 8;
            }
            else
            {
                return blockIndexInTexture + 7;
            }
        } else
        if (md == 6) //Sakura planks
        {
            return blockIndexInTexture + 9;
        } else
        	
        if (md == 7) //Eucalyptus bark
        {
        	if (side == 0 || side == 1)
            {
                return blockIndexInTexture + 11;
            }
            else
            {
                return blockIndexInTexture + 10;
            }
        } else
        if (md == 8) //Eucalyptus planks
        {
            return blockIndexInTexture + 12;
        } else
        
        {
            return blockIndexInTexture;
        }
    }

    protected int damageDropped(int i)
    {
        return i;
    }

    public String getTextureFile()
    {
        return "/floratex/plantblocks.png";
    }

    public int idDropped(int i, Random random, int j)
    {
        return mod_FloraSoma.redwood.blockID;
    }

    public int getFlammability(IBlockAccess iblockaccess, int i, int j, int k, int l, int i1)
    {
    	if(l == 3 || l == 4)
        {
        	return 0;
        } else
        if(l == 5 || l == 6)
        {
        	return 4;
        }
        {
        	return 20;
        }
    }

    public boolean isFlammable(IBlockAccess world, int x, int y, int z, int metadata, int face)
    {
        if(metadata == 3 || metadata == 4) {
        	return false;
        } else {
        	return true;
        }
    	
    }

    public int getFireSpreadSpeed(World world, int i, int j, int k, int md, int i1)
    {
        if(md == 3 || md == 4)
        {
        	return 0;
        } else
        if(md == 5 || md == 6)
        {
        	return 1;
        } else
        {
        	return 5;
        }
    }
    
    public float getHardness(int md) {
    	if(md == 3 || md == 4)
    	{
    		return blockHardness + 5;
    	} else
    	{
    		return blockHardness;
    	}
    }

    public void addCreativeItems(ArrayList arraylist)
    {
        arraylist.add(new ItemStack(mod_FloraSoma.redwood, 1, 0));
        arraylist.add(new ItemStack(mod_FloraSoma.redwood, 1, 1));
        arraylist.add(new ItemStack(mod_FloraSoma.redwood, 1, 2));
        arraylist.add(new ItemStack(mod_FloraSoma.redwood, 1, 3));
        arraylist.add(new ItemStack(mod_FloraSoma.redwood, 1, 4));
        arraylist.add(new ItemStack(mod_FloraSoma.redwood, 1, 5));
        arraylist.add(new ItemStack(mod_FloraSoma.redwood, 1, 6));
        arraylist.add(new ItemStack(mod_FloraSoma.redwood, 1, 7));
        arraylist.add(new ItemStack(mod_FloraSoma.redwood, 1, 8));
    }
}
