package net.minecraft.src;

import net.minecraft.src.forge.ForgeHooks;
import java.util.ArrayList;
import java.util.Random;

public class Block
{
    public static final StepSound soundPowderFootstep = new StepSound("stone", 1.0F, 1.0F);
    public static final StepSound soundWoodFootstep = new StepSound("wood", 1.0F, 1.0F);
    public static final StepSound soundGravelFootstep = new StepSound("gravel", 1.0F, 1.0F);
    public static final StepSound soundGrassFootstep = new StepSound("grass", 1.0F, 1.0F);
    public static final StepSound soundStoneFootstep = new StepSound("stone", 1.0F, 1.0F);
    public static final StepSound soundMetalFootstep = new StepSound("stone", 1.0F, 1.5F);
    public static final StepSound soundGlassFootstep = new StepSoundStone("stone", 1.0F, 1.0F);
    public static final StepSound soundClothFootstep = new StepSound("cloth", 1.0F, 1.0F);
    public static final StepSound soundSandFootstep = new StepSoundSand("sand", 1.0F, 1.0F);

    /** List of ly/ff (BlockType) containing the already registered blocks. */
    public static final Block[] blocksList = new Block[4096];

    /**
     * An array of 256 booleans corresponding to the result of the isOpaqueCube() method for each block ID
     */
    public static final boolean[] opaqueCubeLookup = new boolean[4096];

    /** How much light is subtracted for going through this block */
    public static final int[] lightOpacity = new int[4096];

    /** Array of booleans that tells if a block can grass */
    public static final boolean[] canBlockGrass = new boolean[4096];

    /** Amount of light emitted */
    public static final int[] lightValue = new int[4096];
    public static final boolean[] requiresSelfNotify = new boolean[4096];

    /**
     * Flag if block ID should use the brightest neighbor light value as its own
     */
    public static boolean[] useNeighborBrightness = new boolean[4096];
    public static final Block stone = (new BlockStone(1, 1)).setHardness(1.5F).setResistance(10.0F).setStepSound(soundStoneFootstep).setBlockName("stone");
    public static final BlockGrass grass = (BlockGrass)(new BlockGrass(2)).setHardness(0.6F).setStepSound(soundGrassFootstep).setBlockName("grass");
    public static final Block dirt = (new BlockDirt(3, 2)).setHardness(0.5F).setStepSound(soundGravelFootstep).setBlockName("dirt");
    public static final Block cobblestone = (new Block(4, 16, Material.rock)).setHardness(2.0F).setResistance(10.0F).setStepSound(soundStoneFootstep).setBlockName("stonebrick");
    public static final Block planks = (new Block(5, 4, Material.wood)).setHardness(2.0F).setResistance(5.0F).setStepSound(soundWoodFootstep).setBlockName("wood").setRequiresSelfNotify();
    public static final Block sapling = (new BlockSapling(6, 15)).setHardness(0.0F).setStepSound(soundGrassFootstep).setBlockName("sapling").setRequiresSelfNotify();
    public static final Block bedrock = (new Block(7, 17, Material.rock)).setBlockUnbreakable().setResistance(6000000.0F).setStepSound(soundStoneFootstep).setBlockName("bedrock").disableStats();

    /** Flowing water block */
    public static final Block waterMoving = (new BlockFlowing(8, Material.water)).setHardness(100.0F).setLightOpacity(3).setBlockName("water").disableStats().setRequiresSelfNotify();

    /** Stationary water source block */
    public static final Block waterStill = (new BlockStationary(9, Material.water)).setHardness(100.0F).setLightOpacity(3).setBlockName("water").disableStats().setRequiresSelfNotify();

    /** Flowing lava block */
    public static final Block lavaMoving = (new BlockFlowing(10, Material.lava)).setHardness(0.0F).setLightValue(1.0F).setLightOpacity(255).setBlockName("lava").disableStats().setRequiresSelfNotify();

    /** Stationary lava source block */
    public static final Block lavaStill = (new BlockStationary(11, Material.lava)).setHardness(100.0F).setLightValue(1.0F).setLightOpacity(255).setBlockName("lava").disableStats().setRequiresSelfNotify();
    public static final Block sand = (new BlockSand(12, 18)).setHardness(0.5F).setStepSound(soundSandFootstep).setBlockName("sand");
    public static final Block gravel = (new BlockGravel(13, 19)).setHardness(0.6F).setStepSound(soundGravelFootstep).setBlockName("gravel");
    public static final Block oreGold = (new BlockOre(14, 32)).setHardness(3.0F).setResistance(5.0F).setStepSound(soundStoneFootstep).setBlockName("oreGold");
    public static final Block oreIron = (new BlockOre(15, 33)).setHardness(3.0F).setResistance(5.0F).setStepSound(soundStoneFootstep).setBlockName("oreIron");
    public static final Block oreCoal = (new BlockOre(16, 34)).setHardness(3.0F).setResistance(5.0F).setStepSound(soundStoneFootstep).setBlockName("oreCoal");
    public static final Block wood = (new BlockLog(17)).setHardness(2.0F).setStepSound(soundWoodFootstep).setBlockName("log").setRequiresSelfNotify();
    public static final BlockLeaves leaves = (BlockLeaves)(new BlockLeaves(18, 52)).setHardness(0.2F).setLightOpacity(1).setStepSound(soundGrassFootstep).setBlockName("leaves").setRequiresSelfNotify();
    public static final Block sponge = (new BlockSponge(19)).setHardness(0.6F).setStepSound(soundGrassFootstep).setBlockName("sponge");
    public static final Block glass = (new BlockGlass(20, 49, Material.glass, false)).setHardness(0.3F).setStepSound(soundGlassFootstep).setBlockName("glass");
    public static final Block oreLapis = (new BlockOre(21, 160)).setHardness(3.0F).setResistance(5.0F).setStepSound(soundStoneFootstep).setBlockName("oreLapis");
    public static final Block blockLapis = (new Block(22, 144, Material.rock)).setHardness(3.0F).setResistance(5.0F).setStepSound(soundStoneFootstep).setBlockName("blockLapis");
    public static final Block dispenser = (new BlockDispenser(23)).setHardness(3.5F).setStepSound(soundStoneFootstep).setBlockName("dispenser").setRequiresSelfNotify();
    public static final Block sandStone = (new BlockSandStone(24)).setStepSound(soundStoneFootstep).setHardness(0.8F).setBlockName("sandStone");
    public static final Block music = (new BlockNote(25)).setHardness(0.8F).setBlockName("musicBlock").setRequiresSelfNotify();

    /** Is the block that build the bed in the world. */
    public static final Block bed = (new BlockBed(26)).setHardness(0.2F).setBlockName("bed").disableStats().setRequiresSelfNotify();

    /** Powered rail track. */
    public static final Block railPowered = (new BlockRail(27, 179, true)).setHardness(0.7F).setStepSound(soundMetalFootstep).setBlockName("goldenRail").setRequiresSelfNotify();

    /** Minecart detector rail track. */
    public static final Block railDetector = (new BlockDetectorRail(28, 195)).setHardness(0.7F).setStepSound(soundMetalFootstep).setBlockName("detectorRail").setRequiresSelfNotify();

    /** Is the sticky piston block. */
    public static final Block pistonStickyBase = (new BlockPistonBase(29, 106, true)).setBlockName("pistonStickyBase").setRequiresSelfNotify();

    /** Slows entities on collision. */
    public static final Block web = (new BlockWeb(30, 11)).setLightOpacity(1).setHardness(4.0F).setBlockName("web");

    /** BlockTallGrass */
    public static final BlockTallGrass tallGrass = (BlockTallGrass)(new BlockTallGrass(31, 39)).setHardness(0.0F).setStepSound(soundGrassFootstep).setBlockName("tallgrass");

