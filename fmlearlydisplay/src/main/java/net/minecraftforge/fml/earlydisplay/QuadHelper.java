/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.earlydisplay;

public class QuadHelper {
    public static void loadQuad(SimpleBufferBuilder bb, float x0, float x1, float y0, float y1, float u0, float u1, float v0, float v1, int colour) {
        bb.pos(x0, y0).tex(u0, v0).colour(colour).endVertex();
        bb.pos(x1, y0).tex(u1, v0).colour(colour).endVertex();
        bb.pos(x0, y1).tex(u0, v1).colour(colour).endVertex();
        bb.pos(x1, y1).tex(u1, v1).colour(colour).endVertex();
    }

    public static void loadQuad(SimpleBufferBuilder bb, float x0, float x1, float y0, float y1, float u0, float u1, float v0, float v1) {
        bb.pos(x0, y0).tex(u0, v0).endVertex();
        bb.pos(x1, y0).tex(u1, v0).endVertex();
        bb.pos(x0, y1).tex(u0, v1).endVertex();
        bb.pos(x1, y1).tex(u1, v1).endVertex();
    }
}
