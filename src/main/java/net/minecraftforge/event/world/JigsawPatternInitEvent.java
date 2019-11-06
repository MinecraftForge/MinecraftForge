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
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.feature.jigsaw.JigsawManager;
import net.minecraft.world.gen.feature.jigsaw.JigsawPattern;
import net.minecraft.world.gen.feature.jigsaw.JigsawPiece;
import net.minecraft.world.gen.feature.structure.Structures;
import net.minecraftforge.common.JigsawCategory;
import net.minecraftforge.eventbus.api.Event;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;

/**
 * This event is called every time a {@link JigsawPattern} is created. Use this event to modify the pools of the JigsawPattern
 * <br>
 * Use {@link #addBuilding(JigsawPiece, int)} to add custom JigsawPiece to structure pools
 * <br>
 * Use {@link #removeBuilding(ResourceLocation)} to remove a JigsawPiece from structure pools.
 * <b>Be careful to not to remove all JigsawPieces from the startPool ("village/plains/town_centers" for Village and "pillager_outpost/base_plates" for PillageOutpost)</b>
 *
 */
public class JigsawPatternInitEvent extends Event {
    private static final Logger LOGGER = LogManager.getLogger();
    private Map<ResourceLocation,List<Pair<JigsawPiece,Integer>>> newBuildings = Maps.newHashMap();
    private Map<ResourceLocation,List<ResourceLocation>> remove = Maps.newHashMap();
    private Map<ResourceLocation,JigsawCategory> newCategories = Maps.newHashMap();
    private List<ResourceLocation> removeCategories = Lists.newArrayList();

    public final ResourceLocation jigsawPoolName;
    private final Map<ResourceLocation,JigsawCategory> jigsawPieces = Maps.newHashMap();

    public JigsawPatternInitEvent(ResourceLocation location, List<JigsawCategory> jigsawPieces) {
        this.jigsawPoolName = location;
        jigsawPieces.forEach((category -> this.jigsawPieces.put(category.getRegistryName(),category)));
    }

    public void addCategory(List<Pair<JigsawPiece,Integer>> pieces, int weight, ResourceLocation registryName){
        if(jigsawPieces.containsKey(registryName)){
            LOGGER.warn("the category with the registryname {} already exists", registryName);
            return;
        }
        if(newCategories.containsKey(registryName)){
            LOGGER.warn("a category with the registryname {} was already added", registryName);
            return;
        }
        newCategories.put(registryName,new JigsawCategory(pieces, weight, registryName));
    }

    public void removeCategory(ResourceLocation registryName){
        removeCategories.add(registryName);
    }

    public void addBuilding(@Nonnull JigsawPiece piece, int weight)
    {
        this.addBuilding(JigsawCategory.DEFAULTCATEGORY,piece,weight);
    }
    /**
     * add single JigsawPiece with weight to a pool
     * @param piece The structure that should be added
     * @param weight The weight of the structure
     */
    public void addBuilding(ResourceLocation category, @Nonnull JigsawPiece piece, int weight)
    {
        newBuildings.computeIfAbsent(category, categoryName -> Lists.newArrayList()).add(new Pair<>(piece, weight));
    }

    public void addBuildings(@Nonnull List<Pair<JigsawPiece, Integer>> pieces)
    {
        this.addBuildings(JigsawCategory.DEFAULTCATEGORY,pieces);
    }
    /**
     * Add multiple JigsawPieces with weight to a pool at the same time
     * @param pieces A list of JigsawPieces and weights that should be added to the pool
     */
    public void addBuildings(ResourceLocation category, @Nonnull List<Pair<JigsawPiece, Integer>> pieces)
    {
        newBuildings.computeIfAbsent(category, categoryName -> Lists.newArrayList()).addAll(pieces);
    }

    public void removeBuilding(@Nonnull ResourceLocation building)
    {
        remove.computeIfAbsent(JigsawCategory.DEFAULTCATEGORY, categoryName -> Lists.newArrayList()).add(building);
    }
    /**
     * removes single JigsawPiece from a JigsawPattern
     * @param building The identifier of the JigsawPiece {@link JigsawPiece#getRegistryName()} (see e.g. {@link net.minecraft.world.gen.feature.structure.PlainsVillagePools}
     */
    public void removeBuilding(ResourceLocation category, @Nonnull ResourceLocation building)
    {
        remove.computeIfAbsent(category, categoryName -> Lists.newArrayList()).add(building);
    }

    /**
     * removes multiple JigsawPieces from a JigsawPattern
     * @param buildings The identifiers of the JigsawPieces {@link JigsawPiece#getRegistryName()} (see e.g. {@link net.minecraft.world.gen.feature.structure.PlainsVillagePools}
     */
    public void removeBuildings(@Nonnull List<ResourceLocation> buildings)
    {
        remove.computeIfAbsent(JigsawCategory.DEFAULTCATEGORY, categoryName -> Lists.newArrayList()).addAll(buildings);
    }
    /**
     * removes multiple JigsawPieces from a JigsawPattern
     * @param buildings The identifiers of the JigsawPieces {@link JigsawPiece#getRegistryName()} (see e.g. {@link net.minecraft.world.gen.feature.structure.PlainsVillagePools}
     */
    public void removeBuildings(ResourceLocation category, @Nonnull List<ResourceLocation> buildings)
    {
        remove.computeIfAbsent(category, categoryName -> Lists.newArrayList()).addAll(buildings);
    }

    public List<JigsawCategory> getPool(){
        for(ResourceLocation remove:removeCategories){
            jigsawPieces.remove(remove);
        }
        jigsawPieces.putAll(this.newCategories);
        jigsawPieces.values().forEach((category -> {
            if(remove.containsKey(category.getRegistryName())){
                category.getPieces().removeIf(pieceItem -> remove.get(category.getRegistryName()).contains(pieceItem.getFirst().getRegistryName()));
            }
        }));
        for(Map.Entry<ResourceLocation,List<Pair<JigsawPiece,Integer>>> entry:this.newBuildings.entrySet()){
            jigsawPieces.get(entry.getKey()).getPieces().addAll(entry.getValue());
        }
        return Lists.newArrayList(jigsawPieces.values());
    }

    public boolean isPool(String name){
        return jigsawPoolName.toString().equals(name);
    }

    /**
     * This event should be fired whenever a Jigsaw bases structure initializes its pools.
     * Use this event to register custom JigsawPattern before the corresponding structure is constructed.
     */
    public static abstract class StructureJigsawPoolInitEvent extends Event{

        public void register(JigsawPattern jigsawPattern){
            JigsawManager.field_214891_a.register(jigsawPattern);
        }

        public abstract ResourceLocation getStructureRegistryName();

        public static class Village extends StructureJigsawPoolInitEvent{
            private static boolean fired;

            public Village() {
                fired = true;
            }

            public static boolean isFired(){
                return fired;
            }

            @Override
            public ResourceLocation getStructureRegistryName() {
                return Structures.VILLAGE.getRegistryName();
            }
        }

        public static class PillageOutpost extends StructureJigsawPoolInitEvent{
            @Override
            public ResourceLocation getStructureRegistryName() {
                return Structures.PILLAGER_OUTPOST.getRegistryName();
            }
        }
    }
}
