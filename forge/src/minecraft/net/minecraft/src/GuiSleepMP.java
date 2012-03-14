package net.minecraft.src;

import org.lwjgl.input.Keyboard;

public class GuiSleepMP extends GuiChat
{
    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    public void initGui()
    {
        Keyboard.enableRepeatEvents(true);
        StringTranslate var1 = StringTranslate.getInstance();
        this.controlList.add(new GuiButton(1, this.width / 2 - 100, this.height - 40, var1.translateKey("multiplayer.stopSleeping")));
    }

    /**
     * Called when the screen is unloaded. Used to disable keyboard repeat events
     */
    public void onGuiClosed()
    {
        Keyboard.enableRepeatEvents(false);
    }

    /**
     * Fired when a key is typed. This is the equivalent of KeyListener.keyTyped(KeyEvent e).
     */
    protected void keyTyped(char par1, int par2)
    {
        if (par2 == 1)
        {
            this.wakeEntity();
        }
        else if (par2 == 28)
        {
            String var3 = this.message.trim();

            if (var3.length() > 0)
            {
                this.mc.thePlayer.sendChatMessage(this.message.trim());
            }

            this.message = "";
        }
        else
        {
            super.keyTyped(par1, par2);
        }
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(int par1, int par2, float par3)
    {
        super.drawScreen(par1, par2, par3);
    }

    /**
     * Fired when a control is clicked. This is the equivalent of ActionListener.actionPerformed(ActionEvent e).
     */
    protected void actionPerformed(GuiButton par1GuiButton)
    {
        if (par1GuiButton.id == 1)
        {
            this.wakeEntity();
        }
        else
        {
            super.actionPerformed(par1GuiButton);
        }
    }

    /**
     * Wakes the entity from the bed
     */
    private void wakeEntity()
    {
        if (this.mc.thePlayer instanceof EntityClientPlayerMP)
        {
            NetClientHandler var1 = ((EntityClientPlayerMP)this.mc.thePlayer).sendQueue;
            var1.addToSendQueue(new Packet19EntityAction(this.mc.thePlayer, 3));
        }
    }
}
