package net.minecraft.client.gui.inventory;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.util.Iterator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ContainerBeacon;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.potion.Potion;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class GuiBeacon extends GuiContainer
{
    private static final Logger field_147026_u = LogManager.getLogger();
    private static final ResourceLocation field_147025_v = new ResourceLocation("textures/gui/container/beacon.png");
    private TileEntityBeacon field_147024_w;
    private GuiBeacon.ConfirmButton field_147028_x;
    private boolean field_147027_y;
    private static final String __OBFID = "CL_00000739";

    public GuiBeacon(InventoryPlayer par1InventoryPlayer, TileEntityBeacon par2TileEntityBeacon)
    {
        super(new ContainerBeacon(par1InventoryPlayer, par2TileEntityBeacon));
        this.field_147024_w = par2TileEntityBeacon;
        this.field_146999_f = 230;
        this.field_147000_g = 219;
    }

    // JAVADOC METHOD $$ func_73866_w_
    public void initGui()
    {
        super.initGui();
        this.field_146292_n.add(this.field_147028_x = new GuiBeacon.ConfirmButton(-1, this.field_147003_i + 164, this.field_147009_r + 107));
        this.field_146292_n.add(new GuiBeacon.CancelButton(-2, this.field_147003_i + 190, this.field_147009_r + 107));
        this.field_147027_y = true;
        this.field_147028_x.field_146124_l = false;
    }

    // JAVADOC METHOD $$ func_73876_c
    public void updateScreen()
    {
        super.updateScreen();

        if (this.field_147027_y && this.field_147024_w.func_145998_l() >= 0)
        {
            this.field_147027_y = false;
            int j;
            int k;
            int l;
            int i1;
            GuiBeacon.PowerButton powerbutton;

            for (int i = 0; i <= 2; ++i)
            {
                j = TileEntityBeacon.field_146009_a[i].length;
                k = j * 22 + (j - 1) * 2;

                for (l = 0; l < j; ++l)
                {
                    i1 = TileEntityBeacon.field_146009_a[i][l].id;
                    powerbutton = new GuiBeacon.PowerButton(i << 8 | i1, this.field_147003_i + 76 + l * 24 - k / 2, this.field_147009_r + 22 + i * 25, i1, i);
                    this.field_146292_n.add(powerbutton);

                    if (i >= this.field_147024_w.func_145998_l())
                    {
                        powerbutton.field_146124_l = false;
                    }
                    else if (i1 == this.field_147024_w.func_146007_j())
                    {
                        powerbutton.func_146140_b(true);
                    }
                }
            }

            byte b0 = 3;
            j = TileEntityBeacon.field_146009_a[b0].length + 1;
            k = j * 22 + (j - 1) * 2;

            for (l = 0; l < j - 1; ++l)
            {
                i1 = TileEntityBeacon.field_146009_a[b0][l].id;
                powerbutton = new GuiBeacon.PowerButton(b0 << 8 | i1, this.field_147003_i + 167 + l * 24 - k / 2, this.field_147009_r + 47, i1, b0);
                this.field_146292_n.add(powerbutton);

                if (b0 >= this.field_147024_w.func_145998_l())
                {
                    powerbutton.field_146124_l = false;
                }
                else if (i1 == this.field_147024_w.func_146006_k())
                {
                    powerbutton.func_146140_b(true);
                }
            }

            if (this.field_147024_w.func_146007_j() > 0)
            {
                GuiBeacon.PowerButton powerbutton1 = new GuiBeacon.PowerButton(b0 << 8 | this.field_147024_w.func_146007_j(), this.field_147003_i + 167 + (j - 1) * 24 - k / 2, this.field_147009_r + 47, this.field_147024_w.func_146007_j(), b0);
                this.field_146292_n.add(powerbutton1);

                if (b0 >= this.field_147024_w.func_145998_l())
                {
                    powerbutton1.field_146124_l = false;
                }
                else if (this.field_147024_w.func_146007_j() == this.field_147024_w.func_146006_k())
                {
                    powerbutton1.func_146140_b(true);
                }
            }
        }

        this.field_147028_x.field_146124_l = this.field_147024_w.getStackInSlot(0) != null && this.field_147024_w.func_146007_j() > 0;
    }

    protected void func_146284_a(GuiButton p_146284_1_)
    {
        if (p_146284_1_.field_146127_k == -2)
        {
            this.field_146297_k.func_147108_a((GuiScreen)null);
        }
        else if (p_146284_1_.field_146127_k == -1)
        {
            String s = "MC|Beacon";
            ByteBuf bytebuf = Unpooled.buffer();

            try
            {
                bytebuf.writeInt(this.field_147024_w.func_146007_j());
                bytebuf.writeInt(this.field_147024_w.func_146006_k());
                this.field_146297_k.func_147114_u().func_147297_a(new C17PacketCustomPayload(s, bytebuf));
            }
            catch (Exception exception)
            {
                field_147026_u.error("Couldn\'t send beacon info", exception);
            }
            finally
            {
                bytebuf.release();
            }

            this.field_146297_k.func_147108_a((GuiScreen)null);
        }
        else if (p_146284_1_ instanceof GuiBeacon.PowerButton)
        {
            if (((GuiBeacon.PowerButton)p_146284_1_).func_146141_c())
            {
                return;
            }

            int j = p_146284_1_.field_146127_k;
            int k = j & 255;
            int i = j >> 8;

            if (i < 3)
            {
                this.field_147024_w.func_146001_d(k);
            }
            else
            {
                this.field_147024_w.func_146004_e(k);
            }

            this.field_146292_n.clear();
            this.initGui();
            this.updateScreen();
        }
    }

    protected void func_146979_b(int p_146979_1_, int p_146979_2_)
    {
        RenderHelper.disableStandardItemLighting();
        this.drawCenteredString(this.field_146289_q, I18n.getStringParams("tile.beacon.primary", new Object[0]), 62, 10, 14737632);
        this.drawCenteredString(this.field_146289_q, I18n.getStringParams("tile.beacon.secondary", new Object[0]), 169, 10, 14737632);
        Iterator iterator = this.field_146292_n.iterator();

        while (iterator.hasNext())
        {
            GuiButton guibutton = (GuiButton)iterator.next();

            if (guibutton.func_146115_a())
            {
                guibutton.func_146111_b(p_146979_1_ - this.field_147003_i, p_146979_2_ - this.field_147009_r);
                break;
            }
        }

        RenderHelper.enableGUIStandardItemLighting();
    }

    protected void func_146976_a(float p_146976_1_, int p_146976_2_, int p_146976_3_)
    {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.field_146297_k.getTextureManager().bindTexture(field_147025_v);
        int k = (this.field_146294_l - this.field_146999_f) / 2;
        int l = (this.field_146295_m - this.field_147000_g) / 2;
        this.drawTexturedModalRect(k, l, 0, 0, this.field_146999_f, this.field_147000_g);
        field_146296_j.zLevel = 100.0F;
        field_146296_j.renderItemAndEffectIntoGUI(this.field_146289_q, this.field_146297_k.getTextureManager(), new ItemStack(Items.emerald), k + 42, l + 109);
        field_146296_j.renderItemAndEffectIntoGUI(this.field_146289_q, this.field_146297_k.getTextureManager(), new ItemStack(Items.diamond), k + 42 + 22, l + 109);
        field_146296_j.renderItemAndEffectIntoGUI(this.field_146289_q, this.field_146297_k.getTextureManager(), new ItemStack(Items.gold_ingot), k + 42 + 44, l + 109);
        field_146296_j.renderItemAndEffectIntoGUI(this.field_146289_q, this.field_146297_k.getTextureManager(), new ItemStack(Items.iron_ingot), k + 42 + 66, l + 109);
        field_146296_j.zLevel = 0.0F;
    }

    @SideOnly(Side.CLIENT)
    static class Button extends GuiButton
        {
            private final ResourceLocation field_146145_o;
            private final int field_146144_p;
            private final int field_146143_q;
            private boolean field_146142_r;
            private static final String __OBFID = "CL_00000743";

            protected Button(int par1, int par2, int par3, ResourceLocation par4ResourceLocation, int par5, int par6)
            {
                super(par1, par2, par3, 22, 22, "");
                this.field_146145_o = par4ResourceLocation;
                this.field_146144_p = par5;
                this.field_146143_q = par6;
            }

            public void func_146112_a(Minecraft p_146112_1_, int p_146112_2_, int p_146112_3_)
            {
                if (this.field_146125_m)
                {
                    p_146112_1_.getTextureManager().bindTexture(GuiBeacon.field_147025_v);
                    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                    this.field_146123_n = p_146112_2_ >= this.field_146128_h && p_146112_3_ >= this.field_146129_i && p_146112_2_ < this.field_146128_h + this.field_146120_f && p_146112_3_ < this.field_146129_i + this.field_146121_g;
                    short short1 = 219;
                    int k = 0;

                    if (!this.field_146124_l)
                    {
                        k += this.field_146120_f * 2;
                    }
                    else if (this.field_146142_r)
                    {
                        k += this.field_146120_f * 1;
                    }
                    else if (this.field_146123_n)
                    {
                        k += this.field_146120_f * 3;
                    }

                    this.drawTexturedModalRect(this.field_146128_h, this.field_146129_i, k, short1, this.field_146120_f, this.field_146121_g);

                    if (!GuiBeacon.field_147025_v.equals(this.field_146145_o))
                    {
                        p_146112_1_.getTextureManager().bindTexture(this.field_146145_o);
                    }

                    this.drawTexturedModalRect(this.field_146128_h + 2, this.field_146129_i + 2, this.field_146144_p, this.field_146143_q, 18, 18);
                }
            }

            public boolean func_146141_c()
            {
                return this.field_146142_r;
            }

            public void func_146140_b(boolean p_146140_1_)
            {
                this.field_146142_r = p_146140_1_;
            }
        }

    @SideOnly(Side.CLIENT)
    class CancelButton extends GuiBeacon.Button
    {
        private static final String __OBFID = "CL_00000740";

        public CancelButton(int par2, int par3, int par4)
        {
            super(par2, par3, par4, GuiBeacon.field_147025_v, 112, 220);
        }

        public void func_146111_b(int p_146111_1_, int p_146111_2_)
        {
            GuiBeacon.this.func_146279_a(I18n.getStringParams("gui.cancel", new Object[0]), p_146111_1_, p_146111_2_);
        }
    }

    @SideOnly(Side.CLIENT)
    class PowerButton extends GuiBeacon.Button
    {
        private final int field_146149_p;
        private final int field_146148_q;
        private static final String __OBFID = "CL_00000742";

        public PowerButton(int par2, int par3, int par4, int par5, int par6)
        {
            super(par2, par3, par4, GuiContainer.field_147001_a, 0 + Potion.potionTypes[par5].getStatusIconIndex() % 8 * 18, 198 + Potion.potionTypes[par5].getStatusIconIndex() / 8 * 18);
            this.field_146149_p = par5;
            this.field_146148_q = par6;
        }

        public void func_146111_b(int p_146111_1_, int p_146111_2_)
        {
            String s = I18n.getStringParams(Potion.potionTypes[this.field_146149_p].getName(), new Object[0]);

            if (this.field_146148_q >= 3 && this.field_146149_p != Potion.regeneration.id)
            {
                s = s + " II";
            }

            GuiBeacon.this.func_146279_a(s, p_146111_1_, p_146111_2_);
        }
    }

    @SideOnly(Side.CLIENT)
    class ConfirmButton extends GuiBeacon.Button
    {
        private static final String __OBFID = "CL_00000741";

        public ConfirmButton(int par2, int par3, int par4)
        {
            super(par2, par3, par4, GuiBeacon.field_147025_v, 90, 220);
        }

        public void func_146111_b(int p_146111_1_, int p_146111_2_)
        {
            GuiBeacon.this.func_146279_a(I18n.getStringParams("gui.done", new Object[0]), p_146111_1_, p_146111_2_);
        }
    }
}