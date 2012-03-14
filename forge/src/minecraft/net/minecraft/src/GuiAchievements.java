package net.minecraft.src;

import java.util.Random;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class GuiAchievements extends GuiScreen
{
    /** The top x coordinate of the achievement map */
    private static final int guiMapTop = AchievementList.minDisplayColumn * 24 - 112;

    /** The left y coordinate of the achievement map */
    private static final int guiMapLeft = AchievementList.minDisplayRow * 24 - 112;

    /** The bottom x coordinate of the achievement map */
    private static final int guiMapBottom = AchievementList.maxDisplayColumn * 24 - 77;

    /** The right y coordinate of the achievement map */
    private static final int guiMapRight = AchievementList.maxDisplayRow * 24 - 77;
    protected int achievementsPaneWidth = 256;
    protected int achievementsPaneHeight = 202;

    /** The current mouse x coordinate */
    protected int mouseX = 0;

    /** The current mouse y coordinate */
    protected int mouseY = 0;
    protected double field_27116_m;
    protected double field_27115_n;

    /** The x position of the achievement map */
    protected double guiMapX;

    /** The y position of the achievement map */
    protected double guiMapY;
    protected double field_27112_q;
    protected double field_27111_r;

    /** Whether the Mouse Button is down or not */
    private int isMouseButtonDown = 0;
    private StatFileWriter statFileWriter;

    public GuiAchievements(StatFileWriter par1StatFileWriter)
    {
        this.statFileWriter = par1StatFileWriter;
        short var2 = 141;
        short var3 = 141;
        this.field_27116_m = this.guiMapX = this.field_27112_q = (double)(AchievementList.openInventory.displayColumn * 24 - var2 / 2 - 12);
        this.field_27115_n = this.guiMapY = this.field_27111_r = (double)(AchievementList.openInventory.displayRow * 24 - var3 / 2);
    }

    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    public void initGui()
    {
        this.controlList.clear();
        this.controlList.add(new GuiSmallButton(1, this.width / 2 + 24, this.height / 2 + 74, 80, 20, StatCollector.translateToLocal("gui.done")));
    }

    /**
     * Fired when a control is clicked. This is the equivalent of ActionListener.actionPerformed(ActionEvent e).
     */
    protected void actionPerformed(GuiButton par1GuiButton)
    {
        if (par1GuiButton.id == 1)
        {
            this.mc.displayGuiScreen((GuiScreen)null);
            this.mc.setIngameFocus();
        }

        super.actionPerformed(par1GuiButton);
    }

    /**
     * Fired when a key is typed. This is the equivalent of KeyListener.keyTyped(KeyEvent e).
     */
    protected void keyTyped(char par1, int par2)
    {
        if (par2 == this.mc.gameSettings.keyBindInventory.keyCode)
        {
            this.mc.displayGuiScreen((GuiScreen)null);
            this.mc.setIngameFocus();
        }
        else
        {
            super.keyTyped(par1, par2);
        }
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(int par1, int par2, float par3)
    {
        if (Mouse.isButtonDown(0))
        {
            int var4 = (this.width - this.achievementsPaneWidth) / 2;
            int var5 = (this.height - this.achievementsPaneHeight) / 2;
            int var6 = var4 + 8;
            int var7 = var5 + 17;

            if ((this.isMouseButtonDown == 0 || this.isMouseButtonDown == 1) && par1 >= var6 && par1 < var6 + 224 && par2 >= var7 && par2 < var7 + 155)
            {
                if (this.isMouseButtonDown == 0)
                {
                    this.isMouseButtonDown = 1;
                }
                else
                {
                    this.guiMapX -= (double)(par1 - this.mouseX);
                    this.guiMapY -= (double)(par2 - this.mouseY);
                    this.field_27112_q = this.field_27116_m = this.guiMapX;
                    this.field_27111_r = this.field_27115_n = this.guiMapY;
                }

                this.mouseX = par1;
                this.mouseY = par2;
            }

            if (this.field_27112_q < (double)guiMapTop)
            {
                this.field_27112_q = (double)guiMapTop;
            }

            if (this.field_27111_r < (double)guiMapLeft)
            {
                this.field_27111_r = (double)guiMapLeft;
            }

            if (this.field_27112_q >= (double)guiMapBottom)
            {
                this.field_27112_q = (double)(guiMapBottom - 1);
            }

            if (this.field_27111_r >= (double)guiMapRight)
            {
                this.field_27111_r = (double)(guiMapRight - 1);
            }
        }
        else
        {
            this.isMouseButtonDown = 0;
        }

        this.drawDefaultBackground();
        this.genAchievementBackground(par1, par2, par3);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        this.func_27110_k();
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
    }

    /**
     * Called from the main game loop to update the screen.
     */
    public void updateScreen()
    {
        this.field_27116_m = this.guiMapX;
        this.field_27115_n = this.guiMapY;
        double var1 = this.field_27112_q - this.guiMapX;
        double var3 = this.field_27111_r - this.guiMapY;

        if (var1 * var1 + var3 * var3 < 4.0D)
        {
            this.guiMapX += var1;
            this.guiMapY += var3;
        }
        else
        {
            this.guiMapX += var1 * 0.85D;
            this.guiMapY += var3 * 0.85D;
        }
    }

    protected void func_27110_k()
    {
        int var1 = (this.width - this.achievementsPaneWidth) / 2;
        int var2 = (this.height - this.achievementsPaneHeight) / 2;
        this.fontRenderer.drawString("Achievements", var1 + 15, var2 + 5, 4210752);
    }

    protected void genAchievementBackground(int par1, int par2, float par3)
    {
        int var4 = MathHelper.floor_double(this.field_27116_m + (this.guiMapX - this.field_27116_m) * (double)par3);
        int var5 = MathHelper.floor_double(this.field_27115_n + (this.guiMapY - this.field_27115_n) * (double)par3);

        if (var4 < guiMapTop)
        {
            var4 = guiMapTop;
        }

        if (var5 < guiMapLeft)
        {
            var5 = guiMapLeft;
        }

        if (var4 >= guiMapBottom)
        {
            var4 = guiMapBottom - 1;
        }

        if (var5 >= guiMapRight)
        {
            var5 = guiMapRight - 1;
        }

        int var6 = this.mc.renderEngine.getTexture("/terrain.png");
        int var7 = this.mc.renderEngine.getTexture("/achievement/bg.png");
        int var8 = (this.width - this.achievementsPaneWidth) / 2;
        int var9 = (this.height - this.achievementsPaneHeight) / 2;
        int var10 = var8 + 16;
        int var11 = var9 + 17;
        this.zLevel = 0.0F;
        GL11.glDepthFunc(GL11.GL_GEQUAL);
        GL11.glPushMatrix();
        GL11.glTranslatef(0.0F, 0.0F, -200.0F);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        GL11.glEnable(GL11.GL_COLOR_MATERIAL);
        this.mc.renderEngine.bindTexture(var6);
        int var12 = var4 + 288 >> 4;
        int var13 = var5 + 288 >> 4;
        int var14 = (var4 + 288) % 16;
        int var15 = (var5 + 288) % 16;
        Random var21 = new Random();

        for (int var22 = 0; var22 * 16 - var15 < 155; ++var22)
        {
            float var23 = 0.6F - (float)(var13 + var22) / 25.0F * 0.3F;
            GL11.glColor4f(var23, var23, var23, 1.0F);

            for (int var24 = 0; var24 * 16 - var14 < 224; ++var24)
            {
                var21.setSeed((long)(1234 + var12 + var24));
                var21.nextInt();
                int var25 = var21.nextInt(1 + var13 + var22) + (var13 + var22) / 2;
                int var26 = Block.sand.blockIndexInTexture;

                if (var25 <= 37 && var13 + var22 != 35)
                {
                    if (var25 == 22)
                    {
                        if (var21.nextInt(2) == 0)
                        {
                            var26 = Block.oreDiamond.blockIndexInTexture;
                        }
                        else
                        {
                            var26 = Block.oreRedstone.blockIndexInTexture;
                        }
                    }
                    else if (var25 == 10)
                    {
                        var26 = Block.oreIron.blockIndexInTexture;
                    }
                    else if (var25 == 8)
                    {
                        var26 = Block.oreCoal.blockIndexInTexture;
                    }
                    else if (var25 > 4)
                    {
                        var26 = Block.stone.blockIndexInTexture;
                    }
                    else if (var25 > 0)
                    {
                        var26 = Block.dirt.blockIndexInTexture;
                    }
                }
                else
                {
                    var26 = Block.bedrock.blockIndexInTexture;
                }

                this.drawTexturedModalRect(var10 + var24 * 16 - var14, var11 + var22 * 16 - var15, var26 % 16 << 4, var26 >> 4 << 4, 16, 16);
            }
        }

        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthFunc(GL11.GL_LEQUAL);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        int var17;
        int var16;
        int var34;
        int var38;

        for (var12 = 0; var12 < AchievementList.achievementList.size(); ++var12)
        {
            Achievement var29 = (Achievement)AchievementList.achievementList.get(var12);

            if (var29.parentAchievement != null)
            {
                var14 = var29.displayColumn * 24 - var4 + 11 + var10;
                var15 = var29.displayRow * 24 - var5 + 11 + var11;
                var16 = var29.parentAchievement.displayColumn * 24 - var4 + 11 + var10;
                var17 = var29.parentAchievement.displayRow * 24 - var5 + 11 + var11;
                boolean var18 = false;
                boolean var19 = this.statFileWriter.hasAchievementUnlocked(var29);
                boolean var20 = this.statFileWriter.canUnlockAchievement(var29);
                var38 = Math.sin((double)(System.currentTimeMillis() % 600L) / 600.0D * Math.PI * 2.0D) > 0.6D ? 255 : 130;

                if (var19)
                {
                    var34 = -9408400;
                }
                else if (var20)
                {
                    var34 = 65280 + (var38 << 24);
                }
                else
                {
                    var34 = -16777216;
                }

                this.drawHorizontalLine(var14, var16, var15, var34);
                this.drawVerticalLine(var16, var15, var17, var34);
            }
        }

        Achievement var28 = null;
        RenderItem var27 = new RenderItem();
        RenderHelper.enableGUIStandardItemLighting();
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        GL11.glEnable(GL11.GL_COLOR_MATERIAL);
        int var33;

        for (var14 = 0; var14 < AchievementList.achievementList.size(); ++var14)
        {
            Achievement var32 = (Achievement)AchievementList.achievementList.get(var14);
            var16 = var32.displayColumn * 24 - var4;
            var17 = var32.displayRow * 24 - var5;

            if (var16 >= -24 && var17 >= -24 && var16 <= 224 && var17 <= 155)
            {
                float var37;

                if (this.statFileWriter.hasAchievementUnlocked(var32))
                {
                    var37 = 1.0F;
                    GL11.glColor4f(var37, var37, var37, 1.0F);
                }
                else if (this.statFileWriter.canUnlockAchievement(var32))
                {
                    var37 = Math.sin((double)(System.currentTimeMillis() % 600L) / 600.0D * Math.PI * 2.0D) < 0.6D ? 0.6F : 0.8F;
                    GL11.glColor4f(var37, var37, var37, 1.0F);
                }
                else
                {
                    var37 = 0.3F;
                    GL11.glColor4f(var37, var37, var37, 1.0F);
                }

                this.mc.renderEngine.bindTexture(var7);
                var34 = var10 + var16;
                var33 = var11 + var17;

                if (var32.getSpecial())
                {
                    this.drawTexturedModalRect(var34 - 2, var33 - 2, 26, 202, 26, 26);
                }
                else
                {
                    this.drawTexturedModalRect(var34 - 2, var33 - 2, 0, 202, 26, 26);
                }

                if (!this.statFileWriter.canUnlockAchievement(var32))
                {
                    float var36 = 0.1F;
                    GL11.glColor4f(var36, var36, var36, 1.0F);
                    var27.field_27004_a = false;
                }

                GL11.glEnable(GL11.GL_LIGHTING);
                GL11.glEnable(GL11.GL_CULL_FACE);
                var27.renderItemIntoGUI(this.mc.fontRenderer, this.mc.renderEngine, var32.theItemStack, var34 + 3, var33 + 3);
                GL11.glDisable(GL11.GL_LIGHTING);

                if (!this.statFileWriter.canUnlockAchievement(var32))
                {
                    var27.field_27004_a = true;
                }

                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

                if (par1 >= var10 && par2 >= var11 && par1 < var10 + 224 && par2 < var11 + 155 && par1 >= var34 && par1 <= var34 + 22 && par2 >= var33 && par2 <= var33 + 22)
                {
                    var28 = var32;
                }
            }
        }

        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.renderEngine.bindTexture(var7);
        this.drawTexturedModalRect(var8, var9, 0, 0, this.achievementsPaneWidth, this.achievementsPaneHeight);
        GL11.glPopMatrix();
        this.zLevel = 0.0F;
        GL11.glDepthFunc(GL11.GL_LEQUAL);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        super.drawScreen(par1, par2, par3);

        if (var28 != null)
        {
            String var31 = StatCollector.translateToLocal(var28.getName());
            String var30 = var28.getDescription();
            var17 = par1 + 12;
            var34 = par2 - 4;

            if (this.statFileWriter.canUnlockAchievement(var28))
            {
                var33 = Math.max(this.fontRenderer.getStringWidth(var31), 120);
                int var35 = this.fontRenderer.splitStringWidth(var30, var33);

                if (this.statFileWriter.hasAchievementUnlocked(var28))
                {
                    var35 += 12;
                }

                this.drawGradientRect(var17 - 3, var34 - 3, var17 + var33 + 3, var34 + var35 + 3 + 12, -1073741824, -1073741824);
                this.fontRenderer.drawSplitString(var30, var17, var34 + 12, var33, -6250336);

                if (this.statFileWriter.hasAchievementUnlocked(var28))
                {
                    this.fontRenderer.drawStringWithShadow(StatCollector.translateToLocal("achievement.taken"), var17, var34 + var35 + 4, -7302913);
                }
            }
            else
            {
                var33 = Math.max(this.fontRenderer.getStringWidth(var31), 120);
                String var39 = StatCollector.translateToLocalFormatted("achievement.requires", new Object[] {StatCollector.translateToLocal(var28.parentAchievement.getName())});
                var38 = this.fontRenderer.splitStringWidth(var39, var33);
                this.drawGradientRect(var17 - 3, var34 - 3, var17 + var33 + 3, var34 + var38 + 12 + 3, -1073741824, -1073741824);
                this.fontRenderer.drawSplitString(var39, var17, var34 + 12, var33, -9416624);
            }

            this.fontRenderer.drawStringWithShadow(var31, var17, var34, this.statFileWriter.canUnlockAchievement(var28) ? (var28.getSpecial() ? -128 : -1) : (var28.getSpecial() ? -8355776 : -8355712));
        }

        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_LIGHTING);
        RenderHelper.disableStandardItemLighting();
    }

    /**
     * Returns true if this GUI should pause the game when it is displayed in single-player
     */
    public boolean doesGuiPauseGame()
    {
        return true;
    }
}
