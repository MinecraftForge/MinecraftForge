package net.minecraftforge.common;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.feature.jigsaw.JigsawPiece;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class JigsawCategory {
    private final List<Pair<JigsawPiece, Integer>> pieces;
    private final int categoryWeight;
    private final ResourceLocation registryName;
    private int totalPieceWeight;

    public JigsawCategory(ResourceLocation registryName, int categoryWeight, List<Pair<JigsawPiece, Integer>> pieces) {
        this.pieces = Lists.newArrayList(pieces);
        this.categoryWeight = categoryWeight;
        this.registryName = registryName;
        this.totalPieceWeight = pieces.stream().mapToInt(Pair::getSecond).sum();
    }

    public List<Pair<JigsawPiece, Integer>> getPieces()
    {
        return pieces;
    }

    public ResourceLocation getRegistryName()
    {
        return registryName;
    }

    public int getCategoryWeight()
    {
        return categoryWeight;
    }

    public int getTotalPieceWeight()
    {
        return totalPieceWeight;
    }

    public void recalculateWeight()
    {
        this.totalPieceWeight = pieces.stream().mapToInt(Pair::getSecond).sum();
    }

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

    public static List<JigsawCategory> convertToCategories(List<Pair<JigsawPiece, Integer>> pool, ResourceLocation jigsawPattern)
    {
        Map<String, List<Pair<JigsawPiece,Integer>>> value = Maps.newHashMap();

        for(Pair<JigsawPiece, Integer> pair: pool)
        {
            String[] path = pair.getFirst().getRegistryName().getPath().split("/");
            String name = path[path.length-1];
            String[] components = name.split("_");
            if(components.length <= 1)
                value.computeIfAbsent(name,s -> Lists.newArrayList()).add(pair);
            else if (components.length <= 3)
            {
                if(pool.size() <= 1 || jigsawPattern.getPath().endsWith("decor"))
                {
                    value.computeIfAbsent(name, s -> Lists.newArrayList()).add(pair);
                }else
                    value.computeIfAbsent(components[components.length - 2], s -> Lists.newArrayList()).add(pair);
            }
            else
                {
                StringBuilder registryName = new StringBuilder(components[1]);
                for (int i = 2;i<components.length-1;i++)
                {
                    registryName.append("_");
                    registryName.append(components[i]);
                }
                value.computeIfAbsent(registryName.toString(),s -> Lists.newArrayList()).add(pair);
            }
        }

        List<JigsawCategory> categories = Lists.newArrayList();

        value.forEach((key, value1) ->
        {
            categories.add(new JigsawCategory(new ResourceLocation(jigsawPattern.getNamespace(),key), value1.stream().mapToInt((Pair::getSecond)).sum(), value1));
        });
        return categories;
    }
}
