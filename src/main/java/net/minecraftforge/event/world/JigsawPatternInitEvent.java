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
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.loading.AdvancedLogMessageAdapter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This event is called every time a {@link JigsawPattern} is created. Use this event to modify the pools of the JigsawPattern
 * <br>
 * Use {@link #addBuilding(ResourceLocation, JigsawPiece, int)} to add custom JigsawPiece to structure pools
 * <br>
 * Use {@link #removeBuilding(ResourceLocation, ResourceLocation)} to remove a JigsawPiece from structure pools.
 * <br>
 * <b> Be careful to not to remove all JigsawPieces from the startPool ("village/plains/town_centers" for Village and "pillager_outpost/base_plates" for PillageOutpost) </b>
 *
 */
public class JigsawPatternInitEvent extends Event {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Marker JIGSAWPDUMP = MarkerManager.getMarker("JIGSAWPATTERN");

    /** Maps from {@link JigsawCategory} registryNames to weighed {@link JigsawPiece}s which should be added to the category */
    private final Map<ResourceLocation, List<Pair<JigsawPiece, Integer>>> newBuildings = Maps.newHashMap();
    /** Maps from {@link JigsawCategory} registryNames to {@link JigsawPiece} registryNames which should be removed from this category */
    private final Map<ResourceLocation, List<ResourceLocation>> removeBuildings = Maps.newHashMap();
    /** Maps from category registryName to new {@link JigsawCategory} */
    private final Map<ResourceLocation, JigsawCategory> newCategories = Maps.newHashMap();
    /** {@link JigsawCategory} registryNames to remove from the {@link JigsawPattern}*/
    private final List<ResourceLocation> removeCategories = Lists.newArrayList();

    /** {@link JigsawPattern} registryName */
    public final ResourceLocation jigsawPatternRegistryName;
    /** Maps from category registryName to existing {@link JigsawCategory} */
    private final Map<ResourceLocation, JigsawCategory> jigsawPieces = Maps.newHashMap();

    public JigsawPatternInitEvent(ResourceLocation location, List<JigsawCategory> jigsawPieces)
    {
        this.jigsawPatternRegistryName = location;
        jigsawPieces.forEach((category -> this.jigsawPieces.put(category.getRegistryName(), category)));
    }

    /**
     * Adds a new {@link JigsawCategory} to the {@link JigsawPattern}.
     *
     * @param registryName registryName of the category
     * @param weight {@link JigsawPattern}s internal weight between categories
     * @param pieces List of JigsawPieces with {@link JigsawCategory}s internal weight between jigsawpieces
     */
    public void addCategory(@Nonnull ResourceLocation registryName, int weight, @Nonnull List<Pair<JigsawPiece,Integer>> pieces)
    {
        if(this.jigsawPieces.containsKey(registryName))
        {
            LOGGER.debug("the category with the registryName {} already exists", registryName);
        }
        else if(this.newCategories.containsKey(registryName))
        {
            LOGGER.debug("a category with the registryName {} was already added. Adding pieces to category", registryName);
            JigsawCategory category = this.newCategories.get(registryName);
            category.addJigsawPieces(pieces);
        }
        else
            this.newCategories.put(registryName, new JigsawCategory(registryName, weight, pieces));
    }

    /**
     * Simply deletes to corresponding {@link JigsawCategory} if existent
     * @param registryName RegistryName of the {@link JigsawCategory} to be removed
     */
    public void removeCategory(@Nonnull ResourceLocation registryName)
    {
        this.removeCategories.add(registryName);
    }

    /**
     * Add single JigsawPiece with weight to a pool
     *
     * @param category The category the building should be added. If null the default one will be selected. Some {@link JigsawPattern} may not contains the default category.
     * @param piece The structure that should be added
     * @param weight The weight of the structure
     */
    public void addBuilding(@Nonnull ResourceLocation category, @Nonnull JigsawPiece piece, int weight)
    {
        this.newBuildings.computeIfAbsent(category, categoryName -> Lists.newArrayList()).add(Pair.of(piece, weight));
    }

    /**
     * Add multiple JigsawPieces with weight to a pool at the same time
     *
     * @param category The category the building should be added. If null the default one will be selected. Some {@link JigsawPattern} may not contains the default category.
     * @param pieces A list of JigsawPieces and weights that should be added to the pool
     */
    public void addBuildings(@Nonnull ResourceLocation category, @Nonnull List<Pair<JigsawPiece, Integer>> pieces)
    {
        this.newBuildings.computeIfAbsent(category, categoryName -> Lists.newArrayList()).addAll(pieces);
    }

    /**
     * Removes single JigsawPiece from a JigsawPattern
     *
     * @param category The category the building should be added. If null the default one will be selected. Some {@link JigsawPattern} may not contains the default category.
     * @param building The identifier of the JigsawPiece {@link JigsawPiece#getRegistryName()} (see e.g. {@link net.minecraft.world.gen.feature.structure.PlainsVillagePools}
     */
    public void removeBuilding(@Nonnull ResourceLocation category, @Nonnull ResourceLocation building)
    {
        this.removeBuildings.computeIfAbsent(category, categoryName -> Lists.newArrayList()).add(building);
    }

    /**
     * Removes multiple JigsawPieces from a JigsawPattern
     *
     * @param category The category the building should be added. If null the default one will be selected. Some {@link JigsawPattern} may not contains the default category.
     * @param buildings The identifiers of the JigsawPieces {@link JigsawPiece#getRegistryName()} (see e.g. {@link net.minecraft.world.gen.feature.structure.PlainsVillagePools}
     */
    public void removeBuildings(@Nonnull ResourceLocation category, @Nonnull List<ResourceLocation> buildings)
    {
        removeBuildings.computeIfAbsent(category, categoryName -> Lists.newArrayList()).addAll(buildings);
    }

    /**
     * Applies the changes requested by {@link #newBuildings}, {@link #removeBuildings}, {@link #newCategories}
     * and {@link #removeCategories} to the given {@link JigsawCategory}s.
     * <br>
     * At the end prints all changes to console in debug mode
     *
     * @return computed {@link JigsawCategory}s for a {@link JigsawPattern}
     */
    public List<JigsawCategory> getPool()
    {
        for(ResourceLocation remove: this.removeCategories)
        {
            if(this.jigsawPieces.remove(remove) == null){
                LOGGER.debug("Tried to remove nonexistent JigsawCategory {} from JigsawPattern {}", remove, this.jigsawPatternRegistryName);
            }
        }
        this.jigsawPieces.putAll(this.newCategories);
        for (Map.Entry<ResourceLocation,List<ResourceLocation>> remove: this.removeBuildings.entrySet())
        {
            if(this.jigsawPieces.containsKey(remove.getKey())){
                this.jigsawPieces.get(remove.getKey()).removeJigsawPieces(remove.getValue());
            }
        }
        for(Map.Entry<ResourceLocation,List<Pair<JigsawPiece,Integer>>> entry: this.newBuildings.entrySet())
        {
            JigsawCategory category = this.jigsawPieces.get(entry.getKey());
            if(category == null){
                LOGGER.debug("Building cannot be added. JigsawCategory {} doesn't exits or were removed in JigsawPattern {}", entry.getKey(), this.jigsawPatternRegistryName);
            }else {
                category.addJigsawPieces(entry.getValue());
            }
        }
        this.printChanges();
        return Lists.newArrayList(this.jigsawPieces.values());
    }

    public boolean isPool(String name)
    {
        return this.jigsawPatternRegistryName.toString().equals(name);
    }

    private void printChanges(){
        if(this.removeCategories.size() != 0 || this.newCategories.size() != 0 || this.removeBuildings.size() != 0 || this.newBuildings.size() != 0)
        {
            LOGGER.debug(JIGSAWPDUMP, () -> new AdvancedLogMessageAdapter(stringBuilder ->
            {
                stringBuilder.append("JigsawPattern: ").append(this.jigsawPatternRegistryName).append('\n');
                if (this.removeCategories.size() != 0)
                {
                    AtomicInteger i = new AtomicInteger(0);
                    stringBuilder.append("\tRemoved Categories: ").append('\n');
                    this.removeCategories.forEach(resourceLocation ->
                    {
                        stringBuilder.append("\t\tEntry: ").append(i.getAndIncrement()).append(", ").append(resourceLocation).append('\n');
                    });
                }
                if (this.newCategories.size() != 0)
                {
                    AtomicInteger i = new AtomicInteger(0);
                    stringBuilder.append("\tNew Categories: ").append('\n');
                    this.newCategories.keySet().forEach(resourceLocation ->
                    {
                        stringBuilder.append("\t\tEntry: ").append(i.getAndIncrement()).append(", ").append(resourceLocation).append('\n');
                    });
                }
                if (this.removeBuildings.size() != 0)
                {
                    stringBuilder.append("\tRemoved JigsawPieces from Category").append('\n');
                    this.removeBuildings.entrySet().forEach((resourceLocationListEntry ->
                    {
                        AtomicInteger i = new AtomicInteger(0);
                        stringBuilder.append("\t\tFrom Category: ").append(resourceLocationListEntry.getKey()).append('\n');
                        resourceLocationListEntry.getValue().forEach(resourceLocation ->
                        {
                            stringBuilder.append("\t\t\tEntry: ").append(i.getAndIncrement()).append(", ").append(resourceLocation).append('\n');
                        });
                    }));
                }
                if (this.newBuildings.size() != 0)
                {
                    stringBuilder.append("\tAdded JigsawPieces to Category").append('\n');
                    this.newBuildings.entrySet().forEach((resourceLocationListEntry ->
                    {
                        AtomicInteger i = new AtomicInteger(0);
                        stringBuilder.append("\t\tTo Category: ").append(resourceLocationListEntry.getKey()).append('\n');
                        resourceLocationListEntry.getValue().forEach(pair ->
                        {
                            stringBuilder.append("\t\t\tEntry: ").append(i.getAndIncrement()).append(", ").append(pair.getFirst().getRegistryName()).append('\n');
                        });
                    }));
                }
            }));
        }
    }

    /**
     * This event should be fired whenever a Jigsaw bases structure initializes its pools.
     * Use this event to register custom JigsawPattern before the corresponding structure is constructed.
     */
    public static abstract class StructureJigsawPoolInitEvent extends Event
    {

        public void register(JigsawPattern jigsawPattern)
        {
            JigsawManager.field_214891_a.register(jigsawPattern);
        }

        public abstract ResourceLocation getStructureRegistryName();

        public static class Village extends StructureJigsawPoolInitEvent{
            private static boolean fired = false;

            public static void fire()
            {
                if(!fired)
                {
                    MinecraftForge.EVENT_BUS.post(new Village());
                    fired = true;
                }
            }

            @Override
            public ResourceLocation getStructureRegistryName()
            {
                return Structures.VILLAGE.getRegistryName();
            }
        }

        public static class PillageOutpost extends StructureJigsawPoolInitEvent
        {
            @Override
            public ResourceLocation getStructureRegistryName()
            {
                return Structures.PILLAGER_OUTPOST.getRegistryName();
            }
        }
    }

}
