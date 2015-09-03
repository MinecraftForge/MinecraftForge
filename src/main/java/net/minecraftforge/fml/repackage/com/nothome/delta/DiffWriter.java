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

