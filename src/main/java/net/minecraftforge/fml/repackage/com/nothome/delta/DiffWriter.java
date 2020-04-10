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

/**
 * Interface for DIFF writers.
 */
public interface DiffWriter extends Closeable {

    /**
     * Add a GDIFF copy instruction.
     */
    public void addCopy(long offset, int length) throws IOException;

    /**
     * Add a GDIFF data instruction.
     * Implementors should buffer the data.
     */
	public void addData(byte b) throws IOException;

	/**
	 * Flushes to output, e.g. any data added.
	 */
    public void flush() throws IOException;

    /**
     * Closes this stream.
     * Note that {@link DiffWriter} will invoke this method at the end.
     */
    @Override
    public void close() throws IOException;
}

