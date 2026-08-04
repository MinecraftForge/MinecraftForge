/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

/*


package net.minecraftforge.debug.fluid;

import net.minecraft.init.Blocks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent.CreateFluidSourceEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.FMLInitializationEvent;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.eventbus.api.SubscribeEvent;

//@Mod(modid = "createfluidsourcetest", name = "CreateFluidSourceTest", version = "1.0", acceptableRemoteVersions = "*")
public class CreateFluidSourceEventTest
{
    public static final boolean ENABLE = false;

    @Mod.EventHandler
    public static void init(FMLInitializationEvent event)
    {
        if (ENABLE)
        {
            MinecraftForge.EVENT_BUS.register(CreateFluidSourceEventTest.class);
        }
    }

    @net.minecraftforge.eventbus.api.SubscribeEvent
    public static void onCreateFluidSource(CreateFluidSourceEvent event)
    {
        // make it work exactly the opposite of how it works by default
        if (event.getState().getBlock() == Blocks.FLOWING_WATER)
        {
            event.setResult(Result.DENY);
        }
        else
        {
            event.setResult(Result.ALLOW);
        }
    }
}
*/