    /** BlockDeadBush */
    public static final BlockDeadBush deadBush = (BlockDeadBush)(new BlockDeadBush(32, 55)).setHardness(0.0F).setStepSound(soundGrassFootstep).setBlockName("deadbush");

    /** Is the normal piston block. */
    public static final Block pistonBase = (new BlockPistonBase(33, 107, false)).setBlockName("pistonBase").setRequiresSelfNotify();
    public static final BlockPistonExtension pistonExtension = (BlockPistonExtension)(new BlockPistonExtension(34, 107)).setRequiresSelfNotify();
    public static final Block cloth = (new BlockCloth()).setHardness(0.8F).setStepSound(soundClothFootstep).setBlockName("cloth").setRequiresSelfNotify();
    public static final BlockPistonMoving pistonMoving = new BlockPistonMoving(36);

    /** Was dandelion */
    public static final BlockFlower plantYellow = (BlockFlower)(new BlockFlower(37, 13)).setHardness(0.0F).setStepSound(soundGrassFootstep).setBlockName("flower");

    /** Was rose */
    public static final BlockFlower plantRed = (BlockFlower)(new BlockFlower(38, 12)).setHardness(0.0F).setStepSound(soundGrassFootstep).setBlockName("rose");

    /** Was mushroomKingBolete */
    public static final BlockFlower mushroomBrown = (BlockFlower)(new BlockMushroom(39, 29)).setHardness(0.0F).setStepSound(soundGrassFootstep).setLightValue(0.125F).setBlockName("mushroom");

    /** Was mushroomFlyAgaric */
    public static final BlockFlower mushroomRed = (BlockFlower)(new BlockMushroom(40, 28)).setHardness(0.0F).setStepSound(soundGrassFootstep).setBlockName("mushroom");
    public static final Block blockGold = (new BlockOreStorage(41, 23)).setHardness(3.0F).setResistance(10.0F).setStepSound(soundMetalFootstep).setBlockName("blockGold");
    public static final Block blockSteel = (new BlockOreStorage(42, 22)).setHardness(5.0F).setResistance(10.0F).setStepSound(soundMetalFootstep).setBlockName("blockIron");
    public static final Block stairDouble = (new BlockStep(43, true)).setHardness(2.0F).setResistance(10.0F).setStepSound(soundStoneFootstep).setBlockName("stoneSlab");
    public static final Block stairSingle = (new BlockStep(44, false)).setHardness(2.0F).setResistance(10.0F).setStepSound(soundStoneFootstep).setBlockName("stoneSlab");
    public static final Block brick = (new Block(45, 7, Material.rock)).setHardness(2.0F).setResistance(10.0F).setStepSound(soundStoneFootstep).setBlockName("brick");
    public static final Block tnt = (new BlockTNT(46, 8)).setHardness(0.0F).setStepSound(soundGrassFootstep).setBlockName("tnt");
    public static final Block bookShelf = (new BlockBookshelf(47, 35)).setHardness(1.5F).setStepSound(soundWoodFootstep).setBlockName("bookshelf");
    public static final Block cobblestoneMossy = (new Block(48, 36, Material.rock)).setHardness(2.0F).setResistance(10.0F).setStepSound(soundStoneFootstep).setBlockName("stoneMoss");
    public static final Block obsidian = (new BlockObsidian(49, 37)).setHardness(50.0F).setResistance(2000.0F).setStepSound(soundStoneFootstep).setBlockName("obsidian");
    public static final Block torchWood = (new BlockTorch(50, 80)).setHardness(0.0F).setLightValue(0.9375F).setStepSound(soundWoodFootstep).setBlockName("torch").setRequiresSelfNotify();
    public static final BlockFire fire = (BlockFire)(new BlockFire(51, 31)).setHardness(0.0F).setLightValue(1.0F).setStepSound(soundWoodFootstep).setBlockName("fire").disableStats();
    public static final Block mobSpawner = (new BlockMobSpawner(52, 65)).setHardness(5.0F).setStepSound(soundMetalFootstep).setBlockName("mobSpawner").disableStats();
    public static final Block stairCompactPlanks = (new BlockStairs(53, planks)).setBlockName("stairsWood").setRequiresSelfNotify();
    public static final Block chest = (new BlockChest(54)).setHardness(2.5F).setStepSound(soundWoodFootstep).setBlockName("chest").setRequiresSelfNotify();
    public static final Block redstoneWire = (new BlockRedstoneWire(55, 164)).setHardness(0.0F).setStepSound(soundPowderFootstep).setBlockName("redstoneDust").disableStats().setRequiresSelfNotify();
    public static final Block oreDiamond = (new BlockOre(56, 50)).setHardness(3.0F).setResistance(5.0F).setStepSound(soundStoneFootstep).setBlockName("oreDiamond");
    public static final Block blockDiamond = (new BlockOreStorage(57, 24)).setHardness(5.0F).setResistance(10.0F).setStepSound(soundMetalFootstep).setBlockName("blockDiamond");
    public static final Block workbench = (new BlockWorkbench(58)).setHardness(2.5F).setStepSound(soundWoodFootstep).setBlockName("workbench");
    public static final Block crops = (new BlockCrops(59, 88)).setHardness(0.0F).setStepSound(soundGrassFootstep).setBlockName("crops").disableStats().setRequiresSelfNotify();
    public static final Block tilledField = (new BlockFarmland(60)).setHardness(0.6F).setStepSound(soundGravelFootstep).setBlockName("farmland").setRequiresSelfNotify();
    public static final Block stoneOvenIdle = (new BlockFurnace(61, false)).setHardness(3.5F).setStepSound(soundStoneFootstep).setBlockName("furnace").setRequiresSelfNotify();
    public static final Block stoneOvenActive = (new BlockFurnace(62, true)).setHardness(3.5F).setStepSound(soundStoneFootstep).setLightValue(0.875F).setBlockName("furnace").setRequiresSelfNotify();
    public static final Block signPost = (new BlockSign(63, TileEntitySign.class, true)).setHardness(1.0F).setStepSound(soundWoodFootstep).setBlockName("sign").disableStats().setRequiresSelfNotify();
    public static final Block doorWood = (new BlockDoor(64, Material.wood)).setHardness(3.0F).setStepSound(soundWoodFootstep).setBlockName("doorWood").disableStats().setRequiresSelfNotify();
    public static final Block ladder = (new BlockLadder(65, 83)).setHardness(0.4F).setStepSound(soundWoodFootstep).setBlockName("ladder").setRequiresSelfNotify();
    public static final Block rail = (new BlockRail(66, 128, false)).setHardness(0.7F).setStepSound(soundMetalFootstep).setBlockName("rail").setRequiresSelfNotify();
    public static final Block stairCompactCobblestone = (new BlockStairs(67, cobblestone)).setBlockName("stairsStone").setRequiresSelfNotify();
    public static final Block signWall = (new BlockSign(68, TileEntitySign.class, false)).setHardness(1.0F).setStepSound(soundWoodFootstep).setBlockName("sign").disableStats().setRequiresSelfNotify();
    public static final Block lever = (new BlockLever(69, 96)).setHardness(0.5F).setStepSound(soundWoodFootstep).setBlockName("lever").setRequiresSelfNotify();
    public static final Block pressurePlateStone = (new BlockPressurePlate(70, stone.blockIndexInTexture, EnumMobType.mobs, Material.rock)).setHardness(0.5F).setStepSound(soundStoneFootstep).setBlockName("pressurePlate").setRequiresSelfNotify();
    public static final Block doorSteel = (new BlockDoor(71, Material.iron)).setHardness(5.0F).setStepSound(soundMetalFootstep).setBlockName("doorIron").disableStats().setRequiresSelfNotify();
    public static final Block pressurePlatePlanks = (new BlockPressurePlate(72, planks.blockIndexInTexture, EnumMobType.everything, Material.wood)).setHardness(0.5F).setStepSound(soundWoodFootstep).setBlockName("pressurePlate").setRequiresSelfNotify();
    public static final Block oreRedstone = (new BlockRedstoneOre(73, 51, false)).setHardness(3.0F).setResistance(5.0F).setStepSound(soundStoneFootstep).setBlockName("oreRedstone").setRequiresSelfNotify();
    public static final Block oreRedstoneGlowing = (new BlockRedstoneOre(74, 51, true)).setLightValue(0.625F).setHardness(3.0F).setResistance(5.0F).setStepSound(soundStoneFootstep).setBlockName("oreRedstone").setRequiresSelfNotify();
    public static final Block torchRedstoneIdle = (new BlockRedstoneTorch(75, 115, false)).setHardness(0.0F).setStepSound(soundWoodFootstep).setBlockName("notGate").setRequiresSelfNotify();
    public static final Block torchRedstoneActive = (new BlockRedstoneTorch(76, 99, true)).setHardness(0.0F).setLightValue(0.5F).setStepSound(soundWoodFootstep).setBlockName("notGate").setRequiresSelfNotify();
    public static final Block button = (new BlockButton(77, stone.blockIndexInTexture)).setHardness(0.5F).setStepSound(soundStoneFootstep).setBlockName("button").setRequiresSelfNotify();
    public static final Block snow = (new BlockSnow(78, 66)).setHardness(0.1F).setStepSound(soundClothFootstep).setBlockName("snow").setLightOpacity(0);
    public static final Block ice = (new BlockIce(79, 67)).setHardness(0.5F).setLightOpacity(3).setStepSound(soundGlassFootstep).setBlockName("ice");
    public static final Block blockSnow = (new BlockSnowBlock(80, 66)).setHardness(0.2F).setStepSound(soundClothFootstep).setBlockName("snow");
    public static final Block cactus = (new BlockCactus(81, 70)).setHardness(0.4F).setStepSound(soundClothFootstep).setBlockName("cactus");
    public static final Block blockClay = (new BlockClay(82, 72)).setHardness(0.6F).setStepSound(soundGravelFootstep).setBlockName("clay");
    public static final Block reed = (new BlockReed(83, 73)).setHardness(0.0F).setStepSound(soundGrassFootstep).setBlockName("reeds").disableStats();
    public static final Block jukebox = (new BlockJukeBox(84, 74)).setHardness(2.0F).setResistance(10.0F).setStepSound(soundStoneFootstep).setBlockName("jukebox").setRequiresSelfNotify();
    public static final Block fence = (new BlockFence(85, 4)).setHardness(2.0F).setResistance(5.0F).setStepSound(soundWoodFootstep).setBlockName("fence");

