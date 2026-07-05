/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.earlydisplay;

import net.minecraftforge.fml.loading.FMLConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL.createCapabilities;
import static org.lwjgl.opengl.GL32C.*;

/**
 * OpenGL implementation of {@link BaseRenderBackend}.
 * <p>
 * Contains all OpenGL-specific rendering code extracted from the original DisplayWindow.
 * This class is the legacy GL path and can be removed when OpenGL support is dropped.
 */
public class OpenGLRenderBackend extends BaseRenderBackend {
    private static final Logger LOGGER = LoggerFactory.getLogger("EARLYDISPLAY");
    private static final int[][] GL_VERSIONS = new int[][] {{4,6}, {4,5}, {4,4}, {4,3}, {4,2}, {4,1}, {4,0}, {3,3}};

    private long window;
    private int savedVAO;
    private int savedFB;

    @Override
    public void applyWindowHints() {
        glfwWindowHint(GLFW_CLIENT_API, GLFW_OPENGL_API);
        glfwWindowHint(GLFW_CONTEXT_CREATION_API, GLFW_NATIVE_CONTEXT_API);
    }

    @Override
    public long createWindow(int winWidth, int winHeight, String title) {
        int versidx = 0;
        var skipVersions = FMLConfig.<String>getListConfigValue(FMLConfig.ConfigValue.EARLY_WINDOW_SKIP_GL_VERSIONS);
        boolean showHelpLog = FMLConfig.getBoolConfigValue(FMLConfig.ConfigValue.EARLY_WINDOW_LOG_HELP_MSG);
        final String[] lastGLError = new String[GL_VERSIONS.length];
        long window = 0;
        do {
            final var glVersionToTry = GL_VERSIONS[versidx][0] + "." + GL_VERSIONS[versidx][1];
            if (skipVersions.contains(glVersionToTry)) {
                LOGGER.info("Skipping GL version "+ glVersionToTry+" because of configuration");
                versidx++;
                continue;
            }
            LOGGER.info("Trying GL version " + glVersionToTry);
            if (showHelpLog && versidx == 0) {
                LOGGER.info("""
                If this message is the only thing at the bottom of your log before a crash, you probably have a driver issue.

                Possible solutions:
                A) Make sure Minecraft is set to prefer high performance graphics in the OS and/or driver control panel
                B) Check for driver updates on the graphics brand's website
                C) Try reinstalling your graphics drivers
                D) If still not working after trying all of the above, ask for further help on the Forge forums or Discord

                You can safely ignore this message if the game starts up successfully.""");
            }
            glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, GL_VERSIONS[versidx][0]);
            glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, GL_VERSIONS[versidx][1]);
            glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
            glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);
            window = glfwCreateWindow(winWidth, winHeight, title, 0L, 0L);
            var erridx = versidx;
            EarlyWindow.handleLastGLFWError((error, description) -> lastGLError[erridx] = String.format("Trying %d.%d: GLFW error: [0x%X]%s", GL_VERSIONS[erridx][0], GL_VERSIONS[erridx][1], error, description));
            if (lastGLError[versidx] != null) {
                LOGGER.trace(lastGLError[versidx]);
            }
            versidx++;
        } while (window == 0 && versidx < GL_VERSIONS.length);

        if (versidx == GL_VERSIONS.length && window == 0) {
            LOGGER.error("Failed to find any valid GLFW profile. "+lastGLError[0]);
            throw new IllegalStateException("Failed to find a valid GLFW profile.\nWe tried "+
                    Arrays.stream(GL_VERSIONS).map(p->p[0]+"."+p[1]).filter(o -> !skipVersions.contains(o))
                            .collect(Collector.of(()->new StringJoiner(", ").setEmptyValue("no versions"), StringJoiner::add, StringJoiner::merge, StringJoiner::toString))+
                    " but none of them worked.\n"+ Arrays.stream(lastGLError).filter(Objects::nonNull).collect(Collectors.joining("\n")));
        }

        var requestedVersion = GL_VERSIONS[versidx-1][0]+"."+GL_VERSIONS[versidx-1][1];
        var maj = glfwGetWindowAttrib(window, GLFW_CONTEXT_VERSION_MAJOR);
        var min = glfwGetWindowAttrib(window, GLFW_CONTEXT_VERSION_MINOR);
        var gotVersion = maj+"."+min;
        LOGGER.info("Requested GL version "+requestedVersion+" got version "+gotVersion);
        this.version = gotVersion;

        if (showHelpLog)
            FMLConfig.updateConfig(FMLConfig.ConfigValue.EARLY_WINDOW_LOG_HELP_MSG, false);

        return window;
    }

    @Override
    public void initialize(long window, ColourScheme colours, int fbScale,
                           PerformanceInfo perfInfo, String mcVersion) {
        this.window = window;
        this.colourScheme = colours;
        glfwMakeContextCurrent(window);
        glfwSwapInterval(1);
        createCapabilities();
        LOGGER.info("GL info: "+ glGetString(GL_RENDERER) + " GL version " + glGetString(GL_VERSION) + ", " + glGetString(GL_VENDOR));

        this.shader = new ElementShader();
        try {
            shader.init();
        } catch (Throwable t) {
            LOGGER.error("Crash during shader initialization", t);
            throw new RuntimeException("An error occurred initializing shaders.", t);
        }

        glClearColor(colours.background().redf(), colours.background().greenf(), colours.background().bluef(), 1f);

        this.context = new RenderElement.DisplayContext(854, 480, fbScale, shader, colours, perfInfo, this, this::createBufferBuilder);
        this.framebuffer = new EarlyFramebuffer(854, 480, fbScale, colours);

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    }

    @Override
    public void close() {
        framebuffer.close();
        shader.close();
        SimpleBufferBuilder.destroy();
    }

    @Override
    public void makeCurrent(long window) {
        glfwMakeContextCurrent(window);
    }

    @Override
    public void releaseCurrent() {
        glfwMakeContextCurrent(0);
    }

    @Override
    public void setVsync(boolean enabled) {
        glfwSwapInterval(enabled ? 1 : 0);
    }

    @Override
    public void beginFrame() {
        framebuffer.activate();
        glViewport(0, 0, context.scaledWidth(), context.scaledHeight());
        shader.activate();
        shader.updateScreenSizeUniform(context.scaledWidth(), context.scaledHeight());
        glClearColor(colourScheme.background().redf(), colourScheme.background().greenf(), colourScheme.background().bluef(), 1f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    }

    @Override
    public void endFrame(int fbWidth, int fbHeight) {
        shader.clear();
        framebuffer.deactivate();
        glViewport(0, 0, fbWidth, fbHeight);
        framebuffer.draw(fbWidth, fbHeight);
        glfwSwapBuffers(window);
    }

    @Override
    public void beginOverlay(int alpha) {
        savedVAO = glGetInteger(GL_VERTEX_ARRAY_BINDING);
        savedFB = glGetInteger(GL_READ_FRAMEBUFFER_BINDING);
        glViewport(0, 0, context.scaledWidth(), context.scaledHeight());
        RenderElement.globalAlpha = alpha;
        framebuffer.activate();
        glClearColor(colourScheme.background().redf(), colourScheme.background().greenf(), colourScheme.background().bluef(), alpha / 255f);
        shader.activate();
        shader.updateScreenSizeUniform(context.scaledWidth(), context.scaledHeight());
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    }

    @Override
    public void endOverlay() {
        shader.clear();
        framebuffer.deactivate();
        glBindVertexArray(savedVAO);
        glBindFramebuffer(GL_FRAMEBUFFER, savedFB);
    }

    @Override
    public BaseShader createShader() {
        return new ElementShader();
    }

    @Override
    public BaseFramebuffer createFramebuffer(int width, int height, int scale, ColourScheme colours) {
        return new EarlyFramebuffer(width, height, scale, colours);
    }

    @Override
    public VertexDataBuilder createBufferBuilder() {
        return new SimpleBufferBuilder(1);
    }

    @Override
    public void uploadTexture(ByteBuffer pixels, int width, int height, int slot) {
        var texid = glGenTextures();
        glActiveTexture(GL_TEXTURE0 + slot);
        glBindTexture(GL_TEXTURE_2D, texid);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, pixels);
        glActiveTexture(GL_TEXTURE0);
    }

    @Override
    public void uploadFontTexture(ByteBuffer alphaBitmap, int width, int height, int slot) {
        var fontTextureId = glGenTextures();
        glActiveTexture(GL_TEXTURE0 + slot);
        glBindTexture(GL_TEXTURE_2D, fontTextureId);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RED, width, height, 0, GL_RED, GL_UNSIGNED_BYTE, alphaBitmap);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glActiveTexture(GL_TEXTURE0);
    }

    @Override
    public void bindTexture(long handle) {
        glBindTexture(GL_TEXTURE_2D, (int) handle);
    }

    @Override
    public void unbindTexture() {
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    @Override
    public void prepareHandoff(long window) {
        glfwMakeContextCurrent(window);
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public long getFramebufferTextureHandle() {
        return framebuffer.getTextureHandle();
    }
}
