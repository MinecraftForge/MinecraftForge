package net.minecraftforge.oredict;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import cpw.mods.fml.common.FMLLog;

public class FurnaceOreRecipes {

    private static Map<ArrayList<ItemStack>, ItemStack> outputsMap = new HashMap<ArrayList<ItemStack>, ItemStack>();
    private static Map<ArrayList<ItemStack>, Float> experienceMap = new HashMap<ArrayList<ItemStack>, Float>();

    public static void addSmelting(String ore, ItemStack output, float xp)
    {
        if (output != null && ore != null)
        {
            ArrayList<ItemStack> oreStacks = OreDictionary.getOres(ore);
            outputsMap.put(oreStacks, output.copy());
            experienceMap.put(oreStacks, xp);
        }
    }

    public static ItemStack getSmeltingResult(ItemStack stack)
    {
        return getValue(stack, outputsMap);
    }

    public static float getSmeltingExperience(ItemStack stack)
    {
        Float xp = getValue(stack, experienceMap);
        return xp == null ? -1 : xp;
    }

    private static <T> T getValue(ItemStack stack, Map<ArrayList<ItemStack>, T> map)
    {
        for (Iterator<Entry<ArrayList<ItemStack>, T>> iterator = map.entrySet().iterator(); iterator.hasNext();)
        {
            Entry<ArrayList<ItemStack>, T> entry = iterator.next();
            for (ItemStack target : entry.getKey())
            {
                if (OreDictionary.itemMatches(target, stack, false))
                {
                    return entry.getValue();
                }
            }
        }
        return null;
    }

    public static void replaceSmeltingRecipes()
    {
        Map<ItemStack, String> replacements = new HashMap<ItemStack, String>();
        replacements.put(new ItemStack(Blocks.iron_ore), "oreIron");
        replacements.put(new ItemStack(Blocks.gold_ore), "oreGold");
        replacements.put(new ItemStack(Blocks.diamond_ore), "oreDiamond");
        replacements.put(new ItemStack(Blocks.log), "logWood");
        replacements.put(new ItemStack(Blocks.log2), "logWood");
        replacements.put(new ItemStack(Blocks.emerald_ore), "oreEmerald");
        replacements.put(new ItemStack(Blocks.coal_ore), "oreCoal");
        replacements.put(new ItemStack(Blocks.redstone_ore), "oreRedstone");
        replacements.put(new ItemStack(Blocks.lapis_ore), "oreLapis");
        replacements.put(new ItemStack(Blocks.quartz_ore), "oreQuartz");

        List<ItemStack> stacksToRemove = new ArrayList<ItemStack>();
        List<ItemStack> xpsToRemove = new ArrayList<ItemStack>();
        for (Entry<ItemStack, ItemStack> entry : (Set<Entry<ItemStack, ItemStack>>) FurnaceRecipes.smelting().getSmeltingList().entrySet())
        {
            for (Entry<ItemStack, String> replacement : replacements.entrySet())
            {
                if (OreDictionary.itemMatches(entry.getKey(), replacement.getKey(), false))
                {
                    addSmelting(replacement.getValue(), entry.getValue(), FurnaceRecipes.smelting().func_151398_b(entry.getKey()));
                    stacksToRemove.add(entry.getKey());
                    xpsToRemove.add(entry.getValue());
                }
            }
        }

        for (ItemStack toRemove : stacksToRemove)
        {
            FurnaceRecipes.smelting().getSmeltingList().remove(toRemove);
        }

        for (ItemStack toRemove : xpsToRemove)
        {
            FurnaceRecipes.smelting().getExperienceList().remove(toRemove);
        }

        if (stacksToRemove.size() > 0)
        {
            FMLLog.info("Replaced %d smelting recipes", stacksToRemove.size());
        }
    }
}