package net.minecraftforge.client.model;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.EnumFacing;

public interface IColoredBakedQuad {
    public static class ColoredBakedQuad extends BakedQuad implements IColoredBakedQuad
    {
        public ColoredBakedQuad(int[] data, int tintIndex, EnumFacing side, TextureAtlasSprite sprite, VertexFormat format)
        {
            super(data, tintIndex, side, sprite, format);
        }
    }
}