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
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

/**
 * Wraps a random access file.
 */
public class RandomAccessFileSeekableSource implements SeekableSource {
    
    private RandomAccessFile raf;

    /**
     * Constructs a new RandomAccessFileSeekableSource.
     * @param raf
     */
    public RandomAccessFileSeekableSource(RandomAccessFile raf) {
        if (raf == null)
            throw new NullPointerException("raf");
        this.raf = raf;
    }

    @Override
    public void seek(long pos) throws IOException {
        raf.seek(pos);
    }

    public int read(byte[] b, int off, int len) throws IOException {
        return raf.read(b, off, len);
    }

    public long length() throws IOException {
        return raf.length();
    }

    @Override
    public void close() throws IOException {
        raf.close();
    }

    @Override
    public int read(ByteBuffer bb) throws IOException {
        int c = raf.read(bb.array(), bb.position(), bb.remaining());
        if (c == -1)
            return -1;
        bb.position(bb.position() + c);
        return c;
    }
    
}