    /** A pumpkin */
    public static final Block pumpkin = (new BlockPumpkin(86, 102, false)).setHardness(1.0F).setStepSound(soundWoodFootstep).setBlockName("pumpkin").setRequiresSelfNotify();

    /** Red block, main part of the nether */
    public static final Block netherrack = (new BlockNetherrack(87, 103)).setHardness(0.4F).setStepSound(soundStoneFootstep).setBlockName("hellrock");
    public static final Block slowSand = (new BlockSoulSand(88, 104)).setHardness(0.5F).setStepSound(soundSandFootstep).setBlockName("hellsand");

    /** a brittle glass like substance found only in the nether */
    public static final Block glowStone = (new BlockGlowStone(89, 105, Material.glass)).setHardness(0.3F).setStepSound(soundGlassFootstep).setLightValue(1.0F).setBlockName("lightgem");

    /** The purple teleport blocks inside the obsidian circle */
    public static final BlockPortal portal = (BlockPortal)(new BlockPortal(90, 14)).setHardness(-1.0F).setStepSound(soundGlassFootstep).setLightValue(0.75F).setBlockName("portal");

    /** The Jack-O-Lantern */
    public static final Block pumpkinLantern = (new BlockPumpkin(91, 102, true)).setHardness(1.0F).setStepSound(soundWoodFootstep).setLightValue(1.0F).setBlockName("litpumpkin").setRequiresSelfNotify();
    public static final Block cake = (new BlockCake(92, 121)).setHardness(0.5F).setStepSound(soundClothFootstep).setBlockName("cake").disableStats().setRequiresSelfNotify();

    /** Is the redstone repeater (diode) when isn't powered. */
    public static final Block redstoneRepeaterIdle = (new BlockRedstoneRepeater(93, false)).setHardness(0.0F).setStepSound(soundWoodFootstep).setBlockName("diode").disableStats().setRequiresSelfNotify();

    /** Is the redstone repeater (diode) when powered. */
    public static final Block redstoneRepeaterActive = (new BlockRedstoneRepeater(94, true)).setHardness(0.0F).setLightValue(0.625F).setStepSound(soundWoodFootstep).setBlockName("diode").disableStats().setRequiresSelfNotify();

    /**
     * Is the april fools secret locked chest, only spawn on new chunks on 1st April.
     */
    public static final Block lockedChest = (new BlockLockedChest(95)).setHardness(0.0F).setLightValue(1.0F).setStepSound(soundWoodFootstep).setBlockName("lockedchest").setTickRandomly(true).setRequiresSelfNotify();

    /** Trapdoor block. */
    public static final Block trapdoor = (new BlockTrapDoor(96, Material.wood)).setHardness(3.0F).setStepSound(soundWoodFootstep).setBlockName("trapdoor").disableStats().setRequiresSelfNotify();
    public static final Block silverfish = (new BlockSilverfish(97)).setHardness(0.75F);
    public static final Block stoneBrick = (new BlockStoneBrick(98)).setHardness(1.5F).setResistance(10.0F).setStepSound(soundStoneFootstep).setBlockName("stonebricksmooth");
    public static final Block mushroomCapBrown = (new BlockMushroomCap(99, Material.wood, 142, 0)).setHardness(0.2F).setStepSound(soundWoodFootstep).setBlockName("mushroom").setRequiresSelfNotify();
    public static final Block mushroomCapRed = (new BlockMushroomCap(100, Material.wood, 142, 1)).setHardness(0.2F).setStepSound(soundWoodFootstep).setBlockName("mushroom").setRequiresSelfNotify();
    public static final Block fenceIron = (new BlockPane(101, 85, 85, Material.iron, true)).setHardness(5.0F).setResistance(10.0F).setStepSound(soundMetalFootstep).setBlockName("fenceIron");
    public static final Block thinGlass = (new BlockPane(102, 49, 148, Material.glass, false)).setHardness(0.3F).setStepSound(soundGlassFootstep).setBlockName("thinGlass");
    public static final Block melon = (new BlockMelon(103)).setHardness(1.0F).setStepSound(soundWoodFootstep).setBlockName("melon");
    public static final Block pumpkinStem = (new BlockStem(104, pumpkin)).setHardness(0.0F).setStepSound(soundWoodFootstep).setBlockName("pumpkinStem").setRequiresSelfNotify();
    public static final Block melonStem = (new BlockStem(105, melon)).setHardness(0.0F).setStepSound(soundWoodFootstep).setBlockName("pumpkinStem").setRequiresSelfNotify();
    public static final Block vine = (new BlockVine(106)).setHardness(0.2F).setStepSound(soundGrassFootstep).setBlockName("vine").setRequiresSelfNotify();
    public static final Block fenceGate = (new BlockFenceGate(107, 4)).setHardness(2.0F).setResistance(5.0F).setStepSound(soundWoodFootstep).setBlockName("fenceGate").setRequiresSelfNotify();
    public static final Block stairsBrick = (new BlockStairs(108, brick)).setBlockName("stairsBrick").setRequiresSelfNotify();
    public static final Block stairsStoneBrickSmooth = (new BlockStairs(109, stoneBrick)).setBlockName("stairsStoneBrickSmooth").setRequiresSelfNotify();
    public static final BlockMycelium mycelium = (BlockMycelium)(new BlockMycelium(110)).setHardness(0.6F).setStepSound(soundGrassFootstep).setBlockName("mycel");
    public static final Block waterlily = (new BlockLilyPad(111, 76)).setHardness(0.0F).setStepSound(soundGrassFootstep).setBlockName("waterlily");
    public static final Block netherBrick = (new Block(112, 224, Material.rock)).setHardness(2.0F).setResistance(10.0F).setStepSound(soundStoneFootstep).setBlockName("netherBrick");

