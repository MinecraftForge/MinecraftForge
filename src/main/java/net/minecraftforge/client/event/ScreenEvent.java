/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.event;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.LogicalSide;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
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
@OnlyIn(Dist.CLIENT)
public abstract class ScreenEvent extends Event
{
    private final Screen screen;

    @ApiStatus.Internal
    protected ScreenEvent(Screen screen)
    {
        this.screen = Objects.requireNonNull(screen);
    }

    /**
     * {@return the screen that caused this event}
     */
    public Screen getScreen()
    {
        return screen;
    }

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
    public static abstract class Init extends ScreenEvent
    {
        private final Consumer<GuiEventListener> add;
        private final Consumer<GuiEventListener> remove;

        private final List<GuiEventListener> listenerList;

        @ApiStatus.Internal
        protected Init(Screen screen, List<GuiEventListener> listenerList, Consumer<GuiEventListener> add, Consumer<GuiEventListener> remove)
        {
            super(screen);
            this.listenerList = Collections.unmodifiableList(listenerList);
            this.add = add;
            this.remove = remove;
        }

        /**
         * {@return unmodifiable view of list of event listeners on the screen}
         */
        public List<GuiEventListener> getListenersList()
        {
            return listenerList;
        }

        /**
         * Adds the given {@link GuiEventListener} to the screen.
         *
         * @param listener the listener to add
         */
        public void addListener(GuiEventListener listener)
        {
            add.accept(listener);
        }

