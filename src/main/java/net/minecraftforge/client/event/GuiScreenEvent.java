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
     * The GuiScreen object generating this event (final).
     */
    public final GuiScreen gui;
    
    public GuiScreenEvent(GuiScreen gui)
    {
        this.gui = gui;
    }

    public static class InitGuiEvent extends GuiScreenEvent
    {
        /**
         * The {@code buttonList} field from the GuiScreen referenced by {@code gui}.
         */
        public List buttonList;
        
        public InitGuiEvent(GuiScreen gui, List buttonList)
        {
            super(gui);
            this.buttonList = buttonList;
        }
        
        /**
         * This event is called just after initializing {@code GuiScreen.mc} and {@code GuiScreen.fontRendererObj}, and just before 
         * initializing {@code GuiScreen.width}, {@code GuiScreen.height}, and calling {@code GuiScreen.buttonList.clear()} 
         * and {@code GuiScreen.initGui()}. To bypass or re-implement a screen's initGui() method cancel the event.<br/><br/>
         * 
         * If canceled the following lines are skipped in {@code GuiScreen.setWorldAndResolution()}:<br/>
         * {@code this.width = p_146280_2_;}<br/>
         * {@code this.height = p_146280_3_;}<br/>
         * {@code this.buttonList.clear();}<br/>
         * {@code this.initGui();}<br/>
         * 
         * @author bspkrs
         */
        @Cancelable
        public static class PreInitGuiEvent extends InitGuiEvent
        {
            public PreInitGuiEvent(GuiScreen gui, List buttonList)
            {
                super(gui, buttonList);
            }
        }
        
        /**
         * This event is called right after {@code GuiScreen.initGui()}.
         * This is a good place to alter a GuiScreen's component layout if desired.
         * 
         * @author bspkrs
         */
        public static class PostInitGuiEvent extends InitGuiEvent
        {
            public PostInitGuiEvent(GuiScreen gui, List buttonList)
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
         * Cancel this event to skip {@code GuiScreen.drawScreen()} and {@code PostDrawScreenEvent}.
         * 
         * @author bspkrs
         */
        @Cancelable
        public static class PreDrawScreenEvent extends DrawScreenEvent
        {
            public PreDrawScreenEvent(GuiScreen gui, int mouseX, int mouseY, float renderPartialTicks)
            {
                super(gui, mouseX, mouseY, renderPartialTicks);
            }
        }

        /**
         * This event fires just after {@code GuiScreen.drawScreen()} is called provided that the
         * {@code PreDrawScreenEvent} was not canceled.
         * 
         * @author bspkrs
         */
        public static class PostDrawScreenEvent extends DrawScreenEvent
        {
            public PostDrawScreenEvent(GuiScreen gui, int mouseX, int mouseY, float renderPartialTicks)
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
         * A COPY of the buttonList field from the GuiScreen referenced by {@code gui}.
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
        public static class PreActionPerformedEvent extends ActionPerformedEvent
        {
            public PreActionPerformedEvent(GuiScreen gui, GuiButton button, List buttonList)
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
        public static class PostActionPerformedEvent extends ActionPerformedEvent
        {
            public PostActionPerformedEvent(GuiScreen gui, GuiButton button, List buttonList)
            {
                super(gui, button, buttonList);
            }
        }
    }

    /**
     * This event is called before any GuiScreen will open.
     * If you don't want this to happen cancel the event.
     * If you want to override this GuiScreen set the newGui variable to your own Gui.
     * 
     * @author jk-5
     */
    @Cancelable
    public static class GuiOpenEvent extends GuiScreenEvent
    {
        /**
         * The GuiScreen object that will be opened.
         */
        public GuiScreen newGui;
        
        public GuiOpenEvent(GuiScreen gui)
        {
            super(gui);
            newGui = gui;
        }
    }
}
