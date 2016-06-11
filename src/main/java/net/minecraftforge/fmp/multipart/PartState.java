package net.minecraftforge.fmp.multipart;

import java.util.EnumSet;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.ResourceLocation;
import scala.collection.mutable.StringBuilder;

/**
 * Representation of a part's state. Used for rendering purposes.
 */
public class PartState
{
    
    public final IBlockState state, extendedState;
    public final EnumSet<BlockRenderLayer> renderLayers;
    public final ResourceLocation modelPath;
    public final IBlockColor tintProvider;

    public PartState(IBlockState state, IBlockState extendedState, EnumSet<BlockRenderLayer> renderLayers, ResourceLocation modelPath,
            IBlockColor tintProvider)
    {
        this.state = state;
        this.extendedState = extendedState;
        this.renderLayers = renderLayers;
        this.modelPath = modelPath;
        this.tintProvider = tintProvider;
    }

    public static PartState fromPart(IMultipart part)
    {
        ResourceLocation path = part.getModelPath();
        if (path == null)
        {
            return null;
        }

        EnumSet<BlockRenderLayer> renderLayers = EnumSet.noneOf(BlockRenderLayer.class);
        for (BlockRenderLayer layer : BlockRenderLayer.values())
        {
            if (part.canRenderInLayer(layer))
            {
                renderLayers.add(layer);
            }
        }

        IBlockState state = part.getActualState(MultipartRegistry.getDefaultState(part).getBaseState());
        IBlockState extendedState = part.getExtendedState(state);

        return new PartState(state, extendedState, renderLayers, path, part.getTint());
    }

    @Override
    public int hashCode()
    {
        return state.hashCode() + (renderLayers != null ? renderLayers.hashCode() << 7 : 0)
                + (modelPath != null ? modelPath.hashCode() << 15 : 0);
    }

    @Override
    public String toString()
    {
        return new StringBuilder().append("(state=").append(state).append(", extendedState=").append(extendedState)
                .append(", renderLayers=").append(renderLayers).append(", modelPath=").append(modelPath)
                .append(", tintProvider=").append(tintProvider).append(")").toString();
    }
}
