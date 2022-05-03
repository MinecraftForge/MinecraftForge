/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.model.animation;

import net.minecraft.util.math.vector.TransformationMatrix;

/**
 * Various implementations of IJointClip.
 */
public final class JointClips
{
    public static enum IdentityJointClip implements IJointClip
    {
        INSTANCE;

        @Override
        public TransformationMatrix apply(float time)
        {
            return TransformationMatrix.identity();
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

        @Override
        public TransformationMatrix apply(float time)
        {
            return clip.apply(child).apply(time);
        }
    }
}
