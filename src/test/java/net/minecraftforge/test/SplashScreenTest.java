package net.minecraftforge.test;

import static org.lwjgl.opengl.GL11.*;

import java.util.Iterator;

import org.lwjgl.opengl.Display;

import net.minecraftforge.fml.client.ICustomSplashScreen;
import net.minecraftforge.fml.client.SplashProgress;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.ProgressManager;
import net.minecraftforge.fml.common.ProgressManager.ProgressBar;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;

@Mod(modid="splashscreentest", name="Splash Screen Test", version="0.0.0", clientSideOnly = true)
public class SplashScreenTest {

    public static final boolean ENABLE = false;

    @EventHandler
    public static void construct(FMLConstructionEvent event)
    {
        if (ENABLE)
        {
            SplashProgress.setCustomSplashScreen(TestSplashScreen.INSTANCE);
        }
    }

    public enum TestSplashScreen implements ICustomSplashScreen
    {
        INSTANCE;

        @Override
        public void renderFrame() {
            glClear(GL_COLOR_BUFFER_BIT);

            // matrix setup -- similar as SplashProgress
            int w = Display.getWidth();
            int h = Display.getHeight();
            glViewport(0, 0, w, h);
            glMatrixMode(GL_PROJECTION);
            glLoadIdentity();
            glOrtho(-w/2,  w/2, h/2, -h/2, -1, 1);
            glMatrixMode(GL_MODELVIEW);
            glLoadIdentity();

            // Actual drawing
            Iterator<ProgressBar> i = ProgressManager.barIterator();
            int y = 0;
            glColor3d(0, 0, 0);
            glPushMatrix();
            glScalef(2, 2, 1);
            glEnable(GL_TEXTURE_2D);
            while (i.hasNext())
            {
                ProgressBar b = i.next();

                int startWidth = SplashProgress.fontRenderer.getStringWidth(b.getTitle() + " ");

                SplashProgress.fontRenderer.drawString(b.getTitle() + " ", -startWidth, y, 0);
                SplashProgress.fontRenderer.drawString("- " + b.getMessage(), 0, y, 0);
                String bar = getProgress(b);
                SplashProgress.fontRenderer.drawString(bar, -SplashProgress.fontRenderer.getStringWidth(bar) / 2 , y + 14, 0);

                y += 30;
            }
            glDisable(GL_TEXTURE_2D);
            glPopMatrix();
        }

        private static final int NUM_GAPS = 8;

        private static String getProgress(ProgressBar bar)
        {
            // Builds a string like [=====---] or [==>-----]
            String s = "[";
            double val = NUM_GAPS * bar.getStep() / (double) bar.getSteps();
            int count = (int) val;
            boolean endBig = val % 1 > 0.5;
            for (int i = 0; i < count; i++)
            {
                s += "=";
            }
            if (endBig & count < NUM_GAPS)
            {
                count++;
                s += ">";
            }
            for (int i = count; i < NUM_GAPS; i++)
            {
                s += "-";
            }
            return s + "]";
        }
    }
}
