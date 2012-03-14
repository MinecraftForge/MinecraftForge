package net.minecraft.src;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.lwjgl.opengl.GL11;

import net.minecraft.src.forge.*;

public class EffectRenderer
{
    /** Reference to the World object. */
    protected World worldObj;
    private List[] fxLayers = new List[4];
    private RenderEngine renderer;

    /** RNG. */
    private Random rand = new Random();

    private List effectList = new ArrayList();
    
    public EffectRenderer(World par1World, RenderEngine par2RenderEngine)
    {
        if (par1World != null)
        {
            this.worldObj = par1World;
        }

        this.renderer = par2RenderEngine;

        for (int var3 = 0; var3 < 4; ++var3)
        {
            this.fxLayers[var3] = new ArrayList();
        }
    }

    public void addEffect(EntityFX par1EntityFX)
    {
        int var2 = par1EntityFX.getFXLayer();

        if (this.fxLayers[var2].size() >= 4000)
        {
            this.fxLayers[var2].remove(0);
        }

        this.fxLayers[var2].add(par1EntityFX);
    }

    public void updateEffects()
    {
        for (int var1 = 0; var1 < 4; ++var1)
        {
            for (int var2 = 0; var2 < this.fxLayers[var1].size(); ++var2)
            {
                EntityFX var3 = (EntityFX)this.fxLayers[var1].get(var2);
                var3.onUpdate();

                if (var3.isDead)
                {
                    this.fxLayers[var1].remove(var2--);
                }
            }
        }
        
        for (int x = 0; x < effectList.size(); x++) 
        {
            BlockTextureParticles entry = (BlockTextureParticles)effectList.get(x);
            for (int y = 0; y < entry.effects.size(); y++) 
            {
                EntityFX entityfx = (EntityFX)entry.effects.get(y);
                if (entityfx.isDead) 
                {
                    entry.effects.remove(y--);
                }
            }
            if (effectList.size() == 0)
            {
                effectList.remove(x--);
            }
        }
    }

    /**
     * Renders all current particles. Args player, partialTickTime
     */
    public void renderParticles(Entity par1Entity, float par2)
    {
        float var3 = ActiveRenderInfo.rotationX;
        float var4 = ActiveRenderInfo.rotationZ;
        float var5 = ActiveRenderInfo.rotationYZ;
        float var6 = ActiveRenderInfo.rotationXY;
        float var7 = ActiveRenderInfo.rotationXZ;
        EntityFX.interpPosX = par1Entity.lastTickPosX + (par1Entity.posX - par1Entity.lastTickPosX) * (double)par2;
        EntityFX.interpPosY = par1Entity.lastTickPosY + (par1Entity.posY - par1Entity.lastTickPosY) * (double)par2;
        EntityFX.interpPosZ = par1Entity.lastTickPosZ + (par1Entity.posZ - par1Entity.lastTickPosZ) * (double)par2;

        for (int var8 = 0; var8 < 3; ++var8)
        {
            if (this.fxLayers[var8].size() != 0)
            {
                int var9 = 0;

                if (var8 == 0)
                {
                    var9 = this.renderer.getTexture("/particles.png");
                }

                if (var8 == 1)
                {
                    var9 = this.renderer.getTexture("/terrain.png");
                }

                if (var8 == 2)
                {
                    var9 = this.renderer.getTexture("/gui/items.png");
                }

                GL11.glBindTexture(GL11.GL_TEXTURE_2D, var9);
                Tessellator var10 = Tessellator.instance;
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                var10.startDrawingQuads();

                for (int var11 = 0; var11 < this.fxLayers[var8].size(); ++var11)
                {
                    EntityFX var12 = (EntityFX)this.fxLayers[var8].get(var11);
                    if (var12 instanceof EntityDiggingFX)
                    {
                        continue;
                    }
                    var10.setBrightness(var12.getEntityBrightnessForRender(par2));
                    var12.renderParticle(var10, par2, var3, var7, var4, var5, var6);
                }

                var10.draw();
            }
            Tessellator tessallator = Tessellator.instance;
            for (int x = 0; x < effectList.size(); x++)
            {
                BlockTextureParticles entry = (BlockTextureParticles)effectList.get(x);
                GL11.glBindTexture(GL11.GL_TEXTURE_2D, renderer.getTexture(entry.texture));
                tessallator.startDrawingQuads();
                for (int y = 0; y < entry.effects.size(); y++)
                {
                    EntityFX entryfx = (EntityFX)entry.effects.get(y);
                    tessallator.setBrightness(entryfx.getEntityBrightnessForRender(par2));
                    entryfx.renderParticle(tessallator, par2, var3, var7, var4, var5, var6);
                }
                tessallator.draw();
            }
        }
    }

