/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug;

import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(CreativeModeTabTest.MOD_ID)
public class CreativeModeTabTest
{
    public static final String MOD_ID = "creative_mode_tab_test";
    private static final boolean ENABLED = true;

    private static CreativeModeTab LOGS;
    private static CreativeModeTab STONE;

    public CreativeModeTabTest()
    {
        if (!ENABLED)
            return;

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        modEventBus.addListener(CreativeModeTabTest::onCreativeModeTabRegister);
        modEventBus.addListener(CreativeModeTabTest::onCreativeModeTabBuildContents);
    }

    private static void onCreativeModeTabRegister(CreativeModeTabEvent.Register event)
    {
        LOGS = event.registerCreativeModeTab(new ResourceLocation(MOD_ID, "logs"), builder -> builder.icon(() -> new ItemStack(Blocks.ACACIA_LOG))
                .title(Component.literal("Logs"))
                .withLabelColor(0x00FF00)
                .displayItems((features, output, hasPermissions) -> {
                    output.accept(new ItemStack(Blocks.ACACIA_LOG));
                    output.accept(new ItemStack(Blocks.BIRCH_LOG));
                    output.accept(new ItemStack(Blocks.DARK_OAK_LOG));
                    output.accept(new ItemStack(Blocks.JUNGLE_LOG));
                    output.accept(new ItemStack(Blocks.OAK_LOG));
                    output.accept(new ItemStack(Blocks.SPRUCE_LOG));
                }));

        STONE = event.registerCreativeModeTab(new ResourceLocation(MOD_ID, "stone"), List.of(CreativeModeTabs.BUILDING_BLOCKS), List.of(), builder -> builder.icon(() -> new ItemStack(Blocks.STONE))
                .title(Component.literal("Stone"))
                .withLabelColor(0x0000FF)
                .displayItems((features, output, hasPermissions) -> {
                    output.accept(new ItemStack(Blocks.STONE));
                    output.accept(new ItemStack(Blocks.GRANITE));
                    output.accept(new ItemStack(Blocks.DIORITE));
                    output.accept(new ItemStack(Blocks.ANDESITE));
                }));
    }

    private static void onCreativeModeTabBuildContents(CreativeModeTabEvent.BuildContents event)
    {
        if (event.getTab() == LOGS)
        {
            event.register((flags, builder, hasPermissions) -> {
                builder.accept(new ItemStack(Blocks.STRIPPED_ACACIA_LOG), ItemStack.EMPTY, new ItemStack(Blocks.ACACIA_LOG));
                builder.accept(new ItemStack(Blocks.STRIPPED_BIRCH_LOG), ItemStack.EMPTY, new ItemStack(Blocks.BIRCH_LOG));
                builder.accept(new ItemStack(Blocks.STRIPPED_DARK_OAK_LOG), ItemStack.EMPTY, new ItemStack(Blocks.DARK_OAK_LOG));
                builder.accept(new ItemStack(Blocks.STRIPPED_JUNGLE_LOG), ItemStack.EMPTY, new ItemStack(Blocks.JUNGLE_LOG));
                builder.accept(new ItemStack(Blocks.STRIPPED_OAK_LOG), ItemStack.EMPTY, new ItemStack(Blocks.OAK_LOG));
                builder.accept(new ItemStack(Blocks.STRIPPED_SPRUCE_LOG), ItemStack.EMPTY, new ItemStack(Blocks.SPRUCE_LOG));
            });
        }

        if (event.getTab() == STONE)
        {
            event.register((flags, builder, hasPermissions) -> {
                builder.accept(new ItemStack(Blocks.SMOOTH_STONE), new ItemStack(Blocks.GRANITE), new ItemStack(Blocks.STONE));
                builder.accept(new ItemStack(Blocks.POLISHED_GRANITE), new ItemStack(Blocks.DIORITE), new ItemStack(Blocks.GRANITE));
                builder.accept(new ItemStack(Blocks.POLISHED_DIORITE), new ItemStack(Blocks.ANDESITE), new ItemStack(Blocks.DIORITE));
                builder.accept(new ItemStack(Blocks.POLISHED_ANDESITE), ItemStack.EMPTY, new ItemStack(Blocks.ANDESITE));
            });
        }
    }
}
