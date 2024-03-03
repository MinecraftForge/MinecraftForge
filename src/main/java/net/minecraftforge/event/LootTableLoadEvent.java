/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTables;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

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
    private LootTables lootTableManager;

    public LootTableLoadEvent(ResourceLocation name, LootTable table, LootTables lootTableManager)
    {
        this.name = name;
        this.table = table;
        this.lootTableManager = lootTableManager;
    }

    public ResourceLocation getName()
    {
        return this.name;
    }

    public LootTable getTable()
    {
        return this.table;
    }

    public LootTables getLootTableManager()
    {
        return this.lootTableManager;
    }

    public void setTable(LootTable table)
    {
        this.table = table;
    }
}
