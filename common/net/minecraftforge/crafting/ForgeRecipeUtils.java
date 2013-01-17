package net.minecraftforge.crafting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.item.ItemStack;

public class ForgeRecipeUtils {

    public static boolean itemMatches(ItemStack target, ItemStack input)
    {
        return itemMatches(target, input, false);
    }

    public static boolean itemMatches(ItemStack target, ItemStack input, boolean strict)
    {
        if (input == null && target != null || input != null && target == null)
        {
            return false;
        }
        return target.itemID == input.itemID && (target.getItemDamage() == -1 && !strict || target.getItemDamage() == input.getItemDamage());
    }

    public static boolean itemMatches(ArrayList targets, ItemStack input)
    {
        return itemMatches(targets, input, false);
    }

    public static boolean itemMatches(ArrayList targets, ItemStack input, boolean strict)
    {
        if (input == null && targets != null && targets.size() > 0 || input != null && (targets == null || targets.size() == 0))
        {
            return false;
        }
        if (input == null && (targets == null || targets.size() == 0))
        {
            return true;
        }
        for (Object target : targets)
        {
            if (target instanceof ItemStack && itemMatches((ItemStack)target, input, strict))
            {
                return true;
            }
            else if(target instanceof ICraftingMaterial && ((ICraftingMaterial)target).itemMatches(input))
            {
                return true;
            }
        }
        return false;
    }
    
    public static ArrayList<ItemStack> expandArray(ArrayList items)
    {
        Map<List, ItemStack> tmp = new HashMap<List, ItemStack>();
        for(Object item : items)
        {
            if(item instanceof ItemStack)
            {
                tmp.put(Arrays.asList(((ItemStack)item).itemID, ((ItemStack)item).getItemDamage()), ((ItemStack)item).copy());
            }
            else if(item instanceof ICraftingMaterial)
            {
                for(ItemStack subitem : ((ICraftingMaterial)item).getItems())
                {
                    tmp.put(Arrays.asList(subitem.itemID, subitem.getItemDamage()), subitem);
                }
            }
        }
        
        return new ArrayList<ItemStack>(tmp.values());
    }
    
    public static boolean isValidItemList(Collection items)
    {
        for(Object item : items)
        {
            if(!(item instanceof ItemStack || item instanceof ICraftingMaterial))
            {
                return false;
            }
        }
        
        return true;
    }
}
