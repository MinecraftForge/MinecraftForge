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

package net.minecraftforge.client.event;

import net.minecraftforge.eventbus.api.Event;

public class InputEvent extends Event
{
    public static class MouseInputEvent extends InputEvent
    {
        private final int button;
        private final int action;
        private final int mods;
        public MouseInputEvent(int button, int action, int mods)
        {
            this.button = button;
            this.action = action;
            this.mods = mods;
        }

        public int getButton()
        {
            return this.button;
        }

        public int getAction()
        {
            return this.action;
        }

        public int getMods()
        {
            return this.mods;
        }
    }

    public static class KeyInputEvent extends InputEvent
    {
        private final int key;
        private final int scanCode;
        private final int action;
        private final int modifiers;
        public KeyInputEvent(int key, int scanCode, int action, int modifiers)
        {
            this.key = key;
            this.scanCode = scanCode;
            this.action = action;
            this.modifiers = modifiers;
        }

        public int getKey()
        {
            return this.key;
        }

        public int getScanCode()
        {
            return this.scanCode;
        }

        public int getAction()
        {
            return this.action;
        }

        public int getModifiers()
        {
            return this.modifiers;
        }
    }
}
