/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.gui.overlay;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;

/**
 * A HUD overlay.
 *
 * @see RegisterGuiOverlaysEvent
 */
@FunctionalInterface
public interface IGuiOverlay
{
    void render(ForgeGui gui, PoseStack poseStack, float partialTick, int screenWidth, int screenHeight);
}
