/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.CreativeModeTabRegistry;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.event.IModBusEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

public class CreativeModeTabEvent extends Event implements IModBusEvent
{
    private static final List<Object> DEFAULT_AFTER_ENTRIES = List.of(CreativeModeTabs.SPAWN_EGGS);

    @ApiStatus.Internal
    public CreativeModeTabEvent() {}

    /**
     * A registrar which is capable of registering new tabs.
     */
    @FunctionalInterface
    public interface Registrar {

        /**
         * Register a new tab.
         *
         * @param configurator The tab configurator to register
         * @param name The name of the tab.
         * @param afters A list of tab names (String, ResourceLocations) or tabs that need to appear before this tab in the creative mode menu.
         * @param befores A list of tab names (String, ResourceLocations) or tabs that need to appear after this tab in the creative mode menu.
         * @return The configured tab.
         */
        @NotNull
        CreativeModeTab registerCreativeModeTab(@NotNull final Consumer<CreativeModeTab.Builder> configurator, @NotNull final ResourceLocation name, @NotNull final List<Object> afters, @NotNull final List<Object> befores);
    }

    /**
     * Fired when creative mode tabs can be registered.
     * <p>
     * This event is not {@linkplain Cancelable cancellable}, and does not {@linkplain HasResult have a result}.
     * <p>
     * This event is fired on the {@linkplain FMLJavaModLoadingContext#getModEventBus() mod-specific event bus},
     * on both {@linkplain LogicalSide logical sides}.
     */
    public static final class Register extends CreativeModeTabEvent
    {
        private final Registrar registrar;

        @ApiStatus.Internal
        public Register(Registrar registrar) {
            this.registrar = registrar;
        }

        /**
         * Registers and configures a creative mode tab with the given name and returns the built instance.
         *
         * @param name the name of the tab. Must be unique.
         * @param beforeEntries the list of {@linkplain ResourceLocation tab names} or {@linkplain CreativeModeTab tabs} that should appear before this tab in the creative mode inventory screen
         * @param afterEntries the list of {@linkplain ResourceLocation tab names} or {@linkplain CreativeModeTab tabs} that should appear after this tab in the creative mode inventory screen
         * @param configurator the configurator which configures the builder
         * @return the configured creative mode tab
         * @see #registerCreativeModeTab(ResourceLocation, Consumer)
         */
        public CreativeModeTab registerCreativeModeTab(ResourceLocation name, List<Object> beforeEntries, List<Object> afterEntries, Consumer<CreativeModeTab.Builder> configurator)
        {
            return registrar.registerCreativeModeTab(configurator, name, afterEntries, beforeEntries);
        }

        /**
         * Registers and configures a creative mode tab with the given name and returns the built instance.
         * The tab is sorted by default to be after {@link CreativeModeTabs#SPAWN_EGGS}.
         *
         * @param name the name of the tab. Must be unique.
         * @param configurator the configurator which configures the builder
         * @return the configured creative mode tab
         * @see #registerCreativeModeTab(ResourceLocation, List, List, Consumer)
         */
        public CreativeModeTab registerCreativeModeTab(ResourceLocation name, Consumer<CreativeModeTab.Builder> configurator)
        {
            return registrar.registerCreativeModeTab(configurator, name,  DEFAULT_AFTER_ENTRIES, List.of());
        }
    }

    /**
     * Fired when the contents of a specific creative mode tab are being populated.
     * This event may be fired multiple times if the operator status of the local player or enabled feature flags changes.
     * <p>
     * This event is not {@linkplain Cancelable cancellable}, and does not {@linkplain HasResult have a result}.
     * <p>
     * This event is fired on the {@linkplain FMLJavaModLoadingContext#getModEventBus() mod-specific event bus},
     * only on the {@linkplain LogicalSide#CLIENT logical client}.
     */
    public static final class BuildContents extends CreativeModeTabEvent
    {
        private final CreativeModeTab tab;
        private final Set<DisplayItemsAdapter> customGenerators;

        @ApiStatus.Internal
        public BuildContents(CreativeModeTab tab, Set<DisplayItemsAdapter> customGenerators)
        {
            this.tab = tab;
            this.customGenerators = customGenerators;
        }

        /**
         * Registers the given generator.
         */
        public void register(DisplayItemsAdapter generator)
        {
            this.customGenerators.add(generator);
        }

        /**
         * Registers the given generator if the provided creative mode tab matches this event's currently populating tab.
         */
        public void register(CreativeModeTab tab, DisplayItemsAdapter generator)
        {
            if (this.tab == tab)
                this.customGenerators.add(generator);
        }

        /**
         * Registers the given generator if the provided tab name matches this event's currently populating tab's name.
         */
        public void register(ResourceLocation tabName, DisplayItemsAdapter generator)
        {
            if (this.tab == CreativeModeTabRegistry.getTab(tabName))
                this.customGenerators.add(generator);
        }

        /**
         * Adds the given item if the provided creative mode tab matches this event's currently populating tab.
         */
        public void registerSimple(CreativeModeTab tab, ItemLike item)
        {
            this.register(tab, (enabledFlags, output, permissions) -> output.accept(item));
        }

        /**
         * Adds the given items if the provided creative mode tab matches this event's currently populating tab.
         */
        public void registerSimple(CreativeModeTab tab, ItemLike... items)
        {
            this.register(tab, (enabledFlags, output, permissions) -> output.acceptAll(items));
        }

        /**
         * {@return the creative mode tab currently populating its contents}
         */
        public CreativeModeTab getTab()
        {
            return this.tab;
        }
    }

    @FunctionalInterface
    public interface DisplayItemsAdapter
    {
        void accept(FeatureFlagSet enabledFlags, CreativeModeTabPopulator populator, boolean hasPermissions);
    }

    @FunctionalInterface
    public interface CreativeModeTabPopulator
    {
        void accept(ItemStack stack, CreativeModeTab.TabVisibility visibility, ItemStack before, ItemStack after);

        default void accept(ItemStack stack, ItemStack before, ItemStack after)
        {
            accept(stack, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS, before, after);
        }

        default void accept(ItemLike stack, CreativeModeTab.TabVisibility visibility, ItemStack before, ItemStack after)
        {
            accept(new ItemStack(stack), visibility, before, after);
        }

        default void accept(ItemLike stack, ItemStack before, ItemStack after)
        {
            accept(stack, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS, before, after);
        }

        default void accept(ItemStack stack, CreativeModeTab.TabVisibility visibility)
        {
            accept(stack, visibility, ItemStack.EMPTY, ItemStack.EMPTY);
        }

        default void accept(ItemStack stack)
        {
            this.accept(stack, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
        }

        default void accept(ItemLike itemLike, CreativeModeTab.TabVisibility visibility)
        {
            this.accept(new ItemStack(itemLike), visibility);
        }

        default void accept(ItemLike itemLike)
        {
            this.accept(new ItemStack(itemLike), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
        }

        default void acceptAll(Collection<ItemStack> stacks, CreativeModeTab.TabVisibility visibility)
        {
            stacks.forEach(stack -> this.accept(stack, visibility));
        }

        default void acceptAll(Collection<ItemStack> stacks)
        {
            this.acceptAll(stacks, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
        }

        default void acceptAll(ItemLike... items)
        {
            for (ItemLike item : items)
            {
                this.accept(item);
            }
        }
    }
}
