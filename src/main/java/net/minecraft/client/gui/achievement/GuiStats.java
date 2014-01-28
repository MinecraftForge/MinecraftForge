package net.minecraft.client.gui.achievement;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSlot;
import net.minecraft.client.gui.IProgressMeter;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityList;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatCrafting;
import net.minecraft.stats.StatFileWriter;
import net.minecraft.stats.StatList;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

@SideOnly(Side.CLIENT)
public class GuiStats extends GuiScreen implements IProgressMeter
{
    private static RenderItem field_146544_g = new RenderItem();
    protected GuiScreen field_146549_a;
    protected String field_146542_f = "Select world";
    private GuiStats.StatsGeneral field_146550_h;
    private GuiStats.StatsItem field_146551_i;
    private GuiStats.StatsBlock field_146548_r;
    private GuiStats.StatsMobsList field_146547_s;
    private StatFileWriter field_146546_t;
    private GuiSlot field_146545_u;
    private boolean field_146543_v = true;
    private static final String __OBFID = "CL_00000723";

    public GuiStats(GuiScreen par1GuiScreen, StatFileWriter par2StatFileWriter)
    {
        this.field_146549_a = par1GuiScreen;
        this.field_146546_t = par2StatFileWriter;
    }

    // JAVADOC METHOD $$ func_73866_w_
    public void initGui()
    {
        this.field_146542_f = I18n.getStringParams("gui.stats", new Object[0]);
        this.field_146297_k.func_147114_u().func_147297_a(new C16PacketClientStatus(C16PacketClientStatus.EnumState.REQUEST_STATS));
    }

    public void func_146541_h()
    {
        this.field_146292_n.add(new GuiButton(0, this.field_146294_l / 2 + 4, this.field_146295_m - 28, 150, 20, I18n.getStringParams("gui.done", new Object[0])));
        this.field_146292_n.add(new GuiButton(1, this.field_146294_l / 2 - 160, this.field_146295_m - 52, 80, 20, I18n.getStringParams("stat.generalButton", new Object[0])));
        GuiButton guibutton;
        this.field_146292_n.add(guibutton = new GuiButton(2, this.field_146294_l / 2 - 80, this.field_146295_m - 52, 80, 20, I18n.getStringParams("stat.blocksButton", new Object[0])));
        GuiButton guibutton1;
        this.field_146292_n.add(guibutton1 = new GuiButton(3, this.field_146294_l / 2, this.field_146295_m - 52, 80, 20, I18n.getStringParams("stat.itemsButton", new Object[0])));
        GuiButton guibutton2;
        this.field_146292_n.add(guibutton2 = new GuiButton(4, this.field_146294_l / 2 + 80, this.field_146295_m - 52, 80, 20, I18n.getStringParams("stat.mobsButton", new Object[0])));

        if (this.field_146548_r.func_148127_b() == 0)
        {
            guibutton.field_146124_l = false;
        }

        if (this.field_146551_i.func_148127_b() == 0)
        {
            guibutton1.field_146124_l = false;
        }

        if (this.field_146547_s.func_148127_b() == 0)
        {
            guibutton2.field_146124_l = false;
        }
    }

    protected void func_146284_a(GuiButton p_146284_1_)
    {
        if (p_146284_1_.field_146124_l)
        {
            if (p_146284_1_.field_146127_k == 0)
            {
                this.field_146297_k.func_147108_a(this.field_146549_a);
            }
            else if (p_146284_1_.field_146127_k == 1)
            {
                this.field_146545_u = this.field_146550_h;
            }
            else if (p_146284_1_.field_146127_k == 3)
            {
                this.field_146545_u = this.field_146551_i;
            }
            else if (p_146284_1_.field_146127_k == 2)
            {
                this.field_146545_u = this.field_146548_r;
            }
            else if (p_146284_1_.field_146127_k == 4)
            {
                this.field_146545_u = this.field_146547_s;
            }
            else
            {
                this.field_146545_u.func_148147_a(p_146284_1_);
            }
        }
    }

