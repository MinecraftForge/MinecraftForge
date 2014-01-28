package net.minecraft.client.gui;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import net.minecraft.client.AnvilConverterException;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.SaveFormatComparator;
import net.minecraft.world.storage.WorldInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SideOnly(Side.CLIENT)
public class GuiSelectWorld extends GuiScreen
{
    private static final Logger field_146629_g = LogManager.getLogger();
    private final DateFormat field_146633_h = new SimpleDateFormat();
    protected GuiScreen field_146632_a;
    protected String field_146628_f = "Select world";
    private boolean field_146634_i;
    private int field_146640_r;
    private java.util.List field_146639_s;
    private GuiSelectWorld.List field_146638_t;
    private String field_146637_u;
    private String field_146636_v;
    private String[] field_146635_w = new String[3];
    private boolean field_146643_x;
    private GuiButton field_146642_y;
    private GuiButton field_146641_z;
    private GuiButton field_146630_A;
    private GuiButton field_146631_B;
    private static final String __OBFID = "CL_00000711";

    public GuiSelectWorld(GuiScreen par1GuiScreen)
    {
        this.field_146632_a = par1GuiScreen;
    }

    // JAVADOC METHOD $$ func_73866_w_
    public void initGui()
    {
        this.field_146628_f = I18n.getStringParams("selectWorld.title", new Object[0]);

        try
        {
            this.func_146627_h();
        }
        catch (AnvilConverterException anvilconverterexception)
        {
            field_146629_g.error("Couldn\'t load level list", anvilconverterexception);
            this.field_146297_k.func_147108_a(new GuiErrorScreen("Unable to load worlds", anvilconverterexception.getMessage()));
            return;
        }

        this.field_146637_u = I18n.getStringParams("selectWorld.world", new Object[0]);
        this.field_146636_v = I18n.getStringParams("selectWorld.conversion", new Object[0]);
        this.field_146635_w[WorldSettings.GameType.SURVIVAL.getID()] = I18n.getStringParams("gameMode.survival", new Object[0]);
        this.field_146635_w[WorldSettings.GameType.CREATIVE.getID()] = I18n.getStringParams("gameMode.creative", new Object[0]);
        this.field_146635_w[WorldSettings.GameType.ADVENTURE.getID()] = I18n.getStringParams("gameMode.adventure", new Object[0]);
        this.field_146638_t = new GuiSelectWorld.List();
        this.field_146638_t.func_148134_d(4, 5);
        this.func_146618_g();
    }

    private void func_146627_h() throws AnvilConverterException
    {
        ISaveFormat isaveformat = this.field_146297_k.getSaveLoader();
        this.field_146639_s = isaveformat.getSaveList();
        Collections.sort(this.field_146639_s);
        this.field_146640_r = -1;
    }

    protected String func_146621_a(int p_146621_1_)
    {
        return ((SaveFormatComparator)this.field_146639_s.get(p_146621_1_)).getFileName();
    }

    protected String func_146614_d(int p_146614_1_)
    {
        String s = ((SaveFormatComparator)this.field_146639_s.get(p_146614_1_)).getDisplayName();

        if (s == null || MathHelper.stringNullOrLengthZero(s))
        {
            s = I18n.getStringParams("selectWorld.world", new Object[0]) + " " + (p_146614_1_ + 1);
        }

        return s;
    }

    public void func_146618_g()
    {
        this.field_146292_n.add(this.field_146641_z = new GuiButton(1, this.field_146294_l / 2 - 154, this.field_146295_m - 52, 150, 20, I18n.getStringParams("selectWorld.select", new Object[0])));
        this.field_146292_n.add(new GuiButton(3, this.field_146294_l / 2 + 4, this.field_146295_m - 52, 150, 20, I18n.getStringParams("selectWorld.create", new Object[0])));
        this.field_146292_n.add(this.field_146630_A = new GuiButton(6, this.field_146294_l / 2 - 154, this.field_146295_m - 28, 72, 20, I18n.getStringParams("selectWorld.rename", new Object[0])));
        this.field_146292_n.add(this.field_146642_y = new GuiButton(2, this.field_146294_l / 2 - 76, this.field_146295_m - 28, 72, 20, I18n.getStringParams("selectWorld.delete", new Object[0])));
        this.field_146292_n.add(this.field_146631_B = new GuiButton(7, this.field_146294_l / 2 + 4, this.field_146295_m - 28, 72, 20, I18n.getStringParams("selectWorld.recreate", new Object[0])));
        this.field_146292_n.add(new GuiButton(0, this.field_146294_l / 2 + 82, this.field_146295_m - 28, 72, 20, I18n.getStringParams("gui.cancel", new Object[0])));
        this.field_146641_z.field_146124_l = false;
        this.field_146642_y.field_146124_l = false;
        this.field_146630_A.field_146124_l = false;
        this.field_146631_B.field_146124_l = false;
    }

