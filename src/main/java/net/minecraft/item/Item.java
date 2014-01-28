package net.minecraft.item;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.block.BlockFlower;
import net.minecraft.block.BlockNewLog;
import net.minecraft.block.BlockOldLog;
import net.minecraft.block.BlockQuartz;
import net.minecraft.block.BlockSand;
import net.minecraft.block.BlockSandStone;
import net.minecraft.block.BlockSapling;
import net.minecraft.block.BlockSilverfish;
import net.minecraft.block.BlockStoneBrick;
import net.minecraft.block.BlockWall;
import net.minecraft.block.BlockWood;
import net.minecraft.block.material.Material;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionHelper;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.RegistryNamespaced;
import net.minecraft.util.StatCollector;
import net.minecraft.util.Vec3;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.World;
import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.common.util.EnumHelper;

public class Item
{
    public static final RegistryNamespaced field_150901_e = GameData.itemRegistry;
    protected static final UUID field_111210_e = UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF");
    private CreativeTabs tabToDisplayOn;
    // JAVADOC FIELD $$ field_77697_d
    protected static Random itemRand = new Random();
    // JAVADOC FIELD $$ field_77777_bU
    protected int maxStackSize = 64;
    // JAVADOC FIELD $$ field_77699_b
    private int maxDamage;
    // JAVADOC FIELD $$ field_77789_bW
    protected boolean bFull3D;
    // JAVADOC FIELD $$ field_77787_bX
    protected boolean hasSubtypes;
    private Item containerItem;
    private String potionEffect;
    // JAVADOC FIELD $$ field_77774_bZ
    private String unlocalizedName;
    // JAVADOC FIELD $$ field_77791_bV
    @SideOnly(Side.CLIENT)
    protected IIcon itemIcon;
    // JAVADOC FIELD $$ field_111218_cA
    protected String iconString;
    private static final String __OBFID = "CL_00000041";

    public static int func_150891_b(Item p_150891_0_)
    {
        return p_150891_0_ == null ? 0 : field_150901_e.func_148757_b(p_150891_0_);
    }

    public static Item func_150899_d(int p_150899_0_)
    {
        return (Item)field_150901_e.func_148754_a(p_150899_0_);
    }

    public static Item func_150898_a(Block p_150898_0_)
    {
        return func_150899_d(Block.func_149682_b(p_150898_0_));
    }

