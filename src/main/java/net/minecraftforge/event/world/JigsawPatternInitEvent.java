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

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.feature.jigsaw.JigsawManager;
import net.minecraft.world.gen.feature.jigsaw.JigsawPattern;
import net.minecraft.world.gen.feature.jigsaw.JigsawPiece;
import net.minecraft.world.gen.feature.structure.Structures;
import net.minecraftforge.eventbus.api.Event;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * This event should be called for all jigsaw-based structures after all basic JigsawPieces are initialized.
 * <br>
 * Use {@link #addBuilding(JigsawPiece, int)} to add custom JigsawPiece to structure pools
 * <br>
 * Use {@link #removeBuilding(ResourceLocation)} to remove a JigsawPiece from structure pools.
 * <b>Be careful to not to remove all JigsawPieces from the startPool("village/plains/town_centers" for Village and "pillager_outpost/base_plates" for PillageOutpost</b>
 *
 */
public class JigsawPatternInitEvent extends Event {
    private List<Pair<JigsawPiece,Integer>> newBuildings = Lists.newArrayList();
    private List<ResourceLocation> remove = Lists.newArrayList();

    public final ResourceLocation jigsawPoolName;
    private final List<Pair<JigsawPiece, Integer>> jigsawPieces;

    public JigsawPatternInitEvent(ResourceLocation location, List<Pair<JigsawPiece, Integer>> jigsawPieces) {
        this.jigsawPoolName = location;
        this.jigsawPieces = jigsawPieces;
    }

    /**
     * add single JigsawPiece with weight to a pool
     * @param piece The structure that should be added
     * @param weight The weight of the structure
     */
    public void addBuilding(@Nonnull JigsawPiece piece, int weight)
    {
        newBuildings.add(new Pair<>(piece, weight));
    }

    /**
     * Add multiple JigsawPieces with weight to a pool at the same time
     * @param pieces A list of JigsawPieces and weights that should be added to the pool
     */
    public void addBuildings(@Nonnull List<Pair<JigsawPiece, Integer>> pieces)
    {
        newBuildings.addAll(pieces);
    }

    /**
     * removes single JigsawPiece from a JigsawPattern
     * @param building The identifier of the JigsawPiece {@link JigsawPiece#getRegistryName()} (see e.g. {@link net.minecraft.world.gen.feature.structure.PlainsVillagePools}
     */
    public void removeBuilding(@Nonnull ResourceLocation building)
    {
        remove.add(building);
    }

    /**
     * removes multiple JigsawPieces from a JigsawPattern
     * @param buildings The identifiers of the JigsawPieces {@link JigsawPiece#getRegistryName()} (see e.g. {@link net.minecraft.world.gen.feature.structure.PlainsVillagePools}
     */
    public void removeBuildings(@Nonnull List<ResourceLocation> buildings)
    {
        remove.addAll(buildings);
    }

    public List<Pair<JigsawPiece, Integer>> getPool(){
        jigsawPieces.removeIf((jigsawPieceIntegerPair -> remove.contains(jigsawPieceIntegerPair.getFirst().getRegistryName())));
        jigsawPieces.addAll(newBuildings);
        return jigsawPieces;
    }

    public boolean isPool(String name){
        return jigsawPoolName.toString().equals(name);
    }

    public static abstract class StructureJigsawPoolInitEvent extends Event{

        public void register(JigsawPattern jigsawPattern){
            JigsawManager.field_214891_a.register(jigsawPattern);
        }

        public abstract ResourceLocation getStructureResourceLocation();

        public static class Village extends StructureJigsawPoolInitEvent{
            @Override
            public ResourceLocation getStructureResourceLocation() {
                return Structures.VILLAGE.getRegistryName();
            }
        }

        public static class PillageOutpost extends StructureJigsawPoolInitEvent{
            @Override
            public ResourceLocation getStructureResourceLocation() {
                return Structures.PILLAGER_OUTPOST.getRegistryName();
            }
        }
    }
}
