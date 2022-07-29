/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.gui.overlay;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.DebugScreenOverlay;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.StringUtil;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.scores.Objective;
import net.minecraftforge.client.event.CustomizeGuiOverlayEvent;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.MinecraftForge;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Forge wrapper around {@link Gui} to be able to render {@link IGuiOverlay HUD overlays}.
 */
public class ForgeGui extends Gui
{
    private static final Logger LOGGER = LogManager.getLogger();

    private static final int WHITE = 0xFFFFFF;

    /*
     * If the Euclidean distance to the moused-over block in meters is less than this value, the "Looking at" text will appear on the debug overlay.
     */
    public static double rayTraceDistance = 20.0D;

    public int leftHeight = 39;
    public int rightHeight = 39;

    private Font font = null;

    private final ForgeDebugScreenOverlay debugOverlay;

    public ForgeGui(Minecraft mc)
    {
        super(mc, mc.getItemRenderer());
        debugOverlay = new ForgeDebugScreenOverlay(mc);
    }

    public Minecraft getMinecraft()
    {
        return minecraft;
    }

    public void setupOverlayRenderState(boolean blend, boolean depthText)
    {
        setupOverlayRenderState(blend, depthText, Gui.GUI_ICONS_LOCATION);
    }

    public void setupOverlayRenderState(boolean blend, boolean depthTest, @Nullable ResourceLocation texture)
    {
        if (blend)
        {
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
        }
        else
        {
            RenderSystem.disableBlend();
        }

        if (depthTest)
        {
            RenderSystem.enableDepthTest();
        }
        else
        {
            RenderSystem.disableDepthTest();
        }

        if (texture != null)
        {
            RenderSystem.enableTexture();
            bind(texture);
        }
        else
        {
            RenderSystem.disableTexture();
        }

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
    }

    @Override
    public void render(PoseStack poseStack, float partialTick)
    {
        this.screenWidth = this.minecraft.getWindow().getGuiScaledWidth();
        this.screenHeight = this.minecraft.getWindow().getGuiScaledHeight();

        rightHeight = 39;
        leftHeight = 39;

        if (MinecraftForge.EVENT_BUS.post(new RenderGuiEvent.Pre(minecraft.getWindow(), poseStack, partialTick)))
        {
            return;
        }

        font = minecraft.font;

        this.random.setSeed(tickCount * 312871L);

        GuiOverlayManager.getOverlays().forEach(entry -> {
            try
            {
                IGuiOverlay overlay = entry.overlay();
                if (pre(entry, poseStack)) return;
                overlay.render(this, poseStack, partialTick, screenWidth, screenHeight);
                post(entry, poseStack);
            } catch (Exception e)
            {
                LOGGER.error("Error rendering overlay '{}'", entry.id(), e);
            }
        });

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        MinecraftForge.EVENT_BUS.post(new RenderGuiEvent.Post(minecraft.getWindow(), poseStack, partialTick));
    }

    public boolean shouldDrawSurvivalElements()
    {
        return minecraft.gameMode.canHurtPlayer() && minecraft.getCameraEntity() instanceof Player;
    }

    protected void renderSubtitles(PoseStack poseStack)
    {
        this.subtitleOverlay.render(poseStack);
    }

    protected void renderBossHealth(PoseStack poseStack)
    {
        bind(GuiComponent.GUI_ICONS_LOCATION);
        RenderSystem.defaultBlendFunc();
        minecraft.getProfiler().push("bossHealth");
        this.bossOverlay.render(poseStack);
        minecraft.getProfiler().pop();
    }

    void renderSpyglassOverlay()
    {
        float deltaFrame = this.minecraft.getDeltaFrameTime();
        this.scopeScale = Mth.lerp(0.5F * deltaFrame, this.scopeScale, 1.125F);
        if (this.minecraft.options.getCameraType().isFirstPerson())
        {
            if (this.minecraft.player.isScoping())
            {
                this.renderSpyglassOverlay(this.scopeScale);
            }
            else
            {
                this.scopeScale = 0.5F;
            }
        }
    }

