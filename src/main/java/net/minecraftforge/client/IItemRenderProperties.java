/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.client;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;

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
    default FontRenderer getFont(ItemStack stack)
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
     * @return A ModelBiped to render instead of the default
     */
    default <A extends BipedModel<?>> A getArmorModel(LivingEntity entityLiving, ItemStack itemStack, EquipmentSlotType armorSlot, A _default)
    {
        return null;
    }

    /**
     * Called when the client starts rendering the HUD, for whatever item the player
     * currently has as a helmet. This is where pumpkins would render there overlay.
     *
     * @param stack        The ItemStack that is equipped
     * @param player       Reference to the current client entity
     * @param width        Viewport width
     * @param height       Viewport height
     * @param partialTicks Partial ticks for the renderer, useful for interpolation
     */
    default void renderHelmetOverlay(ItemStack stack, PlayerEntity player, int width, int height, float partialTicks)
    {

    }

    /**
     * @return This Item's renderer, or the default instance if it does not have
     * one.
     */
    default ItemStackTileEntityRenderer getItemStackTileEntityRenderer()
    {
        return ItemStackTileEntityRenderer.instance;
    }
}
