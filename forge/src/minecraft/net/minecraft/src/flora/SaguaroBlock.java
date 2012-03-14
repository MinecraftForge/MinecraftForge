package net.minecraft.src.flora;
import java.util.Random;
import net.minecraft.src.forge.*;
import net.minecraft.src.*;

public class SaguaroBlock extends Block
	implements ITextureProvider
{
    public SaguaroBlock(int i, int j)
    {
        super(i, j, Material.cactus);
    }

    public void updateTick(World world, int i, int j, int k, Random random)
    {
        if (world.isAirBlock(i, j + 1, k))
        {
            int l;
            for (l = 1; world.getBlockId(i, j - l, k) == blockID; l++) { }
            if (l < 6)
            {
                int i1 = world.getBlockMetadata(i, j, k);
                if (i1 == 15)
                {
                    world.setBlockWithNotify(i, j + 1, k, blockID);
                    world.setBlockMetadataWithNotify(i, j, k, 0);
                }
                else
                {
                    world.setBlockMetadataWithNotify(i, j, k, i1 + 1);
                }
            }
        }
    }

    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int i, int j, int k)
    {
        float offset = 0.125F;
                
        return AxisAlignedBB.getBoundingBoxFromPool((float)i + offset, j, (float)k + offset, (float)(i + 1) - offset, (float)(j + 1) - offset, (float)(k + 1) - offset);
    }

    public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int i, int j, int k)
    {
        float f = 0.125F;
        float height = 0.0125F;
        float base = 0F;
        if(world.getBlockId(i, j+1, k) == mod_FloraSoma.saguaro.blockID)
        	height = 0F;
        if(world.getBlockId(i, j-1, k) == 0)
        	base = 0.125F;
        
        return AxisAlignedBB.getBoundingBoxFromPool((float)i + f, j, (float)k + f, (float)(i + 1) - f, (float)(j + 1) - height, (float)(k + 1) - f);
    }

    public int getBlockTextureFromSide(int i)
    {
        if (i == 1)
        {
            return blockIndexInTexture + 1;
        }
        if (i == 0)
        {
            return blockIndexInTexture - 1;
        }
        else
        {
            return blockIndexInTexture;
        }
    }

    public boolean renderAsNormalBlock()
    {
        return false;
    }

    public boolean isOpaqueCube()
    {
        return false;
    }

    public int getRenderType()
    {
        return mod_FloraSoma.saguaroModel;
    }

    public boolean canPlaceBlockAt(World world, int i, int j, int k)
    {
        if (!super.canPlaceBlockAt(world, i, j, k))
        {
            return false;
        }
        else
        {
            return canBlockStay(world, i, j, k);
        }
    }

    public void onNeighborBlockChange(World world, int i, int j, int k, int l)
    {
        if (!canBlockStay(world, i, j, k))
        {
            dropBlockAsItem(world, i, j, k, world.getBlockMetadata(i, j, k), 0);
            world.setBlockWithNotify(i, j, k, 0);
        }
    }

    public boolean canBlockStay(World world, int i, int j, int k)
    {
        /*if (world.getBlockMaterial(i - 1, j, k).isSolid())
        {
            return false;
        }
        if (world.getBlockMaterial(i + 1, j, k).isSolid())
        {
            return false;
        }
        if (world.getBlockMaterial(i, j, k - 1).isSolid())
        {
            return false;
        }
        if (world.getBlockMaterial(i, j, k + 1).isSolid())
        {
            return false;
        }
        else*/
        {
            int l = world.getBlockId(i, j - 1, k);
            return l == mod_FloraSoma.saguaro.blockID || l == Block.sand.blockID || l == 0;
        }
    }

    public void onEntityCollidedWithBlock(World world, int i, int j, int k, Entity entity)
    {
        if(!(entity instanceof EntityItem))
        	entity.attackEntityFrom(DamageSource.cactus, 1);
    }

    public boolean canConnectSuguaroTo(IBlockAccess iblockaccess, int x, int y, int z)
    {
        int bID = iblockaccess.getBlockId(x, y, z);
        if (bID == blockID)
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    
    public String getTextureFile()
    {
        return "/floratex/plantblocks.png";
    }
}
