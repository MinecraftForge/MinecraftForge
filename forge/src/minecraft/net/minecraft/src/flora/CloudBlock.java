package net.minecraft.src.flora;

import net.minecraft.src.forge.ITextureProvider;
import net.minecraft.src.*;

public class CloudBlock extends Block
    implements ITextureProvider
{
    public CloudBlock(int i)
    {
        super(i, 96, Material.cloth);
    }

    @Override
    public void onEntityCollidedWithBlock(World world, int i, int j, int k, Entity entity)
    {
        if (entity.motionY < 0.0D)
        {
            entity.motionY *= 0.0050000000000000001D;
        }
        entity.fallDistance = 0.0F;
    }

    @Override
    public int getRenderBlockPass()
    {
        return 1;
    }

    @Override
    public boolean shouldSideBeRendered(IBlockAccess iblockaccess, int i, int j, int k, int l)
    {
        int i1 = iblockaccess.getBlockId(i, j, k);
        if (i1 == mod_FloraSoma.cloud.blockID)
        {
            return false;
        }
        else
        {
            return super.shouldSideBeRendered(iblockaccess, i, j, k, l);
        }
    }

    @Override
    public boolean renderAsNormalBlock()
    {
        return false;
    }

    @Override
    protected int damageDropped(int i)
    {
        return i;
    }

    @Override
    public boolean isOpaqueCube()
    {
        return false;
    }

    @Override
    public String getTextureFile()
    {
        return "/floratex/plantblocks.png";
    }

    @Override
    public boolean isBlockSolid(IBlockAccess iblockaccess, int i, int j, int k, int l)
    {
        int i1 = iblockaccess.getBlockId(i, j, k);
        if (i1 == mod_FloraSoma.cloud.blockID)
        {
            return false;
        }
        else
        {
            return super.isBlockSolid(iblockaccess, i, j, k, l);
        }
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int i, int j, int k)
    {
        if (world.getBlockId(i, j - 1, k) == mod_FloraSoma.cloud.blockID)
        {
            return null;
        }
        else
        {
            return AxisAlignedBB.getBoundingBoxFromPool(i, j, k, (double)i + 1.0D, (double)j + 0.0625D, (double)k + 1.0D);
        }
    }

    @Override
    public int getBlockTextureFromSideAndMetadata(int i, int j)
    {
        return blockIndexInTexture + j;
    }
}
