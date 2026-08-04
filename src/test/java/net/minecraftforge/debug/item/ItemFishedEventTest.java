/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

/*


package net.minecraftforge.debug.item;

import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.init.Biomes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ItemFishedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.FMLPreInitializationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.apache.logging.log4j.Logger;

//@Mod(modid = "itemfishtest", name = "ItemFishTest", version = "1.0.0", acceptableRemoteVersions = "*")
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

    @net.minecraftforge.eventbus.api.SubscribeEvent
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
*/
