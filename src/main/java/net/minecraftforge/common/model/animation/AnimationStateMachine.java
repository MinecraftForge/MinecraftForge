/*
 * Minecraft Forge
 * Copyright (c) 2016.
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

package net.minecraftforge.common.model.animation;

import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Supplier;
import com.google.common.collect.*;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.animation.Event;
import net.minecraftforge.common.animation.ITimeValue;
import net.minecraftforge.common.animation.TimeValues;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.util.JsonUtils;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.logging.log4j.Level;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.gson.annotations.SerializedName;

import javax.annotation.Nullable;

public final class AnimationStateMachine implements IAnimationStateMachine
{
    private final ImmutableMap<String, ITimeValue> parameters;
    private final ImmutableMap<String, IClip> clips;
    private final ImmutableList<String> states;
    private final ImmutableMultimap<String, String> transitions;
    @SerializedName("start_state")
    private final String startState;

    private transient boolean shouldHandleSpecialEvents;
    private transient String currentStateName;
    private transient IClip currentState;
    private transient float lastPollTime;

    private static final LoadingCache<Triple<? extends IClip, Float, Float>, Pair<IModelState, Iterable<Event>>> clipCache = CacheBuilder.newBuilder()
        .maximumSize(100)
        .expireAfterWrite(100, TimeUnit.MILLISECONDS)
        .build(new CacheLoader<Triple<? extends IClip, Float, Float>, Pair<IModelState, Iterable<Event>>>()
        {
            public Pair<IModelState, Iterable<Event>> load(Triple<? extends IClip, Float, Float> key) throws Exception
            {
                return Clips.apply(key.getLeft(), key.getMiddle(), key.getRight());
            }
        });

    @Deprecated
    public AnimationStateMachine(ImmutableMap<String, ITimeValue> parameters, ImmutableMap<String, IClip> clips, ImmutableList<String> states, ImmutableMap<String, String> transitions, String startState)
    {
        this(parameters, clips, states, ImmutableMultimap.copyOf(Multimaps.newSetMultimap(Maps.transformValues(transitions, new Function<String, Collection<String>>()
        {
            public Collection<String> apply(String input)
            {
                return ImmutableSet.of(input);
            }
        }), new Supplier<Set<String>>()
        {
            public Set<String> get()
            {
                return Sets.newHashSet();
            }
        })), startState);
    }

    public AnimationStateMachine(ImmutableMap<String, ITimeValue> parameters, ImmutableMap<String, IClip> clips, ImmutableList<String> states, ImmutableMultimap<String, String> transitions, String startState)
    {
        this.parameters = parameters;
        this.clips = clips;
        this.states = states;
        this.transitions = transitions;
        this.startState = startState;
    }

    /**
     * post-loading initialization hook.
     */
    void initialize()
    {
        if(parameters == null)
        {
            throw new JsonParseException("Animation State Machine should contain \"parameters\" key.");
        }
        if(clips == null)
        {
            throw new JsonParseException("Animation State Machine should contain \"clips\" key.");
        }
        if(states == null)
        {
            throw new JsonParseException("Animation State Machine should contain \"states\" key.");
        }
        if(transitions == null)
        {
            throw new JsonParseException("Animation State Machine should contain \"transitions\" key.");
        }
        shouldHandleSpecialEvents = true;
        lastPollTime = Float.NEGATIVE_INFINITY;
        // setting the starting state
        IClip state = clips.get(startState);
        if(!clips.containsKey(startState) || !states.contains(startState))
        {
            throw new IllegalStateException("unknown state: " + startState);
        }
        currentStateName = startState;
        currentState = state;
    }

    public Pair<IModelState, Iterable<Event>> apply(float time)
    {
        if(lastPollTime == Float.NEGATIVE_INFINITY)
        {
            lastPollTime = time;
        }
        Pair<IModelState, Iterable<Event>> pair = clipCache.getUnchecked(Triple.of(currentState, lastPollTime, time));
        lastPollTime = time;
        boolean shouldFilter = false;
        if(shouldHandleSpecialEvents)
        {
            for(Event event : ImmutableList.copyOf(pair.getRight()).reverse())
            {
                if(event.event().startsWith("!"))
                {
                    shouldFilter = true;
                    if(event.event().startsWith("!transition:"))
                    {
                        String newState = event.event().substring("!transition:".length());
                        transition(newState);
                    }
                    else
                    {
                        System.out.println("Unknown special event \"" + event.event() + "\", ignoring");
                    }
                }
            }
        }
        if(!shouldFilter)
        {
            return pair;
        }
        return Pair.of(pair.getLeft(), Iterables.filter(pair.getRight(), new Predicate<Event>()
        {
            public boolean apply(Event event)
            {
                return !event.event().startsWith("!");
            }
        }));
    }

    public void transition(String newState)
    {
        IClip nc = clips.get(newState);
        if(!clips.containsKey(newState) || !states.contains(newState))
        {
            throw new IllegalStateException("unknown state: " + newState);
        }
        if(!transitions.containsEntry(currentStateName, newState))
        {
            throw new IllegalArgumentException("no transition from current clip \"" + currentStateName + "\" to the clip \"" + newState + "\" found.");
        }
        currentStateName = newState;
        currentState = nc;
    }

    public String currentState()
    {
        return currentStateName;
    }

    public void shouldHandleSpecialEvents(boolean value)
    {
        shouldHandleSpecialEvents = true;
    }

    /**
     * Load a new instance if AnimationStateMachine at specified location, with specified custom parameters.
     */
    @SideOnly(Side.CLIENT)
    public static IAnimationStateMachine load(IResourceManager manager, ResourceLocation location, ImmutableMap<String, ITimeValue> customParameters)
    {
        try
        {
            ClipResolver clipResolver = new ClipResolver();
            ParameterResolver parameterResolver = new ParameterResolver(customParameters);
            Clips.CommonClipTypeAdapterFactory.INSTANCE.setClipResolver(clipResolver);
            TimeValues.CommonTimeValueTypeAdapterFactory.INSTANCE.setValueResolver(parameterResolver);
            IResource resource = manager.getResource(location);
            AnimationStateMachine asm = asmGson.fromJson(new InputStreamReader(resource.getInputStream(), "UTF-8"), AnimationStateMachine.class);
            clipResolver.asm = asm;
            parameterResolver.asm = asm;
            asm.initialize();
            //String json = asmGson.toJson(asm);
            //System.out.println(location + ": " + json);
            return asm;
        }
        catch(IOException e)
        {
            FMLLog.log.error("Exception loading Animation State Machine {}, skipping", location, e);
            return missing;
        }
        catch(JsonParseException e)
        {
            FMLLog.log.error("Exception loading Animation State Machine {}, skipping", location, e);
            return missing;
        }
        finally
        {
            Clips.CommonClipTypeAdapterFactory.INSTANCE.setClipResolver(null);
            TimeValues.CommonTimeValueTypeAdapterFactory.INSTANCE.setValueResolver(null);
        }
    }

    private static final AnimationStateMachine missing = new AnimationStateMachine(
        ImmutableMap.<String, ITimeValue>of(),
        ImmutableMap.of("missingno", (IClip)Clips.IdentityClip.INSTANCE),
        ImmutableList.of("missingno"),
        ImmutableMultimap.<String, String>of(),
        "missingno");

    static
    {
        missing.initialize();
    }

    public static AnimationStateMachine getMissing()
    {
        return missing;
    }

    private static final class ClipResolver implements Function<String, IClip>
    {
        private AnimationStateMachine asm;

        public IClip apply(String name)
        {
            return asm.clips.get(name);
        }
    }

    private static final class ParameterResolver implements Function<String, ITimeValue>
    {
        private final ImmutableMap<String, ITimeValue> customParameters;
        private AnimationStateMachine asm;

        public ParameterResolver(ImmutableMap<String, ITimeValue> customParameters)
        {
            this.customParameters = customParameters;
        }

        public ITimeValue apply(String name)
        {
            if(asm.parameters.containsKey(name))
            {
                return asm.parameters.get(name);
            }
            return customParameters.get(name);
        }
    }

    private static final Gson asmGson = new GsonBuilder()
        .registerTypeAdapter(ImmutableList.class, JsonUtils.ImmutableListTypeAdapter.INSTANCE)
        .registerTypeAdapter(ImmutableMap.class, JsonUtils.ImmutableMapTypeAdapter.INSTANCE)
        .registerTypeAdapterFactory(Clips.CommonClipTypeAdapterFactory.INSTANCE)
        //.registerTypeAdapterFactory(ClipProviders.CommonClipProviderTypeAdapterFactory.INSTANCE)
        .registerTypeAdapterFactory(TimeValues.CommonTimeValueTypeAdapterFactory.INSTANCE)
        .registerTypeAdapterFactory(TransitionsAdapterFactory.INSTANCE)
        .setPrettyPrinting()
        .enableComplexMapKeySerialization()
        .disableHtmlEscaping()
        .create();

    private enum TransitionsAdapterFactory implements TypeAdapterFactory
    {
        INSTANCE;

        @SuppressWarnings("unchecked")
        @Nullable
        public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type)
        {
            if(type.getRawType() != ImmutableMultimap.class || !(type.getType() instanceof ParameterizedType))
            {
                return null;
            }
            final Type[] typeArguments = ((ParameterizedType) type.getType()).getActualTypeArguments();
            if(typeArguments.length != 2 || typeArguments[0] != String.class || typeArguments[1] != String.class)
            {
                return null;
            }
            final TypeAdapter<Map<String, Collection<String>>> mapAdapter = gson.getAdapter(new TypeToken<Map<String, Collection<String>>>(){});
            final TypeAdapter<Collection<String>> collectionAdapter = gson.getAdapter(new TypeToken<Collection<String>>(){});
            return (TypeAdapter<T>)new TypeAdapter<ImmutableMultimap<String, String>>()
            {
                @Override
                public void write(JsonWriter out, ImmutableMultimap<String, String> value) throws IOException
                {
                    mapAdapter.write(out, value.asMap());
                }

                @Override
                public ImmutableMultimap<String, String> read(JsonReader in) throws IOException
                {
                    ImmutableMultimap.Builder<String, String> builder = ImmutableMultimap.builder();
                    in.beginObject();
                    while(in.hasNext())
                    {
                        String key = in.nextName();
                        switch(in.peek())
                        {
                            case STRING:
                                builder.put(key, in.nextString());
                                break;
                            case BEGIN_ARRAY:
                                builder.putAll(key, collectionAdapter.read(in));
                                break;
                            default:
                                throw new JsonParseException("Expected String or Array, got " + in.peek());
                        }
                    }
                    in.endObject();
                    return builder.build();
                }
            };
        }
    }
}
