/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.earlydisplay.tests;

import net.minecraftforge.fml.earlydisplay.*;
import net.minecraftforge.fml.loading.progress.StartupNotificationManager;
import org.junit.jupiter.api.Test;
import org.lwjgl.opengl.GL;

import java.util.List;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public class DisplayTest {
    @Test
    void testFont() throws InterruptedException {
//        System.setProperty("java.awt.headless", "true");
//        System.setProperty("sun.java2d.opengl", "true");
//        System.setProperty("sun.java2d.trace", "log");
//        System.setProperty("org.lwjgl.util.DebugAllocator", "true");
//        var width = 400;
//        var height = 400;
//        glfwInit();
//        glfwDefaultWindowHints();
//        glfwWindowHint(GLFW_CLIENT_API, GLFW_OPENGL_API);
//        glfwWindowHint(GLFW_CONTEXT_CREATION_API, GLFW_NATIVE_CONTEXT_API);
//        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
//        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
//        long window;
//        int versidx= 0;
//        int[][] VERSIONS = {{5,0}, {4,6}, {4,5}, {4,1}, {3,3}, {3,2}};
//        do {
//            glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, VERSIONS[versidx][0]);
//            glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, VERSIONS[versidx][1]);
//            glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
//            window = glfwCreateWindow(width, height, "Minecraft: Forge Loading...", 0L, 0L);
//            versidx++;
//            if (versidx==VERSIONS.length) {
//                throw new IllegalStateException("Cannot find a profile");
//            }
//        } while (window == 0);
//
//        glfwMakeContextCurrent(window);
//        GL.createCapabilities();
//        var ver = glGetString(GL_VERSION);
//        glfwShowWindow(window);
//        glfwSwapInterval(1);
//
//        var shader = new ElementShader();
//        shader.init();
//
//        var texts = List.of("Hello world!", "This is the new log message overlay", "How are you?", "We are the cheese makers");
//        glEnable(GL_BLEND);
//        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
//        shader.updateScreenSizeUniform(width, height);
//        RenderElement.DisplayContext ctx = new RenderElement.DisplayContext(width, height, shader, ColourScheme.BLACK);
//        glClearColor(ctx.colourScheme().bg().redf(), ctx.colourScheme().bg().greenf(), ctx.colourScheme().bg().bluef(), 1f);
//        var smm = StartupNotificationManager.locatorConsumer().orElseThrow();
//        var elements = List.of(RenderElement.squir(), RenderElement.anvil(), RenderElement.textOverlay());
//        long timer = 0l;
//        int counter = 0;
//        while (true) {
//            var timerstr = String.format("%.2f us", timer/(1000f*(counter+1)));
//            if (counter == 200) timer = 0l;
//
//            if ((counter % 100) == 0) {
//                var idx = (counter % (texts.size() * 100)) / 100;
//                smm.accept(texts.get(idx));
//            }
//            var start = System.nanoTime();
//            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
//            for (RenderElement elt: elements) {
//                elt.render(ctx, counter);
//            }
//            glfwSwapBuffers(window);
//            timer += System.nanoTime() - start;
//            Thread.sleep(50);
//            counter++;
//            glfwPollEvents();
//            System.out.println(timerstr);
//        }
    }

    SimpleBufferBuilder genText(SimpleFont font, SimpleBufferBuilder bb, String timerstr, int counter) {
        bb.begin(SimpleBufferBuilder.Format.POS_TEX_COLOR, SimpleBufferBuilder.Mode.QUADS);
        return font.generateVerticesForTexts(10, 80, bb,
                new SimpleFont.DisplayText("Hello World!\n", 0xFF00FF00),
                new SimpleFont.DisplayText("\tFISH\n\t\tPEPPER!\n", 0x7F0055FF),
                new SimpleFont.DisplayText("smelly cheese!", 0xFFFF00FF),
                new SimpleFont.DisplayText("tasty pee!\n", 0xFF00FF00),
                new SimpleFont.DisplayText("catjam 🄲🄰🅃🄹🄰🄼 catjam\n", 0xFFFFFFFF),
                new SimpleFont.DisplayText("COUNTER "+ counter, 0xFFFFFFFF),
                new SimpleFont.DisplayText("\nTIMER "+ timerstr, 0xFFFFFFFF)
        );
    }

    public static void main(String... arg) throws InterruptedException {
        new DisplayTest().testFont();
    }
}
