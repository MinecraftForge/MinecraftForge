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
 * {@link #resultMessage} contains the {@link ITextComponent} to be returned. If {@code null}, the default vanilla message will be used instead
 */
@Cancelable
public class ScreenshotEvent extends Event
{

    public static final ITextComponent DEFAULT_CANCEL_REASON = new TextComponentString("Screenshot canceled");

    private BufferedImage image;
    private File screenshotFile;

    private ITextComponent resultMessage = null;

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

    public ITextComponent getResultMessage()
    {
        return resultMessage;
    }

    public void setResultMessage(ITextComponent resultMessage)
    {
        this.resultMessage = resultMessage;
    }

    public ITextComponent getCancelMessage()
    {
        return getResultMessage() != null ? getResultMessage() : DEFAULT_CANCEL_REASON;
    }

}
