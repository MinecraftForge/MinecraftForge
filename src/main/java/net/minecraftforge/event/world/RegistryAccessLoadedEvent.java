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
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraftforge.common.world.IBiomeParameters;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;

/**
 * <p>Fires after dynamic registries (biomes, places/configured features, etc) have loaded
 * from datapacks, and before dimensions/chunkgenerators/biomesources load
 * from datapacks. Allows modification of biomes in-place using
 * registryaccess context (allowing e.g. adding features from datapacks to a
 * set of biomes specified by datapacks).</p>
 * 
 * <p>Attempting to *replace* top-level objects in the registries at this point or at server
 * runtime will break everything as value-key lookups are used in many places,
 * so don't do that.</p>
 *
 * <p>In order to maintain the most compatibility possible with other mods'
 * modifications to a biome, the event should be assigned a
 * {@link net.minecraftforge.eventbus.api.EventPriority} as follows:</p>
 *
 * <ul>
 * <li>Additions to any list/map contained in a biome : {@link EventPriority#HIGH}
 * <li>Removals to any list/map contained in a biome : {@link EventPriority#NORMAL}
 * <li>Any other modification : {@link EventPriority#LOW}
 * </ul>
 * 
 * <p>Mods that add worldgen features or spawning mobs and wish to add their
 * features to biomes can do so via this event. Custom biomes should declare any
 * vanilla features in their biome json.</p>
 * 
 * <p>This event is guaranteed to provide a unique and unmodified set of dynamic
 * registry objects each time the event fires, though the event may fire several
 * times when datapacks are imported multiple times.</p>
 * 
 * <p>Important! Don't use this event to add features-created-in-java to biomes,
 * placedfeatures should be defined in json and retrieved from the registries.
 * This will allow datapacks to override your features.</p>
 */
public class RegistryAccessLoadedEvent extends Event
{
    private final @Nonnull RegistryAccess registryAccess;
    private final @Nonnull Map<ResourceKey<Biome>, IBiomeParameters> biomeModifiers;

    public RegistryAccessLoadedEvent(final @Nonnull RegistryAccess registryAccess, final @Nonnull Map<ResourceKey<Biome>, IBiomeParameters> biomeModifiers)
    {
        this.registryAccess = registryAccess;
        this.biomeModifiers = biomeModifiers;
    }

    /**
     * Access to the data registries containing the biomes being modified. Contains
     * biomes, placed/configured features, etc. These have been fully loaded and
     * pasted over any builtin vanilla objects.
     * 
     * @return The registries containing the {@link BuiltinRegistries} objects as
     *         well as any datapack objects that have been added to the registries
     *         or have replaced builtin objects in them. No objects in this
     *         registries instance have been provided to a previous
     *         DynamicRegistriesLoadedEvent (except possibly {@link DimensionType} and
     *         {@link NoiseGeneratorSettings}, which are as of this time not deep-copied by vanilla
     *         before datapacks load).
     */
    public RegistryAccess getRegistryAccess()
    {
        return this.registryAccess;
    }

    /**
     * <p>Gets the map of biome modifiers. Any modifications made by event listeners to
     * these parameters will be applied to biomes at the end of the event. You can add features
     * to biomes by adding them to the generation builder provided in the biome parameters.</p>
     * 
     * <p>These parameters are initially in the same state as the original biome loaded
     * from builtin registries or from datapacks, but may have been modified by
     * other event listeners before being given to your own listener.</p>
     * 
     * @return biome modifiers by biome key
     */
    public Map<ResourceKey<Biome>, IBiomeParameters> getBiomeModifiers()
    {
        return this.biomeModifiers;
    }
}
