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

package net.minecraftforge.fml.common.eventhandler;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.SubscriberExceptionHandler;

/**
 * Event bus that allows exceptions thrown by the exception handler to propagate.
 * TODO remove this in 1.13 and stop using the guava event bus
 */
public class FMLThrowingEventBus extends EventBus
{
    public FMLThrowingEventBus(final SubscriberExceptionHandler exceptionHandler)
    {
        super((exception, context) -> {
            try
            {
                exceptionHandler.handleException(exception, context);
            }
            catch (final Throwable t)
            {
                // this is a hack to break out of the catch statement in EventBus.handleSubscriberException
                throw new RuntimeException()
                {
                    @Override
                    public String toString()
                    {
                        throw t;
                    }
                };
            }
        });
    }
}
