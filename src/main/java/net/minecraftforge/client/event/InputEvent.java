/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.event;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.world.InteractionHand;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.LogicalSide;
import org.jetbrains.annotations.ApiStatus;
import org.lwjgl.glfw.GLFW;

/**
 * Fired when an input is detected from the user's input devices.
 * See the various subclasses to listen for specific devices and inputs.
 *
 * @see InputEvent.MouseButton
 * @see MouseScrollingEvent
 * @see Key
 * @see InteractionKeyMappingTriggered
 */
public abstract class InputEvent extends Event
{
    @ApiStatus.Internal
    protected InputEvent()
    {
    }

    /**
     * Fired when a mouse button is pressed/released. Sub-events get fired {@link Pre before} and {@link Post after} this happens.
     *
     * <p>These events are fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
     * only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
     *
     * @see <a href="https://www.glfw.org/docs/latest/input_guide.html#input_mouse_button" target="_top">the online GLFW documentation</a>
     * @see Pre
     * @see Post
     */
    public static abstract class MouseButton extends InputEvent
    {
        private final int button;
        private final int action;
        private final int modifiers;

        @ApiStatus.Internal
        protected MouseButton(int button, int action, int modifiers)
        {
            this.button = button;
            this.action = action;
            this.modifiers = modifiers;
        }

        /**
         * {@return the mouse button's input code}
         *
         * @see GLFW mouse constants starting with 'GLFW_MOUSE_BUTTON_'
         * @see <a href="https://www.glfw.org/docs/latest/group__buttons.html" target="_top">the online GLFW documentation</a>
         */
        public int getButton()
        {
            return this.button;
        }

        /**
         * {@return the mouse button's action}
         *
         * @see InputConstants#PRESS
         * @see InputConstants#RELEASE
         */
        public int getAction()
        {
            return this.action;
        }

        /**
         * {@return a bit field representing the active modifier keys}
         *
         * @see InputConstants#MOD_CONTROL CTRL modifier key bit
         * @see GLFW#GLFW_MOD_SHIFT SHIFT modifier key bit
         * @see GLFW#GLFW_MOD_ALT ALT modifier key bit
         * @see GLFW#GLFW_MOD_SUPER SUPER modifier key bit
         * @see GLFW#GLFW_KEY_CAPS_LOCK CAPS LOCK modifier key bit
         * @see GLFW#GLFW_KEY_NUM_LOCK NUM LOCK modifier key bit
         * @see <a href="https://www.glfw.org/docs/latest/group__mods.html" target="_top">the online GLFW documentation</a>
         */
        public int getModifiers()
        {
            return this.modifiers;
        }

        /**
         * Fired when a mouse button is pressed/released, <b>before</b> being processed by vanilla.
         *
         * <p>This event is {@linkplain Cancelable cancellable}, and does not {@linkplain HasResult have a result}.
         * If the event is cancelled, then the mouse event will not be processed by vanilla (e.g. keymappings and screens) </p>
         *
         * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
         * only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
         *
         * @see <a href="https://www.glfw.org/docs/latest/input_guide.html#input_mouse_button" target="_top">the online GLFW documentation</a>
         */
        @Cancelable
        public static class Pre extends MouseButton
        {
            @ApiStatus.Internal
            public Pre(int button, int action, int modifiers)
            {
                super(button, action, modifiers);
            }
        }

        /**
         * Fired when a mouse button is pressed/released, <b>after</b> processing.
         *
         * <p>This event is not {@linkplain Cancelable cancellable}, and does not {@linkplain HasResult have a result}.</p>
         *
         * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
         * only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
         *
         * @see <a href="https://www.glfw.org/docs/latest/input_guide.html#input_mouse_button" target="_top">the online GLFW documentation</a>
         */
        public static class Post extends MouseButton
        {
            @ApiStatus.Internal
            public Post(int button, int action, int modifiers)
            {
                super(button, action, modifiers);
            }
        }
    }

    /**
     * Fired when a mouse scroll wheel is used outside of a screen and a player is loaded, <b>before</b> being
     * processed by vanilla.
     *
     * <p>This event is {@linkplain Cancelable cancellable}, and does not {@linkplain HasResult have a result}.
     * If the event is cancelled, then the mouse scroll event will not be processed further.</p>
     *
     * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
     * only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
     *
     * @see <a href="https://www.glfw.org/docs/latest/input_guide.html#input_mouse_button" target="_top">the online GLFW documentation</a>
     */
    @Cancelable
    public static class MouseScrollingEvent extends InputEvent
    {
        private final double scrollDelta;
        private final double mouseX;
        private final double mouseY;
        private final boolean leftDown;
        private final boolean middleDown;
        private final boolean rightDown;

        @ApiStatus.Internal
        public MouseScrollingEvent(double scrollDelta, boolean leftDown, boolean middleDown, boolean rightDown, double mouseX, double mouseY)
        {
            this.scrollDelta = scrollDelta;
            this.leftDown = leftDown;
            this.middleDown = middleDown;
            this.rightDown = rightDown;
            this.mouseX = mouseX;
            this.mouseY = mouseY;
        }

        /**
         * {@return the amount of change / delta of the mouse scroll}
         */
        public double getScrollDelta()
        {
            return this.scrollDelta;
        }

        /**
         * {@return {@code true} if the left mouse button is pressed}
         */
        public boolean isLeftDown()
        {
            return this.leftDown;
        }

