package net.minecraft.src;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.ARBOcclusionQuery;
import org.lwjgl.opengl.GL11;

public class RenderGlobal implements IWorldAccess
{
    public List tileEntities = new ArrayList();

    /** A reference to the World object. */
    public World worldObj;

    /** The RenderEngine instance used by RenderGlobal */
    public RenderEngine renderEngine;
    private List worldRenderersToUpdate = new ArrayList();
    private WorldRenderer[] sortedWorldRenderers;
    private WorldRenderer[] worldRenderers;
    private int renderChunksWide;
    private int renderChunksTall;
    private int renderChunksDeep;

    /** OpenGL render lists base */
    private int glRenderListBase;

    /** A reference to the Minecraft object. */
    public Minecraft mc;

    /** Global render blocks */
    public RenderBlocks globalRenderBlocks;

    /** OpenGL occlusion query base */
    private IntBuffer glOcclusionQueryBase;

    /** Is occlusion testing enabled */
    private boolean occlusionEnabled = false;
    private int cloudOffsetX = 0;

    /** The star GL Call list */
    private int starGLCallList;

    /** OpenGL sky list */
    private int glSkyList;

    /** OpenGL sky list 2 */
    private int glSkyList2;

    /** Minimum block X */
    private int minBlockX;

    /** Minimum block Y */
    private int minBlockY;

    /** Minimum block Z */
    private int minBlockZ;

    /** Maximum block X */
    private int maxBlockX;

    /** Maximum block Y */
    private int maxBlockY;

    /** Maximum block Z */
    private int maxBlockZ;
    private int renderDistance = -1;

    /** Render entities startup counter (init value=2) */
    private int renderEntitiesStartupCounter = 2;

    /** Count entities total */
    private int countEntitiesTotal;

    /** Count entities rendered */
    private int countEntitiesRendered;

    /** Count entities hidden */
    private int countEntitiesHidden;

    /** Dummy buffer (50k) not used */
    int[] dummyBuf50k = new int[50000];

    /** Occlusion query result */
    IntBuffer occlusionResult = GLAllocation.createDirectIntBuffer(64);

    /** How many renderers are loaded this frame that try to be rendered */
    private int renderersLoaded;

    /** How many renderers are being clipped by the frustrum this frame */
    private int renderersBeingClipped;

    /** How many renderers are being occluded this frame */
    private int renderersBeingOccluded;

    /** How many renderers are actually being rendered this frame */
    private int renderersBeingRendered;

    /**
     * How many renderers are skipping rendering due to not having a render pass this frame
     */
    private int renderersSkippingRenderPass;

    /** Dummy render int */
    private int dummyRenderInt;

    /** World renderers check index */
    private int worldRenderersCheckIndex;

    /** List of OpenGL lists for the current render pass */
    private List glRenderLists = new ArrayList();

    /** All render lists (fixed length 4) */
    private RenderList[] allRenderLists = new RenderList[] {new RenderList(), new RenderList(), new RenderList(), new RenderList()};

    /**
     * Previous x position when the renderers were sorted. (Once the distance moves more than 4 units they will be
     * resorted)
     */
    double prevSortX = -9999.0D;

    /**
     * Previous y position when the renderers were sorted. (Once the distance moves more than 4 units they will be
     * resorted)
     */
    double prevSortY = -9999.0D;

    /**
     * Previous Z position when the renderers were sorted. (Once the distance moves more than 4 units they will be
     * resorted)
     */
    double prevSortZ = -9999.0D;

    /** Damage partial time */
    public float damagePartialTime;

    /**
     * The offset used to determine if a renderer is one of the sixteenth that are being updated this frame
     */
    int frustrumCheckOffset = 0;

    public RenderGlobal(Minecraft par1Minecraft, RenderEngine par2RenderEngine)
    {
        this.mc = par1Minecraft;
        this.renderEngine = par2RenderEngine;
        byte var3 = 34;
        byte var4 = 32;
        this.glRenderListBase = GLAllocation.generateDisplayLists(var3 * var3 * var4 * 3);
        this.occlusionEnabled = OpenGlCapsChecker.checkARBOcclusion();

        if (this.occlusionEnabled)
        {
            this.occlusionResult.clear();
            this.glOcclusionQueryBase = GLAllocation.createDirectIntBuffer(var3 * var3 * var4);
            this.glOcclusionQueryBase.clear();
            this.glOcclusionQueryBase.position(0);
            this.glOcclusionQueryBase.limit(var3 * var3 * var4);
            ARBOcclusionQuery.glGenQueriesARB(this.glOcclusionQueryBase);
        }

        this.starGLCallList = GLAllocation.generateDisplayLists(3);
        GL11.glPushMatrix();
        GL11.glNewList(this.starGLCallList, GL11.GL_COMPILE);
        this.renderStars();
        GL11.glEndList();
        GL11.glPopMatrix();
        Tessellator var5 = Tessellator.instance;
        this.glSkyList = this.starGLCallList + 1;
        GL11.glNewList(this.glSkyList, GL11.GL_COMPILE);
        byte var7 = 64;
        int var8 = 256 / var7 + 2;
        float var6 = 16.0F;
        int var9;
        int var10;

        for (var9 = -var7 * var8; var9 <= var7 * var8; var9 += var7)
        {
            for (var10 = -var7 * var8; var10 <= var7 * var8; var10 += var7)
            {
                var5.startDrawingQuads();
                var5.addVertex((double)(var9 + 0), (double)var6, (double)(var10 + 0));
                var5.addVertex((double)(var9 + var7), (double)var6, (double)(var10 + 0));
                var5.addVertex((double)(var9 + var7), (double)var6, (double)(var10 + var7));
                var5.addVertex((double)(var9 + 0), (double)var6, (double)(var10 + var7));
                var5.draw();
            }
        }

        GL11.glEndList();
        this.glSkyList2 = this.starGLCallList + 2;
        GL11.glNewList(this.glSkyList2, GL11.GL_COMPILE);
        var6 = -16.0F;
        var5.startDrawingQuads();

        for (var9 = -var7 * var8; var9 <= var7 * var8; var9 += var7)
        {
            for (var10 = -var7 * var8; var10 <= var7 * var8; var10 += var7)
            {
                var5.addVertex((double)(var9 + var7), (double)var6, (double)(var10 + 0));
                var5.addVertex((double)(var9 + 0), (double)var6, (double)(var10 + 0));
                var5.addVertex((double)(var9 + 0), (double)var6, (double)(var10 + var7));
                var5.addVertex((double)(var9 + var7), (double)var6, (double)(var10 + var7));
            }
        }

        var5.draw();
        GL11.glEndList();
    }

    private void renderStars()
    {
        Random var1 = new Random(10842L);
        Tessellator var2 = Tessellator.instance;
        var2.startDrawingQuads();

        for (int var3 = 0; var3 < 1500; ++var3)
        {
            double var4 = (double)(var1.nextFloat() * 2.0F - 1.0F);
            double var6 = (double)(var1.nextFloat() * 2.0F - 1.0F);
            double var8 = (double)(var1.nextFloat() * 2.0F - 1.0F);
            double var10 = (double)(0.25F + var1.nextFloat() * 0.25F);
            double var12 = var4 * var4 + var6 * var6 + var8 * var8;

            if (var12 < 1.0D && var12 > 0.01D)
            {
                var12 = 1.0D / Math.sqrt(var12);
                var4 *= var12;
                var6 *= var12;
                var8 *= var12;
                double var14 = var4 * 100.0D;
                double var16 = var6 * 100.0D;
                double var18 = var8 * 100.0D;
                double var20 = Math.atan2(var4, var8);
                double var22 = Math.sin(var20);
                double var24 = Math.cos(var20);
                double var26 = Math.atan2(Math.sqrt(var4 * var4 + var8 * var8), var6);
                double var28 = Math.sin(var26);
                double var30 = Math.cos(var26);
                double var32 = var1.nextDouble() * Math.PI * 2.0D;
                double var34 = Math.sin(var32);
                double var36 = Math.cos(var32);

                for (int var38 = 0; var38 < 4; ++var38)
                {
                    double var39 = 0.0D;
                    double var41 = (double)((var38 & 2) - 1) * var10;
                    double var43 = (double)((var38 + 1 & 2) - 1) * var10;
                    double var47 = var41 * var36 - var43 * var34;
                    double var49 = var43 * var36 + var41 * var34;
                    double var53 = var47 * var28 + var39 * var30;
                    double var55 = var39 * var28 - var47 * var30;
                    double var57 = var55 * var22 - var49 * var24;
                    double var61 = var49 * var22 + var55 * var24;
                    var2.addVertex(var14 + var57, var16 + var53, var18 + var61);
                }
            }
        }

        var2.draw();
    }

    /**
     * Changes the world reference in RenderGlobal
     */
    public void changeWorld(World par1World)
    {
        if (this.worldObj != null)
        {
            this.worldObj.removeWorldAccess(this);
        }

        this.prevSortX = -9999.0D;
        this.prevSortY = -9999.0D;
        this.prevSortZ = -9999.0D;
        RenderManager.instance.set(par1World);
        this.worldObj = par1World;
        this.globalRenderBlocks = new RenderBlocks(par1World);

        if (par1World != null)
        {
            par1World.addWorldAccess(this);
            this.loadRenderers();
        }
    }

    /**
     * Loads all the renderers and sets up the basic settings usage
     */
    public void loadRenderers()
    {
        if (this.worldObj != null)
        {
            Block.leaves.setGraphicsLevel(this.mc.gameSettings.fancyGraphics);
            this.renderDistance = this.mc.gameSettings.renderDistance;
            int var1;

            if (this.worldRenderers != null)
            {
                for (var1 = 0; var1 < this.worldRenderers.length; ++var1)
                {
                    this.worldRenderers[var1].stopRendering();
                }
            }

            var1 = 64 << 3 - this.renderDistance;

            if (var1 > 400)
            {
                var1 = 400;
            }

            this.renderChunksWide = var1 / 16 + 1;
            this.renderChunksTall = 16;
            this.renderChunksDeep = var1 / 16 + 1;
            this.worldRenderers = new WorldRenderer[this.renderChunksWide * this.renderChunksTall * this.renderChunksDeep];
            this.sortedWorldRenderers = new WorldRenderer[this.renderChunksWide * this.renderChunksTall * this.renderChunksDeep];
            int var2 = 0;
            int var3 = 0;
            this.minBlockX = 0;
            this.minBlockY = 0;
            this.minBlockZ = 0;
            this.maxBlockX = this.renderChunksWide;
            this.maxBlockY = this.renderChunksTall;
            this.maxBlockZ = this.renderChunksDeep;
            int var4;

            for (var4 = 0; var4 < this.worldRenderersToUpdate.size(); ++var4)
            {
                ((WorldRenderer)this.worldRenderersToUpdate.get(var4)).needsUpdate = false;
            }

            this.worldRenderersToUpdate.clear();
            this.tileEntities.clear();

            for (var4 = 0; var4 < this.renderChunksWide; ++var4)
            {
                for (int var5 = 0; var5 < this.renderChunksTall; ++var5)
                {
                    for (int var6 = 0; var6 < this.renderChunksDeep; ++var6)
                    {
                        this.worldRenderers[(var6 * this.renderChunksTall + var5) * this.renderChunksWide + var4] = new WorldRenderer(this.worldObj, this.tileEntities, var4 * 16, var5 * 16, var6 * 16, this.glRenderListBase + var2);

                        if (this.occlusionEnabled)
                        {
                            this.worldRenderers[(var6 * this.renderChunksTall + var5) * this.renderChunksWide + var4].glOcclusionQuery = this.glOcclusionQueryBase.get(var3);
                        }

                        this.worldRenderers[(var6 * this.renderChunksTall + var5) * this.renderChunksWide + var4].isWaitingOnOcclusionQuery = false;
                        this.worldRenderers[(var6 * this.renderChunksTall + var5) * this.renderChunksWide + var4].isVisible = true;
                        this.worldRenderers[(var6 * this.renderChunksTall + var5) * this.renderChunksWide + var4].isInFrustum = true;
                        this.worldRenderers[(var6 * this.renderChunksTall + var5) * this.renderChunksWide + var4].chunkIndex = var3++;
                        this.worldRenderers[(var6 * this.renderChunksTall + var5) * this.renderChunksWide + var4].markDirty();
                        this.sortedWorldRenderers[(var6 * this.renderChunksTall + var5) * this.renderChunksWide + var4] = this.worldRenderers[(var6 * this.renderChunksTall + var5) * this.renderChunksWide + var4];
                        this.worldRenderersToUpdate.add(this.worldRenderers[(var6 * this.renderChunksTall + var5) * this.renderChunksWide + var4]);
                        var2 += 3;
                    }
                }
            }

            if (this.worldObj != null)
            {
                EntityLiving var7 = this.mc.renderViewEntity;

                if (var7 != null)
                {
                    this.markRenderersForNewPosition(MathHelper.floor_double(var7.posX), MathHelper.floor_double(var7.posY), MathHelper.floor_double(var7.posZ));
                    Arrays.sort(this.sortedWorldRenderers, new EntitySorter(var7));
                }
            }

            this.renderEntitiesStartupCounter = 2;
        }
    }

