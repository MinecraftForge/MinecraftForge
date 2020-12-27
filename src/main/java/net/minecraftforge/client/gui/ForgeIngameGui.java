/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.client.gui;

import static net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType.*;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.IngameGui;
import net.minecraft.client.gui.overlay.DebugOverlayGui;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.block.Blocks;
import net.minecraft.potion.Effects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.FoodStats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.GameType;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.common.MinecraftForge;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.systems.RenderSystem;

@SuppressWarnings("deprecation")
public class ForgeIngameGui extends IngameGui
{
    //private static final ResourceLocation VIGNETTE     = new ResourceLocation("textures/misc/vignette.png");
    //private static final ResourceLocation WIDGITS      = new ResourceLocation("textures/gui/widgets.png");
    //private static final ResourceLocation PUMPKIN_BLUR = new ResourceLocation("textures/misc/pumpkinblur.png");

    private static final int WHITE = 0xFFFFFF;

    //Flags to toggle the rendering of certain aspects of the HUD, valid conditions
    //must be met for them to render normally. If those conditions are met, but this flag
    //is false, they will not be rendered.
    public static boolean renderVignette = true;
    public static boolean renderHelmet = true;
    public static boolean renderPortal = true;
    public static boolean renderSpectatorTooltip = true;
    public static boolean renderHotbar = true;
    public static boolean renderCrosshairs = true;
    public static boolean renderBossHealth = true;
    public static boolean renderHealth = true;
    public static boolean renderArmor = true;
    public static boolean renderFood = true;
    public static boolean renderHealthMount = true;
    public static boolean renderAir = true;
    public static boolean renderExperiance = true;
    public static boolean renderJumpBar = true;
    public static boolean renderObjective = true;

    public static int left_height = 39;
    public static int right_height = 39;
    /*
     * If the Euclidian distance to the moused-over block in meters is less than this value, the "Looking at" text will appear on the debug overlay.
     */
    public static double rayTraceDistance = 20.0D;

    private FontRenderer fontrenderer = null;
    private RenderGameOverlayEvent eventParent;
    //private static final String MC_VERSION = MinecraftForge.MC_VERSION;
    private GuiOverlayDebugForge debugOverlay;

    public ForgeIngameGui(Minecraft mc)
    {
        super(mc);
        debugOverlay = new GuiOverlayDebugForge(mc);
    }

