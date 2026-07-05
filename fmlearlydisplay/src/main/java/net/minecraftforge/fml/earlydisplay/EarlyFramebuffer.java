/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.earlydisplay;

import java.nio.IntBuffer;

import static net.minecraftforge.fml.earlydisplay.RenderElement.clamp;
import static org.lwjgl.opengl.GL32C.*;

public class EarlyFramebuffer extends BaseFramebuffer {
    private final int framebuffer;
    private final int texture;

    private final int contextWidth;
    private final int contextHeight;
    private final int contextScale;
    private final ColourScheme.Colour backgroundColour;

    EarlyFramebuffer(int width, int height, int scale, ColourScheme colours) {
        this.contextWidth = width;
        this.contextHeight = height;
        this.contextScale = scale;
        this.backgroundColour = colours.background();
        this.framebuffer = glGenFramebuffers();
        this.texture = glGenTextures();
        glBindFramebuffer(GL_FRAMEBUFFER, this.framebuffer);
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, this.texture);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width * scale, height * scale, 0, GL_RGBA, GL_UNSIGNED_BYTE, (IntBuffer)null);
        glTexParameterIi(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameterIi(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, this.texture, 0);
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    @Override
    public void activate() {
        glBindFramebuffer(GL_FRAMEBUFFER, this.framebuffer);
    }

    @Override
    public void deactivate() {
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    @Override
    public void draw(int windowFBWidth, int windowFBHeight) {
        var wscale = ((float)windowFBWidth / this.contextWidth);
        var hscale = ((float)windowFBHeight / this.contextHeight);
        var scale = this.contextScale * Math.min(wscale, hscale) / 2f;
        var wleft = (int)(windowFBWidth * 0.5f - scale * this.contextWidth);
        var wtop = (int)(windowFBHeight * 0.5f - scale * this.contextHeight);
        var wright = (int)(windowFBWidth * 0.5f + scale * this.contextWidth);
        var wbottom = (int)(windowFBHeight * 0.5f + scale * this.contextHeight);
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, 0);
        glBindFramebuffer(GL_READ_FRAMEBUFFER, this.framebuffer);
        glClearColor(backgroundColour.redf(), backgroundColour.greenf(), backgroundColour.bluef(), 1f);
        glClear(GL_COLOR_BUFFER_BIT);
        glBlitFramebuffer(0, this.contextHeight * this.contextScale, this.contextWidth * this.contextScale, 0, clamp(wleft, 0, windowFBWidth), clamp(wtop, 0, windowFBHeight), clamp(wright, 0, windowFBWidth), clamp(wbottom, 0, windowFBHeight), GL_COLOR_BUFFER_BIT, GL_NEAREST);
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    @Override
    public long getTextureHandle() {
        return this.texture;
    }

    @Override
    public void close() {
        glDeleteTextures(this.texture);
        glDeleteFramebuffers(this.framebuffer);
    }
}
