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

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

import org.lwjgl.input.Mouse;

import com.google.common.collect.ObjectArrays;
import com.google.common.primitives.Booleans;

import net.minecraft.client.settings.KeyBinding;
import cpw.mods.fml.client.registry.KeyBindingRegistry;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.common.modloader.ModLoaderModContainer;

/**
 * @author cpw
 *
 */
public class ModLoaderKeyBindingHandler extends KeyBindingRegistry.KeyHandler
{
    private ModLoaderModContainer modContainer;
    private List<KeyBinding> helper;
    private boolean[] active = new boolean[0];
    private boolean[] mlRepeats = new boolean[0];
    private boolean[] armed = new boolean[0];

    public ModLoaderKeyBindingHandler()
    {
        super(new KeyBinding[0], new boolean[0]);
    }

    void setModContainer(ModLoaderModContainer modContainer)
    {
        this.modContainer = modContainer;
    }

    public void fireKeyEvent(KeyBinding kb)
    {
        ((net.minecraft.src.BaseMod)modContainer.getMod()).keyboardEvent(kb);
    }

    @Override
    public void keyDown(EnumSet<TickType> type, KeyBinding kb, boolean end, boolean repeats)
    {
        if (!end)
        {
            return;
        }
        int idx = helper.indexOf(kb);
        if (type.contains(TickType.CLIENT))
        {
            armed[idx] = true;
        }
        if (armed[idx] && type.contains(TickType.RENDER) && (!active[idx] || mlRepeats[idx]))
        {
            fireKeyEvent(kb);
            active[idx] = true;
            armed[idx] = false;
        }
    }

    @Override
    public void keyUp(EnumSet<TickType> type, KeyBinding kb, boolean end)
    {
        if (!end)
        {
            return;
        }
        int idx = helper.indexOf(kb);
        active[idx] = false;
    }

    @Override
    public EnumSet<TickType> ticks()
    {
        return EnumSet.of(TickType.CLIENT, TickType.RENDER);
    }

    @Override
    public String getLabel()
    {
        return modContainer.getModId() +" KB "+keyBindings[0].field_74512_d;
    }

    void addKeyBinding(KeyBinding binding, boolean repeats)
    {
        this.keyBindings = ObjectArrays.concat(this.keyBindings, binding);
        this.repeatings = new boolean[this.keyBindings.length];
        Arrays.fill(this.repeatings, true);
        this.active = new boolean[this.keyBindings.length];
        this.armed = new boolean[this.keyBindings.length];
        this.mlRepeats = Booleans.concat(this.mlRepeats, new boolean[] { repeats });
        this.keyDown = new boolean[this.keyBindings.length];
        this.helper = Arrays.asList(this.keyBindings);
    }
}
