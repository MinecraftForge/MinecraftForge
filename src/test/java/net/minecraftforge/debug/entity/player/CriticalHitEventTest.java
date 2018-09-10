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

package net.minecraftforge.debug.entity.player;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import org.apache.logging.log4j.Logger;

import net.minecraft.util.EnumHand;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;

@Mod(modid = "criticalhiteventtest", name = "CriticalHitEventTest", version = "1.0.0", acceptableRemoteVersions = "*")
public class CriticalHitEventTest
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
    public void onCriticalHit(CriticalHitEvent event)
    {
        ItemStack itemstack = event.getEntityPlayer().getHeldItem(EnumHand.MAIN_HAND);
        
        if(event.getDamageModifier()>1F)
        {
            log.info("By default this hit will be critical.");
        }
        else
        {
            log.info("By default this hit won't be critical.");
        }
    
        if (itemstack.getItem() instanceof ItemSword)
        {
            event.setResult(Result.ALLOW); //Every hit is Critical
            log.info("This hit will be critical.");
        }
        else if (!itemstack.isEmpty())
        {
            event.setResult(Result.DENY);//No hit will be Critical
            log.info("This hit wont be critical.");
        }
        else
        {
            event.setResult(Result.DEFAULT); //Vanilla Hits
        }       
            
        log.info("{} got hit by {} with a damagemodifier of {}" , event.getTarget(), event.getEntityPlayer(), event.getDamageModifier());
        event.setDamageModifier(2.0F);
        log.info("The damagemodifier is changed to {}", event.getDamageModifier());
    }
}
