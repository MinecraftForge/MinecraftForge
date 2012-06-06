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

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import net.minecraft.src.KeyBinding;
import cpw.mods.fml.common.IKeyHandler;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.modloader.ModLoaderModContainer;

/**
 * @author cpw
 *
 */
public class KeyBindingHandler implements IKeyHandler
{

    private boolean shouldRepeat;
    private KeyBinding keyBinding;
    private ModContainer modContainer;
    private boolean lastState = false;

    /**
     * @param keyHandler
     * @param allowRepeat
     * @param modContainer 
     */
    public KeyBindingHandler(KeyBinding keyHandler, boolean allowRepeat, ModContainer modContainer)
    {
        this.keyBinding=keyHandler;
        this.shouldRepeat=allowRepeat;
        this.modContainer=modContainer;
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

    @Override
    public void onEndTick()
    {
        int keyCode = keyBinding.field_1370_b;
        boolean state = (keyCode < 0 ? Mouse.isButtonDown(keyCode + 100) : Keyboard.isKeyDown(keyCode));
        if (state && (!lastState || (lastState && shouldRepeat)))
        {
            modContainer.keyBindEvent(keyBinding);
        }
        lastState = state;
    }
}