    // JAVADOC METHOD $$ func_73863_a
    public void drawScreen(int par1, int par2, float par3)
    {
        if (this.field_146543_v)
        {
            this.func_146276_q_();
            this.drawCenteredString(this.field_146289_q, I18n.getStringParams("multiplayer.downloadingStats", new Object[0]), this.field_146294_l / 2, this.field_146295_m / 2, 16777215);
            this.drawCenteredString(this.field_146289_q, field_146510_b_[(int)(Minecraft.getSystemTime() / 150L % (long)field_146510_b_.length)], this.field_146294_l / 2, this.field_146295_m / 2 + this.field_146289_q.FONT_HEIGHT * 2, 16777215);
        }
        else
        {
            this.field_146545_u.func_148128_a(par1, par2, par3);
            this.drawCenteredString(this.field_146289_q, this.field_146542_f, this.field_146294_l / 2, 20, 16777215);
            super.drawScreen(par1, par2, par3);
        }
    }

    public void func_146509_g()
    {
        if (this.field_146543_v)
        {
            this.field_146550_h = new GuiStats.StatsGeneral();
            this.field_146550_h.func_148134_d(1, 1);
            this.field_146551_i = new GuiStats.StatsItem();
            this.field_146551_i.func_148134_d(1, 1);
            this.field_146548_r = new GuiStats.StatsBlock();
            this.field_146548_r.func_148134_d(1, 1);
            this.field_146547_s = new GuiStats.StatsMobsList();
            this.field_146547_s.func_148134_d(1, 1);
            this.field_146545_u = this.field_146550_h;
            this.func_146541_h();
            this.field_146543_v = false;
        }
    }

    // JAVADOC METHOD $$ func_73868_f
    public boolean doesGuiPauseGame()
    {
        return !this.field_146543_v;
    }

    private void func_146521_a(int p_146521_1_, int p_146521_2_, Item p_146521_3_)
    {
        this.func_146531_b(p_146521_1_ + 1, p_146521_2_ + 1);
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        RenderHelper.enableGUIStandardItemLighting();
        field_146544_g.renderItemIntoGUI(this.field_146289_q, this.field_146297_k.getTextureManager(), new ItemStack(p_146521_3_, 1, 0), p_146521_1_ + 2, p_146521_2_ + 2);
        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
    }

    private void func_146531_b(int p_146531_1_, int p_146531_2_)
    {
        this.func_146527_c(p_146531_1_, p_146531_2_, 0, 0);
    }

    private void func_146527_c(int p_146527_1_, int p_146527_2_, int p_146527_3_, int p_146527_4_)
    {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.field_146297_k.getTextureManager().bindTexture(statIcons);
        float f = 0.0078125F;
        float f1 = 0.0078125F;
        boolean flag = true;
        boolean flag1 = true;
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV((double)(p_146527_1_ + 0), (double)(p_146527_2_ + 18), (double)this.zLevel, (double)((float)(p_146527_3_ + 0) * 0.0078125F), (double)((float)(p_146527_4_ + 18) * 0.0078125F));
        tessellator.addVertexWithUV((double)(p_146527_1_ + 18), (double)(p_146527_2_ + 18), (double)this.zLevel, (double)((float)(p_146527_3_ + 18) * 0.0078125F), (double)((float)(p_146527_4_ + 18) * 0.0078125F));
        tessellator.addVertexWithUV((double)(p_146527_1_ + 18), (double)(p_146527_2_ + 0), (double)this.zLevel, (double)((float)(p_146527_3_ + 18) * 0.0078125F), (double)((float)(p_146527_4_ + 0) * 0.0078125F));
        tessellator.addVertexWithUV((double)(p_146527_1_ + 0), (double)(p_146527_2_ + 0), (double)this.zLevel, (double)((float)(p_146527_3_ + 0) * 0.0078125F), (double)((float)(p_146527_4_ + 0) * 0.0078125F));
        tessellator.draw();
    }

