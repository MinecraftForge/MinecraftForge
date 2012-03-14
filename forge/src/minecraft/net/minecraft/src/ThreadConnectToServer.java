package net.minecraft.src;

import java.net.ConnectException;
import java.net.UnknownHostException;
import net.minecraft.client.Minecraft;

class ThreadConnectToServer extends Thread
{
    /** A reference to the Minecraft object. */
    final Minecraft mc;

    final String field_48479_b;

    /** A reference to the Minecraft object. */
    final int port;

    /** A reference to the GuiConnecting object. */
    final GuiConnecting connectingGui;

    ThreadConnectToServer(GuiConnecting par1GuiConnecting, Minecraft par2Minecraft, String par3Str, int par4)
    {
        this.connectingGui = par1GuiConnecting;
        this.mc = par2Minecraft;
        this.field_48479_b = par3Str;
        this.port = par4;
    }

    public void run()
    {
        try
        {
            GuiConnecting.setNetClientHandler(this.connectingGui, new NetClientHandler(this.mc, this.field_48479_b, this.port));

            if (GuiConnecting.isCancelled(this.connectingGui))
            {
                return;
            }

            GuiConnecting.getNetClientHandler(this.connectingGui).addToSendQueue(new Packet2Handshake(this.mc.session.username, this.field_48479_b, this.port));
        }
        catch (UnknownHostException var2)
        {
            if (GuiConnecting.isCancelled(this.connectingGui))
            {
                return;
            }

            this.mc.displayGuiScreen(new GuiDisconnected("connect.failed", "disconnect.genericReason", new Object[] {"Unknown host \'" + this.field_48479_b + "\'"}));
        }
        catch (ConnectException var3)
        {
            if (GuiConnecting.isCancelled(this.connectingGui))
            {
                return;
            }

            this.mc.displayGuiScreen(new GuiDisconnected("connect.failed", "disconnect.genericReason", new Object[] {var3.getMessage()}));
        }
        catch (Exception var4)
        {
            if (GuiConnecting.isCancelled(this.connectingGui))
            {
                return;
            }

            var4.printStackTrace();
            this.mc.displayGuiScreen(new GuiDisconnected("connect.failed", "disconnect.genericReason", new Object[] {var4.toString()}));
        }
    }
}
