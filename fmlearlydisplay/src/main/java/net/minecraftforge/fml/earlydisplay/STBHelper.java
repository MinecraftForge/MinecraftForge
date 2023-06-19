/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.earlydisplay;

import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryUtil;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.util.Objects;

import static org.lwjgl.opengl.GL32C.*;

public class STBHelper {
    public static ByteBuffer readFromClasspath(final String name, int initialCapacity) {
        ByteBuffer buf;
        try (var channel = Channels.newChannel(
                Objects.requireNonNull(STBHelper.class.getClassLoader().getResourceAsStream(name), "The resource "+name+" cannot be found"))) {
            buf = BufferUtils.createByteBuffer(initialCapacity);
            while (true) {
                var readbytes = channel.read(buf);
                if (readbytes == -1) break;
                if (buf.remaining() == 0) { // extend the buffer by 50%
                    var newBuf = BufferUtils.createByteBuffer(buf.capacity() * 3 / 2);
                    buf.flip();
                    newBuf.put(buf);
                    buf = newBuf;
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        buf.flip();
        return MemoryUtil.memSlice(buf); // we trim the final buffer to the size of the content
    }

    public static int[] loadTextureFromClasspath(String file, int size, int textureNumber) {
        int[] lw = new int[1];
        int[] lh = new int[1];
        int[] lc = new int[1];
        var img = loadImageFromClasspath(file, size, lw, lh, lc);
        var texid = glGenTextures();
        glActiveTexture(textureNumber);
        glBindTexture(GL_TEXTURE_2D, texid);
//        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
//        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, lw[0], lh[0], 0, GL_RGBA, GL_UNSIGNED_BYTE, img);
        glActiveTexture(GL_TEXTURE0);
        MemoryUtil.memFree(img);
        return new int[] {lw[0], lh[0]};
    }

    public static ByteBuffer loadImageFromClasspath(String file, int size, int[] width, int[] height, int[] channels) {
        ByteBuffer buf = STBHelper.readFromClasspath(file, size);
        return STBImage.stbi_load_from_memory(buf, width, height, channels, 4);
    }
}