    /**
     * Renders all entities within range and within the frustrum. Args: pos, frustrum, partialTickTime
     */
    public void renderEntities(Vec3D par1Vec3D, ICamera par2ICamera, float par3)
    {
        if (this.renderEntitiesStartupCounter > 0)
        {
            --this.renderEntitiesStartupCounter;
        }
        else
        {
            Profiler.startSection("prepare");
            TileEntityRenderer.instance.cacheActiveRenderInfo(this.worldObj, this.renderEngine, this.mc.fontRenderer, this.mc.renderViewEntity, par3);
            RenderManager.instance.cacheActiveRenderInfo(this.worldObj, this.renderEngine, this.mc.fontRenderer, this.mc.renderViewEntity, this.mc.gameSettings, par3);
            TileEntityRenderer.instance.func_40742_a();
            this.countEntitiesTotal = 0;
            this.countEntitiesRendered = 0;
            this.countEntitiesHidden = 0;
            EntityLiving var4 = this.mc.renderViewEntity;
            RenderManager.renderPosX = var4.lastTickPosX + (var4.posX - var4.lastTickPosX) * (double)par3;
            RenderManager.renderPosY = var4.lastTickPosY + (var4.posY - var4.lastTickPosY) * (double)par3;
            RenderManager.renderPosZ = var4.lastTickPosZ + (var4.posZ - var4.lastTickPosZ) * (double)par3;
            TileEntityRenderer.staticPlayerX = var4.lastTickPosX + (var4.posX - var4.lastTickPosX) * (double)par3;
            TileEntityRenderer.staticPlayerY = var4.lastTickPosY + (var4.posY - var4.lastTickPosY) * (double)par3;
            TileEntityRenderer.staticPlayerZ = var4.lastTickPosZ + (var4.posZ - var4.lastTickPosZ) * (double)par3;
            this.mc.entityRenderer.enableLightmap((double)par3);
            Profiler.endStartSection("global");
            List var5 = this.worldObj.getLoadedEntityList();
            this.countEntitiesTotal = var5.size();
            int var6;
            Entity var7;

            for (var6 = 0; var6 < this.worldObj.weatherEffects.size(); ++var6)
            {
                var7 = (Entity)this.worldObj.weatherEffects.get(var6);
                ++this.countEntitiesRendered;

                if (var7.isInRangeToRenderVec3D(par1Vec3D))
                {
                    RenderManager.instance.renderEntity(var7, par3);
                }
            }

            Profiler.endStartSection("entities");

            for (var6 = 0; var6 < var5.size(); ++var6)
            {
                var7 = (Entity)var5.get(var6);

                if (var7.isInRangeToRenderVec3D(par1Vec3D) && (var7.ignoreFrustrumCheck || par2ICamera.isBoundingBoxInFrustum(var7.boundingBox)) && (var7 != this.mc.renderViewEntity || this.mc.gameSettings.thirdPersonView != 0 || this.mc.renderViewEntity.isPlayerSleeping()) && this.worldObj.blockExists(MathHelper.floor_double(var7.posX), 0, MathHelper.floor_double(var7.posZ)))
                {
                    ++this.countEntitiesRendered;
                    RenderManager.instance.renderEntity(var7, par3);
                }
            }

            Profiler.endStartSection("tileentities");
            RenderHelper.enableStandardItemLighting();

            for (var6 = 0; var6 < this.tileEntities.size(); ++var6)
            {
                TileEntityRenderer.instance.renderTileEntity((TileEntity)this.tileEntities.get(var6), par3);
            }

            this.mc.entityRenderer.disableLightmap((double)par3);
            Profiler.endSection();
        }
    }

    /**
     * Gets the render info for use on the Debug screen
     */
    public String getDebugInfoRenders()
    {
        return "C: " + this.renderersBeingRendered + "/" + this.renderersLoaded + ". F: " + this.renderersBeingClipped + ", O: " + this.renderersBeingOccluded + ", E: " + this.renderersSkippingRenderPass;
    }

    /**
     * Gets the entities info for use on the Debug screen
     */
    public String getDebugInfoEntities()
    {
        return "E: " + this.countEntitiesRendered + "/" + this.countEntitiesTotal + ". B: " + this.countEntitiesHidden + ", I: " + (this.countEntitiesTotal - this.countEntitiesHidden - this.countEntitiesRendered);
    }

    /**
     * Goes through all the renderers setting new positions on them and those that have their position changed are
     * adding to be updated
     */
    private void markRenderersForNewPosition(int par1, int par2, int par3)
    {
        par1 -= 8;
        par2 -= 8;
        par3 -= 8;
        this.minBlockX = Integer.MAX_VALUE;
        this.minBlockY = Integer.MAX_VALUE;
        this.minBlockZ = Integer.MAX_VALUE;
        this.maxBlockX = Integer.MIN_VALUE;
        this.maxBlockY = Integer.MIN_VALUE;
        this.maxBlockZ = Integer.MIN_VALUE;
        int var4 = this.renderChunksWide * 16;
        int var5 = var4 / 2;

        for (int var6 = 0; var6 < this.renderChunksWide; ++var6)
        {
            int var7 = var6 * 16;
            int var8 = var7 + var5 - par1;

            if (var8 < 0)
            {
                var8 -= var4 - 1;
            }

            var8 /= var4;
            var7 -= var8 * var4;

            if (var7 < this.minBlockX)
            {
                this.minBlockX = var7;
            }

            if (var7 > this.maxBlockX)
            {
                this.maxBlockX = var7;
            }

            for (int var9 = 0; var9 < this.renderChunksDeep; ++var9)
            {
                int var10 = var9 * 16;
                int var11 = var10 + var5 - par3;

                if (var11 < 0)
                {
                    var11 -= var4 - 1;
                }

                var11 /= var4;
                var10 -= var11 * var4;

                if (var10 < this.minBlockZ)
                {
                    this.minBlockZ = var10;
                }

                if (var10 > this.maxBlockZ)
                {
                    this.maxBlockZ = var10;
                }

                for (int var12 = 0; var12 < this.renderChunksTall; ++var12)
                {
                    int var13 = var12 * 16;

                    if (var13 < this.minBlockY)
                    {
                        this.minBlockY = var13;
                    }

                    if (var13 > this.maxBlockY)
                    {
                        this.maxBlockY = var13;
                    }

                    WorldRenderer var14 = this.worldRenderers[(var9 * this.renderChunksTall + var12) * this.renderChunksWide + var6];
                    boolean var15 = var14.needsUpdate;
                    var14.setPosition(var7, var13, var10);

                    if (!var15 && var14.needsUpdate)
                    {
                        this.worldRenderersToUpdate.add(var14);
                    }
                }
            }
        }
    }

