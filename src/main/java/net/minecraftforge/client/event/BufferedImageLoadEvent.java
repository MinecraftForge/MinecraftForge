package net.minecraftforge.client.event;

import java.awt.image.BufferedImage;

import javax.annotation.Nonnull;

import net.minecraftforge.fml.common.eventhandler.Event;

public class BufferedImageLoadEvent extends Event
{

    public final String resourceLocation;
    private BufferedImage result;

    public BufferedImageLoadEvent(String s)
    {
        resourceLocation = s;
    }

    public BufferedImage getResultBufferedImage()
    {
        return result;
    }

    public void setResultBufferedImage(@Nonnull BufferedImage result)
    {
        this.result = result;
    }
}
