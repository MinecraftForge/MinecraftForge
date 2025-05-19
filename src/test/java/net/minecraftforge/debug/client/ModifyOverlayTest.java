/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.client;

import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.ModifyOverlayLayersEvent;
import net.minecraftforge.client.gui.overlay.ForgeLayeredDraw;
import net.minecraftforge.common.extensions.IForgeGameTestHelper;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.gametest.GameTest;
import net.minecraftforge.gametest.GameTestNamespace;
import net.minecraftforge.test.BaseTestMod;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.minecraftforge.client.gui.overlay.ForgeLayeredDraw.*;

@GameTestNamespace("forge")
@Mod(ModifyOverlayTest.MODID)
public class ModifyOverlayTest extends BaseTestMod {
    public static final String MODID = "modify_overlay_test";

    private static final ResourceLocation myEditableStackPhase = name("my_stack");
    private static final ForgeLayeredDraw myStack = new ForgeLayeredDraw(myEditableStackPhase, true);
    private static final ResourceLocation myUneditableStackPhase = name("my_uneditable_stack");
    private static final ForgeLayeredDraw myUneditableStack = new ForgeLayeredDraw(myUneditableStackPhase, false);

    private static final Layer notAddedLayer = (gg,tr) -> {};
    private static final Layer layerA = (gg,tr) -> {};
    private static final ResourceLocation layerAName = name("layer_a");
    private static final Layer layerB = (gg,tr) -> {};
    private static final ResourceLocation layerBName = name("layer_b");
    private static final Layer layerC = (gg,tr) -> {};
    private static final ResourceLocation layerCName = name("layer_c");

    private static Layer myStackLayer = null;
    private static Layer myOtherStackLayer = null;


    private static final IForgeGameTestHelper.BoolFlag detectConditionFlag = new IForgeGameTestHelper.BoolFlag("det_cond_flag");
    private static final IForgeGameTestHelper.BoolFlag detectConditionStackFlag = new IForgeGameTestHelper.BoolFlag("det_cond_stack_flag");
    private static final IForgeGameTestHelper.BoolFlag enableConditionFlag = new IForgeGameTestHelper.BoolFlag("en_cond_flag");
    private static final IForgeGameTestHelper.BoolFlag enableConditionStackFlag = new IForgeGameTestHelper.BoolFlag("en_cond_stack_flag");

    private static final Map<ResourceLocation, ForgeLayeredDraw> drawStacks = new HashMap<>();

    public ModifyOverlayTest(FMLJavaModLoadingContext context) {
        super(context);
        context.getModEventBus().addListener(this::overlayTestListener);
    }

    @GameTest
    public static void overlay_addition(GameTestHelper helper) {
        ForgeLayeredDraw stack = drawStacks.get(PRE_SLEEP_PHASE);
        IForgeGameTestHelper.BoolFlag insertFlag = helper.boolFlag("test_overlay_addition_flag");
        Layer renderCode = (gg, tr) -> {
            insertFlag.set(true);
        };
        List<Layer> internalLayersList = null;
        int priorSize = 0;
        try {
            internalLayersList = getInternalLayersList(stack);
            priorSize = internalLayersList.size();
        } catch (Exception e) {
            helper.fail("Threw a " + e.getMessage() + " when trying to get the inner layer list.");
        }
        stack.add(name("test_add_new_layer"), renderCode).computeOrder();
        helper.assertTrue(internalLayersList.size() == priorSize + 1, "Our layer was not added during compute.");
        List<Layer> finalInternalLayersList = internalLayersList;
        helper.runAfterDelay(5, () -> {
            boolean flag = finalInternalLayersList.remove(renderCode); // undo our addition.
            helper.assertTrue(insertFlag.getBool(), "Our render function never ran.");
            helper.assertTrue(flag, "Somehow, a different rendering function was inserted. Wat");
            helper.succeed();
        });
    }

    @GameTest
    public static void not_present_in_stack(GameTestHelper helper) {
        // Test that we can't order against non-existent layers.
        var stack = drawStacks.get(PRE_SLEEP_PHASE);
        List<Layer> internalLayersList = null;
        try {
            internalLayersList = getInternalLayersList(stack);
        } catch (Exception e) {
            helper.fail("Threw a " + e.getMessage() + " when trying to get the inner layer list.");
        }
        helper.assertFalse(internalLayersList.remove(notAddedLayer), "Found our layer when we shouldn't have. Not good!");
        helper.succeed();
    }

    @GameTest
    public static void add_condition(GameTestHelper helper) {
        // Test that we can add conditions to pre-existing layers.
        helper.assertFalse(detectConditionFlag.getBool(), "Conditional rendering ran when it shouldn't have.");
        enableConditionFlag.set(true);
        helper.runAfterDelay(5, () -> {
            boolean result = detectConditionFlag.getBool();// Just in case of weird race conditions.
            enableConditionFlag.set(false);
            helper.assertTrue(result, "");
            helper.succeed();
        });
    }

