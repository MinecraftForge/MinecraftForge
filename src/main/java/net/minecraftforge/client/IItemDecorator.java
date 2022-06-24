/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client;

import net.minecraft.client.gui.Font;
import net.minecraft.world.item.ItemStack;

public interface IItemDecorator {

    void render(Font font, ItemStack stack, int xOffset, int yOffset, float blitOffset);
}
