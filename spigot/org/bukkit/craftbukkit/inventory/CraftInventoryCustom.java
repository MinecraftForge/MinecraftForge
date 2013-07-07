package org.bukkit.craftbukkit.inventory;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryHolder;


public class CraftInventoryCustom extends CraftInventory {
    public CraftInventoryCustom(InventoryHolder owner, InventoryType type) {
        super(new MinecraftInventory(owner, type));
    }

    public CraftInventoryCustom(InventoryHolder owner, int size) {
        super(new MinecraftInventory(owner, size));
    }

    public CraftInventoryCustom(InventoryHolder owner, int size, String title) {
        super(new MinecraftInventory(owner, size, title));
    }

    static class MinecraftInventory implements net.minecraft.inventory.IInventory {
        private final net.minecraft.item.ItemStack[] items;
        private int maxStack = MAX_STACK;
        private final List<HumanEntity> viewers;
        private final String title;
        private InventoryType type;
        private final InventoryHolder owner;

        public MinecraftInventory(InventoryHolder owner, InventoryType type) {
            this(owner, type.getDefaultSize(), type.getDefaultTitle());
            this.type = type;
        }

        public MinecraftInventory(InventoryHolder owner, int size) {
            this(owner, size, "Chest");
        }

        public MinecraftInventory(InventoryHolder owner, int size, String title) {
            this.items = new net.minecraft.item.ItemStack[size];
            this.title = title;
            this.viewers = new ArrayList<HumanEntity>();
            this.owner = owner;
            this.type = InventoryType.CHEST;
        }

        public int getSizeInventory() {
            return items.length;
        }

        public net.minecraft.item.ItemStack getStackInSlot(int i) {
            return items[i];
        }

        public net.minecraft.item.ItemStack decrStackSize(int i, int j) {
            net.minecraft.item.ItemStack stack = this.getStackInSlot(i);
            net.minecraft.item.ItemStack result;
            if (stack == null) return null;
            if (stack.stackSize <= j) {
                this.setInventorySlotContents(i, null);
                result = stack;
            } else {
                result = CraftItemStack.copyNMSStack(stack, j);
                stack.stackSize -= j;
            }
            this.onInventoryChanged();
            return result;
        }

        public net.minecraft.item.ItemStack getStackInSlotOnClosing(int i) {
            net.minecraft.item.ItemStack stack = this.getStackInSlot(i);
            net.minecraft.item.ItemStack result;
            if (stack == null) return null;
            if (stack.stackSize <= 1) {
                this.setInventorySlotContents(i, null);
                result = stack;
            } else {
                result = CraftItemStack.copyNMSStack(stack, 1);
                stack.stackSize -= 1;
            }
            return result;
        }

        public void setInventorySlotContents(int i, net.minecraft.item.ItemStack itemstack) {
            items[i] = itemstack;
            if (itemstack != null && this.getInventoryStackLimit() > 0 && itemstack.stackSize > this.getInventoryStackLimit()) {
                itemstack.stackSize = this.getInventoryStackLimit();
            }
        }

        public String getInvName() {
            return title;
        }

        public int getInventoryStackLimit() {
            return maxStack;
        }

        public void setMaxStackSize(int size) {
            maxStack = size;
        }

        public void onInventoryChanged() {}

        public boolean isUseableByPlayer(net.minecraft.entity.player.EntityPlayer entityhuman) {
            return true;
        }

        public net.minecraft.item.ItemStack[] getContents() {
            return items;
        }

        public void onOpen(CraftHumanEntity who) {
            viewers.add(who);
        }

        public void onClose(CraftHumanEntity who) {
            viewers.remove(who);
        }

        public List<HumanEntity> getViewers() {
            return viewers;
        }

        public InventoryType getType() {
            return type;
        }

        public void closeChest() {}

        public InventoryHolder getOwner() {
            return owner;
        }

        public void openChest() {}

        public boolean isInvNameLocalized() {
            return false;
        }

        public boolean isStackValidForSlot(int i, net.minecraft.item.ItemStack itemstack) {
            return true;
        }
    }
}
