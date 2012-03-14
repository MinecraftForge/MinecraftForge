package net.minecraft.src;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import net.minecraft.client.Minecraft;
import org.lwjgl.input.Keyboard;

public class GameSettings
{
    private static final String[] RENDER_DISTANCES = new String[] {"options.renderDistance.far", "options.renderDistance.normal", "options.renderDistance.short", "options.renderDistance.tiny"};
    private static final String[] DIFFICULTIES = new String[] {"options.difficulty.peaceful", "options.difficulty.easy", "options.difficulty.normal", "options.difficulty.hard"};

    /** GUI scale values */
    private static final String[] GUISCALES = new String[] {"options.guiScale.auto", "options.guiScale.small", "options.guiScale.normal", "options.guiScale.large"};
    private static final String[] PARTICLES = new String[] {"options.particles.all", "options.particles.decreased", "options.particles.minimal"};

    /** Limit framerate labels */
    private static final String[] LIMIT_FRAMERATES = new String[] {"performance.max", "performance.balanced", "performance.powersaver"};
    public float musicVolume = 1.0F;
    public float soundVolume = 1.0F;
    public float mouseSensitivity = 0.5F;
    public boolean invertMouse = false;
    public int renderDistance = 0;
    public boolean viewBobbing = true;
    public boolean anaglyph = false;

    /** Advanced OpenGL */
    public boolean advancedOpengl = false;
    public int limitFramerate = 1;
    public boolean fancyGraphics = true;

    /** Smooth Lighting */
    public boolean ambientOcclusion = true;

    /** Clouds flag */
    public boolean clouds = true;

    /** The name of the selected texture pack. */
    public String skin = "Default";
    public KeyBinding keyBindForward = new KeyBinding("key.forward", 17);
    public KeyBinding keyBindLeft = new KeyBinding("key.left", 30);
    public KeyBinding keyBindBack = new KeyBinding("key.back", 31);
    public KeyBinding keyBindRight = new KeyBinding("key.right", 32);
    public KeyBinding keyBindJump = new KeyBinding("key.jump", 57);
    public KeyBinding keyBindInventory = new KeyBinding("key.inventory", 18);
    public KeyBinding keyBindDrop = new KeyBinding("key.drop", 16);
    public KeyBinding keyBindChat = new KeyBinding("key.chat", 20);
    public KeyBinding keyBindSneak = new KeyBinding("key.sneak", 42);
    public KeyBinding keyBindAttack = new KeyBinding("key.attack", -100);
    public KeyBinding keyBindUseItem = new KeyBinding("key.use", -99);
    public KeyBinding keyBindPlayerList = new KeyBinding("key.playerlist", 15);
    public KeyBinding keyBindPickBlock = new KeyBinding("key.pickItem", -98);
    public KeyBinding[] keyBindings;
    protected Minecraft mc;
    private File optionsFile;
    public int difficulty;
    public boolean hideGUI;
    public int thirdPersonView;

    /** true if debug info should be displayed instead of version */
    public boolean showDebugInfo;

    /** The lastServer string. */
    public String lastServer;

    /** No clipping for singleplayer */
    public boolean noclip;

    /** Smooth Camera Toggle */
    public boolean smoothCamera;
    public boolean debugCamEnable;

    /** No clipping movement rate */
    public float noclipRate;

    /** Change rate for debug camera */
    public float debugCamRate;
    public float fovSetting;
    public float gammaSetting;

    /** GUI scale */
    public int guiScale;

    /** Determines amount of particles. 0 = All, 1 = Decreased, 2 = Minimal */
    public int particleSetting;

    /** Game settings language */
    public String language;

