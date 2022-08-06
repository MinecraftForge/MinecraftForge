/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.misc;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * This class provides a way to test the AnvilUpdateEvent. <br>
 * The event is now fired as described in the javadoc (of AnvilUpdateEvent). <br>
 * "AnvilUpdateEvent is fired whenever the input stacks (left or right) or the name in an anvil changed."
 */
@Mod(AnvilUpdateEventTest.MOD_ID)
public class AnvilUpdateEventTest
{
    private static final boolean ENABLED = false;
    private static final Logger LOGGER = LogUtils.getLogger();
    static final String MOD_ID = "anvil_update_event_fix";

    public AnvilUpdateEventTest()
    {
        if (ENABLED)
        {
            MinecraftForge.EVENT_BUS.addListener(this::anvilUpdate);
        }
    }

    private void anvilUpdate(AnvilUpdateEvent event)
    {
        LOGGER.info("Anvil input or name changed!");
    }
}
