package net.minecraft.client.renderer.tileentity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelSkeletonHead;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

@SideOnly(Side.CLIENT)
public class TileEntitySkullRenderer extends TileEntitySpecialRenderer
{
    private static final ResourceLocation field_147537_c = new ResourceLocation("textures/entity/skeleton/skeleton.png");
    private static final ResourceLocation field_147534_d = new ResourceLocation("textures/entity/skeleton/wither_skeleton.png");
    private static final ResourceLocation field_147535_e = new ResourceLocation("textures/entity/zombie/zombie.png");
    private static final ResourceLocation field_147532_f = new ResourceLocation("textures/entity/creeper/creeper.png");
    public static TileEntitySkullRenderer field_147536_b;
    private ModelSkeletonHead field_147533_g = new ModelSkeletonHead(0, 0, 64, 32);
    private ModelSkeletonHead field_147538_h = new ModelSkeletonHead(0, 0, 64, 64);
    private static final String __OBFID = "CL_00000971";

    public void func_147500_a(TileEntitySkull p_147531_1_, double p_147531_2_, double p_147531_4_, double p_147531_6_, float p_147531_8_)
    {
        this.func_147530_a((float)p_147531_2_, (float)p_147531_4_, (float)p_147531_6_, p_147531_1_.func_145832_p() & 7, (float)(p_147531_1_.func_145906_b() * 360) / 16.0F, p_147531_1_.func_145904_a(), p_147531_1_.func_145907_c());
    }

    public void func_147497_a(TileEntityRendererDispatcher p_147497_1_)
    {
        super.func_147497_a(p_147497_1_);
        field_147536_b = this;
    }

    public void func_147530_a(float p_147530_1_, float p_147530_2_, float p_147530_3_, int p_147530_4_, float p_147530_5_, int p_147530_6_, String p_147530_7_)
    {
        ModelSkeletonHead modelskeletonhead = this.field_147533_g;

        switch (p_147530_6_)
        {
            case 0:
            default:
                this.func_147499_a(field_147537_c);
                break;
            case 1:
                this.func_147499_a(field_147534_d);
                break;
            case 2:
                this.func_147499_a(field_147535_e);
                modelskeletonhead = this.field_147538_h;
                break;
            case 3:
                ResourceLocation resourcelocation = AbstractClientPlayer.locationStevePng;

                if (p_147530_7_ != null && p_147530_7_.length() > 0)
                {
                    resourcelocation = AbstractClientPlayer.getLocationSkull(p_147530_7_);
                    AbstractClientPlayer.getDownloadImageSkin(resourcelocation, p_147530_7_);
                }

                this.func_147499_a(resourcelocation);
                break;
            case 4:
                this.func_147499_a(field_147532_f);
        }

        GL11.glPushMatrix();
        GL11.glDisable(GL11.GL_CULL_FACE);

        if (p_147530_4_ != 1)
        {
            switch (p_147530_4_)
            {
                case 2:
                    GL11.glTranslatef(p_147530_1_ + 0.5F, p_147530_2_ + 0.25F, p_147530_3_ + 0.74F);
                    break;
                case 3:
                    GL11.glTranslatef(p_147530_1_ + 0.5F, p_147530_2_ + 0.25F, p_147530_3_ + 0.26F);
                    p_147530_5_ = 180.0F;
                    break;
                case 4:
                    GL11.glTranslatef(p_147530_1_ + 0.74F, p_147530_2_ + 0.25F, p_147530_3_ + 0.5F);
                    p_147530_5_ = 270.0F;
                    break;
                case 5:
                default:
                    GL11.glTranslatef(p_147530_1_ + 0.26F, p_147530_2_ + 0.25F, p_147530_3_ + 0.5F);
                    p_147530_5_ = 90.0F;
            }
        }
        else
        {
            GL11.glTranslatef(p_147530_1_ + 0.5F, p_147530_2_, p_147530_3_ + 0.5F);
        }

        float f4 = 0.0625F;
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        GL11.glScalef(-1.0F, -1.0F, 1.0F);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        modelskeletonhead.render((Entity)null, 0.0F, 0.0F, 0.0F, p_147530_5_, 0.0F, f4);
        GL11.glPopMatrix();
    }

    public void func_147500_a(TileEntity p_147500_1_, double p_147500_2_, double p_147500_4_, double p_147500_6_, float p_147500_8_)
    {
        this.func_147500_a((TileEntitySkull)p_147500_1_, p_147500_2_, p_147500_4_, p_147500_6_, p_147500_8_);
    }
}