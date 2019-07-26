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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraftforge.event.entity.living.PotionEvent;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod("potion_event_test")
@Mod.EventBusSubscriber
public class PotionEventTest
{
    private static Logger LOGGER = LogManager.getLogger(PotionEventTest.class);

    @SubscribeEvent
    public static void onPotionAdded(PotionEvent.PotionAddedEvent event)
    {
        if (!event.getEntity().getEntityWorld().isRemote)
            LOGGER.info("{} has a new PotionEffect {}, the old one was {}", event.getEntityLiving(), event.getPotionEffect(), event.getOldPotionEffect());
    }

    @SubscribeEvent
    public static void isPotionApplicable(PotionEvent.PotionApplicableEvent event)
    {
        if (!event.getEntity().getEntityWorld().isRemote)
        {
            event.setResult(Result.ALLOW);
            LOGGER.info("Allowed Potion {} for Entity {}", event.getPotionEffect(), event.getEntityLiving());
        }
    }

    @SubscribeEvent
    public static void onPotionRemove(PotionEvent.PotionRemoveEvent event)
    {
        if (!event.getEntity().getEntityWorld().isRemote)
            LOGGER.info("Effect {} got Removed from {}", event.getPotionEffect(), event.getEntityLiving());
    }

    @SubscribeEvent
    public static void onPotionExpiry(PotionEvent.PotionExpiryEvent event)
    {
        if (!event.getEntity().getEntityWorld().isRemote)
            LOGGER.info("Effect {} expired from {}", event.getPotionEffect(), event.getEntityLiving());
    }
}
