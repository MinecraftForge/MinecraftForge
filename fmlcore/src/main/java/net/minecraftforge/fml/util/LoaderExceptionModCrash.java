/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.util;

/**
 * Prevent LoaderException from adding its own stack trace to the wrapped throwable's stack trace.
 */
public class LoaderExceptionModCrash extends LoaderException
{
    private static final long serialVersionUID = 1L;

    public LoaderExceptionModCrash(String message, Throwable cause)
    {
        super(message, cause);
    }

    @Override
    public synchronized Throwable fillInStackTrace()
    {
        return this;
    }
}
