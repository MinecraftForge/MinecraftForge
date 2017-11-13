package net.minecraftforge.common.crafting;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;
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
    @Nonnull
    public ItemStack[] getMatchingStacks()
    {
        if (stacks == null)
        {
            List<ItemStack> tmp = Lists.newArrayList();
            for (Ingredient child : children)
                Collections.addAll(tmp, child.getMatchingStacks());
            stacks = tmp.toArray(new ItemStack[tmp.size()]);

        }
        return stacks;
    }

    @Deprecated
    @Override
    @Nonnull
    @SideOnly(Side.CLIENT)
    public IntList getValidItemStacksPacked()
    {
        //TODO: Add a child.isInvalid()?
        if (this.itemIds == null)
        {
            this.itemIds = new IntArrayList();
            for (Ingredient child : children)
                this.itemIds.addAll(child.getValidItemStacksPacked());
            this.itemIds.sort(IntComparators.NATURAL_COMPARATOR);
        }

        return this.itemIds;
    }

    @Override
    public boolean apply(@Nullable ItemStack target)
    {
        if (target == null)
            return false;

        for (Ingredient child : children)
            if (child.apply(target))
                return true;

        return false;
    }

    @Override
    protected void invalidate()
    {
        this.itemIds = null;
        this.stacks = null;
        //Shouldn't need to invalidate children as this is only called form invalidateAll..
    }
}
