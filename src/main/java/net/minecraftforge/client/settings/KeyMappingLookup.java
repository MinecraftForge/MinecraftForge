/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.settings;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KeyMappingLookup {
    private static final EnumMap<KeyModifier, Map<InputConstants.Key, List<KeyMapping>>> map = new EnumMap<>(KeyModifier.class);
    static {
        for (KeyModifier modifier : KeyModifier.values())
            map.put(modifier, new HashMap<>());
    }

    /** Replaced by {@link #getAll(InputConstants.Key) getAll} */
    @Deprecated(forRemoval = true, since = "1.20.1")
    @Nullable
    public KeyMapping get(InputConstants.Key keyCode) {
        var activeModifier = KeyModifier.getActiveModifier();
        if (!activeModifier.matches(keyCode)) {
            var binding = get(keyCode, activeModifier);
            if (binding != null)
                return binding;
        }
        return get(keyCode, KeyModifier.NONE);
    }

    @Nullable
    @Deprecated(forRemoval = true, since = "1.20.1")
    private KeyMapping get(InputConstants.Key keyCode, KeyModifier keyModifier) {
        var bindings = map.get(keyModifier).get(keyCode);
        if (bindings != null) {
            for (var binding : bindings) {
                if (binding.isActiveAndMatches(keyCode))
                    return binding;
            }
        }
        return null;
    }

    /**
     * Returns all active keys associated with the given key code and the active
     * modifiers and conflict context.
     *
     * @param keyCode the key being pressed
     * @return the list of key mappings
     */
    public List<KeyMapping> getAll(InputConstants.Key keyCode) {
        var ret = new ArrayList<KeyMapping>();
        for (var modifier : KeyModifier.getValues(false)) {
            if (!modifier.isActive(null) || modifier.matches(keyCode))
                continue;

            for (var binding : get(modifier, keyCode)) {
                if (binding.isActiveAndMatches(keyCode))
                    ret.add(binding);
            }
        }

        if (!ret.isEmpty())
            return ret;

        for (var binding : get(KeyModifier.NONE, keyCode)) {
            if (binding.isActiveAndMatches(keyCode))
                ret.add(binding);
        }

        return ret;
    }

    private List<KeyMapping> get(KeyModifier modifier, InputConstants.Key keyCode) {
        var bindings = map.get(modifier).get(keyCode);
        return bindings == null ? Collections.emptyList() : bindings;
    }

    public void put(InputConstants.Key keyCode, KeyMapping keyBinding) {
        var bindingsMap = map.get(keyBinding.getKeyModifier());
        var bindingsForKey = bindingsMap.computeIfAbsent(keyCode, k -> new ArrayList<KeyMapping>());
        bindingsForKey.add(keyBinding);
    }

    public void remove(KeyMapping keyBinding) {
        var keyCode = keyBinding.getKey();
        var bindingsMap = map.get(keyBinding.getKeyModifier());
        var bindingsForKey = bindingsMap.get(keyCode);
        if (bindingsForKey != null) {
            bindingsForKey.remove(keyBinding);
            if (bindingsForKey.isEmpty())
                bindingsMap.remove(keyCode);
        }
    }

    public void clear() {
        map.values().forEach(Map::clear);
    }
}
