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

package net.minecraftforge.client.model.animation;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.TreeMap;

import javax.annotation.Nullable;

import net.minecraft.client.resources.model.ModelState;
import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;
import com.mojang.math.Transformation;
import com.mojang.math.Vector3f;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.client.model.animation.ModelBlockAnimation.Parameter.Interpolation;
import net.minecraftforge.client.model.animation.ModelBlockAnimation.Parameter.Type;
import net.minecraftforge.client.model.animation.ModelBlockAnimation.Parameter.Variable;
import net.minecraftforge.common.animation.Event;
import net.minecraftforge.common.model.animation.IClip;
import net.minecraftforge.common.model.animation.IJoint;
import net.minecraftforge.common.model.animation.IJointClip;
import net.minecraftforge.common.model.animation.JointClips;
import net.minecraftforge.common.util.JsonUtils;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.collect.UnmodifiableIterator;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.SerializedName;

public class ModelBlockAnimation
{
    private static final Logger LOGGER = LogManager.getLogger();

    private final ImmutableMap<String, ImmutableMap<String, float[]>> joints;
    private final ImmutableMap<String, MBClip> clips;
    private transient ImmutableMultimap<Integer, MBJointWeight> jointIndexMap;

    public ModelBlockAnimation(ImmutableMap<String, ImmutableMap<String, float[]>> joints, ImmutableMap<String, MBClip> clips)
    {
        this.joints = joints;
        this.clips = clips;
    }

    public ImmutableMap<String, ? extends IClip> getClips()
    {
        return clips;
    }

    public ImmutableCollection<MBJointWeight> getJoint(int i)
    {
        if(jointIndexMap == null)
        {
            ImmutableMultimap.Builder<Integer, MBJointWeight> builder = ImmutableMultimap.builder();
            for(Map.Entry<String, ImmutableMap<String, float[]>> info : joints.entrySet())
            {
                ImmutableMap.Builder<Integer, float[]> weightBuilder = ImmutableMap.builder();
                for(Map.Entry<String, float[]> e : info.getValue().entrySet())
                {
                    weightBuilder.put(Integer.parseInt(e.getKey()), e.getValue());
                }
                ImmutableMap<Integer, float[]> weightMap = weightBuilder.build();
                for(Map.Entry<Integer, float[]> e : weightMap.entrySet())
                {
                    builder.put(e.getKey(), new MBJointWeight(info.getKey(), weightMap));
                }
            }
            jointIndexMap = builder.build();
        }
        return jointIndexMap.get(i);
    }

    protected static class MBVariableClip
    {
        private final Variable variable;
        @SuppressWarnings("unused")
        private final Type type;
        private final Interpolation interpolation;
        private final float[] samples;

        public MBVariableClip(Variable variable, Type type, Interpolation interpolation, float[] samples)
        {
            this.variable = variable;
            this.type = type;
            this.interpolation = interpolation;
            this.samples = samples;
        }
    }

    protected static class MBClip implements IClip
    {
        private final boolean loop;
        @SerializedName("joint_clips")
        private final ImmutableMap<String, ImmutableList<MBVariableClip>> jointClipsFlat;
        private transient ImmutableMap<String, MBJointClip> jointClips;
        @SerializedName("events")
        private final ImmutableMap<String, String> eventsRaw;
        private transient TreeMap<Float, Event> events;

        public MBClip(boolean loop, ImmutableMap<String, ImmutableList<MBVariableClip>> clips, ImmutableMap<String, String> events)
        {
            this.loop = loop;
            this.jointClipsFlat = clips;
            this.eventsRaw = events;
        }

