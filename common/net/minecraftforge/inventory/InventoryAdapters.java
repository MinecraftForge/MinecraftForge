package net.minecraftforge.inventory;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryLargeChest;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.ISidedInventory;

/**
 * Contains methods for converting between different types of inventories.
 */
public class InventoryAdapters {

    /**
     * Creates an ILinearInventory view of a tile entity side. Returns null if
     * the adapter could not be created. The side must not be null or UNKNOWN.
     */
    public static IForgeLinearInventory asLinearInventory(TileEntity te, ForgeDirection side) throws NullPointerException, IllegalArgumentException
    {
        if (side == null) throw new NullPointerException("side");
        if (side == ForgeDirection.UNKNOWN) throw new IllegalArgumentException("side");

        if (te instanceof IForgeInventoryTile)
        {
            IForgeInventory t = ((IForgeInventoryTile) te).getSideInventory(side);
            if (t instanceof IForgeLinearInventory) return ((IForgeLinearInventory) t);
        }

        if (te instanceof IInventory)
        {
            IForgeLinearInventory rv = asLinearInventory((IInventory) te, side);
            if (rv != null) return rv;
        }

        return null;
    }

    /**
     * Creates an ILinearInventory view of an IInventory. If the IInventory is
     * also an ISidedInventory, the side must be specified. Otherwise, the side
     * may be null. Returns null if the adapter could not be created.
     */
    public static IForgeLinearInventory asLinearInventory(IInventory inv, ForgeDirection side)
    {
        if (inv instanceof TileEntityChest)
        {
            TileEntityChest chest = (TileEntityChest) inv;
            return asLinearInventory(Block.chest.func_94442_h_(chest.worldObj, chest.xCoord, chest.yCoord, chest.zCoord), side);
        }

        if (inv instanceof net.minecraft.inventory.ISidedInventory)
        {
            if (side == null || side == ForgeDirection.UNKNOWN) return null;
            
            return new VanillaSidedToLinearAdapter((net.minecraft.inventory.ISidedInventory) inv, side);
        }

        int start = 0, end = inv.getSizeInventory();

        if (inv instanceof ISidedInventory)
        {
            if (side == null || side == ForgeDirection.UNKNOWN) return null;

            start = ((ISidedInventory) inv).getStartInventorySide(side);
            end = start + ((ISidedInventory) inv).getSizeInventorySide(side);
        }

        if (inv instanceof TileEntityFurnace && side != ForgeDirection.UP && side != ForgeDirection.DOWN)
            return new FurnaceOutputToLinearAdapter(inv, start);
        else
            return new InventoryToLinearAdapter(inv, start, end);
    }

    /**
     * Creates an ICustomInventory view of a (possibly sided) IInventory. If the
     * inventory is sided and the side is null or UNKNOWN, the adapter cannot be
     * created. Returns null if the adapter could not be created.
     */
    public static IForgeCustomInventory asCustomInventory(IInventory inv, ForgeDirection side) throws NullPointerException, IllegalArgumentException
    {
        IForgeLinearInventory li = asLinearInventory(inv, side);
        if (li != null) return new LinearToCustomAdapter(li);
        return null;
    }

    /**
     * Creates an ICustomInventory view of a tile entity side. Returns null if
     * the adapter could not be created. The side must not be null or UNKNOWN.
     */
    public static IForgeCustomInventory asCustomInventory(TileEntity te, ForgeDirection side) throws NullPointerException, IllegalArgumentException
    {
        if (side == null) throw new NullPointerException("side");
        if (side == ForgeDirection.UNKNOWN) throw new IllegalArgumentException("side");

        if (te instanceof IForgeInventoryTile)
        {
            IForgeInventory t = ((IForgeInventoryTile) te).getSideInventory(side);
            if (t instanceof IForgeCustomInventory) return ((IForgeCustomInventory) t);
            if (t instanceof IForgeLinearInventory) return new LinearToCustomAdapter((IForgeLinearInventory) t);
        }
        if (te instanceof IInventory)
        {
            IForgeLinearInventory li = asLinearInventory(te, side);
            if (li != null) return new LinearToCustomAdapter(li);
        }
        return null;
    }

    private static class InventorySlotAdapter implements ILinearInventorySlot {

        private final IInventory inv;
        private final int index;

        public InventorySlotAdapter(IInventory inv, int index)
        {
            this.inv = inv;
            this.index = index;
        }

        @Override
        public ItemStack getStack()
        {
            return ItemStack.copyItemStack(inv.getStackInSlot(index));
        }

        @Override
        public boolean setStack(ItemStack is, boolean simulate)
        {
            if (is != null && is.stackSize > inv.getInventoryStackLimit()) return false;

            if (!inv.isStackValidForSlot(index, is)) return false;

            if (!simulate) inv.setInventorySlotContents(index, is);

            return true;
        }

