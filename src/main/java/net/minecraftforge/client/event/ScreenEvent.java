/*
 * Minecraft Forge - Forge Development LLC
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.event;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraft.client.gui.screens.Screen;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.LogicalSide;
import org.lwjgl.glfw.GLFW;

/**
 * Event classes for GuiScreen events.
 *
 * @author bspkrs
 */
@OnlyIn(Dist.CLIENT)
public class ScreenEvent extends Event
{
    private final Screen screen;

    public ScreenEvent(Screen screen)
    {
        this.screen = Objects.requireNonNull(screen);
    }

    /**
     * The GuiScreen object generating this event.
     */
    public Screen getScreen()
    {
        return screen;
    }

    public static class InitScreenEvent extends ScreenEvent
    {
        private Consumer<GuiEventListener> add;
        private Consumer<GuiEventListener> remove;

        private List<GuiEventListener> listenerList;

        public InitScreenEvent(Screen screen, List<GuiEventListener> listenerList, Consumer<GuiEventListener> add, Consumer<GuiEventListener> remove)
        {
            super(screen);
            this.listenerList = Collections.unmodifiableList(listenerList);
            this.add = add;
            this.remove = remove;
        }

        /**
         * Unmodifiable reference to the list of buttons on the {@link #screen}.
         */
        public List<GuiEventListener> getListenersList()
        {
            return listenerList;
        }

        public void addListener(GuiEventListener button)
        {
            add.accept(button);
        }

        public void removeListener(GuiEventListener button)
        {
            remove.accept(button);
        }

        /**
         * This event fires just after initializing the {@link Minecraft}, font renderer, width,
         * and height fields.<br/><br/>
         *
         * If canceled the following lines are skipped in {@link Screen#init(Minecraft, int, int)}:<br/>
         * {@code this.buttonList.clear();}<br/>
         * {@code this.children.clear();}<br/>
         * {@code this.initGui();}<br/>
         */
        @Cancelable
        public static class Pre extends InitScreenEvent
        {
            public Pre(Screen screen, List<GuiEventListener> list, Consumer<GuiEventListener> add, Consumer<GuiEventListener> remove)
            {
                super(screen, list, add, remove);
            }
        }

        /**
         * This event fires right after {@code Screen#init()}.
         * This is a good place to alter a GuiScreen's component layout if desired.
         */
        public static class Post extends InitScreenEvent
        {
            public Post(Screen screen, List<GuiEventListener> list, Consumer<GuiEventListener> add, Consumer<GuiEventListener> remove)
            {
                super(screen, list, add, remove);
            }
        }
    }

    public static class DrawScreenEvent extends ScreenEvent
    {
        private final PoseStack poseStack;
        private final int mouseX;
        private final int mouseY;
        private final float partialTicks;

        public DrawScreenEvent(Screen screen, PoseStack poseStack, int mouseX, int mouseY, float partialTicks)
        {
            super(screen);
            this.poseStack = poseStack;
            this.mouseX = mouseX;
            this.mouseY = mouseY;
            this.partialTicks = partialTicks;
        }

        /**
         * The PoseStack to render with.
         */
        public PoseStack getPoseStack()
        {
            return poseStack;
        }

        /**
         * The x coordinate of the mouse pointer on the screen.
         */
        public int getMouseX()
        {
            return mouseX;
        }

        /**
         * The y coordinate of the mouse pointer on the screen.
         */
        public int getMouseY()
        {
            return mouseY;
        }

        /**
         * Partial render ticks elapsed.
         */
        public float getPartialTicks()
        {
            return partialTicks;
        }

        /**
         * This event fires just before {@link Screen#render(PoseStack, int, int, float)} is called.
         * Cancel this event to skip the render method.
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
         * This event fires just after {@link Screen#render(PoseStack, int, int, float)} is called.
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
     * This event fires at the end of {@link Screen#renderBackground(PoseStack, int)} and before the rest of the Gui draws.
     * This allows drawing next to Guis, above the background but below any tooltips.
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
         * The PoseStack to render with.
         */
        public PoseStack getPoseStack()
        {
            return poseStack;
        }
    }

