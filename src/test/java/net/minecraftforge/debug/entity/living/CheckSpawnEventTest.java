/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.debug.entity.living;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingSpawnEvent.CheckSpawn;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = CheckSpawnEventTest.MODID, name = "CheckSpawnTest", version = "1.0", acceptableRemoteVersions = "*")
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