    void renderHelmet(float partialTick, PoseStack poseStack)
    {
        ItemStack itemstack = this.minecraft.player.getInventory().getArmor(3);

        if (this.minecraft.options.getCameraType().isFirstPerson() && !itemstack.isEmpty())
        {
            Item item = itemstack.getItem();
            if (item == Blocks.CARVED_PUMPKIN.asItem())
            {
                renderTextureOverlay(PUMPKIN_BLUR_LOCATION, 1.0F);
            }
            else
            {
                IClientItemExtensions.of(item).renderHelmetOverlay(itemstack, minecraft.player, this.screenWidth, this.screenHeight, partialTick);
            }
        }
    }

    void renderFrostbite(PoseStack pStack)
    {
        if (this.minecraft.player.getTicksFrozen() > 0)
        {
            this.renderTextureOverlay(POWDER_SNOW_OUTLINE_LOCATION, this.minecraft.player.getPercentFrozen());
        }
    }

    protected void renderArmor(PoseStack poseStack, int width, int height)
    {
        minecraft.getProfiler().push("armor");

        RenderSystem.enableBlend();
        int left = width / 2 - 91;
        int top = height - leftHeight;

        int level = minecraft.player.getArmorValue();
        for (int i = 1; level > 0 && i < 20; i += 2)
        {
            if (i < level)
            {
                blit(poseStack, left, top, 34, 9, 9, 9);
            }
            else if (i == level)
            {
                blit(poseStack, left, top, 25, 9, 9, 9);
            }
            else if (i > level)
            {
                blit(poseStack, left, top, 16, 9, 9, 9);
            }
            left += 8;
        }
        leftHeight += 10;

        RenderSystem.disableBlend();
        minecraft.getProfiler().pop();
    }

    @Override
    protected void renderPortalOverlay(float partialTick)
    {
        float f1 = Mth.lerp(partialTick, this.minecraft.player.oPortalTime, this.minecraft.player.portalTime);

        if (f1 > 0.0F)
        {
            super.renderPortalOverlay(f1);
        }
    }

    protected void renderAir(int width, int height, PoseStack poseStack)
    {
        minecraft.getProfiler().push("air");
        Player player = (Player) this.minecraft.getCameraEntity();
        RenderSystem.enableBlend();
        int left = width / 2 + 91;
        int top = height - rightHeight;

        int air = player.getAirSupply();
        if (player.isEyeInFluidType(ForgeMod.WATER_TYPE.get()) || air < 300)
        {
            int full = Mth.ceil((double) (air - 2) * 10.0D / 300.0D);
            int partial = Mth.ceil((double) air * 10.0D / 300.0D) - full;

            for (int i = 0; i < full + partial; ++i)
            {
                blit(poseStack, left - i * 8 - 9, top, (i < full ? 16 : 25), 18, 9, 9);
            }
            rightHeight += 10;
        }

        RenderSystem.disableBlend();
        minecraft.getProfiler().pop();
    }

    public void renderHealth(int width, int height, PoseStack pStack)
    {
        bind(GUI_ICONS_LOCATION);
        minecraft.getProfiler().push("health");
        RenderSystem.enableBlend();

        Player player = (Player) this.minecraft.getCameraEntity();
        int health = Mth.ceil(player.getHealth());
        boolean highlight = healthBlinkTime > (long) tickCount && (healthBlinkTime - (long) tickCount) / 3L % 2L == 1L;

        if (health < this.lastHealth && player.invulnerableTime > 0)
        {
            this.lastHealthTime = Util.getMillis();
            this.healthBlinkTime = (long) (this.tickCount + 20);
        }
        else if (health > this.lastHealth && player.invulnerableTime > 0)
        {
            this.lastHealthTime = Util.getMillis();
            this.healthBlinkTime = (long) (this.tickCount + 10);
        }

        if (Util.getMillis() - this.lastHealthTime > 1000L)
        {
            this.lastHealth = health;
            this.displayHealth = health;
            this.lastHealthTime = Util.getMillis();
        }

        this.lastHealth = health;
        int healthLast = this.displayHealth;

        AttributeInstance attrMaxHealth = player.getAttribute(Attributes.MAX_HEALTH);
        float healthMax = Math.max((float) attrMaxHealth.getValue(), Math.max(healthLast, health));
        int absorb = Mth.ceil(player.getAbsorptionAmount());

        int healthRows = Mth.ceil((healthMax + absorb) / 2.0F / 10.0F);
        int rowHeight = Math.max(10 - (healthRows - 2), 3);

        this.random.setSeed((long) (tickCount * 312871));

        int left = width / 2 - 91;
        int top = height - leftHeight;
        leftHeight += (healthRows * rowHeight);
        if (rowHeight != 10) leftHeight += 10 - rowHeight;

        int regen = -1;
        if (player.hasEffect(MobEffects.REGENERATION))
        {
            regen = this.tickCount % Mth.ceil(healthMax + 5.0F);
        }

        this.renderHearts(pStack, player, left, top, rowHeight, regen, healthMax, health, healthLast, absorb, highlight);

        RenderSystem.disableBlend();
        minecraft.getProfiler().pop();
    }

