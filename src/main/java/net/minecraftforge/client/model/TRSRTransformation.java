package net.minecraftforge.client.model;

import javax.vecmath.Matrix3f;
import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;
import javax.vecmath.Tuple3f;
import javax.vecmath.Tuple4f;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;

import net.minecraft.client.renderer.block.model.ItemTransformVec3f;
import net.minecraft.client.resources.model.ModelRotation;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3i;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import com.google.common.base.Objects;
import com.google.common.base.Optional;

/*
 * Interpolation-friendly affine transformation.
 * If created with matrix, should successfully decompose it to a composition
 * of easily interpolatable transformations (translation, first rotation, scale
 * (with generally speaking different factors for each axis) and second rotation.
 * If the inpit matrix is a composition of translation, rotation and scale (in
 * any order), then the interpolation of the derived primitive transformations
 * should result in the same transformation as the interpolation of the originals.
 * Decomposition happens lazily (and is hopefully fast enough), so performance
 * should be comparable to using Matrix4f directly.
 * Immutable.
 */
@SuppressWarnings("deprecation")
public class TRSRTransformation implements IModelState, ITransformation
{
    private final Matrix4f matrix;

    private boolean full;
    private Vector3f translation;
    private Quat4f leftRot;
    private Vector3f scale;
    private Quat4f rightRot;

    public TRSRTransformation(Matrix4f matrix)
    {
        if(matrix == null)
        {
            this.matrix = identity.matrix;
        }
        else
        {
            this.matrix = matrix;
        }
    }

    public TRSRTransformation(Vector3f translation, Quat4f leftRot, Vector3f scale, Quat4f rightRot)
    {
        this.matrix = mul(translation, leftRot, scale, rightRot);
        this.translation = translation != null ? translation : new Vector3f();
        this.leftRot = leftRot != null ? leftRot : new Quat4f(0, 0, 0, 1);
        this.scale = scale != null ? scale : new Vector3f(1, 1, 1);
        this.rightRot = rightRot!= null ? rightRot : new Quat4f(0, 0, 0, 1);
        full = true;
    }

    public TRSRTransformation(ItemTransformVec3f transform)
    {
        this(getMatrix(transform));
    }

    public static Matrix4f getMatrix(ItemTransformVec3f transform)
    {
        TRSRTransformation ret = new TRSRTransformation(toVecmath(transform.translation), quatFromYXZDegrees(toVecmath(transform.rotation)), toVecmath(transform.scale), null);
        return blockCenterToCorner(ret).getMatrix();
    }

    public TRSRTransformation(ModelRotation rotation)
    {
        this(rotation.getMatrix());
    }
    
    public TRSRTransformation(EnumFacing facing)
    {
        this(getMatrix(facing));
    }
    
    public static Matrix4f getMatrix(EnumFacing facing)
    {
        switch(facing)
        {
        case DOWN: return ModelRotation.X90_Y0.getMatrix();
        case UP: return ModelRotation.X270_Y0.getMatrix();
        case NORTH: return TRSRTransformation.identity.matrix;
        case SOUTH: return ModelRotation.X0_Y180.getMatrix();
        case WEST: return ModelRotation.X0_Y270.getMatrix();
        case EAST: return ModelRotation.X0_Y90.getMatrix();
        default: return new Matrix4f();
        }
    }

    private static final TRSRTransformation identity;

    static
    {
        Matrix4f m = new Matrix4f();
        m.setIdentity();
        identity = new TRSRTransformation(m);
        identity.getLeftRot();
    }

    public static TRSRTransformation identity()
    {
        return identity;
    }

    public TRSRTransformation compose(TRSRTransformation b)
    {
        Matrix4f m = getMatrix();
        m.mul(b.getMatrix());
        return new TRSRTransformation(m);
    }

    private void genCheck()
    {
        if(!full)
        {
            Pair<Matrix3f, Vector3f> pair = toAffine(matrix);
            Triple<Quat4f, Vector3f, Quat4f> triple = svdDecompose(pair.getLeft());
            this.translation = pair.getRight();
            this.leftRot = triple.getLeft();
            this.scale = triple.getMiddle();
            this.rightRot = triple.getRight();
            full = true;
        }
    }

    public static Quat4f quatFromYXZDegrees(Vector3f yxz)
    {
        return quatFromYXZ((float)Math.toRadians(yxz.y), (float)Math.toRadians(yxz.x), (float)Math.toRadians(yxz.z));
    }

