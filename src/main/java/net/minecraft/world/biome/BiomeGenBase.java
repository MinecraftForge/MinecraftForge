package net.minecraft.world.biome;

import com.google.common.collect.Sets;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFlower;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.util.WeightedRandom;
import net.minecraft.world.ColorizerFoliage;
import net.minecraft.world.ColorizerGrass;
import net.minecraft.world.World;
import net.minecraft.world.gen.NoiseGeneratorPerlin;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.world.gen.feature.WorldGenBigTree;
import net.minecraft.world.gen.feature.WorldGenDoublePlant;
import net.minecraft.world.gen.feature.WorldGenSwamp;
import net.minecraft.world.gen.feature.WorldGenTallGrass;
import net.minecraft.world.gen.feature.WorldGenTrees;
import net.minecraft.world.gen.feature.WorldGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraftforge.common.*;
import net.minecraftforge.event.terraingen.*;

public abstract class BiomeGenBase
{
    private static final Logger field_150586_aC = LogManager.getLogger();
    protected static final BiomeGenBase.Height field_150596_a = new BiomeGenBase.Height(0.1F, 0.2F);
    protected static final BiomeGenBase.Height field_150594_b = new BiomeGenBase.Height(-0.5F, 0.0F);
    protected static final BiomeGenBase.Height field_150595_c = new BiomeGenBase.Height(-1.0F, 0.1F);
    protected static final BiomeGenBase.Height field_150592_d = new BiomeGenBase.Height(-1.8F, 0.1F);
    protected static final BiomeGenBase.Height field_150593_e = new BiomeGenBase.Height(0.125F, 0.05F);
    protected static final BiomeGenBase.Height field_150590_f = new BiomeGenBase.Height(0.2F, 0.2F);
    protected static final BiomeGenBase.Height field_150591_g = new BiomeGenBase.Height(0.45F, 0.3F);
    protected static final BiomeGenBase.Height field_150602_h = new BiomeGenBase.Height(1.5F, 0.025F);
    protected static final BiomeGenBase.Height field_150603_i = new BiomeGenBase.Height(1.0F, 0.5F);
    protected static final BiomeGenBase.Height field_150600_j = new BiomeGenBase.Height(0.0F, 0.025F);
    protected static final BiomeGenBase.Height field_150601_k = new BiomeGenBase.Height(0.1F, 0.8F);
    protected static final BiomeGenBase.Height field_150598_l = new BiomeGenBase.Height(0.2F, 0.3F);
    protected static final BiomeGenBase.Height field_150599_m = new BiomeGenBase.Height(-0.2F, 0.1F);
    // JAVADOC FIELD $$ field_76773_a
    private static final BiomeGenBase[] biomeList = new BiomeGenBase[256];
    public static final Set field_150597_n = Sets.newHashSet();
    public static final BiomeGenBase ocean = (new BiomeGenOcean(0)).setColor(112).setBiomeName("Ocean").func_150570_a(field_150595_c);
    public static final BiomeGenBase plains = (new BiomeGenPlains(1)).setColor(9286496).setBiomeName("Plains");
    public static final BiomeGenBase desert = (new BiomeGenDesert(2)).setColor(16421912).setBiomeName("Desert").setDisableRain().setTemperatureRainfall(2.0F, 0.0F).func_150570_a(field_150593_e);
    public static final BiomeGenBase extremeHills = (new BiomeGenHills(3, false)).setColor(6316128).setBiomeName("Extreme Hills").func_150570_a(field_150603_i).setTemperatureRainfall(0.2F, 0.3F);
    public static final BiomeGenBase forest = (new BiomeGenForest(4, 0)).setColor(353825).setBiomeName("Forest");
    public static final BiomeGenBase taiga = (new BiomeGenTaiga(5, 0)).setColor(747097).setBiomeName("Taiga").func_76733_a(5159473).setTemperatureRainfall(0.25F, 0.8F).func_150570_a(field_150590_f);
    public static final BiomeGenBase swampland = (new BiomeGenSwamp(6)).setColor(522674).setBiomeName("Swampland").func_76733_a(9154376).func_150570_a(field_150599_m).setTemperatureRainfall(0.8F, 0.9F);
    public static final BiomeGenBase river = (new BiomeGenRiver(7)).setColor(255).setBiomeName("River").func_150570_a(field_150594_b);
    public static final BiomeGenBase hell = (new BiomeGenHell(8)).setColor(16711680).setBiomeName("Hell").setDisableRain().setTemperatureRainfall(2.0F, 0.0F);
    // JAVADOC FIELD $$ field_76779_k
    public static final BiomeGenBase sky = (new BiomeGenEnd(9)).setColor(8421631).setBiomeName("Sky").setDisableRain();
    public static final BiomeGenBase frozenOcean = (new BiomeGenOcean(10)).setColor(9474208).setBiomeName("FrozenOcean").setEnableSnow().func_150570_a(field_150595_c).setTemperatureRainfall(0.0F, 0.5F);
    public static final BiomeGenBase frozenRiver = (new BiomeGenRiver(11)).setColor(10526975).setBiomeName("FrozenRiver").setEnableSnow().func_150570_a(field_150594_b).setTemperatureRainfall(0.0F, 0.5F);
    public static final BiomeGenBase icePlains = (new BiomeGenSnow(12, false)).setColor(16777215).setBiomeName("Ice Plains").setEnableSnow().setTemperatureRainfall(0.0F, 0.5F).func_150570_a(field_150593_e);
    public static final BiomeGenBase iceMountains = (new BiomeGenSnow(13, false)).setColor(10526880).setBiomeName("Ice Mountains").setEnableSnow().func_150570_a(field_150591_g).setTemperatureRainfall(0.0F, 0.5F);
    public static final BiomeGenBase mushroomIsland = (new BiomeGenMushroomIsland(14)).setColor(16711935).setBiomeName("MushroomIsland").setTemperatureRainfall(0.9F, 1.0F).func_150570_a(field_150598_l);
    public static final BiomeGenBase mushroomIslandShore = (new BiomeGenMushroomIsland(15)).setColor(10486015).setBiomeName("MushroomIslandShore").setTemperatureRainfall(0.9F, 1.0F).func_150570_a(field_150600_j);
    // JAVADOC FIELD $$ field_76787_r
    public static final BiomeGenBase beach = (new BiomeGenBeach(16)).setColor(16440917).setBiomeName("Beach").setTemperatureRainfall(0.8F, 0.4F).func_150570_a(field_150600_j);
    // JAVADOC FIELD $$ field_76786_s
    public static final BiomeGenBase desertHills = (new BiomeGenDesert(17)).setColor(13786898).setBiomeName("DesertHills").setDisableRain().setTemperatureRainfall(2.0F, 0.0F).func_150570_a(field_150591_g);
    // JAVADOC FIELD $$ field_76785_t
    public static final BiomeGenBase forestHills = (new BiomeGenForest(18, 0)).setColor(2250012).setBiomeName("ForestHills").func_150570_a(field_150591_g);
    // JAVADOC FIELD $$ field_76784_u
    public static final BiomeGenBase taigaHills = (new BiomeGenTaiga(19, 0)).setColor(1456435).setBiomeName("TaigaHills").func_76733_a(5159473).setTemperatureRainfall(0.25F, 0.8F).func_150570_a(field_150591_g);
    // JAVADOC FIELD $$ field_76783_v
    public static final BiomeGenBase extremeHillsEdge = (new BiomeGenHills(20, true)).setColor(7501978).setBiomeName("Extreme Hills Edge").func_150570_a(field_150603_i.func_150775_a()).setTemperatureRainfall(0.2F, 0.3F);
    // JAVADOC FIELD $$ field_76782_w
    public static final BiomeGenBase jungle = (new BiomeGenJungle(21, false)).setColor(5470985).setBiomeName("Jungle").func_76733_a(5470985).setTemperatureRainfall(0.95F, 0.9F);
    public static final BiomeGenBase jungleHills = (new BiomeGenJungle(22, false)).setColor(2900485).setBiomeName("JungleHills").func_76733_a(5470985).setTemperatureRainfall(0.95F, 0.9F).func_150570_a(field_150591_g);
    public static final BiomeGenBase field_150574_L = (new BiomeGenJungle(23, true)).setColor(6458135).setBiomeName("JungleEdge").func_76733_a(5470985).setTemperatureRainfall(0.95F, 0.8F);
    public static final BiomeGenBase field_150575_M = (new BiomeGenOcean(24)).setColor(48).setBiomeName("Deep Ocean").func_150570_a(field_150592_d);
    public static final BiomeGenBase field_150576_N = (new BiomeGenStoneBeach(25)).setColor(10658436).setBiomeName("Stone Beach").setTemperatureRainfall(0.2F, 0.3F).func_150570_a(field_150601_k);
    public static final BiomeGenBase field_150577_O = (new BiomeGenBeach(26)).setColor(16445632).setBiomeName("Cold Beach").setTemperatureRainfall(0.05F, 0.3F).func_150570_a(field_150600_j).setEnableSnow();
    public static final BiomeGenBase field_150583_P = (new BiomeGenForest(27, 2)).setBiomeName("Birch Forest").setColor(3175492);
    public static final BiomeGenBase field_150582_Q = (new BiomeGenForest(28, 2)).setBiomeName("Birch Forest Hills").setColor(2055986).func_150570_a(field_150591_g);
    public static final BiomeGenBase field_150585_R = (new BiomeGenForest(29, 3)).setColor(4215066).setBiomeName("Roofed Forest");
    public static final BiomeGenBase field_150584_S = (new BiomeGenTaiga(30, 0)).setColor(3233098).setBiomeName("Cold Taiga").func_76733_a(5159473).setEnableSnow().setTemperatureRainfall(-0.5F, 0.4F).func_150570_a(field_150590_f).func_150563_c(16777215);
    public static final BiomeGenBase field_150579_T = (new BiomeGenTaiga(31, 0)).setColor(2375478).setBiomeName("Cold Taiga Hills").func_76733_a(5159473).setEnableSnow().setTemperatureRainfall(-0.5F, 0.4F).func_150570_a(field_150591_g).func_150563_c(16777215);
    public static final BiomeGenBase field_150578_U = (new BiomeGenTaiga(32, 1)).setColor(5858897).setBiomeName("Mega Taiga").func_76733_a(5159473).setTemperatureRainfall(0.3F, 0.8F).func_150570_a(field_150590_f);
    public static final BiomeGenBase field_150581_V = (new BiomeGenTaiga(33, 1)).setColor(4542270).setBiomeName("Mega Taiga Hills").func_76733_a(5159473).setTemperatureRainfall(0.3F, 0.8F).func_150570_a(field_150591_g);
    public static final BiomeGenBase field_150580_W = (new BiomeGenHills(34, true)).setColor(5271632).setBiomeName("Extreme Hills+").func_150570_a(field_150603_i).setTemperatureRainfall(0.2F, 0.3F);
    public static final BiomeGenBase field_150588_X = (new BiomeGenSavanna(35)).setColor(12431967).setBiomeName("Savanna").setTemperatureRainfall(1.2F, 0.0F).setDisableRain().func_150570_a(field_150593_e);
    public static final BiomeGenBase field_150587_Y = (new BiomeGenSavanna(36)).setColor(10984804).setBiomeName("Savanna Plateau").setTemperatureRainfall(1.0F, 0.0F).setDisableRain().func_150570_a(field_150602_h);
    public static final BiomeGenBase field_150589_Z = (new BiomeGenMesa(37, false, false)).setColor(14238997).setBiomeName("Mesa");
    public static final BiomeGenBase field_150607_aa = (new BiomeGenMesa(38, false, true)).setColor(11573093).setBiomeName("Mesa Plateau F").func_150570_a(field_150602_h);
    public static final BiomeGenBase field_150608_ab = (new BiomeGenMesa(39, false, false)).setColor(13274213).setBiomeName("Mesa Plateau").func_150570_a(field_150602_h);
    protected static final NoiseGeneratorPerlin field_150605_ac;
    protected static final NoiseGeneratorPerlin field_150606_ad;
    protected static final WorldGenDoublePlant field_150610_ae;
    public String biomeName;
    public int color;
    public int field_150609_ah;
    // JAVADOC FIELD $$ field_76752_A
    public Block topBlock;
    public int field_150604_aj;
    // JAVADOC FIELD $$ field_76753_B
    public Block fillerBlock;
    public int field_76754_C;
    // JAVADOC FIELD $$ field_76748_D
    public float minHeight;
    // JAVADOC FIELD $$ field_76749_E
    public float maxHeight;
    // JAVADOC FIELD $$ field_76750_F
    public float temperature;
    // JAVADOC FIELD $$ field_76751_G
    public float rainfall;
    // JAVADOC FIELD $$ field_76759_H
    public int waterColorMultiplier;
    // JAVADOC FIELD $$ field_76760_I
    public BiomeDecorator theBiomeDecorator;
    // JAVADOC FIELD $$ field_76761_J
    protected List spawnableMonsterList;
    // JAVADOC FIELD $$ field_76762_K
    protected List spawnableCreatureList;
    // JAVADOC FIELD $$ field_76755_L
    protected List spawnableWaterCreatureList;
    protected List spawnableCaveCreatureList;
    // JAVADOC FIELD $$ field_76766_R
    protected boolean enableSnow;
    // JAVADOC FIELD $$ field_76765_S
    protected boolean enableRain;
    // JAVADOC FIELD $$ field_76756_M
    public final int biomeID;
    // JAVADOC FIELD $$ field_76757_N
    protected WorldGenTrees worldGeneratorTrees;
    // JAVADOC FIELD $$ field_76758_O
    protected WorldGenBigTree worldGeneratorBigTree;
    // JAVADOC FIELD $$ field_76763_Q
    protected WorldGenSwamp worldGeneratorSwamp;
    private static final String __OBFID = "CL_00000158";

