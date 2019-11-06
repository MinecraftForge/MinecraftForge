package net.minecraftforge.common;

import com.mojang.datafixers.util.Pair;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.WeightedRandom;
import net.minecraft.world.gen.feature.jigsaw.JigsawPiece;

import java.util.List;

public class JigsawCategory {
    public static final ResourceLocation DEFAULTCATEGORY = new ResourceLocation("default");

    private final List<Pair<JigsawPiece, Integer>> pieces;
    private final int categoryWeight;
    private final ResourceLocation registryName;

    public JigsawCategory(List<Pair<JigsawPiece, Integer>> pieces, int categoryWeight, ResourceLocation registryName) {
        this.pieces = pieces;
        this.categoryWeight = categoryWeight;
        this.registryName = registryName;
    }

    public List<Pair<JigsawPiece, Integer>> getPieces() {
        return pieces;
    }

    public ResourceLocation getRegistryName() {
        return registryName;
    }

    public int getCategoryWeight(){
        return categoryWeight;
    }

    public static class PieceItem extends WeightedRandom.Item {
        private final JigsawPiece piece;

        public PieceItem(int itemWeightIn, JigsawPiece piece) {
            super(itemWeightIn);
            this.piece = piece;
        }

        public JigsawPiece getPiece() {
            return piece;
        }
    }
}