    public GameSettings(Minecraft par1Minecraft, File par2File)
    {
        this.keyBindings = new KeyBinding[] {this.keyBindAttack, this.keyBindUseItem, this.keyBindForward, this.keyBindLeft, this.keyBindBack, this.keyBindRight, this.keyBindJump, this.keyBindSneak, this.keyBindDrop, this.keyBindInventory, this.keyBindChat, this.keyBindPlayerList, this.keyBindPickBlock};
        this.difficulty = 2;
        this.hideGUI = false;
        this.thirdPersonView = 0;
        this.showDebugInfo = false;
        this.lastServer = "";
        this.noclip = false;
        this.smoothCamera = false;
        this.debugCamEnable = false;
        this.noclipRate = 1.0F;
        this.debugCamRate = 1.0F;
        this.fovSetting = 0.0F;
        this.gammaSetting = 0.0F;
        this.guiScale = 0;
        this.particleSetting = 0;
        this.language = "en_US";
        this.mc = par1Minecraft;
        this.optionsFile = new File(par2File, "options.txt");
        this.loadOptions();
    }

    public GameSettings()
    {
        this.keyBindings = new KeyBinding[] {this.keyBindAttack, this.keyBindUseItem, this.keyBindForward, this.keyBindLeft, this.keyBindBack, this.keyBindRight, this.keyBindJump, this.keyBindSneak, this.keyBindDrop, this.keyBindInventory, this.keyBindChat, this.keyBindPlayerList, this.keyBindPickBlock};
        this.difficulty = 2;
        this.hideGUI = false;
        this.thirdPersonView = 0;
        this.showDebugInfo = false;
        this.lastServer = "";
        this.noclip = false;
        this.smoothCamera = false;
        this.debugCamEnable = false;
        this.noclipRate = 1.0F;
        this.debugCamRate = 1.0F;
        this.fovSetting = 0.0F;
        this.gammaSetting = 0.0F;
        this.guiScale = 0;
        this.particleSetting = 0;
        this.language = "en_US";
    }

    public String getKeyBindingDescription(int par1)
    {
        StringTranslate var2 = StringTranslate.getInstance();
        return var2.translateKey(this.keyBindings[par1].keyDescription);
    }

    /**
     * The string that appears inside the button/slider in the options menu.
     */
    public String getOptionDisplayString(int par1)
    {
        int var2 = this.keyBindings[par1].keyCode;
        return getKeyDisplayString(var2);
    }

    /**
     * Represents a key or mouse button as a string. Args: key
     */
    public static String getKeyDisplayString(int par0)
    {
        return par0 < 0 ? StatCollector.translateToLocalFormatted("key.mouseButton", new Object[] {Integer.valueOf(par0 + 101)}): Keyboard.getKeyName(par0);
    }

    /**
     * Sets a key binding.
     */
    public void setKeyBinding(int par1, int par2)
    {
        this.keyBindings[par1].keyCode = par2;
        this.saveOptions();
    }

    /**
     * If the specified option is controlled by a slider (float value), this will set the float value.
     */
    public void setOptionFloatValue(EnumOptions par1EnumOptions, float par2)
    {
        if (par1EnumOptions == EnumOptions.MUSIC)
        {
            this.musicVolume = par2;
            this.mc.sndManager.onSoundOptionsChanged();
        }

        if (par1EnumOptions == EnumOptions.SOUND)
        {
            this.soundVolume = par2;
            this.mc.sndManager.onSoundOptionsChanged();
        }

        if (par1EnumOptions == EnumOptions.SENSITIVITY)
        {
            this.mouseSensitivity = par2;
        }

        if (par1EnumOptions == EnumOptions.FOV)
        {
            this.fovSetting = par2;
        }

        if (par1EnumOptions == EnumOptions.GAMMA)
        {
            this.gammaSetting = par2;
        }
    }