    public void func_1187_b(Entity par1Entity, float par2)
    {
        float var3 = MathHelper.cos(par1Entity.rotationYaw * (float)Math.PI / 180.0F);
        float var4 = MathHelper.sin(par1Entity.rotationYaw * (float)Math.PI / 180.0F);
        float var5 = -var4 * MathHelper.sin(par1Entity.rotationPitch * (float)Math.PI / 180.0F);
        float var6 = var3 * MathHelper.sin(par1Entity.rotationPitch * (float)Math.PI / 180.0F);
        float var7 = MathHelper.cos(par1Entity.rotationPitch * (float)Math.PI / 180.0F);
        byte var8 = 3;

        if (this.fxLayers[var8].size() != 0)
        {
            Tessellator var9 = Tessellator.instance;

            for (int var10 = 0; var10 < this.fxLayers[var8].size(); ++var10)
            {
                EntityFX var11 = (EntityFX)this.fxLayers[var8].get(var10);
                var9.setBrightness(var11.getEntityBrightnessForRender(par2));
                var11.renderParticle(var9, par2, var3, var7, var4, var5, var6);
            }
        }
    }

    public void clearEffects(World par1World)
    {
        this.worldObj = par1World;

        for (int var2 = 0; var2 < 4; ++var2)
        {
            this.fxLayers[var2].clear();
        }
        
        for (int x = 0; x < effectList.size(); x++) 
        {
            BlockTextureParticles entry = (BlockTextureParticles)effectList.get(x);
            entry.effects.clear();
        }
        effectList.clear();
    }

    public void addBlockDestroyEffects(int par1, int par2, int par3, int par4, int par5)
    {
        if (par4 != 0)
        {
            Block var6 = Block.blocksList[par4];
            byte var7 = 4;

            for (int var8 = 0; var8 < var7; ++var8)
            {
                for (int var9 = 0; var9 < var7; ++var9)
                {
                    for (int var10 = 0; var10 < var7; ++var10)
                    {
                        double var11 = (double)par1 + ((double)var8 + 0.5D) / (double)var7;
                        double var13 = (double)par2 + ((double)var9 + 0.5D) / (double)var7;
                        double var15 = (double)par3 + ((double)var10 + 0.5D) / (double)var7;
                        int var17 = this.rand.nextInt(6);
                        addDigParticleEffect((EntityDiggingFX)(new EntityDiggingFX(this.worldObj, var11, var13, var15, var11 - (double)par1 - 0.5D, var13 - (double)par2 - 0.5D, var15 - (double)par3 - 0.5D, var6, var17, par5)).func_4041_a(par1, par2, par3), var6);
                    }
                }
            }
        }
    }

    /**
     * Adds block hit particles for the specified block. Args: x, y, z, sideHit
     */
    public void addBlockHitEffects(int par1, int par2, int par3, int par4)
    {
        int var5 = this.worldObj.getBlockId(par1, par2, par3);

        if (var5 != 0)
        {
            Block var6 = Block.blocksList[var5];
            float var7 = 0.1F;
            double var8 = (double)par1 + this.rand.nextDouble() * (var6.maxX - var6.minX - (double)(var7 * 2.0F)) + (double)var7 + var6.minX;
            double var10 = (double)par2 + this.rand.nextDouble() * (var6.maxY - var6.minY - (double)(var7 * 2.0F)) + (double)var7 + var6.minY;
            double var12 = (double)par3 + this.rand.nextDouble() * (var6.maxZ - var6.minZ - (double)(var7 * 2.0F)) + (double)var7 + var6.minZ;

            if (par4 == 0)
            {
                var10 = (double)par2 + var6.minY - (double)var7;
            }

            if (par4 == 1)
            {
                var10 = (double)par2 + var6.maxY + (double)var7;
            }

            if (par4 == 2)
            {
                var12 = (double)par3 + var6.minZ - (double)var7;
            }

            if (par4 == 3)
            {
                var12 = (double)par3 + var6.maxZ + (double)var7;
            }

            if (par4 == 4)
            {
                var8 = (double)par1 + var6.minX - (double)var7;
            }

            if (par4 == 5)
            {
                var8 = (double)par1 + var6.maxX + (double)var7;
            }

            addDigParticleEffect((EntityDiggingFX)(new EntityDiggingFX(this.worldObj, var8, var10, var12, 0.0D, 0.0D, 0.0D, var6, par4, this.worldObj.getBlockMetadata(par1, par2, par3))).func_4041_a(par1, par2, par3).multiplyVelocity(0.2F).func_405_d(0.6F), var6);
        }
    }

    public String getStatistics()
    {
        return "" + (this.fxLayers[0].size() + this.fxLayers[1].size() + this.fxLayers[2].size());
    }
    
    public void addDigParticleEffect(EntityDiggingFX effect, Block block) 
    {
        boolean added = false;
        String texture = ForgeHooksClient.getTexture("/terrain.png", block);
        
        for (int x = 0; x < effectList.size(); x++) 
        {
            BlockTextureParticles entry = (BlockTextureParticles)effectList.get(x);
            if (entry.texture.equals(texture)) 
            {
                entry.effects.add(effect);
                added = true;
            }
        }
        
        if (!added) 
        {
            BlockTextureParticles entry = new BlockTextureParticles();
            entry.texture = texture;
            entry.effects.add(effect);
            effectList.add(entry);
        }
        
        addEffect(effect);
    }
}
