/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
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
import java.util.Map;

import com.google.gson.*;
import net.minecraft.util.math.MathHelper;

import net.minecraft.client.renderer.model.ItemTransformVec3f;
import net.minecraft.util.math.vector.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public final class TransformationHelper
{
    @Deprecated
    @OnlyIn(Dist.CLIENT)
    public static TransformationMatrix toTransformation(ItemTransformVec3f transform)
    {
        if (transform.equals(ItemTransformVec3f.DEFAULT)) return TransformationMatrix.identity();

        return new TransformationMatrix(transform.translation, quatFromXYZ(transform.rotation, true), transform.scale, null);
    }

    public static Quaternion quatFromXYZ(Vector3f xyz, boolean degrees)
    {
        return new Quaternion(xyz.getX(), xyz.getY(), xyz.getZ(), degrees);
    }

    public static Quaternion quatFromXYZ(float[] xyz, boolean degrees)
    {
        return new Quaternion(xyz[0], xyz[1], xyz[2], degrees);
    }

    public static Quaternion makeQuaternion(float[] values)
    {
        return new Quaternion(values[0], values[1], values[2], values[3]);
    }

    public static Vector3f lerp(Vector3f from, Vector3f to, float progress)
    {
        Vector3f res = from.copy();
        res.lerp(to, progress);
        return res;
    }

    private static final double THRESHOLD = 0.9995;
    public static Quaternion slerp(Quaternion v0, Quaternion v1, float t)
    {
        // From https://en.wikipedia.org/w/index.php?title=Slerp&oldid=928959428
        // License: CC BY-SA 3.0 https://creativecommons.org/licenses/by-sa/3.0/

        // Compute the cosine of the angle between the two vectors.
        // If the dot product is negative, slerp won't take
        // the shorter path. Note that v1 and -v1 are equivalent when
        // the negation is applied to all four components. Fix by
        // reversing one quaternion.
        float dot = v0.getX() * v1.getX() + v0.getY() * v1.getY() + v0.getZ() * v1.getZ() + v0.getW() * v1.getW();
        if (dot < 0.0f) {
            v1 = new Quaternion(-v1.getX(), -v1.getY(), -v1.getZ(), -v1.getW());
            dot = -dot;
        }

        // If the inputs are too close for comfort, linearly interpolate
        // and normalize the result.
        if (dot > THRESHOLD) {
            float x = MathHelper.lerp(t, v0.getX(), v1.getX());
            float y = MathHelper.lerp(t, v0.getY(), v1.getY());
            float z = MathHelper.lerp(t, v0.getZ(), v1.getZ());
            float w = MathHelper.lerp(t, v0.getW(), v1.getW());
            return new Quaternion(x,y,z,w);
        }

        // Since dot is in range [0, DOT_THRESHOLD], acos is safe
        float angle01 = (float)Math.acos(dot);
        float angle0t = angle01*t;
        float sin0t = MathHelper.sin(angle0t);
        float sin01 = MathHelper.sin(angle01);
        float sin1t = MathHelper.sin(angle01 - angle0t);

        float s1 = sin0t / sin01;
        float s0 = sin1t / sin01;

        return new Quaternion(
                s0 * v0.getX() + s1 * v1.getX(),
                s0 * v0.getY() + s1 * v1.getY(),
                s0 * v0.getZ() + s1 * v1.getZ(),
                s0 * v0.getW() + s1 * v1.getW()
        );
    }

    public static TransformationMatrix slerp(TransformationMatrix one, TransformationMatrix that, float progress)
    {
        return new TransformationMatrix(
            lerp(one.getTranslation(), that.getTranslation(), progress),
            slerp(one.getRotationLeft(), that.getRotationLeft(), progress),
            lerp(one.getScale(), that.getScale(), progress),
            slerp(one.getRightRot(), that.getRightRot(), progress)
        );
    }

    public static boolean epsilonEquals(Vector4f v1, Vector4f v2, float epsilon)
    {
        return MathHelper.abs(v1.getX()-v2.getX()) < epsilon &&
               MathHelper.abs(v1.getY()-v2.getY()) < epsilon &&
               MathHelper.abs(v1.getZ()-v2.getZ()) < epsilon &&
               MathHelper.abs(v1.getW()-v2.getW()) < epsilon;
    }

    public static class Deserializer implements JsonDeserializer<TransformationMatrix>
    {
        private static final Vector3f ORIGIN_CORNER = new Vector3f();
        private static final Vector3f ORIGIN_OPPOSING_CORNER = new Vector3f(1f, 1f, 1f);
        private static final Vector3f ORIGIN_CENTER = new Vector3f(.5f, .5f, .5f);

        @Override
        public TransformationMatrix deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
        {
            if (json.isJsonPrimitive() && json.getAsJsonPrimitive().isString())
            {
                String transform = json.getAsString();
                if(transform.equals("identity"))
                {
                    return TransformationMatrix.identity();
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
            // TODO: Default origin is opposing corner, due to a mistake.
            // This should probably be replaced with center in future versions.
            Vector3f origin = ORIGIN_OPPOSING_CORNER;
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
            if (obj.has("origin"))
            {
                origin = parseOrigin(obj);
                obj.remove("origin");
            }
            if (!obj.entrySet().isEmpty()) throw new JsonParseException("TRSR: can either have single 'matrix' key, or a combination of 'translation', 'rotation', 'scale', 'post-rotation', 'origin'");
            TransformationMatrix matrix = new TransformationMatrix(translation, leftRot, scale, rightRot);

            // Use a different origin if needed.
            if (!ORIGIN_CENTER.equals(origin))
            {
                Vector3f originFromCenter = origin.copy();
                originFromCenter.sub(ORIGIN_CENTER);
                matrix = matrix.applyOrigin(originFromCenter);
            }
            return matrix;
        }

        private static Vector3f parseOrigin(JsonObject obj) {
            Vector3f origin = null;

            // Two types supported: string ("center", "corner") and array ([x, y, z])
            JsonElement originElement = obj.get("origin");
            if (originElement.isJsonArray())
            {
                origin = new Vector3f(parseFloatArray(originElement, 3, "Origin"));
            }
            else if (originElement.isJsonPrimitive())
            {
                String originString = originElement.getAsString();
                if ("center".equals(originString))
                {
                    origin = ORIGIN_CENTER;
                }
                else if ("corner".equals(originString))
                {
                    origin = ORIGIN_CORNER;
                }
                else if ("opposing-corner".equals(originString))
                {
                    // This option can be used to not break models that were written with this origin once the default is changed
                    origin = ORIGIN_OPPOSING_CORNER;
                }
                else
                {
                    throw new JsonParseException("Origin: expected one of 'center', 'corner', 'opposing-corner'");
                }
            }
            else
            {
                throw new JsonParseException("Origin: expected an array or one of 'center', 'corner', 'opposing-corner'");
            }
            return origin;
        }

        public static Matrix4f parseMatrix(JsonElement e)
        {
            if (!e.isJsonArray()) throw new JsonParseException("Matrix: expected an array, got: " + e);
            JsonArray m = e.getAsJsonArray();
            if (m.size() != 3) throw new JsonParseException("Matrix: expected an array of length 3, got: " + m.size());
            float[] values = new float[16];
            for (int i = 0; i < 3; i++)
            {
                if (!m.get(i).isJsonArray()) throw new JsonParseException("Matrix row: expected an array, got: " + m.get(i));
                JsonArray r = m.get(i).getAsJsonArray();
                if (r.size() != 4) throw new JsonParseException("Matrix row: expected an array of length 4, got: " + r.size());
                for (int j = 0; j < 4; j++)
                {
                    try
                    {
                        values[j*4+i] = r.get(j).getAsNumber().floatValue();
                    }
                    catch (ClassCastException ex)
                    {
                        throw new JsonParseException("Matrix element: expected number, got: " + r.get(j));
                    }
                }
            }
            return new Matrix4f(values);
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
                    ret = Vector3f.XP.rotationDegrees(entry.getValue().getAsNumber().floatValue());
                }
                else if (entry.getKey().equals("y"))
                {
                    ret = Vector3f.YP.rotationDegrees(entry.getValue().getAsNumber().floatValue());
                }
                else if (entry.getKey().equals("z"))
                {
                    ret = Vector3f.ZP.rotationDegrees(entry.getValue().getAsNumber().floatValue());
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
                    Quaternion ret = Quaternion.ONE.copy();
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
                        return quatFromXYZ(parseFloatArray(e, 3, "Rotation"), true);
                    else // quaternion
                        return makeQuaternion(parseFloatArray(e, 4, "Rotation"));
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
