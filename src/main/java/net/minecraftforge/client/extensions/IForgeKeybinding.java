/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
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
        return keyCode != InputMappings.INPUT_INVALID && keyCode.equals(getKey()) && getKeyConflictContext().isActive() && getKeyModifier().isActive(getKeyConflictContext());
    }
    
    default void setToDefault()
    {
        setKeyModifierAndCode(getKeyModifierDefault(), getKeyBinding().getDefault());
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
