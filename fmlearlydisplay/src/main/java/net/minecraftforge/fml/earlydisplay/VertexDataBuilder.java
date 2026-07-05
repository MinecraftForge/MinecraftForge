/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.earlydisplay;

import org.lwjgl.system.MemoryUtil;

import java.io.Closeable;
import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * Shared CPU-side vertex buffer building logic.
 * <p>
 * Backend-specific subclasses implement {@link #draw()} to upload and render the buffered vertex data.
 * <p>
 * This is a Triangles only buffer, all data uploaded is in Triangles.
 * Quads are converted to triangles using {@code 0, 1, 2, 0, 2, 3}.
 */
public abstract class VertexDataBuilder implements Closeable {
    private static final MemoryUtil.MemoryAllocator ALLOCATOR = MemoryUtil.getAllocator(false);

    protected long bufferAddr;
    protected ByteBuffer buffer;

    protected Format format;
    protected Mode mode;
    protected boolean building;
    protected int elementIndex;
    protected int index;
    protected int vertices;

    public VertexDataBuilder(int capacity) {
        bufferAddr = ALLOCATOR.malloc(capacity);
        buffer = MemoryUtil.memByteBuffer(bufferAddr, capacity);
    }

    public VertexDataBuilder begin(Format format, Mode mode) {
        if (bufferAddr == MemoryUtil.NULL) {
            throw new IllegalStateException("Buffer has been freed.");
        }
        if (building) {
            throw new IllegalStateException("Already building.");
        }
        this.format = format;
        this.mode = mode;
        building = true;
        elementIndex = 0;
        ensureSpace(format.stride);
        buffer.rewind();
        buffer.limit(buffer.capacity());
        return this;
    }

    public VertexDataBuilder pos(float x, float y) {
        if (!building) throw new IllegalStateException("Not building.");
        if (elementIndex == format.types.length) throw new IllegalStateException("Expected endVertex");
        if (format.types[elementIndex] != Element.POS) throw new IllegalArgumentException("Expected " + format.types[elementIndex]);

        buffer.putFloat(index + 0, x);
        buffer.putFloat(index + 4, y);

        index += format.types[elementIndex].width;
        elementIndex++;
        return this;
    }

    public VertexDataBuilder tex(float u, float v) {
        if (!building) throw new IllegalStateException("Not building.");
        if (elementIndex == format.types.length) throw new IllegalStateException("Expected endVertex");
        if (format.types[elementIndex] != Element.TEX) throw new IllegalArgumentException("Expected " + format.types[elementIndex]);

        buffer.putFloat(index + 0, u);
        buffer.putFloat(index + 4, v);

        index += format.types[elementIndex].width;
        elementIndex++;
        return this;
    }

    public VertexDataBuilder colour(float r, float g, float b, float a) {
        return colour((byte) (r * 255F), (byte) (g * 255F), (byte) (b * 255F), (byte) (a * 255F));
    }

    public VertexDataBuilder colour(int packedColor) {
        if (!building) throw new IllegalStateException("Not building.");
        if (elementIndex == format.types.length) throw new IllegalStateException("Expected endVertex");
        if (format.types[elementIndex] != Element.COLOR) throw new IllegalArgumentException("Expected " + format.types[elementIndex]);

        buffer.putInt(index + 0, packedColor);

        index += format.types[elementIndex].width;
        elementIndex++;
        return this;
    }

    public VertexDataBuilder colour(byte r, byte g, byte b, byte a) {
        if (!building) throw new IllegalStateException("Not building.");
        if (elementIndex == format.types.length) throw new IllegalStateException("Expected endVertex");
        if (format.types[elementIndex] != Element.COLOR) throw new IllegalArgumentException("Expected " + format.types[elementIndex]);

        buffer.put(index + 0, r);
        buffer.put(index + 1, g);
        buffer.put(index + 2, b);
        buffer.put(index + 3, a);

        index += format.types[elementIndex].width;
        elementIndex++;
        return this;
    }

    public VertexDataBuilder endVertex() {
        if (!building) throw new IllegalStateException("Not building.");
        if (elementIndex != format.types.length) throw new IllegalStateException("Expected " + format.types[elementIndex]);

        elementIndex = 0;
        vertices++;
        ensureSpace(format.stride);
        return this;
    }

    private void ensureSpace(int newBytes) {
        int cap = buffer.capacity();
        if (index + newBytes > cap) {
            int newCap = Math.max(3 * cap / 2, 3 * newBytes / 2);
            bufferAddr = ALLOCATOR.realloc(bufferAddr, newCap);
            buffer = MemoryUtil.memByteBuffer(bufferAddr, newCap);
            buffer.rewind();
        }
    }

    public abstract void draw();

    @Override
    public void close() {
        ALLOCATOR.free(bufferAddr);
        bufferAddr = MemoryUtil.NULL;
    }

    public enum Mode {
        TRIANGLES(3),
        QUADS(4),
        ;

        public final int vertices;

        Mode(int vertices) {
            this.vertices = vertices;
        }
    }

    public enum Element {
        POS(DataType.FLOAT, 2, 2 * 4),
        TEX(DataType.FLOAT, 2, 2 * 4),
        COLOR(DataType.UNORM_BYTE, 4, 4);

        public final DataType dataType;
        public final int count;
        public final int width;

        Element(DataType dataType, int count, int width) {
            this.dataType = dataType;
            this.count = count;
            this.width = width;
        }
    }

    public enum Format {
        POS(Element.POS),
        POS_TEX(Element.POS, Element.TEX),
        POS_COLOR(Element.POS, Element.COLOR),
        POS_TEX_COLOR(Element.POS, Element.TEX, Element.COLOR);

        final Element[] types;
        public final int stride;

        Format(Element... types) {
            this.types = types;
            stride = Arrays.stream(types).mapToInt(e -> e.width).sum();
        }
    }
}
