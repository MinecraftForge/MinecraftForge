/*
 * PatchException.java
 *
 * Created on June 6, 2006, 9:34 PM
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

/**
 * Thrown when a patch is invalid.
 */
public class PatchException extends IOException {
    
    private static final long serialVersionUID = 1;

    /**
     * Creates a new instance of <code>PatchException</code> without detail message.
     */
    public PatchException() {
    }
    
    /**
     * Constructs an instance of <code>PatchException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public PatchException(String msg) {
        super(msg);
    }
}
