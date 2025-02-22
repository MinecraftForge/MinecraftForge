/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.event;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.HasResult;
import net.minecraftforge.common.util.Result;
import net.minecraftforge.eventbus.api.bus.CancellableEventBus;
import net.minecraftforge.eventbus.api.bus.EventBus;
import net.minecraftforge.eventbus.api.event.MutableEvent;
import net.minecraftforge.eventbus.api.event.RecordEvent;
import net.minecraftforge.eventbus.api.event.characteristic.Cancellable;
import net.minecraftforge.fml.LogicalSide;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

/**
 * Fired on different events/actions when a {@link Screen} is active and visible.
 * See the various subclasses for listening to different events.
 *
 * <p>These events are fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
 * only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
 *
 * @see Init
 * @see Render
 * @see BackgroundRendered
 * @see MouseInput
 * @see KeyInput
 */
public sealed interface ScreenEvent {
    /** {@return the screen that caused this event} */
    Screen screen();

    /**
     * Fired when a screen is being initialized.
     * See the two subclasses for listening before and after the initialization.
     *
     * <p>Listeners added through this event may also be marked as renderable or narratable, if they inherit from
     * {@link net.minecraft.client.gui.components.Renderable} and {@link net.minecraft.client.gui.narration.NarratableEntry}
     * respectively.</p>
     *
     * @see Init.Pre
     * @see Init.Post
     */
    sealed interface Init extends ScreenEvent {
        Consumer<GuiEventListener> adder();
        Consumer<GuiEventListener> remover();

        /**
         * {@return unmodifiable view of list of event listeners on the screen}
         */
        List<GuiEventListener> listenerList();

        /**
         * Adds the given {@link GuiEventListener} to the screen.
         *
         * @param listener the listener to add
         */
        default void addListener(GuiEventListener listener) {
            adder().accept(listener);
        }

        /**
         * Removes the given {@link GuiEventListener} from the screen.
         *
         * @param listener the listener to remove
         */
        default void removeListener(GuiEventListener listener) {
            remover().accept(listener);
        }

        /**
         * Fired <b>before</b> the screen's overridable initialization method is fired.
         *
         * <p>This event is {@linkplain Cancelable cancellable}, and does not {@linkplain HasResult have a result}.
         * If the event is cancelled, the initialization method will not be called, and the widgets and children lists
         * will not be cleared.</p>
         *
         * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
         * only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
         */
        record Pre(
                Screen screen,
                List<GuiEventListener> listenerList,
                Consumer<GuiEventListener> adder,
                Consumer<GuiEventListener> remover
        ) implements Cancellable, Init, RecordEvent {
            public static final CancellableEventBus<Pre> BUS = CancellableEventBus.create(Pre.class);

            public Pre {
                listenerList = Collections.unmodifiableList(listenerList);
            }
        }

        /**
         * Fired <b>after</b> the screen's overridable initialization method is called.
         *
         * <p>This event is not {@linkplain Cancelable cancellable}, and does not {@linkplain HasResult have a result}.</p>
         *
         * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
         * only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
         */
        record Post(
                Screen screen,
                List<GuiEventListener> listenerList,
                Consumer<GuiEventListener> adder,
                Consumer<GuiEventListener> remover
        ) implements Init, RecordEvent {
            public static final EventBus<Post> BUS = EventBus.create(Post.class);

            public Post {
                listenerList = Collections.unmodifiableList(listenerList);
            }
        }
    }

    /**
     * Fired when a screen is being drawn.
     * See the two subclasses for listening before and after drawing.
     *
     * @see Render.Pre
     * @see Render.Post
     */
    sealed interface Render extends ScreenEvent {
        /**
         * {@return the gui graphics used for rendering}
         */
        GuiGraphics guiGraphics();

        /**
         * {@return the X coordinate of the mouse pointer}
         */
        int mouseX();

        /**
         * {@return the Y coordinate of the mouse pointer}
         */
        int mouseY();

        /**
         * {@return the partial tick}
         */
        float partialTick();