    public BiomeGenBase(int par1)
    {
        this(par1, true);
    }
    public BiomeGenBase(int par1, boolean register)
    {
        this.topBlock = Blocks.grass;
        this.field_150604_aj = 0;
        this.fillerBlock = Blocks.dirt;
        this.field_76754_C = 5169201;
        this.minHeight = field_150596_a.field_150777_a;
        this.maxHeight = field_150596_a.field_150776_b;
        this.temperature = 0.5F;
        this.rainfall = 0.5F;
        this.waterColorMultiplier = 16777215;
        this.spawnableMonsterList = new ArrayList();
        this.spawnableCreatureList = new ArrayList();
        this.spawnableWaterCreatureList = new ArrayList();
        this.spawnableCaveCreatureList = new ArrayList();
        this.enableRain = true;
        this.worldGeneratorTrees = new WorldGenTrees(false);
        this.worldGeneratorBigTree = new WorldGenBigTree(false);
        this.worldGeneratorSwamp = new WorldGenSwamp();
        this.biomeID = par1;
        if (register)
        biomeList[par1] = this;
        this.theBiomeDecorator = this.createBiomeDecorator();
        this.spawnableCreatureList.add(new BiomeGenBase.SpawnListEntry(EntitySheep.class, 12, 4, 4));
        this.spawnableCreatureList.add(new BiomeGenBase.SpawnListEntry(EntityPig.class, 10, 4, 4));
        this.spawnableCreatureList.add(new BiomeGenBase.SpawnListEntry(EntityChicken.class, 10, 4, 4));
        this.spawnableCreatureList.add(new BiomeGenBase.SpawnListEntry(EntityCow.class, 8, 4, 4));
        this.spawnableMonsterList.add(new BiomeGenBase.SpawnListEntry(EntitySpider.class, 100, 4, 4));
        this.spawnableMonsterList.add(new BiomeGenBase.SpawnListEntry(EntityZombie.class, 100, 4, 4));
        this.spawnableMonsterList.add(new BiomeGenBase.SpawnListEntry(EntitySkeleton.class, 100, 4, 4));
        this.spawnableMonsterList.add(new BiomeGenBase.SpawnListEntry(EntityCreeper.class, 100, 4, 4));
        this.spawnableMonsterList.add(new BiomeGenBase.SpawnListEntry(EntitySlime.class, 100, 4, 4));
        this.spawnableMonsterList.add(new BiomeGenBase.SpawnListEntry(EntityEnderman.class, 10, 1, 4));
        this.spawnableMonsterList.add(new BiomeGenBase.SpawnListEntry(EntityWitch.class, 5, 1, 1));
        this.spawnableWaterCreatureList.add(new BiomeGenBase.SpawnListEntry(EntitySquid.class, 10, 4, 4));
        this.spawnableCaveCreatureList.add(new BiomeGenBase.SpawnListEntry(EntityBat.class, 10, 8, 8));
        this.addDefaultFlowers();
    }

