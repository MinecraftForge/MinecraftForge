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
    private static final int DISABLED_MODE = 0;
    private static final int ADD_MODE = 1;
    private static final int NOT_ADDED_MODE = 2;
    private static final int ORDER_MODE = 3;
    private static final int ADD_CONDITION_MODE = 4;
    private static final int STACK_INSERT_MODE = 5;
    private static final int STACK_INSERT_CONDITION_MODE = 6;

    private static final IForgeGameTestHelper.BoolFlag was_ran_flag = new IForgeGameTestHelper.BoolFlag("layer_was_ran_flag");
    private static final IForgeGameTestHelper.IntFlag counterFlag = new IForgeGameTestHelper.IntFlag("count_flag");
    private static final IForgeGameTestHelper.IntFlag testSelector = new IForgeGameTestHelper.IntFlag("selector_flag");
    private static final ForgeLayeredDraw myStack = new ForgeLayeredDraw(name("my_stack"), true);
    private static final ForgeLayeredDraw myUneditableStack = new ForgeLayeredDraw(name("my_uneditable_stack"), false);
    private static final IForgeGameTestHelper.BoolFlag init_success_flag = new IForgeGameTestHelper.BoolFlag("init_success_flag");
    private static final IForgeGameTestHelper.BoolFlag init_fail_flag = new IForgeGameTestHelper.BoolFlag("init_fail_flag");


    public ModifyOverlayTest(FMLJavaModLoadingContext context) {
        super(context);
        init_success_flag.set(false);
        init_fail_flag.set(false);
        context.getModEventBus().addListener(this::overlayTestListener);
    }

    @GameTest
    public static void test_overlay_addition(GameTestHelper helper) {
        // Test that we can add layers in general.
        helper.assertFalse(was_ran_flag.getBool(), "was_ran_flag was already set when attempting this test. It should not have been.");
        testSelector.set(ADD_MODE);
        helper.runAfterDelay(1, () -> {
            testSelector.set(DISABLED_MODE);
            if (was_ran_flag.getBool()) {
                was_ran_flag.set(false);
                helper.succeed();
            } else {
                was_ran_flag.set(false); // Shouldn't need to reset this but... just in case.
                helper.fail("was_ran_flag was not set when it should have been.");
            }
        });
    }

    @GameTest
    public static void test_not_present_in_stack(GameTestHelper helper) {
        // Test that we can't order against non-existent layers.
        helper.assertFalse(was_ran_flag.getBool(), "was_ran_flag was already set when attempting this test. It should not have been");
        testSelector.set(NOT_ADDED_MODE);
        helper.runAfterDelay(1, () -> {
            testSelector.set(DISABLED_MODE); // Have to reset here since the layer isn't supposed to run.
            if (was_ran_flag.getBool()) {
                was_ran_flag.set(false);
                helper.fail("was_ran_flag was set when it should not have been.");
            } else {
                was_ran_flag.set(false); // Shouldn't need to reset this but... just in case.
                helper.succeed();
            }
        });
    }

    @GameTest
    public static void test_add_condition(GameTestHelper helper) {
        // Test that we can add conditions to pre-existing layers.
        helper.assertFalse(was_ran_flag.getBool(), "was_ran_flag was already set when attempting this test, it should not have been.");
        testSelector.set(ADD_CONDITION_MODE);
        helper.runAfterDelay(1, () -> {
            testSelector.set(DISABLED_MODE);
            if (was_ran_flag.getBool()) {
                was_ran_flag.set(false);
                helper.succeed();
            } else {
                was_ran_flag.set(false);
                helper.fail("was_ran_flag was not set when it should have been.");
            }
        });
    }

    @GameTest
    public static void test_ordered_layers(GameTestHelper helper) {
        // Test that layers are in the correct order.
        counterFlag.set(0);
        testSelector.set(ORDER_MODE);
        helper.runAfterDelay(1, () -> {
            testSelector.set(DISABLED_MODE);
            if (counterFlag.getInt() == 3) {
                was_ran_flag.set(false);
                helper.succeed();
            } else {
                was_ran_flag.set(false);
                helper.fail(String.format("Counter was %d when it should have been 3", counterFlag.getInt()));
            }
        });
    }

    @GameTest
    public static void test_editable_status(GameTestHelper helper) {
        helper.assertTrue(init_success_flag.getBool(), "Init flag wasn't set, editable ForgeLayeredDraw can't have been added.");
        helper.assertFalse(init_fail_flag.getBool(), "Init flag was set, an uneditable ForgeLayeredDraw was actually editable.");
        helper.succeed();
    }

    @GameTest
    public static void test_full_stack_insertion(GameTestHelper helper) {
        helper.assertFalse(was_ran_flag.getBool(), "was_ran_flag was already set when attempting this test, it should not have been.");
        testSelector.set(STACK_INSERT_MODE);
        helper.runAfterDelay(1, () -> {
            testSelector.set(DISABLED_MODE);
            if (was_ran_flag.getBool()) {
                was_ran_flag.set(false);
                helper.succeed();
            } else {
                was_ran_flag.set(false);
                helper.fail("was_ran_flag was not set when it should have been.");
            }
        });
    }

    @GameTest
    public static void test_full_stack_condition(GameTestHelper helper) {
        helper.assertFalse(was_ran_flag.getBool(), "was_ran_flag was already set when attempting this test, it should not have been.");
        testSelector.set(STACK_INSERT_CONDITION_MODE);
        helper.runAfterDelay(1, () -> {
            testSelector.set(DISABLED_MODE);
            if (was_ran_flag.getBool()) {
                was_ran_flag.set(false);
                helper.succeed();
            } else {
                was_ran_flag.set(false);
                helper.fail("was_ran_flag was not set when it should have been.");
            }
        });
    }


    private void overlayTestListener(ModifyOverlayLayersEvent event) {
        if (event.isPhase(PRE_SLEEP_PHASE)) {
            var layeredDraw = event.getLayeredDraw();
            // Test if layers may be added. If it never runs, test_add_flag will never set.
            layeredDraw.add(name("test_add"), (graphics, tracker) -> {
                if (isMode(ADD_MODE)) {
                    testSelector.set(DISABLED_MODE);
                    was_ran_flag.set(true);
                }
            });
            layeredDraw.addAbove(name("i_won_t_exist"), SCOREBOARD, (gg, tr) -> {
                // Shouldn't render because SCOREBOARD is not in PRE_SLEEP_PHASE. Also should not reset selector flag, same reason.
                if (isMode(NOT_ADDED_MODE)) {
                    testSelector.set(DISABLED_MODE);
                    was_ran_flag.set(true);
                }
            });
            // Test if layers may be ordered against other layers.
            // Side effect of impl. is that we must add the middle node before we can try to order against it.
            layeredDraw.add(name("node_b"), (gg, tr) -> {
                // a -> [b] -> c
                if (isMode(ORDER_MODE)) {
                    if (counterFlag.getInt() == 1) {
                        counterFlag.set(counterFlag.getInt() + 1);
                    } else {
                        was_ran_flag.set(true);
                    }

                }
            });
            layeredDraw.addAbove(name("node_c"), name("node_b"), (gg, tr) -> {
                // a -> b -> [c]
                if (isMode(ORDER_MODE)) {
                    testSelector.set(DISABLED_MODE);
                    if (counterFlag.getInt() == 2) {
                        counterFlag.set(counterFlag.getInt() + 1);
                    } else {
                        was_ran_flag.set(true);
                    }
                }
            });
            layeredDraw.addBelow(name("node_a"), name("node_b"), (gg, tr) -> {
                // [a] -> b -> c
                if (isMode(ORDER_MODE)) {
                    if (counterFlag.getInt() == 0) {
                        counterFlag.set(counterFlag.getInt()+1);
                    } else {
                        was_ran_flag.set(true);
                    }
                }
            });

            // Test if layers may have conditions attached.
            layeredDraw.addConditionTo(POTION_EFFECTS, () -> {
                if(isMode(ADD_CONDITION_MODE)) {
                    was_ran_flag.set(true);
                    testSelector.set(DISABLED_MODE);
                    return true;
                } else {
                    return false;
                }
            });
        }
        if (event.isPhase(COMBINE_PHASE)) { // COMBINE_PHASE probably needs a new name so ppl don't get confused.
            // myStack may be edited, so this will fire a new event.
            var myStackLayer = myStack.computeOrder().asLayer();

            // myUneditableStack is not an editable stack, so we need to add to it now
            myUneditableStack.add(name("my_uneditable_inner_layer"), (gg, tr) -> {
                was_ran_flag.set(true);
                testSelector.set(DISABLED_MODE);
            });
            myUneditableStack.computeOrder(); // compute and add to COMBINE_PHASE.
            // Demonstrates that entire stacks can have conditions once converted to layers.
            event.getLayeredDraw()
                    .addWithCondition(name("my_uneditable_stack"),
                    myUneditableStack.asLayer(),
                    () -> isMode(STACK_INSERT_CONDITION_MODE))
                    .add(myStackLayer);
            // IMPORTANT: Structure of COMBINE_PHASE is now PRE -> sleep_overlay -> POST -> myUneditStack -> myStack
        }
        if (event.isPhase(name("my_stack"))) {
            // Check that our new stack is editable (fired the event)
            init_success_flag.set(true);
            event.getLayeredDraw().add(name("my_stack_as_layer"), (gg, tr) -> {
                if (isMode(STACK_INSERT_MODE)) { // and that the layers we add are indeed visible.
                    was_ran_flag.set(true);
                    testSelector.set(DISABLED_MODE);
                }
            });
        }
        if (event.isPhase(name("my_uneditable_stack"))) {
            // Shouldn't run since this stack isn't editable outside our initialization.
            init_fail_flag.set(true);
        }
    }

    private static ResourceLocation name(String name) {
        return ResourceLocation.fromNamespaceAndPath(MODID, name);
    }

    private static boolean isMode(int target) {
        return testSelector.getInt() == target;
    }
}
