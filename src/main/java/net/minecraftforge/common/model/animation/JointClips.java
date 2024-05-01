/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.model.animation;

import com.mojang.math.Transformation;

/**
 * Various implementations of IJointClip.
 */
public final class JointClips
{
    public static enum IdentityJointClip implements IJointClip
    {
        INSTANCE;

        @Override
        public Transformation apply(float time)
        {
            return Transformation.identity();
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
        public Transformation apply(float time)
        {
            return clip.apply(child).apply(time);
        }
    }
}
