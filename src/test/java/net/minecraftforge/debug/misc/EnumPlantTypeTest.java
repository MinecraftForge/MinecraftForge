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
