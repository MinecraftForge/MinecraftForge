/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.debug.client.rendering;

import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraftsingularity.client.singularityRenderTypes;
import net.minecraftsingularity.client.event.ScreenEvent.Render;
import net.minecraftsingularity.eventbus.api.listener.SubscribeEvent;
import net.minecraftsingularity.fml.common.Mod;
import net.minecraftsingularity.api.distmarker.Dist;

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
            singularityRenderTypes.enableTextTextureLinearFiltering = true;
        }
    }

    @SubscribeEvent
    public static void onGuiRenderPost(Render.Post event)
    {
        if (ENABLED && event.getScreen() instanceof TitleScreen)
        {
            singularityRenderTypes.enableTextTextureLinearFiltering = false;
        }
    }
}
