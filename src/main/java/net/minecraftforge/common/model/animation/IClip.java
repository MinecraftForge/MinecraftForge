/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.model.animation;

import net.minecraftforge.common.animation.Event;


/**
 * Clip for a rigged model.
 */
public interface IClip
{
    IJointClip apply(IJoint joint);

    Iterable<Event> pastEvents(float lastPollTime, float time);
}
