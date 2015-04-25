package cpw.mods.fml.client;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.lang.Thread.UncaughtExceptionHandler;
import java.nio.IntBuffer;
import java.util.Iterator;
import java.util.Properties;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.FileResourcePack;
import net.minecraft.client.resources.FolderResourcePack;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.crash.CrashReport;
import net.minecraft.util.ResourceLocation;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.Level;
import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.Drawable;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.SharedDrawable;
import org.lwjgl.util.glu.GLU;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.ICrashCallable;
import cpw.mods.fml.common.ProgressManager;
import cpw.mods.fml.common.ProgressManager.ProgressBar;
import cpw.mods.fml.common.asm.FMLSanityChecker;

/**
 * @deprecated not a stable API, will break, don't use this yet
 */
@Deprecated
public class SplashProgress
{
    private static Drawable d;
    private static volatile boolean pause = false;
    private static volatile boolean done = false;
    private static Thread thread;
    private static volatile Throwable threadError;
    private static int angle = 0;
    private static final Lock lock = new ReentrantLock(true);
    private static SplashFontRenderer fontRenderer;

    private static final IResourcePack mcPack = Minecraft.getMinecraft().mcDefaultResourcePack;
    private static final IResourcePack fmlPack = createFmlResourcePack();

    private static Texture fontTexture;
    private static Texture logoTexture;
    private static Texture forgeTexture;

    private static ResourceLocation configLocation = new ResourceLocation("fml", "splash.properties");
    private static final Properties config = loadConfig();

    private static final boolean enabled = Boolean.parseBoolean(config.getProperty("enabled"));
    private static final boolean rotate = Boolean.parseBoolean(config.getProperty("rotate"));
    private static final int logoOffset = getInt("logoOffset");
    private static final int backgroundColor = getInt("background");
    private static final int fontColor = getInt("font");
    private static final int barBorderColor = getInt("barBorder");
    private static final int barColor = getInt("bar");
    private static final int barBackgroundColor = getInt("barBackground");

