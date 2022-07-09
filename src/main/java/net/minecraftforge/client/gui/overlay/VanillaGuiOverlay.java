/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.gui.overlay;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.GameType;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;

/**
 * All the vanilla {@linkplain IGuiOverlay HUD overlays} in the order that they render.
 */
public enum VanillaGuiOverlay
{
    VIGNETTE("vignette", (gui, poseStack, partialTick, screenWidth, screenHeight) -> {
        if (Minecraft.useFancyGraphics())
        {
            gui.setupOverlayRenderState(true, false);
            gui.renderVignette(gui.getMinecraft().getCameraEntity());
        }
    }),
    SPYGLASS("spyglass", (gui, poseStack, partialTick, screenWidth, screenHeight) -> {
        gui.setupOverlayRenderState(true, false);
        gui.renderSpyglassOverlay();
    }),
    HELMET("helmet", (gui, poseStack, partialTick, screenWidth, screenHeight) -> {
        gui.setupOverlayRenderState(true, false);
        gui.renderHelmet(partialTick, poseStack);
    }),
    FROSTBITE("frostbite", (gui, poseStack, partialTick, screenWidth, screenHeight) -> {
        gui.setupOverlayRenderState(true, false);
        gui.renderFrostbite(poseStack);
    }),
    PORTAL("portal", (gui, poseStack, partialTick, screenWidth, screenHeight) -> {

        if (!gui.getMinecraft().player.hasEffect(MobEffects.CONFUSION))
        {
            gui.setupOverlayRenderState(true, false);
            gui.renderPortalOverlay(partialTick);
        }

    }),
    HOTBAR("hotbar", (gui, poseStack, partialTick, screenWidth, screenHeight) -> {
        if (!gui.getMinecraft().options.hideGui)
        {
            gui.setupOverlayRenderState(true, false);
            if (gui.getMinecraft().gameMode.getPlayerMode() == GameType.SPECTATOR)
            {
                gui.getSpectatorGui().renderHotbar(poseStack);
            }
            else
            {
                gui.renderHotbar(partialTick, poseStack);
            }
        }
    }),
    CROSSHAIR("crosshair", (gui, poseStack, partialTick, screenWidth, screenHeight) -> {
        if (!gui.getMinecraft().options.hideGui)
        {
            gui.setupOverlayRenderState(true, false);
            gui.setBlitOffset(-90);

            gui.renderCrosshair(poseStack);
        }
    }),
    BOSS_EVENT_PROGRESS("boss_event_progress", (gui, poseStack, partialTick, screenWidth, screenHeight) -> {
        if (!gui.getMinecraft().options.hideGui)
        {
            gui.setupOverlayRenderState(true, false);
            gui.setBlitOffset(-90);

            gui.renderBossHealth(poseStack);
        }
    }),
    PLAYER_HEALTH("player_health", (gui, poseStack, partialTick, screenWidth, screenHeight) -> {
        if (!gui.getMinecraft().options.hideGui && gui.shouldDrawSurvivalElements())
        {
            gui.setupOverlayRenderState(true, false);
            gui.renderHealth(screenWidth, screenHeight, poseStack);
        }
    }),
    ARMOR_LEVEL("armor_level", (gui, poseStack, partialTick, screenWidth, screenHeight) -> {
        if (!gui.getMinecraft().options.hideGui && gui.shouldDrawSurvivalElements())
        {
            gui.setupOverlayRenderState(true, false);
            gui.renderArmor(poseStack, screenWidth, screenHeight);
        }
    }),
    FOOD_LEVEL("food_level", (gui, poseStack, partialTick, screenWidth, screenHeight) -> {
        boolean isMounted = gui.getMinecraft().player.getVehicle() instanceof LivingEntity;
        if (!isMounted && !gui.getMinecraft().options.hideGui && gui.shouldDrawSurvivalElements())
        {
            gui.setupOverlayRenderState(true, false);
            gui.renderFood(screenWidth, screenHeight, poseStack);
        }
    }),
    MOUNT_HEALTH("mount_health", (gui, poseStack, partialTick, screenWidth, screenHeight) -> {
        if (!gui.getMinecraft().options.hideGui && gui.shouldDrawSurvivalElements())
        {
            gui.setupOverlayRenderState(true, false);
            gui.renderHealthMount(screenWidth, screenHeight, poseStack);
        }
    }),
    AIR_LEVEL("air_level", (gui, poseStack, partialTick, screenWidth, screenHeight) -> {
        if (!gui.getMinecraft().options.hideGui && gui.shouldDrawSurvivalElements())
        {
            gui.setupOverlayRenderState(true, false);
            gui.renderAir(screenWidth, screenHeight, poseStack);
        }
    }),
    JUMP_BAR("jump_bar", (gui, poseStack, partialTick, screenWidth, screenHeight) -> {
        if (gui.getMinecraft().player.isRidingJumpable() && !gui.getMinecraft().options.hideGui)
        {
            gui.setupOverlayRenderState(true, false);
            gui.renderJumpMeter(poseStack, screenWidth / 2 - 91);
        }
    }),
    EXPERIENCE_BAR("experience_bar", (gui, poseStack, partialTick, screenWidth, screenHeight) -> {
        if (!gui.getMinecraft().player.isRidingJumpable() && !gui.getMinecraft().options.hideGui)
        {
            gui.setupOverlayRenderState(true, false);
            gui.renderExperience(screenWidth / 2 - 91, poseStack);
        }
    }),
    ITEM_NAME("item_name", (gui, poseStack, partialTick, screenWidth, screenHeight) -> {
        if (!gui.getMinecraft().options.hideGui)
        {
            gui.setupOverlayRenderState(true, false);
            if (gui.getMinecraft().options.heldItemTooltips && gui.getMinecraft().gameMode.getPlayerMode() != GameType.SPECTATOR)
            {
                gui.renderSelectedItemName(poseStack);
            }
            else if (gui.getMinecraft().player.isSpectator())
            {
                gui.getSpectatorGui().renderTooltip(poseStack);
            }
        }
    }),
    SLEEP_FADE("sleep_fade", (gui, poseStack, partialTick, screenWidth, screenHeight) -> {
        gui.renderSleepFade(screenWidth, screenHeight, poseStack);
    }),
    DEBUG_TEXT("debug_text", (gui, poseStack, partialTick, screenWidth, screenHeight) -> {
        gui.renderHUDText(screenWidth, screenHeight, poseStack);
    }),
    FPS_GRAPH("fps_graph", (gui, poseStack, partialTick, screenWidth, screenHeight) -> {
        gui.renderFPSGraph(poseStack);
    }),
    POTION_ICONS("potion_icons", (gui, poseStack, partialTick, screenWidth, screenHeight) -> {
        gui.renderEffects(poseStack);
    }),
    RECORD_OVERLAY("record_overlay", (gui, poseStack, partialTick, screenWidth, screenHeight) -> {
        if (!gui.getMinecraft().options.hideGui)
        {
            gui.renderRecordOverlay(screenWidth, screenHeight, partialTick, poseStack);
        }
    }),
    SUBTITLES("subtitles", (gui, poseStack, partialTick, screenWidth, screenHeight) -> {
        if (!gui.getMinecraft().options.hideGui)
        {
            gui.renderSubtitles(poseStack);
        }
    }),
    TITLE_TEXT("title_text", (gui, poseStack, partialTick, screenWidth, screenHeight) -> {
        if (!gui.getMinecraft().options.hideGui)
        {
            gui.renderTitle(screenWidth, screenHeight, partialTick, poseStack);
        }
    }),
    SCOREBOARD("scoreboard", (gui, poseStack, partialTick, screenWidth, screenHeight) -> {

        Scoreboard scoreboard = gui.getMinecraft().level.getScoreboard();
        Objective objective = null;
        PlayerTeam scoreplayerteam = scoreboard.getPlayersTeam(gui.getMinecraft().player.getScoreboardName());
        if (scoreplayerteam != null)
        {
            int slot = scoreplayerteam.getColor().getId();
            if (slot >= 0) objective = scoreboard.getDisplayObjective(3 + slot);
        }
        Objective scoreobjective1 = objective != null ? objective : scoreboard.getDisplayObjective(1);
        if (scoreobjective1 != null)
        {
            gui.displayScoreboardSidebar(poseStack, scoreobjective1);
        }
    }),
    CHAT_PANEL("chat_panel", (gui, poseStack, partialTick, screenWidth, screenHeight) -> {

        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);

        gui.renderChat(screenWidth, screenHeight, poseStack);
    }),
    PLAYER_LIST("player_list", (gui, poseStack, partialTick, screenWidth, screenHeight) -> {

        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);

        gui.renderPlayerList(screenWidth, screenHeight, poseStack);
    });

    private final ResourceLocation id;
    final IGuiOverlay overlay;
    NamedGuiOverlay type;

    VanillaGuiOverlay(String id, IGuiOverlay overlay)
    {
        this.id = new ResourceLocation("minecraft", id);
        this.overlay = overlay;
    }

    @NotNull
    public ResourceLocation id()
    {
        return id;
    }

    public NamedGuiOverlay type()
    {
        return type;
    }
}
