/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.event;

import net.minecraft.resources.Identifier;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraftsingularity.eventbus.api.bus.CancellableEventBus;
import net.minecraftsingularity.eventbus.api.event.MutableEvent;
import net.minecraftsingularity.eventbus.api.event.characteristic.Cancellable;
import net.minecraftsingularity.fml.LogicalSide;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

/**
 * Fired when a {@link LootTable} is loaded from JSON.
 * Loot tables loaded from world save datapacks will not fire this event as they are considered user configuration files.
 * This event is fired whenever server resources are loaded or reloaded.
 *
 * <p>This event is {@linkplain Cancellable cancellable}. If the event is cancelled, the loot table will be made empty.</p>
 *
 * <p>This event is fired only on the {@linkplain LogicalSide#SERVER logical server}.</p>
 */
@NullMarked
public final class LootTableLoadEvent extends MutableEvent implements Cancellable {
    public static final CancellableEventBus<LootTableLoadEvent> BUS = CancellableEventBus.create(LootTableLoadEvent.class);

    private final Identifier name;
    private @Nullable LootTable table;

    public LootTableLoadEvent(Identifier name, LootTable table) {
        this.name = name;
        this.table = table;
    }

    public Identifier getName() {
        return this.name;
    }

    @Nullable
    public LootTable getTable() {
        return this.table;
    }

    public void setTable(@Nullable LootTable table) {
        this.table = table;
    }
}
