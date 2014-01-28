package net.minecraft.block;

import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.RegistryNamespaced;
import net.minecraft.util.RegistryNamespacedDefaultedByKey;
import net.minecraft.util.StatCollector;
import net.minecraft.util.Vec3;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldProviderEnd;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.common.util.RotationHelper;
import net.minecraftforge.event.ForgeEventFactory;
import static net.minecraftforge.common.util.ForgeDirection.*;

public class Block
{
    public static final RegistryNamespaced field_149771_c = GameData.blockRegistry;
    private CreativeTabs field_149772_a;
    protected String field_149768_d;
    public static final Block.SoundType field_149769_e = new Block.SoundType("stone", 1.0F, 1.0F);
    public static final Block.SoundType field_149766_f = new Block.SoundType("wood", 1.0F, 1.0F);
    public static final Block.SoundType field_149767_g = new Block.SoundType("gravel", 1.0F, 1.0F);
    public static final Block.SoundType field_149779_h = new Block.SoundType("grass", 1.0F, 1.0F);
    public static final Block.SoundType field_149780_i = new Block.SoundType("stone", 1.0F, 1.0F);
    public static final Block.SoundType field_149777_j = new Block.SoundType("stone", 1.0F, 1.5F);
    public static final Block.SoundType field_149778_k = new Block.SoundType("stone", 1.0F, 1.0F)
    {
        private static final String __OBFID = "CL_00000200";
        public String func_150495_a()
        {
            return "dig.glass";
        }
        public String func_150496_b()
        {
            return "step.stone";
        }
    };
    public static final Block.SoundType field_149775_l = new Block.SoundType("cloth", 1.0F, 1.0F);
    public static final Block.SoundType field_149776_m = new Block.SoundType("sand", 1.0F, 1.0F);
    public static final Block.SoundType field_149773_n = new Block.SoundType("snow", 1.0F, 1.0F);
    public static final Block.SoundType field_149774_o = new Block.SoundType("ladder", 1.0F, 1.0F)
    {
        private static final String __OBFID = "CL_00000201";
        public String func_150495_a()
        {
            return "dig.wood";
        }
    };
    public static final Block.SoundType field_149788_p = new Block.SoundType("anvil", 0.3F, 1.0F)
    {
        private static final String __OBFID = "CL_00000202";
        public String func_150495_a()
        {
            return "dig.stone";
        }
        public String func_150496_b()
        {
            return "random.anvil_land";
        }
    };
    protected boolean field_149787_q;
    protected int field_149786_r;
    protected boolean field_149785_s;
    protected int field_149784_t;
    protected boolean field_149783_u;
    protected float field_149782_v;
    protected float field_149781_w;
    protected boolean field_149791_x = true;
    protected boolean field_149790_y = true;
    protected boolean field_149789_z;
    protected boolean field_149758_A;
    protected double field_149759_B;
    protected double field_149760_C;
    protected double field_149754_D;
    protected double field_149755_E;
    protected double field_149756_F;
    protected double field_149757_G;
    public Block.SoundType field_149762_H;
    public float field_149763_I;
    protected final Material field_149764_J;
    public float field_149765_K;
    private String field_149770_b;
    @SideOnly(Side.CLIENT)
    protected IIcon field_149761_L;
    private static final String __OBFID = "CL_00000199";

    public static int func_149682_b(Block p_149682_0_)
    {
        return field_149771_c.func_148757_b(p_149682_0_);
    }

    public static Block func_149729_e(int p_149729_0_)
    {
        Block ret = (Block)field_149771_c.func_148754_a(p_149729_0_);
        return ret == null ? Blocks.air : ret;
    }

    public static Block func_149634_a(Item p_149634_0_)
    {
        return func_149729_e(Item.func_150891_b(p_149634_0_));
    }

    public static Block func_149684_b(String p_149684_0_)
    {
        if (field_149771_c.func_148741_d(p_149684_0_))
        {
            return (Block)field_149771_c.getObject(p_149684_0_);
        }
        else
        {
            try
            {
                return (Block)field_149771_c.func_148754_a(Integer.parseInt(p_149684_0_));
            }
            catch (NumberFormatException numberformatexception)
            {
                return null;
            }
        }
    }

    public boolean func_149730_j()
    {
        return this.field_149787_q;
    }

    public int func_149717_k()
    {
        return this.field_149786_r;
    }

    @SideOnly(Side.CLIENT)
    public boolean func_149751_l()
    {
        return this.field_149785_s;
    }

    public int func_149750_m()
    {
        return this.field_149784_t;
    }

    public boolean func_149710_n()
    {
        return this.field_149783_u;
    }

    public Material func_149688_o()
    {
        return this.field_149764_J;
    }

    public MapColor func_149728_f(int p_149728_1_)
    {
        return this.func_149688_o().func_151565_r();
    }

