package net.minecraft.src;

import java.util.Random;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.util.glu.GLU;

public class GuiEnchantment extends GuiContainer
{
    private static ModelBook field_40220_w = new ModelBook();
    private Random field_40230_x = new Random();

    /** ContainerEnchantment object associated with this gui */
    private ContainerEnchantment containerEnchantment;
    public int field_40227_h;
    public float field_40229_i;
    public float field_40225_j;
    public float field_40226_k;
    public float field_40223_l;
    public float field_40224_m;
    public float field_40221_n;
    ItemStack field_40222_o;

    public GuiEnchantment(InventoryPlayer par1InventoryPlayer, World par2World, int par3, int par4, int par5)
    {
        super(new ContainerEnchantment(par1InventoryPlayer, par2World, par3, par4, par5));
        this.containerEnchantment = (ContainerEnchantment)this.inventorySlots;
    }

    /**
     * Called when the screen is unloaded. Used to disable keyboard repeat events
     */
    public void onGuiClosed()
    {
        super.onGuiClosed();
    }

    /**
     * Draw the foreground layer for the GuiContainer (everythin in front of the items)
     */
    protected void drawGuiContainerForegroundLayer()
    {
        this.fontRenderer.drawString(StatCollector.translateToLocal("container.enchant"), 12, 6, 4210752);
        this.fontRenderer.drawString(StatCollector.translateToLocal("container.inventory"), 8, this.ySize - 96 + 2, 4210752);
    }

    /**
     * Called from the main game loop to update the screen.
     */
    public void updateScreen()
    {
        super.updateScreen();
        this.func_40219_x_();
    }

    /**
     * Called when the mouse is clicked.
     */
    protected void mouseClicked(int par1, int par2, int par3)
    {
        super.mouseClicked(par1, par2, par3);
        int var4 = (this.width - this.xSize) / 2;
        int var5 = (this.height - this.ySize) / 2;

        for (int var6 = 0; var6 < 3; ++var6)
        {
            int var7 = par1 - (var4 + 60);
            int var8 = par2 - (var5 + 14 + 19 * var6);

            if (var7 >= 0 && var8 >= 0 && var7 < 108 && var8 < 19 && this.containerEnchantment.enchantItem(this.mc.thePlayer, var6))
            {
                this.mc.playerController.func_40593_a(this.containerEnchantment.windowId, var6);
            }
        }
    }

