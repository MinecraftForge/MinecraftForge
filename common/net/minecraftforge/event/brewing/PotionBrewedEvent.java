package net.minecraftforge.event.brewing;

import net.minecraft.item.ItemStack;
import net.minecraftforge.event.Cancelable;
import net.minecraftforge.event.Event;
import net.minecraftforge.event.Event.HasResult;

public class PotionBrewedEvent extends Event
{
    /**
     * The brewing stacks in the brewing stand.  Each index has the possibility to be null, so make sure you check.
     */
    public ItemStack[] brewingStacks;
    public PotionBrewedEvent(ItemStack[] brewingStacks)
    {
        this.brewingStacks = brewingStacks;
    }
    
    /**
     * Called after the Potion Ingredient has been applied to all present
     * Potions. Each index has the possibility to be null, so make sure you
     * check.
     */
    @Cancelable
    public static class BrewingConsumeEvent extends PotionBrewedEvent
    {
        public BrewingConsumeEvent(ItemStack[] brewingStacks) { super(brewingStacks); }
    }
}
