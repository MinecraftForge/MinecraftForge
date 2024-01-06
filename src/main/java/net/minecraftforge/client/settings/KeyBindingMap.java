/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.settings;

import net.minecraft.client.KeyMapping;
import com.mojang.blaze3d.platform.InputConstants;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KeyBindingMap
{
    private static final EnumMap<KeyModifier, Map<InputConstants.Key, Collection<KeyMapping>>> map = new EnumMap<>(KeyModifier.class);
    static
    {
        for (KeyModifier modifier : KeyModifier.values())
        {
            map.put(modifier, new HashMap<>());
        }
    }

    @Nullable
    public KeyMapping lookupActive(InputConstants.Key keyCode)
    {
        KeyModifier activeModifier = KeyModifier.getActiveModifier();
        if (!activeModifier.matches(keyCode))
        {
            KeyMapping binding = getBinding(keyCode, activeModifier);
            if (binding != null)
            {
                return binding;
            }
        }
        return getBinding(keyCode, KeyModifier.NONE);
    }

    @Nullable
    private KeyMapping getBinding(InputConstants.Key keyCode, KeyModifier keyModifier)
    {
        Collection<KeyMapping> bindings = map.get(keyModifier).get(keyCode);
        if (bindings != null)
        {
            for (KeyMapping binding : bindings)
            {
                if (binding.isActiveAndMatches(keyCode))
                {
                    return binding;
                }
            }
        }
        return null;
    }

    public List<KeyMapping> lookupAll(InputConstants.Key keyCode)
    {
        List<KeyMapping> matchingBindings = new ArrayList<KeyMapping>();
        for (Map<InputConstants.Key, Collection<KeyMapping>> bindingsMap : map.values())
        {
            Collection<KeyMapping> bindings = bindingsMap.get(keyCode);
            if (bindings != null)
            {
                matchingBindings.addAll(bindings);
            }
        }
        return matchingBindings;
    }

    public void addKey(InputConstants.Key keyCode, KeyMapping keyBinding)
    {
        KeyModifier keyModifier = keyBinding.getKeyModifier();
        Map<InputConstants.Key, Collection<KeyMapping>> bindingsMap = map.get(keyModifier);
        Collection<KeyMapping> bindingsForKey = bindingsMap.get(keyCode);
        if (bindingsForKey == null)
        {
            bindingsForKey = new ArrayList<KeyMapping>();
            bindingsMap.put(keyCode, bindingsForKey);
        }
        bindingsForKey.add(keyBinding);
    }

    public void removeKey(KeyMapping keyBinding)
    {
        KeyModifier keyModifier = keyBinding.getKeyModifier();
        InputConstants.Key keyCode = keyBinding.getKey();
        Map<InputConstants.Key, Collection<KeyMapping>> bindingsMap = map.get(keyModifier);
        Collection<KeyMapping> bindingsForKey = bindingsMap.get(keyCode);
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
        for (Map<InputConstants.Key, Collection<KeyMapping>> bindings : map.values())
        {
            bindings.clear();
        }
    }
}
