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

package net.minecraft.src;

import java.awt.Dimension;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.RenderEngine;
import net.minecraft.client.renderer.texturefx.TextureFX;
import net.minecraft.client.texturepacks.ITexturePack;
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
        return FMLLog.getLogger();
    }

    public static void log(Level level, String message)
    {
        FMLLog.log(level, message);
    }

    public static void log(Level level, String message, Throwable throwable)
    {
        FMLLog.log(level, throwable, message);
    }

    public static void setTextureDimensions(int textureId, int width, int height, List<TextureFX> textureFXList)
    {
        TextureFXManager.instance().setTextureDimensions(textureId, width, height, textureFXList);
    }

    public static void preRegisterEffect(TextureFX textureFX)
    {
        TextureFXManager.instance().onPreRegisterEffect(textureFX);
    }

    public static boolean onUpdateTextureEffect(TextureFX textureFX)
    {
        return TextureFXManager.instance().onUpdateTextureEffect(textureFX);
    }

    public static Dimension getTextureDimensions(TextureFX textureFX)
    {
        return TextureFXManager.instance().getTextureDimensions(textureFX);
    }

    public static void onTexturePackChange(RenderEngine engine, ITexturePack texturePack, List<TextureFX> textureFXList)
    {
        TextureFXManager.instance().onTexturePackChange(engine, texturePack, textureFXList);
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
}
