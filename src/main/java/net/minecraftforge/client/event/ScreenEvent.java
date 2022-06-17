/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.event;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraft.client.gui.screens.Screen;

import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.LogicalSide;
import org.lwjgl.glfw.GLFW;

/**
 * Fired on different events/actions when a {@link Screen} is active and visible.
 * See the various subclasses for listening to different events.
 *
 * <p>These events are fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
 * only on the {@linkplain LogicalSide#CLIENT logical client}. </p>
 *
 * @see ScreenEvent.InitScreenEvent
 * @see ScreenEvent.DrawScreenEvent
 * @see ScreenEvent.BackgroundDrawnEvent
 * @see ScreenEvent.MouseInputEvent
 * @see ScreenEvent.KeyboardKeyEvent
 */
@OnlyIn(Dist.CLIENT)
public class ScreenEvent extends Event
{
    private final Screen screen;

    /**
     * @hidden
     */
    public ScreenEvent(Screen screen)
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
     * {@link net.minecraft.client.gui.components.Widget} and {@link net.minecraft.client.gui.narration.NarratableEntry}
     * respectively.</p>
     *
     * @see InitScreenEvent.Pre
     * @see InitScreenEvent.Post
     */
    public static class InitScreenEvent extends ScreenEvent
    {
        private final Consumer<GuiEventListener> add;
        private final Consumer<GuiEventListener> remove;

        private final List<GuiEventListener> listenerList;

