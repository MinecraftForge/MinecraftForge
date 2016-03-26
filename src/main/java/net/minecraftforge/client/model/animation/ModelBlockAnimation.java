package net.minecraftforge.client.model.animation;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.TreeMap;

import javax.vecmath.AxisAngle4f;
import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import net.minecraft.client.renderer.block.model.BlockPart;
import net.minecraft.util.MathHelper;
import net.minecraftforge.client.model.IModelState;
import net.minecraftforge.client.model.TRSRTransformation;
import net.minecraftforge.client.model.animation.ModelBlockAnimation.Parameter.Interpolation;
import net.minecraftforge.client.model.animation.ModelBlockAnimation.Parameter.Type;
import net.minecraftforge.client.model.animation.ModelBlockAnimation.Parameter.Variable;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.collect.UnmodifiableIterator;
import com.google.gson.annotations.SerializedName;

public class ModelBlockAnimation
{
    private final ImmutableMap<String, ImmutableMap<String, float[]>> joints;
    private final ImmutableMap<String, MBClip> clips;
    private transient ImmutableMultimap<Integer, MBJointWeight> jointIndexMap;

    public ModelBlockAnimation(ImmutableMap<String, ImmutableMap<String, float[]>> joints, ImmutableMap<String, MBClip> clips)
    {
        this.joints = joints;
        this.clips = clips;
    }

    public ImmutableMap<String, MBClip> getClips()
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
            return JointClips.IdentityJointClip.instance;
        }

        @Override
        public Iterable<Event> pastEvents(final float lastPollTime, final float time)
        {
            initialize();
            return new Iterable<Event>()
            {
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

            public TRSRTransformation apply(float time)
            {
                time -= Math.floor(time);
                Vector3f translation = new Vector3f(0, 0, 0);
                Vector3f scale = new Vector3f(1, 1, 1);
                AxisAngle4f rotation = new AxisAngle4f(0, 0, 0, 0);
                for(MBVariableClip var : variables)
                {
                    int length = loop ? var.samples.length : (var.samples.length - 1);
                    float timeScaled = time * length;
                    int s1 = MathHelper.clamp_int((int)Math.round(Math.floor(timeScaled)), 0, length - 1);
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
                            translation.x = value;
                            break;
                        case Y:
                            translation.y = value;
                            break;
                        case Z:
                            translation.z = value;
                            break;
                        case XROT:
                            rotation.x = value;
                            break;
                        case YROT:
                            rotation.y = value;
                            break;
                        case ZROT:
                            rotation.z = value;
                            break;
                        case ANGLE:
                            rotation.angle = (float)Math.toRadians(value);
                            break;
                        case SCALE:
                            scale.x = scale.y = scale.z = value;
                            break;
                        case XS:
                            scale.x = value;
                            break;
                        case YS:
                            scale.y = value;
                            break;
                        case ZS:
                            scale.z = value;
                            break;
                    }
                }
                Quat4f rot = new Quat4f();
                rot.set(rotation);
                return TRSRTransformation.blockCenterToCorner(new TRSRTransformation(translation, rot, scale, null));
            }
        }
    }

    protected static class MBJoint implements IJoint
    {
        private final String name;
        private final TRSRTransformation invBindPose;

        public MBJoint(String name, BlockPart part)
        {
            this.name = name;
            if(part.partRotation != null)
            {
                float x = 0, y = 0, z = 0;
                switch(part.partRotation.axis)
                {
                    case X:
                        x = 1;
                    case Y:
                        y = 1;
                    case Z:
                        z = 1;
                }
                Quat4f rotation = new Quat4f();
                rotation.set(new AxisAngle4f(x, y, z, 0));
                Matrix4f m = new TRSRTransformation(
                    TRSRTransformation.toVecmath(part.partRotation.origin),
                    rotation,
                    null,
                    null).getMatrix();
                m.invert();
                invBindPose = new TRSRTransformation(m);
            }
            else
            {
                invBindPose = TRSRTransformation.identity();
            }
        }

        public TRSRTransformation getInvBindPose()
        {
            return invBindPose;
        }

        public Optional<? extends IJoint> getParent()
        {
            return Optional.absent();
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
            ZS;
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

    public TRSRTransformation getPartTransform(IModelState state, BlockPart part, int i)
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
                    ModelBlockAnimation.MBJoint joint = new ModelBlockAnimation.MBJoint(info.getName(), part);
                    Optional<TRSRTransformation> trOp = state.apply(Optional.of(joint));
                    if(trOp.isPresent() && trOp.get() != TRSRTransformation.identity())
                    {
                        float w = info.getWeights().get(i)[0];
                        tmp = trOp.get().getMatrix();
                        tmp.mul(w);
                        m.add(tmp);
                        weight += w;
                    }
                }
            }
            if(weight > 1e-5)
            {
                m.mul(1f / weight);
                return new TRSRTransformation(m);
            }
        }
        return null;
    }
}
