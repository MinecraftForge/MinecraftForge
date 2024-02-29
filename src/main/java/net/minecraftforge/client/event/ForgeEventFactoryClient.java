/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.event;

import java.util.Map;

import org.jetbrains.annotations.Nullable;

import com.mojang.blaze3d.audio.Channel;

import net.minecraft.client.MouseHandler;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.resources.PlayerSkin.Model;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.event.sound.PlaySoundSourceEvent;
import net.minecraftforge.client.event.sound.PlayStreamingSourceEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.ModLoader;

public class ForgeEventFactoryClient {
    private static boolean post(Event e) {
        return MinecraftForge.EVENT_BUS.post(e);
    }

    private static <E extends Event> E fire(E e) {
        MinecraftForge.EVENT_BUS.post(e);
        return e;
    }

    public static void gatherLayers(Map<EntityType<?>, EntityRenderer<?>> renderers, Map<Model, EntityRenderer<? extends Player>> playerRenderers, Context context) {
        // TODO: Why is this a ModLoader event...
        ModLoader.get().postEvent(new EntityRenderersEvent.AddLayers(renderers, playerRenderers, context));
    }

    public static boolean onScreenMouseReleasedPre(Screen guiScreen, double mouseX, double mouseY, int button) {
        return post(new ScreenEvent.MouseButtonReleased.Pre(guiScreen, mouseX, mouseY, button));
    }

    public static boolean onScreenMouseReleasedPost(Screen guiScreen, double mouseX, double mouseY, int button, boolean handled) {
        var result = fire(new ScreenEvent.MouseButtonReleased.Post(guiScreen, mouseX, mouseY, button, handled)).getResult();
        return result == Event.Result.DEFAULT ? handled : result == Event.Result.ALLOW;
    }

    public static boolean onScreenMouseClickedPre(Screen guiScreen, double mouseX, double mouseY, int button) {
        return post(new ScreenEvent.MouseButtonPressed.Pre(guiScreen, mouseX, mouseY, button));
    }

    public static boolean onScreenMouseClickedPost(Screen guiScreen, double mouseX, double mouseY, int button, boolean handled) {
        var result = fire(new ScreenEvent.MouseButtonPressed.Post(guiScreen, mouseX, mouseY, button, handled)).getResult();
        return result == Event.Result.DEFAULT ? handled : result == Event.Result.ALLOW;
    }

    public static boolean onMouseButtonPre(int button, int action, int mods) {
        return post(new InputEvent.MouseButton.Pre(button, action, mods));
    }

    public static void onMouseButtonPost(int button, int action, int mods) {
        post(new InputEvent.MouseButton.Post(button, action, mods));
    }

    public static boolean onScreenMouseScrollPre(Screen guiScreen, double mouseX, double mouseY, double deltaX, double deltaY) {
        return post(new ScreenEvent.MouseScrolled.Pre(guiScreen, mouseX, mouseY, deltaX, deltaY));
    }

    public static void onScreenMouseScrollPost(Screen guiScreen, double mouseX, double mouseY, double deltaX, double deltaY) {
        post(new ScreenEvent.MouseScrolled.Post(guiScreen, mouseX, mouseY, deltaX, deltaY));
    }

    public static boolean onMouseScroll(MouseHandler mouseHelper, double deltaX, double deltaY) {
        return post(new InputEvent.MouseScrollingEvent(deltaX, deltaY, mouseHelper.isLeftPressed(), mouseHelper.isMiddlePressed(), mouseHelper.isRightPressed(), mouseHelper.xpos(), mouseHelper.ypos()));
    }

    public static boolean onScreenMouseDragPre(Screen guiScreen, double mouseX, double mouseY, int mouseButton, double dragX, double dragY) {
        return post(new ScreenEvent.MouseDragged.Pre(guiScreen, mouseX, mouseY, mouseButton, dragX, dragY));
    }

    public static void onScreenMouseDragPost(Screen guiScreen, double mouseX, double mouseY, int mouseButton, double dragX, double dragY) {
        post(new ScreenEvent.MouseDragged.Post(guiScreen, mouseX, mouseY, mouseButton, dragX, dragY));
    }

    public static @Nullable Screen onScreenOpening(Screen old, Screen screen) {
         var event = new ScreenEvent.Opening(old, screen);
         if (post(event))
             return null;
         return event.getNewScreen();
    }

    public static void onScreenClose(Screen screen) {
        post(new ScreenEvent.Closing(screen));
    }

    public static void onPlaySoundSource(SoundEngine engine, SoundInstance sound, Channel channel) {
        post(new PlaySoundSourceEvent(engine, sound, channel));
    }

    public static void onPlayStreamingSource(SoundEngine engine, SoundInstance sound, Channel channel) {
        post(new PlayStreamingSourceEvent(engine, sound, channel));
    }
}
