package net.minecraftforge.common;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.feature.jigsaw.JigsawPiece;

import java.util.List;
import java.util.Random;

public class JigsawCategory {
    public static final ResourceLocation DEFAULTCATEGORY = new ResourceLocation("default");

    private final List<Pair<JigsawPiece, Integer>> pieces;
    private final int categoryWeight;
    private final ResourceLocation registryName;
    private int totalPieceWeight;

    @SafeVarargs
    public JigsawCategory(ResourceLocation registryName, int categoryWeight, Pair<JigsawPiece, Integer>... pieces) {
        this(registryName,categoryWeight,Lists.newArrayList(pieces));
    }

    public JigsawCategory(ResourceLocation registryName, int categoryWeight, List<Pair<JigsawPiece, Integer>> pieces) {
        this.pieces = Lists.newArrayList(pieces);
        this.categoryWeight = categoryWeight;
        this.registryName = registryName;
        this.totalPieceWeight = pieces.stream().mapToInt(Pair::getSecond).sum();
    }

    @SafeVarargs
    public JigsawCategory(ResourceLocation registryName, Pair<JigsawPiece, Integer>... pieces) {
        this(registryName,Lists.newArrayList(pieces));
    }

    public JigsawCategory(ResourceLocation registryName, List<Pair<JigsawPiece, Integer>> pieces) {
        this.pieces = Lists.newArrayList(pieces);
        this.registryName = registryName;
        this.totalPieceWeight = pieces.stream().mapToInt(Pair::getSecond).sum();
        this.categoryWeight = totalPieceWeight;
    }

    public List<Pair<JigsawPiece, Integer>> getPieces() {
        return pieces;
    }

    public ResourceLocation getRegistryName() {
        return registryName;
    }

    public int getCategoryWeight() {
        return categoryWeight;
    }

    public int getTotalPieceWeight() {
        return totalPieceWeight;
    }

    public void recalculateWeight(){
        this.totalPieceWeight = pieces.stream().mapToInt(Pair::getSecond).sum();
    }

    public static class PieceItem {
        private final float weight;
        private final JigsawPiece piece;

        public PieceItem(float itemWeightIn, JigsawPiece piece) {
            this.weight = itemWeightIn;
            this.piece = piece;
        }

        public JigsawPiece getPiece() {
            return piece;
        }

        public float getWeight() {
            return weight;
        }
    }

    public static class WeightedFloatRandom {

        public static PieceItem getRandomItem(Random random, List<PieceItem> collection, float totalWeight){
            if (totalWeight <= 0) {
                throw new IllegalArgumentException();
            } else {
                float i = random.nextFloat() * totalWeight;
                return getRandomItem(collection, i);
            }
        }

        public static PieceItem getRandomItem(List<PieceItem> collection, float weight) {
            int i = 0;

            for(int j = collection.size(); i < j; ++i) {
                PieceItem t = collection.get(i);
                weight -= t.getWeight();
                if (weight < 0) {
                    return t;
                }
            }

            return null;
        }
    }
}
