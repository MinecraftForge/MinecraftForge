package net.minecraft.client.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.Random;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.MathHelper;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.WorldInfo;
import org.lwjgl.input.Keyboard;

@SideOnly(Side.CLIENT)
public class GuiCreateWorld extends GuiScreen
{
    private GuiScreen field_146332_f;
    private GuiTextField field_146333_g;
    private GuiTextField field_146335_h;
    private String field_146336_i;
    private String field_146342_r = "survival";
    private boolean field_146341_s = true;
    private boolean field_146340_t;
    private boolean field_146339_u;
    private boolean field_146338_v;
    private boolean field_146337_w;
    private boolean field_146345_x;
    private boolean field_146344_y;
    private GuiButton field_146343_z;
    private GuiButton field_146324_A;
    private GuiButton field_146325_B;
    private GuiButton field_146326_C;
    private GuiButton field_146320_D;
    private GuiButton field_146321_E;
    private GuiButton field_146322_F;
    private String field_146323_G;
    private String field_146328_H;
    private String field_146329_I;
    private String field_146330_J;
    private int field_146331_K;
    public String field_146334_a = "";
    private static final String[] field_146327_L = new String[] {"CON", "COM", "PRN", "AUX", "CLOCK$", "NUL", "COM1", "COM2", "COM3", "COM4", "COM5", "COM6", "COM7", "COM8", "COM9", "LPT1", "LPT2", "LPT3", "LPT4", "LPT5", "LPT6", "LPT7", "LPT8", "LPT9"};
    private static final String __OBFID = "CL_00000689";

    public GuiCreateWorld(GuiScreen par1GuiScreen)
    {
        this.field_146332_f = par1GuiScreen;
        this.field_146329_I = "";
        this.field_146330_J = I18n.getStringParams("selectWorld.newWorld", new Object[0]);
    }

    // JAVADOC METHOD $$ func_73876_c
    public void updateScreen()
    {
        this.field_146333_g.func_146178_a();
        this.field_146335_h.func_146178_a();
    }

    // JAVADOC METHOD $$ func_73866_w_
    public void initGui()
    {
        Keyboard.enableRepeatEvents(true);
        this.field_146292_n.clear();
        this.field_146292_n.add(new GuiButton(0, this.field_146294_l / 2 - 155, this.field_146295_m - 28, 150, 20, I18n.getStringParams("selectWorld.create", new Object[0])));
        this.field_146292_n.add(new GuiButton(1, this.field_146294_l / 2 + 5, this.field_146295_m - 28, 150, 20, I18n.getStringParams("gui.cancel", new Object[0])));
        this.field_146292_n.add(this.field_146343_z = new GuiButton(2, this.field_146294_l / 2 - 75, 115, 150, 20, I18n.getStringParams("selectWorld.gameMode", new Object[0])));
        this.field_146292_n.add(this.field_146324_A = new GuiButton(3, this.field_146294_l / 2 - 75, 187, 150, 20, I18n.getStringParams("selectWorld.moreWorldOptions", new Object[0])));
        this.field_146292_n.add(this.field_146325_B = new GuiButton(4, this.field_146294_l / 2 - 155, 100, 150, 20, I18n.getStringParams("selectWorld.mapFeatures", new Object[0])));
        this.field_146325_B.field_146125_m = false;
        this.field_146292_n.add(this.field_146326_C = new GuiButton(7, this.field_146294_l / 2 + 5, 151, 150, 20, I18n.getStringParams("selectWorld.bonusItems", new Object[0])));
        this.field_146326_C.field_146125_m = false;
        this.field_146292_n.add(this.field_146320_D = new GuiButton(5, this.field_146294_l / 2 + 5, 100, 150, 20, I18n.getStringParams("selectWorld.mapType", new Object[0])));
        this.field_146320_D.field_146125_m = false;
        this.field_146292_n.add(this.field_146321_E = new GuiButton(6, this.field_146294_l / 2 - 155, 151, 150, 20, I18n.getStringParams("selectWorld.allowCommands", new Object[0])));
        this.field_146321_E.field_146125_m = false;
        this.field_146292_n.add(this.field_146322_F = new GuiButton(8, this.field_146294_l / 2 + 5, 120, 150, 20, I18n.getStringParams("selectWorld.customizeType", new Object[0])));
        this.field_146322_F.field_146125_m = false;
        this.field_146333_g = new GuiTextField(this.field_146289_q, this.field_146294_l / 2 - 100, 60, 200, 20);
        this.field_146333_g.func_146195_b(true);
        this.field_146333_g.func_146180_a(this.field_146330_J);
        this.field_146335_h = new GuiTextField(this.field_146289_q, this.field_146294_l / 2 - 100, 60, 200, 20);
        this.field_146335_h.func_146180_a(this.field_146329_I);
        this.func_146316_a(this.field_146344_y);
        this.func_146314_g();
        this.func_146319_h();
    }

