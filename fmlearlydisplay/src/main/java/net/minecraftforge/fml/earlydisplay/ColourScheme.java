/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.earlydisplay;

public enum ColourScheme {
    RED(new Colour(239, 50, 61), new Colour(255, 255, 255)),
    BLACK(new Colour(0, 0, 0), new Colour(255, 255, 255));

    private final Colour background;
    private final Colour foreground;

    ColourScheme(final Colour background, final Colour foreground) {
        this.background = background;
        this.foreground = foreground;
    }

    public Colour background() {
        return background;
    }

    public Colour foreground() {
        return foreground;
    }

    public record Colour(int red, int green, int blue) {
        public float redf() {
            return ((float)red)/255f;
        }
        public float greenf() {
            return ((float)green)/255f;
        }
        public float bluef() {
            return ((float)blue)/255f;
        }

        public int packedint(int a) {
            return ((a & 0xff) << 24) | ((blue & 0xff) << 16) | ((green & 0xff) << 8) | (red & 0xff);
        }
    }
}
