/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.earlydisplay;

import org.lwjgl.system.MemoryUtil;

import java.io.Closeable;
import java.nio.ByteBuffer;
import java.util.Arrays;

import static org.lwjgl.opengl.GL32C.*;

/**
 * A very simple, Mojang inspired BufferBuilder.
 * <em>This has been customized for 2d rendering such as text and simple planar textures</em>
 * <p>
 * Not bound to any specific format, ideally should be held onto for re-use.
 * <p>
 * Can be used for 'immediate mode' style rendering using {@link #draw()}, or
 * upload to external vertex arrays for proper instancing using {@link #finishAndUpload()}.
 * <p>
 * This is a Triangles only buffer, all data uploaded is in Triangles.
 * Quads are converted to triangles using {@code 0, 1, 2, 0, 2, 3}.
 * <p>
 * Any given {@link Format} should have its individual {@link Element} components
 * buffered in the order specified by the {@link Format},
 * followed by an {@link #endVertex()} call to prepare for the next vertex.
 * <p>
 * It is illegal to buffer primitives in any format other than the one specified to
 * {@link #begin(Format, Mode)}.
 *
 * @author covers1624
 */
public class SimpleBufferBuilder implements Closeable {
    
    private static final MemoryUtil.MemoryAllocator ALLOCATOR = MemoryUtil.getAllocator(false);
    
    private static final int[] VERTEX_ARRAYS = new int[Format.values().length];
    private static final int[] VERTEX_BUFFERS = new int[Format.values().length];
    private static final int[] VERTEX_BUFFER_LENGTHS = new int[Format.values().length];
    private static int elementBuffer = 0;
    private static int elementBufferVertexLength = 0;
    
    static {
        Arrays.fill(VERTEX_ARRAYS, 0);
        Arrays.fill(VERTEX_BUFFERS, 0);
        Arrays.fill(VERTEX_BUFFER_LENGTHS, 0);
    }
    
    private long bufferAddr;   // Pointer to the backing buffer.
    private ByteBuffer buffer; // ByteBuffer view of the backing buffer.
    
    private Format format;     // The current format we are buffering.
    private Mode mode;         // The current mode we are buffering.
    private boolean building;  // If we are building the buffer.
    private int elementIndex;  // The current element index we are buffering. if elementIndex == format.types.length, we expect 'endVertex'
    private int index;         // The current index into the buffer we are writing to.
    private int vertices;      // The number of complete vertices we have buffered.

