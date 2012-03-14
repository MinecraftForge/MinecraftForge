package net.minecraft.isom;

import java.applet.Applet;
import java.awt.BorderLayout;
import net.minecraft.src.CanvasIsomPreview;

public class IsomPreviewApplet extends Applet
{
    private CanvasIsomPreview isomPreview = new CanvasIsomPreview();

    public IsomPreviewApplet()
    {
        this.setLayout(new BorderLayout());
        this.add(this.isomPreview, "Center");
    }

    public void start()
    {
        this.isomPreview.start();
    }

    public void stop()
    {
        this.isomPreview.stop();
    }
}
