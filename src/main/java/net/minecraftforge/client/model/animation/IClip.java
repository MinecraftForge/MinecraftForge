package net.minecraftforge.client.model.animation;


/**
 * Clip for a rigged model.
 */
public interface IClip
{
    IJointClip apply(IJoint joint);

    Iterable<Event> pastEvents(float lastPollTime, float time);
}