    /**
     * Create a new SimpleBufferBuilder with an initial capacity.
     * <p>
     * The buffer will be doubled as required.
     * <p>
     * Generally picking a small number, around 128/256 should be a
     * safe bet. Provided you cache your buffers, it should not mean much overall.
     *
     * @param capacity The initial capacity in bytes.
     */
    public SimpleBufferBuilder(int capacity) {
        bufferAddr = ALLOCATOR.malloc(capacity);
        buffer = MemoryUtil.memByteBuffer(bufferAddr, capacity);
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
        
        // treating it as immutable storage, even though it's not
        final var newElementBuffer = glGenBuffers();
        var newElementBufferVertexLength = Math.max(1024, elementBufferVertexLength);
        while (newElementBufferVertexLength < vertices) {
            newElementBufferVertexLength *= 2;
        }
        
        final var oldIndexCount = elementBufferVertexLength + elementBufferVertexLength / 2;
        final var newIndexCount = newElementBufferVertexLength + newElementBufferVertexLength / 2;
        
        // allocate new buffer
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, newElementBuffer);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, newIndexCount * 4L, GL_STATIC_DRAW);
        
        // mapping avoids creating additional CPU copies of the data
        // unsynchronized is fine because this is a brand-new buffer, and the old contents will be copied in afterward
        // also can invalidate the whole buffer too, similarly because brand new, don't care what was there before
        final var mappingOffset = oldIndexCount * 4;
        final var mappingSize = (newIndexCount - oldIndexCount) * 4;
        final var mappedBuffer = glMapBufferRange(GL_ELEMENT_ARRAY_BUFFER, mappingOffset, mappingSize, GL_MAP_WRITE_BIT | GL_MAP_UNSYNCHRONIZED_BIT | GL_MAP_INVALIDATE_BUFFER_BIT);

        if(mappedBuffer == null){
            throw new NullPointerException("OpenGL buffer mapping failed");
        }
        
        final int quads = newElementBufferVertexLength / 4;
        final int oldQuads = elementBufferVertexLength / 4;
        // generate indices for the extension to the buffer
        for (int i = oldQuads; i < quads; i++) {
            // Quads are a bit different, we need to emit 2 triangles such that
            // when combined they make up a single quad.
            mappedBuffer.putInt(i * 4 + 0).putInt(i * 4 + 1).putInt(i * 4 + 2);
            mappedBuffer.putInt(i * 4 + 1).putInt(i * 4 + 3).putInt(i * 4 + 2);
        }

        glUnmapBuffer(GL_ELEMENT_ARRAY_BUFFER);

        if (elementBuffer != 0) {
            // copy old data from previous element buffer
            glBindBuffer(GL_COPY_READ_BUFFER, elementBuffer);
            glCopyBufferSubData(GL_COPY_READ_BUFFER, GL_ELEMENT_ARRAY_BUFFER, 0, 0, mappingOffset);
            glBindBuffer(GL_COPY_READ_BUFFER, 0);
        }
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
        
        glDeleteBuffers(elementBuffer);
        elementBuffer = newElementBuffer;
        elementBufferVertexLength = newElementBufferVertexLength;
    }
    
    /**
     * Start building a new set of vertex data in the
     * given format and mode.
     *
     * @param format The format to start building in.
     * @param mode   The mode to start building in.
     */
    public SimpleBufferBuilder begin(Format format, Mode mode) {
        if (bufferAddr == MemoryUtil.NULL) {
            throw new IllegalStateException("Buffer has been freed."); // You already free'd the buffer
        }
        if (building) {
            throw new IllegalStateException("Already building."); // Your already building verticies.
        }
        this.format = format;
        this.mode = mode;
        building = true;
        elementIndex = 0;
        ensureSpace(format.stride);
        // Rewind ready for new data.
        buffer.rewind();
        buffer.limit(buffer.capacity());
        return this;
    }

    /**
     * Buffer a position element.
     *
     * @param x The x.
     * @param y The y.
     * @param z The z.
     * @return The same builder.
     */
    public SimpleBufferBuilder pos(float x, float y) {
        if (!building) throw new IllegalStateException("Not building."); // You did not call begin.

        if (elementIndex == format.types.length) throw new IllegalStateException("Expected endVertex"); // we have reached the end of elements to buffer for this vertex, we expected an endVertex call.
        if (format.types[elementIndex] != Element.POS) throw new IllegalArgumentException("Expected " + format.types[elementIndex]); // You called the wrong method for the format order.

        // Assumes that our POS element specifies the FLOAT data type.
        buffer.putFloat(index + 0, x);
        buffer.putFloat(index + 4, y);

        // Increment index for the number of bytes we wrote and increment the element index.
        index += format.types[elementIndex].width;
        elementIndex++;
        return this;
    }

    /**
     * Buffer a texture element.
     *
     * @param u The u.
     * @param v The v.
     * @return The same builder.
     */
    public SimpleBufferBuilder tex(float u, float v) {
        if (!building) throw new IllegalStateException("Not building."); // You did not call begin.

        if (elementIndex == format.types.length) throw new IllegalStateException("Expected endVertex"); // we have reached the end of elements to buffer for this vertex, we expected an endVertex call.
        if (format.types[elementIndex] != Element.TEX) throw new IllegalArgumentException("Expected " + format.types[elementIndex]); // You called the wrong method for the format order.

        // Assumes our TEX element specifies the FLOAT data type.
        buffer.putFloat(index + 0, u);
        buffer.putFloat(index + 4, v);

        // Increment index for the number of bytes we wrote and increment the element index.
        index += format.types[elementIndex].width;
        elementIndex++;
        return this;
    }

    /**
     * Buffer a color element.
     *
     * @param r The red component. (0-1)
     * @param g The green component. (0-1)
     * @param b The blue component. (0-1)
     * @param a The alpha component. (0-1)
     * @return The same buffer.
     */
    public SimpleBufferBuilder colour(float r, float g, float b, float a) {
        // Expand floats to 0-255 and forward.
        return colour((byte) (r * 255F), (byte) (g * 255F), (byte) (b * 255F), (byte) (a * 255F));
    }

    /**
     * @see ColourScheme.Colour#packedint(int)
     * @param packedColor an ABGR packed int
     * @return the same buffer.
     */
    public SimpleBufferBuilder colour(int packedColor) {
        if (!building) throw new IllegalStateException("Not building."); // You did not call begin.

        if (elementIndex == format.types.length) throw new IllegalStateException("Expected endVertex"); // we have reached the end of elements to buffer for this vertex, we expected an endVertex call.
        if (format.types[elementIndex] != Element.COLOR) throw new IllegalArgumentException("Expected " + format.types[elementIndex]); // You called the wrong method for the format order.

        // Assumes our COLOR element specifies the UNSIGNED_BYTE data type.
        buffer.putInt(index + 0, packedColor);

        // Increment index for the number of bytes we wrote and increment the element index.
        index += format.types[elementIndex].width;
        elementIndex++;
        return this;
    }
    /**
     * Buffer a color element.
     *
     * @param r The red component. (0-255)
     * @param g The green component. (0-255)
     * @param b The blue component. (0-255)
     * @param a The alpha component. (0-255)
     * @return The same buffer.
     */
    public SimpleBufferBuilder colour(byte r, byte g, byte b, byte a) {
        if (!building) throw new IllegalStateException("Not building."); // You did not call begin.

        if (elementIndex == format.types.length) throw new IllegalStateException("Expected endVertex"); // we have reached the end of elements to buffer for this vertex, we expected an endVertex call.
        if (format.types[elementIndex] != Element.COLOR) throw new IllegalArgumentException("Expected " + format.types[elementIndex]); // You called the wrong method for the format order.

        // Assumes our COLOR element specifies the UNSIGNED_BYTE data type.
        buffer.put(index + 0, r);
        buffer.put(index + 1, g);
        buffer.put(index + 2, b);
        buffer.put(index + 3, a);

        // Increment index for the number of bytes we wrote and increment the element index.
        index += format.types[elementIndex].width;
        elementIndex++;
        return this;
    }

    /**
     * End building the current vertex and prepare for the next.
     *
     * @return The same builder.
     */
    public SimpleBufferBuilder endVertex() {
        if (!building) throw new IllegalStateException("Not building."); // You did not call begin.

        if (elementIndex != format.types.length) throw new IllegalStateException("Expected " + format.types[elementIndex]); // You did not finish building the vertex.

        // Reset elementIndex
        elementIndex = 0;
        // Increment the number of vertices we have so far buffered.
        vertices++;
        // Make sure there is space for the next vertex.
        ensureSpace(format.stride);
        return this;
    }

    // Checks there is enough space in the buffer for specified number of bytes.
    // If there is not enough space, the buffer is increased by 50%.
    private void ensureSpace(int newBytes) {
        int cap = buffer.capacity();
        if (index + newBytes > cap) {
            int newCap = Math.max(3 * cap / 2, 3 * newBytes / 2);
            bufferAddr = ALLOCATOR.realloc(bufferAddr, newCap);
            buffer = MemoryUtil.memByteBuffer(bufferAddr, newCap);
            buffer.rewind();
        }
    }

    /**
     * Upload the current buffer.
     * <p>
     * This will bind a {@link org.lwjgl.opengl.GL32C#GL_ARRAY_BUFFER} and {@link org.lwjgl.opengl.GL32C#GL_ELEMENT_ARRAY_BUFFER}
     * <p>
     * The vertex data and index data is uploaded to their respective buffers.
     * <p>
     * Uploading the buffers finishes drawing and resets for the next buffer operation.
     * <p>
     * This should not be called in conjunction with {@link #draw()}
     *
     * @return The number of indexes that were uploaded.
     */
    public int finishAndUpload() {
        if (!building) throw new IllegalStateException("Not building.");

        int indices;
        try {
            if (elementIndex == format.types.length) throw new IllegalStateException("Expected endVertex"); // You didn't finish building your vertex.
            if (elementIndex != 0) throw new IllegalStateException("Not finished building vertex, Expected: " + format.types[elementIndex]); // You didn't finish building your vertex data.
            if (vertices == 0) return 0; // No vertices buffered, lets not do anything.
            if (vertices % mode.vertices != 0) throw new IllegalStateException("Does not contain vertices aligned to " + mode); // You did not put in enough vertices to cleanly slice the data into TRIANGLES/QUADS

            // Reset position to 0, limit the buffer to our index.
            buffer.position(0);
            buffer.limit(index);
            
            // Upload the raw vertex data in dynamic mode.
            final int vbo = VERTEX_BUFFERS[format.ordinal()];
            final int vboSize = VERTEX_BUFFER_LENGTHS[format.ordinal()];
            glBindBuffer(GL_ARRAY_BUFFER, vbo);
            if (vboSize < index) {
                // expand buffer, it's not big enough
                var newVBOSize = Math.max(1024, vboSize);
                while (newVBOSize < index){
                    newVBOSize *= 2;
                }
                // because everything is overwritten anyway, we can do an in-place reallocation
                glBufferData(GL_ARRAY_BUFFER, newVBOSize, GL_DYNAMIC_DRAW);
                VERTEX_BUFFER_LENGTHS[format.ordinal()] = newVBOSize;
            }
            glBufferSubData(GL_ARRAY_BUFFER, 0, buffer);
            
            // The number of indices for triangles is equal to our vertex count, as that is
            // what we operate in. However, for Quads, we have exactly vertices + vertices / 2
            // vertices once we convert the quads to triangles.
            indices = mode == Mode.TRIANGLES ? vertices : vertices + vertices / 2;
            
            if (mode == Mode.QUADS) {
                ensureElementBufferLength(vertices);
                glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, elementBuffer);
            }
            
            return indices;
        } finally {
            // Reset builder state for next begin call.
            building = false;
            vertices = 0;
            index = 0;
        }
    }

    /**
     * Upload and draw this buffer using one of a number of re-usable set of buffers.
     * <p>
     * This will immediately upload the buffer, resetting this builder for the next
     * buffer operation, and draw the uploaded data.
     * <p>
     * You will need to bind shaders, textures, etc, before calling this function.
     */
    public void draw() {
        if (!building) throw new IllegalStateException("Not building.");

        int vao = VERTEX_ARRAYS[format.ordinal()];
        int vbo = VERTEX_BUFFERS[format.ordinal()];

        if (vao == 0) {
            // These 3 buffers are paired, you can't allocate one without the others.
            assert vbo == 0;

            // Make new vertex array and buffers!
            vao = glGenVertexArrays();
            vbo = glGenBuffers();
            
            // Cache the vertex array and buffers for future re-use.
            VERTEX_ARRAYS[format.ordinal()] = vao;
            VERTEX_BUFFERS[format.ordinal()] = vbo;
            
            // Ask our Format to set up its data layout for the vertex array.
            // but only once, the VAO saves this state
            glBindVertexArray(vao);
            glBindBuffer(GL_ARRAY_BUFFER, vbo);
            format.bind();
            format.enable();
        }
        // Bind the vertex array and buffers!
        glBindVertexArray(vao);
        
        // Upload the data.
        int indices = finishAndUpload();
        
        if (mode == Mode.QUADS) {
            glDrawElements(GL_TRIANGLES, indices, GL_UNSIGNED_INT, 0);
        } else {
            glDrawArrays(GL_TRIANGLES, 0, indices);
        }
        
        // Unbind the vertex array.
        glBindVertexArray(0);
    }

    /**
     * Clear this builder's cached buffer.
     * <p>
     * If you are completely done, call {@link #destroy()}
     */
    @Override
    public void close() {
        ALLOCATOR.free(bufferAddr);
        bufferAddr = MemoryUtil.NULL;
    }

    /**
     * Represents a primitive mode that this builder is capable of buffering in.
     */
    public enum Mode {
        TRIANGLES(3),
        QUADS(4),
        ;

        public final int vertices;

        Mode(int vertices) {
            this.vertices = vertices;
        }
    }

    /**
     * Specifies a vertex element with a specific data type, number of primitives and a size in bytes.
     */
    public enum Element {
        POS(GL_FLOAT, 2, 2 * 4),
        TEX(GL_FLOAT, 2, 2 * 4),
        COLOR(GL_UNSIGNED_BYTE, 4, 4);

        public final int glType;
        public final int count;
        public final int width;

        Element(int glType, int count, int width) {
            this.glType = glType;
            this.count = count;
            this.width = width;
        }
    }

    /**
     * Specifies a combination of vertex elements.
     */
    public enum Format {
        POS(Element.POS),
        POS_TEX(Element.POS, Element.TEX),
        POS_COLOR(Element.POS, Element.COLOR),
        POS_TEX_COLOR(Element.POS, Element.TEX, Element.COLOR);

        private final Element[] types;
        public final int stride;

        Format(Element... types) {
            this.types = types;

            // Stride is the width of each vertex in bytes.
            stride = Arrays.stream(types).mapToInt(e -> e.width).sum();
        }

        /**
         * set up the attribute pointers for this format.
         * <p>
         * Assumes that an array buffer is already bound and ready to go.
         */
        public void bind() {
            int offset = 0;

            // Set up the pointers that tell GL where our interleaved
            // vertex data is in the buffers.
            for (int i = 0; i < types.length; i++) {
                Element type = types[i];
                switch (type.glType) {
                    case GL_FLOAT -> glVertexAttribPointer(i, type.count, GL_FLOAT, false, stride, offset);
                    case GL_UNSIGNED_BYTE -> glVertexAttribPointer(i, type.count, GL_UNSIGNED_BYTE, true, stride, offset);
                    default -> throw new IllegalStateException("Unknown glType, I don't know how to bind this vertex element: " + type);
                }
                // add to the offset for the next element.
                offset += type.width;
            }
        }

        /**
         * Enables the vertex attributes this format contains.
         */
        public void enable() {
            for (int i = 0; i < types.length; i++) {
                glEnableVertexAttribArray(i);
            }
        }

        /**
         * Disables the vertex attributes this format contains.
         */
        public void disable() {
            for (int i = 0; i < types.length; i++) {
                glDisableVertexAttribArray(i);
            }
        }
    }
}
