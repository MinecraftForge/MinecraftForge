/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.settings;

import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KeyBindingMap
{
    private static final EnumMap<KeyModifier, Map<InputMappings.Input, Collection<KeyBinding>>> map = new EnumMap<>(KeyModifier.class);
    static
    {
        for (KeyModifier modifier : KeyModifier.values())
        {
            map.put(modifier, new HashMap<>());
        }
    }

    @Nullable
    public KeyBinding lookupActive(InputMappings.Input keyCode)
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
    private KeyBinding getBinding(InputMappings.Input keyCode, KeyModifier keyModifier)
    {
        Collection<KeyBinding> bindings = map.get(keyModifier).get(keyCode);
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

    public List<KeyBinding> lookupAll(InputMappings.Input keyCode)
    {
        List<KeyBinding> matchingBindings = new ArrayList<KeyBinding>();
        for (Map<InputMappings.Input, Collection<KeyBinding>> bindingsMap : map.values())
        {
            Collection<KeyBinding> bindings = bindingsMap.get(keyCode);
            if (bindings != null)
            {
                matchingBindings.addAll(bindings);
            }
        }
        return matchingBindings;
    }

    public void addKey(InputMappings.Input keyCode, KeyBinding keyBinding)
    {
        KeyModifier keyModifier = keyBinding.getKeyModifier();
        Map<InputMappings.Input, Collection<KeyBinding>> bindingsMap = map.get(keyModifier);
        Collection<KeyBinding> bindingsForKey = bindingsMap.get(keyCode);
        if (bindingsForKey == null)
        {
            bindingsForKey = new ArrayList<KeyBinding>();
            bindingsMap.put(keyCode, bindingsForKey);
        }
        bindingsForKey.add(keyBinding);
    }

    public void removeKey(KeyBinding keyBinding)
    {
        KeyModifier keyModifier = keyBinding.getKeyModifier();
        InputMappings.Input keyCode = keyBinding.getKey();
        Map<InputMappings.Input, Collection<KeyBinding>> bindingsMap = map.get(keyModifier);
        Collection<KeyBinding> bindingsForKey = bindingsMap.get(keyCode);
        if (bindingsForKey != null)
        {
            bindingsForKey.remove(keyBinding);
            if (bindingsForKey.isEmpty())
            {
                bindingsMap.remove(keyCode);
            }
        }
    }

    public void clearMap()
    {
        for (Map<InputMappings.Input, Collection<KeyBinding>> bindings : map.values())
        {
            bindings.clear();
        }
    }
}
