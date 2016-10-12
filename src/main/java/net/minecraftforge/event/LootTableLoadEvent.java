package net.minecraftforge.event;

import com.google.common.collect.Lists;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraftforge.fml.common.eventhandler.Event;

import java.util.List;

/**
 * This event is fired on MinecraftForge.EVENT_BUS immediately after a loot table finishes loading from disk.
 * The path is given so you can add your own loot pools based on the table loading.
 * This is the new replacement for ChestGenHooks/DungeonHooks/FishingHooks, listen for the path of interest and
 * insert your own loot pools.
 * This event has no result, and is not cancelable.
 */
public class LootTableLoadEvent extends Event {
    private final List<LootPool> pools;
    private final ResourceLocation path;

    public LootTableLoadEvent(LootTable originalTable, ResourceLocation path)
    {
        this.pools = Lists.newArrayList(originalTable.pools);
        this.path = path;
    }

    /**
     * Add your own loot pool to the Loot Table.
     * @param pool The pool to add to the list of pools.
     */
    public void addPool(LootPool pool)
    {
        pools.add(pool);
    }

    /**
     * @return the path of the LootTable that is loading
     */
    public ResourceLocation getPath()
    {
        return path;
    }

    /**
     * Build a LootTable based on the currently added pools.
     * This is an immutable snapshot of the current pools,
     * further changes made via this event will not be reflected in the returned object!
     */
    public LootTable getTable()
    {
        return new LootTable(pools.toArray(new LootPool[pools.size()]));
    }
}
