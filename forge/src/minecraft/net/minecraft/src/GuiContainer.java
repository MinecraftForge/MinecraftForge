package net.minecraft.src;

import java.util.List;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public abstract class GuiContainer extends GuiScreen
{
    /** Stacks renderer. Icons, stack size, health, etc... */
    protected static RenderItem itemRenderer = new RenderItem();

    /** The X size of the inventory window in pixels. */
    protected int xSize = 176;

    /** The Y size of the inventory window in pixels. */
    protected int ySize = 166;

    /** A list of the players inventory slots. */
    public Container inventorySlots;

    /**
     * Starting X position for the Gui. Inconsistent use for Gui backgrounds.
     */
    protected int guiLeft;

    /**
     * Starting Y position for the Gui. Inconsistent use for Gui backgrounds.
     */
    protected int guiTop;

    public GuiContainer(Container par1Container)
    {
        this.inventorySlots = par1Container;
    }

    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    public void initGui()
    {
        super.initGui();
        this.mc.thePlayer.craftingInventory = this.inventorySlots;
        this.guiLeft = (this.width - this.xSize) / 2;
        this.guiTop = (this.height - this.ySize) / 2;
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(int par1, int par2, float par3)
    {
        this.drawDefaultBackground();
        int var4 = this.guiLeft;
        int var5 = this.guiTop;
        this.drawGuiContainerBackgroundLayer(par3, par1, par2);
        RenderHelper.enableGUIStandardItemLighting();
        GL11.glPushMatrix();
        GL11.glTranslatef((float)var4, (float)var5, 0.0F);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        Slot var6 = null;
        short var7 = 240;
        short var8 = 240;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)var7 / 1.0F, (float)var8 / 1.0F);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        int var10;

        for (int var20 = 0; var20 < this.inventorySlots.inventorySlots.size(); ++var20)
        {
            Slot var23 = (Slot)this.inventorySlots.inventorySlots.get(var20);
            this.drawSlotInventory(var23);

            if (this.isMouseOverSlot(var23, par1, par2))
            {
                var6 = var23;
                GL11.glDisable(GL11.GL_LIGHTING);
                GL11.glDisable(GL11.GL_DEPTH_TEST);
                int var9 = var23.xDisplayPosition;
                var10 = var23.yDisplayPosition;
                this.drawGradientRect(var9, var10, var9 + 16, var10 + 16, -2130706433, -2130706433);
                GL11.glEnable(GL11.GL_LIGHTING);
                GL11.glEnable(GL11.GL_DEPTH_TEST);
            }
        }

        InventoryPlayer var21 = this.mc.thePlayer.inventory;

        if (var21.getItemStack() != null)
        {
            GL11.glTranslatef(0.0F, 0.0F, 32.0F);
            this.zLevel = 200.0F;
            itemRenderer.zLevel = 200.0F;
            itemRenderer.renderItemIntoGUI(this.fontRenderer, this.mc.renderEngine, var21.getItemStack(), par1 - var4 - 8, par2 - var5 - 8);
            itemRenderer.renderItemOverlayIntoGUI(this.fontRenderer, this.mc.renderEngine, var21.getItemStack(), par1 - var4 - 8, par2 - var5 - 8);
            this.zLevel = 0.0F;
            itemRenderer.zLevel = 0.0F;
        }

        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        this.drawGuiContainerForegroundLayer();

        if (var21.getItemStack() == null && var6 != null && var6.getHasStack())
        {
            ItemStack var22 = var6.getStack();
            List var24 = var22.getItemNameandInformation();

            if (var24.size() > 0)
            {
                var10 = 0;
                int var11;
                int var12;

                for (var11 = 0; var11 < var24.size(); ++var11)
                {
                    var12 = this.fontRenderer.getStringWidth((String)var24.get(var11));

                    if (var12 > var10)
                    {
                        var10 = var12;
                    }
                }

                var11 = par1 - var4 + 12;
                var12 = par2 - var5 - 12;
                int var14 = 8;

                if (var24.size() > 1)
                {
                    var14 += 2 + (var24.size() - 1) * 10;
                }

                this.zLevel = 300.0F;
                itemRenderer.zLevel = 300.0F;
                int var15 = -267386864;
                this.drawGradientRect(var11 - 3, var12 - 4, var11 + var10 + 3, var12 - 3, var15, var15);
                this.drawGradientRect(var11 - 3, var12 + var14 + 3, var11 + var10 + 3, var12 + var14 + 4, var15, var15);
                this.drawGradientRect(var11 - 3, var12 - 3, var11 + var10 + 3, var12 + var14 + 3, var15, var15);
                this.drawGradientRect(var11 - 4, var12 - 3, var11 - 3, var12 + var14 + 3, var15, var15);
                this.drawGradientRect(var11 + var10 + 3, var12 - 3, var11 + var10 + 4, var12 + var14 + 3, var15, var15);
                int var16 = 1347420415;
                int var17 = (var16 & 16711422) >> 1 | var16 & -16777216;
                this.drawGradientRect(var11 - 3, var12 - 3 + 1, var11 - 3 + 1, var12 + var14 + 3 - 1, var16, var17);
                this.drawGradientRect(var11 + var10 + 2, var12 - 3 + 1, var11 + var10 + 3, var12 + var14 + 3 - 1, var16, var17);
                this.drawGradientRect(var11 - 3, var12 - 3, var11 + var10 + 3, var12 - 3 + 1, var16, var16);
                this.drawGradientRect(var11 - 3, var12 + var14 + 2, var11 + var10 + 3, var12 + var14 + 3, var17, var17);

                for (int var18 = 0; var18 < var24.size(); ++var18)
                {
                    String var19 = (String)var24.get(var18);

                    if (var18 == 0)
                    {
                        var19 = "\u00a7" + Integer.toHexString(var22.getRarity().field_40535_e) + var19;
                    }
                    else
                    {
                        var19 = "\u00a77" + var19;
                    }

                    this.fontRenderer.drawStringWithShadow(var19, var11, var12, -1);

                    if (var18 == 0)
                    {
                        var12 += 2;
                    }

                    var12 += 10;
                }

                this.zLevel = 0.0F;
                itemRenderer.zLevel = 0.0F;
            }
        }

        GL11.glPopMatrix();
        super.drawScreen(par1, par2, par3);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
    }

    /**
     * Draw the foreground layer for the GuiContainer (everythin in front of the items)
     */
    protected void drawGuiContainerForegroundLayer() {}

    /**
     * Draw the background layer for the GuiContainer (everything behind the items)
     */
    protected abstract void drawGuiContainerBackgroundLayer(float var1, int var2, int var3);

    /**
     * Draws an inventory slot
     */
    private void drawSlotInventory(Slot par1Slot)
    {
        int var2 = par1Slot.xDisplayPosition;
        int var3 = par1Slot.yDisplayPosition;
        ItemStack var4 = par1Slot.getStack();
        boolean var5 = false;
        this.zLevel = 100.0F;
        itemRenderer.zLevel = 100.0F;

        if (var4 == null)
        {
            int var8 = par1Slot.getBackgroundIconIndex();

            if (var8 >= 0)
            {
                GL11.glDisable(GL11.GL_LIGHTING);
                this.mc.renderEngine.bindTexture(this.mc.renderEngine.getTexture("/gui/items.png"));
                this.drawTexturedModalRect(var2, var3, var8 % 16 * 16, var8 / 16 * 16, 16, 16);
                GL11.glEnable(GL11.GL_LIGHTING);
                var5 = true;
            }
        }

        if (!var5)
        {
            itemRenderer.renderItemIntoGUI(this.fontRenderer, this.mc.renderEngine, var4, var2, var3);
            itemRenderer.renderItemOverlayIntoGUI(this.fontRenderer, this.mc.renderEngine, var4, var2, var3);
        }

        itemRenderer.zLevel = 0.0F;
        this.zLevel = 0.0F;

        if (this == null)
        {
            this.zLevel = 100.0F;
            itemRenderer.zLevel = 100.0F;

            if (var4 == null)
            {
                int var6 = par1Slot.getBackgroundIconIndex();

                if (var6 >= 0)
                {
                    GL11.glDisable(GL11.GL_LIGHTING);
                    this.mc.renderEngine.bindTexture(this.mc.renderEngine.getTexture("/gui/items.png"));
                    this.drawTexturedModalRect(var2, var3, var6 % 16 * 16, var6 / 16 * 16, 16, 16);
                    GL11.glEnable(GL11.GL_LIGHTING);
                    var5 = true;
                }
            }

            if (!var5)
            {
                itemRenderer.renderItemIntoGUI(this.fontRenderer, this.mc.renderEngine, var4, var2, var3);
                itemRenderer.renderItemOverlayIntoGUI(this.fontRenderer, this.mc.renderEngine, var4, var2, var3);
            }

            itemRenderer.zLevel = 0.0F;
            this.zLevel = 0.0F;
        }
    }

    /**
     * Returns the slot at the given coordinates or null if there is none.
     */
    private Slot getSlotAtPosition(int par1, int par2)
    {
        for (int var3 = 0; var3 < this.inventorySlots.inventorySlots.size(); ++var3)
        {
            Slot var4 = (Slot)this.inventorySlots.inventorySlots.get(var3);

            if (this.isMouseOverSlot(var4, par1, par2))
            {
                return var4;
            }
        }

        return null;
    }

    /**
     * Called when the mouse is clicked.
     */
    protected void mouseClicked(int par1, int par2, int par3)
    {
        super.mouseClicked(par1, par2, par3);

        if (par3 == 0 || par3 == 1)
        {
            Slot var4 = this.getSlotAtPosition(par1, par2);
            int var5 = this.guiLeft;
            int var6 = this.guiTop;
            boolean var7 = par1 < var5 || par2 < var6 || par1 >= var5 + this.xSize || par2 >= var6 + this.ySize;
            int var8 = -1;

            if (var4 != null)
            {
                var8 = var4.slotNumber;
            }

            if (var7)
            {
                var8 = -999;
            }

            if (var8 != -1)
            {
                boolean var9 = var8 != -999 && (Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54));
                this.handleMouseClick(var4, var8, par3, var9);
            }
        }
    }

    /**
     * Returns if the passed mouse position is over the specified slot.
     */
    private boolean isMouseOverSlot(Slot par1Slot, int par2, int par3)
    {
        int var4 = this.guiLeft;
        int var5 = this.guiTop;
        par2 -= var4;
        par3 -= var5;
        return par2 >= par1Slot.xDisplayPosition - 1 && par2 < par1Slot.xDisplayPosition + 16 + 1 && par3 >= par1Slot.yDisplayPosition - 1 && par3 < par1Slot.yDisplayPosition + 16 + 1;
    }

    protected void handleMouseClick(Slot par1Slot, int par2, int par3, boolean par4)
    {
        if (par1Slot != null)
        {
            par2 = par1Slot.slotNumber;
        }

        this.mc.playerController.windowClick(this.inventorySlots.windowId, par2, par3, par4, this.mc.thePlayer);
    }

    /**
     * Fired when a key is typed. This is the equivalent of KeyListener.keyTyped(KeyEvent e).
     */
    protected void keyTyped(char par1, int par2)
    {
        if (par2 == 1 || par2 == this.mc.gameSettings.keyBindInventory.keyCode)
        {
            this.mc.thePlayer.closeScreen();
        }
    }

    /**
     * Called when the screen is unloaded. Used to disable keyboard repeat events
     */
    public void onGuiClosed()
    {
        if (this.mc.thePlayer != null)
        {
            this.inventorySlots.onCraftGuiClosed(this.mc.thePlayer);
            this.mc.playerController.func_20086_a(this.inventorySlots.windowId, this.mc.thePlayer);
        }
    }

    /**
     * Returns true if this GUI should pause the game when it is displayed in single-player
     */
    public boolean doesGuiPauseGame()
    {
        return false;
    }

    /**
     * Called from the main game loop to update the screen.
     */
    public void updateScreen()
    {
        super.updateScreen();

        if (!this.mc.thePlayer.isEntityAlive() || this.mc.thePlayer.isDead)
        {
            this.mc.thePlayer.closeScreen();
        }
    }
}
