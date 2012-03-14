package net.minecraft.src;

public class GuiMemoryErrorScreen extends GuiScreen
{
    private int field_28098_a = 0;

    /**
     * Called from the main game loop to update the screen.
     */
    public void updateScreen()
    {
        ++this.field_28098_a;
    }

    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    public void initGui() {}

    /**
     * Fired when a control is clicked. This is the equivalent of ActionListener.actionPerformed(ActionEvent e).
     */
    protected void actionPerformed(GuiButton par1GuiButton) {}

    /**
     * Fired when a key is typed. This is the equivalent of KeyListener.keyTyped(KeyEvent e).
     */
    protected void keyTyped(char par1, int par2) {}

    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(int par1, int par2, float par3)
    {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRenderer, "Out of memory!", this.width / 2, this.height / 4 - 60 + 20, 16777215);
        this.drawString(this.fontRenderer, "Minecraft has run out of memory.", this.width / 2 - 140, this.height / 4 - 60 + 60 + 0, 10526880);
        this.drawString(this.fontRenderer, "This could be caused by a bug in the game or by the", this.width / 2 - 140, this.height / 4 - 60 + 60 + 18, 10526880);
        this.drawString(this.fontRenderer, "Java Virtual Machine not being allocated enough", this.width / 2 - 140, this.height / 4 - 60 + 60 + 27, 10526880);
        this.drawString(this.fontRenderer, "memory. If you are playing in a web browser, try", this.width / 2 - 140, this.height / 4 - 60 + 60 + 36, 10526880);
        this.drawString(this.fontRenderer, "downloading the game and playing it offline.", this.width / 2 - 140, this.height / 4 - 60 + 60 + 45, 10526880);
        this.drawString(this.fontRenderer, "To prevent level corruption, the current game has quit.", this.width / 2 - 140, this.height / 4 - 60 + 60 + 63, 10526880);
        this.drawString(this.fontRenderer, "Please restart the game.", this.width / 2 - 140, this.height / 4 - 60 + 60 + 81, 10526880);
        super.drawScreen(par1, par2, par3);
    }
}
