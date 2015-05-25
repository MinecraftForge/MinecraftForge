package net.minecraftforge.common;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.SensitiveOreDict;

/**
 * This class provides some kind of filter that checks an ItemStack if it satisfy particular requirement.
 */
public abstract class ItemPredicate
{
    protected abstract boolean check(ItemStack itemStack);

    protected boolean checkIgnoreSize(ItemStack itemStack)
    {
        return this.check(itemStack);
    }
    
    public boolean apply(ItemStack itemStack, boolean checkSize) {
        return itemStack != null && (checkSize && check(itemStack) || !checkSize && checkIgnoreSize(itemStack));
    }

    /**
     * Returns new object that checks both this and the other requirement.
     * @param other
     * @return new object
     */
    public ItemPredicate andThen(ItemPredicate other)
    {
        return new ItemPredicate.And(this, other);
    }

    public ItemPredicate minSize(int size)
    {
        return this.andThen(new MinSize(size));
    }

    /**
     * this object checks if an ItemStack is null.      
     */
    public static final ItemPredicate EMPTY_STACK = new ItemPredicate() {
        @Override
        public boolean apply (ItemStack itemStack, boolean checkSize) {
            return itemStack == null || itemStack.stackSize == 0;
        }

        @Override
        protected boolean check (ItemStack itemStack) {
            return false;
        }
    };

    public static ItemPredicate of(Item item, int damage) {
        return new ItemWithDamage(item, damage, true);
    }

    public static ItemPredicate of(Block block, int damage) {
        return new ItemWithDamage(Item.getItemFromBlock(block), damage, true);
    }

    /**
     * Returns new predicate that checks if the item stack's Item and damage is same as that of given ItemStack.
     * @param itemStack
     * @return new predicate
     */
    public static ItemPredicate ofItemStack(ItemStack itemStack)
    {
        return new ItemWithDamage(itemStack.getItem(), itemStack.getItemDamage(), true);
    }

    /**
     * Returns new predicate that checks the an item stack has same Item and same damage and same or more number than that of given ItemStack.
     * @param itemStack
     * @return new predicate
     */
    public static ItemPredicate ofItemStackAndSize(ItemStack itemStack)
    {
        return ofItemStack(itemStack).minSize(itemStack.stackSize);
    }

    public static ItemPredicate ofItem(Item item)
    {
        return new ItemWithDamage(item, 0, false);
    }

    public static ItemPredicate ofBlock(Block block)
    {
        return new ItemWithDamage(Item.getItemFromBlock(block), 0, false);
    }

    public static ItemPredicate ofOre(String name)
    {
        return new Ore(name);
    }

    public static class ItemWithDamage extends ItemPredicate
    {
        public final Item item;
        public final int damage;
        public final boolean sensitive;

        public ItemWithDamage (Item item, int damage, boolean sensitive)
        {
            this.item = item;
            this.damage = damage;
            this.sensitive = sensitive;
        }

        @Override
        protected boolean check (ItemStack itemStack)
        {
            return itemStack != null &&
                    this.item == itemStack.getItem() &&
                    (!this.sensitive || this.damage == itemStack.getItemDamage());
        }
    }

    public static class Ore extends ItemPredicate
    {

        public final String oreName;

        public Ore (String oreName) {
            this.oreName = oreName;
        }

        @Override
        protected boolean check (ItemStack itemStack)
        {
            return SensitiveOreDict.hasName(itemStack, oreName);
        }
    }

    public static class MinSize extends ItemPredicate
    {
        public final int size;

        public MinSize (int size) {
            this.size = size;
        }


        @Override
        protected boolean check (ItemStack itemStack) {
            return itemStack.stackSize >= size;
        }
        
        @Override
        protected boolean checkIgnoreSize(ItemStack itemStack) {
            return true;
        }
    }

    public static class And extends ItemPredicate
    {
        public final ItemPredicate one;
        public final ItemPredicate two;


        public And (ItemPredicate one, ItemPredicate two) {
            this.one = one;
            this.two = two;
        }

        @Override
        public boolean apply (ItemStack itemStack, boolean checkSize)
        {
            return one.apply(itemStack, checkSize) && two.apply(itemStack, checkSize);
        }

        @Override
        protected boolean check (ItemStack itemStack) {
            return false;
        }

        @Override
        protected boolean checkIgnoreSize(ItemStack itemStack)
        {
            return false;
        }
    }

    public static class Or extends ItemPredicate
    {
        public final ItemPredicate one;
        public final ItemPredicate two;


        public Or (ItemPredicate one, ItemPredicate two) 
        {
            this.one = one;
            this.two = two;
        }

        @Override
        public boolean apply (ItemStack itemStack, boolean checkSize)
        {
            return one.apply(itemStack, checkSize) || two.apply(itemStack, checkSize);
        }

        @Override
        protected boolean check (ItemStack itemStack) {
            return false;
        }

        @Override
        protected boolean checkIgnoreSize (ItemStack itemStack)
        {
            return false;
        }
    }
}
