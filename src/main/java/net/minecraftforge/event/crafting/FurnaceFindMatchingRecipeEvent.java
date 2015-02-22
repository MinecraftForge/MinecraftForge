package net.minecraftforge.event.crafting;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;

public class FurnaceFindMatchingRecipeEvent extends FindMatchingRecipeEvent.Block {
    public final TileEntityFurnace furnace;

    public FurnaceFindMatchingRecipeEvent(TileEntityFurnace furnace, ItemStack input, ItemStack output)
    {
        super(furnace.getWorld(), furnace.getPos(), new ItemStack[] { input }, output, EType.SMELT);
        this.furnace = furnace;
    }
}
