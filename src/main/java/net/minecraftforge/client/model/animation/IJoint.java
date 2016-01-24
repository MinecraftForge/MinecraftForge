package net.minecraftforge.client.model.animation;

import com.google.common.base.Optional;

import net.minecraftforge.client.model.IModelPart;
import net.minecraftforge.client.model.TRSRTransformation;

/**
 * Model part that's a part of the hierarchical skeleton.
 */
public interface IJoint extends IModelPart
{
    TRSRTransformation getInvBindPose();

    Optional<? extends IJoint> getParent();
}
