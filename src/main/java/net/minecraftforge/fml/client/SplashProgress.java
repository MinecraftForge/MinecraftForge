/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
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

package net.minecraftforge.fml.client;
/*
import static net.minecraftforge.fml.Logging.SPLASH;
import static net.minecraftforge.fml.Logging.fmlLog;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.nio.IntBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Properties;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import com.google.common.base.CharMatcher;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.DefaultResourcePack;
import net.minecraft.client.resources.FileResourcePack;
import net.minecraft.client.resources.FolderResourcePack;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.SimpleResource;
import net.minecraft.crash.CrashReport;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.CrashReportExtender;
import net.minecraftforge.fml.common.EnhancedRuntimeException;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.ICrashCallable;
import net.minecraftforge.fml.common.ProgressManager;
import net.minecraftforge.fml.common.ProgressManager.ProgressBar;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.LWJGLUtil;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.Drawable;
import org.lwjgl.opengl.SharedDrawable;
import org.lwjgl.util.glu.GLU;

/**
 * Not a fully fleshed out API, may change in future MC versions.
 * However feel free to use and suggest additions.
 */
@SuppressWarnings("serial")
public class SplashProgress
{
/*
    private static Drawable d;
    private static volatile boolean pause = false;
    private static volatile boolean done = false;
    private static Thread thread;
    private static volatile Throwable threadError;
    private static int angle = 0;
    private static final Lock lock = new ReentrantLock(true);
    private static SplashFontRenderer fontRenderer;

    private static final IResourcePack mcPack = Minecraft.getMinecraft().mcDefaultResourcePack;
    private static final IResourcePack fmlPack = mcPack;
    private static IResourcePack miscPack;

    private static Texture fontTexture;
    private static Texture logoTexture;
    private static Texture forgeTexture;

    private static Properties config;

    private static boolean enabled;
    private static boolean rotate;
    private static int logoOffset;
    private static int backgroundColor;
    private static int fontColor;
    private static int barBorderColor;
    private static int barColor;
    private static int barBackgroundColor;
    private static boolean showMemory;
    private static int memoryGoodColor;
    private static int memoryWarnColor;
    private static int memoryLowColor;
    private static float memoryColorPercent;
    private static long memoryColorChangeTime;
    static boolean isDisplayVSyncForced = false;
    private static final int TIMING_FRAME_COUNT = 200;
    private static final int TIMING_FRAME_THRESHOLD = TIMING_FRAME_COUNT * 5 * 1000000; // 5 ms per frame, scaled to nanos

    private static final Semaphore mutex = new Semaphore(1);

    public static Void processMessages() {
        // workaround for windows requiring messages being processed on the main thread
        if (LWJGLUtil.getPlatform() != LWJGLUtil.PLATFORM_WINDOWS) return null;
        // If we can't grab the mutex, the update call is blocked, probably in native code, just skip it and carry on
        // We'll get another go next time
        if (!SplashProgress.mutex.tryAcquire()) return null;
        Display.processMessages();
        SplashProgress.mutex.release();
        return null;
    }

    private static String getString(String name, String def)
    {
        String value = config.getProperty(name, def);
        config.setProperty(name, value);
        return value;
    }

    private static boolean getBool(String name, boolean def)
    {
        return Boolean.parseBoolean(getString(name, Boolean.toString(def)));
    }

    private static int getInt(String name, int def)
    {
        return Integer.decode(getString(name, Integer.toString(def)));
    }

    private static int getHex(String name, int def)
    {
        return Integer.decode(getString(name, "0x" + Integer.toString(def, 16).toUpperCase()));
    }

    public static void start()
    {
        File configFile = new File(Minecraft.getMinecraft().mcDataDir, "config/splash.properties");

        File parent = configFile.getParentFile();
        if (!parent.exists())
            parent.mkdirs();

        config = new Properties();
        try (Reader r = new InputStreamReader(new FileInputStream(configFile), StandardCharsets.UTF_8))
        {
            config.load(r);
        }
        catch(IOException e)
        {
            fmlLog.info(SPLASH, "Could not load splash.properties, will create a default one");
        }

        enabled =            getBool("enabled",      true);
        rotate =             getBool("rotate",       false);
        showMemory =         getBool("showMemory",   true);
        logoOffset =         getInt("logoOffset",    0);
        backgroundColor =    getHex("background",    0xFFFFFF);
        fontColor =          getHex("font",          0x000000);
        barBorderColor =     getHex("barBorder",     0xC0C0C0);
        barColor =           getHex("bar",           0xCB3D35);
        barBackgroundColor = getHex("barBackground", 0xFFFFFF);
        memoryGoodColor =    getHex("memoryGood",    0x78CB34);
        memoryWarnColor =    getHex("memoryWarn",    0xE6E84A);
        memoryLowColor =     getHex("memoryLow",     0xE42F2F);

        final ResourceLocation fontLoc = new ResourceLocation(getString("fontTexture", "textures/font/ascii.png"));
        final ResourceLocation logoLoc = new ResourceLocation("textures/gui/title/mojang.png");
        final ResourceLocation forgeLoc = new ResourceLocation(getString("forgeTexture", "fml:textures/gui/forge.png"));
        final ResourceLocation forgeFallbackLoc = new ResourceLocation("fml:textures/gui/forge.png");

        File miscPackFile = new File(Minecraft.getMinecraft().mcDataDir, getString("resourcePackPath", "resources"));

        try (Writer w = new OutputStreamWriter(new FileOutputStream(configFile), StandardCharsets.UTF_8))
        {
            config.store(w, "Splash screen properties");
        }
        catch(IOException e)
        {
            FMLLog.log.error("Could not save the splash.properties file", e);
        }

        miscPack = createResourcePack(miscPackFile);

        if(!enabled) return;
        // getting debug info out of the way, while we still can
        CrashReportExtender.registerCrashCallable(new ICrashCallable()
        {
            @Override
            public String call() throws Exception
            {
                return "' Vendor: '" + glGetString(GL_VENDOR) +
                       "' Version: '" + glGetString(GL_VERSION) +
                       "' Renderer: '" + glGetString(GL_RENDERER) +
                       "'";
            }

            @Override
            public String getLabel()
            {
                return "GL info";
            }
        });
        CrashReport report = CrashReport.makeCrashReport(new Throwable(), "Loading screen debug info");
        StringBuilder systemDetailsBuilder = new StringBuilder();
        report.getCategory().appendToStringBuilder(systemDetailsBuilder);
        fmlLog.info(SPLASH, systemDetailsBuilder.toString());

        try
        {
            d = new SharedDrawable(Display.getDrawable());
            Display.getDrawable().releaseContext();
            d.makeCurrent();
        }
        catch (LWJGLException e)
        {
            FMLLog.log.error("Error starting SplashProgress:", e);
            disableSplash(e);
        }

        //Call this ASAP if splash is enabled so that threading doesn't cause issues later
        getMaxTextureSize();

        //Thread mainThread = Thread.currentThread();
        thread = new Thread(new Runnable()
        {
            private final int barWidth = 400;
            private final int barHeight = 20;
            private final int textHeight2 = 20;
            private final int barOffset = 55;
            private long updateTiming;
            private long framecount;
            @Override
            public void run()
            {
                setGL();
                fontTexture = new Texture(fontLoc, null);
                logoTexture = new Texture(logoLoc, null, false);
                forgeTexture = new Texture(forgeLoc, forgeFallbackLoc);
                glEnable(GL_TEXTURE_2D);
                fontRenderer = new SplashFontRenderer();
                glDisable(GL_TEXTURE_2D);
                while(!done)
                {
                    framecount++;
                    ProgressBar first = null, penult = null, last = null;
                    Iterator<ProgressBar> i = ProgressManager.barIterator();
                    while(i.hasNext())
                    {
                        if(first == null) first = i.next();
                        else
                        {
                            penult = last;
                            last = i.next();
                        }
                    }

                    glClear(GL_COLOR_BUFFER_BIT);

                    // matrix setup
                    int w = Display.getWidth();
                    int h = Display.getHeight();
                    glViewport(0, 0, w, h);
                    glMatrixMode(GL_PROJECTION);
                    glLoadIdentity();
                    glOrtho(320 - w/2, 320 + w/2, 240 + h/2, 240 - h/2, -1, 1);
                    glMatrixMode(GL_MODELVIEW);
                    glLoadIdentity();

                    // mojang logo
                    setColor(backgroundColor);
                    glEnable(GL_TEXTURE_2D);
                    logoTexture.bind();
                    glBegin(GL_QUADS);
                    logoTexture.texCoord(0, 0, 0);
                    glVertex2f(320 - 256, 240 - 256);
                    logoTexture.texCoord(0, 0, 1);
                    glVertex2f(320 - 256, 240 + 256);
                    logoTexture.texCoord(0, 1, 1);
                    glVertex2f(320 + 256, 240 + 256);
                    logoTexture.texCoord(0, 1, 0);
                    glVertex2f(320 + 256, 240 - 256);
                    glEnd();
                    glDisable(GL_TEXTURE_2D);

                    // memory usage
                    if (showMemory)
                    {
                        glPushMatrix();
                        glTranslatef(320 - (float) barWidth / 2, 20, 0);
                        drawMemoryBar();
                        glPopMatrix();
                    }

                    // bars
                    if(first != null)
                    {
                        glPushMatrix();
                        glTranslatef(320 - (float)barWidth / 2, 310, 0);
                        drawBar(first);
                        if(penult != null)
                        {
                            glTranslatef(0, barOffset, 0);
                            drawBar(penult);
                        }
                        if(last != null)
                        {
                            glTranslatef(0, barOffset, 0);
                            drawBar(last);
                        }
                        glPopMatrix();
                    }

                    angle += 1;

                    // forge logo
                    glColor4f(1, 1, 1, 1);
                    float fw = (float)forgeTexture.getWidth() / 2;
                    float fh = (float)forgeTexture.getHeight() / 2;
                    if(rotate)
                    {
                        float sh = Math.max(fw, fh);
                        glTranslatef(320 + w/2 - sh - logoOffset, 240 + h/2 - sh - logoOffset, 0);
                        glRotatef(angle, 0, 0, 1);
                    }
                    else
                    {
                        glTranslatef(320 + w/2 - fw - logoOffset, 240 + h/2 - fh - logoOffset, 0);
                    }
                    int f = (angle / 5) % forgeTexture.getFrames();
                    glEnable(GL_TEXTURE_2D);
                    forgeTexture.bind();
                    glBegin(GL_QUADS);
                    forgeTexture.texCoord(f, 0, 0);
                    glVertex2f(-fw, -fh);
                    forgeTexture.texCoord(f, 0, 1);
                    glVertex2f(-fw, fh);
                    forgeTexture.texCoord(f, 1, 1);
                    glVertex2f(fw, fh);
                    forgeTexture.texCoord(f, 1, 0);
                    glVertex2f(fw, -fh);
                    glEnd();
                    glDisable(GL_TEXTURE_2D);

                    // We use mutex to indicate safely to the main thread that we're taking the display global lock
                    // So the main thread can skip processing messages while we're updating.
                    // There are system setups where this call can pause for a while, because the GL implementation
                    // is trying to impose a framerate or other thing is occurring. Without the mutex, the main
                    // thread would delay waiting for the same global display lock
                    mutex.acquireUninterruptibly();
                    long updateStart = System.nanoTime();
                    Display.update();
                    // As soon as we're done, we release the mutex. The other thread can now ping the processmessages
                    // call as often as it wants until we get get back here again
                    long dur = System.nanoTime() - updateStart;
                    if (framecount < TIMING_FRAME_COUNT) {
                        updateTiming += dur;
                    }
                    mutex.release();
                    if(pause)
                    {
                        clearGL();
                        setGL();
                    }
                    // Such a hack - if the time taken is greater than 10 milliseconds, we're gonna guess that we're on a
                    // system where vsync is forced through the swapBuffers call - so we have to force a sleep and let the
                    // loading thread have a turn - some badly designed mods access Keyboard and therefore GlobalLock.lock
                    // during splash screen, and mutex against the above Display.update call as a result.
                    // 4 milliseconds is a guess - but it should be enough to trigger in most circumstances. (Maybe if
                    // 240FPS is possible, this won't fire?)
                    if (framecount >= TIMING_FRAME_COUNT && updateTiming > TIMING_FRAME_THRESHOLD) {
                        if (!isDisplayVSyncForced)
                        {
                            isDisplayVSyncForced = true;
                            fmlLog.info(SPLASH,"Using alternative sync timing : {} frames of Display.update took {} nanos", TIMING_FRAME_COUNT, updateTiming);
                        }
                        try { Thread.sleep(16); } catch (InterruptedException ie) {}
                    } else
                    {
                        if (framecount ==TIMING_FRAME_COUNT) {
                            fmlLog.info("Using sync timing. {} frames of Display.update took {} nanos", TIMING_FRAME_COUNT, updateTiming);
                        }
                        Display.sync(100);
                    }
                }
                clearGL();
            }

            private void setColor(int color)
            {
                glColor3ub((byte)((color >> 16) & 0xFF), (byte)((color >> 8) & 0xFF), (byte)(color & 0xFF));
            }

            private void drawBox(int w, int h)
            {
                glBegin(GL_QUADS);
                glVertex2f(0, 0);
                glVertex2f(0, h);
                glVertex2f(w, h);
                glVertex2f(w, 0);
                glEnd();
            }

            private void drawBar(ProgressBar b)
            {
                glPushMatrix();
                // title - message
                setColor(fontColor);
                glScalef(2, 2, 1);
                glEnable(GL_TEXTURE_2D);
                fontRenderer.drawString(b.getTitle() + " - " + b.getMessage(), 0, 0, 0x000000);
                glDisable(GL_TEXTURE_2D);
                glPopMatrix();
                // border
                glPushMatrix();
                glTranslatef(0, textHeight2, 0);
                setColor(barBorderColor);
                drawBox(barWidth, barHeight);
                // interior
                setColor(barBackgroundColor);
                glTranslatef(1, 1, 0);
                drawBox(barWidth - 2, barHeight - 2);
                // slidy part
                setColor(barColor);
                drawBox((barWidth - 2) * (b.getStep() + 1) / (b.getSteps() + 1), barHeight - 2); // Step can sometimes be 0.
                // progress text
                String progress = "" + b.getStep() + "/" + b.getSteps();
                glTranslatef(((float)barWidth - 2) / 2 - fontRenderer.getStringWidth(progress), 2, 0);
                setColor(fontColor);
                glScalef(2, 2, 1);
                glEnable(GL_TEXTURE_2D);
                fontRenderer.drawString(progress, 0, 0, 0x000000);
                glPopMatrix();
            }

            private void drawMemoryBar() {
                int maxMemory = bytesToMb(Runtime.getRuntime().maxMemory());
                int totalMemory = bytesToMb(Runtime.getRuntime().totalMemory());
                int freeMemory = bytesToMb(Runtime.getRuntime().freeMemory());
                int usedMemory = totalMemory - freeMemory;
                float usedMemoryPercent = usedMemory / (float) maxMemory;

                glPushMatrix();
                // title - message
                setColor(fontColor);
                glScalef(2, 2, 1);
                glEnable(GL_TEXTURE_2D);
                fontRenderer.drawString("Memory Used / Total", 0, 0, 0x000000);
                glDisable(GL_TEXTURE_2D);
                glPopMatrix();
                // border
                glPushMatrix();
                glTranslatef(0, textHeight2, 0);
                setColor(barBorderColor);
                drawBox(barWidth, barHeight);
                // interior
                setColor(barBackgroundColor);
                glTranslatef(1, 1, 0);
                drawBox(barWidth - 2, barHeight - 2);
                // slidy part

                long time = System.currentTimeMillis();
                if (usedMemoryPercent > memoryColorPercent || (time - memoryColorChangeTime > 1000))
                {
                    memoryColorChangeTime = time;
                    memoryColorPercent = usedMemoryPercent;
                }

                int memoryBarColor;
                if (memoryColorPercent < 0.75f)
                {
                    memoryBarColor = memoryGoodColor;
                }
                else if (memoryColorPercent < 0.85f)
                {
                    memoryBarColor = memoryWarnColor;
                }
                else
                {
                    memoryBarColor = memoryLowColor;
                }
                setColor(memoryLowColor);
                glPushMatrix();
                glTranslatef((barWidth - 2) * (totalMemory) / (maxMemory) - 2, 0, 0);
                drawBox(2, barHeight - 2);
                glPopMatrix();
                setColor(memoryBarColor);
                drawBox((barWidth - 2) * (usedMemory) / (maxMemory), barHeight - 2);

                // progress text
                String progress = getMemoryString(usedMemory) + " / " + getMemoryString(maxMemory);
                glTranslatef(((float)barWidth - 2) / 2 - fontRenderer.getStringWidth(progress), 2, 0);
                setColor(fontColor);
                glScalef(2, 2, 1);
                glEnable(GL_TEXTURE_2D);
                fontRenderer.drawString(progress, 0, 0, 0x000000);
                glPopMatrix();
            }

            private String getMemoryString(int memory)
            {
                return StringUtils.leftPad(Integer.toString(memory), 4, ' ') + " MB";
            }

            private void setGL()
            {
                lock.lock();
                try
                {
                    Display.getDrawable().makeCurrent();
                }
                catch (LWJGLException e)
                {
                    FMLLog.log.error("Error setting GL context:", e);
                    throw new RuntimeException(e);
                }
                glClearColor((float)((backgroundColor >> 16) & 0xFF) / 0xFF, (float)((backgroundColor >> 8) & 0xFF) / 0xFF, (float)(backgroundColor & 0xFF) / 0xFF, 1);
                glDisable(GL_LIGHTING);
                glDisable(GL_DEPTH_TEST);
                glEnable(GL_BLEND);
                glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
            }

            private void clearGL()
            {
                Minecraft mc = Minecraft.getMinecraft();
                mc.displayWidth = Display.getWidth();
                mc.displayHeight = Display.getHeight();
                mc.resize(mc.displayWidth, mc.displayHeight);
                glClearColor(1, 1, 1, 1);
                glEnable(GL_DEPTH_TEST);
                glDepthFunc(GL_LEQUAL);
                glEnable(GL_ALPHA_TEST);
                glAlphaFunc(GL_GREATER, .1f);
                try
                {
                    Display.getDrawable().releaseContext();
                }
                catch (LWJGLException e)
                {
                    FMLLog.log.error("Error releasing GL context:", e);
                    throw new RuntimeException(e);
                }
                finally
                {
                    lock.unlock();
                }
            }
        });
        thread.setUncaughtExceptionHandler(new UncaughtExceptionHandler()
        {
            @Override
            public void uncaughtException(Thread t, Throwable e)
            {
                FMLLog.log.error("Splash thread Exception", e);
                threadError = e;
            }
        });
        thread.start();
        checkThreadState();
    }

    private static int max_texture_size = -1;
    public static int getMaxTextureSize()
    {
        if (max_texture_size != -1) return max_texture_size;
        for (int i = 0x4000; i > 0; i >>= 1)
        {
            GlStateManager.glTexImage2D(GL_PROXY_TEXTURE_2D, 0, GL_RGBA, i, i, 0, GL_RGBA, GL_UNSIGNED_BYTE, null);
            if (GlStateManager.glGetTexLevelParameteri(GL_PROXY_TEXTURE_2D, 0, GL_TEXTURE_WIDTH) != 0)
            {
                max_texture_size = i;
                return i;
            }
        }
        return -1;
    }

    private static void checkThreadState()
    {
        if(thread.getState() == Thread.State.TERMINATED || threadError != null)
        {
            throw new IllegalStateException("Splash thread", threadError);
        }
    }
    /**
     * Call before you need to explicitly modify GL context state during loading.
     * Resource loading doesn't usually require this call.
     * Call {@link #resume()} when you're done.
     * @deprecated not a stable API, will break, don't use this yet
     * /
    @Deprecated
    public static void pause()
    {
        if(!enabled) return;
        checkThreadState();
        pause = true;
        lock.lock();
        try
        {
            d.releaseContext();
            Display.getDrawable().makeCurrent();
        }
        catch (LWJGLException e)
        {
            FMLLog.log.error("Error setting GL context:", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * @deprecated not a stable API, will break, don't use this yet
     * /
    @Deprecated
    public static void resume()
    {
        if(!enabled) return;
        checkThreadState();
        pause = false;
        try
        {
            Display.getDrawable().releaseContext();
            d.makeCurrent();
        }
        catch (LWJGLException e)
        {
            FMLLog.log.error("Error releasing GL context:", e);
            throw new RuntimeException(e);
        }
        lock.unlock();
    }

    public static void finish()
    {
        if(!enabled) return;
        try
        {
            checkThreadState();
            done = true;
            thread.join();
            glFlush();        // process any remaining GL calls before releaseContext (prevents missing textures on mac)
            d.releaseContext();
            Display.getDrawable().makeCurrent();
            fontTexture.delete();
            logoTexture.delete();
            forgeTexture.delete();
        }
        catch (Exception e)
        {
            FMLLog.log.error("Error finishing SplashProgress:", e);
            disableSplash(e);
        }
    }

    private static boolean disableSplash(Exception e)
    {
        if (disableSplash())
        {
            throw new EnhancedRuntimeException(e)
            {
                @Override
                protected void printStackTrace(WrappedPrintStream stream)
                {
                    stream.println("SplashProgress has detected a error loading Minecraft.");
                    stream.println("This can sometimes be caused by bad video drivers.");
                    stream.println("We have automatically disabled the new Splash Screen in config/splash.properties.");
                    stream.println("Try reloading minecraft before reporting any errors.");
                }
            };
        }
        else
        {
            throw new EnhancedRuntimeException(e)
            {
                @Override
                protected void printStackTrace(WrappedPrintStream stream)
                {
                    stream.println("SplashProgress has detected a error loading Minecraft.");
                    stream.println("This can sometimes be caused by bad video drivers.");
                    stream.println("Please try disabling the new Splash Screen in config/splash.properties.");
                    stream.println("After doing so, try reloading minecraft before reporting any errors.");
                }
            };
        }
    }

    private static boolean disableSplash()
    {
        File configFile = new File(Minecraft.getMinecraft().mcDataDir, "config/splash.properties");
        File parent = configFile.getParentFile();
        if (!parent.exists())
            parent.mkdirs();

        enabled = false;
        config.setProperty("enabled", "false");

        try (Writer w = new OutputStreamWriter(new FileOutputStream(configFile), StandardCharsets.UTF_8))
        {
            config.store(w, "Splash screen properties");
        }
        catch(IOException e)
        {
            FMLLog.log.error("Could not save the splash.properties file", e);
            return false;
        }
        return true;
    }

    private static IResourcePack createResourcePack(File file)
    {
        if(file.isDirectory())
        {
            return new FolderResourcePack(file);
        }
        else
        {
            return new FileResourcePack(file);
        }
    }

    private static final IntBuffer buf = BufferUtils.createIntBuffer(4 * 1024 * 1024);

    // From FontRenderer.renderCharAtPos
    private static final String ALLOWED_CHARS = "\u00c0\u00c1\u00c2\u00c8\u00ca\u00cb\u00cd\u00d3\u00d4\u00d5\u00da\u00df\u00e3\u00f5\u011f\u0130\u0131\u0152\u0153\u015e\u015f\u0174\u0175\u017e\u0207\u0000\u0000\u0000\u0000\u0000\u0000\u0000 !\"#$%&\'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u0000\u00c7\u00fc\u00e9\u00e2\u00e4\u00e0\u00e5\u00e7\u00ea\u00eb\u00e8\u00ef\u00ee\u00ec\u00c4\u00c5\u00c9\u00e6\u00c6\u00f4\u00f6\u00f2\u00fb\u00f9\u00ff\u00d6\u00dc\u00f8\u00a3\u00d8\u00d7\u0192\u00e1\u00ed\u00f3\u00fa\u00f1\u00d1\u00aa\u00ba\u00bf\u00ae\u00ac\u00bd\u00bc\u00a1\u00ab\u00bb\u2591\u2592\u2593\u2502\u2524\u2561\u2562\u2556\u2555\u2563\u2551\u2557\u255d\u255c\u255b\u2510\u2514\u2534\u252c\u251c\u2500\u253c\u255e\u255f\u255a\u2554\u2569\u2566\u2560\u2550\u256c\u2567\u2568\u2564\u2565\u2559\u2558\u2552\u2553\u256b\u256a\u2518\u250c\u2588\u2584\u258c\u2590\u2580\u03b1\u03b2\u0393\u03c0\u03a3\u03c3\u03bc\u03c4\u03a6\u0398\u03a9\u03b4\u221e\u2205\u2208\u2229\u2261\u00b1\u2265\u2264\u2320\u2321\u00f7\u2248\u00b0\u2219\u00b7\u221a\u207f\u00b2\u25a0\u0000";
    private static final CharMatcher DISALLOWED_CHAR_MATCHER = CharMatcher.anyOf(ALLOWED_CHARS).negate();

    public static String stripSpecialChars(String message)
    {
        // We can't handle many unicode points in the splash renderer
        return DISALLOWED_CHAR_MATCHER.removeFrom(net.minecraft.util.StringUtils.stripControlCodes(message));
    }


    @SuppressWarnings("unused")
    private static class Texture
    {
        private final ResourceLocation location;
        private final int name;
        private final int width;
        private final int height;
        private final int frames;
        private final int size;

        public Texture(ResourceLocation location, @Nullable ResourceLocation fallback)
        {
            this(location, fallback, true);
        }

        public Texture(ResourceLocation location, @Nullable ResourceLocation fallback, boolean allowRP)
        {
            InputStream s = null;
            try
            {
                this.location = location;
                s = open(location, fallback, allowRP);
                ImageInputStream stream = ImageIO.createImageInputStream(s);
                Iterator<ImageReader> readers = ImageIO.getImageReaders(stream);
                if(!readers.hasNext()) throw new IOException("No suitable reader found for image" + location);
                ImageReader reader = readers.next();
                reader.setInput(stream);
                int frames = reader.getNumImages(true);
                BufferedImage[] images = new BufferedImage[frames];
                for(int i = 0; i < frames; i++)
                {
                    images[i] = reader.read(i);
                }
                reader.dispose();
                width = images[0].getWidth();
                int height = images[0].getHeight();
                // Animation strip
                if (height > width && height % width == 0)
                {
                    frames = height / width;
                    BufferedImage original = images[0];
                    height = width;
                    images = new BufferedImage[frames];
                    for (int i = 0; i < frames; i++)
                    {
                        images[i] = original.getSubimage(0, i * height, width, height);
                    }
                }
                this.frames = frames;
                this.height = height;
                int size = 1;
                while((size / width) * (size / height) < frames) size *= 2;
                this.size = size;
                glEnable(GL_TEXTURE_2D);
                synchronized(SplashProgress.class)
                {
                    name = glGenTextures();
                    glBindTexture(GL_TEXTURE_2D, name);
                }
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
                glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, size, size, 0, GL_BGRA, GL_UNSIGNED_INT_8_8_8_8_REV, (IntBuffer)null);
                checkGLError("Texture creation");
                for(int i = 0; i * (size / width) < frames; i++)
                {
                    for(int j = 0; i * (size / width) + j < frames && j < size / width; j++)
                    {
                        buf.clear();
                        BufferedImage image = images[i * (size / width) + j];
                        for(int k = 0; k < height; k++)
                        {
                            for(int l = 0; l < width; l++)
                            {
                                buf.put(image.getRGB(l, k));
                            }
                        }
                        buf.position(0).limit(width * height);
                        glTexSubImage2D(GL_TEXTURE_2D, 0, j * width, i * height, width, height, GL_BGRA, GL_UNSIGNED_INT_8_8_8_8_REV, buf);
                        checkGLError("Texture uploading");
                    }
                }
                glBindTexture(GL_TEXTURE_2D, 0);
                glDisable(GL_TEXTURE_2D);
            }
            catch(IOException e)
            {
                FMLLog.log.error("Error reading texture from file: {}", location, e);
                throw new RuntimeException(e);
            }
            finally
            {
                IOUtils.closeQuietly(s);
            }
        }

        public ResourceLocation getLocation()
        {
            return location;
        }

        public int getName()
        {
            return name;
        }

        public int getWidth()
        {
            return width;
        }

        public int getHeight()
        {
            return height;
        }

        public int getFrames()
        {
            return frames;
        }

        public int getSize()
        {
            return size;
        }

        public void bind()
        {
            glBindTexture(GL_TEXTURE_2D, name);
        }

        public void delete()
        {
            glDeleteTextures(name);
        }

        public float getU(int frame, float u)
        {
            return width * (frame % (size / width) + u) / size;
        }

        public float getV(int frame, float v)
        {
            return height * (frame / (size / width) + v) / size;
        }

        public void texCoord(int frame, float u, float v)
        {
            glTexCoord2f(getU(frame, u), getV(frame, v));
        }
    }

    private static class SplashFontRenderer extends FontRenderer
    {
        public SplashFontRenderer()
        {
            super(Minecraft.getMinecraft().gameSettings, fontTexture.getLocation(), null, false);
            super.onResourceManagerReload(null);
        }

        @Override
        protected void bindTexture(@Nonnull ResourceLocation location)
        {
            if(location != locationFontTexture) throw new IllegalArgumentException();
            fontTexture.bind();
        }

        @Nonnull
        @Override
        protected IResource getResource(@Nonnull ResourceLocation location) throws IOException
        {
            DefaultResourcePack pack = Minecraft.getMinecraft().mcDefaultResourcePack;
            return new SimpleResource(pack.getPackName(), location, pack.getInputStream(location), null, null);
        }
    }

    public static void drawVanillaScreen(TextureManager renderEngine) throws LWJGLException
    {
        if(!enabled)
        {
            Minecraft.getMinecraft().drawSplashScreen(renderEngine);
        }
    }

    public static void clearVanillaResources(TextureManager renderEngine, ResourceLocation mojangLogo)
    {
        if(!enabled)
        {
            renderEngine.deleteTexture(mojangLogo);
        }
    }

    public static void checkGLError(String where)
    {
        int err = glGetError();
        if (err != 0)
        {
            throw new IllegalStateException(where + ": " + GLU.gluErrorString(err));
        }
    }

    private static InputStream open(ResourceLocation loc, @Nullable ResourceLocation fallback, boolean allowResourcePack) throws IOException
    {
        if (!allowResourcePack)
            return mcPack.getInputStream(loc);

        if(miscPack.resourceExists(loc))
        {
            return miscPack.getInputStream(loc);
        }
        else if(fmlPack.resourceExists(loc))
        {
            return fmlPack.getInputStream(loc);
        }
        else if(!mcPack.resourceExists(loc) && fallback != null)
        {
            return open(fallback, null, true);
        }
        return mcPack.getInputStream(loc);
    }

    private static int bytesToMb(long bytes)
    {
        return (int) (bytes / 1024L / 1024L);
    }
    */
}