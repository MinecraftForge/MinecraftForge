/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.debug.client;

import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.Identifier;
import net.minecraftsingularity.client.event.AddGuiOverlayLayersEvent;
import net.minecraftsingularity.client.gui.overlay.singularityLayer;
import net.minecraftsingularity.client.gui.overlay.singularityLayeredDraw;
import net.minecraftsingularity.common.extensions.IForgeGameTestHelper;
import net.minecraftsingularity.fml.common.Mod;
import net.minecraftsingularity.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftsingularity.gametest.GameTest;
import net.minecraftsingularity.gametest.GameTestNamespace;
import net.minecraftsingularity.test.BaseTestMod;

import java.util.List;
import java.util.Map;
import java.util.function.BooleanSupplier;

import static net.minecraftsingularity.client.gui.overlay.singularityLayeredDraw.*;

@GameTestNamespace("singularity")
@Mod(ModifyOverlayTest.MODID)
public class ModifyOverlayTest extends BaseTestMod {
    public static final String MODID = "modify_overlay_test";

    private static final Identifier myStackName = name("my_stack_name");
    private static final singularityLayeredDraw myLayerStack = new singularityLayeredDraw(myStackName);

    private static final singularityLayer notAddedLayer = (gg, tr) -> {};
    private static final singularityLayer layerA = (gg,tr) -> {};
    private static final Identifier layerAName = name("layer_a");
    private static final singularityLayer layerB = (gg,tr) -> {};
    private static final Identifier layerBName = name("layer_b");
    private static final singularityLayer layerC = (gg,tr) -> {};
    private static final Identifier layerCName = name("layer_c");


    private static final IForgeGameTestHelper.BoolFlag detectConditionFlag = new IForgeGameTestHelper.BoolFlag("det_cond_flag");
    private static final IForgeGameTestHelper.BoolFlag detectConditionStackFlag = new IForgeGameTestHelper.BoolFlag("det_cond_stack_flag");
    private static final IForgeGameTestHelper.BoolFlag enableConditionFlag = new IForgeGameTestHelper.BoolFlag("en_cond_flag");
    private static final IForgeGameTestHelper.BoolFlag enableConditionStackFlag = new IForgeGameTestHelper.BoolFlag("en_cond_stack_flag");

    private static singularityLayeredDraw drawStack;

    public ModifyOverlayTest(FMLJavaModLoadingContext context) {
        super(context, false, false);
        AddGuiOverlayLayersEvent.BUS.addListener(this::overlayTestListener);
    }

    @GameTest
    public static void not_present_in_stack(GameTestHelper helper) {
        // Test that we can't order against non-existent layers.
        List<singularityLayer> internalLayersList = null;
        try {
            internalLayersList = getInternalLayersList(drawStack);
        } catch (Exception e) {
            helper.fail("Threw a " + e.getMessage() + " when trying to get the inner layer list.");
        }
        helper.assertFalse(internalLayersList.contains(notAddedLayer), "Found our layer when we shouldn't have. Not good!");
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
        List<singularityLayer> internalLayersList = null;
        Map<Identifier, singularityLayer> check = null;
        try {
            Class<?> cls = drawStack.getClass();
            var field = cls.getDeclaredField("subLayerStacks");
            var field1 = cls.getDeclaredField("namedLayers");
            field.setAccessible(true);
            field1.setAccessible(true);
            Map<Identifier, Map.Entry<singularityLayeredDraw, BooleanSupplier>> VROOT = ((Map<Identifier, Map.Entry<singularityLayeredDraw, BooleanSupplier>>) field.get(drawStack));
            var PSS = ((singularityLayeredDraw) VROOT.get(PRE_SLEEP_STACK).getKey());
            check = ((Map<Identifier, singularityLayer>) field1.get(PSS));
            internalLayersList = getInternalLayersList(PSS);
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
    public static void full_stack_condition(GameTestHelper helper) {
        // Test conditional rendering & whether a full stack can be added in the first place.
        helper.assertFalse(detectConditionStackFlag.getBool(), "Conditional rendering ran when it shouldn't have.");
        enableConditionStackFlag.set(true);
        helper.runAfterDelay(5, () -> {
            boolean result = detectConditionStackFlag.getBool();// Just in case of weird race conditions.
            enableConditionStackFlag.set(false);
            helper.assertTrue(result, "");
            helper.succeed();
        });
    }

    private void overlayTestListener(AddGuiOverlayLayersEvent event) {
        drawStack = event.getLayeredDraw();
        var layeredDraw = event.getLayeredDraw();
        layeredDraw.addAbove(VANILLA_ROOT, name("i_won_t_exist"), name("non_existent_target_layer"), notAddedLayer);
        var pEffects = layeredDraw.locateStack(PRE_SLEEP_STACK).get();
        // Test if layers may be ordered against other layers.
        // Layers have to be present to be ordered against, of course, but we tested for that already above.
        pEffects.addAbove(layerBName, POTION_EFFECTS, layerB);
        pEffects.addAbove(layerCName, layerBName, layerC);
        layeredDraw.addBelow(PRE_SLEEP_STACK, layerAName, layerBName, layerA);
        layeredDraw.addConditionTo(PRE_SLEEP_STACK, BOSS_OVERLAY, () -> {
            if (enableConditionFlag.getBool()) {
                detectConditionFlag.set(true);
                return true;
            } else {
                detectConditionFlag.set(false);
                return false;
            }
        });

        myLayerStack.add(name("my_inner_layer_name"), (gg, tr) -> {
            detectConditionStackFlag.set(true);
        });
        // Demonstrates that entire stacks can have conditions attached
        event.getLayeredDraw()
                .add(myStackName, myLayerStack, () -> {
                    if (enableConditionStackFlag.getBool()) {
                        return true;
                    } else {
                        detectConditionStackFlag.set(false);
                        return false;
                    }
                });
    }


    private static Identifier name(String name) {
        return Identifier.fromNamespaceAndPath(MODID, name);
    }

    @SuppressWarnings("unchecked")
    private static List<singularityLayer> getInternalLayersList(singularityLayeredDraw stack) throws ClassCastException, NoSuchFieldException, IllegalAccessException {
        Class<?> cls = stack.getClass();
        var layersField = cls.getDeclaredField("bakedLayers");
        layersField.setAccessible(true);
        return (List<singularityLayer>) layersField.get(stack);
    }
}
