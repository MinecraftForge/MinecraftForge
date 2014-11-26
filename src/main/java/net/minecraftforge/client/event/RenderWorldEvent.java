package net.minecraftforge.client.event;

import net.minecraftforge.fml.common.eventhandler.Event;
//import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.world.ChunkCache;

public abstract class RenderWorldEvent extends Event
{
    public final WorldRenderer renderer;
    public final ChunkCache chunkCache;
    //public final RenderBlocks renderBlocks;
    public final int pass;

    public RenderWorldEvent(WorldRenderer renderer, ChunkCache chunkCache, /*RenderBlocks renderBlocks,*/ int pass)
    {
        this.renderer = renderer;
        this.chunkCache = chunkCache;
        //this.renderBlocks = renderBlocks;
        this.pass = pass;
    }

    /**
     * Fired when 16x16x16 chunk area is being redrawn.
     * Fired after GL state is setup, before tessellator is started.
     */
    public static class Pre extends RenderWorldEvent
    {
        public Pre(WorldRenderer renderer, ChunkCache chunkCache, /*RenderBlocks renderBlocks,*/ int pass){ super(renderer, chunkCache, /*renderBlocks,*/ pass); }
    }

    /**
     * Fired after the tessellator is stopped, before the display list is ended.
     */
    public static class Post extends RenderWorldEvent
    {
        public Post(WorldRenderer renderer, ChunkCache chunkCache, /*RenderBlocks renderBlocks,*/ int pass){ super(renderer, chunkCache, /*renderBlocks,*/ pass); }
    }
}
