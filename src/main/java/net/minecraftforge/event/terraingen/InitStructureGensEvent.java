/*
 * Minecraft Forge
 * Copyright (c) 2016.
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

package net.minecraftforge.event.terraingen;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.structure.MapGenStructure;
import net.minecraftforge.common.MapGenStructureManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.GenericEvent;

import java.util.Collections;
import java.util.List;

/**
 * Used to collect {@link MapGenStructure}s.
 * <p>
 * This event is posted on {@link MinecraftForge#TERRAIN_GEN_BUS} in the ChunkGenerator constructors to allow mods to register their own (additional) structures
 *
 * @param <T> Type of ChunkGenerator
 */
public class InitStructureGensEvent<T extends IChunkGenerator> extends GenericEvent<T>
{
    private final List<MapGenStructure> structureList = Lists.newArrayList();

    public InitStructureGensEvent(Class<T> generatorClass)
    {
        super(generatorClass);
    }

    public void addStructure(MapGenStructure struct)
    {
        structureList.add(struct);
    }

    public List<MapGenStructure> getStructuresImmutable()
    {
        return Collections.unmodifiableList(structureList);
    }
}
