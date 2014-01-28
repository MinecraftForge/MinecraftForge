package net.minecraft.client.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.Arrays;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.EnumChatFormatting;
import org.apache.commons.lang3.ArrayUtils;

@SideOnly(Side.CLIENT)
public class GuiKeyBindingList extends GuiListExtended
{
    private final GuiControls field_148191_k;
    private final Minecraft field_148189_l;
    private final GuiListExtended.IGuiListEntry[] field_148190_m;
    private int field_148188_n = 0;
    private static final String __OBFID = "CL_00000732";

    public GuiKeyBindingList(GuiControls p_i45031_1_, Minecraft p_i45031_2_)
    {
        super(p_i45031_2_, p_i45031_1_.field_146294_l, p_i45031_1_.field_146295_m, 63, p_i45031_1_.field_146295_m - 32, 20);
        this.field_148191_k = p_i45031_1_;
        this.field_148189_l = p_i45031_2_;
        KeyBinding[] akeybinding = (KeyBinding[])ArrayUtils.clone(p_i45031_2_.gameSettings.keyBindings);
        this.field_148190_m = new GuiListExtended.IGuiListEntry[akeybinding.length + KeyBinding.func_151467_c().size()];
        Arrays.sort(akeybinding);
        int i = 0;
        String s = null;
        KeyBinding[] akeybinding1 = akeybinding;
        int j = akeybinding.length;

        for (int k = 0; k < j; ++k)
        {
            KeyBinding keybinding = akeybinding1[k];
            String s1 = keybinding.func_151466_e();

            if (!s1.equals(s))
            {
                s = s1;
                this.field_148190_m[i++] = new GuiKeyBindingList.CategoryEntry(s1);
            }

            int l = p_i45031_2_.fontRenderer.getStringWidth(I18n.getStringParams(keybinding.func_151464_g(), new Object[0]));

            if (l > this.field_148188_n)
            {
                this.field_148188_n = l;
            }

            this.field_148190_m[i++] = new GuiKeyBindingList.KeyEntry(keybinding, null);
        }
    }

    protected int func_148127_b()
    {
        return this.field_148190_m.length;
    }

    public GuiListExtended.IGuiListEntry func_148180_b(int p_148180_1_)
    {
        return this.field_148190_m[p_148180_1_];
    }

    protected int func_148137_d()
    {
        return super.func_148137_d() + 15;
    }

    public int func_148139_c()
    {
        return super.func_148139_c() + 32;
    }

    @SideOnly(Side.CLIENT)
    public class CategoryEntry implements GuiListExtended.IGuiListEntry
    {
        private final String field_148285_b;
        private final int field_148286_c;
        private static final String __OBFID = "CL_00000734";

        public CategoryEntry(String p_i45028_2_)
        {
            this.field_148285_b = I18n.getStringParams(p_i45028_2_, new Object[0]);
            this.field_148286_c = GuiKeyBindingList.this.field_148189_l.fontRenderer.getStringWidth(this.field_148285_b);
        }

        public void func_148279_a(int p_148279_1_, int p_148279_2_, int p_148279_3_, int p_148279_4_, int p_148279_5_, Tessellator p_148279_6_, int p_148279_7_, int p_148279_8_, boolean p_148279_9_)
        {
            GuiKeyBindingList.this.field_148189_l.fontRenderer.drawString(this.field_148285_b, GuiKeyBindingList.this.field_148189_l.currentScreen.field_146294_l / 2 - this.field_148286_c / 2, p_148279_3_ + p_148279_5_ - GuiKeyBindingList.this.field_148189_l.fontRenderer.FONT_HEIGHT - 1, 16777215);
        }

        public boolean func_148278_a(int p_148278_1_, int p_148278_2_, int p_148278_3_, int p_148278_4_, int p_148278_5_, int p_148278_6_)
        {
            return false;
        }

        public void func_148277_b(int p_148277_1_, int p_148277_2_, int p_148277_3_, int p_148277_4_, int p_148277_5_, int p_148277_6_) {}
    }

    @SideOnly(Side.CLIENT)
    public class KeyEntry implements GuiListExtended.IGuiListEntry
    {
        private final KeyBinding field_148282_b;
        private final String field_148283_c;
        private final GuiButton field_148280_d;
        private final GuiButton field_148281_e;
        private static final String __OBFID = "CL_00000735";