    protected void func_146284_a(GuiButton p_146284_1_)
    {
        if (p_146284_1_.field_146124_l)
        {
            if (p_146284_1_.field_146127_k == 2)
            {
                String s = this.func_146614_d(this.field_146640_r);

                if (s != null)
                {
                    this.field_146643_x = true;
                    GuiYesNo guiyesno = func_146623_a(this, s, this.field_146640_r);
                    this.field_146297_k.func_147108_a(guiyesno);
                }
            }
            else if (p_146284_1_.field_146127_k == 1)
            {
                FMLClientHandler.instance().tryLoadWorld(this,this.field_146640_r);
            }
            else if (p_146284_1_.field_146127_k == 3)
            {
                this.field_146297_k.func_147108_a(new GuiCreateWorld(this));
            }
            else if (p_146284_1_.field_146127_k == 6)
            {
                this.field_146297_k.func_147108_a(new GuiRenameWorld(this, this.func_146621_a(this.field_146640_r)));
            }
            else if (p_146284_1_.field_146127_k == 0)
            {
                this.field_146297_k.func_147108_a(this.field_146632_a);
            }
            else if (p_146284_1_.field_146127_k == 7)
            {
                GuiCreateWorld guicreateworld = new GuiCreateWorld(this);
                ISaveHandler isavehandler = this.field_146297_k.getSaveLoader().getSaveLoader(this.func_146621_a(this.field_146640_r), false);
                WorldInfo worldinfo = isavehandler.loadWorldInfo();
                isavehandler.flush();
                guicreateworld.func_146318_a(worldinfo);
                this.field_146297_k.func_147108_a(guicreateworld);
            }
            else
            {
                this.field_146638_t.func_148147_a(p_146284_1_);
            }
        }
    }

    public void func_146615_e(int p_146615_1_)
    {
        this.field_146297_k.func_147108_a((GuiScreen)null);

        if (!this.field_146634_i)
        {
            this.field_146634_i = true;
            String s = this.func_146621_a(p_146615_1_);

            if (s == null)
            {
                s = "World" + p_146615_1_;
            }

            String s1 = this.func_146614_d(p_146615_1_);

            if (s1 == null)
            {
                s1 = "World" + p_146615_1_;
            }

            if (this.field_146297_k.getSaveLoader().canLoadWorld(s))
            {
                this.field_146297_k.launchIntegratedServer(s, s1, (WorldSettings)null);
            }
        }
    }

    public void confirmClicked(boolean par1, int par2)
    {
        if (this.field_146643_x)
        {
            this.field_146643_x = false;

            if (par1)
            {
                ISaveFormat isaveformat = this.field_146297_k.getSaveLoader();
                isaveformat.flushCache();
                isaveformat.deleteWorldDirectory(this.func_146621_a(par2));

                try
                {
                    this.func_146627_h();
                }
                catch (AnvilConverterException anvilconverterexception)
                {
                    field_146629_g.error("Couldn\'t load level list", anvilconverterexception);
                }
            }

            this.field_146297_k.func_147108_a(this);
        }
    }

    // JAVADOC METHOD $$ func_73863_a
    public void drawScreen(int par1, int par2, float par3)
    {
        this.field_146638_t.func_148128_a(par1, par2, par3);
        this.drawCenteredString(this.field_146289_q, this.field_146628_f, this.field_146294_l / 2, 20, 16777215);
        super.drawScreen(par1, par2, par3);
    }

