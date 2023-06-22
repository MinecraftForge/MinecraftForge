/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.earlydisplay;

import net.minecraftforge.fml.loading.progress.Message;
import net.minecraftforge.fml.loading.progress.ProgressMeter;
import net.minecraftforge.fml.loading.progress.StartupNotificationManager;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static org.lwjgl.opengl.GL32C.*;

public class RenderElement {
    static final int INDEX_TEXTURE_OFFSET = 5;
    private final SimpleBufferBuilder bb;
    private final Renderer renderer;
    static int globalAlpha = 255;
    private int retireCount;

    interface Renderer {

        void accept(SimpleBufferBuilder bb, DisplayContext context, int frame);

        default Renderer then(Renderer r) {
            if (r == null) return this;
            return (bb, ctx, frame) -> {
                r.accept(bb, ctx, frame);
                this.accept(bb, ctx, frame);
            };
        }
    }
    interface TextureRenderer {
        void accept(SimpleBufferBuilder bb, DisplayContext context, int[] size, int frame);
    }
    interface Initializer extends Supplier<Renderer> {}

    interface TextGenerator {
        void accept(SimpleBufferBuilder bb, SimpleFont fh, DisplayContext ctx);
    }

    public record DisplayContext(int width, int height, int scale, ElementShader elementShader, ColourScheme colourScheme, PerformanceInfo performance) {
        public int scaledWidth() {
            return scale() * width();
        }

        public int scaledHeight() {
            return scale() * height();
        }
    }

    public RenderElement(final Initializer rendererInitializer) {
        this.bb = new SimpleBufferBuilder(1);
        this.renderer = rendererInitializer.get();
    }

    public boolean render(DisplayContext ctx, int count) {
        this.renderer.accept(bb, ctx, count);
        return this.retireCount == 0 || this.retireCount < count;
    }

    public void retire(final int frame) {
        this.retireCount = frame;
    }

    private static void startupLogMessages(SimpleBufferBuilder bb, SimpleFont font, DisplayContext context) {
        List<StartupNotificationManager.AgeMessage> messages = StartupNotificationManager.getMessages();
        List<SimpleFont.DisplayText> texts = new ArrayList<>();
        for (int i = messages.size() - 1; i >= 0; i--) {
            final StartupNotificationManager.AgeMessage pair = messages.get(i);
            final float fade = clamp((4000.0f - (float) pair.age() - ( i - 4 ) * 1000.0f) / 5000.0f, 0.0f, 1.0f);
            if (fade <0.01f) continue;
            Message msg = pair.message();
            int colour = Math.min((int)(fade * 255f), globalAlpha) << 24 | 0xFFFFFF;
            texts.add(new SimpleFont.DisplayText(msg.getText()+"\n", colour));
        }

        font.generateVerticesForTexts(10, context.scaledHeight() -  texts.size() * font.lineSpacing() + font.descent() - 10, bb, texts.toArray(SimpleFont.DisplayText[]::new));
    }
    public static RenderElement monag() {
        return new RenderElement(RenderElement.initializeTexture("monagstudios.png", 45000, 4, (bb, ctx, sz, frame) -> {
            var size = 256;
            var x0 = (ctx.width() - 2 * size) / 2;
            var y0 = 64;
            QuadHelper.loadQuad(bb, x0, x0+size, y0, y0+size/2f, 0f, 1f, 0f, 0.5f, 0xFFFFFFFF);
            QuadHelper.loadQuad(bb, x0+size, x0+2*size, y0, y0+size/2f, 0f, 1f, 0.5f, 1f, 0xFFFFFFFF);
        }));

    }

