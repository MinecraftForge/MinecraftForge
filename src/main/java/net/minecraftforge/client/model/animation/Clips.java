package net.minecraftforge.client.model.animation;

import java.io.IOException;

import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.IModelPart;
import net.minecraftforge.client.model.IModelState;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.TRSRTransformation;
import net.minecraftforge.fml.common.FMLLog;

import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.tuple.Pair;

import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Ordering;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

/**
 * Various implementations of IClip, and utility methods.
 */
public final class Clips
{
    /**
     * Clip that does nothing.
     */
    public static enum IdentityClip implements IClip, IStringSerializable
    {
        instance;

        public IJointClip apply(IJoint joint)
        {
            return JointClips.IdentityJointClip.instance;
        }

        public Iterable<Event> pastEvents(float lastPollTime, float time)
        {
            return ImmutableSet.<Event>of();
        }

        public String getName()
        {
            return "identity";
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
                    return new ModelClip(clip.get(), modelLocation, clipName);
                }
                FMLLog.getLogger().error("Unable to find clip " + clipName + " in the model " + modelLocation);
            }
        }
        catch (IOException e)
        {
            FMLLog.getLogger().error("Unable to load model" + modelLocation + " while loading clip " + clipName, e);
        }
        // FIXME: missing clip?
        return new ModelClip(IdentityClip.instance, modelLocation, clipName);
    }

    /**
     * Wrapper for model clips; useful for debugging and serialization;
     */
    public static final class ModelClip implements IClip
    {
        private final IClip childClip;
        private final ResourceLocation modelLocation;
        private final String clipName;

        public ModelClip(IClip childClip, ResourceLocation modelLocation, String clipName)
        {
            this.childClip = childClip;
            this.modelLocation = modelLocation;
            this.clipName = clipName;
        }

        public IJointClip apply(IJoint joint)
        {
            return childClip.apply(joint);
        }

        public Iterable<Event> pastEvents(float lastPollTime, float time)
        {
            return childClip.pastEvents(lastPollTime, time);
        }

        @Override
        public int hashCode()
        {
            return Objects.hashCode(modelLocation, clipName);
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
            ModelClip other = (ModelClip) obj;
            return Objects.equal(modelLocation, other.modelLocation) && Objects.equal(clipName, other.clipName);
        }
    }

    /**
     * Clip with custom parameterization of the time.
     */
    public static final class TimeClip implements IClip
    {
        private final IClip childClip;
        private final ITimeValue time;

        public TimeClip(IClip childClip, ITimeValue time)
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

        public Iterable<Event> pastEvents(float lastPollTime, float time)
        {
            return childClip.pastEvents(this.time.apply(lastPollTime), this.time.apply(time));
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

    /**
     * Spherical linear blend between 2 clips.
     */
    public static final class SlerpClip implements IClip
    {
        private final IClip from;
        private final IClip to;
        private final ITimeValue input;
        private final ITimeValue progress;

        public SlerpClip(IClip from, IClip to, ITimeValue input, ITimeValue progress)
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

        public Iterable<Event> pastEvents(float lastPollTime, float time)
        {
            float clipLastPollTime = input.apply(lastPollTime);
            float clipTime = input.apply(time);
            return Iterables.mergeSorted(ImmutableSet.of(
                from.pastEvents(clipLastPollTime, clipTime),
                to.pastEvents(clipLastPollTime, clipTime)
            ), Ordering.<Event>natural());
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

    private static IJointClip blendClips(final IJoint joint, final IJointClip fromClip, final IJointClip toClip, final ITimeValue input, final ITimeValue progress)
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
    public static Pair<IModelState, Iterable<Event>> apply(final IClip clip, final float lastPollTime, final float time)
    {
        return Pair.<IModelState, Iterable<Event>>of(new IModelState()
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
        }, clip.pastEvents(lastPollTime, time));
    }

    /**
     * Clip + Event, triggers when parameter becomes non-negative.
     */
    public static final class TriggerClip implements IClip
    {
        private final IClip clip;
        private final ITimeValue parameter;
        private final String event;

        public TriggerClip(IClip clip, ITimeValue parameter, String event)
        {
            this.clip = clip;
            this.parameter = parameter;
            this.event = event;
        }

        public IJointClip apply(IJoint joint)
        {
            return clip.apply(joint);
        }

        public Iterable<Event> pastEvents(float lastPollTime, float time)
        {
            if(parameter.apply(lastPollTime) < 0 && parameter.apply(time) >= 0)
            {
                return Iterables.mergeSorted(ImmutableSet.of(
                    clip.pastEvents(lastPollTime, time),
                    ImmutableSet.of(new Event(event, 0))
                ), Ordering.<Event>natural());
            }
            return clip.pastEvents(lastPollTime, time);
        }
    }

    /**
      * Reference to another clip.
      * Should only exist during debugging.
      */
    public static final class ClipReference implements IClip, IStringSerializable
    {
        private final String clipName;
        private final Function<String, IClip> clipResolver;
        private IClip clip;

        public ClipReference(String clipName, Function<String, IClip> clipResolver)
        {
            this.clipName = clipName;
            this.clipResolver = clipResolver;
        }

        private void resolve()
        {
            if(clip == null)
            {
                if(clipResolver != null)
                {
                    clip = clipResolver.apply(clipName);
                }
                if(clip == null)
                {
                    throw new IllegalArgumentException("Couldn't resolve clip " + clipName);
                }
            }
        }

        public IJointClip apply(final IJoint joint)
        {
            resolve();
            return clip.apply(joint);
        }

        public Iterable<Event> pastEvents(float lastPollTime, float time)
        {
            resolve();
            return clip.pastEvents(lastPollTime, time);
        }

        public String getName()
        {
            return clipName;
        }

        @Override
        public int hashCode()
        {
            resolve();
            return clip.hashCode();
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
            ClipReference other = (ClipReference) obj;
            resolve();
            other.resolve();
            return Objects.equal(clip, other.clip);
        }
    }

    public static enum CommonClipTypeAdapterFactory implements TypeAdapterFactory
    {
        INSTANCE;

        private final ThreadLocal<Function<String, IClip>> clipResolver = new ThreadLocal<Function<String, IClip>>();

        public void setClipResolver(Function<String, IClip> clipResolver)
        {
            this.clipResolver.set(clipResolver);
        }

        @SuppressWarnings("unchecked")
        public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type)
        {
            if(type.getRawType() != IClip.class)
            {
                return null;
            }

            final TypeAdapter<ITimeValue> parameterAdapter = gson.getAdapter(ITimeValue.class);

            return (TypeAdapter<T>)new TypeAdapter<IClip>()
            {
                public void write(JsonWriter out, IClip clip) throws IOException
                {
                    // IdentityClip + ClipReference
                    if(clip instanceof IStringSerializable)
                    {
                        out.value("#" + ((IStringSerializable)clip).getName());
                        return;
                    }
                    else if(clip instanceof TimeClip)
                    {
                        out.beginArray();
                        out.value("apply");
                        TimeClip timeClip = (TimeClip)clip;
                        write(out, timeClip.childClip);
                        parameterAdapter.write(out, timeClip.time);
                        out.endArray();
                        return;
                    }
                    else if(clip instanceof SlerpClip)
                    {
                        out.beginArray();
                        out.value("slerp");
                        SlerpClip slerpClip = (SlerpClip)clip;
                        write(out, slerpClip.from);
                        write(out, slerpClip.to);
                        parameterAdapter.write(out, slerpClip.input);
                        parameterAdapter.write(out, slerpClip.progress);
                        out.endArray();
                        return;
                    }
                    else if(clip instanceof TriggerClip)
                    {
                        out.beginArray();
                        out.value("trigger_positive");
                        TriggerClip triggerClip = (TriggerClip)clip;
                        write(out, triggerClip.clip);
                        parameterAdapter.write(out, triggerClip.parameter);
                        out.value(triggerClip.event);
                        out.endArray();
                        return;
                    }
                    else if(clip instanceof ModelClip)
                    {
                        ModelClip modelClip = (ModelClip)clip;
                        out.value(modelClip.modelLocation + "@" + modelClip.clipName);
                        return;
                    }
                    // TODO custom clip writing?
                    throw new NotImplementedException("unknown Clip to json: " + clip);
                }

                public IClip read(JsonReader in) throws IOException
                {
                    switch(in.peek())
                    {
                        case BEGIN_ARRAY:
                            in.beginArray();
                            String type = in.nextString();
                            IClip clip;
                            // TimeClip
                            if("apply".equals(type))
                            {
                                clip = new TimeClip(read(in), parameterAdapter.read(in));
                            }
                            else if("slerp".equals(type))
                            {
                                clip = new SlerpClip(read(in), read(in), parameterAdapter.read(in), parameterAdapter.read(in));
                            }
                            else if("trigger_positive".equals(type))
                            {
                                clip = new TriggerClip(read(in), parameterAdapter.read(in), in.nextString());
                            }
                            else
                            {
                                throw new IOException("Unknown Clip type \"" + type + "\"");
                            }
                            in.endArray();
                            return clip;
                        case STRING:
                            String string = in.nextString();
                            // IdentityClip
                            if(string.equals("#identity"))
                            {
                                return IdentityClip.instance;
                            }
                            // Clip reference
                            if(string.startsWith("#"))
                            {
                                return new ClipReference(string.substring(1), clipResolver.get());
                            }
                            // ModelClip
                            else
                            {
                                int at = string.lastIndexOf('@');
                                String location = string.substring(0, at);
                                String clipName = string.substring(at + 1, string.length());
                                ResourceLocation model;
                                if(location.indexOf('#') != -1)
                                {
                                    model = new ModelResourceLocation(location);
                                }
                                else
                                {
                                    model = new ResourceLocation(location);
                                }
                                return getModelClipNode(model, clipName);
                            }
                        default:
                            throw new IOException("expected Clip, got " + in.peek());
                    }
                }
            };
        }
    }
}