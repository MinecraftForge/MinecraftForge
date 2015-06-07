package net.minecraftforge.common.util;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.SensitiveOreDict;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Immutable.
 */
public abstract class ItemPredicate {

    private ItemPredicate(){}
    
    public boolean apply (ItemStack itemStack)
    {
        return itemStack != null && check(itemStack);
    }

    protected abstract boolean check (ItemStack itemStack);

    public List<ItemStack> createAll()
    {
        return Lists.newArrayList();
    }

    public static final ItemPredicate NULL_STACK = new ItemPredicate()
    {
        @Override
        public boolean apply (ItemStack itemStack)
        {
            return itemStack == null;
        }

        @Override
        protected boolean check (ItemStack itemStack)
        {
            return false;
        }
    };

    public static ItemPredicate of (ItemCondition condition)
    {
        return new SinglePredicate(condition);
    }

    /**
     * Returns new ItemPredicate to check if an ItemStack satisfies either of given two ItemConditions.
     * @param c1
     * @param c2
     * @return
     */
    public static ItemPredicate of (ItemCondition c1, ItemCondition c2)
    {
        return new Predicate2(c1, c2);
    }

    /**
     * Returns new ItemPredicate to check if an ItemStack satisfies either of given  ItemConditions.
     * @param c1
     * @param c2
     * @param others
     * @return
     */
    public static ItemPredicate of (ItemCondition c1, ItemCondition c2, ItemCondition... others)
    {
        List<ItemCondition> list = Lists.newArrayListWithCapacity(2 + others.length);
        list.add(c1);
        list.add(c2);
        for (ItemCondition c : others)
        {
            list.add(c);
        }
        return new ItemPredicate.ListPredicate(list);
    }

    /**
     * Returns new ItemPredicate to check if an ItemStack satisfies either of given  ItemConditions.
     * @param collection
     * @return
     */
    public static ItemPredicate of (Collection<ItemCondition> collection)
    {
        return new ListPredicate(collection);
    }

    public static ItemPredicate ofItem (Item item)
    {
        return new SimpleItemPredicate(item);
    }

    public static ItemPredicate ofBlock (Block block)
    {
        return new SimpleItemPredicate(Item.getItemFromBlock(block));
    }

    public static ItemPredicate ofOre(String name)
    {
        return new OrePredicate(name);
    }
    
    public  static class SimpleItemPredicate extends ItemPredicate
    {
        public final Item item;

        private SimpleItemPredicate(Item item)
        {
            this.item = item;
        }

        @Override
        protected boolean check(ItemStack itemStack)
        {
            return this.item == itemStack.getItem();
        }

        @Override
        public List<ItemStack> createAll()
        {
            return Lists.newArrayList(new ItemStack(this.item));
        }
        
        @Override
        public String toString()
        {
            return "ItemPredicate(item = " + this.item.getUnlocalizedName() + ")";
        }
    }

    public static class SinglePredicate extends ItemPredicate
    {
        public final ItemCondition condition;

        private SinglePredicate (ItemCondition condition)
        {
            this.condition = condition;
        }

        @Override
        protected boolean check (ItemStack itemStack)
        {
            return condition.check(itemStack);
        }

        @Override
        public List<ItemStack> createAll ()
        {
            return Lists.newArrayList(this.condition.createStack());
        }
        
        @Override
        public String toString()
        {
            return "ItemPredicate(" + this.condition + ")";
        }
    }

    public static class Predicate2 extends ItemPredicate
    {
        public final ItemCondition cond1;
        public final ItemCondition cond2;

        private Predicate2 (ItemCondition cond1, ItemCondition cond2)
        {
            this.cond1 = cond1;
            this.cond2 = cond2;
        }

        @Override
        protected boolean check (ItemStack itemStack)
        {
            return this.cond1.check(itemStack) || this.cond2.check(itemStack);
        }

        @Override
        public List<ItemStack> createAll ()
        {
            return Lists.newArrayList(this.cond1.createStack(), this.cond2.createStack());
        }
        
        @Override
        public String toString()
        {
            return "ItemPredicate(" + this.cond1 + ", " + this.cond2 + ")";
        }
    }

    public static class ListPredicate extends ItemPredicate
    {
        public final ImmutableList<ItemCondition> conditionList;

        private ListPredicate (Collection<ItemCondition> conditions)
        {
            this.conditionList = ImmutableList.copyOf(conditions);
        }

        @Override
        protected boolean check (ItemStack itemStack) {
            for (ItemCondition cond: this.conditionList)
            {
                if (cond.check(itemStack))
                {
                    return true;
                }
            }

            return false;
        }

        @Override
        public List<ItemStack> createAll ()
        {
            List<ItemStack> list = Lists.newArrayList();
            for (ItemCondition cond: this.conditionList)
            {
                list.add(cond.createStack());
            }
            return list;
        }
        
        @Override
        public String toString()
        {
            return "ItemPredicate(" + this.conditionList + ")";
        }
    }
    
    public static class OrePredicate extends ItemPredicate
    {
        private final String oreName;

        private OrePredicate (String oreName)
        {
            this.oreName = oreName;
        }
        
        @Override
        public String toString()
        {
            return "ItemPredicate(ore = \"" +  this.oreName + "\")";
        }

        @Override
        protected boolean check(ItemStack itemStack)
        {
            return SensitiveOreDict.hasName(itemStack, this.oreName);
        }

        @Override
        public List<ItemStack> createAll()
        {
            List<ItemCondition> condList = SensitiveOreDict.getOres(oreName);
            List<ItemStack> stackList = new ArrayList<ItemStack>(condList.size());
            for (ItemCondition cond : condList)
            {
                stackList.add(cond.createStack());
            }
            return stackList;
        }
    }
}
