/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.util;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.gson.*;
import net.minecraft.util.Mth;

import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;
import com.mojang.math.Transformation;
import com.mojang.math.Vector3f;
import com.mojang.math.Vector4f;

public final class TransformationHelper
{
    public static Quaternion quatFromXYZ(Vector3f xyz, boolean degrees)
    {
        return new Quaternion(xyz.x(), xyz.y(), xyz.z(), degrees);
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
        float dot = v0.i() * v1.i() + v0.j() * v1.j() + v0.k() * v1.k() + v0.r() * v1.r();
        if (dot < 0.0f) {
            v1 = new Quaternion(-v1.i(), -v1.j(), -v1.k(), -v1.r());
            dot = -dot;
        }

        // If the inputs are too close for comfort, linearly interpolate
        // and normalize the result.
        if (dot > THRESHOLD) {
            float x = Mth.lerp(t, v0.i(), v1.i());
            float y = Mth.lerp(t, v0.j(), v1.j());
            float z = Mth.lerp(t, v0.k(), v1.k());
            float w = Mth.lerp(t, v0.r(), v1.r());
            return new Quaternion(x,y,z,w);
        }

        // Since dot is in range [0, DOT_THRESHOLD], acos is safe
        float angle01 = (float)Math.acos(dot);
        float angle0t = angle01*t;
        float sin0t = Mth.sin(angle0t);
        float sin01 = Mth.sin(angle01);
        float sin1t = Mth.sin(angle01 - angle0t);

        float s1 = sin0t / sin01;
        float s0 = sin1t / sin01;

        return new Quaternion(
                s0 * v0.i() + s1 * v1.i(),
                s0 * v0.j() + s1 * v1.j(),
                s0 * v0.k() + s1 * v1.k(),
                s0 * v0.r() + s1 * v1.r()
        );
    }

    public static Transformation slerp(Transformation one, Transformation that, float progress)
    {
        return new Transformation(
            lerp(one.getTranslation(), that.getTranslation(), progress),
            slerp(one.getLeftRotation(), that.getLeftRotation(), progress),
            lerp(one.getScale(), that.getScale(), progress),
            slerp(one.getRightRotation(), that.getRightRotation(), progress)
        );
    }

    public static boolean epsilonEquals(Vector4f v1, Vector4f v2, float epsilon)
    {
        return Mth.abs(v1.x()-v2.x()) < epsilon &&
               Mth.abs(v1.y()-v2.y()) < epsilon &&
               Mth.abs(v1.z()-v2.z()) < epsilon &&
               Mth.abs(v1.w()-v2.w()) < epsilon;
    }

    public static class Deserializer implements JsonDeserializer<Transformation>
    {
        private static final Vector3f ORIGIN_CORNER = new Vector3f();
        private static final Vector3f ORIGIN_OPPOSING_CORNER = new Vector3f(1f, 1f, 1f);
        private static final Vector3f ORIGIN_CENTER = new Vector3f(.5f, .5f, .5f);

        @Override
        public Transformation deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
        {
            if (json.isJsonPrimitive() && json.getAsJsonPrimitive().isString())
            {
                String transform = json.getAsString();
                if(transform.equals("identity"))
                {
                    return Transformation.identity();
                }
                else
                {
                    throw new JsonParseException("TRSR: unknown default string: " + transform);
                }
            }
            if (json.isJsonArray())
            {
                // direct matrix array
                return new Transformation(parseMatrix(json));
            }
            if (!json.isJsonObject()) throw new JsonParseException("TRSR: expected array or object, got: " + json);
            JsonObject obj = json.getAsJsonObject();
            Transformation ret;
            if (obj.has("matrix"))
            {
                // matrix as a sole key
                ret = new Transformation(parseMatrix(obj.get("matrix")));
                if (obj.entrySet().size() > 1)
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
            Vector3f origin = ORIGIN_OPPOSING_CORNER; // TODO: Changing this to ORIGIN_CENTER breaks models, function content needs changing too -C
            Set<String> elements = new HashSet<>(obj.keySet());
            if (obj.has("translation"))
            {
                translation = new Vector3f(parseFloatArray(obj.get("translation"), 3, "Translation"));
                elements.remove("translation");
            }
            if (obj.has("rotation"))
            {
                leftRot = parseRotation(obj.get("rotation"));
                elements.remove("rotation");
            }
            else if (obj.has("left_rotation"))
            {
                leftRot = parseRotation(obj.get("left_rotation"));
                elements.remove("left_rotation");
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
                elements.remove("scale");
            }
            if (obj.has("right_rotation"))
            {
                rightRot = parseRotation(obj.get("right_rotation"));
                elements.remove("right_rotation");
            }
            else if (obj.has("post-rotation"))
            {
                rightRot = parseRotation(obj.get("post-rotation"));
                elements.remove("post-rotation");
            }
            if (obj.has("origin"))
            {
                origin = parseOrigin(obj);
                elements.remove("origin");
            }
            if (!elements.isEmpty()) throw new JsonParseException("TRSR: can either have single 'matrix' key, or a combination of 'translation', 'rotation' OR 'left_rotation', 'scale', 'post-rotation' (legacy) OR 'right_rotation', 'origin'. Found: " + String.join(", ", elements));
            Transformation matrix = new Transformation(translation, leftRot, scale, rightRot);

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
                        ret.mul(parseAxisRotation(a));
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
