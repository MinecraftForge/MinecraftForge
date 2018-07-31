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

package net.minecraftforge.debug.world;

import java.util.Map.Entry;
import org.apache.logging.log4j.Logger;
import net.minecraft.entity.EnumCreatureType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.FoundChunksForSpawningEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Simple mod to test net.minecraftforge.event.world.FoundChunksForSpawningEvent Prints event data to the log every tick.
 */
@Mod(modid = FoundChunksForSpawningEventTest.MODID, name = FoundChunksForSpawningEventTest.NAME, version = FoundChunksForSpawningEventTest.VERSION,
        acceptableRemoteVersions = "*")
public class FoundChunksForSpawningEventTest
{
    public static final String MODID = "foundchunksforspawningeventtest";
    public static final String NAME = "FoundChunksForSpawningEvent Test";
    public static final String VERSION = "1.0";

    private static final boolean ENABLED = false;
    private static Logger logger;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        if (ENABLED)
        {
            logger = event.getModLog();
            MinecraftForge.EVENT_BUS.register(FoundChunksForSpawningEventTest.class);
        }
    }

    @SubscribeEvent
    public static void onFoundChunksForSpawning(FoundChunksForSpawningEvent event)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("FoundChunksForSpawningEvent");
        sb.append("\n  numChunks: ").append(event.getEligibleChunksForSpawning().size()).append("/").append(event.getNumAttemptedEligibleChunksForSpawning());
        sb.append("\n  typeData:");
        for (Entry<EnumCreatureType, FoundChunksForSpawningEvent.CreatureTypeData> entry : event.getCreatureTypeData().entrySet())
        {
            FoundChunksForSpawningEvent.CreatureTypeData v = entry.getValue();
            sb.append("\n    ").append(entry.getKey().name()).append(": ").append(v.getCreatureCount()).append("/").append(v.getMaxCreatureCountWhereCanSpawnMore());
        }
        Exception ex = event.getNewEntityException();
        sb.append("\n  ex: ").append(ex == null ? "null" : ex.getMessage());
        logger.info(sb);
    }
}
