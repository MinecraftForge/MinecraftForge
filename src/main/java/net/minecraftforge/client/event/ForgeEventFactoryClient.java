/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.event;

import java.util.Map;
import java.util.function.Function;

import net.minecraftforge.client.gui.overlay.ForgeLayeredDraw;
import net.minecraftforge.common.util.Result;
import net.minecraftforge.event.TickEvent;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.audio.Channel;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.ClientRecipeBook;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.entity.ClientAvatarEntity;
import net.minecraft.client.entity.ClientMannequin;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.MouseButtonInfo;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.object.skull.SkullModelBase;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.ItemFrameRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.ItemFrameRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.network.Connection;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Avatar;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.PlayerModelType;
import net.minecraft.world.level.block.SkullBlock.Type;
import net.minecraftforge.client.event.sound.PlaySoundSourceEvent;
import net.minecraftforge.client.event.sound.PlayStreamingSourceEvent;

@ApiStatus.Internal
public final class ForgeEventFactoryClient {
    private ForgeEventFactoryClient() {}

    public static void onGatherLayers(Map<EntityType<?>, EntityRenderer<?, ?>> renderers, Map<PlayerModelType, AvatarRenderer<AbstractClientPlayer>> playerRenderers, Map<PlayerModelType, AvatarRenderer<ClientMannequin>> mannequinRenderers, Context context) {
        EntityRenderersEvent.AddLayers.BUS.post(new EntityRenderersEvent.AddLayers(renderers, playerRenderers, mannequinRenderers, context));
    }

    public static boolean onScreenMouseReleased(Screen screen, double mouseX, double mouseY, MouseButtonEvent event) {
        if (ScreenEvent.MouseButtonReleased.Pre.BUS.post(new ScreenEvent.MouseButtonReleased.Pre(screen, mouseX, mouseY, event.button())))
            return true;

        var ret = screen.mouseReleased(event);
        var result = ScreenEvent.MouseButtonReleased.Post.BUS.fire(new ScreenEvent.MouseButtonReleased.Post(screen, mouseX, mouseY, event.button(), ret)).getResult();
        return result == Result.DEFAULT ? ret : result == Result.ALLOW;
    }

    public static boolean onScreenMouseClicked(Screen screen, double mouseX, double mouseY, MouseButtonEvent info, boolean repeate) {
        var ret = ScreenEvent.MouseButtonPressed.Pre.BUS.post(new ScreenEvent.MouseButtonPressed.Pre(screen, mouseX, mouseY, info));
        if (!ret)
            ret = screen.mouseClicked(info, repeate);

        var result = ScreenEvent.MouseButtonPressed.Post.BUS.fire(new ScreenEvent.MouseButtonPressed.Post(screen, mouseX, mouseY, info, ret, new Result.Holder())).getResult();
        return result == Result.DEFAULT ? ret : result == Result.ALLOW;
    }

    public static boolean onMouseButtonPre(MouseButtonInfo info, int action) {
        return InputEvent.MouseButton.Pre.BUS.post(new InputEvent.MouseButton.Pre(info, action));
    }

    public static void onMouseButtonPost(MouseButtonInfo info, int action) {
        InputEvent.MouseButton.Post.BUS.post(new InputEvent.MouseButton.Post(info, action));
    }

    public static boolean onScreenMouseScrollPre(Screen guiScreen, double mouseX, double mouseY, double deltaX, double deltaY) {
        return ScreenEvent.MouseScrolled.Pre.BUS.post(new ScreenEvent.MouseScrolled.Pre(guiScreen, mouseX, mouseY, deltaX, deltaY));
    }

    public static void onScreenMouseScrollPost(Screen guiScreen, double mouseX, double mouseY, double deltaX, double deltaY) {
        ScreenEvent.MouseScrolled.Post.BUS.post(new ScreenEvent.MouseScrolled.Post(guiScreen, mouseX, mouseY, deltaX, deltaY));
    }

    public static boolean onMouseScroll(MouseHandler mouseHelper, double deltaX, double deltaY) {
        return InputEvent.MouseScrollingEvent.BUS.post(new InputEvent.MouseScrollingEvent(deltaX, deltaY, mouseHelper.isLeftPressed(), mouseHelper.isMiddlePressed(), mouseHelper.isRightPressed(), mouseHelper.xpos(), mouseHelper.ypos()));
    }

    public static boolean onScreenMouseDragPre(Screen guiScreen, double mouseX, double mouseY, int mouseButton, double dragX, double dragY) {
        return ScreenEvent.MouseDragged.Pre.BUS.post(new ScreenEvent.MouseDragged.Pre(guiScreen, mouseX, mouseY, mouseButton, dragX, dragY));
    }

    public static boolean onScreenMouseDragPost(Screen guiScreen, double mouseX, double mouseY, int mouseButton, double dragX, double dragY) {
        return ScreenEvent.MouseDragged.Post.BUS.post(new ScreenEvent.MouseDragged.Post(guiScreen, mouseX, mouseY, mouseButton, dragX, dragY));
    }

