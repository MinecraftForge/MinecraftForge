/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.event;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.fml.ModLoader;
import net.minecraftforge.fml.event.IModBusEvent;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import com.mojang.blaze3d.audio.Channel;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;

import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.ItemFrameRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.resources.PlayerSkin.Model;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.resources.ResourceProvider;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.event.sound.PlaySoundSourceEvent;
import net.minecraftforge.client.event.sound.PlayStreamingSourceEvent;
import net.minecraftforge.eventbus.api.Event;

@ApiStatus.Internal
public final class ForgeEventFactoryClient {
    private static final ModLoader ML = ModLoader.get();

    private ForgeEventFactoryClient() {}

    /**
     * Post an event to the {@link MinecraftForge#EVENT_BUS}
     * @return true if the event is {@link Cancelable} and has been canceled
     */
    private static boolean post(Event e) {
        return MinecraftForge.EVENT_BUS.post(e);
    }

    /**
     * Post an event to the {@link MinecraftForge#EVENT_BUS}, then return the event object
     * @return the event object passed in and possibly modified by listeners
     */
    private static <E extends Event> E fire(E e) {
        return MinecraftForge.EVENT_BUS.fire(e);
    }

    /**
     * Post an event to the {@link ModLoader#get()} event bus
     */
    private static <T extends Event & IModBusEvent> void postModBus(T e) {
        ML.postEvent(e);
    }

    public static void onGatherLayers(Map<EntityType<?>, EntityRenderer<?>> renderers, Map<Model, EntityRenderer<? extends Player>> playerRenderers, Context context) {
        // TODO: Why is this a ModLoader event...
        postModBus(new EntityRenderersEvent.AddLayers(renderers, playerRenderers, context));
    }

    public static void onRegisterShaders(ResourceProvider resourceProvider, List<Pair<ShaderInstance, Consumer<ShaderInstance>>> shaderList) {
        // TODO: Why is this a ModLoader event...
        postModBus(new RegisterShadersEvent(resourceProvider, shaderList));
    }

    public static void onScreenMouseReleased(boolean[] state, Screen screen, double mouseX, double mouseY, int button) {
        state[0] = post(new ScreenEvent.MouseButtonReleased.Pre(screen, mouseX, mouseY, button));
        if (!state[0]) {
            state[0] = screen.mouseReleased(mouseX, mouseY, button);
            var result = fire(new ScreenEvent.MouseButtonReleased.Post(screen, mouseX, mouseY, button, state[0])).getResult();
            state[0] = result == Event.Result.DEFAULT ? state[0] : result == Event.Result.ALLOW;
        }
    }

