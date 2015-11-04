package net.minecraftforge.client.model;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.util.EnumFacing;

/**
 * Get various info about face data, which is not recoverable from the raw vertex data.
 */
public interface IQuadInfo
{
    int getTintIndex(int quad);
    EnumFacing getOrientation(int quad);
    boolean isCulled(int quad);
    boolean isColored(int quad);

    /**
     * All side quads are after the general quads, in the order of EnumFacing.values().
     */
    public static class Impl implements IQuadInfo
    {
        private final IFlexibleBakedModel parent;

        protected BakedQuad getQuad(int quad)
        {
            if(quad < parent.getGeneralQuads().size())
            {
                return parent.getGeneralQuads().get(quad);
            }
            quad -= parent.getGeneralQuads().size();
            for(EnumFacing side : EnumFacing.values())
            {
                if(quad < parent.getFaceQuads(side).size())
                {
                    return parent.getFaceQuads(side).get(quad);
                }
                quad -= parent.getFaceQuads(side).size();
            }
            throw new IndexOutOfBoundsException();
        }

        public Impl(IFlexibleBakedModel parent)
        {
            this.parent = parent;
        }

        public int getTintIndex(int quad)
        {
            return getQuad(quad).getTintIndex();
        }

        public EnumFacing getOrientation(int quad)
        {
            return getQuad(quad).getFace();
        }

        public boolean isCulled(int quad)
        {
            return quad >= parent.getGeneralQuads().size();
        }

        public boolean isColored(int quad)
        {
            return getQuad(quad) instanceof IColoredBakedQuad;
        }
    }
}