    public static @Nullable Screen onScreenOpening(Screen old, Screen screen) {
         var event = new ScreenEvent.Opening(old, screen);
         if (ScreenEvent.Opening.BUS.post(event))
             return null;
         return event.getNewScreen();
    }

    public static void onScreenClose(Screen screen) {
        ScreenEvent.Closing.BUS.post(new ScreenEvent.Closing(screen));
    }

    public static void onPlaySoundSource(SoundEngine engine, SoundInstance sound, Channel channel) {
        PlaySoundSourceEvent.BUS.post(new PlaySoundSourceEvent(engine, sound, channel));
    }

    public static void onPlayStreamingSource(SoundEngine engine, SoundInstance sound, Channel channel) {
        PlayStreamingSourceEvent.BUS.post(new PlayStreamingSourceEvent(engine, sound, channel));
    }

    public static boolean onScreenKeyPressedPre(Screen screen, KeyEvent info) {
        return ScreenEvent.KeyPressed.Pre.BUS.post(new ScreenEvent.KeyPressed.Pre(screen, info));
    }

    public static boolean onScreenKeyPressedPost(Screen screen, KeyEvent info) {
        return ScreenEvent.KeyPressed.Post.BUS.post(new ScreenEvent.KeyPressed.Post(screen, info));
    }

    public static boolean onScreenKeyReleasedPre(Screen screen, KeyEvent info) {
        return ScreenEvent.KeyReleased.Pre.BUS.post(new ScreenEvent.KeyReleased.Pre(screen, info));
    }

    public static boolean onScreenKeyReleasedPost(Screen screen, KeyEvent info) {
        return ScreenEvent.KeyReleased.Post.BUS.post(new ScreenEvent.KeyReleased.Post(screen, info));
    }

    public static boolean onScreenCharTypedPre(Screen screen, CharacterEvent info) {
        return ScreenEvent.CharacterTyped.Pre.BUS.post(new ScreenEvent.CharacterTyped.Pre(screen, info));
    }

    public static boolean onScreenCharTypedPost(Screen screen, CharacterEvent info) {
        return ScreenEvent.CharacterTyped.Post.BUS.post(new ScreenEvent.CharacterTyped.Post(screen, info));
    }

    public static boolean onClickInputPickBlock(KeyMapping keyBinding) {
        var event = new InputEvent.InteractionKeyMappingTriggered(2, keyBinding, InteractionHand.MAIN_HAND);
        return InputEvent.InteractionKeyMappingTriggered.BUS.post(event);
    }

    public static void onContainerRenderBackground(AbstractContainerScreen<?> screen, GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        ContainerScreenEvent.Render.Background.BUS.post(new ContainerScreenEvent.Render.Background(screen, graphics, mouseX, mouseY));
    }

    public static void onContainerRenderForeground(AbstractContainerScreen<?> screen, GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        ContainerScreenEvent.Render.Foreground.BUS.post(new ContainerScreenEvent.Render.Foreground(screen, graphics, mouseX, mouseY));
    }

    public static void firePlayerLogin(MultiPlayerGameMode pc, LocalPlayer player, Connection networkManager) {
        ClientPlayerNetworkEvent.LoggingIn.BUS.post(new ClientPlayerNetworkEvent.LoggingIn(pc, player, networkManager));
    }

    public static void firePlayerLogout(@Nullable MultiPlayerGameMode pc, @Nullable LocalPlayer player) {
        ClientPlayerNetworkEvent.LoggingOut.BUS.post(new ClientPlayerNetworkEvent.LoggingOut(pc, player, player != null ? player.connection != null ? player.connection.getConnection() : null : null));
    }

    public static void firePlayerRespawn(MultiPlayerGameMode pc, LocalPlayer oldPlayer, LocalPlayer newPlayer, Connection networkManager) {
        ClientPlayerNetworkEvent.Clone.BUS.post(new ClientPlayerNetworkEvent.Clone(pc, oldPlayer, newPlayer, networkManager));
    }

    public static ViewportEvent.ComputeFov fireComputeFov(GameRenderer renderer, Camera camera, float partialTick, float fov, boolean usedConfiguredFov) {
        return ViewportEvent.ComputeFov.BUS.fire(new ViewportEvent.ComputeFov(renderer, camera, partialTick, fov, usedConfiguredFov));
    }

    public static ViewportEvent.ComputeCameraAngles fireComputeCameraAngles(GameRenderer renderer, Camera camera, float partial) {
        return ViewportEvent.ComputeCameraAngles.BUS.fire(new ViewportEvent.ComputeCameraAngles(renderer, camera, partial, camera.yRot(), camera.xRot(), 0));
    }

    public static <T extends LivingEntity, S extends LivingEntityRenderState, M extends EntityModel<? super S>> boolean onRenderLivingPre(S state, LivingEntityRenderer<T, S, M> renderer, PoseStack poseStack, SubmitNodeCollector nodeCollector, CameraRenderState cameraState) {
        return RenderLivingEvent.Pre.BUS.post(new RenderLivingEvent.Pre<T, S, M>(state, renderer, poseStack, nodeCollector, cameraState));
    }

