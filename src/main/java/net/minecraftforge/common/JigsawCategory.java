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

package net.minecraftforge.common;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.feature.jigsaw.JigsawPattern;
import net.minecraft.world.gen.feature.jigsaw.JigsawPiece;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * JigsawCategory which is used to organize the {@link JigsawPiece}s inside a {@link JigsawPattern}.
 * <br>
 * Additionally preserves the weight of the different {@link JigsawPiece} types if new JigsawPieces were added or removed
 */
public class JigsawCategory {

    /** All weighted {@link JigsawPiece}s inside this {@link JigsawCategory} */
    private final List<Pair<JigsawPiece, Integer>> pieces;
    /** Weight of this category inside a {@link JigsawPattern}*/
    private final int categoryWeight;
    /** RegistryName of this category */
    private final ResourceLocation registryName;
    /** Total weight of all {@link JigsawPiece}s in {@link #pieces} */
    private int totalPieceWeight;

    /**
     * @param registryName RegistryName of this Category
     * @param categoryWeight Weight of this Category
     * @param pieces Weighted JigsawPieces for this category
     */
    public JigsawCategory(ResourceLocation registryName, int categoryWeight, List<Pair<JigsawPiece, Integer>> pieces) {
        this.pieces = Lists.newArrayList(pieces);
        this.categoryWeight = categoryWeight;
        this.registryName = registryName;
        this.totalPieceWeight = pieces.stream().mapToInt(Pair::getSecond).sum();
    }

    /**
     * @return All weighed {@link JigsawPiece}s of this category
     */
    public List<Pair<JigsawPiece, Integer>> getPieces()
    {
        return ImmutableList.copyOf(pieces);
    }

    /**
     * @return RegistryName of this JigsawCategory
     */
    public ResourceLocation getRegistryName()
    {
        return registryName;
    }

    /**
     * @return The weight of this category inside a {@link JigsawPattern}
     */
    public int getCategoryWeight()
    {
        return categoryWeight;
    }

    /**
     * @return The total weight of all {@link JigsawPiece}s inside this category
     */
    public int getTotalPieceWeight()
    {
        return totalPieceWeight;
    }

    /**
     * Recalculated {@link #totalPieceWeight}
     */
    private void recalculateTotalWeight(){
        this.totalPieceWeight = pieces.stream().mapToInt(Pair::getSecond).sum();
    }

    /**
     * Adds new {@link JigsawPiece}s to this category
     *
     * @param pieces Weighted {@link JigsawPiece}s to add to this category
     */
    public void addJigsawPieces(Collection<Pair<JigsawPiece, Integer>> pieces)
    {
        this.pieces.addAll(pieces);
        this.recalculateTotalWeight();
    }

    /**
     * Removes {@link JigsawPiece}s from this category
     *
     * @param registryNames {@link Collection} of JigsawPiece RegistryNames to remove
     */
    public void removeJigsawPieces(Collection<ResourceLocation> registryNames){
        this.pieces.removeIf(weightedPair -> registryNames.contains(weightedPair.getFirst().getRegistryName()));
        this.recalculateTotalWeight();
    }



    /**
     * Creates a list of Categories for the {@link JigsawPattern} based on the RegistryName of the JigsawPieces
     * for which the JigsawPattern should be created.
     *
     * @param pool all weighted {@link JigsawPiece}s inside the {@link JigsawPattern}
     * @param jigsawPattern registryName of the {@link JigsawPattern}
     * @return list of created categories inside the {@link JigsawPattern}
     */
    public static List<JigsawCategory> convertToCategories(List<Pair<JigsawPiece, Integer>> pool, ResourceLocation jigsawPattern)
    {
        Map<String, List<Pair<JigsawPiece,Integer>>> categoryToJigsawPair = Maps.newHashMap();

        for(Pair<JigsawPiece, Integer> jigsawWeightPair: pool)
        {
            String[] nbtPath = jigsawWeightPair.getFirst().getRegistryName().getPath().split("/");
            String nbtName = nbtPath[nbtPath.length-1];
            String[] nbtNameComponents = nbtName.split("_");
            if(nbtNameComponents.length <= 1)
                categoryToJigsawPair.computeIfAbsent(nbtName,s -> Lists.newArrayList()).add(jigsawWeightPair);
            else if (nbtNameComponents.length <= 3)
            {
                if(pool.size() <= 1 || jigsawPattern.getPath().endsWith("decor"))
                {
                    categoryToJigsawPair.computeIfAbsent(nbtName, s -> Lists.newArrayList()).add(jigsawWeightPair);
                }else
                    categoryToJigsawPair.computeIfAbsent(nbtNameComponents[nbtNameComponents.length - 2], s -> Lists.newArrayList()).add(jigsawWeightPair);
            }
            else
            {
                StringBuilder registryName = new StringBuilder(nbtNameComponents[1]);
                for (int i = 2;i<nbtNameComponents.length-1;i++)
                {
                    registryName.append("_");
                    registryName.append(nbtNameComponents[i]);
                }
                categoryToJigsawPair.computeIfAbsent(registryName.toString(),s -> Lists.newArrayList()).add(jigsawWeightPair);
            }
        }

        List<JigsawCategory> categories = Lists.newArrayList();

        categoryToJigsawPair.forEach((name, jigsawPiece) ->
        {
            categories.add(new JigsawCategory(new ResourceLocation(jigsawPattern.getNamespace(),name), jigsawPiece.stream().mapToInt((Pair::getSecond)).sum(), jigsawPiece));
        });
        return categories;
    }


    /**
     * copy of {@link net.minecraft.util.WeightedRandom}, but with float values for exact Calculations.
     *
     * Used to shrink {@link JigsawPattern}'s list of JigsawPieces to a size to calculate with.
     * Otherwise the size would be overwhelming with results in much longer game load time
     */
    public static class WeightedFloatRandom
    {

        public static PieceItem getRandomItem(Random random, List<PieceItem> collection, float totalWeight)
        {
            if (totalWeight <= 0)
            {
                throw new IllegalArgumentException();
            }
            else
                {
                float i = random.nextFloat() * totalWeight;
                return getRandomItem(collection, i);
            }
        }

        private static PieceItem getRandomItem(List<PieceItem> collection, float weight)
        {
            int i = 0;

            for(int j = collection.size(); i < j; ++i)
            {
                PieceItem t = collection.get(i);
                weight -= t.getWeight();
                if (weight < 0)
                {
                    return t;
                }
            }

            return null;
        }
    }

    /**
     * copy of {@link net.minecraft.util.WeightedRandom.Item} for float values
     */
    public static class PieceItem
    {
        private final float weight;
        private final JigsawPiece piece;

        public PieceItem(float itemWeightIn, JigsawPiece piece)
        {
            this.weight = itemWeightIn;
            this.piece = piece;
        }

        public JigsawPiece getPiece()
        {
            return piece;
        }

        public float getWeight()
        {
            return weight;
        }
    }
}
