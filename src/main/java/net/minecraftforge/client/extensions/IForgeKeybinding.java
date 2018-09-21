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

    void setKeyConflictContext(IKeyConflictContext keyConflictContext);

    IKeyConflictContext getKeyConflictContext();

    KeyModifier getKeyModifierDefault();

    KeyModifier getKeyModifier();

    void setKeyModifierAndCode(KeyModifier keyModifier, InputMappings.Input keyCode);

    default void setToDefault()
    {
        setKeyModifierAndCode(getKeyModifierDefault(), getKeyBinding().func_197977_i());
    }

    default boolean isSetToDefaultValue()
    {
        return getKey().equals(getKeyBinding().func_197977_i()) && getKeyModifier() == getKeyModifierDefault();
    }

    /**
     * Returns true when the other keyBinding conflicts with this one
     */
    default boolean conflicts(KeyBinding other)
    {
        if (getKeyConflictContext().conflicts(other.getKeyConflictContext()) || other.getKeyConflictContext().conflicts(getKeyConflictContext()))
        {
            net.minecraftforge.client.settings.KeyModifier keyModifier = getKeyModifier();
            net.minecraftforge.client.settings.KeyModifier otherKeyModifier = other.getKeyModifier();
            if (keyModifier.matches(other.getKey()) || otherKeyModifier.matches(getKey()))
            {
                return true;
            }
            else if (getKey().equals(other.getKey()))
            {
                return keyModifier == otherKeyModifier ||
                        // IN_GAME key contexts have a conflict when at least one modifier is NONE.
                        // For example: If you hold shift to crouch, you can still press E to open your inventory. This means that a Shift+E hotkey is in conflict with E.
                        // GUI and other key contexts do not have this limitation.
                        (getKeyConflictContext().conflicts(net.minecraftforge.client.settings.KeyConflictContext.IN_GAME) &&
                                (keyModifier == net.minecraftforge.client.settings.KeyModifier.NONE || otherKeyModifier == net.minecraftforge.client.settings.KeyModifier.NONE));
            }
        }
        return false;
    }

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

    default String getDisplayName()
    {
        return getKeyModifier().getLocalizedComboName(getKey());
    }
}
