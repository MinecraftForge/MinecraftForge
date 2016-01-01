package net.minecraftforge.client.model.animation;

/**
 * Time-varying value associated with the animation.
 * Return value should be constant with the respect to the input and reasonable context (current render frame).
 * Simplest example is the input time itself.
 * Unity calls them Parameters, Unreal calls them Variables.
 */
public interface ITimeValue
{
    public float apply(float input);
}
