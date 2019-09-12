/*
 * Minecraft Forge
 * Copyright (c) 2016-2019.
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

package net.minecraftforge.event;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.feature.jigsaw.JigsawPiece;
import net.minecraftforge.eventbus.api.Event;

import java.util.List;
import java.util.Map;

/**
 * This event is called after all village
 * {@link net.minecraft.world.gen.feature.jigsaw.JigsawPattern} and {@link net.minecraft.world.gen.feature.jigsaw.JigsawPiece} are initialized.
 *
 */
public class VillageStructureInitEvent extends Event {
    private Map<ResourceLocation,ImmutableList.Builder<Pair<JigsawPiece,Integer>>> buildings = Maps.newConcurrentMap();

    /**
     * add single JigsawPiece with weight to a pool
     * @param type The structure pool where the building should be added
     * @param piece The structure that should be added
     * @param weight The weight of the structure
     */
    public void addBuilding(ResourceLocation type, JigsawPiece piece, int weight)
    {
        if(buildings.containsKey(type))
            buildings.get(type).add(new Pair<>(piece, weight));
        else
            buildings.put(type,new ImmutableList.Builder<Pair<JigsawPiece, Integer>>().add(new Pair<>(piece, weight)));
    }

    /**
     * Add multiple JigsawPieces with weight to a pool at the same time
     * @param type The structure pool where the buildings should be added
     * @param pieces A list of JigsawPieces and weights that should be added to the pool
     */
    public void addBuildings(ResourceLocation type, List<Pair<JigsawPiece, Integer>> pieces)
    {
        if(buildings.containsKey(type))
            buildings.get(type).addAll(pieces);
        else
            buildings.put(type,new ImmutableList.Builder<Pair<JigsawPiece, Integer>>().addAll(pieces));
    }

    Map<ResourceLocation, ImmutableList<Pair<JigsawPiece, Integer>>> getBuildings(){
        Map<ResourceLocation, ImmutableList<Pair<JigsawPiece, Integer>>> builds = Maps.newConcurrentMap();
        for (Map.Entry<ResourceLocation,ImmutableList.Builder<Pair<JigsawPiece, Integer>>> entry: buildings.entrySet()){
            builds.put(entry.getKey(),entry.getValue().build());
        }
        return builds;
    }
}
