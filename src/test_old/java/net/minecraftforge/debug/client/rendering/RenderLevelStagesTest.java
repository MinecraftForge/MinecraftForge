/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.client.rendering;

import net.minecraftforge.fml.loading.FMLLoader;
import org.apache.logging.log4j.LogManager;

import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;

@Mod("render_level_stages_test")
public class RenderLevelStagesTest
{
    public static final org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger();
    
    public RenderLevelStagesTest()
    {
        if (FMLLoader.getDist().isClient())
        {
            MinecraftForge.EVENT_BUS.addListener(this::onRenderLevelStages);
        }
    }
    
    private int count = 0;
    public void onRenderLevelStages(RenderLevelStageEvent event)
    {
        if(count < 10)
        {
            LOGGER.info("Render Stage: " + event.getStage());
            if(event.getStage() == RenderLevelStageEvent.Stage.AFTER_WEATHER)
                count++;
        }
    }
}
