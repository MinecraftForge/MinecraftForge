/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

/*


package net.minecraftforge.debug.entity.living;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingSpawnEvent.CheckSpawn;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

//@Mod(modid = CheckSpawnEventTest.MODID, name = "CheckSpawnTest", version = "1.0", acceptableRemoteVersions = "*")
public class CheckSpawnEventTest
{
    public static final String MODID = "checkspawntest";
    public static final boolean ENABLED = false;

    @EventHandler
    public void onPreInit(FMLPreInitializationEvent event)
    {    	
    	if (ENABLED) 
    	{   		
    		MinecraftForge.EVENT_BUS.register(this);
    	}
    }
    
    @SubscribeEvent
    public void canMobSpawn(CheckSpawn event) 
    {   	
    	event.setResult(Result.DENY);
    }
}
*/
