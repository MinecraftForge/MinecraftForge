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

import java.io.Closeable;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

/**
 * For sources of random-access data, such as {@link RandomAccessFile}.
 */
public interface SeekableSource extends Closeable {
    
    /**
     * Sets the position for the next {@link #read(ByteBuffer)}.
     */
    void seek(long pos) throws IOException ;
    
    /**
     * Reads up to {@link ByteBuffer#remaining()} bytes from the source,
     * returning the number of bytes read, or -1 if no bytes were read
     * and EOF was reached.
     */
    int read(ByteBuffer bb) throws IOException;
    
}