    /**
     * This event fires in {@link EffectRenderingInventoryScreen} in the
     * {@code checkEffectRendering} method when potion effects are active and the gui wants to move over.
     * Cancel this event to prevent the Gui from being moved.
     *
     * @deprecated This event was made redundant by the removal of the screen shifting due to potion indicators in the
     * inventory screen (along with being moved to the right hand side). This has been changed to have no effect, and
     * will be removed in 1.19. See {@link PotionSizeEvent} as a possible alternative instead.
     */
    @Deprecated(forRemoval = true, since = "1.18.1")
    @Cancelable
    public static class PotionShiftEvent extends ScreenEvent
    {
        public PotionShiftEvent(Screen screen)
        {
            super(screen);
        }
    }

    /**
     * Fired to determine whether to render the potion indicators in the {@link EffectRenderingInventoryScreen inventory
     * screen} in compact or classic mode.
     *
     * <p>This event is not {@linkplain Cancelable cancelable} and {@linkplain HasResult has a result}. </p>
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

        public double getMouseX()
        {
            return mouseX;
        }

        public double getMouseY()
        {
            return mouseY;
        }
    }

    public static abstract class MouseClickedEvent extends MouseInputEvent
    {
        private final int button;

        public MouseClickedEvent(Screen screen, double mouseX, double mouseY, int button)
        {
            super(screen, mouseX, mouseY);
            this.button = button;
        }

        public int getButton()
        {
            return button;
        }

        /**
         * This event fires when a mouse click is detected for a GuiScreen, before it is handled.
         * Cancel this event to bypass {@link GuiEventListener#mouseClicked(double, double, int)}.
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
         * This event fires after {@link GuiEventListener#mouseClicked(double, double, int)}.
         *
         * <p>This event {@linkplain HasResult has a result}.<br>
         * <ul>
         *   <li><b>{@link Result#ALLOW}</b> - to force set the mouse click as handled</li>
         *   <li><b>{@link Result#DEFAULT}</b> - to use the default value of {@link #handled}</li>
         *   <li><b>{@link Result#DENY}</b> - to force set the mouse click as not handled</li>
         * </ul>
         * </p>
         *
         * <p>Note that this event is currently pre-cancelled if {@link Screen#mouseClicked} returns {@code true}
         * to retain old behavior. This will be changed in 1.18 when the event is made non-cancellable.</p>
         */
        @HasResult
        public static class Post extends MouseClickedEvent
        {
            private final boolean handled;

            public Post(Screen screen, double mouseX, double mouseY, int button, boolean handled)
            {
                super(screen, mouseX, mouseY, button);
                this.handled = handled;
            }