    public static void onScreenMouseClicked(boolean[] state, Screen screen, double mouseX, double mouseY, int button) {
        state[0] = post(new ScreenEvent.MouseButtonPressed.Pre(screen, mouseX, mouseY, button));
        if (!state[0]) {
            state[0] = screen.mouseClicked(mouseX, mouseY, button);
        }

        var result = fire(new ScreenEvent.MouseButtonPressed.Post(screen, mouseX, mouseY, button, state[0])).getResult();
        state[0] = result == Event.Result.DEFAULT ? state[0] : result == Event.Result.ALLOW;
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

    public static ScreenshotEvent onScreenshot(NativeImage image, File screenshotFile) {
        return fire(new ScreenshotEvent(image, screenshotFile));
    }

    public static boolean onScreenKeyPressedPre(Screen screen, int keyCode, int scanCode, int modifiers) {
        return post(new ScreenEvent.KeyPressed.Pre(screen, keyCode, scanCode, modifiers));
    }

    public static boolean onScreenKeyPressedPost(Screen screen, int keyCode, int scanCode, int modifiers) {
        return post(new ScreenEvent.KeyPressed.Post(screen, keyCode, scanCode, modifiers));
    }

    public static boolean onScreenKeyReleasedPre(Screen screen, int keyCode, int scanCode, int modifiers) {
        return post(new ScreenEvent.KeyReleased.Pre(screen, keyCode, scanCode, modifiers));
    }

    public static boolean onScreenKeyReleasedPost(Screen screen, int keyCode, int scanCode, int modifiers) {
        return post(new ScreenEvent.KeyReleased.Post(screen, keyCode, scanCode, modifiers));
    }

    public static boolean onScreenCharTypedPre(Screen screen, char codePoint, int modifiers) {
        return post(new ScreenEvent.CharacterTyped.Pre(screen, codePoint, modifiers));
    }

    public static boolean onScreenCharTypedPost(Screen screen, char codePoint, int modifiers) {
        return post(new ScreenEvent.CharacterTyped.Post(screen, codePoint, modifiers));
    }

    public static InputEvent.InteractionKeyMappingTriggered onClickInput(int button, KeyMapping keyBinding, InteractionHand hand) {
        return fire(new InputEvent.InteractionKeyMappingTriggered(button, keyBinding, hand));
    }

    public static void onContainerRenderBackground(AbstractContainerScreen<?> screen, GuiGraphics graphics, int mouseX, int mouseY) {
        post(new ContainerScreenEvent.Render.Background(screen, graphics, mouseX, mouseY));
    }

    public static void onContainerRenderForeground(AbstractContainerScreen<?> screen, GuiGraphics graphics, int mouseX, int mouseY) {
        post(new ContainerScreenEvent.Render.Foreground(screen, graphics, mouseX, mouseY));
    }

    public static void firePlayerLogin(MultiPlayerGameMode pc, LocalPlayer player, Connection networkManager) {
        post(new ClientPlayerNetworkEvent.LoggingIn(pc, player, networkManager));
    }

    public static void firePlayerLogout(@Nullable MultiPlayerGameMode pc, @Nullable LocalPlayer player) {
        post(new ClientPlayerNetworkEvent.LoggingOut(pc, player, player != null ? player.connection != null ? player.connection.getConnection() : null : null));
    }

    public static void firePlayerRespawn(MultiPlayerGameMode pc, LocalPlayer oldPlayer, LocalPlayer newPlayer, Connection networkManager) {
        post(new ClientPlayerNetworkEvent.Clone(pc, oldPlayer, newPlayer, networkManager));
    }

    public static ViewportEvent.ComputeFov fireComputeFov(GameRenderer renderer, Camera camera, double partialTick, double fov, boolean usedConfiguredFov) {
        return fire(new ViewportEvent.ComputeFov(renderer, camera, partialTick, fov, usedConfiguredFov));
    }

    public static ViewportEvent.ComputeCameraAngles fireComputeCameraAngles(GameRenderer renderer, Camera camera, float partial) {
        return fire(new ViewportEvent.ComputeCameraAngles(renderer, camera, partial, camera.getYRot(), camera.getXRot(), 0));
    }

    public static <T extends LivingEntity, M extends EntityModel<T>> boolean onRenderLivingPre(LivingEntity entity, LivingEntityRenderer<T, M> renderer, float partialTick, PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight) {
        return post(new RenderLivingEvent.Pre<T, M>(entity, renderer, partialTick, poseStack, multiBufferSource, packedLight));
    }

    public static <T extends LivingEntity, M extends EntityModel<T>> boolean onRenderLivingPost(LivingEntity entity, LivingEntityRenderer<T, M> renderer, float partialTick, PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight) {
        return post(new RenderLivingEvent.Post<T, M>(entity, renderer, partialTick, poseStack, multiBufferSource, packedLight));
    }

    public static boolean onRenderPlayerPre(Player player, PlayerRenderer renderer, float partialTick, PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight) {
        return post(new RenderPlayerEvent.Pre(player, renderer, partialTick, poseStack, multiBufferSource, packedLight));
    }

    public static boolean onRenderPlayerPost(Player player, PlayerRenderer renderer, float partialTick, PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight) {
        return post(new RenderPlayerEvent.Post(player, renderer, partialTick, poseStack, multiBufferSource, packedLight));
    }

    public static boolean onRenderArm(PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight, AbstractClientPlayer player, HumanoidArm arm) {
        return post(new RenderArmEvent(poseStack, multiBufferSource, packedLight, player, arm));
    }

    public static boolean onRenderItemInFrame(ItemFrame itemFrame, ItemFrameRenderer<?> renderItemFrame, PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight) {
        return post(new RenderItemInFrameEvent(itemFrame, renderItemFrame, poseStack, multiBufferSource, packedLight));
    }

    public static RenderNameTagEvent fireRenderNameTagEvent(Entity entity, Component content, EntityRenderer<?> entityRenderer, PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight, float partialTick) {
        return fire(new RenderNameTagEvent(entity, content, entityRenderer, poseStack, multiBufferSource, packedLight, partialTick));
    }

    public static void onRenderScreenBackground(Screen screen, GuiGraphics guiGraphics) {
        fire(new ScreenEvent.BackgroundRendered(screen, guiGraphics));
    }

    public static void onRenderTickStart(DeltaTracker timer) {
        post(new TickEvent.RenderTickEvent.Pre(timer));
    }

    public static void onRenderTickEnd(DeltaTracker timer) {
        post(new TickEvent.RenderTickEvent.Post(timer));
    }
}
