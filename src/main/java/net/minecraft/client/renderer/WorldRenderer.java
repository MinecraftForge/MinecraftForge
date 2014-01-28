package net.minecraft.client.renderer;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.shader.TesselatorVertexState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class WorldRenderer
{
    private TesselatorVertexState field_147894_y;
    // JAVADOC FIELD $$ field_78924_a
    public World worldObj;
    private int glRenderList = -1;
    //private static Tessellator tessellator = Tessellator.instance;
    public static int chunksUpdated;
    public int posX;
    public int posY;
    public int posZ;
    // JAVADOC FIELD $$ field_78918_f
    public int posXMinus;
    // JAVADOC FIELD $$ field_78919_g
    public int posYMinus;
    // JAVADOC FIELD $$ field_78931_h
    public int posZMinus;
    // JAVADOC FIELD $$ field_78932_i
    public int posXClip;
    // JAVADOC FIELD $$ field_78929_j
    public int posYClip;
    // JAVADOC FIELD $$ field_78930_k
    public int posZClip;
    public boolean isInFrustum;
    // JAVADOC FIELD $$ field_78928_m
    public boolean[] skipRenderPass = new boolean[2];
    // JAVADOC FIELD $$ field_78925_n
    public int posXPlus;
    // JAVADOC FIELD $$ field_78926_o
    public int posYPlus;
    // JAVADOC FIELD $$ field_78940_p
    public int posZPlus;
    // JAVADOC FIELD $$ field_78939_q
    public boolean needsUpdate;
    // JAVADOC FIELD $$ field_78938_r
    public AxisAlignedBB rendererBoundingBox;
    // JAVADOC FIELD $$ field_78937_s
    public int chunkIndex;
    // JAVADOC FIELD $$ field_78936_t
    public boolean isVisible = true;
    // JAVADOC FIELD $$ field_78935_u
    public boolean isWaitingOnOcclusionQuery;
    // JAVADOC FIELD $$ field_78934_v
    public int glOcclusionQuery;
    // JAVADOC FIELD $$ field_78933_w
    public boolean isChunkLit;
    private boolean isInitialized;
    public List field_147895_x = new ArrayList();
    private List field_147893_C;
    // JAVADOC FIELD $$ field_78917_C
    private int bytesDrawn;
    private static final String __OBFID = "CL_00000942";

    public WorldRenderer(World par1World, List par2List, int par3, int par4, int par5, int par6)
    {
        this.worldObj = par1World;
        this.field_147894_y = null;
        this.field_147893_C = par2List;
        this.glRenderList = par6;
        this.posX = -999;
        this.setPosition(par3, par4, par5);
        this.needsUpdate = false;
    }

    // JAVADOC METHOD $$ func_78913_a
    public void setPosition(int par1, int par2, int par3)
    {
        if (par1 != this.posX || par2 != this.posY || par3 != this.posZ)
        {
            this.setDontDraw();
            this.posX = par1;
            this.posY = par2;
            this.posZ = par3;
            this.posXPlus = par1 + 8;
            this.posYPlus = par2 + 8;
            this.posZPlus = par3 + 8;
            this.posXClip = par1 & 1023;
            this.posYClip = par2;
            this.posZClip = par3 & 1023;
            this.posXMinus = par1 - this.posXClip;
            this.posYMinus = par2 - this.posYClip;
            this.posZMinus = par3 - this.posZClip;
            float f = 6.0F;
            this.rendererBoundingBox = AxisAlignedBB.getBoundingBox((double)((float)par1 - f), (double)((float)par2 - f), (double)((float)par3 - f), (double)((float)(par1 + 16) + f), (double)((float)(par2 + 16) + f), (double)((float)(par3 + 16) + f));
            GL11.glNewList(this.glRenderList + 2, GL11.GL_COMPILE);
            RenderItem.renderAABB(AxisAlignedBB.getAABBPool().getAABB((double)((float)this.posXClip - f), (double)((float)this.posYClip - f), (double)((float)this.posZClip - f), (double)((float)(this.posXClip + 16) + f), (double)((float)(this.posYClip + 16) + f), (double)((float)(this.posZClip + 16) + f)));
            GL11.glEndList();
            this.markDirty();
        }
    }

    private void setupGLTranslation()
    {
        GL11.glTranslatef((float)this.posXClip, (float)this.posYClip, (float)this.posZClip);
    }

    public void func_147892_a(EntityLivingBase p_147892_1_)
    {
        if (this.needsUpdate)
        {
            this.needsUpdate = false;
            int i = this.posX;
            int j = this.posY;
            int k = this.posZ;
            int l = this.posX + 16;
            int i1 = this.posY + 16;
            int j1 = this.posZ + 16;

            for (int k1 = 0; k1 < 2; ++k1)
            {
                this.skipRenderPass[k1] = true;
            }

            Chunk.isLit = false;
            HashSet hashset = new HashSet();
            hashset.addAll(this.field_147895_x);
            this.field_147895_x.clear();
            Minecraft minecraft = Minecraft.getMinecraft();
            EntityLivingBase entitylivingbase1 = minecraft.renderViewEntity;
            int l1 = MathHelper.floor_double(entitylivingbase1.posX);
            int i2 = MathHelper.floor_double(entitylivingbase1.posY);
            int j2 = MathHelper.floor_double(entitylivingbase1.posZ);
            byte b0 = 1;
            ChunkCache chunkcache = new ChunkCache(this.worldObj, i - b0, j - b0, k - b0, l + b0, i1 + b0, j1 + b0, b0);

            if (!chunkcache.extendedLevelsInChunkCache())
            {
                ++chunksUpdated;
                RenderBlocks renderblocks = new RenderBlocks(chunkcache);
                this.bytesDrawn = 0;
                this.field_147894_y = null;

                for (int k2 = 0; k2 < 2; ++k2)
                {
                    boolean flag = false;
                    boolean flag1 = false;
                    boolean flag2 = false;

                    for (int l2 = j; l2 < i1; ++l2)
                    {
                        for (int i3 = k; i3 < j1; ++i3)
                        {
                            for (int j3 = i; j3 < l; ++j3)
                            {
                                Block block = chunkcache.func_147439_a(j3, l2, i3);

                                if (block.func_149688_o() != Material.field_151579_a)
                                {
                                    if (!flag2)
                                    {
                                        flag2 = true;
                                        this.func_147890_b(k2);
                                    }

                                    if (k2 == 0 && block.hasTileEntity(chunkcache.getBlockMetadata(j3, l2, i3)))
                                    {
                                        TileEntity tileentity = chunkcache.func_147438_o(j3, l2, i3);

                                        if (TileEntityRendererDispatcher.field_147556_a.func_147545_a(tileentity))
                                        {
                                            this.field_147895_x.add(tileentity);
                                        }
                                    }

                                    int k3 = block.func_149701_w();

                                    if (k3 > k2)
                                    {
                                        flag = true;
                                    }

                                    if (!block.canRenderInPass(k2)) continue;

                                    {
                                        flag1 |= renderblocks.func_147805_b(block, j3, l2, i3);

                                        if (block.func_149645_b() == 0 && j3 == l1 && l2 == i2 && i3 == j2)
                                        {
                                            renderblocks.func_147786_a(true);
                                            renderblocks.func_147753_b(true);
                                            renderblocks.func_147805_b(block, j3, l2, i3);
                                            renderblocks.func_147786_a(false);
                                            renderblocks.func_147753_b(false);
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if (flag1)
                    {
                        this.skipRenderPass[k2] = false;
                    }

                    if (flag2)
                    {
                        this.func_147891_a(k2, p_147892_1_);
                    }
                    else
                    {
                        flag1 = false;
                    }

                    if (!flag)
                    {
                        break;
                    }
                }
            }

            HashSet hashset1 = new HashSet();
            hashset1.addAll(this.field_147895_x);
            hashset1.removeAll(hashset);
            this.field_147893_C.addAll(hashset1);
            hashset.removeAll(this.field_147895_x);
            this.field_147893_C.removeAll(hashset);
            this.isChunkLit = Chunk.isLit;
            this.isInitialized = true;
        }
    }

    private void func_147890_b(int p_147890_1_)
    {
        GL11.glNewList(this.glRenderList + p_147890_1_, GL11.GL_COMPILE);
        GL11.glPushMatrix();
        this.setupGLTranslation();
        float f = 1.000001F;
        GL11.glTranslatef(-8.0F, -8.0F, -8.0F);
        GL11.glScalef(f, f, f);
        GL11.glTranslatef(8.0F, 8.0F, 8.0F);
        Tessellator.instance.startDrawingQuads();
        Tessellator.instance.setTranslation((double)(-this.posX), (double)(-this.posY), (double)(-this.posZ));
    }

    private void func_147891_a(int p_147891_1_, EntityLivingBase p_147891_2_)
    {
        if (p_147891_1_ == 1 && !this.skipRenderPass[p_147891_1_])
        {
            this.field_147894_y = Tessellator.instance.func_147564_a((float)p_147891_2_.posX, (float)p_147891_2_.posY, (float)p_147891_2_.posZ);
        }

        //ForgeHooksClient.afterRenderPass(l1); Noop fo now, TODO: Event if anyone needs
        this.bytesDrawn += Tessellator.instance.draw();
        GL11.glPopMatrix();
        GL11.glEndList();
        Tessellator.instance.setTranslation(0.0D, 0.0D, 0.0D);
    }

    public void func_147889_b(EntityLivingBase p_147889_1_)
    {
        if (this.field_147894_y != null && !this.skipRenderPass[1])
        {
            this.func_147890_b(1);
            Tessellator.instance.func_147565_a(this.field_147894_y);
            this.func_147891_a(1, p_147889_1_);
        }
    }

    // JAVADOC METHOD $$ func_78912_a
    public float distanceToEntitySquared(Entity par1Entity)
    {
        float f = (float)(par1Entity.posX - (double)this.posXPlus);
        float f1 = (float)(par1Entity.posY - (double)this.posYPlus);
        float f2 = (float)(par1Entity.posZ - (double)this.posZPlus);
        return f * f + f1 * f1 + f2 * f2;
    }

    // JAVADOC METHOD $$ func_78910_b
    public void setDontDraw()
    {
        for (int i = 0; i < 2; ++i)
        {
            this.skipRenderPass[i] = true;
        }

        this.isInFrustum = false;
        this.isInitialized = false;
        this.field_147894_y = null;
    }

    public void stopRendering()
    {
        this.setDontDraw();
        this.worldObj = null;
    }

    // JAVADOC METHOD $$ func_78909_a
    public int getGLCallListForPass(int par1)
    {
        return !this.isInFrustum ? -1 : (!this.skipRenderPass[par1] ? this.glRenderList + par1 : -1);
    }

    public void updateInFrustum(ICamera par1ICamera)
    {
        this.isInFrustum = par1ICamera.isBoundingBoxInFrustum(this.rendererBoundingBox);
    }

    // JAVADOC METHOD $$ func_78904_d
    public void callOcclusionQueryList()
    {
        GL11.glCallList(this.glRenderList + 2);
    }

    // JAVADOC METHOD $$ func_78906_e
    public boolean skipAllRenderPasses()
    {
        return !this.isInitialized ? false : this.skipRenderPass[0] && this.skipRenderPass[1];
    }

    // JAVADOC METHOD $$ func_78914_f
    public void markDirty()
    {
        this.needsUpdate = true;
    }
}