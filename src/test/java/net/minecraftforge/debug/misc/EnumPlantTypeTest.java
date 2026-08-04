/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

/*


package net.minecraftforge.debug.misc;

import net.minecraftforge.common.BiomeManager.BiomeType;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DeferredWorkQueue;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

//@Mod("enumplanttypetest")
public class EnumPlantTypeTest
{
    private static final Logger LOGGER = LogManager.getLogger();

    public EnumPlantTypeTest()
    {
        FMLModLoadingContext.get().getModEventBus().addListener(this::setup);
    }

    @SubscribeEvent
    public void setup(final FMLCommonSetupEvent event)
    {
        DeferredWorkQueue.runLater(() ->
        {
            int index = BiomeType.values().length;
            BiomeType biomeType = BiomeType.create("FAKE");
            if (biomeType == null || !biomeType.name().equals("FAKE") || biomeType.ordinal() != index)
            {
                LOGGER.warn("RuntimeEnumExtender is working incorrectly for BiomeType!");
            }

            EnumPlantType plantType = EnumPlantType.create("FAKE");
            if (plantType == null || !plantType.name().equals("FAKE") || plantType != EnumPlantType.create("FAKE"))
            {
                LOGGER.warn("RuntimeEnumExtender is working incorrectly for EnumPlantType!");
            }
        });
    }
}
*/