        private KeyEntry(KeyBinding p_i45029_2_)
        {
            this.field_148282_b = p_i45029_2_;
            this.field_148283_c = I18n.getStringParams(p_i45029_2_.func_151464_g(), new Object[0]);
            this.field_148280_d = new GuiButton(0, 0, 0, 75, 18, I18n.getStringParams(p_i45029_2_.func_151464_g(), new Object[0]));
            this.field_148281_e = new GuiButton(0, 0, 0, 50, 18, I18n.getStringParams("controls.reset", new Object[0]));
        }

        public void func_148279_a(int p_148279_1_, int p_148279_2_, int p_148279_3_, int p_148279_4_, int p_148279_5_, Tessellator p_148279_6_, int p_148279_7_, int p_148279_8_, boolean p_148279_9_)
        {
            boolean flag1 = GuiKeyBindingList.this.field_148191_k.field_146491_f == this.field_148282_b;
            GuiKeyBindingList.this.field_148189_l.fontRenderer.drawString(this.field_148283_c, p_148279_2_ + 90 - GuiKeyBindingList.this.field_148188_n, p_148279_3_ + p_148279_5_ / 2 - GuiKeyBindingList.this.field_148189_l.fontRenderer.FONT_HEIGHT / 2, 16777215);
            this.field_148281_e.field_146128_h = p_148279_2_ + 190;
            this.field_148281_e.field_146129_i = p_148279_3_;
            this.field_148281_e.field_146124_l = this.field_148282_b.func_151463_i() != this.field_148282_b.func_151469_h();
            this.field_148281_e.func_146112_a(GuiKeyBindingList.this.field_148189_l, p_148279_7_, p_148279_8_);
            this.field_148280_d.field_146128_h = p_148279_2_ + 105;
            this.field_148280_d.field_146129_i = p_148279_3_;
            this.field_148280_d.field_146126_j = GameSettings.getKeyDisplayString(this.field_148282_b.func_151463_i());
            boolean flag2 = false;

            if (this.field_148282_b.func_151463_i() != 0)
            {
                KeyBinding[] akeybinding = GuiKeyBindingList.this.field_148189_l.gameSettings.keyBindings;
                int l1 = akeybinding.length;

                for (int i2 = 0; i2 < l1; ++i2)
                {
                    KeyBinding keybinding = akeybinding[i2];

                    if (keybinding != this.field_148282_b && keybinding.func_151463_i() == this.field_148282_b.func_151463_i())
                    {
                        flag2 = true;
                        break;
                    }
                }
            }

            if (flag1)
            {
                this.field_148280_d.field_146126_j = EnumChatFormatting.WHITE + "> " + EnumChatFormatting.YELLOW + this.field_148280_d.field_146126_j + EnumChatFormatting.WHITE + " <";
            }
            else if (flag2)
            {
                this.field_148280_d.field_146126_j = EnumChatFormatting.RED + this.field_148280_d.field_146126_j;
            }

            this.field_148280_d.func_146112_a(GuiKeyBindingList.this.field_148189_l, p_148279_7_, p_148279_8_);
        }

        public boolean func_148278_a(int p_148278_1_, int p_148278_2_, int p_148278_3_, int p_148278_4_, int p_148278_5_, int p_148278_6_)
        {
            if (this.field_148280_d.func_146116_c(GuiKeyBindingList.this.field_148189_l, p_148278_2_, p_148278_3_))
            {
                GuiKeyBindingList.this.field_148191_k.field_146491_f = this.field_148282_b;
                return true;
            }
            else if (this.field_148281_e.func_146116_c(GuiKeyBindingList.this.field_148189_l, p_148278_2_, p_148278_3_))
            {
                GuiKeyBindingList.this.field_148189_l.gameSettings.func_151440_a(this.field_148282_b, this.field_148282_b.func_151469_h());
                KeyBinding.resetKeyBindingArrayAndHash();
                return true;
            }
            else
            {
                return false;
            }
        }

        public void func_148277_b(int p_148277_1_, int p_148277_2_, int p_148277_3_, int p_148277_4_, int p_148277_5_, int p_148277_6_)
        {
            this.field_148280_d.func_146118_a(p_148277_2_, p_148277_3_);
            this.field_148281_e.func_146118_a(p_148277_2_, p_148277_3_);
        }

        KeyEntry(KeyBinding p_i45030_2_, Object p_i45030_3_)
        {
            this(p_i45030_2_);
        }
    }
}