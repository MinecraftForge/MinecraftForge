package net.minecraft.src;

import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

class ServerGuiFocusAdapter extends FocusAdapter
{
    /** Reference to the ServerGui object. */
    final ServerGUI mcServerGui;

    ServerGuiFocusAdapter(ServerGUI par1ServerGUI)
    {
        this.mcServerGui = par1ServerGUI;
    }

    public void focusGained(FocusEvent par1FocusEvent) {}
}
