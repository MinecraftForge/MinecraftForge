package net.minecraft.world;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.util.Vec3;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManagerHell;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderHell;

public class WorldProviderHell extends WorldProvider
{
    private static final String __OBFID = "CL_00000387";

    // JAVADOC METHOD $$ func_76572_b
    public void registerWorldChunkManager()
    {
        this.worldChunkMgr = new WorldChunkManagerHell(BiomeGenBase.hell, 0.0F);
        this.isHellWorld = true;
        this.hasNoSky = true;
        this.dimensionId = -1;
    }

    // JAVADOC METHOD $$ func_76562_b
    @SideOnly(Side.CLIENT)
    public Vec3 getFogColor(float par1, float par2)
    {
        return this.worldObj.getWorldVec3Pool().getVecFromPool(0.20000000298023224D, 0.029999999329447746D, 0.029999999329447746D);
    }

    // JAVADOC METHOD $$ func_76556_a
    protected void generateLightBrightnessTable()
    {
        float f = 0.1F;

        for (int i = 0; i <= 15; ++i)
        {
            float f1 = 1.0F - (float)i / 15.0F;
            this.lightBrightnessTable[i] = (1.0F - f1) / (f1 * 3.0F + 1.0F) * (1.0F - f) + f;
        }
    }

    // JAVADOC METHOD $$ func_76555_c
    public IChunkProvider createChunkGenerator()
    {
        return new ChunkProviderHell(this.worldObj, this.worldObj.getSeed());
    }

    // JAVADOC METHOD $$ func_76569_d
    public boolean isSurfaceWorld()
    {
        return false;
    }

    // JAVADOC METHOD $$ func_76566_a
    public boolean canCoordinateBeSpawn(int par1, int par2)
    {
        return false;
    }

    // JAVADOC METHOD $$ func_76563_a
    public float calculateCelestialAngle(long par1, float par3)
    {
        return 0.5F;
    }

    // JAVADOC METHOD $$ func_76567_e
    public boolean canRespawnHere()
    {
        return false;
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
        return "Nether";
    }
}