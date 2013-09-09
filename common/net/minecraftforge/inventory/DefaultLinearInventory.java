package net.minecraftforge.inventory;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class DefaultLinearInventory implements IForgeLinearInventory {

    private DefaultLinearInventorySlot[] slots;

    public DefaultLinearInventory(int size)
    {
        slots = new DefaultLinearInventorySlot[size];
        for (int k = 0; k < size; k++)
            slots[k] = new DefaultLinearInventorySlot();
    }

    @Override
    public int getNumInventorySlots()
    {
        return slots.length;
    }

    @Override
    public ILinearInventorySlot getInventorySlot(int index) throws IndexOutOfBoundsException
    {
        return slots[index];
    }

    public void readFromNBT(NBTTagCompound tag)
    {
        NBTTagList itemsTag = tag.getTagList("Items");
        for (int k = 0; k < itemsTag.tagCount(); k++)
        {
            NBTTagCompound itemTag = (NBTTagCompound) itemsTag.tagAt(k);
            slots[itemTag.getInteger("Slot")].readFromNBT(itemTag);
        }
    }

    public void writeToNBT(NBTTagCompound tag)
    {
        NBTTagList itemsTag = new NBTTagList();

        for (int k = 0; k < slots.length; k++)
        {
            if (slots[k].getStack() != null)
            {
                NBTTagCompound itemTag = new NBTTagCompound();

                slots[k].writeToNBT(itemTag);
                itemTag.setInteger("Slot", k);

                itemsTag.appendTag(itemTag);
            }
        }

        tag.setTag("Items", itemsTag);
    }

    public void setMaximumStackSize(int size) throws IllegalArgumentException
    {
        for (DefaultLinearInventorySlot slot : slots)
            slot.setMaximumStackSize(size);
    }

}
