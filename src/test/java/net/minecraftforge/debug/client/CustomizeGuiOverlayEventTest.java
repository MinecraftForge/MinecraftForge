/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.client;

import net.minecraftforge.client.event.CustomizeGuiOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;

@Mod(CustomizeGuiOverlayEventTest.MODID)
public class CustomizeGuiOverlayEventTest
{
    private static final boolean ENABLED = false;

    public static final String MODID = "custom_gui_event_test";

    public CustomizeGuiOverlayEventTest() {
        if(ENABLED) {
            MinecraftForge.EVENT_BUS.addListener(this::onRenderOverlaychat);
            MinecraftForge.EVENT_BUS.addListener(this::onRenderOverlayDebug);
        }
    }

    public void onRenderOverlayDebug(CustomizeGuiOverlayEvent.DebugText event) {
        if(CustomizeGuiOverlayEvent.DebugText.Side.Left == event.getSide()) {
            event.getText().add("Left Side");
        } else {
            event.getText().add("Right Side");
        }
    }

    public void onRenderOverlaychat(CustomizeGuiOverlayEvent.Chat event) {
        event.setPosY(-200);
    }
}