    /**
     * The Block ID that the generator is allowed to replace while generating the terrain.
     */
    public static final Block netherFence = (new BlockFence(113, 224, Material.rock)).setHardness(2.0F).setResistance(10.0F).setStepSound(soundStoneFootstep).setBlockName("netherFence");
    public static final Block stairsNetherBrick = (new BlockStairs(114, netherBrick)).setBlockName("stairsNetherBrick").setRequiresSelfNotify();
    public static final Block netherStalk = (new BlockNetherStalk(115)).setBlockName("netherStalk").setRequiresSelfNotify();
    public static final Block enchantmentTable = (new BlockEnchantmentTable(116)).setHardness(5.0F).setResistance(2000.0F).setBlockName("enchantmentTable");
    public static final Block brewingStand = (new BlockBrewingStand(117)).setHardness(0.5F).setLightValue(0.125F).setBlockName("brewingStand").setRequiresSelfNotify();
    public static final Block cauldron = (new BlockCauldron(118)).setHardness(2.0F).setBlockName("cauldron").setRequiresSelfNotify();
    public static final Block endPortal = (new BlockEndPortal(119, Material.portal)).setHardness(-1.0F).setResistance(6000000.0F);
    public static final Block endPortalFrame = (new BlockEndPortalFrame(120)).setStepSound(soundGlassFootstep).setLightValue(0.125F).setHardness(-1.0F).setBlockName("endPortalFrame").setRequiresSelfNotify().setResistance(6000000.0F);
    public static final Block whiteStone = (new Block(121, 175, Material.rock)).setHardness(3.0F).setResistance(15.0F).setStepSound(soundStoneFootstep).setBlockName("whiteStone");
    public static final Block dragonEgg = (new BlockDragonEgg(122, 167)).setHardness(3.0F).setResistance(15.0F).setStepSound(soundStoneFootstep).setLightValue(0.125F).setBlockName("dragonEgg");
    public static final Block field_48209_bL = (new BlockRedstoneLight(123, false)).setHardness(0.3F).setStepSound(soundGlassFootstep).setBlockName("redstoneLight");
    public static final Block field_48210_bM = (new BlockRedstoneLight(124, true)).setHardness(0.3F).setStepSound(soundGlassFootstep).setBlockName("redstoneLight");

    /**
     * The index of the texture to be displayed for this block. May vary based on graphics settings. Mostly seems to
     * come from terrain.png, and the index is 0-based (grass is 0).
     */
    public int blockIndexInTexture;

    /** ID of the block. */
    public final int blockID;

    /** Indicates how many hits it takes to break a block. */
    protected float blockHardness;

    /** Indicates the blocks resistance to explosions. */
    protected float blockResistance;

    /**
     * set to true when Block's constructor is called through the chain of super()'s. Note: Never used
     */
    protected boolean blockConstructorCalled;

    /**
     * If this field is true, the block is counted for statistics (mined or placed)
     */
    protected boolean enableStats;

    /**
     * Flags whether or not this block is of a type that needs random ticking. Ref-counted by ExtendedBlockStorage in
     * order to broadly cull a chunk from the random chunk update list for efficiency's sake.
     */
    protected boolean needsRandomTick;
    protected boolean field_48207_bU;

    /** minimum X for the block bounds (local coordinates) */
    public double minX;

    /** minimum Y for the block bounds (local coordinates) */
    public double minY;

    /** minimum Z for the block bounds (local coordinates) */
    public double minZ;

    /** maximum X for the block bounds (local coordinates) */
    public double maxX;

    /** maximum Y for the block bounds (local coordinates) */
    public double maxY;

    /** maximum Z for the block bounds (local coordinates) */
    public double maxZ;

    /** Sound of stepping on the block */
    public StepSound stepSound;
    public float blockParticleGravity;

    /** Block material definition. */
    public final Material blockMaterial;

    /**
     * Determines how much velocity is maintained while moving on top of this block
     */
    public float slipperiness;
    private String blockName;

    protected Block(int par1, Material par2Material)
    {
        this.blockConstructorCalled = true;
        this.enableStats = true;
        this.stepSound = soundPowderFootstep;
        this.blockParticleGravity = 1.0F;
        this.slipperiness = 0.6F;

        if (blocksList[par1] != null)
        {
            throw new IllegalArgumentException("Slot " + par1 + " is already occupied by " + blocksList[par1] + " when adding " + this);
        }
        else
        {
            this.blockMaterial = par2Material;
            blocksList[par1] = this;
            this.blockID = par1;
            this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
            opaqueCubeLookup[par1] = this.isOpaqueCube();
            lightOpacity[par1] = this.isOpaqueCube() ? 255 : 0;
            canBlockGrass[par1] = !par2Material.getCanBlockGrass();
        }
    }

    /**
     * Blocks with this attribute will not notify all near blocks when it's metadata change. The default behavior is
     * always notify every neightbor block when anything changes.
     */
    protected Block setRequiresSelfNotify()
    {
        requiresSelfNotify[this.blockID] = true;
        return this;
    }

    /**
     * This method is called on a block after all other blocks gets already created. You can use it to reference and
     * configure something on the block that needs the others ones.
     */
    protected void initializeBlock() {}

    protected Block(int par1, int par2, Material par3Material)
    {
        this(par1, par3Material);
        this.blockIndexInTexture = par2;
    }

    /**
     * Sets the footstep sound for the block. Returns the object for convenience in constructing.
     */
    protected Block setStepSound(StepSound par1StepSound)
    {
        this.stepSound = par1StepSound;
        return this;
    }

    /**
     * Sets how much light is blocked going through this block. Returns the object for convenience in constructing.
     */
    protected Block setLightOpacity(int par1)
    {
        lightOpacity[this.blockID] = par1;
        return this;
    }

    /**
     * Sets the amount of light emitted by a block from 0.0f to 1.0f (converts internally to 0-15). Returns the object
     * for convenience in constructing.
     */
    protected Block setLightValue(float par1)
    {
        lightValue[this.blockID] = (int)(15.0F * par1);
        return this;
    }

    /**
     * Sets the the blocks resistance to explosions. Returns the object for convenience in constructing.
     */
    protected Block setResistance(float par1)
    {
        this.blockResistance = par1 * 3.0F;
        return this;
    }

    public static boolean isNormalCube(int par0)
    {
        Block var1 = blocksList[par0];
        return var1 == null ? false : var1.blockMaterial.isOpaque() && var1.renderAsNormalBlock();
    }

    /**
     * If this block doesn't render as an ordinary block it will return False (examples: signs, buttons, stairs, etc)
     */
    public boolean renderAsNormalBlock()
    {
        return true;
    }

    public boolean getBlocksMovement(IBlockAccess par1IBlockAccess, int par2, int par3, int par4)
    {
        return !this.blockMaterial.blocksMovement();
    }

    /**
     * The type of render function that is called for this block
     */
    public int getRenderType()
    {
        return 0;
    }

    /**
     * Sets how many hits it takes to break a block.
     */
    protected Block setHardness(float par1)
    {
        this.blockHardness = par1;

        if (this.blockResistance < par1 * 5.0F)
        {
            this.blockResistance = par1 * 5.0F;
        }

        return this;
    }

    /**
     * This method will make the hardness of the block equals to -1, and the block is indestructible.
     */
    protected Block setBlockUnbreakable()
    {
        this.setHardness(-1.0F);
        return this;
    }

