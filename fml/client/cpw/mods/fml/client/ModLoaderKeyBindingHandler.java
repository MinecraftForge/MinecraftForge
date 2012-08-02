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

import net.minecraft.src.KeyBinding;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import cpw.mods.fml.client.registry.KeyBindingRegistry;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.common.modloader.ModLoaderModContainer;
import cpw.mods.fml.common.registry.TickRegistry;

/**
 * @author cpw
 *
 */
public class ModLoaderKeyBindingHandler extends KeyBindingRegistry.KeyHandler
{
    private ModLoaderModContainer modContainer;
    private boolean downArmed;
    private boolean upArmed;

    /**
     * @param keyHandler
     * @param allowRepeat
     * @param modContainer
     */
    public ModLoaderKeyBindingHandler(KeyBinding keyBinding, boolean allowRepeat, ModLoaderModContainer modContainer)
    {
        super(keyBinding, allowRepeat);
        this.modContainer=modContainer;
    }

    public void onRenderEndTick()
    {
        ((net.minecraft.src.BaseMod)modContainer.getMod()).keyboardEvent(keyBinding);
    }

    @Override
    public void keyDown(EnumSet<TickType> type, boolean end, boolean repeats)
    {
        if (!end)
        {
            return;
        }
        upArmed = false;
        if (type.contains(TickType.GUILOAD)|| type.contains(TickType.GAME))
        {
            downArmed = true;
        }
        if (type.contains(TickType.RENDER) && downArmed)
        {
            onRenderEndTick();
            downArmed = false;
        }
    }

    @Override
    public void keyUp(EnumSet<TickType> type, boolean end)
    {
        if (!end)
        {
            return;
        }
        downArmed = false;
        if (type.contains(TickType.GUILOAD)|| type.contains(TickType.GAME))
        {
            upArmed = true;
        }
        if (type.contains(TickType.RENDER) && upArmed)
        {
            onRenderEndTick();
            upArmed = false;
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
        return modContainer.getModId() +" KB "+keyBinding.field_74512_d;
    }
}
