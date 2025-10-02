/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.util.MutableHashedLinkedMap;
import net.minecraftforge.eventbus.api.bus.BusGroup;
import net.minecraftforge.eventbus.api.bus.EventBus;
import net.minecraftforge.eventbus.api.event.MutableEvent;
import net.minecraftforge.fml.LogicalSide;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NullMarked;

import java.util.function.Supplier;

/**
 * Fired when the contents of a specific creative mode tab are being populated.
 * This event may be fired multiple times if the operator status of the local player or enabled feature flags changes.
 * <p>
 * This event is fired only on the {@linkplain LogicalSide#CLIENT logical client}.
 */
@NullMarked
public final class BuildCreativeModeTabContentsEvent extends MutableEvent implements CreativeModeTab.Output {
    public static final EventBus<BuildCreativeModeTabContentsEvent> BUS = EventBus.create(BuildCreativeModeTabContentsEvent.class);

    @Deprecated(forRemoval = true, since = "1.21.9")
    public static EventBus<BuildCreativeModeTabContentsEvent> getBus(BusGroup modBusGroup) {
        return BUS;
    }

    private final CreativeModeTab tab;
    private final CreativeModeTab.ItemDisplayParameters parameters;
    private final MutableHashedLinkedMap<ItemStack, CreativeModeTab.TabVisibility> entries;
    private final ResourceKey<CreativeModeTab> tabKey;

    @ApiStatus.Internal
    public BuildCreativeModeTabContentsEvent(CreativeModeTab tab, ResourceKey<CreativeModeTab> tabKey, CreativeModeTab.ItemDisplayParameters parameters, MutableHashedLinkedMap<ItemStack, CreativeModeTab.TabVisibility> entries) {
        this.tab = tab;
        this.tabKey = tabKey;
        this.parameters = parameters;
        this.entries = entries;
    }

    /**
     * {@return the creative mode tab currently populating its contents}
     */
    public CreativeModeTab getTab() {
        return this.tab;
    }

    /**
     * {@return the key of the creative mode tab currently populating its contents}
     */
    public ResourceKey<CreativeModeTab> getTabKey() {
        return this.tabKey;
    }

    public FeatureFlagSet getFlags() {
        return this.parameters.enabledFeatures();
    }

    public CreativeModeTab.ItemDisplayParameters getParameters() {
        return parameters;
    }

    public boolean hasPermissions() {
        return this.parameters.hasPermissions();
    }

    public MutableHashedLinkedMap<ItemStack, CreativeModeTab.TabVisibility> getEntries() {
        return this.entries;
    }

    @Override
    public void accept(ItemStack stack, CreativeModeTab.TabVisibility visibility) {
        getEntries().put(stack, visibility);
    }

    public void accept(Supplier<? extends ItemLike> item, CreativeModeTab.TabVisibility visibility) {
        this.accept(item.get(), visibility);
    }

    public void accept(Supplier<? extends ItemLike> item) {
        this.accept(item.get());
    }
}
