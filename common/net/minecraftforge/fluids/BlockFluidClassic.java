
package net.minecraftforge.fluids;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

/**
 * This is a fluid block implementation which emulates vanilla Minecraft fluid behavior.
 * 
 * It is highly recommended that you use/extend this class for "classic" fluid blocks.
 * 
 * @author King Lemming
 * 
 */
public class BlockFluidClassic extends BlockFluidBase
{
    protected boolean[] isOptimalFlowDirection = new boolean[4];
    protected int[] flowCost = new int[4];

    protected FluidStack stack;

    public BlockFluidClassic(int id, Fluid fluid, Material material)
    {
        super(id, fluid, material);
        stack = new FluidStack(fluid, FluidContainerRegistry.BUCKET_VOLUME);
    }

    public BlockFluidClassic setFluidStack(FluidStack stack)
    {
        this.stack = stack;
        return this;
    }

    public BlockFluidClassic setFluidStackAmount(int amount)
    {
        this.stack.amount = amount;
        return this;
    }

    @Override
    public int getQuantaValue(IBlockAccess world, int x, int y, int z)
    {
        if (world.getBlockId(x, y, z) == 0)
        {
            return 0;
        }

        if (world.getBlockId(x, y, z) != blockID)
        {
            return -1;
        }

        int quantaRemaining = quantaPerBlock - world.getBlockMetadata(x, y, z);
        return quantaRemaining;
    }

    @Override
    public boolean canCollideCheck(int meta, boolean fullHit)
    {
        return fullHit && meta == 0;
    }

    @Override
    public int getMaxRenderHeightMeta()
    {
        return 0;
    }

    @Override
    public int getLightValue(IBlockAccess world, int x, int y, int z)
    {
        if (maxScaledLight == 0)
        {
            return super.getLightValue(world, x, y, z);
        }
        int data = quantaPerBlock - world.getBlockMetadata(x, y, z) - 1;
        return (int) (data / quantaPerBlockFloat * maxScaledLight);
    }

    @Override
    public void updateTick(World world, int x, int y, int z, Random rand)
    {
        int quantaRemaining = quantaPerBlock - world.getBlockMetadata(x, y, z);
        int expQuanta = -101;

        // check adjacent block levels if non-source
        if (quantaRemaining < quantaPerBlock)
        {
            int y2 = y - densityDir;

            if (world.getBlockId(x,     y2, z    ) == blockID || 
                world.getBlockId(x - 1, y2, z    ) == blockID || 
                world.getBlockId(x + 1, y2, z    ) == blockID ||
                world.getBlockId(x,     y2, z - 1) == blockID ||
                world.getBlockId(x,     y2, z + 1) == blockID)
            {
                expQuanta = quantaPerBlock - 1;
            }
            else
            {
                int maxQuanta = -100;
                maxQuanta = getLargerQuanta(world, x - 1, y, z,     maxQuanta);
                maxQuanta = getLargerQuanta(world, x + 1, y, z,     maxQuanta);
                maxQuanta = getLargerQuanta(world, x,     y, z - 1, maxQuanta);
                maxQuanta = getLargerQuanta(world, x,     y, z + 1, maxQuanta);

                expQuanta = maxQuanta - 1;
            }

            // decay calculation
            if (expQuanta != quantaRemaining)
            {
                quantaRemaining = expQuanta;

                if (expQuanta <= 0)
                {
                    world.setBlockToAir(x, y, z);
                }
                else
                {
                    world.setBlockMetadataWithNotify(x, y, z, quantaPerBlock - expQuanta, 3);
                    world.scheduleBlockUpdate(x, y, z, blockID, tickRate);
                    world.notifyBlocksOfNeighborChange(x, y, z, blockID);
                }
            }
        }
        else if (quantaRemaining > quantaPerBlock)
        {
            world.setBlockMetadataWithNotify(x, y, z, 0, 3);
        }

        // Flow vertically if possible
        if (canDisplace(world, x, y + densityDir, z))
        {
            flowIntoBlock(world, x, y + densityDir, z, 1);
            return;
        }

        // Flow outward if possible
        int flowMeta = quantaPerBlock - quantaRemaining + 1;
        if (flowMeta >= quantaPerBlock)
        {
            return;
        }

        if (isSourceBlock(world, x, y, z) || !isFlowingVertically(world, x, y, z))
        {
            if (world.getBlockId(x, y - densityDir, z) == blockID)
            {
                flowMeta = 1;
            }
            boolean flowTo[] = getOptimalFlowDirections(world, x, y, z);

            if (flowTo[0]) flowIntoBlock(world, x - 1, y, z,     flowMeta);
            if (flowTo[1]) flowIntoBlock(world, x + 1, y, z,     flowMeta);
            if (flowTo[2]) flowIntoBlock(world, x,     y, z - 1, flowMeta);
            if (flowTo[3]) flowIntoBlock(world, x,     y, z + 1, flowMeta);
        }
    }

