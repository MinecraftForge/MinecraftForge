package net.minecraft.src;

import org.lwjgl.opengl.GL11;

public class EntityPickupFX extends EntityFX
{
    private Entity field_675_a;
    private Entity field_679_o;
    private int age = 0;
    private int maxAge = 0;
    private float field_676_r;

    public EntityPickupFX(World par1World, Entity par2Entity, Entity par3Entity, float par4)
    {
        super(par1World, par2Entity.posX, par2Entity.posY, par2Entity.posZ, par2Entity.motionX, par2Entity.motionY, par2Entity.motionZ);
        this.field_675_a = par2Entity;
        this.field_679_o = par3Entity;
        this.maxAge = 3;
        this.field_676_r = par4;
    }

    public void renderParticle(Tessellator par1Tessellator, float par2, float par3, float par4, float par5, float par6, float par7)
    {
        float var8 = ((float)this.age + par2) / (float)this.maxAge;
        var8 *= var8;
        double var9 = this.field_675_a.posX;
        double var11 = this.field_675_a.posY;
        double var13 = this.field_675_a.posZ;
        double var15 = this.field_679_o.lastTickPosX + (this.field_679_o.posX - this.field_679_o.lastTickPosX) * (double)par2;
        double var17 = this.field_679_o.lastTickPosY + (this.field_679_o.posY - this.field_679_o.lastTickPosY) * (double)par2 + (double)this.field_676_r;
        double var19 = this.field_679_o.lastTickPosZ + (this.field_679_o.posZ - this.field_679_o.lastTickPosZ) * (double)par2;
        double var21 = var9 + (var15 - var9) * (double)var8;
        double var23 = var11 + (var17 - var11) * (double)var8;
        double var25 = var13 + (var19 - var13) * (double)var8;
        int var27 = MathHelper.floor_double(var21);
        int var28 = MathHelper.floor_double(var23 + (double)(this.yOffset / 2.0F));
        int var29 = MathHelper.floor_double(var25);
        int var30 = this.getEntityBrightnessForRender(par2);
        int var31 = var30 % 65536;
        int var32 = var30 / 65536;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)var31 / 1.0F, (float)var32 / 1.0F);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        var21 -= interpPosX;
        var23 -= interpPosY;
        var25 -= interpPosZ;
        RenderManager.instance.renderEntityWithPosYaw(this.field_675_a, (double)((float)var21), (double)((float)var23), (double)((float)var25), this.field_675_a.rotationYaw, par2);
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate()
    {
        ++this.age;

        if (this.age == this.maxAge)
        {
            this.setEntityDead();
        }
    }

    public int getFXLayer()
    {
        return 3;
    }
}
