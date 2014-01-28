package net.minecraft.client.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ContainerMerchant;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.util.ResourceLocation;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

@SideOnly(Side.CLIENT)
public class GuiMerchant extends GuiContainer
{
    private static final Logger field_147039_u = LogManager.getLogger();
    private static final ResourceLocation field_147038_v = new ResourceLocation("textures/gui/container/villager.png");
    private IMerchant field_147037_w;
    private GuiMerchant.MerchantButton field_147043_x;
    private GuiMerchant.MerchantButton field_147042_y;
    private int field_147041_z;
    private String field_147040_A;
    private static final String __OBFID = "CL_00000762";

    public GuiMerchant(InventoryPlayer par1InventoryPlayer, IMerchant par2IMerchant, World par3World, String par4Str)
    {
        super(new ContainerMerchant(par1InventoryPlayer, par2IMerchant, par3World));
        this.field_147037_w = par2IMerchant;
        this.field_147040_A = par4Str != null && par4Str.length() >= 1 ? par4Str : I18n.getStringParams("entity.Villager.name", new Object[0]);
    }

    // JAVADOC METHOD $$ func_73866_w_
    public void initGui()
    {
        super.initGui();
        int i = (this.field_146294_l - this.field_146999_f) / 2;
        int j = (this.field_146295_m - this.field_147000_g) / 2;
        this.field_146292_n.add(this.field_147043_x = new GuiMerchant.MerchantButton(1, i + 120 + 27, j + 24 - 1, true));
        this.field_146292_n.add(this.field_147042_y = new GuiMerchant.MerchantButton(2, i + 36 - 19, j + 24 - 1, false));
        this.field_147043_x.field_146124_l = false;
        this.field_147042_y.field_146124_l = false;
    }

    protected void func_146979_b(int p_146979_1_, int p_146979_2_)
    {
        this.field_146289_q.drawString(this.field_147040_A, this.field_146999_f / 2 - this.field_146289_q.getStringWidth(this.field_147040_A) / 2, 6, 4210752);
        this.field_146289_q.drawString(I18n.getStringParams("container.inventory", new Object[0]), 8, this.field_147000_g - 96 + 2, 4210752);
    }

    // JAVADOC METHOD $$ func_73876_c
    public void updateScreen()
    {
        super.updateScreen();
        MerchantRecipeList merchantrecipelist = this.field_147037_w.getRecipes(this.field_146297_k.thePlayer);

        if (merchantrecipelist != null)
        {
            this.field_147043_x.field_146124_l = this.field_147041_z < merchantrecipelist.size() - 1;
            this.field_147042_y.field_146124_l = this.field_147041_z > 0;
        }
    }

    protected void func_146284_a(GuiButton p_146284_1_)
    {
        boolean flag = false;

        if (p_146284_1_ == this.field_147043_x)
        {
            ++this.field_147041_z;
            flag = true;
        }
        else if (p_146284_1_ == this.field_147042_y)
        {
            --this.field_147041_z;
            flag = true;
        }

        if (flag)
        {
            ((ContainerMerchant)this.field_147002_h).setCurrentRecipeIndex(this.field_147041_z);
            ByteBuf bytebuf = Unpooled.buffer();

            try
            {
                bytebuf.writeInt(this.field_147041_z);
                this.field_146297_k.func_147114_u().func_147297_a(new C17PacketCustomPayload("MC|TrSel", bytebuf));
            }
            catch (Exception exception)
            {
                field_147039_u.error("Couldn\'t send trade info", exception);
            }
            finally
            {
                bytebuf.release();
            }
        }
    }

    protected void func_146976_a(float p_146976_1_, int p_146976_2_, int p_146976_3_)
    {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.field_146297_k.getTextureManager().bindTexture(field_147038_v);
        int k = (this.field_146294_l - this.field_146999_f) / 2;
        int l = (this.field_146295_m - this.field_147000_g) / 2;
        this.drawTexturedModalRect(k, l, 0, 0, this.field_146999_f, this.field_147000_g);
        MerchantRecipeList merchantrecipelist = this.field_147037_w.getRecipes(this.field_146297_k.thePlayer);

        if (merchantrecipelist != null && !merchantrecipelist.isEmpty())
        {
            int i1 = this.field_147041_z;
            MerchantRecipe merchantrecipe = (MerchantRecipe)merchantrecipelist.get(i1);

            if (merchantrecipe.func_82784_g())
            {
                this.field_146297_k.getTextureManager().bindTexture(field_147038_v);
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                GL11.glDisable(GL11.GL_LIGHTING);
                this.drawTexturedModalRect(this.field_147003_i + 83, this.field_147009_r + 21, 212, 0, 28, 21);
                this.drawTexturedModalRect(this.field_147003_i + 83, this.field_147009_r + 51, 212, 0, 28, 21);
            }
        }
    }

