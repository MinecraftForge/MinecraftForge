package net.minecraft.client.renderer.entity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAnvil;
import net.minecraft.block.BlockDragonEgg;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class RenderFallingBlock extends Render
{
    private final RenderBlocks field_147920_a = new RenderBlocks();
    private static final String __OBFID = "CL_00000994";

    public RenderFallingBlock()
    {
        this.shadowSize = 0.5F;
    }

    // JAVADOC METHOD $$ func_76986_a
    public void doRender(EntityFallingBlock p_147918_1_, double p_147918_2_, double p_147918_4_, double p_147918_6_, float p_147918_8_, float p_147918_9_)
    {
        World world = p_147918_1_.func_145807_e();
        Block block = p_147918_1_.func_145805_f();
        int i = MathHelper.floor_double(p_147918_1_.posX);
        int j = MathHelper.floor_double(p_147918_1_.posY);
        int k = MathHelper.floor_double(p_147918_1_.posZ);

        if (block != null && block != world.func_147439_a(i, j, k))
        {
            GL11.glPushMatrix();
            GL11.glTranslatef((float)p_147918_2_, (float)p_147918_4_, (float)p_147918_6_);
            this.bindEntityTexture(p_147918_1_);
            GL11.glDisable(GL11.GL_LIGHTING);
            Tessellator tessellator;

            if (block instanceof BlockAnvil)
            {
                this.field_147920_a.field_147845_a = world;
                tessellator = Tessellator.instance;
                tessellator.startDrawingQuads();
                tessellator.setTranslation((double)((float)(-i) - 0.5F), (double)((float)(-j) - 0.5F), (double)((float)(-k) - 0.5F));
                this.field_147920_a.func_147780_a((BlockAnvil)block, i, j, k, p_147918_1_.field_145814_a);
                tessellator.setTranslation(0.0D, 0.0D, 0.0D);
                tessellator.draw();
            }
            else if (block instanceof BlockDragonEgg)
            {
                this.field_147920_a.field_147845_a = world;
                tessellator = Tessellator.instance;
                tessellator.startDrawingQuads();
                tessellator.setTranslation((double)((float)(-i) - 0.5F), (double)((float)(-j) - 0.5F), (double)((float)(-k) - 0.5F));
                this.field_147920_a.func_147802_a((BlockDragonEgg)block, i, j, k);
                tessellator.setTranslation(0.0D, 0.0D, 0.0D);
                tessellator.draw();
            }
            else
            {
                this.field_147920_a.func_147775_a(block);
                this.field_147920_a.func_147749_a(block, world, i, j, k, p_147918_1_.field_145814_a);
            }

            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glPopMatrix();
        }
    }

    // JAVADOC METHOD $$ func_110775_a
    protected ResourceLocation getEntityTexture(EntityFallingBlock p_147919_1_)
    {
        return TextureMap.locationBlocksTexture;
    }

    // JAVADOC METHOD $$ func_110775_a
    protected ResourceLocation getEntityTexture(Entity par1Entity)
    {
        return this.getEntityTexture((EntityFallingBlock)par1Entity);
    }

    // JAVADOC METHOD $$ func_76986_a
    public void doRender(Entity par1Entity, double par2, double par4, double par6, float par8, float par9)
    {
        this.doRender((EntityFallingBlock)par1Entity, par2, par4, par6, par8, par9);
    }
}