    public static GuiYesNo func_146623_a(GuiScreen p_146623_0_, String p_146623_1_, int p_146623_2_)
    {
        String s1 = I18n.getStringParams("selectWorld.deleteQuestion", new Object[0]);
        String s2 = "\'" + p_146623_1_ + "\' " + I18n.getStringParams("selectWorld.deleteWarning", new Object[0]);
        String s3 = I18n.getStringParams("selectWorld.deleteButton", new Object[0]);
        String s4 = I18n.getStringParams("gui.cancel", new Object[0]);
        GuiYesNo guiyesno = new GuiYesNo(p_146623_0_, s1, s2, s3, s4, p_146623_2_);
        return guiyesno;
    }

    @SideOnly(Side.CLIENT)
    class List extends GuiSlot
    {
        private static final String __OBFID = "CL_00000712";

        public List()
        {
            super(GuiSelectWorld.this.field_146297_k, GuiSelectWorld.this.field_146294_l, GuiSelectWorld.this.field_146295_m, 32, GuiSelectWorld.this.field_146295_m - 64, 36);
        }

        protected int func_148127_b()
        {
            return GuiSelectWorld.this.field_146639_s.size();
        }

        protected void func_148144_a(int p_148144_1_, boolean p_148144_2_, int p_148144_3_, int p_148144_4_)
        {
            GuiSelectWorld.this.field_146640_r = p_148144_1_;
            boolean flag1 = GuiSelectWorld.this.field_146640_r >= 0 && GuiSelectWorld.this.field_146640_r < this.func_148127_b();
            GuiSelectWorld.this.field_146641_z.field_146124_l = flag1;
            GuiSelectWorld.this.field_146642_y.field_146124_l = flag1;
            GuiSelectWorld.this.field_146630_A.field_146124_l = flag1;
            GuiSelectWorld.this.field_146631_B.field_146124_l = flag1;

            if (p_148144_2_ && flag1)
            {
                FMLClientHandler.instance().tryLoadWorld(GuiSelectWorld.this,p_148144_1_);
            }
        }

        protected boolean func_148131_a(int p_148131_1_)
        {
            return p_148131_1_ == GuiSelectWorld.this.field_146640_r;
        }

        protected int func_148138_e()
        {
            return GuiSelectWorld.this.field_146639_s.size() * 36;
        }

        protected void func_148123_a()
        {
            GuiSelectWorld.this.func_146276_q_();
        }

        protected void func_148126_a(int p_148126_1_, int p_148126_2_, int p_148126_3_, int p_148126_4_, Tessellator p_148126_5_, int p_148126_6_, int p_148126_7_)
        {
            SaveFormatComparator saveformatcomparator = (SaveFormatComparator)GuiSelectWorld.this.field_146639_s.get(p_148126_1_);
            String s = saveformatcomparator.getDisplayName();

            if (s == null || MathHelper.stringNullOrLengthZero(s))
            {
                s = GuiSelectWorld.this.field_146637_u + " " + (p_148126_1_ + 1);
            }

            String s1 = saveformatcomparator.getFileName();
            s1 = s1 + " (" + GuiSelectWorld.this.field_146633_h.format(new Date(saveformatcomparator.getLastTimePlayed()));
            s1 = s1 + ")";
            String s2 = "";

            if (saveformatcomparator.requiresConversion())
            {
                s2 = GuiSelectWorld.this.field_146636_v + " " + s2;
            }
            else
            {
                s2 = GuiSelectWorld.this.field_146635_w[saveformatcomparator.getEnumGameType().getID()];

                if (saveformatcomparator.isHardcoreModeEnabled())
                {
                    s2 = EnumChatFormatting.DARK_RED + I18n.getStringParams("gameMode.hardcore", new Object[0]) + EnumChatFormatting.RESET;
                }

                if (saveformatcomparator.getCheatsEnabled())
                {
                    s2 = s2 + ", " + I18n.getStringParams("selectWorld.cheats", new Object[0]);
                }
            }

            GuiSelectWorld.this.drawString(GuiSelectWorld.this.field_146289_q, s, p_148126_2_ + 2, p_148126_3_ + 1, 16777215);
            GuiSelectWorld.this.drawString(GuiSelectWorld.this.field_146289_q, s1, p_148126_2_ + 2, p_148126_3_ + 12, 8421504);
            GuiSelectWorld.this.drawString(GuiSelectWorld.this.field_146289_q, s2, p_148126_2_ + 2, p_148126_3_ + 12 + 10, 8421504);
        }
    }
}