    public static Quat4f quatFromYXZ(Vector3f yxz)
    {
        return quatFromYXZ(yxz.y, yxz.x, yxz.z);
    }

    public static Quat4f quatFromYXZ(float y, float x, float z)
    {
        Quat4f ret = new Quat4f(0, 0, 0, 1), t = new Quat4f();
        t.set(0, (float)Math.sin(y/2), 0, (float)Math.cos(y/2));
        ret.mul(t);
        t.set((float)Math.sin(x/2), 0, 0, (float)Math.cos(x/2));
        ret.mul(t);
        t.set(0, 0, (float)Math.sin(z/2), (float)Math.cos(z/2));
        ret.mul(t);
        return ret;
    }

    public static Vector3f toYXZDegrees(Quat4f q)
    {
        Vector3f yxz = toYXZ(q);
        return new Vector3f((float)Math.toDegrees(yxz.x), (float)Math.toDegrees(yxz.y), (float)Math.toDegrees(yxz.z));
    }

    public static Vector3f toYXZ(Quat4f q)
    {
        float w2 = q.w * q.w;
        float x2 = q.x * q.x;
        float y2 = q.y * q.y;
        float z2 = q.z * q.z;
        float l = w2 + x2 + y2 + z2;
        float sx = 2 * q.w * q.x - 2 * q.y * q.z;
        float x = (float)Math.asin(sx / l);
        if(Math.abs(sx) > .999f * l)
        {
            return new Vector3f(
                 x,
                 2 * (float)Math.atan2(q.y, q.w),
                 0
            );
        }
        return new Vector3f(
            x,
            (float)Math.atan2(2 * q.x * q.z + 2 * q.y * q.w, w2 - x2 - y2 + z2),
            (float)Math.atan2(2 * q.x * q.y + 2 * q.w * q.z, w2 - x2 + y2 - z2)
        );
    }

    public static Matrix4f mul(Vector3f translation, Quat4f leftRot, Vector3f scale, Quat4f rightRot)
    {
        Matrix4f res = new Matrix4f(), t = new Matrix4f();
        res.setIdentity();
        if(leftRot != null)
        {
            t.set(leftRot);
            res.mul(t);
        }
        if(scale != null)
        {
            t.setIdentity();
            t.m00 = scale.x;
            t.m11 = scale.y;
            t.m22 = scale.z;
            res.mul(t);
        }
        if(rightRot != null)
        {
            t.set(rightRot);
            res.mul(t);
        }
        if(translation != null) res.setTranslation(translation);
        return res;
    }

    /*
     * Performs SVD decomposition of m, accumulating reflection in the scale (U and V are pure rotations).
     */
    public static Triple<Quat4f, Vector3f, Quat4f> svdDecompose(Matrix3f m)
    {
        // determine V by doing 5 steps of Jacobi iteration on MT * M
        Quat4f u = new Quat4f(0, 0, 0, 1), v = new Quat4f(0, 0, 0, 1), qt = new Quat4f();
        Matrix3f b = new Matrix3f(m), t = new Matrix3f();
        t.transpose(m);
        b.mul(t, b);

        for(int i = 0; i < 5; i++) v.mul(stepJacobi(b));

        v.normalize();
        t.set(v);
        b.set(m);
        b.mul(t);

        // FIXME: this doesn't work correctly for some reason; not crucial, so disabling for now; investigate in the future.
        //sortSingularValues(b, v);

        Pair<Float, Float> p;

        float ul = 1f;

        p = qrGivensQuat(b.m00, b.m10);
        qt.set(0, 0, p.getLeft(), p.getRight());
        u.mul(qt);
        t.setIdentity();
        t.m00 = qt.w * qt.w - qt.z * qt.z;
        t.m11 = t.m00;
        t.m10 = -2 * qt.z * qt.w;
        t.m01 = -t.m10;
        t.m22 = qt.w * qt.w + qt.z * qt.z;
        ul *= t.m22;
        b.mul(t, b);

        p = qrGivensQuat(b.m00, b.m20);
        qt.set(0, -p.getLeft(), 0, p.getRight());
        u.mul(qt);
        t.setIdentity();
        t.m00 = qt.w * qt.w - qt.y * qt.y;
        t.m22 = t.m00;
        t.m20 = 2 * qt.y * qt.w;
        t.m02 = -t.m20;
        t.m11 = qt.w * qt.w + qt.y * qt.y;
        ul *= t.m11;
        b.mul(t, b);

        p = qrGivensQuat(b.m11, b.m21);
        qt.set(p.getLeft(), 0, 0, p.getRight());
        u.mul(qt);
        t.setIdentity();
        t.m11 = qt.w * qt.w - qt.x * qt.x;
        t.m22 = t.m11;
        t.m21 = -2 * qt.x * qt.w;
        t.m12 = -t.m21;
        t.m00 = qt.w * qt.w + qt.x * qt.x;
        ul *= t.m00;
        b.mul(t, b);

        ul = 1f / ul;
        u.scale((float)Math.sqrt(ul));

        Vector3f s = new Vector3f(b.m00 * ul, b.m11 * ul, b.m22 * ul);

        return Triple.of(u, s, v);
    }

