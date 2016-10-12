package net.minecraftforge.client.model.animation;

import com.google.common.base.Objects;

/**
 * Clip that has a definite length.
 * Main purpose is for the state machine to catch transition ends.
 */
public final class ClipLength implements IClip
{
    private final IClip clip;
    private final float length;

    public ClipLength(IClip clip, float length)
    {
        this.clip = clip;
        this.length = length;
    }

    public IJointClip apply(final IJoint joint)
    {
        return clip.apply(joint);
    }

    public float getLength()
    {
        return length;
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode(clip, length);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ClipLength other = (ClipLength) obj;
        return Objects.equal(clip, other.clip) && Objects.equal(length, other.length);
    }
}
