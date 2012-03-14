package net.minecraft.src;

import org.lwjgl.input.Keyboard;

public class GuiRenameWorld extends GuiScreen
{
    private GuiScreen parentGuiScreen;
    private GuiTextField theGuiTextField;
    private final String worldName;

    public GuiRenameWorld(GuiScreen par1GuiScreen, String par2Str)
    {
        this.parentGuiScreen = par1GuiScreen;
        this.worldName = par2Str;
    }

    /**
     * Called from the main game loop to update the screen.
     */
    public void updateScreen()
    {
        this.theGuiTextField.updateCursorCounter();
    }

    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    public void initGui()
    {
        StringTranslate var1 = StringTranslate.getInstance();
        Keyboard.enableRepeatEvents(true);
        this.controlList.clear();
        this.controlList.add(new GuiButton(0, this.width / 2 - 100, this.height / 4 + 96 + 12, var1.translateKey("selectWorld.renameButton")));
        this.controlList.add(new GuiButton(1, this.width / 2 - 100, this.height / 4 + 120 + 12, var1.translateKey("gui.cancel")));
        ISaveFormat var2 = this.mc.getSaveLoader();
        WorldInfo var3 = var2.getWorldInfo(this.worldName);
        String var4 = var3.getWorldName();
        this.theGuiTextField = new GuiTextField(this, this.fontRenderer, this.width / 2 - 100, 60, 200, 20, var4);
        this.theGuiTextField.isFocused = true;
        this.theGuiTextField.setMaxStringLength(32);
    }

    /**
     * Called when the screen is unloaded. Used to disable keyboard repeat events
     */
    public void onGuiClosed()
    {
        Keyboard.enableRepeatEvents(false);
    }

    /**
     * Fired when a control is clicked. This is the equivalent of ActionListener.actionPerformed(ActionEvent e).
     */
    protected void actionPerformed(GuiButton par1GuiButton)
    {
        if (par1GuiButton.enabled)
        {
            if (par1GuiButton.id == 1)
            {
                this.mc.displayGuiScreen(this.parentGuiScreen);
            }
            else if (par1GuiButton.id == 0)
            {
                ISaveFormat var2 = this.mc.getSaveLoader();
                var2.renameWorld(this.worldName, this.theGuiTextField.getText().trim());
                this.mc.displayGuiScreen(this.parentGuiScreen);
            }
        }
    }

    /**
     * Fired when a key is typed. This is the equivalent of KeyListener.keyTyped(KeyEvent e).
     */
    protected void keyTyped(char par1, int par2)
    {
        this.theGuiTextField.textboxKeyTyped(par1, par2);
        ((GuiButton)this.controlList.get(0)).enabled = this.theGuiTextField.getText().trim().length() > 0;

        if (par1 == 13)
        {
            this.actionPerformed((GuiButton)this.controlList.get(0));
        }
    }

    /**
     * Called when the mouse is clicked.
     */
    protected void mouseClicked(int par1, int par2, int par3)
    {
        super.mouseClicked(par1, par2, par3);
        this.theGuiTextField.mouseClicked(par1, par2, par3);
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(int par1, int par2, float par3)
    {
        StringTranslate var4 = StringTranslate.getInstance();
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRenderer, var4.translateKey("selectWorld.renameTitle"), this.width / 2, this.height / 4 - 60 + 20, 16777215);
        this.drawString(this.fontRenderer, var4.translateKey("selectWorld.enterName"), this.width / 2 - 100, 47, 10526880);
        this.theGuiTextField.drawTextBox();
        super.drawScreen(par1, par2, par3);
    }
}
