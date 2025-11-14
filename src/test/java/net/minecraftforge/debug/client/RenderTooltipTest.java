/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.Item;
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

@GameTestNamespace("forge")
@Mod(RenderTooltipTest.MODID)
public class RenderTooltipTest extends BaseTestMod {
    public static final String MODID = "render_tooltip_test";
    private static boolean testMode = false;
    private static int shouldOpen = 0;
    // Need some randomness so this test isn't asinine.
    private static Object[] possibleChoices = BuiltInRegistries.ITEM.listElements().map(Holder::get).map(Item::getDefaultInstance).toArray();
    private static int itemStack = 0;
    private static ItemStack lastItemstackSeenInEvent = null;
    public RenderTooltipTest(FMLJavaModLoadingContext context) {
        super(context, false, false);
        AddGuiOverlayLayersEvent.BUS.addListener(event -> {
            event.getLayeredDraw().addWithCondition(rl("render_tooltip_test"), (gg, dt) -> {
                gg.setTooltipForNextFrame(Minecraft.getInstance().font, (ItemStack) possibleChoices[itemStack], 50, 50);
            }, () -> testMode) ;
        });
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
                Minecraft.getInstance().setScreen(new InventoryScreen(Minecraft.getInstance().player));
                shouldOpen = 2;
            } else if (shouldOpen == 2) {
                Minecraft.getInstance().setScreen(null);
                shouldOpen = 0;
            }
        });
        itemStack = new Random().nextInt(possibleChoices.length);
        helper.runAfterDelay(10, () -> {
            testMode = false;
            var copyOfLastSeen = lastItemstackSeenInEvent;
            boolean testPassed = lastItemstackSeenInEvent == possibleChoices[itemStack];
            lastItemstackSeenInEvent = null;
            helper.assertTrue(testPassed, "Itemstack from last tooltip was not correct. Was " + copyOfLastSeen + " expected " + possibleChoices[itemStack]);
            helper.succeed();
        });
    }
}