    // JAVADOC METHOD $$ func_76729_a
    public BiomeDecorator createBiomeDecorator()
    {
        return getModdedBiomeDecorator(new BiomeDecorator());
    }

    // JAVADOC METHOD $$ func_76732_a
    public BiomeGenBase setTemperatureRainfall(float par1, float par2)
    {
        if (par1 > 0.1F && par1 < 0.2F)
        {
            throw new IllegalArgumentException("Please avoid temperatures in the range 0.1 - 0.2 because of snow");
        }
        else
        {
            this.temperature = par1;
            this.rainfall = par2;
            return this;
        }
    }

    public final BiomeGenBase func_150570_a(BiomeGenBase.Height p_150570_1_)
    {
        this.minHeight = p_150570_1_.field_150777_a;
        this.maxHeight = p_150570_1_.field_150776_b;
        return this;
    }

    // JAVADOC METHOD $$ func_76745_m
    public BiomeGenBase setDisableRain()
    {
        this.enableRain = false;
        return this;
    }

    public WorldGenAbstractTree func_150567_a(Random p_150567_1_)
    {
        return (WorldGenAbstractTree)(p_150567_1_.nextInt(10) == 0 ? this.worldGeneratorBigTree : this.worldGeneratorTrees);
    }

    // JAVADOC METHOD $$ func_76730_b
    public WorldGenerator getRandomWorldGenForGrass(Random par1Random)
    {
        return new WorldGenTallGrass(Blocks.tallgrass, 1);
    }