        /**
         * Fired <b>before</b> the screen is drawn.
         *
         * <p>This event is {@linkplain Cancelable cancellable}, and does not {@linkplain HasResult have a result}.
         * If the event is cancelled, the screen will not be drawn.</p>
         *
         * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
         * only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
         */
        record Pre(Screen screen, GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick)
                implements Cancellable, Render, RecordEvent {
            public static final CancellableEventBus<Pre> BUS = CancellableEventBus.create(Pre.class);
        }

        /**
         * Fired <b>after</b> the screen is drawn.
         *
         * <p>This event is not {@linkplain Cancelable cancellable}, and does not {@linkplain HasResult have a result}.</p>
         *
         * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
         * only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
         */
        record Post(Screen screen, GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick)
                implements Render, RecordEvent {
            public static final EventBus<Post> BUS = EventBus.create(Post.class);
        }
    }

    /**
     * Fired directly after the background of the screen is drawn.
     * Can be used for drawing above the background but below the tooltips.
     *
     * <p>This event is not {@linkplain Cancelable cancellable}, and does not {@linkplain HasResult have a result}.</p>
     *
     * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
     * only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
     *
     * @param guiGraphics the gui graphics used for rendering
     */
    record BackgroundRendered(Screen screen, GuiGraphics guiGraphics) implements RecordEvent, ScreenEvent {
        public static final EventBus<BackgroundRendered> BUS = EventBus.create(BackgroundRendered.class);
    }

    /**
     * Fired ahead of rendering any active mob effects in the {@link EffectsInInventory inventory screen}.
     * Can be used to select the size of the effects display (full or compact) or even hide or replace vanilla's rendering entirely.
     * This event can also be used to modify the horizontal position of the stack of effects being rendered.
     *
     * <p>This event is {@linkplain Cancelable cancellable} and does not {@linkplain HasResult have a result}.
     * Cancelling this event will prevent vanilla rendering.</p>
     *
     * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
     * only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
     */
    final class RenderInventoryMobEffects extends MutableEvent implements Cancellable, ScreenEvent {
        public static final CancellableEventBus<RenderInventoryMobEffects> BUS = CancellableEventBus.create(RenderInventoryMobEffects.class);

        private final Screen screen;
        private final int availableSpace;
        private boolean compact;
        private int horizontalOffset;

        @ApiStatus.Internal
        public RenderInventoryMobEffects(Screen screen, int availableSpace, boolean compact, int horizontalOffset) {
            this.screen = screen;
            this.availableSpace = availableSpace;
            this.compact = compact;
            this.horizontalOffset = horizontalOffset;
        }

        /**
         * The available space to the right of the inventory.
         */
        public int getAvailableSpace() {
            return availableSpace;
        }

        /**
         * Whether the effects should be rendered in compact mode (only icons, no text), or the default full size.
         */
        public boolean isCompact() {
            return compact;
        }

        /**
         * The distance from the left side of the screen that the effect stack is rendered. Positive values shift this more to the right.
         */
        public int getHorizontalOffset() {
            return horizontalOffset;
        }

        /**
         * Replaces the horizontal offset of the effect stack
         */
        public void setHorizontalOffset(int offset) {
            horizontalOffset = offset;
        }

        /**
         * Adds to the horizontal offset of the effect stack. Negative values are acceptable.
         */
        public void addHorizontalOffset(int offset) {
            horizontalOffset += offset;
        }

        /**
         * Sets whether the effects should be rendered in compact mode (only icons, no text), or the default full size.
         */
        public void setCompact(boolean compact) {
            this.compact = compact;
        }

        @Override
        public Screen screen() {
            return screen;
        }
    }

    /**
     * Fired whenever an action is performed by the mouse.
     * See the various subclasses to listen for different actions.
     *
     * @see MouseButtonPressed
     * @see MouseButtonReleased
     * @see MouseDragged
     * @see MouseScrolled
     */
    sealed interface MouseInput extends ScreenEvent {
        /**
         * {@return the X position of the mouse cursor, relative to the screen}
         */
        double mouseX();

        /**
         * {@return the Y position of the mouse cursor, relative to the screen}
         */
        double mouseY();
    }