    public static void func_150900_l()
    {
        field_150901_e.func_148756_a(256, "iron_shovel", (new ItemSpade(Item.ToolMaterial.IRON)).setUnlocalizedName("shovelIron").setTextureName("iron_shovel"));
        field_150901_e.func_148756_a(257, "iron_pickaxe", (new ItemPickaxe(Item.ToolMaterial.IRON)).setUnlocalizedName("pickaxeIron").setTextureName("iron_pickaxe"));
        field_150901_e.func_148756_a(258, "iron_axe", (new ItemAxe(Item.ToolMaterial.IRON)).setUnlocalizedName("hatchetIron").setTextureName("iron_axe"));
        field_150901_e.func_148756_a(259, "flint_and_steel", (new ItemFlintAndSteel()).setUnlocalizedName("flintAndSteel").setTextureName("flint_and_steel"));
        field_150901_e.func_148756_a(260, "apple", (new ItemFood(4, 0.3F, false)).setUnlocalizedName("apple").setTextureName("apple"));
        field_150901_e.func_148756_a(261, "bow", (new ItemBow()).setUnlocalizedName("bow").setTextureName("bow"));
        field_150901_e.func_148756_a(262, "arrow", (new Item()).setUnlocalizedName("arrow").setCreativeTab(CreativeTabs.tabCombat).setTextureName("arrow"));
        field_150901_e.func_148756_a(263, "coal", (new ItemCoal()).setUnlocalizedName("coal").setTextureName("coal"));
        field_150901_e.func_148756_a(264, "diamond", (new Item()).setUnlocalizedName("diamond").setCreativeTab(CreativeTabs.tabMaterials).setTextureName("diamond"));
        field_150901_e.func_148756_a(265, "iron_ingot", (new Item()).setUnlocalizedName("ingotIron").setCreativeTab(CreativeTabs.tabMaterials).setTextureName("iron_ingot"));
        field_150901_e.func_148756_a(266, "gold_ingot", (new Item()).setUnlocalizedName("ingotGold").setCreativeTab(CreativeTabs.tabMaterials).setTextureName("gold_ingot"));
        field_150901_e.func_148756_a(267, "iron_sword", (new ItemSword(Item.ToolMaterial.IRON)).setUnlocalizedName("swordIron").setTextureName("iron_sword"));
        field_150901_e.func_148756_a(268, "wooden_sword", (new ItemSword(Item.ToolMaterial.WOOD)).setUnlocalizedName("swordWood").setTextureName("wood_sword"));
        field_150901_e.func_148756_a(269, "wooden_shovel", (new ItemSpade(Item.ToolMaterial.WOOD)).setUnlocalizedName("shovelWood").setTextureName("wood_shovel"));
        field_150901_e.func_148756_a(270, "wooden_pickaxe", (new ItemPickaxe(Item.ToolMaterial.WOOD)).setUnlocalizedName("pickaxeWood").setTextureName("wood_pickaxe"));
        field_150901_e.func_148756_a(271, "wooden_axe", (new ItemAxe(Item.ToolMaterial.WOOD)).setUnlocalizedName("hatchetWood").setTextureName("wood_axe"));
        field_150901_e.func_148756_a(272, "stone_sword", (new ItemSword(Item.ToolMaterial.STONE)).setUnlocalizedName("swordStone").setTextureName("stone_sword"));
        field_150901_e.func_148756_a(273, "stone_shovel", (new ItemSpade(Item.ToolMaterial.STONE)).setUnlocalizedName("shovelStone").setTextureName("stone_shovel"));
        field_150901_e.func_148756_a(274, "stone_pickaxe", (new ItemPickaxe(Item.ToolMaterial.STONE)).setUnlocalizedName("pickaxeStone").setTextureName("stone_pickaxe"));
        field_150901_e.func_148756_a(275, "stone_axe", (new ItemAxe(Item.ToolMaterial.STONE)).setUnlocalizedName("hatchetStone").setTextureName("stone_axe"));
        field_150901_e.func_148756_a(276, "diamond_sword", (new ItemSword(Item.ToolMaterial.EMERALD)).setUnlocalizedName("swordDiamond").setTextureName("diamond_sword"));
        field_150901_e.func_148756_a(277, "diamond_shovel", (new ItemSpade(Item.ToolMaterial.EMERALD)).setUnlocalizedName("shovelDiamond").setTextureName("diamond_shovel"));
        field_150901_e.func_148756_a(278, "diamond_pickaxe", (new ItemPickaxe(Item.ToolMaterial.EMERALD)).setUnlocalizedName("pickaxeDiamond").setTextureName("diamond_pickaxe"));
        field_150901_e.func_148756_a(279, "diamond_axe", (new ItemAxe(Item.ToolMaterial.EMERALD)).setUnlocalizedName("hatchetDiamond").setTextureName("diamond_axe"));
        field_150901_e.func_148756_a(280, "stick", (new Item()).setFull3D().setUnlocalizedName("stick").setCreativeTab(CreativeTabs.tabMaterials).setTextureName("stick"));
        field_150901_e.func_148756_a(281, "bowl", (new Item()).setUnlocalizedName("bowl").setCreativeTab(CreativeTabs.tabMaterials).setTextureName("bowl"));
        field_150901_e.func_148756_a(282, "mushroom_stew", (new ItemSoup(6)).setUnlocalizedName("mushroomStew").setTextureName("mushroom_stew"));
        field_150901_e.func_148756_a(283, "golden_sword", (new ItemSword(Item.ToolMaterial.GOLD)).setUnlocalizedName("swordGold").setTextureName("gold_sword"));
        field_150901_e.func_148756_a(284, "golden_shovel", (new ItemSpade(Item.ToolMaterial.GOLD)).setUnlocalizedName("shovelGold").setTextureName("gold_shovel"));
        field_150901_e.func_148756_a(285, "golden_pickaxe", (new ItemPickaxe(Item.ToolMaterial.GOLD)).setUnlocalizedName("pickaxeGold").setTextureName("gold_pickaxe"));
        field_150901_e.func_148756_a(286, "golden_axe", (new ItemAxe(Item.ToolMaterial.GOLD)).setUnlocalizedName("hatchetGold").setTextureName("gold_axe"));
        field_150901_e.func_148756_a(287, "string", (new ItemReed(Blocks.tripwire)).setUnlocalizedName("string").setCreativeTab(CreativeTabs.tabMaterials).setTextureName("string"));
        field_150901_e.func_148756_a(288, "feather", (new Item()).setUnlocalizedName("feather").setCreativeTab(CreativeTabs.tabMaterials).setTextureName("feather"));
        field_150901_e.func_148756_a(289, "gunpowder", (new Item()).setUnlocalizedName("sulphur").setPotionEffect(PotionHelper.gunpowderEffect).setCreativeTab(CreativeTabs.tabMaterials).setTextureName("gunpowder"));
        field_150901_e.func_148756_a(290, "wooden_hoe", (new ItemHoe(Item.ToolMaterial.WOOD)).setUnlocalizedName("hoeWood").setTextureName("wood_hoe"));
        field_150901_e.func_148756_a(291, "stone_hoe", (new ItemHoe(Item.ToolMaterial.STONE)).setUnlocalizedName("hoeStone").setTextureName("stone_hoe"));
        field_150901_e.func_148756_a(292, "iron_hoe", (new ItemHoe(Item.ToolMaterial.IRON)).setUnlocalizedName("hoeIron").setTextureName("iron_hoe"));
        field_150901_e.func_148756_a(293, "diamond_hoe", (new ItemHoe(Item.ToolMaterial.EMERALD)).setUnlocalizedName("hoeDiamond").setTextureName("diamond_hoe"));
        field_150901_e.func_148756_a(294, "golden_hoe", (new ItemHoe(Item.ToolMaterial.GOLD)).setUnlocalizedName("hoeGold").setTextureName("gold_hoe"));
        field_150901_e.func_148756_a(295, "wheat_seeds", (new ItemSeeds(Blocks.wheat, Blocks.farmland)).setUnlocalizedName("seeds").setTextureName("seeds_wheat"));
        field_150901_e.func_148756_a(296, "wheat", (new Item()).setUnlocalizedName("wheat").setCreativeTab(CreativeTabs.tabMaterials).setTextureName("wheat"));
        field_150901_e.func_148756_a(297, "bread", (new ItemFood(5, 0.6F, false)).setUnlocalizedName("bread").setTextureName("bread"));
        field_150901_e.func_148756_a(298, "leather_helmet", (new ItemArmor(ItemArmor.ArmorMaterial.CLOTH, 0, 0)).setUnlocalizedName("helmetCloth").setTextureName("leather_helmet"));
        field_150901_e.func_148756_a(299, "leather_chestplate", (new ItemArmor(ItemArmor.ArmorMaterial.CLOTH, 0, 1)).setUnlocalizedName("chestplateCloth").setTextureName("leather_chestplate"));
        field_150901_e.func_148756_a(300, "leather_leggings", (new ItemArmor(ItemArmor.ArmorMaterial.CLOTH, 0, 2)).setUnlocalizedName("leggingsCloth").setTextureName("leather_leggings"));
        field_150901_e.func_148756_a(301, "leather_boots", (new ItemArmor(ItemArmor.ArmorMaterial.CLOTH, 0, 3)).setUnlocalizedName("bootsCloth").setTextureName("leather_boots"));
        field_150901_e.func_148756_a(302, "chainmail_helmet", (new ItemArmor(ItemArmor.ArmorMaterial.CHAIN, 1, 0)).setUnlocalizedName("helmetChain").setTextureName("chainmail_helmet"));
        field_150901_e.func_148756_a(303, "chainmail_chestplate", (new ItemArmor(ItemArmor.ArmorMaterial.CHAIN, 1, 1)).setUnlocalizedName("chestplateChain").setTextureName("chainmail_chestplate"));
        field_150901_e.func_148756_a(304, "chainmail_leggings", (new ItemArmor(ItemArmor.ArmorMaterial.CHAIN, 1, 2)).setUnlocalizedName("leggingsChain").setTextureName("chainmail_leggings"));
        field_150901_e.func_148756_a(305, "chainmail_boots", (new ItemArmor(ItemArmor.ArmorMaterial.CHAIN, 1, 3)).setUnlocalizedName("bootsChain").setTextureName("chainmail_boots"));
        field_150901_e.func_148756_a(306, "iron_helmet", (new ItemArmor(ItemArmor.ArmorMaterial.IRON, 2, 0)).setUnlocalizedName("helmetIron").setTextureName("iron_helmet"));
        field_150901_e.func_148756_a(307, "iron_chestplate", (new ItemArmor(ItemArmor.ArmorMaterial.IRON, 2, 1)).setUnlocalizedName("chestplateIron").setTextureName("iron_chestplate"));
        field_150901_e.func_148756_a(308, "iron_leggings", (new ItemArmor(ItemArmor.ArmorMaterial.IRON, 2, 2)).setUnlocalizedName("leggingsIron").setTextureName("iron_leggings"));
        field_150901_e.func_148756_a(309, "iron_boots", (new ItemArmor(ItemArmor.ArmorMaterial.IRON, 2, 3)).setUnlocalizedName("bootsIron").setTextureName("iron_boots"));
        field_150901_e.func_148756_a(310, "diamond_helmet", (new ItemArmor(ItemArmor.ArmorMaterial.DIAMOND, 3, 0)).setUnlocalizedName("helmetDiamond").setTextureName("diamond_helmet"));
        field_150901_e.func_148756_a(311, "diamond_chestplate", (new ItemArmor(ItemArmor.ArmorMaterial.DIAMOND, 3, 1)).setUnlocalizedName("chestplateDiamond").setTextureName("diamond_chestplate"));
        field_150901_e.func_148756_a(312, "diamond_leggings", (new ItemArmor(ItemArmor.ArmorMaterial.DIAMOND, 3, 2)).setUnlocalizedName("leggingsDiamond").setTextureName("diamond_leggings"));
        field_150901_e.func_148756_a(313, "diamond_boots", (new ItemArmor(ItemArmor.ArmorMaterial.DIAMOND, 3, 3)).setUnlocalizedName("bootsDiamond").setTextureName("diamond_boots"));
        field_150901_e.func_148756_a(314, "golden_helmet", (new ItemArmor(ItemArmor.ArmorMaterial.GOLD, 4, 0)).setUnlocalizedName("helmetGold").setTextureName("gold_helmet"));
        field_150901_e.func_148756_a(315, "golden_chestplate", (new ItemArmor(ItemArmor.ArmorMaterial.GOLD, 4, 1)).setUnlocalizedName("chestplateGold").setTextureName("gold_chestplate"));
        field_150901_e.func_148756_a(316, "golden_leggings", (new ItemArmor(ItemArmor.ArmorMaterial.GOLD, 4, 2)).setUnlocalizedName("leggingsGold").setTextureName("gold_leggings"));
        field_150901_e.func_148756_a(317, "golden_boots", (new ItemArmor(ItemArmor.ArmorMaterial.GOLD, 4, 3)).setUnlocalizedName("bootsGold").setTextureName("gold_boots"));
        field_150901_e.func_148756_a(318, "flint", (new Item()).setUnlocalizedName("flint").setCreativeTab(CreativeTabs.tabMaterials).setTextureName("flint"));
        field_150901_e.func_148756_a(319, "porkchop", (new ItemFood(3, 0.3F, true)).setUnlocalizedName("porkchopRaw").setTextureName("porkchop_raw"));
        field_150901_e.func_148756_a(320, "cooked_porkchop", (new ItemFood(8, 0.8F, true)).setUnlocalizedName("porkchopCooked").setTextureName("porkchop_cooked"));
        field_150901_e.func_148756_a(321, "painting", (new ItemHangingEntity(EntityPainting.class)).setUnlocalizedName("painting").setTextureName("painting"));
        field_150901_e.func_148756_a(322, "golden_apple", (new ItemAppleGold(4, 1.2F, false)).setAlwaysEdible().setPotionEffect(Potion.regeneration.id, 5, 1, 1.0F).setUnlocalizedName("appleGold").setTextureName("apple_golden"));
        field_150901_e.func_148756_a(323, "sign", (new ItemSign()).setUnlocalizedName("sign").setTextureName("sign"));
        field_150901_e.func_148756_a(324, "wooden_door", (new ItemDoor(Material.field_151575_d)).setUnlocalizedName("doorWood").setTextureName("door_wood"));
        Item item = (new ItemBucket(Blocks.air)).setUnlocalizedName("bucket").setMaxStackSize(16).setTextureName("bucket_empty");
        field_150901_e.func_148756_a(325, "bucket", item);
        field_150901_e.func_148756_a(326, "water_bucket", (new ItemBucket(Blocks.flowing_water)).setUnlocalizedName("bucketWater").setContainerItem(item).setTextureName("bucket_water"));
        field_150901_e.func_148756_a(327, "lava_bucket", (new ItemBucket(Blocks.flowing_lava)).setUnlocalizedName("bucketLava").setContainerItem(item).setTextureName("bucket_lava"));
        field_150901_e.func_148756_a(328, "minecart", (new ItemMinecart(0)).setUnlocalizedName("minecart").setTextureName("minecart_normal"));
        field_150901_e.func_148756_a(329, "saddle", (new ItemSaddle()).setUnlocalizedName("saddle").setTextureName("saddle"));
        field_150901_e.func_148756_a(330, "iron_door", (new ItemDoor(Material.field_151573_f)).setUnlocalizedName("doorIron").setTextureName("door_iron"));
        field_150901_e.func_148756_a(331, "redstone", (new ItemRedstone()).setUnlocalizedName("redstone").setPotionEffect(PotionHelper.redstoneEffect).setTextureName("redstone_dust"));
        field_150901_e.func_148756_a(332, "snowball", (new ItemSnowball()).setUnlocalizedName("snowball").setTextureName("snowball"));
        field_150901_e.func_148756_a(333, "boat", (new ItemBoat()).setUnlocalizedName("boat").setTextureName("boat"));
        field_150901_e.func_148756_a(334, "leather", (new Item()).setUnlocalizedName("leather").setCreativeTab(CreativeTabs.tabMaterials).setTextureName("leather"));
        field_150901_e.func_148756_a(335, "milk_bucket", (new ItemBucketMilk()).setUnlocalizedName("milk").setContainerItem(item).setTextureName("bucket_milk"));
        field_150901_e.func_148756_a(336, "brick", (new Item()).setUnlocalizedName("brick").setCreativeTab(CreativeTabs.tabMaterials).setTextureName("brick"));
        field_150901_e.func_148756_a(337, "clay_ball", (new Item()).setUnlocalizedName("clay").setCreativeTab(CreativeTabs.tabMaterials).setTextureName("clay_ball"));
        field_150901_e.func_148756_a(338, "reeds", (new ItemReed(Blocks.reeds)).setUnlocalizedName("reeds").setCreativeTab(CreativeTabs.tabMaterials).setTextureName("reeds"));
        field_150901_e.func_148756_a(339, "paper", (new Item()).setUnlocalizedName("paper").setCreativeTab(CreativeTabs.tabMisc).setTextureName("paper"));
        field_150901_e.func_148756_a(340, "book", (new ItemBook()).setUnlocalizedName("book").setCreativeTab(CreativeTabs.tabMisc).setTextureName("book_normal"));
        field_150901_e.func_148756_a(341, "slime_ball", (new Item()).setUnlocalizedName("slimeball").setCreativeTab(CreativeTabs.tabMisc).setTextureName("slimeball"));
        field_150901_e.func_148756_a(342, "chest_minecart", (new ItemMinecart(1)).setUnlocalizedName("minecartChest").setTextureName("minecart_chest"));
        field_150901_e.func_148756_a(343, "furnace_minecart", (new ItemMinecart(2)).setUnlocalizedName("minecartFurnace").setTextureName("minecart_furnace"));
        field_150901_e.func_148756_a(344, "egg", (new ItemEgg()).setUnlocalizedName("egg").setTextureName("egg"));
        field_150901_e.func_148756_a(345, "compass", (new Item()).setUnlocalizedName("compass").setCreativeTab(CreativeTabs.tabTools).setTextureName("compass"));
        field_150901_e.func_148756_a(346, "fishing_rod", (new ItemFishingRod()).setUnlocalizedName("fishingRod").setTextureName("fishing_rod"));
        field_150901_e.func_148756_a(347, "clock", (new Item()).setUnlocalizedName("clock").setCreativeTab(CreativeTabs.tabTools).setTextureName("clock"));
        field_150901_e.func_148756_a(348, "glowstone_dust", (new Item()).setUnlocalizedName("yellowDust").setPotionEffect(PotionHelper.glowstoneEffect).setCreativeTab(CreativeTabs.tabMaterials).setTextureName("glowstone_dust"));
        field_150901_e.func_148756_a(349, "fish", (new ItemFishFood(false)).setUnlocalizedName("fish").setTextureName("fish_raw").setHasSubtypes(true));
        field_150901_e.func_148756_a(350, "cooked_fished", (new ItemFishFood(true)).setUnlocalizedName("fish").setTextureName("fish_cooked").setHasSubtypes(true));
        field_150901_e.func_148756_a(351, "dye", (new ItemDye()).setUnlocalizedName("dyePowder").setTextureName("dye_powder"));
        field_150901_e.func_148756_a(352, "bone", (new Item()).setUnlocalizedName("bone").setFull3D().setCreativeTab(CreativeTabs.tabMisc).setTextureName("bone"));
        field_150901_e.func_148756_a(353, "sugar", (new Item()).setUnlocalizedName("sugar").setPotionEffect(PotionHelper.sugarEffect).setCreativeTab(CreativeTabs.tabMaterials).setTextureName("sugar"));
        field_150901_e.func_148756_a(354, "cake", (new ItemReed(Blocks.cake)).setMaxStackSize(1).setUnlocalizedName("cake").setCreativeTab(CreativeTabs.tabFood).setTextureName("cake"));
        field_150901_e.func_148756_a(355, "bed", (new ItemBed()).setMaxStackSize(1).setUnlocalizedName("bed").setTextureName("bed"));
        field_150901_e.func_148756_a(356, "repeater", (new ItemReed(Blocks.unpowered_repeater)).setUnlocalizedName("diode").setCreativeTab(CreativeTabs.tabRedstone).setTextureName("repeater"));
        field_150901_e.func_148756_a(357, "cookie", (new ItemFood(2, 0.1F, false)).setUnlocalizedName("cookie").setTextureName("cookie"));
        field_150901_e.func_148756_a(358, "filled_map", (new ItemMap()).setUnlocalizedName("map").setTextureName("map_filled"));
        field_150901_e.func_148756_a(359, "shears", (new ItemShears()).setUnlocalizedName("shears").setTextureName("shears"));
        field_150901_e.func_148756_a(360, "melon", (new ItemFood(2, 0.3F, false)).setUnlocalizedName("melon").setTextureName("melon"));
        field_150901_e.func_148756_a(361, "pumpkin_seeds", (new ItemSeeds(Blocks.pumpkin_stem, Blocks.farmland)).setUnlocalizedName("seeds_pumpkin").setTextureName("seeds_pumpkin"));
        field_150901_e.func_148756_a(362, "melon_seeds", (new ItemSeeds(Blocks.melon_stem, Blocks.farmland)).setUnlocalizedName("seeds_melon").setTextureName("seeds_melon"));
        field_150901_e.func_148756_a(363, "beef", (new ItemFood(3, 0.3F, true)).setUnlocalizedName("beefRaw").setTextureName("beef_raw"));
        field_150901_e.func_148756_a(364, "cooked_beef", (new ItemFood(8, 0.8F, true)).setUnlocalizedName("beefCooked").setTextureName("beef_cooked"));
        field_150901_e.func_148756_a(365, "chicken", (new ItemFood(2, 0.3F, true)).setPotionEffect(Potion.hunger.id, 30, 0, 0.3F).setUnlocalizedName("chickenRaw").setTextureName("chicken_raw"));
        field_150901_e.func_148756_a(366, "cooked_chicken", (new ItemFood(6, 0.6F, true)).setUnlocalizedName("chickenCooked").setTextureName("chicken_cooked"));
        field_150901_e.func_148756_a(367, "rotten_flesh", (new ItemFood(4, 0.1F, true)).setPotionEffect(Potion.hunger.id, 30, 0, 0.8F).setUnlocalizedName("rottenFlesh").setTextureName("rotten_flesh"));
        field_150901_e.func_148756_a(368, "ender_pearl", (new ItemEnderPearl()).setUnlocalizedName("enderPearl").setTextureName("ender_pearl"));
        field_150901_e.func_148756_a(369, "blaze_rod", (new Item()).setUnlocalizedName("blazeRod").setCreativeTab(CreativeTabs.tabMaterials).setTextureName("blaze_rod"));
        field_150901_e.func_148756_a(370, "ghast_tear", (new Item()).setUnlocalizedName("ghastTear").setPotionEffect(PotionHelper.ghastTearEffect).setCreativeTab(CreativeTabs.tabBrewing).setTextureName("ghast_tear"));
        field_150901_e.func_148756_a(371, "gold_nugget", (new Item()).setUnlocalizedName("goldNugget").setCreativeTab(CreativeTabs.tabMaterials).setTextureName("gold_nugget"));
        field_150901_e.func_148756_a(372, "nether_wart", (new ItemSeeds(Blocks.nether_wart, Blocks.soul_sand)).setUnlocalizedName("netherStalkSeeds").setPotionEffect("+4").setTextureName("nether_wart"));
        field_150901_e.func_148756_a(373, "potion", (new ItemPotion()).setUnlocalizedName("potion").setTextureName("potion"));
        field_150901_e.func_148756_a(374, "glass_bottle", (new ItemGlassBottle()).setUnlocalizedName("glassBottle").setTextureName("potion_bottle_empty"));
        field_150901_e.func_148756_a(375, "spider_eye", (new ItemFood(2, 0.8F, false)).setPotionEffect(Potion.poison.id, 5, 0, 1.0F).setUnlocalizedName("spiderEye").setPotionEffect(PotionHelper.spiderEyeEffect).setTextureName("spider_eye"));
        field_150901_e.func_148756_a(376, "fermented_spider_eye", (new Item()).setUnlocalizedName("fermentedSpiderEye").setPotionEffect(PotionHelper.fermentedSpiderEyeEffect).setCreativeTab(CreativeTabs.tabBrewing).setTextureName("spider_eye_fermented"));
        field_150901_e.func_148756_a(377, "blaze_powder", (new Item()).setUnlocalizedName("blazePowder").setPotionEffect(PotionHelper.blazePowderEffect).setCreativeTab(CreativeTabs.tabBrewing).setTextureName("blaze_powder"));
        field_150901_e.func_148756_a(378, "magma_cream", (new Item()).setUnlocalizedName("magmaCream").setPotionEffect(PotionHelper.magmaCreamEffect).setCreativeTab(CreativeTabs.tabBrewing).setTextureName("magma_cream"));
        field_150901_e.func_148756_a(379, "brewing_stand", (new ItemReed(Blocks.brewing_stand)).setUnlocalizedName("brewingStand").setCreativeTab(CreativeTabs.tabBrewing).setTextureName("brewing_stand"));
        field_150901_e.func_148756_a(380, "cauldron", (new ItemReed(Blocks.cauldron)).setUnlocalizedName("cauldron").setCreativeTab(CreativeTabs.tabBrewing).setTextureName("cauldron"));
        field_150901_e.func_148756_a(381, "ender_eye", (new ItemEnderEye()).setUnlocalizedName("eyeOfEnder").setTextureName("ender_eye"));
        field_150901_e.func_148756_a(382, "speckled_melon", (new Item()).setUnlocalizedName("speckledMelon").setPotionEffect(PotionHelper.speckledMelonEffect).setCreativeTab(CreativeTabs.tabBrewing).setTextureName("melon_speckled"));
        field_150901_e.func_148756_a(383, "spawn_egg", (new ItemMonsterPlacer()).setUnlocalizedName("monsterPlacer").setTextureName("spawn_egg"));
        field_150901_e.func_148756_a(384, "experience_bottle", (new ItemExpBottle()).setUnlocalizedName("expBottle").setTextureName("experience_bottle"));
        field_150901_e.func_148756_a(385, "fire_charge", (new ItemFireball()).setUnlocalizedName("fireball").setTextureName("fireball"));
        field_150901_e.func_148756_a(386, "writable_book", (new ItemWritableBook()).setUnlocalizedName("writingBook").setCreativeTab(CreativeTabs.tabMisc).setTextureName("book_writable"));
        field_150901_e.func_148756_a(387, "written_book", (new ItemEditableBook()).setUnlocalizedName("writtenBook").setTextureName("book_written").setMaxStackSize(16));
        field_150901_e.func_148756_a(388, "emerald", (new Item()).setUnlocalizedName("emerald").setCreativeTab(CreativeTabs.tabMaterials).setTextureName("emerald"));
        field_150901_e.func_148756_a(389, "item_frame", (new ItemHangingEntity(EntityItemFrame.class)).setUnlocalizedName("frame").setTextureName("item_frame"));
        field_150901_e.func_148756_a(390, "flower_pot", (new ItemReed(Blocks.flower_pot)).setUnlocalizedName("flowerPot").setCreativeTab(CreativeTabs.tabDecorations).setTextureName("flower_pot"));
        field_150901_e.func_148756_a(391, "carrot", (new ItemSeedFood(4, 0.6F, Blocks.carrots, Blocks.farmland)).setUnlocalizedName("carrots").setTextureName("carrot"));
        field_150901_e.func_148756_a(392, "potato", (new ItemSeedFood(1, 0.3F, Blocks.potatoes, Blocks.farmland)).setUnlocalizedName("potato").setTextureName("potato"));
        field_150901_e.func_148756_a(393, "baked_potato", (new ItemFood(6, 0.6F, false)).setUnlocalizedName("potatoBaked").setTextureName("potato_baked"));
        field_150901_e.func_148756_a(394, "poisonous_potato", (new ItemFood(2, 0.3F, false)).setPotionEffect(Potion.poison.id, 5, 0, 0.6F).setUnlocalizedName("potatoPoisonous").setTextureName("potato_poisonous"));
        field_150901_e.func_148756_a(395, "map", (new ItemEmptyMap()).setUnlocalizedName("emptyMap").setTextureName("map_empty"));
        field_150901_e.func_148756_a(396, "golden_carrot", (new ItemFood(6, 1.2F, false)).setUnlocalizedName("carrotGolden").setPotionEffect(PotionHelper.goldenCarrotEffect).setTextureName("carrot_golden"));
        field_150901_e.func_148756_a(397, "skull", (new ItemSkull()).setUnlocalizedName("skull").setTextureName("skull"));
        field_150901_e.func_148756_a(398, "carrot_on_a_stick", (new ItemCarrotOnAStick()).setUnlocalizedName("carrotOnAStick").setTextureName("carrot_on_a_stick"));
        field_150901_e.func_148756_a(399, "nether_star", (new ItemSimpleFoiled()).setUnlocalizedName("netherStar").setCreativeTab(CreativeTabs.tabMaterials).setTextureName("nether_star"));
        field_150901_e.func_148756_a(400, "pumpkin_pie", (new ItemFood(8, 0.3F, false)).setUnlocalizedName("pumpkinPie").setCreativeTab(CreativeTabs.tabFood).setTextureName("pumpkin_pie"));
        field_150901_e.func_148756_a(401, "fireworks", (new ItemFirework()).setUnlocalizedName("fireworks").setTextureName("fireworks"));
        field_150901_e.func_148756_a(402, "firework_charge", (new ItemFireworkCharge()).setUnlocalizedName("fireworksCharge").setCreativeTab(CreativeTabs.tabMisc).setTextureName("fireworks_charge"));
        field_150901_e.func_148756_a(403, "enchanted_book", (new ItemEnchantedBook()).setMaxStackSize(1).setUnlocalizedName("enchantedBook").setTextureName("book_enchanted"));
        field_150901_e.func_148756_a(404, "comparator", (new ItemReed(Blocks.unpowered_comparator)).setUnlocalizedName("comparator").setCreativeTab(CreativeTabs.tabRedstone).setTextureName("comparator"));
        field_150901_e.func_148756_a(405, "netherbrick", (new Item()).setUnlocalizedName("netherbrick").setCreativeTab(CreativeTabs.tabMaterials).setTextureName("netherbrick"));
        field_150901_e.func_148756_a(406, "quartz", (new Item()).setUnlocalizedName("netherquartz").setCreativeTab(CreativeTabs.tabMaterials).setTextureName("quartz"));
        field_150901_e.func_148756_a(407, "tnt_minecart", (new ItemMinecart(3)).setUnlocalizedName("minecartTnt").setTextureName("minecart_tnt"));
        field_150901_e.func_148756_a(408, "hopper_minecart", (new ItemMinecart(5)).setUnlocalizedName("minecartHopper").setTextureName("minecart_hopper"));
        field_150901_e.func_148756_a(417, "iron_horse_armor", (new Item()).setUnlocalizedName("horsearmormetal").setMaxStackSize(1).setCreativeTab(CreativeTabs.tabMisc).setTextureName("iron_horse_armor"));
        field_150901_e.func_148756_a(418, "golden_horse_armor", (new Item()).setUnlocalizedName("horsearmorgold").setMaxStackSize(1).setCreativeTab(CreativeTabs.tabMisc).setTextureName("gold_horse_armor"));
        field_150901_e.func_148756_a(419, "diamond_horse_armor", (new Item()).setUnlocalizedName("horsearmordiamond").setMaxStackSize(1).setCreativeTab(CreativeTabs.tabMisc).setTextureName("diamond_horse_armor"));
        field_150901_e.func_148756_a(420, "lead", (new ItemLead()).setUnlocalizedName("leash").setTextureName("lead"));
        field_150901_e.func_148756_a(421, "name_tag", (new ItemNameTag()).setUnlocalizedName("nameTag").setTextureName("name_tag"));
        field_150901_e.func_148756_a(422, "command_block_minecart", (new ItemMinecart(6)).setUnlocalizedName("minecartCommandBlock").setTextureName("minecart_command_block").setCreativeTab((CreativeTabs)null));
        field_150901_e.func_148756_a(2256, "record_13", (new ItemRecord("13")).setUnlocalizedName("record").setTextureName("record_13"));
        field_150901_e.func_148756_a(2257, "record_cat", (new ItemRecord("cat")).setUnlocalizedName("record").setTextureName("record_cat"));
        field_150901_e.func_148756_a(2258, "record_blocks", (new ItemRecord("blocks")).setUnlocalizedName("record").setTextureName("record_blocks"));
        field_150901_e.func_148756_a(2259, "record_chirp", (new ItemRecord("chirp")).setUnlocalizedName("record").setTextureName("record_chirp"));
        field_150901_e.func_148756_a(2260, "record_far", (new ItemRecord("far")).setUnlocalizedName("record").setTextureName("record_far"));
        field_150901_e.func_148756_a(2261, "record_mall", (new ItemRecord("mall")).setUnlocalizedName("record").setTextureName("record_mall"));
        field_150901_e.func_148756_a(2262, "record_mellohi", (new ItemRecord("mellohi")).setUnlocalizedName("record").setTextureName("record_mellohi"));
        field_150901_e.func_148756_a(2263, "record_stal", (new ItemRecord("stal")).setUnlocalizedName("record").setTextureName("record_stal"));
        field_150901_e.func_148756_a(2264, "record_strad", (new ItemRecord("strad")).setUnlocalizedName("record").setTextureName("record_strad"));
        field_150901_e.func_148756_a(2265, "record_ward", (new ItemRecord("ward")).setUnlocalizedName("record").setTextureName("record_ward"));
        field_150901_e.func_148756_a(2266, "record_11", (new ItemRecord("11")).setUnlocalizedName("record").setTextureName("record_11"));
        field_150901_e.func_148756_a(2267, "record_wait", (new ItemRecord("wait")).setUnlocalizedName("record").setTextureName("record_wait"));
        HashSet hashset = Sets.newHashSet(new Block[] {Blocks.air, Blocks.brewing_stand, Blocks.bed, Blocks.nether_wart, Blocks.cauldron, Blocks.flower_pot, Blocks.wheat, Blocks.reeds, Blocks.cake, Blocks.skull, Blocks.piston_head, Blocks.piston_extension, Blocks.lit_redstone_ore, Blocks.powered_repeater, Blocks.pumpkin_stem, Blocks.standing_sign, Blocks.powered_comparator, Blocks.tripwire, Blocks.lit_redstone_lamp, Blocks.melon_stem, Blocks.unlit_redstone_torch, Blocks.unpowered_comparator, Blocks.redstone_wire, Blocks.wall_sign, Blocks.unpowered_repeater, Blocks.iron_door, Blocks.wooden_door});
        Iterator iterator = Block.field_149771_c.func_148742_b().iterator();

        while (iterator.hasNext())
        {
            String s = (String)iterator.next();
            Block block = (Block)Block.field_149771_c.getObject(s);
            Object object;

            if (block == Blocks.wool)
            {
                object = (new ItemCloth(Blocks.wool)).setUnlocalizedName("cloth");
            }
            else if (block == Blocks.stained_hardened_clay)
            {
                object = (new ItemCloth(Blocks.stained_hardened_clay)).setUnlocalizedName("clayHardenedStained");
            }
            else if (block == Blocks.stained_glass)
            {
                object = (new ItemCloth(Blocks.stained_glass)).setUnlocalizedName("stainedGlass");
            }
            else if (block == Blocks.stained_glass_pane)
            {
                object = (new ItemCloth(Blocks.stained_glass_pane)).setUnlocalizedName("stainedGlassPane");
            }
            else if (block == Blocks.carpet)
            {
                object = (new ItemCloth(Blocks.carpet)).setUnlocalizedName("woolCarpet");
            }
            else if (block == Blocks.dirt)
            {
                object = (new ItemMultiTexture(Blocks.dirt, Blocks.dirt, BlockDirt.field_150009_a)).setUnlocalizedName("dirt");
            }
            else if (block == Blocks.sand)
            {
                object = (new ItemMultiTexture(Blocks.sand, Blocks.sand, BlockSand.field_149838_a)).setUnlocalizedName("sand");
            }
            else if (block == Blocks.log)
            {
                object = (new ItemMultiTexture(Blocks.log, Blocks.log, BlockOldLog.field_150168_M)).setUnlocalizedName("log");
            }
            else if (block == Blocks.log2)
            {
                object = (new ItemMultiTexture(Blocks.log2, Blocks.log2, BlockNewLog.field_150169_M)).setUnlocalizedName("log");
            }
            else if (block == Blocks.planks)
            {
                object = (new ItemMultiTexture(Blocks.planks, Blocks.planks, BlockWood.field_150096_a)).setUnlocalizedName("wood");
            }
            else if (block == Blocks.monster_egg)
            {
                object = (new ItemMultiTexture(Blocks.monster_egg, Blocks.monster_egg, BlockSilverfish.field_150198_a)).setUnlocalizedName("monsterStoneEgg");
            }
            else if (block == Blocks.stonebrick)
            {
                object = (new ItemMultiTexture(Blocks.stonebrick, Blocks.stonebrick, BlockStoneBrick.field_150142_a)).setUnlocalizedName("stonebricksmooth");
            }
            else if (block == Blocks.sandstone)
            {
                object = (new ItemMultiTexture(Blocks.sandstone, Blocks.sandstone, BlockSandStone.field_150157_a)).setUnlocalizedName("sandStone");
            }
            else if (block == Blocks.quartz_block)
            {
                object = (new ItemMultiTexture(Blocks.quartz_block, Blocks.quartz_block, BlockQuartz.field_150191_a)).setUnlocalizedName("quartzBlock");
            }
            else if (block == Blocks.stone_slab)
            {
                object = (new ItemSlab(Blocks.stone_slab, Blocks.stone_slab, Blocks.double_stone_slab, false)).setUnlocalizedName("stoneSlab");
            }
            else if (block == Blocks.double_stone_slab)
            {
                object = (new ItemSlab(Blocks.double_stone_slab, Blocks.stone_slab, Blocks.double_stone_slab, true)).setUnlocalizedName("stoneSlab");
            }
            else if (block == Blocks.wooden_slab)
            {
                object = (new ItemSlab(Blocks.wooden_slab, Blocks.wooden_slab, Blocks.double_wooden_slab, false)).setUnlocalizedName("woodSlab");
            }
            else if (block == Blocks.double_wooden_slab)
            {
                object = (new ItemSlab(Blocks.double_wooden_slab, Blocks.wooden_slab, Blocks.double_wooden_slab, true)).setUnlocalizedName("woodSlab");
            }
            else if (block == Blocks.sapling)
            {
                object = (new ItemMultiTexture(Blocks.sapling, Blocks.sapling, BlockSapling.field_149882_a)).setUnlocalizedName("sapling");
            }
            else if (block == Blocks.leaves)
            {
                object = (new ItemLeaves(Blocks.leaves)).setUnlocalizedName("leaves");
            }
            else if (block == Blocks.leaves2)
            {
                object = (new ItemLeaves(Blocks.leaves2)).setUnlocalizedName("leaves");
            }
            else if (block == Blocks.vine)
            {
                object = new ItemColored(Blocks.vine, false);
            }
            else if (block == Blocks.tallgrass)
            {
                object = (new ItemColored(Blocks.tallgrass, true)).func_150943_a(new String[] {"shrub", "grass", "fern"});
            }
            else if (block == Blocks.yellow_flower)
            {
                object = (new ItemMultiTexture(Blocks.yellow_flower, Blocks.yellow_flower, BlockFlower.field_149858_b)).setUnlocalizedName("flower");
            }
            else if (block == Blocks.red_flower)
            {
                object = (new ItemMultiTexture(Blocks.red_flower, Blocks.red_flower, BlockFlower.field_149859_a)).setUnlocalizedName("rose");
            }
            else if (block == Blocks.snow_layer)
            {
                object = new ItemSnow(Blocks.snow_layer, Blocks.snow_layer);
            }
            else if (block == Blocks.waterlily)
            {
                object = new ItemLilyPad(Blocks.waterlily);
            }
            else if (block == Blocks.piston)
            {
                object = new ItemPiston(Blocks.piston);
            }
            else if (block == Blocks.sticky_piston)
            {
                object = new ItemPiston(Blocks.sticky_piston);
            }
            else if (block == Blocks.cobblestone_wall)
            {
                object = (new ItemMultiTexture(Blocks.cobblestone_wall, Blocks.cobblestone_wall, BlockWall.field_150092_a)).setUnlocalizedName("cobbleWall");
            }
            else if (block == Blocks.anvil)
            {
                object = (new ItemAnvilBlock(Blocks.anvil)).setUnlocalizedName("anvil");
            }
            else if (block == Blocks.double_plant)
            {
                object = (new ItemDoublePlant(Blocks.double_plant, Blocks.double_plant, BlockDoublePlant.field_149892_a)).setUnlocalizedName("doublePlant");
            }
            else
            {
                if (hashset.contains(block))
                {
                    continue;
                }

                object = new ItemBlock(block);
            }

            field_150901_e.func_148756_a(Block.func_149682_b(block), s, object);
        }
    }

