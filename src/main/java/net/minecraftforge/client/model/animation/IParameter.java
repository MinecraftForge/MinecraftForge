package net.minecraftforge.client.model.animation;

/**
 * Time-varying parameter of the animation.
 * Simplest example is the input time itself.
 */
public interface IParameter
{
    public float apply(float input);
}
