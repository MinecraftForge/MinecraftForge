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

package net.minecraftforge.event.world;

import java.util.Map;

import javax.annotation.Nonnull;

import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.StructureFeatureConfiguration;
import net.minecraftforge.common.world.IBiomeParameters;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;

/**
 * Fires after dynamic registries (biomes, configuredfeatures, etc) have loaded
 * from datapacks, and before dimensions/chunkgenerators/biomeproviders load
 * from datapacks. Allows modification of biomes in-place using
 * dynamicregistries context (allowing e.g. adding features from datapacks to a
 * set of biomes specified by datapacks).
 * 
 * Attempting to *replace* top-level objects in the registries at this point or at server
 * runtime will break everything as value-key lookups are used in many places,
 * so don't do that.
 *
 * In order to maintain the most compatibility possible with other mods'
 * modifications to a biome, the event should be assigned a
 * {@link net.minecraftforge.eventbus.api.EventPriority} as follows:
 *
 * - Additions to any list/map contained in a biome : {@link EventPriority#HIGH}
 * - Removals to any list/map contained in a biome : {@link EventPriority#NORMAL}
 * - Any other modification : {@link EventPriority#LOW}
 * 
 * Mods that add worldgen features or spawning mobs and wish to add their
 * features to biomes can do so via this event. Custom biomes should declare any
 * vanilla features in their biome json.
 * 
 * This event is guaranteed to provide a unique and unmodified set of dynamic
 * registry objects each time the event fires, though the event may fire several
 * times when datapacks are imported multiple times.
 * 
 * Important! Don't use this event to add features-created-in-java to biomes,
 * configuredfeatures should be defined in json and retrieved from the registries.
 * This will allow datapacks to modify your configuredfeatures.
 */
public class DynamicRegistriesLoadedEvent extends Event
{
    private final @Nonnull RegistryAccess dataRegistries;
    private final @Nonnull Map<ResourceKey<Biome>, IBiomeParameters> biomeModifiers;
    private final @Nonnull Map<ResourceKey<NoiseGeneratorSettings>, Map<StructureFeature<?>, StructureFeatureConfiguration>> structureConfigs;

    public DynamicRegistriesLoadedEvent(final @Nonnull RegistryAccess dataRegistries, final @Nonnull Map<ResourceKey<Biome>, IBiomeParameters> biomeModifiers, final @Nonnull Map<ResourceKey<NoiseGeneratorSettings>, Map<StructureFeature<?>, StructureFeatureConfiguration>> structureConfigs)
    {
        this.dataRegistries = dataRegistries;
        this.biomeModifiers = biomeModifiers;
        this.structureConfigs = structureConfigs;
    }

    /**
     * Access to the data registries containing the biomes being modified. Contains
     * biomes, features, dimensiontypes, etc. These have been fully loaded and
     * pasted over any builtin vanilla objects.
     * 
     * @return The registries containing the builtin WorldGenRegistries objects as
     *         well as any datapack objects that have been added to the registries
     *         or have replaced builtin objects in them. No objects in this
     *         registries instance have been provided to a previous
     *         DynamicRegistriesLoadedEvent.
     */
    public RegistryAccess getDataRegistries()
    {
        return this.dataRegistries;
    }

    /**
     * Gets the map of biome modifiers. Any modifications made by event listeners to
     * these parameters will be applied to biomes at the end of the event.
     * 
     * These parameters are initially in the same state as the original biome loaded
     * from builtin registries or from datapacks, but may have been modified by
     * other event listeners before being given to your own listener.
     * 
     * Add features to your biome by adding them to the generation builder via this,
     * it's nicer than copying the biome's immutable feature lists
     * 
     * @return biome modifiers by biome key
     */
    public Map<ResourceKey<Biome>, IBiomeParameters> getBiomeModifiers()
    {
        return this.biomeModifiers;
    }
    
    /**
     * Gets the map of structure configs. The maps of structures-to-seperation-settings are
     * mutable and can have structure spacings added to them as needed.
     * @return structure seperation maps by noise setting key
     */
    public Map<ResourceKey<NoiseGeneratorSettings>, Map<StructureFeature<?>, StructureFeatureConfiguration>> getStructureSeparations()
    {
        return this.structureConfigs;
    }
}
