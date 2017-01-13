/*
 * Minecraft Forge
 * Copyright (c) 2016.
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

package net.minecraftforge.common.util;

import org.apache.commons.io.IOUtils;

import javax.annotation.Nullable;
import java.io.Closeable;
import java.io.IOException;
import java.util.zip.ZipFile;

public class Java6Utils
{
    /**
     * {@link ZipFile} does not implement {@link Closeable} on Java 6.
     * This method is the same as {@link IOUtils#closeQuietly(Closeable)} but works on {@link ZipFile} on Java 6.
     */
    public static void closeZipQuietly(@Nullable ZipFile file)
    {
        try
        {
            if (file != null)
            {
                file.close();
            }
        }
        catch (IOException ioe)
        {
            // ignore
        }
    }
}
