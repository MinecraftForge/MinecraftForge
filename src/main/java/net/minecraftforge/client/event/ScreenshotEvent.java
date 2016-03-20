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
 *
 * {@link #screenshotFile} contains the file the screenshot will be/was saved to
 */
public class ScreenshotEvent extends Event
{

    protected File screenshotFile;

    public File getScreenshotFile() {
        return screenshotFile;
    }

    /**
     * This event is fired before a screenshot is taken
     * This event is {@link Cancelable}
     *
     * {@link #screenshotFile} contains the file the screenshot will be/was saved to
	 * {@link #image} contains the {@link BufferedImage} containing the screenshot
     * {@link #cancelReason} contains the {@link ITextComponent} to be used if the event is canceled
     */
    @Cancelable
    public static class Pre extends ScreenshotEvent
    {
		private BufferedImage image;
        private ITextComponent cancelReason = new TextComponentString("Screenshot canceled for unknown reason");

        public Pre(BufferedImage image, File screenshotFile)
        {
			this.image = image;
            this.screenshotFile = screenshotFile;
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

		public BufferedImage getImage() {
			return image;
		}

		public void setImage(BufferedImage image) {
			this.image = image;
		}
	}

    /**
     * This event is fired after a screenshot is taken
     * This event is <b>not</b> {@link Cancelable}
     *
     * {@link #screenshotFile} contains the file the screenshot will be/was saved to
     */
    public static class Post extends ScreenshotEvent
    {

        public Post(File screenshotFile)
        {
            this.screenshotFile = screenshotFile;
        }

    }

}
