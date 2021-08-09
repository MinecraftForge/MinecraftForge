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

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Screenshot;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.LogicalSide;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;

/**
 * Fired when a screenshot is taken, but before it is written to disk.
 *
 * <p>This event is {@linkplain Cancelable cancelable}, and does not {@linkplain HasResult have a result}.
 * If this event is cancelled, then the screenshot is not written to disk, and the message in the event will be posted
 * to the player's chat.</p>
 *
 * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
 * only on the {@linkplain LogicalSide#CLIENT logical client}. </p>
 *
 * @see Screenshot
 */
@Cancelable
public class ScreenshotEvent extends Event
{
    public static final Component DEFAULT_CANCEL_REASON = new TextComponent("Screenshot canceled");

    private final NativeImage image;
    private File screenshotFile;

    @Nullable
    private Component resultMessage = null;

    public ScreenshotEvent(NativeImage image, File screenshotFile)
    {
        this.image = image;
        this.screenshotFile = screenshotFile;
        try {
            this.screenshotFile = screenshotFile.getCanonicalFile(); // FORGE: Fix errors on Windows with paths that include \.\
        } catch (IOException ignored) { // Ignore if we can't get the canonical file
        }
    }

    /**
     * {@return the in-memory image of the screenshot}
     */
    public NativeImage getImage()
    {
        return image;
    }

    /**
     * @return the filepath where the screenshot will be saved to
     */
    public File getScreenshotFile()
    {
        return screenshotFile;
    }

    /**
     * Sets the new filepath where the screenshot will be saved to.
     *
     * @param screenshotFile the new filepath
     */
    public void setScreenshotFile(File screenshotFile)
    {
        this.screenshotFile = screenshotFile;
    }

    /**
     * {@return the custom cancellation message, or {@code null} if no custom message is set}
     */
    @Nullable
    public Component getResultMessage()
    {
        return resultMessage;
    }

    /**
     * Sets the new custom cancellation message used to inform the player.
     * It may be {@code null}, in which case the {@link #DEFAULT_CANCEL_REASON default cancel reason} will be used.
     *
     * @param resultMessage the new result message
     */
    public void setResultMessage(@Nullable Component resultMessage)
    {
        this.resultMessage = resultMessage;
    }

    /**
     * Returns the cancellation message to be used in informing the player.
     *
     * <p>If there is no custom message given ({@link #getResultMessage()} returns {@code null}), then
     * the message will be the {@link #DEFAULT_CANCEL_REASON default cancel reason message}</p>
     *
     * @return the cancel message for the player
     */
    public Component getCancelMessage()
    {
        return getResultMessage() != null ? getResultMessage() : DEFAULT_CANCEL_REASON;
    }
}
