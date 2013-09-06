
package net.minecraftforge.fluids;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

/**
 * This is a cellular-automata based finite fluid block implementation.
 * 
 * It is highly recommended that you use/extend this class for finite fluid blocks.
 * 
 * @author OvermindDL1, KingLemming
 * 
 */
public class BlockFluidFinite extends BlockFluidBase
{
    public BlockFluidFinite(int id, Fluid fluid, Material material)
    {
        super(id, fluid, material);
    }

    @Override
    public int getQuantaValue(IBlockAccess world, int x, int y, int z)
    {
        if (world.isAirBlock(x, y, z))
        {
            return 0;
        }

        if (world.getBlockId(x, y, z) != blockID)
        {
            return -1;
        }

        int quantaRemaining = world.getBlockMetadata(x, y, z) + 1;
        return quantaRemaining;
    }

    @Override
    public boolean canCollideCheck(int meta, boolean fullHit)
    {
        return fullHit && meta == quantaPerBlock - 1;
    }

    @Override
    public int getMaxRenderHeightMeta()
    {
        return quantaPerBlock - 1;
    }

    @Override
    public void updateTick(World world, int x, int y, int z, Random rand)
    {
        boolean changed = false;
        int quantaRemaining = world.getBlockMetadata(x, y, z) + 1;

        // Flow vertically if possible
        int prevRemaining = quantaRemaining;
        quantaRemaining = tryToFlowVerticallyInto(world, x, y, z, quantaRemaining);

        if (quantaRemaining < 1)
        {
            return;
        }
        else if (quantaRemaining != prevRemaining)
        {
            changed = true;
            if (quantaRemaining == 1)
            {
                world.setBlockMetadataWithNotify(x, y, z, quantaRemaining - 1, 2);
                return;
            }
        }
        else if (quantaRemaining == 1)
        {
            return;
        }

        // Flow out if possible
        int lowerthan = quantaRemaining - 1;
        if (displaceIfPossible(world, x,     y, z - 1)) world.setBlock(x,     y, z - 1, 0);
        if (displaceIfPossible(world, x,     y, z + 1)) world.setBlock(x,     y, z + 1, 0);
        if (displaceIfPossible(world, x - 1, y, z    )) world.setBlock(x - 1, y, z,     0);
        if (displaceIfPossible(world, x + 1, y, z    )) world.setBlock(x + 1, y, z,     0);
        int north = getQuantaValueBelow(world, x,     y, z - 1, lowerthan);
        int south = getQuantaValueBelow(world, x,     y, z + 1, lowerthan);
        int west  = getQuantaValueBelow(world, x - 1, y, z,     lowerthan);
        int east  = getQuantaValueBelow(world, x + 1, y, z,     lowerthan);
        int total = quantaRemaining;
        int count = 1;

        if (north >= 0)
        {
            count++;
            total += north;
        }

        if (south >= 0)
        {
            count++;
            total += south;
        }

        if (west >= 0)
        {
            count++;
            total += west;
        }

        if (east >= 0)
        {
            ++count;
            total += east;
        }

        if (count == 1)
        {
            if (changed)
            {
                world.setBlockMetadataWithNotify(x, y, z, quantaRemaining - 1, 2);
            }
            return;
        }

        int each = total / count;
        int rem = total % count;
        if (north >= 0)
        {
            int newnorth = each;
            if (rem == count || rem > 1 && rand.nextInt(count - rem) != 0)
            {
                ++newnorth;
                --rem;
            }

            if (newnorth != north)
            {
                if (newnorth == 0)
                {
                    world.setBlock(x, y, z - 1, 0);
                }
                else
                {
                    world.setBlock(x, y, z - 1, blockID, newnorth - 1, 2);
                }
                world.scheduleBlockUpdate(x, y, z - 1, blockID, tickRate);
            }
            --count;
        }

        if (south >= 0)
        {
            int newsouth = each;
            if (rem == count || rem > 1 && rand.nextInt(count - rem) != 0)
            {
                ++newsouth;
                --rem;
            }

            if (newsouth != south)
            {
                if (newsouth == 0)
                {
                    world.setBlock(x, y, z + 1, 0);
                }
                else
                {
                    world.setBlock(x, y, z + 1, blockID, newsouth - 1, 2);
                }
                world.scheduleBlockUpdate(x, y, z + 1, blockID, tickRate);
            }
            --count;
        }

        if (west >= 0)
        {
            int newwest = each;
            if (rem == count || rem > 1 && rand.nextInt(count - rem) != 0)
            {
                ++newwest;
                --rem;
            }
            if (newwest != west)
            {
                if (newwest == 0)
                {
                    world.setBlock(x - 1, y, z, 0);
                }
                else
                {
                    world.setBlock(x - 1, y, z, blockID, newwest - 1, 2);
                }
                world.scheduleBlockUpdate(x - 1, y, z, blockID, tickRate);
            }
            --count;
        }

        if (east >= 0)
        {
            int neweast = each;
            if (rem == count || rem > 1 && rand.nextInt(count - rem) != 0)
            {
                ++neweast;
                --rem;
            }

            if (neweast != east)
            {
                if (neweast == 0)
                {
                    world.setBlock(x + 1, y, z, 0);
                }
                else
                {
                    world.setBlock(x + 1, y, z, blockID, neweast - 1, 2);
                }
                world.scheduleBlockUpdate(x + 1, y, z, blockID, tickRate);
            }
            --count;
        }

        if (rem > 0)
        {
            ++each;
        }
        world.setBlockMetadataWithNotify(x, y, z, each - 1, 2);
    }

