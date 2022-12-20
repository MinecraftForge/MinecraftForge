/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.extensions;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Transformation;
import org.joml.Vector3f;

/**
 * Extension interface for {@link com.mojang.blaze3d.vertex.PoseStack}.
 */
public interface IForgePoseStack
{
    private PoseStack self()
    {
        return (PoseStack) this;
    }

    /**
     * Pushes and applies the {@code transformation} to this pose stack. <br>
     * The effects of this method can be reversed by a corresponding {@link PoseStack#popPose()} call.
     *
     * @param transformation the transformation to push
     */
    default void pushTransformation(Transformation transformation)
    {
        final PoseStack self = self();
        self.pushPose();

        Vector3f trans = transformation.getTranslation();
        self.translate(trans.x(), trans.y(), trans.z());

        self.mulPose(transformation.getLeftRotation());

        Vector3f scale = transformation.getScale();
        self.scale(scale.x(), scale.y(), scale.z());

        self.mulPose(transformation.getRightRotation());
    }
}
