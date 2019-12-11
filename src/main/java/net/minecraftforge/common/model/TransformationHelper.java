/*
 * Minecraft Forge
 * Copyright (c) 2016-2019.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.common.model;

import java.lang.reflect.Type;
import java.util.EnumMap;
import java.util.Map;

import javax.annotation.Nullable;

import com.google.gson.*;
import net.minecraft.client.renderer.*;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import com.google.common.collect.Maps;

import net.minecraft.client.renderer.model.ItemTransformVec3f;
import net.minecraft.client.renderer.model.ModelRotation;
import net.minecraft.util.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ForgeHooksClient;

public final class TransformationHelper
{
    @Deprecated
    @OnlyIn(Dist.CLIENT)
    public static TransformationMatrix toTransformation(ItemTransformVec3f transform)
    {
        if (transform.equals(ItemTransformVec3f.DEFAULT)) return TransformationMatrix.func_227983_a_();

        return new TransformationMatrix(transform.translation, quatFromXYZ(transform.rotation, true), transform.scale, null);
    }

    public static void transform(Matrix4f matrix, Vector4f vector)
    {
        javax.vecmath.Vector4f copy = toVecmath(vector);
        toVecmath(matrix).transform(copy);
        vector.set(copy.x, copy.y, copy.z, copy.w);
    }

    public static Quaternion quatFromXYZ(Vector3f xyz, boolean degrees)
    {
        return new Quaternion(xyz.getX(), xyz.getY(), xyz.getZ(), degrees);
    }

    public static Direction rotate(Matrix4f matrix, Direction facing)
    {
        Vec3i dir = facing.getDirectionVec();
        javax.vecmath.Vector4f vec = new javax.vecmath.Vector4f(dir.getX(), dir.getY(), dir.getZ(), 0);
        toVecmath(matrix).transform(vec);
        return Direction.getFacingFromVector(vec.x, vec.y, vec.z);
    }

    /**
     * convert transformation from assuming center-block system to corner-block system
     */
    public static TransformationMatrix blockCenterToCorner(TransformationMatrix transform)
    {
        if (transform.isIdentity()) return TransformationMatrix.func_227983_a_();

        javax.vecmath.Matrix4f ret = toVecmath(transform.func_227988_c_()), tmp = new javax.vecmath.Matrix4f();
        tmp.setIdentity();
        tmp.m03 = tmp.m13 = tmp.m23 = .5f;
        ret.mul(tmp, ret);
        tmp.m03 = tmp.m13 = tmp.m23 = -.5f;
        ret.mul(tmp);
        return new TransformationMatrix(toMojang(ret));
    }

    /**
     * convert transformation from assuming corner-block system to center-block system
     */
    public static TransformationMatrix blockCornerToCenter(TransformationMatrix transform)
    {
        if (transform.isIdentity()) return TransformationMatrix.func_227983_a_();

        javax.vecmath.Matrix4f ret = toVecmath(transform.func_227988_c_()), tmp = new javax.vecmath.Matrix4f();
        tmp.setIdentity();
        tmp.m03 = tmp.m13 = tmp.m23 = -.5f;
        ret.mul(tmp, ret);
        tmp.m03 = tmp.m13 = tmp.m23 = .5f;
        ret.mul(tmp);
        return new TransformationMatrix(toMojang(ret));
    }

    @OnlyIn(Dist.CLIENT)
    public static javax.vecmath.Vector3f toVecmath(Vector3f vec)
    {
        return new javax.vecmath.Vector3f(vec.getX(), vec.getY(), vec.getZ());
    }

    @OnlyIn(Dist.CLIENT)
    public static javax.vecmath.Vector4f toVecmath(Vector4f vec)
    {
        return new javax.vecmath.Vector4f(vec.getX(), vec.getY(), vec.getZ(), vec.getW());
    }

    @OnlyIn(Dist.CLIENT)
    public static javax.vecmath.Matrix4f toVecmath(Matrix4f m)
    {
        float[] all = new float[16];
        m.write(all);
        return new javax.vecmath.Matrix4f(all);
    }

    public static javax.vecmath.Quat4f toVecmath(Quaternion q)
    {
        return new javax.vecmath.Quat4f(q.getX(), q.getY(), q.getZ(), q.getW());
    }
    
    public static Quaternion toMojang(javax.vecmath.Quat4f q)
    {
        return new Quaternion(q.getX(), q.getY(), q.getZ(), q.getW());
    }

    @OnlyIn(Dist.CLIENT)
    public static Vector3f toMojang(javax.vecmath.Vector3f vec)
    {
        return new Vector3f(vec.x, vec.y, vec.z);
    }

    @OnlyIn(Dist.CLIENT)
    public static Vector4f toMojang(javax.vecmath.Vector4f vec)
    {
        return new Vector4f(vec.x, vec.y, vec.z, vec.w);
    }

    @OnlyIn(Dist.CLIENT)
    public static Matrix4f toMojang(javax.vecmath.Matrix4f m)
    {
        Matrix4f r = new Matrix4f();
        float[] row = new float[4];
        float[] all = new float[16];
        for (int x = 0; x < 4; x++)
        {
            m.getRow(x, row);
            for (int y = 0; y < 4; y++)
            {
                all[y*4+x] = row[y];
            }
        }
        r.set(all);
        return r;
    }

    public static javax.vecmath.Vector3f lerp(javax.vecmath.Tuple3f from, javax.vecmath.Tuple3f to, float progress)
    {
        javax.vecmath.Vector3f res = new javax.vecmath.Vector3f(from);
        res.interpolate(from, to, progress);
        return res;
    }

    public static javax.vecmath.Vector4f lerp(javax.vecmath.Tuple4f from, javax.vecmath.Tuple4f to, float progress)
    {
        javax.vecmath.Vector4f res = new javax.vecmath.Vector4f(from);
        res.interpolate(from, to, progress);
        return res;
    }

    public static Vector3f lerp(Vector3f from, Vector3f to, float progress)
    {
        Vector3f res = from.func_229195_e_();
        res.func_229190_a_(to, progress);
        return res;
    }

    public static Quaternion slerp(Quaternion from, Quaternion to, float progress)
    {
        javax.vecmath.Quat4f res = new javax.vecmath.Quat4f();
        res.interpolate(toVecmath(from), toVecmath(to), progress);
        return toMojang(res);
    }

    public static TransformationMatrix slerp(TransformationMatrix one, TransformationMatrix that, float progress)
    {
        return new TransformationMatrix(
            lerp(one.getTranslation(), that.getTranslation(), progress),
            slerp(one.func_227989_d_(), that.func_227989_d_(), progress),
            lerp(one.getScale(), that.getScale(), progress),
            slerp(one.getRightRot(), that.getRightRot(), progress)
        );
    }

    private static final EnumMap<Direction, TransformationMatrix> vanillaUvTransformLocalToGlobal = Maps.newEnumMap(Direction.class);
    private static final EnumMap<Direction, TransformationMatrix> vanillaUvTransformGlobalToLocal = Maps.newEnumMap(Direction.class);

    static
    {
        vanillaUvTransformLocalToGlobal.put(Direction.SOUTH, TransformationMatrix.func_227983_a_());
        javax.vecmath.Quat4f tmp = new javax.vecmath.Quat4f();
        tmp.set(new javax.vecmath.AxisAngle4f(0, 1, 0, (float)Math.toRadians(90)));
        vanillaUvTransformLocalToGlobal.put(Direction.EAST,  new TransformationMatrix(null, toMojang(tmp), null, null));
        tmp.set(new javax.vecmath.AxisAngle4f(0, 1, 0, (float)Math.toRadians(-90)));
        vanillaUvTransformLocalToGlobal.put(Direction.WEST,  new TransformationMatrix(null, toMojang(tmp), null, null));
        tmp.set(new javax.vecmath.AxisAngle4f(0, 1, 0, (float)Math.toRadians(180)));
        vanillaUvTransformLocalToGlobal.put(Direction.NORTH, new TransformationMatrix(null, toMojang(tmp), null, null));
        tmp.set(new javax.vecmath.AxisAngle4f(1, 0, 0, (float)Math.toRadians(-90)));
        vanillaUvTransformLocalToGlobal.put(Direction.UP,    new TransformationMatrix(null, toMojang(tmp), null, null));
        tmp.set(new javax.vecmath.AxisAngle4f(1, 0, 0, (float)Math.toRadians(90)));
        vanillaUvTransformLocalToGlobal.put(Direction.DOWN,  new TransformationMatrix(null, toMojang(tmp), null, null));

        for(Direction side : Direction.values())
        {
            vanillaUvTransformGlobalToLocal.put(side, vanillaUvTransformLocalToGlobal.get(side).inverse());
        }
    }

    public static TransformationMatrix getVanillaUvTransformLocalToGlobal(Direction side)
    {
        return vanillaUvTransformLocalToGlobal.get(side);
    }

    public static TransformationMatrix getVanillaUvTransformGlobalToLocal(Direction side)
    {
        return vanillaUvTransformGlobalToLocal.get(side);
    }

    public static TransformationMatrix getUVLockTransform(TransformationMatrix matrix, Direction originalSide)
    {
        Direction newSide = matrix.rotateTransform(originalSide);
        try
        {
            return blockCenterToCorner(vanillaUvTransformGlobalToLocal.get(originalSide).compose(blockCornerToCenter(matrix.inverse())).compose(vanillaUvTransformLocalToGlobal.get(newSide)));
        }
        catch(javax.vecmath.SingularMatrixException e)
        {
            return new TransformationMatrix(null, null, new Vector3f(0, 0, 0), null);
        }
    }

    public static class Deserializer implements JsonDeserializer<TransformationMatrix>
    {
        @Override
        public TransformationMatrix deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
        {
            if (json.isJsonPrimitive() && json.getAsJsonPrimitive().isString())
            {
                String transform = json.getAsString();
                if(transform.equals("identity"))
                {
                    return TransformationMatrix.func_227983_a_();
                }
                else
                {
                    throw new JsonParseException("TRSR: unknown default string: " + transform);
                }
            }
            if (json.isJsonArray())
            {
                // direct matrix array
                return new TransformationMatrix(parseMatrix(json));
            }
            if (!json.isJsonObject()) throw new JsonParseException("TRSR: expected array or object, got: " + json);
            JsonObject obj = json.getAsJsonObject();
            TransformationMatrix ret;
            if (obj.has("matrix"))
            {
                // matrix as a sole key
                ret = new TransformationMatrix(parseMatrix(obj.get("matrix")));
                obj.remove("matrix");
                if (obj.entrySet().size() != 0)
                {
                    throw new JsonParseException("TRSR: can't combine matrix and other keys");
                }
                return ret;
            }
            Vector3f translation = null;
            Quaternion leftRot = null;
            Vector3f scale = null;
            Quaternion rightRot = null;
            if (obj.has("translation"))
            {
                translation = new Vector3f(parseFloatArray(obj.get("translation"), 3, "Translation"));
                obj.remove("translation");
            }
            if (obj.has("rotation"))
            {
                leftRot = parseRotation(obj.get("rotation"));
                obj.remove("rotation");
            }
            if (obj.has("scale"))
            {
                if(!obj.get("scale").isJsonArray())
                {
                    try
                    {
                        float s = obj.get("scale").getAsNumber().floatValue();
                        scale = new Vector3f(s, s, s);
                    }
                    catch (ClassCastException ex)
                    {
                        throw new JsonParseException("TRSR scale: expected number or array, got: " + obj.get("scale"));
                    }
                }
                else
                {
                    scale = new Vector3f(parseFloatArray(obj.get("scale"), 3, "Scale"));
                }
                obj.remove("scale");
            }
            if (obj.has("post-rotation"))
            {
                rightRot = parseRotation(obj.get("post-rotation"));
                obj.remove("post-rotation");
            }
            if (!obj.entrySet().isEmpty()) throw new JsonParseException("TRSR: can either have single 'matrix' key, or a combination of 'translation', 'rotation', 'scale', 'post-rotation'");
            return new TransformationMatrix(translation, leftRot, scale, rightRot);
        }

        public static Matrix4f parseMatrix(JsonElement e)
        {
            if (!e.isJsonArray()) throw new JsonParseException("Matrix: expected an array, got: " + e);
            JsonArray m = e.getAsJsonArray();
            if (m.size() != 3) throw new JsonParseException("Matrix: expected an array of length 3, got: " + m.size());
            javax.vecmath.Matrix4f ret = new javax.vecmath.Matrix4f();
            for (int i = 0; i < 3; i++)
            {
                if (!m.get(i).isJsonArray()) throw new JsonParseException("Matrix row: expected an array, got: " + m.get(i));
                JsonArray r = m.get(i).getAsJsonArray();
                if (r.size() != 4) throw new JsonParseException("Matrix row: expected an array of length 4, got: " + r.size());
                for (int j = 0; j < 4; j++)
                {
                    try
                    {
                        ret.setElement(i, j, r.get(j).getAsNumber().floatValue());
                    }
                    catch (ClassCastException ex)
                    {
                        throw new JsonParseException("Matrix element: expected number, got: " + r.get(j));
                    }
                }
            }
            return toMojang(ret);
        }

        public static float[] parseFloatArray(JsonElement e, int length, String prefix)
        {
            if (!e.isJsonArray()) throw new JsonParseException(prefix + ": expected an array, got: " + e);
            JsonArray t = e.getAsJsonArray();
            if (t.size() != length) throw new JsonParseException(prefix + ": expected an array of length " + length + ", got: " + t.size());
            float[] ret = new float[length];
            for (int i = 0; i < length; i++)
            {
                try
                {
                    ret[i] = t.get(i).getAsNumber().floatValue();
                }
                catch (ClassCastException ex)
                {
                    throw new JsonParseException(prefix + " element: expected number, got: " + t.get(i));
                }
            }
            return ret;
        }

        public static Quaternion parseAxisRotation(JsonElement e)
        {
            if (!e.isJsonObject()) throw new JsonParseException("Axis rotation: object expected, got: " + e);
            JsonObject obj  = e.getAsJsonObject();
            if (obj.entrySet().size() != 1) throw new JsonParseException("Axis rotation: expected single axis object, got: " + e);
            Map.Entry<String, JsonElement> entry = obj.entrySet().iterator().next();
            Quaternion ret;
            try
            {
                if (entry.getKey().equals("x"))
                {
                    ret = new Quaternion(new Vector3f(1, 0, 0), entry.getValue().getAsNumber().floatValue(), true);
                }
                else if (entry.getKey().equals("y"))
                {
                    ret = new Quaternion(new Vector3f(0, 1, 0), entry.getValue().getAsNumber().floatValue(), true);
                }
                else if (entry.getKey().equals("z"))
                {
                    ret = new Quaternion(new Vector3f(0, 0, 1), entry.getValue().getAsNumber().floatValue(), true);
                }
                else throw new JsonParseException("Axis rotation: expected single axis key, got: " + entry.getKey());
            }
            catch(ClassCastException ex)
            {
                throw new JsonParseException("Axis rotation value: expected number, got: " + entry.getValue());
            }
            return ret;
        }

        public static Quaternion parseRotation(JsonElement e)
        {
            if (e.isJsonArray())
            {
                if (e.getAsJsonArray().get(0).isJsonObject())
                {
                    Quaternion ret = new Quaternion(0, 0, 0, 1);
                    for (JsonElement a : e.getAsJsonArray())
                    {
                        ret.multiply(parseAxisRotation(a));
                    }
                    return ret;
                }
                else if (e.isJsonArray())
                {
                    JsonArray array = e.getAsJsonArray();
                    if (array.size() == 3) //Vanilla rotation
                        return quatFromXYZ(new Vector3f(parseFloatArray(e, 3, "Rotation")), true);
                    else // quaternion
                        return new Quaternion(parseFloatArray(e, 4, "Rotation"));
                }
                else throw new JsonParseException("Rotation: expected array or object, got: " + e);
            }
            else if (e.isJsonObject())
            {
                return parseAxisRotation(e);
            }
            else throw new JsonParseException("Rotation: expected array or object, got: " + e);
        }
    }
}
