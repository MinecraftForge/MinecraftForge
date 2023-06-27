/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.renderer.transparency;

public final class OITShaders
{

    private OITShaders()
    {
        throw new IllegalStateException("Can not instantiate an instance of: ForgeTransparencyShaders. This is a utility class");
    }

    public static final String FRAGMENT_SHADER_VERSION_HEADER = """
            #version 150 core
            #extension GL_ARB_explicit_attrib_location : require
            """;

    public static final String FRAGMENT_SHADER_PREFIX = """
            layout (location = 0) out vec4 accum;
            layout (location = 1) out float reveal;
            """;

    public static final String FRAGMENT_SHADER_SUFFIX = """
            float weight(float depth, float alpha) {
                float absDepth = abs(depth);
                //return alpha * max(0.01, min(3000, 10 / (0.00001 + pow(absDepth / 10, 3) + pow(absDepth / 200, 6))));
                return alpha * max(0.01, min(1500, 0.000001 / (0.00001 + pow(absDepth / 10, 3))));
            }
            
            void main() {
                mainShader();
            
                if (!oitEnabled) {
                    accum = %OUT_COLOR%;
                    return;
                }
            
                vec4 color = %OUT_COLOR%;
                            
                float weight = weight(%Z_VALUE%, color.a);
                                                
                accum = vec4(color.rgb * weight, color.a);
                reveal = color.a * weight;
            }
            """;
}
