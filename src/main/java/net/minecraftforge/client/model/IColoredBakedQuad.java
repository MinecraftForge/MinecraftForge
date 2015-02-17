package net.minecraftforge.client.model;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.util.EnumFacing;

public interface IColoredBakedQuad {
    public static class ColoredBakedQuad extends BakedQuad implements IColoredBakedQuad
    {
        public ColoredBakedQuad(int[] data, int tintIndex, EnumFacing side)
        {
            super(data, tintIndex, side);
        }
    }
}