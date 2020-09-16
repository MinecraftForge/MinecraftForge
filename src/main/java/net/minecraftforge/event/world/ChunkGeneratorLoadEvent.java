/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
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

import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.settings.DimensionStructuresSettings;
import net.minecraft.world.gen.settings.StructureSeparationSettings;
import net.minecraft.world.gen.settings.StructureSpreadSettings;
import net.minecraftforge.eventbus.api.Event;

import javax.annotation.Nullable;
import java.util.Map;

/**
 * This event fires whenever a new instance of a chunk generator is created.
 * It allows mods to edit the structure separation settings and the structure spread settings.
 *
 * To maintain the most possible compatibility with other mods' changes,
 * the event should be assigned a {@link net.minecraftforge.eventbus.api.EventPriority}:
 *
 * - Additions to the separation settings: {@link net.minecraftforge.eventbus.api.EventPriority#HIGH}
 * - Removals from the separation settings: {@link net.minecraftforge.eventbus.api.EventPriority#NORMAL}
 * - Other modifications: {@link net.minecraftforge.eventbus.api.EventPriority#LOW}
 *
 * This event is fired on the {@link net.minecraftforge.common.MinecraftForge#EVENT_BUS}
 */

public class ChunkGeneratorLoadEvent extends Event
{
    private final BiomeProvider biomeProvider;
    private final Map<Structure<?>, StructureSeparationSettings> structureSeparationSettings;
    private StructureSpreadSettings structureSpreadSettings;
    private final long seed;

    public ChunkGeneratorLoadEvent(BiomeProvider biomeProvider, DimensionStructuresSettings settings, long seed)
    {
        this.biomeProvider = biomeProvider;
        this.structureSeparationSettings = settings.func_236195_a_();
        this.structureSpreadSettings = settings.func_236199_b_();
        this.seed = seed;
    }

    public BiomeProvider getBiomeProvider()
    {
        return biomeProvider;
    }

    public Map<Structure<?>, StructureSeparationSettings> getStructureSeparationSettings()
    {
        return structureSeparationSettings;
    }

    @Nullable
    public StructureSpreadSettings getStructureSpreadSettings()
    {
        return structureSpreadSettings;
    }

    public long getSeed()
    {
        return seed;
    }

    public void setStructureSpreadSettings(StructureSpreadSettings structureSpreadSettings)
    {
        this.structureSpreadSettings = structureSpreadSettings;
    }
}