    public static void func_149671_p()
    {
        field_149771_c.func_148756_a(0, "air", (new BlockAir()).func_149663_c("air"));
        field_149771_c.func_148756_a(1, "stone", (new BlockStone()).func_149711_c(1.5F).func_149752_b(10.0F).func_149672_a(field_149780_i).func_149663_c("stone").func_149658_d("stone"));
        field_149771_c.func_148756_a(2, "grass", (new BlockGrass()).func_149711_c(0.6F).func_149672_a(field_149779_h).func_149663_c("grass").func_149658_d("grass"));
        field_149771_c.func_148756_a(3, "dirt", (new BlockDirt()).func_149711_c(0.5F).func_149672_a(field_149767_g).func_149663_c("dirt").func_149658_d("dirt"));
        Block block = (new Block(Material.field_151576_e)).func_149711_c(2.0F).func_149752_b(10.0F).func_149672_a(field_149780_i).func_149663_c("stonebrick").func_149647_a(CreativeTabs.tabBlock).func_149658_d("cobblestone");
        field_149771_c.func_148756_a(4, "cobblestone", block);
        Block block1 = (new BlockWood()).func_149711_c(2.0F).func_149752_b(5.0F).func_149672_a(field_149766_f).func_149663_c("wood").func_149658_d("planks");
        field_149771_c.func_148756_a(5, "planks", block1);
        field_149771_c.func_148756_a(6, "sapling", (new BlockSapling()).func_149711_c(0.0F).func_149672_a(field_149779_h).func_149663_c("sapling").func_149658_d("sapling"));
        field_149771_c.func_148756_a(7, "bedrock", (new Block(Material.field_151576_e)).func_149722_s().func_149752_b(6000000.0F).func_149672_a(field_149780_i).func_149663_c("bedrock").func_149649_H().func_149647_a(CreativeTabs.tabBlock).func_149658_d("bedrock"));
        field_149771_c.func_148756_a(8, "flowing_water", (new BlockDynamicLiquid(Material.field_151586_h)).func_149711_c(100.0F).func_149713_g(3).func_149663_c("water").func_149649_H().func_149658_d("water_flow"));
        field_149771_c.func_148756_a(9, "water", (new BlockStaticLiquid(Material.field_151586_h)).func_149711_c(100.0F).func_149713_g(3).func_149663_c("water").func_149649_H().func_149658_d("water_still"));
        field_149771_c.func_148756_a(10, "flowing_lava", (new BlockDynamicLiquid(Material.field_151587_i)).func_149711_c(100.0F).func_149715_a(1.0F).func_149663_c("lava").func_149649_H().func_149658_d("lava_flow"));
        field_149771_c.func_148756_a(11, "lava", (new BlockStaticLiquid(Material.field_151587_i)).func_149711_c(100.0F).func_149715_a(1.0F).func_149663_c("lava").func_149649_H().func_149658_d("lava_still"));
        field_149771_c.func_148756_a(12, "sand", (new BlockSand()).func_149711_c(0.5F).func_149672_a(field_149776_m).func_149663_c("sand").func_149658_d("sand"));
        field_149771_c.func_148756_a(13, "gravel", (new BlockGravel()).func_149711_c(0.6F).func_149672_a(field_149767_g).func_149663_c("gravel").func_149658_d("gravel"));
        field_149771_c.func_148756_a(14, "gold_ore", (new BlockOre()).func_149711_c(3.0F).func_149752_b(5.0F).func_149672_a(field_149780_i).func_149663_c("oreGold").func_149658_d("gold_ore"));
        field_149771_c.func_148756_a(15, "iron_ore", (new BlockOre()).func_149711_c(3.0F).func_149752_b(5.0F).func_149672_a(field_149780_i).func_149663_c("oreIron").func_149658_d("iron_ore"));
        field_149771_c.func_148756_a(16, "coal_ore", (new BlockOre()).func_149711_c(3.0F).func_149752_b(5.0F).func_149672_a(field_149780_i).func_149663_c("oreCoal").func_149658_d("coal_ore"));
        field_149771_c.func_148756_a(17, "log", (new BlockOldLog()).func_149663_c("log").func_149658_d("log"));
        field_149771_c.func_148756_a(18, "leaves", (new BlockOldLeaf()).func_149663_c("leaves").func_149658_d("leaves"));
        field_149771_c.func_148756_a(19, "sponge", (new BlockSponge()).func_149711_c(0.6F).func_149672_a(field_149779_h).func_149663_c("sponge").func_149658_d("sponge"));
        field_149771_c.func_148756_a(20, "glass", (new BlockGlass(Material.field_151592_s, false)).func_149711_c(0.3F).func_149672_a(field_149778_k).func_149663_c("glass").func_149658_d("glass"));
        field_149771_c.func_148756_a(21, "lapis_ore", (new BlockOre()).func_149711_c(3.0F).func_149752_b(5.0F).func_149672_a(field_149780_i).func_149663_c("oreLapis").func_149658_d("lapis_ore"));
        field_149771_c.func_148756_a(22, "lapis_block", (new BlockCompressed(MapColor.field_151652_H)).func_149711_c(3.0F).func_149752_b(5.0F).func_149672_a(field_149780_i).func_149663_c("blockLapis").func_149647_a(CreativeTabs.tabBlock).func_149658_d("lapis_block"));
        field_149771_c.func_148756_a(23, "dispenser", (new BlockDispenser()).func_149711_c(3.5F).func_149672_a(field_149780_i).func_149663_c("dispenser").func_149658_d("dispenser"));
        Block block2 = (new BlockSandStone()).func_149672_a(field_149780_i).func_149711_c(0.8F).func_149663_c("sandStone").func_149658_d("sandstone");
        field_149771_c.func_148756_a(24, "sandstone", block2);
        field_149771_c.func_148756_a(25, "noteblock", (new BlockNote()).func_149711_c(0.8F).func_149663_c("musicBlock").func_149658_d("noteblock"));
        field_149771_c.func_148756_a(26, "bed", (new BlockBed()).func_149711_c(0.2F).func_149663_c("bed").func_149649_H().func_149658_d("bed"));
        field_149771_c.func_148756_a(27, "golden_rail", (new BlockRailPowered()).func_149711_c(0.7F).func_149672_a(field_149777_j).func_149663_c("goldenRail").func_149658_d("rail_golden"));
        field_149771_c.func_148756_a(28, "detector_rail", (new BlockRailDetector()).func_149711_c(0.7F).func_149672_a(field_149777_j).func_149663_c("detectorRail").func_149658_d("rail_detector"));
        field_149771_c.func_148756_a(29, "sticky_piston", (new BlockPistonBase(true)).func_149663_c("pistonStickyBase"));
        field_149771_c.func_148756_a(30, "web", (new BlockWeb()).func_149713_g(1).func_149711_c(4.0F).func_149663_c("web").func_149658_d("web"));
        field_149771_c.func_148756_a(31, "tallgrass", (new BlockTallGrass()).func_149711_c(0.0F).func_149672_a(field_149779_h).func_149663_c("tallgrass"));
        field_149771_c.func_148756_a(32, "deadbush", (new BlockDeadBush()).func_149711_c(0.0F).func_149672_a(field_149779_h).func_149663_c("deadbush").func_149658_d("deadbush"));
        field_149771_c.func_148756_a(33, "piston", (new BlockPistonBase(false)).func_149663_c("pistonBase"));
        field_149771_c.func_148756_a(34, "piston_head", new BlockPistonExtension());
        field_149771_c.func_148756_a(35, "wool", (new BlockColored(Material.field_151580_n)).func_149711_c(0.8F).func_149672_a(field_149775_l).func_149663_c("cloth").func_149658_d("wool_colored"));
        field_149771_c.func_148756_a(36, "piston_extension", new BlockPistonMoving());
        field_149771_c.func_148756_a(37, "yellow_flower", (new BlockFlower(0)).func_149711_c(0.0F).func_149672_a(field_149779_h).func_149663_c("flower1").func_149658_d("flower_dandelion"));
        field_149771_c.func_148756_a(38, "red_flower", (new BlockFlower(1)).func_149711_c(0.0F).func_149672_a(field_149779_h).func_149663_c("flower2").func_149658_d("flower_rose"));
        field_149771_c.func_148756_a(39, "brown_mushroom", (new BlockMushroom()).func_149711_c(0.0F).func_149672_a(field_149779_h).func_149715_a(0.125F).func_149663_c("mushroom").func_149658_d("mushroom_brown"));
        field_149771_c.func_148756_a(40, "red_mushroom", (new BlockMushroom()).func_149711_c(0.0F).func_149672_a(field_149779_h).func_149663_c("mushroom").func_149658_d("mushroom_red"));
        field_149771_c.func_148756_a(41, "gold_block", (new BlockCompressed(MapColor.field_151647_F)).func_149711_c(3.0F).func_149752_b(10.0F).func_149672_a(field_149777_j).func_149663_c("blockGold").func_149658_d("gold_block"));
        field_149771_c.func_148756_a(42, "iron_block", (new BlockCompressed(MapColor.field_151668_h)).func_149711_c(5.0F).func_149752_b(10.0F).func_149672_a(field_149777_j).func_149663_c("blockIron").func_149658_d("iron_block"));
        field_149771_c.func_148756_a(43, "double_stone_slab", (new BlockStoneSlab(true)).func_149711_c(2.0F).func_149752_b(10.0F).func_149672_a(field_149780_i).func_149663_c("stoneSlab"));
        field_149771_c.func_148756_a(44, "stone_slab", (new BlockStoneSlab(false)).func_149711_c(2.0F).func_149752_b(10.0F).func_149672_a(field_149780_i).func_149663_c("stoneSlab"));
        Block block3 = (new Block(Material.field_151576_e)).func_149711_c(2.0F).func_149752_b(10.0F).func_149672_a(field_149780_i).func_149663_c("brick").func_149647_a(CreativeTabs.tabBlock).func_149658_d("brick");
        field_149771_c.func_148756_a(45, "brick_block", block3);
        field_149771_c.func_148756_a(46, "tnt", (new BlockTNT()).func_149711_c(0.0F).func_149672_a(field_149779_h).func_149663_c("tnt").func_149658_d("tnt"));
        field_149771_c.func_148756_a(47, "bookshelf", (new BlockBookshelf()).func_149711_c(1.5F).func_149672_a(field_149766_f).func_149663_c("bookshelf").func_149658_d("bookshelf"));
        field_149771_c.func_148756_a(48, "mossy_cobblestone", (new Block(Material.field_151576_e)).func_149711_c(2.0F).func_149752_b(10.0F).func_149672_a(field_149780_i).func_149663_c("stoneMoss").func_149647_a(CreativeTabs.tabBlock).func_149658_d("cobblestone_mossy"));
        field_149771_c.func_148756_a(49, "obsidian", (new BlockObsidian()).func_149711_c(50.0F).func_149752_b(2000.0F).func_149672_a(field_149780_i).func_149663_c("obsidian").func_149658_d("obsidian"));
        field_149771_c.func_148756_a(50, "torch", (new BlockTorch()).func_149711_c(0.0F).func_149715_a(0.9375F).func_149672_a(field_149766_f).func_149663_c("torch").func_149658_d("torch_on"));
        field_149771_c.func_148756_a(51, "fire", (new BlockFire()).func_149711_c(0.0F).func_149715_a(1.0F).func_149672_a(field_149766_f).func_149663_c("fire").func_149649_H().func_149658_d("fire"));
        field_149771_c.func_148756_a(52, "mob_spawner", (new BlockMobSpawner()).func_149711_c(5.0F).func_149672_a(field_149777_j).func_149663_c("mobSpawner").func_149649_H().func_149658_d("mob_spawner"));
        field_149771_c.func_148756_a(53, "oak_stairs", (new BlockStairs(block1, 0)).func_149663_c("stairsWood"));
        field_149771_c.func_148756_a(54, "chest", (new BlockChest(0)).func_149711_c(2.5F).func_149672_a(field_149766_f).func_149663_c("chest"));
        field_149771_c.func_148756_a(55, "redstone_wire", (new BlockRedstoneWire()).func_149711_c(0.0F).func_149672_a(field_149769_e).func_149663_c("redstoneDust").func_149649_H().func_149658_d("redstone_dust"));
        field_149771_c.func_148756_a(56, "diamond_ore", (new BlockOre()).func_149711_c(3.0F).func_149752_b(5.0F).func_149672_a(field_149780_i).func_149663_c("oreDiamond").func_149658_d("diamond_ore"));
        field_149771_c.func_148756_a(57, "diamond_block", (new BlockCompressed(MapColor.field_151648_G)).func_149711_c(5.0F).func_149752_b(10.0F).func_149672_a(field_149777_j).func_149663_c("blockDiamond").func_149658_d("diamond_block"));
        field_149771_c.func_148756_a(58, "crafting_table", (new BlockWorkbench()).func_149711_c(2.5F).func_149672_a(field_149766_f).func_149663_c("workbench").func_149658_d("crafting_table"));
        field_149771_c.func_148756_a(59, "wheat", (new BlockCrops()).func_149663_c("crops").func_149658_d("wheat"));
        Block block4 = (new BlockFarmland()).func_149711_c(0.6F).func_149672_a(field_149767_g).func_149663_c("farmland").func_149658_d("farmland");
        field_149771_c.func_148756_a(60, "farmland", block4);
        field_149771_c.func_148756_a(61, "furnace", (new BlockFurnace(false)).func_149711_c(3.5F).func_149672_a(field_149780_i).func_149663_c("furnace").func_149647_a(CreativeTabs.tabDecorations));
        field_149771_c.func_148756_a(62, "lit_furnace", (new BlockFurnace(true)).func_149711_c(3.5F).func_149672_a(field_149780_i).func_149715_a(0.875F).func_149663_c("furnace"));
        field_149771_c.func_148756_a(63, "standing_sign", (new BlockSign(TileEntitySign.class, true)).func_149711_c(1.0F).func_149672_a(field_149766_f).func_149663_c("sign").func_149649_H());
        field_149771_c.func_148756_a(64, "wooden_door", (new BlockDoor(Material.field_151575_d)).func_149711_c(3.0F).func_149672_a(field_149766_f).func_149663_c("doorWood").func_149649_H().func_149658_d("door_wood"));
        field_149771_c.func_148756_a(65, "ladder", (new BlockLadder()).func_149711_c(0.4F).func_149672_a(field_149774_o).func_149663_c("ladder").func_149658_d("ladder"));
        field_149771_c.func_148756_a(66, "rail", (new BlockRail()).func_149711_c(0.7F).func_149672_a(field_149777_j).func_149663_c("rail").func_149658_d("rail_normal"));
        field_149771_c.func_148756_a(67, "stone_stairs", (new BlockStairs(block, 0)).func_149663_c("stairsStone"));
        field_149771_c.func_148756_a(68, "wall_sign", (new BlockSign(TileEntitySign.class, false)).func_149711_c(1.0F).func_149672_a(field_149766_f).func_149663_c("sign").func_149649_H());
        field_149771_c.func_148756_a(69, "lever", (new BlockLever()).func_149711_c(0.5F).func_149672_a(field_149766_f).func_149663_c("lever").func_149658_d("lever"));
        field_149771_c.func_148756_a(70, "stone_pressure_plate", (new BlockPressurePlate("stone", Material.field_151576_e, BlockPressurePlate.Sensitivity.mobs)).func_149711_c(0.5F).func_149672_a(field_149780_i).func_149663_c("pressurePlate"));
        field_149771_c.func_148756_a(71, "iron_door", (new BlockDoor(Material.field_151573_f)).func_149711_c(5.0F).func_149672_a(field_149777_j).func_149663_c("doorIron").func_149649_H().func_149658_d("door_iron"));
        field_149771_c.func_148756_a(72, "wooden_pressure_plate", (new BlockPressurePlate("planks_oak", Material.field_151575_d, BlockPressurePlate.Sensitivity.everything)).func_149711_c(0.5F).func_149672_a(field_149766_f).func_149663_c("pressurePlate"));
        field_149771_c.func_148756_a(73, "redstone_ore", (new BlockRedstoneOre(false)).func_149711_c(3.0F).func_149752_b(5.0F).func_149672_a(field_149780_i).func_149663_c("oreRedstone").func_149647_a(CreativeTabs.tabBlock).func_149658_d("redstone_ore"));
        field_149771_c.func_148756_a(74, "lit_redstone_ore", (new BlockRedstoneOre(true)).func_149715_a(0.625F).func_149711_c(3.0F).func_149752_b(5.0F).func_149672_a(field_149780_i).func_149663_c("oreRedstone").func_149658_d("redstone_ore"));
        field_149771_c.func_148756_a(75, "unlit_redstone_torch", (new BlockRedstoneTorch(false)).func_149711_c(0.0F).func_149672_a(field_149766_f).func_149663_c("notGate").func_149658_d("redstone_torch_off"));
        field_149771_c.func_148756_a(76, "redstone_torch", (new BlockRedstoneTorch(true)).func_149711_c(0.0F).func_149715_a(0.5F).func_149672_a(field_149766_f).func_149663_c("notGate").func_149647_a(CreativeTabs.tabRedstone).func_149658_d("redstone_torch_on"));
        field_149771_c.func_148756_a(77, "stone_button", (new BlockButtonStone()).func_149711_c(0.5F).func_149672_a(field_149780_i).func_149663_c("button"));
        field_149771_c.func_148756_a(78, "snow_layer", (new BlockSnow()).func_149711_c(0.1F).func_149672_a(field_149773_n).func_149663_c("snow").func_149713_g(0).func_149658_d("snow"));
        field_149771_c.func_148756_a(79, "ice", (new BlockIce()).func_149711_c(0.5F).func_149713_g(3).func_149672_a(field_149778_k).func_149663_c("ice").func_149658_d("ice"));
        field_149771_c.func_148756_a(80, "snow", (new BlockSnowBlock()).func_149711_c(0.2F).func_149672_a(field_149773_n).func_149663_c("snow").func_149658_d("snow"));
        field_149771_c.func_148756_a(81, "cactus", (new BlockCactus()).func_149711_c(0.4F).func_149672_a(field_149775_l).func_149663_c("cactus").func_149658_d("cactus"));
        field_149771_c.func_148756_a(82, "clay", (new BlockClay()).func_149711_c(0.6F).func_149672_a(field_149767_g).func_149663_c("clay").func_149658_d("clay"));
        field_149771_c.func_148756_a(83, "reeds", (new BlockReed()).func_149711_c(0.0F).func_149672_a(field_149779_h).func_149663_c("reeds").func_149649_H().func_149658_d("reeds"));
        field_149771_c.func_148756_a(84, "jukebox", (new BlockJukebox()).func_149711_c(2.0F).func_149752_b(10.0F).func_149672_a(field_149780_i).func_149663_c("jukebox").func_149658_d("jukebox"));
        field_149771_c.func_148756_a(85, "fence", (new BlockFence("planks_oak", Material.field_151575_d)).func_149711_c(2.0F).func_149752_b(5.0F).func_149672_a(field_149766_f).func_149663_c("fence"));
        Block block5 = (new BlockPumpkin(false)).func_149711_c(1.0F).func_149672_a(field_149766_f).func_149663_c("pumpkin").func_149658_d("pumpkin");
        field_149771_c.func_148756_a(86, "pumpkin", block5);
        field_149771_c.func_148756_a(87, "netherrack", (new BlockNetherrack()).func_149711_c(0.4F).func_149672_a(field_149780_i).func_149663_c("hellrock").func_149658_d("netherrack"));
        field_149771_c.func_148756_a(88, "soul_sand", (new BlockSoulSand()).func_149711_c(0.5F).func_149672_a(field_149776_m).func_149663_c("hellsand").func_149658_d("soul_sand"));
        field_149771_c.func_148756_a(89, "glowstone", (new BlockGlowstone(Material.field_151592_s)).func_149711_c(0.3F).func_149672_a(field_149778_k).func_149715_a(1.0F).func_149663_c("lightgem").func_149658_d("glowstone"));
        field_149771_c.func_148756_a(90, "portal", (new BlockPortal()).func_149711_c(-1.0F).func_149672_a(field_149778_k).func_149715_a(0.75F).func_149663_c("portal").func_149658_d("portal"));
        field_149771_c.func_148756_a(91, "lit_pumpkin", (new BlockPumpkin(true)).func_149711_c(1.0F).func_149672_a(field_149766_f).func_149715_a(1.0F).func_149663_c("litpumpkin").func_149658_d("pumpkin"));
        field_149771_c.func_148756_a(92, "cake", (new BlockCake()).func_149711_c(0.5F).func_149672_a(field_149775_l).func_149663_c("cake").func_149649_H().func_149658_d("cake"));
        field_149771_c.func_148756_a(93, "unpowered_repeater", (new BlockRedstoneRepeater(false)).func_149711_c(0.0F).func_149672_a(field_149766_f).func_149663_c("diode").func_149649_H().func_149658_d("repeater_off"));
        field_149771_c.func_148756_a(94, "powered_repeater", (new BlockRedstoneRepeater(true)).func_149711_c(0.0F).func_149715_a(0.625F).func_149672_a(field_149766_f).func_149663_c("diode").func_149649_H().func_149658_d("repeater_on"));
        field_149771_c.func_148756_a(95, "stained_glass", (new BlockStainedGlass(Material.field_151592_s)).func_149711_c(0.3F).func_149672_a(field_149778_k).func_149663_c("stainedGlass").func_149658_d("glass"));
        field_149771_c.func_148756_a(96, "trapdoor", (new BlockTrapDoor(Material.field_151575_d)).func_149711_c(3.0F).func_149672_a(field_149766_f).func_149663_c("trapdoor").func_149649_H().func_149658_d("trapdoor"));
        field_149771_c.func_148756_a(97, "monster_egg", (new BlockSilverfish()).func_149711_c(0.75F).func_149663_c("monsterStoneEgg"));
        Block block6 = (new BlockStoneBrick()).func_149711_c(1.5F).func_149752_b(10.0F).func_149672_a(field_149780_i).func_149663_c("stonebricksmooth").func_149658_d("stonebrick");
        field_149771_c.func_148756_a(98, "stonebrick", block6);
        field_149771_c.func_148756_a(99, "brown_mushroom_block", (new BlockHugeMushroom(Material.field_151575_d, 0)).func_149711_c(0.2F).func_149672_a(field_149766_f).func_149663_c("mushroom").func_149658_d("mushroom_block"));
        field_149771_c.func_148756_a(100, "red_mushroom_block", (new BlockHugeMushroom(Material.field_151575_d, 1)).func_149711_c(0.2F).func_149672_a(field_149766_f).func_149663_c("mushroom").func_149658_d("mushroom_block"));
        field_149771_c.func_148756_a(101, "iron_bars", (new BlockPane("iron_bars", "iron_bars", Material.field_151573_f, true)).func_149711_c(5.0F).func_149752_b(10.0F).func_149672_a(field_149777_j).func_149663_c("fenceIron"));
        field_149771_c.func_148756_a(102, "glass_pane", (new BlockPane("glass", "glass_pane_top", Material.field_151592_s, false)).func_149711_c(0.3F).func_149672_a(field_149778_k).func_149663_c("thinGlass"));
        Block block7 = (new BlockMelon()).func_149711_c(1.0F).func_149672_a(field_149766_f).func_149663_c("melon").func_149658_d("melon");
        field_149771_c.func_148756_a(103, "melon_block", block7);
        field_149771_c.func_148756_a(104, "pumpkin_stem", (new BlockStem(block5)).func_149711_c(0.0F).func_149672_a(field_149766_f).func_149663_c("pumpkinStem").func_149658_d("pumpkin_stem"));
        field_149771_c.func_148756_a(105, "melon_stem", (new BlockStem(block7)).func_149711_c(0.0F).func_149672_a(field_149766_f).func_149663_c("pumpkinStem").func_149658_d("melon_stem"));
        field_149771_c.func_148756_a(106, "vine", (new BlockVine()).func_149711_c(0.2F).func_149672_a(field_149779_h).func_149663_c("vine").func_149658_d("vine"));
        field_149771_c.func_148756_a(107, "fence_gate", (new BlockFenceGate()).func_149711_c(2.0F).func_149752_b(5.0F).func_149672_a(field_149766_f).func_149663_c("fenceGate"));
        field_149771_c.func_148756_a(108, "brick_stairs", (new BlockStairs(block3, 0)).func_149663_c("stairsBrick"));
        field_149771_c.func_148756_a(109, "stone_brick_stairs", (new BlockStairs(block6, 0)).func_149663_c("stairsStoneBrickSmooth"));
        field_149771_c.func_148756_a(110, "mycelium", (new BlockMycelium()).func_149711_c(0.6F).func_149672_a(field_149779_h).func_149663_c("mycel").func_149658_d("mycelium"));
        field_149771_c.func_148756_a(111, "waterlily", (new BlockLilyPad()).func_149711_c(0.0F).func_149672_a(field_149779_h).func_149663_c("waterlily").func_149658_d("waterlily"));
        Block block8 = (new Block(Material.field_151576_e)).func_149711_c(2.0F).func_149752_b(10.0F).func_149672_a(field_149780_i).func_149663_c("netherBrick").func_149647_a(CreativeTabs.tabBlock).func_149658_d("nether_brick");
        field_149771_c.func_148756_a(112, "nether_brick", block8);
        field_149771_c.func_148756_a(113, "nether_brick_fence", (new BlockFence("nether_brick", Material.field_151576_e)).func_149711_c(2.0F).func_149752_b(10.0F).func_149672_a(field_149780_i).func_149663_c("netherFence"));
        field_149771_c.func_148756_a(114, "nether_brick_stairs", (new BlockStairs(block8, 0)).func_149663_c("stairsNetherBrick"));
        field_149771_c.func_148756_a(115, "nether_wart", (new BlockNetherWart()).func_149663_c("netherStalk").func_149658_d("nether_wart"));
        field_149771_c.func_148756_a(116, "enchanting_table", (new BlockEnchantmentTable()).func_149711_c(5.0F).func_149752_b(2000.0F).func_149663_c("enchantmentTable").func_149658_d("enchanting_table"));
        field_149771_c.func_148756_a(117, "brewing_stand", (new BlockBrewingStand()).func_149711_c(0.5F).func_149715_a(0.125F).func_149663_c("brewingStand").func_149658_d("brewing_stand"));
        field_149771_c.func_148756_a(118, "cauldron", (new BlockCauldron()).func_149711_c(2.0F).func_149663_c("cauldron").func_149658_d("cauldron"));
        field_149771_c.func_148756_a(119, "end_portal", (new BlockEndPortal(Material.field_151567_E)).func_149711_c(-1.0F).func_149752_b(6000000.0F));
        field_149771_c.func_148756_a(120, "end_portal_frame", (new BlockEndPortalFrame()).func_149672_a(field_149778_k).func_149715_a(0.125F).func_149711_c(-1.0F).func_149663_c("endPortalFrame").func_149752_b(6000000.0F).func_149647_a(CreativeTabs.tabDecorations).func_149658_d("endframe"));
        field_149771_c.func_148756_a(121, "end_stone", (new Block(Material.field_151576_e)).func_149711_c(3.0F).func_149752_b(15.0F).func_149672_a(field_149780_i).func_149663_c("whiteStone").func_149647_a(CreativeTabs.tabBlock).func_149658_d("end_stone"));
        field_149771_c.func_148756_a(122, "dragon_egg", (new BlockDragonEgg()).func_149711_c(3.0F).func_149752_b(15.0F).func_149672_a(field_149780_i).func_149715_a(0.125F).func_149663_c("dragonEgg").func_149658_d("dragon_egg"));
        field_149771_c.func_148756_a(123, "redstone_lamp", (new BlockRedstoneLight(false)).func_149711_c(0.3F).func_149672_a(field_149778_k).func_149663_c("redstoneLight").func_149647_a(CreativeTabs.tabRedstone).func_149658_d("redstone_lamp_off"));
        field_149771_c.func_148756_a(124, "lit_redstone_lamp", (new BlockRedstoneLight(true)).func_149711_c(0.3F).func_149672_a(field_149778_k).func_149663_c("redstoneLight").func_149658_d("redstone_lamp_on"));
        field_149771_c.func_148756_a(125, "double_wooden_slab", (new BlockWoodSlab(true)).func_149711_c(2.0F).func_149752_b(5.0F).func_149672_a(field_149766_f).func_149663_c("woodSlab"));
        field_149771_c.func_148756_a(126, "wooden_slab", (new BlockWoodSlab(false)).func_149711_c(2.0F).func_149752_b(5.0F).func_149672_a(field_149766_f).func_149663_c("woodSlab"));
        field_149771_c.func_148756_a(127, "cocoa", (new BlockCocoa()).func_149711_c(0.2F).func_149752_b(5.0F).func_149672_a(field_149766_f).func_149663_c("cocoa").func_149658_d("cocoa"));
        field_149771_c.func_148756_a(128, "sandstone_stairs", (new BlockStairs(block2, 0)).func_149663_c("stairsSandStone"));
        field_149771_c.func_148756_a(129, "emerald_ore", (new BlockOre()).func_149711_c(3.0F).func_149752_b(5.0F).func_149672_a(field_149780_i).func_149663_c("oreEmerald").func_149658_d("emerald_ore"));
        field_149771_c.func_148756_a(130, "ender_chest", (new BlockEnderChest()).func_149711_c(22.5F).func_149752_b(1000.0F).func_149672_a(field_149780_i).func_149663_c("enderChest").func_149715_a(0.5F));
        field_149771_c.func_148756_a(131, "tripwire_hook", (new BlockTripWireHook()).func_149663_c("tripWireSource").func_149658_d("trip_wire_source"));
        field_149771_c.func_148756_a(132, "tripwire", (new BlockTripWire()).func_149663_c("tripWire").func_149658_d("trip_wire"));
        field_149771_c.func_148756_a(133, "emerald_block", (new BlockCompressed(MapColor.field_151653_I)).func_149711_c(5.0F).func_149752_b(10.0F).func_149672_a(field_149777_j).func_149663_c("blockEmerald").func_149658_d("emerald_block"));
        field_149771_c.func_148756_a(134, "spruce_stairs", (new BlockStairs(block1, 1)).func_149663_c("stairsWoodSpruce"));
        field_149771_c.func_148756_a(135, "birch_stairs", (new BlockStairs(block1, 2)).func_149663_c("stairsWoodBirch"));
        field_149771_c.func_148756_a(136, "jungle_stairs", (new BlockStairs(block1, 3)).func_149663_c("stairsWoodJungle"));
        field_149771_c.func_148756_a(137, "command_block", (new BlockCommandBlock()).func_149722_s().func_149752_b(6000000.0F).func_149663_c("commandBlock").func_149658_d("command_block"));
        field_149771_c.func_148756_a(138, "beacon", (new BlockBeacon()).func_149663_c("beacon").func_149715_a(1.0F).func_149658_d("beacon"));
        field_149771_c.func_148756_a(139, "cobblestone_wall", (new BlockWall(block)).func_149663_c("cobbleWall"));
        field_149771_c.func_148756_a(140, "flower_pot", (new BlockFlowerPot()).func_149711_c(0.0F).func_149672_a(field_149769_e).func_149663_c("flowerPot").func_149658_d("flower_pot"));
        field_149771_c.func_148756_a(141, "carrots", (new BlockCarrot()).func_149663_c("carrots").func_149658_d("carrots"));
        field_149771_c.func_148756_a(142, "potatoes", (new BlockPotato()).func_149663_c("potatoes").func_149658_d("potatoes"));
        field_149771_c.func_148756_a(143, "wooden_button", (new BlockButtonWood()).func_149711_c(0.5F).func_149672_a(field_149766_f).func_149663_c("button"));
        field_149771_c.func_148756_a(144, "skull", (new BlockSkull()).func_149711_c(1.0F).func_149672_a(field_149780_i).func_149663_c("skull").func_149658_d("skull"));
        field_149771_c.func_148756_a(145, "anvil", (new BlockAnvil()).func_149711_c(5.0F).func_149672_a(field_149788_p).func_149752_b(2000.0F).func_149663_c("anvil"));
        field_149771_c.func_148756_a(146, "trapped_chest", (new BlockChest(1)).func_149711_c(2.5F).func_149672_a(field_149766_f).func_149663_c("chestTrap"));
        field_149771_c.func_148756_a(147, "light_weighted_pressure_plate", (new BlockPressurePlateWeighted("gold_block", Material.field_151573_f, 15)).func_149711_c(0.5F).func_149672_a(field_149766_f).func_149663_c("weightedPlate_light"));
        field_149771_c.func_148756_a(148, "heavy_weighted_pressure_plate", (new BlockPressurePlateWeighted("iron_block", Material.field_151573_f, 150)).func_149711_c(0.5F).func_149672_a(field_149766_f).func_149663_c("weightedPlate_heavy"));
        field_149771_c.func_148756_a(149, "unpowered_comparator", (new BlockRedstoneComparator(false)).func_149711_c(0.0F).func_149672_a(field_149766_f).func_149663_c("comparator").func_149649_H().func_149658_d("comparator_off"));
        field_149771_c.func_148756_a(150, "powered_comparator", (new BlockRedstoneComparator(true)).func_149711_c(0.0F).func_149715_a(0.625F).func_149672_a(field_149766_f).func_149663_c("comparator").func_149649_H().func_149658_d("comparator_on"));
        field_149771_c.func_148756_a(151, "daylight_detector", (new BlockDaylightDetector()).func_149711_c(0.2F).func_149672_a(field_149766_f).func_149663_c("daylightDetector").func_149658_d("daylight_detector"));
        field_149771_c.func_148756_a(152, "redstone_block", (new BlockCompressedPowered(MapColor.field_151656_f)).func_149711_c(5.0F).func_149752_b(10.0F).func_149672_a(field_149777_j).func_149663_c("blockRedstone").func_149658_d("redstone_block"));
        field_149771_c.func_148756_a(153, "quartz_ore", (new BlockOre()).func_149711_c(3.0F).func_149752_b(5.0F).func_149672_a(field_149780_i).func_149663_c("netherquartz").func_149658_d("quartz_ore"));
        field_149771_c.func_148756_a(154, "hopper", (new BlockHopper()).func_149711_c(3.0F).func_149752_b(8.0F).func_149672_a(field_149766_f).func_149663_c("hopper").func_149658_d("hopper"));
        Block block9 = (new BlockQuartz()).func_149672_a(field_149780_i).func_149711_c(0.8F).func_149663_c("quartzBlock").func_149658_d("quartz_block");
        field_149771_c.func_148756_a(155, "quartz_block", block9);
        field_149771_c.func_148756_a(156, "quartz_stairs", (new BlockStairs(block9, 0)).func_149663_c("stairsQuartz"));
        field_149771_c.func_148756_a(157, "activator_rail", (new BlockRailPowered()).func_149711_c(0.7F).func_149672_a(field_149777_j).func_149663_c("activatorRail").func_149658_d("rail_activator"));
        field_149771_c.func_148756_a(158, "dropper", (new BlockDropper()).func_149711_c(3.5F).func_149672_a(field_149780_i).func_149663_c("dropper").func_149658_d("dropper"));
        field_149771_c.func_148756_a(159, "stained_hardened_clay", (new BlockColored(Material.field_151576_e)).func_149711_c(1.25F).func_149752_b(7.0F).func_149672_a(field_149780_i).func_149663_c("clayHardenedStained").func_149658_d("hardened_clay_stained"));
        field_149771_c.func_148756_a(160, "stained_glass_pane", (new BlockStainedGlassPane()).func_149711_c(0.3F).func_149672_a(field_149778_k).func_149663_c("thinStainedGlass").func_149658_d("glass"));
        field_149771_c.func_148756_a(161, "leaves2", (new BlockNewLeaf()).func_149663_c("leaves").func_149658_d("leaves"));
        field_149771_c.func_148756_a(162, "log2", (new BlockNewLog()).func_149663_c("log").func_149658_d("log"));
        field_149771_c.func_148756_a(163, "acacia_stairs", (new BlockStairs(block1, 4)).func_149663_c("stairsWoodAcacia"));
        field_149771_c.func_148756_a(164, "dark_oak_stairs", (new BlockStairs(block1, 5)).func_149663_c("stairsWoodDarkOak"));
        field_149771_c.func_148756_a(170, "hay_block", (new BlockHay()).func_149711_c(0.5F).func_149672_a(field_149779_h).func_149663_c("hayBlock").func_149647_a(CreativeTabs.tabBlock).func_149658_d("hay_block"));
        field_149771_c.func_148756_a(171, "carpet", (new BlockCarpet()).func_149711_c(0.1F).func_149672_a(field_149775_l).func_149663_c("woolCarpet").func_149713_g(0));
        field_149771_c.func_148756_a(172, "hardened_clay", (new BlockHardenedClay()).func_149711_c(1.25F).func_149752_b(7.0F).func_149672_a(field_149780_i).func_149663_c("clayHardened").func_149658_d("hardened_clay"));
        field_149771_c.func_148756_a(173, "coal_block", (new Block(Material.field_151576_e)).func_149711_c(5.0F).func_149752_b(10.0F).func_149672_a(field_149780_i).func_149663_c("blockCoal").func_149647_a(CreativeTabs.tabBlock).func_149658_d("coal_block"));
        field_149771_c.func_148756_a(174, "packed_ice", (new BlockPackedIce()).func_149711_c(0.5F).func_149672_a(field_149778_k).func_149663_c("icePacked").func_149658_d("ice_packed"));
        field_149771_c.func_148756_a(175, "double_plant", new BlockDoublePlant());
        Iterator iterator = field_149771_c.iterator();

        while (iterator.hasNext())
        {
            Block block10 = (Block)iterator.next();

            if (block10.field_149764_J == Material.field_151579_a)
            {
                block10.field_149783_u = false;
            }
            else
            {
                boolean flag = false;
                boolean flag1 = block10.func_149645_b() == 10;
                boolean flag2 = block10 instanceof BlockSlab;
                boolean flag3 = block10 == block4;
                boolean flag4 = block10.field_149785_s;
                boolean flag5 = block10.field_149786_r == 0;

                if (flag1 || flag2 || flag3 || flag4 || flag5)
                {
                    flag = true;
                }

                block10.field_149783_u = flag;
            }
        }
    }