        private void initialize()
        {
            if(jointClips == null)
            {
                ImmutableMap.Builder<String, MBJointClip> builder = ImmutableMap.builder();
                for (Map.Entry<String, ImmutableList<MBVariableClip>> e : jointClipsFlat.entrySet())
                {
                    builder.put(e.getKey(), new MBJointClip(loop, e.getValue()));
                }
                jointClips = builder.build();
                events = Maps.newTreeMap();
                if (!eventsRaw.isEmpty())
                {
                    TreeMap<Float, String> times = Maps.newTreeMap();
                    for (String time : eventsRaw.keySet())
                    {
                        times.put(Float.parseFloat(time), time);
                    }
                    float lastTime = Float.POSITIVE_INFINITY;
                    if (loop)
                    {
                        lastTime = times.firstKey();
                    }
                    for (Map.Entry<Float, String> entry : times.descendingMap().entrySet())
                    {
                        float time = entry.getKey();
                        float offset = lastTime - time;
                        if (loop)
                        {
                            offset = 1 - (1 - offset) % 1;
                        }
                        events.put(time, new Event(eventsRaw.get(entry.getValue()), offset));
                    }
                }
            }
        }

        @Override
        public IJointClip apply(IJoint joint)
        {
            initialize();
            if(joint instanceof MBJoint)
            {
                MBJoint mbJoint = (MBJoint)joint;
                //MBJointInfo = jointInfos.
                MBJointClip clip = jointClips.get(mbJoint.getName());
                if(clip != null) return clip;
            }
            return JointClips.IdentityJointClip.INSTANCE;
        }

        @Override
        public Iterable<Event> pastEvents(final float lastPollTime, final float time)
        {
            initialize();
            return new Iterable<Event>()
            {
                @Override
                public Iterator<Event> iterator()
                {
                    return new UnmodifiableIterator<Event>()
                    {
                        private Float curKey;
                        private Event firstEvent;
                        private float stopTime;
                        {
                            if(lastPollTime >= time)
                            {
                                curKey = null;
                            }
                            else
                            {
                                float fractTime = time - (float)Math.floor(time);
                                float fractLastTime = lastPollTime - (float)Math.floor(lastPollTime);
                                // swap if not in order
                                if(fractLastTime > fractTime)
                                {
                                    float tmp = fractTime;
                                    fractTime = fractLastTime;
                                    fractLastTime = tmp;
                                }
                                // need to wrap around, swap again
                                if(fractTime - fractLastTime > .5f)
                                {
                                    float tmp = fractTime;
                                    fractTime = fractLastTime;
                                    fractLastTime = tmp;
                                }

                                stopTime = fractLastTime;

                                curKey = events.floorKey(fractTime);
                                if(curKey == null && loop && !events.isEmpty())
                                {
                                    curKey = events.lastKey();
                                }
                                if(curKey != null)
                                {
                                    float checkCurTime = curKey;
                                    float checkStopTime = stopTime;
                                    if(checkCurTime >= fractTime) checkCurTime--;
                                    if(checkStopTime >= fractTime) checkStopTime--;
                                    float offset = fractTime - checkCurTime;
                                    Event event = events.get(curKey);
                                    if(checkCurTime < checkStopTime)
                                    {
                                        curKey = null;
                                    }
                                    else if(offset != event.offset())
                                    {
                                        firstEvent = new Event(event.event(), offset);
                                    }
                                }
                            }
                        }

                        @Override
                        public boolean hasNext()
                        {
                            return curKey != null;
                        }

                        @Override
                        public Event next()
                        {
                            if(curKey == null)
                            {
                                throw new NoSuchElementException();
                            }
                            Event event;
                            if(firstEvent == null)
                            {
                                event = events.get(curKey);
                            }
                            else
                            {
                                event = firstEvent;
                                firstEvent = null;
                            }
                            curKey = events.lowerKey(curKey);
                            if(curKey == null && loop)
                            {
                                curKey = events.lastKey();
                            }
                            if(curKey != null)
                            {
                                float checkStopTime = stopTime;
                                while(curKey + events.get(curKey).offset() < checkStopTime) checkStopTime--;
                                while(curKey + events.get(curKey).offset() >= checkStopTime + 1) checkStopTime++;
                                if(curKey <= checkStopTime)
                                {
                                    curKey = null;
                                }
                            }
                            return event;
                        }
                    };
                }
            };
        }

