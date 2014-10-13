package net.minecraftforge.client.event;

import net.minecraft.client.renderer.EntityRenderer;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.eventhandler.Cancelable;
import cpw.mods.fml.common.eventhandler.Event;

/**
 * Render3DEvent is fired when {@link EntityRenderer.renderWorld} is called. 
 * If a method utilizes this {@link Event} as its parameter, the method will 
 * receive every child event of this class.
 * <br>
 * All children of this event are fired on the {@link MinecraftForge#EVENT_BUS}.<br>
 */
public class Render3DEvent extends Event 
{
    /**
     * The partial render ticks.
     */
    public final float partialTicks;
    
    public Render3DEvent(float partialTicks)
    {
        this.partialTicks = partialTicks;
    }
    
    /**
     * Fired before {@link EntityRenderer.renderWorld} is called. 
     * Canceling this event will result in the method not being called.
     */
    @Cancelable
    public static class Pre extends Render3DEvent
    {
        public Pre(float partialTicks) { super(partialTicks); }        
    }

    /**
     * Fired after {@link EntityRenderer.renderWorld} was called. 
     * {@link wasCanceled} will be true if Pre was canceled, and thus renderWorld was not called. 
     */
    public static class Post extends Render3DEvent
    {
        /**
         * Indicates if {@link Render3DEvent.Pre} was canceled.
         */
        public final boolean wasCanceled;
        
        public Post(float partialTicks, boolean wasCanceled)
        { 
            super(partialTicks);
            this.wasCanceled = wasCanceled;
        }        
    }
}
