package net.minecraftforge.client.settings;

import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.IntHashMap;

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

    public KeyBinding lookupActive(int keyCode)
    {
        KeyModifier activeModifier = KeyModifier.isKeyCodeModifier(keyCode) ? KeyModifier.NONE : KeyModifier.getActiveModifier();
        Collection<KeyBinding> bindings = map.get(activeModifier).lookup(keyCode);
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
