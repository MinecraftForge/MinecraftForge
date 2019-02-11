/*
 * Minecraft Forge
 * Copyright (c) 2016-2019.
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

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.InputMappings;

import org.lwjgl.glfw.GLFW;

public enum KeyModifier {
    CONTROL {
        @Override
        public boolean matches(InputMappings.Input key)
        {
            int keyCode = key.getKeyCode();
            if (Minecraft.IS_RUNNING_ON_MAC)
            {
                return keyCode == GLFW.GLFW_KEY_LEFT_ALT || keyCode == GLFW.GLFW_KEY_RIGHT_ALT;
            }
            else
            {
                return keyCode == GLFW.GLFW_KEY_LEFT_CONTROL || keyCode == GLFW.GLFW_KEY_RIGHT_CONTROL;
            }
        }

        @Override
        public boolean isActive(@Nullable IKeyConflictContext conflictContext)
        {
            return GuiScreen.isCtrlKeyDown();
        }

        @Override
        public String getLocalizedComboName(InputMappings.Input key)
        {
            String keyName = key.getName();
            String localizationFormatKey = Minecraft.IS_RUNNING_ON_MAC ? "forge.controlsgui.control.mac" : "forge.controlsgui.control";
            return I18n.format(localizationFormatKey, keyName);
        }
    },
    SHIFT {
        @Override
        public boolean matches(InputMappings.Input key)
        {
            return key.getKeyCode() == GLFW.GLFW_KEY_LEFT_SHIFT || key.getKeyCode() == GLFW.GLFW_KEY_RIGHT_SHIFT;
        }

        @Override
        public boolean isActive(@Nullable IKeyConflictContext conflictContext)
        {
            return GuiScreen.isShiftKeyDown();
        }

        @Override
        public String getLocalizedComboName(InputMappings.Input key)
        {
            return I18n.format("forge.controlsgui.shift", key.getName());
        }
    },
    ALT {
        @Override
        public boolean matches(InputMappings.Input key)
        {
            return key.getKeyCode() == GLFW.GLFW_KEY_LEFT_ALT || key.getKeyCode() == GLFW.GLFW_KEY_RIGHT_ALT;
        }

        @Override
        public boolean isActive(@Nullable IKeyConflictContext conflictContext)
        {
            return GuiScreen.isAltKeyDown();
        }

        @Override
        public String getLocalizedComboName(InputMappings.Input keyCode)
        {
            return I18n.format("forge.controlsgui.alt", keyCode.getName());
        }
    },
    NONE {
        @Override
        public boolean matches(InputMappings.Input key)
        {
            return false;
        }

        @Override
        public boolean isActive(@Nullable IKeyConflictContext conflictContext)
        {
            if (conflictContext != null && !conflictContext.conflicts(KeyConflictContext.IN_GAME))
            {
                for (KeyModifier keyModifier : MODIFIER_VALUES)
                {
                    if (keyModifier.isActive(conflictContext))
                    {
                        return false;
                    }
                }
            }
            return true;
        }

        @Override
        public String getLocalizedComboName(InputMappings.Input key)
        {
            return key.getName();
        }
    };

    public static final KeyModifier[] MODIFIER_VALUES = {SHIFT, CONTROL, ALT};

    public static KeyModifier getActiveModifier()
    {
        for (KeyModifier keyModifier : MODIFIER_VALUES)
        {
            if (keyModifier.isActive(null))
            {
                return keyModifier;
            }
        }
        return NONE;
    }

    public static boolean isKeyCodeModifier(InputMappings.Input key)
    {
        for (KeyModifier keyModifier : MODIFIER_VALUES)
        {
            if (keyModifier.matches(key))
            {
                return true;
            }
        }
        return false;
    }

    public static KeyModifier valueFromString(String stringValue)
    {
        try
        {
            return valueOf(stringValue);
        }
        catch (NullPointerException | IllegalArgumentException ignored)
        {
            return NONE;
        }
    }

    public abstract boolean matches(InputMappings.Input key);

    public abstract boolean isActive(@Nullable IKeyConflictContext conflictContext);

    public abstract String getLocalizedComboName(InputMappings.Input key);
}