    @SideOnly(Side.CLIENT)
    class StatsMobsList extends GuiSlot
    {
        private final List field_148222_l = new ArrayList();
        private static final String __OBFID = "CL_00000729";

        public StatsMobsList()
        {
            super(GuiStats.this.field_146297_k, GuiStats.this.field_146294_l, GuiStats.this.field_146295_m, 32, GuiStats.this.field_146295_m - 64, GuiStats.this.field_146289_q.FONT_HEIGHT * 4);
            this.func_148130_a(false);
            Iterator iterator = EntityList.entityEggs.values().iterator();

            while (iterator.hasNext())
            {
                EntityList.EntityEggInfo entityegginfo = (EntityList.EntityEggInfo)iterator.next();

                if (GuiStats.this.field_146546_t.writeStat(entityegginfo.field_151512_d) > 0 || GuiStats.this.field_146546_t.writeStat(entityegginfo.field_151513_e) > 0)
                {
                    this.field_148222_l.add(entityegginfo);
                }
            }
        }

        protected int func_148127_b()
        {
            return this.field_148222_l.size();
        }

        protected void func_148144_a(int p_148144_1_, boolean p_148144_2_, int p_148144_3_, int p_148144_4_) {}

        protected boolean func_148131_a(int p_148131_1_)
        {
            return false;
        }

        protected int func_148138_e()
        {
            return this.func_148127_b() * GuiStats.this.field_146289_q.FONT_HEIGHT * 4;
        }

        protected void func_148123_a()
        {
            GuiStats.this.func_146276_q_();
        }

        protected void func_148126_a(int p_148126_1_, int p_148126_2_, int p_148126_3_, int p_148126_4_, Tessellator p_148126_5_, int p_148126_6_, int p_148126_7_)
        {
            EntityList.EntityEggInfo entityegginfo = (EntityList.EntityEggInfo)this.field_148222_l.get(p_148126_1_);
            String s = I18n.getStringParams("entity." + EntityList.getStringFromID(entityegginfo.spawnedID) + ".name", new Object[0]);
            int k1 = GuiStats.this.field_146546_t.writeStat(entityegginfo.field_151512_d);
            int l1 = GuiStats.this.field_146546_t.writeStat(entityegginfo.field_151513_e);
            String s1 = I18n.getStringParams("stat.entityKills", new Object[] {Integer.valueOf(k1), s});
            String s2 = I18n.getStringParams("stat.entityKilledBy", new Object[] {s, Integer.valueOf(l1)});

            if (k1 == 0)
            {
                s1 = I18n.getStringParams("stat.entityKills.none", new Object[] {s});
            }

            if (l1 == 0)
            {
                s2 = I18n.getStringParams("stat.entityKilledBy.none", new Object[] {s});
            }

            GuiStats.this.drawString(GuiStats.this.field_146289_q, s, p_148126_2_ + 2 - 10, p_148126_3_ + 1, 16777215);
            GuiStats.this.drawString(GuiStats.this.field_146289_q, s1, p_148126_2_ + 2, p_148126_3_ + 1 + GuiStats.this.field_146289_q.FONT_HEIGHT, k1 == 0 ? 6316128 : 9474192);
            GuiStats.this.drawString(GuiStats.this.field_146289_q, s2, p_148126_2_ + 2, p_148126_3_ + 1 + GuiStats.this.field_146289_q.FONT_HEIGHT * 2, l1 == 0 ? 6316128 : 9474192);
        }
    }

    @SideOnly(Side.CLIENT)
    class StatsBlock extends GuiStats.Stats
    {
        private static final String __OBFID = "CL_00000724";