    private static float rsqrt(float f)
    {
        float f2 = .5f * f;
        int i = Float.floatToIntBits(f);
        i = 0x5f3759df - (i >> 1);
        f = Float.intBitsToFloat(i);
        f *= 1.5f - f2 * f * f;
        return f;
    }

    private static final float eps = 1e-6f;
    private static final float g = 3f + 2f * (float)Math.sqrt(2);
    private static final float cs = (float)Math.cos(Math.PI / 8);
    private static final float ss = (float)Math.sin(Math.PI / 8);
    private static final float sq2 = 1f / (float)Math.sqrt(2);

    private static Pair<Float, Float> approxGivensQuat(float a11, float a12, float a22)
    {
        float ch = 2f * (a11 - a22);
        float sh = a12;
        boolean b = g * sh * sh < ch * ch;
        float w = rsqrt(sh * sh + ch * ch);
        ch = b ? w * ch : cs;
        sh = b ? w * sh : ss;
        return Pair.of(sh, ch);
    }

    private static final void swapNeg(Matrix3f m, int i, int j)
    {
        float[] t = new float[3];
        m.getColumn(j, t);
        for(int k = 0; k < 3; k++)
        {
            m.setElement(k, j, -m.getElement(k, i));
        }
        m.setColumn(i, t);
    }

    @SuppressWarnings("unused")
    private static void sortSingularValues(Matrix3f b, Quat4f v)
    {
        float p0 = b.m00 * b.m00 + b.m10 * b.m10 + b.m20 * b.m20;
        float p1 = b.m01 * b.m01 + b.m11 * b.m11 + b.m21 * b.m21;
        float p2 = b.m02 * b.m02 + b.m12 * b.m12 + b.m22 * b.m22;
        Quat4f t = new Quat4f();
        if(p0 < p1)
        {
            swapNeg(b, 0, 1);
            t.set(0, 0, sq2, sq2);
            v.mul(t);
            float f = p0;
            p0 = p1;
            p1 = f;
        }
        if(p0 < p2)
        {
            swapNeg(b, 0, 2);
            t.set(0, sq2, 0, sq2);
            v.mul(t);
            float f = p0;
            p0 = p2;
            p2 = f;
        }
        if(p1 < p2)
        {
            swapNeg(b, 1, 2);
            t.set(sq2, 0, 0, sq2);
            v.mul(t);
        }
    }

    private static Pair<Float, Float> qrGivensQuat(float a1, float a2)
    {
        float p = (float)Math.sqrt(a1 * a1 + a2 * a2);
        float sh = p > eps ? a2 : 0;
        float ch = Math.abs(a1) + Math.max(p, eps);
        if(a1 < 0)
        {
            float f = sh;
            sh = ch;
            ch = f;
        }
        //float w = 1.f / (float)Math.sqrt(ch * ch + sh * sh);
        float w = rsqrt(ch * ch + sh * sh);
        ch *= w;
        sh *= w;
        return Pair.of(sh, ch);
    }

