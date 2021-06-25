package net.minecraftforge.fluids.capability.context;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class CursorFluidActionContext implements IFluidActionContext {
    private final PlayerInventory inventory;

    public CursorFluidActionContext(PlayerEntity player) {
        this(player.inventory);
    }

    public CursorFluidActionContext(PlayerInventory inventory) {
        this.inventory = inventory;
    }

    @Override
    public ItemStack getStack() {
        return inventory.getCarried();
    }

    @Override
    public boolean trySetStack(ItemStack newStack, IFluidHandler.FluidAction action) {
        if (action.execute()) {
            inventory.setCarried(newStack);
        }
        return true;
    }

    @Override
    public boolean tryInsertStack(ItemStack newStack, IFluidHandler.FluidAction action) {
        if (action.execute()) {
            inventory.placeItemBackInInventory(inventory.player.level, newStack);
        }
        return true;
    }
}