    public void renderFood(int width, int height, PoseStack poseStack)
    {
        minecraft.getProfiler().push("food");

        Player player = (Player) this.minecraft.getCameraEntity();
        RenderSystem.enableBlend();
        int left = width / 2 + 91;
        int top = height - rightHeight;
        rightHeight += 10;
        boolean unused = false;// Unused flag in vanilla, seems to be part of a 'fade out' mechanic

        FoodData stats = minecraft.player.getFoodData();
        int level = stats.getFoodLevel();

        for (int i = 0; i < 10; ++i)
        {
            int idx = i * 2 + 1;
            int x = left - i * 8 - 9;
            int y = top;
            int icon = 16;
            byte background = 0;

            if (minecraft.player.hasEffect(MobEffects.HUNGER))
            {
                icon += 36;
                background = 13;
            }
            if (unused) background = 1; //Probably should be a += 1 but vanilla never uses this

            if (player.getFoodData().getSaturationLevel() <= 0.0F && tickCount % (level * 3 + 1) == 0)
            {
                y = top + (random.nextInt(3) - 1);
            }

            blit(poseStack, x, y, 16 + background * 9, 27, 9, 9);

            if (idx < level)
                blit(poseStack, x, y, icon + 36, 27, 9, 9);
            else if (idx == level)
                blit(poseStack, x, y, icon + 45, 27, 9, 9);
        }
        RenderSystem.disableBlend();
        minecraft.getProfiler().pop();
    }

    protected void renderSleepFade(int width, int height, PoseStack poseStack)
    {
        if (minecraft.player.getSleepTimer() > 0)
        {
            minecraft.getProfiler().push("sleep");
            RenderSystem.disableDepthTest();
            // RenderSystem.disableAlphaTest();
            int sleepTime = minecraft.player.getSleepTimer();
            float opacity = (float) sleepTime / 100.0F;

            if (opacity > 1.0F)
            {
                opacity = 1.0F - (float) (sleepTime - 100) / 10.0F;
            }

            int color = (int) (220.0F * opacity) << 24 | 1052704;
            fill(poseStack, 0, 0, width, height, color);
            // RenderSystem.enableAlphaTest();
            RenderSystem.enableDepthTest();
            minecraft.getProfiler().pop();
        }
    }

