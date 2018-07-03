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

package net.minecraftforge.debug.item;

import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.init.Biomes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ItemFishedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.Logger;

@Mod(modid = "itemfishtest", name = "ItemFishTest", version = "1.0.0", acceptableRemoteVersions = "*")
public class ItemFishedEventTest
{

    private static final boolean ENABLE = false;
    private static Logger logger;

    @Mod.EventHandler
    public void onInit(FMLPreInitializationEvent event)
    {
        if (ENABLE)
        {
            logger = event.getModLog();
            logger.info("Enabling Fishing Test mod");
            MinecraftForge.EVENT_BUS.register(this);
        }
    }

    @SubscribeEvent
    public void onItemFished(ItemFishedEvent event)
    {
        EntityFishHook hook = event.getHookEntity();
        BlockPos bobberPos = hook.getPosition();
        Biome biomeFishedAt = hook.getEntityWorld().getBiome(bobberPos);
        logger.info("Item fished in Biome {}", biomeFishedAt.getBiomeName());
        if (biomeFishedAt.equals(Biomes.OCEAN))
        {
            logger.info("Canceling the event because biome is ocean");
            event.setCanceled(true);
        }
        event.damageRodBy(50);
    }
}
