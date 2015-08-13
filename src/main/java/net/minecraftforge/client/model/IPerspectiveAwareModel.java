package net.minecraftforge.client.model;

import javax.vecmath.Matrix4f;

import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.resources.model.IBakedModel;

import org.apache.commons.lang3.tuple.Pair;

/*
 * Model that changes based on the rendering perspective
 * (first-person, GUI, e.t.c - see TransformType)
 */
public interface IPerspectiveAwareModel extends IBakedModel
{
    /*
     * Returns the pair of the model for the given perspective, and the matrix
     * that should be applied to the GL state before rendering it (matrix may be null).
     */
    Pair<IBakedModel, Matrix4f> handlePerspective(TransformType cameraTransformType);
}
