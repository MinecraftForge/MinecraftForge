package net.minecraftforge.client.settings;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import org.lwjgl.input.Keyboard;

public enum KeyModifier {
    CONTROL {
        @Override
        public boolean matches(int keyCode)
        {
            if (Minecraft.isRunningOnMac)
            {
                return keyCode == Keyboard.KEY_LMETA || keyCode == Keyboard.KEY_RMETA;
            }
            else
            {
                return keyCode == Keyboard.KEY_LCONTROL || keyCode == Keyboard.KEY_RCONTROL;
            }
        }

        @Override
        public boolean isActive()
        {
            return GuiScreen.isCtrlKeyDown();
        }

        @Override
        public String getLocalizedComboName(int keyCode)
        {
            String keyName = GameSettings.getKeyDisplayString(keyCode);
            return I18n.format("forge.controlsgui.control", keyName);
        }
    },
    SHIFT {
        @Override
        public boolean matches(int keyCode)
        {
            return keyCode == Keyboard.KEY_LSHIFT || keyCode == Keyboard.KEY_RSHIFT;
        }

        @Override
        public boolean isActive()
        {
            return GuiScreen.isShiftKeyDown();
        }

        @Override
        public String getLocalizedComboName(int keyCode)
        {
            String keyName = GameSettings.getKeyDisplayString(keyCode);
            return I18n.format("forge.controlsgui.shift", keyName);
        }
    },
    ALT {
        @Override
        public boolean matches(int keyCode)
        {
            return keyCode == Keyboard.KEY_LMENU || keyCode == Keyboard.KEY_RMENU;
        }

        @Override
        public boolean isActive()
        {
            return GuiScreen.isAltKeyDown();
        }

        @Override
        public String getLocalizedComboName(int keyCode)
        {
            String keyName = GameSettings.getKeyDisplayString(keyCode);
            return I18n.format("forge.controlsgui.alt", keyName);
        }
    },
    NONE {
        @Override
        public boolean matches(int keyCode)
        {
            return false;
        }

        @Override
        public boolean isActive()
        {
            return true;
        }

        @Override
        public String getLocalizedComboName(int keyCode)
        {
            return GameSettings.getKeyDisplayString(keyCode);
        }
    };

    public static final KeyModifier[] MODIFIER_VALUES = {SHIFT, CONTROL, ALT};

    public static KeyModifier getActiveModifier()
    {
        for (KeyModifier keyModifier : MODIFIER_VALUES)
        {
            if (keyModifier.isActive())
            {
                return keyModifier;
            }
        }
        return NONE;
    }

    public static boolean isKeyCodeModifier(int keyCode)
    {
        for (KeyModifier keyModifier : MODIFIER_VALUES)
        {
            if (keyModifier.matches(keyCode))
            {
                return true;
            }
        }
        return false;
    }

    public abstract boolean matches(int keyCode);

    public abstract boolean isActive();

    public abstract String getLocalizedComboName(int keyCode);
}
