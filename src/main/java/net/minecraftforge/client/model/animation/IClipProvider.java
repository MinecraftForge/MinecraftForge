package net.minecraftforge.client.model.animation;

/**
 * Factory for clips, time-aware.
 * Used mainly to generate transitional clips for animation transitions.
 */
public interface IClipProvider
{
    ClipLength apply(float time);
}
