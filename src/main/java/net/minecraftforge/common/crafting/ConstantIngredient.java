package net.minecraftforge.common.crafting;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ConstantIngredient extends Ingredient
{
    public final Ingredient parent;
    public final String constant;
    
    public ConstantIngredient(Ingredient parent, String constant)
    {
        super(parent.getMatchingStacks());
        this.parent = parent;
        this.constant = constant;
    }
    
    @Override
    public boolean apply(ItemStack stack)
    {
        return this.parent.apply(stack);
    }

    @SideOnly(Side.CLIENT)
    public IntList getValidItemStacksPacked()
    {
        return this.parent.getValidItemStacksPacked();
    }
    
    @Override
    public JsonElement toJson()
    {
        JsonObject json = new JsonObject();

        json.addProperty("type", "minecraft:item");
        json.addProperty("item", "#" + this.constant);
        
        return json;
    }
}
