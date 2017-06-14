package net.minecraftforge.common.crafting;

import java.util.Collection;
import java.util.List;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntComparators;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class CompoundIngredient extends Ingredient
{
    private Collection<Ingredient> children;
    private ItemStack[] stacks;
    private IntList itemIds;

    protected CompoundIngredient(Collection<Ingredient> children)
    {
        super(0);
        this.children = children;
    }

    @Override
    public ItemStack[] func_193365_a()
    {
        if (stacks == null)
        {
            List<ItemStack> tmp = Lists.newArrayList();
            for (Ingredient child : children)
                for (ItemStack stack : child.func_193365_a())
                    tmp.add(stack);
            stacks = tmp.toArray(new ItemStack[tmp.size()]);

        }
        return stacks;
    }

    @SideOnly(Side.CLIENT)
    public IntList func_194139_b()
    {
        //TODO: Add a child.isInvalid()?
        if (this.itemIds == null)
        {
            this.itemIds = new IntArrayList();
            for (Ingredient child : children)
                this.itemIds.addAll(child.func_194139_b());
            this.itemIds.sort(IntComparators.NATURAL_COMPARATOR);
        }

        return this.itemIds;
    }


    public boolean apply(@Nullable ItemStack target)
    {
        if (target == null)
            return false;

        for (Ingredient child : children)
            if (child.apply(target))
                return true;

        return false;
    }

    protected void invalidate()
    {
        this.itemIds = null;
        this.stacks = null;
        //Shouldn't need to invalidate children as this is only called form invalidateAll..
    }
}