    protected Block(Material p_i45394_1_)
    {
        this.field_149762_H = field_149769_e;
        this.field_149763_I = 1.0F;
        this.field_149765_K = 0.6F;
        this.field_149764_J = p_i45394_1_;
        this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        this.field_149787_q = this.func_149662_c();
        this.field_149786_r = this.func_149662_c() ? 255 : 0;
        this.field_149785_s = !p_i45394_1_.getCanBlockGrass();
    }

    public Block func_149672_a(Block.SoundType p_149672_1_)
    {
        this.field_149762_H = p_149672_1_;
        return this;
    }

    public Block func_149713_g(int p_149713_1_)
    {
        this.field_149786_r = p_149713_1_;
        return this;
    }

    public Block func_149715_a(float p_149715_1_)
    {
        this.field_149784_t = (int)(15.0F * p_149715_1_);
        return this;
    }

    public Block func_149752_b(float p_149752_1_)
    {
        this.field_149781_w = p_149752_1_ * 3.0F;
        return this;
    }

    @SideOnly(Side.CLIENT)
    public boolean func_149637_q()
    {
        return this.field_149764_J.blocksMovement() && this.func_149686_d();
    }

    public boolean func_149721_r()
    {
        return this.field_149764_J.isOpaque() && this.func_149686_d() && !this.func_149744_f();
    }

