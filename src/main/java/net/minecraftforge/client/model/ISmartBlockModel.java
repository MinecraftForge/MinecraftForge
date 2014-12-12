package net.minecraftforge.client.model;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.model.IBakedModel;

public interface ISmartBlockModel extends IBakedModel
{
    IBakedModel handleBlockState(IBlockState state);
}
