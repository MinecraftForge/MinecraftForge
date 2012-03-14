package net.minecraft.src;

import java.util.Iterator;
import org.lwjgl.opengl.GL11;

public class GuiGameOver extends GuiScreen
{
    private int field_48154_a;

    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    public void initGui()
    {
        this.controlList.clear();

        if (this.mc.theWorld.getWorldInfo().isHardcoreModeEnabled())
        {
            this.controlList.add(new GuiButton(1, this.width / 2 - 100, this.height / 4 + 96, StatCollector.translateToLocal("deathScreen.deleteWorld")));
        }
        else
        {
            this.controlList.add(new GuiButton(1, this.width / 2 - 100, this.height / 4 + 72, StatCollector.translateToLocal("deathScreen.respawn")));
            this.controlList.add(new GuiButton(2, this.width / 2 - 100, this.height / 4 + 96, StatCollector.translateToLocal("deathScreen.titleScreen")));

            if (this.mc.session == null)
            {
                ((GuiButton)this.controlList.get(1)).enabled = false;
            }
        }

        GuiButton var2;

        for (Iterator var1 = this.controlList.iterator(); var1.hasNext(); var2.enabled = false)
        {
            var2 = (GuiButton)var1.next();
        }
    }

    /**
     * Fired when a key is typed. This is the equivalent of KeyListener.keyTyped(KeyEvent e).
     */
    protected void keyTyped(char par1, int par2) {}

    /**
     * Fired when a control is clicked. This is the equivalent of ActionListener.actionPerformed(ActionEvent e).
     */
    protected void actionPerformed(GuiButton par1GuiButton)
    {
        if (par1GuiButton.id == 0)
        {
            ;
        }

        if (par1GuiButton.id == 1)
        {
            if (this.mc.theWorld.getWorldInfo().isHardcoreModeEnabled())
            {
                World var2 = this.mc.theWorld;
                this.mc.exitToMainMenu("Deleting world");
                ISaveFormat var3 = this.mc.getSaveLoader();
                var3.flushCache();
                var3.deleteWorldDirectory(var2.getSaveHandler().getSaveDirectoryName());
                this.mc.displayGuiScreen(new GuiMainMenu());
            }
            else
            {
                this.mc.thePlayer.respawnPlayer();
                this.mc.displayGuiScreen((GuiScreen)null);
            }
        }

        if (par1GuiButton.id == 2)
        {
            if (this.mc.isMultiplayerWorld())
            {
                this.mc.theWorld.sendQuittingDisconnectingPacket();
            }

            this.mc.changeWorld1((World)null);
            this.mc.displayGuiScreen(new GuiMainMenu());
        }
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(int par1, int par2, float par3)
    {
        this.drawGradientRect(0, 0, this.width, this.height, 1615855616, -1602211792);
        GL11.glPushMatrix();
        GL11.glScalef(2.0F, 2.0F, 2.0F);

        if (this.mc.theWorld.getWorldInfo().isHardcoreModeEnabled())
        {
            this.drawCenteredString(this.fontRenderer, StatCollector.translateToLocal("deathScreen.title.hardcore"), this.width / 2 / 2, 30, 16777215);
        }
        else
        {
            this.drawCenteredString(this.fontRenderer, StatCollector.translateToLocal("deathScreen.title"), this.width / 2 / 2, 30, 16777215);
        }

        GL11.glPopMatrix();

        if (this.mc.theWorld.getWorldInfo().isHardcoreModeEnabled())
        {
            this.drawCenteredString(this.fontRenderer, StatCollector.translateToLocal("deathScreen.hardcoreInfo"), this.width / 2, 144, 16777215);
        }

        this.drawCenteredString(this.fontRenderer, StatCollector.translateToLocal("deathScreen.score") + ": \u00a7e" + this.mc.thePlayer.getScore(), this.width / 2, 100, 16777215);
        super.drawScreen(par1, par2, par3);
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
        ++this.field_48154_a;
        GuiButton var2;

        if (this.field_48154_a == 20)
        {
            for (Iterator var1 = this.controlList.iterator(); var1.hasNext(); var2.enabled = true)
            {
                var2 = (GuiButton)var1.next();
            }
        }
    }
}
