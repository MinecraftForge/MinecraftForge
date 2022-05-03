/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.model;

public class ModelLoadingException extends RuntimeException
{
    public ModelLoadingException(String message)
    {
        super(message);
    }

    public ModelLoadingException(String message, Throwable cause)
    {
        super(message, cause);
    }

    private static final long serialVersionUID = 1L;
}
