package net.minecraftforge.event.crafting;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ContainerPlayer;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;

public class PlayerFindMatchingRecipeEvent extends FindMatchingRecipeEvent.Player {
    public final ContainerPlayer container;// The container that the crafting is taking place in

    public PlayerFindMatchingRecipeEvent(ContainerPlayer container, EntityPlayer player, ItemStack output)
    {
        super(player.worldObj, player, getInput(container.craftMatrix), output, FindMatchingRecipeEvent.EType.CRAFT);
        this.container = container;
    }

    public static ItemStack[] getInput(InventoryCrafting craftMatrix)
    {
        ItemStack[] in = new ItemStack[craftMatrix.getSizeInventory()];
        for (int i = 0; i < in.length; i++)
            in[i] = craftMatrix.getStackInSlot(i);
        return in;
    }
}
