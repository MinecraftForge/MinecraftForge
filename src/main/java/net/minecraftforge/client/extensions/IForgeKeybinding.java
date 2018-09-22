package net.minecraftforge.client.extensions;

import javax.annotation.Nonnull;

import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraftforge.client.settings.IKeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;

public interface IForgeKeybinding
{
    default KeyBinding getKeyBinding() { return (KeyBinding) this; }
    
    @Nonnull InputMappings.Input getKey();

    /**
     * Checks that the key conflict context and modifier are active, and that the keyCode matches this binding.
     */
    default boolean isActiveAndMatches(InputMappings.Input keyCode)
    {
        return keyCode.func_197937_c() != 0 && keyCode.equals(getKey()) && getKeyConflictContext().isActive() && getKeyModifier().isActive(getKeyConflictContext());
    }
    
    default void setToDefault()
    {
        setKeyModifierAndCode(getKeyModifierDefault(), getKeyBinding().func_197977_i());
    }

    void setKeyConflictContext(IKeyConflictContext keyConflictContext);

    IKeyConflictContext getKeyConflictContext();

    KeyModifier getKeyModifierDefault();

    KeyModifier getKeyModifier();

    void setKeyModifierAndCode(KeyModifier keyModifier, InputMappings.Input keyCode);

    /**
     * Returns true when one of the bindings' key codes conflicts with the other's modifier.
     */
    default boolean hasKeyCodeModifierConflict(KeyBinding other)
    {
        if (getKeyConflictContext().conflicts(other.getKeyConflictContext()) || other.getKeyConflictContext().conflicts(getKeyConflictContext()))
        {
            if (getKeyModifier().matches(other.getKey()) || other.getKeyModifier().matches(getKey()))
            {
                return true;
            }
        }
        return false;
    }
}
