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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.entity.EntityClassification;
import net.minecraft.world.biome.MobSpawnInfo;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraftforge.eventbus.api.Event;

//TODO: Docs
public class StructureSpawnListGatherEvent extends Event
{

    private final Structure<?> structure;
    private final Map<EntityClassification, List<MobSpawnInfo.Spawners>> entitySpawns = new HashMap<>();
    private boolean checkPositionInStructurePieces;

    public StructureSpawnListGatherEvent(Structure<?> structure)
    {
        this.structure = structure;
        //The Pillager Outpost and Ocean Monument check the full structure by default instead of limiting themselves to being within the structure's bounds
        //TODO: Allow custom structures to default this for themselves
        this.checkPositionInStructurePieces = this.structure != Structure.field_236366_b_ && this.structure != Structure.field_236376_l_;
        if (!this.structure.getSpawnList().isEmpty())
            this.entitySpawns.put(EntityClassification.MONSTER, new ArrayList<>(this.structure.getSpawnList()));
        if (!this.structure.getCreatureSpawnList().isEmpty())
            this.entitySpawns.put(EntityClassification.CREATURE, new ArrayList<>(this.structure.getCreatureSpawnList()));
    }

    //TODO: Should we even be exposing the structure as we are doing this from the constructor
    // so it may not b fully initialized in terms of the stuff the sub classes want to do in their constructors
    // The registry name is also likely not set yet, but for precision modification it can probably be done via
    // object comparison
    public Structure<?> getStructure()
    {
        return structure;
    }

    public void onlyCheckInsideStructurePieces()
    {
        this.checkPositionInStructurePieces = true;
    }

    public void checkOutsideStructurePieces()
    {
        this.checkPositionInStructurePieces = false;
    }

    public boolean getOnlyChecksInside()
    {
        return checkPositionInStructurePieces;
    }

    public void addEntityClassification(EntityClassification classification)
    {
        //TODO: Should we require that they also pass a spawner in to populate this classification?
        this.entitySpawns.putIfAbsent(classification, new ArrayList<>());
    }

    //TODO: We probably want a better way of adding than having this map be modified directly, such as with some helpers
    public Map<EntityClassification, List<MobSpawnInfo.Spawners>> getEntitySpawns()
    {
        return entitySpawns;
    }
}