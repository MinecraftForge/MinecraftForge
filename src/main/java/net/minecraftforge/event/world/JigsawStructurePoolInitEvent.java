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

package net.minecraftforge.event.world;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
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

    private static List<ResourceLocation> computed = Lists.newArrayList();

    /**
     * add single JigsawPiece with weight to a pool
     * @param type The structure pool where the building should be added
     * @param piece The structure that should be added
     * @param weight The weight of the structure
     */
    public void addBuilding(@Nonnull ResourceLocation type, @Nonnull JigsawPiece piece, int weight)
    {
        newBuildings.computeIfAbsent(type,(resourceLocation -> new ImmutableList.Builder<>())).add(new Pair<>(piece, weight));
    }

    /**
     * Add multiple JigsawPieces with weight to a pool at the same time
     * @param type The structure pool where the buildings should be added
     * @param pieces A list of JigsawPieces and weights that should be added to the pool
     */
    public void addBuildings(@Nonnull ResourceLocation type, @Nonnull List<Pair<JigsawPiece, Integer>> pieces)
    {
        newBuildings.computeIfAbsent(type,(resourceLocation -> new ImmutableList.Builder<>())).addAll(pieces);
    }

    /**
     * removes single JigsawPiece from a JigsawPattern
     * @param type The ResourceLocation of the JigsawPattern
     * @param building The identifier of the JigsawPiece {@link JigsawPiece#getRegistryName()} (see e.g. {@link net.minecraft.world.gen.feature.structure.PlainsVillagePools}
     */
    public void removeBuilding(@Nonnull ResourceLocation type, @Nonnull ResourceLocation building)
    {
        remove.computeIfAbsent(type,resourceLocation -> new ImmutableList.Builder<>()).add(building);
    }

    /**
     * removes multiple JigsawPieces from a JigsawPattern
     * @param type The ResourceLocation of the JigsawPattern
     * @param buildings The identifiers of the JigsawPieces {@link JigsawPiece#getRegistryName()} (see e.g. {@link net.minecraft.world.gen.feature.structure.PlainsVillagePools}
     */
    public void removeBuildings(@Nonnull ResourceLocation type, @Nonnull List<ResourceLocation> buildings)
    {
        remove.computeIfAbsent(type,resourceLocation -> new ImmutableList.Builder<>()).addAll(buildings);
    }

    /**
     * method forwarding for compatibility. {@link net.minecraft.world.gen.feature.jigsaw.JigsawPatternRegistry#register(JigsawPattern)}
     */
    public void registerNewJigsawPattern(@Nonnull JigsawPattern pattern)
    {
        JigsawManager.field_214891_a.register(pattern);
    }

    public void computeChanges()
    {
        if(computed.contains(this.getStructureRegistryName()))return;
        for (Map.Entry<ResourceLocation,ImmutableList.Builder<ResourceLocation>> entry: remove.entrySet())
        {
            JigsawPattern pattern = JigsawManager.field_214891_a.get(entry.getKey());
            pattern.field_214952_d.removeIf(pair -> entry.getValue().build().contains(pair.getFirst().getRegistryName()));
            rebuildPattern(pattern);
        }
        for (Map.Entry<ResourceLocation,ImmutableList.Builder<Pair<JigsawPiece,Integer>>> entry: newBuildings.entrySet())
        {
            JigsawPattern pattern = JigsawManager.field_214891_a.get(entry.getKey());
            pattern.field_214952_d.addAll(entry.getValue().build());
            rebuildPattern(pattern);
        }
        computed.add(this.getStructureRegistryName());
    }

    private void rebuildPattern(JigsawPattern pattern)
    {
        pattern.field_214953_e.clear();
        for(Pair<JigsawPiece, Integer> pair: pattern.field_214952_d)
            for(Integer integer = 0; integer < pair.getSecond(); integer = integer + 1)
                pattern.field_214953_e.add(pair.getFirst().setPlacementBehaviour(pattern.field_214955_g));
    }

    public boolean alreadyComputed()
    {
        return computed.contains(getStructureRegistryName());
    }

    public abstract ResourceLocation getStructureRegistryName();

    public static class Village extends JigsawStructurePoolInitEvent
    {
        @Override
        public ResourceLocation getStructureRegistryName()
        {
            return Structures.VILLAGE.getRegistryName();
        }

    }

    public static class PillageOutpost extends JigsawStructurePoolInitEvent
    {
        @Override
        public ResourceLocation getStructureRegistryName()
        {
            return Structures.PILLAGER_OUTPOST.getRegistryName();
        }
    }
}
