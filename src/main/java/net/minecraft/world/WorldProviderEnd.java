package net.minecraft.world;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManagerHell;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderEnd;

public class WorldProviderEnd extends WorldProvider
{
    private static final String __OBFID = "CL_00000389";

    // JAVADOC METHOD $$ func_76572_b
    public void registerWorldChunkManager()
    {
        this.worldChunkMgr = new WorldChunkManagerHell(BiomeGenBase.sky, 0.0F);
        this.dimensionId = 1;
        this.hasNoSky = true;
    }

    // JAVADOC METHOD $$ func_76555_c
    public IChunkProvider createChunkGenerator()
    {
        return new ChunkProviderEnd(this.worldObj, this.worldObj.getSeed());
    }

    // JAVADOC METHOD $$ func_76563_a
    public float calculateCelestialAngle(long par1, float par3)
    {
        return 0.0F;
    }

    // JAVADOC METHOD $$ func_76560_a
    @SideOnly(Side.CLIENT)
    public float[] calcSunriseSunsetColors(float par1, float par2)
    {
        return null;
    }

    // JAVADOC METHOD $$ func_76562_b
    @SideOnly(Side.CLIENT)
    public Vec3 getFogColor(float par1, float par2)
    {
        int i = 10518688;
        float f2 = MathHelper.cos(par1 * (float)Math.PI * 2.0F) * 2.0F + 0.5F;

        if (f2 < 0.0F)
        {
            f2 = 0.0F;
        }

        if (f2 > 1.0F)
        {
            f2 = 1.0F;
        }

        float f3 = (float)(i >> 16 & 255) / 255.0F;
        float f4 = (float)(i >> 8 & 255) / 255.0F;
        float f5 = (float)(i & 255) / 255.0F;
        f3 *= f2 * 0.0F + 0.15F;
        f4 *= f2 * 0.0F + 0.15F;
        f5 *= f2 * 0.0F + 0.15F;
        return this.worldObj.getWorldVec3Pool().getVecFromPool((double)f3, (double)f4, (double)f5);
    }

    @SideOnly(Side.CLIENT)
    public boolean isSkyColored()
    {
        return false;
    }

    // JAVADOC METHOD $$ func_76567_e
    public boolean canRespawnHere()
    {
        return false;
    }

    // JAVADOC METHOD $$ func_76569_d
    public boolean isSurfaceWorld()
    {
        return false;
    }

    // JAVADOC METHOD $$ func_76571_f
    @SideOnly(Side.CLIENT)
    public float getCloudHeight()
    {
        return 8.0F;
    }

    // JAVADOC METHOD $$ func_76566_a
    public boolean canCoordinateBeSpawn(int par1, int par2)
    {
        return this.worldObj.func_147474_b(par1, par2).func_149688_o().blocksMovement();
    }

    // JAVADOC METHOD $$ func_76554_h
    public ChunkCoordinates getEntrancePortalLocation()
    {
        return new ChunkCoordinates(100, 50, 0);
    }

    public int getAverageGroundLevel()
    {
        return 50;
    }

    // JAVADOC METHOD $$ func_76568_b
    @SideOnly(Side.CLIENT)
    public boolean doesXZShowFog(int par1, int par2)
    {
        return true;
    }

    // JAVADOC METHOD $$ func_80007_l
    public String getDimensionName()
    {
        return "The End";
    }
}