package net.minecraft.src;

import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11;
import net.minecraft.src.forge.IArmorTextureProvider;

public class RenderPlayer extends RenderLiving
{
    private ModelBiped modelBipedMain;
    private ModelBiped modelArmorChestplate;
    private ModelBiped modelArmor;
    private static final String[] armorFilenamePrefix = new String[] {"cloth", "chain", "iron", "diamond", "gold"};

    public RenderPlayer()
    {
        super(new ModelBiped(0.0F), 0.5F);
        this.modelBipedMain = (ModelBiped)this.mainModel;
        this.modelArmorChestplate = new ModelBiped(1.0F);
        this.modelArmor = new ModelBiped(0.5F);
    }

    /**
     * Set the specified armor model as the player model. Args: player, armorSlot, partialTick
     */
    protected int setArmorModel(EntityPlayer par1EntityPlayer, int par2, float par3)
    {
        ItemStack var4 = par1EntityPlayer.inventory.armorItemInSlot(3 - par2);

        if (var4 != null)
        {
            Item var5 = var4.getItem();

            if (var5 instanceof ItemArmor)
            {
                ItemArmor var6 = (ItemArmor)var5;
                if (var5 instanceof IArmorTextureProvider)
                {
                    loadTexture(((IArmorTextureProvider)var5).getArmorTextureFile(var4));
                }
                else
                {
                    this.loadTexture("/armor/" + armorFilenamePrefix[var6.renderIndex] + "_" + (par2 == 2 ? 2 : 1) + ".png");
                }
                ModelBiped var7 = par2 == 2 ? this.modelArmor : this.modelArmorChestplate;
                var7.bipedHead.showModel = par2 == 0;
                var7.bipedHeadwear.showModel = par2 == 0;
                var7.bipedBody.showModel = par2 == 1 || par2 == 2;
                var7.bipedRightArm.showModel = par2 == 1;
                var7.bipedLeftArm.showModel = par2 == 1;
                var7.bipedRightLeg.showModel = par2 == 2 || par2 == 3;
                var7.bipedLeftLeg.showModel = par2 == 2 || par2 == 3;
                this.setRenderPassModel(var7);

                if (var4.isItemEnchanted())
                {
                    return 15;
                }

                return 1;
            }
        }

        return -1;
    }

    public void renderPlayer(EntityPlayer par1EntityPlayer, double par2, double par4, double par6, float par8, float par9)
    {
        ItemStack var10 = par1EntityPlayer.inventory.getCurrentItem();
        this.modelArmorChestplate.heldItemRight = this.modelArmor.heldItemRight = this.modelBipedMain.heldItemRight = var10 != null ? 1 : 0;

        if (var10 != null && par1EntityPlayer.getItemInUseCount() > 0)
        {
            EnumAction var11 = var10.getItemUseAction();

            if (var11 == EnumAction.block)
            {
                this.modelArmorChestplate.heldItemRight = this.modelArmor.heldItemRight = this.modelBipedMain.heldItemRight = 3;
            }
            else if (var11 == EnumAction.bow)
            {
                this.modelArmorChestplate.aimedBow = this.modelArmor.aimedBow = this.modelBipedMain.aimedBow = true;
            }
        }

        this.modelArmorChestplate.isSneak = this.modelArmor.isSneak = this.modelBipedMain.isSneak = par1EntityPlayer.isSneaking();
        double var13 = par4 - (double)par1EntityPlayer.yOffset;

        if (par1EntityPlayer.isSneaking() && !(par1EntityPlayer instanceof EntityPlayerSP))
        {
            var13 -= 0.125D;
        }

        super.doRenderLiving(par1EntityPlayer, par2, var13, par6, par8, par9);
        this.modelArmorChestplate.aimedBow = this.modelArmor.aimedBow = this.modelBipedMain.aimedBow = false;
        this.modelArmorChestplate.isSneak = this.modelArmor.isSneak = this.modelBipedMain.isSneak = false;
        this.modelArmorChestplate.heldItemRight = this.modelArmor.heldItemRight = this.modelBipedMain.heldItemRight = 0;
    }

