package net.minecraftforge.client.model.animation;

import net.minecraftforge.client.model.TRSRTransformation;

/**
 * Various implementations of IJointClip.
 */
public final class JointClips
{
    public static enum IdentityJointClip implements IJointClip
    {
        instance;

        public TRSRTransformation apply(float time)
        {
            return TRSRTransformation.identity();
        }
    }

    public static class NodeJointClip implements IJointClip
    {
        private final IJoint child;
        private final IClip clip;

        public NodeJointClip(IJoint joint, IClip clip)
        {
            this.child = joint;
            this.clip = clip;
        }

        public TRSRTransformation apply(float time)
        {
            return clip.apply(child).apply(time);
        }
    }
}
