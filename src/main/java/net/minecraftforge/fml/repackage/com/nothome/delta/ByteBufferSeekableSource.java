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
    
    @Override
    public void seek(long pos) throws IOException {
        cur = bb.slice();
        if (pos > cur.limit())
            throw new IOException("pos " + pos + " cannot seek " + cur.limit());
        cur.position((int) pos);
    }
    
    @Override
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
    
    @Override
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
