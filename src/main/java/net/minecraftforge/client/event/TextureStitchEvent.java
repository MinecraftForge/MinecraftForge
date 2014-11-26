package net.minecraftforge.client.event;

import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraft.client.renderer.texture.TextureMap;


public class TextureStitchEvent extends Event
{
    public final TextureMap map;

    public TextureStitchEvent(TextureMap map)
    {
        this.map = map;
    }

    /**
     * Fired when the TextureMap is told to refresh it's stitched texture. 
     * Called after the Stitched list is cleared, but before any blocks or items
     * add themselves to the list.
     */
    public static class Pre extends TextureStitchEvent
    {
        public Pre(TextureMap map){ super(map); }
    }

    /**
     * This event is fired once the texture map has loaded all textures and 
     * stitched them together. All Icons should have there locations defined
     * by the time this is fired.
     */
    public static class Post extends TextureStitchEvent
    {
        public Post(TextureMap map){ super(map); }
    }
}
