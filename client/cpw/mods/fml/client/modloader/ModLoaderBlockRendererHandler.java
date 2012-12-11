/*
 * The FML Forge Mod Loader suite.
 * Copyright (C) 2012 cpw
 *
 * This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
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
