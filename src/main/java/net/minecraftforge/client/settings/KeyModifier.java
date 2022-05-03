/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.settings;

import java.util.function.Supplier;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.InputMappings;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import org.lwjgl.glfw.GLFW;

public enum KeyModifier {
    CONTROL {
        @Override
        public boolean matches(InputMappings.Input key)
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
        public ITextComponent getCombinedName(InputMappings.Input key, Supplier<ITextComponent> defaultLogic)
        {
            String localizationFormatKey = Minecraft.ON_OSX ? "forge.controlsgui.control.mac" : "forge.controlsgui.control";
            return new TranslationTextComponent(localizationFormatKey, defaultLogic.get());
        }
    },
    SHIFT {
        @Override
        public boolean matches(InputMappings.Input key)
        {
            return key.getValue() == GLFW.GLFW_KEY_LEFT_SHIFT || key.getValue() == GLFW.GLFW_KEY_RIGHT_SHIFT;
        }

        @Override
        public boolean isActive(@Nullable IKeyConflictContext conflictContext)
        {
            return Screen.hasShiftDown();
        }

        @Override
        public ITextComponent getCombinedName(InputMappings.Input key, Supplier<ITextComponent> defaultLogic)
        {
            return new TranslationTextComponent("forge.controlsgui.shift", defaultLogic.get());
        }
    },
    ALT {
        @Override
        public boolean matches(InputMappings.Input key)
        {
            return key.getValue() == GLFW.GLFW_KEY_LEFT_ALT || key.getValue() == GLFW.GLFW_KEY_RIGHT_ALT;
        }

        @Override
        public boolean isActive(@Nullable IKeyConflictContext conflictContext)
        {
            return Screen.hasAltDown();
        }

        @Override
        public ITextComponent getCombinedName(InputMappings.Input keyCode, Supplier<ITextComponent> defaultLogic)
        {
            return new TranslationTextComponent("forge.controlsgui.alt", defaultLogic.get());
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
        public ITextComponent getCombinedName(InputMappings.Input key, Supplier<ITextComponent> defaultLogic)
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

    public abstract ITextComponent getCombinedName(InputMappings.Input key, Supplier<ITextComponent> defaultLogic);
}
