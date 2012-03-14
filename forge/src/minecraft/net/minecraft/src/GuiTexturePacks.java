package net.minecraft.src;

import java.io.File;
import java.net.URI;
import net.minecraft.client.Minecraft;
import org.lwjgl.Sys;

public class GuiTexturePacks extends GuiScreen
{
    protected GuiScreen guiScreen;
    private int refreshTimer = -1;

    /** the absolute location of this texture pack */
    private String fileLocation = "";

    /**
     * the GuiTexturePackSlot that contains all the texture packs and their descriptions
     */
    private GuiTexturePackSlot guiTexturePackSlot;

    public GuiTexturePacks(GuiScreen par1GuiScreen)
    {
        this.guiScreen = par1GuiScreen;
    }

    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    public void initGui()
    {
        StringTranslate var1 = StringTranslate.getInstance();
        this.controlList.add(new GuiSmallButton(5, this.width / 2 - 154, this.height - 48, var1.translateKey("texturePack.openFolder")));
        this.controlList.add(new GuiSmallButton(6, this.width / 2 + 4, this.height - 48, var1.translateKey("gui.done")));
        this.mc.texturePackList.updateAvaliableTexturePacks();
        this.fileLocation = (new File(Minecraft.getMinecraftDir(), "texturepacks")).getAbsolutePath();
        this.guiTexturePackSlot = new GuiTexturePackSlot(this);
        this.guiTexturePackSlot.registerScrollButtons(this.controlList, 7, 8);
    }

    /**
     * Fired when a control is clicked. This is the equivalent of ActionListener.actionPerformed(ActionEvent e).
     */
    protected void actionPerformed(GuiButton par1GuiButton)
    {
        if (par1GuiButton.enabled)
        {
            if (par1GuiButton.id == 5)
            {
                boolean var2 = false;

                try
                {
                    Class var3 = Class.forName("java.awt.Desktop");
                    Object var4 = var3.getMethod("getDesktop", new Class[0]).invoke((Object)null, new Object[0]);
                    var3.getMethod("browse", new Class[] {URI.class}).invoke(var4, new Object[] {(new File(Minecraft.getMinecraftDir(), "texturepacks")).toURI()});
                }
                catch (Throwable var5)
                {
                    var5.printStackTrace();
                    var2 = true;
                }

                if (var2)
                {
                    System.out.println("Opening via Sys class!");
                    Sys.openURL("file://" + this.fileLocation);
                }
            }
            else if (par1GuiButton.id == 6)
            {
                this.mc.renderEngine.refreshTextures();
                this.mc.displayGuiScreen(this.guiScreen);
            }
            else
            {
                this.guiTexturePackSlot.actionPerformed(par1GuiButton);
            }
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
     * Called when the mouse is moved or a mouse button is released.  Signature: (mouseX, mouseY, which) which==-1 is
     * mouseMove, which==0 or which==1 is mouseUp
     */
    protected void mouseMovedOrUp(int par1, int par2, int par3)
    {
        super.mouseMovedOrUp(par1, par2, par3);
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(int par1, int par2, float par3)
    {
        this.guiTexturePackSlot.drawScreen(par1, par2, par3);

        if (this.refreshTimer <= 0)
        {
            this.mc.texturePackList.updateAvaliableTexturePacks();
            this.refreshTimer += 20;
        }

        StringTranslate var4 = StringTranslate.getInstance();
        this.drawCenteredString(this.fontRenderer, var4.translateKey("texturePack.title"), this.width / 2, 16, 16777215);
        this.drawCenteredString(this.fontRenderer, var4.translateKey("texturePack.folderInfo"), this.width / 2 - 77, this.height - 26, 8421504);
        super.drawScreen(par1, par2, par3);
    }

    /**
     * Called from the main game loop to update the screen.
     */
    public void updateScreen()
    {
        super.updateScreen();
        --this.refreshTimer;
    }

    static Minecraft func_22124_a(GuiTexturePacks par0GuiTexturePacks)
    {
        return par0GuiTexturePacks.mc;
    }

    static Minecraft func_22126_b(GuiTexturePacks par0GuiTexturePacks)
    {
        return par0GuiTexturePacks.mc;
    }

    static Minecraft func_22119_c(GuiTexturePacks par0GuiTexturePacks)
    {
        return par0GuiTexturePacks.mc;
    }

    static Minecraft func_22122_d(GuiTexturePacks par0GuiTexturePacks)
    {
        return par0GuiTexturePacks.mc;
    }

    static Minecraft func_22117_e(GuiTexturePacks par0GuiTexturePacks)
    {
        return par0GuiTexturePacks.mc;
    }

    static Minecraft func_35307_f(GuiTexturePacks par0GuiTexturePacks)
    {
        return par0GuiTexturePacks.mc;
    }

    static Minecraft func_35308_g(GuiTexturePacks par0GuiTexturePacks)
    {
        return par0GuiTexturePacks.mc;
    }

    static Minecraft func_22118_f(GuiTexturePacks par0GuiTexturePacks)
    {
        return par0GuiTexturePacks.mc;
    }

    static Minecraft func_22116_g(GuiTexturePacks par0GuiTexturePacks)
    {
        return par0GuiTexturePacks.mc;
    }

    static Minecraft func_22121_h(GuiTexturePacks par0GuiTexturePacks)
    {
        return par0GuiTexturePacks.mc;
    }

    static Minecraft func_22123_i(GuiTexturePacks par0GuiTexturePacks)
    {
        return par0GuiTexturePacks.mc;
    }

    static FontRenderer func_22127_j(GuiTexturePacks par0GuiTexturePacks)
    {
        return par0GuiTexturePacks.fontRenderer;
    }

    static FontRenderer func_22120_k(GuiTexturePacks par0GuiTexturePacks)
    {
        return par0GuiTexturePacks.fontRenderer;
    }

    static FontRenderer func_22125_l(GuiTexturePacks par0GuiTexturePacks)
    {
        return par0GuiTexturePacks.fontRenderer;
    }
}
