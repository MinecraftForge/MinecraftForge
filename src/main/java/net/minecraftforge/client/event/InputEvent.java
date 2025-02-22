/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.event;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.world.InteractionHand;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.bus.CancellableEventBus;
import net.minecraftforge.eventbus.api.bus.EventBus;
import net.minecraftforge.eventbus.api.event.MutableEvent;
import net.minecraftforge.eventbus.api.event.RecordEvent;
import net.minecraftforge.eventbus.api.event.characteristic.Cancellable;
import net.minecraftforge.fml.LogicalSide;
import org.intellij.lang.annotations.MagicConstant;
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
public sealed interface InputEvent {
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
    sealed interface MouseButton extends InputEvent {
        /**
         * {@return the mouse button's input code}
         *
         * @see GLFW mouse constants starting with 'GLFW_MOUSE_BUTTON_'
         * @see <a href="https://www.glfw.org/docs/latest/group__buttons.html" target="_top">the online GLFW documentation</a>
         */
        int button();

        /**
         * {@return the mouse button's action}
         *
         * @see InputConstants#PRESS
         * @see InputConstants#RELEASE
         */
        @MagicConstant(intValues = { InputConstants.PRESS, InputConstants.RELEASE })
        int action();

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
        int modifiers();

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
        record Pre(int button, int action, int modifiers) implements Cancellable, MouseButton, RecordEvent {
            public static final EventBus<Pre> BUS = EventBus.create(Pre.class);
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
        record Post(int button, int action, int modifiers) implements Cancellable, MouseButton, RecordEvent {
            public static final CancellableEventBus<Post> BUS = CancellableEventBus.create(Post.class);
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
     * @param deltaX the amount of change / delta of the mouse scroll in the vertical direction
     * @param deltaY the amount of change / delta of the mouse scroll in the horizontal direction
     * @param leftDown {@code true} if the left mouse button is pressed
     * @param middleDown {@code true} if the right mouse button is pressed
     * @param rightDown {@code true} if the middle mouse button is pressed
     * @param mouseX the X position of the mouse cursor
     * @param mouseY the Y position of the mouse cursor
     *
     * @see <a href="https://www.glfw.org/docs/latest/input_guide.html#input_mouse_button" target="_top">the online GLFW documentation</a>
     */
    record MouseScrollingEvent(
            double deltaX,
            double deltaY,
            boolean leftDown,
            boolean middleDown,
            boolean rightDown,
            double mouseX,
            double mouseY
    ) implements Cancellable, InputEvent, RecordEvent {
        public static final CancellableEventBus<MouseScrollingEvent> BUS = CancellableEventBus.create(MouseScrollingEvent.class);
    }

    /**
     * Fired when a keyboard key input occurs, such as pressing, releasing, or repeating a key.
     *
     * <p>This event is not {@linkplain Cancelable cancellable}, and does not {@linkplain HasResult have a result}.</p>
     *
     * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
     * only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
     */
    record Key(int key, int scanCode, int action, int modifiers) implements InputEvent, RecordEvent {
        public static final EventBus<Key> BUS = EventBus.create(Key.class);

        /**
         * {@return the {@code GLFW} (platform-agnostic) key code}
         *
         * @see InputConstants input constants starting with {@code KEY_}
         * @see GLFW key constants starting with {@code GLFW_KEY_}
         * @see <a href="https://www.glfw.org/docs/latest/group__keys.html" target="_top">the online GLFW documentation</a>
         */
        public int key() {
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
        public int scanCode() {
            return this.scanCode;
        }

        /**
         * {@return the mouse button's action}
         *
         * @see InputConstants#PRESS
         * @see InputConstants#RELEASE
         * @see InputConstants#REPEAT
         */
        public int action() {
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
        public int modifiers() {
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
    // TODO: Change the 'button' to sub events. - Lex 0422202
    final class InteractionKeyMappingTriggered extends MutableEvent implements Cancellable, InputEvent {
        public static final CancellableEventBus<InteractionKeyMappingTriggered> BUS = CancellableEventBus.create(InteractionKeyMappingTriggered.class);

        private final int button;
        private final KeyMapping keyMapping;
        private final InteractionHand hand;
        private boolean handSwing = true;

        @ApiStatus.Internal
        public InteractionKeyMappingTriggered(int button, KeyMapping keyMapping, InteractionHand hand) {
            this.button = button;
            this.keyMapping = keyMapping;
            this.hand = hand;
        }

        /**
         * Sets whether to swing the hand. This takes effect whether or not the event is cancelled.
         *
         * @param value whether to swing the hand
         */
        public void setSwingHand(boolean value) {
            handSwing = value;
        }

        /**
         * {@return whether to swing the hand; always takes effect, regardless of cancellation}
         */
        public boolean shouldSwingHand() {
            return handSwing;
        }

        /**
         * {@return the hand that caused the input}
         * <p>
         * The event will be called for both hands if this is a use item input regardless
         * of both event's cancellation.
         * Will always be {@link InteractionHand#MAIN_HAND} if this is an attack or pick block input.
         */
        public InteractionHand getHand() {
            return hand;
        }

        /**
         * {@return {@code true} if the mouse button is the left mouse button}
         */
        public boolean isAttack() {
            return button == 0;
        }

        /**
         * {@return {@code true} if the mouse button is the right mouse button}
         */
        public boolean isUseItem() {
            return button == 1;
        }

        /**
         * {@return {@code true} if the mouse button is the middle mouse button}
         */
        public boolean isPickBlock() {
            return button == 2;
        }

        /**
         * {@return the key mapping which triggered this event}
         */
        public KeyMapping getKeyMapping() {
            return keyMapping;
        }
    }
}
