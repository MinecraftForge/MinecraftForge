/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
public class TestScreen extends Screen
{
    public static int open() {
        Minecraft.getInstance().setScreen(new TestScreen(Component.translatable("screen.patch")));
        return 1;
    }

    public TestScreen(Component pComponent) {
        super(pComponent);
    }

    @Override
    public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        renderBackground(pPoseStack);
    }
}