    public String func_150572_a(Random p_150572_1_, int p_150572_2_, int p_150572_3_, int p_150572_4_)
    {
        return p_150572_1_.nextInt(3) > 0 ? BlockFlower.field_149858_b[0] : BlockFlower.field_149859_a[0];
    }

    // JAVADOC METHOD $$ func_76742_b
    public BiomeGenBase setEnableSnow()
    {
        this.enableSnow = true;
        return this;
    }

    public BiomeGenBase setBiomeName(String par1Str)
    {
        this.biomeName = par1Str;
        return this;
    }

    public BiomeGenBase func_76733_a(int par1)
    {
        this.field_76754_C = par1;
        return this;
    }

    public BiomeGenBase setColor(int par1)
    {
        this.func_150557_a(par1, false);
        return this;
    }

    public BiomeGenBase func_150563_c(int p_150563_1_)
    {
        this.field_150609_ah = p_150563_1_;
        return this;
    }

    public BiomeGenBase func_150557_a(int p_150557_1_, boolean p_150557_2_)
    {
        this.color = p_150557_1_;

        if (p_150557_2_)
        {
            this.field_150609_ah = (p_150557_1_ & 16711422) >> 1;
        }
        else
        {
            this.field_150609_ah = p_150557_1_;
        }

        return this;
    }