    /**
     * Sorts all renderers based on the passed in entity. Args: entityLiving, renderPass, partialTickTime
     */
    public int sortAndRender(EntityLiving par1EntityLiving, int par2, double par3)
    {
        Profiler.startSection("sortchunks");

        for (int var5 = 0; var5 < 10; ++var5)
        {
            this.worldRenderersCheckIndex = (this.worldRenderersCheckIndex + 1) % this.worldRenderers.length;
            WorldRenderer var6 = this.worldRenderers[this.worldRenderersCheckIndex];

            if (var6.needsUpdate && !this.worldRenderersToUpdate.contains(var6))
            {
                this.worldRenderersToUpdate.add(var6);
            }
        }

        if (this.mc.gameSettings.renderDistance != this.renderDistance)
        {
            this.loadRenderers();
        }

        if (par2 == 0)
        {
            this.renderersLoaded = 0;
            this.dummyRenderInt = 0;
            this.renderersBeingClipped = 0;
            this.renderersBeingOccluded = 0;
            this.renderersBeingRendered = 0;
            this.renderersSkippingRenderPass = 0;
        }

        double var33 = par1EntityLiving.lastTickPosX + (par1EntityLiving.posX - par1EntityLiving.lastTickPosX) * par3;
        double var7 = par1EntityLiving.lastTickPosY + (par1EntityLiving.posY - par1EntityLiving.lastTickPosY) * par3;
        double var9 = par1EntityLiving.lastTickPosZ + (par1EntityLiving.posZ - par1EntityLiving.lastTickPosZ) * par3;
        double var11 = par1EntityLiving.posX - this.prevSortX;
        double var13 = par1EntityLiving.posY - this.prevSortY;
        double var15 = par1EntityLiving.posZ - this.prevSortZ;

        if (var11 * var11 + var13 * var13 + var15 * var15 > 16.0D)
        {
            this.prevSortX = par1EntityLiving.posX;
            this.prevSortY = par1EntityLiving.posY;
            this.prevSortZ = par1EntityLiving.posZ;
            this.markRenderersForNewPosition(MathHelper.floor_double(par1EntityLiving.posX), MathHelper.floor_double(par1EntityLiving.posY), MathHelper.floor_double(par1EntityLiving.posZ));
            Arrays.sort(this.sortedWorldRenderers, new EntitySorter(par1EntityLiving));
        }

        RenderHelper.disableStandardItemLighting();
        byte var17 = 0;
        int var34;

        if (this.occlusionEnabled && this.mc.gameSettings.advancedOpengl && !this.mc.gameSettings.anaglyph && par2 == 0)
        {
            byte var18 = 0;
            int var19 = 16;
            this.checkOcclusionQueryResult(var18, var19);

            for (int var20 = var18; var20 < var19; ++var20)
            {
                this.sortedWorldRenderers[var20].isVisible = true;
            }

            Profiler.endStartSection("render");
            var34 = var17 + this.renderSortedRenderers(var18, var19, par2, par3);

            do
            {
                Profiler.endStartSection("occ");
                int var35 = var19;
                var19 *= 2;

                if (var19 > this.sortedWorldRenderers.length)
                {
                    var19 = this.sortedWorldRenderers.length;
                }

                GL11.glDisable(GL11.GL_TEXTURE_2D);
                GL11.glDisable(GL11.GL_LIGHTING);
                GL11.glDisable(GL11.GL_ALPHA_TEST);
                GL11.glDisable(GL11.GL_FOG);
                GL11.glColorMask(false, false, false, false);
                GL11.glDepthMask(false);
                Profiler.startSection("check");
                this.checkOcclusionQueryResult(var35, var19);
                Profiler.endSection();
                GL11.glPushMatrix();
                float var36 = 0.0F;
                float var21 = 0.0F;
                float var22 = 0.0F;

                for (int var23 = var35; var23 < var19; ++var23)
                {
                    if (this.sortedWorldRenderers[var23].skipAllRenderPasses())
                    {
                        this.sortedWorldRenderers[var23].isInFrustum = false;
                    }
                    else
                    {
                        if (!this.sortedWorldRenderers[var23].isInFrustum)
                        {
                            this.sortedWorldRenderers[var23].isVisible = true;
                        }

                        if (this.sortedWorldRenderers[var23].isInFrustum && !this.sortedWorldRenderers[var23].isWaitingOnOcclusionQuery)
                        {
                            float var24 = MathHelper.sqrt_float(this.sortedWorldRenderers[var23].distanceToEntitySquared(par1EntityLiving));
                            int var25 = (int)(1.0F + var24 / 128.0F);

                            if (this.cloudOffsetX % var25 == var23 % var25)
                            {
                                WorldRenderer var26 = this.sortedWorldRenderers[var23];
                                float var27 = (float)((double)var26.posXMinus - var33);
                                float var28 = (float)((double)var26.posYMinus - var7);
                                float var29 = (float)((double)var26.posZMinus - var9);
                                float var30 = var27 - var36;
                                float var31 = var28 - var21;
                                float var32 = var29 - var22;

                                if (var30 != 0.0F || var31 != 0.0F || var32 != 0.0F)
                                {
                                    GL11.glTranslatef(var30, var31, var32);
                                    var36 += var30;
                                    var21 += var31;
                                    var22 += var32;
                                }

                                Profiler.startSection("bb");
                                ARBOcclusionQuery.glBeginQueryARB(ARBOcclusionQuery.GL_SAMPLES_PASSED_ARB, this.sortedWorldRenderers[var23].glOcclusionQuery);
                                this.sortedWorldRenderers[var23].callOcclusionQueryList();
                                ARBOcclusionQuery.glEndQueryARB(ARBOcclusionQuery.GL_SAMPLES_PASSED_ARB);
                                Profiler.endSection();
                                this.sortedWorldRenderers[var23].isWaitingOnOcclusionQuery = true;
                            }
                        }
                    }
                }

                GL11.glPopMatrix();

                if (this.mc.gameSettings.anaglyph)
                {
                    if (EntityRenderer.anaglyphField == 0)
                    {
                        GL11.glColorMask(false, true, true, true);
                    }
                    else
                    {
                        GL11.glColorMask(true, false, false, true);
                    }
                }
                else
                {
                    GL11.glColorMask(true, true, true, true);
                }

                GL11.glDepthMask(true);
                GL11.glEnable(GL11.GL_TEXTURE_2D);
                GL11.glEnable(GL11.GL_ALPHA_TEST);
                GL11.glEnable(GL11.GL_FOG);
                Profiler.endStartSection("render");
                var34 += this.renderSortedRenderers(var35, var19, par2, par3);
            }
            while (var19 < this.sortedWorldRenderers.length);
        }
        else
        {
            Profiler.endStartSection("render");
            var34 = var17 + this.renderSortedRenderers(0, this.sortedWorldRenderers.length, par2, par3);
        }

        Profiler.endSection();
        return var34;
    }

    private void checkOcclusionQueryResult(int par1, int par2)
    {
        for (int var3 = par1; var3 < par2; ++var3)
        {
            if (this.sortedWorldRenderers[var3].isWaitingOnOcclusionQuery)
            {
                this.occlusionResult.clear();
                ARBOcclusionQuery.glGetQueryObjectuARB(this.sortedWorldRenderers[var3].glOcclusionQuery, ARBOcclusionQuery.GL_QUERY_RESULT_AVAILABLE_ARB, this.occlusionResult);

                if (this.occlusionResult.get(0) != 0)
                {
                    this.sortedWorldRenderers[var3].isWaitingOnOcclusionQuery = false;
                    this.occlusionResult.clear();
                    ARBOcclusionQuery.glGetQueryObjectuARB(this.sortedWorldRenderers[var3].glOcclusionQuery, ARBOcclusionQuery.GL_QUERY_RESULT_ARB, this.occlusionResult);
                    this.sortedWorldRenderers[var3].isVisible = this.occlusionResult.get(0) != 0;
                }
            }
        }
    }

    /**
     * Renders the sorted renders for the specified render pass. Args: startRenderer, numRenderers, renderPass,
     * partialTickTime
     */
    private int renderSortedRenderers(int par1, int par2, int par3, double par4)
    {
        this.glRenderLists.clear();
        int var6 = 0;

        for (int var7 = par1; var7 < par2; ++var7)
        {
            if (par3 == 0)
            {
                ++this.renderersLoaded;

                if (this.sortedWorldRenderers[var7].skipRenderPass[par3])
                {
                    ++this.renderersSkippingRenderPass;
                }
                else if (!this.sortedWorldRenderers[var7].isInFrustum)
                {
                    ++this.renderersBeingClipped;
                }
                else if (this.occlusionEnabled && !this.sortedWorldRenderers[var7].isVisible)
                {
                    ++this.renderersBeingOccluded;
                }
                else
                {
                    ++this.renderersBeingRendered;
                }
            }

            if (!this.sortedWorldRenderers[var7].skipRenderPass[par3] && this.sortedWorldRenderers[var7].isInFrustum && (!this.occlusionEnabled || this.sortedWorldRenderers[var7].isVisible))
            {
                int var8 = this.sortedWorldRenderers[var7].getGLCallListForPass(par3);

                if (var8 >= 0)
                {
                    this.glRenderLists.add(this.sortedWorldRenderers[var7]);
                    ++var6;
                }
            }
        }

        EntityLiving var19 = this.mc.renderViewEntity;
        double var20 = var19.lastTickPosX + (var19.posX - var19.lastTickPosX) * par4;
        double var10 = var19.lastTickPosY + (var19.posY - var19.lastTickPosY) * par4;
        double var12 = var19.lastTickPosZ + (var19.posZ - var19.lastTickPosZ) * par4;
        int var14 = 0;
        int var15;

        for (var15 = 0; var15 < this.allRenderLists.length; ++var15)
        {
            this.allRenderLists[var15].func_859_b();
        }

        for (var15 = 0; var15 < this.glRenderLists.size(); ++var15)
        {
            WorldRenderer var16 = (WorldRenderer)this.glRenderLists.get(var15);
            int var17 = -1;

            for (int var18 = 0; var18 < var14; ++var18)
            {
                if (this.allRenderLists[var18].func_862_a(var16.posXMinus, var16.posYMinus, var16.posZMinus))
                {
                    var17 = var18;
                }
            }

            if (var17 < 0)
            {
                var17 = var14++;
                this.allRenderLists[var17].func_861_a(var16.posXMinus, var16.posYMinus, var16.posZMinus, var20, var10, var12);
            }

            this.allRenderLists[var17].func_858_a(var16.getGLCallListForPass(par3));
        }

        this.renderAllRenderLists(par3, par4);
        return var6;
    }

    /**
     * Render all render lists
     */
    public void renderAllRenderLists(int par1, double par2)
    {
        this.mc.entityRenderer.enableLightmap(par2);

        for (int var4 = 0; var4 < this.allRenderLists.length; ++var4)
        {
            this.allRenderLists[var4].func_860_a();
        }

        this.mc.entityRenderer.disableLightmap(par2);
    }

    public void updateClouds()
    {
        ++this.cloudOffsetX;
    }

