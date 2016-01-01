package net.minecraftforge.client.model.animation;

import net.minecraftforge.client.model.TRSRTransformation;

/**
 * Returns Local joint pose; animation clip for specific model part.
 */
public interface IJointClip
{
    TRSRTransformation apply(float time);
}