    /**
     * Draw the background layer for the GuiContainer (everything behind the items)
     */
    protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3)
    {
        int var4 = this.mc.renderEngine.getTexture("/gui/enchant.png");
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.renderEngine.bindTexture(var4);
        int var5 = (this.width - this.xSize) / 2;
        int var6 = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(var5, var6, 0, 0, this.xSize, this.ySize);
        GL11.glPushMatrix();
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        ScaledResolution var7 = new ScaledResolution(this.mc.gameSettings, this.mc.displayWidth, this.mc.displayHeight);
        GL11.glViewport((var7.getScaledWidth() - 320) / 2 * var7.scaleFactor, (var7.getScaledHeight() - 240) / 2 * var7.scaleFactor, 320 * var7.scaleFactor, 240 * var7.scaleFactor);
        GL11.glTranslatef(-0.34F, 0.23F, 0.0F);
        GLU.gluPerspective(90.0F, 1.3333334F, 9.0F, 80.0F);
        float var8 = 1.0F;
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glLoadIdentity();
        RenderHelper.enableStandardItemLighting();
        GL11.glTranslatef(0.0F, 3.3F, -16.0F);
        GL11.glScalef(var8, var8, var8);
        float var9 = 5.0F;
        GL11.glScalef(var9, var9, var9);
        GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
        this.mc.renderEngine.bindTexture(this.mc.renderEngine.getTexture("/item/book.png"));
        GL11.glRotatef(20.0F, 1.0F, 0.0F, 0.0F);
        float var10 = this.field_40221_n + (this.field_40224_m - this.field_40221_n) * par1;
        GL11.glTranslatef((1.0F - var10) * 0.2F, (1.0F - var10) * 0.1F, (1.0F - var10) * 0.25F);
        GL11.glRotatef(-(1.0F - var10) * 90.0F - 90.0F, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
        float var11 = this.field_40225_j + (this.field_40229_i - this.field_40225_j) * par1 + 0.25F;
        float var12 = this.field_40225_j + (this.field_40229_i - this.field_40225_j) * par1 + 0.75F;
        var11 = (var11 - (float)MathHelper.func_40346_b((double)var11)) * 1.6F - 0.3F;
        var12 = (var12 - (float)MathHelper.func_40346_b((double)var12)) * 1.6F - 0.3F;

        if (var11 < 0.0F)
        {
            var11 = 0.0F;
        }

        if (var12 < 0.0F)
        {
            var12 = 0.0F;
        }

        if (var11 > 1.0F)
        {
            var11 = 1.0F;
        }

        if (var12 > 1.0F)
        {
            var12 = 1.0F;
        }

        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        field_40220_w.render((Entity)null, 0.0F, var11, var12, var10, 0.0F, 0.0625F);
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        RenderHelper.disableStandardItemLighting();
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glViewport(0, 0, this.mc.displayWidth, this.mc.displayHeight);
        GL11.glPopMatrix();
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glPopMatrix();
        RenderHelper.disableStandardItemLighting();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.renderEngine.bindTexture(var4);
        EnchantmentNameParts.field_40253_a.func_40250_a(this.containerEnchantment.nameSeed);

        for (int var13 = 0; var13 < 3; ++var13)
        {
            String var14 = EnchantmentNameParts.field_40253_a.func_40249_a();
            this.zLevel = 0.0F;
            this.mc.renderEngine.bindTexture(var4);
            int var15 = this.containerEnchantment.enchantLevels[var13];
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

            if (var15 == 0)
            {
                this.drawTexturedModalRect(var5 + 60, var6 + 14 + 19 * var13, 0, 185, 108, 19);
            }
            else
            {
                String var16 = "" + var15;
                FontRenderer var17 = this.mc.standardGalacticFontRenderer;
                int var18 = 6839882;

                if (this.mc.thePlayer.experienceLevel < var15 && !this.mc.thePlayer.capabilities.depleteBuckets)
                {
                    this.drawTexturedModalRect(var5 + 60, var6 + 14 + 19 * var13, 0, 185, 108, 19);
                    var17.drawSplitString(var14, var5 + 62, var6 + 16 + 19 * var13, 104, (var18 & 16711422) >> 1);
                    var17 = this.mc.fontRenderer;
                    var18 = 4226832;
                    var17.drawStringWithShadow(var16, var5 + 62 + 104 - var17.getStringWidth(var16), var6 + 16 + 19 * var13 + 7, var18);
                }
                else
                {
                    int var19 = par2 - (var5 + 60);
                    int var20 = par3 - (var6 + 14 + 19 * var13);

                    if (var19 >= 0 && var20 >= 0 && var19 < 108 && var20 < 19)
                    {
                        this.drawTexturedModalRect(var5 + 60, var6 + 14 + 19 * var13, 0, 204, 108, 19);
                        var18 = 16777088;
                    }
                    else
                    {
                        this.drawTexturedModalRect(var5 + 60, var6 + 14 + 19 * var13, 0, 166, 108, 19);
                    }

                    var17.drawSplitString(var14, var5 + 62, var6 + 16 + 19 * var13, 104, var18);
                    var17 = this.mc.fontRenderer;
                    var18 = 8453920;
                    var17.drawStringWithShadow(var16, var5 + 62 + 104 - var17.getStringWidth(var16), var6 + 16 + 19 * var13 + 7, var18);
                }
            }
        }
    }

    public void func_40219_x_()
    {
        ItemStack var1 = this.inventorySlots.getSlot(0).getStack();

        if (!ItemStack.areItemStacksEqual(var1, this.field_40222_o))
        {
            this.field_40222_o = var1;

            do
            {
                this.field_40226_k += (float)(this.field_40230_x.nextInt(4) - this.field_40230_x.nextInt(4));
            }
            while (this.field_40229_i <= this.field_40226_k + 1.0F && this.field_40229_i >= this.field_40226_k - 1.0F);
        }

        ++this.field_40227_h;
        this.field_40225_j = this.field_40229_i;
        this.field_40221_n = this.field_40224_m;
        boolean var2 = false;

        for (int var3 = 0; var3 < 3; ++var3)
        {
            if (this.containerEnchantment.enchantLevels[var3] != 0)
            {
                var2 = true;
            }
        }

        if (var2)
        {
            this.field_40224_m += 0.2F;
        }
        else
        {
            this.field_40224_m -= 0.2F;
        }

        if (this.field_40224_m < 0.0F)
        {
            this.field_40224_m = 0.0F;
        }

        if (this.field_40224_m > 1.0F)
        {
            this.field_40224_m = 1.0F;
        }

        float var5 = (this.field_40226_k - this.field_40229_i) * 0.4F;
        float var4 = 0.2F;

        if (var5 < -var4)
        {
            var5 = -var4;
        }

        if (var5 > var4)
        {
            var5 = var4;
        }

        this.field_40223_l += (var5 - this.field_40223_l) * 0.9F;
        this.field_40229_i += this.field_40223_l;
    }
}
