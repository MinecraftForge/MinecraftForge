/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
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

import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.util.Hand;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

import org.lwjgl.glfw.GLFW;

public class InputEvent extends Event
{
    /**
     * A cancellable mouse event fired before key bindings are updated
     */
    @Cancelable
    public static class RawMouseEvent extends InputEvent
    {
        private final int button;
        private final int action;
        private final int mods;

        public RawMouseEvent(int button, int action, int mods)
        {
            this.button = button;
            this.action = action;
            this.mods = mods;
        }

        /**
         * The mouse button that triggered this event.
         * https://www.glfw.org/docs/latest/group__buttons.html
         *
         * @see GLFW mouse constants starting with "GLFW_MOUSE_BUTTON_"
         */
        public int getButton()
        {
            return this.button;
        }

        /**
         * Integer representing the mouse button's action.
         *
         * @see GLFW#GLFW_PRESS
         * @see GLFW#GLFW_RELEASE
         */
        public int getAction()
        {
            return this.action;
        }

        /**
         * Bit field representing the modifier keys pressed.
         * https://www.glfw.org/docs/latest/group__mods.html
         *
         * @see GLFW#GLFW_MOD_SHIFT
         * @see GLFW#GLFW_MOD_CONTROL
         * @see GLFW#GLFW_MOD_ALT
         * @see GLFW#GLFW_MOD_SUPER
         */
        public int getMods()
        {
            return this.mods;
        }
    }

    /**
     * This event fires when a mouse input is detected.
     */
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

        /**
         * The mouse button that triggered this event.
         * https://www.glfw.org/docs/latest/group__buttons.html
         *
         * @see GLFW mouse constants starting with "GLFW_MOUSE_BUTTON_"
         */
        public int getButton()
        {
            return this.button;
        }

        /**
         * Integer representing the mouse button's action.
         *
         * @see GLFW#GLFW_PRESS
         * @see GLFW#GLFW_RELEASE
         */
        public int getAction()
        {
            return this.action;
        }

        /**
         * Bit field representing the modifier keys pressed.
         * https://www.glfw.org/docs/latest/group__mods.html
         *
         * @see GLFW#GLFW_MOD_SHIFT
         * @see GLFW#GLFW_MOD_CONTROL
         * @see GLFW#GLFW_MOD_ALT
         * @see GLFW#GLFW_MOD_SUPER
         */
        public int getMods()
        {
            return this.mods;
        }
    }

    /**
     * This event fires when the mouse scroll wheel is used outside of a gui.
     */
    @Cancelable
    public static class MouseScrollEvent extends InputEvent
    {
        private final double scrollDelta;
        private final double mouseX;
        private final double mouseY;
        private final boolean leftDown;
        private final boolean middleDown;
        private final boolean rightDown;
        public MouseScrollEvent(double scrollDelta, boolean leftDown, boolean middleDown, boolean rightDown, double mouseX, double mouseY)
        {
            this.scrollDelta = scrollDelta;
            this.leftDown = leftDown;
            this.middleDown = middleDown;
            this.rightDown = rightDown;
            this.mouseX = mouseX;
            this.mouseY = mouseY;
        }

        public double getScrollDelta()
        {
            return this.scrollDelta;
        }

        public boolean isLeftDown()
        {
            return this.leftDown;
        }

        public boolean isRightDown()
        {
            return this.rightDown;
        }

        public boolean isMiddleDown()
        {
            return this.middleDown;
        }

        public double getMouseX()
        {
            return this.mouseX;
        }

        public double getMouseY()
        {
            return this.mouseY;
        }
    }

    /**
     * This event fires when a keyboard input is detected.
     */
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

        /**
         * The keyboard key that triggered this event.
         * https://www.glfw.org/docs/latest/group__keys.html
         *
         * @see GLFW key constants starting with "GLFW_KEY_"
         */
        public int getKey()
        {
            return this.key;
        }

        /**
         * Platform-specific scan code.
         * Used for {@link InputMappings#getInputByCode(int, int)}
         *
         * The scan code is unique for every key, regardless of whether it has a key code.
         * Scan codes are platform-specific but consistent over time, so keys will have different scan codes depending
         * on the platform but they are safe to save to disk as custom key bindings.
         */
        public int getScanCode()
        {
            return this.scanCode;
        }

        /**
         * Integer representing the key's action.
         *
         * @see GLFW#GLFW_PRESS
         * @see GLFW#GLFW_RELEASE
         * @see GLFW#GLFW_REPEAT
         */
        public int getAction()
        {
            return this.action;
        }

        /**
         * Bit field representing the modifier keys pressed.
         * https://www.glfw.org/docs/latest/group__mods.html
         *
         * @see GLFW#GLFW_MOD_SHIFT
         * @see GLFW#GLFW_MOD_CONTROL
         * @see GLFW#GLFW_MOD_ALT
         * @see GLFW#GLFW_MOD_SUPER
         */
        public int getModifiers()
        {
            return this.modifiers;
        }
    }

    /**
     * This event fires when one of the keybindings that by default involves clicking the mouse buttons
     * is triggered.
     *
     * These key bindings are use item, pick block and attack keybindings. (right, middle and left mouse click)
     * In the case that these key bindings are re-bound to a keyboard key the event will still be fired as normal.
     */
    @Cancelable
    public static class ClickInputEvent extends InputEvent
    {
        private final int button;
        private final KeyBinding keyBinding;
        private final Hand hand;
        private boolean handSwing = true;
        public ClickInputEvent(int button, KeyBinding keyBinding, Hand hand)
        {
            this.button = button;
            this.keyBinding = keyBinding;
            this.hand = hand;
        }

        /**
         * Set to false to disable the hand swing animation.
         * Has no effect if this is a pick block input.
         */
        public void setSwingHand(boolean value)
        {
            handSwing = value;
        }

        public boolean shouldSwingHand()
        {
            return handSwing;
        }

        /**
         * The hand which is causing the event to get triggered.
         * The event will be called for both hands if this is a use item input regardless
         * of if either gets canceled.
         * Will always be MAIN_HAND if this is an attack or pick block input.
         */
        public Hand getHand()
        {
            return hand;
        }

        public boolean isAttack()
        {
            return button == 0;
        }

        public boolean isUseItem()
        {
            return button == 1;
        }

        public boolean isPickBlock()
        {
            return button == 2;
        }

        public KeyBinding getKeyBinding()
        {
            return keyBinding;
        }
    }
}
