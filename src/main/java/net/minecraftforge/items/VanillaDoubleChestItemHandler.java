/*
 * Minecraft Forge
 * Copyright (c) 2016.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.items;

import com.google.common.base.Objects;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.filter.IStackFilter;
import net.minecraftforge.items.templates.EmptyHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.ref.WeakReference;
import java.util.OptionalInt;

public class VanillaDoubleChestItemHandler extends WeakReference<TileEntityChest> implements IItemHandler
{
    // Dummy cache value to signify that we have checked and definitely found no adjacent chests
    public static final VanillaDoubleChestItemHandler NO_ADJACENT_CHESTS_INSTANCE = new VanillaDoubleChestItemHandler(null, null, false);
    private final boolean mainChestIsUpper;
    private final TileEntityChest mainChest;
    private final CombinedInvWrapper wrapper;
    private final int hashCode;

    public VanillaDoubleChestItemHandler(@Nullable TileEntityChest mainChest, @Nullable TileEntityChest other, boolean mainChestIsUpper)
    {
        super(other);
        this.mainChest = mainChest;
        this.mainChestIsUpper = mainChestIsUpper;
        if (mainChest == null || other == null)
            wrapper = new EmptyCombinedInvWrapper();
        else
            wrapper = new CombinedInvWrapper(getChest(true).getSingleChestHandler(), getChest(false).getSingleChestHandler());
        hashCode = Objects.hashCode(mainChestIsUpper ? mainChest : other) * 31 + Objects.hashCode(!mainChestIsUpper ? mainChest : other);
    }

    @Nullable
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

    @Nullable
    public TileEntityChest getChest(boolean accessingUpper)
    {
        if (accessingUpper == mainChestIsUpper)
            return mainChest;
        else
        {
            return getOtherChest();
        }
    }

    @Nullable
    private TileEntityChest getOtherChest()
    {
        TileEntityChest tileEntityChest = get();
        return tileEntityChest != null && !tileEntityChest.isInvalid() ? tileEntityChest : null;
    }

    @Override
    public int size()
    {
        return 27 * 2;
    }

    @Override
    public void clearInv()
    {
        TileEntityChest upperChest = getChest(true);

        if (upperChest != null)
            upperChest.getSingleChestHandler().clearInv();

        TileEntityChest LowerChest = getChest(false);

        if (LowerChest != null)
            LowerChest.getSingleChestHandler().clearInv();
    }

    @Override
    @Nonnull
    public ItemStack getStackInSlot(int slot)
    {
        boolean accessingUpperChest = slot < 27;
        int targetSlot = accessingUpperChest ? slot : slot - 27;
        TileEntityChest chest = getChest(accessingUpperChest);
        return chest != null ? chest.getStackInSlot(targetSlot) : ItemStack.EMPTY;
    }

    @Nonnull
    @Override
    public ItemStack insert(OptionalInt slot, @Nonnull ItemStack stack, boolean simulate)
    {
        if (wrapper.isValid())
            return wrapper.insert(slot, stack, simulate);
        return ItemStack.EMPTY;
    }

    @Nonnull
    @Override
    public ItemStack extract(OptionalInt slot, IStackFilter filter, int amount, boolean simulate)
    {
        if (wrapper.isValid())
            return wrapper.extract(slot, filter, amount, simulate);
        return ItemStack.EMPTY;
    }

    @Override
    public int getSlotLimit(int slot)
    {
        boolean accessingUpperChest = slot < 27;
        return getChest(accessingUpperChest).getInventoryStackLimit();
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

    private static class EmptyCombinedInvWrapper extends CombinedInvWrapper
    {
        public EmptyCombinedInvWrapper()
        {
            super(EmptyHandler.INSTANCE);
        }
    }

}
