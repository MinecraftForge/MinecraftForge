package net.minecraftforge.oredict;

import java.util.Iterator;
import java.util.List;

import net.minecraft.item.ItemStack;

import com.google.common.base.Predicate;

public class OreNamePredicate implements Predicate<ItemStack>, Iterable<ItemStack>
{
    public final String name;
    private List<ItemStack> items; // lazily initiated to avoid load order issues

    public OreNamePredicate(String name)
    {
        this.name = name;
    }

    @Override
    public boolean apply(ItemStack input)
    {
        for (ItemStack item : this)
        {
            if (OreDictionary.itemMatches(item, input, false))
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public Iterator<ItemStack> iterator()
    {
        List<ItemStack> items = this.items;
        if (items == null)
        {
            this.items = items = OreDictionary.getOres(name);
        }
        return items.iterator();
    }
}