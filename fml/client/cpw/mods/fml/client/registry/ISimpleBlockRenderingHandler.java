package cpw.mods.fml.client.registry;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;

public interface ISimpleBlockRenderingHandler
{
    public abstract void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer);

    public abstract boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer);

    public abstract boolean shouldRender3DInInventory();

    public abstract int getRenderId();
}
