package net.minecraft.src;

import java.util.Random;
import org.lwjgl.input.Keyboard;

public class GuiCreateWorld extends GuiScreen
{
    private GuiScreen parentGuiScreen;
    private GuiTextField textboxWorldName;
    private GuiTextField textboxSeed;
    private String folderName;

    /** 'hardcore', 'creative' or 'survival' */
    private String gameMode = "survival";
    private boolean field_35365_g = true;
    private boolean field_40232_h = false;
    private boolean createClicked;

    /**
     * True if the extra options (Seed box, structure toggle button, world type button, etc.) are being shown
     */
    private boolean moreOptions;

    /** The GUIButton that you click to change game modes. */
    private GuiButton gameModeButton;

    /**
     * The GUIButton that you click to get to options like the seed when creating a world.
     */
    private GuiButton moreWorldOptions;

    /** The GuiButton in the 'More World Options' screen. Toggles ON/OFF */
    private GuiButton generateStructuresButton;

    /**
     * the GUIButton in the more world options screen. It's currently greyed out and unused in minecraft 1.0.0
     */
    private GuiButton worldTypeButton;

    /** The first line of text describing the currently selected game mode. */
    private String gameModeDescriptionLine1;

    /** The second line of text describing the currently selected game mode. */
    private String gameModeDescriptionLine2;

    /** The current textboxSeed text */
    private String seed;

    /** E.g. New World, Neue Welt, Nieuwe wereld, Neuvo Mundo */
    private String localizedNewWorldText;
    private int field_46030_z = 0;

    public GuiCreateWorld(GuiScreen par1GuiScreen)
    {
        this.parentGuiScreen = par1GuiScreen;
        this.seed = "";
        this.localizedNewWorldText = StatCollector.translateToLocal("selectWorld.newWorld");
    }

    /**
     * Called from the main game loop to update the screen.
     */
    public void updateScreen()
    {
        this.textboxWorldName.updateCursorCounter();
        this.textboxSeed.updateCursorCounter();
    }

    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    public void initGui()
    {
        StringTranslate var1 = StringTranslate.getInstance();
        Keyboard.enableRepeatEvents(true);
        this.controlList.clear();
        this.controlList.add(new GuiButton(0, this.width / 2 - 155, this.height - 28, 150, 20, var1.translateKey("selectWorld.create")));
        this.controlList.add(new GuiButton(1, this.width / 2 + 5, this.height - 28, 150, 20, var1.translateKey("gui.cancel")));
        this.controlList.add(this.gameModeButton = new GuiButton(2, this.width / 2 - 75, 100, 150, 20, var1.translateKey("selectWorld.gameMode")));
        this.controlList.add(this.moreWorldOptions = new GuiButton(3, this.width / 2 - 75, 172, 150, 20, var1.translateKey("selectWorld.moreWorldOptions")));
        this.controlList.add(this.generateStructuresButton = new GuiButton(4, this.width / 2 - 155, 100, 150, 20, var1.translateKey("selectWorld.mapFeatures")));
        this.generateStructuresButton.drawButton = false;
        this.controlList.add(this.worldTypeButton = new GuiButton(5, this.width / 2 + 5, 100, 150, 20, var1.translateKey("selectWorld.mapType")));
        this.worldTypeButton.drawButton = false;
        this.textboxWorldName = new GuiTextField(this, this.fontRenderer, this.width / 2 - 100, 60, 200, 20, this.localizedNewWorldText);
        this.textboxWorldName.isFocused = true;
        this.textboxWorldName.setMaxStringLength(32);
        this.textboxSeed = new GuiTextField(this, this.fontRenderer, this.width / 2 - 100, 60, 200, 20, this.seed);
        this.makeUseableName();
        this.func_35363_g();
    }

