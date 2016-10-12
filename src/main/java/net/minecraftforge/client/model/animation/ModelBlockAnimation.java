package net.minecraftforge.client.model.animation;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Map;

import javax.vecmath.AxisAngle4f;
import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import net.minecraft.client.renderer.block.model.BlockPart;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.TRSRTransformation;
import net.minecraftforge.client.model.animation.ModelBlockAnimation.Parameter.Interpolation;
import net.minecraftforge.client.model.animation.ModelBlockAnimation.Parameter.Type;
import net.minecraftforge.client.model.animation.ModelBlockAnimation.Parameter.Variable;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Sets;

public class ModelBlockAnimation
{
    private final ResourceLocation location;
    private final ImmutableMap<String, MBJointInfo> jointInfos;
    private final ImmutableMap<String, Optional<MBClip>> clips;
    private final ImmutableMultimap<Integer, MBJointInfo> joints;

    public ModelBlockAnimation(ResourceLocation location, ImmutableMap<String, MBJointInfo> jointInfos, ImmutableMap<String, Optional<MBClip>> clips)
    {
        this.location = location;
        this.jointInfos = jointInfos;
        this.clips = clips;
        ImmutableMultimap.Builder<Integer, MBJointInfo> builder = ImmutableMultimap.builder();
        for(MBJointInfo info : jointInfos.values())
        {
            for(Map.Entry<Integer, float[]> e : info.getWeights().entrySet())
            {
                builder.put(e.getKey(), info);
            }
        }
        joints = builder.build();
    }

    public ImmutableMap<String, Optional<MBClip>> getClips()
    {
        return clips;
    }

    public ImmutableCollection<MBJointInfo> getJoint(int i)
    {
        return joints.get(i);
    }

    public static class MBVariableClip
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

    public static class MBJointClip implements IJointClip
    {
        private final MBJointInfo jointInfo;
        private final boolean loop;
        private final ImmutableList<MBVariableClip> vars;

        public MBJointClip(MBJointInfo jointInfo, boolean loop, ImmutableList<MBVariableClip> vars)
        {
            this.jointInfo = jointInfo;
            this.loop = loop;
            this.vars = vars;
            EnumSet<Variable> hadVar = Sets.newEnumSet(Collections.<Variable>emptyList(), Variable.class);
            for(MBVariableClip var : vars)
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
            for(MBVariableClip var : vars)
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
                    case Linear:
                        if(var.variable == Variable.AR)
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
                    case Nearest:
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
                    case XR:
                        rotation.x = value;
                        break;
                    case YR:
                        rotation.y = value;
                        break;
                    case ZR:
                        rotation.z = value;
                        break;
                    case AR:
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

        public MBJointInfo getJointInfo()
        {
            return jointInfo;
        }
    }

    public static class MBClip implements IClip
    {
        private final ImmutableMap<String, MBJointClip> jointClips;

        public MBClip(ImmutableList<MBJointClip> jointClips)
        {
            ImmutableMap.Builder<String, MBJointClip> builder = ImmutableMap.builder();
            for(MBJointClip clip : jointClips)
            {
                builder.put(clip.getJointInfo().getName(), clip);
            }
            this.jointClips = builder.build();
        }

        @Override
        public IJointClip apply(IJoint joint)
        {
            if(joint instanceof MBJoint)
            {
                MBJoint mbJoint = (MBJoint)joint;
                //MBJointInfo = jointInfos.
                MBJointClip clip = jointClips.get(mbJoint.getName());
                if(clip != null) return clip;
            }
            return JointClips.IdentityJointClip.instance;
        }
    }

    public static class MBJoint implements IJoint
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

    public static class MBJointInfo
    {
        private final String name;
        private final ImmutableMap<Integer, float[]> weights;

        public MBJointInfo(String name, ImmutableMap<Integer, float[]> weights)
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

    public static class Parameter
    {
        public static enum Variable
        {
            X,
            Y,
            Z,
            XR,
            YR,
            ZR,
            AR,
            SCALE,
            XS,
            YS,
            ZS;
        }

        public static enum Type
        {
            Uniform;
        }

        public static enum Interpolation
        {
            Linear, Nearest;
        }
    }
}
