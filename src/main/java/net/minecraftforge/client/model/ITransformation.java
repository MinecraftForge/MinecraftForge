package net.minecraftforge.client.model;

import javax.vecmath.Matrix4f;

import net.minecraft.util.EnumFacing;

/*
 * Replacement interface for ModelRotation to allow custom transformations of vanilla models.
 * You should probably use TRSRTransformation directly.
 */
<<<<<<< HEAD
public interface ITransformation {
=======
public interface ITransformation
{
>>>>>>> Added model loader registry
    public Matrix4f getMatrix();

    public EnumFacing rotate(EnumFacing facing);

    public int rotate(EnumFacing facing, int vertexIndex);
}
