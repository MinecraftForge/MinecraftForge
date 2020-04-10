/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
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

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.InventoryEffectRenderer;
import org.lwjgl.input.Mouse;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;

import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Event classes for GuiScreen events.
 * 
 * @author bspkrs
 */
@SideOnly(Side.CLIENT)
public class GuiScreenEvent extends Event 
{
    private final GuiScreen gui;
    
    public GuiScreenEvent(GuiScreen gui)
    {
        this.gui = gui;
    }

    /**
     * The GuiScreen object generating this event.
     */
    public GuiScreen getGui()
    {
        return gui;
    }

    public static class InitGuiEvent extends GuiScreenEvent
    {
        private List<GuiButton> buttonList;
        
        public InitGuiEvent(GuiScreen gui, List<GuiButton> buttonList)
        {
            super(gui);
            this.setButtonList(buttonList);
        }

        /**
         * The {@link #buttonList} field from the GuiScreen object referenced by {@link #gui}.
         */
        public List<GuiButton> getButtonList()
        {
            return buttonList;
        }

        public void setButtonList(List<GuiButton> buttonList)
        {
            this.buttonList = buttonList;
        }

        /**
         * This event fires just after initializing {@link GuiScreen#mc}, {@link GuiScreen#fontRenderer},
         * {@link GuiScreen#width}, and {@link GuiScreen#height}.<br/><br/>
         * 
         * If canceled the following lines are skipped in {@link GuiScreen#setWorldAndResolution(Minecraft, int, int)}:<br/>
         * {@code this.buttonList.clear();}<br/>
         * {@code this.initGui();}<br/>
         */
        @Cancelable
        public static class Pre extends InitGuiEvent
        {
            public Pre(GuiScreen gui, List<GuiButton> buttonList)
            {
                super(gui, buttonList);
            }
        }
        
        /**
         * This event fires right after {@link GuiScreen#initGui()}.
         * This is a good place to alter a GuiScreen's component layout if desired.
         */
        public static class Post extends InitGuiEvent
        {
            public Post(GuiScreen gui, List<GuiButton> buttonList)
            {
                super(gui, buttonList);
            }
        }
    }
    
    public static class DrawScreenEvent extends GuiScreenEvent
    {
        private final int mouseX;
        private final int mouseY;
        private final float renderPartialTicks;

        public DrawScreenEvent(GuiScreen gui, int mouseX, int mouseY, float renderPartialTicks)
        {
            super(gui);
            this.mouseX = mouseX;
            this.mouseY = mouseY;
            this.renderPartialTicks = renderPartialTicks;
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
        public float getRenderPartialTicks()
        {
            return renderPartialTicks;
        }

        /**
         * This event fires just before {@link GuiScreen#drawScreen(int, int, float)} is called.
         * Cancel this event to skip {@link GuiScreen#drawScreen(int, int, float)}.
         */
        @Cancelable
        public static class Pre extends DrawScreenEvent
        {
            public Pre(GuiScreen gui, int mouseX, int mouseY, float renderPartialTicks)
            {
                super(gui, mouseX, mouseY, renderPartialTicks);
            }
        }

        /**
         * This event fires just after {@link GuiScreen#drawScreen(int, int, float)} is called.
         */
        public static class Post extends DrawScreenEvent
        {
            public Post(GuiScreen gui, int mouseX, int mouseY, float renderPartialTicks)
            {
                super(gui, mouseX, mouseY, renderPartialTicks);
            }
        }
    }

    /**
     * This event fires at the end of {@link GuiScreen#drawDefaultBackground()} and before the rest of the Gui draws.
     * This allows drawing next to Guis, above the background but below any tooltips.
     */
    public static class BackgroundDrawnEvent extends GuiScreenEvent
    {
        private final int mouseX;
        private final int mouseY;

        public BackgroundDrawnEvent(GuiScreen gui)
        {
            super(gui);
            final ScaledResolution scaledresolution = new ScaledResolution(gui.mc);
            final int scaledWidth = scaledresolution.getScaledWidth();
            final int scaledHeight = scaledresolution.getScaledHeight();
            this.mouseX = Mouse.getX() * scaledWidth / gui.mc.displayWidth;
            this.mouseY = scaledHeight - Mouse.getY() * scaledHeight / gui.mc.displayHeight - 1;
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
    }