    /**
     * For non-float options. Toggles the option on/off, or cycles through the list i.e. render distances.
     */
    public void setOptionValue(EnumOptions par1EnumOptions, int par2)
    {
        if (par1EnumOptions == EnumOptions.INVERT_MOUSE)
        {
            this.invertMouse = !this.invertMouse;
        }

        if (par1EnumOptions == EnumOptions.RENDER_DISTANCE)
        {
            this.renderDistance = this.renderDistance + par2 & 3;
        }

        if (par1EnumOptions == EnumOptions.GUI_SCALE)
        {
            this.guiScale = this.guiScale + par2 & 3;
        }

        if (par1EnumOptions == EnumOptions.PARTICLES)
        {
            this.particleSetting = (this.particleSetting + par2) % 3;
        }

        if (par1EnumOptions == EnumOptions.VIEW_BOBBING)
        {
            this.viewBobbing = !this.viewBobbing;
        }

        if (par1EnumOptions == EnumOptions.RENDER_CLOUDS)
        {
            this.clouds = !this.clouds;
        }

        if (par1EnumOptions == EnumOptions.ADVANCED_OPENGL)
        {
            this.advancedOpengl = !this.advancedOpengl;
            this.mc.renderGlobal.loadRenderers();
        }

        if (par1EnumOptions == EnumOptions.ANAGLYPH)
        {
            this.anaglyph = !this.anaglyph;
            this.mc.renderEngine.refreshTextures();
        }

        if (par1EnumOptions == EnumOptions.FRAMERATE_LIMIT)
        {
            this.limitFramerate = (this.limitFramerate + par2 + 3) % 3;
        }

        if (par1EnumOptions == EnumOptions.DIFFICULTY)
        {
            this.difficulty = this.difficulty + par2 & 3;
        }

        if (par1EnumOptions == EnumOptions.GRAPHICS)
        {
            this.fancyGraphics = !this.fancyGraphics;
            this.mc.renderGlobal.loadRenderers();
        }

        if (par1EnumOptions == EnumOptions.AMBIENT_OCCLUSION)
        {
            this.ambientOcclusion = !this.ambientOcclusion;
            this.mc.renderGlobal.loadRenderers();
        }

        this.saveOptions();
    }

    public float getOptionFloatValue(EnumOptions par1EnumOptions)
    {
        return par1EnumOptions == EnumOptions.FOV ? this.fovSetting : (par1EnumOptions == EnumOptions.GAMMA ? this.gammaSetting : (par1EnumOptions == EnumOptions.MUSIC ? this.musicVolume : (par1EnumOptions == EnumOptions.SOUND ? this.soundVolume : (par1EnumOptions == EnumOptions.SENSITIVITY ? this.mouseSensitivity : 0.0F))));
    }

    public boolean getOptionOrdinalValue(EnumOptions par1EnumOptions)
    {
        switch (EnumOptionsMappingHelper.enumOptionsMappingHelperArray[par1EnumOptions.ordinal()])
        {
            case 1:
                return this.invertMouse;

            case 2:
                return this.viewBobbing;

            case 3:
                return this.anaglyph;

            case 4:
                return this.advancedOpengl;

            case 5:
                return this.ambientOcclusion;

            case 6:
                return this.clouds;

            default:
                return false;
        }
    }

    private static String func_48571_a(String[] par0ArrayOfStr, int par1)
    {
        if (par1 < 0 || par1 >= par0ArrayOfStr.length)
        {
            par1 = 0;
        }

        StringTranslate var2 = StringTranslate.getInstance();
        return var2.translateKey(par0ArrayOfStr[par1]);
    }

