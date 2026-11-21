package net.minecraftforge.client.textures;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.renderer.texture.SpriteContents;

public interface UpscaleFunction {
    /**
     * Upscale a given image to {@param ratio} times its size
     * An image of N x M will now be N * ratio x M * ratio. In other words, aspect ratio is preserved.
     * @param original original sprite
     * @param upscale upscaled, but empty, image to be written into. Output parameter.
     * @param ratio scale factor that has been applied, necessary for some upscaling algorithms.
     */
    void process(final SpriteContents original, final NativeImage upscale, int ratio);
}
