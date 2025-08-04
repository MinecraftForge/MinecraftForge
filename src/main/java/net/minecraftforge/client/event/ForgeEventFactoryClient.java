/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.event;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.Result;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.ModLoader;
import net.minecraftforge.fml.event.IModBusEvent;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.audio.Channel;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.ClientRecipeBook;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.SkullModelBase;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.ItemFrameRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.ItemFrameRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.resources.PlayerSkin.Model;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SkullBlock.Type;
import net.minecraftforge.client.event.sound.PlaySoundSourceEvent;
import net.minecraftforge.client.event.sound.PlayStreamingSourceEvent;

@ApiStatus.Internal
public final class ForgeEventFactoryClient {
    private static final ModLoader ML = ModLoader.get();

    private ForgeEventFactoryClient() {}

    /**
     * Post an event to the {@link ModLoader#get()} event bus
     */
    private static <T extends IModBusEvent> T fireModBus(T e) {
        return ML.postEventWithReturn(e);
    }

    public static void onGatherLayers(Map<EntityType<?>, EntityRenderer<?, ?>> renderers, Map<Model, EntityRenderer<? extends Player, ?>> playerRenderers, Context context) {
        // TODO: Why is this a ModLoader event...
        fireModBus(new EntityRenderersEvent.AddLayers(renderers, playerRenderers, context));
    }

    public static boolean onScreenMouseReleased(Screen screen, double mouseX, double mouseY, int button) {
        if (ScreenEvent.MouseButtonReleased.Pre.BUS.post(new ScreenEvent.MouseButtonReleased.Pre(screen, mouseX, mouseY, button)))
            return true;

        var ret = screen.mouseReleased(mouseX, mouseY, button);
        var result = ScreenEvent.MouseButtonReleased.Post.BUS.fire(new ScreenEvent.MouseButtonReleased.Post(screen, mouseX, mouseY, button, ret)).getResult();
        return result == Result.DEFAULT ? ret : result == Result.ALLOW;
    }

    public static boolean onScreenMouseClicked(Screen screen, double mouseX, double mouseY, int button) {
        var ret = ScreenEvent.MouseButtonPressed.Pre.BUS.post(new ScreenEvent.MouseButtonPressed.Pre(screen, mouseX, mouseY, button));
        if (!ret)
            ret = screen.mouseClicked(mouseX, mouseY, button);

        var result = ScreenEvent.MouseButtonPressed.Post.BUS.fire(new ScreenEvent.MouseButtonPressed.Post(screen, mouseX, mouseY, button, ret)).getResult();
        return result == Result.DEFAULT ? ret : result == Result.ALLOW;
    }

    public static boolean onMouseButtonPre(int button, int action, int mods) {
        return InputEvent.MouseButton.Pre.BUS.post(new InputEvent.MouseButton.Pre(button, action, mods));
    }