    @Override
    public void renderIngameGui(MatrixStack mStack, float partialTicks)
    {
        this.scaledWidth = this.mc.getMainWindow().getScaledWidth();
        this.scaledHeight = this.mc.getMainWindow().getScaledHeight();
        eventParent = new RenderGameOverlayEvent(mStack, partialTicks, this.mc.getMainWindow());
        renderHealthMount = mc.player.getRidingEntity() instanceof LivingEntity;
        renderFood = !renderHealthMount;
        renderJumpBar = mc.player.isRidingHorse();

        right_height = 39;
        left_height = 39;

        if (pre(ALL, mStack)) return;

        fontrenderer = mc.fontRenderer;
        //mc.entityRenderer.setupOverlayRendering();
        RenderSystem.enableBlend();
        if (renderVignette && Minecraft.isFancyGraphicsEnabled())
        {
            renderVignette(mc.getRenderViewEntity());
        }
        else
        {
            RenderSystem.enableDepthTest();
            RenderSystem.defaultBlendFunc();
        }

        if (renderHelmet) renderHelmet(partialTicks, mStack);

        if (renderPortal && !mc.player.isPotionActive(Effects.NAUSEA))
        {
            renderPortal(partialTicks);
        }

        if (this.mc.playerController.getCurrentGameType() == GameType.SPECTATOR)
        {
            if (renderSpectatorTooltip) spectatorGui.func_238528_a_(mStack, partialTicks);
        }
        else if (!this.mc.gameSettings.hideGUI)
        {
            if (renderHotbar) renderHotbar(partialTicks, mStack);
        }

        if (!this.mc.gameSettings.hideGUI) {
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            setBlitOffset(-90);
            rand.setSeed((long)(ticks * 312871));

            if (renderCrosshairs) func_238456_d_(mStack);
            if (renderBossHealth) renderBossHealth(mStack);

            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            if (this.mc.playerController.shouldDrawHUD() && this.mc.getRenderViewEntity() instanceof PlayerEntity)
            {
                if (renderHealth) renderHealth(this.scaledWidth, this.scaledHeight, mStack);
                if (renderArmor)  renderArmor(mStack, this.scaledWidth, this.scaledHeight);
                if (renderFood)   renderFood(this.scaledWidth, this.scaledHeight, mStack);
                if (renderHealthMount) renderHealthMount(this.scaledWidth, this.scaledHeight, mStack);
                if (renderAir)    renderAir(this.scaledWidth, this.scaledHeight, mStack);
            }

            if (renderJumpBar)
            {
                renderHorseJumpBar(mStack, this.scaledWidth / 2 - 91);
            }
            else if (renderExperiance)
            {
                renderExperience(this.scaledWidth / 2 - 91, mStack);
            }
            if (this.mc.gameSettings.heldItemTooltips && this.mc.playerController.getCurrentGameType() != GameType.SPECTATOR) {
                this.func_238453_b_(mStack);
             } else if (this.mc.player.isSpectator()) {
                this.spectatorGui.func_238527_a_(mStack);
             }
        }

        renderSleepFade(this.scaledWidth, this.scaledHeight, mStack);

        renderHUDText(this.scaledWidth, this.scaledHeight, mStack);
        renderFPSGraph(mStack);
        renderPotionIcons(mStack);
        if (!mc.gameSettings.hideGUI) {
            renderRecordOverlay(this.scaledWidth, this.scaledHeight, partialTicks, mStack);
            renderSubtitles(mStack);
            renderTitle(this.scaledWidth, this.scaledHeight, partialTicks, mStack);
        }


        Scoreboard scoreboard = this.mc.world.getScoreboard();
        ScoreObjective objective = null;
        ScorePlayerTeam scoreplayerteam = scoreboard.getPlayersTeam(mc.player.getScoreboardName());
        if (scoreplayerteam != null)
        {
            int slot = scoreplayerteam.getColor().getColorIndex();
            if (slot >= 0) objective = scoreboard.getObjectiveInDisplaySlot(3 + slot);
        }
        ScoreObjective scoreobjective1 = objective != null ? objective : scoreboard.getObjectiveInDisplaySlot(1);
        if (renderObjective && scoreobjective1 != null)
        {
            this.func_238447_a_(mStack, scoreobjective1);
        }

        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
        RenderSystem.disableAlphaTest();

        renderChat(this.scaledWidth, this.scaledHeight, mStack);

        renderPlayerList(this.scaledWidth, this.scaledHeight, mStack);

        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.enableAlphaTest();

        post(ALL, mStack);
    }

    @Override
    protected void func_238456_d_(MatrixStack mStack)
    {
        if (pre(CROSSHAIRS, mStack)) return;
        bind(AbstractGui.GUI_ICONS_LOCATION);
        RenderSystem.enableBlend();
        RenderSystem.enableAlphaTest();
        super.func_238456_d_(mStack);
        post(CROSSHAIRS, mStack);
    }

    @Override
    protected void renderPotionIcons(MatrixStack mStack)
    {
        if (pre(POTION_ICONS, mStack)) return;
        super.renderPotionIcons(mStack);
        post(POTION_ICONS, mStack);
    }

    protected void renderSubtitles(MatrixStack mStack)
    {
        if (pre(SUBTITLES, mStack)) return;
        this.overlaySubtitle.render(mStack);
        post(SUBTITLES, mStack);
    }

    protected void renderBossHealth(MatrixStack mStack)
    {
        if (pre(BOSSHEALTH, mStack)) return;
        bind(AbstractGui.GUI_ICONS_LOCATION);
        RenderSystem.defaultBlendFunc();
        mc.getProfiler().startSection("bossHealth");
        RenderSystem.enableBlend();
        this.overlayBoss.func_238484_a_(mStack);
        RenderSystem.disableBlend();
        mc.getProfiler().endSection();
        post(BOSSHEALTH, mStack);
    }

    @Override
    protected void renderVignette(Entity entity)
    {
        MatrixStack mStack = new MatrixStack();
        if (pre(VIGNETTE, mStack))
        {
            // Need to put this here, since Vanilla assumes this state after the vignette was rendered.
            RenderSystem.enableDepthTest();
            RenderSystem.defaultBlendFunc();
            return;
        }
        super.renderVignette(entity);
        post(VIGNETTE, mStack);
    }