    public static RenderElement mojang(final int textureId, final int frameStart) {
        return new RenderElement(()->(bb, ctx, frame) -> {
            var size = 256 * ctx.scale();
            var x0 = (ctx.scaledWidth() - 2 * size) / 2;
            var y0 = 64 * ctx.scale() + 32;
            ctx.elementShader().updateTextureUniform(0);
            ctx.elementShader().updateRenderTypeUniform(ElementShader.RenderType.TEXTURE);
            var fade = Math.min((frame - frameStart) * 10, 255);
            glBindTexture(GL_TEXTURE_2D, textureId);
            bb.begin(SimpleBufferBuilder.Format.POS_TEX_COLOR, SimpleBufferBuilder.Mode.QUADS);
            QuadHelper.loadQuad(bb, x0, x0+size, y0, y0+size/2f, 0f, 1f, 0f, 0.5f, (fade << 24) | 0xFFFFFF);
            QuadHelper.loadQuad(bb, x0+size, x0+2*size, y0, y0+size/2f, 0f, 1f, 0.5f, 1f, (fade << 24) | 0xFFFFFF);
            bb.draw();
            glBindTexture(GL_TEXTURE_2D, 0);
        });
    }
    public static RenderElement logMessageOverlay(SimpleFont font) {
        return new RenderElement(RenderElement.initializeText(font, RenderElement::startupLogMessages));
    }

    public static RenderElement forgeVersionOverlay(SimpleFont font, String version) {
        return new RenderElement(RenderElement.initializeText(font, (bb, fnt, ctx)->
                font.generateVerticesForTexts(ctx.scaledWidth() - font.stringWidth(version) - 10,
                        ctx.scaledHeight() - font.lineSpacing() + font.descent() - 10, bb,
                        new SimpleFont.DisplayText(version, ctx.colourScheme.foreground().packedint(RenderElement.globalAlpha)))));
    }
    public static RenderElement squir() {
        return new RenderElement(RenderElement.initializeTexture("squirrel.png", 45000, 3, (bb, context, size, frame) -> {
            var inset = 5f;
            var x0 = inset;
            var x1 = inset + size[0] * context.scale();
            var y0 = inset;
            var y1 = inset + size[1] * context.scale();
            int fade = (int) (Math.cos(frame * Math.PI / 16) * 16) + 16;
//            int fade = 0xff;
            var colour = (Math.min(fade, globalAlpha) & 0xff) << 24 | 0xffffff;
            QuadHelper.loadQuad(bb, x0, x1, y0, y1, 0f, 1f, 0f, 1f, colour);
        }));
    }

    public static RenderElement anvil(SimpleFont font) {
        return new RenderElement(RenderElement.initializeTexture("forge_anvil.png", 20000, 2, (bb, context, size, frame) -> {
            var x0 = context.scaledWidth() - size[0] * context.scale();
            var x1 = context.scaledWidth();
            var y0 = context.scaledHeight() - size[0] * context.scale() - font.descent() - font.lineSpacing();
            var y1 = context.scaledHeight() - font.descent() - font.lineSpacing();
            int frameidx = frame % 32;
            float framepos = (frameidx * (float)size[0]) / size[1];
            float framesize = size[0] / (float)size[1];
            QuadHelper.loadQuad(bb, x0, x1, y0, y1, 0f, 1f, framepos, framepos+framesize, globalAlpha << 24 | 0xFFFFFF);
        }));
    }
    public static RenderElement progressBars(SimpleFont font) {
        return new RenderElement(() -> (bb, ctx, frame) -> RenderElement.startupProgressBars(font, bb, ctx, frame));
    }

    public static RenderElement performanceBar(SimpleFont font) {
        return new RenderElement(() -> (bb, ctx, frame) -> RenderElement.memoryInfo(font, bb, ctx, frame));
    }