    public int tryToFlowVerticallyInto(World world, int x, int y, int z, int amtToInput)
    {
        int otherY = y + densityDir;
        if (otherY < 0 || otherY >= world.getHeight())
        {
            world.setBlockToAir(x, y, z);
            return 0;
        }

        int amt = getQuantaValueBelow(world, x, otherY, z, quantaPerBlock);
        if (amt >= 0)
        {
            amt += amtToInput;
            if (amt > quantaPerBlock)
            {
                world.setBlock(x, otherY, z, blockID, quantaPerBlock - 1, 3);
                world.scheduleBlockUpdate(x, otherY, z, blockID, tickRate);
                return amt - quantaPerBlock;
            }
            else if (amt > 0)
            {
                world.setBlock(x, otherY, z, blockID, amt - 1, 3);
                world.scheduleBlockUpdate(x, otherY, z, blockID, tickRate);
                world.setBlockToAir(x, y, z);
                return 0;
            }
            return amtToInput;
        }
        else
        {
            int density_other = getDensity(world, x, otherY, z);
            if (density_other == Integer.MAX_VALUE)
            {
                if (displaceIfPossible(world, x, otherY, z))
                {
                    world.setBlock(x, otherY, z, blockID, amtToInput - 1, 3);
                    world.scheduleBlockUpdate(x, otherY, z, blockID, tickRate);
                    world.setBlockToAir(x, y, z);
                    return 0;
                }
                else
                {
                    return amtToInput;
                }
            }

            if (densityDir < 0)
            {
                if (density_other < density) // then swap
                {
                    int bId = world.getBlockId(x, otherY, z);
                    BlockFluidBase block = (BlockFluidBase) Block.blocksList[bId];
                    int otherData = world.getBlockMetadata(x, otherY, z);
                    world.setBlock(x, otherY, z, blockID, amtToInput - 1, 3);
                    world.setBlock(x, y, z, bId, otherData, 3);
                    world.scheduleBlockUpdate(x, otherY, z, blockID, tickRate);
                    world.scheduleBlockUpdate(x, y, z, bId, block.tickRate(world));
                    return 0;
                }
            }
            else
            {
                if (density_other > density)
                {
                    int bId = world.getBlockId(x, otherY, z);
                    BlockFluidBase block = (BlockFluidBase) Block.blocksList[bId];
                    int otherData = world.getBlockMetadata(x, otherY, z);
                    world.setBlock(x, otherY, z, blockID, amtToInput - 1, 3);
                    world.setBlock(x, y, z, bId, otherData, 3);
                    world.scheduleBlockUpdate(x, otherY, z, blockID, tickRate);
                    world.scheduleBlockUpdate(x, y, z, bId, block.tickRate(world));
                    return 0;
                }
            }
            return amtToInput;
        }
    }

    /* IFluidBlock */
    @Override
    public FluidStack drain(World world, int x, int y, int z, boolean doDrain)
    {
        return null;
    }

    @Override
    public boolean canDrain(World world, int x, int y, int z)
    {
        return false;
    }
}
