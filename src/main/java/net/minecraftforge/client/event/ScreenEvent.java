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

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
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
 * @see ScreenEvent.InitEvent
 * @see ScreenEvent.DrawScreenEvent
 * @see ScreenEvent.BackgroundDrawnEvent
 * @see ScreenEvent.PotionShiftEvent
 * @see ScreenEvent.MouseInputEvent
 * @see ScreenEvent.KeyboardKeyEvent
 * @author bspkrs
 */
@OnlyIn(Dist.CLIENT)
public class ScreenEvent extends Event
{
    private final Screen screen;

    public ScreenEvent(Screen screen)
    {
        this.screen = screen;
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
     * @see InitEvent.Pre
     * @see InitEvent.Post
     */
    public static class InitEvent extends ScreenEvent
    {
        private final List<GuiEventListener> list;
        private final Consumer<GuiEventListener> add;
        private final Consumer<GuiEventListener> remove;

        public InitEvent(Screen gui, List<GuiEventListener> list, Consumer<GuiEventListener> add, Consumer<GuiEventListener> remove)
        {
            super(gui);
            this.list = Collections.unmodifiableList(list);
            this.add = add;
            this.remove = remove;
        }

        // TODO: should these still be called widgets? or expanded to 'listeners'
        /**
         * {@return unmodifiable view of list of widgets on the screen}
         */
        public List<GuiEventListener> getWidgetList()
        {
            return list;
        }

        /**
         * Adds the given {@code GuiEventListener} to the screen.
         *
         * @param button the widget to add
         */
        public void addWidget(GuiEventListener button)
        {
            add.accept(button);
        }

        /**
         * Removes the given {@code GuiEventListener} from the screen.
         *
         * @param button the widget to remove
         */
        public void removeWidget(GuiEventListener button)
        {
            remove.accept(button);
        }

        /**
         * Fired <b>before</b> the screen's overridable initialization method is fired.
         *
         * <p>This event is {@linkplain Cancelable cancelable}, and does not {@linkplain HasResult have a result}.
         * If the event is cancelled, the initialization method will not be called, and the widgets and children lists
         * will not be cleared. </p>
         *
         * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
         * only on the {@linkplain LogicalSide#CLIENT logical client}. </p>
         */
        @Cancelable
        public static class Pre extends InitEvent
        {
            public Pre(Screen screen, List<GuiEventListener> list, Consumer<GuiEventListener> add, Consumer<GuiEventListener> remove)
            {
                super(screen, list, add, remove);
            }
        }

        /**
         * Fired <b>after</b> the screen's overridable initialization method is called.
         *
         * <p>This event is not {@linkplain Cancelable cancelable}, and does not {@linkplain HasResult have a result}. </p>
         *
         * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
         * only on the {@linkplain LogicalSide#CLIENT logical client}. </p>
         */
        public static class Post extends InitEvent
        {
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
     * @see net.minecraftforge.client.ForgeHooksClient#drawScreen(Screen, PoseStack, int, int, float)
     */
    public static class DrawScreenEvent extends ScreenEvent
    {
        private final PoseStack poseStack;
        private final int mouseX;
        private final int mouseY;
        private final float partialTick;

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
         * {@return the x coordinate of the mouse pointer}
         */
        public int getMouseX()
        {
            return mouseX;
        }

        /**
         * {@return the y coordinate of the mouse pointer}
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
         * <p>This event is {@linkplain Cancelable cancelable}, and does not {@linkplain HasResult have a result}.
         * If the event is cancelled, the screen will not be drawn. </p>
         *
         * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
         * only on the {@linkplain LogicalSide#CLIENT logical client}. </p>
         */
        @Cancelable
        public static class Pre extends DrawScreenEvent
        {
            public Pre(Screen screen, PoseStack poseStack, int mouseX, int mouseY, float partialTick)
            {
                super(screen, poseStack, mouseX, mouseY, partialTick);
            }
        }

        /**
         * Fired <b>after</b> the screen is drawn.
         *
         * <p>This event is not {@linkplain Cancelable cancelable}, and does not {@linkplain HasResult have a result}. </p>
         *
         * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
         * only on the {@linkplain LogicalSide#CLIENT logical client}. </p>
         */
        public static class Post extends DrawScreenEvent
        {
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
     * <p>This event is not {@linkplain Cancelable cancelable}, and does not {@linkplain HasResult have a result}. </p>
     *
     * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
     * only on the {@linkplain LogicalSide#CLIENT logical client}. </p>
     */
    public static class BackgroundDrawnEvent extends ScreenEvent
    {
        private final PoseStack poseStack;

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
     * Fired when there are potion effects applied and the screen is being shifted to make room.
     *
     * <p>This event is {@linkplain Cancelable cancelable}, and does not {@linkplain HasResult have a result}.
     * If the event is cancelled, the screen will be prevented from shifting. </p>
     *
     * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
     * only on the {@linkplain LogicalSide#CLIENT logical client}. </p>
     */
    @Cancelable
    public static class PotionShiftEvent extends ScreenEvent
    {
        public PotionShiftEvent(Screen screen)
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
         * <p>This event is {@linkplain Cancelable cancelable}, and does not {@linkplain HasResult have a result}.
         * If the event is cancelled, the screen's mouse click handler will be bypassed
         * and the corresponding {@link MouseClickedEvent.Post} will not be fired. </p>
         *
         * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
         * only on the {@linkplain LogicalSide#CLIENT logical client}. </p>
         *
         * @see ForgeHooksClient#onMouseClickedPre(Screen, double, double, int)
         */
        @Cancelable
        public static class Pre extends MouseClickedEvent
        {
            public Pre(Screen screen, double mouseX, double mouseY, int button)
            {
                super(screen, mouseX, mouseY, button);
            }
        }

        /**
         * Fired <b>after</b> the mouse click is handled, if not handled by the screen
         * and the corresponding {@link MouseClickedEvent.Pre} is not cancelled.
         *
         * <p>This event is {@linkplain Cancelable cancelable}, and does not {@linkplain HasResult have a result}.
         * If the event is cancelled, the mouse click will be set as handled. </p>
         *
         * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
         * only on the {@linkplain LogicalSide#CLIENT logical client}. </p>
         *
         * @see ForgeHooksClient#onMouseClickedPost(Screen, double, double, int)
         */
        @Cancelable
        public static class Post extends MouseClickedEvent
        {
            public Post(Screen screen, double mouseX, double mouseY, int button)
            {
                super(screen, mouseX, mouseY, button);
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
         * <p>This event is {@linkplain Cancelable cancelable}, and does not {@linkplain HasResult have a result}.
         * If the event is cancelled, the screen's mouse release handler will be bypassed
         * and the corresponding {@link MouseReleasedEvent.Post} will not be fired. </p>
         *
         * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
         * only on the {@linkplain LogicalSide#CLIENT logical client}. </p>
         *
         * @see ForgeHooksClient#onMouseReleasedPre(Screen, double, double, int)
         */
        @Cancelable
        public static class Pre extends MouseReleasedEvent
        {
            public Pre(Screen screen, double mouseX, double mouseY, int button)
            {
                super(screen, mouseX, mouseY, button);
            }
        }

        /**
         * Fired <b>after</b> the mouse release is handled, if not handled by the screen
         * and the corresponding {@link MouseReleasedEvent.Pre} is not cancelled.
         *
         * <p>This event is {@linkplain Cancelable cancelable}, and does not {@linkplain HasResult have a result}.
         * If the event is cancelled, the mouse release will be set as handled. </p>
         *
         * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
         * only on the {@linkplain LogicalSide#CLIENT logical client}. </p>
         *
         * @see ForgeHooksClient#onMouseReleasedPost(Screen, double, double, int)
         */
        @Cancelable
        public static class Post extends MouseReleasedEvent
        {
            public Post(Screen screen, double mouseX, double mouseY, int button)
            {
                super(screen, mouseX, mouseY, button);
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
         * {@return amount of mouse drag along the x axis}
         */
        public double getDragX()
        {
            return dragX;
        }

        /**
         * {@return amount of mouse drag along the y axis}
         */
        public double getDragY()
        {
            return dragY;
        }

        /**
         * Fired <b>before</b> the mouse drag is handled by the screen.
         *
         * <p>This event is {@linkplain Cancelable cancelable}, and does not {@linkplain HasResult have a result}.
         * If the event is cancelled, the screen's mouse drag handler will be bypassed
         * and the corresponding {@link MouseDragEvent.Post} will not be fired. </p>
         *
         * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
         * only on the {@linkplain LogicalSide#CLIENT logical client}. </p>
         *
         * @see ForgeHooksClient#onMouseDragPre(Screen, double, double, int, double, double)
         */
        @Cancelable
        public static class Pre extends MouseDragEvent
        {
            public Pre(Screen screen, double mouseX, double mouseY, int mouseButton, double dragX, double dragY)
            {
                super(screen, mouseX, mouseY, mouseButton, dragX, dragY);
            }
        }

        /**
         * Fired <b>after</b> the mouse drag is handled, if not handled by the screen
         * and the corresponding {@link MouseDragEvent.Pre} is not cancelled.
         *
         * <p>This event is {@linkplain Cancelable cancelable}, and does not {@linkplain HasResult have a result}.
         * If the event is cancelled, the mouse drag will be set as handled. </p>
         *
         * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
         * only on the {@linkplain LogicalSide#CLIENT logical client}. </p>
         *
         * @see ForgeHooksClient#onMouseDragPost(Screen, double, double, int, double, double)
         */
        @Cancelable
        public static class Post extends MouseDragEvent
        {
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

        public MouseScrollEvent(Screen screen, double mouseX, double mouseY, double scrollDelta)
        {
            super(screen, mouseX, mouseY);
            this.scrollDelta = scrollDelta;
        }

        /**
         * @return the amount of change / delta of the mouse scroll
         */
        public double getScrollDelta()
        {
            return scrollDelta;
        }

        /**
         * Fired <b>before</b> the mouse scroll is handled by the screen.
         *
         * <p>This event is {@linkplain Cancelable cancelable}, and does not {@linkplain HasResult have a result}.
         * If the event is cancelled, the screen's mouse scroll handler will be bypassed
         * and the corresponding {@link MouseScrollEvent.Post} will not be fired. </p>
         *
         * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
         * only on the {@linkplain LogicalSide#CLIENT logical client}. </p>
         *
         * @see ForgeHooksClient#onMouseScrollPre(MouseHandler, Screen, double)
         */
        @Cancelable
        public static class Pre extends MouseScrollEvent
        {
            public Pre(Screen screen, double mouseX, double mouseY, double scrollDelta)
            {
                super(screen, mouseX, mouseY, scrollDelta);
            }
        }

        /**
         * Fired <b>after</b> the mouse scroll is handled, if not handled by the screen
         * and the corresponding {@link MouseScrollEvent.Pre} is not cancelled.
         *
         * <p>This event is {@linkplain Cancelable cancelable}, and does not {@linkplain HasResult have a result}.
         * If the event is cancelled, the mouse scroll will be set as handled. </p>
         *
         * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
         * only on the {@linkplain LogicalSide#CLIENT logical client}. </p>
         *
         * @see ForgeHooksClient#onMouseScrollPost(MouseHandler, Screen, double)
         */
        @Cancelable
        public static class Post extends MouseScrollEvent
        {
            public Post(Screen screen, double mouseX, double mouseY, double scrollDelta)
            {
                super(screen, mouseX, mouseY, scrollDelta);
            }
        }
    }

    /**
     * <p>Fired whenever a keyboard key is pressed or released. <br/>
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
        public KeyboardKeyPressedEvent(Screen screen, int keyCode, int scanCode, int modifiers)
        {
            super(screen,  keyCode, scanCode, modifiers);
        }

        /**
         * Fired <b>before</b> the key press is handled by the screen.
         *
         * <p>This event is {@linkplain Cancelable cancelable} and does not {@linkplain HasResult have a result}.
         * If the event is cancelled, the screen's key press handler will be bypassed
         * and the corresponding {@link KeyboardKeyPressedEvent.Post} will not be fired. </p>
         *
         * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
         * only on the {@linkplain LogicalSide#CLIENT logical client}. </p>
         *
         * @see ForgeHooksClient#onKeyPressedPre(Screen, int, int, int)
         */
        @Cancelable
        public static class Pre extends KeyboardKeyPressedEvent
        {
            public Pre(Screen screen, int keyCode, int scanCode, int modifiers)
            {
                super(screen, keyCode, scanCode, modifiers);
            }
        }

        /**
         * Fired <b>after</b> the key press is handled, if not handled by the screen
         * and the corresponding {@link KeyboardKeyPressedEvent.Pre} is not cancelled.
         *
         * <p>This event is {@linkplain Cancelable cancelable}, and does not {@linkplain HasResult have a result}.
         * If the event is cancelled, the key press will be set as handled. </p>
         *
         * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
         * only on the {@linkplain LogicalSide#CLIENT logical client}. </p>
         *
         * @see ForgeHooksClient#onKeyPressedPost(Screen, int, int, int)
         */
        @Cancelable
        public static class Post extends KeyboardKeyPressedEvent
        {
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
        public KeyboardKeyReleasedEvent(Screen screen, int keyCode, int scanCode, int modifiers)
        {
            super(screen, keyCode, scanCode, modifiers);
        }

        /**
         * Fired <b>before</b> the key release is handled by the screen.
         *
         * <p>This event is {@linkplain Cancelable cancelable}, and does not {@linkplain HasResult have a result}.
         * If the event is cancelled, the screen's key release handler will be bypassed
         * and the corresponding {@link KeyboardKeyReleasedEvent.Post} will not be fired. </p>
         *
         * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
         * only on the {@linkplain LogicalSide#CLIENT logical client}. </p>
         *
         * @see ForgeHooksClient#onKeyPressedPost(Screen, int, int, int)
         */
        @Cancelable
        public static class Pre extends KeyboardKeyReleasedEvent
        {
            public Pre(Screen screen, int keyCode, int scanCode, int modifiers)
            {
                super(screen, keyCode, scanCode, modifiers);
            }
        }

        /**
         * Fired <b>after</b> the key release is handled, if not handled by the screen
         * and the corresponding {@link KeyboardKeyReleasedEvent.Pre} is not cancelled.
         *
         * <p>This event is {@linkplain Cancelable cancelable}, and does not {@linkplain HasResult have a result}.
         * If the event is cancelled, the key release will be set as handled. </p>
         *
         * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
         * only on the {@linkplain LogicalSide#CLIENT logical client}. </p>
         *
         * @see ForgeHooksClient#onKeyReleasedPost(Screen, int, int, int)
         */
        @Cancelable
        public static class Post extends KeyboardKeyReleasedEvent
        {
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

        public KeyboardCharTypedEvent(Screen screen, char codePoint, int modifiers)
        {
            super(screen);
            this.codePoint = codePoint;
            this.modifiers = modifiers;
        }

        /**
         * {@return the character code point typed}
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
         * <p>This event is {@linkplain Cancelable cancelable}, and does not {@linkplain HasResult have a result}.
         * If the event is cancelled, the screen's character input handler will be bypassed
         * and the corresponding {@link KeyboardCharTypedEvent.Post} will not be fired. </p>
         *
         * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
         * only on the {@linkplain LogicalSide#CLIENT logical client}. </p>
         *
         * @see KeyboardCharTypedEvent <em>the superclass for event firing information</em>
         * @see ForgeHooksClient#onCharTypedPre(Screen, char, int)
         */
        @Cancelable
        public static class Pre extends KeyboardCharTypedEvent
        {
            public Pre(Screen screen, char codePoint, int modifiers)
            {
                super(screen, codePoint, modifiers);
            }
        }

        /**
         * Fired <b>after</b> the character input is handled, if not handled by the screen
         * and the corresponding {@link KeyboardCharTypedEvent.Pre} is not cancelled.
         *
         * <p>This event is {@linkplain Cancelable cancelable}, and does not {@linkplain HasResult have a result}.
         * If the event is cancelled, the character input will be set as handled. </p>
         *
         * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
         * only on the {@linkplain LogicalSide#CLIENT logical client}. </p>
         *
         * @see ForgeHooksClient#onCharTypedPost(Screen, char, int)
         */
        @Cancelable
        public static class Post extends KeyboardCharTypedEvent
        {
            public Post(Screen screen, char codePoint, int modifiers)
            {
                super(screen, codePoint, modifiers);
            }
        }
    }
}
