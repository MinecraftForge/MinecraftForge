package net.minecraftforge.client.event;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.event.Cancelable;
import net.minecraftforge.event.Event;

/**
 * This event is called before any Gui will open.
 * If you don't want this to happen, cancel the event.
 * If you want to override this Gui, simply set the gui variable to your own Gui.
 * 
 * @author jk-5
 */
@Cancelable
public class GuiOpenEvent extends Event
{
    public GuiScreen gui;
    public GuiOpenEvent(GuiScreen gui)
    {
        this.gui = gui;
    }
}