        public StatsBlock()
        {
            this.field_148219_m = new ArrayList();
            Iterator iterator = StatList.objectMineStats.iterator();

            while (iterator.hasNext())
            {
                StatCrafting statcrafting = (StatCrafting)iterator.next();
                boolean flag = false;
                int i = Item.func_150891_b(statcrafting.func_150959_a());

                if (GuiStats.this.field_146546_t.writeStat(statcrafting) > 0)
                {
                    flag = true;
                }
                else if (StatList.objectUseStats[i] != null && GuiStats.this.field_146546_t.writeStat(StatList.objectUseStats[i]) > 0)
                {
                    flag = true;
                }
                else if (StatList.objectCraftStats[i] != null && GuiStats.this.field_146546_t.writeStat(StatList.objectCraftStats[i]) > 0)
                {
                    flag = true;
                }

                if (flag)
                {
                    this.field_148219_m.add(statcrafting);
                }
            }

            this.field_148216_n = new Comparator()
            {
                private static final String __OBFID = "CL_00000725";
                public int compare(StatCrafting p_148339_1_, StatCrafting p_148339_2_)
                {
                    int j = Item.func_150891_b(p_148339_1_.func_150959_a());
                    int k = Item.func_150891_b(p_148339_2_.func_150959_a());
                    StatBase statbase = null;
                    StatBase statbase1 = null;

                    if (StatsBlock.this.field_148217_o == 2)
                    {
                        statbase = StatList.mineBlockStatArray[j];
                        statbase1 = StatList.mineBlockStatArray[k];
                    }
                    else if (StatsBlock.this.field_148217_o == 0)
                    {
                        statbase = StatList.objectCraftStats[j];
                        statbase1 = StatList.objectCraftStats[k];
                    }
                    else if (StatsBlock.this.field_148217_o == 1)
                    {
                        statbase = StatList.objectUseStats[j];
                        statbase1 = StatList.objectUseStats[k];
                    }

                    if (statbase != null || statbase1 != null)
                    {
                        if (statbase == null)
                        {
                            return 1;
                        }

                        if (statbase1 == null)
                        {
                            return -1;
                        }

                        int l = GuiStats.this.field_146546_t.writeStat(statbase);
                        int i1 = GuiStats.this.field_146546_t.writeStat(statbase1);

                        if (l != i1)
                        {
                            return (l - i1) * StatsBlock.this.field_148215_p;
                        }
                    }

                    return j - k;
                }
                public int compare(Object par1Obj, Object par2Obj)
                {
                    return this.compare((StatCrafting)par1Obj, (StatCrafting)par2Obj);
                }
            };
        }

        protected void func_148129_a(int p_148129_1_, int p_148129_2_, Tessellator p_148129_3_)
        {
            super.func_148129_a(p_148129_1_, p_148129_2_, p_148129_3_);

            if (this.field_148218_l == 0)
            {
                GuiStats.this.func_146527_c(p_148129_1_ + 115 - 18 + 1, p_148129_2_ + 1 + 1, 18, 18);
            }
            else
            {
                GuiStats.this.func_146527_c(p_148129_1_ + 115 - 18, p_148129_2_ + 1, 18, 18);
            }

            if (this.field_148218_l == 1)
            {
                GuiStats.this.func_146527_c(p_148129_1_ + 165 - 18 + 1, p_148129_2_ + 1 + 1, 36, 18);
            }
            else
            {
                GuiStats.this.func_146527_c(p_148129_1_ + 165 - 18, p_148129_2_ + 1, 36, 18);
            }

            if (this.field_148218_l == 2)
            {
                GuiStats.this.func_146527_c(p_148129_1_ + 215 - 18 + 1, p_148129_2_ + 1 + 1, 54, 18);
            }
            else
            {
                GuiStats.this.func_146527_c(p_148129_1_ + 215 - 18, p_148129_2_ + 1, 54, 18);
            }
        }

