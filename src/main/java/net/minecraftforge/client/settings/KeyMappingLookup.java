/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.settings;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KeyMappingLookup
{
    private static final EnumMap<KeyModifier, Map<InputConstants.Key, Collection<KeyMapping>>> map = new EnumMap<>(KeyModifier.class);
    static
    {
        for (KeyModifier modifier : KeyModifier.values())
        {
            map.put(modifier, new HashMap<>());
        }
    }

    @Deprecated(forRemoval = true)
    @Nullable
    public KeyMapping get(InputConstants.Key keyCode)
    {
        KeyModifier activeModifier = KeyModifier.getActiveModifier();
        if (!activeModifier.matches(keyCode))
        {
            KeyMapping binding = get(keyCode, activeModifier);
            if (binding != null)
            {
                return binding;
            }
        }
        return get(keyCode, KeyModifier.NONE);
    }

    @Nullable
    private KeyMapping get(InputConstants.Key keyCode, KeyModifier keyModifier)
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

    /**
     * Returns all active keys associated with the given key code and the active
     * modifiers and conflict context.
     *
     * This could probably be renamed to something like getActiveMappings, but
     * that would be a breaking change.
     *
     * @param keyCode the key being pressed
     * @return the list of key mappings
     */
    public List<KeyMapping> getAll(InputConstants.Key keyCode)
    {
        List<KeyMapping> matchingBindings = new ArrayList<KeyMapping>();
        for (Map<InputConstants.Key, Collection<KeyMapping>> bindingsMap : map.values())
        {
            Collection<KeyMapping> bindings = bindingsMap.get(keyCode);
            if (bindings != null)
            {
                for (KeyMapping binding : bindings)
                {
                    if (binding.isConflictContextAndModifierActive())
                        matchingBindings.add(binding);
                }
            }
        }
        return matchingBindings;
    }

    public void put(InputConstants.Key keyCode, KeyMapping keyBinding)
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

    public void remove(KeyMapping keyBinding)
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

    public void clear()
    {
        for (Map<InputConstants.Key, Collection<KeyMapping>> bindings : map.values())
        {
            bindings.clear();
        }
    }
}
