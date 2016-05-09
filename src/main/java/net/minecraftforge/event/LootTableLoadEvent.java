package net.minecraftforge.event;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * Event fired when a LootTable json is loaded from json.
 * This event is fired whenever resources are loaded, or when the server starts.
 * This event will NOT be fired for LootTables loaded from the world folder, these are
 * considered configurations files and should not be modified by mods.
 *
 * Canceling the event will make it load a empty loot table.
 *
 */
@Cancelable
public class LootTableLoadEvent extends Event
{
    private final ResourceLocation name;
    private LootTable table;

    public LootTableLoadEvent(ResourceLocation name, LootTable table)
    {
        this.name = name;
        this.table = table;
    }

    public ResourceLocation getName()
    {
        return this.name;
    }

    public LootTable getTable()
    {
        return this.table;
    }

    public void setTable(LootTable table)
    {
        this.table = table;
    }
}
