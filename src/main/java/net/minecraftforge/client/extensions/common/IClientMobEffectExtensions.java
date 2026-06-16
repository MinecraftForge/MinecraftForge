/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.extensions.common;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Hud;
import net.minecraft.client.gui.screens.inventory.EffectsInInventory;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraftforge.fml.LogicalSide;

import java.util.function.Consumer;

/**
 * {@linkplain LogicalSide#CLIENT Client-only} extensions to {@link MobEffect}.
 *
 * @see MobEffect#initializeClient(Consumer)
 */
public interface IClientMobEffectExtensions {
    IClientMobEffectExtensions DEFAULT = new IClientMobEffectExtensions() { };

    static IClientMobEffectExtensions of(MobEffectInstance instance) {
        return of(instance.getEffect().value());
    }

    static IClientMobEffectExtensions of(MobEffect effect) {
        return effect.getEffectRendererInternal() instanceof IClientMobEffectExtensions r ? r : DEFAULT;
    }

    /**
     * Queries whether the given effect should be shown in the player's inventory.
     * <p>
     * By default, this returns {@code true}.
     */
    default boolean isVisibleInInventory(MobEffectInstance instance) {
        return true;
    }

    /**
     * Queries whether the given effect should be shown in the HUD.
     * <p>
     * By default, this returns {@code true}.
     */
    default boolean isVisibleInGui(MobEffectInstance instance) {
        return true;
    }

    /**
     * Renders the text and icon of the specified effect in the player's inventory.
     *
     * @return true to prevent default rendering, false otherwise
     */
    default boolean extractInventory(MobEffectInstance instance, EffectsInInventory effects, GuiGraphicsExtractor graphics, int x, int y, int blitOffset) {
        return false;
    }

    /**
     * Renders the icon of the specified effect on the player's HUD.
     * This can be used to render icons from your own texture sheet.
     *
     * @param alpha       The alpha value. Blinks when the effect is about to run out
     * @return true to prevent default rendering, false otherwise
     */
    default boolean extractGuiIcon(MobEffectInstance instance, Hud hud, GuiGraphicsExtractor graphics, int x, int y, float z, float alpha) {
        return false;
    }
}
