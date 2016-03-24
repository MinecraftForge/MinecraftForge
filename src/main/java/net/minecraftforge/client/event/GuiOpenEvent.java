package net.minecraftforge.client.event;

import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraft.client.gui.GuiScreen;

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
    private GuiScreen gui;
    public GuiOpenEvent(GuiScreen gui)
    {
        this.setGui(gui);
    }

    public GuiScreen getGui()
    {
        return gui;
    }

    public void setGui(GuiScreen gui)
    {
        this.gui = gui;
    }
}
