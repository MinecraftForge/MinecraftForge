package net.minecraftforge.common;

import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class ItemInventory implements IInventory {

    public static final String INVENTORY_SLOT_TAG = "slots";
    public static final String INVENTORY_SIZE_TAG = "size";

    ItemStack stack;
    int stackLimit;
    String inventoryName = "";
    boolean isInvNameLocalized = false;
    int inventoryStackLimit = 0;
    boolean useableByPlayer = false;
    
    /*
     *Example Inventory 
     */
    public ItemInventory (ItemStack stack, int stackLimit, String inventoryName, boolean isInvNameLocalized, int inventoryStackLimit, boolean useableByPlayer) {

        this.stack = stack;
        this.stackLimit = stackLimit;
        this.inventoryName = inventoryName;
        this.isInvNameLocalized = isInvNameLocalized;
        this.inventoryStackLimit = inventoryStackLimit;
        this.useableByPlayer = useableByPlayer;
    }

    public ItemStack getInventoryContainerItem() {

        return stack;
    }

    @Override
    public int getSizeInventory() {

        return this.getTAGfromItemstack(stack).getInteger(INVENTORY_SIZE_TAG);
    }

    @Override
    public ItemStack getStackInSlot(int slot) {

        return this.getNBTTagInventory(stack, INVENTORY_SLOT_TAG).get(slot);
    }

    @Override
    public ItemStack decrStackSize(int slot, int amount) {

        ItemStack stack = this.getStackInSlot(slot);

        if (stack != null) {

            int toLeave = stack.stackSize - amount;

            ItemStack temp = stack.copy();
            temp.stackSize = amount;

            ItemStack stackLeft = stack.copy();
            stackLeft.stackSize = toLeave;

            this.setInventorySlotContents(slot, stackLeft);

            if(stackLeft.stackSize <= 0)
                this.setInventorySlotContents(slot, null);

            return temp;
        }
        return null;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int slot) {

        return this.getStackInSlot(slot);
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack itemStack) {

        ArrayList<ItemStack> list = this.getNBTTagInventory(itemStack, INVENTORY_SLOT_TAG);
        list.set(slot, itemStack);
        this.setNBTTagInventory(itemStack, INVENTORY_SLOT_TAG, list);
    }

    @Override
    public String getInvName() {

        return inventoryName;
    }

    @Override
    public boolean isInvNameLocalized() {

        return isInvNameLocalized;
    }

    @Override
    public int getInventoryStackLimit() {

        return inventoryStackLimit;
    }

    @Override
    public void onInventoryChanged() {

    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer entityplayer) {

        return useableByPlayer;
    }

    @Override
    public void openChest() {
        // TODO Auto-generated method stub

    }

    @Override
    public void closeChest() {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean isItemValidForSlot(int i, ItemStack itemstack) {

        return true;
    }

    private NBTTagCompound getTAGfromItemstack(ItemStack itemStack) {

        if (itemStack != null) {

            NBTTagCompound tag = itemStack.getTagCompound();

            if (tag == null) {

                tag = new NBTTagCompound();
                itemStack.setTagCompound(tag);
            }
            return tag;
        }
        return null;
    }

    public void setNBTTagInventory(ItemStack itemStack, String tag, ArrayList<ItemStack> inventory) {

        NBTTagCompound compound = getTAGfromItemstack(itemStack);
        NBTTagList nbttaglist = new NBTTagList();

        for (int i = 0; i < inventory.size(); i++) {

            if (inventory.get(i) != null) {

                NBTTagCompound nbttagcompound = new NBTTagCompound();
                inventory.get(i).writeToNBT(nbttagcompound);
                nbttaglist.appendTag(nbttagcompound);
            }
        }
        compound.setTag(tag, nbttaglist);
    }

    public ArrayList<ItemStack> getNBTTagInventory(ItemStack itemStack, String tag) {

        ArrayList<ItemStack> itemList = new ArrayList<ItemStack>();

        NBTTagCompound compound = getTAGfromItemstack(itemStack);

        if (compound != null) {

            NBTTagList nbttaglist = compound.getTagList(tag);

            for(int pos = 0; pos < nbttaglist.tagCount(); pos++) {

                NBTTagCompound nbttagcompound = (NBTTagCompound) nbttaglist.tagAt(pos);

                itemList.add(ItemStack.loadItemStackFromNBT(nbttagcompound));
            }
        }
        return itemList;
    }
}
