package net.minecraftforge.items.wrapper;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

/**
 * Exposes the player inventory WITHOUT the armor inventory as IItemHandler.
 * Also takes core of inserting/extracting having the same logic as picking up items.
 */
public class PlayerMainInvWrapper extends RangedWrapper
{
    private final InventoryPlayer inventoryPlayer;

    public PlayerMainInvWrapper(InventoryPlayer inv)
    {
        super(new InvWrapper(inv), 0, inv.mainInventory.length);
        inventoryPlayer = inv;
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate)
    {
        ItemStack rest = super.insertItem(slot, stack, simulate);
        if (rest == null || rest.stackSize != stack.stackSize)
        {
            // the stack in the slot changed, animate it
            ItemStack inSlot = getStackInSlot(slot);
            if(inSlot != null)
            {
                if (getInventoryPlayer().player.worldObj.isRemote)
                {
                    inSlot.animationsToGo = 5;
                }
                else if(getInventoryPlayer().player instanceof EntityPlayerMP) {
                    getInventoryPlayer().player.openContainer.detectAndSendChanges();
                }
            }
        }
        return rest;
    }

    public InventoryPlayer getInventoryPlayer()
    {
        return inventoryPlayer;
    }
}