    // JAVADOC METHOD $$ func_73863_a
    public void drawScreen(int par1, int par2, float par3)
    {
        super.drawScreen(par1, par2, par3);
        MerchantRecipeList merchantrecipelist = this.field_147037_w.getRecipes(this.field_146297_k.thePlayer);

        if (merchantrecipelist != null && !merchantrecipelist.isEmpty())
        {
            int k = (this.field_146294_l - this.field_146999_f) / 2;
            int l = (this.field_146295_m - this.field_147000_g) / 2;
            int i1 = this.field_147041_z;
            MerchantRecipe merchantrecipe = (MerchantRecipe)merchantrecipelist.get(i1);
            GL11.glPushMatrix();
            ItemStack itemstack = merchantrecipe.getItemToBuy();
            ItemStack itemstack1 = merchantrecipe.getSecondItemToBuy();
            ItemStack itemstack2 = merchantrecipe.getItemToSell();
            RenderHelper.enableGUIStandardItemLighting();
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
            GL11.glEnable(GL11.GL_COLOR_MATERIAL);
            GL11.glEnable(GL11.GL_LIGHTING);
            field_146296_j.zLevel = 100.0F;
            field_146296_j.renderItemAndEffectIntoGUI(this.field_146289_q, this.field_146297_k.getTextureManager(), itemstack, k + 36, l + 24);
            field_146296_j.renderItemOverlayIntoGUI(this.field_146289_q, this.field_146297_k.getTextureManager(), itemstack, k + 36, l + 24);

            if (itemstack1 != null)
            {
                field_146296_j.renderItemAndEffectIntoGUI(this.field_146289_q, this.field_146297_k.getTextureManager(), itemstack1, k + 62, l + 24);
                field_146296_j.renderItemOverlayIntoGUI(this.field_146289_q, this.field_146297_k.getTextureManager(), itemstack1, k + 62, l + 24);
            }

            field_146296_j.renderItemAndEffectIntoGUI(this.field_146289_q, this.field_146297_k.getTextureManager(), itemstack2, k + 120, l + 24);
            field_146296_j.renderItemOverlayIntoGUI(this.field_146289_q, this.field_146297_k.getTextureManager(), itemstack2, k + 120, l + 24);
            field_146296_j.zLevel = 0.0F;
            GL11.glDisable(GL11.GL_LIGHTING);

            if (this.func_146978_c(36, 24, 16, 16, par1, par2))
            {
                this.func_146285_a(itemstack, par1, par2);
            }
            else if (itemstack1 != null && this.func_146978_c(62, 24, 16, 16, par1, par2))
            {
                this.func_146285_a(itemstack1, par1, par2);
            }
            else if (this.func_146978_c(120, 24, 16, 16, par1, par2))
            {
                this.func_146285_a(itemstack2, par1, par2);
            }

            GL11.glPopMatrix();
            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            RenderHelper.enableStandardItemLighting();
        }
    }

    public IMerchant func_147035_g()
    {
        return this.field_147037_w;
    }

    @SideOnly(Side.CLIENT)
    static class MerchantButton extends GuiButton
        {
            private final boolean field_146157_o;
            private static final String __OBFID = "CL_00000763";

            public MerchantButton(int par1, int par2, int par3, boolean par4)
            {
                super(par1, par2, par3, 12, 19, "");
                this.field_146157_o = par4;
            }

            public void func_146112_a(Minecraft p_146112_1_, int p_146112_2_, int p_146112_3_)
            {
                if (this.field_146125_m)
                {
                    p_146112_1_.getTextureManager().bindTexture(GuiMerchant.field_147038_v);
                    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                    boolean flag = p_146112_2_ >= this.field_146128_h && p_146112_3_ >= this.field_146129_i && p_146112_2_ < this.field_146128_h + this.field_146120_f && p_146112_3_ < this.field_146129_i + this.field_146121_g;
                    int k = 0;
                    int l = 176;

                    if (!this.field_146124_l)
                    {
                        l += this.field_146120_f * 2;
                    }
                    else if (flag)
                    {
                        l += this.field_146120_f;
                    }

                    if (!this.field_146157_o)
                    {
                        k += this.field_146121_g;
                    }

                    this.drawTexturedModalRect(this.field_146128_h, this.field_146129_i, l, k, this.field_146120_f, this.field_146121_g);
                }
            }
        }
}