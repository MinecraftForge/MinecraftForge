package net.minecraft.src;

import net.minecraft.src.forge.ITextureProvider;

public class InfiTexture extends Item
    implements ITextureProvider
{
    public String texturePath;

    public InfiTexture(int i, String s)
    {
        super(i);
        texturePath = s;
        setHasSubtypes(true);
        setMaxDamage(0);
    }

    public String getTextureFile()
    {
        return texturePath;
    }
}
