package net.minecraftforge.items;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import java.lang.ref.WeakReference;

import com.google.common.base.Objects;

public class VanillaDoubleChestItemHandler extends WeakReference<TileEntityChest> implements IItemHandler
{
    // Dummy cache value to signify that we have checked and definitely found no adjacent chests
    public static final VanillaDoubleChestItemHandler NO_ADJACENT_CHESTS_INSTANCE = new VanillaDoubleChestItemHandler(null, null, false);
    private final boolean mainChestIsUpper;
    private final TileEntityChest mainChest;
    private final int hashCode;

    public VanillaDoubleChestItemHandler(TileEntityChest mainChest, TileEntityChest other, boolean mainChestIsUpper)
    {
        super(other);
        this.mainChest = mainChest;
        this.mainChestIsUpper = mainChestIsUpper;
        hashCode = Objects.hashCode(mainChestIsUpper ? mainChest : other) * 31 + Objects.hashCode(!mainChestIsUpper ? mainChest : other);
    }

    public static VanillaDoubleChestItemHandler get(TileEntityChest chest)
    {
        World world = chest.getWorld();
        BlockPos pos = chest.getPos();
        if (world == null || pos == null || !world.isBlockLoaded(pos))
            return null; // Still loading

        Block blockType = chest.getBlockType();

        EnumFacing[] horizontals = EnumFacing.HORIZONTALS;
        for (int i = horizontals.length - 1; i >= 0; i--)   // Use reverse order so we can return early
        {
            EnumFacing enumfacing = horizontals[i];
            BlockPos blockpos = pos.offset(enumfacing);
            Block block = world.getBlockState(blockpos).getBlock();

            if (block == blockType)
            {
                TileEntity otherTE = world.getTileEntity(blockpos);

                if (otherTE instanceof TileEntityChest)
                {
                    TileEntityChest otherChest = (TileEntityChest) otherTE;
                    return new VanillaDoubleChestItemHandler(chest, otherChest,
                            enumfacing != net.minecraft.util.EnumFacing.WEST && enumfacing != net.minecraft.util.EnumFacing.NORTH);

                }
            }
        }
        return NO_ADJACENT_CHESTS_INSTANCE; //All alone
    }

    public TileEntityChest getChest(boolean accessingUpper)
    {
        if (accessingUpper == mainChestIsUpper)
            return mainChest;
        else
        {
            return getOtherChest();
        }
    }

    private TileEntityChest getOtherChest()
    {
        TileEntityChest tileEntityChest = get();
        return tileEntityChest != null && !tileEntityChest.isInvalid() ? tileEntityChest : null;
    }

    @Override
    public int getSlots()
    {
        return 27 * 2;
    }

    @Override
    public ItemStack getStackInSlot(int slot)
    {
        boolean accessingUpperChest = slot < 27;
        int targetSlot = accessingUpperChest ? slot : slot - 27;
        TileEntityChest chest = getChest(accessingUpperChest);
        return chest != null ? chest.getStackInSlot(targetSlot) : null;
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate)
    {
        boolean accessingUpperChest = slot < 27;
        int targetSlot = accessingUpperChest ? slot : slot - 27;
        TileEntityChest chest = getChest(accessingUpperChest);
        return chest != null ? chest.getSingleChestHandler().insertItem(targetSlot, stack, simulate) : stack;
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate)
    {
        boolean accessingUpperChest = slot < 27;
        int targetSlot = accessingUpperChest ? slot : slot - 27;
        TileEntityChest chest = getChest(accessingUpperChest);
        return chest != null ? chest.getSingleChestHandler().extractItem(targetSlot, amount, simulate) : null;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        VanillaDoubleChestItemHandler that = (VanillaDoubleChestItemHandler) o;

        if (hashCode != that.hashCode)
            return false;

        final TileEntityChest otherChest = getOtherChest();
        if (mainChestIsUpper == that.mainChestIsUpper)
            return Objects.equal(mainChest, that.mainChest) && Objects.equal(otherChest, that.getOtherChest());
        else
            return Objects.equal(mainChest, that.getOtherChest()) && Objects.equal(otherChest, that.mainChest);
    }

    @Override
    public int hashCode()
    {
        return hashCode;
    }

    public boolean needsRefresh()
    {
        if (this == NO_ADJACENT_CHESTS_INSTANCE)
            return false;
        TileEntityChest tileEntityChest = get();
        return tileEntityChest == null || tileEntityChest.isInvalid();
    }
}