        @Override
        public int getMaximumStackSize()
        {
            return inv.getInventoryStackLimit();
        }

        @Override
        public boolean canExtractItems()
        {
            return inv.getStackInSlot(index) != null;
        }

        @Override
        public boolean canInsertItem(ItemStack is)
        {
            return true;
        }
    }

    private static class FurnaceOutputSlotAdapter extends InventorySlotAdapter {
        public FurnaceOutputSlotAdapter(IInventory inv, int slot)
        {
            super(inv, slot);
        }

        public boolean setStack(ItemStack is, boolean simulate)
        {
            if (is == null) return super.setStack(is, simulate);

            ItemStack old = getStack();

            // can't put in more items than were in there already
            if (old == null) return false;
            if (is.stackSize > old.stackSize) return false;

            // can't change the item either
            if (old.itemID != is.itemID || old.getItemDamage() != is.getItemDamage() || !ItemStack.areItemStackTagsEqual(old, is)) return false;

            return super.setStack(is, simulate);
        }
    }

    private static class InventoryToLinearAdapter implements IForgeLinearInventory {

        private final int start, end;
        private final IInventory inv;

        public InventoryToLinearAdapter(IInventory inv, int start, int end)
        {
            this.inv = inv;
            this.start = start;
            this.end = end;
        }

        @Override
        public int getNumInventorySlots()
        {
            return end - start;
        }

        @Override
        public ILinearInventorySlot getInventorySlot(int index) throws IndexOutOfBoundsException
        {
            if (index < 0 || index >= end - start)
                throw new IndexOutOfBoundsException("Slot index " + index + " out of range (max is " + (end - start + 1) + ")");
            return new InventorySlotAdapter(inv, start + index);
        }
    }

    private static class FurnaceOutputToLinearAdapter implements IForgeLinearInventory {
        private final int slot;
        private final IInventory inv;

        public FurnaceOutputToLinearAdapter(IInventory inv, int slot)
        {
            this.inv = inv;
            this.slot = slot;
        }

        @Override
        public int getNumInventorySlots()
        {
            return 1;
        }

        @Override
        public ILinearInventorySlot getInventorySlot(int index) throws IndexOutOfBoundsException
        {
            if (index != 0) throw new IndexOutOfBoundsException("Slot index " + index + " out of range (max is 0)");
            return new FurnaceOutputSlotAdapter(inv, slot);
        }
    }

    private static class LinearToCustomAdapter implements IForgeCustomInventory {
        private final IForgeLinearInventory inv;

        public LinearToCustomAdapter(IForgeLinearInventory inv)
        {
            this.inv = inv;
        }

        @Override
        public int insertInventoryItems(ItemStack item, int amount, boolean simulate) throws NullPointerException, IllegalArgumentException
        {
            if (item == null) throw new NullPointerException("item");
            if (amount <= 0) throw new IllegalArgumentException("amount <= 0");

            int size = inv.getNumInventorySlots();

            for (int k = 0; k < size; k++)
            {
                ILinearInventorySlot slot = inv.getInventorySlot(k);
                ItemStack oldStack = slot.getStack();

                if (oldStack != null && oldStack.itemID == item.itemID && oldStack.getItemDamage() == item.getItemDamage()
                        && ItemStack.areItemStackTagsEqual(oldStack, item))
                {
                    int adding = Math.min(amount, slot.getMaximumStackSize() - oldStack.stackSize);
                    if (adding > 0)
                    {
                        ItemStack newStack = oldStack.copy();
                        newStack.stackSize += adding;
                        if (slot.setStack(newStack, simulate))
                        {
                            amount -= adding;
                            if (amount == 0) return 0;
                        }
                    }
                }
            }

            for (int k = 0; k < size; k++)
            {
                ILinearInventorySlot slot = inv.getInventorySlot(k);
                ItemStack oldStack = slot.getStack();

                if (oldStack == null)
                {
                    ItemStack newStack = item.copy();
                    newStack.stackSize = Math.min(amount, slot.getMaximumStackSize());
                    if (slot.setStack(newStack, simulate))
                    {
                        amount -= newStack.stackSize;
                        if (amount == 0) return 0;
                    }
                }
            }

            return amount;
        }

