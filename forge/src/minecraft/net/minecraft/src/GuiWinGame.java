package net.minecraft.src;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.lwjgl.opengl.GL11;

public class GuiWinGame extends GuiScreen
{
    /** Counts the number of screen updates. */
    private int updateCounter = 0;
    private List field_41044_b;
    private int[] field_41045_c;
    private int field_41042_d = 0;
    private float field_41043_e = 0.5F;

    /**
     * Called from the main game loop to update the screen.
     */
    public void updateScreen()
    {
        ++this.updateCounter;
        float var1 = (float)(this.field_41042_d + this.height + this.height + 24) / this.field_41043_e;

        if ((float)this.updateCounter > var1)
        {
            this.func_41041_e();
        }
    }

    /**
     * Fired when a key is typed. This is the equivalent of KeyListener.keyTyped(KeyEvent e).
     */
    protected void keyTyped(char par1, int par2)
    {
        if (par2 == 1)
        {
            this.func_41041_e();
        }
    }

    private void func_41041_e()
    {
        if (this.mc.theWorld.isRemote)
        {
            EntityClientPlayerMP var1 = (EntityClientPlayerMP)this.mc.thePlayer;
            var1.sendInventoryChanged();
            var1.sendQueue.addToSendQueue(new Packet9Respawn(var1.dimension, (byte)this.mc.theWorld.difficultySetting, this.mc.theWorld.getWorldInfo().getTerrainType(), this.mc.theWorld.getWorldHeight(), 0));
        }
        else
        {
            this.mc.displayGuiScreen((GuiScreen)null);
            this.mc.respawn(this.mc.theWorld.isRemote, 0, true);
        }
    }

    /**
     * Returns true if this GUI should pause the game when it is displayed in single-player
     */
    public boolean doesGuiPauseGame()
    {
        return true;
    }

    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    public void initGui()
    {
        if (this.field_41044_b == null)
        {
            this.field_41044_b = new ArrayList();

            try
            {
                String var1 = "";
                String var2 = "\u00a7f\u00a7k\u00a7a\u00a7b";
                BufferedReader var3 = new BufferedReader(new InputStreamReader(GuiWinGame.class.getResourceAsStream("/title/win.txt"), Charset.forName("UTF-8")));
                int var5;

                for (Random var4 = new Random(8124371L); (var1 = var3.readLine()) != null; this.field_41044_b.add(var1))
                {
                    String var6;
                    String var7;

                    for (var1 = var1.replaceAll("PLAYERNAME", this.mc.session.username); var1.indexOf(var2) >= 0; var1 = var6 + "\u00a7f\u00a7k" + "XXXXXXXX".substring(0, var4.nextInt(4) + 3) + var7)
                    {
                        var5 = var1.indexOf(var2);
                        var6 = var1.substring(0, var5);
                        var7 = var1.substring(var5 + var2.length());
                    }

                    if (var1.length() == 0)
                    {
                        this.field_41044_b.add(var1);
                    }
                }

                for (var5 = 0; var5 < 16; ++var5)
                {
                    this.field_41044_b.add("");
                }

                for (var3 = new BufferedReader(new InputStreamReader(GuiWinGame.class.getResourceAsStream("/title/credits.txt"), Charset.forName("UTF-8"))); (var1 = var3.readLine()) != null; this.field_41044_b.add(var1))
                {
                    var1 = var1.replaceAll("PLAYERNAME", this.mc.session.username);
                    var1 = var1.replaceAll("\t", "    ");

                    if (var1.length() == 0)
                    {
                        this.field_41044_b.add(var1);
                    }
                }

                this.field_41045_c = new int[this.field_41044_b.size()];
                short var9 = 274;
                this.fontRenderer.FONT_HEIGHT = 12;

                for (int var10 = 0; var10 < this.field_41044_b.size(); ++var10)
                {
                    this.field_41042_d += this.field_41045_c[var10] = this.fontRenderer.splitStringWidth((String)this.field_41044_b.get(var10), var9);
                }

                this.fontRenderer.FONT_HEIGHT = 8;
            }
            catch (Exception var8)
            {
                var8.printStackTrace();
            }
        }
    }

    /**
     * Fired when a control is clicked. This is the equivalent of ActionListener.actionPerformed(ActionEvent e).
     */
    protected void actionPerformed(GuiButton par1GuiButton) {}

