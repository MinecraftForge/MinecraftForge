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

package com.google.common.eventbus;

import com.google.common.base.Preconditions;

/**
 * Event bus that allows exceptions thrown by the exception handler to propagate.
 * TODO remove this in 1.13 and stop using the guava event bus
 */
public class FMLThrowingEventBus extends EventBus
{
    private final SubscriberExceptionHandler exceptionHandler;

    public FMLThrowingEventBus(SubscriberExceptionHandler exceptionHandler)
    {
        this.exceptionHandler = Preconditions.checkNotNull(exceptionHandler);
    }

    @Override
    void handleSubscriberException(Throwable e, SubscriberExceptionContext context)
    {
        Preconditions.checkNotNull(e);
        Preconditions.checkNotNull(context);
        exceptionHandler.handleException(e, context);
    }
}
