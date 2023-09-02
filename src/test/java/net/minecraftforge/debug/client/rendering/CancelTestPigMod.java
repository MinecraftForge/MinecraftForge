/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.client.rendering;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLivingModificationEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod(CancelTestPigMod.MODID)
public class CancelTestPigMod {

    public static final String MODID = "cancel_test_pig";

    public static final boolean DO_CANCEL = true;


    @Mod.EventBusSubscriber(modid = MODID, value= Dist.CLIENT, bus= Mod.EventBusSubscriber.Bus.FORGE)
    private static class CancelTestPigClient
    {

        @SubscribeEvent(priority = EventPriority.LOW)
        public static void entityRender(RenderLivingModificationEvent<?, ?> evt)
        {
            if (DO_CANCEL && evt.getRenderer() instanceof RenderingPrePostTest.SpecialPigRenderer)
            {
                evt.setCanceled(true);
            }
        }

    }
}