    /**
     * Renders the sky with the partial tick time. Args: partialTickTime
     */
    public void renderSky(float par1)
    {
        if (this.mc.theWorld.worldProvider.worldType == 1)
        {
            GL11.glDisable(GL11.GL_FOG);
            GL11.glDisable(GL11.GL_ALPHA_TEST);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            RenderHelper.disableStandardItemLighting();
            GL11.glDepthMask(false);
            this.renderEngine.bindTexture(this.renderEngine.getTexture("/misc/tunnel.png"));
            Tessellator var19 = Tessellator.instance;

            for (int var20 = 0; var20 < 6; ++var20)
            {
                GL11.glPushMatrix();

                if (var20 == 1)
                {
                    GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
                }

                if (var20 == 2)
                {
                    GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
                }

                if (var20 == 3)
                {
                    GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
                }

                if (var20 == 4)
                {
                    GL11.glRotatef(90.0F, 0.0F, 0.0F, 1.0F);
                }

                if (var20 == 5)
                {
                    GL11.glRotatef(-90.0F, 0.0F, 0.0F, 1.0F);
                }

                var19.startDrawingQuads();
                var19.setColorOpaque_I(1579032);
                var19.addVertexWithUV(-100.0D, -100.0D, -100.0D, 0.0D, 0.0D);
                var19.addVertexWithUV(-100.0D, -100.0D, 100.0D, 0.0D, 16.0D);
                var19.addVertexWithUV(100.0D, -100.0D, 100.0D, 16.0D, 16.0D);
                var19.addVertexWithUV(100.0D, -100.0D, -100.0D, 16.0D, 0.0D);
                var19.draw();
                GL11.glPopMatrix();
            }

            GL11.glDepthMask(true);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glEnable(GL11.GL_ALPHA_TEST);
        }
        else if (this.mc.theWorld.worldProvider.func_48217_e())
        {
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            Vec3D var2 = this.worldObj.getSkyColor(this.mc.renderViewEntity, par1);
            float var3 = (float)var2.xCoord;
            float var4 = (float)var2.yCoord;
            float var5 = (float)var2.zCoord;
            float var7;
            float var8;

            if (this.mc.gameSettings.anaglyph)
            {
                float var6 = (var3 * 30.0F + var4 * 59.0F + var5 * 11.0F) / 100.0F;
                var7 = (var3 * 30.0F + var4 * 70.0F) / 100.0F;
                var8 = (var3 * 30.0F + var5 * 70.0F) / 100.0F;
                var3 = var6;
                var4 = var7;
                var5 = var8;
            }

            GL11.glColor3f(var3, var4, var5);
            Tessellator var21 = Tessellator.instance;
            GL11.glDepthMask(false);
            GL11.glEnable(GL11.GL_FOG);
            GL11.glColor3f(var3, var4, var5);
            GL11.glCallList(this.glSkyList);
            GL11.glDisable(GL11.GL_FOG);
            GL11.glDisable(GL11.GL_ALPHA_TEST);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            RenderHelper.disableStandardItemLighting();
            float[] var22 = this.worldObj.worldProvider.calcSunriseSunsetColors(this.worldObj.getCelestialAngle(par1), par1);
            float var9;
            float var10;
            float var11;
            float var12;
            float var15;
            int var25;

            if (var22 != null)
            {
                GL11.glDisable(GL11.GL_TEXTURE_2D);
                GL11.glShadeModel(GL11.GL_SMOOTH);
                GL11.glPushMatrix();
                GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(MathHelper.sin(this.worldObj.getCelestialAngleRadians(par1)) < 0.0F ? 180.0F : 0.0F, 0.0F, 0.0F, 1.0F);
                GL11.glRotatef(90.0F, 0.0F, 0.0F, 1.0F);
                var8 = var22[0];
                var9 = var22[1];
                var10 = var22[2];
                float var13;

                if (this.mc.gameSettings.anaglyph)
                {
                    var11 = (var8 * 30.0F + var9 * 59.0F + var10 * 11.0F) / 100.0F;
                    var12 = (var8 * 30.0F + var9 * 70.0F) / 100.0F;
                    var13 = (var8 * 30.0F + var10 * 70.0F) / 100.0F;
                    var8 = var11;
                    var9 = var12;
                    var10 = var13;
                }

                var21.startDrawing(6);
                var21.setColorRGBA_F(var8, var9, var10, var22[3]);
                var21.addVertex(0.0D, 100.0D, 0.0D);
                byte var24 = 16;
                var21.setColorRGBA_F(var22[0], var22[1], var22[2], 0.0F);

                for (var25 = 0; var25 <= var24; ++var25)
                {
                    var13 = (float)var25 * (float)Math.PI * 2.0F / (float)var24;
                    float var14 = MathHelper.sin(var13);
                    var15 = MathHelper.cos(var13);
                    var21.addVertex((double)(var14 * 120.0F), (double)(var15 * 120.0F), (double)(-var15 * 40.0F * var22[3]));
                }

                var21.draw();
                GL11.glPopMatrix();
                GL11.glShadeModel(GL11.GL_FLAT);
            }

            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
            GL11.glPushMatrix();
            var7 = 1.0F - this.worldObj.getRainStrength(par1);
            var8 = 0.0F;
            var9 = 0.0F;
            var10 = 0.0F;
            GL11.glColor4f(1.0F, 1.0F, 1.0F, var7);
            GL11.glTranslatef(var8, var9, var10);
            GL11.glRotatef(-90.0F, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(this.worldObj.getCelestialAngle(par1) * 360.0F, 1.0F, 0.0F, 0.0F);
            var11 = 30.0F;
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.renderEngine.getTexture("/terrain/sun.png"));
            var21.startDrawingQuads();
            var21.addVertexWithUV((double)(-var11), 100.0D, (double)(-var11), 0.0D, 0.0D);
            var21.addVertexWithUV((double)var11, 100.0D, (double)(-var11), 1.0D, 0.0D);
            var21.addVertexWithUV((double)var11, 100.0D, (double)var11, 1.0D, 1.0D);
            var21.addVertexWithUV((double)(-var11), 100.0D, (double)var11, 0.0D, 1.0D);
            var21.draw();
            var11 = 20.0F;
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.renderEngine.getTexture("/terrain/moon_phases.png"));
            var25 = this.worldObj.getMoonPhase(par1);
            int var26 = var25 % 4;
            int var27 = var25 / 4 % 2;
            var15 = (float)(var26 + 0) / 4.0F;
            float var16 = (float)(var27 + 0) / 2.0F;
            float var17 = (float)(var26 + 1) / 4.0F;
            float var18 = (float)(var27 + 1) / 2.0F;
            var21.startDrawingQuads();
            var21.addVertexWithUV((double)(-var11), -100.0D, (double)var11, (double)var17, (double)var18);
            var21.addVertexWithUV((double)var11, -100.0D, (double)var11, (double)var15, (double)var18);
            var21.addVertexWithUV((double)var11, -100.0D, (double)(-var11), (double)var15, (double)var16);
            var21.addVertexWithUV((double)(-var11), -100.0D, (double)(-var11), (double)var17, (double)var16);
            var21.draw();
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            var12 = this.worldObj.getStarBrightness(par1) * var7;

            if (var12 > 0.0F)
            {
                GL11.glColor4f(var12, var12, var12, var12);
                GL11.glCallList(this.starGLCallList);
            }

            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glEnable(GL11.GL_ALPHA_TEST);
            GL11.glEnable(GL11.GL_FOG);
            GL11.glPopMatrix();
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glColor3f(0.0F, 0.0F, 0.0F);
            double var23 = this.mc.thePlayer.getPosition(par1).yCoord - this.worldObj.getSeaLevel();

            if (var23 < 0.0D)
            {
                GL11.glPushMatrix();
                GL11.glTranslatef(0.0F, 12.0F, 0.0F);
                GL11.glCallList(this.glSkyList2);
                GL11.glPopMatrix();
                var9 = 1.0F;
                var10 = -((float)(var23 + 65.0D));
                var11 = -var9;
                var21.startDrawingQuads();
                var21.setColorRGBA_I(0, 255);
                var21.addVertex((double)(-var9), (double)var10, (double)var9);
                var21.addVertex((double)var9, (double)var10, (double)var9);
                var21.addVertex((double)var9, (double)var11, (double)var9);
                var21.addVertex((double)(-var9), (double)var11, (double)var9);
                var21.addVertex((double)(-var9), (double)var11, (double)(-var9));
                var21.addVertex((double)var9, (double)var11, (double)(-var9));
                var21.addVertex((double)var9, (double)var10, (double)(-var9));
                var21.addVertex((double)(-var9), (double)var10, (double)(-var9));
                var21.addVertex((double)var9, (double)var11, (double)(-var9));
                var21.addVertex((double)var9, (double)var11, (double)var9);
                var21.addVertex((double)var9, (double)var10, (double)var9);
                var21.addVertex((double)var9, (double)var10, (double)(-var9));
                var21.addVertex((double)(-var9), (double)var10, (double)(-var9));
                var21.addVertex((double)(-var9), (double)var10, (double)var9);
                var21.addVertex((double)(-var9), (double)var11, (double)var9);
                var21.addVertex((double)(-var9), (double)var11, (double)(-var9));
                var21.addVertex((double)(-var9), (double)var11, (double)(-var9));
                var21.addVertex((double)(-var9), (double)var11, (double)var9);
                var21.addVertex((double)var9, (double)var11, (double)var9);
                var21.addVertex((double)var9, (double)var11, (double)(-var9));
                var21.draw();
            }

            if (this.worldObj.worldProvider.isSkyColored())
            {
                GL11.glColor3f(var3 * 0.2F + 0.04F, var4 * 0.2F + 0.04F, var5 * 0.6F + 0.1F);
            }
            else
            {
                GL11.glColor3f(var3, var4, var5);
            }

            GL11.glPushMatrix();
            GL11.glTranslatef(0.0F, -((float)(var23 - 16.0D)), 0.0F);
            GL11.glCallList(this.glSkyList2);
            GL11.glPopMatrix();
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glDepthMask(true);
        }
    }

    public void renderClouds(float par1)
    {
        if (this.mc.theWorld.worldProvider.func_48217_e())
        {
            if (this.mc.gameSettings.fancyGraphics)
            {
                this.renderCloudsFancy(par1);
            }
            else
            {
                GL11.glDisable(GL11.GL_CULL_FACE);
                float var2 = (float)(this.mc.renderViewEntity.lastTickPosY + (this.mc.renderViewEntity.posY - this.mc.renderViewEntity.lastTickPosY) * (double)par1);
                byte var3 = 32;
                int var4 = 256 / var3;
                Tessellator var5 = Tessellator.instance;
                GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.renderEngine.getTexture("/environment/clouds.png"));
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                Vec3D var6 = this.worldObj.drawClouds(par1);
                float var7 = (float)var6.xCoord;
                float var8 = (float)var6.yCoord;
                float var9 = (float)var6.zCoord;
                float var10;

                if (this.mc.gameSettings.anaglyph)
                {
                    var10 = (var7 * 30.0F + var8 * 59.0F + var9 * 11.0F) / 100.0F;
                    float var11 = (var7 * 30.0F + var8 * 70.0F) / 100.0F;
                    float var12 = (var7 * 30.0F + var9 * 70.0F) / 100.0F;
                    var7 = var10;
                    var8 = var11;
                    var9 = var12;
                }

                var10 = 4.8828125E-4F;
                double var24 = (double)((float)this.cloudOffsetX + par1);
                double var13 = this.mc.renderViewEntity.prevPosX + (this.mc.renderViewEntity.posX - this.mc.renderViewEntity.prevPosX) * (double)par1 + var24 * 0.029999999329447746D;
                double var15 = this.mc.renderViewEntity.prevPosZ + (this.mc.renderViewEntity.posZ - this.mc.renderViewEntity.prevPosZ) * (double)par1;
                int var17 = MathHelper.floor_double(var13 / 2048.0D);
                int var18 = MathHelper.floor_double(var15 / 2048.0D);
                var13 -= (double)(var17 * 2048);
                var15 -= (double)(var18 * 2048);
                float var19 = this.worldObj.worldProvider.getCloudHeight() - var2 + 0.33F;
                float var20 = (float)(var13 * (double)var10);
                float var21 = (float)(var15 * (double)var10);
                var5.startDrawingQuads();
                var5.setColorRGBA_F(var7, var8, var9, 0.8F);

                for (int var22 = -var3 * var4; var22 < var3 * var4; var22 += var3)
                {
                    for (int var23 = -var3 * var4; var23 < var3 * var4; var23 += var3)
                    {
                        var5.addVertexWithUV((double)(var22 + 0), (double)var19, (double)(var23 + var3), (double)((float)(var22 + 0) * var10 + var20), (double)((float)(var23 + var3) * var10 + var21));
                        var5.addVertexWithUV((double)(var22 + var3), (double)var19, (double)(var23 + var3), (double)((float)(var22 + var3) * var10 + var20), (double)((float)(var23 + var3) * var10 + var21));
                        var5.addVertexWithUV((double)(var22 + var3), (double)var19, (double)(var23 + 0), (double)((float)(var22 + var3) * var10 + var20), (double)((float)(var23 + 0) * var10 + var21));
                        var5.addVertexWithUV((double)(var22 + 0), (double)var19, (double)(var23 + 0), (double)((float)(var22 + 0) * var10 + var20), (double)((float)(var23 + 0) * var10 + var21));
                    }
                }

                var5.draw();
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                GL11.glDisable(GL11.GL_BLEND);
                GL11.glEnable(GL11.GL_CULL_FACE);
            }
        }
    }

    public boolean func_27307_a(double par1, double par3, double par5, float par7)
    {
        return false;
    }

    /**
     * Renders the 3d fancy clouds
     */
    public void renderCloudsFancy(float par1)
    {
        GL11.glDisable(GL11.GL_CULL_FACE);
        float var2 = (float)(this.mc.renderViewEntity.lastTickPosY + (this.mc.renderViewEntity.posY - this.mc.renderViewEntity.lastTickPosY) * (double)par1);
        Tessellator var3 = Tessellator.instance;
        float var4 = 12.0F;
        float var5 = 4.0F;
        double var6 = (double)((float)this.cloudOffsetX + par1);
        double var8 = (this.mc.renderViewEntity.prevPosX + (this.mc.renderViewEntity.posX - this.mc.renderViewEntity.prevPosX) * (double)par1 + var6 * 0.029999999329447746D) / (double)var4;
        double var10 = (this.mc.renderViewEntity.prevPosZ + (this.mc.renderViewEntity.posZ - this.mc.renderViewEntity.prevPosZ) * (double)par1) / (double)var4 + 0.33000001311302185D;
        float var12 = this.worldObj.worldProvider.getCloudHeight() - var2 + 0.33F;
        int var13 = MathHelper.floor_double(var8 / 2048.0D);
        int var14 = MathHelper.floor_double(var10 / 2048.0D);
        var8 -= (double)(var13 * 2048);
        var10 -= (double)(var14 * 2048);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.renderEngine.getTexture("/environment/clouds.png"));
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        Vec3D var15 = this.worldObj.drawClouds(par1);
        float var16 = (float)var15.xCoord;
        float var17 = (float)var15.yCoord;
        float var18 = (float)var15.zCoord;
        float var19;
        float var21;
        float var20;

        if (this.mc.gameSettings.anaglyph)
        {
            var19 = (var16 * 30.0F + var17 * 59.0F + var18 * 11.0F) / 100.0F;
            var20 = (var16 * 30.0F + var17 * 70.0F) / 100.0F;
            var21 = (var16 * 30.0F + var18 * 70.0F) / 100.0F;
            var16 = var19;
            var17 = var20;
            var18 = var21;
        }

        var19 = (float)(var8 * 0.0D);
        var20 = (float)(var10 * 0.0D);
        var21 = 0.00390625F;
        var19 = (float)MathHelper.floor_double(var8) * var21;
        var20 = (float)MathHelper.floor_double(var10) * var21;
        float var22 = (float)(var8 - (double)MathHelper.floor_double(var8));
        float var23 = (float)(var10 - (double)MathHelper.floor_double(var10));
        byte var24 = 8;
        byte var25 = 4;
        float var26 = 9.765625E-4F;
        GL11.glScalef(var4, 1.0F, var4);

        for (int var27 = 0; var27 < 2; ++var27)
        {
            if (var27 == 0)
            {
                GL11.glColorMask(false, false, false, false);
            }
            else if (this.mc.gameSettings.anaglyph)
            {
                if (EntityRenderer.anaglyphField == 0)
                {
                    GL11.glColorMask(false, true, true, true);
                }
                else
                {
                    GL11.glColorMask(true, false, false, true);
                }
            }
            else
            {
                GL11.glColorMask(true, true, true, true);
            }

            for (int var28 = -var25 + 1; var28 <= var25; ++var28)
            {
                for (int var29 = -var25 + 1; var29 <= var25; ++var29)
                {
                    var3.startDrawingQuads();
                    float var30 = (float)(var28 * var24);
                    float var31 = (float)(var29 * var24);
                    float var32 = var30 - var22;
                    float var33 = var31 - var23;

                    if (var12 > -var5 - 1.0F)
                    {
                        var3.setColorRGBA_F(var16 * 0.7F, var17 * 0.7F, var18 * 0.7F, 0.8F);
                        var3.setNormal(0.0F, -1.0F, 0.0F);
                        var3.addVertexWithUV((double)(var32 + 0.0F), (double)(var12 + 0.0F), (double)(var33 + (float)var24), (double)((var30 + 0.0F) * var21 + var19), (double)((var31 + (float)var24) * var21 + var20));
                        var3.addVertexWithUV((double)(var32 + (float)var24), (double)(var12 + 0.0F), (double)(var33 + (float)var24), (double)((var30 + (float)var24) * var21 + var19), (double)((var31 + (float)var24) * var21 + var20));
                        var3.addVertexWithUV((double)(var32 + (float)var24), (double)(var12 + 0.0F), (double)(var33 + 0.0F), (double)((var30 + (float)var24) * var21 + var19), (double)((var31 + 0.0F) * var21 + var20));
                        var3.addVertexWithUV((double)(var32 + 0.0F), (double)(var12 + 0.0F), (double)(var33 + 0.0F), (double)((var30 + 0.0F) * var21 + var19), (double)((var31 + 0.0F) * var21 + var20));
                    }

                    if (var12 <= var5 + 1.0F)
                    {
                        var3.setColorRGBA_F(var16, var17, var18, 0.8F);
                        var3.setNormal(0.0F, 1.0F, 0.0F);
                        var3.addVertexWithUV((double)(var32 + 0.0F), (double)(var12 + var5 - var26), (double)(var33 + (float)var24), (double)((var30 + 0.0F) * var21 + var19), (double)((var31 + (float)var24) * var21 + var20));
                        var3.addVertexWithUV((double)(var32 + (float)var24), (double)(var12 + var5 - var26), (double)(var33 + (float)var24), (double)((var30 + (float)var24) * var21 + var19), (double)((var31 + (float)var24) * var21 + var20));
                        var3.addVertexWithUV((double)(var32 + (float)var24), (double)(var12 + var5 - var26), (double)(var33 + 0.0F), (double)((var30 + (float)var24) * var21 + var19), (double)((var31 + 0.0F) * var21 + var20));
                        var3.addVertexWithUV((double)(var32 + 0.0F), (double)(var12 + var5 - var26), (double)(var33 + 0.0F), (double)((var30 + 0.0F) * var21 + var19), (double)((var31 + 0.0F) * var21 + var20));
                    }

                    var3.setColorRGBA_F(var16 * 0.9F, var17 * 0.9F, var18 * 0.9F, 0.8F);
                    int var34;

                    if (var28 > -1)
                    {
                        var3.setNormal(-1.0F, 0.0F, 0.0F);

                        for (var34 = 0; var34 < var24; ++var34)
                        {
                            var3.addVertexWithUV((double)(var32 + (float)var34 + 0.0F), (double)(var12 + 0.0F), (double)(var33 + (float)var24), (double)((var30 + (float)var34 + 0.5F) * var21 + var19), (double)((var31 + (float)var24) * var21 + var20));
                            var3.addVertexWithUV((double)(var32 + (float)var34 + 0.0F), (double)(var12 + var5), (double)(var33 + (float)var24), (double)((var30 + (float)var34 + 0.5F) * var21 + var19), (double)((var31 + (float)var24) * var21 + var20));
                            var3.addVertexWithUV((double)(var32 + (float)var34 + 0.0F), (double)(var12 + var5), (double)(var33 + 0.0F), (double)((var30 + (float)var34 + 0.5F) * var21 + var19), (double)((var31 + 0.0F) * var21 + var20));
                            var3.addVertexWithUV((double)(var32 + (float)var34 + 0.0F), (double)(var12 + 0.0F), (double)(var33 + 0.0F), (double)((var30 + (float)var34 + 0.5F) * var21 + var19), (double)((var31 + 0.0F) * var21 + var20));
                        }
                    }

                    if (var28 <= 1)
                    {
                        var3.setNormal(1.0F, 0.0F, 0.0F);

                        for (var34 = 0; var34 < var24; ++var34)
                        {
                            var3.addVertexWithUV((double)(var32 + (float)var34 + 1.0F - var26), (double)(var12 + 0.0F), (double)(var33 + (float)var24), (double)((var30 + (float)var34 + 0.5F) * var21 + var19), (double)((var31 + (float)var24) * var21 + var20));
                            var3.addVertexWithUV((double)(var32 + (float)var34 + 1.0F - var26), (double)(var12 + var5), (double)(var33 + (float)var24), (double)((var30 + (float)var34 + 0.5F) * var21 + var19), (double)((var31 + (float)var24) * var21 + var20));
                            var3.addVertexWithUV((double)(var32 + (float)var34 + 1.0F - var26), (double)(var12 + var5), (double)(var33 + 0.0F), (double)((var30 + (float)var34 + 0.5F) * var21 + var19), (double)((var31 + 0.0F) * var21 + var20));
                            var3.addVertexWithUV((double)(var32 + (float)var34 + 1.0F - var26), (double)(var12 + 0.0F), (double)(var33 + 0.0F), (double)((var30 + (float)var34 + 0.5F) * var21 + var19), (double)((var31 + 0.0F) * var21 + var20));
                        }
                    }

                    var3.setColorRGBA_F(var16 * 0.8F, var17 * 0.8F, var18 * 0.8F, 0.8F);

                    if (var29 > -1)
                    {
                        var3.setNormal(0.0F, 0.0F, -1.0F);

                        for (var34 = 0; var34 < var24; ++var34)
                        {
                            var3.addVertexWithUV((double)(var32 + 0.0F), (double)(var12 + var5), (double)(var33 + (float)var34 + 0.0F), (double)((var30 + 0.0F) * var21 + var19), (double)((var31 + (float)var34 + 0.5F) * var21 + var20));
                            var3.addVertexWithUV((double)(var32 + (float)var24), (double)(var12 + var5), (double)(var33 + (float)var34 + 0.0F), (double)((var30 + (float)var24) * var21 + var19), (double)((var31 + (float)var34 + 0.5F) * var21 + var20));
                            var3.addVertexWithUV((double)(var32 + (float)var24), (double)(var12 + 0.0F), (double)(var33 + (float)var34 + 0.0F), (double)((var30 + (float)var24) * var21 + var19), (double)((var31 + (float)var34 + 0.5F) * var21 + var20));
                            var3.addVertexWithUV((double)(var32 + 0.0F), (double)(var12 + 0.0F), (double)(var33 + (float)var34 + 0.0F), (double)((var30 + 0.0F) * var21 + var19), (double)((var31 + (float)var34 + 0.5F) * var21 + var20));
                        }
                    }

                    if (var29 <= 1)
                    {
                        var3.setNormal(0.0F, 0.0F, 1.0F);

                        for (var34 = 0; var34 < var24; ++var34)
                        {
                            var3.addVertexWithUV((double)(var32 + 0.0F), (double)(var12 + var5), (double)(var33 + (float)var34 + 1.0F - var26), (double)((var30 + 0.0F) * var21 + var19), (double)((var31 + (float)var34 + 0.5F) * var21 + var20));
                            var3.addVertexWithUV((double)(var32 + (float)var24), (double)(var12 + var5), (double)(var33 + (float)var34 + 1.0F - var26), (double)((var30 + (float)var24) * var21 + var19), (double)((var31 + (float)var34 + 0.5F) * var21 + var20));
                            var3.addVertexWithUV((double)(var32 + (float)var24), (double)(var12 + 0.0F), (double)(var33 + (float)var34 + 1.0F - var26), (double)((var30 + (float)var24) * var21 + var19), (double)((var31 + (float)var34 + 0.5F) * var21 + var20));
                            var3.addVertexWithUV((double)(var32 + 0.0F), (double)(var12 + 0.0F), (double)(var33 + (float)var34 + 1.0F - var26), (double)((var30 + 0.0F) * var21 + var19), (double)((var31 + (float)var34 + 0.5F) * var21 + var20));
                        }
                    }

                    var3.draw();
                }
            }
        }

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_CULL_FACE);
    }

    /**
     * Updates some of the renderers sorted by distance from the player
     */
    public boolean updateRenderers(EntityLiving par1EntityLiving, boolean par2)
    {
        boolean var3 = false;

        if (var3)
        {
            Collections.sort(this.worldRenderersToUpdate, new RenderSorter(par1EntityLiving));
            int var17 = this.worldRenderersToUpdate.size() - 1;
            int var18 = this.worldRenderersToUpdate.size();

            for (int var19 = 0; var19 < var18; ++var19)
            {
                WorldRenderer var20 = (WorldRenderer)this.worldRenderersToUpdate.get(var17 - var19);

                if (!par2)
                {
                    if (var20.distanceToEntitySquared(par1EntityLiving) > 256.0F)
                    {
                        if (var20.isInFrustum)
                        {
                            if (var19 >= 30)
                            {
                                return false;
                            }
                        }
                        else if (var19 >= 1)
                        {
                            return false;
                        }
                    }
                }
                else if (!var20.isInFrustum)
                {
                    continue;
                }

                var20.updateRenderer();
                this.worldRenderersToUpdate.remove(var20);
                var20.needsUpdate = false;
            }

            return this.worldRenderersToUpdate.size() == 0;
        }
        else
        {
            byte var4 = 2;
            RenderSorter var5 = new RenderSorter(par1EntityLiving);
            WorldRenderer[] var6 = new WorldRenderer[var4];
            ArrayList var7 = null;
            int var8 = this.worldRenderersToUpdate.size();
            int var9 = 0;
            int var10;
            WorldRenderer var11;
            int var12;
            int var13;
            label168:

            for (var10 = 0; var10 < var8; ++var10)
            {
                var11 = (WorldRenderer)this.worldRenderersToUpdate.get(var10);

                if (!par2)
                {
                    if (var11.distanceToEntitySquared(par1EntityLiving) > 256.0F)
                    {
                        for (var12 = 0; var12 < var4 && (var6[var12] == null || var5.doCompare(var6[var12], var11) <= 0); ++var12)
                        {
                            ;
                        }

                        --var12;

                        if (var12 > 0)
                        {
                            var13 = var12;

                            while (true)
                            {
                                --var13;

                                if (var13 == 0)
                                {
                                    var6[var12] = var11;
                                    continue label168;
                                }

                                var6[var13 - 1] = var6[var13];
                            }
                        }

                        continue;
                    }
                }
                else if (!var11.isInFrustum)
                {
                    continue;
                }

                if (var7 == null)
                {
                    var7 = new ArrayList();
                }

                ++var9;
                var7.add(var11);
                this.worldRenderersToUpdate.set(var10, (Object)null);
            }

            if (var7 != null)
            {
                if (var7.size() > 1)
                {
                    Collections.sort(var7, var5);
                }

                for (var10 = var7.size() - 1; var10 >= 0; --var10)
                {
                    var11 = (WorldRenderer)var7.get(var10);
                    var11.updateRenderer();
                    var11.needsUpdate = false;
                }
            }

            var10 = 0;
            int var21;

            for (var21 = var4 - 1; var21 >= 0; --var21)
            {
                WorldRenderer var22 = var6[var21];

                if (var22 != null)
                {
                    if (!var22.isInFrustum && var21 != var4 - 1)
                    {
                        var6[var21] = null;
                        var6[0] = null;
                        break;
                    }

                    var6[var21].updateRenderer();
                    var6[var21].needsUpdate = false;
                    ++var10;
                }
            }

            var21 = 0;
            var12 = 0;

            for (var13 = this.worldRenderersToUpdate.size(); var21 != var13; ++var21)
            {
                WorldRenderer var14 = (WorldRenderer)this.worldRenderersToUpdate.get(var21);

                if (var14 != null)
                {
                    boolean var15 = false;

                    for (int var16 = 0; var16 < var4 && !var15; ++var16)
                    {
                        if (var14 == var6[var16])
                        {
                            var15 = true;
                        }
                    }

                    if (!var15)
                    {
                        if (var12 != var21)
                        {
                            this.worldRenderersToUpdate.set(var12, var14);
                        }

                        ++var12;
                    }
                }
            }

            while (true)
            {
                --var21;

                if (var21 < var12)
                {
                    return var8 == var9 + var10;
                }

                this.worldRenderersToUpdate.remove(var21);
            }
        }
    }

    public void drawBlockBreaking(EntityPlayer par1EntityPlayer, MovingObjectPosition par2MovingObjectPosition, int par3, ItemStack par4ItemStack, float par5)
    {
        Tessellator var6 = Tessellator.instance;
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, (MathHelper.sin((float)System.currentTimeMillis() / 100.0F) * 0.2F + 0.4F) * 0.5F);
        int var8;

        if (par3 == 0)
        {
            if (this.damagePartialTime > 0.0F)
            {
                GL11.glBlendFunc(GL11.GL_DST_COLOR, GL11.GL_SRC_COLOR);
                int var7 = this.renderEngine.getTexture("/terrain.png");
                GL11.glBindTexture(GL11.GL_TEXTURE_2D, var7);
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.5F);
                GL11.glPushMatrix();
                var8 = this.worldObj.getBlockId(par2MovingObjectPosition.blockX, par2MovingObjectPosition.blockY, par2MovingObjectPosition.blockZ);
                Block var9 = var8 > 0 ? Block.blocksList[var8] : null;
                GL11.glDisable(GL11.GL_ALPHA_TEST);
                GL11.glPolygonOffset(-3.0F, -3.0F);
                GL11.glEnable(GL11.GL_POLYGON_OFFSET_FILL);
                double var10 = par1EntityPlayer.lastTickPosX + (par1EntityPlayer.posX - par1EntityPlayer.lastTickPosX) * (double)par5;
                double var12 = par1EntityPlayer.lastTickPosY + (par1EntityPlayer.posY - par1EntityPlayer.lastTickPosY) * (double)par5;
                double var14 = par1EntityPlayer.lastTickPosZ + (par1EntityPlayer.posZ - par1EntityPlayer.lastTickPosZ) * (double)par5;

                if (var9 == null)
                {
                    var9 = Block.stone;
                }

                GL11.glEnable(GL11.GL_ALPHA_TEST);
                var6.startDrawingQuads();
                var6.setTranslationD(-var10, -var12, -var14);
                var6.disableColor();
                this.globalRenderBlocks.renderBlockUsingTexture(var9, par2MovingObjectPosition.blockX, par2MovingObjectPosition.blockY, par2MovingObjectPosition.blockZ, 240 + (int)(this.damagePartialTime * 10.0F));
                var6.draw();
                var6.setTranslationD(0.0D, 0.0D, 0.0D);
                GL11.glDisable(GL11.GL_ALPHA_TEST);
                GL11.glPolygonOffset(0.0F, 0.0F);
                GL11.glDisable(GL11.GL_POLYGON_OFFSET_FILL);
                GL11.glEnable(GL11.GL_ALPHA_TEST);
                GL11.glDepthMask(true);
                GL11.glPopMatrix();
            }
        }
        else if (par4ItemStack != null)
        {
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            float var16 = MathHelper.sin((float)System.currentTimeMillis() / 100.0F) * 0.2F + 0.8F;
            GL11.glColor4f(var16, var16, var16, MathHelper.sin((float)System.currentTimeMillis() / 200.0F) * 0.2F + 0.5F);
            var8 = this.renderEngine.getTexture("/terrain.png");
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, var8);
            int var17 = par2MovingObjectPosition.blockX;
            int var18 = par2MovingObjectPosition.blockY;
            int var11 = par2MovingObjectPosition.blockZ;

            if (par2MovingObjectPosition.sideHit == 0)
            {
                --var18;
            }

            if (par2MovingObjectPosition.sideHit == 1)
            {
                ++var18;
            }

            if (par2MovingObjectPosition.sideHit == 2)
            {
                --var11;
            }

            if (par2MovingObjectPosition.sideHit == 3)
            {
                ++var11;
            }

            if (par2MovingObjectPosition.sideHit == 4)
            {
                --var17;
            }

            if (par2MovingObjectPosition.sideHit == 5)
            {
                ++var17;
            }
        }

        GL11.glDisable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
    }

    /**
     * Draws the selection box for the player. Args: entityPlayer, rayTraceHit, i, itemStack, partialTickTime
     */
    public void drawSelectionBox(EntityPlayer par1EntityPlayer, MovingObjectPosition par2MovingObjectPosition, int par3, ItemStack par4ItemStack, float par5)
    {
        if (par3 == 0 && par2MovingObjectPosition.typeOfHit == EnumMovingObjectType.TILE)
        {
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glColor4f(0.0F, 0.0F, 0.0F, 0.4F);
            GL11.glLineWidth(2.0F);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glDepthMask(false);
            float var6 = 0.002F;
            int var7 = this.worldObj.getBlockId(par2MovingObjectPosition.blockX, par2MovingObjectPosition.blockY, par2MovingObjectPosition.blockZ);

            if (var7 > 0)
            {
                Block.blocksList[var7].setBlockBoundsBasedOnState(this.worldObj, par2MovingObjectPosition.blockX, par2MovingObjectPosition.blockY, par2MovingObjectPosition.blockZ);
                double var8 = par1EntityPlayer.lastTickPosX + (par1EntityPlayer.posX - par1EntityPlayer.lastTickPosX) * (double)par5;
                double var10 = par1EntityPlayer.lastTickPosY + (par1EntityPlayer.posY - par1EntityPlayer.lastTickPosY) * (double)par5;
                double var12 = par1EntityPlayer.lastTickPosZ + (par1EntityPlayer.posZ - par1EntityPlayer.lastTickPosZ) * (double)par5;
                this.drawOutlinedBoundingBox(Block.blocksList[var7].getSelectedBoundingBoxFromPool(this.worldObj, par2MovingObjectPosition.blockX, par2MovingObjectPosition.blockY, par2MovingObjectPosition.blockZ).expand((double)var6, (double)var6, (double)var6).getOffsetBoundingBox(-var8, -var10, -var12));
            }

            GL11.glDepthMask(true);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glDisable(GL11.GL_BLEND);
        }
    }

    /**
     * Draws lines for the edges of the bounding box.
     */
    private void drawOutlinedBoundingBox(AxisAlignedBB par1AxisAlignedBB)
    {
        Tessellator var2 = Tessellator.instance;
        var2.startDrawing(3);
        var2.addVertex(par1AxisAlignedBB.minX, par1AxisAlignedBB.minY, par1AxisAlignedBB.minZ);
        var2.addVertex(par1AxisAlignedBB.maxX, par1AxisAlignedBB.minY, par1AxisAlignedBB.minZ);
        var2.addVertex(par1AxisAlignedBB.maxX, par1AxisAlignedBB.minY, par1AxisAlignedBB.maxZ);
        var2.addVertex(par1AxisAlignedBB.minX, par1AxisAlignedBB.minY, par1AxisAlignedBB.maxZ);
        var2.addVertex(par1AxisAlignedBB.minX, par1AxisAlignedBB.minY, par1AxisAlignedBB.minZ);
        var2.draw();
        var2.startDrawing(3);
        var2.addVertex(par1AxisAlignedBB.minX, par1AxisAlignedBB.maxY, par1AxisAlignedBB.minZ);
        var2.addVertex(par1AxisAlignedBB.maxX, par1AxisAlignedBB.maxY, par1AxisAlignedBB.minZ);
        var2.addVertex(par1AxisAlignedBB.maxX, par1AxisAlignedBB.maxY, par1AxisAlignedBB.maxZ);
        var2.addVertex(par1AxisAlignedBB.minX, par1AxisAlignedBB.maxY, par1AxisAlignedBB.maxZ);
        var2.addVertex(par1AxisAlignedBB.minX, par1AxisAlignedBB.maxY, par1AxisAlignedBB.minZ);
        var2.draw();
        var2.startDrawing(1);
        var2.addVertex(par1AxisAlignedBB.minX, par1AxisAlignedBB.minY, par1AxisAlignedBB.minZ);
        var2.addVertex(par1AxisAlignedBB.minX, par1AxisAlignedBB.maxY, par1AxisAlignedBB.minZ);
        var2.addVertex(par1AxisAlignedBB.maxX, par1AxisAlignedBB.minY, par1AxisAlignedBB.minZ);
        var2.addVertex(par1AxisAlignedBB.maxX, par1AxisAlignedBB.maxY, par1AxisAlignedBB.minZ);
        var2.addVertex(par1AxisAlignedBB.maxX, par1AxisAlignedBB.minY, par1AxisAlignedBB.maxZ);
        var2.addVertex(par1AxisAlignedBB.maxX, par1AxisAlignedBB.maxY, par1AxisAlignedBB.maxZ);
        var2.addVertex(par1AxisAlignedBB.minX, par1AxisAlignedBB.minY, par1AxisAlignedBB.maxZ);
        var2.addVertex(par1AxisAlignedBB.minX, par1AxisAlignedBB.maxY, par1AxisAlignedBB.maxZ);
        var2.draw();
    }

    /**
     * Marks the blocks in the given range for update
     */
    public void markBlocksForUpdate(int par1, int par2, int par3, int par4, int par5, int par6)
    {
        int var7 = MathHelper.bucketInt(par1, 16);
        int var8 = MathHelper.bucketInt(par2, 16);
        int var9 = MathHelper.bucketInt(par3, 16);
        int var10 = MathHelper.bucketInt(par4, 16);
        int var11 = MathHelper.bucketInt(par5, 16);
        int var12 = MathHelper.bucketInt(par6, 16);

        for (int var13 = var7; var13 <= var10; ++var13)
        {
            int var14 = var13 % this.renderChunksWide;

            if (var14 < 0)
            {
                var14 += this.renderChunksWide;
            }

            for (int var15 = var8; var15 <= var11; ++var15)
            {
                int var16 = var15 % this.renderChunksTall;

                if (var16 < 0)
                {
                    var16 += this.renderChunksTall;
                }

                for (int var17 = var9; var17 <= var12; ++var17)
                {
                    int var18 = var17 % this.renderChunksDeep;

                    if (var18 < 0)
                    {
                        var18 += this.renderChunksDeep;
                    }

                    int var19 = (var18 * this.renderChunksTall + var16) * this.renderChunksWide + var14;
                    WorldRenderer var20 = this.worldRenderers[var19];

                    if (!var20.needsUpdate)
                    {
                        this.worldRenderersToUpdate.add(var20);
                        var20.markDirty();
                    }
                }
            }
        }
    }

    /**
     * Will mark the block and neighbors that their renderers need an update (could be all the same renderer
     * potentially) Args: x, y, z
     */
    public void markBlockNeedsUpdate(int par1, int par2, int par3)
    {
        this.markBlocksForUpdate(par1 - 1, par2 - 1, par3 - 1, par1 + 1, par2 + 1, par3 + 1);
    }

    public void func_48180_b(int par1, int par2, int par3)
    {
        this.markBlocksForUpdate(par1 - 1, par2 - 1, par3 - 1, par1 + 1, par2 + 1, par3 + 1);
    }

    /**
     * Called across all registered IWorldAccess instances when a block range is invalidated. Args: minX, minY, minZ,
     * maxX, maxY, maxZ
     */
    public void markBlockRangeNeedsUpdate(int par1, int par2, int par3, int par4, int par5, int par6)
    {
        this.markBlocksForUpdate(par1 - 1, par2 - 1, par3 - 1, par4 + 1, par5 + 1, par6 + 1);
    }

    /**
     * Checks all renderers that previously weren't in the frustrum and 1/16th of those that previously were in the
     * frustrum for frustrum clipping Args: frustrum, partialTickTime
     */
    public void clipRenderersByFrustrum(ICamera par1ICamera, float par2)
    {
        for (int var3 = 0; var3 < this.worldRenderers.length; ++var3)
        {
            if (!this.worldRenderers[var3].skipAllRenderPasses() && (!this.worldRenderers[var3].isInFrustum || (var3 + this.frustrumCheckOffset & 15) == 0))
            {
                this.worldRenderers[var3].updateInFrustrum(par1ICamera);
            }
        }

        ++this.frustrumCheckOffset;
    }

    /**
     * Plays the specified record. Arg: recordName, x, y, z
     */
    public void playRecord(String par1Str, int par2, int par3, int par4)
    {
        if (par1Str != null)
        {
            this.mc.ingameGUI.setRecordPlayingMessage("C418 - " + par1Str);
        }

        this.mc.sndManager.playStreaming(par1Str, (float)par2, (float)par3, (float)par4, 1.0F, 1.0F);
    }

    /**
     * Plays the specified sound. Arg: x, y, z, soundName, unknown1, unknown2
     */
    public void playSound(String par1Str, double par2, double par4, double par6, float par8, float par9)
    {
        float var10 = 16.0F;

        if (par8 > 1.0F)
        {
            var10 *= par8;
        }

        if (this.mc.renderViewEntity.getDistanceSq(par2, par4, par6) < (double)(var10 * var10))
        {
            this.mc.sndManager.playSound(par1Str, (float)par2, (float)par4, (float)par6, par8, par9);
        }
    }

    /**
     * Spawns a particle. Arg: particleType, x, y, z, velX, velY, velZ
     */
    public void spawnParticle(String par1Str, double par2, double par4, double par6, double par8, double par10, double par12)
    {
        this.func_40193_b(par1Str, par2, par4, par6, par8, par10, par12);
    }

    public EntityFX func_40193_b(String par1Str, double par2, double par4, double par6, double par8, double par10, double par12)
    {
        if (this.mc != null && this.mc.renderViewEntity != null && this.mc.effectRenderer != null)
        {
            int var14 = this.mc.gameSettings.particleSetting;

            if (var14 == 1 && this.worldObj.rand.nextInt(3) == 0)
            {
                var14 = 2;
            }

            double var15 = this.mc.renderViewEntity.posX - par2;
            double var17 = this.mc.renderViewEntity.posY - par4;
            double var19 = this.mc.renderViewEntity.posZ - par6;
            EntityFX var21 = null;

            if (par1Str.equals("hugeexplosion"))
            {
                this.mc.effectRenderer.addEffect(var21 = new EntityHugeExplodeFX(this.worldObj, par2, par4, par6, par8, par10, par12));
            }
            else if (par1Str.equals("largeexplode"))
            {
                this.mc.effectRenderer.addEffect(var21 = new EntityLargeExplodeFX(this.renderEngine, this.worldObj, par2, par4, par6, par8, par10, par12));
            }

            if (var21 != null)
            {
                return (EntityFX)var21;
            }
            else
            {
                double var22 = 16.0D;

                if (var15 * var15 + var17 * var17 + var19 * var19 > var22 * var22)
                {
                    return null;
                }
                else if (var14 > 1)
                {
                    return null;
                }
                else
                {
                    if (par1Str.equals("bubble"))
                    {
                        var21 = new EntityBubbleFX(this.worldObj, par2, par4, par6, par8, par10, par12);
                    }
                    else if (par1Str.equals("suspended"))
                    {
                        var21 = new EntitySuspendFX(this.worldObj, par2, par4, par6, par8, par10, par12);
                    }
                    else if (par1Str.equals("depthsuspend"))
                    {
                        var21 = new EntityAuraFX(this.worldObj, par2, par4, par6, par8, par10, par12);
                    }
                    else if (par1Str.equals("townaura"))
                    {
                        var21 = new EntityAuraFX(this.worldObj, par2, par4, par6, par8, par10, par12);
                    }
                    else if (par1Str.equals("crit"))
                    {
                        var21 = new EntityCritFX(this.worldObj, par2, par4, par6, par8, par10, par12);
                    }
                    else if (par1Str.equals("magicCrit"))
                    {
                        var21 = new EntityCritFX(this.worldObj, par2, par4, par6, par8, par10, par12);
                        ((EntityFX)var21).func_40097_b(((EntityFX)var21).func_40098_n() * 0.3F, ((EntityFX)var21).func_40101_o() * 0.8F, ((EntityFX)var21).func_40102_p());
                        ((EntityFX)var21).setParticleTextureIndex(((EntityFX)var21).getParticleTextureIndex() + 1);
                    }
                    else if (par1Str.equals("smoke"))
                    {
                        var21 = new EntitySmokeFX(this.worldObj, par2, par4, par6, par8, par10, par12);
                    }
                    else if (par1Str.equals("mobSpell"))
                    {
                        var21 = new EntitySpellParticleFX(this.worldObj, par2, par4, par6, 0.0D, 0.0D, 0.0D);
                        ((EntityFX)var21).func_40097_b((float)par8, (float)par10, (float)par12);
                    }
                    else if (par1Str.equals("spell"))
                    {
                        var21 = new EntitySpellParticleFX(this.worldObj, par2, par4, par6, par8, par10, par12);
                    }
                    else if (par1Str.equals("instantSpell"))
                    {
                        var21 = new EntitySpellParticleFX(this.worldObj, par2, par4, par6, par8, par10, par12);
                        ((EntitySpellParticleFX)var21).func_40110_b(144);
                    }
                    else if (par1Str.equals("note"))
                    {
                        var21 = new EntityNoteFX(this.worldObj, par2, par4, par6, par8, par10, par12);
                    }
                    else if (par1Str.equals("portal"))
                    {
                        var21 = new EntityPortalFX(this.worldObj, par2, par4, par6, par8, par10, par12);
                    }
                    else if (par1Str.equals("enchantmenttable"))
                    {
                        var21 = new EntityEnchantmentTableParticleFX(this.worldObj, par2, par4, par6, par8, par10, par12);
                    }
                    else if (par1Str.equals("explode"))
                    {
                        var21 = new EntityExplodeFX(this.worldObj, par2, par4, par6, par8, par10, par12);
                    }
                    else if (par1Str.equals("flame"))
                    {
                        var21 = new EntityFlameFX(this.worldObj, par2, par4, par6, par8, par10, par12);
                    }
                    else if (par1Str.equals("lava"))
                    {
                        var21 = new EntityLavaFX(this.worldObj, par2, par4, par6);
                    }
                    else if (par1Str.equals("footstep"))
                    {
                        var21 = new EntityFootStepFX(this.renderEngine, this.worldObj, par2, par4, par6);
                    }
                    else if (par1Str.equals("splash"))
                    {
                        var21 = new EntitySplashFX(this.worldObj, par2, par4, par6, par8, par10, par12);
                    }
                    else if (par1Str.equals("largesmoke"))
                    {
                        var21 = new EntitySmokeFX(this.worldObj, par2, par4, par6, par8, par10, par12, 2.5F);
                    }
                    else if (par1Str.equals("cloud"))
                    {
                        var21 = new EntityCloudFX(this.worldObj, par2, par4, par6, par8, par10, par12);
                    }
                    else if (par1Str.equals("reddust"))
                    {
                        var21 = new EntityReddustFX(this.worldObj, par2, par4, par6, (float)par8, (float)par10, (float)par12);
                    }
                    else if (par1Str.equals("snowballpoof"))
                    {
                        var21 = new EntityBreakingFX(this.worldObj, par2, par4, par6, Item.snowball);
                    }
                    else if (par1Str.equals("dripWater"))
                    {
                        var21 = new EntityDropParticleFX(this.worldObj, par2, par4, par6, Material.water);
                    }
                    else if (par1Str.equals("dripLava"))
                    {
                        var21 = new EntityDropParticleFX(this.worldObj, par2, par4, par6, Material.lava);
                    }
                    else if (par1Str.equals("snowshovel"))
                    {
                        var21 = new EntitySnowShovelFX(this.worldObj, par2, par4, par6, par8, par10, par12);
                    }
                    else if (par1Str.equals("slime"))
                    {
                        var21 = new EntityBreakingFX(this.worldObj, par2, par4, par6, Item.slimeBall);
                    }
                    else if (par1Str.equals("heart"))
                    {
                        var21 = new EntityHeartFX(this.worldObj, par2, par4, par6, par8, par10, par12);
                    }
                    else
                    {
                        int var24;

                        if (par1Str.startsWith("iconcrack_"))
                        {
                            var24 = Integer.parseInt(par1Str.substring(par1Str.indexOf("_") + 1));
                            var21 = new EntityBreakingFX(this.worldObj, par2, par4, par6, par8, par10, par12, Item.itemsList[var24]);
                        }
                        else if (par1Str.startsWith("tilecrack_"))
                        {
                            var24 = Integer.parseInt(par1Str.substring(par1Str.indexOf("_") + 1));
                            var21 = new EntityDiggingFX(this.worldObj, par2, par4, par6, par8, par10, par12, Block.blocksList[var24], 0, 0);
                        }
                    }

                    if (var21 != null)
                    {
                        this.mc.effectRenderer.addEffect((EntityFX)var21);
                    }

                    return (EntityFX)var21;
                }
            }
        }
        else
        {
            return null;
        }
    }

    /**
     * Start the skin for this entity downloading, if necessary, and increment its reference counter
     */
    public void obtainEntitySkin(Entity par1Entity)
    {
        par1Entity.updateCloak();

        if (par1Entity.skinUrl != null)
        {
            this.renderEngine.obtainImageData(par1Entity.skinUrl, new ImageBufferDownload());
        }

        if (par1Entity.cloakUrl != null)
        {
            this.renderEngine.obtainImageData(par1Entity.cloakUrl, new ImageBufferDownload());
        }
    }

    /**
     * Decrement the reference counter for this entity's skin image data
     */
    public void releaseEntitySkin(Entity par1Entity)
    {
        if (par1Entity.skinUrl != null)
        {
            this.renderEngine.releaseImageData(par1Entity.skinUrl);
        }

        if (par1Entity.cloakUrl != null)
        {
            this.renderEngine.releaseImageData(par1Entity.cloakUrl);
        }
    }

    /**
     * In all implementations, this method does nothing.
     */
    public void doNothingWithTileEntity(int par1, int par2, int par3, TileEntity par4TileEntity) {}

    public void func_28137_f()
    {
        GLAllocation.deleteDisplayLists(this.glRenderListBase);
    }

    /**
     * Plays a pre-canned sound effect along with potentially auxiliary data-driven one-shot behaviour (particles, etc).
     */
    public void playAuxSFX(EntityPlayer par1EntityPlayer, int par2, int par3, int par4, int par5, int par6)
    {
        Random var7 = this.worldObj.rand;
        int var8;
        double var10;
        double var12;
        String var14;
        int var15;
        double var21;
        double var23;
        double var25;
        double var27;
        double var29;
        double var33;

        switch (par2)
        {
            case 1000:
                this.worldObj.playSoundEffect((double)par3, (double)par4, (double)par5, "random.click", 1.0F, 1.0F);
                break;

            case 1001:
                this.worldObj.playSoundEffect((double)par3, (double)par4, (double)par5, "random.click", 1.0F, 1.2F);
                break;

            case 1002:
                this.worldObj.playSoundEffect((double)par3, (double)par4, (double)par5, "random.bow", 1.0F, 1.2F);
                break;

            case 1003:
                if (Math.random() < 0.5D)
                {
                    this.worldObj.playSoundEffect((double)par3 + 0.5D, (double)par4 + 0.5D, (double)par5 + 0.5D, "random.door_open", 1.0F, this.worldObj.rand.nextFloat() * 0.1F + 0.9F);
                }
                else
                {
                    this.worldObj.playSoundEffect((double)par3 + 0.5D, (double)par4 + 0.5D, (double)par5 + 0.5D, "random.door_close", 1.0F, this.worldObj.rand.nextFloat() * 0.1F + 0.9F);
                }

                break;

            case 1004:
                this.worldObj.playSoundEffect((double)((float)par3 + 0.5F), (double)((float)par4 + 0.5F), (double)((float)par5 + 0.5F), "random.fizz", 0.5F, 2.6F + (var7.nextFloat() - var7.nextFloat()) * 0.8F);
                break;

            case 1005:
                if (Item.itemsList[par6] instanceof ItemRecord)
                {
                    this.worldObj.playRecord(((ItemRecord)Item.itemsList[par6]).recordName, par3, par4, par5);
                }
                else
                {
                    this.worldObj.playRecord((String)null, par3, par4, par5);
                }

                break;

            case 1007:
                this.worldObj.playSoundEffect((double)par3 + 0.5D, (double)par4 + 0.5D, (double)par5 + 0.5D, "mob.ghast.charge", 10.0F, (var7.nextFloat() - var7.nextFloat()) * 0.2F + 1.0F);
                break;

            case 1008:
                this.worldObj.playSoundEffect((double)par3 + 0.5D, (double)par4 + 0.5D, (double)par5 + 0.5D, "mob.ghast.fireball", 10.0F, (var7.nextFloat() - var7.nextFloat()) * 0.2F + 1.0F);
                break;

            case 1010:
                this.worldObj.playSoundEffect((double)par3 + 0.5D, (double)par4 + 0.5D, (double)par5 + 0.5D, "mob.zombie.wood", 2.0F, (var7.nextFloat() - var7.nextFloat()) * 0.2F + 1.0F);
                break;

            case 1011:
                this.worldObj.playSoundEffect((double)par3 + 0.5D, (double)par4 + 0.5D, (double)par5 + 0.5D, "mob.zombie.metal", 2.0F, (var7.nextFloat() - var7.nextFloat()) * 0.2F + 1.0F);
                break;

            case 1012:
                this.worldObj.playSoundEffect((double)par3 + 0.5D, (double)par4 + 0.5D, (double)par5 + 0.5D, "mob.zombie.woodbreak", 2.0F, (var7.nextFloat() - var7.nextFloat()) * 0.2F + 1.0F);
                break;

            case 2000:
                var8 = par6 % 3 - 1;
                int var35 = par6 / 3 % 3 - 1;
                var10 = (double)par3 + (double)var8 * 0.6D + 0.5D;
                var12 = (double)par4 + 0.5D;
                double var36 = (double)par5 + (double)var35 * 0.6D + 0.5D;

                for (int var37 = 0; var37 < 10; ++var37)
                {
                    double var39 = var7.nextDouble() * 0.2D + 0.01D;
                    double var40 = var10 + (double)var8 * 0.01D + (var7.nextDouble() - 0.5D) * (double)var35 * 0.5D;
                    var21 = var12 + (var7.nextDouble() - 0.5D) * 0.5D;
                    var23 = var36 + (double)var35 * 0.01D + (var7.nextDouble() - 0.5D) * (double)var8 * 0.5D;
                    var25 = (double)var8 * var39 + var7.nextGaussian() * 0.01D;
                    var27 = -0.03D + var7.nextGaussian() * 0.01D;
                    var29 = (double)var35 * var39 + var7.nextGaussian() * 0.01D;
                    this.spawnParticle("smoke", var40, var21, var23, var25, var27, var29);
                }

                return;

            case 2001:
                var8 = par6 & 4095;

                if (var8 > 0)
                {
                    Block var34 = Block.blocksList[var8];
                    this.mc.sndManager.playSound(var34.stepSound.getBreakSound(), (float)par3 + 0.5F, (float)par4 + 0.5F, (float)par5 + 0.5F, (var34.stepSound.getVolume() + 1.0F) / 2.0F, var34.stepSound.getPitch() * 0.8F);
                }

                this.mc.effectRenderer.addBlockDestroyEffects(par3, par4, par5, par6 & 4095, par6 >> 12 & 255);
                break;

            case 2002:
                var33 = (double)par3;
                var10 = (double)par4;
                var12 = (double)par5;
                var14 = "iconcrack_" + Item.potion.shiftedIndex;

                for (var15 = 0; var15 < 8; ++var15)
                {
                    this.spawnParticle(var14, var33, var10, var12, var7.nextGaussian() * 0.15D, var7.nextDouble() * 0.2D, var7.nextGaussian() * 0.15D);
                }

                var15 = Item.potion.getColorFromDamage(par6, 0);
                float var16 = (float)(var15 >> 16 & 255) / 255.0F;
                float var17 = (float)(var15 >> 8 & 255) / 255.0F;
                float var18 = (float)(var15 >> 0 & 255) / 255.0F;
                String var19 = "spell";

                if (Item.potion.isEffectInstant(par6))
                {
                    var19 = "instantSpell";
                }

                for (int var20 = 0; var20 < 100; ++var20)
                {
                    var21 = var7.nextDouble() * 4.0D;
                    var23 = var7.nextDouble() * Math.PI * 2.0D;
                    var25 = Math.cos(var23) * var21;
                    var27 = 0.01D + var7.nextDouble() * 0.5D;
                    var29 = Math.sin(var23) * var21;
                    EntityFX var31 = this.func_40193_b(var19, var33 + var25 * 0.1D, var10 + 0.3D, var12 + var29 * 0.1D, var25, var27, var29);

                    if (var31 != null)
                    {
                        float var32 = 0.75F + var7.nextFloat() * 0.25F;
                        var31.func_40097_b(var16 * var32, var17 * var32, var18 * var32);
                        var31.multiplyVelocity((float)var21);
                    }
                }

                this.worldObj.playSoundEffect((double)par3 + 0.5D, (double)par4 + 0.5D, (double)par5 + 0.5D, "random.glass", 1.0F, this.worldObj.rand.nextFloat() * 0.1F + 0.9F);
                break;

            case 2003:
                var33 = (double)par3 + 0.5D;
                var10 = (double)par4;
                var12 = (double)par5 + 0.5D;
                var14 = "iconcrack_" + Item.eyeOfEnder.shiftedIndex;

                for (var15 = 0; var15 < 8; ++var15)
                {
                    this.spawnParticle(var14, var33, var10, var12, var7.nextGaussian() * 0.15D, var7.nextDouble() * 0.2D, var7.nextGaussian() * 0.15D);
                }

                for (double var38 = 0.0D; var38 < (Math.PI * 2D); var38 += 0.15707963267948966D)
                {
                    this.spawnParticle("portal", var33 + Math.cos(var38) * 5.0D, var10 - 0.4D, var12 + Math.sin(var38) * 5.0D, Math.cos(var38) * -5.0D, 0.0D, Math.sin(var38) * -5.0D);
                    this.spawnParticle("portal", var33 + Math.cos(var38) * 5.0D, var10 - 0.4D, var12 + Math.sin(var38) * 5.0D, Math.cos(var38) * -7.0D, 0.0D, Math.sin(var38) * -7.0D);
                }

                return;

            case 2004:
                for (var8 = 0; var8 < 20; ++var8)
                {
                    double var9 = (double)par3 + 0.5D + ((double)this.worldObj.rand.nextFloat() - 0.5D) * 2.0D;
                    double var11 = (double)par4 + 0.5D + ((double)this.worldObj.rand.nextFloat() - 0.5D) * 2.0D;
                    double var13 = (double)par5 + 0.5D + ((double)this.worldObj.rand.nextFloat() - 0.5D) * 2.0D;
                    this.worldObj.spawnParticle("smoke", var9, var11, var13, 0.0D, 0.0D, 0.0D);
                    this.worldObj.spawnParticle("flame", var9, var11, var13, 0.0D, 0.0D, 0.0D);
                }
        }
    }
}
