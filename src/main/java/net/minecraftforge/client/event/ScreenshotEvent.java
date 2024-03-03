/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.event;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
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
 * {@link #resultMessage} contains the {@link Component} to be returned. If {@code null}, the default vanilla message will be used instead
 */
@Cancelable
public class ScreenshotEvent extends Event
{

    public static final Component DEFAULT_CANCEL_REASON = new TextComponent("Screenshot canceled");

    private NativeImage image;
    private File screenshotFile;

    private Component resultMessage = null;

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

    public Component getResultMessage()
    {
        return resultMessage;
    }

    public void setResultMessage(Component resultMessage)
    {
        this.resultMessage = resultMessage;
    }

    public Component getCancelMessage()
    {
        return getResultMessage() != null ? getResultMessage() : DEFAULT_CANCEL_REASON;
    }

}
