/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.animation;



/**
 * Handler for animation events;
 */
public interface IEventHandler<T>
{
    void handleEvents(T instance, float time, Iterable<Event> pastEvents);
}