    /**
     * Returns the block hardness.
     */
    public float getHardness()
    {
        return this.blockHardness;
    }

    /**
     * Sets whether this block type will receive random update ticks
     */
    protected Block setTickRandomly(boolean par1)
    {
        this.needsRandomTick = par1;
        return this;
    }

    /**
     * Returns whether or not this block is of a type that needs random ticking. Called for ref-counting purposes by
     * ExtendedBlockStorage in order to broadly cull a chunk from the random chunk update list for efficiency's sake.
     */
    public boolean getTickRandomly()
    {
        return this.needsRandomTick;
    }

    public boolean func_48205_p()
    {
        return hasTileEntity(0);
    }

    /**
     * Sets the bounds of the block.  minX, minY, minZ, maxX, maxY, maxZ
     */
    public void setBlockBounds(float par1, float par2, float par3, float par4, float par5, float par6)
    {
        this.minX = (double)par1;
        this.minY = (double)par2;
        this.minZ = (double)par3;
        this.maxX = (double)par4;
        this.maxY = (double)par5;
        this.maxZ = (double)par6;
    }

    /**
     * How bright to render this block based on the light its receiving. Args: iBlockAccess, x, y, z
     */
    public float getBlockBrightness(IBlockAccess par1IBlockAccess, int par2, int par3, int par4)
    {
        return par1IBlockAccess.getBrightness(par2, par3, par4, getLightValue(par1IBlockAccess, par2, par3, par4));
    }

    /**
     * 'Goes straight to getLightBrightnessForSkyBlocks for Blocks, does some fancy computing for Fluids'
     */
    public int getMixedBrightnessForBlock(IBlockAccess par1IBlockAccess, int par2, int par3, int par4)
    {
        return par1IBlockAccess.getLightBrightnessForSkyBlocks(par2, par3, par4, getLightValue(par1IBlockAccess, par2, par3, par4));
    }

    /**
     * Returns true if the given side of this block type should be rendered, if the adjacent block is at the given
     * coordinates.  Args: blockAccess, x, y, z, side
     */
    public boolean shouldSideBeRendered(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5)
    {
        return par5 == 0 && this.minY > 0.0D ? true : (par5 == 1 && this.maxY < 1.0D ? true : (par5 == 2 && this.minZ > 0.0D ? true : (par5 == 3 && this.maxZ < 1.0D ? true : (par5 == 4 && this.minX > 0.0D ? true : (par5 == 5 && this.maxX < 1.0D ? true : !par1IBlockAccess.isBlockOpaqueCube(par2, par3, par4))))));
    }

    /**
     * Returns Returns true if the given side of this block type should be rendered (if it's solid or not), if the
     * adjacent block is at the given coordinates. Args: blockAccess, x, y, z, side
     */
    public boolean isBlockSolid(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5)
    {
        return par1IBlockAccess.getBlockMaterial(par2, par3, par4).isSolid();
    }

    /**
     * Retrieves the block texture to use based on the display side. Args: iBlockAccess, x, y, z, side
     */
    public int getBlockTexture(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5)
    {
        return this.getBlockTextureFromSideAndMetadata(par5, par1IBlockAccess.getBlockMetadata(par2, par3, par4));
    }

    /**
     * From the specified side and block metadata retrieves the blocks texture. Args: side, metadata
     */
    public int getBlockTextureFromSideAndMetadata(int par1, int par2)
    {
        return this.getBlockTextureFromSide(par1);
    }

    /**
     * Returns the block texture based on the side being looked at.  Args: side
     */
    public int getBlockTextureFromSide(int par1)
    {
        return this.blockIndexInTexture;
    }

