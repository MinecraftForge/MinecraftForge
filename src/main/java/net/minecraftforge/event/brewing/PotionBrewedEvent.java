package net.minecraftforge.event.brewing;

import cpw.mods.fml.common.eventhandler.Event;
import net.minecraft.item.ItemStack;

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
}
