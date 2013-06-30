/*
 * Forge Mod Loader
 * Copyright (c) 2012-2013 cpw.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 *
 * Contributors:
 *     cpw - implementation
 */

package cpw.mods.fml.client.registry;

import java.util.List;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.entity.Entity;
import net.minecraft.world.IBlockAccess;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.ObjectArrays;

import cpw.mods.fml.client.TextureFXManager;

/**
 * @author cpw
 *
 */
public class RenderingRegistry
{
    private static final RenderingRegistry INSTANCE = new RenderingRegistry();

    private int nextRenderId = 40;

    private Map<Integer, ISimpleBlockRenderingHandler> blockRenderers = Maps.newHashMap();

    private List<EntityRendererInfo> entityRenderers = Lists.newArrayList();

    /**
     * Add a new armour prefix to the RenderPlayer
     *
     * @param armor
     */
    public static int addNewArmourRendererPrefix(String armor)
    {
        RenderBiped.field_82424_k = ObjectArrays.concat(RenderBiped.field_82424_k, armor);
        return RenderBiped.field_82424_k.length - 1;
    }

    /**
     * Register an entity rendering handler. This will, after mod initialization, be inserted into the main
     * render map for entities
     *
     * @param entityClass
     * @param renderer
     */
    public static void registerEntityRenderingHandler(Class<? extends Entity> entityClass, Render renderer)
    {
        instance().entityRenderers.add(new EntityRendererInfo(entityClass, renderer));
    }

    /**
     * Register a simple block rendering handler
     *
     * @param handler
     */
    public static void registerBlockHandler(ISimpleBlockRenderingHandler handler)
    {
        instance().blockRenderers.put(handler.getRenderId(), handler);
    }

    /**
     * Register the simple block rendering handler
     * This version will not call getRenderId on the passed in handler, instead using the supplied ID, so you
     * can easily re-use the same rendering handler for multiple IDs
     *
     * @param renderId
     * @param handler
     */
    public static void registerBlockHandler(int renderId, ISimpleBlockRenderingHandler handler)
    {
        instance().blockRenderers.put(renderId, handler);
    }
    /**
     * Get the next available renderId from the block render ID list
     */
    public static int getNextAvailableRenderId()
    {
        return instance().nextRenderId++;
    }

    /**
     * Add a texture override for the given path and return the used index
     *
     * @param fileToOverride
     * @param fileToAdd
     */
    @Deprecated
    public static int addTextureOverride(String fileToOverride, String fileToAdd)
    {
        return -1;
    }

    /**
     * Add a texture override for the given path and index
     *
     * @param path
     * @param overlayPath
     * @param index
     */
    public static void addTextureOverride(String path, String overlayPath, int index)
    {
//        TextureFXManager.instance().addNewTextureOverride(path, overlayPath, index);
    }

    /**
     * Get and reserve a unique texture index for the supplied path
     *
     * @param path
     */
    @Deprecated
    public static int getUniqueTextureIndex(String path)
    {
        return -1;
    }

    @Deprecated public static RenderingRegistry instance()
    {
        return INSTANCE;
    }

    private static class EntityRendererInfo
    {
        public EntityRendererInfo(Class<? extends Entity> target, Render renderer)
        {
            this.target = target;
            this.renderer = renderer;
        }
        private Class<? extends Entity> target;
        private Render renderer;
    }

    public boolean renderWorldBlock(RenderBlocks renderer, IBlockAccess world, int x, int y, int z, Block block, int modelId)
    {
        if (!blockRenderers.containsKey(modelId)) { return false; }
        ISimpleBlockRenderingHandler bri = blockRenderers.get(modelId);
        return bri.renderWorldBlock(world, x, y, z, block, modelId, renderer);
    }

    public void renderInventoryBlock(RenderBlocks renderer, Block block, int metadata, int modelID)
    {
        if (!blockRenderers.containsKey(modelID)) { return; }
        ISimpleBlockRenderingHandler bri = blockRenderers.get(modelID);
        bri.renderInventoryBlock(block, metadata, modelID, renderer);
    }

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
            info.renderer.func_76976_a(RenderManager.field_78727_a);
        }
    }
}
