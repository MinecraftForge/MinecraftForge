/*
 * Minecraft Forge
 * Copyright (c) 2016-2022.
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

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Checks that {@link RegistryObject} works correctly, specifically that get() functions immediately
 * after construction, if registries are already populated.
 */
@Mod(RegistryObjectTest.MODID)
public class RegistryObjectTest
{

    static final String MODID = "registry_object_test";

    private static final boolean ENABLED = true;

    private static final Logger LOGGER = LogManager.getLogger();

    public RegistryObjectTest()
    {
        if (!ENABLED) return;

        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        modBus.addListener(this::commonSetup);
    }

    public void commonSetup(FMLCommonSetupEvent event)
    {
        LOGGER.info("Stone 1: {}", RegistryObject.of(new ResourceLocation("minecraft", "stone"), ForgeRegistries.BLOCKS).get());
        LOGGER.info("Stone 2: {}", RegistryObject.of(new ResourceLocation("minecraft", "stone"), Block.class, MODID).get());
    }
}
