/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.loading;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

/**
 * This is for allowing the plugging in of alternative early display implementations.
 *
 * They can be selected through the config value "earlyWindowProvider" which defaults to "fmlearlywindow" implemented by {@link net.minecraftforge.fml.earlydisplay.DisplayWindow}
 *
 * There are a few key things to keep in mind if following through on implementation. You cannot access the game state as it
 * literally DOES NOT EXIST at the time this object is constructed. You have to be very careful about managing the handoff
 * to mojang, be sure that if you're trying to tick your window in a background thread (a nice idea!) that you properly
 * transition to the main thread before handoff is complete. Do note that in general, you should construct your GL objects
 * on the MAIN thread before starting your ticker, to ensure MacOS compatibility.
 *
 * No doubt many more things can be said here.
 */
public interface ImmediateWindowProvider {
    /**
     * @return The name of this window provider. Do NOT use fmlearlywindow.
     */
    String name();

    /**
     * This is called very early on to initialize ourselves. Use this to initialize the window and other GL core resources.
     *
     * One thing we want to ensure is that we try and create the highest GL_PROFILE we can accomplish.
     * GLFW_CONTEXT_VERSION_MAJOR,GLFW_CONTEXT_VERSION_MINOR should be as high as possible on the created window,
     * and it should have all the typical profile settings.
     *
     * @param arguments The arguments provided to the Java process. This is the entire command line, so you can process
     *                  stuff from it.
     * @return A runnable that will be periodically ticked by FML during startup ON THE MAIN THREAD. This is usually
     * a good place to put glfwPollEvents() tests.
     */
    Runnable initialize(String[] arguments);

    /**
     * This will be called during the handoff to minecraft to update minecraft with the size of the framebuffer we have.
     * Generally won't be called because Minecraft figures it out for itself.
     * @param width Consumer of the framebuffer width
     * @param height Consumer of the framebuffer height
     */
    void updateFramebufferSize(IntConsumer width, IntConsumer height);

    /**
     * This is called to setup the minecraft window, as if Mojang had done it themselves in their Window class. This
     * handoff is difficult to get right - you have to make sure that any activities you're doing to the window are finished
     * prior to returning. You should try and setup the width and height as Mojang expects - the suppliers give you all that
     * information. Alternatively, you can force Mojang to update from the current position of the window in {@link #positionWindow(Optional, IntConsumer, IntConsumer, IntConsumer, IntConsumer)}
     * instead. This might give a more seamless experience.
     *
     * @param width This is the width of the window Mojang expects
     * @param height This is the height of the Window Mojang expects.
     * @param title This is the title for the window.
     * @param monitor This is the monitor it should appear on.
     * @return The window id
     */
    long setupMinecraftWindow(final IntSupplier width, final IntSupplier height, final Supplier<String> title, final LongSupplier monitor);

    /**
     * This is called after window handoff to allow us to tell Mojang about our window's position. This might give a
     * preferrable user experience to users, because we just tell Mojang our truth, rather than accept theirs.
     * @param monitor This is the monitor we're rendering on. Note that this is the Mojang monitor object. You might have trouble unwrapping it.
     * @param widthSetter This sets the width on the Mojang side
     * @param heightSetter This sets the height on the Mojang side
     * @param xSetter This sets the x coordinate on the Mojang side
     * @param ySetter This sets the y coordinate on the Mojang side
     * @return true if you've handled the window positioning - this skips the "forced fullscreen" code until a later stage
     */
    boolean positionWindow(Optional<Object> monitor, IntConsumer widthSetter, IntConsumer heightSetter, IntConsumer xSetter, IntConsumer ySetter);

    /**
     * Return a Supplier of an object extending the LoadingOverlay class from Mojang. This is what will be used once
     * the Mojang window code has taken over rendering of the window, to render the later stages of the loading process.
     *
     * @param mc This supplies the Minecraft object
     * @param ri This supplies the ReloadInstance object that tells us when the loading is finished
     * @param ex This Consumes the final state of the loading - if it's an error you pass it the Throwable, otherwise you
     *           pass Optional.empty()
     * @param fade This is the fade flag passed to LoadingOverlay. You probably want to ignore it.
     * @param <T> This is the type LoadingOverlay to allow type binding on the Mojang side
     * @return A supplier of your later LoadingOverlay screen.
     */
    <T> Supplier<T> loadingOverlay(Supplier<?> mc, Supplier<?> ri, Consumer<Optional<Throwable>> ex, boolean fade);

    /**
     * This is called during the module loading process to allow us to find objects inside the GAME layer, such as a
     * later loading screen.
     * @param layer This is the GAME layer from ModLauncher
     */
    void updateModuleReads(ModuleLayer layer);

    /**
     * This is called periodically during the loading process to "tick" the window. It is typically the same as the Runnable
     * from {@link #initialize(String[])}
     */
    void periodicTick();

    /**
     * This is called to construct a {@link net.minecraftforge.forgespi.locating.ForgeFeature} for the GL_VERSION we
     * managed to create for the window. Should be a string of the format {MAJOR}.{MINOR}, such as 4.6, 4.5 or such.
     *
     * @return the GL profile we created
     */
    String getGLVersion();
}
