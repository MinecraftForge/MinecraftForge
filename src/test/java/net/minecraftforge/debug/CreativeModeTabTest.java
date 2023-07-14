/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug;

import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTab.TabVisibility;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegisterEvent;

@Mod(CreativeModeTabTest.MOD_ID)
public class CreativeModeTabTest
{
    public static final String MOD_ID = "creative_mode_tab_test";
    private static final boolean ENABLED = true;

    private static final ResourceKey<CreativeModeTab> LOGS = ResourceKey.create(Registries.CREATIVE_MODE_TAB, new ResourceLocation(MOD_ID, "logs"));
    private static final ResourceKey<CreativeModeTab> STONE = ResourceKey.create(Registries.CREATIVE_MODE_TAB, new ResourceLocation(MOD_ID, "stone"));

    public CreativeModeTabTest()
    {
        if (!ENABLED)
            return;

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        modEventBus.addListener(CreativeModeTabTest::onCreativeModeTabRegister);
        modEventBus.addListener(CreativeModeTabTest::onCreativeModeTabBuildContents);
    }

    private static void onCreativeModeTabRegister(RegisterEvent event)
    {
        event.register(Registries.CREATIVE_MODE_TAB, helper -> {
            helper.register(LOGS, CreativeModeTab.builder().icon(() -> new ItemStack(Blocks.ACACIA_LOG))
                    .title(Component.literal("Logs"))
                    .withLabelColor(0x00FF00)
                    .displayItems((params, output) -> {
                        output.accept(new ItemStack(Blocks.ACACIA_LOG));
                        output.accept(new ItemStack(Blocks.BIRCH_LOG));
                        output.accept(new ItemStack(Blocks.DARK_OAK_LOG));
                        output.accept(new ItemStack(Blocks.JUNGLE_LOG));
                        output.accept(new ItemStack(Blocks.OAK_LOG));
                        output.accept(new ItemStack(Blocks.SPRUCE_LOG));
                    })
                    .build());
            helper.register(STONE, CreativeModeTab.builder().icon(() -> new ItemStack(Blocks.STONE))
                    .title(Component.literal("Stone"))
                    .withLabelColor(0x0000FF)
                    .displayItems((params, output) -> {
                        output.accept(new ItemStack(Blocks.STONE));
                        output.accept(new ItemStack(Blocks.GRANITE));
                        output.accept(new ItemStack(Blocks.DIORITE));
                        output.accept(new ItemStack(Blocks.ANDESITE));
                    })
                    .withTabsAfter(CreativeModeTabs.BUILDING_BLOCKS)
                    .build());

            helper.register(new ResourceLocation(MOD_ID, "colors"), CreativeModeTab.builder().title(Component.literal("Colors"))
                    .displayItems((params, output) ->
                    {
                        for (DyeColor color : DyeColor.values())
                        {
                            output.accept(DyeItem.byColor(color));
                        }
                    })
                    .withTabFactory(CreativeModeColorTab::new)
                    .withTabsBefore(CreativeModeTabs.COLORED_BLOCKS)
                    .build()
            );

            List<Block> blocks = List.of(Blocks.GRANITE, Blocks.DIORITE, Blocks.ANDESITE, Blocks.COBBLESTONE);
            for (int i = 0; i < blocks.size(); i++)
            {
                Block block = blocks.get(i);
                helper.register(new ResourceLocation(MOD_ID, "dummy" + i), CreativeModeTab.builder()
                      .title(Component.literal("Dummy " + i))
                      .icon(() -> new ItemStack(block))
                      .displayItems((params, output) -> output.accept(block))
                      .build()
                );
            }

            final ResourceLocation custom_tabs_image = new ResourceLocation(MOD_ID, "textures/gui/container/creative_inventory/custom_tabs.png");
            helper.register(new ResourceLocation(MOD_ID, "with_tabs_image"), CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.with_tabs_image"))
                    .icon(() -> new ItemStack(Blocks.BRICKS))
                    .displayItems((params, output) -> output.accept(Blocks.BRICKS))
                    .withTabsImage(custom_tabs_image)
                    .build());
        });
    }

    private static ItemStack i(ItemLike item) { return new ItemStack(item); }

    private static void onCreativeModeTabBuildContents(BuildCreativeModeTabContentsEvent event)
    {
        var entries = event.getEntries();
        var vis = TabVisibility.PARENT_AND_SEARCH_TABS;
        if (event.getTabKey() == LOGS)
        {
            entries.putAfter(i(Blocks.ACACIA_LOG),   i(Blocks.STRIPPED_ACACIA_LOG),   vis);
            entries.putAfter(i(Blocks.BIRCH_LOG),    i(Blocks.STRIPPED_BIRCH_LOG),    vis);
            entries.putAfter(i(Blocks.DARK_OAK_LOG), i(Blocks.STRIPPED_DARK_OAK_LOG), vis);
            entries.putAfter(i(Blocks.JUNGLE_LOG),   i(Blocks.STRIPPED_JUNGLE_LOG),   vis);
            entries.putAfter(i(Blocks.OAK_LOG),      i(Blocks.STRIPPED_OAK_LOG),      vis);
            entries.putAfter(i(Blocks.SPRUCE_LOG),   i(Blocks.STRIPPED_SPRUCE_LOG),   vis);
        }

        if (event.getTabKey() == STONE)
        {
            entries.putBefore(i(Blocks.STONE),    i(Blocks.SMOOTH_STONE),      vis);
            entries.putBefore(i(Blocks.GRANITE),  i(Blocks.POLISHED_GRANITE),  vis);
            entries.putBefore(i(Blocks.DIORITE),  i(Blocks.POLISHED_DIORITE),  vis);
            entries.putBefore(i(Blocks.ANDESITE), i(Blocks.POLISHED_ANDESITE), vis);
        }
    }

    private static class CreativeModeColorTab extends CreativeModeTab
    {
        private final ItemStack[] iconItems;

        public CreativeModeColorTab(CreativeModeTab.Builder builder)
        {
            super(builder);

            DyeColor[] colors = DyeColor.values();
            iconItems = new ItemStack[colors.length];
            for (int i = 0; i < colors.length; i++)
            {
                iconItems[i] = new ItemStack(DyeItem.byColor(colors[i]));
            }
        }

        @Override
        public ItemStack getIconItem()
        {
            int idx = (int)(System.currentTimeMillis() / 1200) % iconItems.length;
            return iconItems[idx];
        }
    }
}
