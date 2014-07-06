package net.minecraftforge.event.brewing;

import cpw.mods.fml.common.eventhandler.Event;
import net.minecraft.item.ItemStack;

/**
 * PotionBrewedEvent is fired when a potion is brewed in the brewing stand.
 * <br>
 * The event is fired during the TileEntityBrewingStand#brewPotions() method invocation.<br>
 * <br>
 * {@link #brewingStacks} contains the itemstack array from the TileEntityBrewer holding all items in Brewer.<br>
 * <br>
 * This event is not {@link Cancelable}.<br>
 * <br>
 * This event does not have a result. {@link HasResult}<br>
 * <br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.<br>
 **/
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
