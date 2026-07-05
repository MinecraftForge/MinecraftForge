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

/**
 * Shared IO and STB image decoding utilities.
 * Backend-specific texture upload is handled by {@link BaseRenderBackend}.
 */
public class STBHelper {
    public static ByteBuffer readFromClasspath(final String name, int initialCapacity) {
        ByteBuffer buf;
        try (var channel = Channels.newChannel(
                Objects.requireNonNull(STBHelper.class.getClassLoader().getResourceAsStream(name), "The resource "+name+" cannot be found"))) {
            buf = BufferUtils.createByteBuffer(initialCapacity);
            while (true) {
                var readbytes = channel.read(buf);
                if (readbytes == -1) break;
                if (buf.remaining() == 0) {
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
        return MemoryUtil.memSlice(buf);
    }

    public static ByteBuffer loadImageFromClasspath(String file, int size, int[] width, int[] height, int[] channels) {
        ByteBuffer buf = STBHelper.readFromClasspath(file, size);
        return STBImage.stbi_load_from_memory(buf, width, height, channels, 4);
    }
}
