package net.minecraftforge.items;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraftforge.items.wrapper.InvWrapper;

public class VanillaHopperItemHandler extends InvWrapper
{
    private final TileEntityHopper hopper;

    public VanillaHopperItemHandler(TileEntityHopper hopper)
    {
        super(hopper);
        this.hopper = hopper;
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate)
    {
        if (stack == null)
            return null;
        if (simulate || !hopper.mayTransfer())
            return super.insertItem(slot, stack, simulate);

        int curStackSize = stack.stackSize;
        ItemStack itemStack = super.insertItem(slot, stack, false);
        if (itemStack == null || curStackSize != itemStack.stackSize)
        {
            hopper.setTransferCooldown(8);
        }
        return itemStack;
    }
}
