/*
 *
 * Copyright (c) 2001 Torgeir Veimo
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE.
 *
 */

package net.minecraftforge.fml.repackage.com.nothome.delta;


import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * For debugging patch generation.
 */
public class DebugDiffWriter implements DiffWriter {

    private ByteArrayOutputStream os = new ByteArrayOutputStream();

    /**
     * Constructs a new DebugDiffWriter.
     */
    public DebugDiffWriter() {}

    @Override
    public void addCopy(long offset, int length) throws IOException {
        if (os.size() > 0)
            writeBuf();
        System.err.println("COPY off: " + offset + ", len: " + length);
    }

    @Override
    public void addData(byte b) throws IOException {
        os.write(b);
        writeBuf();
    }
    private void writeBuf() {
        System.err.print("DATA: ");
        byte[] ba = os.toByteArray();
        for (int ix = 0; ix < ba.length; ix++) {
            if (ba[ix] == '\n')
                System.err.print("\\n");
            else
                System.err.print(String.valueOf((char)((char) ba[ix])));
            //System.err.print("0x" + Integer.toHexString(buf[ix]) + " "); // hex output
        }
        System.err.println("");
        os.reset();
    }

    @Override
    public void flush() throws IOException {
        System.err.println("FLUSH");
    }
    @Override
    public void close() throws IOException {
        System.err.println("CLOSE");
    }

}

