/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.eventtest.tests.forge.client;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventtest.internal.EventTest;
import net.minecraftforge.eventtest.internal.TestHolder;

/**
 * Test case for the EntityJoinWorldEvent.
 *
 * Event fires during gameplay.
 * Success condition:
 *      EntityJoinWorldEvent fired for Minecraft.getInstance().player
 * Failure info:
 *      "EntityJoinWorldEvent was not fired"
 *
 */

// @TestHolder("EntityJoinWorld")
public class EntityJoinedWorldTest extends EventTest {

    @Override
    public void registerEvents() {
        MinecraftForge.EVENT_BUS.addListener(this::eventListener);
    }

    private void eventListener(EntityJoinLevelEvent event) {
        final Entity eventEntity = event.getEntity();
        final Player gamePlayer = Minecraft.getInstance().player;

        final boolean eventFiredForPlayer = eventEntity == gamePlayer;

        if(eventFiredForPlayer)
            pass();
    }
}