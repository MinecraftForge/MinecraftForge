package net.minecraftforge.client.event;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

import java.awt.image.BufferedImage;
import java.io.File;

/**
 * This event is fired before and after a screenshot is taken
 * This event is fired on the {@link net.minecraftforge.common.MinecraftForge#EVENT_BUS}
 * This event is {@link Cancelable}
 *
 * {@link #screenshotFile} contains the file the screenshot will be/was saved to
 * {@link #image} contains the {@link BufferedImage} that will be saved
 * {@link #cancelReason} contains the {@link ITextComponent} to be used if the event is canceled
 */
@Cancelable
public class ScreenshotEvent extends Event
{

    private BufferedImage image;
    private File screenshotFile;

    private ITextComponent cancelReason = new TextComponentString("Screenshot canceled for unknown reason");

    public ScreenshotEvent(BufferedImage image, File screenshotFile)
    {
        this.image = image;
        this.screenshotFile = screenshotFile;
    }

    public BufferedImage getImage()
    {
        return image;
    }

    public File getScreenshotFile()
    {
        return screenshotFile;
    }

    public void setScreenshotFile(File screenshotFile)
    {
        this.screenshotFile = screenshotFile;
    }

    public ITextComponent getCancelReason()
    {
        return cancelReason;
    }

    public void setCancelReason(ITextComponent cancelReason)
    {
        this.cancelReason = cancelReason;
    }

}
