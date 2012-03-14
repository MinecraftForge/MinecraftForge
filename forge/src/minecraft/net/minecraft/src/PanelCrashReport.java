package net.minecraft.src;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Panel;
import java.awt.TextArea;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import org.lwjgl.Sys;
import org.lwjgl.opengl.GL11;

public class PanelCrashReport extends Panel
{
    public PanelCrashReport(UnexpectedThrowable par1UnexpectedThrowable)
    {
        this.setBackground(new Color(3028036));
        this.setLayout(new BorderLayout());
        StringWriter var2 = new StringWriter();
        par1UnexpectedThrowable.exception.printStackTrace(new PrintWriter(var2));
        String var3 = var2.toString();
        String var4 = "";
        String var5 = "";

        try
        {
            var5 = var5 + "Generated " + (new SimpleDateFormat()).format(new Date()) + "\n";
            var5 = var5 + "\n";
            var5 = var5 + "Minecraft: Minecraft 1.2.3\n";
            var5 = var5 + "OS: " + System.getProperty("os.name") + " (" + System.getProperty("os.arch") + ") version " + System.getProperty("os.version") + "\n";
            var5 = var5 + "Java: " + System.getProperty("java.version") + ", " + System.getProperty("java.vendor") + "\n";
            var5 = var5 + "VM: " + System.getProperty("java.vm.name") + " (" + System.getProperty("java.vm.info") + "), " + System.getProperty("java.vm.vendor") + "\n";
            var5 = var5 + "LWJGL: " + Sys.getVersion() + "\n";
            var4 = GL11.glGetString(GL11.GL_VENDOR);
            var5 = var5 + "OpenGL: " + GL11.glGetString(GL11.GL_RENDERER) + " version " + GL11.glGetString(GL11.GL_VERSION) + ", " + GL11.glGetString(GL11.GL_VENDOR) + "\n";
        }
        catch (Throwable var9)
        {
            var5 = var5 + "[failed to get system properties (" + var9 + ")]\n";
        }

        var5 = var5 + "\n";
        var5 = var5 + var3;
        String var6 = "";
        var6 = var6 + "Mods loaded: " + (ModLoader.getLoadedMods().size() + 1) + "\n";
        var6 = var6 + "ModLoader 1.2.3" + "\n";
        BaseMod var7;

        for (Iterator var8 = ModLoader.getLoadedMods().iterator(); var8.hasNext(); var6 = var6 + var7.getName() + " " + var7.getVersion() + "\n")
        {
            var7 = (BaseMod)var8.next();
        }

        var6 = var6 + "\n";

        if (var3.contains("Pixel format not accelerated"))
        {
            var6 = var6 + "      Bad video card drivers!      \n";
            var6 = var6 + "      -----------------------      \n";
            var6 = var6 + "\n";
            var6 = var6 + "Minecraft was unable to start because it failed to find an accelerated OpenGL mode.\n";
            var6 = var6 + "This can usually be fixed by updating the video card drivers.\n";

            if (var4.toLowerCase().contains("nvidia"))
            {
                var6 = var6 + "\n";
                var6 = var6 + "You might be able to find drivers for your video card here:\n";
                var6 = var6 + "  http://www.nvidia.com/\n";
            }
            else if (var4.toLowerCase().contains("ati"))
            {
                var6 = var6 + "\n";
                var6 = var6 + "You might be able to find drivers for your video card here:\n";
                var6 = var6 + "  http://www.amd.com/\n";
            }
        }
        else
        {
            var6 = var6 + "      Minecraft has crashed!      \n";
            var6 = var6 + "      ----------------------      \n";
            var6 = var6 + "\n";
            var6 = var6 + "Minecraft has stopped running because it encountered a problem.\n";
            var6 = var6 + "\n";
        }

        var6 = var6 + "\n";
        var6 = var6 + "\n";
        var6 = var6 + "\n";
        var6 = var6 + "--- BEGIN ERROR REPORT " + Integer.toHexString(var6.hashCode()) + " --------\n";
        var6 = var6 + var5;
        var6 = var6 + "--- END ERROR REPORT " + Integer.toHexString(var6.hashCode()) + " ----------\n";
        var6 = var6 + "\n";
        var6 = var6 + "\n";
        TextArea var10 = new TextArea(var6, 0, 0, 1);
        var10.setFont(new Font("Monospaced", 0, 12));
        this.add(new CanvasMojangLogo(), "North");
        this.add(new CanvasCrashReport(80), "East");
        this.add(new CanvasCrashReport(80), "West");
        this.add(new CanvasCrashReport(100), "South");
        this.add(var10, "Center");
    }
}
