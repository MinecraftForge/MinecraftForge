/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.earlydisplay;

import static org.lwjgl.opengl.GL32C.*;

/**
 * OpenGL implementation of {@link VertexDataBuilder}.
 * <p>
 * Adds GL-specific vertex upload and draw via VAOs/VBOs.
 * The CPU-side buffer building logic is inherited from {@link VertexDataBuilder}.
 * <p>
 * This class can be removed when OpenGL support is dropped.
 */
public class SimpleBufferBuilder extends VertexDataBuilder {
    private static final int[] VERTEX_ARRAYS = new int[Format.values().length];
    private static final int[] VERTEX_BUFFERS = new int[Format.values().length];
    private static final int[] VERTEX_BUFFER_LENGTHS = new int[Format.values().length];
    private static int elementBuffer = 0;
    private static int elementBufferVertexLength = 0;

    static {
        java.util.Arrays.fill(VERTEX_ARRAYS, 0);
        java.util.Arrays.fill(VERTEX_BUFFERS, 0);
        java.util.Arrays.fill(VERTEX_BUFFER_LENGTHS, 0);
    }

    public SimpleBufferBuilder(int capacity) {
        super(capacity);
    }

    public static void destroy() {
        glDeleteBuffers(VERTEX_BUFFERS);
        glDeleteBuffers(elementBuffer);
        glDeleteVertexArrays(VERTEX_ARRAYS);
    }

    private static void ensureElementBufferLength(int vertices) {
        if (elementBufferVertexLength >= vertices) {
            return;
        }

        final var newElementBuffer = glGenBuffers();
        var newElementBufferVertexLength = Math.max(1024, elementBufferVertexLength);
        while (newElementBufferVertexLength < vertices) {
            newElementBufferVertexLength *= 2;
        }

        final var oldIndexCount = elementBufferVertexLength + elementBufferVertexLength / 2;
        final var newIndexCount = newElementBufferVertexLength + newElementBufferVertexLength / 2;

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, newElementBuffer);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, newIndexCount * 4L, GL_STATIC_DRAW);

        final var mappingOffset = oldIndexCount * 4;
        final var mappingSize = (newIndexCount - oldIndexCount) * 4;
        final var mappedBuffer = glMapBufferRange(GL_ELEMENT_ARRAY_BUFFER, mappingOffset, mappingSize, GL_MAP_WRITE_BIT | GL_MAP_UNSYNCHRONIZED_BIT | GL_MAP_INVALIDATE_BUFFER_BIT);

        if (mappedBuffer == null) {
            throw new NullPointerException("OpenGL buffer mapping failed");
        }

        final int quads = newElementBufferVertexLength / 4;
        final int oldQuads = elementBufferVertexLength / 4;
        for (int i = oldQuads; i < quads; i++) {
            mappedBuffer.putInt(i * 4 + 0).putInt(i * 4 + 1).putInt(i * 4 + 2);
            mappedBuffer.putInt(i * 4 + 1).putInt(i * 4 + 3).putInt(i * 4 + 2);
        }

        glUnmapBuffer(GL_ELEMENT_ARRAY_BUFFER);

        if (elementBuffer != 0) {
            glBindBuffer(GL_COPY_READ_BUFFER, elementBuffer);
            glCopyBufferSubData(GL_COPY_READ_BUFFER, GL_ELEMENT_ARRAY_BUFFER, 0, 0, mappingOffset);
            glBindBuffer(GL_COPY_READ_BUFFER, 0);
        }
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);

        glDeleteBuffers(elementBuffer);
        elementBuffer = newElementBuffer;
        elementBufferVertexLength = newElementBufferVertexLength;
    }

    public int finishAndUpload() {
        if (!building) throw new IllegalStateException("Not building.");

        int indices;
        try {
            if (elementIndex == format.types.length) throw new IllegalStateException("Expected endVertex");
            if (elementIndex != 0) throw new IllegalStateException("Not finished building vertex, Expected: " + format.types[elementIndex]);
            if (vertices == 0) return 0;
            if (vertices % mode.vertices != 0) throw new IllegalStateException("Does not contain vertices aligned to " + mode);

            buffer.position(0);
            buffer.limit(index);

            final int vbo = VERTEX_BUFFERS[format.ordinal()];
            final int vboSize = VERTEX_BUFFER_LENGTHS[format.ordinal()];
            glBindBuffer(GL_ARRAY_BUFFER, vbo);
            if (vboSize < index) {
                var newVBOSize = Math.max(1024, vboSize);
                while (newVBOSize < index) {
                    newVBOSize *= 2;
                }
                glBufferData(GL_ARRAY_BUFFER, newVBOSize, GL_DYNAMIC_DRAW);
                VERTEX_BUFFER_LENGTHS[format.ordinal()] = newVBOSize;
            }
            glBufferSubData(GL_ARRAY_BUFFER, 0, buffer);

            indices = mode == Mode.TRIANGLES ? vertices : vertices + vertices / 2;

            if (mode == Mode.QUADS) {
                ensureElementBufferLength(vertices);
                glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, elementBuffer);
            }

            return indices;
        } finally {
            building = false;
            vertices = 0;
            index = 0;
        }
    }

    @Override
    public void draw() {
        if (!building) throw new IllegalStateException("Not building.");

        int vao = VERTEX_ARRAYS[format.ordinal()];
        int vbo = VERTEX_BUFFERS[format.ordinal()];

        if (vao == 0) {
            assert vbo == 0;

            vao = glGenVertexArrays();
            vbo = glGenBuffers();

            VERTEX_ARRAYS[format.ordinal()] = vao;
            VERTEX_BUFFERS[format.ordinal()] = vbo;

            glBindVertexArray(vao);
            glBindBuffer(GL_ARRAY_BUFFER, vbo);
            bindFormat(format);
            enableFormat(format);
        }
        glBindVertexArray(vao);

        int indices = finishAndUpload();

        if (mode == Mode.QUADS) {
            glDrawElements(GL_TRIANGLES, indices, GL_UNSIGNED_INT, 0);
        } else {
            glDrawArrays(GL_TRIANGLES, 0, indices);
        }

        glBindVertexArray(0);
    }

    private void bindFormat(Format fmt) {
        int offset = 0;
        for (int i = 0; i < fmt.types.length; i++) {
            Element type = fmt.types[i];
            switch (type.dataType) {
                case FLOAT -> glVertexAttribPointer(i, type.count, GL_FLOAT, false, fmt.stride, offset);
                case UNORM_BYTE -> glVertexAttribPointer(i, type.count, GL_UNSIGNED_BYTE, true, fmt.stride, offset);
            }
            offset += type.width;
        }
    }

    private void enableFormat(Format fmt) {
        for (int i = 0; i < fmt.types.length; i++) {
            glEnableVertexAttribArray(i);
        }
    }
}
