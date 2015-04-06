package net.minecraftforge.client.model;

import javax.vecmath.Matrix4f;

import net.minecraft.util.EnumFacing;

/*
 * Replacement interface for ModelRotation to allow custom transformations of vanilla models.
 * You should probably use TRSRTransformation directly.
 */
public interface ITransformation
{
    public Matrix4f getMatrix();

    public EnumFacing rotate(EnumFacing facing);

    public int rotate(EnumFacing facing, int vertexIndex);
}
