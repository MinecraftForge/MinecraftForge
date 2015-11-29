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

package net.minecraftforge.fml.client.registry;

import org.apache.commons.lang3.ArrayUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ClientRegistry
{
    /**
     *
     * Utility method for registering a tile entity and it's renderer at once - generally you should register them separately
     *
     * @param tileEntityClass
     * @param id
     * @param specialRenderer
     */
    public static <T extends TileEntity> void registerTileEntity(Class<T> tileEntityClass, String id, TileEntitySpecialRenderer<T> specialRenderer)
    {
        GameRegistry.registerTileEntity(tileEntityClass, id);
        bindTileEntitySpecialRenderer(tileEntityClass, specialRenderer);
    }

    public static <T extends TileEntity> void bindTileEntitySpecialRenderer(Class<T> tileEntityClass, TileEntitySpecialRenderer<T> specialRenderer)
    {
        TileEntityRendererDispatcher.instance.mapSpecialRenderers.put(tileEntityClass, specialRenderer);
        specialRenderer.setRendererDispatcher(TileEntityRendererDispatcher.instance);
    }

    public static void registerKeyBinding(KeyBinding key)
    {
        Minecraft.getMinecraft().gameSettings.keyBindings = ArrayUtils.add(Minecraft.getMinecraft().gameSettings.keyBindings, key);
    }
}
