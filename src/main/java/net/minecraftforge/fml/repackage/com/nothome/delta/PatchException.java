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