    /**
     * Makes a the name for a world save folder based on your world name, replacing specific characters for _s and
     * appending -s to the end until a free name is available.
     */
    private void makeUseableName()
    {
        this.folderName = this.textboxWorldName.getText().trim();
        char[] var1 = ChatAllowedCharacters.allowedCharactersArray;
        int var2 = var1.length;

        for (int var3 = 0; var3 < var2; ++var3)
        {
            char var4 = var1[var3];
            this.folderName = this.folderName.replace(var4, '_');
        }

        if (MathHelper.stringNullOrLengthZero(this.folderName))
        {
            this.folderName = "World";
        }

        this.folderName = func_25097_a(this.mc.getSaveLoader(), this.folderName);
    }

    private void func_35363_g()
    {
        StringTranslate var1 = StringTranslate.getInstance();
        this.gameModeButton.displayString = var1.translateKey("selectWorld.gameMode") + " " + var1.translateKey("selectWorld.gameMode." + this.gameMode);
        this.gameModeDescriptionLine1 = var1.translateKey("selectWorld.gameMode." + this.gameMode + ".line1");
        this.gameModeDescriptionLine2 = var1.translateKey("selectWorld.gameMode." + this.gameMode + ".line2");
        this.generateStructuresButton.displayString = var1.translateKey("selectWorld.mapFeatures") + " ";

        if (this.field_35365_g)
        {
            this.generateStructuresButton.displayString = this.generateStructuresButton.displayString + var1.translateKey("options.on");
        }
        else
        {
            this.generateStructuresButton.displayString = this.generateStructuresButton.displayString + var1.translateKey("options.off");
        }

        this.worldTypeButton.displayString = var1.translateKey("selectWorld.mapType") + " " + var1.translateKey(WorldType.field_48637_a[this.field_46030_z].func_46136_a());
    }

