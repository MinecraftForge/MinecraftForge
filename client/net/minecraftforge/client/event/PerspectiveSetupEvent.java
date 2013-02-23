/*
 * This software is provided under the terms of the Minecraft Forge Public
 * License v1.0.
 */

package net.minecraftforge.client.event;

import net.minecraftforge.client.event.PerspectiveSetupEvent.RenderTarget;
import net.minecraftforge.event.Event;

/**
 * 
 * @author zsawyer
 */
public abstract class PerspectiveSetupEvent extends Event {

    public enum RenderTarget {
        WORLD, HAND
    }

    public int viewCounter;
    public float xCorrectionFactor;
    public RenderTarget target;

    public PerspectiveSetupEvent(RenderTarget target, int viewCounter, float xCorrectionFactor)
    {
        super();
        this.viewCounter = viewCounter;
        this.xCorrectionFactor = xCorrectionFactor;
        this.target = target;
    }

    /**
     * 
     * @author zsawyer
     */
    public static class Before extends PerspectiveSetupEvent {

        public Before(RenderTarget target, int viewCounter, float xCorrectionFactor)
        {
            super(target, viewCounter, xCorrectionFactor);
        }
    }

    /**
     * 
     * @author zsawyer
     */
    public static class After extends PerspectiveSetupEvent {

        public After(RenderTarget target, int viewCounter, float xCorrectionFactor)
        {
            super(target, viewCounter, xCorrectionFactor);
        }
    }
}
