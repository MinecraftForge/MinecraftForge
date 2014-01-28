package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.Iterator;
import java.util.Map;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.Language;
import net.minecraft.client.resources.LanguageManager;
import net.minecraft.client.settings.GameSettings;

@SideOnly(Side.CLIENT)
public class GuiLanguage extends GuiScreen
{
    protected GuiScreen field_146453_a;
    private GuiLanguage.List field_146450_f;
    private final GameSettings field_146451_g;
    private final LanguageManager field_146454_h;
    private GuiOptionButton field_146455_i;
    private GuiOptionButton field_146452_r;
    private static final String __OBFID = "CL_00000698";

    public GuiLanguage(GuiScreen par1GuiScreen, GameSettings par2GameSettings, LanguageManager par3LanguageManager)
    {
        this.field_146453_a = par1GuiScreen;
        this.field_146451_g = par2GameSettings;
        this.field_146454_h = par3LanguageManager;
    }

    // JAVADOC METHOD $$ func_73866_w_
    public void initGui()
    {
        this.field_146292_n.add(this.field_146455_i = new GuiOptionButton(100, this.field_146294_l / 2 - 155, this.field_146295_m - 38, GameSettings.Options.FORCE_UNICODE_FONT, this.field_146451_g.getKeyBinding(GameSettings.Options.FORCE_UNICODE_FONT)));
        this.field_146292_n.add(this.field_146452_r = new GuiOptionButton(6, this.field_146294_l / 2 - 155 + 160, this.field_146295_m - 38, I18n.getStringParams("gui.done", new Object[0])));
        this.field_146450_f = new GuiLanguage.List();
        this.field_146450_f.func_148134_d(7, 8);
    }

    protected void func_146284_a(GuiButton p_146284_1_)
    {
        if (p_146284_1_.field_146124_l)
        {
            switch (p_146284_1_.field_146127_k)
            {
                case 5:
                    break;
                case 6:
                    this.field_146297_k.func_147108_a(this.field_146453_a);
                    break;
                case 100:
                    if (p_146284_1_ instanceof GuiOptionButton)
                    {
                        this.field_146451_g.setOptionValue(((GuiOptionButton)p_146284_1_).func_146136_c(), 1);
                        p_146284_1_.field_146126_j = this.field_146451_g.getKeyBinding(GameSettings.Options.FORCE_UNICODE_FONT);
                    }

                    break;
                default:
                    this.field_146450_f.func_148147_a(p_146284_1_);
            }
        }
    }

    // JAVADOC METHOD $$ func_73863_a
    public void drawScreen(int par1, int par2, float par3)
    {
        this.field_146450_f.func_148128_a(par1, par2, par3);
        this.drawCenteredString(this.field_146289_q, I18n.getStringParams("options.language", new Object[0]), this.field_146294_l / 2, 16, 16777215);
        this.drawCenteredString(this.field_146289_q, "(" + I18n.getStringParams("options.languageWarning", new Object[0]) + ")", this.field_146294_l / 2, this.field_146295_m - 56, 8421504);
        super.drawScreen(par1, par2, par3);
    }

    @SideOnly(Side.CLIENT)
    class List extends GuiSlot
    {
        private final java.util.List field_148176_l = Lists.newArrayList();
        private final Map field_148177_m = Maps.newHashMap();
        private static final String __OBFID = "CL_00000699";

        public List()
        {
            super(GuiLanguage.this.field_146297_k, GuiLanguage.this.field_146294_l, GuiLanguage.this.field_146295_m, 32, GuiLanguage.this.field_146295_m - 65 + 4, 18);
            Iterator iterator = GuiLanguage.this.field_146454_h.getLanguages().iterator();

            while (iterator.hasNext())
            {
                Language language = (Language)iterator.next();
                this.field_148177_m.put(language.getLanguageCode(), language);
                this.field_148176_l.add(language.getLanguageCode());
            }
        }

        protected int func_148127_b()
        {
            return this.field_148176_l.size();
        }

        protected void func_148144_a(int p_148144_1_, boolean p_148144_2_, int p_148144_3_, int p_148144_4_)
        {
            Language language = (Language)this.field_148177_m.get(this.field_148176_l.get(p_148144_1_));
            GuiLanguage.this.field_146454_h.setCurrentLanguage(language);
            GuiLanguage.this.field_146451_g.language = language.getLanguageCode();
            GuiLanguage.this.field_146297_k.refreshResources();
            GuiLanguage.this.field_146289_q.setUnicodeFlag(GuiLanguage.this.field_146454_h.isCurrentLocaleUnicode() || GuiLanguage.this.field_146451_g.field_151455_aw);
            GuiLanguage.this.field_146289_q.setBidiFlag(GuiLanguage.this.field_146454_h.isCurrentLanguageBidirectional());
            GuiLanguage.this.field_146452_r.field_146126_j = I18n.getStringParams("gui.done", new Object[0]);
            GuiLanguage.this.field_146455_i.field_146126_j = GuiLanguage.this.field_146451_g.getKeyBinding(GameSettings.Options.FORCE_UNICODE_FONT);
            GuiLanguage.this.field_146451_g.saveOptions();
        }

        protected boolean func_148131_a(int p_148131_1_)
        {
            return ((String)this.field_148176_l.get(p_148131_1_)).equals(GuiLanguage.this.field_146454_h.getCurrentLanguage().getLanguageCode());
        }

        protected int func_148138_e()
        {
            return this.func_148127_b() * 18;
        }

        protected void func_148123_a()
        {
            GuiLanguage.this.func_146276_q_();
        }

        protected void func_148126_a(int p_148126_1_, int p_148126_2_, int p_148126_3_, int p_148126_4_, Tessellator p_148126_5_, int p_148126_6_, int p_148126_7_)
        {
            GuiLanguage.this.field_146289_q.setBidiFlag(true);
            GuiLanguage.this.drawCenteredString(GuiLanguage.this.field_146289_q, ((Language)this.field_148177_m.get(this.field_148176_l.get(p_148126_1_))).toString(), this.field_148155_a / 2, p_148126_3_ + 1, 16777215);
            GuiLanguage.this.field_146289_q.setBidiFlag(GuiLanguage.this.field_146454_h.getCurrentLanguage().isBidirectional());
        }
    }
}