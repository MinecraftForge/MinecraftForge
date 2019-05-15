/*
 * Minecraft Forge
 * Copyright (c) 2016-2019.
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

package net.minecraftforge.debug;

import org.apache.logging.log4j.Logger;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.PotionEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

//@Mod(modid = "potioneventtest", name = "PotionEventTest", version = "1.0.0", acceptableRemoteVersions = "*")
public class PotionEventTest
{
    public static final boolean ENABLE = false;
    private Logger log;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        log = event.getModLog();
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        if (ENABLE)
            MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onPotionAdded(PotionEvent.PotionAddedEvent event)
    {
        if (!event.getEntity().getEntityWorld().isRemote)
        {
            log.info("{} has a new PotionEffect {}, the old one was {}", event.getEntityLiving(), event.getPotionEffect(), event.getOldPotionEffect());
        }
    }
    
    @SubscribeEvent
    public void isPotionApplicable(PotionEvent.PotionApplicableEvent event)
    {
        if (!event.getEntity().getEntityWorld().isRemote)
        {
            event.setResult(Result.ALLOW);
            log.info("Allowed Potion {} for Entity {}", event.getPotionEffect(), event.getEntityLiving());
        }
    }
    
    @SubscribeEvent
    public void onPotionRemove(PotionEvent.PotionRemoveEvent event)
    {
        if (!event.getEntity().getEntityWorld().isRemote)
        {
            log.info("Effect {} got Removed from {}", event.getPotionEffect(), event.getEntityLiving());
        }
    }
    
    @SubscribeEvent
    public void onPotionExpiry(PotionEvent.PotionExpiryEvent event)
    {
        if (!event.getEntity().getEntityWorld().isRemote)
        {
            log.info("Effect {} expired from {}", event.getPotionEffect(), event.getEntityLiving());
        }
    }
}
