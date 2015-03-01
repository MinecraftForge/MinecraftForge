package net.minecraftforge.oredict;

import java.util.Iterator;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.RegistryDelegate;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterators;

public class ItemPredicate implements Predicate<ItemStack>, Iterable<ItemStack>
{
    private final RegistryDelegate<Item> item;
    private final int damage;

    public ItemPredicate(ItemStack item)
    {
        this.item = item.getItem().delegate;
        damage = item.getItemDamage();
    }

    @Override
    public boolean apply(ItemStack in)
    {
        return in != null && item.get() == in.getItem() && (damage == OreDictionary.WILDCARD_VALUE || damage == in.getItemDamage());
    }

    @Override
    public Iterator<ItemStack> iterator()
    {
        return Iterators.singletonIterator(new ItemStack(item.get(), 1, damage));
    }
}