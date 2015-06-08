package net.minecraftforge.common.util;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.Collections;
import java.util.Set;

/**
 *  All the instances of subclasses of this class is immutable.
 */
public abstract class ItemCondition
{
    private ItemCondition(){}
    
    public abstract boolean check(ItemStack itemStack);

    public abstract ItemStack createStack();

    public static ItemCondition ofItem(Item item)
    {
        return new ConditionSimple(item);
    }

    public static ItemCondition ofBlock(Block block)
    {
        return new ConditionSimple(Item.getItemFromBlock(block));
    }

    /**
     * Creates new ItemCondition that checks the itemStack's item and damage.
     * @param itemStack
     * @return
     */
    public static ItemCondition ofItemStack(ItemStack itemStack)
    {
        return new ConditionSensitive(itemStack.getItem(), true, itemStack.getItemDamage(), Collections.<EvidenceAndValue>emptySet());
    }

    public static class ConditionSensitive extends ItemCondition
    {
        public final Item item;
        public final boolean checkDamage;
        public final int damage;
        public final Set<EvidenceAndValue> condNBT;

        private ConditionSensitive(Item item, boolean checkDamage, int damage, Set<EvidenceAndValue> set)
        {
            this.item = item;
            this.checkDamage = checkDamage;
            this.damage = damage;
            this.condNBT = ImmutableSet.copyOf(set);
        }

        @Override
        public boolean check(ItemStack itemStack)
        {
            if (this.item != itemStack.getItem() || (this.checkDamage && this.damage != itemStack.getItemDamage()))
            {
                return false;
            }

            if (condNBT.isEmpty())
            {
                return true;
            }

            for (EvidenceAndValue ev: condNBT)
            {
                if (!ev.check(itemStack.getTagCompound())) return false;
            }
            return true;
        }

        @Override
        public ItemStack createStack()
        {
            ItemStack stack = new ItemStack(this.item, 1, this.damage);

            if (condNBT.isEmpty())
            {
                return stack;
            }

            NBTTagCompound tag = new NBTTagCompound();

            for (EvidenceAndValue ev: condNBT)
            {
                ev.write(tag);
            }

            stack.setTagCompound(tag);
            return stack;
        }

        @Override
        public boolean equals(Object obj)
        {
            if (!(obj instanceof ConditionSensitive))
            {
                return false;
            }
                ConditionSensitive other = (ConditionSensitive) obj;
            return this.item == other.item
                    && this.checkDamage == other.checkDamage
                    && this.damage == other.damage
                    && this.condNBT.equals(other.condNBT);
        }

        @Override
        public int hashCode()
        {
            return Objects.hashCode(this.item, this.checkDamage, this.damage, this.condNBT);
        }
        
        @Override
        public String toString()
        {
            return "ItemCondition(item=" + this.item.getUnlocalizedName() + (this.checkDamage ? ", damage=" + this.damage : "") + ", nbt = " + this.condNBT + ")";
        }
    }

    public static class ConditionSimple extends ItemCondition
    {
        public final Item item;

        private ConditionSimple(Item item)
        {
            this.item = item;
        }

        @Override
        public boolean check(ItemStack itemStack)
        {
            return this.item == itemStack.getItem();
        }

        @Override
        public ItemStack createStack()
        {
            return new ItemStack(this.item);
        }

        @Override
        public boolean equals(Object obj)
        {
            return (obj instanceof ConditionSimple) && this.item == ((ConditionSimple) obj).item;
        }

        @Override
        public int hashCode()
        {
            return this.item.hashCode();
        }
        
        @Override
        public String toString()
        {
            return "ItemCondition(item=" + this.item.getUnlocalizedName() + ")";
        }
    }

    public static class Builder
    {
        private Set<EvidenceAndValue> set = Sets.newHashSet();
        private boolean checkDamage = false;
        private int damage = 0;

        private final Item item;

        public Builder(Item item)
        {
            this.item = item;
        }

        public Builder(Block block)
        {
            this.item = Item.getItemFromBlock(block);
        }

        public Builder(ItemStack itemStack)
        {
            this(itemStack.getItem());
            checkDamage(itemStack.getItemDamage());
        }

        public Builder checkDamage(int damage)
        {
            this.checkDamage = true;
            this.damage = damage;
            return this;
        }

        public <T> Builder addCondition(String key, NBTEvidence<T> ev, T value)
        {
            this.set.add(new EvidenceAndValue(key, ev, value));
            return this;
        }

        public ItemCondition build()
        {
            if (!checkDamage && set.isEmpty())
            {
                return ofItem(this.item);
            }

            return new ItemCondition.ConditionSensitive(this.item, this.checkDamage, this.damage, this.set);
        }
    }

    private static class EvidenceAndValue
    {
        private final String key;
        private final NBTEvidence ev;
        private final Object value;

        private EvidenceAndValue(String key, NBTEvidence ev, Object value)
        {
            this.key = key;
            this.ev = ev;
            this.value = value;
        }

        private boolean check(NBTTagCompound tagCompound)
        {
            return tagCompound.hasKey(this.key) && this.value == this.ev.get(tagCompound, key);
        }

        @SuppressWarnings("unchecked")
        public void write(NBTTagCompound tagCompound)
        {
            this.ev.write(tagCompound, this.key, this.value);
        }

        @Override
        public boolean equals(Object obj)
        {
            return (obj instanceof EvidenceAndValue) && this.key.equals(((EvidenceAndValue) obj).key);
        }

        @Override
        public int hashCode()
        {
            return this.key.hashCode();
        }
        
        @Override
        public String toString()
        {
            return this.key + "<" + this.ev.getTypeString() + ">: " + this.value;
        }
    }
}
