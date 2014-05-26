package net.minecraftforge.client.event;

import cpw.mods.fml.common.eventhandler.Event;
import net.minecraft.client.gui.GuiTextField;

/**
 * This event is called when the focused state of a GuiTextField changes.
 *
 * @author Kobata
 */
public class TextFieldFocusEvent extends Event
{
    public GuiTextField gui;
    public TextFieldFocusEvent(GuiTextField gui) { this.gui = gui; }
}
