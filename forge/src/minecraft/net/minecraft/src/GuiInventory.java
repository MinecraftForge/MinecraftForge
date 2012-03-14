package net.minecraft.src;

import java.util.Collection;
import java.util.Iterator;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class GuiInventory extends GuiContainer
{
    /**
     * x size of the inventory window in pixels. Defined as float, passed as int
     */
    private float xSize_lo;

    /**
     * y size of the inventory window in pixels. Defined as float, passed as int.
     */
    private float ySize_lo;

    public GuiInventory(EntityPlayer par1EntityPlayer)
    {
        super(par1EntityPlayer.inventorySlots);
        this.allowUserInput = true;
        par1EntityPlayer.addStat(AchievementList.openInventory, 1);
    }

    /**
     * Called from the main game loop to update the screen.
     */
    public void updateScreen()
    {
        if (this.mc.playerController.isInCreativeMode())
        {
            this.mc.displayGuiScreen(new GuiContainerCreative(this.mc.thePlayer));
        }
    }

    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    public void initGui()
    {
        this.controlList.clear();

        if (this.mc.playerController.isInCreativeMode())
        {
            this.mc.displayGuiScreen(new GuiContainerCreative(this.mc.thePlayer));
        }
        else
        {
            super.initGui();

            if (!this.mc.thePlayer.getActivePotionEffects().isEmpty())
            {
                this.guiLeft = 160 + (this.width - this.xSize - 200) / 2;
            }
        }
    }

    /**
     * Draw the foreground layer for the GuiContainer (everythin in front of the items)
     */
    protected void drawGuiContainerForegroundLayer()
    {
        this.fontRenderer.drawString(StatCollector.translateToLocal("container.crafting"), 86, 16, 4210752);
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(int par1, int par2, float par3)
    {
        super.drawScreen(par1, par2, par3);
        this.xSize_lo = (float)par1;
        this.ySize_lo = (float)par2;
    }

    /**
     * Draw the background layer for the GuiContainer (everything behind the items)
     */
    protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3)
    {
        int var4 = this.mc.renderEngine.getTexture("/gui/inventory.png");
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.renderEngine.bindTexture(var4);
        int var5 = this.guiLeft;
        int var6 = this.guiTop;
        this.drawTexturedModalRect(var5, var6, 0, 0, this.xSize, this.ySize);
        this.func_40218_g();
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        GL11.glEnable(GL11.GL_COLOR_MATERIAL);
        GL11.glPushMatrix();
        GL11.glTranslatef((float)(var5 + 51), (float)(var6 + 75), 50.0F);
        float var7 = 30.0F;
        GL11.glScalef(-var7, var7, var7);
        GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
        float var8 = this.mc.thePlayer.renderYawOffset;
        float var9 = this.mc.thePlayer.rotationYaw;
        float var10 = this.mc.thePlayer.rotationPitch;
        float var11 = (float)(var5 + 51) - this.xSize_lo;
        float var12 = (float)(var6 + 75 - 50) - this.ySize_lo;
        GL11.glRotatef(135.0F, 0.0F, 1.0F, 0.0F);
        RenderHelper.enableStandardItemLighting();
        GL11.glRotatef(-135.0F, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(-((float)Math.atan((double)(var12 / 40.0F))) * 20.0F, 1.0F, 0.0F, 0.0F);
        this.mc.thePlayer.renderYawOffset = (float)Math.atan((double)(var11 / 40.0F)) * 20.0F;
        this.mc.thePlayer.rotationYaw = (float)Math.atan((double)(var11 / 40.0F)) * 40.0F;
        this.mc.thePlayer.rotationPitch = -((float)Math.atan((double)(var12 / 40.0F))) * 20.0F;
        this.mc.thePlayer.prevRotationYaw2 = this.mc.thePlayer.rotationYaw;
        GL11.glTranslatef(0.0F, this.mc.thePlayer.yOffset, 0.0F);
        RenderManager.instance.playerViewY = 180.0F;
        RenderManager.instance.renderEntityWithPosYaw(this.mc.thePlayer, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F);
        this.mc.thePlayer.renderYawOffset = var8;
        this.mc.thePlayer.rotationYaw = var9;
        this.mc.thePlayer.rotationPitch = var10;
        GL11.glPopMatrix();
        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
    }

    /**
     * Fired when a control is clicked. This is the equivalent of ActionListener.actionPerformed(ActionEvent e).
     */
    protected void actionPerformed(GuiButton par1GuiButton)
    {
        if (par1GuiButton.id == 0)
        {
            this.mc.displayGuiScreen(new GuiAchievements(this.mc.statFileWriter));
        }

        if (par1GuiButton.id == 1)
        {
            this.mc.displayGuiScreen(new GuiStats(this, this.mc.statFileWriter));
        }
    }

    private void func_40218_g()
    {
        int var1 = this.guiLeft - 124;
        int var2 = this.guiTop;
        int var3 = this.mc.renderEngine.getTexture("/gui/inventory.png");
        Collection var4 = this.mc.thePlayer.getActivePotionEffects();

        if (!var4.isEmpty())
        {
            int var5 = 33;

            if (var4.size() > 5)
            {
                var5 = 132 / (var4.size() - 1);
            }

            for (Iterator var6 = this.mc.thePlayer.getActivePotionEffects().iterator(); var6.hasNext(); var2 += var5)
            {
                PotionEffect var7 = (PotionEffect)var6.next();
                Potion var8 = Potion.potionTypes[var7.getPotionID()];
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                this.mc.renderEngine.bindTexture(var3);
                this.drawTexturedModalRect(var1, var2, 0, this.ySize, 140, 32);

                if (var8.hasStatusIcon())
                {
                    int var9 = var8.getStatusIconIndex();
                    this.drawTexturedModalRect(var1 + 6, var2 + 7, 0 + var9 % 8 * 18, this.ySize + 32 + var9 / 8 * 18, 18, 18);
                }

                String var11 = StatCollector.translateToLocal(var8.getName());

                if (var7.getAmplifier() == 1)
                {
                    var11 = var11 + " II";
                }
                else if (var7.getAmplifier() == 2)
                {
                    var11 = var11 + " III";
                }
                else if (var7.getAmplifier() == 3)
                {
                    var11 = var11 + " IV";
                }

                this.fontRenderer.drawStringWithShadow(var11, var1 + 10 + 18, var2 + 6, 16777215);
                String var10 = Potion.getDurationString(var7);
                this.fontRenderer.drawStringWithShadow(var10, var1 + 10 + 18, var2 + 6 + 10, 8355711);
            }
        }
    }
}
