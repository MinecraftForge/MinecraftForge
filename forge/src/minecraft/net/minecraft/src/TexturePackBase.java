package net.minecraft.src;

import java.io.IOException;
import java.io.InputStream;
import net.minecraft.client.Minecraft;

public abstract class TexturePackBase
{
    /**
     * The file name of the texture pack, or Default if not from a custom texture pack.
     */
    public String texturePackFileName;

    /**
     * The first line of the texture pack description (read from the pack.txt file)
     */
    public String firstDescriptionLine;

    /**
     * The second line of the texture pack description (read from the pack.txt file)
     */
    public String secondDescriptionLine;

    /** Texture pack ID */
    public String texturePackID;

    public void func_6482_a() {}

    /**
     * Closes the zipfile associated to this texture pack. Does nothing for the default texture pack.
     */
    public void closeTexturePackFile() {}

    public void func_6485_a(Minecraft par1Minecraft) throws IOException {}

    /**
     * Unbinds the thumbnail texture for texture pack screen
     */
    public void unbindThumbnailTexture(Minecraft par1Minecraft) {}

    /**
     * binds the texture corresponding to the pack's thumbnail image
     */
    public void bindThumbnailTexture(Minecraft par1Minecraft) {}

    /**
     * Gives a texture resource as InputStream.
     */
    public InputStream getResourceAsStream(String par1Str)
    {
        return TexturePackBase.class.getResourceAsStream(par1Str);
    }
}