    public Item setMaxStackSize(int par1)
    {
        this.maxStackSize = par1;
        return this;
    }

    // JAVADOC METHOD $$ func_94901_k
    @SideOnly(Side.CLIENT)
    public int getSpriteNumber()
    {
        return 1;
    }

    // JAVADOC METHOD $$ func_77617_a
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int par1)
    {
        return this.itemIcon;
    }

    // JAVADOC METHOD $$ func_77650_f
    @SideOnly(Side.CLIENT)
    public IIcon getIconIndex(ItemStack par1ItemStack)
    {
        return this.getIconFromDamage(par1ItemStack.getItemDamage());
    }

    // JAVADOC METHOD $$ func_77648_a
    public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, int par4, int par5, int par6, int par7, float par8, float par9, float par10)
    {
        return false;
    }

    public float func_150893_a(ItemStack p_150893_1_, Block p_150893_2_)
    {
        return 1.0F;
    }

    // JAVADOC METHOD $$ func_77659_a
    public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer)
    {
        return par1ItemStack;
    }

    public ItemStack onEaten(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer)
    {
        return par1ItemStack;
    }

    // JAVADOC METHOD $$ func_77639_j
    @Deprecated
    public int getItemStackLimit()
    {
        return this.maxStackSize;
    }

    // JAVADOC METHOD $$ func_77647_b
    public int getMetadata(int par1)
    {
        return 0;
    }

    public boolean getHasSubtypes()
    {
        return this.hasSubtypes;
    }

    public Item setHasSubtypes(boolean par1)
    {
        this.hasSubtypes = par1;
        return this;
    }

    // JAVADOC METHOD $$ func_77612_l
    public int getMaxDamage()
    {
        return this.maxDamage;
    }

    // JAVADOC METHOD $$ func_77656_e
    public Item setMaxDamage(int par1)
    {
        this.maxDamage = par1;
        return this;
    }

    public boolean isDamageable()
    {
        return this.maxDamage > 0 && !this.hasSubtypes;
    }

    // JAVADOC METHOD $$ func_77644_a
    public boolean hitEntity(ItemStack par1ItemStack, EntityLivingBase par2EntityLivingBase, EntityLivingBase par3EntityLivingBase)
    {
        return false;
    }

    public boolean func_150894_a(ItemStack p_150894_1_, World p_150894_2_, Block p_150894_3_, int p_150894_4_, int p_150894_5_, int p_150894_6_, EntityLivingBase p_150894_7_)
    {
        return false;
    }

    public boolean func_150897_b(Block p_150897_1_)
    {
        return false;
    }

    // JAVADOC METHOD $$ func_111207_a
    public boolean itemInteractionForEntity(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, EntityLivingBase par3EntityLivingBase)
    {
        return false;
    }

    // JAVADOC METHOD $$ func_77664_n
    public Item setFull3D()
    {
        this.bFull3D = true;
        return this;
    }

    // JAVADOC METHOD $$ func_77662_d
    @SideOnly(Side.CLIENT)
    public boolean isFull3D()
    {
        return this.bFull3D;
    }

    // JAVADOC METHOD $$ func_77629_n_
    @SideOnly(Side.CLIENT)
    public boolean shouldRotateAroundWhenRendering()
    {
        return false;
    }

    // JAVADOC METHOD $$ func_77655_b
    public Item setUnlocalizedName(String par1Str)
    {
        this.unlocalizedName = par1Str;
        return this;
    }

    // JAVADOC METHOD $$ func_77657_g
    public String getUnlocalizedNameInefficiently(ItemStack par1ItemStack)
    {
        String s = this.getUnlocalizedName(par1ItemStack);
        return s == null ? "" : StatCollector.translateToLocal(s);
    }

    // JAVADOC METHOD $$ func_77658_a
    public String getUnlocalizedName()
    {
        return "item." + this.unlocalizedName;
    }

    // JAVADOC METHOD $$ func_77667_c
    public String getUnlocalizedName(ItemStack par1ItemStack)
    {
        return "item." + this.unlocalizedName;
    }

    public Item setContainerItem(Item par1Item)
    {
        this.containerItem = par1Item;
        return this;
    }

    // JAVADOC METHOD $$ func_77630_h
    public boolean doesContainerItemLeaveCraftingGrid(ItemStack par1ItemStack)
    {
        return true;
    }

    // JAVADOC METHOD $$ func_77651_p
    public boolean getShareTag()
    {
        return true;
    }

    public Item getContainerItem()
    {
        return this.containerItem;
    }

    // JAVADOC METHOD $$ func_77634_r
    public boolean hasContainerItem()
    {
        return this.containerItem != null;
    }

    @SideOnly(Side.CLIENT)
    public int getColorFromItemStack(ItemStack par1ItemStack, int par2)
    {
        return 16777215;
    }

    // JAVADOC METHOD $$ func_77663_a
    public void onUpdate(ItemStack par1ItemStack, World par2World, Entity par3Entity, int par4, boolean par5) {}

    // JAVADOC METHOD $$ func_77622_d
    public void onCreated(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer) {}

    // JAVADOC METHOD $$ func_77643_m_
    public boolean isMap()
    {
        return false;
    }

    // JAVADOC METHOD $$ func_77661_b
    public EnumAction getItemUseAction(ItemStack par1ItemStack)
    {
        return EnumAction.none;
    }

    // JAVADOC METHOD $$ func_77626_a
    public int getMaxItemUseDuration(ItemStack par1ItemStack)
    {
        return 0;
    }

    // JAVADOC METHOD $$ func_77615_a
    public void onPlayerStoppedUsing(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer, int par4) {}

    // JAVADOC METHOD $$ func_77631_c
    public Item setPotionEffect(String par1Str)
    {
        this.potionEffect = par1Str;
        return this;
    }

    public String func_150896_i(ItemStack p_150896_1_)
    {
        return this.potionEffect;
    }

    public boolean func_150892_m(ItemStack p_150892_1_)
    {
        return this.func_150896_i(p_150892_1_) != null;
    }

    // JAVADOC METHOD $$ func_77624_a
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4) {}

    public String getItemStackDisplayName(ItemStack par1ItemStack)
    {
        return ("" + StatCollector.translateToLocal(this.getUnlocalizedNameInefficiently(par1ItemStack) + ".name")).trim();
    }

    @SideOnly(Side.CLIENT)
    @Deprecated
    public boolean hasEffect(ItemStack par1ItemStack)
    {
        return par1ItemStack.isItemEnchanted();
    }

    // JAVADOC METHOD $$ func_77613_e
    public EnumRarity getRarity(ItemStack par1ItemStack)
    {
        return par1ItemStack.isItemEnchanted() ? EnumRarity.rare : EnumRarity.common;
    }

    // JAVADOC METHOD $$ func_77616_k
    public boolean isItemTool(ItemStack par1ItemStack)
    {
        return this.getItemStackLimit(par1ItemStack) == 1 && this.isDamageable();
    }

    protected MovingObjectPosition getMovingObjectPositionFromPlayer(World par1World, EntityPlayer par2EntityPlayer, boolean par3)
    {
        float f = 1.0F;
        float f1 = par2EntityPlayer.prevRotationPitch + (par2EntityPlayer.rotationPitch - par2EntityPlayer.prevRotationPitch) * f;
        float f2 = par2EntityPlayer.prevRotationYaw + (par2EntityPlayer.rotationYaw - par2EntityPlayer.prevRotationYaw) * f;
        double d0 = par2EntityPlayer.prevPosX + (par2EntityPlayer.posX - par2EntityPlayer.prevPosX) * (double)f;
        double d1 = par2EntityPlayer.prevPosY + (par2EntityPlayer.posY - par2EntityPlayer.prevPosY) * (double)f + (double)(par1World.isRemote ? par2EntityPlayer.getEyeHeight() - par2EntityPlayer.getDefaultEyeHeight() : par2EntityPlayer.getEyeHeight()); // isRemote check to revert changes to ray trace position due to adding the eye height clientside and player yOffset differences
        double d2 = par2EntityPlayer.prevPosZ + (par2EntityPlayer.posZ - par2EntityPlayer.prevPosZ) * (double)f;
        Vec3 vec3 = par1World.getWorldVec3Pool().getVecFromPool(d0, d1, d2);
        float f3 = MathHelper.cos(-f2 * 0.017453292F - (float)Math.PI);
        float f4 = MathHelper.sin(-f2 * 0.017453292F - (float)Math.PI);
        float f5 = -MathHelper.cos(-f1 * 0.017453292F);
        float f6 = MathHelper.sin(-f1 * 0.017453292F);
        float f7 = f4 * f5;
        float f8 = f3 * f5;
        double d3 = 5.0D;
        if (par2EntityPlayer instanceof EntityPlayerMP)
        {
            d3 = ((EntityPlayerMP)par2EntityPlayer).theItemInWorldManager.getBlockReachDistance();
        }
        Vec3 vec31 = vec3.addVector((double)f7 * d3, (double)f6 * d3, (double)f8 * d3);
        return par1World.func_147447_a(vec3, vec31, par3, !par3, false);
    }

    // JAVADOC METHOD $$ func_77619_b
    public int getItemEnchantability()
    {
        return 0;
    }

    @SideOnly(Side.CLIENT)
    public boolean requiresMultipleRenderPasses()
    {
        return false;
    }

    // JAVADOC METHOD $$ func_77618_c
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamageForRenderPass(int par1, int par2)
    {
        return this.getIconFromDamage(par1);
    }

    @SideOnly(Side.CLIENT)
    public void func_150895_a(Item p_150895_1_, CreativeTabs p_150895_2_, List p_150895_3_)
    {
        p_150895_3_.add(new ItemStack(p_150895_1_, 1, 0));
    }

    // JAVADOC METHOD $$ func_77637_a
    public Item setCreativeTab(CreativeTabs par1CreativeTabs)
    {
        this.tabToDisplayOn = par1CreativeTabs;
        return this;
    }

    // JAVADOC METHOD $$ func_77640_w
    @SideOnly(Side.CLIENT)
    public CreativeTabs getCreativeTab()
    {
        return this.tabToDisplayOn;
    }

    // JAVADOC METHOD $$ func_82788_x
    public boolean canItemEditBlocks()
    {
        return true;
    }

    // JAVADOC METHOD $$ func_82789_a
    public boolean getIsRepairable(ItemStack par1ItemStack, ItemStack par2ItemStack)
    {
        return false;
    }

    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister par1IconRegister)
    {
        this.itemIcon = par1IconRegister.registerIcon(this.getIconString());
    }

    // JAVADOC METHOD $$ func_111205_h
    public Multimap getItemAttributeModifiers()
    {
        return HashMultimap.create();
    }

    public Item setTextureName(String par1Str)
    {
        this.iconString = par1Str;
        return this;
    }

    // JAVADOC METHOD $$ func_111208_A
    @SideOnly(Side.CLIENT)
    protected String getIconString()
    {
        return this.iconString == null ? "MISSING_ICON_ITEM_" + field_150901_e.func_148757_b(this) + "_" + this.unlocalizedName : this.iconString;
    }

    /* ======================================== FORGE START =====================================*/
    /**
     * Called when a player drops the item into the world,
     * returning false from this will prevent the item from
     * being removed from the players inventory and spawning
     * in the world
     *
     * @param player The player that dropped the item
     * @param item The item stack, before the item is removed.
     */
    public boolean onDroppedByPlayer(ItemStack item, EntityPlayer player)
    {
        return true;
    }

    /**
     * This is called when the item is used, before the block is activated.
     * @param stack The Item Stack
     * @param player The Player that used the item
     * @param world The Current World
     * @param x Target X Position
     * @param y Target Y Position
     * @param z Target Z Position
     * @param side The side of the target hit
     * @return Return true to prevent any further processing.
     */
    public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ)
    {
        return false;
    }

    /**
     * Metadata-sensitive version of getStrVsBlock
     * @param itemstack The Item Stack
     * @param block The block the item is trying to break
     * @param metadata The items current metadata
     * @return The damage strength
     */
    public float getDigSpeed(ItemStack itemstack, Block block, int metadata)
    {
        return func_150893_a(itemstack, block);
    }

    protected boolean canRepair = true;
    /**
     * Called by CraftingManager to determine if an item is reparable.
     * @return True if reparable
     */
    public boolean isRepairable()
    {
        return canRepair && isDamageable();
    }

    /**
     * Call to disable repair recipes.
     * @return The current Item instance
     */
    public Item setNoRepair()
    {
        canRepair = false;
        return this;
    }

    /**
     * Called before a block is broken.  Return true to prevent default block harvesting.
     *
     * Note: In SMP, this is called on both client and server sides!
     *
     * @param itemstack The current ItemStack
     * @param X The X Position
     * @param Y The X Position
     * @param Z The X Position
     * @param player The Player that is wielding the item
     * @return True to prevent harvesting, false to continue as normal
     */
    public boolean onBlockStartBreak(ItemStack itemstack, int X, int Y, int Z, EntityPlayer player)
    {
        return false;
    }

    /**
     * Called each tick while using an item.
     * @param stack The Item being used
     * @param player The Player using the item
     * @param count The amount of time in tick the item has been used for continuously
     */
    public void onUsingTick(ItemStack stack, EntityPlayer player, int count)
    {
    }

    /**
     * Called when the player Left Clicks (attacks) an entity.
     * Processed before damage is done, if return value is true further processing is canceled
     * and the entity is not attacked.
     *
     * @param stack The Item being used
     * @param player The player that is attacking
     * @param entity The entity being attacked
     * @return True to cancel the rest of the interaction.
     */
    public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity)
    {
        return false;
    }

    /**
     * Player, Render pass, and item usage sensitive version of getIconIndex.
     *
     * @param stack The item stack to get the icon for. (Usually this, and usingItem will be the same if usingItem is not null)
     * @param renderPass The pass to get the icon for, 0 is default.
     * @param player The player holding the item
     * @param usingItem The item the player is actively using. Can be null if not using anything.
     * @param useRemaining The ticks remaining for the active item.
     * @return The icon index
     */
    public IIcon getIcon(ItemStack stack, int renderPass, EntityPlayer player, ItemStack usingItem, int useRemaining)
    {
        return getIcon(stack, renderPass);
    }

    /**
     * Returns the number of render passes/layers this item has.
     * Usually equates to ItemRenderer.renderItem being called for this many passes.
     * Does not get called unless requiresMultipleRenderPasses() is true;
     *
     * @param metadata The item's metadata
     * @return The number of passes to run.
     */
    public int getRenderPasses(int metadata)
    {
        return requiresMultipleRenderPasses() ? 2 : 1;
    }

    /**
     * ItemStack sensitive version of getContainerItem.
     * Returns a full ItemStack instance of the result.
     *
     * @param itemStack The current ItemStack
     * @return The resulting ItemStack
     */
    public ItemStack getContainerItem(ItemStack itemStack)
    {
        if (!hasContainerItem())
        {
            return null;
        }
        return new ItemStack(getContainerItem());
    }

    /**
     * Retrieves the normal 'lifespan' of this item when it is dropped on the ground as a EntityItem.
     * This is in ticks, standard result is 6000, or 5 mins.
     *
     * @param itemStack The current ItemStack
     * @param world The world the entity is in
     * @return The normal lifespan in ticks.
     */
    public int getEntityLifespan(ItemStack itemStack, World world)
    {
        return 6000;
    }

    /**
     * Determines if this Item has a special entity for when they are in the world.
     * Is called when a EntityItem is spawned in the world, if true and Item#createCustomEntity
     * returns non null, the EntityItem will be destroyed and the new Entity will be added to the world.
     *
     * @param stack The current item stack
     * @return True of the item has a custom entity, If true, Item#createCustomEntity will be called
     */
    public boolean hasCustomEntity(ItemStack stack)
    {
        return false;
    }

    /**
     * This function should return a new entity to replace the dropped item.
     * Returning null here will not kill the EntityItem and will leave it to function normally.
     * Called when the item it placed in a world.
     *
     * @param world The world object
     * @param location The EntityItem object, useful for getting the position of the entity
     * @param itemstack The current item stack
     * @return A new Entity object to spawn or null
     */
    public Entity createEntity(World world, Entity location, ItemStack itemstack)
    {
        return null;
    }

    /**
     * Called by the default implemetation of EntityItem's onUpdate method, allowing for cleaner 
     * control over the update of the item without having to write a subclass.
     * 
     * @param entityItem The entity Item
     * @return Return true to skip any further update code.
     */
    public boolean onEntityItemUpdate(EntityItem entityItem)
    {
        return false;
    }

    /**
     * Gets a list of tabs that items belonging to this class can display on,
     * combined properly with getSubItems allows for a single item to span
     * many sub-items across many tabs.
     *
     * @return A list of all tabs that this item could possibly be one.
     */
    public CreativeTabs[] getCreativeTabs()
    {
        return new CreativeTabs[]{ getCreativeTab() };
    }

    /**
     * Determines the base experience for a player when they remove this item from a furnace slot.
     * This number must be between 0 and 1 for it to be valid.
     * This number will be multiplied by the stack size to get the total experience.
     *
     * @param item The item stack the player is picking up.
     * @return The amount to award for each item.
     */
    public float getSmeltingExperience(ItemStack item)
    {
        return -1; //-1 will default to the old lookups.
    }

    /**
     * Return the correct icon for rendering based on the supplied ItemStack and render pass.
     *
     * Defers to {@link #getIconFromDamageForRenderPass(int, int)}
     * @param stack to render for
     * @param pass the multi-render pass
     * @return the icon
     */
    public IIcon getIcon(ItemStack stack, int pass)
    {
        return getIconFromDamageForRenderPass(stack.getItemDamage(), pass);
    }

    /**
     * Generates the base Random item for a specific instance of the chest gen,
     * Enchanted books use this to pick a random enchantment.
     *
     * @param chest The chest category to generate for
     * @param rnd World RNG
     * @param original Original result registered with the chest gen hooks.
     * @return New values to use as the random item, typically this will be original
     */
    public WeightedRandomChestContent getChestGenBase(ChestGenHooks chest, Random rnd, WeightedRandomChestContent original)
    {
        if (this instanceof ItemEnchantedBook)
        {
            return ((ItemEnchantedBook)this).func_92112_a(rnd,
                    original.theMinimumChanceToGenerateItem,
                    original.theMaximumChanceToGenerateItem, original.itemWeight);
        }
        return original;
    }

    /**
     *
     * Should this item, when held, allow sneak-clicks to pass through to the underlying block?
     *
     * @param world The world 
     * @param x The X Position
     * @param y The X Position
     * @param z The X Position
     * @param player The Player that is wielding the item
     * @return
     */
    public boolean doesSneakBypassUse(World world, int x, int y, int z, EntityPlayer player)
    {
        return false;
    }

    /**
     * Called to tick armor in the armor slot. Override to do something
     *
     * @param world
     * @param player
     * @param itemStack
     */
    public void onArmorTick(World world, EntityPlayer player, ItemStack itemStack)
    {

    }

    /**
     * Determines if the specific ItemStack can be placed in the specified armor slot.
     *
     * @param stack The ItemStack
     * @param armorType Armor slot ID: 0: Helmet, 1: Chest, 2: Legs, 3: Boots
     * @param entity The entity trying to equip the armor
     * @return True if the given ItemStack can be inserted in the slot
     */
    public boolean isValidArmor(ItemStack stack, int armorType, Entity entity)
    {
        if (this instanceof ItemArmor)
        {
            return ((ItemArmor)this).armorType == armorType;
        }

        if (armorType == 0)
        {
            return this == Item.func_150898_a(Blocks.pumpkin) || this == Items.skull;
        }

        return false;
    }

    /**
     * Allow or forbid the specific book/item combination as an anvil enchant
     *
     * @param stack The item
     * @param book The book
     * @return if the enchantment is allowed
     */
    public boolean isBookEnchantable(ItemStack stack, ItemStack book)
    {
        return true;
    }

    /**
     * Called by RenderBiped and RenderPlayer to determine the armor texture that 
     * should be use for the currently equiped item.
     * This will only be called on instances of ItemArmor. 
     * 
     * Returning null from this function will use the default value.
     * 
     * @param stack ItemStack for the equpt armor
     * @param entity The entity wearing the armor
     * @param slot The slot the armor is in
     * @param type The subtype, can be null or "overlay"
     * @return Path of texture to bind, or null to use default
     */
    public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type)
    {
        return null;
    }

    /**
     * Returns the font renderer used to render tooltips and overlays for this item.
     * Returning null will use the standard font renderer.
     * 
     * @param stack The current item stack
     * @return A instance of FontRenderer or null to use default
     */
    @SideOnly(Side.CLIENT)
    public FontRenderer getFontRenderer(ItemStack stack)
    {
        return null;
    }

    /**
     * Override this method to have an item handle its own armor rendering.
     * 
     * @param  entityLiving  The entity wearing the armor 
     * @param  itemStack  The itemStack to render the model of 
     * @param  armorSlot  0=head, 1=torso, 2=legs, 3=feet
     * 
     * @return  A ModelBiped to render instead of the default
     */
    @SideOnly(Side.CLIENT)
    public ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack itemStack, int armorSlot)
    {
        return null;
    }

    /**
     * Called when a entity tries to play the 'swing' animation.
     *  
     * @param entityLiving The entity swinging the item.
     * @param stack The Item stack
     * @return True to cancel any further processing by EntityLiving 
     */
    public boolean onEntitySwing(EntityLivingBase entityLiving, ItemStack stack)
    {
        return false;
    }

    /**
     * Called when the client starts rendering the HUD, for whatever item the player currently has as a helmet. 
     * This is where pumpkins would render there overlay.
     *  
     * @param stack The ItemStack that is equipped
     * @param player Reference to the current client entity
     * @param resolution Resolution information about the current viewport and configured GUI Scale
     * @param partialTicks Partial ticks for the renderer, useful for interpolation
     * @param hasScreen If the player has a screen up, which will be rendered after this.
     * @param mouseX Mouse's X position on screen
     * @param mouseY Mouse's Y position on screen
     */
    @SideOnly(Side.CLIENT)
    public void renderHelmetOverlay(ItemStack stack, EntityPlayer player, ScaledResolution resolution, float partialTicks, boolean hasScreen, int mouseX, int mouseY){}

    /**
     * Return the itemDamage represented by this ItemStack. Defaults to the itemDamage field on ItemStack, but can be overridden here for other sources such as NBT.
     *
     * @param stack The itemstack that is damaged
     * @return the damage value
     */
    public int getDamage(ItemStack stack)
    {
        return stack.itemDamage;
    }

    /**
     * Return the itemDamage display value represented by this itemstack.
     * @param stack the stack
     * @return the damage value
     */
    public int getDisplayDamage(ItemStack stack)
    {
        return stack.itemDamage;
    }
    
    /**
     * Return the maxDamage for this ItemStack. Defaults to the maxDamage field in this item, 
     * but can be overridden here for other sources such as NBT.
     *
     * @param stack The itemstack that is damaged
     * @return the damage value
     */
    public int getMaxDamage(ItemStack stack)
    {
        return getMaxDamage();
    }

    /**
     * Return if this itemstack is damaged. Note only called if {@link #isDamageable()} is true.
     * @param stack the stack
     * @return if the stack is damaged
     */
    public boolean isDamaged(ItemStack stack)
    {
        return stack.itemDamage > 0;
    }

    /**
     * Set the damage for this itemstack. Note, this method is responsible for zero checking.
     * @param stack the stack
     * @param damage the new damage value
     */
    public void setDamage(ItemStack stack, int damage)
    {
        stack.itemDamage = damage;

        if (stack.itemDamage < 0)
        {
            stack.itemDamage = 0;
        }
    }

    /**
     * ItemStack sensitive version of {@link #canHarvestBlock(Block)} 
     * @param par1Block The block trying to harvest
     * @param itemStack The itemstack used to harvest the block
     * @return true if can harvest the block
     */
    public boolean canHarvestBlock(Block par1Block, ItemStack itemStack)
    {
        return func_150897_b(par1Block);  
    }

    /**
     * Render Pass sensitive version of hasEffect()
     */
    @SideOnly(Side.CLIENT)
    public boolean hasEffect(ItemStack par1ItemStack, int pass)
    {
        return hasEffect(par1ItemStack) && (pass == 0 || this != Items.potionitem);
    }

    /**
     * Gets the maximum number of items that this stack should be able to hold. 
     * This is a ItemStack (and thus NBT) sensitive version of Item.getItemStackLimit()
     * 
     * @param stack The ItemStack
     * @return THe maximum number this item can be stacked to
     */
    public int getItemStackLimit(ItemStack stack)
    {
        return this.getItemStackLimit();
    }

    private HashMap<String, Integer> toolClasses = new HashMap<String, Integer>();
    /**
     * Sets or removes the harvest level for the specified tool class.
     * 
     * @param toolClass Class
     * @param level Harvest level:
     *     Wood:    0
     *     Stone:   1
     *     Iron:    2
     *     Diamond: 3
     *     Gold:    0
     */
    public void setHarvestLevel(String toolClass, int level)
    {
        if (level < 0)
            toolClasses.remove(toolClass);
        else
            toolClasses.put(toolClass, level);
    }

    public Set<String> getToolClasses(ItemStack stack)
    {
        return toolClasses.keySet();
    }

    /**
     * Queries the harvest level of this item stack for the specifred tool class,
     * Returns -1 if this tool is not of the specified type
     * 
     * @param stack This item stack instance
     * @param toolClass Tool Class
     * @return Harvest level, or -1 if not the specified tool type.
     */
    public int getHarvestLevel(ItemStack stack, String toolClass)
    {
        Integer ret = toolClasses.get(toolClass);
        return ret == null ? -1 : ret; 
    }
    /* ======================================== FORGE END   =====================================*/

    public static enum ToolMaterial
    {
        WOOD(0, 59, 2.0F, 0.0F, 15),
        STONE(1, 131, 4.0F, 1.0F, 5),
        IRON(2, 250, 6.0F, 2.0F, 14),
        EMERALD(3, 1561, 8.0F, 3.0F, 10),
        GOLD(0, 32, 12.0F, 0.0F, 22);
        // JAVADOC FIELD $$ field_78001_f
        private final int harvestLevel;
        // JAVADOC FIELD $$ field_78002_g
        private final int maxUses;
        // JAVADOC FIELD $$ field_78010_h
        private final float efficiencyOnProperMaterial;
        // JAVADOC FIELD $$ field_78011_i
        private final float damageVsEntity;
        // JAVADOC FIELD $$ field_78008_j
        private final int enchantability;

        private static final String __OBFID = "CL_00000042";

        //Added by forge for custom Tool materials.
        public Item customCraftingMaterial = null;

        private ToolMaterial(int par3, int par4, float par5, float par6, int par7)
        {
            this.harvestLevel = par3;
            this.maxUses = par4;
            this.efficiencyOnProperMaterial = par5;
            this.damageVsEntity = par6;
            this.enchantability = par7;
        }

        // JAVADOC METHOD $$ func_77997_a
        public int getMaxUses()
        {
            return this.maxUses;
        }

        // JAVADOC METHOD $$ func_77998_b
        public float getEfficiencyOnProperMaterial()
        {
            return this.efficiencyOnProperMaterial;
        }

        // JAVADOC METHOD $$ func_78000_c
        public float getDamageVsEntity()
        {
            return this.damageVsEntity;
        }

        // JAVADOC METHOD $$ func_77996_d
        public int getHarvestLevel()
        {
            return this.harvestLevel;
        }

        // JAVADOC METHOD $$ func_77995_e
        public int getEnchantability()
        {
            return this.enchantability;
        }

        public Item func_150995_f()
        {
            switch (this)
            {
                case WOOD:    return Item.func_150898_a(Blocks.planks);
                case STONE:   return Item.func_150898_a(Blocks.cobblestone);
                case GOLD:    return Items.gold_ingot;
                case IRON:    return Items.iron_ingot;
                case EMERALD: return Items.diamond;
                default:      return customCraftingMaterial;
            }
        }
    }
}