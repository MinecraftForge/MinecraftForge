package net.minecraft.src.flora;
import net.minecraft.src.*;
import net.minecraft.src.forge.*;
import java.util.ArrayList;
import java.util.Random;
import net.minecraft.src.*;

public class BerryBush extends BlockLeavesBase
    implements ITextureProvider
{
    private int icon;
    Random random;

    public BerryBush(int i, int j)
    {
        super(i, j, Material.leaves, false);
        icon = j;
        this.setTickRandomly(true);
        random = new Random();
    }

    public boolean isOpaqueCube()
    {
        //return !graphicsLevel;
    	return false;
    }
    
    public void setGraphicsLevel(boolean flag)
	{
		graphicsLevel = flag;
		this.blockIndexInTexture = this.icon + (flag ? 0 : 32);
	}

    @Override
    public int getBlockTextureFromSideAndMetadata(int i, int j)
    {
        if (j < 12)
        {
            return blockIndexInTexture + j % 4;
        }
        else
        {
            return blockIndexInTexture + 16 + j % 4;
        }
    }

    protected int damageDropped(int i)
    {
        return i % 4;
    }

    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int i, int j, int k)
    {
        int l = world.getBlockMetadata(i, j, k);
        if (l < 4)
        {
            return AxisAlignedBB.getBoundingBoxFromPool((double)i + 0.25D, j, (double)k + 0.25D, (double)i + 0.75D, (double)j + 0.5D, (double)k + 0.75D);
        } else
        if (l < 8)
        {
            return AxisAlignedBB.getBoundingBoxFromPool((double)i + 0.125D, j, (double)k + 0.125D, (double)i + 0.875D, (double)j + 0.75D, (double)k + 0.875D);
        }
        else
        {
            return AxisAlignedBB.getBoundingBoxFromPool(i, j, k, (double)i + 1.0D, (double)j + 1.0D, (double)k + 1.0D);
        }
    }
    
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z)
    {
        int l = world.getBlockMetadata(x, y, z);
        if (l < 4)
        {
            return AxisAlignedBB.getBoundingBoxFromPool((double)x + 0.25D, y, (double)z + 0.25D, (double)x + 0.75D, (double)y + 0.5D, (double)z + 0.75D);
        } else
        if (l < 8)
        {
            return AxisAlignedBB.getBoundingBoxFromPool((double)x + 0.125D, y, (double)z + 0.125D, (double)x + 0.875D, (double)y + 0.75D, (double)z + 0.875D);
        }
        else
        {
            return AxisAlignedBB.getBoundingBoxFromPool(x, y, z, (double)x + 1.0D, (double)y + 1.0D, (double)z + 1.0D);
        }
    }
    
    public void setBlockBoundsBasedOnState(IBlockAccess iblockaccess, int x, int y, int z)
    {
    	int md = iblockaccess.getBlockMetadata(x, y, z);
    	
        float minX;
        float minY = 0F;
        float minZ;
        float maxX;
        float maxY;
        float maxZ;
        
        if(md < 4)
        {
        	minX = minZ = 0.25F;
        	maxX = maxZ = 0.75F;
        	maxY = 0.5F;
        } else
        
        if(md < 8)
        {
        	minX = minZ = 0.125F;
        	maxX = maxZ = 0.875F;
        	maxY = 0.75F;
        }
        
        else
        {
        	minX = minZ = 0.0F;
        	maxX = maxZ = 1.0F;
        	maxY = 1.0F;
        }
        setBlockBounds(minX, minY, minZ, maxX, maxY, maxZ);
    }

    public boolean blockActivated(World world, int i, int j, int k, EntityPlayer entityplayer)
    {
        if (world.isRemote)
        {
            return false;
        }
        int l = world.getBlockMetadata(i, j, k);
        if (l >= 12)
        {
            world.setBlockAndMetadataWithNotify(i, j, k, blockID, l - 4);
            int i1 = 1;
            EntityItem entityitem = new EntityItem(world, entityplayer.posX, entityplayer.posY - 1.0D, entityplayer.posZ, new ItemStack(mod_FloraSoma.berry.shiftedIndex, i1, l - 12));
            world.spawnEntityInWorld(entityitem);
        }
        return true;
    }

    public boolean renderAsNormalBlock()
    {
        return false;
    }

    public int getRenderType()
    {
        return mod_FloraSoma.berryModelID;
    }

    public void updateTick(World world, int i, int j, int k, Random random1)
    {
        if (world.isRemote)
        {
            return;
        }
        
        if (random1.nextInt(16) == 0 && world.getBlockLightValue(i, j, k) >= 8)
        {
        	int md = world.getBlockMetadata(i, j, k);
            if (md < 12)
            {
                world.setBlockAndMetadataWithNotify(i, j, k, blockID, md + 4);
            }
            if (random1.nextInt(3) == 0 && world.getBlockId(i, j - 1, k) != blockID && world.getBlockId(i, j + 1, k) == 0 && md >= 8)
            {
                world.setBlockAndMetadataWithNotify(i, j + 1, k, blockID, md % 4);
            }
        }
    }

    public String getTextureFile()
    {
        return "/floratex/plantblocks.png";
    }

    public int getFlammability(IBlockAccess iblockaccess, int i, int j, int k, int l, int i1)
    {
        return 25;
    }

    public boolean isFlammable(IBlockAccess iblockaccess, int i, int j, int k, int l, int i1)
    {
        return true;
    }

    public int getFireSpreadSpeed(World world, int i, int j, int k, int l, int i1)
    {
        return 4;
    }

    public void addCreativeItems(ArrayList arraylist)
    {
        arraylist.add(new ItemStack(mod_FloraSoma.berryBush, 1, 8));
        arraylist.add(new ItemStack(mod_FloraSoma.berryBush, 1, 9));
        arraylist.add(new ItemStack(mod_FloraSoma.berryBush, 1, 10));
        arraylist.add(new ItemStack(mod_FloraSoma.berryBush, 1, 11));
        arraylist.add(new ItemStack(mod_FloraSoma.berryBush, 1, 12));
        arraylist.add(new ItemStack(mod_FloraSoma.berryBush, 1, 13));
        arraylist.add(new ItemStack(mod_FloraSoma.berryBush, 1, 14));
        arraylist.add(new ItemStack(mod_FloraSoma.berryBush, 1, 15));
    }
}
