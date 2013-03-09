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

package cpw.mods.fml.client.modloader;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.src.BaseMod;
import net.minecraft.world.IBlockAccess;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;

/**
 * @author cpw
 *
 */
public class ModLoaderBlockRendererHandler implements ISimpleBlockRenderingHandler
{
    private int renderId;
    private boolean render3dInInventory;
    private BaseMod mod;

    /**
     * @param mod
     *
     */
    public ModLoaderBlockRendererHandler(int renderId, boolean render3dInInventory, BaseMod mod)
    {
        this.renderId=renderId;
        this.render3dInInventory=render3dInInventory;
        this.mod=mod;
    }

    @Override
    public int getRenderId()
    {
        return renderId;
    }

    @Override
    public boolean shouldRender3DInInventory()
    {
        return render3dInInventory;
    }

    /**
     * @param world
     * @param x
     * @param y
     * @param z
     * @param block
     * @param modelId
     * @param renderer
     */
    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer)
    {
        return mod.renderWorldBlock(renderer, world, x, y, z, block, modelId);
    }

    /**
     * @param block
     * @param metadata
     * @param modelID
     * @param renderer
     */
    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer)
    {
        mod.renderInvBlock(renderer, block, metadata, modelID);
    }

}