            /**
             * @return {@code true} if the mouse click was already handled by its screen
             */
            public boolean wasHandled()
            {
                return handled;
            }
        }
    }

    public static abstract class MouseReleasedEvent extends MouseInputEvent
    {
        private final int button;

        public MouseReleasedEvent(Screen screen, double mouseX, double mouseY, int button)
        {
            super(screen, mouseX, mouseY);
            this.button = button;
        }

        public int getButton()
        {
            return button;
        }

        /**
         * This event fires when a mouse release is detected for a GuiScreen, before it is handled.
         * Cancel this event to bypass {@link GuiEventListener#mouseReleased(double, double, int)}.
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
         * This event fires after {@link GuiEventListener#mouseReleased(double, double, int)}.
         *
         * <p>This event {@linkplain HasResult has a result}.<br>
         * <ul>
         *   <li><b>{@link Result#ALLOW}</b> - to force set the mouse release as handled</li>
         *   <li><b>{@link Result#DEFAULT}</b> - to use the default value of {@link #handled}</li>
         *   <li><b>{@link Result#DENY}</b> - to force set the mouse release as not handled</li>
         * </ul>
         * </p>
         *
         */
        @HasResult
        public static class Post extends MouseReleasedEvent
        {
            private final boolean handled;

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

        public int getMouseButton()
        {
            return mouseButton;
        }

        public double getDragX()
        {
            return dragX;
        }

        public double getDragY()
        {
            return dragY;
        }

        /**
         * This event fires when a mouse drag is detected for a GuiScreen, before it is handled.
         * Cancel this event to bypass {@link GuiEventListener#mouseDragged(double, double, int, double, double)}.
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
         * This event fires after {@link GuiEventListener#mouseDragged(double, double, int, double, double)} if the drag was not already handled.
         * Cancel this event when you successfully use the mouse drag, to prevent other handlers from using the same input.
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

    public static abstract class MouseScrollEvent extends MouseInputEvent
    {
        private final double scrollDelta;

        public MouseScrollEvent(Screen screen, double mouseX, double mouseY, double scrollDelta)
        {
            super(screen, mouseX, mouseY);
            this.scrollDelta = scrollDelta;
        }

        public double getScrollDelta()
        {
            return scrollDelta;
        }

        /**
         * This event fires when a mouse scroll is detected for a GuiScreen, before it is handled.
         * Cancel this event to bypass {@link GuiEventListener#mouseScrolled(double, double, double)}.
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
         * This event fires after {@link GuiEventListener#mouseScrolled(double, double, double)} if the scroll was not already handled.
         * Cancel this event when you successfully use the mouse scroll, to prevent other handlers from using the same input.
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
         * The keyboard key that was pressed or released
         * https://www.glfw.org/docs/latest/group__keys.html
         *
         * @see GLFW key constants starting with "GLFW_KEY_"
         */
        public int getKeyCode()
        {
            return keyCode;
        }

        /**
         * Platform-specific scan code.
         * Used for {@link com.mojang.blaze3d.platform.InputConstants#getKey(int, int)}
         *
         * The scan code is unique for every key, regardless of whether it has a key code.
         * Scan codes are platform-specific but consistent over time, so keys will have different scan codes depending
         * on the platform but they are safe to save to disk as custom key bindings.
         */
        public int getScanCode()
        {
            return scanCode;
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
            return modifiers;
        }
    }

    public static abstract class KeyboardKeyPressedEvent extends KeyboardKeyEvent
    {
        public KeyboardKeyPressedEvent(Screen screen, int keyCode, int scanCode, int modifiers)
        {
            super(screen,  keyCode, scanCode, modifiers);
        }

        /**
         * This event fires when keyboard input is detected for a GuiScreen, before it is handled.
         * Cancel this event to bypass {@link GuiEventListener#keyPressed(int, int, int)}.
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
         * This event fires after {@link GuiEventListener#keyPressed(int, int, int)} if the key was not already handled.
         * Cancel this event when you successfully use the keyboard input to prevent other handlers from using the same input.
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

    public static abstract class KeyboardKeyReleasedEvent extends KeyboardKeyEvent
    {
        public KeyboardKeyReleasedEvent(Screen screen, int keyCode, int scanCode, int modifiers)
        {
            super(screen, keyCode, scanCode, modifiers);
        }

        /**
         * This event fires when keyboard input is detected for a GuiScreen, before it is handled.
         * Cancel this event to bypass {@link GuiEventListener#keyReleased(int, int, int)}.
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
         * This event fires after {@link GuiEventListener#keyReleased(int, int, int)} if the key was not already handled.
         * Cancel this event when you successfully use the keyboard input to prevent other handlers from using the same input.
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
         * The code point typed, used for text entry.
         */
        public char getCodePoint()
        {
            return codePoint;
        }

        /**
         * Bit field representing the modifier keys pressed.
         *
         * @see GLFW#GLFW_MOD_SHIFT
         * @see GLFW#GLFW_MOD_CONTROL
         * @see GLFW#GLFW_MOD_ALT
         * @see GLFW#GLFW_MOD_SUPER
         */
        public int getModifiers()
        {
            return modifiers;
        }

        /**
         * This event fires when keyboard character input is detected for a GuiScreen, before it is handled.
         * Cancel this event to bypass {@link GuiEventListener#charTyped(char, int)}.
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
         * This event fires after {@link GuiEventListener#charTyped(char, int)} if the character was not already handled.
         * Cancel this event when you successfully use the keyboard input to prevent other handlers from using the same input.
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
