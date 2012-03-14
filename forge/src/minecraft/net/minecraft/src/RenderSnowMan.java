package net.minecraft.src;

import org.lwjgl.opengl.GL11;

public class RenderSnowMan extends RenderLiving
{
    /** A reference to the Snowman model in RenderSnowMan. */
    private ModelSnowMan snowmanModel;

    public RenderSnowMan()
    {
        super(new ModelSnowMan(), 0.5F);
        this.snowmanModel = (ModelSnowMan)super.mainModel;
        this.setRenderPassModel(this.snowmanModel);
    }

    protected void func_40288_a(EntitySnowman par1EntitySnowman, float par2)
    {
        super.renderEquippedItems(par1EntitySnowman, par2);
        ItemStack var3 = new ItemStack(Block.pumpkin, 1);

        if (var3 != null && var3.getItem().shiftedIndex < 256)
        {
            GL11.glPushMatrix();
            this.snowmanModel.field_40305_c.postRender(0.0625F);

            if (RenderBlocks.renderItemIn3d(Block.blocksList[var3.itemID].getRenderType()))
            {
                float var4 = 0.625F;
                GL11.glTranslatef(0.0F, -0.34375F, 0.0F);
                GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
                GL11.glScalef(var4, -var4, var4);
            }

            this.renderManager.itemRenderer.renderItem(par1EntitySnowman, var3, 0);
            GL11.glPopMatrix();
        }
    }

    protected void renderEquippedItems(EntityLiving par1EntityLiving, float par2)
    {
        this.func_40288_a((EntitySnowman)par1EntityLiving, par2);
    }
}
