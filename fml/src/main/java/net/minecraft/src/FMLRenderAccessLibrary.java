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

package net.minecraft.src;

import java.awt.Dimension;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;

import cpw.mods.fml.client.TextureFXManager;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLLog;

/**
 *
 * A static hook library for optifine and other basemod editing code to access FML functions
 *
 * @author cpw
 *
 */
public class FMLRenderAccessLibrary
{
    public static Logger getLogger()
    {
        Logger l = Logger.getLogger("FMLRenderAccessLibrary");
        l.setParent(FMLLog.getLogger());
        return l;
    }

    public static void log(Level level, String message)
    {
        FMLLog.log("FMLRenderAccessLibrary", level, message);
    }

    public static void log(Level level, String message, Throwable throwable)
    {
        FMLLog.log(level, throwable, message);
    }

    @SuppressWarnings("deprecation")
    public static boolean renderWorldBlock(RenderBlocks renderer, IBlockAccess world, int x, int y, int z, Block block, int modelId)
    {
        return RenderingRegistry.instance().renderWorldBlock(renderer, world, x, y, z, block, modelId);
    }

    @SuppressWarnings("deprecation")
    public static void renderInventoryBlock(RenderBlocks renderer, Block block, int metadata, int modelID)
    {
        RenderingRegistry.instance().renderInventoryBlock(renderer, block, metadata, modelID);
    }

    @SuppressWarnings("deprecation")
    public static boolean renderItemAsFull3DBlock(int modelId)
    {
        return RenderingRegistry.instance().renderItemAsFull3DBlock(modelId);
    }

//    public static void doTextureCopy(Texture atlas, Texture source, int targetX, int targetY)
//    {
//        TextureFXManager.instance().getHelper().doTextureCopy(atlas, source, targetX, targetY);
//    }
}