    public static void startupProgressBars(SimpleFont font, final SimpleBufferBuilder buffer, final DisplayContext context, final int frameNumber) {
        Renderer acc = null;
        var barCount = 2;
        List<ProgressMeter> currentProgress = StartupNotificationManager.getCurrentProgress();
        var size = currentProgress.size();
        var alpha = 0xFF;
        for (int i = 0; i < barCount && i < size; i++) {
            final ProgressMeter pm = currentProgress.get(i);
            Renderer barRenderer = barRenderer(i, alpha, font, pm, context);
            acc = barRenderer.then(acc);
            alpha >>= 1;
        }
        if (acc != null)
            acc.accept(buffer, context, frameNumber);
    }
    private static final int BAR_HEIGHT = 20;
    private static final int BAR_WIDTH = 400;
    private static Renderer barRenderer(int cnt, int alpha, SimpleFont font, ProgressMeter pm, DisplayContext context) {
        var barSpacing = font.lineSpacing() - font.descent() + BAR_HEIGHT;
        var y = 250 * context.scale() + cnt * barSpacing;
        var colour = (alpha << 24) | 0xFFFFFF;
        Renderer bar;
        if (pm.steps() == 0) {
            bar = progressBar(ctx->new int[] {(ctx.scaledWidth() - BAR_WIDTH * ctx.scale()) / 2, y + font.lineSpacing() - font.descent(), BAR_WIDTH * ctx.scale()}, f->colour, frame -> indeterminateBar(frame, cnt == 0));
        } else {
            bar = progressBar(ctx -> new int[]{(ctx.scaledWidth() - BAR_WIDTH * ctx.scale()) / 2, y + font.lineSpacing() - font.descent(), BAR_WIDTH * ctx.scale()}, f -> colour, f -> new float[]{0f, pm.progress()});
        }
        Renderer label = (bb, ctx, frame) -> renderText(font, text((ctx.scaledWidth() - BAR_WIDTH * ctx.scale()) / 2, y, pm.label().getText(), colour), bb, ctx);
        return bar.then(label);
    }
    private static float[] indeterminateBar(int frame, boolean isActive) {
        if (RenderElement.globalAlpha != 0xFF || !isActive) {
            return new float[] {0f,1f};
        } else {
            var progress = frame % 100;
            return new float[]{clamp((progress - 2) / 100f, 0f, 1f), clamp((progress + 2) / 100f, 0f, 1f)};
        }
    }

    private static void memoryInfo(SimpleFont font, final SimpleBufferBuilder buffer, final DisplayContext context, final int frameNumber) {
        var y = 10 * context.scale();
        PerformanceInfo pi = context.performance();
        final int colour = hsvToRGB((1.0f - (float)Math.pow(pi.memory(), 1.5f)) / 3f, 1.0f, 0.5f);
        var bar = progressBar(ctx -> new int[]{(ctx.scaledWidth() - BAR_WIDTH * ctx.scale()) / 2, y, BAR_WIDTH * ctx.scale()}, f -> colour, f -> new float[]{0f, pi.memory()});
        var width = font.stringWidth(pi.text());
        Renderer label = (bb, ctx, frame) -> renderText(font, text(ctx.scaledWidth() / 2 - width / 2, y + 18, pi.text(), context.colourScheme.foreground().packedint(globalAlpha)), bb, ctx);
        bar.then(label).accept(buffer, context, frameNumber);
    }

    interface ColourFunction {
        int colour(int frame);
    }

    interface ProgressDisplay {
        float[] progress(int frame);
    }

    interface BarPosition {
        int[] location(DisplayContext context);
    }
    public static Renderer progressBar(BarPosition position, ColourFunction colourFunction, ProgressDisplay progressDisplay) {
        return (bb, context, frame) -> {
            var colour = colourFunction.colour(frame);
            var alpha = (colour & 0xFF000000) >> 24;
            context.elementShader().updateTextureUniform(0);
            context.elementShader().updateRenderTypeUniform(ElementShader.RenderType.BAR);
            var progress = progressDisplay.progress(frame);
            bb.begin(SimpleBufferBuilder.Format.POS_TEX_COLOR, SimpleBufferBuilder.Mode.QUADS);
            var inset = 2;
            var pos = position.location(context);
            var x0 = pos[0];
            var x1 = pos[0] + pos[2] + 4 * inset;
            var y0 = pos[1];
            var y1 = y0 + BAR_HEIGHT;
            QuadHelper.loadQuad(bb, x0, x1, y0, y1, 0f, 0f, 0f, 0f, context.colourScheme().foreground().packedint(alpha));

            x0 += inset;
            x1 -= inset;
            y0 += inset;
            y1 -= inset;
            QuadHelper.loadQuad(bb, x0, x1, y0, y1, 0f, 0f, 0f, 0f, context.colourScheme().background().packedint(RenderElement.globalAlpha));

            x1 = x0 + inset + (int)(progress[1] * pos[2]);
            x0 += inset + progress[0] * pos[2];
            y0 += inset;
            y1 -= inset;
            QuadHelper.loadQuad(bb, x0, x1, y0, y1, 0f, 0f, 0f, 0f, colour);
            bb.draw();
        };
    }