    private void renderHelmet(float partialTicks, MatrixStack mStack)
    {
        if (pre(HELMET, mStack)) return;

        ItemStack itemstack = this.mc.player.inventory.armorItemInSlot(3);

        if (this.mc.gameSettings.getPointOfView().func_243192_a() && !itemstack.isEmpty())
        {
            Item item = itemstack.getItem();
            if (item == Blocks.CARVED_PUMPKIN.asItem())
            {
                renderPumpkinOverlay();
            }
            else
            {
                item.renderHelmetOverlay(itemstack, mc.player, this.scaledWidth, this.scaledHeight, partialTicks);
            }
        }

        post(HELMET, mStack);
    }

    protected void renderArmor(MatrixStack mStack, int width, int height)
    {
        if (pre(ARMOR, mStack)) return;
        mc.getProfiler().startSection("armor");

        RenderSystem.enableBlend();
        int left = width / 2 - 91;
        int top = height - left_height;

        int level = mc.player.getTotalArmorValue();
        for (int i = 1; level > 0 && i < 20; i += 2)
        {
            if (i < level)
            {
                blit(mStack, left, top, 34, 9, 9, 9);
            }
            else if (i == level)
            {
                blit(mStack, left, top, 25, 9, 9, 9);
            }
            else if (i > level)
            {
                blit(mStack, left, top, 16, 9, 9, 9);
            }
            left += 8;
        }
        left_height += 10;

        RenderSystem.disableBlend();
        mc.getProfiler().endSection();
        post(ARMOR, mStack);
    }

    @Override
    protected void renderPortal(float partialTicks)
    {
        MatrixStack mStack = new MatrixStack();
        if (pre(PORTAL, mStack)) return;

        float f1 = mc.player.prevTimeInPortal + (mc.player.timeInPortal - mc.player.prevTimeInPortal) * partialTicks;

        if (f1 > 0.0F)
        {
            super.renderPortal(f1);
        }

        post(PORTAL, mStack);
    }

    @Override
    protected void renderHotbar(float partialTicks, MatrixStack mStack)
    {
        if (pre(HOTBAR, mStack)) return;

        if (mc.playerController.getCurrentGameType() == GameType.SPECTATOR)
        {
            this.spectatorGui.func_238528_a_(mStack, partialTicks);
        }
        else
        {
            super.renderHotbar(partialTicks, mStack);
        }

        post(HOTBAR, mStack);
    }

    protected void renderAir(int width, int height, MatrixStack mStack)
    {
        if (pre(AIR, mStack)) return;
        mc.getProfiler().startSection("air");
        PlayerEntity player = (PlayerEntity)this.mc.getRenderViewEntity();
        RenderSystem.enableBlend();
        int left = width / 2 + 91;
        int top = height - right_height;

        int air = player.getAir();
        if (player.areEyesInFluid(FluidTags.WATER) || air < 300)
        {
            int full = MathHelper.ceil((double)(air - 2) * 10.0D / 300.0D);
            int partial = MathHelper.ceil((double)air * 10.0D / 300.0D) - full;

            for (int i = 0; i < full + partial; ++i)
            {
                blit(mStack, left - i * 8 - 9, top, (i < full ? 16 : 25), 18, 9, 9);
            }
            right_height += 10;
        }

        RenderSystem.disableBlend();
        mc.getProfiler().endSection();
        post(AIR, mStack);
    }