    // JAVADOC METHOD $$ func_76731_a
    @SideOnly(Side.CLIENT)
    public int getSkyColorByTemp(float par1)
    {
        par1 /= 3.0F;

        if (par1 < -1.0F)
        {
            par1 = -1.0F;
        }

        if (par1 > 1.0F)
        {
            par1 = 1.0F;
        }

        return Color.getHSBColor(0.62222224F - par1 * 0.05F, 0.5F + par1 * 0.1F, 1.0F).getRGB();
    }

    // JAVADOC METHOD $$ func_76747_a
    public List getSpawnableList(EnumCreatureType par1EnumCreatureType)
    {
        return par1EnumCreatureType == EnumCreatureType.monster ? this.spawnableMonsterList : (par1EnumCreatureType == EnumCreatureType.creature ? this.spawnableCreatureList : (par1EnumCreatureType == EnumCreatureType.waterCreature ? this.spawnableWaterCreatureList : (par1EnumCreatureType == EnumCreatureType.ambient ? this.spawnableCaveCreatureList : null)));
    }

    // JAVADOC METHOD $$ func_76746_c
    public boolean getEnableSnow()
    {
        return this.func_150559_j();
    }

    // JAVADOC METHOD $$ func_76738_d
    public boolean canSpawnLightningBolt()
    {
        return this.func_150559_j() ? false : this.enableRain;
    }

