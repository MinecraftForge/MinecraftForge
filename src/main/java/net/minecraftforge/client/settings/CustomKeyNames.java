/*
 * Minecraft Forge
 * Copyright (c) 2017.
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

import net.minecraft.client.resources.I18n;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Allow overriding the keybinding to name function. This lets one define names for
 * keys other than for example "Button -99".
 *
 * Useful context: adding a special keybinding for handling mousewheel and having it
 * display correctly in the settings GUI.
 */
public class CustomKeyNames
{
    private static CustomKeyNames INSTANCE;

    /**
     * Add a custom key name.
     * @param keyCode the key code to replace. Must be less than -199 or greater than 1000
     * @param languageKey the language format key to use
     */
    public static void addCustomKeyName(int keyCode, String languageKey) {
        if (keyCode > -200 && keyCode < 1000) {
            throw new IllegalArgumentException("You cannot override an existing keyCode");
        }
        if (INSTANCE == null) {
            INSTANCE = new CustomKeyNames();
        }
        INSTANCE.customKeyNames.put(keyCode, languageKey);
    }

    static String getCustomKeyName(int keyCode, Function<Integer, String> defaultLookup) {
        if (INSTANCE == null || ! INSTANCE.customKeyNames.containsKey(keyCode)) {
            return defaultLookup.apply(keyCode);
        }
        else {
            return I18n.format(INSTANCE.customKeyNames.get(keyCode));
        }
    }
    private final Map<Integer, String> customKeyNames = new HashMap<>();
}