        /**
         * @hidden
         */
        public InitScreenEvent(Screen screen, List<GuiEventListener> listenerList, Consumer<GuiEventListener> add, Consumer<GuiEventListener> remove)
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
         * will not be cleared. </p>
         *
         * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
         * only on the {@linkplain LogicalSide#CLIENT logical client}. </p>
         */
        @Cancelable
        public static class Pre extends InitScreenEvent
        {
            /**
             * @hidden
             */
            public Pre(Screen screen, List<GuiEventListener> list, Consumer<GuiEventListener> add, Consumer<GuiEventListener> remove)
            {
                super(screen, list, add, remove);
            }
        }

        /**
         * Fired <b>after</b> the screen's overridable initialization method is called.
         *
         * <p>This event is not {@linkplain Cancelable cancellable}, and does not {@linkplain HasResult have a result}. </p>
         *
         * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
         * only on the {@linkplain LogicalSide#CLIENT logical client}. </p>
         */
        public static class Post extends InitScreenEvent
        {
            /**
             * @hidden
             */
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
     * @see DrawScreenEvent.Pre
     * @see DrawScreenEvent.Post
     */
    public static class DrawScreenEvent extends ScreenEvent
    {
        private final PoseStack poseStack;
        private final int mouseX;
        private final int mouseY;
        private final float partialTick;

        /**
         * @hidden
         * @see net.minecraftforge.client.ForgeHooksClient#drawScreen(Screen, PoseStack, int, int, float)
         */
        public DrawScreenEvent(Screen screen, PoseStack poseStack, int mouseX, int mouseY, float partialTick)
        {
            super(screen);
            this.poseStack = poseStack;
            this.mouseX = mouseX;
            this.mouseY = mouseY;
            this.partialTick = partialTick;
        }

        /**
         * {@return the pose stack used for rendering}
         */
        public PoseStack getPoseStack()
        {
            return poseStack;
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
         * If the event is cancelled, the screen will not be drawn. </p>
         *
         * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
         * only on the {@linkplain LogicalSide#CLIENT logical client}. </p>
         */
        @Cancelable
        public static class Pre extends DrawScreenEvent
        {
            /**
             * @hidden
             */
            public Pre(Screen screen, PoseStack poseStack, int mouseX, int mouseY, float partialTick)
            {
                super(screen, poseStack, mouseX, mouseY, partialTick);
            }
        }

        /**
         * Fired <b>after</b> the screen is drawn.
         *
         * <p>This event is not {@linkplain Cancelable cancellable}, and does not {@linkplain HasResult have a result}. </p>
         *
         * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
         * only on the {@linkplain LogicalSide#CLIENT logical client}. </p>
         */
        public static class Post extends DrawScreenEvent
        {
            /**
             * @hidden
             */
            public Post(Screen screen, PoseStack poseStack, int mouseX, int mouseY, float partialTick)
            {
                super(screen, poseStack, mouseX, mouseY, partialTick);
            }
        }
    }

    /**
     * Fired directly after the background of the screen is drawn.
     * Can be used for drawing above the background but below the tooltips.
     *
     * <p>This event is not {@linkplain Cancelable cancellable}, and does not {@linkplain HasResult have a result}. </p>
     *
     * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
     * only on the {@linkplain LogicalSide#CLIENT logical client}. </p>
     */
    public static class BackgroundDrawnEvent extends ScreenEvent
    {
        private final PoseStack poseStack;

        /**
         * @hidden
         */
        public BackgroundDrawnEvent(Screen screen, PoseStack poseStack)
        {
            super(screen);
            this.poseStack = poseStack;
        }

        /**
         * {@return the pose stack used for rendering}
         */
        public PoseStack getPoseStack()
        {
            return poseStack;
        }
    }

    /**
     * Fired to determine whether to render the potion indicators in the {@link EffectRenderingInventoryScreen inventory
     * screen} in compact or classic mode.
     *
     * <p>This event is not {@linkplain Cancelable cancellable} and {@linkplain HasResult has a result}. </p>
     * <ul>
     *   <li>{@link Result#ALLOW} - forcibly renders the potion indicators in <em>compact</em> mode.</li>
     *   <li>{@link Result#DEFAULT} - defaults to vanilla behavior to using compact mode if the the screen width is too
     *   small for classic rendering of potion indicators.</li>
     *   <li>{@link Result#DENY} - forcibly renders the potion indicators in <em>classic</em> mode.</li>
     * </ul>
     *
     * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
     * only on the {@linkplain LogicalSide#CLIENT logical client}. </p>
     */
    @HasResult
    public static class PotionSizeEvent extends ScreenEvent
    {
        /**
         * @hidden For internal use only.
         */
        public PotionSizeEvent(Screen screen)
        {
            super(screen);
        }
    }

    /**
     * Fired whenever an action is performed by the mouse.
     * See the various subclasses to listen for different actions.
     *
     * @see ScreenEvent.MouseClickedEvent
     * @see ScreenEvent.MouseReleasedEvent
     * @see ScreenEvent.MouseDragEvent
     * @see ScreenEvent.MouseScrollEvent
     */
    public static abstract class MouseInputEvent extends ScreenEvent
    {
        private final double mouseX;
        private final double mouseY;

        /**
         * @hidden
         */
        public MouseInputEvent(Screen screen, double mouseX, double mouseY)
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
     * Fired when a mouse button is clicked.
     * See the two subclasses for listening before and after the normal handling.
     *
     * @see MouseClickedEvent.Pre
     * @see MouseClickedEvent.Post
     */
    public static abstract class MouseClickedEvent extends MouseInputEvent
    {
        private final int button;

        /**
         * @hidden
         */
        public MouseClickedEvent(Screen screen, double mouseX, double mouseY, int button)
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
         * and the corresponding {@link MouseClickedEvent.Post} will not be fired. </p>
         *
         * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
         * only on the {@linkplain LogicalSide#CLIENT logical client}. </p>
         */
        @Cancelable
        public static class Pre extends MouseClickedEvent
        {
            /**
             * @hidden
             * @see ForgeHooksClient#onScreenMouseClickedPre(Screen, double, double, int)
             */
            public Pre(Screen screen, double mouseX, double mouseY, int button)
            {
                super(screen, mouseX, mouseY, button);
            }
        }

        /**
         * Fired <b>after</b> the mouse click is handled, if the corresponding {@link MouseClickedEvent.Pre} was not
         * cancelled.
         *
         * <p>This event is not {@linkplain Cancelable cancellable}, {@linkplain HasResult has a result}. </p>
         * <ul>
         *   <li>{@link Result#ALLOW} - forcibly sets the mouse click as handled</li>
         *   <li>{@link Result#DEFAULT} - defaults to the return value of
         *   {@link Screen#mouseClicked(double, double, int)} from the screen (see {@link #wasHandled()}.</li>
         *   <li>{@link Result#DENY} - forcibly sets the mouse click as not handled.</li>
         * </ul>
         *
         * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
         * only on the {@linkplain LogicalSide#CLIENT logical client}. </p>
         */
        @HasResult
        public static class Post extends MouseClickedEvent
        {
            private final boolean handled;

            /**
             * @hidden
             * @see ForgeHooksClient#onScreenMouseClickedPost(Screen, double, double, int, boolean)
             */
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
     * @see MouseReleasedEvent.Pre
     * @see MouseReleasedEvent.Post
     */
    public static abstract class MouseReleasedEvent extends MouseInputEvent
    {
        private final int button;

        /**
         * @hidden
         */
        public MouseReleasedEvent(Screen screen, double mouseX, double mouseY, int button)
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
         * and the corresponding {@link MouseReleasedEvent.Post} will not be fired. </p>
         *
         * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
         * only on the {@linkplain LogicalSide#CLIENT logical client}. </p>
         */
        @Cancelable
        public static class Pre extends MouseReleasedEvent
        {
            /**
             * @hidden
             * @see ForgeHooksClient#onScreenMouseReleasedPre(Screen, double, double, int)
             */
            public Pre(Screen screen, double mouseX, double mouseY, int button)
            {
                super(screen, mouseX, mouseY, button);
            }
        }

        /**
         * Fired <b>after</b> the mouse release is handled, if the corresponding {@link MouseReleasedEvent.Pre} was
         * not cancelled.
         *
         * <p>This event is not {@linkplain Cancelable cancellable}, {@linkplain HasResult has a result}. </p>
         * <ul>
         *   <li>{@link Result#ALLOW} - forcibly sets the mouse release as handled</li>
         *   <li>{@link Result#DEFAULT} - defaults to the return value of
         *   {@link Screen#mouseReleased(double, double, int)} from the screen (see {@link #wasHandled()}.</li>
         *   <li>{@link Result#DENY} - forcibly sets the mouse release as not handled.</li>
         * </ul>
         *
         * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
         * only on the {@linkplain LogicalSide#CLIENT logical client}. </p>
         */
        @HasResult
        public static class Post extends MouseReleasedEvent
        {
            private final boolean handled;

            /**
             * @hidden
             * @see ForgeHooksClient#onScreenMouseReleasedPost(Screen, double, double, int, boolean)
             */
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
     * @see MouseDragEvent.Pre
     * @see MouseDragEvent.Post
     */
    public static abstract class MouseDragEvent extends MouseInputEvent
    {
        private final int mouseButton;
        private final double dragX;
        private final double dragY;

        /**
         * @hidden
         */
        public MouseDragEvent(Screen screen, double mouseX, double mouseY, int mouseButton, double dragX, double dragY)
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
         * and the corresponding {@link MouseDragEvent.Post} will not be fired. </p>
         *
         * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
         * only on the {@linkplain LogicalSide#CLIENT logical client}. </p>
         */
        @Cancelable
        public static class Pre extends MouseDragEvent
        {
            /**
             * @hidden
             * @see ForgeHooksClient#onScreenMouseDragPre(Screen, double, double, int, double, double)
             */
            public Pre(Screen screen, double mouseX, double mouseY, int mouseButton, double dragX, double dragY)
            {
                super(screen, mouseX, mouseY, mouseButton, dragX, dragY);
            }
        }

        /**
         * Fired <b>after</b> the mouse drag is handled, if not handled by the screen
         * and the corresponding {@link MouseDragEvent.Pre} is not cancelled.
         *
         * <p>This event is {@linkplain Cancelable cancellable}, and does not {@linkplain HasResult have a result}.
         * If the event is cancelled, the mouse drag will be set as handled. </p>
         *
         * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
         * only on the {@linkplain LogicalSide#CLIENT logical client}. </p>
         */
        @Cancelable
        public static class Post extends MouseDragEvent
        {
            /**
             * @hidden
             * @see ForgeHooksClient#onScreenMouseDragPost(Screen, double, double, int, double, double)
             */
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
     * @see MouseScrollEvent.Pre
     * @see MouseScrollEvent.Post
     */
    public static abstract class MouseScrollEvent extends MouseInputEvent
    {
        private final double scrollDelta;

        /**
         * @hidden
         */
        public MouseScrollEvent(Screen screen, double mouseX, double mouseY, double scrollDelta)
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
         * and the corresponding {@link MouseScrollEvent.Post} will not be fired. </p>
         *
         * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
         * only on the {@linkplain LogicalSide#CLIENT logical client}. </p>
         */
        @Cancelable
        public static class Pre extends MouseScrollEvent
        {
            /**
             * @hidden
             * @see ForgeHooksClient#onScreenMouseScrollPre(MouseHandler, Screen, double)
             */
            public Pre(Screen screen, double mouseX, double mouseY, double scrollDelta)
            {
                super(screen, mouseX, mouseY, scrollDelta);
            }
        }

        /**
         * Fired <b>after</b> the mouse scroll is handled, if not handled by the screen
         * and the corresponding {@link MouseScrollEvent.Pre} is not cancelled.
         *
         * <p>This event is {@linkplain Cancelable cancellable}, and does not {@linkplain HasResult have a result}.
         * If the event is cancelled, the mouse scroll will be set as handled. </p>
         *
         * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
         * only on the {@linkplain LogicalSide#CLIENT logical client}. </p>
         */
        @Cancelable
        public static class Post extends MouseScrollEvent
        {
            /**
             * @hidden
             * @see ForgeHooksClient#onScreenMouseScrollPost(MouseHandler, Screen, double)
             */
            public Post(Screen screen, double mouseX, double mouseY, double scrollDelta)
            {
                super(screen, mouseX, mouseY, scrollDelta);
            }
        }
    }

    /**
     * <p>Fired whenever a keyboard key is pressed or released.
     * See the various subclasses to listen for key pressing or releasing. </p>
     *
     * @see ScreenEvent.KeyboardKeyPressedEvent
     * @see ScreenEvent.KeyboardKeyReleasedEvent
     * @see InputConstants
     * @see <a href="https://www.glfw.org/docs/latest/input_guide.html#input_key" target="_top">the online GLFW documentation</a>
     */
    public static abstract class KeyboardKeyEvent extends ScreenEvent
    {
        private final int keyCode;
        private final int scanCode;
        private final int modifiers;

        /**
         * @hidden
         */
        public KeyboardKeyEvent(Screen screen, int keyCode, int scanCode, int modifiers)
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
         *
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
     * @see KeyboardKeyPressedEvent.Pre
     * @see KeyboardKeyPressedEvent.Post
     */
    public static abstract class KeyboardKeyPressedEvent extends KeyboardKeyEvent
    {
        /**
         * @hidden
         */
        public KeyboardKeyPressedEvent(Screen screen, int keyCode, int scanCode, int modifiers)
        {
            super(screen,  keyCode, scanCode, modifiers);
        }

        /**
         * Fired <b>before</b> the key press is handled by the screen.
         *
         * <p>This event is {@linkplain Cancelable cancellable} and does not {@linkplain HasResult have a result}.
         * If the event is cancelled, the screen's key press handler will be bypassed
         * and the corresponding {@link KeyboardKeyPressedEvent.Post} will not be fired. </p>
         *
         * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
         * only on the {@linkplain LogicalSide#CLIENT logical client}. </p>
         */
        @Cancelable
        public static class Pre extends KeyboardKeyPressedEvent
        {
            /**
             * @hidden
             * @see ForgeHooksClient#onScreenKeyPressedPre(Screen, int, int, int)
             */
            public Pre(Screen screen, int keyCode, int scanCode, int modifiers)
            {
                super(screen, keyCode, scanCode, modifiers);
            }
        }

        /**
         * Fired <b>after</b> the key press is handled, if not handled by the screen
         * and the corresponding {@link KeyboardKeyPressedEvent.Pre} is not cancelled.
         *
         * <p>This event is {@linkplain Cancelable cancellable}, and does not {@linkplain HasResult have a result}.
         * If the event is cancelled, the key press will be set as handled. </p>
         *
         * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
         * only on the {@linkplain LogicalSide#CLIENT logical client}. </p>
         */
        @Cancelable
        public static class Post extends KeyboardKeyPressedEvent
        {
            /**
             * @hidden
             * @see ForgeHooksClient#onScreenKeyPressedPost(Screen, int, int, int)
             */
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
     * @see KeyboardKeyReleasedEvent.Pre
     * @see KeyboardKeyReleasedEvent.Post
     */
    public static abstract class KeyboardKeyReleasedEvent extends KeyboardKeyEvent
    {
        /**
         * @hidden
         */
        public KeyboardKeyReleasedEvent(Screen screen, int keyCode, int scanCode, int modifiers)
        {
            super(screen, keyCode, scanCode, modifiers);
        }

        /**
         * Fired <b>before</b> the key release is handled by the screen.
         *
         * <p>This event is {@linkplain Cancelable cancellable}, and does not {@linkplain HasResult have a result}.
         * If the event is cancelled, the screen's key release handler will be bypassed
         * and the corresponding {@link KeyboardKeyReleasedEvent.Post} will not be fired. </p>
         *
         * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
         * only on the {@linkplain LogicalSide#CLIENT logical client}. </p>
         */
        @Cancelable
        public static class Pre extends KeyboardKeyReleasedEvent
        {
            /**
             * @hidden
             * @see ForgeHooksClient#onScreenKeyReleasedPre(Screen, int, int, int)
             */
            public Pre(Screen screen, int keyCode, int scanCode, int modifiers)
            {
                super(screen, keyCode, scanCode, modifiers);
            }
        }

        /**
         * Fired <b>after</b> the key release is handled, if not handled by the screen
         * and the corresponding {@link KeyboardKeyReleasedEvent.Pre} is not cancelled.
         *
         * <p>This event is {@linkplain Cancelable cancellable}, and does not {@linkplain HasResult have a result}.
         * If the event is cancelled, the key release will be set as handled. </p>
         *
         * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
         * only on the {@linkplain LogicalSide#CLIENT logical client}. </p>
         */
        @Cancelable
        public static class Post extends KeyboardKeyReleasedEvent
        {
            /**
             * @hidden
             * @see ForgeHooksClient#onScreenKeyReleasedPost(Screen, int, int, int)
             */
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
     * @see KeyboardCharTypedEvent.Pre
     * @see KeyboardCharTypedEvent.Post
     * @see <a href="https://www.glfw.org/docs/latest/input_guide.html#input_char" target="_top">the online GLFW documentation</a>
     */
    public static class KeyboardCharTypedEvent extends ScreenEvent
    {
        private final char codePoint;
        private final int modifiers;

        /**
         * @hidden
         */
        public KeyboardCharTypedEvent(Screen screen, char codePoint, int modifiers)
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
         * and the corresponding {@link KeyboardCharTypedEvent.Post} will not be fired. </p>
         *
         * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
         * only on the {@linkplain LogicalSide#CLIENT logical client}. </p>
         */
        @Cancelable
        public static class Pre extends KeyboardCharTypedEvent
        {
            /**
             * @hidden
             * @see ForgeHooksClient#onScreenCharTypedPre(Screen, char, int)
             */
            public Pre(Screen screen, char codePoint, int modifiers)
            {
                super(screen, codePoint, modifiers);
            }
        }

        /**
         * Fired <b>after</b> the character input is handled, if not handled by the screen
         * and the corresponding {@link KeyboardCharTypedEvent.Pre} is not cancelled.
         *
         * <p>This event is {@linkplain Cancelable cancellable}, and does not {@linkplain HasResult have a result}.
         * If the event is cancelled, the character input will be set as handled. </p>
         *
         * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
         * only on the {@linkplain LogicalSide#CLIENT logical client}. </p>
         */
        @Cancelable
        public static class Post extends KeyboardCharTypedEvent
        {
            /**
             * @hidden
             * @see ForgeHooksClient#onScreenCharTypedPost(Screen, char, int)
             */
            public Post(Screen screen, char codePoint, int modifiers)
            {
                super(screen, codePoint, modifiers);
            }
        }
    }
}