    public boolean func_149686_d()
    {
        return true;
    }

    public boolean func_149655_b(IBlockAccess p_149655_1_, int p_149655_2_, int p_149655_3_, int p_149655_4_)
    {
        return !this.field_149764_J.blocksMovement();
    }

    public int func_149645_b()
    {
        return 0;
    }

    public Block func_149711_c(float p_149711_1_)
    {
        this.field_149782_v = p_149711_1_;

        if (this.field_149781_w < p_149711_1_ * 5.0F)
        {
            this.field_149781_w = p_149711_1_ * 5.0F;
        }

        return this;
    }

    public Block func_149722_s()
    {
        this.func_149711_c(-1.0F);
        return this;
    }

    public float func_149712_f(World p_149712_1_, int p_149712_2_, int p_149712_3_, int p_149712_4_)
    {
        return this.field_149782_v;
    }

    public Block func_149675_a(boolean p_149675_1_)
    {
        this.field_149789_z = p_149675_1_;
        return this;
    }

    public boolean func_149653_t()
    {
        return this.field_149789_z;
    }

    @Deprecated //Forge: New Metadata sensitive version.
    public boolean func_149716_u()
    {
        return hasTileEntity(0);
    }

    public final void func_149676_a(float p_149676_1_, float p_149676_2_, float p_149676_3_, float p_149676_4_, float p_149676_5_, float p_149676_6_)
    {
        this.field_149759_B = (double)p_149676_1_;
        this.field_149760_C = (double)p_149676_2_;
        this.field_149754_D = (double)p_149676_3_;
        this.field_149755_E = (double)p_149676_4_;
        this.field_149756_F = (double)p_149676_5_;
        this.field_149757_G = (double)p_149676_6_;
    }

    @SideOnly(Side.CLIENT)
    public int func_149677_c(IBlockAccess p_149677_1_, int p_149677_2_, int p_149677_3_, int p_149677_4_)
    {
        Block block = p_149677_1_.func_147439_a(p_149677_2_, p_149677_3_, p_149677_4_);
        int l = p_149677_1_.getLightBrightnessForSkyBlocks(p_149677_2_, p_149677_3_, p_149677_4_, block.getLightValue(p_149677_1_, p_149677_2_, p_149677_3_, p_149677_4_));

        if (l == 0 && block instanceof BlockSlab)
        {
            --p_149677_3_;
            block = p_149677_1_.func_147439_a(p_149677_2_, p_149677_3_, p_149677_4_);
            return p_149677_1_.getLightBrightnessForSkyBlocks(p_149677_2_, p_149677_3_, p_149677_4_, block.getLightValue(p_149677_1_, p_149677_2_, p_149677_3_, p_149677_4_));
        }
        else
        {
            return l;
        }
    }

    @SideOnly(Side.CLIENT)
    public boolean func_149646_a(IBlockAccess p_149646_1_, int p_149646_2_, int p_149646_3_, int p_149646_4_, int p_149646_5_)
    {
        return p_149646_5_ == 0 && this.field_149760_C > 0.0D ? true : (p_149646_5_ == 1 && this.field_149756_F < 1.0D ? true : (p_149646_5_ == 2 && this.field_149754_D > 0.0D ? true : (p_149646_5_ == 3 && this.field_149757_G < 1.0D ? true : (p_149646_5_ == 4 && this.field_149759_B > 0.0D ? true : (p_149646_5_ == 5 && this.field_149755_E < 1.0D ? true : !p_149646_1_.func_147439_a(p_149646_2_, p_149646_3_, p_149646_4_).func_149662_c())))));
    }

    public boolean func_149747_d(IBlockAccess p_149747_1_, int p_149747_2_, int p_149747_3_, int p_149747_4_, int p_149747_5_)
    {
        return p_149747_1_.func_147439_a(p_149747_2_, p_149747_3_, p_149747_4_).func_149688_o().isSolid();
    }

    @SideOnly(Side.CLIENT)
    public IIcon func_149673_e(IBlockAccess p_149673_1_, int p_149673_2_, int p_149673_3_, int p_149673_4_, int p_149673_5_)
    {
        return this.func_149691_a(p_149673_5_, p_149673_1_.getBlockMetadata(p_149673_2_, p_149673_3_, p_149673_4_));
    }

    @SideOnly(Side.CLIENT)
    public IIcon func_149691_a(int p_149691_1_, int p_149691_2_)
    {
        return this.field_149761_L;
    }

    public void func_149743_a(World p_149743_1_, int p_149743_2_, int p_149743_3_, int p_149743_4_, AxisAlignedBB p_149743_5_, List p_149743_6_, Entity p_149743_7_)
    {
        AxisAlignedBB axisalignedbb1 = this.func_149668_a(p_149743_1_, p_149743_2_, p_149743_3_, p_149743_4_);

        if (axisalignedbb1 != null && p_149743_5_.intersectsWith(axisalignedbb1))
        {
            p_149743_6_.add(axisalignedbb1);
        }
    }

    public AxisAlignedBB func_149668_a(World p_149668_1_, int p_149668_2_, int p_149668_3_, int p_149668_4_)
    {
        return AxisAlignedBB.getAABBPool().getAABB((double)p_149668_2_ + this.field_149759_B, (double)p_149668_3_ + this.field_149760_C, (double)p_149668_4_ + this.field_149754_D, (double)p_149668_2_ + this.field_149755_E, (double)p_149668_3_ + this.field_149756_F, (double)p_149668_4_ + this.field_149757_G);
    }

    @SideOnly(Side.CLIENT)
    public final IIcon func_149733_h(int p_149733_1_)
    {
        return this.func_149691_a(p_149733_1_, 0);
    }

    @SideOnly(Side.CLIENT)
    public AxisAlignedBB func_149633_g(World p_149633_1_, int p_149633_2_, int p_149633_3_, int p_149633_4_)
    {
        return AxisAlignedBB.getAABBPool().getAABB((double)p_149633_2_ + this.field_149759_B, (double)p_149633_3_ + this.field_149760_C, (double)p_149633_4_ + this.field_149754_D, (double)p_149633_2_ + this.field_149755_E, (double)p_149633_3_ + this.field_149756_F, (double)p_149633_4_ + this.field_149757_G);
    }

    public boolean func_149662_c()
    {
        return true;
    }

    public boolean func_149678_a(int p_149678_1_, boolean p_149678_2_)
    {
        return this.func_149703_v();
    }

    public boolean func_149703_v()
    {
        return true;
    }

    public void func_149674_a(World p_149674_1_, int p_149674_2_, int p_149674_3_, int p_149674_4_, Random p_149674_5_) {}

    @SideOnly(Side.CLIENT)
    public void func_149734_b(World p_149734_1_, int p_149734_2_, int p_149734_3_, int p_149734_4_, Random p_149734_5_) {}

    public void func_149664_b(World p_149664_1_, int p_149664_2_, int p_149664_3_, int p_149664_4_, int p_149664_5_) {}

