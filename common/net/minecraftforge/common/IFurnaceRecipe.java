package net.minecraftforge.common;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;

public interface IFurnaceRecipe {

    boolean matches(ItemStack smeltedItem);
    
    ItemStack getResult(ItemStack smeltedItem);
    
    float getExperience(ItemStack smeltedItem, EntityPlayer player);
    
    int getSmeltTime(ItemStack smeltedItem);
    
}
