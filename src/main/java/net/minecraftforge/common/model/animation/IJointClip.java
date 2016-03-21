package net.minecraftforge.common.model.animation;

import net.minecraftforge.common.model.TRSRTransformation;

/**
 * Returns Local joint pose; animation clip for specific model part.
 */
public interface IJointClip
{
    TRSRTransformation apply(float time);
}