    public void renderHealth(int width, int height, MatrixStack mStack)
    {
        bind(GUI_ICONS_LOCATION);
        if (pre(HEALTH, mStack)) return;
        mc.getProfiler().startSection("health");
        RenderSystem.enableBlend();

        PlayerEntity player = (PlayerEntity)this.mc.getRenderViewEntity();
        int health = MathHelper.ceil(player.getHealth());
        boolean highlight = healthUpdateCounter > (long)ticks && (healthUpdateCounter - (long)ticks) / 3L %2L == 1L;

        if (health < this.playerHealth && player.hurtResistantTime > 0)
        {
            this.lastSystemTime = Util.milliTime();
            this.healthUpdateCounter = (long)(this.ticks + 20);
        }
        else if (health > this.playerHealth && player.hurtResistantTime > 0)
        {
            this.lastSystemTime = Util.milliTime();
            this.healthUpdateCounter = (long)(this.ticks + 10);
        }

        if (Util.milliTime() - this.lastSystemTime > 1000L)
        {
            this.playerHealth = health;
            this.lastPlayerHealth = health;
            this.lastSystemTime = Util.milliTime();
        }

        this.playerHealth = health;
        int healthLast = this.lastPlayerHealth;

        ModifiableAttributeInstance attrMaxHealth = player.getAttribute(Attributes.MAX_HEALTH);
        float healthMax = (float)attrMaxHealth.getValue();
        float absorb = MathHelper.ceil(player.getAbsorptionAmount());

        int healthRows = MathHelper.ceil((healthMax + absorb) / 2.0F / 10.0F);
        int rowHeight = Math.max(10 - (healthRows - 2), 3);

        this.rand.setSeed((long)(ticks * 312871));

        int left = width / 2 - 91;
        int top = height - left_height;
        left_height += (healthRows * rowHeight);
        if (rowHeight != 10) left_height += 10 - rowHeight;

        int regen = -1;
        if (player.isPotionActive(Effects.REGENERATION))
        {
            regen = ticks % 25;
        }

        final int TOP =  9 * (mc.world.getWorldInfo().isHardcore() ? 5 : 0);
        final int BACKGROUND = (highlight ? 25 : 16);
        int MARGIN = 16;
        if (player.isPotionActive(Effects.POISON))      MARGIN += 36;
        else if (player.isPotionActive(Effects.WITHER)) MARGIN += 72;
        float absorbRemaining = absorb;

        for (int i = MathHelper.ceil((healthMax + absorb) / 2.0F) - 1; i >= 0; --i)
        {
            //int b0 = (highlight ? 1 : 0);
            int row = MathHelper.ceil((float)(i + 1) / 10.0F) - 1;
            int x = left + i % 10 * 8;
            int y = top - row * rowHeight;

            if (health <= 4) y += rand.nextInt(2);
            if (i == regen) y -= 2;

            blit(mStack, x, y, BACKGROUND, TOP, 9, 9);

            if (highlight)
            {
                if (i * 2 + 1 < healthLast)
                    blit(mStack, x, y, MARGIN + 54, TOP, 9, 9); //6
                else if (i * 2 + 1 == healthLast)
                    blit(mStack, x, y, MARGIN + 63, TOP, 9, 9); //7
            }

            if (absorbRemaining > 0.0F)
            {
                if (absorbRemaining == absorb && absorb % 2.0F == 1.0F)
                {
                    blit(mStack, x, y, MARGIN + 153, TOP, 9, 9); //17
                    absorbRemaining -= 1.0F;
                }
                else
                {
                    blit(mStack, x, y, MARGIN + 144, TOP, 9, 9); //16
                    absorbRemaining -= 2.0F;
                }
            }
            else
            {
                if (i * 2 + 1 < health)
                    blit(mStack, x, y, MARGIN + 36, TOP, 9, 9); //4
                else if (i * 2 + 1 == health)
                    blit(mStack, x, y, MARGIN + 45, TOP, 9, 9); //5
            }
        }

        RenderSystem.disableBlend();
        mc.getProfiler().endSection();
        post(HEALTH, mStack);
    }

    public void renderFood(int width, int height, MatrixStack mStack)
    {
        if (pre(FOOD, mStack)) return;
        mc.getProfiler().startSection("food");

        PlayerEntity player = (PlayerEntity)this.mc.getRenderViewEntity();
        RenderSystem.enableBlend();
        int left = width / 2 + 91;
        int top = height - right_height;
        right_height += 10;
        boolean unused = false;// Unused flag in vanilla, seems to be part of a 'fade out' mechanic

        FoodStats stats = mc.player.getFoodStats();
        int level = stats.getFoodLevel();

        for (int i = 0; i < 10; ++i)
        {
            int idx = i * 2 + 1;
            int x = left - i * 8 - 9;
            int y = top;
            int icon = 16;
            byte background = 0;

            if (mc.player.isPotionActive(Effects.HUNGER))
            {
                icon += 36;
                background = 13;
            }
            if (unused) background = 1; //Probably should be a += 1 but vanilla never uses this

            if (player.getFoodStats().getSaturationLevel() <= 0.0F && ticks % (level * 3 + 1) == 0)
            {
                y = top + (rand.nextInt(3) - 1);
            }

            blit(mStack, x, y, 16 + background * 9, 27, 9, 9);

            if (idx < level)
                blit(mStack, x, y, icon + 36, 27, 9, 9);
            else if (idx == level)
                blit(mStack, x, y, icon + 45, 27, 9, 9);
        }
        RenderSystem.disableBlend();
        mc.getProfiler().endSection();
        post(FOOD, mStack);
    }

