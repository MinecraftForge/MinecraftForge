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

package cpw.mods.fml.client;

import java.util.EnumSet;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import net.minecraft.src.KeyBinding;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.IKeyHandler;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.common.modloader.ModLoaderModContainer;

/**
 * @author cpw
 *
 */
public class ModLoaderKeyBindingHandler implements IKeyHandler
{

    private boolean shouldRepeat;
    private KeyBinding keyBinding;
    private ModContainer modContainer;
    private boolean lastState = false;
    private boolean armed;

    /**
     * @param keyHandler
     * @param allowRepeat
     * @param modContainer
     */
    public ModLoaderKeyBindingHandler(KeyBinding keyHandler, boolean allowRepeat, ModContainer modContainer)
    {
        this.keyBinding=keyHandler;
        this.shouldRepeat=allowRepeat;
        this.modContainer=modContainer;
        FMLCommonHandler.instance().registerTickHandler(this);
    }

    @Override
    public Object getKeyBinding()
    {
        return this.keyBinding;
    }

    /**
     * @return the modContainer
     */
    public ModContainer getOwningContainer()
    {
        return modContainer;
    }

    public void onRenderEndTick()
    {
        int keyCode = keyBinding.field_1370_b;
        boolean state = (keyCode < 0 ? Mouse.isButtonDown(keyCode + 100) : Keyboard.isKeyDown(keyCode));
        if (state && (!lastState || (lastState && shouldRepeat)))
        {
            modContainer.keyBindEvent(keyBinding);
        }
        lastState = state;
    }

    @Override
    public void tickStart(EnumSet<TickType> type, Object... tickData)
    {
        // NO-OP for ML
    }

    @Override
    public void tickEnd(EnumSet<TickType> type, Object... tickData)
    {
        if (type.contains(TickType.GUILOAD)|| type.contains(TickType.GAME))
        {
            armed = true;
        }
        if (type.contains(TickType.RENDER) && armed)
        {
            onRenderEndTick();
            armed = false;
        }
    }

    @Override
    public EnumSet<TickType> ticks()
    {
        return EnumSet.of(TickType.GAME, TickType.RENDER, TickType.GUILOAD);
    }

    @Override
    public String getLabel()
    {
        return getOwningContainer()+" KB "+keyBinding.field_1371_a;
    }
}
