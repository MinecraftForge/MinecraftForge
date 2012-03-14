package net.minecraft.src;

public class GuiVideoSettings extends GuiScreen
{
    private GuiScreen parentGuiScreen;

    /** The title string that is displayed in the top-center of the screen. */
    protected String screenTitle = "Video Settings";

    /** GUI game settings */
    private GameSettings guiGameSettings;

    /**
     * True if the system is 64-bit (using a simple indexOf test on a system property)
     */
    private boolean is64bit = false;

    /** An array of all of EnumOption's video options. */
    private static EnumOptions[] videoOptions = new EnumOptions[] {EnumOptions.GRAPHICS, EnumOptions.RENDER_DISTANCE, EnumOptions.AMBIENT_OCCLUSION, EnumOptions.FRAMERATE_LIMIT, EnumOptions.ANAGLYPH, EnumOptions.VIEW_BOBBING, EnumOptions.GUI_SCALE, EnumOptions.ADVANCED_OPENGL, EnumOptions.GAMMA, EnumOptions.RENDER_CLOUDS, EnumOptions.PARTICLES};

    public GuiVideoSettings(GuiScreen par1GuiScreen, GameSettings par2GameSettings)
    {
        this.parentGuiScreen = par1GuiScreen;
        this.guiGameSettings = par2GameSettings;
    }

    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    public void initGui()
    {
        StringTranslate var1 = StringTranslate.getInstance();
        this.screenTitle = var1.translateKey("options.videoTitle");
        int var2 = 0;
        EnumOptions[] var3 = videoOptions;
        int var4 = var3.length;
        int var5;

        for (var5 = 0; var5 < var4; ++var5)
        {
            EnumOptions var6 = var3[var5];

            if (!var6.getEnumFloat())
            {
                this.controlList.add(new GuiSmallButton(var6.returnEnumOrdinal(), this.width / 2 - 155 + var2 % 2 * 160, this.height / 6 + 24 * (var2 >> 1), var6, this.guiGameSettings.getKeyBinding(var6)));
            }
            else
            {
                this.controlList.add(new GuiSlider(var6.returnEnumOrdinal(), this.width / 2 - 155 + var2 % 2 * 160, this.height / 6 + 24 * (var2 >> 1), var6, this.guiGameSettings.getKeyBinding(var6), this.guiGameSettings.getOptionFloatValue(var6)));
            }

            ++var2;
        }

        this.controlList.add(new GuiButton(200, this.width / 2 - 100, this.height / 6 + 168, var1.translateKey("gui.done")));
        this.is64bit = false;
        String[] var9 = new String[] {"sun.arch.data.model", "com.ibm.vm.bitmode", "os.arch"};
        String[] var10 = var9;
        var5 = var9.length;

        for (int var11 = 0; var11 < var5; ++var11)
        {
            String var7 = var10[var11];
            String var8 = System.getProperty(var7);

            if (var8 != null && var8.indexOf("64") >= 0)
            {
                this.is64bit = true;
                break;
            }
        }
    }

    /**
     * Fired when a control is clicked. This is the equivalent of ActionListener.actionPerformed(ActionEvent e).
     */
    protected void actionPerformed(GuiButton par1GuiButton)
    {
        if (par1GuiButton.enabled)
        {
            int var2 = this.guiGameSettings.guiScale;

            if (par1GuiButton.id < 100 && par1GuiButton instanceof GuiSmallButton)
            {
                this.guiGameSettings.setOptionValue(((GuiSmallButton)par1GuiButton).returnEnumOptions(), 1);
                par1GuiButton.displayString = this.guiGameSettings.getKeyBinding(EnumOptions.getEnumOptions(par1GuiButton.id));
            }

            if (par1GuiButton.id == 200)
            {
                this.mc.gameSettings.saveOptions();
                this.mc.displayGuiScreen(this.parentGuiScreen);
            }

            if (this.guiGameSettings.guiScale != var2)
            {
                ScaledResolution var3 = new ScaledResolution(this.mc.gameSettings, this.mc.displayWidth, this.mc.displayHeight);
                int var4 = var3.getScaledWidth();
                int var5 = var3.getScaledHeight();
                this.setWorldAndResolution(this.mc, var4, var5);
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

        if (!this.is64bit && this.guiGameSettings.renderDistance == 0)
        {
            this.drawCenteredString(this.fontRenderer, StatCollector.translateToLocal("options.farWarning1"), this.width / 2, this.height / 6 + 144, 11468800);
            this.drawCenteredString(this.fontRenderer, StatCollector.translateToLocal("options.farWarning2"), this.width / 2, this.height / 6 + 144 + 12, 11468800);
        }

        super.drawScreen(par1, par2, par3);
    }
}