    /**
     * Fired when a mouse button is pressed.
     * See the two subclasses for listening before and after the normal handling.
     *
     * @see MouseButtonPressed.Pre
     * @see MouseButtonPressed.Post
     */
    sealed interface MouseButtonPressed extends MouseInput {
        /**
         * {@return the mouse button's input code}
         *
         * @see GLFW mouse constants starting with 'GLFW_MOUSE_BUTTON_'
         * @see <a href="https://www.glfw.org/docs/latest/group__buttons.html" target="_top">the online GLFW documentation</a>
         */
        int button();

        /**
         * Fired <b>before</b> the mouse click is handled by the screen.
         *
         * <p>This event is {@linkplain Cancelable cancellable}, and does not {@linkplain HasResult have a result}.
         * If the event is cancelled, the screen's mouse click handler will be bypassed
         * and the corresponding {@link MouseButtonPressed.Post} will not be fired.</p>
         *
         * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
         * only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
         */
        record Pre(Screen screen, double mouseX, double mouseY, int button)
                implements Cancellable, MouseButtonPressed, RecordEvent {
            public static final CancellableEventBus<Pre> BUS = CancellableEventBus.create(Pre.class);
        }

        /**
         * Fired <b>after</b> the mouse click is handled, if the corresponding {@link MouseButtonPressed.Pre} was not
         * cancelled.
         *
         * <p>This event is not {@linkplain Cancelable cancellable}, {@linkplain HasResult has a result}.</p>
         * <ul>
         *   <li>{@link Result#ALLOW} - forcibly sets the mouse click as handled</li>
         *   <li>{@link Result#DEFAULT} - defaults to the return value of
         *   {@link Screen#mouseClicked(double, double, int)} from the screen (see {@link #wasHandled()}.</li>
         *   <li>{@link Result#DENY} - forcibly sets the mouse click as not handled.</li>
         * </ul>
         *
         * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
         * only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
         *
         * @param wasHandled {@code true} if the mouse click was already handled by its screen
         */
        record Post(Screen screen, double mouseX, double mouseY, int button, boolean wasHandled, Result.Holder resultHolder)
                implements MouseButtonPressed, RecordEvent, HasResult.Record {
            public static final EventBus<Post> BUS = EventBus.create(Post.class);
        }
    }

    /**
     * Fired when a mouse button is released.
     * See the two subclasses for listening before and after the normal handling.
     *
     * @see MouseButtonReleased.Pre
     * @see MouseButtonReleased.Post
     */
    sealed interface MouseButtonReleased extends MouseInput {
        /**
         * {@return the mouse button's input code}
         *
         * @see GLFW mouse constants starting with 'GLFW_MOUSE_BUTTON_'
         * @see <a href="https://www.glfw.org/docs/latest/group__buttons.html" target="_top">the online GLFW documentation</a>
         */
        int button();

        /**
         * Fired <b>before</b> the mouse release is handled by the screen.
         *
         * <p>This event is {@linkplain Cancelable cancellable}, and does not {@linkplain HasResult have a result}.
         * If the event is cancelled, the screen's mouse release handler will be bypassed
         * and the corresponding {@link MouseButtonReleased.Post} will not be fired.</p>
         *
         * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
         * only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
         */
        record Pre(Screen screen, double mouseX, double mouseY, int button)
                implements Cancellable, MouseButtonReleased, RecordEvent {
            public static final CancellableEventBus<Pre> BUS = CancellableEventBus.create(Pre.class);
        }

        /**
         * Fired <b>after</b> the mouse release is handled, if the corresponding {@link MouseButtonReleased.Pre} was
         * not cancelled.
         *
         * <p>This event is not {@linkplain Cancelable cancellable}, {@linkplain HasResult has a result}.</p>
         * <ul>
         *   <li>{@link Result#ALLOW} - forcibly sets the mouse release as handled</li>
         *   <li>{@link Result#DEFAULT} - defaults to the return value of
         *   {@link Screen#mouseReleased(double, double, int)} from the screen (see {@link #wasHandled()}.</li>
         *   <li>{@link Result#DENY} - forcibly sets the mouse release as not handled.</li>
         * </ul>
         *
         * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
         * only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
         *
         * @param wasHandled {@code true} if the mouse release was already handled by its screen
         */
        record Post(Screen screen, double mouseX, double mouseY, int button, boolean wasHandled, Result.Holder resultHolder)
                implements MouseButtonReleased, RecordEvent, HasResult.Record {
            public static final EventBus<Post> BUS = EventBus.create(Post.class);
        }
    }

