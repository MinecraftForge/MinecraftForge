/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.AddGuiOverlayLayersEvent;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.gametest.GameTest;
import net.minecraftforge.gametest.GameTestNamespace;
import net.minecraftforge.test.BaseTestMod;

import java.util.Random;

import org.jspecify.annotations.Nullable;

@GameTestNamespace("forge")
@Mod(RenderTooltipTest.MODID)
public class RenderTooltipTest extends BaseTestMod {
    public static final String MODID = "render_tooltip_test";
    private static boolean testMode = false;
    private static int shouldOpen = 0;
    private static ItemStack lastItemstackSeenInEvent = null;
    private static @Nullable ItemStack itemStack;

    public RenderTooltipTest(FMLJavaModLoadingContext context) {
        super(context, false, false);
        AddGuiOverlayLayersEvent.BUS.addListener(event -> {
            event.getLayeredDraw().addWithCondition(rl("render_tooltip_test"), (gg, dt) -> {
                if (itemStack != null)
                    gg.setTooltipForNextFrame(Minecraft.getInstance().font, itemStack, 50, 50);
            }, () -> testMode) ;
        });
    }

    // Need some randomness so this test isn't asinine.
    private static void pickRandomItem() {
        var keys = BuiltInRegistries.ITEM.keySet();
        var idx = new Random().nextInt(keys.size());
        var item = BuiltInRegistries.ITEM.listElements().skip(idx).findFirst().get();
        itemStack = item.get().getDefaultInstance();
    }

    @SuppressWarnings("all")
    @GameTest
    public static void stack_present_in_pre(GameTestHelper helper) {
        testMode = true;
        shouldOpen = 1;
        helper.addEventListener(RenderTooltipEvent.Pre.BUS, event -> {
            lastItemstackSeenInEvent = event.getItemStack();
        });

        helper.addRecordListener(TickEvent.RenderTickEvent.Pre.BUS, (event) -> {
            if (shouldOpen == 1) {
                Minecraft.getInstance().gui.setScreen(new InventoryScreen(Minecraft.getInstance().player));
                shouldOpen = 2;
            } else if (shouldOpen == 2) {
                Minecraft.getInstance().gui.setScreen(null);
                shouldOpen = 0;
            }
        });

        pickRandomItem();

        helper.runAfterDelay(10, () -> {
            testMode = false;
            var copyOfLastSeen = lastItemstackSeenInEvent;
            boolean testPassed = lastItemstackSeenInEvent == itemStack;
            lastItemstackSeenInEvent = null;
            helper.assertTrue(testPassed, "Itemstack from last tooltip was not correct. Was " + copyOfLastSeen + " expected " + itemStack);
            helper.succeed();
        });
    }
}