        /**
         * {@return {@code true} if the right mouse button is pressed}
         */
        public boolean isRightDown()
        {
            return this.rightDown;
        }

        /**
         * {@return  {@code true} if the middle mouse button is pressed}
         */
        public boolean isMiddleDown()
        {
            return this.middleDown;
        }

        /**
         * {@return the X position of the mouse cursor}
         */
        public double getMouseX()
        {
            return this.mouseX;
        }

        /**
         * {@return the Y position of the mouse cursor}
         */
        public double getMouseY()
        {
            return this.mouseY;
        }
    }

    /**
     * Fired when a keyboard key input occurs, such as pressing, releasing, or repeating a key.
     *
     * <p>This event is not {@linkplain Cancelable cancellable}, and does not {@linkplain HasResult have a result}.</p>
     *
     * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
     * only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
     */
    public static class Key extends InputEvent
    {
        private final int key;
        private final int scanCode;
        private final int action;
        private final int modifiers;

        @ApiStatus.Internal
        public Key(int key, int scanCode, int action, int modifiers)
        {
            this.key = key;
            this.scanCode = scanCode;
            this.action = action;
            this.modifiers = modifiers;
        }

        /**
         * {@return the {@code GLFW} (platform-agnostic) key code}
         *
         * @see InputConstants input constants starting with {@code KEY_}
         * @see GLFW key constants starting with {@code GLFW_KEY_}
         * @see <a href="https://www.glfw.org/docs/latest/group__keys.html" target="_top">the online GLFW documentation</a>
         */
        public int getKey()
        {
            return this.key;
        }

        /**
         * {@return the platform-specific scan code}
         * <p>
         * The scan code is unique for every key, regardless of whether it has a key code.
         * Scan codes are platform-specific but consistent over time, so keys will have different scan codes depending
         * on the platform but they are safe to save to disk as custom key bindings.
         *
         * @see InputConstants#getKey(int, int)
         */
        public int getScanCode()
        {
            return this.scanCode;
        }

        /**
         * {@return the mouse button's action}
         *
         * @see InputConstants#PRESS
         * @see InputConstants#RELEASE
         * @see InputConstants#REPEAT
         */
        public int getAction()
        {
            return this.action;
        }

        /**
         * {@return a bit field representing the active modifier keys}
         *
         * @see InputConstants#MOD_CONTROL CTRL modifier key bit
         * @see GLFW#GLFW_MOD_SHIFT SHIFT modifier key bit
         * @see GLFW#GLFW_MOD_ALT ALT modifier key bit
         * @see GLFW#GLFW_MOD_SUPER SUPER modifier key bit
         * @see GLFW#GLFW_KEY_CAPS_LOCK CAPS LOCK modifier key bit
         * @see GLFW#GLFW_KEY_NUM_LOCK NUM LOCK modifier key bit
         * @see <a href="https://www.glfw.org/docs/latest/group__mods.html" target="_top">the online GLFW documentation</a>
         */
        public int getModifiers()
        {
            return this.modifiers;
        }
    }

    /**
     * Fired when a keymapping that by default involves clicking the mouse buttons is triggered.
     *
     * <p>The key bindings that trigger this event are:</p>
     * <ul>
     *     <li><b>Use Item</b> - defaults to <em>left mouse click</em></li>
     *     <li><b>Pick Block</b> - defaults to <em>middle mouse click</em></li>
     *     <li><b>Attack</b> - defaults to <em>right mouse click</em></li>
     * </ul>
     *
     * <p>This event is {@linkplain Cancelable cancellable}, and does not {@linkplain HasResult have a result}.
     * If this event is cancelled, then the keymapping's action is not processed further, and the hand will be swung
     * according to {@link #shouldSwingHand()}.</p>
     *
     * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
     * only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
     */
    @Cancelable
    public static class InteractionKeyMappingTriggered extends InputEvent
    {
        private final int button;
        private final KeyMapping keyMapping;
        private final InteractionHand hand;
        private boolean handSwing = true;

        @ApiStatus.Internal
        public InteractionKeyMappingTriggered(int button, KeyMapping keyMapping, InteractionHand hand)
        {
            this.button = button;
            this.keyMapping = keyMapping;
            this.hand = hand;
        }

        /**
         * Sets whether to swing the hand. This takes effect whether or not the event is cancelled.
         *
         * @param value whether to swing the hand
         */
        public void setSwingHand(boolean value)
        {
            handSwing = value;
        }

        /**
         * {@return whether to swing the hand; always takes effect, regardless of cancellation}
         */
        public boolean shouldSwingHand()
        {
            return handSwing;
        }

        /**
         * {@return the hand that caused the input}
         * <p>
         * The event will be called for both hands if this is a use item input regardless
         * of both event's cancellation.
         * Will always be {@link InteractionHand#MAIN_HAND} if this is an attack or pick block input.
         */
        public InteractionHand getHand()
        {
            return hand;
        }

        /**
         * {@return {@code true} if the mouse button is the left mouse button}
         */
        public boolean isAttack()
        {
            return button == 0;
        }

        /**
         * {@return {@code true} if the mouse button is the right mouse button}
         */
        public boolean isUseItem()
        {
            return button == 1;
        }

        /**
         * {@return {@code true} if the mouse button is the middle mouse button}
         */
        public boolean isPickBlock()
        {
            return button == 2;
        }

        /**
         * {@return the key mapping which triggered this event}
         */
        public KeyMapping getKeyMapping()
        {
            return keyMapping;
        }
    }
}
