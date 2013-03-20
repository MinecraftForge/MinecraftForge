package net.minecraftforge.event.brewing;

import net.minecraft.item.ItemStack;
import net.minecraftforge.event.Event;

public class PotionBrewedEvent extends Event{
    public ItemStack[] brewingStacks;
    public PotionBrewedEvent(ItemStack[] resultingPotionStacks)
    {
        brewingStacks = resultingPotionStacks;
    }
}
