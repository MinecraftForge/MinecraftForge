package net.minecraftforge.client.event;

import net.minecraft.client.texturepacks.ITexturePack;
import net.minecraftforge.event.Event;

public class TextureLoadEvent extends Event
{

    public final String texture;
    public final ITexturePack pack;

    public TextureLoadEvent(String texture, ITexturePack pack)
    {
        this.texture = texture;
        this.pack = pack;
    }
}