    public void func_149695_a(World p_149695_1_, int p_149695_2_, int p_149695_3_, int p_149695_4_, Block p_149695_5_) {}

    public int func_149738_a(World p_149738_1_)
    {
        return 10;
    }

    public void func_149726_b(World p_149726_1_, int p_149726_2_, int p_149726_3_, int p_149726_4_) {}

    public void func_149749_a(World p_149749_1_, int p_149749_2_, int p_149749_3_, int p_149749_4_, Block p_149749_5_, int p_149749_6_)
    {
        if (hasTileEntity(p_149749_6_) && !(this instanceof BlockContainer))
        {
            p_149749_1_.func_147475_p(p_149749_2_, p_149749_3_, p_149749_4_);
        }
    }

    public int func_149745_a(Random p_149745_1_)
    {
        return 1;
    }

    public Item func_149650_a(int p_149650_1_, Random p_149650_2_, int p_149650_3_)
    {
        return Item.func_150898_a(this);
    }

    public float func_149737_a(EntityPlayer p_149737_1_, World p_149737_2_, int p_149737_3_, int p_149737_4_, int p_149737_5_)
    {
        return ForgeHooks.blockStrength(this, p_149737_1_, p_149737_2_, p_149737_3_, p_149737_4_, p_149737_5_);
    }

    public final void func_149697_b(World p_149697_1_, int p_149697_2_, int p_149697_3_, int p_149697_4_, int p_149697_5_, int p_149697_6_)
    {
        this.func_149690_a(p_149697_1_, p_149697_2_, p_149697_3_, p_149697_4_, p_149697_5_, 1.0F, p_149697_6_);
    }

    public void func_149690_a(World p_149690_1_, int p_149690_2_, int p_149690_3_, int p_149690_4_, int p_149690_5_, float p_149690_6_, int p_149690_7_)
    {
        if (!p_149690_1_.isRemote)
        {
            ArrayList<ItemStack> items = getDrops(p_149690_1_, p_149690_2_, p_149690_3_, p_149690_4_, p_149690_5_, p_149690_7_);
            p_149690_6_ = ForgeEventFactory.fireBlockHarvesting(items, p_149690_1_, this, p_149690_2_, p_149690_3_, p_149690_4_, p_149690_5_, p_149690_7_, p_149690_6_, false, harvesters.get());

            for (ItemStack item : items)
            {
                if (p_149690_1_.rand.nextFloat() <= p_149690_6_)
                {
                    this.func_149642_a(p_149690_1_, p_149690_2_, p_149690_3_, p_149690_4_, item);
                }
            }
        }
    }