    private static Quat4f stepJacobi(Matrix3f m)
    {
        Matrix3f t = new Matrix3f();
        Quat4f qt = new Quat4f(), ret = new Quat4f(0, 0, 0, 1);
        Pair<Float, Float> p;
        // 01
        if(m.m01 * m.m01 + m.m10 * m.m10 > eps)
        {
            p = approxGivensQuat(m.m00, .5f * (m.m01 + m.m10), m.m11);
            qt.set(0, 0, p.getLeft(), p.getRight());
            //qt.normalize();
            ret.mul(qt);
            //t.set(qt);
            t.setIdentity();
            t.m00 = qt.w * qt.w - qt.z * qt.z;
            t.m11 = t.m00;
            t.m10 = 2 * qt.z * qt.w;
            t.m01 = -t.m10;
            t.m22 = qt.w * qt.w + qt.z * qt.z;
            m.mul(m, t);
            t.transpose();
            m.mul(t, m);
        }
        // 02
        if(m.m02 * m.m02 + m.m20 * m.m20 > eps)
        {
            p = approxGivensQuat(m.m00, .5f * (m.m02 + m.m20), m.m22);
            qt.set(0, -p.getLeft(), 0, p.getRight());
            //qt.normalize();
            ret.mul(qt);
            //t.set(qt);
            t.setIdentity();
            t.m00 = qt.w * qt.w - qt.y * qt.y;
            t.m22 = t.m00;
            t.m20 = -2 * qt.y * qt.w;
            t.m02 = -t.m20;
            t.m11 = qt.w * qt.w + qt.y * qt.y;
            m.mul(m, t);
            t.transpose();
            m.mul(t, m);
        }
        // 12
        if(m.m12 * m.m12 + m.m21 * m.m21 > eps)
        {
            p = approxGivensQuat(m.m11, .5f * (m.m12 + m.m21), m.m22);
            qt.set(p.getLeft(), 0, 0, p.getRight());
            //qt.normalize();
            ret.mul(qt);
            //t.set(qt);
            t.setIdentity();
            t.m11 = qt.w * qt.w - qt.x * qt.x;
            t.m22 = t.m11;
            t.m21 = 2 * qt.x * qt.w;
            t.m12 = -t.m21;
            t.m00 = qt.w * qt.w + qt.x * qt.x;
            m.mul(m, t);
            t.transpose();
            m.mul(t, m);
        }
        return ret;
    }

    /*
     * Divides m by m33, sets last row to (0, 0, 0, 1), extracts linear and translation parts 
     */
    public static Pair<Matrix3f, Vector3f> toAffine(Matrix4f m)
    {
        m.mul(1.f / m.m33);
        Vector3f trans = new Vector3f(m.m03, m.m13, m.m23);
        Matrix3f linear = new Matrix3f(m.m00, m.m01, m.m02, m.m10, m.m11, m.m12, m.m20, m.m21, m.m22);
        return Pair.of(linear, trans);
    }

    /*
     * Don't use this if you don't need to, conversion is lossy (second rotation component is lost).
     */
    public ItemTransformVec3f toItemTransform()
    {
        return new ItemTransformVec3f(toLwjgl(toYXZDegrees(getLeftRot())), toLwjgl(getTranslation()), toLwjgl(getScale()));
    }

    public Matrix4f getMatrix()
    {
        return (Matrix4f)matrix.clone();
    }

    public Vector3f getTranslation()
    {
        genCheck();
        return (Vector3f)translation.clone();
    }

    public Quat4f getLeftRot()
    {
        genCheck();
        return (Quat4f)leftRot.clone();
    }

    public Vector3f getScale()
    {
        genCheck();
        return (Vector3f)scale.clone();
    }

    public Quat4f getRightRot()
    {
        genCheck();
        return (Quat4f)rightRot.clone();
    }

    public Optional<TRSRTransformation> apply(Optional<? extends IModelPart> part)
    {
        if(part.isPresent())
        {
            return Optional.absent();
        }
        return Optional.of(this);
    }

    public EnumFacing rotate(EnumFacing facing)
    {
        return rotate(matrix, facing);
    }

    public static EnumFacing rotate(Matrix4f matrix, EnumFacing facing)
    {
        Vec3i dir = facing.getDirectionVec();
        Vector4f vec = new Vector4f(dir.getX(), dir.getY(), dir.getZ(), 0);
        matrix.transform(vec);
        return EnumFacing.getFacingFromVector(vec.x, vec.y, vec.z);
    }

    public static boolean isInteger(Matrix4f matrix)
    {
        Matrix4f m = new Matrix4f();
        m.setIdentity();
        m.m30 = m.m31 = m.m32 = 1;
        m.m33 = 0;
        m.mul(matrix, m);
        for(int i = 0; i < 3; i++)
        {
            for(int j = 0; j < 3; j++)
            {
                float v = m.getElement(i, j) / m.getElement(3, j);
                if(Math.abs(v - Math.round(v)) > 1e-5) return false;
            }
        }
        return true;
    }

    public int rotate(EnumFacing facing, int vertexIndex)
    {
        // FIXME check if this is good enough
        return vertexIndex;
    }

