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