    /**
     * Fired when the mouse was dragged while a button is being held down.
     * See the two subclasses for listening before and after the normal handling.
     *
     * @see MouseDragged.Pre
     * @see MouseDragged.Post
     */
    sealed interface MouseDragged extends MouseInput {
        /**
         * {@return the mouse button's input code}
         *
         * @see GLFW mouse constants starting with 'GLFW_MOUSE_BUTTON_'
         * @see <a href="https://www.glfw.org/docs/latest/group__buttons.html" target="_top">the online GLFW documentation</a>
         */
        int mouseButton();

        /**
         * {@return amount of mouse drag along the X axis}
         */
        double dragX();

        /**
         * {@return amount of mouse drag along the Y axis}
         */
        double dragY();

        /**
         * Fired <b>before</b> the mouse drag is handled by the screen.
         *
         * <p>This event is {@linkplain Cancelable cancellable}, and does not {@linkplain HasResult have a result}.
         * If the event is cancelled, the screen's mouse drag handler will be bypassed
         * and the corresponding {@link MouseDragged.Post} will not be fired.</p>
         *
         * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
         * only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
         */
        record Pre(Screen screen, double mouseX, double mouseY, int mouseButton, double dragX, double dragY)
                implements Cancellable, MouseDragged, RecordEvent {
            public static final CancellableEventBus<Pre> BUS = CancellableEventBus.create(Pre.class);
        }

        /**
         * Fired <b>after</b> the mouse drag is handled, if not handled by the screen
         * and the corresponding {@link MouseDragged.Pre} is not cancelled.
         *
         * <p>This event is not {@linkplain Cancelable cancellable}, and does not {@linkplain HasResult have a result}.
         * If the event is cancelled, the mouse drag will be set as handled.</p>
         *
         * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
         * only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
         */
        record Post(Screen screen, double mouseX, double mouseY, int mouseButton, double dragX, double dragY)
                implements MouseDragged, RecordEvent {
            public static final EventBus<Post> BUS = EventBus.create(Post.class);
        }
    }

    /**
     * Fired when the mouse was dragged while a button is being held down.
     * See the two subclasses for listening before and after the normal handling.
     *
     * @see MouseScrolled.Pre
     * @see MouseScrolled.Post
     */
    sealed interface MouseScrolled extends MouseInput {
        /**
         * {@return the amount of change / delta of the mouse scroll in the vertical direction}
         */
        double deltaX();

        /**
         * {@return the amount of change / delta of the mouse scroll in the horizontal direction}
         */
        double deltaY();

        /**
         * Fired <b>before</b> the mouse scroll is handled by the screen.
         *
         * <p>This event is {@linkplain Cancelable cancellable}, and does not {@linkplain HasResult have a result}.
         * If the event is cancelled, the screen's mouse scroll handler will be bypassed
         * and the corresponding {@link MouseScrolled.Post} will not be fired.</p>
         *
         * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
         * only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
         */
        record Pre(Screen screen, double mouseX, double mouseY, double deltaX, double deltaY)
                implements Cancellable, MouseScrolled, RecordEvent {
            public static final CancellableEventBus<Pre> BUS = CancellableEventBus.create(Pre.class);
        }

        /**
         * Fired <b>after</b> the mouse scroll is handled, if not handled by the screen
         * and the corresponding {@link MouseScrolled.Pre} is not cancelled.
         *
         * <p>This event is not {@linkplain Cancelable cancellable}, and does not {@linkplain HasResult have a result}.
         * If the event is cancelled, the mouse scroll will be set as handled.</p>
         *
         * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
         * only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
         */
        record Post(Screen screen, double mouseX, double mouseY, double deltaX, double deltaY)
                implements MouseScrolled, RecordEvent {
            public static final EventBus<Post> BUS = EventBus.create(Post.class);
        }
    }

