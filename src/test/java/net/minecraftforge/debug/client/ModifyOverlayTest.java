/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.client;

import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.AddGuiOverlayLayersEvent;
import net.minecraftforge.client.gui.overlay.ForgeLayeredDraw;
import net.minecraftforge.common.extensions.IForgeGameTestHelper;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.gametest.GameTest;
import net.minecraftforge.gametest.GameTestNamespace;
import net.minecraftforge.test.BaseTestMod;

import java.util.List;
import java.util.Map;

import static net.minecraftforge.client.gui.overlay.ForgeLayeredDraw.*;

@GameTestNamespace("forge")
@Mod(ModifyOverlayTest.MODID)
public class ModifyOverlayTest extends BaseTestMod {
    public static final String MODID = "modify_overlay_test";

    private static final ResourceLocation myStackName = name("my_stack_name");
    private static final ForgeLayeredDraw myLayerStack = new ForgeLayeredDraw(myStackName);

    private static final Layer notAddedLayer = (gg,tr) -> {};
    private static final Layer layerA = (gg,tr) -> {};
    private static final ResourceLocation layerAName = name("layer_a");
    private static final Layer layerB = (gg,tr) -> {};
    private static final ResourceLocation layerBName = name("layer_b");
    private static final Layer layerC = (gg,tr) -> {};
    private static final ResourceLocation layerCName = name("layer_c");


    private static final IForgeGameTestHelper.BoolFlag detectConditionFlag = new IForgeGameTestHelper.BoolFlag("det_cond_flag");
    private static final IForgeGameTestHelper.BoolFlag detectConditionStackFlag = new IForgeGameTestHelper.BoolFlag("det_cond_stack_flag");
    private static final IForgeGameTestHelper.BoolFlag enableConditionFlag = new IForgeGameTestHelper.BoolFlag("en_cond_flag");
    private static final IForgeGameTestHelper.BoolFlag enableConditionStackFlag = new IForgeGameTestHelper.BoolFlag("en_cond_stack_flag");

    private static ForgeLayeredDraw drawStack;

    public ModifyOverlayTest(FMLJavaModLoadingContext context) {
        super(context);
        context.getModEventBus().addListener(this::overlayTestListener);
    }

    @GameTest
    public static void not_present_in_stack(GameTestHelper helper) {
        // Test that we can't order against non-existent layers.
        List<Layer> internalLayersList = null;
        try {
            internalLayersList = getInternalLayersList(drawStack);
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
        List<Layer> internalLayersList = null;
        Map<ResourceLocation, Layer> check = null;
        try {
            Class<?> cls = drawStack.getClass();
            var field = cls.getDeclaredField("namedLayers");
            var method = cls.getDeclaredMethod("findLayer", ResourceLocation.class);
            method.setAccessible(true);
            field.setAccessible(true);
            var selection = (ForgeLayeredDraw) method.invoke(drawStack, POTION_EFFECTS);
            check = (Map<ResourceLocation, Layer>) field.get(selection);
            internalLayersList = getInternalLayersList(selection);
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
    public static void full_stack_insertion(GameTestHelper helper) {
        // Test that both our new stacks are added into the COMBINE_PHASE.
        boolean is_present = false;
        try {
            Class<?> cls = drawStack.getClass();
            var field = cls.getDeclaredField("namedLayers");
            var method = cls.getDeclaredMethod("findLayer", ResourceLocation.class);
            method.setAccessible(true);
            field.setAccessible(true);
            var selection = (ForgeLayeredDraw) method.invoke(drawStack, myStackName);
            is_present = selection != null; // won't be null if it's present.
        } catch (Exception e) {
            helper.fail("Failed whilst reflecting into LayeredDraw.");
        }
        helper.assertTrue(is_present, "Our new stack wasn't added");
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


    private void overlayTestListener(AddGuiOverlayLayersEvent event) {
        drawStack = event.getLayeredDraw();
        var layeredDraw = event.getLayeredDraw();
        layeredDraw.addAbove(name("i_won_t_exist"), name("non_existent_target_layer"), notAddedLayer);
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