    @GameTest
    public static void ordered_layers(GameTestHelper helper) {
        // Test that layers are in the correct order.
        var stack = drawStacks.get(PRE_SLEEP_PHASE);
        List<Layer> internalLayersList = null;
        Map<ResourceLocation, Layer> check = null;
        try {
            Class<?> cls = stack.getClass();
            var field = cls.getDeclaredField("namedLayers");
            field.setAccessible(true);
            check = (Map<ResourceLocation, Layer>) field.get(stack);
            internalLayersList = getInternalLayersList(stack);
        } catch (Exception e) {
            helper.fail("Threw a " + e.getMessage() + " when trying to get the inner layer list.");
        }
        int locationOfPotionEffects = internalLayersList.indexOf(check.get(POTION_EFFECTS));
        int locationOfLayerA = internalLayersList.indexOf(check.get(layerAName));
        int locationOfLayerB = internalLayersList.indexOf(check.get(layerBName));
        int locationOfLayerC = internalLayersList.indexOf(check.get(layerCName));
        helper.assertTrue(locationOfPotionEffects == locationOfLayerA - 1, "Layer offset from vanilla -> A was incorrect");
        helper.assertTrue(locationOfLayerB == locationOfLayerA + 1, "Layer offset from A -> B was incorrect");
        helper.assertTrue(locationOfLayerC == locationOfLayerB + 1, "Layer offset from B -> C was incorrect");
        helper.succeed();
    }

    @GameTest
    public static void editable_status(GameTestHelper helper) {
        var phases = new ResourceLocation[]{PRE_SLEEP_PHASE, COMBINE_PHASE, POST_SLEEP_PHASE, myEditableStackPhase};
        helper.assertTrue(drawStacks.size() == phases.length, "");
        for (ResourceLocation phase : phases) {
            helper.assertTrue(drawStacks.containsKey(phase), "Did not find " + phase + " added when it should have been present.");
        }
        helper.assertFalse(drawStacks.containsKey(myUneditableStackPhase), myUneditableStackPhase + " was editable when it should not have been.");
        helper.succeed();
    }

    @GameTest
    public static void full_stack_insertion(GameTestHelper helper) {
        // Test that both our new stacks are added into the COMBINE_PHASE. Technically already tested, but let's make sure.
        var stack = drawStacks.get(COMBINE_PHASE);
        List<Layer> internalLayersList = null;
        try {
            internalLayersList = getInternalLayersList(stack);
        } catch (Exception e) {
            helper.fail("Threw a " + e.getMessage() + " when trying to get the inner layer list.");
        }
        helper.assertTrue(internalLayersList.contains(myStackLayer), "Stack as layer was not added into COMBINE_PHASE");
        helper.assertFalse(internalLayersList.contains(myOtherStackLayer), "Stack as layer with added condition should NOT be identical");
        helper.succeed();
    }

    @GameTest
    public static void full_stack_condition(GameTestHelper helper) {
        helper.assertFalse(detectConditionStackFlag.getBool(), "Conditional rendering ran when it shouldn't have.");
        enableConditionStackFlag.set(true);
        helper.runAfterDelay(5, () -> {
            boolean result = detectConditionStackFlag.getBool();// Just in case of weird race conditions.
            enableConditionStackFlag.set(false);
            helper.assertTrue(result, "");
            helper.succeed();
        });
    }


    private void overlayTestListener(ModifyOverlayLayersEvent event) {
        if (drawStacks.containsKey(event.getLayeredDraw().getPhase())) {
            throw new IllegalStateException("Multiple events fired from single ForgeLayeredDraw");
        }
        drawStacks.put(event.getLayeredDraw().getPhase(), event.getLayeredDraw());
        if (event.isPhase(PRE_SLEEP_PHASE)) {
            var layeredDraw = event.getLayeredDraw();
            layeredDraw.addAbove(name("i_won_t_exist"), SCOREBOARD, notAddedLayer);
            // Test if layers may be ordered against other layers.
            // Layers have to be present to be ordered against, of course, but we tested for that already above.
            layeredDraw.addAbove(layerBName, POTION_EFFECTS, layerB);
            layeredDraw.addAbove(layerCName, layerBName, layerC);
            layeredDraw.addBelow(layerAName, layerBName, layerA);
            layeredDraw.addConditionTo(BOSS_OVERLAY, () -> {
                if (enableConditionFlag.getBool()) {
                    detectConditionFlag.set(true);
                    return true;
                } else {
                    detectConditionFlag.set(false);
                    return false;
                }
            });
        }
        if (event.isPhase(COMBINE_PHASE)) { // COMBINE_PHASE probably needs a new name so ppl don't get confused.
            // myStack may be edited, so this will fire a new event.
            myStackLayer = myStack.computeOrder().asLayer();
            // myUneditableStack is not an editable stack, so we need to add to it now
            myUneditableStack.add(name("my_uneditable_inner_layer"), (gg, tr) -> {
                detectConditionStackFlag.set(true);
            });
            myUneditableStack.computeOrder(); // compute and add to COMBINE_PHASE.
            myOtherStackLayer = myUneditableStack.asLayer();
            // Demonstrates that entire stacks can have conditions once converted to layers.
            event.getLayeredDraw()
                    .addWithCondition(name("my_uneditable_stack"), myOtherStackLayer, () -> {
                        if (enableConditionStackFlag.getBool()) {
                            return true;
                        } else {
                            detectConditionStackFlag.set(false);
                            return false;
                        }
                    })
                    .add(name("my_stack_layer"), myStackLayer);
        }
    }

    private static ResourceLocation name(String name) {
        return ResourceLocation.fromNamespaceAndPath(MODID, name);
    }

    @SuppressWarnings("unchecked")
    private static List<Layer> getInternalLayersList(ForgeLayeredDraw stack) throws ClassCastException, NoSuchFieldException, IllegalAccessException {
        Class<?> cls = stack.getClass().getSuperclass();
        var layersField = cls.getDeclaredField("layers");
        layersField.setAccessible(true);
        return (List<Layer>) layersField.get(stack);
    }
}