    /**
     * Used to render a player's name above their head
     */
    protected void renderName(EntityPlayer par1EntityPlayer, double par2, double par4, double par6)
    {
        if (Minecraft.isGuiEnabled() && par1EntityPlayer != this.renderManager.livingPlayer)
        {
            float var8 = 1.6F;
            float var9 = 0.016666668F * var8;
            float var10 = par1EntityPlayer.getDistanceToEntity(this.renderManager.livingPlayer);
            float var11 = par1EntityPlayer.isSneaking() ? 32.0F : 64.0F;

            if (var10 < var11)
            {
                String var12 = par1EntityPlayer.username;

                if (!par1EntityPlayer.isSneaking())
                {
                    if (par1EntityPlayer.isPlayerSleeping())
                    {
                        this.renderLivingLabel(par1EntityPlayer, var12, par2, par4 - 1.5D, par6, 64);
                    }
                    else
                    {
                        this.renderLivingLabel(par1EntityPlayer, var12, par2, par4, par6, 64);
                    }
                }
                else
                {
                    FontRenderer var13 = this.getFontRendererFromRenderManager();
                    GL11.glPushMatrix();
                    GL11.glTranslatef((float)par2 + 0.0F, (float)par4 + 2.3F, (float)par6);
                    GL11.glNormal3f(0.0F, 1.0F, 0.0F);
                    GL11.glRotatef(-this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
                    GL11.glRotatef(this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
                    GL11.glScalef(-var9, -var9, var9);
                    GL11.glDisable(GL11.GL_LIGHTING);
                    GL11.glTranslatef(0.0F, 0.25F / var9, 0.0F);
                    GL11.glDepthMask(false);
                    GL11.glEnable(GL11.GL_BLEND);
                    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                    Tessellator var14 = Tessellator.instance;
                    GL11.glDisable(GL11.GL_TEXTURE_2D);
                    var14.startDrawingQuads();
                    int var15 = var13.getStringWidth(var12) / 2;
                    var14.setColorRGBA_F(0.0F, 0.0F, 0.0F, 0.25F);
                    var14.addVertex((double)(-var15 - 1), -1.0D, 0.0D);
                    var14.addVertex((double)(-var15 - 1), 8.0D, 0.0D);
                    var14.addVertex((double)(var15 + 1), 8.0D, 0.0D);
                    var14.addVertex((double)(var15 + 1), -1.0D, 0.0D);
                    var14.draw();
                    GL11.glEnable(GL11.GL_TEXTURE_2D);
                    GL11.glDepthMask(true);
                    var13.drawString(var12, -var13.getStringWidth(var12) / 2, 0, 553648127);
                    GL11.glEnable(GL11.GL_LIGHTING);
                    GL11.glDisable(GL11.GL_BLEND);
                    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                    GL11.glPopMatrix();
                }
            }
        }
    }

    /**
     * Method for adding special render rules
     */
    protected void renderSpecials(EntityPlayer par1EntityPlayer, float par2)
    {
        super.renderEquippedItems(par1EntityPlayer, par2);
        ItemStack var3 = par1EntityPlayer.inventory.armorItemInSlot(3);

        if (var3 != null && var3.getItem().shiftedIndex < 256)
        {
            GL11.glPushMatrix();
            this.modelBipedMain.bipedHead.postRender(0.0625F);

            if (RenderBlocks.renderItemIn3d(Block.blocksList[var3.itemID].getRenderType()))
            {
                float var4 = 0.625F;
                GL11.glTranslatef(0.0F, -0.25F, 0.0F);
                GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
                GL11.glScalef(var4, -var4, var4);
            }

            this.renderManager.itemRenderer.renderItem(par1EntityPlayer, var3, 0);
            GL11.glPopMatrix();
        }

        float var6;

        if (par1EntityPlayer.username.equals("deadmau5") && this.loadDownloadableImageTexture(par1EntityPlayer.skinUrl, (String)null))
        {
            for (int var19 = 0; var19 < 2; ++var19)
            {
                float var5 = par1EntityPlayer.prevRotationYaw + (par1EntityPlayer.rotationYaw - par1EntityPlayer.prevRotationYaw) * par2 - (par1EntityPlayer.prevRenderYawOffset + (par1EntityPlayer.renderYawOffset - par1EntityPlayer.prevRenderYawOffset) * par2);
                var6 = par1EntityPlayer.prevRotationPitch + (par1EntityPlayer.rotationPitch - par1EntityPlayer.prevRotationPitch) * par2;
                GL11.glPushMatrix();
                GL11.glRotatef(var5, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(var6, 1.0F, 0.0F, 0.0F);
                GL11.glTranslatef(0.375F * (float)(var19 * 2 - 1), 0.0F, 0.0F);
                GL11.glTranslatef(0.0F, -0.375F, 0.0F);
                GL11.glRotatef(-var6, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(-var5, 0.0F, 1.0F, 0.0F);
                float var7 = 1.3333334F;
                GL11.glScalef(var7, var7, var7);
                this.modelBipedMain.renderEars(0.0625F);
                GL11.glPopMatrix();
            }
        }

        float var10;

        if (this.loadDownloadableImageTexture(par1EntityPlayer.playerCloakUrl, (String)null))
        {
            GL11.glPushMatrix();
            GL11.glTranslatef(0.0F, 0.0F, 0.125F);
            double var22 = par1EntityPlayer.field_20066_r + (par1EntityPlayer.field_20063_u - par1EntityPlayer.field_20066_r) * (double)par2 - (par1EntityPlayer.prevPosX + (par1EntityPlayer.posX - par1EntityPlayer.prevPosX) * (double)par2);
            double var23 = par1EntityPlayer.field_20065_s + (par1EntityPlayer.field_20062_v - par1EntityPlayer.field_20065_s) * (double)par2 - (par1EntityPlayer.prevPosY + (par1EntityPlayer.posY - par1EntityPlayer.prevPosY) * (double)par2);
            double var8 = par1EntityPlayer.field_20064_t + (par1EntityPlayer.field_20061_w - par1EntityPlayer.field_20064_t) * (double)par2 - (par1EntityPlayer.prevPosZ + (par1EntityPlayer.posZ - par1EntityPlayer.prevPosZ) * (double)par2);
            var10 = par1EntityPlayer.prevRenderYawOffset + (par1EntityPlayer.renderYawOffset - par1EntityPlayer.prevRenderYawOffset) * par2;
            double var11 = (double)MathHelper.sin(var10 * (float)Math.PI / 180.0F);
            double var13 = (double)(-MathHelper.cos(var10 * (float)Math.PI / 180.0F));
            float var15 = (float)var23 * 10.0F;

            if (var15 < -6.0F)
            {
                var15 = -6.0F;
            }

            if (var15 > 32.0F)
            {
                var15 = 32.0F;
            }

            float var16 = (float)(var22 * var11 + var8 * var13) * 100.0F;
            float var17 = (float)(var22 * var13 - var8 * var11) * 100.0F;

            if (var16 < 0.0F)
            {
                var16 = 0.0F;
            }

            float var18 = par1EntityPlayer.prevCameraYaw + (par1EntityPlayer.cameraYaw - par1EntityPlayer.prevCameraYaw) * par2;
            var15 += MathHelper.sin((par1EntityPlayer.prevDistanceWalkedModified + (par1EntityPlayer.distanceWalkedModified - par1EntityPlayer.prevDistanceWalkedModified) * par2) * 6.0F) * 32.0F * var18;

            if (par1EntityPlayer.isSneaking())
            {
                var15 += 25.0F;
            }

            GL11.glRotatef(6.0F + var16 / 2.0F + var15, 1.0F, 0.0F, 0.0F);
            GL11.glRotatef(var17 / 2.0F, 0.0F, 0.0F, 1.0F);
            GL11.glRotatef(-var17 / 2.0F, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
            this.modelBipedMain.renderCloak(0.0625F);
            GL11.glPopMatrix();
        }

        ItemStack var21 = par1EntityPlayer.inventory.getCurrentItem();

        if (var21 != null)
        {
            GL11.glPushMatrix();
            this.modelBipedMain.bipedRightArm.postRender(0.0625F);
            GL11.glTranslatef(-0.0625F, 0.4375F, 0.0625F);

            if (par1EntityPlayer.fishEntity != null)
            {
                var21 = new ItemStack(Item.stick);
            }

            EnumAction var20 = null;

            if (par1EntityPlayer.getItemInUseCount() > 0)
            {
                var20 = var21.getItemUseAction();
            }

            if (var21.itemID < 256 && RenderBlocks.renderItemIn3d(Block.blocksList[var21.itemID].getRenderType()))
            {
                var6 = 0.5F;
                GL11.glTranslatef(0.0F, 0.1875F, -0.3125F);
                var6 *= 0.75F;
                GL11.glRotatef(20.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
                GL11.glScalef(var6, -var6, var6);
            }
            else if (var21.itemID == Item.bow.shiftedIndex)
            {
                var6 = 0.625F;
                GL11.glTranslatef(0.0F, 0.125F, 0.3125F);
                GL11.glRotatef(-20.0F, 0.0F, 1.0F, 0.0F);
                GL11.glScalef(var6, -var6, var6);
                GL11.glRotatef(-100.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
            }
            else if (Item.itemsList[var21.itemID].isFull3D())
            {
                var6 = 0.625F;

                if (Item.itemsList[var21.itemID].shouldRotateAroundWhenRendering())
                {
                    GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
                    GL11.glTranslatef(0.0F, -0.125F, 0.0F);
                }

                if (par1EntityPlayer.getItemInUseCount() > 0 && var20 == EnumAction.block)
                {
                    GL11.glTranslatef(0.05F, 0.0F, -0.1F);
                    GL11.glRotatef(-50.0F, 0.0F, 1.0F, 0.0F);
                    GL11.glRotatef(-10.0F, 1.0F, 0.0F, 0.0F);
                    GL11.glRotatef(-60.0F, 0.0F, 0.0F, 1.0F);
                }

                GL11.glTranslatef(0.0F, 0.1875F, 0.0F);
                GL11.glScalef(var6, -var6, var6);
                GL11.glRotatef(-100.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
            }
            else
            {
                var6 = 0.375F;
                GL11.glTranslatef(0.25F, 0.1875F, -0.1875F);
                GL11.glScalef(var6, var6, var6);
                GL11.glRotatef(60.0F, 0.0F, 0.0F, 1.0F);
                GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(20.0F, 0.0F, 0.0F, 1.0F);
            }

            if (var21.getItem().func_46058_c())
            {
                for (int var25 = 0; var25 <= 1; ++var25)
                {
                    int var24 = var21.getItem().getColorFromDamage(var21.getItemDamage(), var25);
                    float var26 = (float)(var24 >> 16 & 255) / 255.0F;
                    float var9 = (float)(var24 >> 8 & 255) / 255.0F;
                    var10 = (float)(var24 & 255) / 255.0F;
                    GL11.glColor4f(var26, var9, var10, 1.0F);
                    this.renderManager.itemRenderer.renderItem(par1EntityPlayer, var21, var25);
                }
            }
            else
            {
                this.renderManager.itemRenderer.renderItem(par1EntityPlayer, var21, 0);
            }

            GL11.glPopMatrix();
        }
    }

    protected void renderPlayerScale(EntityPlayer par1EntityPlayer, float par2)
    {
        float var3 = 0.9375F;
        GL11.glScalef(var3, var3, var3);
    }

    public void drawFirstPersonHand()
    {
        this.modelBipedMain.onGround = 0.0F;
        this.modelBipedMain.setRotationAngles(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
        this.modelBipedMain.bipedRightArm.render(0.0625F);
    }

    /**
     * Renders player with sleeping offset if sleeping
     */
    protected void renderPlayerSleep(EntityPlayer par1EntityPlayer, double par2, double par4, double par6)
    {
        if (par1EntityPlayer.isEntityAlive() && par1EntityPlayer.isPlayerSleeping())
        {
            super.renderLivingAt(par1EntityPlayer, par2 + (double)par1EntityPlayer.field_22063_x, par4 + (double)par1EntityPlayer.field_22062_y, par6 + (double)par1EntityPlayer.field_22061_z);
        }
        else
        {
            super.renderLivingAt(par1EntityPlayer, par2, par4, par6);
        }
    }

    /**
     * Rotates the player if the player is sleeping. This method is called in rotateCorpse.
     */
    protected void rotatePlayer(EntityPlayer par1EntityPlayer, float par2, float par3, float par4)
    {
        if (par1EntityPlayer.isEntityAlive() && par1EntityPlayer.isPlayerSleeping())
        {
            GL11.glRotatef(par1EntityPlayer.getBedOrientationInDegrees(), 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(this.getDeathMaxRotation(par1EntityPlayer), 0.0F, 0.0F, 1.0F);
            GL11.glRotatef(270.0F, 0.0F, 1.0F, 0.0F);
        }
        else
        {
            super.rotateCorpse(par1EntityPlayer, par2, par3, par4);
        }
    }

    /**
     * Passes the specialRender and renders it
     */
    protected void passSpecialRender(EntityLiving par1EntityLiving, double par2, double par4, double par6)
    {
        this.renderName((EntityPlayer)par1EntityLiving, par2, par4, par6);
    }

    /**
     * Allows the render to do any OpenGL state modifications necessary before the model is rendered. Args:
     * entityLiving, partialTickTime
     */
    protected void preRenderCallback(EntityLiving par1EntityLiving, float par2)
    {
        this.renderPlayerScale((EntityPlayer)par1EntityLiving, par2);
    }

    /**
     * Queries whether should render the specified pass or not.
     */
    protected int shouldRenderPass(EntityLiving par1EntityLiving, int par2, float par3)
    {
        return this.setArmorModel((EntityPlayer)par1EntityLiving, par2, par3);
    }

    protected void renderEquippedItems(EntityLiving par1EntityLiving, float par2)
    {
        this.renderSpecials((EntityPlayer)par1EntityLiving, par2);
    }

    protected void rotateCorpse(EntityLiving par1EntityLiving, float par2, float par3, float par4)
    {
        this.rotatePlayer((EntityPlayer)par1EntityLiving, par2, par3, par4);
    }

    /**
     * Sets a simple glTranslate on a LivingEntity.
     */
    protected void renderLivingAt(EntityLiving par1EntityLiving, double par2, double par4, double par6)
    {
        this.renderPlayerSleep((EntityPlayer)par1EntityLiving, par2, par4, par6);
    }

    public void doRenderLiving(EntityLiving par1EntityLiving, double par2, double par4, double par6, float par8, float par9)
    {
        this.renderPlayer((EntityPlayer)par1EntityLiving, par2, par4, par6, par8, par9);
    }

    /**
     * Actually renders the given argument. This is a synthetic bridge method, always casting down its argument and then
     * handing it off to a worker function which does the actual work. In all probabilty, the class Render is generic
     * (Render<T extends Entity) and this method has signature public void doRender(T entity, double d, double d1,
     * double d2, float f, float f1). But JAD is pre 1.5 so doesn't do that.
     */
    public void doRender(Entity par1Entity, double par2, double par4, double par6, float par8, float par9)
    {
        this.renderPlayer((EntityPlayer)par1Entity, par2, par4, par6, par8, par9);
    }
}
