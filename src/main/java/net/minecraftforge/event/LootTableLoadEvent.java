/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.LogicalSide;

/**
 * Fired when a {@link LootTable} is loaded from JSON.
 * Loot tables loaded from world save datapacks will not fire this event as they are considered user configuration files.
 * This event is fired whenever server resources are loaded or reloaded.
 *
 * <p>This event is {@linkplain Cancelable cancellable}, and does not {@linkplain HasResult have a result}.
 * If the event is cancelled, the loot table will be made empty.</p>
 *
 * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
 * only on the {@linkplain LogicalSide#SERVER logical server}.</p>
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
