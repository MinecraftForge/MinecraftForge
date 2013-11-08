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
