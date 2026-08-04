/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

/*


package net.minecraftforge.debug.client;

import net.minecraft.block.material.Material;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.event.FMLInitializationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

*/
/**
 * Simple mod to test fov modifier.
 *//*

//@Mod(modid = "fovmodifiertest", name = "FOV Modifier Test", version = "0.0.0", clientSideOnly = true)
public class FOVModifierEventTest
{
    static final boolean ENABLED = false;

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        if (ENABLED)
        {
            MinecraftForge.EVENT_BUS.register(this);
        }
    }

    @SubscribeEvent
    public void getFOVModifier(EntityViewRenderEvent.FOVModifier event)
    {
        if (event.getState().getMaterial() == Material.WATER)
        {
            event.setFOV(event.getFOV() / 60.0f * 50.0f);
        }
    }
}
*/