    private void func_146314_g()
    {
        this.field_146336_i = this.field_146333_g.func_146179_b().trim();
        char[] achar = ChatAllowedCharacters.allowedCharactersArray;
        int i = achar.length;

        for (int j = 0; j < i; ++j)
        {
            char c0 = achar[j];
            this.field_146336_i = this.field_146336_i.replace(c0, '_');
        }

        if (MathHelper.stringNullOrLengthZero(this.field_146336_i))
        {
            this.field_146336_i = "World";
        }

        this.field_146336_i = func_146317_a(this.field_146297_k.getSaveLoader(), this.field_146336_i);
    }

    private void func_146319_h()
    {
        this.field_146343_z.field_146126_j = I18n.getStringParams("selectWorld.gameMode", new Object[0]) + " " + I18n.getStringParams("selectWorld.gameMode." + this.field_146342_r, new Object[0]);
        this.field_146323_G = I18n.getStringParams("selectWorld.gameMode." + this.field_146342_r + ".line1", new Object[0]);
        this.field_146328_H = I18n.getStringParams("selectWorld.gameMode." + this.field_146342_r + ".line2", new Object[0]);
        this.field_146325_B.field_146126_j = I18n.getStringParams("selectWorld.mapFeatures", new Object[0]) + " ";

        if (this.field_146341_s)
        {
            this.field_146325_B.field_146126_j = this.field_146325_B.field_146126_j + I18n.getStringParams("options.on", new Object[0]);
        }
        else
        {
            this.field_146325_B.field_146126_j = this.field_146325_B.field_146126_j + I18n.getStringParams("options.off", new Object[0]);
        }

        this.field_146326_C.field_146126_j = I18n.getStringParams("selectWorld.bonusItems", new Object[0]) + " ";

        if (this.field_146338_v && !this.field_146337_w)
        {
            this.field_146326_C.field_146126_j = this.field_146326_C.field_146126_j + I18n.getStringParams("options.on", new Object[0]);
        }
        else
        {
            this.field_146326_C.field_146126_j = this.field_146326_C.field_146126_j + I18n.getStringParams("options.off", new Object[0]);
        }

        this.field_146320_D.field_146126_j = I18n.getStringParams("selectWorld.mapType", new Object[0]) + " " + I18n.getStringParams(WorldType.worldTypes[this.field_146331_K].getTranslateName(), new Object[0]);
        this.field_146321_E.field_146126_j = I18n.getStringParams("selectWorld.allowCommands", new Object[0]) + " ";

        if (this.field_146340_t && !this.field_146337_w)
        {
            this.field_146321_E.field_146126_j = this.field_146321_E.field_146126_j + I18n.getStringParams("options.on", new Object[0]);
        }
        else
        {
            this.field_146321_E.field_146126_j = this.field_146321_E.field_146126_j + I18n.getStringParams("options.off", new Object[0]);
        }
    }

    public static String func_146317_a(ISaveFormat p_146317_0_, String p_146317_1_)
    {
        p_146317_1_ = p_146317_1_.replaceAll("[\\./\"]", "_");
        String[] astring = field_146327_L;
        int i = astring.length;

        for (int j = 0; j < i; ++j)
        {
            String s1 = astring[j];

            if (p_146317_1_.equalsIgnoreCase(s1))
            {
                p_146317_1_ = "_" + p_146317_1_ + "_";
            }
        }

        while (p_146317_0_.getWorldInfo(p_146317_1_) != null)
        {
            p_146317_1_ = p_146317_1_ + "-";
        }

        return p_146317_1_;
    }

    public void func_146281_b()
    {
        Keyboard.enableRepeatEvents(false);
    }

