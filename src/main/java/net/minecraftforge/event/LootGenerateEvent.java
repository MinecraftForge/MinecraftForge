package net.minecraftforge.event;

import net.minecraft.item.ItemStack;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

import java.util.List;

/**
 * This event is fired on MinecraftForge.EVENT_BUS after a LootTable has finished generating a list of possible loot.
 * Modifications to the ItemStack list will change the loot directly.
 * Canceling the event will cause an empty list to be returned in the end (existing list will not be cleared).
 * This is so that uncanceling the event will work as intended.
 * This event has no result.
 */
@Cancelable
public class LootGenerateEvent extends Event
{
    private final LootTable table;
    private final LootContext context;
    private final List<ItemStack> lootList;

    public LootGenerateEvent(LootTable table, LootContext context, List<ItemStack> lootList)
    {
        this.table = table;
        this.context = context;
        this.lootList = lootList;
    }

    /**
     * @return The loot table currently generating
     */
    public LootTable getTable()
    {
        return table;
    }

    /**
     * @return The loot context currently in use
     */
    public LootContext getContext()
    {
        return context;
    }

    /**
     * @return The loot that this loot table has produced. Modifying this list will change the final result
     */
    public List<ItemStack> getLoot()
    {
        return lootList;
    }
}
