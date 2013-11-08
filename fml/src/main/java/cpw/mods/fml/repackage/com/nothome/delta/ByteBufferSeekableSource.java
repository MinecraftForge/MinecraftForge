/*
 * ByteArraySeekableSource.java
 *
 * Created on May 17, 2006, 12:41 PM
 * Copyright (c) 2006 Heiko Klein
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

package cpw.mods.fml.repackage.com.nothome.delta;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Wraps a byte buffer as a source
 */
public class ByteBufferSeekableSource implements SeekableSource {
    
    private ByteBuffer bb;
    private ByteBuffer cur;
    
    /**
     * Constructs a new ByteArraySeekableSource.
     */
    public ByteBufferSeekableSource(byte[] source) {
        this(ByteBuffer.wrap(source));
    }
    
    /**
     * Constructs a new ByteArraySeekableSource.
     */
    public ByteBufferSeekableSource(ByteBuffer bb) {
        if (bb == null)
            throw new NullPointerException("bb");
        this.bb = bb;
        bb.rewind();
        try {
            seek(0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    public void seek(long pos) throws IOException {
        cur = bb.slice();
        if (pos > cur.limit())
            throw new IOException("pos " + pos + " cannot seek " + cur.limit());
        cur.position((int) pos);
    }
    
    public int read(ByteBuffer dest) throws IOException {
        if (!cur.hasRemaining())
            return -1;
        int c = 0;
        while (cur.hasRemaining() && dest.hasRemaining()) {
            dest.put(cur.get());
            c++;
        }
        return c;
    }
    
    public void close() throws IOException {
        bb = null;
        cur = null;
    }

    /**
     * Returns a debug <code>String</code>.
     */
    @Override
    public String toString()
    {
        return "BBSeekable" +
            " bb=" + this.bb.position() + "-" + bb.limit() +
            " cur=" + this.cur.position() + "-" + cur.limit() +
            "";
    }
    
}