    public boolean isFlowingVertically(IBlockAccess world, int x, int y, int z)
    {
        return world.getBlockId(x, y + densityDir, z) == blockID ||
            (world.getBlockId(x, y, z) == blockID && canFlowInto(world, x, y + densityDir, z));
    }

    public boolean isSourceBlock(IBlockAccess world, int x, int y, int z)
    {
        return world.getBlockId(x, y, z) == blockID && world.getBlockMetadata(x, y, z) == 0;
    }

    protected boolean[] getOptimalFlowDirections(World world, int x, int y, int z)
    {
        for (int side = 0; side < 4; side++)
        {
            flowCost[side] = 1000;

            int x2 = x;
            int y2 = y;
            int z2 = z;

            switch (side)
            {
                case 0: --x2; break;
                case 1: ++x2; break;
                case 2: --z2; break;
                case 3: ++z2; break;
            }

            if (!canFlowInto(world, x2, y2, z2) || isSourceBlock(world, x2, y2, z2))
            {
                continue;
            }

            if (canFlowInto(world, x2, y2 + densityDir, z2))
            {
                flowCost[side] = 0;
            }
            else
            {
                flowCost[side] = calculateFlowCost(world, x2, y2, z2, 1, side);
            }
        }

        int min = flowCost[0];
        for (int side = 1; side < 4; side++)
        {
            if (flowCost[side] < min)
            {
                min = flowCost[side];
            }
        }
        for (int side = 0; side < 4; side++)
        {
            isOptimalFlowDirection[side] = flowCost[side] == min;
        }
        return isOptimalFlowDirection;
    }

    protected int calculateFlowCost(World world, int x, int y, int z, int recurseDepth, int side)
    {
        int cost = 1000;
        for (int adjSide = 0; adjSide < 4; adjSide++)
        {
            if ((adjSide == 0 && side == 1) || 
                (adjSide == 1 && side == 0) ||
                (adjSide == 2 && side == 3) ||
                (adjSide == 3 && side == 2))
            {
                continue;
            }

            int x2 = x;
            int y2 = y;
            int z2 = z;

            switch (adjSide)
            {
                case 0: --x2; break;
                case 1: ++x2; break;
                case 2: --z2; break;
                case 3: ++z2; break;
            }

            if (!canFlowInto(world, x2, y2, z2) || isSourceBlock(world, x2, y2, z2))
            {
                continue;
            }

            if (canFlowInto(world, x2, y2 + densityDir, z2))
            {
                return recurseDepth;
            }

            if (recurseDepth >= 4)
            {
                continue;
            }

            int min = calculateFlowCost(world, x2, y2, z2, recurseDepth + 1, adjSide);
            if (min < cost)
            {
                cost = min;
            }
        }
        return cost;
    }

    protected void flowIntoBlock(World world, int x, int y, int z, int meta)
    {
        if (meta < 0) return;
        if (displaceIfPossible(world, x, y, z))
        {
            world.setBlock(x, y, z, this.blockID, meta, 3);
        }
    }

    protected boolean canFlowInto(IBlockAccess world, int x, int y, int z)
    {
        if (world.isAirBlock(x, y, z)) return true;

        int bId = world.getBlockId(x, y, z);
        if (bId == blockID)
        {
            return true;
        }

        if (displacementIds.containsKey(bId))
        {
            return displacementIds.get(bId);
        }

        Material material = Block.blocksList[bId].blockMaterial;
        if (material.blocksMovement()  ||
            material == Material.water ||
            material == Material.lava  ||
            material == Material.portal)
        {
            return false;
        }
        return true;
    }

    protected int getLargerQuanta(IBlockAccess world, int x, int y, int z, int compare)
    {
        int quantaRemaining = getQuantaValue(world, x, y, z);
        if (quantaRemaining <= 0)
        {
            return compare;
        }
        return quantaRemaining >= compare ? quantaRemaining : compare;
    }

    /* IFluidBlock */
    @Override
    public FluidStack drain(World world, int x, int y, int z, boolean doDrain)
    {
        if (!isSourceBlock(world, x, y, z))
        {
            return null;
        }

        if (doDrain)
        {
            world.setBlockToAir(x, y, z);
        }

        return stack.copy();
    }

    @Override
    public boolean canDrain(World world, int x, int y, int z)
    {
        return isSourceBlock(world, x, y, z);
    }
}
