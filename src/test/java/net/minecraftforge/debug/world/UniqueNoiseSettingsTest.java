/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
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

import net.minecraft.util.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.FlatChunkGenerator;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructureFeatures;
import net.minecraft.world.gen.settings.DimensionStructuresSettings;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(UniqueNoiseSettingsTest.MODID)
public class UniqueNoiseSettingsTest {


    public static final String MODID = "unique_noise_settings_test";
    private static final Logger LOGGER = LogManager.getLogger(MODID);

    public UniqueNoiseSettingsTest()
    {
        MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, this::addDimensionalSpacing);
        MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, this::addToAllBiomes);
    }

    // Add Bastion to all biomes
    public void addToAllBiomes(final BiomeLoadingEvent event) {
        event.getGeneration().getStructures().add(() -> StructureFeatures.BASTION_REMNANT);
    }

    /*
     * Add Bastion spacing to Overworld and End.
     * Remove Bastion spacing from Nether and Custom dimension.
     *
     * Expectation: Bastion spawns in only Overworld and End.
     * Result if PR #7777 fails: Bastion only spawns in End and no other dimension.
     */
    public void addDimensionalSpacing(final WorldEvent.Load event) {
        if(event.getWorld() instanceof ServerWorld) { // Sanity check
            ServerWorld serverWorld = (ServerWorld)event.getWorld();
            RegistryKey<World> worldKey = serverWorld.dimension();
            ChunkGenerator chunkGenerator = serverWorld.getChunkSource().getGenerator();

            /*
             * Prevent spawning our structure in Vanilla's superflat world as
             * people seem to want their superflat worlds free of modded structures.
             * Also that vanilla superflat is really tricky and buggy to work with in my experience.
             */
            if(chunkGenerator instanceof FlatChunkGenerator && worldKey.equals(World.OVERWORLD)) {
                return;
            }

            /*
             * This part is how people will add their structures to dimensions or remove from dimensions.
             * Thus allowing a dimensional whitelisting/blacklisting to be possible and safe.
             *
             * This adds the Bastion to Overworld and End dimensions.
             */
            if(worldKey.equals(World.OVERWORLD) || worldKey.equals(World.END)) {
                chunkGenerator.getSettings().structureConfig()
                        .putIfAbsent(Structure.BASTION_REMNANT, DimensionStructuresSettings.DEFAULTS.get(Structure.BASTION_REMNANT));
            }
            // Remove Bastion from all other dimensions.
            else {
                chunkGenerator.getSettings().structureConfig().remove(Structure.BASTION_REMNANT);
            }
        }
    }
}
