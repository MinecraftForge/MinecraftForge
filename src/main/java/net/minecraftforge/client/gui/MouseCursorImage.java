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

package net.minecraftforge.client.gui;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.NativeImage.Format;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.system.MemoryUtil;
import java.io.IOException;

/**
 * Helper class to manage cursor image.
 */
public class MouseCursorImage
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static final long[] STANDARD_CURSORS = new long[StandardCursor.values().length];

    private static long lastCursorAddress = 0;

    /**
     * Sets cursor image using given resource location.
     *
     * @param rl image resource location
     * @return cursor texture reference
     */
    public static CursorTexture setCursorImage(final ResourceLocation rl)
    {
        AbstractTexture texture = Minecraft.getInstance().getTextureManager().getTexture(rl);
        if (texture == null || !(texture instanceof CursorTexture))
        {
            texture = new CursorTexture(rl);
            Minecraft.getInstance().getTextureManager().register(rl, texture);
        }

        final CursorTexture cursorTexture = (CursorTexture) texture;
        cursorTexture.setCursor();
        return cursorTexture;
    }

    /**
     * Sets cursor image to standard shapes provided by GLFW.
     * 
     * @param shape cursor shape
     * @see MouseCursorImage.StandardCursor
     */
    public static void setStandardCursor(final StandardCursor shape)
    {
        final int ord = shape.ordinal();

        if (STANDARD_CURSORS[ord] == 0)
        {
            RenderSystem.assertThread(RenderSystem::isOnRenderThread);
            STANDARD_CURSORS[ord] = GLFW.glfwCreateStandardCursor(shape.glfwValue);
            if (STANDARD_CURSORS[ord] == 0)
            {
                LOGGER.error("Cannot create standard cursor for shape: " + shape);
                return;
            }
        }

        setCursorAddress(STANDARD_CURSORS[ord]);
    }

    /**
     * Sets cursor image to default (usually arrow).
     */
    public static void resetCursor()
    {
        setCursorAddress(0);
    }

    /**
     * Sets cursor image address. If null (zero), cursor is reset to default (usually arrow).
     * 
     * @param cursorAddress cursor handle address or null
     */
    public static void setCursorAddress(final long cursorAddress)
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        if (cursorAddress != lastCursorAddress)
        {
            GLFW.glfwSetCursor(Minecraft.getInstance().getWindow().getWindow(), cursorAddress);
            lastCursorAddress = cursorAddress;
        }
    }

    /**
     * Enum represing all possible cursors defined by GLFW
     */
    public enum StandardCursor
    {
        ARROW(GLFW.GLFW_ARROW_CURSOR),
        IBEAM(GLFW.GLFW_IBEAM_CURSOR),
        CROSSHAIR(GLFW.GLFW_CROSSHAIR_CURSOR),
        HAND(GLFW.GLFW_HAND_CURSOR),
        HRESIZE(GLFW.GLFW_HRESIZE_CURSOR),
        VRESIZE(GLFW.GLFW_VRESIZE_CURSOR);

        private final int glfwValue;

        private StandardCursor(final int glfwValue)
        {
            this.glfwValue = glfwValue;
        }
    }

    /**
     * Used for textured cursors.
     *
     * @see MouseCursorImage#setCursorImage(ResourceLocation, int, int)
     */
    public static class CursorTexture extends SimpleTexture
    {
        private int hotspotX = 0;
        private int hotspotY = 0;
        private NativeImage nativeImage = null;
        private long glfwCursorAddress = 0;

        private CursorTexture(final ResourceLocation resLoc)
        {
            super(resLoc);
        }

        /**
         * Sets cursor hotspot. Hotspot is position in the image which should be used as 0,0 when rendering the
         * cursor (eg. image with 24x24 resolution will be centered on mouse point with hotspot 12x12).
         * 
         * @param x hotspot left offset
         * @param y hotspot top offset
         */
        public void setHotspot(final int x, final int y)
        {
            if (hotspotX != x || hotspotY != y)
            {
                hotspotX = x;
                hotspotY = y;
                onDataChange();
            }
        }

        /**
         * @return true if this is current cursor image, false otherwise
         */
        public boolean isCursorNow()
        {
            return MouseCursorImage.lastCursorAddress == glfwCursorAddress && glfwCursorAddress != 0;
        }

        private void onDataChange()
        {
            if (!RenderSystem.isOnRenderThread())
            {
                RenderSystem.recordRenderCall(this::onDataChange);
                return;
            }

            if (isCursorNow())
            {
                destroyCursorHandle();
                setCursor();
            }
            else
            {
                destroyCursorHandle();
            }
        }

        private void destroyCursorHandle()
        {
            if (glfwCursorAddress != 0)
            {
                RenderSystem.assertThread(RenderSystem::isOnRenderThread);
                if (isCursorNow())
                {
                    MouseCursorImage.resetCursor();
                }

                GLFW.glfwDestroyCursor(glfwCursorAddress);
                glfwCursorAddress = 0;
            }
        }

        /**
         * Sets this texture as cursor image. Resets to default if anything went wrong during setup of this texture.
         */
        public void setCursor()
        {
            if (glfwCursorAddress == 0 && nativeImage != null)
            {
                RenderSystem.assertThread(RenderSystem::isOnRenderThread);
                try (final GLFWImage image = GLFWImage.mallocStack())
                {
                    image.width(nativeImage.getWidth());
                    image.height(nativeImage.getHeight());
                    MemoryUtil.memPutAddress(image.address() + GLFWImage.PIXELS, nativeImage.pixels);
                    glfwCursorAddress = GLFW.glfwCreateCursor(image, hotspotX, hotspotY);
                }

                if (glfwCursorAddress == 0)
                {
                    LOGGER.error("Cannot create textured cursor for resource location: " + location);
                }
            }

            if (glfwCursorAddress != 0)
            {
                MouseCursorImage.setCursorAddress(glfwCursorAddress);
            }
            else
            {
                MouseCursorImage.resetCursor();
            }
        }

        @Override
        public void load(final ResourceManager resourceManager) throws IOException
        {
            if (nativeImage != null)
            {
                close();
            }

            nativeImage = getTextureImage(resourceManager).getImage();
            if (nativeImage.format() != Format.RGBA)
            {
                LOGGER.error("Cannot load texture for cursor as it is not in RGBA format, resource location: " + location);
                close();
            }
            onDataChange();
        }

        @Override
        public void close()
        {
            destroyCursorHandle();
            if (nativeImage != null)
            {
                nativeImage.close();
                nativeImage = null;
            }
        }
    }
}
