/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.settings;

import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import com.mojang.blaze3d.platform.InputConstants;

import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

public enum KeyModifier {
    CONTROL {
        @Override
        public boolean matches(InputConstants.Key key)
        {
            int keyCode = key.getValue();
            if (Minecraft.ON_OSX)
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
            return Screen.hasControlDown();
        }

        @Override
        public Component getCombinedName(InputConstants.Key key, Supplier<Component> defaultLogic)
        {
            String localizationFormatKey = Minecraft.ON_OSX ? "forge.controlsgui.control.mac" : "forge.controlsgui.control";
            return Component.translatable(localizationFormatKey, defaultLogic.get());
        }
    },
    SHIFT {
        @Override
        public boolean matches(InputConstants.Key key)
        {
            return key.getValue() == GLFW.GLFW_KEY_LEFT_SHIFT || key.getValue() == GLFW.GLFW_KEY_RIGHT_SHIFT;
        }

        @Override
        public boolean isActive(@Nullable IKeyConflictContext conflictContext)
        {
            return Screen.hasShiftDown();
        }

        @Override
        public Component getCombinedName(InputConstants.Key key, Supplier<Component> defaultLogic)
        {
            return Component.translatable("forge.controlsgui.shift", defaultLogic.get());
        }
    },
    ALT {
        @Override
        public boolean matches(InputConstants.Key key)
        {
            return key.getValue() == GLFW.GLFW_KEY_LEFT_ALT || key.getValue() == GLFW.GLFW_KEY_RIGHT_ALT;
        }

        @Override
        public boolean isActive(@Nullable IKeyConflictContext conflictContext)
        {
            return Screen.hasAltDown();
        }

        @Override
        public Component getCombinedName(InputConstants.Key keyCode, Supplier<Component> defaultLogic)
        {
            return Component.translatable("forge.controlsgui.alt", defaultLogic.get());
        }
    },
    NONE {
        @Override
        public boolean matches(InputConstants.Key key)
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
        public Component getCombinedName(InputConstants.Key key, Supplier<Component> defaultLogic)
        {
            return defaultLogic.get();
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

    public static boolean isKeyCodeModifier(InputConstants.Key key)
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

    public abstract boolean matches(InputConstants.Key key);

    public abstract boolean isActive(@Nullable IKeyConflictContext conflictContext);

    public abstract Component getCombinedName(InputConstants.Key key, Supplier<Component> defaultLogic);
}
