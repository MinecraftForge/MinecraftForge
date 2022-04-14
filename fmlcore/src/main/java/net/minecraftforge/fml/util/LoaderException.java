/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.util;


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
