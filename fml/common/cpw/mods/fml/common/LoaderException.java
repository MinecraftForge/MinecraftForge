/*
 * Forge Mod Loader
 * Copyright (c) 2012-2013 cpw.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     cpw - implementation
 */

package cpw.mods.fml.common;

public class LoaderException extends RuntimeException
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
}
