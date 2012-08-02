package cpw.mods.fml.client;

import java.util.HashMap;

import cpw.mods.fml.common.modloader.ModLoaderHelper;
import cpw.mods.fml.common.modloader.ModLoaderModContainer;

import net.minecraft.src.BaseMod;
import net.minecraft.src.Block;
import net.minecraft.src.IBlockAccess;
import net.minecraft.src.RenderBlocks;

public class BlockRenderManager
{
    private static final BlockRenderManager INSTANCE = new BlockRenderManager();

    private HashMap<Integer, BlockRenderInfo> blockModelIds = new HashMap<Integer, BlockRenderInfo>();

    private int nextRenderId = 30;

    public static BlockRenderManager instance()
    {
        return INSTANCE;
    }

    /**
     * @param renderer
     * @param world
     * @param x
     * @param y
     * @param z
     * @param block
     * @param modelId
     * @return
     */
    public boolean renderWorldBlock(RenderBlocks renderer, IBlockAccess world, int x, int y, int z, Block block, int modelId)
    {
        if (!blockModelIds.containsKey(modelId)) { return false; }
        BlockRenderInfo bri = blockModelIds.get(modelId);
        return bri.renderWorldBlock(world, x, y, z, block, modelId, renderer);
    }

    /**
     * @param renderer
     * @param block
     * @param metadata
     * @param modelID
     */
    public void renderInventoryBlock(RenderBlocks renderer, Block block, int metadata, int modelID)
    {
        if (!blockModelIds.containsKey(modelID)) { return; }
        BlockRenderInfo bri = blockModelIds.get(modelID);
        bri.renderInventoryBlock(block, metadata, modelID, renderer);
    }

    /**
     * @param p_1219_0_
     * @return
     */
    public boolean renderItemAsFull3DBlock(int modelId)
    {
        BlockRenderInfo bri = blockModelIds.get(modelId);
        if (bri != null) { return bri.shouldRender3DInInventory(); }
        return false;
    }


    /**
     * @param mod
     * @param inventoryRenderer
     * @return
     */
    public int obtainBlockModelIdFor(BaseMod mod, boolean inventoryRenderer)
    {
        ModLoaderModContainer mlmc=ModLoaderHelper.registerRenderHelper(mod);
        int renderId=nextRenderId++;
        BlockRenderInfo bri=new BlockRenderInfo(renderId, inventoryRenderer, mlmc);
        blockModelIds.put(renderId, bri);
        return renderId;
    }
}