        protected static class MBJointClip implements IJointClip
        {
            private final boolean loop;
            private final ImmutableList<MBVariableClip> variables;

            public MBJointClip(boolean loop, ImmutableList<MBVariableClip> variables)
            {
                this.loop = loop;
                this.variables = variables;
                EnumSet<Variable> hadVar = Sets.newEnumSet(Collections.<Variable>emptyList(), Variable.class);
                for(MBVariableClip var : variables)
                {
                    if(hadVar.contains(var.variable))
                    {
                        throw new IllegalArgumentException("duplicate variable: " + var);
                    }
                    hadVar.add(var.variable);
                }
            }

            @Override
            public Transformation apply(float time)
            {
                time -= Math.floor(time);
                Vector3f translation = new Vector3f(0, 0, 0);
                Vector3f scale = new Vector3f(1, 1, 1);
                Vector3f origin = new Vector3f(0, 0, 0);
                Vector3f rotation_axis = new Vector3f(0, 0, 0);
                float rotation_angle = 0;
                for(MBVariableClip var : variables)
                {
                    int length = loop ? var.samples.length : (var.samples.length - 1);
                    float timeScaled = time * length;
                    int s1 = Mth.clamp((int)Math.round(Math.floor(timeScaled)), 0, length - 1);
                    float progress = timeScaled - s1;
                    int s2 = s1 + 1;
                    if(s2 == length && loop) s2 = 0;
                    float value = 0;
                    switch(var.interpolation)
                    {
                        case LINEAR:
                            if(var.variable == Variable.ANGLE)
                            {
                                float v1 = var.samples[s1];
                                float v2 = var.samples[s2];
                                float diff = ((v2 - v1) % 360 + 540) % 360 - 180;
                                value = v1 + diff * progress;
                            }
                            else
                            {
                                value = var.samples[s1] * (1 - progress) + var.samples[s2] * progress;
                            }
                            break;
                        case NEAREST:
                            value = var.samples[progress < .5f ? s1 : s2];
                            break;
                    }
                    switch(var.variable)
                    {
                        case X:
                            translation.setX(value);
                            break;
                        case Y:
                            translation.setY(value);
                            break;
                        case Z:
                            translation.setZ(value);
                            break;
                        case XROT:
                            rotation_axis.setX(value);
                            break;
                        case YROT:
                            rotation_axis.setY(value);
                            break;
                        case ZROT:
                            rotation_axis.setZ(value);
                            break;
                        case ANGLE:
                            rotation_angle = (float)Math.toRadians(value);
                            break;
                        case SCALE:
                            scale.set(value, value, value);
                            break;
                        case XS:
                            scale.setX(value);
                            break;
                        case YS:
                            scale.setY(value);
                            break;
                        case ZS:
                            scale.setX(value);
                            break;
                        case XORIGIN:
                            origin.setX(value - 0.5F);
                            break;
                        case YORIGIN:
                            origin.setY(value - 0.5F);
                            break;
                        case ZORIGIN:
                            origin.setX(value - 0.5F);
                            break;
                    }
                }
                Quaternion rot = new Quaternion(rotation_axis, rotation_angle, false);
                Transformation base = new Transformation(translation, rot, scale, null);
                Vector3f negOrigin = origin.copy();
                negOrigin.mul(-1,-1,-1);
                base = new Transformation(origin, null, null, null).compose(base).compose(new Transformation(negOrigin, null, null, null));
                return base.blockCenterToCorner();
            }
        }
    }

    protected static class MBJoint implements IJoint
    {
        private final String name;

        public MBJoint(String name)
        {
            this.name = name;
        }

        @Override
        public Transformation getInvBindPose()
        {
            return Transformation.identity();
        }

        @Override
        public Optional<? extends IJoint> getParent()
        {
            return Optional.empty();
        }

        public String getName()
        {
            return name;
        }
    }

