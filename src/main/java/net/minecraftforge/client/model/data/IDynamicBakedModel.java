package net.minecraftforge.client.model.data;

import java.util.List;
import java.util.Random;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.util.EnumFacing;

/**
 * Convenience interface with default implementation of {@link IBakedModel#getQuads(net.minecraft.block.state.IBlockState, net.minecraft.util.EnumFacing, java.util.Random)}.
 */
public interface IDynamicBakedModel extends IBakedModel
{
    @Override
    default @Nonnull List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, @Nonnull Random rand)
    {
        return getQuads(state, side, rand, EmptyModelData.INSTANCE);
    }
    
    // Force this to be overriden otherwise this introduces a default cycle between the two overloads.
    @Override
    @Nonnull
    List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, @Nonnull Random rand, @Nonnull IModelData extraData);
}
