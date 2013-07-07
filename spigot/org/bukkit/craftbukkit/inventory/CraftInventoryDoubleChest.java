package org.bukkit.craftbukkit.inventory;

import org.bukkit.block.DoubleChest;
import org.bukkit.inventory.DoubleChestInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;


public class CraftInventoryDoubleChest extends CraftInventory implements DoubleChestInventory {
    private final CraftInventory left;
    private final CraftInventory right;

    public CraftInventoryDoubleChest(CraftInventory left, CraftInventory right) {
        super(new net.minecraft.inventory.InventoryLargeChest("Large chest", left.getInventory(), right.getInventory()));
        this.left = left;
        this.right = right;
    }

    public CraftInventoryDoubleChest(net.minecraft.inventory.InventoryLargeChest largeChest) {
        super(largeChest);
        if (largeChest.upperChest instanceof net.minecraft.inventory.InventoryLargeChest) {
            left = new CraftInventoryDoubleChest((net.minecraft.inventory.InventoryLargeChest) largeChest.upperChest);
        } else {
            left = new CraftInventory(largeChest.upperChest);
        }
        if (largeChest.lowerChest instanceof net.minecraft.inventory.InventoryLargeChest) {
            right = new CraftInventoryDoubleChest((net.minecraft.inventory.InventoryLargeChest) largeChest.lowerChest);
        } else {
            right = new CraftInventory(largeChest.lowerChest);
        }
    }

    public Inventory getLeftSide() {
        return left;
    }

    public Inventory getRightSide() {
        return right;
    }

    @Override
    public void setContents(ItemStack[] items) {
        if (getInventory().getContents().length < items.length) {
            throw new IllegalArgumentException("Invalid inventory size; expected " + getInventory().getContents().length + " or less");
        }
        ItemStack[] leftItems = new ItemStack[left.getSize()], rightItems = new ItemStack[right.getSize()];
        System.arraycopy(items, 0, leftItems, 0, Math.min(left.getSize(),items.length));
        left.setContents(leftItems);
        if (items.length >= left.getSize()) {
            System.arraycopy(items, left.getSize(), rightItems, 0, Math.min(right.getSize(), items.length - left.getSize()));
            right.setContents(rightItems);
        }
    }

    @Override
    public DoubleChest getHolder() {
        return new DoubleChest(this);
    }
}
