package net.minecraftforge.common.smelting;

import gnu.trove.strategy.HashingStrategy;
import net.minecraft.item.ItemStack;

final class ItemStackHasher implements HashingStrategy<ItemStack>
{

    @Override
    public int computeHashCode(ItemStack stack)
    {
        int hash = 31 + System.identityHashCode(stack.getItem());
        hash = 31 * hash + stack.getMetadata();
        return hash;
    }

    @Override
    public boolean equals(ItemStack o1, ItemStack o2)
    {
        return o1.getItem() == o2.getItem() && o1.getMetadata() == o2.getMetadata();
    }

}
