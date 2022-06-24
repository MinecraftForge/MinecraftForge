/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.earlydisplay;


import static org.lwjgl.opengl.GL32C.*;

public class ElementShader {
    private int program;
    private int textureUniform;
    private int screenSizeUniform;
    private int renderTypeUniform;

    public void init() {
        int vertexShader = glCreateShader(GL_VERTEX_SHADER);
        int fragmentShader = glCreateShader(GL_FRAGMENT_SHADER);

        // Bind the source of our shaders to the ones created above
        glShaderSource(fragmentShader, """
                 #version 150 core
                 uniform sampler2D tex;
                 uniform int rendertype;
                 in vec2 fTex;
                 in vec4 fColour;
                 out vec4 fragColor;
                 
                 void main() {
                     if (rendertype == 0)
                            fragColor = vec4(1,1,1,texture(tex, fTex).r) * fColour;
                     if (rendertype == 1)
                            fragColor = texture(tex, fTex) * fColour;
                     if (rendertype == 2)
                            fragColor = fColour;
                 }
        """);
        glShaderSource(vertexShader, """
                 #version 150 core
                 in vec2 position;
                 in vec2 tex;
                 in vec4 colour;
                 uniform vec2 screenSize;
                 out vec2 fTex;
                 out vec4 fColour;
                 void main() {
                     fTex = tex;
                     fColour = colour;
                     gl_Position = vec4((position/screenSize) * 2 - 1, 0.0, 1.0);
                 }
        """);

        // Compile the vertex and fragment elementShader so that we can use them
        glCompileShader(vertexShader);
        if (glGetShaderi(vertexShader, GL_COMPILE_STATUS) == GL_FALSE) {
            throw new IllegalStateException("VertexShader linkage failure. \n" + glGetShaderInfoLog(vertexShader));
        }
        glCompileShader(fragmentShader);
        if (glGetShaderi(fragmentShader, GL_COMPILE_STATUS) == GL_FALSE) {
            throw new IllegalStateException("FragmentShader linkage failure. \n" + glGetShaderInfoLog(fragmentShader));
        }

        var program = glCreateProgram();
        glBindAttribLocation(program, 0, "position");
        glBindAttribLocation(program, 1, "tex");
        glBindAttribLocation(program, 2, "colour");
        glAttachShader(program, vertexShader);
        glAttachShader(program, fragmentShader);
        glLinkProgram(program);
        if (glGetProgrami(program, GL_LINK_STATUS) == GL_FALSE) {
            throw new RuntimeException("ShaderProgram linkage failure. \n" + glGetProgramInfoLog(program));
        }
        this.program = program;

        glDetachShader(program, vertexShader);
        glDetachShader(program, fragmentShader);
        glDeleteShader(vertexShader);
        glDeleteShader(fragmentShader);
        textureUniform = glGetUniformLocation(program, "tex");
        screenSizeUniform = glGetUniformLocation(program, "screenSize");
        renderTypeUniform = glGetUniformLocation(program, "rendertype");

        activate();
    }

    public void activate() {
        glUseProgram(program);
    }
    public void updateTextureUniform(int textureNumber) {
        glUniform1i(textureUniform, textureNumber);
    }

    public void updateScreenSizeUniform(int width, int height) {
        glUniform2f(screenSizeUniform, width, height);
    }

    public void updateRenderTypeUniform(RenderType type) {
        glUniform1i(renderTypeUniform, type.ordinal());
    }

    public void clear() {
        glUseProgram(0);
    }

    public void close() {
        glDeleteProgram(program);
    }

    public enum RenderType {
        FONT, TEXTURE, BAR;
    }

    public int program() {
        return program;
    }
}