    /**
     * This event fires in {@link InventoryEffectRenderer#updateActivePotionEffects()}
     * when potion effects are active and the gui wants to move over.
     * Cancel this event to prevent the Gui from being moved.
     */
    @Cancelable
    public static class PotionShiftEvent extends GuiScreenEvent
    {
        public PotionShiftEvent(GuiScreen gui)
        {
            super(gui);
        }
    }
    
    public static class ActionPerformedEvent extends GuiScreenEvent
    {
        private GuiButton button;
        private List<GuiButton> buttonList;

        public ActionPerformedEvent(GuiScreen gui, GuiButton button, List<GuiButton> buttonList)
        {
            super(gui);
            this.setButton(button);
            this.setButtonList(new ArrayList<GuiButton>(buttonList));
        }

        /**
         * The button that was clicked.
         */
        public GuiButton getButton()
        {
            return button;
        }

        public void setButton(GuiButton button)
        {
            this.button = button;
        }

        /**
         * A COPY of the {@link #buttonList} field from the GuiScreen referenced by {@link #gui}.
         */
        public List<GuiButton> getButtonList()
        {
            return buttonList;
        }

        public void setButtonList(List<GuiButton> buttonList)
        {
            this.buttonList = buttonList;
        }

        /**
         * This event fires once it has been determined that a GuiButton object has been clicked.
         * Cancel this event to bypass {@link GuiScreen#actionPerformed(GuiButton)}.
         * Replace button with a different button from buttonList to have that button's action executed.
         */
        @Cancelable
        public static class Pre extends ActionPerformedEvent
        {
            public Pre(GuiScreen gui, GuiButton button, List<GuiButton> buttonList)
            {
                super(gui, button, buttonList);
            }
        }
        
        /**
         * This event fires after {@link GuiScreen#actionPerformed(GuiButton)} provided that the active
         * screen has not been changed as a result of {@link GuiScreen#actionPerformed(GuiButton)}.
         */
        public static class Post extends ActionPerformedEvent
        {
            public Post(GuiScreen gui, GuiButton button, List<GuiButton> buttonList)
            {
                super(gui, button, buttonList);
            }
        }
    }

    public static class MouseInputEvent extends GuiScreenEvent
    {
        public MouseInputEvent(GuiScreen gui)
        {
            super(gui);
        }

        /**
         * This event fires when mouse input is detected by a GuiScreen.
         * Cancel this event to bypass {@link GuiScreen#handleMouseInput()}.
         */
        @Cancelable
        public static class Pre extends MouseInputEvent
        {
            public Pre(GuiScreen gui)
            {
                super(gui);
            }
        }

        /**
         * This event fires after {@link GuiScreen#handleMouseInput()} provided that the active
         * screen has not been changed as a result of {@link GuiScreen#handleMouseInput()} and
         * the {@link GuiScreen#mouseHandled} flag has not been set.
         * Cancel this event when you successfully use the mouse input to prevent other handlers from using the same input.
         */
        @Cancelable
        public static class Post extends MouseInputEvent
        {
            public Post(GuiScreen gui)
            {
                super(gui);
            }
        }
    }

    public static class KeyboardInputEvent extends GuiScreenEvent
    {
        public KeyboardInputEvent(GuiScreen gui)
        {
            super(gui);
        }

        /**
         * This event fires when keyboard input is detected by a GuiScreen.
         * Cancel this event to bypass {@link GuiScreen#handleKeyboardInput()}.
         */
        @Cancelable
        public static class Pre extends KeyboardInputEvent
        {
            public Pre(GuiScreen gui)
            {
                super(gui);
            }
        }

        /**
         * This event fires after {@link GuiScreen#handleKeyboardInput()} provided that the active
         * screen has not been changed as a result of {@link GuiScreen#handleKeyboardInput()} and
         * the {@link GuiScreen#keyHandled} flag has not been set.
         * Cancel this event when you successfully use the keyboard input to prevent other handlers from using the same input.
         */
        @Cancelable
        public static class Post extends KeyboardInputEvent
        {
            public Post(GuiScreen gui)
            {
                super(gui);
            }
        }
    }
}
