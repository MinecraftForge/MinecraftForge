package net.minecraft.src;

import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class GuiScreen extends Gui
{
    /** Reference to the Minecraft object. */
    protected Minecraft mc;

    /** The width of the screen object. */
    public int width;

    /** The height of the screen object. */
    public int height;

    /** A list of all the controls added to this container. */
    protected List controlList = new ArrayList();
    public boolean allowUserInput = false;

    /** The FontRenderer used by GuiScreen */
    protected FontRenderer fontRenderer;
    public GuiParticle guiParticles;

    /** The button that was just pressed. */
    private GuiButton selectedButton = null;

    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(int par1, int par2, float par3)
    {
        for (int var4 = 0; var4 < this.controlList.size(); ++var4)
        {
            GuiButton var5 = (GuiButton)this.controlList.get(var4);
            var5.drawButton(this.mc, par1, par2);
        }
    }

    /**
     * Fired when a key is typed. This is the equivalent of KeyListener.keyTyped(KeyEvent e).
     */
    protected void keyTyped(char par1, int par2)
    {
        if (par2 == 1)
        {
            this.mc.displayGuiScreen((GuiScreen)null);
            this.mc.setIngameFocus();
        }
    }

    /**
     * Returns a string stored in the system clipboard.
     */
    public static String getClipboardString()
    {
        try
        {
            Transferable var0 = Toolkit.getDefaultToolkit().getSystemClipboard().getContents((Object)null);

            if (var0 != null && var0.isDataFlavorSupported(DataFlavor.stringFlavor))
            {
                String var1 = (String)var0.getTransferData(DataFlavor.stringFlavor);
                return var1;
            }
        }
        catch (Exception var2)
        {
            ;
        }

        return null;
    }

    /**
     * Called when the mouse is clicked.
     */
    protected void mouseClicked(int par1, int par2, int par3)
    {
        if (par3 == 0)
        {
            for (int var4 = 0; var4 < this.controlList.size(); ++var4)
            {
                GuiButton var5 = (GuiButton)this.controlList.get(var4);

                if (var5.mousePressed(this.mc, par1, par2))
                {
                    this.selectedButton = var5;
                    this.mc.sndManager.playSoundFX("random.click", 1.0F, 1.0F);
                    this.actionPerformed(var5);
                }
            }
        }
    }

    /**
     * Called when the mouse is moved or a mouse button is released.  Signature: (mouseX, mouseY, which) which==-1 is
     * mouseMove, which==0 or which==1 is mouseUp
     */
    protected void mouseMovedOrUp(int par1, int par2, int par3)
    {
        if (this.selectedButton != null && par3 == 0)
        {
            this.selectedButton.mouseReleased(par1, par2);
            this.selectedButton = null;
        }
    }

    /**
     * Fired when a control is clicked. This is the equivalent of ActionListener.actionPerformed(ActionEvent e).
     */
    protected void actionPerformed(GuiButton par1GuiButton) {}

    /**
     * Causes the screen to lay out its subcomponents again. This is the equivalent of the Java call
     * Container.validate()
     */
    public void setWorldAndResolution(Minecraft par1Minecraft, int par2, int par3)
    {
        this.guiParticles = new GuiParticle(par1Minecraft);
        this.mc = par1Minecraft;
        this.fontRenderer = par1Minecraft.fontRenderer;
        this.width = par2;
        this.height = par3;
        this.controlList.clear();
        this.initGui();
    }

    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    public void initGui() {}

    /**
     * Delegates mouse and keyboard input.
     */
    public void handleInput()
    {
        while (Mouse.next())
        {
            this.handleMouseInput();
        }

        while (Keyboard.next())
        {
            this.handleKeyboardInput();
        }
    }

    /**
     * Handles mouse input.
     */
    public void handleMouseInput()
    {
        int var1;
        int var2;

        if (Mouse.getEventButtonState())
        {
            var1 = Mouse.getEventX() * this.width / this.mc.displayWidth;
            var2 = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;
            this.mouseClicked(var1, var2, Mouse.getEventButton());
        }
        else
        {
            var1 = Mouse.getEventX() * this.width / this.mc.displayWidth;
            var2 = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;
            this.mouseMovedOrUp(var1, var2, Mouse.getEventButton());
        }
    }

    /**
     * Handles keyboard input.
     */
    public void handleKeyboardInput()
    {
        if (Keyboard.getEventKeyState())
        {
            if (Keyboard.getEventKey() == 87)
            {
                this.mc.toggleFullscreen();
                return;
            }

            this.keyTyped(Keyboard.getEventCharacter(), Keyboard.getEventKey());
        }
    }

    /**
     * Called from the main game loop to update the screen.
     */
    public void updateScreen() {}

    /**
     * Called when the screen is unloaded. Used to disable keyboard repeat events
     */
    public void onGuiClosed() {}

    /**
     * Draws either a gradient over the background screen (when it exists) or a flat gradient over background.png
     */
    public void drawDefaultBackground()
    {
        this.drawWorldBackground(0);
    }

    public void drawWorldBackground(int par1)
    {
        if (this.mc.theWorld != null)
        {
            this.drawGradientRect(0, 0, this.width, this.height, -1072689136, -804253680);
        }
        else
        {
            this.drawBackground(par1);
        }
    }

    /**
     * Draws the background (i is always 0 as of 1.2.2)
     */
    public void drawBackground(int par1)
    {
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_FOG);
        Tessellator var2 = Tessellator.instance;
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.mc.renderEngine.getTexture("/gui/background.png"));
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        float var3 = 32.0F;
        var2.startDrawingQuads();
        var2.setColorOpaque_I(4210752);
        var2.addVertexWithUV(0.0D, (double)this.height, 0.0D, 0.0D, (double)((float)this.height / var3 + (float)par1));
        var2.addVertexWithUV((double)this.width, (double)this.height, 0.0D, (double)((float)this.width / var3), (double)((float)this.height / var3 + (float)par1));
        var2.addVertexWithUV((double)this.width, 0.0D, 0.0D, (double)((float)this.width / var3), (double)(0 + par1));
        var2.addVertexWithUV(0.0D, 0.0D, 0.0D, 0.0D, (double)(0 + par1));
        var2.draw();
    }

    /**
     * Returns true if this GUI should pause the game when it is displayed in single-player
     */
    public boolean doesGuiPauseGame()
    {
        return true;
    }

    /**
     * Deletes the selected world.
     */
    public void deleteWorld(boolean par1, int par2) {}

    public void selectNextField() {}
}