    protected void renderSleepFade(int width, int height, MatrixStack mStack)
    {
        if (mc.player.getSleepTimer() > 0)
        {
            mc.getProfiler().startSection("sleep");
            RenderSystem.disableDepthTest();
            RenderSystem.disableAlphaTest();
            int sleepTime = mc.player.getSleepTimer();
            float opacity = (float)sleepTime / 100.0F;

            if (opacity > 1.0F)
            {
                opacity = 1.0F - (float)(sleepTime - 100) / 10.0F;
            }

            int color = (int)(220.0F * opacity) << 24 | 1052704;
            fill(mStack, 0, 0, width, height, color);
            RenderSystem.enableAlphaTest();
            RenderSystem.enableDepthTest();
            mc.getProfiler().endSection();
        }
    }

    protected void renderExperience(int x, MatrixStack mStack)
    {
        bind(GUI_ICONS_LOCATION);
        if (pre(EXPERIENCE, mStack)) return;
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.disableBlend();

        if (mc.playerController.gameIsSurvivalOrAdventure())
        {
            super.func_238454_b_(mStack, x);
        }
        RenderSystem.enableBlend();
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);

        post(EXPERIENCE, mStack);
    }

    @Override
    public void renderHorseJumpBar(MatrixStack mStack, int x)
    {
        bind(GUI_ICONS_LOCATION);
        if (pre(JUMPBAR, mStack)) return;
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.disableBlend();

        super.renderHorseJumpBar(mStack, x);

        RenderSystem.enableBlend();
        mc.getProfiler().endSection();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        post(JUMPBAR, mStack);
    }

    protected void renderHUDText(int width, int height, MatrixStack mStack)
    {
        mc.getProfiler().startSection("forgeHudText");
        RenderSystem.defaultBlendFunc();
        ArrayList<String> listL = new ArrayList<String>();
        ArrayList<String> listR = new ArrayList<String>();

        if (mc.isDemo())
        {
            long time = mc.world.getGameTime();
            if (time >= 120500L)
            {
                listR.add(I18n.format("demo.demoExpired"));
            }
            else
            {
                listR.add(I18n.format("demo.remainingTime", StringUtils.ticksToElapsedTime((int)(120500L - time))));
            }
        }

        if (this.mc.gameSettings.showDebugInfo && !pre(DEBUG, mStack))
        {
            debugOverlay.update();
            listL.addAll(debugOverlay.getLeft());
            listR.addAll(debugOverlay.getRight());
            post(DEBUG, mStack);
        }

        RenderGameOverlayEvent.Text event = new RenderGameOverlayEvent.Text(mStack, eventParent, listL, listR);
        if (!MinecraftForge.EVENT_BUS.post(event))
        {
            int top = 2;
            for (String msg : listL)
            {
                if (msg == null) continue;
                fill(mStack, 1, top - 1, 2 + fontrenderer.getStringWidth(msg) + 1, top + fontrenderer.FONT_HEIGHT - 1, -1873784752);
                fontrenderer.drawString(mStack, msg, 2, top, 14737632);
                top += fontrenderer.FONT_HEIGHT;
            }

            top = 2;
            for (String msg : listR)
            {
                if (msg == null) continue;
                int w = fontrenderer.getStringWidth(msg);
                int left = width - 2 - w;
                fill(mStack, left - 1, top - 1, left + w + 1, top + fontrenderer.FONT_HEIGHT - 1, -1873784752);
                fontrenderer.drawString(mStack, msg, left, top, 14737632);
                top += fontrenderer.FONT_HEIGHT;
            }
        }

        mc.getProfiler().endSection();
        post(TEXT, mStack);
    }

    protected void renderFPSGraph(MatrixStack mStack)
    {
        if (this.mc.gameSettings.showDebugInfo && this.mc.gameSettings.showLagometer && !pre(FPS_GRAPH, mStack))
        {
            this.debugOverlay.render(mStack);
            post(FPS_GRAPH, mStack);
        }
    }

    protected void renderRecordOverlay(int width, int height, float partialTicks, MatrixStack mStack)
    {
        if (overlayMessageTime > 0)
        {
            mc.getProfiler().startSection("overlayMessage");
            float hue = (float)overlayMessageTime - partialTicks;
            int opacity = (int)(hue * 255.0F / 20.0F);
            if (opacity > 255) opacity = 255;

            if (opacity > 8)
            {
                RenderSystem.pushMatrix();
                RenderSystem.translatef((float)(width / 2), (float)(height - 68), 0.0F);
                RenderSystem.enableBlend();
                RenderSystem.defaultBlendFunc();
                int color = (animateOverlayMessageColor ? MathHelper.hsvToRGB(hue / 50.0F, 0.7F, 0.6F) & WHITE : WHITE);
                func_238448_a_(mStack, fontrenderer, -4, fontrenderer.getStringPropertyWidth(overlayMessage), 16777215 | (opacity << 24));
                fontrenderer.func_238422_b_(mStack, overlayMessage.func_241878_f(), -fontrenderer.getStringPropertyWidth(overlayMessage) / 2, -4, color | (opacity << 24));
                RenderSystem.disableBlend();
                RenderSystem.popMatrix();
            }

            mc.getProfiler().endSection();
        }
    }

    protected void renderTitle(int width, int height, float partialTicks, MatrixStack mStack)
    {
        if (displayedTitle != null && titlesTimer > 0)
        {
            mc.getProfiler().startSection("titleAndSubtitle");
            float age = (float)this.titlesTimer - partialTicks;
            int opacity = 255;

            if (titlesTimer > titleFadeOut + titleDisplayTime)
            {
                float f3 = (float)(titleFadeIn + titleDisplayTime + titleFadeOut) - age;
                opacity = (int)(f3 * 255.0F / (float)titleFadeIn);
            }
            if (titlesTimer <= titleFadeOut) opacity = (int)(age * 255.0F / (float)this.titleFadeOut);

            opacity = MathHelper.clamp(opacity, 0, 255);

            if (opacity > 8)
            {
                RenderSystem.pushMatrix();
                RenderSystem.translatef((float)(width / 2), (float)(height / 2), 0.0F);
                RenderSystem.enableBlend();
                RenderSystem.defaultBlendFunc();
                RenderSystem.pushMatrix();
                RenderSystem.scalef(4.0F, 4.0F, 4.0F);
                int l = opacity << 24 & -16777216;
                this.getFontRenderer().func_238407_a_(mStack, this.displayedTitle.func_241878_f(), (float)(-this.getFontRenderer().getStringPropertyWidth(this.displayedTitle) / 2), -10.0F, 16777215 | l);
                RenderSystem.popMatrix();
                if (this.displayedSubTitle != null)
                {
                    RenderSystem.pushMatrix();
                    RenderSystem.scalef(2.0F, 2.0F, 2.0F);
                    this.getFontRenderer().func_238407_a_(mStack, this.displayedSubTitle.func_241878_f(), (float)(-this.getFontRenderer().getStringPropertyWidth(this.displayedSubTitle) / 2), 5.0F, 16777215 | l);
                    RenderSystem.popMatrix();
                }
                RenderSystem.disableBlend();
                RenderSystem.popMatrix();
            }

            this.mc.getProfiler().endSection();
        }
    }

    protected void renderChat(int width, int height, MatrixStack mStack)
    {
        mc.getProfiler().startSection("chat");

        RenderGameOverlayEvent.Chat event = new RenderGameOverlayEvent.Chat(mStack, eventParent, 0, height - 48);
        if (MinecraftForge.EVENT_BUS.post(event)) return;

        RenderSystem.pushMatrix();
        RenderSystem.translatef((float) event.getPosX(), (float) event.getPosY(), 0.0F);
        persistantChatGUI.func_238492_a_(mStack, ticks);
        RenderSystem.popMatrix();

        post(CHAT, mStack);

        mc.getProfiler().endSection();
    }

    protected void renderPlayerList(int width, int height, MatrixStack mStack)
    {
        ScoreObjective scoreobjective = this.mc.world.getScoreboard().getObjectiveInDisplaySlot(0);
        ClientPlayNetHandler handler = mc.player.connection;

        if (mc.gameSettings.keyBindPlayerList.isKeyDown() && (!mc.isIntegratedServerRunning() || handler.getPlayerInfoMap().size() > 1 || scoreobjective != null))
        {
            this.overlayPlayerList.setVisible(true);
            if (pre(PLAYER_LIST, mStack)) return;
            this.overlayPlayerList.func_238523_a_(mStack, width, this.mc.world.getScoreboard(), scoreobjective);
            post(PLAYER_LIST, mStack);
        }
        else
        {
            this.overlayPlayerList.setVisible(false);
        }
    }

    protected void renderHealthMount(int width, int height, MatrixStack mStack)
    {
        PlayerEntity player = (PlayerEntity)mc.getRenderViewEntity();
        Entity tmp = player.getRidingEntity();
        if (!(tmp instanceof LivingEntity)) return;

        bind(GUI_ICONS_LOCATION);

        if (pre(HEALTHMOUNT, mStack)) return;

        boolean unused = false;
        int left_align = width / 2 + 91;

        mc.getProfiler().endStartSection("mountHealth");
        RenderSystem.enableBlend();
        LivingEntity mount = (LivingEntity)tmp;
        int health = (int)Math.ceil((double)mount.getHealth());
        float healthMax = mount.getMaxHealth();
        int hearts = (int)(healthMax + 0.5F) / 2;

        if (hearts > 30) hearts = 30;

        final int MARGIN = 52;
        final int BACKGROUND = MARGIN + (unused ? 1 : 0);
        final int HALF = MARGIN + 45;
        final int FULL = MARGIN + 36;

        for (int heart = 0; hearts > 0; heart += 20)
        {
            int top = height - right_height;

            int rowCount = Math.min(hearts, 10);
            hearts -= rowCount;

            for (int i = 0; i < rowCount; ++i)
            {
                int x = left_align - i * 8 - 9;
                blit(mStack, x, top, BACKGROUND, 9, 9, 9);

                if (i * 2 + 1 + heart < health)
                    blit(mStack, x, top, FULL, 9, 9, 9);
                else if (i * 2 + 1 + heart == health)
                    blit(mStack, x, top, HALF, 9, 9, 9);
            }

            right_height += 10;
        }
        RenderSystem.disableBlend();
        post(HEALTHMOUNT, mStack);
    }

    //Helper macros
    private boolean pre(ElementType type, MatrixStack mStack)
    {
        return MinecraftForge.EVENT_BUS.post(new RenderGameOverlayEvent.Pre(mStack, eventParent, type));
    }
    private void post(ElementType type, MatrixStack mStack)
    {
        MinecraftForge.EVENT_BUS.post(new RenderGameOverlayEvent.Post(mStack, eventParent, type));
    }
    private void bind(ResourceLocation res)
    {
        mc.getTextureManager().bindTexture(res);
    }

    private class GuiOverlayDebugForge extends DebugOverlayGui
    {
        private Minecraft mc;
        private GuiOverlayDebugForge(Minecraft mc)
        {
            super(mc);
            this.mc = mc;
        }
        public void update()
        {
            Entity entity = this.mc.getRenderViewEntity();
            this.rayTraceBlock = entity.pick(rayTraceDistance, 0.0F, false);
            this.rayTraceFluid = entity.pick(rayTraceDistance, 0.0F, true);
        }
        @Override protected void renderDebugInfoLeft(MatrixStack mStack){}
        @Override protected void renderDebugInfoRight(MatrixStack mStack){}
        private List<String> getLeft()
        {
            List<String> ret = this.getDebugInfoLeft();
            ret.add("");
            ret.add("Debug: Pie [shift]: " + (this.mc.gameSettings.showDebugProfilerChart ? "visible" : "hidden") + " FPS [alt]: " + (this.mc.gameSettings.showLagometer ? "visible" : "hidden"));
            ret.add("For help: press F3 + Q");
            return ret;
        }
        private List<String> getRight(){ return this.getDebugInfoRight(); }
    }
}