    /**
     * Gets a key binding.
     */
    public String getKeyBinding(EnumOptions par1EnumOptions)
    {
        StringTranslate var2 = StringTranslate.getInstance();
        String var3 = var2.translateKey(par1EnumOptions.getEnumString()) + ": ";

        if (par1EnumOptions.getEnumFloat())
        {
            float var5 = this.getOptionFloatValue(par1EnumOptions);
            return par1EnumOptions == EnumOptions.SENSITIVITY ? (var5 == 0.0F ? var3 + var2.translateKey("options.sensitivity.min") : (var5 == 1.0F ? var3 + var2.translateKey("options.sensitivity.max") : var3 + (int)(var5 * 200.0F) + "%")) : (par1EnumOptions == EnumOptions.FOV ? (var5 == 0.0F ? var3 + var2.translateKey("options.fov.min") : (var5 == 1.0F ? var3 + var2.translateKey("options.fov.max") : var3 + (int)(70.0F + var5 * 40.0F))) : (par1EnumOptions == EnumOptions.GAMMA ? (var5 == 0.0F ? var3 + var2.translateKey("options.gamma.min") : (var5 == 1.0F ? var3 + var2.translateKey("options.gamma.max") : var3 + "+" + (int)(var5 * 100.0F) + "%")) : (var5 == 0.0F ? var3 + var2.translateKey("options.off") : var3 + (int)(var5 * 100.0F) + "%")));
        }
        else if (par1EnumOptions.getEnumBoolean())
        {
            boolean var4 = this.getOptionOrdinalValue(par1EnumOptions);
            return var4 ? var3 + var2.translateKey("options.on") : var3 + var2.translateKey("options.off");
        }
        else
        {
            return par1EnumOptions == EnumOptions.RENDER_DISTANCE ? var3 + func_48571_a(RENDER_DISTANCES, this.renderDistance) : (par1EnumOptions == EnumOptions.DIFFICULTY ? var3 + func_48571_a(DIFFICULTIES, this.difficulty) : (par1EnumOptions == EnumOptions.GUI_SCALE ? var3 + func_48571_a(GUISCALES, this.guiScale) : (par1EnumOptions == EnumOptions.PARTICLES ? var3 + func_48571_a(PARTICLES, this.particleSetting) : (par1EnumOptions == EnumOptions.FRAMERATE_LIMIT ? var3 + func_48571_a(LIMIT_FRAMERATES, this.limitFramerate) : (par1EnumOptions == EnumOptions.GRAPHICS ? (this.fancyGraphics ? var3 + var2.translateKey("options.graphics.fancy") : var3 + var2.translateKey("options.graphics.fast")) : var3)))));
        }
    }

