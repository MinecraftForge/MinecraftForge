package net.minecraftforge.inventory;

import net.minecraft.item.ItemStack;

public interface IStackFilter {
    /**
     * Returns true if the given item matches this filter. The stack size is
     * ignored.
     * 
     * @param item
     *            The item to match.
     * @return True if the item matches the filter.
     */
    boolean matchesItem(ItemStack item);

    /**
     * A default filter that matches any item.
     */
    static final IStackFilter MATCH_ANY = new IStackFilter() {
        @Override
        public boolean matchesItem(ItemStack item)
        {
            return true;
        }
    };
}
