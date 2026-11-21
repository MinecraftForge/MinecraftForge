package net.minecraftforge.client.textures;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.logging.LogUtils;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.resources.metadata.animation.FrameSize;
import net.minecraft.util.Mth;
import net.minecraftforge.common.ForgeConfig;

import java.util.*;

public class UndersizedSpriteUpscaler {
    public static List<SpriteContents> correctUndersizedSprites(final List<SpriteContents> sprites, final int expectedMipLevels) {
        if (expectedMipLevels == 0) return sprites; // Skip for no mips.
        int lowestOneBit = 1 << expectedMipLevels;
        List<SpriteContents> newSpriteList = new ArrayList<>(sprites);
        List<SpriteTargetData> undersizedSprites = new ArrayList<>();
        for (int i = 0; i < sprites.size(); i++) {
            var spriteInfo = sprites.get(i);
            int lowestTextureBit = Math.min(Integer.lowestOneBit(spriteInfo.width()), Integer.lowestOneBit(spriteInfo.height()));
            if (lowestTextureBit < lowestOneBit) { // Same as vanilla's check, but we mark the bad sprites.
                undersizedSprites.add(new SpriteTargetData(i, Mth.log2(lowestTextureBit)));
                LogUtils.getLogger().debug("Found undersized sprite: {} had dimensions {} x {} which would limit mip level from {} to {}. Correcting.",
                        spriteInfo.name(),
                        spriteInfo.width(),
                        spriteInfo.height(),
                        Mth.log2(lowestOneBit),
                        Mth.log2(lowestTextureBit));
            }
        }
        updateSprites(newSpriteList, undersizedSprites, expectedMipLevels);
        return newSpriteList;
    }

    private static void updateSprites(List<SpriteContents> sprites, List<SpriteTargetData> targets, int targetMips) {
        for (SpriteTargetData target : targets) {
            var original = sprites.get(target.index);
            // Scale the sprite only by what is needed
            int newWidth = original.width() << (Math.max(0, targetMips - target.availableMips));
            int newHeight = original.height() << (Math.max(0, targetMips - target.availableMips));
            var newSpriteImage = new NativeImage(newWidth,newHeight,false);
            var ratio = (int) Mth.sqrt(newSpriteImage.getPixels().length / original.getOriginalImage().getPixels().length);
            ForgeConfig.CLIENT.mipmapUpscalingMethod.get().method.process(sprites.get(target.index), newSpriteImage, ratio);
            var newSprite =new SpriteContents(original.name(),
                    new FrameSize(newWidth, newHeight),
                    newSpriteImage);
            sprites.set(target.index, newSprite);
        }
    }

    // Option for no image. Will result in full black missing texture if texture actually used.
    // Only use in the case of textures not actually being used. e.g. if a resource pack
    // copies the entity atlas into the blocks atlas, which is dumb but it's the reason
    // that this class exists in the first place.
    private static void noAlgo(final SpriteContents original, final NativeImage upscale, int ratio) {}

    // Strict image multiply. Preserves original image, just bigger.
    private static void scaleMultiply(final SpriteContents original, final NativeImage upscale, int ratio) {
        var orig = original.getOriginalImage();
        for (int i = 0; i < upscale.getWidth(); i++) {
            for (int j = 0; j < upscale.getHeight(); j++) {
                upscale.setPixel(i, j, orig.getPixel(i / ratio, j / ratio));
            }
        }
    }

    // AdvMAME2x. Applies log2(ratio) times to reach the needed size
    // Let A,B,C,D,P be pixels in the original image.
    //  A
    //C P B
    //  D
    // Let 1,2,3,4 be the output pixels for a given pass.
    // 1 2
    // 3 4
    // Algo is as follows:
    // Initialize 1=P; 2=P; 3=P; 4=P;
    // If given source px OOB, inherit P instead.
    // IF C==A AND C!=D AND A!=B => 1=A
    // IF A==B AND A!=C AND B!=D => 2=B
    // IF D==C AND D!=B AND C!=A => 3=C
    // IF B==D AND B!=A AND D!=C => 4=D
    // Apply algorithm again to result until size is reached.
    private static void advMAME2x(final SpriteContents original, final NativeImage upscale, int ratio) {
        int applyN = Mth.log2(ratio);
        NativeImage last = original.getOriginalImage();
        for (int i = applyN; i > 0; i--) {
            NativeImage result = null;
            if (i == 1) { // is this the last application? If so, write to the output image.
                result = upscale;
            }
            if (result == null) {
                result = new NativeImage(last.getWidth() * 2, last.getHeight() * 2, false);
            }
            int lw = last.getWidth();
            int lh = last.getHeight();
            for (int w = 0; w < lw; w++) {
                for (int h = 0; h < lh; h++) {
                    // 1 = 2 = 3 = 4 = P
                    int one = last.getPixel(w, h);
                    int two = one;
                    int three = one;
                    int four = one;

                    // Inherit P if out of bounds.
                    int a = inBounds(h - 1, lh) ? last.getPixel(w, h - 1) : one;
                    int b = inBounds(w + 1, lw) ? last.getPixel(w + 1, h) : one;
                    int c = inBounds(w - 1, lw) ? last.getPixel(w - 1, h) : one;
                    int d = inBounds(h + 1, lh) ? last.getPixel(w, h + 1) : one;

                    if (c == a && c != d && a != b) one = a;
                    if (a == b && a != c && b != d) two = b;
                    if (d == c && d != b && c != a) three = c;
                    if (b == d && b != a && d != c) four = d;

                    result.setPixel(w * 2, h * 2, one);
                    result.setPixel(w * 2 + 1, h * 2, two);
                    result.setPixel(w * 2, h * 2 + 1, three);
                    result.setPixel(w * 2 + 1, h * 2 + 1, four);
                }
            }
            if (last != original.getOriginalImage()) last.close(); // Assuming this is proper, not sure if needed tbh
            last = result;
        }
    }

    private static boolean inBounds(int a, int lim) {
        return 0 < a && a < lim;
    }

    private record SpriteTargetData(int index, int availableMips){};

    public enum UpscaleMethod {
        NONE("NONE: Do nothing, upscaled images are left blank. May be preferred if the only undersized sprites are unused.", UndersizedSpriteUpscaler::noAlgo, "forge.configgui.mipmapUpscalingMethod.none"),
        MULTIPLY("MULTIPLY: Default setting. Preserves original image pixels, performs no blending or smoothing.", UndersizedSpriteUpscaler::scaleMultiply, "forge.configgui.mipmapUpscalingMethod.multiply"),
        ADVMAME2X("ADVMAME2X: Performs some blending/smoothing during upscaling.", UndersizedSpriteUpscaler::advMAME2x, "forge.configgui.mipmapUpscalingMethod.advmame2x");

        final String description;
        final UpscaleFunction method;
        final String translationKey; // todo Uncertain if needed. Remove if not. Needs adding to en_us.json if needed.

        UpscaleMethod(String description, UpscaleFunction method, String translationKey) {
            this.description = description;
            this.method = method;
            this.translationKey = translationKey;
        }

        public String getDescription() {
            return description;
        }

        public String getTranslationKey() {
            return translationKey;
        }
    }
}