    public static <T extends LivingEntity, S extends LivingEntityRenderState, M extends EntityModel<? super S>> boolean onRenderLivingPost(S state, LivingEntityRenderer<T, S, M> renderer, PoseStack poseStack, SubmitNodeCollector nodeCollector, CameraRenderState cameraState) {
        return RenderLivingEvent.Post.BUS.post(new RenderLivingEvent.Post<T, S, M>(state, renderer, poseStack, nodeCollector, cameraState));
    }

    public static <AvatarlikeEntity extends Avatar & ClientAvatarEntity> boolean onRenderAvatarPre(AvatarRenderState player, AvatarRenderer<AvatarlikeEntity> renderer, PoseStack poseStack, SubmitNodeCollector nodeCollector, CameraRenderState camera) {
        return RenderAvatarEvent.Pre.BUS.post(new RenderAvatarEvent.Pre(player, renderer, poseStack, nodeCollector, camera));
    }

    public static <AvatarlikeEntity extends Avatar & ClientAvatarEntity> boolean onRenderAvatarPost(AvatarRenderState player, AvatarRenderer<AvatarlikeEntity> renderer, PoseStack poseStack, SubmitNodeCollector nodeCollector, CameraRenderState camera) {
        return RenderAvatarEvent.Post.BUS.post(new RenderAvatarEvent.Post(player, renderer, poseStack, nodeCollector, camera));
    }

    public static boolean onRenderArm(PoseStack poseStack, SubmitNodeCollector nodeCollector, int packedLight, HumanoidArm arm) {
        return RenderArmEvent.BUS.post(new RenderArmEvent(poseStack, nodeCollector, packedLight, arm));
    }

    public static boolean onRenderItemInFrame(ItemFrameRenderState state, ItemFrameRenderer<?> renderItemFrame, PoseStack poseStack, SubmitNodeCollector nodeCollector, int packedLight) {
        return RenderItemInFrameEvent.BUS.post(new RenderItemInFrameEvent(state, renderItemFrame, poseStack, nodeCollector, packedLight));
    }

    public static RenderNameTagEvent fireRenderNameTagEvent(EntityRenderState state, EntityRenderer<?, ?> entityRenderer, PoseStack poseStack, SubmitNodeCollector nodeCollector, CameraRenderState cameraState) {
        return RenderNameTagEvent.BUS.fire(new RenderNameTagEvent(state, entityRenderer, poseStack, nodeCollector, cameraState));
    }

    public static void onRenderScreenBackground(Screen screen, GuiGraphicsExtractor guiGraphics) {
        ScreenEvent.BackgroundRendered.BUS.post(new ScreenEvent.BackgroundRendered(screen, guiGraphics));
    }

    public static void onRenderTickStart(DeltaTracker timer) {
        TickEvent.RenderTickEvent.Pre.BUS.post(new TickEvent.RenderTickEvent.Pre(timer));
    }

    public static void onRenderTickEnd(DeltaTracker timer) {
        TickEvent.RenderTickEvent.Post.BUS.post(new TickEvent.RenderTickEvent.Post(timer));
    }

    public static boolean onToastAdd(Toast toast) {
        return ToastAddEvent.BUS.post(new ToastAddEvent(toast));
    }

    public static @Nullable ScreenEvent.RenderInventoryMobEffects onScreenEffectSize(Screen screen, int availableSpace, boolean compact, int horizontalOffset) {
        var event = new ScreenEvent.RenderInventoryMobEffects(screen, availableSpace, compact, horizontalOffset);
        return ScreenEvent.RenderInventoryMobEffects.BUS.post(event) ? null : event;
    }

    public static void onRecipesUpdated(ClientRecipeBook book) {
        RecipesUpdatedEvent.BUS.post(new RecipesUpdatedEvent(book));
    }

    public static ComputeFovModifierEvent fireFovModifierEvent(Player entity, float modifier, float scale) {
        return ComputeFovModifierEvent.BUS.fire(new ComputeFovModifierEvent(entity, modifier, scale));
    }

    public static Map<Type, Function<EntityModelSet, SkullModelBase>> onCreateSkullModels() {
        var builder = ImmutableMap.<Type, Function<EntityModelSet, SkullModelBase>>builder();
        EntityRenderersEvent.CreateSkullModels.BUS.post(new EntityRenderersEvent.CreateSkullModels(builder));
        return builder.build();
    }

    public static ModelEvent.RegisterModelStateDefinitions onRegisterModeStateDefinitions() {
        return ModelEvent.RegisterModelStateDefinitions.BUS.fire(new ModelEvent.RegisterModelStateDefinitions());
    }

    public static void onInitLevelRenderer() {
        AddFramePassEvent.BUS.post(new AddFramePassEvent());
    }

    public static void onComputeLayerOrder(ForgeLayeredDraw layeredDraw) {
        AddGuiOverlayLayersEvent.BUS.post(new AddGuiOverlayLayersEvent(layeredDraw));
    }
}
