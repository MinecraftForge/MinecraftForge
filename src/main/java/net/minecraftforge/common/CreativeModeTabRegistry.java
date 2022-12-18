/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.graph.ElementOrder;
import com.google.common.graph.GraphBuilder;
import com.google.common.graph.MutableGraph;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.CreativeModeTabSearchRegistry;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.fml.ModLoader;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.toposort.TopologicalSort;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public final class CreativeModeTabRegistry
{
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final ResourceLocation CREATIVE_MODE_TAB_ORDERING_JSON = new ResourceLocation("forge", "creative_mode_tab_ordering.json");
    private static final List<CreativeModeTab> SORTED_TABS = new ArrayList<>();
    private static final List<CreativeModeTab> SORTED_TABS_VIEW = Collections.unmodifiableList(SORTED_TABS);

    /**
     * {@return an unmodifiable view of the sorted list of creative mode tabs in ascending order}
     */
    public static List<CreativeModeTab> getSortedCreativeModeTabs()
    {
        return SORTED_TABS_VIEW;
    }

    /**
     * {@return the {@linkplain CreativeModeTab} with the given name, or null}
     *
     * @param name the name to look up
     */
    @Nullable
    public static CreativeModeTab getTab(ResourceLocation name)
    {
        return creativeModeTabs.get(name);
    }

    /**
     * {@return the name of the given {@linkplain CreativeModeTab}, or null}
     *
     * @param tab the tab to look up
     */
    @Nullable
    public static ResourceLocation getName(CreativeModeTab tab)
    {
        return creativeModeTabs.inverse().get(tab);
    }

    private static void processCreativeModeTab(CreativeModeTab creativeModeTab, ResourceLocation name, List<Object> afterEntries, List<Object> beforeEntries)
    {
        creativeModeTabs.put(name, creativeModeTab);
        for (Object after : afterEntries)
        {
            ResourceLocation other = getCreativeModeTabName(after);
            edges.put(other, name);
        }
        for (Object before : beforeEntries)
        {
            ResourceLocation other = getCreativeModeTabName(before);
            edges.put(name, other);
        }
    }

    private static ResourceLocation getCreativeModeTabName(Object entry)
    {
        if (entry instanceof String s)
            return new ResourceLocation(s);
        if (entry instanceof ResourceLocation rl)
            return rl;
        if (entry instanceof CreativeModeTab t)
            return Objects.requireNonNull(getName(t), "Can't have sorting dependencies for creative mode tabs not registered in the CreativeModeTabSortingRegistry");
        throw new IllegalStateException("Invalid object type passed into the CreativeModeTab dependencies " + entry.getClass());
    }

    private static final BiMap<ResourceLocation, CreativeModeTab> creativeModeTabs = HashBiMap.create();
    private static final BiMap<ResourceLocation, CreativeModeTab> vanillaCreativeModeTabs = HashBiMap.create();
    private static final Multimap<ResourceLocation, ResourceLocation> edges = HashMultimap.create();
    private static final Multimap<ResourceLocation, ResourceLocation> vanillaEdges = HashMultimap.create();

    static
    {
        final ResourceLocation buildingBlocks = new ResourceLocation("building_blocks");
        final ResourceLocation coloredBlocks = new ResourceLocation("colored_blocks");
        final ResourceLocation naturalBlocks = new ResourceLocation("natural_blocks");
        final ResourceLocation functionalBlocks = new ResourceLocation("functional_blocks");
        final ResourceLocation redstoneBlocks = new ResourceLocation("redstone_blocks");
        final ResourceLocation toolsAndUtilities = new ResourceLocation("tools_and_utilities");
        final ResourceLocation combat = new ResourceLocation("combat");
        final ResourceLocation foodAndDrinks = new ResourceLocation("food_and_drinks");
        final ResourceLocation ingredients = new ResourceLocation("ingredients");
        final ResourceLocation spawnEggs = new ResourceLocation("spawn_eggs");

        processCreativeModeTab(CreativeModeTabs.BUILDING_BLOCKS, buildingBlocks, List.of(), List.of());
        processCreativeModeTab(CreativeModeTabs.COLORED_BLOCKS, coloredBlocks, List.of(buildingBlocks), List.of(naturalBlocks));
        processCreativeModeTab(CreativeModeTabs.NATURAL_BLOCKS, naturalBlocks, List.of(coloredBlocks), List.of(functionalBlocks));
        processCreativeModeTab(CreativeModeTabs.FUNCTIONAL_BLOCKS, functionalBlocks, List.of(naturalBlocks), List.of(redstoneBlocks));
        processCreativeModeTab(CreativeModeTabs.REDSTONE_BLOCKS, redstoneBlocks, List.of(functionalBlocks), List.of(toolsAndUtilities));
        processCreativeModeTab(CreativeModeTabs.TOOLS_AND_UTILITIES, toolsAndUtilities, List.of(redstoneBlocks), List.of(combat));
        processCreativeModeTab(CreativeModeTabs.COMBAT, combat, List.of(toolsAndUtilities), List.of(foodAndDrinks));
        processCreativeModeTab(CreativeModeTabs.FOOD_AND_DRINKS, foodAndDrinks, List.of(combat), List.of(ingredients));
        processCreativeModeTab(CreativeModeTabs.INGREDIENTS, ingredients, List.of(foodAndDrinks), List.of(spawnEggs));
        processCreativeModeTab(CreativeModeTabs.SPAWN_EGGS, spawnEggs, List.of(ingredients), List.of());

        vanillaEdges.putAll(edges);
        vanillaCreativeModeTabs.putAll(creativeModeTabs);
    }

    static PreparableReloadListener getReloadListener()
    {
        return new SimplePreparableReloadListener<JsonObject>()
        {
            final Gson gson = new GsonBuilder().create();

            @NotNull
            @Override
            protected JsonObject prepare(@NotNull ResourceManager resourceManager, ProfilerFiller profiler)
            {
                Optional<Resource> res = resourceManager.getResource(CREATIVE_MODE_TAB_ORDERING_JSON);
                if (res.isEmpty())
                    return new JsonObject();

                try (Reader reader = res.get().openAsReader())
                {
                    return this.gson.fromJson(reader, JsonObject.class);
                }
                catch (IOException e)
                {
                    LOGGER.error("Could not read CreativeModeTab sorting file " + CREATIVE_MODE_TAB_ORDERING_JSON, e);
                    return new JsonObject();
                }
            }

            @Override
            protected void apply(@NotNull JsonObject data, @NotNull ResourceManager resourceManager, ProfilerFiller p)
            {
                try
                {
                    if (data.size() > 0)
                    {
                        JsonArray order = GsonHelper.getAsJsonArray(data, "order");
                        List<CreativeModeTab> customOrder = new ArrayList<>();
                        for (JsonElement entry : order)
                        {
                            ResourceLocation id = new ResourceLocation(entry.getAsString());
                            CreativeModeTab CreativeModeTab = getTab(id);
                            if (CreativeModeTab == null)
                                throw new IllegalStateException("CreativeModeTab not found with name " + id);
                            customOrder.add(CreativeModeTab);
                        }

                        List<CreativeModeTab> missingCreativeModeTabs = creativeModeTabs.values().stream().filter(CreativeModeTab -> !customOrder.contains(CreativeModeTab)).toList();
                        if (!missingCreativeModeTabs.isEmpty())
                            throw new IllegalStateException("CreativeModeTabs missing from the ordered list: " + missingCreativeModeTabs.stream()
                                    .map(CreativeModeTab -> Objects.toString(CreativeModeTabRegistry.getName(CreativeModeTab))).collect(Collectors.joining(", ")));

                        setCreativeModeTabOrder(customOrder);
                        return;
                    }
                }
                catch (Exception e)
                {
                    LOGGER.error("Error parsing CreativeModeTab sorting file " + CREATIVE_MODE_TAB_ORDERING_JSON, e);
                }

                recalculateItemCreativeModeTabs();
            }
        };
    }

    @SuppressWarnings("UnstableApiUsage")
    private static void recalculateItemCreativeModeTabs()
    {
        final MutableGraph<CreativeModeTab> graph = GraphBuilder.directed().nodeOrder(ElementOrder.<CreativeModeTab>insertion()).build();

        for (CreativeModeTab CreativeModeTab : creativeModeTabs.values())
        {
            graph.addNode(CreativeModeTab);
        }
        edges.forEach((key, value) -> {
            if (creativeModeTabs.containsKey(key) && creativeModeTabs.containsKey(value))
                graph.putEdge(creativeModeTabs.get(key), creativeModeTabs.get(value));
        });
        List<CreativeModeTab> tierList = TopologicalSort.topologicalSort(graph, null);

        setCreativeModeTabOrder(tierList);
    }

    private static void setCreativeModeTabOrder(List<CreativeModeTab> tierList)
    {
        runInServerThreadIfPossible(hasServer -> {
            SORTED_TABS.clear();
            SORTED_TABS.addAll(tierList);
        });
    }

    private static void runInServerThreadIfPossible(BooleanConsumer runnable)
    {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server != null)
            server.execute(() -> runnable.accept(true));
        else
            runnable.accept(false);
    }

    @ApiStatus.Internal
    public static void fireCollectionEvent()
    {
        edges.clear();
        edges.putAll(vanillaEdges);

        creativeModeTabs.clear();
        creativeModeTabs.putAll(vanillaCreativeModeTabs);

        ModLoader.get().postEvent(new CreativeModeTabEvent.Register(CreativeModeTabRegistry::registerCreativeModeTab));

        recalculateItemCreativeModeTabs();

        if (FMLEnvironment.dist == Dist.CLIENT && !FMLLoader.getLaunchHandler().isData())
            CreativeModeTabSearchRegistry.createSearchTrees();
    }

    @ApiStatus.Internal
    private static CreativeModeTab registerCreativeModeTab(Consumer<CreativeModeTab.Builder> configurator, ResourceLocation name, List<Object> afterEntries, List<Object> beforeEntries)
    {
        if (creativeModeTabs.containsKey(name))
            throw new IllegalStateException("Duplicate creative mode tab with name: " + name);

        // The initial values for the builder do not matter
        CreativeModeTab.Builder builder = new CreativeModeTab.Builder(CreativeModeTab.Row.TOP, 0);
        configurator.accept(builder);
        CreativeModeTab creativeModeTab = builder.build();

        if (creativeModeTab.isAlignedRight())
            throw new IllegalStateException("CreativeModeTab " + name + " is aligned right, this is not supported!");

        processCreativeModeTab(creativeModeTab, name, afterEntries, beforeEntries);

        return creativeModeTab;
    }
}
