/*
 * Minecraft Forge - Forge Development LLC
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.world;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(ServerWorldCreationTest.MODID)
public class ServerWorldCreationTest {
    static final String MODID = "server_world_creation_test";
    private static final Logger LOGGER = LogManager.getLogger(MODID);
    public ServerWorldCreationTest(){
    }
}

