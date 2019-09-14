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

package net.minecraftforge.event.world.jigsaw;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.feature.jigsaw.JigsawManager;
import net.minecraft.world.gen.feature.jigsaw.JigsawPattern;
import net.minecraft.world.gen.feature.jigsaw.JigsawPiece;
import net.minecraft.world.gen.feature.structure.Structures;
import net.minecraftforge.eventbus.api.Event;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;

/**
 * This event should be called for all jigsaw-based structures after all basic JigsawPieces are initialized.
 * <br>
 * Use {@link #addBuilding(ResourceLocation, JigsawPiece, int)} to add custom JigsawPiece to structure pools
 * <br>
 * Use {@link #removeBuilding(ResourceLocation, ResourceLocation)} to remove a JigsawPiece from structure pools.
 * <b>Be careful to not to remove all JigsawPieces from the startPool("village/plains/town_centers" for Village and "pillager_outpost/base_plates" for PillageOutpost</b>
 * <br>
 * Use {@link #registerNewJigsawPattern(JigsawPattern)} to register new JigsawPattern
 *
 */
public abstract class JigsawStructurePoolInitEvent extends Event {
    private Map<ResourceLocation,ImmutableList.Builder<Pair<JigsawPiece,Integer>>> newBuildings = Maps.newConcurrentMap();
    private Map<ResourceLocation,ImmutableList.Builder<ResourceLocation>> remove = Maps.newConcurrentMap();

    /**
     * add single JigsawPiece with weight to a pool
     * @param type The structure pool where the building should be added
     * @param piece The structure that should be added
     * @param weight The weight of the structure
     */
    public void addBuilding(@Nonnull ResourceLocation type, @Nonnull JigsawPiece piece, int weight)
    {
        if(newBuildings.containsKey(type))
            newBuildings.get(type).add(new Pair<>(piece, weight));
        else
            newBuildings.put(type,new ImmutableList.Builder<Pair<JigsawPiece, Integer>>().add(new Pair<>(piece, weight)));
    }

    /**
     * Add multiple JigsawPieces with weight to a pool at the same time
     * @param type The structure pool where the buildings should be added
     * @param pieces A list of JigsawPieces and weights that should be added to the pool
     */
    public void addBuildings(@Nonnull ResourceLocation type, @Nonnull List<Pair<JigsawPiece, Integer>> pieces)
    {
        if(newBuildings.containsKey(type))
            newBuildings.get(type).addAll(pieces);
        else
            newBuildings.put(type,new ImmutableList.Builder<Pair<JigsawPiece, Integer>>().addAll(pieces));
    }

    /**
     * removes single JigsawPiece from a JigsawPattern
     * @param type The ResourceLocation of the JigsawPattern
     * @param building The identifier of the JigsawPiece {@link JigsawPiece#getIdentifier()} (see e.g. {@link net.minecraft.world.gen.feature.structure.PlainsVillagePools}
     */
    public void removeBuilding(@Nonnull ResourceLocation type, @Nonnull ResourceLocation building)
    {
        if(remove.containsKey(type))
            remove.get(type).add(building);
        else
            remove.put(type,new ImmutableList.Builder<ResourceLocation>().add(building));
    }

    /**
     * removes multiple JigsawPieces from a JigsawPattern
     * @param type The ResourceLocation of the JigsawPattern
     * @param buildings The identifiers of the JigsawPieces {@link JigsawPiece#getIdentifier()} (see e.g. {@link net.minecraft.world.gen.feature.structure.PlainsVillagePools}
     */
    public void removeBuildings(@Nonnull ResourceLocation type, @Nonnull List<ResourceLocation> buildings)
    {
        if(remove.containsKey(type))
            remove.get(type).addAll(buildings);
        else
            remove.put(type,new ImmutableList.Builder<ResourceLocation>().addAll(buildings));
    }

    /**
     * method forwarding for compatibility. {@link net.minecraft.world.gen.feature.jigsaw.JigsawPatternRegistry#register(JigsawPattern)}
     */
    public void registerNewJigsawPattern(@Nonnull JigsawPattern pattern)
    {
        JigsawManager.field_214891_a.register(pattern);
    }

    @Nonnull
    public Map<ResourceLocation, ImmutableList<Pair<JigsawPiece, Integer>>> getNewBuildings()
    {
        Map<ResourceLocation, ImmutableList<Pair<JigsawPiece, Integer>>> builds = Maps.newConcurrentMap();
        for (Map.Entry<ResourceLocation,ImmutableList.Builder<Pair<JigsawPiece, Integer>>> entry: newBuildings.entrySet()){
            builds.put(entry.getKey(),entry.getValue().build());
        }
        return builds;
    }

    @Nonnull
    public Map<ResourceLocation, ImmutableList<ResourceLocation>> getRemoveBuildings()
    {
        Map<ResourceLocation, ImmutableList<ResourceLocation>> builds = Maps.newConcurrentMap();
        for (Map.Entry<ResourceLocation,ImmutableList.Builder<ResourceLocation>> entry: remove.entrySet()){
            builds.put(entry.getKey(),entry.getValue().build());
        }
        return builds;
    }

    @Nonnull
    public abstract String getStructureName();

    public static class Village extends JigsawStructurePoolInitEvent
    {
        @Nonnull
        @Override
        public String getStructureName()
        {
            return Structures.VILLAGE.getStructureName();
        }
    }

    public static class PillageOutpost extends JigsawStructurePoolInitEvent
    {
        @Nonnull
        @Override
        public String getStructureName()
        {
            return Structures.PILLAGER_OUTPOST.getStructureName();
        }
    }
}
