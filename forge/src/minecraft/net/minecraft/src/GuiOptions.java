package net.minecraft.src;

public class GuiOptions extends GuiScreen
{
    /**
     * A reference to the screen object that created this. Used for navigating between screens.
     */
    private GuiScreen parentScreen;

    /** The title string that is displayed in the top-center of the screen. */
    protected String screenTitle = "Options";

    /** Reference to the GameSettings object. */
    private GameSettings options;

    /**
     * An array of options that can be changed directly from the options GUI.
     */
    private static EnumOptions[] relevantOptions = new EnumOptions[] {EnumOptions.MUSIC, EnumOptions.SOUND, EnumOptions.INVERT_MOUSE, EnumOptions.SENSITIVITY, EnumOptions.FOV, EnumOptions.DIFFICULTY};

    public GuiOptions(GuiScreen par1GuiScreen, GameSettings par2GameSettings)
    {
        this.parentScreen = par1GuiScreen;
        this.options = par2GameSettings;
    }

    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    public void initGui()
    {
        StringTranslate var1 = StringTranslate.getInstance();
        this.screenTitle = var1.translateKey("options.title");
        int var2 = 0;
        EnumOptions[] var3 = relevantOptions;
        int var4 = var3.length;

        for (int var5 = 0; var5 < var4; ++var5)
        {
            EnumOptions var6 = var3[var5];

            if (!var6.getEnumFloat())
            {
                GuiSmallButton var7 = new GuiSmallButton(var6.returnEnumOrdinal(), this.width / 2 - 155 + var2 % 2 * 160, this.height / 6 + 24 * (var2 >> 1), var6, this.options.getKeyBinding(var6));

                if (var6 == EnumOptions.DIFFICULTY && this.mc.theWorld != null && this.mc.theWorld.getWorldInfo().isHardcoreModeEnabled())
                {
                    var7.enabled = false;
                    var7.displayString = StatCollector.translateToLocal("options.difficulty") + ": " + StatCollector.translateToLocal("options.difficulty.hardcore");
                }

                this.controlList.add(var7);
            }
            else
            {
                this.controlList.add(new GuiSlider(var6.returnEnumOrdinal(), this.width / 2 - 155 + var2 % 2 * 160, this.height / 6 + 24 * (var2 >> 1), var6, this.options.getKeyBinding(var6), this.options.getOptionFloatValue(var6)));
            }

            ++var2;
        }

        this.controlList.add(new GuiButton(101, this.width / 2 - 100, this.height / 6 + 96 - 6, var1.translateKey("options.video")));
        this.controlList.add(new GuiButton(100, this.width / 2 - 100, this.height / 6 + 120 - 6, var1.translateKey("options.controls")));
        this.controlList.add(new GuiButton(102, this.width / 2 - 100, this.height / 6 + 144 - 6, var1.translateKey("options.language")));
        this.controlList.add(new GuiButton(200, this.width / 2 - 100, this.height / 6 + 168, var1.translateKey("gui.done")));
    }

    /**
     * Fired when a control is clicked. This is the equivalent of ActionListener.actionPerformed(ActionEvent e).
     */
    protected void actionPerformed(GuiButton par1GuiButton)
    {
        if (par1GuiButton.enabled)
        {
            if (par1GuiButton.id < 100 && par1GuiButton instanceof GuiSmallButton)
            {
                this.options.setOptionValue(((GuiSmallButton)par1GuiButton).returnEnumOptions(), 1);
                par1GuiButton.displayString = this.options.getKeyBinding(EnumOptions.getEnumOptions(par1GuiButton.id));
            }

            if (par1GuiButton.id == 101)
            {
                this.mc.gameSettings.saveOptions();
                this.mc.displayGuiScreen(new GuiVideoSettings(this, this.options));
            }

            if (par1GuiButton.id == 100)
            {
                this.mc.gameSettings.saveOptions();
                this.mc.displayGuiScreen(new GuiControls(this, this.options));
            }

            if (par1GuiButton.id == 102)
            {
                this.mc.gameSettings.saveOptions();
                this.mc.displayGuiScreen(new GuiLanguage(this, this.options));
            }

            if (par1GuiButton.id == 200)
            {
                this.mc.gameSettings.saveOptions();
                this.mc.displayGuiScreen(this.parentScreen);
            }
        }
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(int par1, int par2, float par3)
    {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRenderer, this.screenTitle, this.width / 2, 20, 16777215);
        super.drawScreen(par1, par2, par3);
    }
}
