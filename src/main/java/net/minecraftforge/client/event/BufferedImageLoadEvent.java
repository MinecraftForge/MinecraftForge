/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
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

import java.awt.image.BufferedImage;

import javax.annotation.Nonnull;

import net.minecraftforge.fml.common.eventhandler.Event;

public class BufferedImageLoadEvent extends Event
{

    private final String resourceLocation;
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

    public String getResourceLocation()
    {
        return resourceLocation;
    }
}
