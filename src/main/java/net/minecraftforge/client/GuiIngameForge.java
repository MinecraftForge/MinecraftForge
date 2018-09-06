/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
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

package net.minecraftforge.client;

import static net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType.*;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.GuiOverlayDebug;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
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
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.GameType;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;

import org.lwjgl.opengl.GL11;

public class GuiIngameForge extends GuiIngame
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

    private FontRenderer fontrenderer = null;
    private RenderGameOverlayEvent eventParent;
    //private static final String MC_VERSION = MinecraftForge.MC_VERSION;
    private GuiOverlayDebugForge debugOverlay;

    public GuiIngameForge(Minecraft mc)
    {
        super(mc);
        debugOverlay = new GuiOverlayDebugForge(mc);
    }

    @Override
    public void renderGameOverlay(float partialTicks)
    {
        this.field_194811_H = this.mc.field_195558_d.func_198107_o();
        this.field_194812_I = this.mc.field_195558_d.func_198087_p();
        eventParent = new RenderGameOverlayEvent(partialTicks, this.mc.field_195558_d);
        renderHealthMount = mc.player.getRidingEntity() instanceof EntityLivingBase;
        renderFood = mc.player.getRidingEntity() == null;
        renderJumpBar = mc.player.isRidingHorse();

        right_height = 39;
        left_height = 39;

        if (pre(ALL)) return;

        fontrenderer = mc.fontRenderer;
        //mc.entityRenderer.setupOverlayRendering();
        GlStateManager.enableBlend();

        if (renderVignette && Minecraft.isFancyGraphicsEnabled())
        {
            func_194800_d(mc.player.getBrightness());
        }
        else
        {
            GlStateManager.enableDepth();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        }

        if (renderHelmet) renderHelmet(partialTicks);

        if (renderPortal && !mc.player.isPotionActive(MobEffects.NAUSEA))
        {
            func_194805_e(partialTicks);
        }

        if (renderHotbar) func_194806_b(partialTicks);

        if (!this.mc.gameSettings.hideGUI) {
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            zLevel = -90.0F;
            rand.setSeed((long)(updateCounter * 312871));

            if (renderCrosshairs) func_194798_c(partialTicks);
            if (renderBossHealth) renderBossHealth();

            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            if (this.mc.playerController.shouldDrawHUD() && this.mc.getRenderViewEntity() instanceof EntityPlayer)
            {
                if (renderHealth) renderHealth(this.field_194811_H, this.field_194812_I);
                if (renderArmor)  renderArmor(this.field_194811_H, this.field_194812_I);
                if (renderFood)   renderFood(this.field_194811_H, this.field_194812_I);
                if (renderHealthMount) renderHealthMount(this.field_194811_H, this.field_194812_I);
                if (renderAir)    renderAir(this.field_194811_H, this.field_194812_I);
            }

            if (renderJumpBar)
            {
                func_194803_a(this.field_194811_H / 2 - 91);
            }
            else if (renderExperiance)
            {
                renderExperience(this.field_194811_H / 2 - 91);
            }
            if (this.mc.gameSettings.heldItemTooltips && this.mc.playerController.getCurrentGameType() != GameType.SPECTATOR) {
                this.func_194801_c();
             } else if (this.mc.player.isSpectator()) {
                this.spectatorGui.func_195623_a();
             }
        }

        renderSleepFade(this.field_194811_H, this.field_194812_I);

        renderHUDText(this.field_194811_H, this.field_194812_I);
        renderFPSGraph();
        func_194809_b();
        if (!mc.gameSettings.hideGUI) {
            renderRecordOverlay(this.field_194811_H, this.field_194812_I, partialTicks);
            renderSubtitles();
            renderTitle(this.field_194811_H, this.field_194812_I, partialTicks);
        }


        Scoreboard scoreboard = this.mc.world.getScoreboard();
        ScoreObjective objective = null;
        ScorePlayerTeam scoreplayerteam = scoreboard.getPlayersTeam(mc.player.func_195047_I_());
        if (scoreplayerteam != null)
        {
            int slot = scoreplayerteam.getColor().getColorIndex();
            if (slot >= 0) objective = scoreboard.getObjectiveInDisplaySlot(3 + slot);
        }
        ScoreObjective scoreobjective1 = objective != null ? objective : scoreboard.getObjectiveInDisplaySlot(1);
        if (renderObjective && scoreobjective1 != null)
        {
            this.func_194802_a(scoreobjective1);
        }

        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
        GlStateManager.disableAlpha();

        renderChat(this.field_194811_H, this.field_194812_I);

        renderPlayerList(this.field_194811_H, this.field_194812_I);

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.disableLighting();
        GlStateManager.enableAlpha();

        post(ALL);
    }

    @Override
    protected void func_194798_c(float partialTicks)
    {
        if (pre(CROSSHAIRS)) return;
        bind(Gui.ICONS);
        GlStateManager.enableBlend();
        super.func_194798_c(partialTicks);
        post(CROSSHAIRS);
    }

    @Override
    protected void func_194809_b()
    {
        if (pre(POTION_ICONS)) return;
        super.func_194809_b();
        post(POTION_ICONS);
    }

    protected void renderSubtitles()
    {
        if (pre(SUBTITLES)) return;
        this.overlaySubtitle.func_195620_a();
        post(SUBTITLES);
    }

    //@Override
    protected void renderBossHealth()
    {
        if (pre(BOSSHEALTH)) return;
        bind(Gui.ICONS);
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        mc.profiler.startSection("bossHealth");
        GlStateManager.enableBlend();
        this.overlayBoss.renderBossHealth();
        GlStateManager.disableBlend();
        mc.profiler.endSection();
        post(BOSSHEALTH);
    }

    @Override
    protected void func_194800_d(float lightLevel)
    {
        if (pre(VIGNETTE))
        {
            // Need to put this here, since Vanilla assumes this state after the vignette was rendered.
            GlStateManager.enableDepth();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            return;
        }
        super.func_194800_d(lightLevel);
        post(VIGNETTE);
    }

    private void renderHelmet(float partialTicks)
    {
        if (pre(HELMET)) return;

        ItemStack itemstack = this.mc.player.inventory.armorItemInSlot(3);

        if (this.mc.gameSettings.thirdPersonView == 0 && !itemstack.isEmpty())
        {
            Item item = itemstack.getItem();
            if (item == Blocks.field_196625_cS.func_199767_j())
            {
                func_194808_p();
            }
            else
            {
                item.renderHelmetOverlay(itemstack, mc.player, this.field_194811_H, this.field_194812_I, partialTicks);
            }
        }

        post(HELMET);
    }

    protected void renderArmor(int width, int height)
    {
        if (pre(ARMOR)) return;
        mc.profiler.startSection("armor");

        GlStateManager.enableBlend();
        int left = width / 2 - 91;
        int top = height - left_height;

        int level = ForgeHooks.getTotalArmorValue(mc.player);
        for (int i = 1; level > 0 && i < 20; i += 2)
        {
            if (i < level)
            {
                drawTexturedModalRect(left, top, 34, 9, 9, 9);
            }
            else if (i == level)
            {
                drawTexturedModalRect(left, top, 25, 9, 9, 9);
            }
            else if (i > level)
            {
                drawTexturedModalRect(left, top, 16, 9, 9, 9);
            }
            left += 8;
        }
        left_height += 10;

        GlStateManager.disableBlend();
        mc.profiler.endSection();
        post(ARMOR);
    }

    @Override
    protected void func_194805_e(float partialTicks)
    {
        if (pre(PORTAL)) return;

        float f1 = mc.player.prevTimeInPortal + (mc.player.timeInPortal - mc.player.prevTimeInPortal) * partialTicks;

        if (f1 > 0.0F)
        {
            super.func_194805_e(f1);
        }

        post(PORTAL);
    }

    @Override
    protected void func_194806_b(float partialTicks)
    {
        if (pre(HOTBAR)) return;

        if (mc.playerController.getCurrentGameType() == GameType.SPECTATOR)
        {
            this.spectatorGui.func_195622_a(partialTicks);
        }
        else
        {
            super.func_194806_b(partialTicks);
        }

        post(HOTBAR);
    }

    @Override
    public void setOverlayMessage(ITextComponent component, boolean animateColor)
    {
        this.setOverlayMessage(component.getFormattedText(), animateColor);
    }

    protected void renderAir(int width, int height)
    {
        if (pre(AIR)) return;
        mc.profiler.startSection("air");
        EntityPlayer player = (EntityPlayer)this.mc.getRenderViewEntity();
        GlStateManager.enableBlend();
        int left = width / 2 + 91;
        int top = height - right_height;

        int air = player.getAir();
        if (player.func_208600_a(FluidTags.field_206959_a) || air < 300)
        {
            int full = MathHelper.ceil((double)(air - 2) * 10.0D / 300.0D);
            int partial = MathHelper.ceil((double)air * 10.0D / 300.0D) - full;

            for (int i = 0; i < full + partial; ++i)
            {
                drawTexturedModalRect(left - i * 8 - 9, top, (i < full ? 16 : 25), 18, 9, 9);
            }
            right_height += 10;
        }

        GlStateManager.disableBlend();
        mc.profiler.endSection();
        post(AIR);
    }

    public void renderHealth(int width, int height)
    {
        bind(ICONS);
        if (pre(HEALTH)) return;
        mc.profiler.startSection("health");
        GlStateManager.enableBlend();

        EntityPlayer player = (EntityPlayer)this.mc.getRenderViewEntity();
        int health = MathHelper.ceil(player.getHealth());
        boolean highlight = healthUpdateCounter > (long)updateCounter && (healthUpdateCounter - (long)updateCounter) / 3L %2L == 1L;

        if (health < this.playerHealth && player.hurtResistantTime > 0)
        {
            this.lastSystemTime = Util.func_211177_b();
            this.healthUpdateCounter = (long)(this.updateCounter + 20);
        }
        else if (health > this.playerHealth && player.hurtResistantTime > 0)
        {
            this.lastSystemTime = Util.func_211177_b();
            this.healthUpdateCounter = (long)(this.updateCounter + 10);
        }

        if (Util.func_211177_b() - this.lastSystemTime > 1000L)
        {
            this.playerHealth = health;
            this.lastPlayerHealth = health;
            this.lastSystemTime = Util.func_211177_b();
        }

        this.playerHealth = health;
        int healthLast = this.lastPlayerHealth;

        IAttributeInstance attrMaxHealth = player.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH);
        float healthMax = (float)attrMaxHealth.getAttributeValue();
        float absorb = MathHelper.ceil(player.getAbsorptionAmount());

        int healthRows = MathHelper.ceil((healthMax + absorb) / 2.0F / 10.0F);
        int rowHeight = Math.max(10 - (healthRows - 2), 3);

        this.rand.setSeed((long)(updateCounter * 312871));

        int left = width / 2 - 91;
        int top = height - left_height;
        left_height += (healthRows * rowHeight);
        if (rowHeight != 10) left_height += 10 - rowHeight;

        int regen = -1;
        if (player.isPotionActive(MobEffects.REGENERATION))
        {
            regen = updateCounter % 25;
        }

        final int TOP =  9 * (mc.world.getWorldInfo().isHardcoreModeEnabled() ? 5 : 0);
        final int BACKGROUND = (highlight ? 25 : 16);
        int MARGIN = 16;
        if (player.isPotionActive(MobEffects.POISON))      MARGIN += 36;
        else if (player.isPotionActive(MobEffects.WITHER)) MARGIN += 72;
        float absorbRemaining = absorb;

        for (int i = MathHelper.ceil((healthMax + absorb) / 2.0F) - 1; i >= 0; --i)
        {
            //int b0 = (highlight ? 1 : 0);
            int row = MathHelper.ceil((float)(i + 1) / 10.0F) - 1;
            int x = left + i % 10 * 8;
            int y = top - row * rowHeight;

            if (health <= 4) y += rand.nextInt(2);
            if (i == regen) y -= 2;

            drawTexturedModalRect(x, y, BACKGROUND, TOP, 9, 9);

            if (highlight)
            {
                if (i * 2 + 1 < healthLast)
                    drawTexturedModalRect(x, y, MARGIN + 54, TOP, 9, 9); //6
                else if (i * 2 + 1 == healthLast)
                    drawTexturedModalRect(x, y, MARGIN + 63, TOP, 9, 9); //7
            }

            if (absorbRemaining > 0.0F)
            {
                if (absorbRemaining == absorb && absorb % 2.0F == 1.0F)
                {
                    drawTexturedModalRect(x, y, MARGIN + 153, TOP, 9, 9); //17
                    absorbRemaining -= 1.0F;
                }
                else
                {
                    drawTexturedModalRect(x, y, MARGIN + 144, TOP, 9, 9); //16
                    absorbRemaining -= 2.0F;
                }
            }
            else
            {
                if (i * 2 + 1 < health)
                    drawTexturedModalRect(x, y, MARGIN + 36, TOP, 9, 9); //4
                else if (i * 2 + 1 == health)
                    drawTexturedModalRect(x, y, MARGIN + 45, TOP, 9, 9); //5
            }
        }

        GlStateManager.disableBlend();
        mc.profiler.endSection();
        post(HEALTH);
    }

    public void renderFood(int width, int height)
    {
        if (pre(FOOD)) return;
        mc.profiler.startSection("food");

        EntityPlayer player = (EntityPlayer)this.mc.getRenderViewEntity();
        GlStateManager.enableBlend();
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

            if (mc.player.isPotionActive(MobEffects.HUNGER))
            {
                icon += 36;
                background = 13;
            }
            if (unused) background = 1; //Probably should be a += 1 but vanilla never uses this

            if (player.getFoodStats().getSaturationLevel() <= 0.0F && updateCounter % (level * 3 + 1) == 0)
            {
                y = top + (rand.nextInt(3) - 1);
            }

            drawTexturedModalRect(x, y, 16 + background * 9, 27, 9, 9);

            if (idx < level)
                drawTexturedModalRect(x, y, icon + 36, 27, 9, 9);
            else if (idx == level)
                drawTexturedModalRect(x, y, icon + 45, 27, 9, 9);
        }
        GlStateManager.disableBlend();
        mc.profiler.endSection();
        post(FOOD);
    }

    protected void renderSleepFade(int width, int height)
    {
        if (mc.player.getSleepTimer() > 0)
        {
            mc.profiler.startSection("sleep");
            GlStateManager.disableDepth();
            GlStateManager.disableAlpha();
            int sleepTime = mc.player.getSleepTimer();
            float opacity = (float)sleepTime / 100.0F;

            if (opacity > 1.0F)
            {
                opacity = 1.0F - (float)(sleepTime - 100) / 10.0F;
            }

            int color = (int)(220.0F * opacity) << 24 | 1052704;
            drawRect(0, 0, width, height, color);
            GlStateManager.enableAlpha();
            GlStateManager.enableDepth();
            mc.profiler.endSection();
        }
    }

    protected void renderExperience(int x)
    {
        bind(ICONS);
        if (pre(EXPERIENCE)) return;
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.disableBlend();

        if (mc.playerController.gameIsSurvivalOrAdventure())
        {
            super.func_194804_b(x);
        }
        GlStateManager.enableBlend();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        post(EXPERIENCE);
    }

    @Override
    public void func_194803_a(int x)
    {
        bind(ICONS);
        if (pre(JUMPBAR)) return;
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.disableBlend();

        super.func_194803_a(x);

        GlStateManager.enableBlend();
        mc.profiler.endSection();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        post(JUMPBAR);
    }

    protected void renderHUDText(int width, int height)
    {
        mc.profiler.startSection("forgeHudText");
        OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
        ArrayList<String> listL = new ArrayList<String>();
        ArrayList<String> listR = new ArrayList<String>();

        if (mc.isDemo())
        {
            long time = mc.world.getTotalWorldTime();
            if (time >= 120500L)
            {
                listR.add(I18n.format("demo.demoExpired"));
            }
            else
            {
                listR.add(I18n.format("demo.remainingTime", StringUtils.ticksToElapsedTime((int)(120500L - time))));
            }
        }

        if (this.mc.gameSettings.showDebugInfo && !pre(DEBUG))
        {
            listL.addAll(debugOverlay.getLeft());
            listR.addAll(debugOverlay.getRight());
            post(DEBUG);
        }

        RenderGameOverlayEvent.Text event = new RenderGameOverlayEvent.Text(eventParent, listL, listR);
        if (!MinecraftForge.EVENT_BUS.post(event))
        {
            int top = 2;
            for (String msg : listL)
            {
                if (msg == null) continue;
                drawRect(1, top - 1, 2 + fontrenderer.getStringWidth(msg) + 1, top + fontrenderer.FONT_HEIGHT - 1, -1873784752);
                fontrenderer.drawStringWithShadow(msg, 2, top, 14737632);
                top += fontrenderer.FONT_HEIGHT;
            }

            top = 2;
            for (String msg : listR)
            {
                if (msg == null) continue;
                int w = fontrenderer.getStringWidth(msg);
                int left = width - 2 - w;
                drawRect(left - 1, top - 1, left + w + 1, top + fontrenderer.FONT_HEIGHT - 1, -1873784752);
                fontrenderer.drawStringWithShadow(msg, left, top, 14737632);
                top += fontrenderer.FONT_HEIGHT;
            }
        }

        mc.profiler.endSection();
        post(TEXT);
    }

    protected void renderFPSGraph()
    {
        if (this.mc.gameSettings.showDebugInfo && this.mc.gameSettings.showLagometer && !pre(FPS_GRAPH))
        {
            this.debugOverlay.renderLagometer();
            post(FPS_GRAPH);
        }
    }

    protected void renderRecordOverlay(int width, int height, float partialTicks)
    {
        if (overlayMessageTime > 0)
        {
            mc.profiler.startSection("overlayMessage");
            float hue = (float)overlayMessageTime - partialTicks;
            int opacity = (int)(hue * 256.0F / 20.0F);
            if (opacity > 255) opacity = 255;

            if (opacity > 0)
            {
                GlStateManager.pushMatrix();
                GlStateManager.translate((float)(width / 2), (float)(height - 68), 0.0F);
                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
                int color = (animateOverlayMessageColor ? Color.HSBtoRGB(hue / 50.0F, 0.7F, 0.6F) & WHITE : WHITE);
                fontrenderer.drawStringWithShadow(overlayMessage, -fontrenderer.getStringWidth(overlayMessage) / 2, -4, color | (opacity << 24));
                GlStateManager.disableBlend();
                GlStateManager.popMatrix();
            }

            mc.profiler.endSection();
        }
    }

    protected void renderTitle(int width, int height, float partialTicks)
    {
        if (titlesTimer > 0)
        {
            mc.profiler.startSection("titleAndSubtitle");
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
                GlStateManager.pushMatrix();
                GlStateManager.translate((float)(width / 2), (float)(height / 2), 0.0F);
                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
                GlStateManager.pushMatrix();
                GlStateManager.scale(4.0F, 4.0F, 4.0F);
                int l = opacity << 24 & -16777216;
                this.getFontRenderer().drawStringWithShadow(this.displayedTitle, (float)(-this.getFontRenderer().getStringWidth(this.displayedTitle) / 2), -10.0F, 16777215 | l);
                GlStateManager.popMatrix();
                GlStateManager.pushMatrix();
                GlStateManager.scale(2.0F, 2.0F, 2.0F);
                this.getFontRenderer().drawStringWithShadow(this.displayedSubTitle, (float)(-this.getFontRenderer().getStringWidth(this.displayedSubTitle) / 2), 5.0F, 16777215 | l);
                GlStateManager.popMatrix();
                GlStateManager.disableBlend();
                GlStateManager.popMatrix();
            }

            this.mc.profiler.endSection();
        }
    }

    protected void renderChat(int width, int height)
    {
        mc.profiler.startSection("chat");

        RenderGameOverlayEvent.Chat event = new RenderGameOverlayEvent.Chat(eventParent, 0, height - 48);
        if (MinecraftForge.EVENT_BUS.post(event)) return;

        GlStateManager.pushMatrix();
        GlStateManager.translate((float) event.getPosX(), (float) event.getPosY(), 0.0F);
        persistantChatGUI.drawChat(updateCounter);
        GlStateManager.popMatrix();

        post(CHAT);

        mc.profiler.endSection();
    }

    protected void renderPlayerList(int width, int height)
    {
        ScoreObjective scoreobjective = this.mc.world.getScoreboard().getObjectiveInDisplaySlot(0);
        NetHandlerPlayClient handler = mc.player.connection;

        if (mc.gameSettings.keyBindPlayerList.isKeyDown() && (!mc.isIntegratedServerRunning() || handler.getPlayerInfoMap().size() > 1 || scoreobjective != null))
        {
            this.overlayPlayerList.updatePlayerList(true);
            if (pre(PLAYER_LIST)) return;
            this.overlayPlayerList.renderPlayerlist(width, this.mc.world.getScoreboard(), scoreobjective);
            post(PLAYER_LIST);
        }
        else
        {
            this.overlayPlayerList.updatePlayerList(false);
        }
    }

    protected void renderHealthMount(int width, int height)
    {
        EntityPlayer player = (EntityPlayer)mc.getRenderViewEntity();
        Entity tmp = player.getRidingEntity();
        if (!(tmp instanceof EntityLivingBase)) return;

        bind(ICONS);

        if (pre(HEALTHMOUNT)) return;

        boolean unused = false;
        int left_align = width / 2 + 91;

        mc.profiler.endStartSection("mountHealth");
        GlStateManager.enableBlend();
        EntityLivingBase mount = (EntityLivingBase)tmp;
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
                drawTexturedModalRect(x, top, BACKGROUND, 9, 9, 9);

                if (i * 2 + 1 + heart < health)
                    drawTexturedModalRect(x, top, FULL, 9, 9, 9);
                else if (i * 2 + 1 + heart == health)
                    drawTexturedModalRect(x, top, HALF, 9, 9, 9);
            }

            right_height += 10;
        }
        GlStateManager.disableBlend();
        post(HEALTHMOUNT);
    }

    //Helper macros
    private boolean pre(ElementType type)
    {
        return MinecraftForge.EVENT_BUS.post(new RenderGameOverlayEvent.Pre(eventParent, type));
    }
    private void post(ElementType type)
    {
        MinecraftForge.EVENT_BUS.post(new RenderGameOverlayEvent.Post(eventParent, type));
    }
    private void bind(ResourceLocation res)
    {
        mc.getTextureManager().bindTexture(res);
    }

    private class GuiOverlayDebugForge extends GuiOverlayDebug
    {
        private Minecraft mc;
        private GuiOverlayDebugForge(Minecraft mc)
        {
            super(mc);
            this.mc = mc;
        }
        @Override protected void renderDebugInfoLeft(){}
        @Override protected void func_194819_c(){}
        private List<String> getLeft()
        {
            List<String> ret = this.func_209011_c();
            ret.add("");
            ret.add("Debug: Pie [shift]: " + (this.mc.gameSettings.showDebugProfilerChart ? "visible" : "hidden") + " FPS [alt]: " + (this.mc.gameSettings.showLagometer ? "visible" : "hidden"));
            ret.add("For help: press F3 + Q");
            return ret;
        }
        private List<String> getRight(){ return this.getDebugInfoRight(); }
    }
}