    private static Initializer initializeText(SimpleFont font, TextGenerator textGenerator) {
        return () -> (bb, context, frame) -> renderText(font, textGenerator, bb, context);
    }

    private static void renderText(final SimpleFont font, final TextGenerator textGenerator, final SimpleBufferBuilder bb, final DisplayContext context) {
        context.elementShader().updateTextureUniform(font.textureNumber());
        context.elementShader().updateRenderTypeUniform(ElementShader.RenderType.FONT);
        bb.begin(SimpleBufferBuilder.Format.POS_TEX_COLOR, SimpleBufferBuilder.Mode.QUADS);
        textGenerator.accept(bb, font, context);
        bb.draw();
    }

    private static TextGenerator text(int x, int y, String text, int colour) {
        return (bb, font, context) -> font.generateVerticesForTexts(x, y, bb, new SimpleFont.DisplayText(text, colour));
    }
    private static Initializer initializeTexture(final String textureFileName, int size, int textureNumber, TextureRenderer positionAndColour) {
        return ()->{
            int[] imgSize = STBHelper.loadTextureFromClasspath(textureFileName, size, GL_TEXTURE0 + textureNumber + INDEX_TEXTURE_OFFSET);
            return (bb, ctx, frame) -> {
                ctx.elementShader().updateTextureUniform(textureNumber + INDEX_TEXTURE_OFFSET);
                ctx.elementShader().updateRenderTypeUniform(ElementShader.RenderType.TEXTURE);
                renderTexture(bb, ctx, frame, imgSize, positionAndColour);
            };
        };
    }

    private static void renderTexture(SimpleBufferBuilder bb, DisplayContext context, int frame, int[] size, TextureRenderer positionAndColour) {
        bb.begin(SimpleBufferBuilder.Format.POS_TEX_COLOR, SimpleBufferBuilder.Mode.QUADS);
        positionAndColour.accept(bb, context, size, frame);
        bb.draw();
    }


    public static float clamp(float num, float min, float max) {
        if (num < min) {
            return min;
        } else {
            return num > max ? max : num;
        }
    }

    public static int clamp(int num, int min, int max) {
        if (num < min) {
            return min;
        } else {
            return num > max ? max : num;
        }
    }

    public static int hsvToRGB(float hue, float saturation, float value) {
        int i = (int)(hue * 6.0F) % 6;
        float f = hue * 6.0F - (float)i;
        float f1 = value * (1.0F - saturation);
        float f2 = value * (1.0F - f * saturation);
        float f3 = value * (1.0F - (1.0F - f) * saturation);
        float f4;
        float f5;
        float f6;
        switch(i) {
            case 0:
                f4 = value;
                f5 = f3;
                f6 = f1;
                break;
            case 1:
                f4 = f2;
                f5 = value;
                f6 = f1;
                break;
            case 2:
                f4 = f1;
                f5 = value;
                f6 = f3;
                break;
            case 3:
                f4 = f1;
                f5 = f2;
                f6 = value;
                break;
            case 4:
                f4 = f3;
                f5 = f1;
                f6 = value;
                break;
            case 5:
                f4 = value;
                f5 = f1;
                f6 = f2;
                break;
            default:
                throw new RuntimeException("Something went wrong when converting from HSV to RGB. Input was " + hue + ", " + saturation + ", " + value);
        }

        int j = clamp((int)(f4 * 255.0F), 0, 255);
        int k = clamp((int)(f5 * 255.0F), 0, 255);
        int l = clamp((int)(f6 * 255.0F), 0, 255);
        return 0xFF << 24 | j << 16 | k << 8 | l;
    }
}