    public static void onMouseButtonPost(int button, int action, int mods) {
        InputEvent.MouseButton.Post.BUS.post(new InputEvent.MouseButton.Post(button, action, mods));
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

    public static boolean onScreenKeyPressedPre(Screen screen, int keyCode, int scanCode, int modifiers) {
        return ScreenEvent.KeyPressed.Pre.BUS.post(new ScreenEvent.KeyPressed.Pre(screen, keyCode, scanCode, modifiers));
    }

    public static boolean onScreenKeyPressedPost(Screen screen, int keyCode, int scanCode, int modifiers) {
        return ScreenEvent.KeyPressed.Post.BUS.post(new ScreenEvent.KeyPressed.Post(screen, keyCode, scanCode, modifiers));
    }

    public static boolean onScreenKeyReleasedPre(Screen screen, int keyCode, int scanCode, int modifiers) {
        return ScreenEvent.KeyReleased.Pre.BUS.post(new ScreenEvent.KeyReleased.Pre(screen, keyCode, scanCode, modifiers));
    }

    public static boolean onScreenKeyReleasedPost(Screen screen, int keyCode, int scanCode, int modifiers) {
        return ScreenEvent.KeyReleased.Post.BUS.post(new ScreenEvent.KeyReleased.Post(screen, keyCode, scanCode, modifiers));
    }

    public static boolean onScreenCharTypedPre(Screen screen, char codePoint, int modifiers) {
        return ScreenEvent.CharacterTyped.Pre.BUS.post(new ScreenEvent.CharacterTyped.Pre(screen, codePoint, modifiers));
    }

    public static boolean onScreenCharTypedPost(Screen screen, char codePoint, int modifiers) {
        return ScreenEvent.CharacterTyped.Post.BUS.post(new ScreenEvent.CharacterTyped.Post(screen, codePoint, modifiers));
    }

    public static boolean onClickInputPickBlock(KeyMapping keyBinding) {
        var event = new InputEvent.InteractionKeyMappingTriggered(2, keyBinding, InteractionHand.MAIN_HAND);
        return InputEvent.InteractionKeyMappingTriggered.BUS.post(event);
    }

    public static void onContainerRenderBackground(AbstractContainerScreen<?> screen, GuiGraphics graphics, int mouseX, int mouseY) {
        ContainerScreenEvent.Render.Background.BUS.post(new ContainerScreenEvent.Render.Background(screen, graphics, mouseX, mouseY));
    }

    public static void onContainerRenderForeground(AbstractContainerScreen<?> screen, GuiGraphics graphics, int mouseX, int mouseY) {
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

    public static ViewportEvent.ComputeFov fireComputeFov(GameRenderer renderer, Camera camera, double partialTick, float fov, boolean usedConfiguredFov) {
        return ViewportEvent.ComputeFov.BUS.fire(new ViewportEvent.ComputeFov(renderer, camera, partialTick, fov, usedConfiguredFov));
    }

    public static ViewportEvent.ComputeCameraAngles fireComputeCameraAngles(GameRenderer renderer, Camera camera, float partial) {
        return ViewportEvent.ComputeCameraAngles.BUS.fire(new ViewportEvent.ComputeCameraAngles(renderer, camera, partial, camera.getYRot(), camera.getXRot(), 0));
    }

    public static <T extends LivingEntity, S extends LivingEntityRenderState, M extends EntityModel<? super S>> boolean onRenderLivingPre(S state, LivingEntityRenderer<T, S, M> renderer, PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight) {
        return RenderLivingEvent.Pre.BUS.post(new RenderLivingEvent.Pre<T, S, M>(state, renderer, poseStack, multiBufferSource, packedLight));
    }

    public static <T extends LivingEntity, S extends LivingEntityRenderState, M extends EntityModel<? super S>> boolean onRenderLivingPost(S state, LivingEntityRenderer<T, S, M> renderer, PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight) {
        return RenderLivingEvent.Post.BUS.post(new RenderLivingEvent.Post<T, S, M>(state, renderer, poseStack, multiBufferSource, packedLight));
    }

    public static boolean onRenderPlayerPre(PlayerRenderState player, PlayerRenderer renderer, PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight) {
        return RenderPlayerEvent.Pre.BUS.post(new RenderPlayerEvent.Pre(player, renderer, poseStack, multiBufferSource, packedLight));
    }

    public static boolean onRenderPlayerPost(PlayerRenderState player, PlayerRenderer renderer, PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight) {
        return RenderPlayerEvent.Post.BUS.post(new RenderPlayerEvent.Post(player, renderer, poseStack, multiBufferSource, packedLight));
    }

    public static boolean onRenderArm(PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight, HumanoidArm arm) {
        return RenderArmEvent.BUS.post(new RenderArmEvent(poseStack, multiBufferSource, packedLight, arm));
    }

    public static boolean onRenderItemInFrame(ItemFrameRenderState state, ItemFrameRenderer<?> renderItemFrame, PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight) {
        return RenderItemInFrameEvent.BUS.post(new RenderItemInFrameEvent(state, renderItemFrame, poseStack, multiBufferSource, packedLight));
    }

    public static RenderNameTagEvent fireRenderNameTagEvent(EntityRenderState state, Component content, EntityRenderer<?, ?> entityRenderer, PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight) {
        return RenderNameTagEvent.BUS.fire(new RenderNameTagEvent(state, content, entityRenderer, poseStack, multiBufferSource, packedLight));
    }

    public static void onRenderScreenBackground(Screen screen, GuiGraphics guiGraphics) {
        ScreenEvent.BackgroundRendered.BUS.post(new ScreenEvent.BackgroundRendered(screen, guiGraphics));
    }

    public static void onRenderTickStart(DeltaTracker timer) {
        TickEvent.RenderTickEvent.Pre.BUS.post(new TickEvent.RenderTickEvent.Pre(timer));
    }

    public static void onRenderTickEnd(DeltaTracker timer) {
        TickEvent.RenderTickEvent.Post.BUS.post(new TickEvent.RenderTickEvent.Post(timer));
    }

    public static RenderTooltipEvent.Background onRenderTooltipBackground(@NotNull ItemStack stack, GuiGraphics graphics, int x, int y, @NotNull Font font, @NotNull List<ClientTooltipComponent> components, @Nullable ResourceLocation backgroundPrefix) {
        return RenderTooltipEvent.Background.BUS.fire(new RenderTooltipEvent.Background(stack, graphics, x, y, font, components, backgroundPrefix));
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

    public static void onCreateSpecialBlockRenderers(Map<Block, SpecialModelRenderer.Unbaked> map) {
        CreateSpecialBlockRendererEvent.BUS.post(new CreateSpecialBlockRendererEvent(map));
    }

    public static Map<Type, Function<EntityModelSet, SkullModelBase>> onCreateSkullModels() {
        var builder = ImmutableMap.<Type, Function<EntityModelSet, SkullModelBase>>builder();
        fireModBus(new EntityRenderersEvent.CreateSkullModels(builder));
        return builder.build();
    }

    public static ModelEvent.RegisterModelStateDefinitions onRegisterModeStateDefinitions() {
        // This is on the mod bus because it happens during the initial texture reload, which is while the Forge bus is shut down.
        return fireModBus(new ModelEvent.RegisterModelStateDefinitions());
    }
}