    @Override
    public String toString()
    {
        genCheck();
        return Objects.toStringHelper(this.getClass())
            .add("matrix", matrix)
            .add("translation", translation)
            .add("leftRot", leftRot)
            .add("scale", scale)
            .add("rightRot", rightRot)
            .toString();
    }

    /**
     * convert transformation from assuming center-block system to corner-block system
     */
    public static TRSRTransformation blockCenterToCorner(TRSRTransformation transform)
    {
        Matrix4f ret = new Matrix4f(transform.getMatrix()), tmp = new Matrix4f();
        tmp.setIdentity();
        tmp.m03 = tmp.m13 = tmp.m23 = .5f;
        ret.mul(tmp, ret);
        tmp.m03 = tmp.m13 = tmp.m23 = -.5f;
        ret.mul(tmp);
        return new TRSRTransformation(ret);
    }

    /**
     * convert transformation from assuming corner-block system to center-block system
     */
    public static TRSRTransformation blockCornerToCenter(TRSRTransformation transform)
    {
        Matrix4f ret = new Matrix4f(transform.getMatrix()), tmp = new Matrix4f();
        tmp.setIdentity();
        tmp.m03 = tmp.m13 = tmp.m23 = -.5f;
        ret.mul(tmp, ret);
        tmp.m03 = tmp.m13 = tmp.m23 = .5f;
        ret.mul(tmp);
        return new TRSRTransformation(ret);
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((matrix == null) ? 0 : matrix.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        TRSRTransformation other = (TRSRTransformation) obj;
        if (matrix == null)
        {
            if (other.matrix != null) return false;
        }
        else if (!matrix.equals(other.matrix)) return false;
        return true;
    }

    public static Vector3f toVecmath(org.lwjgl.util.vector.Vector3f vec)
    {
        return new Vector3f(vec.x, vec.y, vec.z);
    }

    public static Vector4f toVecmath(org.lwjgl.util.vector.Vector4f vec)
    {
        return new Vector4f(vec.x, vec.y, vec.z, vec.w);
    }

    public static Matrix4f toVecmath(org.lwjgl.util.vector.Matrix4f m)
    {
        return new Matrix4f(
            m.m00, m.m10, m.m20, m.m30,
            m.m01, m.m11, m.m21, m.m31,
            m.m02, m.m12, m.m22, m.m32,
            m.m03, m.m13, m.m23, m.m33);
    }

    public static org.lwjgl.util.vector.Vector3f toLwjgl(Vector3f vec)
    {
        return new org.lwjgl.util.vector.Vector3f(vec.x, vec.y, vec.z);
    }

    public static org.lwjgl.util.vector.Vector4f toLwjgl(Vector4f vec)
    {
        return new org.lwjgl.util.vector.Vector4f(vec.x, vec.y, vec.z, vec.w);
    }

    public static org.lwjgl.util.vector.Matrix4f toLwjgl(Matrix4f m)
    {
        org.lwjgl.util.vector.Matrix4f r = new org.lwjgl.util.vector.Matrix4f();
        r.m00 = m.m00;
        r.m01 = m.m10;
        r.m02 = m.m20;
        r.m03 = m.m30;
        r.m10 = m.m01;
        r.m11 = m.m11;
        r.m12 = m.m21;
        r.m13 = m.m31;
        r.m20 = m.m02;
        r.m21 = m.m12;
        r.m22 = m.m22;
        r.m23 = m.m32;
        r.m30 = m.m03;
        r.m31 = m.m13;
        r.m32 = m.m23;
        r.m33 = m.m33;
        return r;
    }

    public static Vector3f lerp(Tuple3f from, Tuple3f to, float progress)
    {
        Vector3f res = new Vector3f(from);
        res.interpolate(from, to, progress);
        return res;
    }

    public static Vector4f lerp(Tuple4f from, Tuple4f to, float progress)
    {
        Vector4f res = new Vector4f(from);
        res.interpolate(from, to, progress);
        return res;
    }

    public static Quat4f slerp(Quat4f from, Quat4f to, float progress)
    {
        Quat4f res = new Quat4f();
        res.interpolate(from, to, progress);
        return res;
    }

    public TRSRTransformation slerp(TRSRTransformation that, float progress)
    {
        return new TRSRTransformation(
            lerp(this.getTranslation(), that.getTranslation(), progress),
            slerp(this.getLeftRot(), that.getLeftRot(), progress),
            lerp(this.getScale(), that.getScale(), progress),
            slerp(this.getRightRot(), that.getRightRot(), progress)
        );
    }
}
