/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.client;

import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.ModifyOverlayLayersEvent;
import net.minecraftforge.client.gui.overlay.ForgeLayeredDraw;
import net.minecraftforge.common.extensions.IForgeGameTestHelper;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.gametest.GameTest;
import net.minecraftforge.gametest.GameTestNamespace;
import net.minecraftforge.test.BaseTestMod;

import static net.minecraftforge.client.gui.overlay.ForgeLayeredDraw.*;

@GameTestNamespace("forge")
@Mod(ModifyOverlayTest.MODID)
public class ModifyOverlayTest extends BaseTestMod {
    public static final String MODID = "modify_overlay_test";
    private static final IForgeGameTestHelper.BoolFlag was_ran_flag = new IForgeGameTestHelper.BoolFlag("layer_was_ran_flag");
    private static final IForgeGameTestHelper.IntFlag counterFlag = new IForgeGameTestHelper.IntFlag("count_flag");
    private static final IForgeGameTestHelper.IntFlag testSelector = new IForgeGameTestHelper.IntFlag("selector_flag");
    private static final ForgeLayeredDraw myStack = new ForgeLayeredDraw(name("my_stack"), true);
    private static final IForgeGameTestHelper.BoolFlag init_success_flag = new IForgeGameTestHelper.BoolFlag("init_success_flag");
    public ModifyOverlayTest(FMLJavaModLoadingContext context) {
        super(context);
        init_success_flag.set(false);
        context.getModEventBus().addListener(this::overlayTestListener);
    }

    @GameTest
    public static void test_overlay_addition(GameTestHelper helper) {
        // Test that we can add layers in general.
        helper.assertFalse(was_ran_flag.getBool(), was_ran_flag + " was already set when attempting this test. It should not have been.");
        testSelector.set(1);
        helper.runAfterDelay(1, () -> {
            testSelector.set(0);
            if (was_ran_flag.getBool()) {
                was_ran_flag.set(false);
                helper.succeed();
            } else {
                was_ran_flag.set(false); // Shouldn't need to reset this but... just in case.
                helper.fail(was_ran_flag + " was not set when it should have been.");
            }
        });
    }

    @GameTest
    public static void test_not_present_in_stack(GameTestHelper helper) {
        // Test that we can't order against non-existent layers.
        helper.assertFalse(was_ran_flag.getBool(), was_ran_flag + " was already set when attempting this test. It should not have been");
        testSelector.set(2);
        helper.runAfterDelay(1, () -> {
            testSelector.set(0); // Have to reset here since the layer isn't supposed to run.
            if (was_ran_flag.getBool()) {
                was_ran_flag.set(false);
                helper.fail(was_ran_flag + " was set when it should not have been.");
            } else {
                was_ran_flag.set(false); // Shouldn't need to reset this but... just in case.
                helper.succeed();
            }
        });
    }

    @GameTest
    public static void test_add_condition(GameTestHelper helper) {
        // Test that we can add conditions to pre-existing layers.
        helper.assertFalse(was_ran_flag.getBool(), was_ran_flag + " was already set when attempting this test, it should not have been.");
        testSelector.set(4);
        helper.runAfterDelay(1, () -> {
            testSelector.set(0);
            if (was_ran_flag.getBool()) {
                was_ran_flag.set(false);
                helper.succeed();
            } else {
                was_ran_flag.set(false);
                helper.fail(was_ran_flag + " was not set when it should have been.");
            }
        });
    }

    @GameTest
    public static void test_ordered_layers(GameTestHelper helper) {
        // Test that layers are in the correct order.
        counterFlag.set(0);
        testSelector.set(3);
        helper.runAfterDelay(1, () -> {
            testSelector.set(0);
            if (counterFlag.getInt() == 3) {
                helper.succeed();
            } else {
                helper.fail(String.format("%s was %d when it should have been 3", counterFlag, counterFlag.getInt()));
            }
        });
    }

    @GameTest
    public static void test_add_stack(GameTestHelper helper) {

    }


    private void overlayTestListener(ModifyOverlayLayersEvent event) {
        if (event.isPhase(PRE_SLEEP_PHASE)) {
            var layeredDraw = event.getLayeredDraw();
            // Test if layers may be added. If it never runs, test_add_flag will never set.
            layeredDraw.add(name("test_add"), (graphics, tracker) -> {
                if (testSelector.getInt() == 1) {
                    testSelector.set(0);
                    was_ran_flag.set(true);
                }
            });
            layeredDraw.addAbove(name("i_won_t_exist"), SCOREBOARD, (gg, tr) -> {
                // Shouldn't render because SCOREBOARD is not in PRE_SLEEP_PHASE. Also should not reset selector flag, same reason.
                if (testSelector.getInt() == 2) {
                    testSelector.set(0);
                    was_ran_flag.set(true);
                }
            });
            // Test if layers may be ordered against other layers.
            // Side effect of impl. is that we must add the middle node before we can try to order against it.
            layeredDraw.add(name("node_b"), (gg, tr) -> {
                // a -> [b] -> c
                if (testSelector.getInt() == 3 && counterFlag.getInt() == 2) {
                    counterFlag.set(counterFlag.getInt() + 1);
                }
            });
            layeredDraw.addAbove(name("node_c"), name("node_b"), (gg, tr) -> {
                // a -> b -> [c]
                if (testSelector.getInt() == 3 && counterFlag.getInt() == 1) {
                    testSelector.set(0);
                    counterFlag.set(counterFlag.getInt() + 1);
                }
            });
            layeredDraw.addBelow(name("node_a"), name("node_b"), (gg, tr) -> {
                // [a] -> b -> c
                if (testSelector.getInt() == 3 && counterFlag.getInt() == 3) {
                    counterFlag.set(counterFlag.getInt()+1);
                }
            });

            // Test if layers may have conditions attached.
            layeredDraw.addConditionTo(POTION_EFFECTS, () -> {
                if(testSelector.getInt() == 4) {
                    was_ran_flag.set(true);
                    testSelector.set(0);
                    return true;
                } else {
                    return false;
                }
            });
        }
        if (event.isPhase(COMBINE_PHASE)) { // COMBINE_PHASE probably needs a new name so ppl don't get confused.
            myStack.computeOrder(); // myStack may be edited, so we fire a new event.
            var myStackLayer = myStack.asLayer(); // convert to layer
            event.getLayeredDraw().addWithCondition(name("my_stack_as_layer"), (gg, tr) -> {
                was_ran_flag.set(true);
                testSelector.set(0);
            }, () -> testSelector.getInt() == 5);
        }
        if (event.isPhase(name("my_stack"))) {
            // Check that it worked.
            init_success_flag.set(true);
        }
    }

    private static ResourceLocation name(String name) {
        return ResourceLocation.fromNamespaceAndPath(MODID, name);
    }
}
