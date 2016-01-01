package net.minecraftforge.client.model.animation;

import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraftforge.client.model.IModelState;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableTable;

/**
 * Main controller for the animation logic.
 * API is a simple state machine.
 */
public class AnimationStateMachine
{
    private final ImmutableMap<String, ? extends IClip> clips;
    private final ImmutableList<String> states;
    private final ImmutableTable<IClip, IClip, ? extends IClipProvider> transitions;

    private static final LoadingCache<Pair<? extends IClip, Float>, IModelState> clipCache = CacheBuilder.newBuilder()
        .maximumSize(100)
        .expireAfterWrite(100, TimeUnit.MILLISECONDS)
        .build(new CacheLoader<Pair<? extends IClip, Float>, IModelState>()
        {
            public IModelState load(Pair<? extends IClip, Float> key) throws Exception
            {
                IModelState state = Clips.apply(key.getLeft(), key.getRight());
                return state;
            }
        });

    public AnimationStateMachine(ImmutableMap<String, IClip> clips, ImmutableList<String> states, ImmutableTable<IClip, IClip, ? extends IClipProvider> transitions, String startState)
    {
        this.clips = clips;
        this.states = states;
        this.transitions = transitions;
        IClip state = clips.get(startState);
        if(!clips.containsKey(startState) || !states.contains(startState))
        {
            throw new IllegalStateException("unknown state: " + startState);
        }
        this.currentStateName = startState;
        this.currentState = state;
    }

    private String currentStateName;
    private IClip currentState;
    private ClipLength currentTransition = null;
    private boolean transitioning = false;
    private float transitionStart = Float.MIN_VALUE;

    /**
     * Sample the state at the current time.
     */
    public IModelState apply(float time)
    {
        checkTransitionEnd(time);
        if(transitioning)
        {
            return clipCache.getUnchecked(Pair.of(currentTransition, time));
        }
        return clipCache.getUnchecked(Pair.of(currentState, time));
    }

    /**
     * Initiate transition to a new state.
     * If another transition is in progress, IllegalStateException is thrown.
     */
    public void transition(float currentTime, String newClip)
    {
        checkTransitionEnd(currentTime);
        System.out.println("transition " + currentTime + " " + newClip);
        if(transitioning)
        {
            throw new IllegalStateException("can't transition in a middle of another transition.");
        }
        IClip nc = clips.get(newClip);
        if(!clips.containsKey(newClip) || !states.contains(newClip))
        {
            throw new IllegalStateException("unknown state: " + newClip);
        }
        if(!transitions.contains(currentState, nc))
        {
            throw new IllegalArgumentException("no transition from current clip to the clip " + newClip + " found.");
        }
        currentTransition = transitions.get(currentState, nc).apply(currentTime);
        currentStateName = newClip;
        currentState = nc;
        transitionStart = currentTime;
        transitioning = true;
    }

    private void checkTransitionEnd(float time)
    {
        if(transitioning && time > transitionStart + currentTransition.getLength())
        {
            currentTransition = null;
            transitioning = false;
            System.out.println("transition end " + time);
        }
    }

    /**
     * Check if another transition is in progress.
     */
    public boolean transitioning()
    {
        return transitioning;
    }

    /**
     * Get the name of the current state.
     */
    public String currentState()
    {
        if(transitioning)
        {
            // FIXME
            return null;
        }
        return currentStateName;
    }
}