    /**
     * Returns the bounding box of the wired rectangular prism to render.
     */
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World par1World, int par2, int par3, int par4)
    {
        return AxisAlignedBB.getBoundingBoxFromPool((double)par2 + this.minX, (double)par3 + this.minY, (double)par4 + this.minZ, (double)par2 + this.maxX, (double)par3 + this.maxY, (double)par4 + this.maxZ);
    }

    /**
     * Adds to the supplied array any colliding bounding boxes with the passed in bounding box. Args: world, x, y, z,
     * axisAlignedBB, arrayList
     */
    public void getCollidingBoundingBoxes(World par1World, int par2, int par3, int par4, AxisAlignedBB par5AxisAlignedBB, ArrayList par6ArrayList)
    {
        AxisAlignedBB var7 = this.getCollisionBoundingBoxFromPool(par1World, par2, par3, par4);

        if (var7 != null && par5AxisAlignedBB.intersectsWith(var7))
        {
            par6ArrayList.add(var7);
        }
    }

    /**
     * Returns a bounding box from the pool of bounding boxes (this means this box can change after the pool has been
     * cleared to be reused)
     */
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World par1World, int par2, int par3, int par4)
    {
        return AxisAlignedBB.getBoundingBoxFromPool((double)par2 + this.minX, (double)par3 + this.minY, (double)par4 + this.minZ, (double)par2 + this.maxX, (double)par3 + this.maxY, (double)par4 + this.maxZ);
    }

    /**
     * Is this block (a) opaque and (b) a full 1m cube?  This determines whether or not to render the shared face of two
     * adjacent blocks and also whether the player can attach torches, redstone wire, etc to this block.
     */
    public boolean isOpaqueCube()
    {
        return true;
    }

    /**
     * Returns whether this block is collideable based on the arguments passed in Args: blockMetaData, unknownFlag
     */
    public boolean canCollideCheck(int par1, boolean par2)
    {
        return this.isCollidable();
    }

    /**
     * Returns if this block is collidable (only used by Fire). Args: x, y, z
     */
    public boolean isCollidable()
    {
        return true;
    }

    /**
     * Ticks the block if it's been scheduled
     */
    public void updateTick(World par1World, int par2, int par3, int par4, Random par5Random) {}

    /**
     * A randomly called display update to be able to add particles or other items for display
     */
    public void randomDisplayTick(World par1World, int par2, int par3, int par4, Random par5Random) {}

    /**
     * Called right before the block is destroyed by a player.  Args: world, x, y, z, metaData
     */
    public void onBlockDestroyedByPlayer(World par1World, int par2, int par3, int par4, int par5) {}

    /**
     * Lets the block know when one of its neighbor changes. Doesn't know which neighbor changed (coordinates passed are
     * their own) Args: x, y, z, neighbor blockID
     */
    public void onNeighborBlockChange(World par1World, int par2, int par3, int par4, int par5) {}

    /**
     * How many world ticks before ticking
     */
    public int tickRate()
    {
        return 10;
    }

    /**
     * Called whenever the block is added into the world. Args: world, x, y, z
     */
    public void onBlockAdded(World par1World, int par2, int par3, int par4) {}

    /**
     * Called whenever the block is removed.
     */
    public void onBlockRemoval(World par1World, int par2, int par3, int par4) {}

    /**
     * Returns the quantity of items to drop on block destruction.
     */
    public int quantityDropped(Random par1Random)
    {
        return 1;
    }

    /**
     * Returns the ID of the items to drop on destruction.
     */
    public int idDropped(int par1, Random par2Random, int par3)
    {
        return this.blockID;
    }

    /**
     * Deprecated in favor of a metadata sensitive version
     * @param entityplayer Entity attacking the block
     * @return
     */
    @Deprecated
    public float blockStrength(EntityPlayer par1EntityPlayer)
    {
    	return blockStrength(par1EntityPlayer, 0);
    }

    /**
     * Drops the specified block items
     */
    public final void dropBlockAsItem(World par1World, int par2, int par3, int par4, int par5, int par6)
    {
        this.dropBlockAsItemWithChance(par1World, par2, par3, par4, par5, 1.0F, par6);
    }

    /**
     * Drops the block items with a specified chance of dropping the specified items
     */
    public void dropBlockAsItemWithChance(World par1World, int par2, int par3, int par4, int par5, float par6, int par7)
    {
        if (!par1World.isRemote)
        {
            int var8 = this.quantityDroppedWithBonus(par7, par1World.rand);

            ArrayList<ItemStack> items = getBlockDropped(par1World, par2, par3, par4, par5, par7);
            for (ItemStack item : items)
            {
                if (par1World.rand.nextFloat() <= par6)
                {
                    this.dropBlockAsItem_do(par1World, par2, par3, par4, item);
                }
            }
        }
    }

    /**
     * drops the block as an item. Does nothing if a multiplayer world?
     */
    protected void dropBlockAsItem_do(World par1World, int par2, int par3, int par4, ItemStack par5ItemStack)
    {
        if (!par1World.isRemote)
        {
            float var6 = 0.7F;
            double var7 = (double)(par1World.rand.nextFloat() * var6) + (double)(1.0F - var6) * 0.5D;
            double var9 = (double)(par1World.rand.nextFloat() * var6) + (double)(1.0F - var6) * 0.5D;
            double var11 = (double)(par1World.rand.nextFloat() * var6) + (double)(1.0F - var6) * 0.5D;
            EntityItem var13 = new EntityItem(par1World, (double)par2 + var7, (double)par3 + var9, (double)par4 + var11, par5ItemStack);
            var13.delayBeforeCanPickup = 10;
            par1World.spawnEntityInWorld(var13);
        }
    }

    /**
     * Determines the damage on the item the block drops. Used in cloth and wood.
     */
    protected int damageDropped(int par1)
    {
        return 0;
    }

    /**
     * Returns how much this block can resist explosions from the passed in entity.
     */
    public float getExplosionResistance(Entity par1Entity)
    {
        return this.blockResistance / 5.0F;
    }

    /**
     * Ray traces through the blocks collision from start vector to end vector returning a ray trace hit. Args: world,
     * x, y, z, startVec, endVec
     */
    public MovingObjectPosition collisionRayTrace(World par1World, int par2, int par3, int par4, Vec3D par5Vec3D, Vec3D par6Vec3D)
    {
        this.setBlockBoundsBasedOnState(par1World, par2, par3, par4);
        par5Vec3D = par5Vec3D.addVector((double)(-par2), (double)(-par3), (double)(-par4));
        par6Vec3D = par6Vec3D.addVector((double)(-par2), (double)(-par3), (double)(-par4));
        Vec3D var7 = par5Vec3D.getIntermediateWithXValue(par6Vec3D, this.minX);
        Vec3D var8 = par5Vec3D.getIntermediateWithXValue(par6Vec3D, this.maxX);
        Vec3D var9 = par5Vec3D.getIntermediateWithYValue(par6Vec3D, this.minY);
        Vec3D var10 = par5Vec3D.getIntermediateWithYValue(par6Vec3D, this.maxY);
        Vec3D var11 = par5Vec3D.getIntermediateWithZValue(par6Vec3D, this.minZ);
        Vec3D var12 = par5Vec3D.getIntermediateWithZValue(par6Vec3D, this.maxZ);

        if (!this.isVecInsideYZBounds(var7))
        {
            var7 = null;
        }

        if (!this.isVecInsideYZBounds(var8))
        {
            var8 = null;
        }

        if (!this.isVecInsideXZBounds(var9))
        {
            var9 = null;
        }

        if (!this.isVecInsideXZBounds(var10))
        {
            var10 = null;
        }

        if (!this.isVecInsideXYBounds(var11))
        {
            var11 = null;
        }

        if (!this.isVecInsideXYBounds(var12))
        {
            var12 = null;
        }

        Vec3D var13 = null;

        if (var7 != null && (var13 == null || par5Vec3D.distanceTo(var7) < par5Vec3D.distanceTo(var13)))
        {
            var13 = var7;
        }

        if (var8 != null && (var13 == null || par5Vec3D.distanceTo(var8) < par5Vec3D.distanceTo(var13)))
        {
            var13 = var8;
        }

        if (var9 != null && (var13 == null || par5Vec3D.distanceTo(var9) < par5Vec3D.distanceTo(var13)))
        {
            var13 = var9;
        }

        if (var10 != null && (var13 == null || par5Vec3D.distanceTo(var10) < par5Vec3D.distanceTo(var13)))
        {
            var13 = var10;
        }

        if (var11 != null && (var13 == null || par5Vec3D.distanceTo(var11) < par5Vec3D.distanceTo(var13)))
        {
            var13 = var11;
        }

        if (var12 != null && (var13 == null || par5Vec3D.distanceTo(var12) < par5Vec3D.distanceTo(var13)))
        {
            var13 = var12;
        }

        if (var13 == null)
        {
            return null;
        }
        else
        {
            byte var14 = -1;

            if (var13 == var7)
            {
                var14 = 4;
            }

            if (var13 == var8)
            {
                var14 = 5;
            }

            if (var13 == var9)
            {
                var14 = 0;
            }

            if (var13 == var10)
            {
                var14 = 1;
            }

            if (var13 == var11)
            {
                var14 = 2;
            }

            if (var13 == var12)
            {
                var14 = 3;
            }

            return new MovingObjectPosition(par2, par3, par4, var14, var13.addVector((double)par2, (double)par3, (double)par4));
        }
    }

    /**
     * Checks if a vector is within the Y and Z bounds of the block.
     */
    private boolean isVecInsideYZBounds(Vec3D par1Vec3D)
    {
        return par1Vec3D == null ? false : par1Vec3D.yCoord >= this.minY && par1Vec3D.yCoord <= this.maxY && par1Vec3D.zCoord >= this.minZ && par1Vec3D.zCoord <= this.maxZ;
    }

    /**
     * Checks if a vector is within the X and Z bounds of the block.
     */
    private boolean isVecInsideXZBounds(Vec3D par1Vec3D)
    {
        return par1Vec3D == null ? false : par1Vec3D.xCoord >= this.minX && par1Vec3D.xCoord <= this.maxX && par1Vec3D.zCoord >= this.minZ && par1Vec3D.zCoord <= this.maxZ;
    }

    /**
     * Checks if a vector is within the X and Y bounds of the block.
     */
    private boolean isVecInsideXYBounds(Vec3D par1Vec3D)
    {
        return par1Vec3D == null ? false : par1Vec3D.xCoord >= this.minX && par1Vec3D.xCoord <= this.maxX && par1Vec3D.yCoord >= this.minY && par1Vec3D.yCoord <= this.maxY;
    }

    /**
     * Called upon the block being destroyed by an explosion
     */
    public void onBlockDestroyedByExplosion(World par1World, int par2, int par3, int par4) {}

    /**
     * Returns which pass should this block be rendered on. 0 for solids and 1 for alpha
     */
    public int getRenderBlockPass()
    {
        return 0;
    }

    public boolean canPlaceBlockOnSide(World par1World, int par2, int par3, int par4, int par5)
    {
        return this.canPlaceBlockAt(par1World, par2, par3, par4);
    }

    /**
     * Checks to see if its valid to put this block at the specified coordinates. Args: world, x, y, z
     */
    public boolean canPlaceBlockAt(World par1World, int par2, int par3, int par4)
    {
        int var5 = par1World.getBlockId(par2, par3, par4);
        return var5 == 0 || blocksList[var5].blockMaterial.isGroundCover();
    }

    /**
     * Called upon block activation (left or right click on the block.). The three integers represent x,y,z of the
     * block.
     */
    public boolean blockActivated(World par1World, int par2, int par3, int par4, EntityPlayer par5EntityPlayer)
    {
        return false;
    }

    /**
     * Called whenever an entity is walking on top of this block. Args: world, x, y, z, entity
     */
    public void onEntityWalking(World par1World, int par2, int par3, int par4, Entity par5Entity) {}

    /**
     * Called when a block is placed using an item. Used often for taking the facing and figuring out how to position
     * the item. Args: x, y, z, facing
     */
    public void onBlockPlaced(World par1World, int par2, int par3, int par4, int par5) {}

    /**
     * Called when the block is clicked by a player. Args: x, y, z, entityPlayer
     */
    public void onBlockClicked(World par1World, int par2, int par3, int par4, EntityPlayer par5EntityPlayer) {}

    /**
     * Can add to the passed in vector for a movement vector to be applied to the entity. Args: x, y, z, entity, vec3d
     */
    public void velocityToAddToEntity(World par1World, int par2, int par3, int par4, Entity par5Entity, Vec3D par6Vec3D) {}

    /**
     * Updates the blocks bounds based on its current state. Args: world, x, y, z
     */
    public void setBlockBoundsBasedOnState(IBlockAccess par1IBlockAccess, int par2, int par3, int par4) {}

    public int getBlockColor()
    {
        return 16777215;
    }

    /**
     * Returns the color this block should be rendered. Used by leaves.
     */
    public int getRenderColor(int par1)
    {
        return 16777215;
    }

    /**
     * Returns a integer with hex for 0xrrggbb with this color multiplied against the blocks color. Note only called
     * when first determining what to render.
     */
    public int colorMultiplier(IBlockAccess par1IBlockAccess, int par2, int par3, int par4)
    {
        return 16777215;
    }

    /**
     * Is this block powering the block on the specified side
     */
    public boolean isPoweringTo(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5)
    {
        return false;
    }

    /**
     * Can this block provide power. Only wire currently seems to have this change based on its state.
     */
    public boolean canProvidePower()
    {
        return false;
    }

    /**
     * Triggered whenever an entity collides with this block (enters into the block). Args: world, x, y, z, entity
     */
    public void onEntityCollidedWithBlock(World par1World, int par2, int par3, int par4, Entity par5Entity) {}

    /**
     * Is this block indirectly powering the block on the specified side
     */
    public boolean isIndirectlyPoweringTo(World par1World, int par2, int par3, int par4, int par5)
    {
        return false;
    }

    /**
     * Sets the block's bounds for rendering it as an item
     */
    public void setBlockBoundsForItemRender() {}

    /**
     * Called when the player destroys a block with an item that can harvest it. (i, j, k) are the coordinates of the
     * block and l is the block's subtype/damage.
     */
    public void harvestBlock(World par1World, EntityPlayer par2EntityPlayer, int par3, int par4, int par5, int par6)
    {
        par2EntityPlayer.addStat(StatList.mineBlockStatArray[this.blockID], 1);
        par2EntityPlayer.addExhaustion(0.025F);

    	if (this.renderAsNormalBlock() && !hasTileEntity(par6) && EnchantmentHelper.getSilkTouchModifier(par2EntityPlayer.inventory))
        {
            ItemStack var8 = this.createStackedBlock(par6);

            if (var8 != null)
            {
                this.dropBlockAsItem_do(par1World, par3, par4, par5, var8);
            }
        }
        else
        {
            int var7 = EnchantmentHelper.getFortuneModifier(par2EntityPlayer.inventory);
            this.dropBlockAsItem(par1World, par3, par4, par5, par6, var7);
        }
    }

    /**
     * Returns an item stack containing a single instance of the current block type. 'i' is the block's subtype/damage
     * and is ignored for blocks which do not support subtypes. Blocks which cannot be harvested should return null.
     */
    protected ItemStack createStackedBlock(int par1)
    {
        int var2 = 0;

        if (this.blockID >= 0 && this.blockID < Item.itemsList.length && Item.itemsList[this.blockID].getHasSubtypes())
        {
            var2 = par1;
        }

        return new ItemStack(this.blockID, 1, var2);
    }

    /**
     * Returns the usual quantity dropped by the block plus a bonus of 1 to 'i' (inclusive).
     */
    public int quantityDroppedWithBonus(int par1, Random par2Random)
    {
        return this.quantityDropped(par2Random);
    }

    /**
     * Can this block stay at this position.  Similar to canPlaceBlockAt except gets checked often with plants.
     */
    public boolean canBlockStay(World par1World, int par2, int par3, int par4)
    {
        return true;
    }

    /**
     * Called when a block is placed by using an ItemStack from inventory and passed in who placed it. Args:
     * x,y,z,entityliving
     */
    public void onBlockPlacedBy(World par1World, int par2, int par3, int par4, EntityLiving par5EntityLiving) {}

    /**
     * set name of block from language file
     */
    public Block setBlockName(String par1Str)
    {
        this.blockName = "tile." + par1Str;
        return this;
    }

    public String translateBlockName()
    {
        return StatCollector.translateToLocal(this.getBlockName() + ".name");
    }

    public String getBlockName()
    {
        return this.blockName;
    }

    public void powerBlock(World par1World, int par2, int par3, int par4, int par5, int par6) {}

    /**
     * Return the state of blocks statistics flags - if the block is counted for mined and placed.
     */
    public boolean getEnableStats()
    {
        return this.enableStats;
    }

    /**
     * Disable statistics for the block, the block will no count for mined or placed.
     */
    protected Block disableStats()
    {
        this.enableStats = false;
        return this;
    }

    /**
     * Returns the mobility information of the block, 0 = free, 1 = can't push but can move over, 2 = total immobility
     * and stop pistons
     */
    public int getMobilityFlag()
    {
        return this.blockMaterial.getMaterialMobility();
    }

    /**
     * Returns the default ambient occlusion value based on block opacity
     */
    public float getAmbientOcclusionLightValue(IBlockAccess par1IBlockAccess, int par2, int par3, int par4)
    {
        return par1IBlockAccess.isBlockNormalCube(par2, par3, par4) ? 0.2F : 1.0F;
    }

    /**
     * Block's chance to react to an entity falling on it.
     */
    public void onFallenUpon(World par1World, int par2, int par3, int par4, Entity par5Entity, float par6) {}

    /* =================================================== FORGE START =====================================*/
    /**
     * Get a light value for this block, normal ranges are between 0 and 15
     * 
     * @param world The current world
     * @param x X Position
     * @param y Y position
     * @param z Z position
     * @return The light value
     */
    public int getLightValue(IBlockAccess world, int x, int y, int z) 
    {
	    return lightValue[blockID];
    }

    /**
     * Checks if a player or entity can use this block to 'climb' like a ladder.
     * 
     * @param world The current world
     * @param x X Position
     * @param y Y position
     * @param z Z position
     * @return True if the block should act like a ladder
     */
    public boolean isLadder(World world, int x, int y, int z) 
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
    public boolean isBlockNormalCube(World world, int x, int y, int z) 
    {
	    return blockMaterial.isOpaque() && renderAsNormalBlock();
    }

    /**
     * Checks if the block is a solid face on the given side, used by placement logic.
     * 
     * @param world The current world
     * @param x X Position
     * @param y Y position
     * @param z Z position
     * @param size The side to check
     * @return True if the block is solid on the specified side.
     */
    public boolean isBlockSolidOnSide(World world, int x, int y, int z, int side) 
    {
	    return isBlockNormalCube(world, x, y, z);
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
    public boolean isBlockReplaceable(World world, int x, int y, int z) 
    {
	    return false;
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
    public boolean isBlockBurning(World world, int x, int y, int z) 
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
    public boolean isAirBlock(World world, int x, int y, int z) 
    {
	    return false;
    }

    /**
     * Metadata sensitive version of the default getHardness function.
     * 
     * @param meta The block's current metatdata
     * @return Block hardness
     */
    public float getHardness(int meta) 
    {
	    return blockHardness;
    }

    /**
     * Determines this block's strength against being destroyed by a player.
     * 
     * @param world The current world
     * @param player The player damaging the block, may be null
     * @param x X Position
     * @param y Y position
     * @param z Z position
     * @return A value 
     */
    public float blockStrength(World world, EntityPlayer player, int x, int y, int z) 
    {
	    return blockStrength(player, world.getBlockMetadata(x, y, z));
    }

    /**
     * Determines this block's strength against being destroyed by a player.
     * 
     * @param player The player damaging the block, may be null
     * @param meta The block's current metadata
     * @return The larger the value, the faster it breaks.
     */
    public float blockStrength(EntityPlayer player, int meta) 
    {
	    return ForgeHooks.blockStrength(this, player, meta);
    }

    /**
     * Determines if the player can harvest this block, obtaining it's drops when the block is destroyed.
     * 
     * @param player The player damaging the block, may be null
     * @param meta The block's current metadata
     * @return True tospawn the drops
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
    public boolean removeBlockByPlayer(World world, EntityPlayer player, int x, int y, int z) 
    {
        return world.setBlockWithNotify(x, y, z, 0);
    }

    /**
     * Called when a new CreativeContainer is opened, populate the list 
     * with all of the items for this block you want a player in creative mode
     * to have access to.
     * 
     * @param itemList The list of items to display on the creative inventory.
     */
    public void addCreativeItems(ArrayList itemList)
    {    	
    }
    
    protected static int blockFireSpreadSpeed[] = new int[256];
    protected static int blockFlammability[] = new int[256];
    
    /**
     * Chance that fire will spread and consume this block.
     * 300 being a 100% chance, 0, being a 0% chance.
     * 
     * @param world The current world
     * @param x The blocks X position
     * @param y The blocks Y position
     * @param z The blocks Z position
     * @param metadata The blocks current metadata
     * @param face The face that the fire is coming from
     * @return A number ranging from 0 to 300 relating used to determine if the block will be consumed by fire
     */
    public int getFlammability(IBlockAccess world, int x, int y, int z, int metadata, int face)
    {
    	return blockFlammability[blockID];
    }
    
    /**
     * Called when fire is updating, checks if a block face can catch fire.
     * 
     * 
     * @param world The current world
     * @param x The blocks X position
     * @param y The blocks Y position
     * @param z The blocks Z position
     * @param metadata The blocks current metadata
     * @param face The face that the fire is coming from
     * @return True if the face can be on fire, false otherwise.
     */
    public boolean isFlammable(IBlockAccess world, int x, int y, int z, int metadata, int face)
    {
    	return getFlammability(world, x, y, z, metadata, face) > 0;
    }
    
    /**
     * Called when fire is updating on a neighbor block.
     * The higher the number returned, the faster fire will spread around this block.
     * 
     * @param world The current world
     * @param x The blocks X position
     * @param y The blocks Y position
     * @param z The blocks Z position
     * @param metadata The blocks current metadata
     * @param face The face that the fire is coming from
     * @return A number that is used to determine the speed of fire growth around the block
     */
    public int getFireSpreadSpeed(World world, int x, int y, int z, int metadata, int face)
    {
    	return blockFireSpreadSpeed[blockID];
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
     * @param face The face that the fire is coming from
     * @return 
     */
    public boolean isFireSource(World world, int x, int y, int z, int metadata, int face)
    {
    	if (blockID == Block.netherrack.blockID && face == 0)
    	{
    		return true;
    	}
    	if ((world.worldProvider instanceof WorldProviderEnd) && blockID == Block.bedrock.blockID && face == 0)
    	{
    		return true;
    	}
    	return false;
    }

    /**
     * Called by BlockFire to setup the burn values of vanilla blocks.
     * @param id The block id
     * @param encouragement How much the block encourages fire to spread
     * @param flammability how easy a block is to catch fire
     */
    public static void setBurnProperties(int id, int encouragement, int flammability)
    {
    	blockFireSpreadSpeed[id] = encouragement;
    	blockFlammability[id] = flammability;
    }

    /**
     * Called throughout the code as a replacement for block instanceof BlockContainer
     * Moving this to the Block base class allows for mods that wish to extend vinella 
     * blocks, and also want to have a tile entity on that block, may.
     * 
     * Return true from this function to specify this block has a tile entity.
     * 
     * @param metadata Metadata of the current block
     * @return True if block has a tile entity, false otherwise
     */
    public boolean hasTileEntity(int metadata)
    {
    	return field_48207_bU;
    }
    
    /**
     * Called throughout the code as a replacement for BlockContainer.getBlockEntity
     * Return the same thing you would from that function.
     * This will fall back to BlockContainer.getBlockEntity if this block is a BlockContainer.
     * 
     * @param metadata The Metadata of the current block
     * @return A instance of a class extending TileEntity
     */
    public TileEntity getTileEntity(int metadata)
    {
    	if (this instanceof BlockContainer)
    	{
    		return ((BlockContainer)this).getBlockEntity(metadata);
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
    	return quantityDroppedWithBonus(fortune, random);
    }
    
    /**
     * This returns a complete list of items dropped from this block.
     * 
     * @param world The current world
     * @param x X Position
     * @param Y Y Position
     * @param Z Z Position
     * @param metadata Current metadata
     * @param fortune Breakers fortune level
     * @return A ArrayList containing all items this block drops
     */
    public ArrayList<ItemStack> getBlockDropped(World world, int x, int y, int z, int metadata, int fortune)
    {
    	ArrayList<ItemStack> ret = new ArrayList<ItemStack>();
    	
    	int count = quantityDropped(metadata, fortune, world.rand);
    	for(int i = 0; i < count; i++)
    	{
    		int id = idDropped(metadata, world.rand, 0);
    		if (id > 0)
    		{
    			ret.add(new ItemStack(id, 1, damageDropped(metadata)));
    		}
    	}
    	return ret;
    }
    
    static
    {
        Item.itemsList[cloth.blockID] = (new ItemCloth(cloth.blockID - 256)).setItemName("cloth");
        Item.itemsList[wood.blockID] = (new ItemMetadata(wood.blockID - 256, wood)).setItemName("log");
        Item.itemsList[stoneBrick.blockID] = (new ItemMetadata(stoneBrick.blockID - 256, stoneBrick)).setItemName("stonebricksmooth");
        Item.itemsList[stairSingle.blockID] = (new ItemSlab(stairSingle.blockID - 256)).setItemName("stoneSlab");
        Item.itemsList[sapling.blockID] = (new ItemSapling(sapling.blockID - 256)).setItemName("sapling");
        Item.itemsList[leaves.blockID] = (new ItemLeaves(leaves.blockID - 256)).setItemName("leaves");
        Item.itemsList[vine.blockID] = new ItemColored(vine.blockID - 256, false);
        Item.itemsList[tallGrass.blockID] = (new ItemColored(tallGrass.blockID - 256, true)).setBlockNames(new String[] {"shrub", "grass", "fern"});
        Item.itemsList[waterlily.blockID] = new ItemLilyPad(waterlily.blockID - 256);
        Item.itemsList[pistonBase.blockID] = new ItemPiston(pistonBase.blockID - 256);
        Item.itemsList[pistonStickyBase.blockID] = new ItemPiston(pistonStickyBase.blockID - 256);

        for (int var0 = 0; var0 < 256; ++var0)
        {
            if (blocksList[var0] != null)
            {
                if (Item.itemsList[var0] == null)
                {
                    Item.itemsList[var0] = new ItemBlock(var0 - 256);
                    blocksList[var0].initializeBlock();
                }

                boolean var1 = false;

                if (var0 > 0 && blocksList[var0].getRenderType() == 10)
                {
                    var1 = true;
                }

                if (var0 > 0 && blocksList[var0] instanceof BlockStep)
                {
                    var1 = true;
                }

                if (var0 == tilledField.blockID)
                {
                    var1 = true;
                }

                if (canBlockGrass[var0])
                {
                    var1 = true;
                }

                useNeighborBrightness[var0] = var1;
            }
        }

        canBlockGrass[0] = true;
        StatList.initBreakableStats();
    }
}
