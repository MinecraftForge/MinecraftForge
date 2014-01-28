package net.minecraft.client.renderer.tileentity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureCompass;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.MapData;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class RenderItemFrame extends Render
{
    private static final ResourceLocation mapBackgroundTextures = new ResourceLocation("textures/map/map_background.png");
    private final RenderBlocks field_147916_f = new RenderBlocks();
    private final Minecraft field_147917_g = Minecraft.getMinecraft();
    private IIcon field_94147_f;
    private static final String __OBFID = "CL_00001002";

    public void updateIcons(IIconRegister par1IconRegister)
    {
        this.field_94147_f = par1IconRegister.registerIcon("itemframe_background");
    }

    // JAVADOC METHOD $$ func_76986_a
    public void doRender(EntityItemFrame par1EntityItemFrame, double par2, double par4, double par6, float par8, float par9)
    {
        GL11.glPushMatrix();
        double d3 = par1EntityItemFrame.posX - par2 - 0.5D;
        double d4 = par1EntityItemFrame.posY - par4 - 0.5D;
        double d5 = par1EntityItemFrame.posZ - par6 - 0.5D;
        int i = par1EntityItemFrame.field_146063_b + Direction.offsetX[par1EntityItemFrame.hangingDirection];
        int j = par1EntityItemFrame.field_146064_c;
        int k = par1EntityItemFrame.field_146062_d + Direction.offsetZ[par1EntityItemFrame.hangingDirection];
        GL11.glTranslated((double)i - d3, (double)j - d4, (double)k - d5);

        if (par1EntityItemFrame.getDisplayedItem() != null && par1EntityItemFrame.getDisplayedItem().getItem() == Items.filled_map)
        {
            this.func_147915_b(par1EntityItemFrame);
        }
        else
        {
            this.renderFrameItemAsBlock(par1EntityItemFrame);
        }

        this.func_82402_b(par1EntityItemFrame);
        GL11.glPopMatrix();
        this.func_147914_a(par1EntityItemFrame, par2 + (double)((float)Direction.offsetX[par1EntityItemFrame.hangingDirection] * 0.3F), par4 - 0.25D, par6 + (double)((float)Direction.offsetZ[par1EntityItemFrame.hangingDirection] * 0.3F));
    }

    // JAVADOC METHOD $$ func_110775_a
    protected ResourceLocation getEntityTexture(EntityItemFrame par1EntityItemFrame)
    {
        return null;
    }

    private void func_147915_b(EntityItemFrame p_147915_1_)
    {
        GL11.glPushMatrix();
        GL11.glRotatef(p_147915_1_.rotationYaw, 0.0F, 1.0F, 0.0F);
        this.renderManager.renderEngine.bindTexture(TextureMap.locationBlocksTexture);
        Block block = Blocks.planks;
        float f = 0.0625F;
        float f1 = 1.0F;
        float f2 = f1 / 2.0F;
        GL11.glPushMatrix();
        this.field_147916_f.func_147770_b(0.0D, (double)(0.5F - f2 + 0.0625F), (double)(0.5F - f2 + 0.0625F), (double)f, (double)(0.5F + f2 - 0.0625F), (double)(0.5F + f2 - 0.0625F));
        this.field_147916_f.func_147757_a(this.field_94147_f);
        this.field_147916_f.func_147800_a(block, 0, 1.0F);
        this.field_147916_f.func_147771_a();
        this.field_147916_f.func_147762_c();
        GL11.glPopMatrix();
        this.field_147916_f.func_147757_a(Blocks.planks.func_149691_a(1, 2));
        GL11.glPushMatrix();
        this.field_147916_f.func_147770_b(0.0D, (double)(0.5F - f2), (double)(0.5F - f2), (double)(f + 1.0E-4F), (double)(f + 0.5F - f2), (double)(0.5F + f2));
        this.field_147916_f.func_147800_a(block, 0, 1.0F);
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        this.field_147916_f.func_147770_b(0.0D, (double)(0.5F + f2 - f), (double)(0.5F - f2), (double)(f + 1.0E-4F), (double)(0.5F + f2), (double)(0.5F + f2));
        this.field_147916_f.func_147800_a(block, 0, 1.0F);
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        this.field_147916_f.func_147770_b(0.0D, (double)(0.5F - f2), (double)(0.5F - f2), (double)f, (double)(0.5F + f2), (double)(f + 0.5F - f2));
        this.field_147916_f.func_147800_a(block, 0, 1.0F);
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        this.field_147916_f.func_147770_b(0.0D, (double)(0.5F - f2), (double)(0.5F + f2 - f), (double)f, (double)(0.5F + f2), (double)(0.5F + f2));
        this.field_147916_f.func_147800_a(block, 0, 1.0F);
        GL11.glPopMatrix();
        this.field_147916_f.func_147762_c();
        this.field_147916_f.func_147771_a();
        GL11.glPopMatrix();
    }

    // JAVADOC METHOD $$ func_82403_a
    private void renderFrameItemAsBlock(EntityItemFrame par1EntityItemFrame)
    {
        GL11.glPushMatrix();
        GL11.glRotatef(par1EntityItemFrame.rotationYaw, 0.0F, 1.0F, 0.0F);
        this.renderManager.renderEngine.bindTexture(TextureMap.locationBlocksTexture);
        Block block = Blocks.planks;
        float f = 0.0625F;
        float f1 = 0.75F;
        float f2 = f1 / 2.0F;
        GL11.glPushMatrix();
        this.field_147916_f.func_147770_b(0.0D, (double)(0.5F - f2 + 0.0625F), (double)(0.5F - f2 + 0.0625F), (double)(f * 0.5F), (double)(0.5F + f2 - 0.0625F), (double)(0.5F + f2 - 0.0625F));
        this.field_147916_f.func_147757_a(this.field_94147_f);
        this.field_147916_f.func_147800_a(block, 0, 1.0F);
        this.field_147916_f.func_147771_a();
        this.field_147916_f.func_147762_c();
        GL11.glPopMatrix();
        this.field_147916_f.func_147757_a(Blocks.planks.func_149691_a(1, 2));
        GL11.glPushMatrix();
        this.field_147916_f.func_147770_b(0.0D, (double)(0.5F - f2), (double)(0.5F - f2), (double)(f + 1.0E-4F), (double)(f + 0.5F - f2), (double)(0.5F + f2));
        this.field_147916_f.func_147800_a(block, 0, 1.0F);
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        this.field_147916_f.func_147770_b(0.0D, (double)(0.5F + f2 - f), (double)(0.5F - f2), (double)(f + 1.0E-4F), (double)(0.5F + f2), (double)(0.5F + f2));
        this.field_147916_f.func_147800_a(block, 0, 1.0F);
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        this.field_147916_f.func_147770_b(0.0D, (double)(0.5F - f2), (double)(0.5F - f2), (double)f, (double)(0.5F + f2), (double)(f + 0.5F - f2));
        this.field_147916_f.func_147800_a(block, 0, 1.0F);
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        this.field_147916_f.func_147770_b(0.0D, (double)(0.5F - f2), (double)(0.5F + f2 - f), (double)f, (double)(0.5F + f2), (double)(0.5F + f2));
        this.field_147916_f.func_147800_a(block, 0, 1.0F);
        GL11.glPopMatrix();
        this.field_147916_f.func_147762_c();
        this.field_147916_f.func_147771_a();
        GL11.glPopMatrix();
    }

    private void func_82402_b(EntityItemFrame par1EntityItemFrame)
    {
        ItemStack itemstack = par1EntityItemFrame.getDisplayedItem();

        if (itemstack != null)
        {
            EntityItem entityitem = new EntityItem(par1EntityItemFrame.worldObj, 0.0D, 0.0D, 0.0D, itemstack);
            Item item = entityitem.getEntityItem().getItem();
            entityitem.getEntityItem().stackSize = 1;
            entityitem.hoverStart = 0.0F;
            GL11.glPushMatrix();
            GL11.glTranslatef(-0.453125F * (float)Direction.offsetX[par1EntityItemFrame.hangingDirection], -0.18F, -0.453125F * (float)Direction.offsetZ[par1EntityItemFrame.hangingDirection]);
            GL11.glRotatef(180.0F + par1EntityItemFrame.rotationYaw, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef((float)(-90 * par1EntityItemFrame.getRotation()), 0.0F, 0.0F, 1.0F);

            switch (par1EntityItemFrame.getRotation())
            {
                case 1:
                    GL11.glTranslatef(-0.16F, -0.16F, 0.0F);
                    break;
                case 2:
                    GL11.glTranslatef(0.0F, -0.32F, 0.0F);
                    break;
                case 3:
                    GL11.glTranslatef(0.16F, -0.16F, 0.0F);
            }

            if (item == Items.filled_map)
            {
                this.renderManager.renderEngine.bindTexture(mapBackgroundTextures);
                Tessellator tessellator = Tessellator.instance;
                GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
                float f = 0.0078125F;
                GL11.glScalef(f, f, f);

                switch (par1EntityItemFrame.getRotation())
                {
                    case 0:
                        GL11.glTranslatef(-64.0F, -87.0F, -1.5F);
                        break;
                    case 1:
                        GL11.glTranslatef(-66.5F, -84.5F, -1.5F);
                        break;
                    case 2:
                        GL11.glTranslatef(-64.0F, -82.0F, -1.5F);
                        break;
                    case 3:
                        GL11.glTranslatef(-61.5F, -84.5F, -1.5F);
                }

                GL11.glNormal3f(0.0F, 0.0F, -1.0F);
                MapData mapdata = Items.filled_map.getMapData(entityitem.getEntityItem(), par1EntityItemFrame.worldObj);
                GL11.glTranslatef(0.0F, 0.0F, -1.0F);

                if (mapdata != null)
                {
                    this.field_147917_g.entityRenderer.func_147701_i().func_148250_a(mapdata, true);
                }
            }
            else
            {
                if (item == Items.compass)
                {
                    TextureManager texturemanager = Minecraft.getMinecraft().getTextureManager();
                    texturemanager.bindTexture(TextureMap.locationItemsTexture);
                    TextureAtlasSprite textureatlassprite1 = ((TextureMap)texturemanager.getTexture(TextureMap.locationItemsTexture)).getAtlasSprite(Items.compass.getIconIndex(entityitem.getEntityItem()).getIconName());

                    if (textureatlassprite1 instanceof TextureCompass)
                    {
                        TextureCompass texturecompass = (TextureCompass)textureatlassprite1;
                        double d0 = texturecompass.currentAngle;
                        double d1 = texturecompass.angleDelta;
                        texturecompass.currentAngle = 0.0D;
                        texturecompass.angleDelta = 0.0D;
                        texturecompass.updateCompass(par1EntityItemFrame.worldObj, par1EntityItemFrame.posX, par1EntityItemFrame.posZ, (double)MathHelper.wrapAngleTo180_float((float)(180 + par1EntityItemFrame.hangingDirection * 90)), false, true);
                        texturecompass.currentAngle = d0;
                        texturecompass.angleDelta = d1;
                    }
                }

                RenderItem.renderInFrame = true;
                RenderManager.instance.func_147940_a(entityitem, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F);
                RenderItem.renderInFrame = false;

                if (item == Items.compass)
                {
                    TextureAtlasSprite textureatlassprite = ((TextureMap)Minecraft.getMinecraft().getTextureManager().getTexture(TextureMap.locationItemsTexture)).getAtlasSprite(Items.compass.getIconIndex(entityitem.getEntityItem()).getIconName());

                    if (textureatlassprite.getFrameCount() > 0)
                    {
                        textureatlassprite.updateAnimation();
                    }
                }
            }

            GL11.glPopMatrix();
        }
    }

    protected void func_147914_a(EntityItemFrame p_147914_1_, double p_147914_2_, double p_147914_4_, double p_147914_6_)
    {
        if (Minecraft.isGuiEnabled() && p_147914_1_.getDisplayedItem() != null && p_147914_1_.getDisplayedItem().hasDisplayName() && this.renderManager.field_147941_i == p_147914_1_)
        {
            float f = 1.6F;
            float f1 = 0.016666668F * f;
            double d3 = p_147914_1_.getDistanceSqToEntity(this.renderManager.livingPlayer);
            float f2 = p_147914_1_.isSneaking() ? 32.0F : 64.0F;

            if (d3 < (double)(f2 * f2))
            {
                String s = p_147914_1_.getDisplayedItem().getDisplayName();

                if (p_147914_1_.isSneaking())
                {
                    FontRenderer fontrenderer = this.getFontRendererFromRenderManager();
                    GL11.glPushMatrix();
                    GL11.glTranslatef((float)p_147914_2_ + 0.0F, (float)p_147914_4_ + p_147914_1_.height + 0.5F, (float)p_147914_6_);
                    GL11.glNormal3f(0.0F, 1.0F, 0.0F);
                    GL11.glRotatef(-this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
                    GL11.glRotatef(this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
                    GL11.glScalef(-f1, -f1, f1);
                    GL11.glDisable(GL11.GL_LIGHTING);
                    GL11.glTranslatef(0.0F, 0.25F / f1, 0.0F);
                    GL11.glDepthMask(false);
                    GL11.glEnable(GL11.GL_BLEND);
                    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                    Tessellator tessellator = Tessellator.instance;
                    GL11.glDisable(GL11.GL_TEXTURE_2D);
                    tessellator.startDrawingQuads();
                    int i = fontrenderer.getStringWidth(s) / 2;
                    tessellator.setColorRGBA_F(0.0F, 0.0F, 0.0F, 0.25F);
                    tessellator.addVertex((double)(-i - 1), -1.0D, 0.0D);
                    tessellator.addVertex((double)(-i - 1), 8.0D, 0.0D);
                    tessellator.addVertex((double)(i + 1), 8.0D, 0.0D);
                    tessellator.addVertex((double)(i + 1), -1.0D, 0.0D);
                    tessellator.draw();
                    GL11.glEnable(GL11.GL_TEXTURE_2D);
                    GL11.glDepthMask(true);
                    fontrenderer.drawString(s, -fontrenderer.getStringWidth(s) / 2, 0, 553648127);
                    GL11.glEnable(GL11.GL_LIGHTING);
                    GL11.glDisable(GL11.GL_BLEND);
                    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                    GL11.glPopMatrix();
                }
                else
                {
                    this.func_147906_a(p_147914_1_, s, p_147914_2_, p_147914_4_, p_147914_6_, 64);
                }
            }
        }
    }

    // JAVADOC METHOD $$ func_110775_a
    protected ResourceLocation getEntityTexture(Entity par1Entity)
    {
        return this.getEntityTexture((EntityItemFrame)par1Entity);
    }

    // JAVADOC METHOD $$ func_76986_a
    public void doRender(Entity par1Entity, double par2, double par4, double par6, float par8, float par9)
    {
        this.doRender((EntityItemFrame)par1Entity, par2, par4, par6, par8, par9);
    }
}