    // JAVADOC METHOD $$ func_76736_e
    public boolean isHighHumidity()
    {
        return this.rainfall > 0.85F;
    }

    // JAVADOC METHOD $$ func_76741_f
    public float getSpawningChance()
    {
        return 0.1F;
    }

    // JAVADOC METHOD $$ func_76744_g
    public final int getIntRainfall()
    {
        return (int)(this.rainfall * 65536.0F);
    }

    // JAVADOC METHOD $$ func_76727_i
    @SideOnly(Side.CLIENT)
    public final float getFloatRainfall()
    {
        return this.rainfall;
    }

    public final float func_150564_a(int p_150564_1_, int p_150564_2_, int p_150564_3_)
    {
        if (p_150564_2_ > 64)
        {
            float f = (float)field_150605_ac.func_151601_a((double)p_150564_1_ * 1.0D / 8.0D, (double)p_150564_3_ * 1.0D / 8.0D) * 4.0F;
            return this.temperature - (f + (float)p_150564_2_ - 64.0F) * 0.05F / 30.0F;
        }
        else
        {
            return this.temperature;
        }
    }

    public void decorate(World par1World, Random par2Random, int par3, int par4)
    {
        this.theBiomeDecorator.func_150512_a(par1World, par2Random, this, par3, par4);
    }

    @SideOnly(Side.CLIENT)
    public int func_150558_b(int p_150558_1_, int p_150558_2_, int p_150558_3_)
    {
        double d0 = (double)MathHelper.clamp_float(this.func_150564_a(p_150558_1_, p_150558_2_, p_150558_3_), 0.0F, 1.0F);
        double d1 = (double)MathHelper.clamp_float(this.getFloatRainfall(), 0.0F, 1.0F);
        return getModdedBiomeGrassColor(ColorizerGrass.getGrassColor(d0, d1));
    }

    @SideOnly(Side.CLIENT)
    public int func_150571_c(int p_150571_1_, int p_150571_2_, int p_150571_3_)
    {
        double d0 = (double)MathHelper.clamp_float(this.func_150564_a(p_150571_1_, p_150571_2_, p_150571_3_), 0.0F, 1.0F);
        double d1 = (double)MathHelper.clamp_float(this.getFloatRainfall(), 0.0F, 1.0F);
        return getModdedBiomeFoliageColor(ColorizerFoliage.getFoliageColor(d0, d1));
    }

    public boolean func_150559_j()
    {
        return this.enableSnow;
    }

    public void func_150573_a(World p_150573_1_, Random p_150573_2_, Block[] p_150573_3_, byte[] p_150573_4_, int p_150573_5_, int p_150573_6_, double p_150573_7_)
    {
        this.func_150560_b(p_150573_1_, p_150573_2_, p_150573_3_, p_150573_4_, p_150573_5_, p_150573_6_, p_150573_7_);
    }

