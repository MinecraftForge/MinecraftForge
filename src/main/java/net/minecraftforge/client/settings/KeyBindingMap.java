/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.client.settings;

import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.IntHashMap;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.List;

public class KeyBindingMap
{
    private static final EnumMap<KeyModifier, IntHashMap<Collection<KeyBinding>>> map = new java.util.EnumMap<KeyModifier, IntHashMap<Collection<KeyBinding>>>(KeyModifier.class);
    static
    {
        for (KeyModifier modifier : KeyModifier.values())
        {
            map.put(modifier, new IntHashMap<Collection<KeyBinding>>());
        }
    }

    @Nullable
    public KeyBinding lookupActive(int keyCode)
    {
        KeyModifier activeModifier = KeyModifier.getActiveModifier();
        if (!activeModifier.matches(keyCode))
        {
            KeyBinding binding = getBinding(keyCode, activeModifier);
            if (binding != null)
            {
                return binding;
            }
        }
        return getBinding(keyCode, KeyModifier.NONE);
    }

    @Nullable
    private KeyBinding getBinding(int keyCode, KeyModifier keyModifier)
    {
        Collection<KeyBinding> bindings = map.get(keyModifier).lookup(keyCode);
        if (bindings != null)
        {
            for (KeyBinding binding : bindings)
            {
                if (binding.isActiveAndMatches(keyCode))
                {
                    return binding;
                }
            }
        }
        return null;
    }

    public List<KeyBinding> lookupAll(int keyCode)
    {
        List<KeyBinding> matchingBindings = new ArrayList<KeyBinding>();
        for (IntHashMap<Collection<KeyBinding>> bindingsMap : map.values())
        {
            Collection<KeyBinding> bindings = bindingsMap.lookup(keyCode);
            if (bindings != null)
            {
                matchingBindings.addAll(bindings);
            }
        }
        return matchingBindings;
    }

    public void addKey(int keyCode, KeyBinding keyBinding)
    {
        KeyModifier keyModifier = keyBinding.getKeyModifier();
        IntHashMap<Collection<KeyBinding>> bindingsMap = map.get(keyModifier);
        Collection<KeyBinding> bindingsForKey = bindingsMap.lookup(keyCode);
        if (bindingsForKey == null)
        {
            bindingsForKey = new ArrayList<KeyBinding>();
            bindingsMap.addKey(keyCode, bindingsForKey);
        }
        bindingsForKey.add(keyBinding);
    }

    public void removeKey(KeyBinding keyBinding)
    {
        KeyModifier keyModifier = keyBinding.getKeyModifier();
        int keyCode = keyBinding.getKeyCode();
        IntHashMap<Collection<KeyBinding>> bindingsMap = map.get(keyModifier);
        Collection<KeyBinding> bindingsForKey = bindingsMap.lookup(keyCode);
        if (bindingsForKey != null)
        {
            bindingsForKey.remove(keyBinding);
            if (bindingsForKey.isEmpty())
            {
                bindingsMap.removeObject(keyCode);
            }
        }
    }

    public void clearMap()
    {
        for (IntHashMap<Collection<KeyBinding>> bindings : map.values())
        {
            bindings.clearMap();
        }
    }
}
