/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
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

package net.minecraftforge.fml.repackage.com.nothome.delta;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Outputs a diff following the GDIFF file specification available at
 * http://www.w3.org/TR/NOTE-gdiff-19970901.html.
 */
public class GDiffWriter implements DiffWriter {
    
    /**
     * Max length of a chunk.
     */
    public static final int CHUNK_SIZE = Short.MAX_VALUE;
    
    public static final byte EOF = 0;
    
    /**
     * Max length for single length data encode.
     */
    public static final int DATA_MAX = 246;
    
    public static final int DATA_USHORT = 247;
    public static final int DATA_INT = 248;
    public static final int COPY_USHORT_UBYTE = 249;
    public static final int COPY_USHORT_USHORT = 250;
    public static final int COPY_USHORT_INT = 251;
    public static final int COPY_INT_UBYTE = 252;
    public static final int COPY_INT_USHORT = 253;
    public static final int COPY_INT_INT = 254;
    public static final int COPY_LONG_INT = 255;

    private ByteArrayOutputStream buf = new ByteArrayOutputStream();

    private boolean debug = false;
    
    private DataOutputStream output = null;
    
    /**
     * Constructs a new GDiffWriter.
     */
    public GDiffWriter(DataOutputStream os) throws IOException {
        this.output = os;
        // write magic string "d1 ff d1 ff 04"
        output.writeByte(0xd1);
        output.writeByte(0xff);
        output.writeByte(0xd1);
        output.writeByte(0xff);
        output.writeByte(0x04);
    }
    
    /**
     * Constructs a new GDiffWriter.
     */
    public GDiffWriter(OutputStream output) throws IOException {
        this(new DataOutputStream(output));
    }

    @Override
    public void addCopy(long offset, int length) throws IOException {
        writeBuf();
        
        //output debug data        
        if (debug)
            System.err.println("COPY off: " + offset + ", len: " + length);
        
        // output real data
        if (offset > Integer.MAX_VALUE) {
            // Actually, we don't support longer files than int.MAX_VALUE at the moment..
            output.writeByte(COPY_LONG_INT);
            output.writeLong(offset);
            output.writeInt(length);
        } else if (offset < 65536)  {
            if (length < 256) {                
                output.writeByte(COPY_USHORT_UBYTE);
                output.writeShort((int)offset);
                output.writeByte(length);
            } else if (length > 65535) {
                output.writeByte(COPY_USHORT_INT);
                output.writeShort((int)offset);
                output.writeInt(length);
            } else {
                output.writeByte(COPY_USHORT_USHORT);
                output.writeShort((int)offset);
                output.writeShort(length);
            }
        } else {
            if (length < 256) {
                output.writeByte(COPY_INT_UBYTE);
                output.writeInt((int)offset);
                output.writeByte(length);
            } else if (length > 65535) {
                output.writeByte(COPY_INT_INT);
                output.writeInt((int)offset);
                output.writeInt(length);
            } else {
                output.writeByte(COPY_INT_USHORT);
                output.writeInt((int)offset);
                output.writeShort(length);
            }
        }
    }
    
    /**
     * Adds a data byte.
     */
    @Override
    public void addData(byte b) throws IOException {
        buf.write(b);
        if (buf.size() >= CHUNK_SIZE)
            writeBuf();
    }
    
    private void writeBuf() throws IOException {
        if (buf.size() > 0) {
            if (buf.size() <= DATA_MAX) {
                output.writeByte(buf.size());
            } else if (buf.size() <= 65535) {
                output.writeByte(DATA_USHORT);
                output.writeShort(buf.size());
            } else {
                output.writeByte(DATA_INT);
                output.writeInt(buf.size());
            }
            buf.writeTo(output);
            buf.reset();
        }
    }
    
    /**
     * Flushes accumulated data bytes, if any.
     */
    @Override
    public void flush() throws IOException 
    { 
		writeBuf(); 
    	output.flush(); 
    }
    
    /**
     * Writes the final EOF byte, closes the underlying stream.
     */
    @Override
    public void close() throws IOException {
        this.flush();
        output.write(EOF);
        output.close();
    }

}