    private void func_41040_b(int par1, int par2, float par3)
    {
        Tessellator var4 = Tessellator.instance;
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.mc.renderEngine.getTexture("%blur%/gui/background.png"));
        var4.startDrawingQuads();
        var4.setColorRGBA_F(1.0F, 1.0F, 1.0F, 1.0F);
        int var5 = this.width;
        float var6 = 0.0F - ((float)this.updateCounter + par3) * 0.5F * this.field_41043_e;
        float var7 = (float)this.height - ((float)this.updateCounter + par3) * 0.5F * this.field_41043_e;
        float var8 = 0.015625F;
        float var9 = ((float)this.updateCounter + par3 - 0.0F) * 0.02F;
        float var10 = (float)(this.field_41042_d + this.height + this.height + 24) / this.field_41043_e;
        float var11 = (var10 - 20.0F - ((float)this.updateCounter + par3)) * 0.005F;

        if (var11 < var9)
        {
            var9 = var11;
        }

        if (var9 > 1.0F)
        {
            var9 = 1.0F;
        }

        var9 *= var9;
        var9 = var9 * 96.0F / 255.0F;
        var4.setColorOpaque_F(var9, var9, var9);
        var4.addVertexWithUV(0.0D, (double)this.height, (double)this.zLevel, 0.0D, (double)(var6 * var8));
        var4.addVertexWithUV((double)var5, (double)this.height, (double)this.zLevel, (double)((float)var5 * var8), (double)(var6 * var8));
        var4.addVertexWithUV((double)var5, 0.0D, (double)this.zLevel, (double)((float)var5 * var8), (double)(var7 * var8));
        var4.addVertexWithUV(0.0D, 0.0D, (double)this.zLevel, 0.0D, (double)(var7 * var8));
        var4.draw();
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(int par1, int par2, float par3)
    {
        this.func_41040_b(par1, par2, par3);
        Tessellator var4 = Tessellator.instance;
        short var5 = 274;
        int var6 = this.width / 2 - var5 / 2;
        int var7 = this.height + 50;
        float var8 = -((float)this.updateCounter + par3) * this.field_41043_e;
        GL11.glPushMatrix();
        GL11.glTranslatef(0.0F, var8, 0.0F);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.mc.renderEngine.getTexture("/title/mclogo.png"));
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.drawTexturedModalRect(var6 + 0, var7 + 0, 0, 0, 155, 44);
        this.drawTexturedModalRect(var6 + 155, var7 + 0, 0, 45, 155, 44);
        var4.setColorOpaque_I(16777215);
        this.fontRenderer.FONT_HEIGHT = 12;
        int var9 = var7 + 200;
        int var10;
        int var11;

        for (var10 = 0; var10 < this.field_41044_b.size(); ++var10)
        {
            var11 = this.field_41045_c[var10];

            if (var10 == this.field_41044_b.size() - 1)
            {
                float var12 = (float)var9 + var8 - (float)(this.height / 2 - var11 / 2);

                if (var12 < 0.0F)
                {
                    GL11.glTranslatef(0.0F, -var12, 0.0F);
                }
            }

            if ((float)var9 + var8 + (float)var11 + 8.0F > 0.0F && (float)var9 + var8 < (float)this.height)
            {
                String var13 = (String)this.field_41044_b.get(var10);

                if (var13.startsWith("[C]"))
                {
                    this.fontRenderer.drawStringWithShadow(var13.substring(3), var6 + (var5 - this.fontRenderer.getStringWidth(var13.substring(3))) / 2, var9, 16777215);
                }
                else
                {
                    this.fontRenderer.fontRandom.setSeed((long)var10 * 4238972211L + (long)(this.updateCounter / 4));
                    this.fontRenderer.drawSplitString(var13, var6 + 1, var9 + 1, var5, 16777215, true);
                    this.fontRenderer.fontRandom.setSeed((long)var10 * 4238972211L + (long)(this.updateCounter / 4));
                    this.fontRenderer.drawSplitString(var13, var6, var9, var5, 16777215, false);
                }
            }

            var9 += var11;
        }

        this.fontRenderer.FONT_HEIGHT = 8;
        GL11.glPopMatrix();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.mc.renderEngine.getTexture("%blur%/misc/vignette.png"));
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_ZERO, GL11.GL_ONE_MINUS_SRC_COLOR);
        var4.startDrawingQuads();
        var4.setColorRGBA_F(1.0F, 1.0F, 1.0F, 1.0F);
        var10 = this.width;
        var11 = this.height;
        var4.addVertexWithUV(0.0D, (double)var11, (double)this.zLevel, 0.0D, 1.0D);
        var4.addVertexWithUV((double)var10, (double)var11, (double)this.zLevel, 1.0D, 1.0D);
        var4.addVertexWithUV((double)var10, 0.0D, (double)this.zLevel, 1.0D, 0.0D);
        var4.addVertexWithUV(0.0D, 0.0D, (double)this.zLevel, 0.0D, 0.0D);
        var4.draw();
        GL11.glDisable(GL11.GL_BLEND);
        super.drawScreen(par1, par2, par3);
    }
}
