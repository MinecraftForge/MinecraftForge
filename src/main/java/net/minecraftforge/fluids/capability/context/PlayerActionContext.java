package net.minecraftforge.fluids.capability.context;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

public class PlayerActionContext extends ItemHandlerActionContext {
    private final PlayerInventory playerInventory;

    public PlayerActionContext(PlayerInventory playerInventory, int slot) {
        super(new InvWrapper(playerInventory), slot);
        this.playerInventory = playerInventory;
    }

    // Override try insert to use placeItemBackInInventory() for players.
    @Override
    public boolean tryInsertStack(ItemStack newStack, IFluidHandler.FluidAction action) {
        if (action.execute()) {
            playerInventory.placeItemBackInInventory(playerInventory.player.level, newStack);
        }
        return true;
    }
}
