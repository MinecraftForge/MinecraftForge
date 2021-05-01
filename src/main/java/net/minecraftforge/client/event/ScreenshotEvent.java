/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.client.event;

import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

import java.io.File;
import java.io.IOException;

/**
 * This event is fired before and after a screenshot is taken
 * This event is fired on the {@link net.minecraftforge.common.MinecraftForge#EVENT_BUS}
 * This event is {@link Cancelable}
 *
 * {@link #screenshotFile} contains the file the screenshot will be/was saved to
 * {@link #image} contains the {@link NativeImage} that will be saved
 * {@link #resultMessage} contains the {@link ITextComponent} to be returned. If {@code null}, the default vanilla message will be used instead
 */
@Cancelable
public class ScreenshotEvent extends Event
{

    public static final ITextComponent DEFAULT_CANCEL_REASON = new StringTextComponent("Screenshot canceled");

    private NativeImage image;
    private File screenshotFile;

    private ITextComponent resultMessage = null;

    public ScreenshotEvent(NativeImage image, File screenshotFile)
    {
        this.image = image;
        this.screenshotFile = screenshotFile;
        try {
            this.screenshotFile = screenshotFile.getCanonicalFile(); // FORGE: Fix errors on Windows with paths that include \.\
        } catch (IOException e) {}
    }

    public NativeImage getImage()
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