        protected void func_148126_a(int p_148126_1_, int p_148126_2_, int p_148126_3_, int p_148126_4_, Tessellator p_148126_5_, int p_148126_6_, int p_148126_7_)
        {
            StatCrafting statcrafting = this.func_148211_c(p_148126_1_);
            Item item = statcrafting.func_150959_a();
            GuiStats.this.func_146521_a(p_148126_2_ + 40, p_148126_3_, item);
            int k1 = Item.func_150891_b(item);
            this.func_148209_a(StatList.objectCraftStats[k1], p_148126_2_ + 115, p_148126_3_, p_148126_1_ % 2 == 0);
            this.func_148209_a(StatList.objectUseStats[k1], p_148126_2_ + 165, p_148126_3_, p_148126_1_ % 2 == 0);
            this.func_148209_a(statcrafting, p_148126_2_ + 215, p_148126_3_, p_148126_1_ % 2 == 0);
        }

        protected String func_148210_b(int p_148210_1_)
        {
            return p_148210_1_ == 0 ? "stat.crafted" : (p_148210_1_ == 1 ? "stat.used" : "stat.mined");
        }
    }

    @SideOnly(Side.CLIENT)
    class StatsItem extends GuiStats.Stats
    {
        private static final String __OBFID = "CL_00000727";

        public StatsItem()
        {
            this.field_148219_m = new ArrayList();
            Iterator iterator = StatList.itemStats.iterator();

            while (iterator.hasNext())
            {
                StatCrafting statcrafting = (StatCrafting)iterator.next();
                boolean flag = false;
                int i = Item.func_150891_b(statcrafting.func_150959_a());

                if (GuiStats.this.field_146546_t.writeStat(statcrafting) > 0)
                {
                    flag = true;
                }
                else if (StatList.objectBreakStats[i] != null && GuiStats.this.field_146546_t.writeStat(StatList.objectBreakStats[i]) > 0)
                {
                    flag = true;
                }
                else if (StatList.objectCraftStats[i] != null && GuiStats.this.field_146546_t.writeStat(StatList.objectCraftStats[i]) > 0)
                {
                    flag = true;
                }

                if (flag)
                {
                    this.field_148219_m.add(statcrafting);
                }
            }

            this.field_148216_n = new Comparator()
            {
                private static final String __OBFID = "CL_00000728";
                public int compare(StatCrafting p_148342_1_, StatCrafting p_148342_2_)
                {
                    int j = Item.func_150891_b(p_148342_1_.func_150959_a());
                    int k = Item.func_150891_b(p_148342_2_.func_150959_a());
                    StatBase statbase = null;
                    StatBase statbase1 = null;

                    if (StatsItem.this.field_148217_o == 0)
                    {
                        statbase = StatList.objectBreakStats[j];
                        statbase1 = StatList.objectBreakStats[k];
                    }
                    else if (StatsItem.this.field_148217_o == 1)
                    {
                        statbase = StatList.objectCraftStats[j];
                        statbase1 = StatList.objectCraftStats[k];
                    }
                    else if (StatsItem.this.field_148217_o == 2)
                    {
                        statbase = StatList.objectUseStats[j];
                        statbase1 = StatList.objectUseStats[k];
                    }

                    if (statbase != null || statbase1 != null)
                    {
                        if (statbase == null)
                        {
                            return 1;
                        }

                        if (statbase1 == null)
                        {
                            return -1;
                        }

                        int l = GuiStats.this.field_146546_t.writeStat(statbase);
                        int i1 = GuiStats.this.field_146546_t.writeStat(statbase1);

                        if (l != i1)
                        {
                            return (l - i1) * StatsItem.this.field_148215_p;
                        }
                    }

                    return j - k;
                }
                public int compare(Object par1Obj, Object par2Obj)
                {
                    return this.compare((StatCrafting)par1Obj, (StatCrafting)par2Obj);
                }
            };
        }

