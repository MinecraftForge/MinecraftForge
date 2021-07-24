/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.client.loading;

import net.minecraftforge.fml.loading.progress.StartupMessageManager;
import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.stb.STBEasyFont;
import org.lwjgl.system.MemoryUtil;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;
import java.nio.ByteBuffer;
import java.util.List;

import static org.lwjgl.opengl.GL30C.*;

public class StartupMessageRenderer {
    private static final String vertexShaderSource = """
            #version 150 core
        
            in vec2 position;
        
            uniform vec2 posMultiplier;
            uniform vec2 posShift;
            uniform float size;
        
            void main() {
                gl_Position = vec4((((position * size) + posShift) * posMultiplier) - 1.0, 0.0, 1.0);
                gl_Position.y *= -1.0;
            }
            """;
    
    private static final String fragmentShaderSource = """
            #version 150 core
            
            uniform vec4 colorIn;
            
            out vec4 color;
            
            void main() {
                color = colorIn;
            }
            """;
    
    private final int program;
    private final int posMultiplierLocation;
    private final int posShiftLocation;
    private final int colorInLocation;
    private final int sizeLocation;
    
    private final int VAO;
    
    private final int dataBuffer;
    private int bufferLength = 27000;
    private ByteBuffer charBuffer;
    
    private int quadCount = 0;
    private final int elementBuffer;
    
    private float height;
    
    public StartupMessageRenderer() {
        int previousVAO = glGetInteger(GL_VERTEX_ARRAY_BINDING);
        int previousArrayBuffer = glGetInteger(GL_ARRAY_BUFFER_BINDING);
        int previousElementBuffer = glGetInteger(GL_ELEMENT_ARRAY_BUFFER_BINDING);
        
        int vertexShader = glCreateShader(GL_VERTEX_SHADER);
        int fragmentShader = glCreateShader(GL_FRAGMENT_SHADER);
        
        glShaderSource(vertexShader, vertexShaderSource);
        glShaderSource(fragmentShader, fragmentShaderSource);
        
        glCompileShader(vertexShader);
        glCompileShader(fragmentShader);
        
        if (glGetShaderi(vertexShader, GL_COMPILE_STATUS) != GL_TRUE || glGetShaderi(fragmentShader, GL_COMPILE_STATUS) != GL_TRUE) {
            System.err.println("Shader compilation failed");
            System.err.println("Vertex shader log\n" + glGetShaderInfoLog(vertexShader));
            System.err.println("Fragment shader log\n" + glGetShaderInfoLog(fragmentShader));
            throw new IllegalStateException("Shader compilation failed");
        }
        
        program = glCreateProgram();
        glAttachShader(program, vertexShader);
        glAttachShader(program, fragmentShader);
        glLinkProgram(program);
        
        if (glGetProgrami(program, GL_LINK_STATUS) != GL_TRUE) {
            System.err.println("Program linking failed");
            System.err.println("Program link log\n" + glGetProgramInfoLog(program));
            throw new IllegalStateException("Program link failed");
        }
        
        glDetachShader(program, vertexShader);
        glDetachShader(program, fragmentShader);
        
        glDeleteShader(vertexShader);
        glDeleteShader(fragmentShader);
        
        posMultiplierLocation = glGetUniformLocation(program, "posMultiplier");
        posShiftLocation = glGetUniformLocation(program, "posShift");
        colorInLocation = glGetUniformLocation(program, "colorIn");
        sizeLocation = glGetUniformLocation(program, "size");
        
        int inputLocation = glGetAttribLocation(program, "position");
        
        VAO = glGenVertexArrays();
        dataBuffer = glGenBuffers();
        elementBuffer = glGenBuffers();
        
        glBindVertexArray(VAO);
        
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, elementBuffer);
        
        glBindBuffer(GL_ARRAY_BUFFER, dataBuffer);
        glVertexAttribPointer(inputLocation, 2, GL_FLOAT, false, 16, 0);
        glEnableVertexAttribArray(inputLocation);
        
        glBindVertexArray(0);
        
        glBufferData(GL_ARRAY_BUFFER, bufferLength, GL_DYNAMIC_DRAW);
        charBuffer = MemoryUtil.memAlloc(bufferLength);
        
