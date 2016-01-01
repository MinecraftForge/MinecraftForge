package net.minecraftforge.client.model.animation;

import java.io.IOException;

import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.IModelPart;
import net.minecraftforge.client.model.IModelState;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.TRSRTransformation;
import net.minecraftforge.fml.common.FMLLog;

import com.google.common.base.Objects;
import com.google.common.base.Optional;

/**
 * Various implementations of IClip, and utility methods.
 */
public class Clips
{
    /**
     * Clip that does nothing.
     */
    public static enum IdentityClip implements IClip
    {
        instance;

        public IJointClip apply(IJoint joint)
        {
            return JointClips.IdentityJointClip.instance;
        }
    }

    /**
     * Retrieves the clip from the model.
     */
    public static IClip getModelClipNode(ResourceLocation modelLocation, String clipName)
    {
        IModel model;
        try
        {
            model = ModelLoaderRegistry.getModel(modelLocation);
            if(model instanceof IAnimatedModel)
            {
                Optional<? extends IClip> clip = ((IAnimatedModel)model).getClip(clipName);
                if(clip.isPresent())
                {
                    return clip.get();
                }
                FMLLog.getLogger().error("Unable to find clip " + clipName + " in the model " + modelLocation);
            }
        }
        catch (IOException e)
        {
            FMLLog.getLogger().error("Unable to load model" + modelLocation + " while loading clip " + clipName, e);
        }
        return IdentityClip.instance;
    }

    /**
     * Clip with custom parameterization of the time.
     */
    public static final class TimeClip implements IClip
    {
        private final IClip childClip;
        private final IParameter time;

        public TimeClip(IClip childClip, IParameter time)
        {
            this.childClip = childClip;
            this.time = time;
        }

        public IJointClip apply(final IJoint joint)
        {
            return new IJointClip()
            {
                private final IJointClip parent = childClip.apply(joint);
                public TRSRTransformation apply(float time)
                {
                    return parent.apply(TimeClip.this.time.apply(time));
                }
            };
        }

        @Override
        public int hashCode()
        {
            return Objects.hashCode(childClip, time);
        }

        @Override
        public boolean equals(Object obj)
        {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            TimeClip other = (TimeClip) obj;
            return Objects.equal(childClip, other.childClip) && Objects.equal(time, other.time);
        }
    }

    public static IClipProvider createClipLength(final IClip clip, final IParameter length)
    {
        return new IClipProvider()
        {
            public ClipLength apply(float time)
            {
                return new ClipLength(clip, length.apply(time));
            }
        };
    }

    public static ClipLength createSlerpClip(IClip from, IClip to, IParameter input, float start, float length)
    {
        IParameter progress = new Parameters.LinearParameter(1f / length, -start / length);
        return new ClipLength(new SlerpClip(from, to, input, progress), length);
    }

    public static IClipProvider slerpFactory(final IClip from, final IClip to, final IParameter input, final float length)
    {
        return new IClipProvider()
        {
            public ClipLength apply(float time)
            {
                return createSlerpClip(from, to, input, time, length);
            }
        };
    }

    /**
     * Spherical linear blend between 2 clips.
     */
    public static final class SlerpClip implements IClip
    {
        private final IClip from;
        private final IClip to;
        private final IParameter input;
        private final IParameter progress;

        public SlerpClip(IClip from, IClip to, IParameter input, IParameter progress)
        {
            this.from = from;
            this.to = to;
            this.input = input;
            this.progress = progress;
        }

        public IJointClip apply(IJoint joint)
        {
            IJointClip fromClip = from.apply(joint);
            IJointClip toClip = to.apply(joint);
            return blendClips(joint, fromClip, toClip, input, progress);
        }

        @Override
        public int hashCode()
        {
            return Objects.hashCode(from, to, input, progress);
        }

        @Override
        public boolean equals(Object obj)
        {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            SlerpClip other = (SlerpClip) obj;
            return Objects.equal(from, other.from) &&
                Objects.equal(to, other.to) &&
                Objects.equal(input, other.input) &&
                Objects.equal(progress, other.progress);
        }
    }

    /*public static class AdditiveLerpClip implements IClip
    {
        private final IClip base;
        private final IClip add;
        private final IParameter progress;

        public AdditiveLerpClip(IClip base, IClip add, IParameter progress)
        {
            this.base = base;
            this.add = add;
            this.progress = progress;
        }

        public IJointClip apply(IJoint joint)
        {
            throw new NotImplementedException("AdditiveLerpClip.apply");
        }
    }*/

    private static IJointClip blendClips(final IJoint joint, final IJointClip fromClip, final IJointClip toClip, final IParameter input, final IParameter progress)
    {
        return new IJointClip()
        {
            public TRSRTransformation apply(float time)
            {
                float clipTime = input.apply(time);
                return fromClip.apply(clipTime).slerp(toClip.apply(clipTime), MathHelper.clamp_float(progress.apply(time), 0, 1));
            }
        };
    }

    /**
     * IModelState wrapper for a Clip, sampled at specified time.
     */
    public static IModelState apply(final IClip clip, final float time)
    {
        return new IModelState()
        {
            public Optional<TRSRTransformation> apply(Optional<? extends IModelPart> part)
            {
                if(!part.isPresent() || !(part.get() instanceof IJoint))
                {
                    return Optional.absent();
                }
                IJoint joint = (IJoint)part.get();
                if(!joint.getParent().isPresent())
                {
                    return Optional.of(clip.apply(joint).apply(time).compose(joint.getInvBindPose()));
                }
                return Optional.of(clip.apply(joint.getParent().get()).apply(time).compose(clip.apply(joint).apply(time)).compose(joint.getInvBindPose()));
            }
        };
    }
}
