package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.settings.GameSettings;

@SideOnly(Side.CLIENT)
public class GuiOptionsRowList extends GuiListExtended
{
    private final List field_148184_k = Lists.newArrayList();
    private static final String __OBFID = "CL_00000677";

    public GuiOptionsRowList(Minecraft p_i45015_1_, int p_i45015_2_, int p_i45015_3_, int p_i45015_4_, int p_i45015_5_, int p_i45015_6_, GameSettings.Options ... p_i45015_7_)
    {
        super(p_i45015_1_, p_i45015_2_, p_i45015_3_, p_i45015_4_, p_i45015_5_, p_i45015_6_);
        this.field_148163_i = false;

        for (int j1 = 0; j1 < p_i45015_7_.length; j1 += 2)
        {
            GameSettings.Options options = p_i45015_7_[j1];
            GameSettings.Options options1 = j1 < p_i45015_7_.length - 1 ? p_i45015_7_[j1 + 1] : null;
            GuiButton guibutton = this.func_148182_a(p_i45015_1_, p_i45015_2_ / 2 - 155, 0, options);
            GuiButton guibutton1 = this.func_148182_a(p_i45015_1_, p_i45015_2_ / 2 - 155 + 160, 0, options1);
            this.field_148184_k.add(new GuiOptionsRowList.Row(guibutton, guibutton1));
        }
    }

    private GuiButton func_148182_a(Minecraft p_148182_1_, int p_148182_2_, int p_148182_3_, GameSettings.Options p_148182_4_)
    {
        if (p_148182_4_ == null)
        {
            return null;
        }
        else
        {
            int k = p_148182_4_.returnEnumOrdinal();
            return (GuiButton)(p_148182_4_.getEnumFloat() ? new GuiOptionSlider(k, p_148182_2_, p_148182_3_, p_148182_4_) : new GuiOptionButton(k, p_148182_2_, p_148182_3_, p_148182_4_, p_148182_1_.gameSettings.getKeyBinding(p_148182_4_)));
        }
    }

    public GuiOptionsRowList.Row func_148180_b(int p_148183_1_)
    {
        return (GuiOptionsRowList.Row)this.field_148184_k.get(p_148183_1_);
    }

    protected int func_148127_b()
    {
        return this.field_148184_k.size();
    }

    public int func_148139_c()
    {
        return 400;
    }

    protected int func_148137_d()
    {
        return super.func_148137_d() + 32;
    }

    @SideOnly(Side.CLIENT)
    public static class Row implements GuiListExtended.IGuiListEntry
        {
            private final Minecraft field_148325_a = Minecraft.getMinecraft();
            private final GuiButton field_148323_b;
            private final GuiButton field_148324_c;
            private static final String __OBFID = "CL_00000678";

            public Row(GuiButton p_i45014_1_, GuiButton p_i45014_2_)
            {
                this.field_148323_b = p_i45014_1_;
                this.field_148324_c = p_i45014_2_;
            }

            public void func_148279_a(int p_148279_1_, int p_148279_2_, int p_148279_3_, int p_148279_4_, int p_148279_5_, Tessellator p_148279_6_, int p_148279_7_, int p_148279_8_, boolean p_148279_9_)
            {
                if (this.field_148323_b != null)
                {
                    this.field_148323_b.field_146129_i = p_148279_3_;
                    this.field_148323_b.func_146112_a(this.field_148325_a, p_148279_7_, p_148279_8_);
                }

                if (this.field_148324_c != null)
                {
                    this.field_148324_c.field_146129_i = p_148279_3_;
                    this.field_148324_c.func_146112_a(this.field_148325_a, p_148279_7_, p_148279_8_);
                }
            }

            public boolean func_148278_a(int p_148278_1_, int p_148278_2_, int p_148278_3_, int p_148278_4_, int p_148278_5_, int p_148278_6_)
            {
                if (this.field_148323_b.func_146116_c(this.field_148325_a, p_148278_2_, p_148278_3_))
                {
                    if (this.field_148323_b instanceof GuiOptionButton)
                    {
                        this.field_148325_a.gameSettings.setOptionValue(((GuiOptionButton)this.field_148323_b).func_146136_c(), 1);
                        this.field_148323_b.field_146126_j = this.field_148325_a.gameSettings.getKeyBinding(GameSettings.Options.getEnumOptions(this.field_148323_b.field_146127_k));
                    }

                    return true;
                }
                else if (this.field_148324_c != null && this.field_148324_c.func_146116_c(this.field_148325_a, p_148278_2_, p_148278_3_))
                {
                    if (this.field_148324_c instanceof GuiOptionButton)
                    {
                        this.field_148325_a.gameSettings.setOptionValue(((GuiOptionButton)this.field_148324_c).func_146136_c(), 1);
                        this.field_148324_c.field_146126_j = this.field_148325_a.gameSettings.getKeyBinding(GameSettings.Options.getEnumOptions(this.field_148324_c.field_146127_k));
                    }

                    return true;
                }
                else
                {
                    return false;
                }
            }

            public void func_148277_b(int p_148277_1_, int p_148277_2_, int p_148277_3_, int p_148277_4_, int p_148277_5_, int p_148277_6_)
            {
                if (this.field_148323_b != null)
                {
                    this.field_148323_b.func_146118_a(p_148277_2_, p_148277_3_);
                }

                if (this.field_148324_c != null)
                {
                    this.field_148324_c.func_146118_a(p_148277_2_, p_148277_3_);
                }
            }
        }
}