        /**
         * Removes the given {@link GuiEventListener} from the screen.
         *
         * @param listener the listener to remove
         */
        public void removeListener(GuiEventListener listener)
        {
            remove.accept(listener);
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
        @Cancelable
        public static class Pre extends Init
        {
            @ApiStatus.Internal
            public Pre(Screen screen, List<GuiEventListener> list, Consumer<GuiEventListener> add, Consumer<GuiEventListener> remove)
            {
                super(screen, list, add, remove);
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
        public static class Post extends Init
        {
            @ApiStatus.Internal
            public Post(Screen screen, List<GuiEventListener> list, Consumer<GuiEventListener> add, Consumer<GuiEventListener> remove)
            {
                super(screen, list, add, remove);
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
    public static abstract class Render extends ScreenEvent
    {
        private final GuiGraphics guiGraphics;
        private final int mouseX;
        private final int mouseY;
        private final float partialTick;

        @ApiStatus.Internal
        protected Render(Screen screen, GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick)
        {
            super(screen);
            this.guiGraphics = guiGraphics;
            this.mouseX = mouseX;
            this.mouseY = mouseY;
            this.partialTick = partialTick;
        }

        /**
         * {@return the gui graphics used for rendering}
         */
        public GuiGraphics getGuiGraphics()
        {
            return guiGraphics;
        }

        /**
         * {@return the X coordinate of the mouse pointer}
         */
        public int getMouseX()
        {
            return mouseX;
        }

        /**
         * {@return the Y coordinate of the mouse pointer}
         */
        public int getMouseY()
        {
            return mouseY;
        }

        /**
         * {@return the partial tick}
         */
        public float getPartialTick()
        {
            return partialTick;
        }

        /**
         * Fired <b>before</b> the screen is drawn.
         *
         * <p>This event is {@linkplain Cancelable cancellable}, and does not {@linkplain HasResult have a result}.
         * If the event is cancelled, the screen will not be drawn.</p>
         *
         * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
         * only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
         */
        @Cancelable
        public static class Pre extends Render
        {
            @ApiStatus.Internal
            public Pre(Screen screen, GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick)
            {
                super(screen, guiGraphics, mouseX, mouseY, partialTick);
            }
        }

        /**
         * Fired <b>after</b> the screen is drawn.
         *
         * <p>This event is not {@linkplain Cancelable cancellable}, and does not {@linkplain HasResult have a result}.</p>
         *
         * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
         * only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
         */
        public static class Post extends Render
        {
            @ApiStatus.Internal
            public Post(Screen screen, GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick)
            {
                super(screen, guiGraphics, mouseX, mouseY, partialTick);
            }
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
     */
    public static class BackgroundRendered extends ScreenEvent
    {
        private final GuiGraphics guiGraphics;

        @ApiStatus.Internal
        public BackgroundRendered(Screen screen, GuiGraphics guiGraphics)
        {
            super(screen);
            this.guiGraphics = guiGraphics;
        }

        /**
         * {@return the gui graphics used for rendering}
         */
        public GuiGraphics getGuiGraphics()
        {
            return guiGraphics;
        }
    }

    /**
     * Fired ahead of rendering any active mob effects in the {@link EffectRenderingInventoryScreen inventory screen}.
     * Can be used to select the size of the effects display (full or compact) or even hide or replace vanilla's rendering entirely.
     * This event can also be used to modify the horizontal position of the stack of effects being rendered.
     *
     * <p>This event is {@linkplain Cancelable cancellable} and does not {@linkplain HasResult have a result}.
     * Cancelling this event will prevent vanilla rendering.</p>
     *
     * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
     * only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
     */
    @Cancelable
    public static class RenderInventoryMobEffects extends ScreenEvent
    {
        private final int availableSpace;
        private boolean compact;
        private int horizontalOffset;

        @ApiStatus.Internal
        public RenderInventoryMobEffects(Screen screen, int availableSpace, boolean compact, int horizontalOffset)
        {
            super(screen);
            this.availableSpace = availableSpace;
            this.compact = compact;
            this.horizontalOffset = horizontalOffset;
        }

        /**
         * The available space to the right of the inventory.
         */
        public int getAvailableSpace()
        {
            return availableSpace;
        }

        /**
         * Whether the effects should be rendered in compact mode (only icons, no text), or the default full size.
         */
        public boolean isCompact()
        {
            return compact;
        }

        /**
         * The distance from the left side of the screen that the effect stack is rendered. Positive values shift this more to the right.
         */
        public int getHorizontalOffset()
        {
            return horizontalOffset;
        }

        /**
         * Replaces the horizontal offset of the effect stack
         */
        public void setHorizontalOffset(int offset)
        {
            horizontalOffset = offset;
        }

        /**
         * Adds to the horizontal offset of the effect stack. Negative values are acceptable.
         */
        public void addHorizontalOffset(int offset)
        {
            horizontalOffset += offset;
        }

        /**
         * Sets whether the effects should be rendered in compact mode (only icons, no text), or the default full size.
         */
        public void setCompact(boolean compact)
        {
            this.compact = compact;
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
    private static abstract class MouseInput extends ScreenEvent
    {
        private final double mouseX;
        private final double mouseY;

        @ApiStatus.Internal
        protected MouseInput(Screen screen, double mouseX, double mouseY)
        {
            super(screen);
            this.mouseX = mouseX;
            this.mouseY = mouseY;
        }

        /**
         * {@return the X position of the mouse cursor, relative to the screen}
         */
        public double getMouseX()
        {
            return mouseX;
        }

        /**
         * {@return the Y position of the mouse cursor, relative to the screen}
         */
        public double getMouseY()
        {
            return mouseY;
        }
    }

    /**
     * Fired when a mouse button is pressed.
     * See the two subclasses for listening before and after the normal handling.
     *
     * @see MouseButtonPressed.Pre
     * @see MouseButtonPressed.Post
     */
    public static abstract class MouseButtonPressed extends MouseInput
    {
        private final int button;

        @ApiStatus.Internal
        public MouseButtonPressed(Screen screen, double mouseX, double mouseY, int button)
        {
            super(screen, mouseX, mouseY);
            this.button = button;
        }

        /**
         * {@return the mouse button's input code}
         *
         * @see GLFW mouse constants starting with 'GLFW_MOUSE_BUTTON_'
         * @see <a href="https://www.glfw.org/docs/latest/group__buttons.html" target="_top">the online GLFW documentation</a>
         */
        public int getButton()
        {
            return button;
        }

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
        @Cancelable
        public static class Pre extends MouseButtonPressed
        {
            @ApiStatus.Internal
            public Pre(Screen screen, double mouseX, double mouseY, int button)
            {
                super(screen, mouseX, mouseY, button);
            }
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
         */
        @HasResult
        public static class Post extends MouseButtonPressed
        {
            private final boolean handled;

            @ApiStatus.Internal
            public Post(Screen screen, double mouseX, double mouseY, int button, boolean handled)
            {
                super(screen, mouseX, mouseY, button);
                this.handled = handled;
            }

            /**
             * {@return {@code true} if the mouse click was already handled by its screen}
             */
            public boolean wasHandled()
            {
                return handled;
            }
        }
    }

    /**
     * Fired when a mouse button is released.
     * See the two subclasses for listening before and after the normal handling.
     *
     * @see MouseButtonReleased.Pre
     * @see MouseButtonReleased.Post
     */
    public static abstract class MouseButtonReleased extends MouseInput
    {
        private final int button;

        @ApiStatus.Internal
        public MouseButtonReleased(Screen screen, double mouseX, double mouseY, int button)
        {
            super(screen, mouseX, mouseY);
            this.button = button;
        }

        /**
         * {@return the mouse button's input code}
         *
         * @see GLFW mouse constants starting with 'GLFW_MOUSE_BUTTON_'
         * @see <a href="https://www.glfw.org/docs/latest/group__buttons.html" target="_top">the online GLFW documentation</a>
         */
        public int getButton()
        {
            return button;
        }

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
        @Cancelable
        public static class Pre extends MouseButtonReleased
        {
            @ApiStatus.Internal
            public Pre(Screen screen, double mouseX, double mouseY, int button)
            {
                super(screen, mouseX, mouseY, button);
            }
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
         */
        @HasResult
        public static class Post extends MouseButtonReleased
        {
            private final boolean handled;

            @ApiStatus.Internal
            public Post(Screen screen, double mouseX, double mouseY, int button, boolean handled)
            {
                super(screen, mouseX, mouseY, button);
                this.handled = handled;
            }

            /**
             * @return {@code true} if the mouse release was already handled by its screen
             */
            public boolean wasHandled()
            {
                return handled;
            }
        }
    }

    /**
     * Fired when the mouse was dragged while a button is being held down.
     * See the two subclasses for listening before and after the normal handling.
     *
     * @see MouseDragged.Pre
     * @see MouseDragged.Post
     */
    public static abstract class MouseDragged extends MouseInput
    {
        private final int mouseButton;
        private final double dragX;
        private final double dragY;

        @ApiStatus.Internal
        public MouseDragged(Screen screen, double mouseX, double mouseY, int mouseButton, double dragX, double dragY)
        {
            super(screen, mouseX, mouseY);
            this.mouseButton = mouseButton;
            this.dragX = dragX;
            this.dragY = dragY;
        }

        /**
         * {@return the mouse button's input code}
         *
         * @see GLFW mouse constants starting with 'GLFW_MOUSE_BUTTON_'
         * @see <a href="https://www.glfw.org/docs/latest/group__buttons.html" target="_top">the online GLFW documentation</a>
         */
        public int getMouseButton()
        {
            return mouseButton;
        }

        /**
         * {@return amount of mouse drag along the X axis}
         */
        public double getDragX()
        {
            return dragX;
        }

        /**
         * {@return amount of mouse drag along the Y axis}
         */
        public double getDragY()
        {
            return dragY;
        }

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
        @Cancelable
        public static class Pre extends MouseDragged
        {
            @ApiStatus.Internal
            public Pre(Screen screen, double mouseX, double mouseY, int mouseButton, double dragX, double dragY)
            {
                super(screen, mouseX, mouseY, mouseButton, dragX, dragY);
            }
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
        public static class Post extends MouseDragged
        {
            @ApiStatus.Internal
            public Post(Screen screen, double mouseX, double mouseY, int mouseButton, double dragX, double dragY)
            {
                super(screen, mouseX, mouseY, mouseButton, dragX, dragY);
            }
        }
    }

    /**
     * Fired when the mouse was dragged while a button is being held down.
     * See the two subclasses for listening before and after the normal handling.
     *
     * @see MouseScrolled.Pre
     * @see MouseScrolled.Post
     */
    public static abstract class MouseScrolled extends MouseInput
    {
        private final double scrollDelta;

        @ApiStatus.Internal
        public MouseScrolled(Screen screen, double mouseX, double mouseY, double scrollDelta)
        {
            super(screen, mouseX, mouseY);
            this.scrollDelta = scrollDelta;
        }

        /**
         * {@return the amount of change / delta of the mouse scroll}
         */
        public double getScrollDelta()
        {
            return scrollDelta;
        }

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
        @Cancelable
        public static class Pre extends MouseScrolled
        {
            @ApiStatus.Internal
            public Pre(Screen screen, double mouseX, double mouseY, double scrollDelta)
            {
                super(screen, mouseX, mouseY, scrollDelta);
            }
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
        public static class Post extends MouseScrolled
        {
            @ApiStatus.Internal
            public Post(Screen screen, double mouseX, double mouseY, double scrollDelta)
            {
                super(screen, mouseX, mouseY, scrollDelta);
            }
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
    private static abstract class KeyInput extends ScreenEvent
    {
        private final int keyCode;
        private final int scanCode;
        private final int modifiers;

        @ApiStatus.Internal
        protected KeyInput(Screen screen, int keyCode, int scanCode, int modifiers)
        {
            super(screen);
            this.keyCode = keyCode;
            this.scanCode = scanCode;
            this.modifiers = modifiers;
        }

        /**
         * {@return the {@code GLFW} (platform-agnostic) key code}
         *
         * @see InputConstants input constants starting with {@code KEY_}
         * @see GLFW key constants starting with {@code GLFW_KEY_}
         * @see <a href="https://www.glfw.org/docs/latest/group__keys.html" target="_top">the online GLFW documentation</a>
         */
        public int getKeyCode()
        {
            return keyCode;
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
            return scanCode;
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
            return modifiers;
        }
    }

    /**
     * Fired when a keyboard key is pressed.
     * See the two subclasses for listening before and after the normal handling.
     *
     * @see KeyPressed.Pre
     * @see KeyPressed.Post
     */
    public static abstract class KeyPressed extends KeyInput
    {
        @ApiStatus.Internal
        public KeyPressed(Screen screen, int keyCode, int scanCode, int modifiers)
        {
            super(screen, keyCode, scanCode, modifiers);
        }

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
        @Cancelable
        public static class Pre extends KeyPressed
        {
            @ApiStatus.Internal
            public Pre(Screen screen, int keyCode, int scanCode, int modifiers)
            {
                super(screen, keyCode, scanCode, modifiers);
            }
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
        @Cancelable
        public static class Post extends KeyPressed
        {
            @ApiStatus.Internal
            public Post(Screen screen, int keyCode, int scanCode, int modifiers)
            {
                super(screen, keyCode, scanCode, modifiers);
            }
        }
    }

    /**
     * Fired when a keyboard key is released.
     * See the two subclasses for listening before and after the normal handling.
     *
     * @see KeyReleased.Pre
     * @see KeyReleased.Post
     */
    public static abstract class KeyReleased extends KeyInput
    {
        @ApiStatus.Internal
        public KeyReleased(Screen screen, int keyCode, int scanCode, int modifiers)
        {
            super(screen, keyCode, scanCode, modifiers);
        }

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
        @Cancelable
        public static class Pre extends KeyReleased
        {
            @ApiStatus.Internal
            public Pre(Screen screen, int keyCode, int scanCode, int modifiers)
            {
                super(screen, keyCode, scanCode, modifiers);
            }
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
        @Cancelable
        public static class Post extends KeyReleased
        {
            @ApiStatus.Internal
            public Post(Screen screen, int keyCode, int scanCode, int modifiers)
            {
                super(screen, keyCode, scanCode, modifiers);
            }
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
    public static class CharacterTyped extends ScreenEvent
    {
        private final char codePoint;
        private final int modifiers;

        @ApiStatus.Internal
        public CharacterTyped(Screen screen, char codePoint, int modifiers)
        {
            super(screen);
            this.codePoint = codePoint;
            this.modifiers = modifiers;
        }

        /**
         * {@return the character code point}
         */
        public char getCodePoint()
        {
            return codePoint;
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
            return modifiers;
        }

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
        @Cancelable
        public static class Pre extends CharacterTyped
        {
            @ApiStatus.Internal
            public Pre(Screen screen, char codePoint, int modifiers)
            {
                super(screen, codePoint, modifiers);
            }
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
        public static class Post extends CharacterTyped
        {
            @ApiStatus.Internal
            public Post(Screen screen, char codePoint, int modifiers)
            {
                super(screen, codePoint, modifiers);
            }
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
    @Cancelable
    public static class Opening extends ScreenEvent
    {
        @Nullable
        private final Screen currentScreen;
        private Screen newScreen;

        @ApiStatus.Internal
        public Opening(@Nullable Screen currentScreen, Screen screen)
        {
            super(screen);
            this.currentScreen = currentScreen;
            this.newScreen = screen;
        }

        /**
         * Gets the currently open screen at the time of the event being fired.
         * <p>
         * May be null if no screen was open.
         */
        @Nullable
        public Screen getCurrentScreen()
        {
            return currentScreen;
        }

        /**
         * @return The screen that will be opened if the event is not cancelled. May be null.
         */
        @Nullable
        public Screen getNewScreen()
        {
            return newScreen;
        }

        /**
         * Sets the new screen to be opened if the event is not cancelled. May be null.
         */
        public void setNewScreen(Screen newScreen)
        {
            this.newScreen = newScreen;
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
    public static class Closing extends ScreenEvent
    {
        @ApiStatus.Internal
        public Closing(Screen screen)
        {
            super(screen);
        }
    }
}