    protected void func_146284_a(GuiButton p_146284_1_)
    {
        if (p_146284_1_.field_146124_l)
        {
            if (p_146284_1_.field_146127_k == 1)
            {
                this.field_146297_k.func_147108_a(this.field_146332_f);
            }
            else if (p_146284_1_.field_146127_k == 0)
            {
                this.field_146297_k.func_147108_a((GuiScreen)null);

                if (this.field_146345_x)
                {
                    return;
                }

                this.field_146345_x = true;
                long i = (new Random()).nextLong();
                String s = this.field_146335_h.func_146179_b();

                if (!MathHelper.stringNullOrLengthZero(s))
                {
                    try
                    {
                        long j = Long.parseLong(s);

                        if (j != 0L)
                        {
                            i = j;
                        }
                    }
                    catch (NumberFormatException numberformatexception)
                    {
                        i = (long)s.hashCode();
                    }
                }

                WorldType.worldTypes[this.field_146331_K].onGUICreateWorldPress();

                WorldSettings.GameType gametype = WorldSettings.GameType.getByName(this.field_146342_r);
                WorldSettings worldsettings = new WorldSettings(i, gametype, this.field_146341_s, this.field_146337_w, WorldType.worldTypes[this.field_146331_K]);
                worldsettings.func_82750_a(this.field_146334_a);

                if (this.field_146338_v && !this.field_146337_w)
                {
                    worldsettings.enableBonusChest();
                }

                if (this.field_146340_t && !this.field_146337_w)
                {
                    worldsettings.enableCommands();
                }

                this.field_146297_k.launchIntegratedServer(this.field_146336_i, this.field_146333_g.func_146179_b().trim(), worldsettings);
            }
            else if (p_146284_1_.field_146127_k == 3)
            {
                this.func_146315_i();
            }
            else if (p_146284_1_.field_146127_k == 2)
            {
                if (this.field_146342_r.equals("survival"))
                {
                    if (!this.field_146339_u)
                    {
                        this.field_146340_t = false;
                    }

                    this.field_146337_w = false;
                    this.field_146342_r = "hardcore";
                    this.field_146337_w = true;
                    this.field_146321_E.field_146124_l = false;
                    this.field_146326_C.field_146124_l = false;
                    this.func_146319_h();
                }
                else if (this.field_146342_r.equals("hardcore"))
                {
                    if (!this.field_146339_u)
                    {
                        this.field_146340_t = true;
                    }

                    this.field_146337_w = false;
                    this.field_146342_r = "creative";
                    this.func_146319_h();
                    this.field_146337_w = false;
                    this.field_146321_E.field_146124_l = true;
                    this.field_146326_C.field_146124_l = true;
                }
                else
                {
                    if (!this.field_146339_u)
                    {
                        this.field_146340_t = false;
                    }

                    this.field_146342_r = "survival";
                    this.func_146319_h();
                    this.field_146321_E.field_146124_l = true;
                    this.field_146326_C.field_146124_l = true;
                    this.field_146337_w = false;
                }

                this.func_146319_h();
            }
            else if (p_146284_1_.field_146127_k == 4)
            {
                this.field_146341_s = !this.field_146341_s;
                this.func_146319_h();
            }
            else if (p_146284_1_.field_146127_k == 7)
            {
                this.field_146338_v = !this.field_146338_v;
                this.func_146319_h();
            }
            else if (p_146284_1_.field_146127_k == 5)
            {
                ++this.field_146331_K;

                if (this.field_146331_K >= WorldType.worldTypes.length)
                {
                    this.field_146331_K = 0;
                }

                while (WorldType.worldTypes[this.field_146331_K] == null || !WorldType.worldTypes[this.field_146331_K].getCanBeCreated())
                {
                    ++this.field_146331_K;

                    if (this.field_146331_K >= WorldType.worldTypes.length)
                    {
                        this.field_146331_K = 0;
                    }
                }

                this.field_146334_a = "";
                this.func_146319_h();
                this.func_146316_a(this.field_146344_y);
            }
            else if (p_146284_1_.field_146127_k == 6)
            {
                this.field_146339_u = true;
                this.field_146340_t = !this.field_146340_t;
                this.func_146319_h();
            }
            else if (p_146284_1_.field_146127_k == 8)
            {
                WorldType.worldTypes[field_146331_K].onCustomizeButton(field_146297_k, this);
            }
        }
    }

    private void func_146315_i()
    {
        this.func_146316_a(!this.field_146344_y);
    }

    private void func_146316_a(boolean p_146316_1_)
    {
        this.field_146344_y = p_146316_1_;
        this.field_146343_z.field_146125_m = !this.field_146344_y;
        this.field_146325_B.field_146125_m = this.field_146344_y;
        this.field_146326_C.field_146125_m = this.field_146344_y;
        this.field_146320_D.field_146125_m = this.field_146344_y;
        this.field_146321_E.field_146125_m = this.field_146344_y;
        this.field_146322_F.field_146125_m = this.field_146344_y && WorldType.worldTypes[this.field_146331_K].isCustomizable();

        if (this.field_146344_y)
        {
            this.field_146324_A.field_146126_j = I18n.getStringParams("gui.done", new Object[0]);
        }
        else
        {
            this.field_146324_A.field_146126_j = I18n.getStringParams("selectWorld.moreWorldOptions", new Object[0]);
        }
    }

