package cpw.mods.fml.client.registry;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.ObjectArrays;

import net.minecraft.src.Block;
import net.minecraft.src.Entity;
import net.minecraft.src.IBlockAccess;
import net.minecraft.src.Render;
import net.minecraft.src.RenderBlocks;
import net.minecraft.src.RenderPlayer;
import cpw.mods.fml.client.modloader.ModLoaderBlockRendererHandler;
import cpw.mods.fml.common.ObfuscationReflectionHelper;

public class RenderingRegistry
{
    private static final RenderingRegistry INSTANCE = new RenderingRegistry();

    private int nextRenderId = 32;

    private Map<Integer, ISimpleBlockRenderingHandler> blockRenderers = Maps.newHashMap();

    private List<EntityRendererInfo> entityRenderers = Lists.newArrayList();

    public int addNewArmourRendererPrefix(String armor)
    {
        RenderPlayer.field_77110_j = ObjectArrays.concat(RenderPlayer.field_77110_j, armor);
        return RenderPlayer.field_77110_j.length - 1;
    }

    public static RenderingRegistry instance()
    {
        return INSTANCE;
    }

    private class EntityRendererInfo
    {
        public EntityRendererInfo(Class<? extends Entity> target, Render renderer)
        {
            this.target = target;
            this.renderer = renderer;
        }
        private Class<? extends Entity> target;
        private Render renderer;
    }

    public void registerEntityRenderingHandler(Class<? extends Entity> entityClass, Render renderer)
    {
        entityRenderers.add(new EntityRendererInfo(entityClass, renderer));
    }

    public void registerBlockHandler(ISimpleBlockRenderingHandler handler)
    {
        blockRenderers.put(handler.getRenderId(), handler);
    }

    public int getNextAvailableRenderId()
    {
        return nextRenderId++;
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
        if (!blockRenderers.containsKey(modelId)) { return false; }
        ISimpleBlockRenderingHandler bri = blockRenderers.get(modelId);
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
        if (!blockRenderers.containsKey(modelID)) { return; }
        ISimpleBlockRenderingHandler bri = blockRenderers.get(modelID);
        bri.renderInventoryBlock(block, metadata, modelID, renderer);
    }

    /**
     * @param p_1219_0_
     * @return
     */
    public boolean renderItemAsFull3DBlock(int modelId)
    {
        ISimpleBlockRenderingHandler bri = blockRenderers.get(modelId);
        return bri != null && bri.shouldRender3DInInventory();
    }

    public void loadEntityRenderers(Map<Class<? extends Entity>, Render> rendererMap)
    {
        for (EntityRendererInfo info : entityRenderers)
        {
            rendererMap.put(info.target, info.renderer);
        }
    }
}