    /**
     * Loads the options from the options file. It appears that this has replaced the previous 'loadOptions'
     */
    public void loadOptions()
    {
        try
        {
            if (!this.optionsFile.exists())
            {
                return;
            }

            BufferedReader var1 = new BufferedReader(new FileReader(this.optionsFile));
            String var2 = "";

            while ((var2 = var1.readLine()) != null)
            {
                try
                {
                    String[] var3 = var2.split(":");

                    if (var3[0].equals("music"))
                    {
                        this.musicVolume = this.parseFloat(var3[1]);
                    }

                    if (var3[0].equals("sound"))
                    {
                        this.soundVolume = this.parseFloat(var3[1]);
                    }

                    if (var3[0].equals("mouseSensitivity"))
                    {
                        this.mouseSensitivity = this.parseFloat(var3[1]);
                    }

                    if (var3[0].equals("fov"))
                    {
                        this.fovSetting = this.parseFloat(var3[1]);
                    }

                    if (var3[0].equals("gamma"))
                    {
                        this.gammaSetting = this.parseFloat(var3[1]);
                    }

                    if (var3[0].equals("invertYMouse"))
                    {
                        this.invertMouse = var3[1].equals("true");
                    }

                    if (var3[0].equals("viewDistance"))
                    {
                        this.renderDistance = Integer.parseInt(var3[1]);
                    }

                    if (var3[0].equals("guiScale"))
                    {
                        this.guiScale = Integer.parseInt(var3[1]);
                    }

                    if (var3[0].equals("particles"))
                    {
                        this.particleSetting = Integer.parseInt(var3[1]);
                    }

                    if (var3[0].equals("bobView"))
                    {
                        this.viewBobbing = var3[1].equals("true");
                    }

                    if (var3[0].equals("anaglyph3d"))
                    {
                        this.anaglyph = var3[1].equals("true");
                    }

                    if (var3[0].equals("advancedOpengl"))
                    {
                        this.advancedOpengl = var3[1].equals("true");
                    }

                    if (var3[0].equals("fpsLimit"))
                    {
                        this.limitFramerate = Integer.parseInt(var3[1]);
                    }

                    if (var3[0].equals("difficulty"))
                    {
                        this.difficulty = Integer.parseInt(var3[1]);
                    }

                    if (var3[0].equals("fancyGraphics"))
                    {
                        this.fancyGraphics = var3[1].equals("true");
                    }

                    if (var3[0].equals("ao"))
                    {
                        this.ambientOcclusion = var3[1].equals("true");
                    }

                    if (var3[0].equals("clouds"))
                    {
                        this.clouds = var3[1].equals("true");
                    }

                    if (var3[0].equals("skin"))
                    {
                        this.skin = var3[1];
                    }

                    if (var3[0].equals("lastServer") && var3.length >= 2)
                    {
                        this.lastServer = var3[1];
                    }

                    if (var3[0].equals("lang") && var3.length >= 2)
                    {
                        this.language = var3[1];
                    }

                    for (int var4 = 0; var4 < this.keyBindings.length; ++var4)
                    {
                        if (var3[0].equals("key_" + this.keyBindings[var4].keyDescription))
                        {
                            this.keyBindings[var4].keyCode = Integer.parseInt(var3[1]);
                        }
                    }
                }
                catch (Exception var5)
                {
                    System.out.println("Skipping bad option: " + var2);
                }
            }

            KeyBinding.resetKeyBindingArrayAndHash();
            var1.close();
        }
        catch (Exception var6)
        {
            System.out.println("Failed to load options");
            var6.printStackTrace();
        }
    }

    /**
     * Parses a string into a float.
     */
    private float parseFloat(String par1Str)
    {
        return par1Str.equals("true") ? 1.0F : (par1Str.equals("false") ? 0.0F : Float.parseFloat(par1Str));
    }

    /**
     * Saves the options to the options file.
     */
    public void saveOptions()
    {
        try
        {
            PrintWriter var1 = new PrintWriter(new FileWriter(this.optionsFile));
            var1.println("music:" + this.musicVolume);
            var1.println("sound:" + this.soundVolume);
            var1.println("invertYMouse:" + this.invertMouse);
            var1.println("mouseSensitivity:" + this.mouseSensitivity);
            var1.println("fov:" + this.fovSetting);
            var1.println("gamma:" + this.gammaSetting);
            var1.println("viewDistance:" + this.renderDistance);
            var1.println("guiScale:" + this.guiScale);
            var1.println("particles:" + this.particleSetting);
            var1.println("bobView:" + this.viewBobbing);
            var1.println("anaglyph3d:" + this.anaglyph);
            var1.println("advancedOpengl:" + this.advancedOpengl);
            var1.println("fpsLimit:" + this.limitFramerate);
            var1.println("difficulty:" + this.difficulty);
            var1.println("fancyGraphics:" + this.fancyGraphics);
            var1.println("ao:" + this.ambientOcclusion);
            var1.println("clouds:" + this.clouds);
            var1.println("skin:" + this.skin);
            var1.println("lastServer:" + this.lastServer);
            var1.println("lang:" + this.language);

            for (int var2 = 0; var2 < this.keyBindings.length; ++var2)
            {
                var1.println("key_" + this.keyBindings[var2].keyDescription + ":" + this.keyBindings[var2].keyCode);
            }

            var1.close();
        }
        catch (Exception var3)
        {
            System.out.println("Failed to save options");
            var3.printStackTrace();
        }
    }

    /**
     * Should render clouds
     */
    public boolean shouldRenderClouds()
    {
        return this.renderDistance < 2 && this.clouds;
    }
}
