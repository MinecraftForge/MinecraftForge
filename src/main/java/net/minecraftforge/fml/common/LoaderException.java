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

package net.minecraftforge.fml.common;
import net.minecraftforge.fml.common.EnhancedRuntimeException.WrappedPrintStream;


public class LoaderException extends EnhancedRuntimeException
{
    /**
     *
     */
    private static final long serialVersionUID = -5675297950958861378L;

    public LoaderException(Throwable wrapped)
    {
        super(wrapped);
    }

    public LoaderException()
    {
    }
    public LoaderException(String message)
    {
        super(message);
    }
    public LoaderException(String message, Throwable cause)
    {
        super(message, cause);
    }

    @Override protected void printStackTrace(WrappedPrintStream stream){}
}
