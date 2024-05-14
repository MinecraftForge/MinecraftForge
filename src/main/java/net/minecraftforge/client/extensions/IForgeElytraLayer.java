/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.extensions;

import net.minecraft.client.renderer.entity.layers.ElytraLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public interface IForgeElytraLayer<T> {
    /**
     * Determines if the ElytraLayer should render.
     * ItemStack and Entity are provided for modder convenience,
     * For example, using the same ElytraLayer for multiple custom Elytra.
     *
     * @param stack  The Elytra ItemStack
     * @param entity The entity being rendered.
     * @return If the ElytraLayer should render.
     */
    default boolean shouldRender(ItemStack stack, T entity) {
        return stack.getItem() == Items.ELYTRA;
    }

    /**
     * Gets the texture to use with this ElytraLayer.
     * This assumes the vanilla Elytra model.
     *
     * @param stack  The Elytra ItemStack.
     * @param entity The entity being rendered.
     * @return The texture.
     */
    default ResourceLocation getElytraTexture(ItemStack stack, T entity) {
        return ElytraLayer.WINGS_LOCATION;
    }
}