    protected void func_149642_a(World p_149642_1_, int p_149642_2_, int p_149642_3_, int p_149642_4_, ItemStack p_149642_5_)
    {
        if (!p_149642_1_.isRemote && p_149642_1_.getGameRules().getGameRuleBooleanValue("doTileDrops"))
        {
            float f = 0.7F;
            double d0 = (double)(p_149642_1_.rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
            double d1 = (double)(p_149642_1_.rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
            double d2 = (double)(p_149642_1_.rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
            EntityItem entityitem = new EntityItem(p_149642_1_, (double)p_149642_2_ + d0, (double)p_149642_3_ + d1, (double)p_149642_4_ + d2, p_149642_5_);
            entityitem.field_145804_b = 10;
            p_149642_1_.spawnEntityInWorld(entityitem);
        }
    }

    public void func_149657_c(World p_149657_1_, int p_149657_2_, int p_149657_3_, int p_149657_4_, int p_149657_5_)
    {
        if (!p_149657_1_.isRemote)
        {
            while (p_149657_5_ > 0)
            {
                int i1 = EntityXPOrb.getXPSplit(p_149657_5_);
                p_149657_5_ -= i1;
                p_149657_1_.spawnEntityInWorld(new EntityXPOrb(p_149657_1_, (double)p_149657_2_ + 0.5D, (double)p_149657_3_ + 0.5D, (double)p_149657_4_ + 0.5D, i1));
            }
        }
    }

    public int func_149692_a(int p_149692_1_)
    {
        return 0;
    }

    public float func_149638_a(Entity p_149638_1_)
    {
        return this.field_149781_w / 5.0F;
    }

    public MovingObjectPosition func_149731_a(World p_149731_1_, int p_149731_2_, int p_149731_3_, int p_149731_4_, Vec3 p_149731_5_, Vec3 p_149731_6_)
    {
        this.func_149719_a(p_149731_1_, p_149731_2_, p_149731_3_, p_149731_4_);
        p_149731_5_ = p_149731_5_.addVector((double)(-p_149731_2_), (double)(-p_149731_3_), (double)(-p_149731_4_));
        p_149731_6_ = p_149731_6_.addVector((double)(-p_149731_2_), (double)(-p_149731_3_), (double)(-p_149731_4_));
        Vec3 vec32 = p_149731_5_.getIntermediateWithXValue(p_149731_6_, this.field_149759_B);
        Vec3 vec33 = p_149731_5_.getIntermediateWithXValue(p_149731_6_, this.field_149755_E);
        Vec3 vec34 = p_149731_5_.getIntermediateWithYValue(p_149731_6_, this.field_149760_C);
        Vec3 vec35 = p_149731_5_.getIntermediateWithYValue(p_149731_6_, this.field_149756_F);
        Vec3 vec36 = p_149731_5_.getIntermediateWithZValue(p_149731_6_, this.field_149754_D);
        Vec3 vec37 = p_149731_5_.getIntermediateWithZValue(p_149731_6_, this.field_149757_G);

        if (!this.func_149654_a(vec32))
        {
            vec32 = null;
        }

        if (!this.func_149654_a(vec33))
        {
            vec33 = null;
        }

        if (!this.func_149687_b(vec34))
        {
            vec34 = null;
        }

        if (!this.func_149687_b(vec35))
        {
            vec35 = null;
        }

        if (!this.func_149661_c(vec36))
        {
            vec36 = null;
        }

        if (!this.func_149661_c(vec37))
        {
            vec37 = null;
        }

        Vec3 vec38 = null;

        if (vec32 != null && (vec38 == null || p_149731_5_.squareDistanceTo(vec32) < p_149731_5_.squareDistanceTo(vec38)))
        {
            vec38 = vec32;
        }

        if (vec33 != null && (vec38 == null || p_149731_5_.squareDistanceTo(vec33) < p_149731_5_.squareDistanceTo(vec38)))
        {
            vec38 = vec33;
        }

        if (vec34 != null && (vec38 == null || p_149731_5_.squareDistanceTo(vec34) < p_149731_5_.squareDistanceTo(vec38)))
        {
            vec38 = vec34;
        }

        if (vec35 != null && (vec38 == null || p_149731_5_.squareDistanceTo(vec35) < p_149731_5_.squareDistanceTo(vec38)))
        {
            vec38 = vec35;
        }

        if (vec36 != null && (vec38 == null || p_149731_5_.squareDistanceTo(vec36) < p_149731_5_.squareDistanceTo(vec38)))
        {
            vec38 = vec36;
        }

        if (vec37 != null && (vec38 == null || p_149731_5_.squareDistanceTo(vec37) < p_149731_5_.squareDistanceTo(vec38)))
        {
            vec38 = vec37;
        }

        if (vec38 == null)
        {
            return null;
        }
        else
        {
            byte b0 = -1;

            if (vec38 == vec32)
            {
                b0 = 4;
            }

            if (vec38 == vec33)
            {
                b0 = 5;
            }

            if (vec38 == vec34)
            {
                b0 = 0;
            }

            if (vec38 == vec35)
            {
                b0 = 1;
            }

            if (vec38 == vec36)
            {
                b0 = 2;
            }

            if (vec38 == vec37)
            {
                b0 = 3;
            }

            return new MovingObjectPosition(p_149731_2_, p_149731_3_, p_149731_4_, b0, vec38.addVector((double)p_149731_2_, (double)p_149731_3_, (double)p_149731_4_));
        }
    }

    private boolean func_149654_a(Vec3 p_149654_1_)
    {
        return p_149654_1_ == null ? false : p_149654_1_.yCoord >= this.field_149760_C && p_149654_1_.yCoord <= this.field_149756_F && p_149654_1_.zCoord >= this.field_149754_D && p_149654_1_.zCoord <= this.field_149757_G;
    }

    private boolean func_149687_b(Vec3 p_149687_1_)
    {
        return p_149687_1_ == null ? false : p_149687_1_.xCoord >= this.field_149759_B && p_149687_1_.xCoord <= this.field_149755_E && p_149687_1_.zCoord >= this.field_149754_D && p_149687_1_.zCoord <= this.field_149757_G;
    }

    private boolean func_149661_c(Vec3 p_149661_1_)
    {
        return p_149661_1_ == null ? false : p_149661_1_.xCoord >= this.field_149759_B && p_149661_1_.xCoord <= this.field_149755_E && p_149661_1_.yCoord >= this.field_149760_C && p_149661_1_.yCoord <= this.field_149756_F;
    }

    public void func_149723_a(World p_149723_1_, int p_149723_2_, int p_149723_3_, int p_149723_4_, Explosion p_149723_5_) {}

    public boolean func_149705_a(World p_149705_1_, int p_149705_2_, int p_149705_3_, int p_149705_4_, int p_149705_5_, ItemStack p_149705_6_)
    {
        return this.func_149707_d(p_149705_1_, p_149705_2_, p_149705_3_, p_149705_4_, p_149705_5_);
    }

    @SideOnly(Side.CLIENT)
    public int func_149701_w()
    {
        return 0;
    }

    public boolean func_149707_d(World p_149707_1_, int p_149707_2_, int p_149707_3_, int p_149707_4_, int p_149707_5_)
    {
        return this.func_149742_c(p_149707_1_, p_149707_2_, p_149707_3_, p_149707_4_);
    }

    public boolean func_149742_c(World p_149742_1_, int p_149742_2_, int p_149742_3_, int p_149742_4_)
    {
        return p_149742_1_.func_147439_a(p_149742_2_, p_149742_3_, p_149742_4_).isReplaceable(p_149742_1_, p_149742_2_, p_149742_3_, p_149742_4_);
    }

    public boolean func_149727_a(World p_149727_1_, int p_149727_2_, int p_149727_3_, int p_149727_4_, EntityPlayer p_149727_5_, int p_149727_6_, float p_149727_7_, float p_149727_8_, float p_149727_9_)
    {
        return false;
    }

    public void func_149724_b(World p_149724_1_, int p_149724_2_, int p_149724_3_, int p_149724_4_, Entity p_149724_5_) {}

    public int func_149660_a(World p_149660_1_, int p_149660_2_, int p_149660_3_, int p_149660_4_, int p_149660_5_, float p_149660_6_, float p_149660_7_, float p_149660_8_, int p_149660_9_)
    {
        return p_149660_9_;
    }

    public void func_149699_a(World p_149699_1_, int p_149699_2_, int p_149699_3_, int p_149699_4_, EntityPlayer p_149699_5_) {}

    public void func_149640_a(World p_149640_1_, int p_149640_2_, int p_149640_3_, int p_149640_4_, Entity p_149640_5_, Vec3 p_149640_6_) {}

    public void func_149719_a(IBlockAccess p_149719_1_, int p_149719_2_, int p_149719_3_, int p_149719_4_) {}

    public final double func_149704_x()
    {
        return this.field_149759_B;
    }

    public final double func_149753_y()
    {
        return this.field_149755_E;
    }

    public final double func_149665_z()
    {
        return this.field_149760_C;
    }

    public final double func_149669_A()
    {
        return this.field_149756_F;
    }

    public final double func_149706_B()
    {
        return this.field_149754_D;
    }

    public final double func_149693_C()
    {
        return this.field_149757_G;
    }

    @SideOnly(Side.CLIENT)
    public int func_149635_D()
    {
        return 16777215;
    }

    @SideOnly(Side.CLIENT)
    public int func_149741_i(int p_149741_1_)
    {
        return 16777215;
    }

    @SideOnly(Side.CLIENT)
    public int func_149720_d(IBlockAccess p_149720_1_, int p_149720_2_, int p_149720_3_, int p_149720_4_)
    {
        return 16777215;
    }

    public int func_149709_b(IBlockAccess p_149709_1_, int p_149709_2_, int p_149709_3_, int p_149709_4_, int p_149709_5_)
    {
        return 0;
    }

    public boolean func_149744_f()
    {
        return false;
    }

    public void func_149670_a(World p_149670_1_, int p_149670_2_, int p_149670_3_, int p_149670_4_, Entity p_149670_5_) {}

    public int func_149748_c(IBlockAccess p_149748_1_, int p_149748_2_, int p_149748_3_, int p_149748_4_, int p_149748_5_)
    {
        return 0;
    }

    public void func_149683_g() {}

    public void func_149636_a(World p_149636_1_, EntityPlayer p_149636_2_, int p_149636_3_, int p_149636_4_, int p_149636_5_, int p_149636_6_)
    {
        p_149636_2_.addStat(StatList.mineBlockStatArray[func_149682_b(this)], 1);
        p_149636_2_.addExhaustion(0.025F);

        if (this.canSilkHarvest(p_149636_1_, p_149636_2_, p_149636_3_, p_149636_4_, p_149636_5_, p_149636_6_) && EnchantmentHelper.getSilkTouchModifier(p_149636_2_))
        {
            ArrayList<ItemStack> items = new ArrayList<ItemStack>();
            ItemStack itemstack = this.func_149644_j(p_149636_6_);

            if (itemstack != null)
            {
                items.add(itemstack);
            }

            ForgeEventFactory.fireBlockHarvesting(items, p_149636_1_, this, p_149636_3_, p_149636_4_, p_149636_5_, p_149636_6_, 0, 1.0f, true, p_149636_2_);
            for (ItemStack is : items)
            {
                this.func_149642_a(p_149636_1_, p_149636_3_, p_149636_4_, p_149636_5_, is);
            }
        }
        else
        {
            harvesters.set(p_149636_2_);
            int i1 = EnchantmentHelper.getFortuneModifier(p_149636_2_);
            this.func_149697_b(p_149636_1_, p_149636_3_, p_149636_4_, p_149636_5_, p_149636_6_, i1);
            harvesters.set(null);
        }
    }

    protected boolean func_149700_E()
    {
        return this.func_149686_d() && !this.hasTileEntity(silk_check_meta.get());
    }

    protected ItemStack func_149644_j(int p_149644_1_)
    {
        int j = 0;
        Item item = Item.func_150898_a(this);

        if (item != null && item.getHasSubtypes())
        {
            j = p_149644_1_;
        }

        return new ItemStack(item, 1, j);
    }

    public int func_149679_a(int p_149679_1_, Random p_149679_2_)
    {
        return this.func_149745_a(p_149679_2_);
    }

    public boolean func_149718_j(World p_149718_1_, int p_149718_2_, int p_149718_3_, int p_149718_4_)
    {
        return true;
    }

    public void func_149689_a(World p_149689_1_, int p_149689_2_, int p_149689_3_, int p_149689_4_, EntityLivingBase p_149689_5_, ItemStack p_149689_6_) {}

    public void func_149714_e(World p_149714_1_, int p_149714_2_, int p_149714_3_, int p_149714_4_, int p_149714_5_) {}

    public Block func_149663_c(String p_149663_1_)
    {
        this.field_149770_b = p_149663_1_;
        return this;
    }

    public String func_149732_F()
    {
        return StatCollector.translateToLocal(this.func_149739_a() + ".name");
    }

    public String func_149739_a()
    {
        return "tile." + this.field_149770_b;
    }

    public boolean func_149696_a(World p_149696_1_, int p_149696_2_, int p_149696_3_, int p_149696_4_, int p_149696_5_, int p_149696_6_)
    {
        return false;
    }

    public boolean func_149652_G()
    {
        return this.field_149790_y;
    }

    protected Block func_149649_H()
    {
        this.field_149790_y = false;
        return this;
    }

    public int func_149656_h()
    {
        return this.field_149764_J.getMaterialMobility();
    }

    @SideOnly(Side.CLIENT)
    public float func_149685_I()
    {
        return this.func_149637_q() ? 0.2F : 1.0F;
    }

    public void func_149746_a(World p_149746_1_, int p_149746_2_, int p_149746_3_, int p_149746_4_, Entity p_149746_5_, float p_149746_6_) {}

    @SideOnly(Side.CLIENT)
    public Item func_149694_d(World p_149694_1_, int p_149694_2_, int p_149694_3_, int p_149694_4_)
    {
        return Item.func_150898_a(this);
    }

    public int func_149643_k(World p_149643_1_, int p_149643_2_, int p_149643_3_, int p_149643_4_)
    {
        return this.func_149692_a(p_149643_1_.getBlockMetadata(p_149643_2_, p_149643_3_, p_149643_4_));
    }

    @SideOnly(Side.CLIENT)
    public void func_149666_a(Item p_149666_1_, CreativeTabs p_149666_2_, List p_149666_3_)
    {
        p_149666_3_.add(new ItemStack(p_149666_1_, 1, 0));
    }

    public Block func_149647_a(CreativeTabs p_149647_1_)
    {
        this.field_149772_a = p_149647_1_;
        return this;
    }

    public void func_149681_a(World p_149681_1_, int p_149681_2_, int p_149681_3_, int p_149681_4_, int p_149681_5_, EntityPlayer p_149681_6_) {}

    @SideOnly(Side.CLIENT)
    public CreativeTabs func_149708_J()
    {
        return this.field_149772_a;
    }

    public void func_149725_f(World p_149725_1_, int p_149725_2_, int p_149725_3_, int p_149725_4_, int p_149725_5_) {}

    public void func_149639_l(World p_149639_1_, int p_149639_2_, int p_149639_3_, int p_149639_4_) {}

    @SideOnly(Side.CLIENT)
    public boolean func_149648_K()
    {
        return false;
    }

    public boolean func_149698_L()
    {
        return true;
    }

    public boolean func_149659_a(Explosion p_149659_1_)
    {
        return true;
    }

    public boolean func_149667_c(Block p_149667_1_)
    {
        return this == p_149667_1_;
    }

    public static boolean func_149680_a(Block p_149680_0_, Block p_149680_1_)
    {
        return p_149680_0_ != null && p_149680_1_ != null ? (p_149680_0_ == p_149680_1_ ? true : p_149680_0_.func_149667_c(p_149680_1_)) : false;
    }

    public boolean func_149740_M()
    {
        return false;
    }

    public int func_149736_g(World p_149736_1_, int p_149736_2_, int p_149736_3_, int p_149736_4_, int p_149736_5_)
    {
        return 0;
    }

    public Block func_149658_d(String p_149658_1_)
    {
        this.field_149768_d = p_149658_1_;
        return this;
    }

    @SideOnly(Side.CLIENT)
    protected String func_149641_N()
    {
        return this.field_149768_d == null ? "MISSING_ICON_BLOCK_" + func_149682_b(this) + "_" + this.field_149770_b : this.field_149768_d;
    }

    @SideOnly(Side.CLIENT)
    public IIcon func_149735_b(int p_149735_1_, int p_149735_2_)
    {
        return this.func_149691_a(p_149735_1_, p_149735_2_);
    }

    @SideOnly(Side.CLIENT)
    public void func_149651_a(IIconRegister p_149651_1_)
    {
        this.field_149761_L = p_149651_1_.registerIcon(this.func_149641_N());
    }

    @SideOnly(Side.CLIENT)
    public String func_149702_O()
    {
        return null;
    }

    /* ======================================== FORGE START =====================================*/
    private ThreadLocal<EntityPlayer> harvesters = new ThreadLocal();
    private ThreadLocal<Integer> silk_check_meta = new ThreadLocal(); 
    /**
     * Get a light value for the block at the specified coordinates, normal ranges are between 0 and 15
     *
     * @param world The current world
     * @param x X Position
     * @param y Y position
     * @param z Z position
     * @return The light value
     */
    public int getLightValue(IBlockAccess world, int x, int y, int z)
    {
        Block block = world.func_147439_a(x, y, z);
        if (block != this)
        {
            return block.getLightValue(world, x, y, z);
        }
        return func_149750_m();
    }

    /**
     * Checks if a player or entity can use this block to 'climb' like a ladder.
     *
     * @param world The current world
     * @param x X Position
     * @param y Y position
     * @param z Z position
     * @param entity The entity trying to use the ladder, CAN be null.
     * @return True if the block should act like a ladder
     */
    public boolean isLadder(IBlockAccess world, int x, int y, int z, EntityLivingBase entity)
    {
        return false;
    }

    /**
     * Return true if the block is a normal, solid cube.  This
     * determines indirect power state, entity ejection from blocks, and a few
     * others.
     *
     * @param world The current world
     * @param x X Position
     * @param y Y position
     * @param z Z position
     * @return True if the block is a full cube
     */
    public boolean isNormalCube(IBlockAccess world, int x, int y, int z)
    {
        return func_149688_o().isOpaque() && func_149686_d() && !func_149744_f();
    }

    /**
     * Checks if the block is a solid face on the given side, used by placement logic.
     *
     * @param world The current world
     * @param x X Position
     * @param y Y position
     * @param z Z position
     * @param side The side to check
     * @return True if the block is solid on the specified side.
     */
    public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side)
    {
        int meta = world.getBlockMetadata(x, y, z);

        if (this instanceof BlockSlab)
        {
            return (((meta & 8) == 8 && (side == UP)) || func_149730_j());
        }
        else if (this instanceof BlockFarmland)
        {
            return (side != DOWN && side != UP);
        }
        else if (this instanceof BlockStairs)
        {
            boolean flipped = ((meta & 4) != 0);
            return ((meta & 3) + side.ordinal() == 5) || (side == UP && flipped);
        }
        else if (this instanceof BlockSnow)
        {
            return (meta & 7) == 7;
        }
        else if (this instanceof BlockHopper && side == UP)
        {
            return true;
        }
        else if (this instanceof BlockCompressedPowered)
        {
            return true;
        }
        return isNormalCube(world, x, y, z);
    }

    /**
     * Determines if a new block can be replace the space occupied by this one,
     * Used in the player's placement code to make the block act like water, and lava.
     *
     * @param world The current world
     * @param x X Position
     * @param y Y position
     * @param z Z position
     * @return True if the block is replaceable by another block
     */
    public boolean isReplaceable(IBlockAccess world, int x, int y, int z)
    {
        return field_149764_J.isReplaceable();
    }

    /**
     * Determines if this block should set fire and deal fire damage
     * to entities coming into contact with it.
     *
     * @param world The current world
     * @param x X Position
     * @param y Y position
     * @param z Z position
     * @return True if the block should deal damage
     */
    public boolean isBurning(IBlockAccess world, int x, int y, int z)
    {
        return false;
    }

    /**
     * Determines this block should be treated as an air block
     * by the rest of the code. This method is primarily
     * useful for creating pure logic-blocks that will be invisible
     * to the player and otherwise interact as air would.
     *
     * @param world The current world
     * @param x X Position
     * @param y Y position
     * @param z Z position
     * @return True if the block considered air
     */
    public boolean isAir(IBlockAccess world, int x, int y, int z)
    {
        return func_149688_o() == Material.field_151579_a;
    }

    /**
     * Determines if the player can harvest this block, obtaining it's drops when the block is destroyed.
     *
     * @param player The player damaging the block, may be null
     * @param meta The block's current metadata
     * @return True to spawn the drops
     */
    public boolean canHarvestBlock(EntityPlayer player, int meta)
    {
        return ForgeHooks.canHarvestBlock(this, player, meta);
    }

    /**
     * Called when a player removes a block.  This is responsible for
     * actually destroying the block, and the block is intact at time of call.
     * This is called regardless of whether the player can harvest the block or
     * not.
     *
     * Return true if the block is actually destroyed.
     *
     * Note: When used in multiplayer, this is called on both client and
     * server sides!
     *
     * @param world The current world
     * @param player The player damaging the block, may be null
     * @param x X Position
     * @param y Y position
     * @param z Z position
     * @return True if the block is actually destroyed.
     */
    public boolean removedByPlayer(World world, EntityPlayer player, int x, int y, int z)
    {
        return world.func_147468_f(x, y, z);
    }

    /**
     * Chance that fire will spread and consume this block.
     * 300 being a 100% chance, 0, being a 0% chance.
     *
     * @param world The current world
     * @param x The blocks X position
     * @param y The blocks Y position
     * @param z The blocks Z position
     * @param face The face that the fire is coming from
     * @return A number ranging from 0 to 300 relating used to determine if the block will be consumed by fire
     */
    public int getFlammability(IBlockAccess world, int x, int y, int z, ForgeDirection face)
    {
        return Blocks.fire.getFlammability(this);
    }

    /**
     * Called when fire is updating, checks if a block face can catch fire.
     *
     *
     * @param world The current world
     * @param x The blocks X position
     * @param y The blocks Y position
     * @param z The blocks Z position
     * @param face The face that the fire is coming from
     * @return True if the face can be on fire, false otherwise.
     */
    public boolean isFlammable(IBlockAccess world, int x, int y, int z, ForgeDirection face)
    {
        return getFlammability(world, x, y, z, face) > 0;
    }

    /**
     * Called when fire is updating on a neighbor block.
     * The higher the number returned, the faster fire will spread around this block.
     *
     * @param world The current world
     * @param x The blocks X position
     * @param y The blocks Y position
     * @param z The blocks Z position
     * @param face The face that the fire is coming from
     * @return A number that is used to determine the speed of fire growth around the block
     */
    public int getFireSpreadSpeed(IBlockAccess world, int x, int y, int z, ForgeDirection face)
    {
        return Blocks.fire.getEncouragement(this);
    }

    /**
     * Currently only called by fire when it is on top of this block.
     * Returning true will prevent the fire from naturally dying during updating.
     * Also prevents firing from dying from rain.
     *
     * @param world The current world
     * @param x The blocks X position
     * @param y The blocks Y position
     * @param z The blocks Z position
     * @param metadata The blocks current metadata
     * @param side The face that the fire is coming from
     * @return True if this block sustains fire, meaning it will never go out.
     */
    public boolean isFireSource(World world, int x, int y, int z, ForgeDirection side)
    {
        if (this == Blocks.netherrack && side == UP)
        {
            return true;
        }
        if ((world.provider instanceof WorldProviderEnd) && this == Blocks.bedrock && side == UP)
        {
            return true;
        }
        return false;
    }

    private boolean isTileProvider = this instanceof ITileEntityProvider;
    /**
     * Called throughout the code as a replacement for block instanceof BlockContainer
     * Moving this to the Block base class allows for mods that wish to extend vanilla
     * blocks, and also want to have a tile entity on that block, may.
     *
     * Return true from this function to specify this block has a tile entity.
     *
     * @param metadata Metadata of the current block
     * @return True if block has a tile entity, false otherwise
     */
    public boolean hasTileEntity(int metadata)
    {
        return isTileProvider;
    }

    /**
     * Called throughout the code as a replacement for ITileEntityProvider.createNewTileEntity
     * Return the same thing you would from that function.
     * This will fall back to ITileEntityProvider.createNewTileEntity(World) if this block is a ITileEntityProvider
     *
     * @param metadata The Metadata of the current block
     * @return A instance of a class extending TileEntity
     */
    public TileEntity createTileEntity(World world, int metadata)
    {
        if (isTileProvider)
        {
            return ((ITileEntityProvider)this).func_149915_a(world, metadata);
        }
        return null;
    }

    /**
     * Metadata and fortune sensitive version, this replaces the old (int meta, Random rand)
     * version in 1.1.
     *
     * @param meta Blocks Metadata
     * @param fortune Current item fortune level
     * @param random Random number generator
     * @return The number of items to drop
     */
    public int quantityDropped(int meta, int fortune, Random random)
    {
        return func_149679_a(fortune, random);
    }

    /**
     * This returns a complete list of items dropped from this block.
     *
     * @param world The current world
     * @param x X Position
     * @param y Y Position
     * @param z Z Position
     * @param metadata Current metadata
     * @param fortune Breakers fortune level
     * @return A ArrayList containing all items this block drops
     */
    public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune)
    {
        ArrayList<ItemStack> ret = new ArrayList<ItemStack>();

        int count = quantityDropped(metadata, fortune, world.rand);
        for(int i = 0; i < count; i++)
        {
            Item item = func_149650_a(metadata, world.rand, fortune);
            if (item != null)
            {
                ret.add(new ItemStack(item, 1, func_149692_a(metadata)));
            }
        }
        return ret;
    }

    /**
     * Return true from this function if the player with silk touch can harvest this block directly, and not it's normal drops.
     *
     * @param world The world
     * @param player The player doing the harvesting
     * @param x X Position
     * @param y Y Position
     * @param z Z Position
     * @param metadata The metadata
     * @return True if the block can be directly harvested using silk touch
     */
    public boolean canSilkHarvest(World world, EntityPlayer player, int x, int y, int z, int metadata)
    {
        silk_check_meta.set(metadata);;
        boolean ret = this.func_149700_E();
        silk_check_meta.set(null);
        return ret;
    }

    /**
     * Determines if a specified mob type can spawn on this block, returning false will
     * prevent any mob from spawning on the block.
     *
     * @param type The Mob Category Type
     * @param world The current world
     * @param x The X Position
     * @param y The Y Position
     * @param z The Z Position
     * @return True to allow a mob of the specified category to spawn, false to prevent it.
     */
    public boolean canCreatureSpawn(EnumCreatureType type, IBlockAccess world, int x, int y, int z)
    {
        int meta = world.getBlockMetadata(x, y, z);
        if (this instanceof BlockSlab)
        {
            return (((meta & 8) == 8) || func_149730_j());
        }
        else if (this instanceof BlockStairs)
        {
            return ((meta & 4) != 0);
        }
        return isSideSolid(world, x, y, z, UP);
    }

    /**
     * Determines if this block is classified as a Bed, Allowing
     * players to sleep in it, though the block has to specifically
     * perform the sleeping functionality in it's activated event.
     *
     * @param world The current world
     * @param x X Position
     * @param y Y Position
     * @param z Z Position
     * @param player The player or camera entity, null in some cases.
     * @return True to treat this as a bed
     */
    public boolean isBed(IBlockAccess world, int x, int y, int z, EntityLivingBase player)
    {
        return this == Blocks.bed;
    }

    /**
     * Returns the position that the player is moved to upon
     * waking up, or respawning at the bed.
     *
     * @param world The current world
     * @param x X Position
     * @param y Y Position
     * @param z Z Position
     * @param player The player or camera entity, null in some cases.
     * @return The spawn position
     */
    public ChunkCoordinates getBedSpawnPosition(IBlockAccess world, int x, int y, int z, EntityPlayer player)
    {
        if (world instanceof World)
            return BlockBed.func_149977_a((World)world, x, y, z, 0);
        return null;
    }

    /**
     * Called when a user either starts or stops sleeping in the bed.
     *
     * @param world The current world
     * @param x X Position
     * @param y Y Position
     * @param z Z Position
     * @param player The player or camera entity, null in some cases.
     * @param occupied True if we are occupying the bed, or false if they are stopping use of the bed
     */
    public void setBedOccupied(IBlockAccess world, int x, int y, int z, EntityPlayer player, boolean occupied)
    {
        if (world instanceof World)
            BlockBed.func_149979_a((World)world,  x, y, z, occupied);
    }

    /**
     * Returns the direction of the block. Same values that
     * are returned by BlockDirectional
     *
     * @param world The current world
     * @param x X Position
     * @param y Y Position
     * @param z Z Position
     * @return Bed direction
     */
    public int getBedDirection(IBlockAccess world, int x, int y, int z)
    {
        return BlockBed.func_149895_l(world.getBlockMetadata(x,  y, z));
    }

    /**
     * Determines if the current block is the foot half of the bed.
     *
     * @param world The current world
     * @param x X Position
     * @param y Y Position
     * @param z Z Position
     * @return True if the current block is the foot side of a bed.
     */
    public boolean isBedFoot(IBlockAccess world, int x, int y, int z)
    {
        return BlockBed.func_149975_b(world.getBlockMetadata(x,  y, z));
    }

    /**
     * Called when a leaf should start its decay process.
     *
     * @param world The current world
     * @param x X Position
     * @param y Y Position
     * @param z Z Position
     */
    public void beginLeavesDecay(World world, int x, int y, int z){}

    /**
     * Determines if this block can prevent leaves connected to it from decaying.
     *
     * @param world The current world
     * @param x X Position
     * @param y Y Position
     * @param z Z Position
     * @return true if the presence this block can prevent leaves from decaying.
     */
    public boolean canSustainLeaves(IBlockAccess world, int x, int y, int z)
    {
        return false;
    }

    /**
     * Determines if this block is considered a leaf block, used to apply the leaf decay and generation system.
     *
     * @param world The current world
     * @param x X Position
     * @param y Y Position
     * @param z Z Position
     * @return true if this block is considered leaves.
     */
    public boolean isLeaves(IBlockAccess world, int x, int y, int z)
    {
        return func_149688_o() == Material.field_151584_j;
    }

    /**
     * Used during tree growth to determine if newly generated leaves can replace this block.
     *
     * @param world The current world
     * @param x X Position
     * @param y Y Position
     * @param z Z Position
     * @return true if this block can be replaced by growing leaves.
     */
    public boolean canBeReplacedByLeaves(IBlockAccess world, int x, int y, int z)
    {
        return !func_149730_j();
    }

    /**
     *
     * @param world The current world
     * @param x X Position
     * @param y Y Position
     * @param z Z Position
     * @return  true if the block is wood (logs)
     */
    public boolean isWood(IBlockAccess world, int x, int y, int z)
    {
         return false;
    }

    /**
     * Determines if the current block is replaceable by Ore veins during world generation.
     *
     * @param world The current world
     * @param x X Position
     * @param y Y Position
     * @param z Z Position
     * @param target The generic target block the gen is looking for, Standards define stone
     *      for overworld generation, and neatherack for the nether.
     * @return True to allow this block to be replaced by a ore
     */
    public boolean isReplaceableOreGen(World world, int x, int y, int z, Block target)
    {
        return this == target;
    }

    /**
     * Location sensitive version of getExplosionRestance
     *
     * @param par1Entity The entity that caused the explosion
     * @param world The current world
     * @param x X Position
     * @param y Y Position
     * @param z Z Position
     * @param explosionX Explosion source X Position
     * @param explosionY Explosion source X Position
     * @param explosionZ Explosion source X Position
     * @return The amount of the explosion absorbed.
     */
    public float getExplosionResistance(Entity par1Entity, World world, int x, int y, int z, double explosionX, double explosionY, double explosionZ)
    {
        return func_149638_a(par1Entity);
    }

    /**
     * Called when the block is destroyed by an explosion.
     * Useful for allowing the block to take into account tile entities,
     * metadata, etc. when exploded, before it is removed.
     *
     * @param world The current world
     * @param x X Position
     * @param y Y Position
     * @param z Z Position
     * @param Explosion The explosion instance affecting the block
     */
    public void onBlockExploded(World world, int x, int y, int z, Explosion explosion)
    {
        world.func_147468_f(x, y, z);
        func_149723_a(world, x, y, z, explosion);
    }

    /**
     * Determine if this block can make a redstone connection on the side provided,
     * Useful to control which sides are inputs and outputs for redstone wires.
     *
     * Side:
     *  -1: UP
     *   0: NORTH
     *   1: EAST
     *   2: SOUTH
     *   3: WEST
     *
     * @param world The current world
     * @param x X Position
     * @param y Y Position
     * @param z Z Position
     * @param side The side that is trying to make the connection
     * @return True to make the connection
     */
    public boolean canConnectRedstone(IBlockAccess world, int x, int y, int z, int side)
    {
        return func_149744_f() && side != -1;
    }

    /**
     * Determines if a torch can be placed on the top surface of this block.
     * Useful for creating your own block that torches can be on, such as fences.
     *
     * @param world The current world
     * @param x X Position
     * @param y Y Position
     * @param z Z Position
     * @return True to allow the torch to be placed
     */
    public boolean canPlaceTorchOnTop(World world, int x, int y, int z)
    {
        if (isSideSolid(world, x, y, z, UP))
        {
            return true;
        }
        else
        {
            return this == Blocks.fence || this == Blocks.nether_brick_fence || this == Blocks.glass || this == Blocks.cobblestone_wall;
        }
    }

    /**
     * Determines if this block should render in this pass.
     *
     * @param pass The pass in question
     * @return True to render
     */
    public boolean canRenderInPass(int pass)
    {
        return pass == func_149701_w();
    }

    /**
     * Called when a user uses the creative pick block button on this block
     *
     * @param target The full target the player is looking at
     * @return A ItemStack to add to the player's inventory, Null if nothing should be added.
     */
    public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z)
    {
        Item item = func_149694_d(world, x, y, z);

        if (item == null)
        {
            return null;
        }

        Block block = item instanceof ItemBlock && !func_149648_K() ? Block.func_149634_a(item) : this;
        return new ItemStack(item, 1, block.func_149643_k(world, x, y, z));
    }

