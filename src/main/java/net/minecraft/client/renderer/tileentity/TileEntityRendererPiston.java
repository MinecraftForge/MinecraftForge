package net.minecraft.client.renderer.tileentity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityPiston;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class TileEntityRendererPiston extends TileEntitySpecialRenderer
{
    private RenderBlocks field_147516_b;
    private static final String __OBFID = "CL_00000969";

    public void func_147500_a(TileEntityPiston p_147515_1_, double p_147515_2_, double p_147515_4_, double p_147515_6_, float p_147515_8_)
    {
        Block block = p_147515_1_.func_145861_a();

        if (block.func_149688_o() != Material.field_151579_a && p_147515_1_.func_145860_a(p_147515_8_) < 1.0F)
        {
            Tessellator tessellator = Tessellator.instance;
            this.func_147499_a(TextureMap.locationBlocksTexture);
            RenderHelper.disableStandardItemLighting();
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glDisable(GL11.GL_CULL_FACE);

            if (Minecraft.isAmbientOcclusionEnabled())
            {
                GL11.glShadeModel(GL11.GL_SMOOTH);
            }
            else
            {
                GL11.glShadeModel(GL11.GL_FLAT);
            }

            tessellator.startDrawingQuads();
            tessellator.setTranslation((double)((float)p_147515_2_ - (float)p_147515_1_.field_145851_c + p_147515_1_.func_145865_b(p_147515_8_)), (double)((float)p_147515_4_ - (float)p_147515_1_.field_145848_d + p_147515_1_.func_145862_c(p_147515_8_)), (double)((float)p_147515_6_ - (float)p_147515_1_.field_145849_e + p_147515_1_.func_145859_d(p_147515_8_)));
            tessellator.setColorOpaque_F(1.0F, 1.0F, 1.0F);

            if (block == Blocks.piston_head && p_147515_1_.func_145860_a(p_147515_8_) < 0.5F)
            {
                this.field_147516_b.func_147750_a(block, p_147515_1_.field_145851_c, p_147515_1_.field_145848_d, p_147515_1_.field_145849_e, false);
            }
            else if (p_147515_1_.func_145867_d() && !p_147515_1_.func_145868_b())
            {
                Blocks.piston_head.func_150086_a(((BlockPistonBase)block).func_150073_e());
                this.field_147516_b.func_147750_a(Blocks.piston_head, p_147515_1_.field_145851_c, p_147515_1_.field_145848_d, p_147515_1_.field_145849_e, p_147515_1_.func_145860_a(p_147515_8_) < 0.5F);
                Blocks.piston_head.func_150087_e();
                tessellator.setTranslation((double)((float)p_147515_2_ - (float)p_147515_1_.field_145851_c), (double)((float)p_147515_4_ - (float)p_147515_1_.field_145848_d), (double)((float)p_147515_6_ - (float)p_147515_1_.field_145849_e));
                this.field_147516_b.func_147804_d(block, p_147515_1_.field_145851_c, p_147515_1_.field_145848_d, p_147515_1_.field_145849_e);
            }
            else
            {
                this.field_147516_b.func_147769_a(block, p_147515_1_.field_145851_c, p_147515_1_.field_145848_d, p_147515_1_.field_145849_e);
            }

            tessellator.setTranslation(0.0D, 0.0D, 0.0D);
            tessellator.draw();
            RenderHelper.enableStandardItemLighting();
        }
    }

    public void func_147496_a(World p_147496_1_)
    {
        this.field_147516_b = new RenderBlocks(p_147496_1_);
    }

    public void func_147500_a(TileEntity p_147500_1_, double p_147500_2_, double p_147500_4_, double p_147500_6_, float p_147500_8_)
    {
        this.func_147500_a((TileEntityPiston)p_147500_1_, p_147500_2_, p_147500_4_, p_147500_6_, p_147500_8_);
    }
}