    public final void func_150560_b(World p_150560_1_, Random p_150560_2_, Block[] p_150560_3_, byte[] p_150560_4_, int p_150560_5_, int p_150560_6_, double p_150560_7_)
    {
        boolean flag = true;
        Block block = this.topBlock;
        byte b0 = (byte)(this.field_150604_aj & 255);
        Block block1 = this.fillerBlock;
        int k = -1;
        int l = (int)(p_150560_7_ / 3.0D + 3.0D + p_150560_2_.nextDouble() * 0.25D);
        int i1 = p_150560_5_ & 15;
        int j1 = p_150560_6_ & 15;
        int k1 = p_150560_3_.length / 256;

        for (int l1 = 255; l1 >= 0; --l1)
        {
            int i2 = (j1 * 16 + i1) * k1 + l1;

            if (l1 <= 0 + p_150560_2_.nextInt(5))
            {
                p_150560_3_[i2] = Blocks.bedrock;
            }
            else
            {
                Block block2 = p_150560_3_[i2];

                if (block2 != null && block2.func_149688_o() != Material.field_151579_a)
                {
                    if (block2 == Blocks.stone)
                    {
                        if (k == -1)
                        {
                            if (l <= 0)
                            {
                                block = null;
                                b0 = 0;
                                block1 = Blocks.stone;
                            }
                            else if (l1 >= 59 && l1 <= 64)
                            {
                                block = this.topBlock;
                                b0 = (byte)(this.field_150604_aj & 255);
                                block1 = this.fillerBlock;
                            }

                            if (l1 < 63 && (block == null || block.func_149688_o() == Material.field_151579_a))
                            {
                                if (this.func_150564_a(p_150560_5_, l1, p_150560_6_) < 0.15F)
                                {
                                    block = Blocks.ice;
                                    b0 = 0;
                                }
                                else
                                {
                                    block = Blocks.water;
                                    b0 = 0;
                                }
                            }

                            k = l;

                            if (l1 >= 62)
                            {
                                p_150560_3_[i2] = block;
                                p_150560_4_[i2] = b0;
                            }
                            else if (l1 < 56 - l)
                            {
                                block = null;
                                block1 = Blocks.stone;
                                p_150560_3_[i2] = Blocks.gravel;
                            }
                            else
                            {
                                p_150560_3_[i2] = block1;
                            }
                        }
                        else if (k > 0)
                        {
                            --k;
                            p_150560_3_[i2] = block1;

                            if (k == 0 && block1 == Blocks.sand)
                            {
                                k = p_150560_2_.nextInt(4) + Math.max(0, l1 - 63);
                                block1 = Blocks.sandstone;
                            }
                        }
                    }
                }
                else
                {
                    k = -1;
                }
            }
        }
    }

    public BiomeGenBase func_150566_k()
    {
        return new BiomeGenMutated(this.biomeID + 128, this);
    }

    public Class func_150562_l()
    {
        return this.getClass();
    }

    public boolean func_150569_a(BiomeGenBase p_150569_1_)
    {
        return p_150569_1_ == this ? true : (p_150569_1_ == null ? false : this.func_150562_l() == p_150569_1_.func_150562_l());
    }

    public BiomeGenBase.TempCategory func_150561_m()
    {
        return (double)this.temperature < 0.2D ? BiomeGenBase.TempCategory.COLD : ((double)this.temperature < 1.0D ? BiomeGenBase.TempCategory.MEDIUM : BiomeGenBase.TempCategory.WARM);
    }

    public static BiomeGenBase[] func_150565_n()
    {
        // JAVADOC FIELD $$ field_76773_a
        return biomeList;
    }

    public static BiomeGenBase func_150568_d(int p_150568_0_)
    {
        if (p_150568_0_ >= 0 && p_150568_0_ <= biomeList.length)
        {
            return biomeList[p_150568_0_];
        }
        else
        {
            field_150586_aC.warn("Biome ID is out of bounds: " + p_150568_0_ + ", defaulting to 0 (Ocean)");
            return ocean;
        }
    }

    /* ========================================= FORGE START ======================================*/
    protected List<FlowerEntry> flowers = new ArrayList<FlowerEntry>();

    public BiomeDecorator getModdedBiomeDecorator(BiomeDecorator original)
    {
        return new DeferredBiomeDecorator(original);
    }

    public int getWaterColorMultiplier()
    {
        BiomeEvent.GetWaterColor event = new BiomeEvent.GetWaterColor(this, waterColorMultiplier);
        MinecraftForge.EVENT_BUS.post(event);
        return event.newColor;
    }
    
    public int getModdedBiomeGrassColor(int original)
    {
        BiomeEvent.GetGrassColor event = new BiomeEvent.GetGrassColor(this, original);
        MinecraftForge.EVENT_BUS.post(event);
        return event.newColor;
    }

    public int getModdedBiomeFoliageColor(int original)
    {
        BiomeEvent.GetFoliageColor event = new BiomeEvent.GetFoliageColor(this, original);
        MinecraftForge.EVENT_BUS.post(event);
        return event.newColor;
    }

    /**
     * Weighted random holder class used to hold possible flowers 
     * that can spawn in this biome when bonemeal is used on grass.
     */
    public static class FlowerEntry extends WeightedRandom.Item
    {
        public final Block block;
        public final int metadata;
        public FlowerEntry(Block block, int meta, int weight)
        {
            super(weight);
            this.block = block;
            this.metadata = meta;
        }
    }

