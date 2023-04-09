/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.brigadier.Command;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class TestScreen extends Screen
{
    public static int open()
    {
        Minecraft.getInstance().setScreen(new TestScreen());
        return Command.SINGLE_SUCCESS;
    }

    public TestScreen()
    {
        super(Component.literal("testscreen"));
    }

    @Override
    public void render(PoseStack PoseStack, int MouseX, int MouseY, float PartialTick)
    {
        renderBackground(PoseStack);
    }
}
