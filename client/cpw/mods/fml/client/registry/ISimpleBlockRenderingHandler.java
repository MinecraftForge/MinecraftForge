package cpw.mods.fml.client.registry;

import net.minecraft.src.Block;
import net.minecraft.src.IBlockAccess;
import net.minecraft.src.RenderBlocks;

public interface ISimpleBlockRenderingHandler
{
    public abstract void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer);

    public abstract boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer);

    public abstract boolean shouldRender3DInInventory();

    public abstract int getRenderId();
}
