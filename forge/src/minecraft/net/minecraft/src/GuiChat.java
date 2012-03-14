package net.minecraft.src;

import org.lwjgl.input.Keyboard;

public class GuiChat extends GuiScreen
{
    /** The chat message. */
    protected String message = "";

    /** Counts the number of screen updates. Used to make the caret flash. */
    private int updateCounter = 0;

    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    public void initGui()
    {
        Keyboard.enableRepeatEvents(true);
    }

    /**
     * Called when the screen is unloaded. Used to disable keyboard repeat events
     */
    public void onGuiClosed()
    {
        Keyboard.enableRepeatEvents(false);
    }

    /**
     * Called from the main game loop to update the screen.
     */
    public void updateScreen()
    {
        ++this.updateCounter;
    }

    /**
     * Fired when a key is typed. This is the equivalent of KeyListener.keyTyped(KeyEvent e).
     */
    protected void keyTyped(char par1, int par2)
    {
        if (par2 == 1)
        {
            this.mc.displayGuiScreen((GuiScreen)null);
        }
        else if (par2 == 28)
        {
            String var3 = this.message.trim();

            if (var3.length() > 0)
            {
                String var4 = this.message.trim();

                if (!this.mc.lineIsCommand(var4))
                {
                    this.mc.thePlayer.sendChatMessage(var4);
                }
            }

            this.mc.displayGuiScreen((GuiScreen)null);
        }
        else
        {
            if (par2 == 14 && this.message.length() > 0)
            {
                this.message = this.message.substring(0, this.message.length() - 1);
            }

            if (ChatAllowedCharacters.func_48614_a(par1) && this.message.length() < 100)
            {
                this.message = this.message + par1;
            }
        }
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(int par1, int par2, float par3)
    {
        this.drawRect(2, this.height - 14, this.width - 2, this.height - 2, Integer.MIN_VALUE);
        this.drawString(this.fontRenderer, "> " + this.message + (this.updateCounter / 6 % 2 == 0 ? "_" : ""), 4, this.height - 12, 14737632);
        super.drawScreen(par1, par2, par3);
    }

    /**
     * Called when the mouse is clicked.
     */
    protected void mouseClicked(int par1, int par2, int par3)
    {
        if (par3 == 0)
        {
            if (this.mc.ingameGUI.field_933_a != null)
            {
                if (this.message.length() > 0 && !this.message.endsWith(" "))
                {
                    this.message = this.message + " ";
                }

                this.message = this.message + this.mc.ingameGUI.field_933_a;
                byte var4 = 100;

                if (this.message.length() > var4)
                {
                    this.message = this.message.substring(0, var4);
                }
            }
            else
            {
                super.mouseClicked(par1, par2, par3);
            }
        }
    }
}