    /**
     * <p>Fired whenever a keyboard key is pressed or released.
     * See the various subclasses to listen for key pressing or releasing.</p>
     *
     * @see KeyPressed
     * @see KeyReleased
     * @see InputConstants
     * @see <a href="https://www.glfw.org/docs/latest/input_guide.html#input_key" target="_top">the online GLFW documentation</a>
     */
    sealed interface KeyInput extends ScreenEvent {
        /**
         * {@return the {@code GLFW} (platform-agnostic) key code}
         *
         * @see InputConstants input constants starting with {@code KEY_}
         * @see GLFW key constants starting with {@code GLFW_KEY_}
         * @see <a href="https://www.glfw.org/docs/latest/group__keys.html" target="_top">the online GLFW documentation</a>
         */
        int keyCode();

        /**
         * {@return the platform-specific scan code}
         * <p>
         * The scan code is unique for every key, regardless of whether it has a key code.
         * Scan codes are platform-specific but consistent over time, so keys will have different scan codes depending
         * on the platform but they are safe to save to disk as custom key bindings.
         *
         * @see InputConstants#getKey(int, int)
         */
        int scanCode();

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
    }

    /**
     * Fired when a keyboard key is pressed.
     * See the two subclasses for listening before and after the normal handling.
     *
     * @see KeyPressed.Pre
     * @see KeyPressed.Post
     */
    sealed interface KeyPressed extends KeyInput {
        /**
         * Fired <b>before</b> the key press is handled by the screen.
         *
         * <p>This event is {@linkplain Cancelable cancellable} and does not {@linkplain HasResult have a result}.
         * If the event is cancelled, the screen's key press handler will be bypassed
         * and the corresponding {@link KeyPressed.Post} will not be fired.</p>
         *
         * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
         * only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
         */
        record Pre(Screen screen, int keyCode, int scanCode, int modifiers)
                implements Cancellable, KeyPressed, RecordEvent {
            public static final CancellableEventBus<Pre> BUS = CancellableEventBus.create(Pre.class);
        }

        /**
         * Fired <b>after</b> the key press is handled, if not handled by the screen
         * and the corresponding {@link KeyPressed.Pre} is not cancelled.
         *
         * <p>This event is {@linkplain Cancelable cancellable}, and does not {@linkplain HasResult have a result}.
         * If the event is cancelled, the key press will be set as handled.</p>
         *
         * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
         * only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
         */
        record Post(Screen screen, int keyCode, int scanCode, int modifiers)
                implements Cancellable, KeyPressed, RecordEvent {
            public static final CancellableEventBus<Post> BUS = CancellableEventBus.create(Post.class);
        }
    }

    /**
     * Fired when a keyboard key is released.
     * See the two subclasses for listening before and after the normal handling.
     *
     * @see KeyReleased.Pre
     * @see KeyReleased.Post
     */
    sealed interface KeyReleased extends KeyInput {
        /**
         * Fired <b>before</b> the key release is handled by the screen.
         *
         * <p>This event is {@linkplain Cancelable cancellable}, and does not {@linkplain HasResult have a result}.
         * If the event is cancelled, the screen's key release handler will be bypassed
         * and the corresponding {@link KeyReleased.Post} will not be fired.</p>
         *
         * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
         * only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
         */
        record Pre(Screen screen, int keyCode, int scanCode, int modifiers)
                implements Cancellable, KeyReleased, RecordEvent {
            public static final CancellableEventBus<Pre> BUS = CancellableEventBus.create(Pre.class);
        }