        glBindBuffer(GL_ARRAY_BUFFER, previousArrayBuffer);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, previousElementBuffer);
        glBindVertexArray(previousVAO);
    }
    
    public void close() {
        MemoryUtil.memFree(charBuffer);
        
        glDeleteBuffers(elementBuffer);
        glDeleteBuffers(dataBuffer);
        glDeleteVertexArrays(VAO);
        
        glDeleteProgram(program);
    }
    
    public void render(float width, float height, float textSize, boolean isDarkMode) {
        this.height = height;
        
        int previousVAO = glGetInteger(GL_VERTEX_ARRAY_BINDING);
        int previousArrayBuffer = glGetInteger(GL_ARRAY_BUFFER_BINDING);
        int previousElementBuffer = glGetInteger(GL_ELEMENT_ARRAY_BUFFER_BINDING);
        
        int previousProgram = glGetInteger(GL_CURRENT_PROGRAM);
        
        int blendEnabled = glGetInteger(GL_BLEND);
        int blendSrcMode = glGetInteger(GL_BLEND_SRC_ALPHA);
        int blendDstMode = glGetInteger(GL_BLEND_DST_ALPHA);
        
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, elementBuffer);
        glBindBuffer(GL_ARRAY_BUFFER, dataBuffer);
        
        glBindVertexArray(VAO);
        glUseProgram(program);
        
        {
            glUniform2f(posMultiplierLocation, 2.0f / width, 2.0f / height);
            // TODO: 21/06/12: handle text resizing better, this is ok, but its not perfect
            float size = Math.min(width / 854, height / 480) * textSize;
            glUniform1f(sizeLocation, size);
            
            List<Pair<Integer, StartupMessageManager.Message>> messages = StartupMessageManager.getMessages();
            for (int i = 0; i < messages.size(); i++) {
                boolean nofade = i == 0;
                final Pair<Integer, StartupMessageManager.Message> pair = messages.get(i);
                final float fade = nofade ? 1.0f : clamp((4000.0f - (float) pair.getLeft() - (i - 4) * 1000.0f) / 5000.0f, 0.0f, 1.0f);
                if (fade < 0.01f) {
                    continue;
                }
                StartupMessageManager.Message msg = pair.getRight();
                renderMessage(msg.getText(), msg.getTypeColor(isDarkMode), 1, i, size, true, fade);
            }
            renderMemoryInfo(size);
        }
        
        glBindVertexArray(previousVAO);
        glUseProgram(previousProgram);
        
        glBindBuffer(GL_ARRAY_BUFFER, previousArrayBuffer);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, previousElementBuffer);
        
        glBlendFunc(blendSrcMode, blendDstMode);
        if (blendEnabled != GL_TRUE) {
            glDisable(GL_BLEND);
        }
    }
    
    private void renderMemoryInfo(float size) {
        final MemoryUsage heapusage = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
        final MemoryUsage offheapusage = ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage();
        final float pctmemory = (float) heapusage.getUsed() / heapusage.getMax();
        String memory = String.format("Memory Heap: %d / %d MB (%.1f%%)  OffHeap: %d MB", heapusage.getUsed() >> 20, heapusage.getMax() >> 20, pctmemory * 100.0, offheapusage.getUsed() >> 20);
        
        float[] memorycolour = hsvToRGB((1.0f - (float) Math.pow(pctmemory, 1.5f)) / 3f, 1.0f, 0.5f);
        renderMessage(memory, memorycolour, 2, 1.5f, size, false, 1.0f);
    }
    
    private void renderMessage(final String message, final float[] colour, int startingColumn, float row, float size, boolean fromBottom, float alpha) {
        if (bufferLength < message.length() * 270) {
            bufferLength = (int) (message.length() * 1.2 * 270);
            MemoryUtil.memFree(charBuffer);
            charBuffer = MemoryUtil.memAlloc(bufferLength);
            glBufferData(GL_ARRAY_BUFFER, bufferLength, GL_DYNAMIC_DRAW);
        }
        int quads = STBEasyFont.stb_easy_font_print(0, 0, message, null, charBuffer);
        
        if (quads > quadCount) {
            ByteBuffer elements = MemoryUtil.memAlloc(quads * 6 * 4);
            for (int i = 0; i < quads; i++) {
                elements.putInt((i * 4));
                elements.putInt((i * 4) + 2);
                elements.putInt((i * 4) + 1);
                elements.putInt((i * 4) + 2);
                elements.putInt((i * 4));
                elements.putInt((i * 4) + 3);
            }
            elements.rewind();
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, elements, GL_DYNAMIC_DRAW);
            MemoryUtil.memFree(elements);
            quadCount = quads;
        }
        
        glBufferSubData(GL_ARRAY_BUFFER, 0, charBuffer);
        
        glUniform4f(colorInLocation, colour[0], colour[1], colour[2], alpha);
        float rowPosition = row * 10 * size;
        if (fromBottom) {
            rowPosition = height - rowPosition;
            rowPosition -= 10 * size;
        }
        glUniform2f(posShiftLocation, 5 * startingColumn * size, rowPosition);
        glDrawElements(GL_TRIANGLES, quads * 6, GL_UNSIGNED_INT, 0);
    }
    
    private static float clamp(float num, float min, float max) {
        if (num < min) {
            return min;
        } else {
            return num > max ? max : num;
        }
    }
    
    private static float[] hsvToRGB(float hue, float saturation, float value) {
        int h = (int) (hue * 6);
        float f = hue * 6 - h;
        float p = value * (1 - saturation);
        float q = value * (1 - f * saturation);
        float t = value * (1 - (1 - f) * saturation);
        
        return switch (h) {
            case 0 -> new float[]{value, t, p};
            case 1 -> new float[]{q, value, p};
            case 2 -> new float[]{p, value, t};
            case 3 -> new float[]{p, q, value};
            case 4 -> new float[]{t, p, value};
            case 5 -> new float[]{value, p, q};
            default -> throw new RuntimeException("Something went wrong when converting from HSV to RGB. Input was " + hue + ", " + saturation + ", " + value);
        };
    }
}