    public static void start()
    {
        if(!enabled) return;
        // getting debug info out of the way, while we still can
        FMLCommonHandler.instance().registerCrashCallable(new ICrashCallable()
        {
            public String call() throws Exception
            {
                return "' Vendor: '" + glGetString(GL_VENDOR) +
                       "' Version: '" + glGetString(GL_VERSION) +
                       "' Renderer: '" + glGetString(GL_RENDERER) +
                       "'";
            }

            public String getLabel()
            {
                return "GL info";
            }
        });
        CrashReport report = CrashReport.makeCrashReport(new Throwable(), "Loading screen debug info");
        System.out.println(report.getCompleteReport());

        try
        {
            d = new SharedDrawable(Display.getDrawable());
            Display.getDrawable().releaseContext();
            d.makeCurrent();
        }
        catch (LWJGLException e)
        {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        Thread mainThread = Thread.currentThread();
        thread = new Thread(new Runnable()
        {
            private final int barWidth = 400;
            private final int barHeight = 20;
            private final int textHeight2 = 20;
            private final int barOffset = 55;

            public void run()
            {
                setGL();
                fontTexture = new Texture(mcPack, new ResourceLocation("textures/font/ascii.png"));
                logoTexture = new Texture(mcPack, new ResourceLocation("textures/gui/title/mojang.png"));
                forgeTexture = new Texture(fmlPack, new ResourceLocation(config.getProperty("forgeTexture")));
                glEnable(GL_TEXTURE_2D);
                fontRenderer = new SplashFontRenderer();
                glDisable(GL_TEXTURE_2D);
                while(!done)
                {
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
                    setColor(backgroundColor);
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
                    int f = (angle / 10) % forgeTexture.getFrames();
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

                    Display.update();
                    if(pause)
                    {
                        clearGL();
                        setGL();
                    }
                    Display.sync(100);
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
                drawBox((barWidth - 2) * b.getStep() / b.getSteps(), barHeight - 2);
                // progress text
                String progress = "" + b.getStep() + "/" + b.getSteps();
                glTranslatef(((float)barWidth - 2) / 2 - fontRenderer.getStringWidth(progress), 2, 0);
                setColor(fontColor);
                glScalef(2, 2, 1);
                glEnable(GL_TEXTURE_2D);
                fontRenderer.drawString(progress, 0, 0, 0x000000);
                glPopMatrix();
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
                    e.printStackTrace();
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
                    e.printStackTrace();
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
            public void uncaughtException(Thread t, Throwable e)
            {
                FMLLog.log(Level.ERROR, e, "Splash thread Exception");
                threadError = e;
            }
        });
        thread.start();
        checkThreadState();
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
     */
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
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * @deprecated not a stable API, will break, don't use this yet
     */
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
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        lock.unlock();
    }

    public static void finish()
    {
        if(!enabled) return;
        checkThreadState();
        try
        {
            done = true;
            thread.join();
            d.releaseContext();
            Display.getDrawable().makeCurrent();
            fontTexture.delete();
            logoTexture.delete();
            forgeTexture.delete();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private static IResourcePack createFmlResourcePack()
    {
        if(FMLSanityChecker.fmlLocation.isDirectory())
        {
            return new FolderResourcePack(FMLSanityChecker.fmlLocation);
        }
        else
        {
            return new FileResourcePack(FMLSanityChecker.fmlLocation);
        }
    }

    private static Properties loadConfig()
    {
        InputStream s = null;
        try
        {
            s = fmlPack.getInputStream(configLocation);
            Properties config = new Properties();
            config.load(s);
            return config;
        }
        catch(IOException e)
        {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        finally
        {
            IOUtils.closeQuietly(s);
        }
    }

    private static int getInt(String name)
    {
        return Integer.decode(config.getProperty(name));
    }

    private static final IntBuffer buf = BufferUtils.createIntBuffer(4 * 1024 * 1024);

    private static class Texture
    {
        private final ResourceLocation location;
        private final int name;
        private final int width;
        private final int height;
        private final int frames;
        private final int size;

        public Texture(IResourcePack pack, ResourceLocation location)
        {
            InputStream s = null;
            try
            {
                this.location = location;
                s = pack.getInputStream(location);
                ImageInputStream stream = ImageIO.createImageInputStream(s);
                Iterator<ImageReader> readers = ImageIO.getImageReaders(stream);
                if(!readers.hasNext()) throw new IOException("No suitable reader found for image" + location);
                ImageReader reader = readers.next();
                reader.setInput(stream);
                frames = reader.getNumImages(true);
                BufferedImage[] images = new BufferedImage[frames];
                for(int i = 0; i < frames; i++)
                {
                    images[i] = reader.read(i);
                }
                reader.dispose();
                int size = 1;
                width = images[0].getWidth();
                height = images[0].getHeight();
                while((size / width) * (size / height) < frames) size *= 2;
                this.size = size;
                name = glGenTextures();
                glEnable(GL_TEXTURE_2D);
                glBindTexture(GL_TEXTURE_2D, name);
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
                glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, size, size, 0, GL_BGRA, GL_UNSIGNED_INT_8_8_8_8_REV, (IntBuffer)null);
                checkGLError("Texture creation");
                for(int i = 0; i * (size / width) < frames; i++)
                {
                    for(int j = 0; i * (size / width) + j < frames && j < size / width; j++)
                    {
                        FMLLog.info("loc: %s, i: %s, j: %s, size: %s, width: %s, height: %s", location, i, j, size, width, height);
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
                e.printStackTrace();
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
        protected void bindTexture(ResourceLocation location)
        {
            if(location != locationFontTexture) throw new IllegalArgumentException();
            fontTexture.bind();
        }

        @Override
        protected InputStream getResourceInputStream(ResourceLocation location) throws IOException
        {
            return Minecraft.getMinecraft().mcDefaultResourcePack.getInputStream(location);
        }
    }

    public static void drawVanillaScreen() throws LWJGLException
    {
        if(!enabled)
        {
            Minecraft.getMinecraft().loadScreen();
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
        int err = GL11.glGetError();
        if (err != 0)
        {
            throw new IllegalStateException(where + ": " + GLU.gluErrorString(err));
        }
    }
}
