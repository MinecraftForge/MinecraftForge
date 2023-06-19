/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.earlydisplay;

import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBTTAlignedQuad;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.stb.STBTTPackContext;
import org.lwjgl.stb.STBTTPackRange;
import org.lwjgl.stb.STBTTPackedchar;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import static org.lwjgl.opengl.GL32C.*;
import static org.lwjgl.stb.STBTruetype.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class SimpleFont {
    private final int textureNumber;
    private final int lineSpacing;
    private final int descent;
    private final int GLYPH_COUNT = 127-32;
    private Glyph[] glyphs;

    private record Glyph(char c, int charwidth, int[] pos, float[] uv) {
        Pos loadQuad(Pos pos, int colour, SimpleBufferBuilder bb) {
            final var x0 = pos.x() + pos()[0];
            final var y0 = pos.y() + pos()[1];
            final var x1 = pos.x() + pos()[2];
            final var y1 = pos.y() + pos()[3];
            bb.pos(x0, y0).tex(uv()[0], uv()[1]).colour(colour).endVertex();
            bb.pos(x1, y0).tex(uv()[2], uv()[1]).colour(colour).endVertex();
            bb.pos(x0, y1).tex(uv()[0], uv()[3]).colour(colour).endVertex();
            bb.pos(x1, y1).tex(uv()[2], uv()[3]).colour(colour).endVertex();
            return new Pos(pos.x()+charwidth(), pos.y(), pos.minx());
        }
    }

    /**
     * Build the font and store it in the textureNumber location
     */
    public SimpleFont(String fontName, int scale, int bufferSize, int textureNumber) {
        ByteBuffer buf = STBHelper.readFromClasspath(fontName, bufferSize);
        var info = STBTTFontinfo.create();
        if (!stbtt_InitFont(info, buf)) {
            throw new IllegalStateException("Bad font");
        }

        var ascent = new float[1];
        var descent = new float[1];
        var lineGap = new float[1];
        int fontSize = 24;
        stbtt_GetScaledFontVMetrics(buf, 0, fontSize, ascent, descent, lineGap);
        this.lineSpacing = (int)(ascent[0] - descent[0] + lineGap[0]);
        this.descent = (int)Math.floor(descent[0]);
        int fontTextureId = glGenTextures();
        glActiveTexture(GL_TEXTURE0+textureNumber);
        this.textureNumber = textureNumber;
        glBindTexture(GL_TEXTURE_2D, fontTextureId);
        try (var packedchars = STBTTPackedchar.malloc(GLYPH_COUNT)) {
            int texwidth = 256;
            int texheight = 128;
            try (STBTTPackRange.Buffer packRanges = STBTTPackRange.malloc(1)) {
                var bitmap = BufferUtils.createByteBuffer(texwidth * texheight);
                try (STBTTPackRange packRange = STBTTPackRange.malloc()) {
                    packRanges.put(packRange.set(fontSize, 32, null, GLYPH_COUNT, packedchars, (byte) 1, (byte) 1));
                    packRanges.flip();
                }

                try (STBTTPackContext pc = STBTTPackContext.malloc()) {
                    stbtt_PackBegin(pc, bitmap, texwidth, texheight, 0, 1, NULL);
                    stbtt_PackSetOversampling(pc, 1, 1);
                    stbtt_PackSetSkipMissingCodepoints(pc, true);
                    stbtt_PackFontRanges(pc, buf, 0, packRanges);
                    stbtt_PackEnd(pc);
                    glTexImage2D(GL_TEXTURE_2D, 0, GL_RED, texwidth, texheight, 0, GL_RED, GL_UNSIGNED_BYTE, bitmap);
                    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
                    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
                    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
                    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
                }
            }
            glActiveTexture(GL_TEXTURE0);
            try (var q = STBTTAlignedQuad.malloc()) {
                float[] x = new float[1];
                float[] y = new float[1];
                glyphs = new Glyph[GLYPH_COUNT];

                for (int i = 0; i < GLYPH_COUNT; i++) {
                    x[0] = 0f;
                    y[0] = fontSize;
                    stbtt_GetPackedQuad(packedchars, texwidth, texheight, i, x, y, q, true);
                    glyphs[i] = new Glyph((char) (i + 32), (int) (x[0] - 0f), new int[]{(int) q.x0(), (int) q.y0(), (int) q.x1(), (int) q.y1()}, new float[]{q.s0(), q.t0(), q.s1(), q.t1()});
                }
            }
        }
    }

    int lineSpacing() {
        return lineSpacing;
    }

    int textureNumber() {
        return textureNumber;
    }

    int descent() {
        return descent;
    }

    public int stringWidth(String text) {
        var bytes = text.getBytes(StandardCharsets.US_ASCII);
        int len = 0;
        for (int i = 0; i < bytes.length; i++) {
            final byte c = bytes[i];
            len += switch (c) {
                case '\n', '\t' -> 0;
                case ' ' -> glyphs[0].charwidth();
                default -> {
                    if (c - 32 < this.GLYPH_COUNT && c > 32) {
                        yield this.glyphs[c - 32].charwidth();
                    } else {
                        yield 0;
                    }
                }
            };
        }
        return len;
    }
    private record Pos(int x, int y, int minx) {}

    /**
     * A piece of text to display
     *
     * @param string The text
     * @param colour The colour of the text as an RGBA packed int
     */
    public record DisplayText(String string, int colour) {
        private byte[] asBytes() {
            return string.getBytes(StandardCharsets.US_ASCII);
        }

        Pos generateStringArray(SimpleFont font, Pos pos, SimpleBufferBuilder bb) {
            for (int i = 0; i < asBytes().length; i++) {
                byte c = asBytes()[i];
                pos = switch (c) {
                    case '\n' -> new Pos(pos.minx(), pos.y()+font.lineSpacing(), pos.minx());
                    case '\t' -> new Pos(pos.x()+font.glyphs[0].charwidth() * 4, pos.y(), pos.minx());
                    case ' ' -> new Pos(pos.x()+font.glyphs[0].charwidth(), pos.y(), pos.minx());
                    default -> {
                        if (c-32 < font.GLYPH_COUNT && c > 32) {
                            pos = font.glyphs[c - 32].loadQuad(pos, colour(), bb);
                        }
                        yield pos;
                    }
                };
            }
            return pos;
        }
    }

    /**
     * Generate vertices for a set of display texts
     * @param x The starting screen x coordinate
     * @param y The starting screen y coordinate
     * @param texts Some {@link DisplayText} to display
     * @return a {@link SimpleBufferBuilder} that can draw the texts
     */
    public SimpleBufferBuilder generateVerticesForTexts(int x, int y, SimpleBufferBuilder textBB, DisplayText... texts) {
        var pos = new Pos(x, y, x);
        for (DisplayText text : texts) {
            pos = text.generateStringArray(this, pos, textBB);
        }
        return textBB;
    }

}