        /**
         * Fired <b>after</b> the key release is handled, if not handled by the screen
         * and the corresponding {@link KeyReleased.Pre} is not cancelled.
         *
         * <p>This event is {@linkplain Cancelable cancellable}, and does not {@linkplain HasResult have a result}.
         * If the event is cancelled, the key release will be set as handled.</p>
         *
         * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
         * only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
         */
        record Post(Screen screen, int keyCode, int scanCode, int modifiers) implements KeyReleased, RecordEvent {
            public static final EventBus<Post> BUS = EventBus.create(Post.class);
        }
    }

    /**
     * Fired when a keyboard key corresponding to a character is typed.
     * See the two subclasses for listening before and after the normal handling.
     *
     * @see CharacterTyped.Pre
     * @see CharacterTyped.Post
     * @see <a href="https://www.glfw.org/docs/latest/input_guide.html#input_char" target="_top">the online GLFW documentation</a>
     */
    sealed interface CharacterTyped extends ScreenEvent {
        /**
         * {@return the character code point}
         */
        char codePoint();

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
         * Fired <b>before</b> the character input is handled by the screen.
         *
         * <p>This event is {@linkplain Cancelable cancellable}, and does not {@linkplain HasResult have a result}.
         * If the event is cancelled, the screen's character input handler will be bypassed
         * and the corresponding {@link CharacterTyped.Post} will not be fired.</p>
         *
         * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
         * only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
         */
        record Pre(Screen screen, char codePoint, int modifiers) implements Cancellable, CharacterTyped, RecordEvent {
            public static final CancellableEventBus<Pre> BUS = CancellableEventBus.create(Pre.class);
        }

        /**
         * Fired <b>after</b> the character input is handled, if not handled by the screen
         * and the corresponding {@link CharacterTyped.Pre} is not cancelled.
         *
         * <p>This event is {@linkplain Cancelable cancellable}, and does not {@linkplain HasResult have a result}.
         * If the event is cancelled, the character input will be set as handled.</p>
         *
         * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
         * only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
         */
        record Post(Screen screen, char codePoint, int modifiers) implements Cancellable, CharacterTyped, RecordEvent {
            public static final CancellableEventBus<Post> BUS = CancellableEventBus.create(Post.class);
        }
    }

    /**
     * Fired before any {@link Screen} is opened, to allow changing it or preventing it from being opened.
     * All screen layers on the screen are closed before this event is fired.
     *
     * <p>This event is {@linkplain Cancelable cancellable}, and does not {@linkplain HasResult have a result}.
     * If this event is cancelled, then the {@code Screen} shall be prevented from opening and any previous screen
     * will remain open. However, cancelling this event will not prevent the closing of screen layers which happened before
     * this event fired.</p>
     *
     * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
     * only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
     */
    final class Opening extends MutableEvent implements Cancellable, ScreenEvent {
        public static final CancellableEventBus<Opening> BUS = CancellableEventBus.create(Opening.class);

        private final Screen screen;

        private final @Nullable Screen currentScreen;
        private @Nullable Screen newScreen;

        @ApiStatus.Internal
        public Opening(@Nullable Screen currentScreen, Screen screen) {
            this.screen = screen;
            this.currentScreen = currentScreen;
            this.newScreen = screen;
        }

        /**
         * Gets the currently open screen at the time of the event being fired.
         * <p>
         * May be null if no screen was open.
         */
        @Nullable
        public Screen getCurrentScreen() {
            return currentScreen;
        }

        /**
         * @return The screen that will be opened if the event is not cancelled. May be null.
         */
        @Nullable
        public Screen getNewScreen() {
            return newScreen;
        }

        /**
         * Sets the new screen to be opened if the event is not cancelled. May be null.
         */
        public void setNewScreen(@Nullable Screen newScreen) {
            this.newScreen = newScreen;
        }

        @Override
        public Screen screen() {
            return screen;
        }
    }

    /**
     * Fired before a {@link Screen} is closed.
     * All screen layers on the screen are closed before this event is fired.
     *
     * <p>This event is not {@linkplain Cancelable cancellable}, and does not {@linkplain HasResult have a result}.</p>
     *
     * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
     * only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
     */
    record Closing(Screen screen) implements ScreenEvent, RecordEvent {
        public static final EventBus<Closing> BUS = EventBus.create(Closing.class);
    }
}
