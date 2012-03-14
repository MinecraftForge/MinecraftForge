package net.minecraft.src;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class GuiContainerCreative extends GuiContainer
{
    private static InventoryBasic inventory = new InventoryBasic("tmp", 72);

    /** Amount scrolled in Creative mode inventory (0 = top, 1 = bottom) */
    private float currentScroll = 0.0F;

    /** True if the scrollbar is being dragged */
    private boolean isScrolling = false;

    /**
     * True if the left mouse button was held down last time drawScreen was called.
     */
    private boolean wasClicking;

    public GuiContainerCreative(EntityPlayer par1EntityPlayer)
    {
        super(new ContainerCreative(par1EntityPlayer));
        par1EntityPlayer.craftingInventory = this.inventorySlots;
        this.allowUserInput = true;
        par1EntityPlayer.addStat(AchievementList.openInventory, 1);
        this.ySize = 208;
    }

    /**
     * Called from the main game loop to update the screen.
     */
    public void updateScreen()
    {
        if (!this.mc.playerController.isInCreativeMode())
        {
            this.mc.displayGuiScreen(new GuiInventory(this.mc.thePlayer));
        }
    }

    protected void handleMouseClick(Slot par1Slot, int par2, int par3, boolean par4)
    {
        InventoryPlayer var5;
        ItemStack var6;

        if (par1Slot != null)
        {
            if (par1Slot.inventory == inventory)
            {
                var5 = this.mc.thePlayer.inventory;
                var6 = var5.getItemStack();
                ItemStack var7 = par1Slot.getStack();

                if (var6 != null && var7 != null && var6.itemID == var7.itemID)
                {
                    if (par3 == 0)
                    {
                        if (par4)
                        {
                            var6.stackSize = var6.getMaxStackSize();
                        }
                        else if (var6.stackSize < var6.getMaxStackSize())
                        {
                            ++var6.stackSize;
                        }
                    }
                    else if (var6.stackSize <= 1)
                    {
                        var5.setItemStack((ItemStack)null);
                    }
                    else
                    {
                        --var6.stackSize;
                    }
                }
                else if (var6 != null)
                {
                    var5.setItemStack((ItemStack)null);
                }
                else if (var7 == null)
                {
                    var5.setItemStack((ItemStack)null);
                }
                else if (var6 == null || var6.itemID != var7.itemID)
                {
                    var5.setItemStack(ItemStack.copyItemStack(var7));
                    var6 = var5.getItemStack();

                    if (par4)
                    {
                        var6.stackSize = var6.getMaxStackSize();
                    }
                }
            }
            else
            {
                this.inventorySlots.slotClick(par1Slot.slotNumber, par3, par4, this.mc.thePlayer);
                ItemStack var8 = this.inventorySlots.getSlot(par1Slot.slotNumber).getStack();
                this.mc.playerController.sendSlotPacket(var8, par1Slot.slotNumber - this.inventorySlots.inventorySlots.size() + 9 + 36);
            }
        }
        else
        {
            var5 = this.mc.thePlayer.inventory;

            if (var5.getItemStack() != null)
            {
                if (par3 == 0)
                {
                    this.mc.thePlayer.func_48153_a(var5.getItemStack());
                    this.mc.playerController.func_35639_a(var5.getItemStack());
                    var5.setItemStack((ItemStack)null);
                }

                if (par3 == 1)
                {
                    var6 = var5.getItemStack().splitStack(1);
                    this.mc.thePlayer.func_48153_a(var6);
                    this.mc.playerController.func_35639_a(var6);

                    if (var5.getItemStack().stackSize == 0)
                    {
                        var5.setItemStack((ItemStack)null);
                    }
                }
            }
        }
    }

    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    public void initGui()
    {
        if (!this.mc.playerController.isInCreativeMode())
        {
            this.mc.displayGuiScreen(new GuiInventory(this.mc.thePlayer));
        }
        else
        {
            super.initGui();
            this.controlList.clear();
        }
    }

    /**
     * Draw the foreground layer for the GuiContainer (everythin in front of the items)
     */
    protected void drawGuiContainerForegroundLayer()
    {
        this.fontRenderer.drawString(StatCollector.translateToLocal("container.creative"), 8, 6, 4210752);
    }

    /**
     * Handles mouse input.
     */
    public void handleMouseInput()
    {
        super.handleMouseInput();
        int var1 = Mouse.getEventDWheel();

        if (var1 != 0)
        {
            int var2 = ((ContainerCreative)this.inventorySlots).itemList.size() / 8 - 8 + 1;

            if (var1 > 0)
            {
                var1 = 1;
            }

            if (var1 < 0)
            {
                var1 = -1;
            }

            this.currentScroll = (float)((double)this.currentScroll - (double)var1 / (double)var2);

            if (this.currentScroll < 0.0F)
            {
                this.currentScroll = 0.0F;
            }

            if (this.currentScroll > 1.0F)
            {
                this.currentScroll = 1.0F;
            }

            ((ContainerCreative)this.inventorySlots).scrollTo(this.currentScroll);
        }
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(int par1, int par2, float par3)
    {
        boolean var4 = Mouse.isButtonDown(0);
        int var5 = this.guiLeft;
        int var6 = this.guiTop;
        int var7 = var5 + 155;
        int var8 = var6 + 17;
        int var9 = var7 + 14;
        int var10 = var8 + 160 + 2;

        if (!this.wasClicking && var4 && par1 >= var7 && par2 >= var8 && par1 < var9 && par2 < var10)
        {
            this.isScrolling = true;
        }

        if (!var4)
        {
            this.isScrolling = false;
        }

        this.wasClicking = var4;

        if (this.isScrolling)
        {
            this.currentScroll = (float)(par2 - (var8 + 8)) / ((float)(var10 - var8) - 16.0F);

            if (this.currentScroll < 0.0F)
            {
                this.currentScroll = 0.0F;
            }

            if (this.currentScroll > 1.0F)
            {
                this.currentScroll = 1.0F;
            }

            ((ContainerCreative)this.inventorySlots).scrollTo(this.currentScroll);
        }

        super.drawScreen(par1, par2, par3);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glDisable(GL11.GL_LIGHTING);
    }

    /**
     * Draw the background layer for the GuiContainer (everything behind the items)
     */
    protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3)
    {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        int var4 = this.mc.renderEngine.getTexture("/gui/allitems.png");
        this.mc.renderEngine.bindTexture(var4);
        int var5 = this.guiLeft;
        int var6 = this.guiTop;
        this.drawTexturedModalRect(var5, var6, 0, 0, this.xSize, this.ySize);
        int var7 = var5 + 155;
        int var8 = var6 + 17;
        int var9 = var8 + 160 + 2;
        this.drawTexturedModalRect(var5 + 154, var6 + 17 + (int)((float)(var9 - var8 - 17) * this.currentScroll), 0, 208, 16, 16);
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

    /**
     * Returns the creative inventory
     */
    static InventoryBasic getInventory()
    {
        return inventory;
    }
}
