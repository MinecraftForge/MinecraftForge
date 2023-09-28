/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.client.rendering;

import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraftforge.client.ForgeRenderTypes;
import net.minecraftforge.client.event.ScreenEvent.Render;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.api.distmarker.Dist;

@Mod(LinearTextTextureFilteringTest.MODID)
@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class LinearTextTextureFilteringTest
{
    public static final String MODID = "text_linear_filtering_test";
    static final boolean ENABLED = false;

    @SubscribeEvent
    public static void onGuiRenderPre(Render.Pre event)
    {
        if (ENABLED && event.getScreen() instanceof TitleScreen)
        {
            ForgeRenderTypes.enableTextTextureLinearFiltering = true;
        }
    }

    @SubscribeEvent
    public static void onGuiRenderPost(Render.Post event)
    {
        if (ENABLED && event.getScreen() instanceof TitleScreen)
        {
            ForgeRenderTypes.enableTextTextureLinearFiltering = false;
        }
    }
}
