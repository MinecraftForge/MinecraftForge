package net.minecraft.src;

import net.minecraft.src.forge.GuiControlsScrollPanel;

public class GuiControls extends GuiScreen
{
    /**
     * A reference to the screen object that created this. Used for navigating between screens.
     */
    private GuiScreen parentScreen;

    /** The title string that is displayed in the top-center of the screen. */
    protected String screenTitle = "Controls";

    /** Reference to the GameSettings object. */
    private GameSettings options;

    /** The ID of the  button that has been pressed. */
    private int buttonId = -1;
    
    private GuiControlsScrollPanel scrollPane;

    public GuiControls(GuiScreen par1GuiScreen, GameSettings par2GameSettings)
    {
        this.parentScreen = par1GuiScreen;
        this.options = par2GameSettings;
    }

    private int func_20080_j()
    {
        return this.width / 2 - 155;
    }

    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    public void initGui()
    {
        scrollPane = new GuiControlsScrollPanel(this, options, mc);
        StringTranslate var1 = StringTranslate.getInstance();
        int var2 = this.func_20080_j();

        this.controlList.add(new GuiButton(200, this.width / 2 - 100, this.height - 28, var1.translateKey("gui.done")));
        scrollPane.registerScrollButtons(controlList, 7, 8);
        this.screenTitle = var1.translateKey("controls.title");
    }

    /**
     * Fired when a control is clicked. This is the equivalent of ActionListener.actionPerformed(ActionEvent e).
     */
    protected void actionPerformed(GuiButton par1GuiButton)
    {
        if (par1GuiButton.id == 200)
        {
            this.mc.displayGuiScreen(this.parentScreen);
        }
    }

    /**
     * Called when the mouse is clicked.
     */
    protected void mouseClicked(int par1, int par2, int par3)
    {
        super.mouseClicked(par1, par2, par3);
    }

    /**
     * Fired when a key is typed. This is the equivalent of KeyListener.keyTyped(KeyEvent e).
     */
    protected void keyTyped(char par1, int par2)
    {
        if (scrollPane.keyTyped(par1, par2))
        {
            super.keyTyped(par1, par2);
        }
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(int par1, int par2, float par3)
    {
        this.drawDefaultBackground();
        
        scrollPane.drawScreen(par1, par2, par3);
        drawCenteredString(fontRenderer, screenTitle, width / 2, 4, 0xffffff);
        super.drawScreen(par1, par2, par3);
    }
}