    /**
     * Adds the default flowers, as of 1.7, it is 2 yellow, and 1 red. I chose 10 to allow some wiggle room in the numbers.
     */
    public void addDefaultFlowers()
    {
        this.flowers.add(new FlowerEntry(Blocks.yellow_flower, 0, 20));
        this.flowers.add(new FlowerEntry(Blocks.red_flower,    0, 10));
    }

    /** Register a new plant to be planted when bonemeal is used on grass.
     * @param block The block to place.
     * @param metadata The metadata to set for the block when being placed.
     * @param weight The weight of the plant, where red flowers are
     *               10 and yellow flowers are 20.
     */
    public void addFlower(Block block, int metadata, int weight)
    {
        this.flowers.add(new FlowerEntry(block, metadata, weight));
    }

    public void plantFlower(World world, Random rand, int x, int y, int z)
    {
        BiomeGenBase biome = world.getBiomeGenForCoords(x, z);
        String flowername = biome.func_150572_a(rand, x, y, z);
        
        FlowerEntry flower = (FlowerEntry)WeightedRandom.getRandomItem(rand, flowers);
        if (flower == null || flower.block == null || !flower.block.func_149718_j(world, x, y, z))
        {
            return;
        }

        world.func_147465_d(x, y, z, flower.block, flower.metadata, 3);
    }

    
    /* ========================================= FORGE END ======================================*/

    static
    {
        plains.func_150566_k();
        desert.func_150566_k();
        forest.func_150566_k();
        taiga.func_150566_k();
        swampland.func_150566_k();
        icePlains.func_150566_k();
        jungle.func_150566_k();
        field_150574_L.func_150566_k();
        field_150584_S.func_150566_k();
        field_150588_X.func_150566_k();
        field_150587_Y.func_150566_k();
        field_150589_Z.func_150566_k();
        field_150607_aa.func_150566_k();
        field_150608_ab.func_150566_k();
        field_150583_P.func_150566_k();
        field_150582_Q.func_150566_k();
        field_150585_R.func_150566_k();
        field_150578_U.func_150566_k();
        extremeHills.func_150566_k();
        field_150580_W.func_150566_k();
        biomeList[field_150581_V.biomeID + 128] = biomeList[field_150578_U.biomeID + 128];
        BiomeGenBase[] var0 = biomeList;
        int var1 = var0.length;

        for (int var2 = 0; var2 < var1; ++var2)
        {
            BiomeGenBase var3 = var0[var2];

            if (var3 != null && var3.biomeID < 128)
            {
                field_150597_n.add(var3);
            }
        }

        field_150597_n.remove(hell);
        field_150597_n.remove(sky);
        field_150605_ac = new NoiseGeneratorPerlin(new Random(1234L), 1);
        field_150606_ad = new NoiseGeneratorPerlin(new Random(2345L), 1);
        field_150610_ae = new WorldGenDoublePlant();
    }

    public static class SpawnListEntry extends WeightedRandom.Item
        {
            // JAVADOC FIELD $$ field_76300_b
            public Class entityClass;
            public int minGroupCount;
            public int maxGroupCount;
            private static final String __OBFID = "CL_00000161";

            public SpawnListEntry(Class par1Class, int par2, int par3, int par4)
            {
                super(par2);
                this.entityClass = par1Class;
                this.minGroupCount = par3;
                this.maxGroupCount = par4;
            }

            public String toString()
            {
                return this.entityClass.getSimpleName() + "*(" + this.minGroupCount + "-" + this.maxGroupCount + "):" + this.itemWeight;
            }
        }

    public static class Height
        {
            public float field_150777_a;
            public float field_150776_b;
            private static final String __OBFID = "CL_00000159";

            public Height(float p_i45371_1_, float p_i45371_2_)
            {
                this.field_150777_a = p_i45371_1_;
                this.field_150776_b = p_i45371_2_;
            }

            public BiomeGenBase.Height func_150775_a()
            {
                return new BiomeGenBase.Height(this.field_150777_a * 0.8F, this.field_150776_b * 0.6F);
            }
        }

    public static enum TempCategory
    {
        OCEAN,
        COLD,
        MEDIUM,
        WARM;

        private static final String __OBFID = "CL_00000160";
    }
}