    // JAVADOC METHOD $$ func_73869_a
    protected void keyTyped(char par1, int par2)
    {
        if (this.field_146333_g.func_146206_l() && !this.field_146344_y)
        {
            this.field_146333_g.func_146201_a(par1, par2);
            this.field_146330_J = this.field_146333_g.func_146179_b();
        }
        else if (this.field_146335_h.func_146206_l() && this.field_146344_y)
        {
            this.field_146335_h.func_146201_a(par1, par2);
            this.field_146329_I = this.field_146335_h.func_146179_b();
        }

        if (par2 == 28 || par2 == 156)
        {
            this.func_146284_a((GuiButton)this.field_146292_n.get(0));
        }

        ((GuiButton)this.field_146292_n.get(0)).field_146124_l = this.field_146333_g.func_146179_b().length() > 0;
        this.func_146314_g();
    }

    // JAVADOC METHOD $$ func_73864_a
    protected void mouseClicked(int par1, int par2, int par3)
    {
        super.mouseClicked(par1, par2, par3);

        if (this.field_146344_y)
        {
            this.field_146335_h.func_146192_a(par1, par2, par3);
        }
        else
        {
            this.field_146333_g.func_146192_a(par1, par2, par3);
        }
    }

    // JAVADOC METHOD $$ func_73863_a
    public void drawScreen(int par1, int par2, float par3)
    {
        this.func_146276_q_();
        this.drawCenteredString(this.field_146289_q, I18n.getStringParams("selectWorld.create", new Object[0]), this.field_146294_l / 2, 20, -1);

        if (this.field_146344_y)
        {
            this.drawString(this.field_146289_q, I18n.getStringParams("selectWorld.enterSeed", new Object[0]), this.field_146294_l / 2 - 100, 47, -6250336);
            this.drawString(this.field_146289_q, I18n.getStringParams("selectWorld.seedInfo", new Object[0]), this.field_146294_l / 2 - 100, 85, -6250336);
            this.drawString(this.field_146289_q, I18n.getStringParams("selectWorld.mapFeatures.info", new Object[0]), this.field_146294_l / 2 - 150, 122, -6250336);
            this.drawString(this.field_146289_q, I18n.getStringParams("selectWorld.allowCommands.info", new Object[0]), this.field_146294_l / 2 - 150, 172, -6250336);
            this.field_146335_h.func_146194_f();

            if (WorldType.worldTypes[this.field_146331_K].func_151357_h())
            {
                this.field_146289_q.drawSplitString(I18n.getStringParams(WorldType.worldTypes[this.field_146331_K].func_151359_c(), new Object[0]), this.field_146320_D.field_146128_h + 2, this.field_146320_D.field_146129_i + 22, this.field_146320_D.func_146117_b(), 10526880);
            }
        }
        else
        {
            this.drawString(this.field_146289_q, I18n.getStringParams("selectWorld.enterName", new Object[0]), this.field_146294_l / 2 - 100, 47, -6250336);
            this.drawString(this.field_146289_q, I18n.getStringParams("selectWorld.resultFolder", new Object[0]) + " " + this.field_146336_i, this.field_146294_l / 2 - 100, 85, -6250336);
            this.field_146333_g.func_146194_f();
            this.drawString(this.field_146289_q, this.field_146323_G, this.field_146294_l / 2 - 100, 137, -6250336);
            this.drawString(this.field_146289_q, this.field_146328_H, this.field_146294_l / 2 - 100, 149, -6250336);
        }

        super.drawScreen(par1, par2, par3);
    }

    public void func_146318_a(WorldInfo p_146318_1_)
    {
        this.field_146330_J = I18n.getStringParams("selectWorld.newWorld.copyOf", new Object[] {p_146318_1_.getWorldName()});
        this.field_146329_I = p_146318_1_.getSeed() + "";
        this.field_146331_K = p_146318_1_.getTerrainType().getWorldTypeID();
        this.field_146334_a = p_146318_1_.getGeneratorOptions();
        this.field_146341_s = p_146318_1_.isMapFeaturesEnabled();
        this.field_146340_t = p_146318_1_.areCommandsAllowed();

        if (p_146318_1_.isHardcoreModeEnabled())
        {
            this.field_146342_r = "hardcore";
        }
        else if (p_146318_1_.getGameType().isSurvivalOrAdventure())
        {
            this.field_146342_r = "survival";
        }
        else if (p_146318_1_.getGameType().isCreative())
        {
            this.field_146342_r = "creative";
        }
    }
}