        protected void func_148129_a(int p_148129_1_, int p_148129_2_, Tessellator p_148129_3_)
        {
            super.func_148129_a(p_148129_1_, p_148129_2_, p_148129_3_);

            if (this.field_148218_l == 0)
            {
                GuiStats.this.func_146527_c(p_148129_1_ + 115 - 18 + 1, p_148129_2_ + 1 + 1, 72, 18);
            }
            else
            {
                GuiStats.this.func_146527_c(p_148129_1_ + 115 - 18, p_148129_2_ + 1, 72, 18);
            }

            if (this.field_148218_l == 1)
            {
                GuiStats.this.func_146527_c(p_148129_1_ + 165 - 18 + 1, p_148129_2_ + 1 + 1, 18, 18);
            }
            else
            {
                GuiStats.this.func_146527_c(p_148129_1_ + 165 - 18, p_148129_2_ + 1, 18, 18);
            }

            if (this.field_148218_l == 2)
            {
                GuiStats.this.func_146527_c(p_148129_1_ + 215 - 18 + 1, p_148129_2_ + 1 + 1, 36, 18);
            }
            else
            {
                GuiStats.this.func_146527_c(p_148129_1_ + 215 - 18, p_148129_2_ + 1, 36, 18);
            }
        }

        protected void func_148126_a(int p_148126_1_, int p_148126_2_, int p_148126_3_, int p_148126_4_, Tessellator p_148126_5_, int p_148126_6_, int p_148126_7_)
        {
            StatCrafting statcrafting = this.func_148211_c(p_148126_1_);
            Item item = statcrafting.func_150959_a();
            GuiStats.this.func_146521_a(p_148126_2_ + 40, p_148126_3_, item);
            int k1 = Item.func_150891_b(item);
            this.func_148209_a(StatList.objectBreakStats[k1], p_148126_2_ + 115, p_148126_3_, p_148126_1_ % 2 == 0);
            this.func_148209_a(StatList.objectCraftStats[k1], p_148126_2_ + 165, p_148126_3_, p_148126_1_ % 2 == 0);
            this.func_148209_a(statcrafting, p_148126_2_ + 215, p_148126_3_, p_148126_1_ % 2 == 0);
        }

        protected String func_148210_b(int p_148210_1_)
        {
            return p_148210_1_ == 1 ? "stat.crafted" : (p_148210_1_ == 2 ? "stat.used" : "stat.depleted");
        }
    }

    @SideOnly(Side.CLIENT)
    class StatsGeneral extends GuiSlot
    {
        private static final String __OBFID = "CL_00000726";

        public StatsGeneral()
        {
            super(GuiStats.this.field_146297_k, GuiStats.this.field_146294_l, GuiStats.this.field_146295_m, 32, GuiStats.this.field_146295_m - 64, 10);
            this.func_148130_a(false);
        }

        protected int func_148127_b()
        {
            return StatList.generalStats.size();
        }

        protected void func_148144_a(int p_148144_1_, boolean p_148144_2_, int p_148144_3_, int p_148144_4_) {}

        protected boolean func_148131_a(int p_148131_1_)
        {
            return false;
        }

        protected int func_148138_e()
        {
            return this.func_148127_b() * 10;
        }

        protected void func_148123_a()
        {
            GuiStats.this.func_146276_q_();
        }

        protected void func_148126_a(int p_148126_1_, int p_148126_2_, int p_148126_3_, int p_148126_4_, Tessellator p_148126_5_, int p_148126_6_, int p_148126_7_)
        {
            StatBase statbase = (StatBase)StatList.generalStats.get(p_148126_1_);
            GuiStats.this.drawString(GuiStats.this.field_146289_q, statbase.func_150951_e().func_150260_c(), p_148126_2_ + 2, p_148126_3_ + 1, p_148126_1_ % 2 == 0 ? 16777215 : 9474192);
            String s = statbase.func_75968_a(GuiStats.this.field_146546_t.writeStat(statbase));
            GuiStats.this.drawString(GuiStats.this.field_146289_q, s, p_148126_2_ + 2 + 213 - GuiStats.this.field_146289_q.getStringWidth(s), p_148126_3_ + 1, p_148126_1_ % 2 == 0 ? 16777215 : 9474192);
        }
    }