    /**
     * Used by getTopSolidOrLiquidBlock while placing biome decorations, villages, etc
     * Also used to determine if the player can spawn on this block.
     *
     * @return False to disallow spawning
     */
    public boolean isFoliage(IBlockAccess world, int x, int y, int z)
    {
        return false;
    }

    /**
     * Spawn a digging particle effect in the world, this is a wrapper
     * around EffectRenderer.addBlockHitEffects to allow the block more
     * control over the particles. Useful when you have entirely different
     * texture sheets for different sides/locations in the world.
     *
     * @param world The current world
     * @param target The target the player is looking at {x/y/z/side/sub}
     * @param effectRenderer A reference to the current effect renderer.
     * @return True to prevent vanilla digging particles form spawning.
     */
    @SideOnly(Side.CLIENT)
    public boolean addHitEffects(World worldObj, MovingObjectPosition target, EffectRenderer effectRenderer)
    {
        return false;
    }

    /**
     * Spawn particles for when the block is destroyed. Due to the nature
     * of how this is invoked, the x/y/z locations are not always guaranteed
     * to host your block. So be sure to do proper sanity checks before assuming
     * that the location is this block.
     *
     * @param world The current world
     * @param x X position to spawn the particle
     * @param y Y position to spawn the particle
     * @param z Z position to spawn the particle
     * @param meta The metadata for the block before it was destroyed.
     * @param effectRenderer A reference to the current effect renderer.
     * @return True to prevent vanilla break particles from spawning.
     */
    @SideOnly(Side.CLIENT)
    public boolean addDestroyEffects(World world, int x, int y, int z, int meta, EffectRenderer effectRenderer)
    {
        return false;
    }

