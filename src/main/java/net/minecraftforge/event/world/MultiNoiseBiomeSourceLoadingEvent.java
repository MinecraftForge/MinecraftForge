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

import java.util.List;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import com.mojang.datafixers.util.Pair;

import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate.ParameterPoint;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;

/**
 * <p>Event for modifying multi-noise biome sources.
 * This event is fired whenever a MultiNoiseBiomeSource is deserialized during dimension loading.
 * This occurs after datapacks and worldgen registries load, but before chunk generators are constructed
 * (and before the biome source itself is constructed).
 * Standard tag wrappers are not resolvable at this time.</p>
 * 
 * <p>Best practice for use of this event:</p>
 * <ul>
 * <li>Additions during {@link EventPriority#HIGH}</li>
 * <li>Removals during {@link EventPriority#NORMAL}</li>
 * <li>Modifications during {@link EventPriority#LOW}</li>
 * </ul>
 * 
 * <p>Cancelling this event will prevent the biome source from being modified.</p>
 * 
 * <p>This event is fired on the {@link MinecraftForge.EVENT_BUS}.</p>
 */
@Cancelable
public class MultiNoiseBiomeSourceLoadingEvent extends Event
{
    private final List<Pair<ParameterPoint, Supplier<Biome>>> parameters;
    @Nullable
    private final ResourceLocation name;
    private final RegistryAccess registries;
    
    public MultiNoiseBiomeSourceLoadingEvent(List<Pair<ParameterPoint, Supplier<Biome>>> parameters, ResourceLocation name, RegistryAccess registries)
    {
        this.parameters = parameters;
        this.name = name;
        this.registries = registries;
    }
    
    /**
     * Convenience method for adding a single biome entry to the parameter list
     * @param point Biome entry parameters
     * @param biomeSupplier Biome supplier
     */
    public void addParameter(ParameterPoint point, Supplier<Biome> biomeSupplier)
    {
        this.getParameters().add(Pair.of(point, biomeSupplier));
    }

    /**
     * @return Mutable parameter list that can be modified as needed.
     */
    public List<Pair<ParameterPoint, Supplier<Biome>>> getParameters()
    {
        return parameters;
    }

    /**
     * Retrieves the name of this biome source.
     * For vanilla biome sources and preset-based biome sources, this will be the name of the preset (typically minecraft:overworld/nether/end).
     * Non-preset biome sources may specify the preset ID in the biome source json object with a "name" field.
     * When present, this should generally match the ID of the dimension this biome source belongs to.
     * If this is not present, this indicates that a datapack has defined this biome source and has not specified a name.
     * @return Biome source name, if present.
     */
    @Nullable
    public ResourceLocation getName()
    {
        return name;
    }

    /**
     * Retrieves the worldgen/dynamic registries, for reading biomes and other worldgen data.
     * @return Worldgen/dynamic registries
     */
    public RegistryAccess getRegistries()
    {
        return registries;
    }    
}