    @SideOnly(Side.CLIENT)
    abstract class Stats extends GuiSlot
    {
        protected int field_148218_l = -1;
        protected List field_148219_m;
        protected Comparator field_148216_n;
        protected int field_148217_o = -1;
        protected int field_148215_p;
        private static final String __OBFID = "CL_00000730";

        protected Stats()
        {
            super(GuiStats.this.field_146297_k, GuiStats.this.field_146294_l, GuiStats.this.field_146295_m, 32, GuiStats.this.field_146295_m - 64, 20);
            this.func_148130_a(false);
            this.func_148133_a(true, 20);
        }

        protected void func_148144_a(int p_148144_1_, boolean p_148144_2_, int p_148144_3_, int p_148144_4_) {}

        protected boolean func_148131_a(int p_148131_1_)
        {
            return false;
        }

        protected void func_148123_a()
        {
            GuiStats.this.func_146276_q_();
        }

        protected void func_148129_a(int p_148129_1_, int p_148129_2_, Tessellator p_148129_3_)
        {
            if (!Mouse.isButtonDown(0))
            {
                this.field_148218_l = -1;
            }

            if (this.field_148218_l == 0)
            {
                GuiStats.this.func_146527_c(p_148129_1_ + 115 - 18, p_148129_2_ + 1, 0, 0);
            }
            else
            {
                GuiStats.this.func_146527_c(p_148129_1_ + 115 - 18, p_148129_2_ + 1, 0, 18);
            }

            if (this.field_148218_l == 1)
            {
                GuiStats.this.func_146527_c(p_148129_1_ + 165 - 18, p_148129_2_ + 1, 0, 0);
            }
            else
            {
                GuiStats.this.func_146527_c(p_148129_1_ + 165 - 18, p_148129_2_ + 1, 0, 18);
            }

            if (this.field_148218_l == 2)
            {
                GuiStats.this.func_146527_c(p_148129_1_ + 215 - 18, p_148129_2_ + 1, 0, 0);
            }
            else
            {
                GuiStats.this.func_146527_c(p_148129_1_ + 215 - 18, p_148129_2_ + 1, 0, 18);
            }

            if (this.field_148217_o != -1)
            {
                short short1 = 79;
                byte b0 = 18;

                if (this.field_148217_o == 1)
                {
                    short1 = 129;
                }
                else if (this.field_148217_o == 2)
                {
                    short1 = 179;
                }

                if (this.field_148215_p == 1)
                {
                    b0 = 36;
                }

                GuiStats.this.func_146527_c(p_148129_1_ + short1, p_148129_2_ + 1, b0, 0);
            }
        }

        protected void func_148132_a(int p_148132_1_, int p_148132_2_)
        {
            this.field_148218_l = -1;

            if (p_148132_1_ >= 79 && p_148132_1_ < 115)
            {
                this.field_148218_l = 0;
            }
            else if (p_148132_1_ >= 129 && p_148132_1_ < 165)
            {
                this.field_148218_l = 1;
            }
            else if (p_148132_1_ >= 179 && p_148132_1_ < 215)
            {
                this.field_148218_l = 2;
            }

            if (this.field_148218_l >= 0)
            {
                this.func_148212_h(this.field_148218_l);
                GuiStats.this.field_146297_k.func_147118_V().func_147682_a(PositionedSoundRecord.func_147674_a(new ResourceLocation("gui.button.press"), 1.0F));
            }
        }

        protected final int func_148127_b()
        {
            return this.field_148219_m.size();
        }

        protected final StatCrafting func_148211_c(int p_148211_1_)
        {
            return (StatCrafting)this.field_148219_m.get(p_148211_1_);
        }

        protected abstract String func_148210_b(int var1);