    public static String func_25097_a(ISaveFormat par0ISaveFormat, String par1Str)
    {
        while (par0ISaveFormat.getWorldInfo(par1Str) != null)
        {
            par1Str = par1Str + "-";
        }

        return par1Str;
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
                this.mc.displayGuiScreen((GuiScreen)null);

                if (this.createClicked)
                {
                    return;
                }

                this.createClicked = true;
                long var2 = (new Random()).nextLong();
                String var4 = this.textboxSeed.getText();

                if (!MathHelper.stringNullOrLengthZero(var4))
                {
                    try
                    {
                        long var5 = Long.parseLong(var4);

                        if (var5 != 0L)
                        {
                            var2 = var5;
                        }
                    }
                    catch (NumberFormatException var7)
                    {
                        var2 = (long)var4.hashCode();
                    }
                }

                byte var9 = 0;

                if (this.gameMode.equals("creative"))
                {
                    var9 = 1;
                    this.mc.playerController = new PlayerControllerCreative(this.mc);
                }
                else
                {
                    this.mc.playerController = new PlayerControllerSP(this.mc);
                }

                this.mc.startWorld(this.folderName, this.textboxWorldName.getText(), new WorldSettings(var2, var9, this.field_35365_g, this.field_40232_h, WorldType.field_48637_a[this.field_46030_z]));
                this.mc.displayGuiScreen((GuiScreen)null);
            }
            else if (par1GuiButton.id == 3)
            {
                this.moreOptions = !this.moreOptions;
                this.gameModeButton.drawButton = !this.moreOptions;
                this.generateStructuresButton.drawButton = this.moreOptions;
                this.worldTypeButton.drawButton = this.moreOptions;
                StringTranslate var8;

                if (this.moreOptions)
                {
                    var8 = StringTranslate.getInstance();
                    this.moreWorldOptions.displayString = var8.translateKey("gui.done");
                }
                else
                {
                    var8 = StringTranslate.getInstance();
                    this.moreWorldOptions.displayString = var8.translateKey("selectWorld.moreWorldOptions");
                }
            }
            else if (par1GuiButton.id == 2)
            {
                if (this.gameMode.equals("survival"))
                {
                    this.field_40232_h = false;
                    this.gameMode = "hardcore";
                    this.field_40232_h = true;
                    this.func_35363_g();
                }
                else if (this.gameMode.equals("hardcore"))
                {
                    this.field_40232_h = false;
                    this.gameMode = "creative";
                    this.func_35363_g();
                    this.field_40232_h = false;
                }
                else
                {
                    this.gameMode = "survival";
                    this.func_35363_g();
                    this.field_40232_h = false;
                }

                this.func_35363_g();
            }
            else if (par1GuiButton.id == 4)
            {
                this.field_35365_g = !this.field_35365_g;
                this.func_35363_g();
            }
            else if (par1GuiButton.id == 5)
            {
                ++this.field_46030_z;

                if (this.field_46030_z >= WorldType.field_48637_a.length)
                {
                    this.field_46030_z = 0;
                }

                while (WorldType.field_48637_a[this.field_46030_z] == null || !WorldType.field_48637_a[this.field_46030_z].func_48627_d())
                {
                    ++this.field_46030_z;

                    if (this.field_46030_z >= WorldType.field_48637_a.length)
                    {
                        this.field_46030_z = 0;
                    }
                }

                this.func_35363_g();
            }
        }
    }

    /**
     * Fired when a key is typed. This is the equivalent of KeyListener.keyTyped(KeyEvent e).
     */
    protected void keyTyped(char par1, int par2)
    {
        if (this.textboxWorldName.isFocused && !this.moreOptions)
        {
            this.textboxWorldName.textboxKeyTyped(par1, par2);
            this.localizedNewWorldText = this.textboxWorldName.getText();
        }
        else if (this.textboxSeed.isFocused && this.moreOptions)
        {
            this.textboxSeed.textboxKeyTyped(par1, par2);
            this.seed = this.textboxSeed.getText();
        }

        if (par1 == 13)
        {
            this.actionPerformed((GuiButton)this.controlList.get(0));
        }

        ((GuiButton)this.controlList.get(0)).enabled = this.textboxWorldName.getText().length() > 0;
        this.makeUseableName();
    }

    /**
     * Called when the mouse is clicked.
     */
    protected void mouseClicked(int par1, int par2, int par3)
    {
        super.mouseClicked(par1, par2, par3);

        if (!this.moreOptions)
        {
            this.textboxWorldName.mouseClicked(par1, par2, par3);
        }
        else
        {
            this.textboxSeed.mouseClicked(par1, par2, par3);
        }
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(int par1, int par2, float par3)
    {
        StringTranslate var4 = StringTranslate.getInstance();
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRenderer, var4.translateKey("selectWorld.create"), this.width / 2, 20, 16777215);

        if (!this.moreOptions)
        {
            this.drawString(this.fontRenderer, var4.translateKey("selectWorld.enterName"), this.width / 2 - 100, 47, 10526880);
            this.drawString(this.fontRenderer, var4.translateKey("selectWorld.resultFolder") + " " + this.folderName, this.width / 2 - 100, 85, 10526880);
            this.textboxWorldName.drawTextBox();
            this.drawString(this.fontRenderer, this.gameModeDescriptionLine1, this.width / 2 - 100, 122, 10526880);
            this.drawString(this.fontRenderer, this.gameModeDescriptionLine2, this.width / 2 - 100, 134, 10526880);
        }
        else
        {
            this.drawString(this.fontRenderer, var4.translateKey("selectWorld.enterSeed"), this.width / 2 - 100, 47, 10526880);
            this.drawString(this.fontRenderer, var4.translateKey("selectWorld.seedInfo"), this.width / 2 - 100, 85, 10526880);
            this.drawString(this.fontRenderer, var4.translateKey("selectWorld.mapFeatures.info"), this.width / 2 - 150, 122, 10526880);
            this.textboxSeed.drawTextBox();
        }

        super.drawScreen(par1, par2, par3);
    }

    public void selectNextField()
    {
        if (this.textboxWorldName.isFocused)
        {
            this.textboxWorldName.setFocused(false);
            this.textboxSeed.setFocused(true);
        }
        else
        {
            this.textboxWorldName.setFocused(true);
            this.textboxSeed.setFocused(false);
        }
    }
}