    protected static class MBJointWeight
    {
        private final String name;
        private final ImmutableMap<Integer, float[]> weights;

        public MBJointWeight(String name, ImmutableMap<Integer, float[]> weights)
        {
            this.name = name;
            this.weights = weights;
        }

        public String getName()
        {
            return name;
        }

        public ImmutableMap<Integer, float[]> getWeights()
        {
            return weights;
        }
    }

    protected static class Parameter
    {
        public static enum Variable
        {
            @SerializedName("offset_x")
            X,
            @SerializedName("offset_y")
            Y,
            @SerializedName("offset_z")
            Z,
            @SerializedName("axis_x")
            XROT,
            @SerializedName("axis_y")
            YROT,
            @SerializedName("axis_z")
            ZROT,
            @SerializedName("angle")
            ANGLE,
            @SerializedName("scale")
            SCALE,
            @SerializedName("scale_x")
            XS,
            @SerializedName("scale_y")
            YS,
            @SerializedName("scale_z")
            ZS,
            @SerializedName("origin_x")
            XORIGIN,
            @SerializedName("origin_y")
            YORIGIN,
            @SerializedName("origin_z")
            ZORIGIN;
        }

        public static enum Type
        {
            @SerializedName("uniform")
            UNIFORM;
        }

        public static enum Interpolation
        {
            @SerializedName("linear")
            LINEAR,
            @SerializedName("nearest")
            NEAREST;
        }
    }

    @Nullable
    public Transformation getPartTransform(ModelState state, BlockElement part, int i)
    {
        return getPartTransform(state, i);
    }

    @Nullable
    public Transformation getPartTransform(ModelState state, int i)
    {
        ImmutableCollection<MBJointWeight> infos = getJoint(i);
        if(!infos.isEmpty())
        {
            Matrix4f m = new Matrix4f(), tmp;
            float weight = 0;
            for(MBJointWeight info : infos)
            {
                if(info.getWeights().containsKey(i))
                {
                    ModelBlockAnimation.MBJoint joint = new ModelBlockAnimation.MBJoint(info.getName());
                    Transformation trOp = state.getPartTransformation(joint);
                    if(!trOp.isIdentity())
                    {
                        float w = info.getWeights().get(i)[0];
                        tmp = trOp.getMatrix();
                        tmp.multiply(w);
                        m.add(tmp);
                        weight += w;
                    }
                }
            }
            if(weight > 1e-5)
            {
                m.multiply(1f / weight);
                return new Transformation(m);
            }
        }
        return null;
    }

    /**
     * Load armature associated with a vanilla model.
     */
    public static ModelBlockAnimation loadVanillaAnimation(ResourceManager manager, ResourceLocation armatureLocation)
    {
        try
        {
            try (Resource resource = manager.getResource(armatureLocation))
            {
                ModelBlockAnimation mba = mbaGson.fromJson(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8), ModelBlockAnimation.class);
                //String json = mbaGson.toJson(mba);
                return mba;
            }
            catch(FileNotFoundException e)
            {
                // this is normal. FIXME: error reporting?
                return defaultModelBlockAnimation;
            }
        }
        catch(IOException | JsonParseException e)
        {
            LOGGER.error("Exception loading vanilla model animation {}, skipping", armatureLocation, e);
            return defaultModelBlockAnimation;
        }
    }

    private static final Gson mbaGson = new GsonBuilder()
        .registerTypeAdapter(ImmutableList.class, JsonUtils.ImmutableListTypeAdapter.INSTANCE)
        .registerTypeAdapter(ImmutableMap.class, JsonUtils.ImmutableMapTypeAdapter.INSTANCE)
        .setPrettyPrinting()
        .enableComplexMapKeySerialization()
        .disableHtmlEscaping()
        .create();

    private static final ModelBlockAnimation defaultModelBlockAnimation = new ModelBlockAnimation(ImmutableMap.<String, ImmutableMap<String, float[]>>of(), ImmutableMap.<String, ModelBlockAnimation.MBClip>of());
}
