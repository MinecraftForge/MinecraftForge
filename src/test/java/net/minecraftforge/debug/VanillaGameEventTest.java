/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.VanillaGameEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Tests {@link VanillaGameEvent} by listening for and printing out any uses of shears in the overworld.
 */
@Mod(VanillaGameEventTest.MODID)
public class VanillaGameEventTest
{

    static final String MODID = "vanilla_game_event_test";

    private static final boolean ENABLED = true;

    private static final Logger LOGGER = LogManager.getLogger();

    public VanillaGameEventTest()
    {
        if (!ENABLED) return;

        MinecraftForge.EVENT_BUS.addListener(this::vanillaEvent);
    }

    public void vanillaEvent(VanillaGameEvent event)
    {
        if (event.getVanillaEvent() == GameEvent.SHEAR && event.getLevel().dimension() == Level.OVERWORLD)
        {
            Entity cause = event.getCause();
            if (cause == null)
            {
                //One case this will be fired is when a dispenser shears an entity like a sheep
                LOGGER.info("Target at {} in the overworld was sheared.", event.getEventPosition());
            }
            else
            {
                //This will be fired if a player shears an entity like a sheep or carves a block like a pumpkin
                LOGGER.info("{} sheared a target at {} in the overworld.", cause, event.getEventPosition());
            }
        }
    }
}