    /**
     * Determines if this block can support the passed in plant, allowing it to be planted and grow.
     * Some examples:
     *   Reeds check if its a reed, or if its sand/dirt/grass and adjacent to water
     *   Cacti checks if its a cacti, or if its sand
     *   Nether types check for soul sand
     *   Crops check for tilled soil
     *   Caves check if it's a solid surface
     *   Plains check if its grass or dirt
     *   Water check if its still water
     *
     * @param world The current world
     * @param x X Position
     * @param y Y Position
     * @param z Z position
     * @param direction The direction relative to the given position the plant wants to be, typically its UP
     * @param plantable The plant that wants to check
     * @return True to allow the plant to be planted/stay.
     */
    public boolean canSustainPlant(IBlockAccess world, int x, int y, int z, ForgeDirection direction, IPlantable plantable)
    {
        Block plant = plantable.getPlant(world, x, y + 1, z);
        EnumPlantType plantType = plantable.getPlantType(world, x, y + 1, z);

        if (plant == Blocks.cactus && this == Blocks.cactus)
        {
            return true;
        }

        if (plant == Blocks.reeds && this == Blocks.reeds)
        {
            return true;
        }

        if (plantable instanceof BlockBush && ((BlockBush)plantable).func_149854_a(this))
        {
            return true;
        }

        switch (plantType)
        {
            case Desert: return this == Blocks.sand;
            case Nether: return this == Blocks.soul_sand;
            case Crop:   return this == Blocks.farmland;
            case Cave:   return isSideSolid(world, x, y, z, UP);
            case Plains: return this == Blocks.grass || this == Blocks.dirt;
            case Water:  return world.func_147439_a(x, y, z).func_149688_o() == Material.field_151586_h && world.getBlockMetadata(x, y, z) == 0;
            case Beach:
                boolean isBeach = this == Blocks.grass || this == Blocks.dirt || this == Blocks.sand;
                boolean hasWater = (world.func_147439_a(x - 1, y, z    ).func_149688_o() == Material.field_151586_h ||
                                    world.func_147439_a(x + 1, y, z    ).func_149688_o() == Material.field_151586_h ||
                                    world.func_147439_a(x,     y, z - 1).func_149688_o() == Material.field_151586_h ||
                                    world.func_147439_a(x,     y, z + 1).func_149688_o() == Material.field_151586_h);
                return isBeach && hasWater;
        }

        return false;
    }

    /**
     * Called when a plant grows on this block, only implemented for saplings using the WorldGen*Trees classes right now.
     * Modder may implement this for custom plants.
     * This does not use ForgeDirection, because large/huge trees can be located in non-representable direction,
     * so the source location is specified.
     * Currently this just changes the block to dirt if it was grass.
     *
     * Note: This happens DURING the generation, the generation may not be complete when this is called.
     *
     * @param world Current world
     * @param x Soil X
     * @param y Soil Y
     * @param z Soil Z
     * @param sourceX Plant growth location X
     * @param sourceY Plant growth location Y
     * @param sourceZ Plant growth location Z
     */
    public void onPlantGrow(World world, int x, int y, int z, int sourceX, int sourceY, int sourceZ)
    {
        if (this == Blocks.grass || this == Blocks.farmland)
        {
            world.func_147465_d(x, y, z, Blocks.dirt, 0, 2);
        }
    }

    /**
     * Checks if this soil is fertile, typically this means that growth rates
     * of plants on this soil will be slightly sped up.
     * Only vanilla case is tilledField when it is within range of water.
     *
     * @param world The current world
     * @param x X Position
     * @param y Y Position
     * @param z Z position
     * @return True if the soil should be considered fertile.
     */
    public boolean isFertile(World world, int x, int y, int z)
    {
        if (this == Blocks.farmland)
        {
            return world.getBlockMetadata(x, y, z) > 0;
        }

        return false;
    }

    /**
     * Location aware and overrideable version of the lightOpacity array,
     * return the number to subtract from the light value when it passes through this block.
     *
     * This is not guaranteed to have the tile entity in place before this is called, so it is
     * Recommended that you have your tile entity call relight after being placed if you
     * rely on it for light info.
     *
     * @param world The current world
     * @param x X Position
     * @param y Y Position
     * @param z Z position
     * @return The amount of light to block, 0 for air, 255 for fully opaque.
     */
    public int getLightOpacity(IBlockAccess world, int x, int y, int z)
    {
        return func_149717_k();
    }

    /**
     * Determines if this block is can be destroyed by the specified entities normal behavior.
     *
     * @param world The current world
     * @param x X Position
     * @param y Y Position
     * @param z Z position
     * @return True to allow the ender dragon to destroy this block
     */
    public boolean canEntityDestroy(IBlockAccess world, int x, int y, int z, Entity entity)
    {
        if (entity instanceof EntityWither)
        {
            return this != Blocks.bedrock && this != Blocks.end_portal && this != Blocks.end_portal_frame && this != Blocks.command_block;
        }
        else if (entity instanceof EntityDragon)
        {
            return this != Blocks.obsidian && this != Blocks.end_stone && this != Blocks.bedrock; 
        }

        return true;
    }

    /**
     * Determines if this block can be used as the base of a beacon.
     *
     * @param world The current world
     * @param x X Position
     * @param y Y Position
     * @param z Z position
     * @param beaconX Beacons X Position
     * @param beaconY Beacons Y Position
     * @param beaconZ Beacons Z Position
     * @return True, to support the beacon, and make it active with this block.
     */
    public boolean isBeaconBase(IBlockAccess worldObj, int x, int y, int z, int beaconX, int beaconY, int beaconZ)
    {
        return this == Blocks.emerald_block || this == Blocks.gold_block || this == Blocks.diamond_block || this == Blocks.iron_block;
    }

    /**
     * Rotate the block. For vanilla blocks this rotates around the axis passed in (generally, it should be the "face" that was hit).
     * Note: for mod blocks, this is up to the block and modder to decide. It is not mandated that it be a rotation around the
     * face, but could be a rotation to orient *to* that face, or a visiting of possible rotations.
     * The method should return true if the rotation was successful though.
     *
     * @param worldObj The world
     * @param x X position
     * @param y Y position
     * @param z Z position
     * @param axis The axis to rotate around
     * @return True if the rotation was successful, False if the rotation failed, or is not possible
     */
    public boolean rotateBlock(World worldObj, int x, int y, int z, ForgeDirection axis)
    {
        return RotationHelper.rotateVanillaBlock(this, worldObj, x, y, z, axis);
    }

    /**
     * Get the rotations that can apply to the block at the specified coordinates. Null means no rotations are possible.
     * Note, this is up to the block to decide. It may not be accurate or representative.
     * @param worldObj The world
     * @param x X position
     * @param y Y position
     * @param z Z position
     * @return An array of valid axes to rotate around, or null for none or unknown
     */
    public ForgeDirection[] getValidRotations(World worldObj, int x, int y, int z)
    {
        return RotationHelper.getValidVanillaBlockRotations(this);
    }

    /**
     * Determines the amount of enchanting power this block can provide to an enchanting table.
     * @param world The World
     * @param x X position
     * @param y Y position
     * @param z Z position
     * @return The amount of enchanting power this block produces.
     */
    public float getEnchantPowerBonus(World world, int x, int y, int z)
    {
        return this == Blocks.bookshelf ? 1 : 0;
    }

    /**
     * Common way to recolour a block with an external tool
     * @param world The world
     * @param x X
     * @param y Y
     * @param z Z
     * @param side The side hit with the colouring tool
     * @param colour The colour to change to
     * @return If the recolouring was successful
     */
    public boolean recolourBlock(World world, int x, int y, int z, ForgeDirection side, int colour)
    {
        if (this == Blocks.wool)
        {
            int meta = world.getBlockMetadata(x, y, z);
            if (meta != colour)
            {
                world.setBlockMetadataWithNotify(x, y, z, colour, 3);
                return true;
            }
        }
        return false;
    }

    /**
     * Gathers how much experience this block drops when broken.
     * 
     * @param world The world
     * @param metadata
     * @param fortune
     * @return Amount of XP from breaking this block.
     */
    public int getExpDrop(IBlockAccess world, int metadata, int fortune)
    {
        return 0;
    }

    /**
     * Called when a tile entity on a side of this block changes is created or is destroyed.
     * @param world The world
     * @param x The x position of this block instance
     * @param y The y position of this block instance
     * @param z The z position of this block instance
     * @param tileX The x position of the tile that changed
     * @param tileY The y position of the tile that changed
     * @param tileZ The z position of the tile that changed
     */
    public void onNeighborChange(IBlockAccess world, int x, int y, int z, int tileX, int tileY, int tileZ)
    {
    }

    /**
     * Called to determine whether to allow the a block to handle its own indirect power rather than using the default rules.
     * @param world The world
     * @param x The x position of this block instance
     * @param y The y position of this block instance
     * @param z The z position of this block instance
     * @param side The INPUT side of the block to be powered - ie the opposite of this block's output side
     * @return Whether Block#isProvidingWeakPower should be called when determining indirect power
     */
    public boolean shouldCheckWeakPower(IBlockAccess world, int x, int y, int z, int side)
    {
        return func_149721_r();
    }

    /**
     * If this block should be notified of weak changes.
     * Weak changes are changes 1 block away through a solid block.
     * Similar to comparators.
     * 
     * @param world The current world
     * @param x X Position
     * @param y Y position
     * @param z Z position
     * @param side The side to check
     * @return true To be notified of changes
     */
    public boolean getWeakChanges(IBlockAccess world, int x, int y, int z)
    {
        return false;
    }

    private String[] harvestTool = new String[16];
    private int[] harvestLevel = new int[]{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
    /**
     * Sets or removes the tool and level required to harvest this block.
     * 
     * @param toolClass Class
     * @param level Harvest level:
     *     Wood:    0
     *     Stone:   1
     *     Iton:    2
     *     Diamond: 3
     *     Gold:    0
     */
    public void setHarvestLevel(String toolClass, int level)
    {
        for (int m = 0; m < 16; m++)
        {
            setHarvestLevel(toolClass, level, m);
        }
    }

    /**
     * Sets or removes the tool and level required to harvest this block.
     * 
     * @param toolClass Class
     * @param level Harvest level:
     *     Wood:    0
     *     Stone:   1
     *     Iton:    2
     *     Diamond: 3
     *     Gold:    0
     * @param metadata The specific metadata to set
     */
    public void setHarvestLevel(String toolClass, int level, int metadata)
    {
        this.harvestTool[metadata] = toolClass;
        this.harvestLevel[metadata] = level;
    }

    /**
     * Queries the class of tool required to harvest this block, if null is returned 
     * we assume that anything can harvest this block.
     * 
     * @param metadata
     * @return
     */
    public String getHarvestTool(int metadata)
    {
        return harvestTool[metadata];
    }

    /**
     * Queries the harvest level of this item stack for the specifred tool class,
     * Returns -1 if this tool is not of the specified type
     * 
     * @param stack This item stack instance
     * @return Harvest level, or -1 if not the specified tool type.
     */
    public int getHarvestLevel(int metadata)
    {
        return harvestLevel[metadata];
    }

    /**
     * Checks if the specified tool type is efficient on this block, 
     * meaning that it digs at full speed.
     * 
     * @param type
     * @param metadata
     * @return
     */
    public boolean isToolEffective(String type, int metadata)
    {
        if ("pickaxe".equals(type) && (this == Blocks.redstone_ore || this == Blocks.lit_redstone_ore || this == Blocks.obsidian))
            return false;
        if (harvestTool[metadata] == null) return true;
        return harvestTool[metadata].equals(type);
    }
    /* ========================================= FORGE END ======================================*/

    public static class SoundType
        {
            public final String field_150501_a;
            public final float field_150499_b;
            public final float field_150500_c;
            private static final String __OBFID = "CL_00000203";

            public SoundType(String p_i45393_1_, float p_i45393_2_, float p_i45393_3_)
            {
                this.field_150501_a = p_i45393_1_;
                this.field_150499_b = p_i45393_2_;
                this.field_150500_c = p_i45393_3_;
            }

            public float func_150497_c()
            {
                return this.field_150499_b;
            }

            public float func_150494_d()
            {
                return this.field_150500_c;
            }

            public String func_150495_a()
            {
                return "dig." + this.field_150501_a;
            }

            public String func_150498_e()
            {
                return "step." + this.field_150501_a;
            }

            public String func_150496_b()
            {
                return this.func_150495_a();
            }
        }
}