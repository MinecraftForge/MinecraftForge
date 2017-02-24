package net.minecraftforge.fml.client;

/**
 * This class defines a replacement for the default forge splash screen.
 *
 * Only one splash screen can be registered at a time. It it registered with SplashProgress.setCustomSplashScreen().
 * If you use this, make it a configuration option.
 */
public interface ICustomSplashScreen {
    /**
     * Render a single frame of the splash screen.
     */
    void renderFrame();
}