    protected void renderExperience(int x, PoseStack poseStack)
    {
        bind(GUI_ICONS_LOCATION);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.disableBlend();

        if (minecraft.gameMode.hasExperience())
        {
            super.renderExperienceBar(poseStack, x);
        }
        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    @Override
    public void renderJumpMeter(PoseStack poseStack, int x)
    {
        bind(GUI_ICONS_LOCATION);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.disableBlend();

        super.renderJumpMeter(poseStack, x);

        RenderSystem.enableBlend();
        minecraft.getProfiler().pop();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    protected void renderHUDText(int width, int height, PoseStack poseStack)
    {
        minecraft.getProfiler().push("forgeHudText");
        RenderSystem.defaultBlendFunc();

        var listL = new ArrayList<String>();
        var listR = new ArrayList<String>();

        if (minecraft.isDemo())
        {
            long time = minecraft.level.getGameTime();
            if (time >= 120500L)
            {
                listR.add(I18n.get("demo.demoExpired"));
            }
            else
            {
                listR.add(I18n.get("demo.remainingTime", StringUtil.formatTickDuration((int) (120500L - time))));
            }
        }

        if (this.minecraft.options.renderDebug)
        {
            debugOverlay.update();
            listL.addAll(debugOverlay.getLeft());
            listR.addAll(debugOverlay.getRight());
        }

        var event = new CustomizeGuiOverlayEvent.DebugText(minecraft.getWindow(), poseStack, minecraft.getFrameTime(), listL, listR);
        MinecraftForge.EVENT_BUS.post(event);

        int top = 2;
        for (String msg : listL)
        {
            if (msg != null && !msg.isEmpty())
            {
                fill(poseStack, 1, top - 1, 2 + font.width(msg) + 1, top + font.lineHeight - 1, -1873784752);
                font.draw(poseStack, msg, 2, top, 14737632);
            }
            top += font.lineHeight;
        }

        top = 2;
        for (String msg : listR)
        {
            if (msg != null && !msg.isEmpty())
            {
                int w = font.width(msg);
                int left = width - 2 - w;
                fill(poseStack, left - 1, top - 1, left + w + 1, top + font.lineHeight - 1, -1873784752);
                font.draw(poseStack, msg, left, top, 14737632);
            }
            top += font.lineHeight;
        }

        minecraft.getProfiler().pop();
    }

    protected void renderFPSGraph(PoseStack poseStack)
    {
        if (this.minecraft.options.renderDebug && this.minecraft.options.renderFpsChart)
        {
            this.debugOverlay.render(poseStack);
        }
    }

    protected void renderRecordOverlay(int width, int height, float partialTick, PoseStack pStack)
    {
        if (overlayMessageTime > 0)
        {
            minecraft.getProfiler().push("overlayMessage");
            float hue = (float) overlayMessageTime - partialTick;
            int opacity = (int) (hue * 255.0F / 20.0F);
            if (opacity > 255) opacity = 255;

            if (opacity > 8)
            {
                pStack.pushPose();
                pStack.translate(width / 2D, height - 68, 0.0D);
                RenderSystem.enableBlend();
                RenderSystem.defaultBlendFunc();
                int color = (animateOverlayMessageColor ? Mth.hsvToRgb(hue / 50.0F, 0.7F, 0.6F) & WHITE : WHITE);
                drawBackdrop(pStack, font, -4, font.width(overlayMessageString), 16777215 | (opacity << 24));
                font.drawShadow(pStack, overlayMessageString.getVisualOrderText(), -font.width(overlayMessageString) / 2, -4, color | (opacity << 24));
                RenderSystem.disableBlend();
                pStack.popPose();
            }

            minecraft.getProfiler().pop();
        }
    }

    protected void renderTitle(int width, int height, float partialTick, PoseStack pStack)
    {
        if (title != null && titleTime > 0)
        {
            minecraft.getProfiler().push("titleAndSubtitle");
            float age = (float) this.titleTime - partialTick;
            int opacity = 255;

            if (titleTime > titleFadeOutTime + titleStayTime)
            {
                float f3 = (float) (titleFadeInTime + titleStayTime + titleFadeOutTime) - age;
                opacity = (int) (f3 * 255.0F / (float) titleFadeInTime);
            }
            if (titleTime <= titleFadeOutTime) opacity = (int) (age * 255.0F / (float) this.titleFadeOutTime);

            opacity = Mth.clamp(opacity, 0, 255);

            if (opacity > 8)
            {
                pStack.pushPose();
                pStack.translate(width / 2D, height / 2D, 0.0D);
                RenderSystem.enableBlend();
                RenderSystem.defaultBlendFunc();
                pStack.pushPose();
                pStack.scale(4.0F, 4.0F, 4.0F);
                int l = opacity << 24 & -16777216;
                this.getFont().drawShadow(pStack, this.title.getVisualOrderText(), (float) (-this.getFont().width(this.title) / 2), -10.0F, 16777215 | l);
                pStack.popPose();
                if (this.subtitle != null)
                {
                    pStack.pushPose();
                    pStack.scale(2.0F, 2.0F, 2.0F);
                    this.getFont().drawShadow(pStack, this.subtitle.getVisualOrderText(), (float) (-this.getFont().width(this.subtitle) / 2), 5.0F, 16777215 | l);
                    pStack.popPose();
                }
                RenderSystem.disableBlend();
                pStack.popPose();
            }

            this.minecraft.getProfiler().pop();
        }
    }

    protected void renderChat(int width, int height, PoseStack pStack)
    {
        minecraft.getProfiler().push("chat");

        var event = new CustomizeGuiOverlayEvent.Chat(minecraft.getWindow(), pStack, minecraft.getFrameTime(), 0, height - 48);
        MinecraftForge.EVENT_BUS.post(event);

        pStack.pushPose();
        pStack.translate(event.getPosX(), event.getPosY(), 0.0D);
        chat.render(pStack, tickCount);
        pStack.popPose();

        minecraft.getProfiler().pop();
    }

    protected void renderPlayerList(int width, int height, PoseStack poseStack)
    {
        Objective scoreobjective = this.minecraft.level.getScoreboard().getDisplayObjective(0);
        ClientPacketListener handler = minecraft.player.connection;

        if (minecraft.options.keyPlayerList.isDown() && (!minecraft.isLocalServer() || handler.getOnlinePlayers().size() > 1 || scoreobjective != null))
        {
            this.tabList.setVisible(true);
            this.tabList.render(poseStack, width, this.minecraft.level.getScoreboard(), scoreobjective);

        }
        else
        {
            this.tabList.setVisible(false);
        }
    }

    protected void renderHealthMount(int width, int height, PoseStack poseStack)
    {
        Player player = (Player) minecraft.getCameraEntity();
        Entity tmp = player.getVehicle();
        if (!(tmp instanceof LivingEntity)) return;

        bind(GUI_ICONS_LOCATION);

        boolean unused = false;
        int left_align = width / 2 + 91;

        minecraft.getProfiler().popPush("mountHealth");
        RenderSystem.enableBlend();
        LivingEntity mount = (LivingEntity) tmp;
        int health = (int) Math.ceil((double) mount.getHealth());
        float healthMax = mount.getMaxHealth();
        int hearts = (int) (healthMax + 0.5F) / 2;

        if (hearts > 30) hearts = 30;

        final int MARGIN = 52;
        final int BACKGROUND = MARGIN + (unused ? 1 : 0);
        final int HALF = MARGIN + 45;
        final int FULL = MARGIN + 36;

        for (int heart = 0; hearts > 0; heart += 20)
        {
            int top = height - rightHeight;

            int rowCount = Math.min(hearts, 10);
            hearts -= rowCount;

            for (int i = 0; i < rowCount; ++i)
            {
                int x = left_align - i * 8 - 9;
                blit(poseStack, x, top, BACKGROUND, 9, 9, 9);

                if (i * 2 + 1 + heart < health)
                    blit(poseStack, x, top, FULL, 9, 9, 9);
                else if (i * 2 + 1 + heart == health)
                    blit(poseStack, x, top, HALF, 9, 9, 9);
            }

            rightHeight += 10;
        }
        RenderSystem.disableBlend();
    }

    //Helper macros
    private boolean pre(NamedGuiOverlay overlay, PoseStack poseStack)
    {
        return MinecraftForge.EVENT_BUS.post(new RenderGuiOverlayEvent.Pre(minecraft.getWindow(), poseStack, minecraft.getFrameTime(), overlay));
    }

    private void post(NamedGuiOverlay overlay, PoseStack poseStack)
    {
        MinecraftForge.EVENT_BUS.post(new RenderGuiOverlayEvent.Post(minecraft.getWindow(), poseStack, minecraft.getFrameTime(), overlay));
    }

    private void bind(ResourceLocation res)
    {
        RenderSystem.setShaderTexture(0, res);
    }

    private static class ForgeDebugScreenOverlay extends DebugScreenOverlay
    {
        private final Minecraft mc;

        private ForgeDebugScreenOverlay(Minecraft mc)
        {
            super(mc);
            this.mc = mc;
        }

        public void update()
        {
            Entity entity = this.mc.getCameraEntity();
            this.block = entity.pick(rayTraceDistance, 0.0F, false);
            this.liquid = entity.pick(rayTraceDistance, 0.0F, true);
        }

        @Override
        protected void drawGameInformation(PoseStack poseStack) {}

        @Override
        protected void drawSystemInformation(PoseStack poseStack) {}

        private List<String> getLeft()
        {
            List<String> ret = this.getGameInformation();
            ret.add("");
            boolean flag = this.mc.getSingleplayerServer() != null;
            ret.add("Debug: Pie [shift]: " + (this.mc.options.renderDebugCharts ? "visible" : "hidden") + (flag ? " FPS + TPS" : " FPS") + " [alt]: " + (this.mc.options.renderFpsChart ? "visible" : "hidden"));
            ret.add("For help: press F3 + Q");
            return ret;
        }

        private List<String> getRight()
        {
            return this.getSystemInformation();
        }
    }
}
