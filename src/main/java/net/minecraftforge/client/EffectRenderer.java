/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.world.effect.MobEffectInstance;


public abstract class EffectRenderer
{
    public static final EffectRenderer DUMMY = new EffectRenderer()
    {
        @Override
        public void renderInventoryEffect(MobEffectInstance effectInstance, EffectRenderingInventoryScreen<?> gui, PoseStack poseStack, int x, int y, float z)
        {

        }

        @Override
        public void renderHUDEffect(MobEffectInstance effectInstance, GuiComponent gui, PoseStack poseStack, int x, int y, float z, float alpha)
        {

        }
    };

    /**
     * If the Potion effect should be displayed in the players inventory
     *
     * @param effect the active PotionEffect
     * @return true to display it (default), false to hide it.
     */
    public boolean shouldRender(MobEffectInstance effect)
    {
        return true;
    }

    /**
     * If the standard PotionEffect text (name and duration) should be drawn when this potion is active.
     *
     * @param effect the active PotionEffect
     * @return true to draw the standard text
     */
    public boolean shouldRenderInvText(MobEffectInstance effect)
    {
        return true;
    }

    /**
     * If the Potion effect should be displayed in the player's ingame HUD
     *
     * @param effect the active PotionEffect
     * @return true to display it (default), false to hide it.
     */
    public boolean shouldRenderHUD(MobEffectInstance effect)
    {
        return true;
    }

    /**
     * Called to draw the this Potion onto the player's inventory when it's active.
     * This can be used to e.g. render Potion icons from your own texture.
     *
     * @param effectInstance the effect instance
     * @param gui            the gui instance
     * @param poseStack      the pose stack
     * @param x              the x coordinate
     * @param y              the y coordinate
     * @param z              the z level
     */
    public abstract void renderInventoryEffect(MobEffectInstance effectInstance, EffectRenderingInventoryScreen<?> gui, PoseStack poseStack, int x, int y, float z);

    /**
     * Called to draw the this Potion onto the player's ingame HUD when it's active.
     * This can be used to e.g. render Potion icons from your own texture.
     *
     * @param effectInstance the active PotionEffect
     * @param gui            the gui instance
     * @param poseStack      the pose stack
     * @param x              the x coordinate
     * @param y              the y coordinate
     * @param z              the z level
     * @param alpha          the alpha value, blinks when the potion is about to run out
     */
    public abstract void renderHUDEffect(MobEffectInstance effectInstance, GuiComponent gui, PoseStack poseStack, int x, int y, float z, float alpha);
}
