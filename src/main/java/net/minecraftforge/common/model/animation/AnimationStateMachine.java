package net.minecraftforge.common.model.animation;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

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
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.SerializedName;

public final class AnimationStateMachine implements IAnimationStateMachine
{
    private final ImmutableMap<String, ITimeValue> parameters;
    private final ImmutableMap<String, IClip> clips;
    private final ImmutableList<String> states;
    private final ImmutableMap<String, String> transitions;
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

    public AnimationStateMachine(ImmutableMap<String, ITimeValue> parameters, ImmutableMap<String, IClip> clips, ImmutableList<String> states, ImmutableMap<String, String> transitions, String startState)
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
        if(!transitions.get(currentStateName).equals(newState))
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
            FMLLog.log(Level.ERROR, e, "Exception loading Animation State Machine %s, skipping", location);
            return missing;
        }
        catch(JsonParseException e)
        {
            FMLLog.log(Level.ERROR, e, "Exception loading Animation State Machine %s, skipping", location);
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
        ImmutableMap.<String, String>of(),
        "missingno");

    static
    {
        missing.initialize();
    }

    public static final AnimationStateMachine getMissing()
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
        .setPrettyPrinting()
        .enableComplexMapKeySerialization()
        .disableHtmlEscaping()
        .create();
}
