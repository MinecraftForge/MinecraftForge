/*
 * Minecraft Forge
 * Copyright (c) 2016.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.items.filter;

import com.google.common.collect.Range;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Predicate;

public class ItemStackFilter implements IStackFilter
{
    // only used to check the capability NBT
    private final ItemStack stack;
    private final Item item;
    private final Range<Integer> metadata;
    private final Predicate<NBTTagCompound> nbtTag;
    private final Range<Integer> stackSize;
    private final boolean matchNBT;
    private final boolean matchItem;
    private final boolean matchCapNBTData;
    private final boolean matchMeta;
    private final boolean matchStackSize;
    private final boolean inverted;

    protected ItemStackFilter(ItemStack stack, Item item, Range<Integer> metadata, Predicate<NBTTagCompound> nbtTag,
                              Range<Integer> stackSize, boolean matchNBT, boolean matchItem, boolean matchStack, boolean matchMeta, boolean matchStackSize, boolean inverted)
    {
        this.stack = stack;
        this.item = item;
        this.metadata = metadata;
        this.nbtTag = nbtTag;
        this.stackSize = stackSize;
        this.matchNBT = matchNBT;
        this.matchItem = matchItem;
        this.matchCapNBTData = matchStack;
        this.matchMeta = matchMeta;
        this.matchStackSize = matchStackSize;

        this.inverted = inverted;
    }

    public static Builder filterBuilder()
    {
        return new Builder();
    }

    @Override
    public boolean test(ItemStack stack)
    {
        if (matchItem)
        {
            if (stack.getItem() != item)
            {
                return inverted;
            }
        }
        if (matchMeta)
        {
            if (!metadata.contains(stack.getMetadata()))
            {
                return inverted;
            }
        }
        if (matchStackSize)
        {
            if (!stackSize.contains(stack.getCount()))
            {
                return inverted;
            }
        }
        if (matchNBT)
        {
            NBTTagCompound tag = stack.getTagCompound();
            if ((nbtTag == null) != (tag == null))
            {
                return inverted;
            }
            return (tag == null || nbtTag.equals(tag)) != inverted;
        }
        if (matchCapNBTData)
        {
            if (!this.stack.areCapsCompatible(stack))
                return inverted;
        }
        return true;
    }

    public static class Builder
    {
        @Nullable
        private Range<Integer> stackSize = null;
        @Nonnull
        private ItemStack stack = ItemStack.EMPTY;
        @Nullable
        private Item item = null;
        @Nullable
        private Range<Integer> metadata = null;
        @Nullable
        private Predicate<NBTTagCompound> nbtTag = null;
        private boolean inverted = false;


        public Builder setInverted()
        {
            this.inverted = true;
            return this;
        }

        public Builder withNbtTag(Predicate<NBTTagCompound> nbtTag)
        {
            this.nbtTag = nbtTag;
            return this;
        }

        public Builder withMetadata(Range<Integer> metadata)
        {
            this.metadata = metadata;
            return this;
        }

        public Builder withCapNBTData(ItemStack stack)
        {
            this.stack = stack;
            return this;
        }

        public Builder withItem(Item item)
        {
            this.item = item;
            return this;
        }

        public Builder withStackSizeFilter(Range<Integer> stackSize)
        {
            this.stackSize = stackSize;
            return this;
        }

        public ItemStackFilter build()
        {
            return new ItemStackFilter(stack, item, metadata, nbtTag, stackSize, nbtTag != null, item != null, !stack.isEmpty(), metadata != null, stackSize != null, inverted);
        }
    }
}
