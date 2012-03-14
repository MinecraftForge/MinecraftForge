package net.minecraft.src;

public class GuiDownloadTerrain extends GuiScreen
{
    /** Network object that downloads the terrain data. */
    private NetClientHandler netHandler;

    /** Counts the number of screen updates. */
    private int updateCounter = 0;

    public GuiDownloadTerrain(NetClientHandler par1NetClientHandler)
    {
        this.netHandler = par1NetClientHandler;
    }

    /**
     * Fired when a key is typed. This is the equivalent of KeyListener.keyTyped(KeyEvent e).
     */
    protected void keyTyped(char par1, int par2) {}

    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    public void initGui()
    {
        this.controlList.clear();
    }

    /**
     * Called from the main game loop to update the screen.
     */
    public void updateScreen()
    {
        ++this.updateCounter;

        if (this.updateCounter % 20 == 0)
        {
            this.netHandler.addToSendQueue(new Packet0KeepAlive());
        }

        if (this.netHandler != null)
        {
            this.netHandler.processReadPackets();
        }
    }

    /**
     * Fired when a control is clicked. This is the equivalent of ActionListener.actionPerformed(ActionEvent e).
     */
    protected void actionPerformed(GuiButton par1GuiButton) {}

    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(int par1, int par2, float par3)
    {
        this.drawBackground(0);
        StringTranslate var4 = StringTranslate.getInstance();
        this.drawCenteredString(this.fontRenderer, var4.translateKey("multiplayer.downloadingTerrain"), this.width / 2, this.height / 2 - 50, 16777215);
        super.drawScreen(par1, par2, par3);
    }
}
