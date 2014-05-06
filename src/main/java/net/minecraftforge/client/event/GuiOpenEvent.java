package net.minecraftforge.client.event;

import cpw.mods.fml.common.eventhandler.Cancelable;
import cpw.mods.fml.common.eventhandler.Event;
import net.minecraft.client.gui.GuiScreen;

/**
 * Deprecated to move it to GuiScreenEvent.GuiOpenEvent
 * 
 * This event is called before any Gui will open.
 * If you don't want this to happen, cancel the event.
 * If you want to override this Gui, simply set the gui variable to your own Gui.
 * 
 * @author jk-5
 */
@Cancelable
@Deprecated
public class GuiOpenEvent extends Event
{
    public GuiScreen gui;
    public GuiOpenEvent(GuiScreen gui)
    {
        this.gui = gui;
    }
}
