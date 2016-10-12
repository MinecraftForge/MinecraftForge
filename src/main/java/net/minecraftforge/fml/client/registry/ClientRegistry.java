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
import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.client.IKeyBinding;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClientRegistry
{
    public static final String CATEGORY_CONTEXT = "forge.key.category.context";
    private static HashMap<String, KeyBindingInfo> mapContexts = new HashMap<String, KeyBindingInfo>();

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

    public static void registerKeyBinding(String description, int suggestedKeyCode, String category, IKeyBinding callback)
    {
        registerKeyBinding(description, suggestedKeyCode, description, category, callback);
    }

    public static void registerKeyBindingContext(String description, int suggestedKeyCode, String context, IKeyBinding callback)
    {
        registerKeyBinding(description, suggestedKeyCode, context, CATEGORY_CONTEXT, callback);
    }

    private static void registerKeyBinding(String description, int suggestedKeyCode, String context, String category, IKeyBinding callback)
    {
        KeyBindingInfo info = mapContexts.get(context);

        if (info == null)
        {
            info = new KeyBindingInfo(new KeyBinding(description, suggestedKeyCode, category));
            mapContexts.put(context, info);
            registerKeyBinding(info.key);
        }

        info.callbacks.add(callback);
    }

    public static void onKeyInput(InputEvent event)
    {
        Minecraft minecraft = Minecraft.getMinecraft();
        boolean ctrl = Minecraft.isRunningOnMac ? Keyboard.isKeyDown(Keyboard.KEY_LMETA) || Keyboard.isKeyDown(Keyboard.KEY_RMETA) : Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL);
        boolean shift = Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT);
        boolean alt = Keyboard.isKeyDown(Keyboard.KEY_LMENU) || Keyboard.isKeyDown(Keyboard.KEY_RMENU);

        for (Map.Entry<String, KeyBindingInfo> entry : mapContexts.entrySet())
        {
            if (entry.getValue().key.isPressed())
            {
                for (IKeyBinding callback : entry.getValue().callbacks)
                {
                    if (callback.onKeyDown(minecraft, minecraft.thePlayer, entry.getKey(), ctrl, shift, alt))
                    {
                        break;
                    }
                }
            }

            if (entry.getValue().key.isKeyDown())
            {
                for (IKeyBinding callback : entry.getValue().callbacks)
                {
                    if (callback.onKeyHold(minecraft, minecraft.thePlayer, entry.getKey(), ctrl, shift, alt))
                    {
                        break;
                    }
                }
            }
        }
    }

    private static class KeyBindingInfo
    {
        public final KeyBinding key;
        public final List<IKeyBinding> callbacks;

        public KeyBindingInfo(KeyBinding key)
        {
            this.key = key;
            this.callbacks = new ArrayList<IKeyBinding>();
        }
    }
}