        protected void func_148209_a(StatBase p_148209_1_, int p_148209_2_, int p_148209_3_, boolean p_148209_4_)
        {
            String s;

            if (p_148209_1_ != null)
            {
                s = p_148209_1_.func_75968_a(GuiStats.this.field_146546_t.writeStat(p_148209_1_));
                GuiStats.this.drawString(GuiStats.this.field_146289_q, s, p_148209_2_ - GuiStats.this.field_146289_q.getStringWidth(s), p_148209_3_ + 5, p_148209_4_ ? 16777215 : 9474192);
            }
            else
            {
                s = "-";
                GuiStats.this.drawString(GuiStats.this.field_146289_q, s, p_148209_2_ - GuiStats.this.field_146289_q.getStringWidth(s), p_148209_3_ + 5, p_148209_4_ ? 16777215 : 9474192);
            }
        }

        protected void func_148142_b(int p_148142_1_, int p_148142_2_)
        {
            if (p_148142_2_ >= this.field_148153_b && p_148142_2_ <= this.field_148154_c)
            {
                int k = this.func_148124_c(p_148142_1_, p_148142_2_);
                int l = this.field_148155_a / 2 - 92 - 16;

                if (k >= 0)
                {
                    if (p_148142_1_ < l + 40 || p_148142_1_ > l + 40 + 20)
                    {
                        return;
                    }

                    StatCrafting statcrafting = this.func_148211_c(k);
                    this.func_148213_a(statcrafting, p_148142_1_, p_148142_2_);
                }
                else
                {
                    String s = "";

                    if (p_148142_1_ >= l + 115 - 18 && p_148142_1_ <= l + 115)
                    {
                        s = this.func_148210_b(0);
                    }
                    else if (p_148142_1_ >= l + 165 - 18 && p_148142_1_ <= l + 165)
                    {
                        s = this.func_148210_b(1);
                    }
                    else
                    {
                        if (p_148142_1_ < l + 215 - 18 || p_148142_1_ > l + 215)
                        {
                            return;
                        }

                        s = this.func_148210_b(2);
                    }

                    s = ("" + I18n.getStringParams(s, new Object[0])).trim();

                    if (s.length() > 0)
                    {
                        int i1 = p_148142_1_ + 12;
                        int j1 = p_148142_2_ - 12;
                        int k1 = GuiStats.this.field_146289_q.getStringWidth(s);
                        GuiStats.this.drawGradientRect(i1 - 3, j1 - 3, i1 + k1 + 3, j1 + 8 + 3, -1073741824, -1073741824);
                        GuiStats.this.field_146289_q.drawStringWithShadow(s, i1, j1, -1);
                    }
                }
            }
        }

        protected void func_148213_a(StatCrafting p_148213_1_, int p_148213_2_, int p_148213_3_)
        {
            if (p_148213_1_ != null)
            {
                Item item = p_148213_1_.func_150959_a();
                String s = ("" + I18n.getStringParams(item.getUnlocalizedName() + ".name", new Object[0])).trim();

                if (s.length() > 0)
                {
                    int k = p_148213_2_ + 12;
                    int l = p_148213_3_ - 12;
                    int i1 = GuiStats.this.field_146289_q.getStringWidth(s);
                    GuiStats.this.drawGradientRect(k - 3, l - 3, k + i1 + 3, l + 8 + 3, -1073741824, -1073741824);
                    GuiStats.this.field_146289_q.drawStringWithShadow(s, k, l, -1);
                }
            }
        }

        protected void func_148212_h(int p_148212_1_)
        {
            if (p_148212_1_ != this.field_148217_o)
            {
                this.field_148217_o = p_148212_1_;
                this.field_148215_p = -1;
            }
            else if (this.field_148215_p == -1)
            {
                this.field_148215_p = 1;
            }
            else
            {
                this.field_148217_o = -1;
                this.field_148215_p = 0;
            }

            Collections.sort(this.field_148219_m, this.field_148216_n);
        }
    }
}