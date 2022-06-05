/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface IItemRenderProperties
{
    IItemRenderProperties DUMMY = new IItemRenderProperties()
    {
    };

    /**
     * Returns the font renderer used to render tooltips and overlays for this item.
     * Returning null will use the standard font renderer.
     *
     * @param stack The current item stack
     * @return A instance of FontRenderer or null to use default
     */
    default Font getFont(ItemStack stack)
    {
        return null;
    }

    /**
     * Override this method to have an item handle its own armor rendering.
     *
     * @param entityLiving The entity wearing the armor
     * @param itemStack    The itemStack to render the model of
     * @param armorSlot    The slot the armor is in
     * @param _default     Original armor model. Will have attributes set.
     * @return A HumanoidModel to render instead of the default, will have the relevant properties copied in {@link #getBaseArmorModel(LivingEntity, ItemStack, EquipmentSlot, HumanoidModel).
     *         Returning null will cause the default to render.
     * @see #getBaseArmorModel(LivingEntity, ItemStack, EquipmentSlot, HumanoidModel)
     */
    @Nullable
    default HumanoidModel<?> getArmorModel(LivingEntity entityLiving, ItemStack itemStack, EquipmentSlot armorSlot, HumanoidModel<?> _default)
    {
        return null;
    }

    /**
     * Override this method to return a generic model rather than a {@link HumanoidModel}. More ideal for wrapping the original model or returning non-standard models like elytra wings.
     * By default, this hook copies in the model properties from the default into the model returned by {@link #getArmorModel(LivingEntity, ItemStack, EquipmentSlot, HumanoidModel)},
     * so if you override this method you are responsible for copying properties you care about
     *
     * @param entityLiving The entity wearing the armor
     * @param itemStack    The itemStack to render the model of
     * @param armorSlot    The slot the armor is in
     * @param _default     Original armor model. Will have attributes set.
     * @return A Model to render instead of the default
     * @see #getArmorModel(LivingEntity, ItemStack, EquipmentSlot, HumanoidModel)
     */
    @Nonnull
    default Model getBaseArmorModel(LivingEntity entityLiving, ItemStack itemStack, EquipmentSlot armorSlot, HumanoidModel<?> _default)
    {
        HumanoidModel<?> replacement = getArmorModel(entityLiving, itemStack, armorSlot, _default);
        if (replacement != null && replacement != _default)
        {
            ForgeHooksClient.copyModelProperties(_default, replacement);
            return replacement;
        }
        return _default;
    }

    /**
     * Called when the client starts rendering the HUD, for whatever item the player
     * currently has as a helmet. This is where pumpkins would render there overlay.
     *
     * @param stack        The ItemStack that is equipped
     * @param player       Reference to the current client entity
     * @param width        Viewport width
     * @param height       Viewport height
     * @param partialTick  Partial tick for the renderer, useful for interpolation
     */
    default void renderHelmetOverlay(ItemStack stack, Player player, int width, int height, float partialTick)
    {

    }

    /**
     * @return This Item's renderer, or the default instance if it does not have
     * one.
     */
    default BlockEntityWithoutLevelRenderer getItemStackRenderer()
    {
        return Minecraft.getInstance().getItemRenderer().getBlockEntityRenderer();
    }
}
