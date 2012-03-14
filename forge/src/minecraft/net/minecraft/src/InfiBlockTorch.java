package net.minecraft.src;
import net.minecraft.src.*;
import net.minecraft.src.forge.*;
import java.util.Random;

public class InfiBlockTorch extends BlockTorch
	implements ITextureProvider
{
    protected InfiBlockTorch(int i, int j)
    {
        super(i, j);
        this.setTickRandomly(true);
    }

    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int i, int j, int k)
    {
        return null;
    }
    
    public int getBlockTextureFromSideAndMetadata(int side, int md)
    {
        if(md < 5) {
        	return blockIndexInTexture;
        }
        else if (md < 10) {
        	return blockIndexInTexture + 1;
        }
        else {
        	return blockIndexInTexture + 2;
        }
    }

    public int getRenderType()
    {
        return mod_InfiLighting.torchModel;
    }

    private boolean canPlaceTorchOn(World world, int i, int j, int k)
    {
        if (world.isBlockSolidOnSide(i, j, k, 1))
        {
            return true;
        }
        else
        {
            int l = world.getBlockId(i, j, k);
            return l == Block.fence.blockID || l == Block.netherFence.blockID;
        }
    }
    
    public void onBlockPlaced(World world, int i, int j, int k, int l)
    {
        int meta = world.getBlockMetadata(i, j, k);
        System.out.println("meta: " + meta);
        int md = 0;
        if(meta >= 5)
        	md += 1;
        if(meta >= 10)
        	md += 1;
        md *= 5;
        System.out.println("md: " + md);
        
        if (l == 1 && canPlaceTorchOn(world, i, j - 1, k))
        {
            meta = 0 + md;
        }
        if (l == 2 && world.isBlockSolidOnSide(i, j, k + 1, 2))
        {
            meta = 4 + md;
        }
        if (l == 3 && world.isBlockSolidOnSide(i, j, k - 1, 3))
        {
            meta = 3 + md;
        }
        if (l == 4 && world.isBlockSolidOnSide(i + 1, j, k, 4))
        {
            meta = 2 + md;
        }
        if (l == 5 && world.isBlockSolidOnSide(i - 1, j, k, 5))
        {
            meta = 1 + md;
        }
        world.setBlockMetadataWithNotify(i, j, k, meta);
        System.out.println("mdw: " + world.getBlockMetadata(i, j, k));
    }
    
    protected int damageDropped(int md)
    {
        return md - md % 5;
    }
    
    @Override
    public void onBlockAdded(World world, int i, int j, int k) {}
    
    @Override
    public void updateTick(World world, int i, int j, int k, Random random) {}
    
    public String getTextureFile()
    {
        return "/lighttex/torches.png";
    }
}
