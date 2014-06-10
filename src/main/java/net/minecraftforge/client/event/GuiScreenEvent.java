package net.minecraftforge.client.event;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import cpw.mods.fml.common.eventhandler.Cancelable;
import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Event classes for GuiScreen events.
 * 
 * @author bspkrs
 */
@SideOnly(Side.CLIENT)
public class GuiScreenEvent extends Event 
{
    /**
     * The GuiScreen object generating this event.
     */
    public final GuiScreen gui;
    
    public GuiScreenEvent(GuiScreen gui)
    {
        this.gui = gui;
    }

    public static class InitGuiEvent extends GuiScreenEvent
    {
        /**
         * The {@code buttonList} field from the GuiScreen object referenced by {@code gui}.
         */
        public List buttonList;
        
        public InitGuiEvent(GuiScreen gui, List buttonList)
        {
            super(gui);
            this.buttonList = buttonList;
        }
        
        /**
         * This event fires just after initializing {@code GuiScreen.mc}, {@code GuiScreen.fontRendererObj}, 
         * {@code GuiScreen.width}, and {@code GuiScreen.height}, and just before calling {@code GuiScreen.buttonList.clear()} 
         * and {@code GuiScreen.initGui()}. To skip or override a screen's initGui() method cancel the event.<br/><br/>
         * 
         * If canceled the following lines are skipped in {@code GuiScreen.setWorldAndResolution()}:<br/>
         * {@code this.buttonList.clear();}<br/>
         * {@code this.initGui();}<br/>
         * 
         * @author bspkrs
         */
        @Cancelable
        public static class Pre extends InitGuiEvent
        {
            public Pre(GuiScreen gui, List buttonList)
            {
                super(gui, buttonList);
            }
        }
        
        /**
         * This event fires right after {@code GuiScreen.initGui()}.
         * This is a good place to alter a GuiScreen's component layout if desired.
         * 
         * @author bspkrs
         */
        public static class Post extends InitGuiEvent
        {
            public Post(GuiScreen gui, List buttonList)
            {
                super(gui, buttonList);
            }
        }
    }
    
    public static class DrawScreenEvent extends GuiScreenEvent
    {
        /**
         * The x coordinate of the mouse pointer on the screen.
         */
        public final int mouseX;
        /**
         * The y coordinate of the mouse pointer on the screen.
         */
        public final int mouseY;
        /**
         * Partial render ticks elapsed.
         */
        public final float renderPartialTicks;

        public DrawScreenEvent(GuiScreen gui, int mouseX, int mouseY, float renderPartialTicks)
        {
            super(gui);
            this.mouseX = mouseX;
            this.mouseY = mouseY;
            this.renderPartialTicks = renderPartialTicks;
        }
        
        /**
         * This event fires just before {@code GuiScreen.drawScreen()} is called.
         * Cancel this event to skip {@code GuiScreen.drawScreen()}.
         * 
         * @author bspkrs
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
         * This event fires just after {@code GuiScreen.drawScreen()} is called.
         * 
         * @author bspkrs
         */
        public static class Post extends DrawScreenEvent
        {
            public Post(GuiScreen gui, int mouseX, int mouseY, float renderPartialTicks)
            {
                super(gui, mouseX, mouseY, renderPartialTicks);
            }
        }
    }
    
    public static class ActionPerformedEvent extends GuiScreenEvent
    {
        /**
         * The button that was clicked.
         */
        public GuiButton button;
        /**
         * A COPY of the {@code buttonList} field from the GuiScreen referenced by {@code gui}.
         */
        public List buttonList;

        public ActionPerformedEvent(GuiScreen gui, GuiButton button, List buttonList)
        {
            super(gui);
            this.button = button;
            this.buttonList = new ArrayList(buttonList);
        }
        
        /**
         * This event fires once it has been determined that a GuiButton object has been clicked.
         * Cancel this event to bypass {@code GuiScreen.actionPerformed()}.
         * Replace button with a different button from buttonList to have that button's action executed.
         * 
         * @author bspkrs
         */
        @Cancelable
        public static class Pre extends ActionPerformedEvent
        {
            public Pre(GuiScreen gui, GuiButton button, List buttonList)
            {
                super(gui, button, buttonList);
            }
        }
        
        /**
         * This event fires after {@code GuiScreen.actionPerformed()} provided that the active 
         * screen has not been changed as a result of {@code GuiScreen.actionPerformed()}.
         * 
         * @author bspkrs
         */
        public static class Post extends ActionPerformedEvent
        {
            public Post(GuiScreen gui, GuiButton button, List buttonList)
            {
                super(gui, button, buttonList);
            }
        }
    }
}
