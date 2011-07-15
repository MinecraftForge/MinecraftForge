package net.minecraft.src.forge;

/**
 * This interface has to be implemented by a Block, and provide custom texture
 * capabilities.
 */
public interface ITextureProvider {

    /**
     * This interface has to return the path to a file that is the same size as
     * terrain.png, but not named terrain.png. If the block implements
     * getRenderType() by returning any of the following:
     * BuildCraftCore.customTextureModel BuildCraftCore.pipeModel it will use
     * that terrain file to render texture instead of the default terrain.png
     * one.
     */
    public String getTextureFile();

}
