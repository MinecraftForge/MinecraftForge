package net.minecraft.client.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;

@SideOnly(Side.CLIENT)
public class GuiControls extends GuiScreen
{
    private static final GameSettings.Options[] field_146492_g = new GameSettings.Options[] {GameSettings.Options.INVERT_MOUSE, GameSettings.Options.SENSITIVITY, GameSettings.Options.TOUCHSCREEN};
    private GuiScreen field_146496_h;
    protected String field_146495_a = "Controls";
    private GameSettings field_146497_i;
    public KeyBinding field_146491_f = null;
    private GuiKeyBindingList field_146494_r;
    private GuiButton field_146493_s;
    private static final String __OBFID = "CL_00000736";

    public GuiControls(GuiScreen par1GuiScreen, GameSettings par2GameSettings)
    {
        this.field_146496_h = par1GuiScreen;
        this.field_146497_i = par2GameSettings;
    }

    // JAVADOC METHOD $$ func_73866_w_
    public void initGui()
    {
        this.field_146494_r = new GuiKeyBindingList(this, this.field_146297_k);
        this.field_146292_n.add(new GuiButton(200, this.field_146294_l / 2 - 155, this.field_146295_m - 29, 150, 20, I18n.getStringParams("gui.done", new Object[0])));
        this.field_146292_n.add(this.field_146493_s = new GuiButton(201, this.field_146294_l / 2 - 155 + 160, this.field_146295_m - 29, 150, 20, I18n.getStringParams("controls.resetAll", new Object[0])));
        this.field_146495_a = I18n.getStringParams("controls.title", new Object[0]);
        int i = 0;
        GameSettings.Options[] aoptions = field_146492_g;
        int j = aoptions.length;

        for (int k = 0; k < j; ++k)
        {
            GameSettings.Options options = aoptions[k];

            if (options.getEnumFloat())
            {
                this.field_146292_n.add(new GuiOptionSlider(options.returnEnumOrdinal(), this.field_146294_l / 2 - 155 + i % 2 * 160, 18 + 24 * (i >> 1), options));
            }
            else
            {
                this.field_146292_n.add(new GuiOptionButton(options.returnEnumOrdinal(), this.field_146294_l / 2 - 155 + i % 2 * 160, 18 + 24 * (i >> 1), options, this.field_146497_i.getKeyBinding(options)));
            }

            ++i;
        }
    }

    protected void func_146284_a(GuiButton p_146284_1_)
    {
        if (p_146284_1_.field_146127_k == 200)
        {
            this.field_146297_k.func_147108_a(this.field_146496_h);
        }
        else if (p_146284_1_.field_146127_k == 201)
        {
            KeyBinding[] akeybinding = this.field_146297_k.gameSettings.keyBindings;
            int i = akeybinding.length;

            for (int j = 0; j < i; ++j)
            {
                KeyBinding keybinding = akeybinding[j];
                keybinding.func_151462_b(keybinding.func_151469_h());
            }

            KeyBinding.resetKeyBindingArrayAndHash();
        }
        else if (p_146284_1_.field_146127_k < 100 && p_146284_1_ instanceof GuiOptionButton)
        {
            this.field_146497_i.setOptionValue(((GuiOptionButton)p_146284_1_).func_146136_c(), 1);
            p_146284_1_.field_146126_j = this.field_146497_i.getKeyBinding(GameSettings.Options.getEnumOptions(p_146284_1_.field_146127_k));
        }
    }

    // JAVADOC METHOD $$ func_73864_a
    protected void mouseClicked(int par1, int par2, int par3)
    {
        if (this.field_146491_f != null)
        {
            this.field_146497_i.func_151440_a(this.field_146491_f, -100 + par3);
            this.field_146491_f = null;
            KeyBinding.resetKeyBindingArrayAndHash();
        }
        else if (par3 != 0 || !this.field_146494_r.func_148179_a(par1, par2, par3))
        {
            super.mouseClicked(par1, par2, par3);
        }
    }

    protected void func_146286_b(int p_146286_1_, int p_146286_2_, int p_146286_3_)
    {
        if (p_146286_3_ != 0 || !this.field_146494_r.func_148181_b(p_146286_1_, p_146286_2_, p_146286_3_))
        {
            super.func_146286_b(p_146286_1_, p_146286_2_, p_146286_3_);
        }
    }

    // JAVADOC METHOD $$ func_73869_a
    protected void keyTyped(char par1, int par2)
    {
        if (this.field_146491_f != null)
        {
            if (par2 == 1)
            {
                this.field_146497_i.func_151440_a(this.field_146491_f, 0);
            }
            else
            {
                this.field_146497_i.func_151440_a(this.field_146491_f, par2);
            }

            this.field_146491_f = null;
            KeyBinding.resetKeyBindingArrayAndHash();
        }
        else
        {
            super.keyTyped(par1, par2);
        }
    }

    // JAVADOC METHOD $$ func_73863_a
    public void drawScreen(int par1, int par2, float par3)
    {
        this.func_146276_q_();
        this.field_146494_r.func_148128_a(par1, par2, par3);
        this.drawCenteredString(this.field_146289_q, this.field_146495_a, this.field_146294_l / 2, 8, 16777215);
        boolean flag = true;
        KeyBinding[] akeybinding = this.field_146497_i.keyBindings;
        int k = akeybinding.length;

        for (int l = 0; l < k; ++l)
        {
            KeyBinding keybinding = akeybinding[l];

            if (keybinding.func_151463_i() != keybinding.func_151469_h())
            {
                flag = false;
                break;
            }
        }

        this.field_146493_s.field_146124_l = !flag;
        super.drawScreen(par1, par2, par3);
    }
}