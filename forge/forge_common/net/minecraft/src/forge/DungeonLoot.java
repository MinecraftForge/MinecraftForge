package net.minecraft.src.forge;

import java.util.Random;
import net.minecraft.src.ItemStack;

/**
 *
 * Used to hold a list of all items that can be spawned in a world dungeon
 *
 */
public class DungeonLoot
{
    private ItemStack itemStack;
    private int minCount = 1;
    private int maxCount = 1;

    /**
     * @param item A item stack
     * @param min Minimum stack size when randomly generating
     * @param max Maximum stack size when randomly generating
     */
    public DungeonLoot(ItemStack item, int min, int max)
    {
        this.itemStack = item;
        minCount = min;
        maxCount = max;
    }

    /**
     * Grabs a ItemStack ready to be added to the dungeon chest,
     * the stack size will be between minCount and maxCount
     * @param rand World gen random number generator
     * @return The ItemStack to be added to the chest
     */
    public ItemStack generateStack(Random rand)
    {
        ItemStack ret = this.itemStack.copy();
        ret.stackSize = minCount + (rand.nextInt(maxCount - minCount + 1));
        return ret;
    }

    public boolean equals(ItemStack item, int min, int max)
    {
        return (min == minCount && max == maxCount && item.isItemEqual(this.itemStack));
    }

    public boolean equals(ItemStack item)
    {
        return item.isItemEqual(this.itemStack);
    }
}
