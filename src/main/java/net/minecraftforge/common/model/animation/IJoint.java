package net.minecraftforge.common.model.animation;

import com.google.common.base.Optional;

import net.minecraftforge.common.model.IModelPart;
import net.minecraftforge.common.model.TRSRTransformation;

/**
 * Model part that's a part of the hierarchical skeleton.
 */
public interface IJoint extends IModelPart
{
    TRSRTransformation getInvBindPose();

    Optional<? extends IJoint> getParent();
}
