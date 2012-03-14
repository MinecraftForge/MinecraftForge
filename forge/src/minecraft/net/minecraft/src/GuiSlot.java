package net.minecraft.src;

import java.util.List;
import net.minecraft.client.Minecraft;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public abstract class GuiSlot
{
    private final Minecraft mc;

    /**
     * The width of the GuiScreen. Affects the container rendering, but not the overlays.
     */
    private final int width;

    /**
     * The height of the GuiScreen. Affects the container rendering, but not the overlays or the scrolling.
     */
    private final int height;

    /** The top of the slot container. Affects the overlays and scrolling. */
    protected final int top;

    /** The bottom of the slot container. Affects the overlays and scrolling. */
    protected final int bottom;
    private final int right;
    private final int left;

    /** The height of a slot. */
    protected final int slotHeight;

    /** button id of the button used to scroll up */
    private int scrollUpButtonID;

    /** the buttonID of the button used to scroll down */
    private int scrollDownButtonID;
    protected int field_35409_k;
    protected int field_35408_l;

    /** where the mouse was in the window when you first clicked to scroll */
    private float initialClickY = -2.0F;

    /**
     * what to multiply the amount you moved your mouse by(used for slowing down scrolling when over the items and no on
     * scroll bar)
     */
    private float scrollMultiplier;

    /** how far down this slot has been scrolled */
    private float amountScrolled;

    /** the element in the list that was selected */
    private int selectedElement = -1;

    /** the time when this button was last clicked. */
    private long lastClicked = 0L;
    private boolean field_25123_p = true;
    private boolean field_27262_q;
    private int field_27261_r;

    public GuiSlot(Minecraft par1Minecraft, int par2, int par3, int par4, int par5, int par6)
    {
        this.mc = par1Minecraft;
        this.width = par2;
        this.height = par3;
        this.top = par4;
        this.bottom = par5;
        this.slotHeight = par6;
        this.left = 0;
        this.right = par2;
    }

    public void func_27258_a(boolean par1)
    {
        this.field_25123_p = par1;
    }

    protected void func_27259_a(boolean par1, int par2)
    {
        this.field_27262_q = par1;
        this.field_27261_r = par2;

        if (!par1)
        {
            this.field_27261_r = 0;
        }
    }

    /**
     * Gets the size of the current slot list.
     */
    protected abstract int getSize();

    /**
     * the element in the slot that was clicked, boolean for wether it was double clicked or not
     */
    protected abstract void elementClicked(int var1, boolean var2);

    /**
     * returns true if the element passed in is currently selected
     */
    protected abstract boolean isSelected(int var1);

    /**
     * return the height of the content being scrolled
     */
    protected int getContentHeight()
    {
        return this.getSize() * this.slotHeight + this.field_27261_r;
    }

    protected abstract void drawBackground();

    protected abstract void drawSlot(int var1, int var2, int var3, int var4, Tessellator var5);

    protected void func_27260_a(int par1, int par2, Tessellator par3Tessellator) {}

    protected void func_27255_a(int par1, int par2) {}

    protected void func_27257_b(int par1, int par2) {}

    public int func_27256_c(int par1, int par2)
    {
        int var3 = this.width / 2 - 110;
        int var4 = this.width / 2 + 110;
        int var5 = par2 - this.top - this.field_27261_r + (int)this.amountScrolled - 4;
        int var6 = var5 / this.slotHeight;
        return par1 >= var3 && par1 <= var4 && var6 >= 0 && var5 >= 0 && var6 < this.getSize() ? var6 : -1;
    }

    /**
     * Registers the IDs that can be used for the scrollbar's buttons.
     */
    public void registerScrollButtons(List par1List, int par2, int par3)
    {
        this.scrollUpButtonID = par2;
        this.scrollDownButtonID = par3;
    }

    /**
     * stop the thing from scrolling out of bounds
     */
    private void bindAmountScrolled()
    {
        int var1 = this.getContentHeight() - (this.bottom - this.top - 4);

        if (var1 < 0)
        {
            var1 /= 2;
        }

        if (this.amountScrolled < 0.0F)
        {
            this.amountScrolled = 0.0F;
        }

        if (this.amountScrolled > (float)var1)
        {
            this.amountScrolled = (float)var1;
        }
    }

    public void actionPerformed(GuiButton par1GuiButton)
    {
        if (par1GuiButton.enabled)
        {
            if (par1GuiButton.id == this.scrollUpButtonID)
            {
                this.amountScrolled -= (float)(this.slotHeight * 2 / 3);
                this.initialClickY = -2.0F;
                this.bindAmountScrolled();
            }
            else if (par1GuiButton.id == this.scrollDownButtonID)
            {
                this.amountScrolled += (float)(this.slotHeight * 2 / 3);
                this.initialClickY = -2.0F;
                this.bindAmountScrolled();
            }
        }
    }

    /**
     * draws the slot to the screen, pass in mouse's current x and y and partial ticks
     */
    public void drawScreen(int par1, int par2, float par3)
    {
        this.field_35409_k = par1;
        this.field_35408_l = par2;
        this.drawBackground();
        int var4 = this.getSize();
        int var5 = this.width / 2 + 124;
        int var6 = var5 + 6;
        int var9;
        int var10;
        int var11;
        int var13;
        int var19;

        if (Mouse.isButtonDown(0))
        {
            if (this.initialClickY == -1.0F)
            {
                boolean var7 = true;

                if (par2 >= this.top && par2 <= this.bottom)
                {
                    int var8 = this.width / 2 - 110;
                    var9 = this.width / 2 + 110;
                    var10 = par2 - this.top - this.field_27261_r + (int)this.amountScrolled - 4;
                    var11 = var10 / this.slotHeight;

                    if (par1 >= var8 && par1 <= var9 && var11 >= 0 && var10 >= 0 && var11 < var4)
                    {
                        boolean var12 = var11 == this.selectedElement && System.currentTimeMillis() - this.lastClicked < 250L;
                        this.elementClicked(var11, var12);
                        this.selectedElement = var11;
                        this.lastClicked = System.currentTimeMillis();
                    }
                    else if (par1 >= var8 && par1 <= var9 && var10 < 0)
                    {
                        this.func_27255_a(par1 - var8, par2 - this.top + (int)this.amountScrolled - 4);
                        var7 = false;
                    }

                    if (par1 >= var5 && par1 <= var6)
                    {
                        this.scrollMultiplier = -1.0F;
                        var19 = this.getContentHeight() - (this.bottom - this.top - 4);

                        if (var19 < 1)
                        {
                            var19 = 1;
                        }

                        var13 = (int)((float)((this.bottom - this.top) * (this.bottom - this.top)) / (float)this.getContentHeight());

                        if (var13 < 32)
                        {
                            var13 = 32;
                        }

                        if (var13 > this.bottom - this.top - 8)
                        {
                            var13 = this.bottom - this.top - 8;
                        }

                        this.scrollMultiplier /= (float)(this.bottom - this.top - var13) / (float)var19;
                    }
                    else
                    {
                        this.scrollMultiplier = 1.0F;
                    }

                    if (var7)
                    {
                        this.initialClickY = (float)par2;
                    }
                    else
                    {
                        this.initialClickY = -2.0F;
                    }
                }
                else
                {
                    this.initialClickY = -2.0F;
                }
            }
            else if (this.initialClickY >= 0.0F)
            {
                this.amountScrolled -= ((float)par2 - this.initialClickY) * this.scrollMultiplier;
                this.initialClickY = (float)par2;
            }
        }
        else
        {
            while (Mouse.next())
            {
                int var16 = Mouse.getEventDWheel();

                if (var16 != 0)
                {
                    if (var16 > 0)
                    {
                        var16 = -1;
                    }
                    else if (var16 < 0)
                    {
                        var16 = 1;
                    }

                    this.amountScrolled += (float)(var16 * this.slotHeight / 2);
                }
            }

            this.initialClickY = -1.0F;
        }

        this.bindAmountScrolled();
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_FOG);
        Tessellator var18 = Tessellator.instance;
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.mc.renderEngine.getTexture("/gui/background.png"));
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        float var17 = 32.0F;
        var18.startDrawingQuads();
        var18.setColorOpaque_I(2105376);
        var18.addVertexWithUV((double)this.left, (double)this.bottom, 0.0D, (double)((float)this.left / var17), (double)((float)(this.bottom + (int)this.amountScrolled) / var17));
        var18.addVertexWithUV((double)this.right, (double)this.bottom, 0.0D, (double)((float)this.right / var17), (double)((float)(this.bottom + (int)this.amountScrolled) / var17));
        var18.addVertexWithUV((double)this.right, (double)this.top, 0.0D, (double)((float)this.right / var17), (double)((float)(this.top + (int)this.amountScrolled) / var17));
        var18.addVertexWithUV((double)this.left, (double)this.top, 0.0D, (double)((float)this.left / var17), (double)((float)(this.top + (int)this.amountScrolled) / var17));
        var18.draw();
        var9 = this.width / 2 - 92 - 16;
        var10 = this.top + 4 - (int)this.amountScrolled;

        if (this.field_27262_q)
        {
            this.func_27260_a(var9, var10, var18);
        }

        int var14;

        for (var11 = 0; var11 < var4; ++var11)
        {
            var19 = var10 + var11 * this.slotHeight + this.field_27261_r;
            var13 = this.slotHeight - 4;

            if (var19 <= this.bottom && var19 + var13 >= this.top)
            {
                if (this.field_25123_p && this.isSelected(var11))
                {
                    var14 = this.width / 2 - 110;
                    int var15 = this.width / 2 + 110;
                    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                    GL11.glDisable(GL11.GL_TEXTURE_2D);
                    var18.startDrawingQuads();
                    var18.setColorOpaque_I(8421504);
                    var18.addVertexWithUV((double)var14, (double)(var19 + var13 + 2), 0.0D, 0.0D, 1.0D);
                    var18.addVertexWithUV((double)var15, (double)(var19 + var13 + 2), 0.0D, 1.0D, 1.0D);
                    var18.addVertexWithUV((double)var15, (double)(var19 - 2), 0.0D, 1.0D, 0.0D);
                    var18.addVertexWithUV((double)var14, (double)(var19 - 2), 0.0D, 0.0D, 0.0D);
                    var18.setColorOpaque_I(0);
                    var18.addVertexWithUV((double)(var14 + 1), (double)(var19 + var13 + 1), 0.0D, 0.0D, 1.0D);
                    var18.addVertexWithUV((double)(var15 - 1), (double)(var19 + var13 + 1), 0.0D, 1.0D, 1.0D);
                    var18.addVertexWithUV((double)(var15 - 1), (double)(var19 - 1), 0.0D, 1.0D, 0.0D);
                    var18.addVertexWithUV((double)(var14 + 1), (double)(var19 - 1), 0.0D, 0.0D, 0.0D);
                    var18.draw();
                    GL11.glEnable(GL11.GL_TEXTURE_2D);
                }

                this.drawSlot(var11, var9, var19, var13, var18);
            }
        }

        GL11.glDisable(GL11.GL_DEPTH_TEST);
        byte var20 = 4;
        this.overlayBackground(0, this.top, 255, 255);
        this.overlayBackground(this.bottom, this.height, 255, 255);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glShadeModel(GL11.GL_SMOOTH);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        var18.startDrawingQuads();
        var18.setColorRGBA_I(0, 0);
        var18.addVertexWithUV((double)this.left, (double)(this.top + var20), 0.0D, 0.0D, 1.0D);
        var18.addVertexWithUV((double)this.right, (double)(this.top + var20), 0.0D, 1.0D, 1.0D);
        var18.setColorRGBA_I(0, 255);
        var18.addVertexWithUV((double)this.right, (double)this.top, 0.0D, 1.0D, 0.0D);
        var18.addVertexWithUV((double)this.left, (double)this.top, 0.0D, 0.0D, 0.0D);
        var18.draw();
        var18.startDrawingQuads();
        var18.setColorRGBA_I(0, 255);
        var18.addVertexWithUV((double)this.left, (double)this.bottom, 0.0D, 0.0D, 1.0D);
        var18.addVertexWithUV((double)this.right, (double)this.bottom, 0.0D, 1.0D, 1.0D);
        var18.setColorRGBA_I(0, 0);
        var18.addVertexWithUV((double)this.right, (double)(this.bottom - var20), 0.0D, 1.0D, 0.0D);
        var18.addVertexWithUV((double)this.left, (double)(this.bottom - var20), 0.0D, 0.0D, 0.0D);
        var18.draw();
        var19 = this.getContentHeight() - (this.bottom - this.top - 4);

        if (var19 > 0)
        {
            var13 = (this.bottom - this.top) * (this.bottom - this.top) / this.getContentHeight();

            if (var13 < 32)
            {
                var13 = 32;
            }

            if (var13 > this.bottom - this.top - 8)
            {
                var13 = this.bottom - this.top - 8;
            }

            var14 = (int)this.amountScrolled * (this.bottom - this.top - var13) / var19 + this.top;

            if (var14 < this.top)
            {
                var14 = this.top;
            }

            var18.startDrawingQuads();
            var18.setColorRGBA_I(0, 255);
            var18.addVertexWithUV((double)var5, (double)this.bottom, 0.0D, 0.0D, 1.0D);
            var18.addVertexWithUV((double)var6, (double)this.bottom, 0.0D, 1.0D, 1.0D);
            var18.addVertexWithUV((double)var6, (double)this.top, 0.0D, 1.0D, 0.0D);
            var18.addVertexWithUV((double)var5, (double)this.top, 0.0D, 0.0D, 0.0D);
            var18.draw();
            var18.startDrawingQuads();
            var18.setColorRGBA_I(8421504, 255);
            var18.addVertexWithUV((double)var5, (double)(var14 + var13), 0.0D, 0.0D, 1.0D);
            var18.addVertexWithUV((double)var6, (double)(var14 + var13), 0.0D, 1.0D, 1.0D);
            var18.addVertexWithUV((double)var6, (double)var14, 0.0D, 1.0D, 0.0D);
            var18.addVertexWithUV((double)var5, (double)var14, 0.0D, 0.0D, 0.0D);
            var18.draw();
            var18.startDrawingQuads();
            var18.setColorRGBA_I(12632256, 255);
            var18.addVertexWithUV((double)var5, (double)(var14 + var13 - 1), 0.0D, 0.0D, 1.0D);
            var18.addVertexWithUV((double)(var6 - 1), (double)(var14 + var13 - 1), 0.0D, 1.0D, 1.0D);
            var18.addVertexWithUV((double)(var6 - 1), (double)var14, 0.0D, 1.0D, 0.0D);
            var18.addVertexWithUV((double)var5, (double)var14, 0.0D, 0.0D, 0.0D);
            var18.draw();
        }

        this.func_27257_b(par1, par2);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glShadeModel(GL11.GL_FLAT);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glDisable(GL11.GL_BLEND);
    }

    /**
     * Overlays the background to hide scrolled items
     */
    private void overlayBackground(int par1, int par2, int par3, int par4)
    {
        Tessellator var5 = Tessellator.instance;
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.mc.renderEngine.getTexture("/gui/background.png"));
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        float var6 = 32.0F;
        var5.startDrawingQuads();
        var5.setColorRGBA_I(4210752, par4);
        var5.addVertexWithUV(0.0D, (double)par2, 0.0D, 0.0D, (double)((float)par2 / var6));
        var5.addVertexWithUV((double)this.width, (double)par2, 0.0D, (double)((float)this.width / var6), (double)((float)par2 / var6));
        var5.setColorRGBA_I(4210752, par3);
        var5.addVertexWithUV((double)this.width, (double)par1, 0.0D, (double)((float)this.width / var6), (double)((float)par1 / var6));
        var5.addVertexWithUV(0.0D, (double)par1, 0.0D, 0.0D, (double)((float)par1 / var6));
        var5.draw();
    }
}
