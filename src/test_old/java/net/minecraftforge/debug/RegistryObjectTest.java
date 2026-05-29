/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.debug;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftsingularity.eventbus.api.IEventBus;
import net.minecraftsingularity.fml.common.Mod;
import net.minecraftsingularity.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftsingularity.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftsingularity.registries.singularityRegistries;
import net.minecraftsingularity.registries.RegistryObject;
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
        LOGGER.info("Stone 1: {}", RegistryObject.create(new ResourceLocation("minecraft", "stone"), singularityRegistries.BLOCKS).get());
        LOGGER.info("Stone 2: {}", RegistryObject.create(new ResourceLocation("minecraft", "stone"), singularityRegistries.Keys.BLOCKS, MODID).get());
    }
}