        @Override
        public ItemStack extractInventoryItems(IStackFilter filter, int amount, boolean simulate) throws NullPointerException, IllegalArgumentException
        {
            if (filter == null) throw new NullPointerException("filter");
            if (amount <= 0) throw new IllegalArgumentException("amount <= 0");

            int size = inv.getNumInventorySlots();

            ItemStack rv = null;

            for (int k = 0; k < size; k++)
            {
                ILinearInventorySlot slot = inv.getInventorySlot(k);
                ItemStack oldStack = slot.getStack();

                if (oldStack == null) continue;

                if (rv != null
                        && (rv.itemID != oldStack.itemID || rv.getItemDamage() != oldStack.getItemDamage() || !ItemStack.areItemStackTagsEqual(rv, oldStack)))
                    continue;

                if (oldStack != null && filter.matchesItem(oldStack))
                {
                    int removing = Math.min(amount, oldStack.stackSize);

                    ItemStack newStack;
                    if (removing == oldStack.stackSize)
                    {
                        newStack = null;
                    }
                    else
                    {
                        newStack = oldStack.copy();
                        newStack.stackSize -= removing;
                    }

                    if (slot.setStack(newStack, simulate))
                    {
                        amount -= removing;

                        if (rv == null)
                        {
                            rv = oldStack.copy();
                            rv.stackSize = removing;
                        }
                        else
                            rv.stackSize += removing;

                        if (amount == 0) return rv;
                    }
                }
            }

            return rv;
        }
    }

    private static class VanillaSidedSlotAdapter implements ILinearInventorySlot {
        private final net.minecraft.inventory.ISidedInventory inv;
        private final int slotIndex;
        private final int side;

        public VanillaSidedSlotAdapter(net.minecraft.inventory.ISidedInventory inv, int slot, int side)
        {
            this.inv = inv;
            this.slotIndex = slot;
            this.side = side;
        }

        @Override
        public int getMaximumStackSize()
        {
            return inv.getInventoryStackLimit();
        }

        @Override
        public ItemStack getStack()
        {
            return ItemStack.copyItemStack(inv.getStackInSlot(slotIndex));
        }

        @Override
        public boolean setStack(ItemStack is, boolean simulate)
        {
            ItemStack old = inv.getStackInSlot(slotIndex);
            int newCount = (is == null ? 0 : is.stackSize);
            int oldCount = (old == null ? 0 : old.stackSize);

            if (old.itemID != is.itemID || old.getItemDamage() != is.getItemDamage() || !ItemStack.areItemStackTagsEqual(old, is))
            {
                // If changing the item type, check that we can extract all the
                // old
                // items and insert all the new ones

                if (old != null && !inv.func_102008_b(slotIndex, old, side)) return false;
                if (is != null && !inv.isStackValidForSlot(slotIndex, is)) return false;
                if (is != null && !inv.func_102007_a(slotIndex, is, side)) return false;
            }
            else if (newCount > oldCount && is != null)
            {
                // If we're only inserting items, check we can do that.
                ItemStack newItems = is.copy();
                newItems.stackSize = newCount - oldCount;

                if (!inv.isStackValidForSlot(slotIndex, newItems)) return false;
                if (!inv.func_102007_a(slotIndex, newItems, side)) return false;
            }
            else if (oldCount > newCount && old != null)
            {
                // If we're only extracting items, check we can do that.
                ItemStack removedItems = old.copy();
                removedItems.stackSize = oldCount - newCount;

                if (!inv.func_102008_b(slotIndex, removedItems, side)) return false;
            }

            if (!simulate) inv.setInventorySlotContents(slotIndex, is);

            return true;

        }

        @Override
        public boolean canExtractItems()
        {
            ItemStack stack = inv.getStackInSlot(slotIndex);
            return stack != null && inv.func_102008_b(slotIndex, stack, side);
        }

        @Override
        public boolean canInsertItem(ItemStack is)
        {
            return is != null && inv.isStackValidForSlot(slotIndex, is) && inv.func_102007_a(slotIndex, is, side);
        }
    }

    private static class VanillaSidedToLinearAdapter implements IForgeLinearInventory {
        private final int side;
        private final net.minecraft.inventory.ISidedInventory inv;
        private final int[] accessibleSlotIndices;

        public VanillaSidedToLinearAdapter(net.minecraft.inventory.ISidedInventory inv, ForgeDirection side)
        {
            this.inv = inv;
            this.side = side.ordinal();
            this.accessibleSlotIndices = inv.getSizeInventorySide(this.side);
        }

        @Override
        public ILinearInventorySlot getInventorySlot(int index) throws IndexOutOfBoundsException
        {
            if (index < 0 || index >= accessibleSlotIndices.length)
                throw new IndexOutOfBoundsException("Slot index " + index + " out of range (max is " + (accessibleSlotIndices.length - 1) + ")");

            return new VanillaSidedSlotAdapter(inv, accessibleSlotIndices[index], side);
        }

        @Override
        public int getNumInventorySlots()
        {
            return accessibleSlotIndices.length;
        }
    }
}
