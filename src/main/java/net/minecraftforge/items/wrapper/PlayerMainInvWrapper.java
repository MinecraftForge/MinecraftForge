package net.minecraftforge.items.wrapper;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

/**
 * Exposes the player inventory WITHOUT the armor inventory as IItemHandler.
 * Also takes core of inserting/extracting having the same logic as picking up items.
 */
public class PlayerMainInvWrapper extends InvWrapper
{

    public final InventoryPlayer inventoryPlayer;

    public PlayerMainInvWrapper(InventoryPlayer inv)
    {
        super(inv);

        inventoryPlayer = inv;
    }

    @Override
    public int getSlots()
    {
        return inventoryPlayer.mainInventory.length;
    }

    @Override
    public void setStackInSlot(int slot, ItemStack stack)
    {
        // prevent setting of armor inventory
        if (slot > getSlots())
        {
            return;
        }
        super.setStackInSlot(slot, stack);
    }

    @Override
    public ItemStack getStackInSlot(int slot)
    {
        // prevent getting of armor inventory
        if (slot > getSlots())
        {
            return null;
        }
        return super.getStackInSlot(slot);
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate)
    {
        // prevent inserting into armor inventory
        if (slot > getSlots())
        {
            return stack;
        }

        ItemStack rest = super.insertItem(slot, stack, simulate);
        if (rest == null || rest.stackSize != stack.stackSize)
        {
            // the stack in the slot changed, animate it
            ItemStack inSlot = getStackInSlot(slot);
            if(inSlot != null)
            {
                if (inventoryPlayer.player.worldObj.isRemote)
                {
                    inSlot.animationsToGo = 5;
                }
                else if(inventoryPlayer.player instanceof EntityPlayerMP) {
                    inventoryPlayer.player.openContainer.detectAndSendChanges();
                }
            }
        }
        return rest;
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate)
    {
        // prevent extraction from armor inventory
        if (slot > getSlots())
        {
            return null;
        }
        return super.extractItem(slot, amount, simulate);
    }
}
