package net.minecraft.src;

public class GuiDisconnected extends GuiScreen
{
    /** The error message. */
    private String errorMessage;

    /** The details about the error. */
    private String errorDetail;

    public GuiDisconnected(String par1Str, String par2Str, Object[] par3ArrayOfObj)
    {
        StringTranslate var4 = StringTranslate.getInstance();
        this.errorMessage = var4.translateKey(par1Str);

        if (par3ArrayOfObj != null)
        {
            this.errorDetail = var4.translateKeyFormat(par2Str, par3ArrayOfObj);
        }
        else
        {
            this.errorDetail = var4.translateKey(par2Str);
        }
    }

    /**
     * Called from the main game loop to update the screen.
     */
    public void updateScreen() {}

    /**
     * Fired when a key is typed. This is the equivalent of KeyListener.keyTyped(KeyEvent e).
     */
    protected void keyTyped(char par1, int par2) {}

    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    public void initGui()
    {
        StringTranslate var1 = StringTranslate.getInstance();
        this.controlList.clear();
        this.controlList.add(new GuiButton(0, this.width / 2 - 100, this.height / 4 + 120 + 12, var1.translateKey("gui.toMenu")));
    }

    /**
     * Fired when a control is clicked. This is the equivalent of ActionListener.actionPerformed(ActionEvent e).
     */
    protected void actionPerformed(GuiButton par1GuiButton)
    {
        if (par1GuiButton.id == 0)
        {
            this.mc.displayGuiScreen(new GuiMainMenu());
        }
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(int par1, int par2, float par3)
    {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRenderer, this.errorMessage, this.width / 2, this.height / 2 - 50, 16777215);
        String[] var4 = this.errorDetail.split("\n");

        for (int var5 = 0; var5 < var4.length; ++var5)
        {
            this.drawCenteredString(this.fontRenderer, var4[var5], this.width / 2, this.height / 2 - 10 + var5 * 10, 16777215);
        }

        super.drawScreen(par1, par2, par3);
    }
}
