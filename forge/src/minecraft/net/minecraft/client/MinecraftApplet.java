package net.minecraft.client;

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Canvas;
import net.minecraft.src.CanvasMinecraftApplet;
import net.minecraft.src.MinecraftAppletImpl;
import net.minecraft.src.Session;

public class MinecraftApplet extends Applet
{
    /** Reference to the applet canvas. */
    private Canvas mcCanvas;

    /** Reference to the Minecraft object. */
    private Minecraft mc;

    /** Reference to the Minecraft main thread. */
    private Thread mcThread = null;

    public void init()
    {
        this.mcCanvas = new CanvasMinecraftApplet(this);
        boolean var1 = false;

        if (this.getParameter("fullscreen") != null)
        {
            var1 = this.getParameter("fullscreen").equalsIgnoreCase("true");
        }

        this.mc = new MinecraftAppletImpl(this, this, this.mcCanvas, this, this.getWidth(), this.getHeight(), var1);
        this.mc.minecraftUri = this.getDocumentBase().getHost();

        if (this.getDocumentBase().getPort() > 0)
        {
            this.mc.minecraftUri = this.mc.minecraftUri + ":" + this.getDocumentBase().getPort();
        }

        if (this.getParameter("username") != null && this.getParameter("sessionid") != null)
        {
            this.mc.session = new Session(this.getParameter("username"), this.getParameter("sessionid"));
            System.out.println("Setting user: " + this.mc.session.username + ", " + this.mc.session.sessionId);

            if (this.getParameter("mppass") != null)
            {
                this.mc.session.mpPassParameter = this.getParameter("mppass");
            }
        }
        else
        {
            this.mc.session = new Session("Player", "");
        }

        if (this.getParameter("server") != null && this.getParameter("port") != null)
        {
            this.mc.setServer(this.getParameter("server"), Integer.parseInt(this.getParameter("port")));
        }

        this.mc.hideQuitButton = true;

        if ("true".equals(this.getParameter("stand-alone")))
        {
            this.mc.hideQuitButton = false;
        }

        this.setLayout(new BorderLayout());
        this.add(this.mcCanvas, "Center");
        this.mcCanvas.setFocusable(true);
        this.validate();
    }

    public void startMainThread()
    {
        if (this.mcThread == null)
        {
            this.mcThread = new Thread(this.mc, "Minecraft main thread");
            this.mcThread.start();
        }
    }

    public void start()
    {
        if (this.mc != null)
        {
            this.mc.isGamePaused = false;
        }
    }

    public void stop()
    {
        if (this.mc != null)
        {
            this.mc.isGamePaused = true;
        }
    }

    public void destroy()
    {
        this.shutdown();
    }

    /**
     * Called when the applet window is closed.
     */
    public void shutdown()
    {
        if (this.mcThread != null)
        {
            this.mc.shutdown();

            try
            {
                this.mcThread.join(10000L);
            }
            catch (InterruptedException var4)
            {
                try
                {
                    this.mc.shutdownMinecraftApplet();
                }
                catch (Exception var3)
                {
                    var3.printStackTrace();
                }
            }

            this.mcThread = null;
        }
    }

    /**
     * Removes all the components from the applet and lays it out again. Called on shutdown.
     */
    public void clearApplet()
    {
        this.mcCanvas = null;
        this.mc = null;
        this.mcThread = null;

        try
        {
            this.removeAll();
            this.validate();
        }
        catch (Exception var2)
        {
            ;
        }
    }
}
