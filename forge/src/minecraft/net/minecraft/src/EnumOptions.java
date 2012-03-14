package net.minecraft.src;

public enum EnumOptions
{
    MUSIC("options.music", true, false),
    SOUND("options.sound", true, false),
    INVERT_MOUSE("options.invertMouse", false, true),
    SENSITIVITY("options.sensitivity", true, false),
    FOV("options.fov", true, false),
    GAMMA("options.gamma", true, false),
    RENDER_DISTANCE("options.renderDistance", false, false),
    VIEW_BOBBING("options.viewBobbing", false, true),
    ANAGLYPH("options.anaglyph", false, true),
    ADVANCED_OPENGL("options.advancedOpengl", false, true),
    FRAMERATE_LIMIT("options.framerateLimit", false, false),
    DIFFICULTY("options.difficulty", false, false),
    GRAPHICS("options.graphics", false, false),
    AMBIENT_OCCLUSION("options.ao", false, true),
    GUI_SCALE("options.guiScale", false, false),
    RENDER_CLOUDS("options.renderClouds", false, true),
    PARTICLES("options.particles", false, false);
    private final boolean enumFloat;
    private final boolean enumBoolean;
    private final String enumString;

    public static EnumOptions getEnumOptions(int par0)
    {
        EnumOptions[] var1 = values();
        int var2 = var1.length;

        for (int var3 = 0; var3 < var2; ++var3)
        {
            EnumOptions var4 = var1[var3];

            if (var4.returnEnumOrdinal() == par0)
            {
                return var4;
            }
        }

        return null;
    }

    private EnumOptions(String par3Str, boolean par4, boolean par5)
    {
        this.enumString = par3Str;
        this.enumFloat = par4;
        this.enumBoolean = par5;
    }

    public boolean getEnumFloat()
    {
        return this.enumFloat;
    }

    public boolean getEnumBoolean()
    {
        return this.enumBoolean;
    }

    public int returnEnumOrdinal()
    {
        return this.ordinal();
    }

    public String getEnumString()
    {
        return this.enumString;
    }
}
