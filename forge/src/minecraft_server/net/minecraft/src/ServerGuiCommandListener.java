package net.minecraft.src;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JTextField;

class ServerGuiCommandListener implements ActionListener
{
    /** Text field. */
    final JTextField textField;

    /** Reference to the ServerGui object. */
    final ServerGUI mcServerGui;

    ServerGuiCommandListener(ServerGUI par1ServerGUI, JTextField par2JTextField)
    {
        this.mcServerGui = par1ServerGUI;
        this.textField = par2JTextField;
    }

    public void actionPerformed(ActionEvent par1ActionEvent)
    {
        String var2 = this.textField.getText().trim();

        if (var2.length() > 0)
        {
            ServerGUI.getMinecraftServer(this.mcServerGui).addCommand(var2, this.mcServerGui);
        }

        this.textField.setText("");
    }
}
