package net.minecraft.src;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import net.minecraft.src.forge.*;

public class GuiIngame extends Gui
{
    private static RenderItem itemRenderer = new RenderItem();

    /** A list with all the chat messages in. */
    private List chatMessageList = new ArrayList();
    private Random rand = new Random();
    private Minecraft mc;
    public String field_933_a = null;
    private int updateCounter = 0;

    /** The string specifying which record music is playing */
    private String recordPlaying = "";

    /** How many ticks the record playing message will be displayed */
    private int recordPlayingUpFor = 0;
    private boolean recordIsPlaying = false;

    /** Damage partial time (GUI) */
    public float damageGuiPartialTime;

    /** Previous frame vignette brightness (slowly changes by 1% each frame) */
    float prevVignetteBrightness = 1.0F;

    public GuiIngame(Minecraft par1Minecraft)
    {
        this.mc = par1Minecraft;
    }

    /**
     * Render the ingame overlay with quick icon bar, ...
     */
    public void renderGameOverlay(float par1, boolean par2, int par3, int par4)
    {
        ScaledResolution var5 = new ScaledResolution(this.mc.gameSettings, this.mc.displayWidth, this.mc.displayHeight);
        int var6 = var5.getScaledWidth();
        int var7 = var5.getScaledHeight();
        FontRenderer var8 = this.mc.fontRenderer;
        this.mc.entityRenderer.setupOverlayRendering();
        GL11.glEnable(GL11.GL_BLEND);

        if (Minecraft.isFancyGraphicsEnabled())
        {
            this.renderVignette(this.mc.thePlayer.getEntityBrightness(par1), var6, var7);
        }
        else
        {
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        }

        ItemStack var9 = this.mc.thePlayer.inventory.armorItemInSlot(3);

        if (this.mc.gameSettings.thirdPersonView == 0 && var9 != null && var9.itemID == Block.pumpkin.blockID)
        {
            this.renderPumpkinBlur(var6, var7);
        }

        float var10;

        if (!this.mc.thePlayer.isPotionActive(Potion.confusion))
        {
            var10 = this.mc.thePlayer.prevTimeInPortal + (this.mc.thePlayer.timeInPortal - this.mc.thePlayer.prevTimeInPortal) * par1;

            if (var10 > 0.0F)
            {
                this.renderPortalOverlay(var10, var6, var7);
            }
        }

        boolean var11;
        int var12;
        int var13;
        int var17;
        int var16;
        int var18;
        int var20;
        int var22;
        int var51;

        if (!this.mc.playerController.func_35643_e())
        {
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.mc.renderEngine.getTexture("/gui/gui.png"));
            InventoryPlayer var33 = this.mc.thePlayer.inventory;
            this.zLevel = -90.0F;
            this.drawTexturedModalRect(var6 / 2 - 91, var7 - 22, 0, 0, 182, 22);
            this.drawTexturedModalRect(var6 / 2 - 91 - 1 + var33.currentItem * 20, var7 - 22 - 1, 0, 22, 24, 22);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.mc.renderEngine.getTexture("/gui/icons.png"));
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_ONE_MINUS_DST_COLOR, GL11.GL_ONE_MINUS_SRC_COLOR);
            this.drawTexturedModalRect(var6 / 2 - 7, var7 / 2 - 7, 0, 0, 16, 16);
            GL11.glDisable(GL11.GL_BLEND);
            var11 = this.mc.thePlayer.heartsLife / 3 % 2 == 1;

            if (this.mc.thePlayer.heartsLife < 10)
            {
                var11 = false;
            }

            var12 = this.mc.thePlayer.getEntityHealth();
            var13 = this.mc.thePlayer.prevHealth;
            this.rand.setSeed((long)(this.updateCounter * 312871));
            boolean var14 = false;
            FoodStats var15 = this.mc.thePlayer.getFoodStats();
            var16 = var15.getFoodLevel();
            var17 = var15.getPrevFoodLevel();
            this.renderBossHealth();
            int var19;

            if (this.mc.playerController.shouldDrawHUD())
            {
                var18 = var6 / 2 - 91;
                var19 = var6 / 2 + 91;
                var20 = this.mc.thePlayer.xpBarCap();
                int var23;

                if (var20 > 0)
                {
                    short var21 = 182;
                    var22 = (int)(this.mc.thePlayer.experience * (float)(var21 + 1));
                    var23 = var7 - 32 + 3;
                    this.drawTexturedModalRect(var18, var23, 0, 64, var21, 5);

                    if (var22 > 0)
                    {
                        this.drawTexturedModalRect(var18, var23, 0, 69, var22, 5);
                    }
                }

                var51 = var7 - 39;
                var22 = var51 - 10;
                var23 = 0;
                for (int x = 0; x < mc.thePlayer.inventory.armorInventory.length; x++)
                {
                    ItemStack stack = mc.thePlayer.inventory.armorInventory[x];
                    if (stack != null && stack.getItem() instanceof ISpecialArmor)
                    {
                        var23 += ((ISpecialArmor)stack.getItem()).getArmorDisplay(mc.thePlayer, stack, x);
                    }
                    else if (stack != null && stack.getItem() instanceof ItemArmor)
                    {
                        var23 += ((ItemArmor)stack.getItem()).damageReduceAmount;
                    }
                }
                int var24 = -1;

                if (this.mc.thePlayer.isPotionActive(Potion.regeneration))
                {
                    var24 = this.updateCounter % 25;
                }

                int var25;
                int var26;
                int var29;

                for (var25 = 0; var25 < 10; ++var25)
                {
                    if (var23 > 0)
                    {
                        var26 = var18 + var25 * 8;

                        if (var25 * 2 + 1 < var23)
                        {
                            this.drawTexturedModalRect(var26, var22, 34, 9, 9, 9);
                        }

                        if (var25 * 2 + 1 == var23)
                        {
                            this.drawTexturedModalRect(var26, var22, 25, 9, 9, 9);
                        }

                        if (var25 * 2 + 1 > var23)
                        {
                            this.drawTexturedModalRect(var26, var22, 16, 9, 9, 9);
                        }
                    }

                    var26 = 16;

                    if (this.mc.thePlayer.isPotionActive(Potion.poison))
                    {
                        var26 += 36;
                    }

                    byte var27 = 0;

                    if (var11)
                    {
                        var27 = 1;
                    }

                    int var28 = var18 + var25 * 8;
                    var29 = var51;

                    if (var12 <= 4)
                    {
                        var29 = var51 + this.rand.nextInt(2);
                    }

                    if (var25 == var24)
                    {
                        var29 -= 2;
                    }

                    byte var30 = 0;

                    if (this.mc.theWorld.getWorldInfo().isHardcoreModeEnabled())
                    {
                        var30 = 5;
                    }

                    this.drawTexturedModalRect(var28, var29, 16 + var27 * 9, 9 * var30, 9, 9);

                    if (var11)
                    {
                        if (var25 * 2 + 1 < var13)
                        {
                            this.drawTexturedModalRect(var28, var29, var26 + 54, 9 * var30, 9, 9);
                        }

                        if (var25 * 2 + 1 == var13)
                        {
                            this.drawTexturedModalRect(var28, var29, var26 + 63, 9 * var30, 9, 9);
                        }
                    }

                    if (var25 * 2 + 1 < var12)
                    {
                        this.drawTexturedModalRect(var28, var29, var26 + 36, 9 * var30, 9, 9);
                    }

                    if (var25 * 2 + 1 == var12)
                    {
                        this.drawTexturedModalRect(var28, var29, var26 + 45, 9 * var30, 9, 9);
                    }
                }

                int var55;

                for (var25 = 0; var25 < 10; ++var25)
                {
                    var26 = var51;
                    var55 = 16;
                    byte var56 = 0;

                    if (this.mc.thePlayer.isPotionActive(Potion.hunger))
                    {
                        var55 += 36;
                        var56 = 13;
                    }

                    if (this.mc.thePlayer.getFoodStats().getSaturationLevel() <= 0.0F && this.updateCounter % (var16 * 3 + 1) == 0)
                    {
                        var26 = var51 + (this.rand.nextInt(3) - 1);
                    }

                    if (var14)
                    {
                        var56 = 1;
                    }

                    var29 = var19 - var25 * 8 - 9;
                    this.drawTexturedModalRect(var29, var26, 16 + var56 * 9, 27, 9, 9);

                    if (var14)
                    {
                        if (var25 * 2 + 1 < var17)
                        {
                            this.drawTexturedModalRect(var29, var26, var55 + 54, 27, 9, 9);
                        }

                        if (var25 * 2 + 1 == var17)
                        {
                            this.drawTexturedModalRect(var29, var26, var55 + 63, 27, 9, 9);
                        }
                    }

                    if (var25 * 2 + 1 < var16)
                    {
                        this.drawTexturedModalRect(var29, var26, var55 + 36, 27, 9, 9);
                    }

                    if (var25 * 2 + 1 == var16)
                    {
                        this.drawTexturedModalRect(var29, var26, var55 + 45, 27, 9, 9);
                    }
                }

                if (this.mc.thePlayer.isInsideOfMaterial(Material.water))
                {
                    var25 = (int)Math.ceil((double)(this.mc.thePlayer.getAir() - 2) * 10.0D / 300.0D);
                    var26 = (int)Math.ceil((double)this.mc.thePlayer.getAir() * 10.0D / 300.0D) - var25;

                    for (var55 = 0; var55 < var25 + var26; ++var55)
                    {
                        if (var55 < var25)
                        {
                            this.drawTexturedModalRect(var19 - var55 * 8 - 9, var22, 16, 18, 9, 9);
                        }
                        else
                        {
                            this.drawTexturedModalRect(var19 - var55 * 8 - 9, var22, 25, 18, 9, 9);
                        }
                    }
                }
            }

            GL11.glDisable(GL11.GL_BLEND);
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
            RenderHelper.enableGUIStandardItemLighting();

            for (var18 = 0; var18 < 9; ++var18)
            {
                var19 = var6 / 2 - 90 + var18 * 20 + 2;
                var20 = var7 - 16 - 3;
                this.renderInventorySlot(var18, var19, var20, par1);
            }

            RenderHelper.disableStandardItemLighting();
            GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        }

        int var34;

        if (this.mc.thePlayer.getSleepTimer() > 0)
        {
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            GL11.glDisable(GL11.GL_ALPHA_TEST);
            var34 = this.mc.thePlayer.getSleepTimer();
            float var37 = (float)var34 / 100.0F;

            if (var37 > 1.0F)
            {
                var37 = 1.0F - (float)(var34 - 100) / 10.0F;
            }

            var12 = (int)(220.0F * var37) << 24 | 1052704;
            this.drawRect(0, 0, var6, var7, var12);
            GL11.glEnable(GL11.GL_ALPHA_TEST);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
        }

        int var40;
        int var44;

        if (this.mc.playerController.func_35642_f() && this.mc.thePlayer.experienceLevel > 0)
        {
            boolean var31 = false;
            var40 = var31 ? 16777215 : 8453920;
            String var36 = "" + this.mc.thePlayer.experienceLevel;
            var13 = (var6 - var8.getStringWidth(var36)) / 2;
            var44 = var7 - 31 - 4;
            var8.drawString(var36, var13 + 1, var44, 0);
            var8.drawString(var36, var13 - 1, var44, 0);
            var8.drawString(var36, var13, var44 + 1, 0);
            var8.drawString(var36, var13, var44 - 1, 0);
            var8.drawString(var36, var13, var44, var40);
        }

        String var49;

        if (this.mc.gameSettings.showDebugInfo)
        {
            GL11.glPushMatrix();

            if (Minecraft.hasPaidCheckTime > 0L)
            {
                GL11.glTranslatef(0.0F, 32.0F, 0.0F);
            }

            var8.drawStringWithShadow("Minecraft 1.2.3 (" + this.mc.debug + ")", 2, 2, 16777215);
            var8.drawStringWithShadow(this.mc.debugInfoRenders(), 2, 12, 16777215);
            var8.drawStringWithShadow(this.mc.getEntityDebug(), 2, 22, 16777215);
            var8.drawStringWithShadow(this.mc.debugInfoEntities(), 2, 32, 16777215);
            var8.drawStringWithShadow(this.mc.getWorldProviderName(), 2, 42, 16777215);
            long var32 = Runtime.getRuntime().maxMemory();
            long var41 = Runtime.getRuntime().totalMemory();
            long var45 = Runtime.getRuntime().freeMemory();
            long var48 = var41 - var45;
            var49 = "Used memory: " + var48 * 100L / var32 + "% (" + var48 / 1024L / 1024L + "MB) of " + var32 / 1024L / 1024L + "MB";
            this.drawString(var8, var49, var6 - var8.getStringWidth(var49) - 2, 2, 14737632);
            var49 = "Allocated memory: " + var41 * 100L / var32 + "% (" + var41 / 1024L / 1024L + "MB)";
            this.drawString(var8, var49, var6 - var8.getStringWidth(var49) - 2, 12, 14737632);
            this.drawString(var8, "x: " + this.mc.thePlayer.posX, 2, 64, 14737632);
            this.drawString(var8, "y: " + this.mc.thePlayer.posY, 2, 72, 14737632);
            this.drawString(var8, "z: " + this.mc.thePlayer.posZ, 2, 80, 14737632);
            this.drawString(var8, "f: " + (MathHelper.floor_double((double)(this.mc.thePlayer.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3), 2, 88, 14737632);
            var34 = MathHelper.floor_double(this.mc.thePlayer.posX);
            var40 = MathHelper.floor_double(this.mc.thePlayer.posY);
            var12 = MathHelper.floor_double(this.mc.thePlayer.posZ);

            if (this.mc.theWorld != null && this.mc.theWorld.blockExists(var34, var40, var12))
            {
                Chunk var38 = this.mc.theWorld.getChunkFromBlockCoords(var34, var12);
                this.drawString(var8, "lc: " + (var38.func_48498_h() + 15) + " b: " + var38.func_48490_a(var34 & 15, var12 & 15, this.mc.theWorld.getWorldChunkManager()).biomeName + " bl: " + var38.getSavedLightValue(EnumSkyBlock.Block, var34 & 15, var40, var12 & 15) + " sl: " + var38.getSavedLightValue(EnumSkyBlock.Sky, var34 & 15, var40, var12 & 15) + " rl: " + var38.getBlockLightValue(var34 & 15, var40, var12 & 15, 0), 2, 96, 14737632);
            }

            this.drawString(var8, "Seed: " + this.mc.theWorld.getSeed(), 2, 112, 14737632);
            GL11.glPopMatrix();
        }

        if (this.recordPlayingUpFor > 0)
        {
            var10 = (float)this.recordPlayingUpFor - par1;
            var40 = (int)(var10 * 256.0F / 20.0F);

            if (var40 > 255)
            {
                var40 = 255;
            }

            if (var40 > 0)
            {
                GL11.glPushMatrix();
                GL11.glTranslatef((float)(var6 / 2), (float)(var7 - 48), 0.0F);
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                var12 = 16777215;

                if (this.recordIsPlaying)
                {
                    var12 = Color.HSBtoRGB(var10 / 50.0F, 0.7F, 0.6F) & 16777215;
                }

                var8.drawString(this.recordPlaying, -var8.getStringWidth(this.recordPlaying) / 2, -4, var12 + (var40 << 24));
                GL11.glDisable(GL11.GL_BLEND);
                GL11.glPopMatrix();
            }
        }

        byte var35 = 10;
        var11 = false;

        if (this.mc.currentScreen instanceof GuiChat)
        {
            var35 = 20;
            var11 = true;
        }

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glPushMatrix();
        GL11.glTranslatef(0.0F, (float)(var7 - 48), 0.0F);
        int var42;

        for (var12 = 0; var12 < this.chatMessageList.size() && var12 < var35; ++var12)
        {
            if (((ChatLine)this.chatMessageList.get(var12)).updateCounter < 200 || var11)
            {
                double var39 = (double)((ChatLine)this.chatMessageList.get(var12)).updateCounter / 200.0D;
                var39 = 1.0D - var39;
                var39 *= 10.0D;

                if (var39 < 0.0D)
                {
                    var39 = 0.0D;
                }

                if (var39 > 1.0D)
                {
                    var39 = 1.0D;
                }

                var39 *= var39;
                var42 = (int)(255.0D * var39);

                if (var11)
                {
                    var42 = 255;
                }

                if (var42 > 0)
                {
                    byte var47 = 2;
                    var17 = -var12 * 9;
                    var49 = ((ChatLine)this.chatMessageList.get(var12)).message;
                    this.drawRect(var47, var17 - 1, var47 + 320, var17 + 8, var42 / 2 << 24);
                    GL11.glEnable(GL11.GL_BLEND);
                    var8.drawStringWithShadow(var49, var47, var17, 16777215 + (var42 << 24));
                }
            }
        }

        GL11.glPopMatrix();

        if (this.mc.thePlayer instanceof EntityClientPlayerMP && this.mc.gameSettings.keyBindPlayerList.pressed)
        {
            NetClientHandler var43 = ((EntityClientPlayerMP)this.mc.thePlayer).sendQueue;
            List var46 = var43.playerNames;
            var44 = var43.currentServerMaxPlayers;
            var42 = var44;

            for (var16 = 1; var42 > 20; var42 = (var44 + var16 - 1) / var16)
            {
                ++var16;
            }

            var17 = 300 / var16;

            if (var17 > 150)
            {
                var17 = 150;
            }

            var18 = (var6 - var16 * var17) / 2;
            byte var50 = 10;
            this.drawRect(var18 - 1, var50 - 1, var18 + var17 * var16, var50 + 9 * var42, Integer.MIN_VALUE);

            for (var20 = 0; var20 < var44; ++var20)
            {
                var51 = var18 + var20 % var16 * var17;
                var22 = var50 + var20 / var16 * 9;
                this.drawRect(var51, var22, var51 + var17 - 1, var22 + 8, 553648127);
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                GL11.glEnable(GL11.GL_ALPHA_TEST);

                if (var20 < var46.size())
                {
                    GuiPlayerInfo var54 = (GuiPlayerInfo)var46.get(var20);
                    var8.drawStringWithShadow(var54.name, var51, var22, 16777215);
                    this.mc.renderEngine.bindTexture(this.mc.renderEngine.getTexture("/gui/icons.png"));
                    boolean var52 = false;
                    boolean var57 = false;
                    byte var53 = 0;
                    var57 = false;
                    byte var58;

                    if (var54.responseTime < 0)
                    {
                        var58 = 5;
                    }
                    else if (var54.responseTime < 150)
                    {
                        var58 = 0;
                    }
                    else if (var54.responseTime < 300)
                    {
                        var58 = 1;
                    }
                    else if (var54.responseTime < 600)
                    {
                        var58 = 2;
                    }
                    else if (var54.responseTime < 1000)
                    {
                        var58 = 3;
                    }
                    else
                    {
                        var58 = 4;
                    }

                    this.zLevel += 100.0F;
                    this.drawTexturedModalRect(var51 + var17 - 12, var22, 0 + var53 * 10, 176 + var58 * 8, 10, 8);
                    this.zLevel -= 100.0F;
                }
            }
        }

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
    }

    /**
     * Renders dragon's (boss) health on the HUD
     */
    private void renderBossHealth()
    {
        if (RenderDragon.entityDragon != null)
        {
            EntityDragon var1 = RenderDragon.entityDragon;
            RenderDragon.entityDragon = null;
            FontRenderer var2 = this.mc.fontRenderer;
            ScaledResolution var3 = new ScaledResolution(this.mc.gameSettings, this.mc.displayWidth, this.mc.displayHeight);
            int var4 = var3.getScaledWidth();
            short var5 = 182;
            int var6 = var4 / 2 - var5 / 2;
            int var7 = (int)((float)var1.func_41010_ax() / (float)var1.getMaxHealth() * (float)(var5 + 1));
            byte var8 = 12;
            this.drawTexturedModalRect(var6, var8, 0, 74, var5, 5);
            this.drawTexturedModalRect(var6, var8, 0, 74, var5, 5);

            if (var7 > 0)
            {
                this.drawTexturedModalRect(var6, var8, 0, 79, var7, 5);
            }

            String var9 = "Boss health";
            var2.drawStringWithShadow(var9, var4 / 2 - var2.getStringWidth(var9) / 2, var8 - 10, 16711935);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.mc.renderEngine.getTexture("/gui/icons.png"));
        }
    }

    private void renderPumpkinBlur(int par1, int par2)
    {
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(false);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.mc.renderEngine.getTexture("%blur%/misc/pumpkinblur.png"));
        Tessellator var3 = Tessellator.instance;
        var3.startDrawingQuads();
        var3.addVertexWithUV(0.0D, (double)par2, -90.0D, 0.0D, 1.0D);
        var3.addVertexWithUV((double)par1, (double)par2, -90.0D, 1.0D, 1.0D);
        var3.addVertexWithUV((double)par1, 0.0D, -90.0D, 1.0D, 0.0D);
        var3.addVertexWithUV(0.0D, 0.0D, -90.0D, 0.0D, 0.0D);
        var3.draw();
        GL11.glDepthMask(true);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    }

    /**
     * Renders the vignette. Args: vignetteBrightness, width, height
     */
    private void renderVignette(float par1, int par2, int par3)
    {
        par1 = 1.0F - par1;

        if (par1 < 0.0F)
        {
            par1 = 0.0F;
        }

        if (par1 > 1.0F)
        {
            par1 = 1.0F;
        }

        this.prevVignetteBrightness = (float)((double)this.prevVignetteBrightness + (double)(par1 - this.prevVignetteBrightness) * 0.01D);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(false);
        GL11.glBlendFunc(GL11.GL_ZERO, GL11.GL_ONE_MINUS_SRC_COLOR);
        GL11.glColor4f(this.prevVignetteBrightness, this.prevVignetteBrightness, this.prevVignetteBrightness, 1.0F);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.mc.renderEngine.getTexture("%blur%/misc/vignette.png"));
        Tessellator var4 = Tessellator.instance;
        var4.startDrawingQuads();
        var4.addVertexWithUV(0.0D, (double)par3, -90.0D, 0.0D, 1.0D);
        var4.addVertexWithUV((double)par2, (double)par3, -90.0D, 1.0D, 1.0D);
        var4.addVertexWithUV((double)par2, 0.0D, -90.0D, 1.0D, 0.0D);
        var4.addVertexWithUV(0.0D, 0.0D, -90.0D, 0.0D, 0.0D);
        var4.draw();
        GL11.glDepthMask(true);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
    }

    /**
     * Renders the portal overlay. Args: portalStrength, width, height
     */
    private void renderPortalOverlay(float par1, int par2, int par3)
    {
        if (par1 < 1.0F)
        {
            par1 *= par1;
            par1 *= par1;
            par1 = par1 * 0.8F + 0.2F;
        }

        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(false);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, par1);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.mc.renderEngine.getTexture("/terrain.png"));
        float var4 = (float)(Block.portal.blockIndexInTexture % 16) / 16.0F;
        float var5 = (float)(Block.portal.blockIndexInTexture / 16) / 16.0F;
        float var6 = (float)(Block.portal.blockIndexInTexture % 16 + 1) / 16.0F;
        float var7 = (float)(Block.portal.blockIndexInTexture / 16 + 1) / 16.0F;
        Tessellator var8 = Tessellator.instance;
        var8.startDrawingQuads();
        var8.addVertexWithUV(0.0D, (double)par3, -90.0D, (double)var4, (double)var7);
        var8.addVertexWithUV((double)par2, (double)par3, -90.0D, (double)var6, (double)var7);
        var8.addVertexWithUV((double)par2, 0.0D, -90.0D, (double)var6, (double)var5);
        var8.addVertexWithUV(0.0D, 0.0D, -90.0D, (double)var4, (double)var5);
        var8.draw();
        GL11.glDepthMask(true);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    }

    /**
     * Renders the specified item of the inventory slot at the specified location. Args: slot, x, y, partialTick
     */
    private void renderInventorySlot(int par1, int par2, int par3, float par4)
    {
        ItemStack var5 = this.mc.thePlayer.inventory.mainInventory[par1];

        if (var5 != null)
        {
            float var6 = (float)var5.animationsToGo - par4;

            if (var6 > 0.0F)
            {
                GL11.glPushMatrix();
                float var7 = 1.0F + var6 / 5.0F;
                GL11.glTranslatef((float)(par2 + 8), (float)(par3 + 12), 0.0F);
                GL11.glScalef(1.0F / var7, (var7 + 1.0F) / 2.0F, 1.0F);
                GL11.glTranslatef((float)(-(par2 + 8)), (float)(-(par3 + 12)), 0.0F);
            }

            itemRenderer.renderItemIntoGUI(this.mc.fontRenderer, this.mc.renderEngine, var5, par2, par3);

            if (var6 > 0.0F)
            {
                GL11.glPopMatrix();
            }

            itemRenderer.renderItemOverlayIntoGUI(this.mc.fontRenderer, this.mc.renderEngine, var5, par2, par3);
        }
    }

    /**
     * The update tick for the ingame UI
     */
    public void updateTick()
    {
        if (this.recordPlayingUpFor > 0)
        {
            --this.recordPlayingUpFor;
        }

        ++this.updateCounter;

        for (int var1 = 0; var1 < this.chatMessageList.size(); ++var1)
        {
            ++((ChatLine)this.chatMessageList.get(var1)).updateCounter;
        }
    }

    /**
     * Clear all chat messages.
     */
    public void clearChatMessages()
    {
        this.chatMessageList.clear();
    }

    /**
     * Adds a chat message to the list of chat messages. Args: msg
     */
    public void addChatMessage(String par1Str)
    {
        while (this.mc.fontRenderer.getStringWidth(par1Str) > 320)
        {
            int var2;

            for (var2 = 1; var2 < par1Str.length() && this.mc.fontRenderer.getStringWidth(par1Str.substring(0, var2 + 1)) <= 320; ++var2)
            {
                ;
            }

            this.addChatMessage(par1Str.substring(0, var2));
            par1Str = par1Str.substring(var2);
        }

        this.chatMessageList.add(0, new ChatLine(par1Str));

        while (this.chatMessageList.size() > 50)
        {
            this.chatMessageList.remove(this.chatMessageList.size() - 1);
        }
    }

    public void setRecordPlayingMessage(String par1Str)
    {
        this.recordPlaying = "Now playing: " + par1Str;
        this.recordPlayingUpFor = 60;
        this.recordIsPlaying = true;
    }

    /**
     * Adds the string to chat message after translate it with the language file.
     */
    public void addChatMessageTranslate(String par1Str)
    {
        StringTranslate var2 = StringTranslate.getInstance();
        String var3 = var2.translateKey(par1Str);
        this.addChatMessage(var3);
    }
}
