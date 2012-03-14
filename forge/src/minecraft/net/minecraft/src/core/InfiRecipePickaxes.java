package net.minecraft.src.core;

import net.minecraft.src.forge.MinecraftForge;
import net.minecraft.src.*;

public class InfiRecipePickaxes
{
    public InfiRecipePickaxes()
    {
    }

    public static int recipeStorm()
    {
        mod_InfiTools.stWoodPickaxe.setIconCoord(1, 0);
        ModLoader.addName(mod_InfiTools.stWoodPickaxe, "Stony Wooden Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.stWoodPickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Block.planks, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_InfiTools.saWoodPickaxe.setIconCoord(2, 0);
        ModLoader.addName(mod_InfiTools.saWoodPickaxe, "Sandy Wooden Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.saWoodPickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Block.planks, Character.valueOf('|'), mod_InfiTools.sandstoneRod
                });
        mod_InfiTools.bWoodPickaxe.setIconCoord(3, 0);
        ModLoader.addName(mod_InfiTools.bWoodPickaxe, "Necrotic Wooden Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bWoodPickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Block.planks, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bWoodPickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Block.planks, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_InfiTools.pWoodPickaxe.setIconCoord(4, 0);
        ModLoader.addName(mod_InfiTools.pWoodPickaxe, "Fibery Wooden Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.pWoodPickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Block.planks, Character.valueOf('|'), mod_InfiTools.paperRod
                });
        mod_InfiTools.nWoodPickaxe.setIconCoord(5, 0);
        ModLoader.addName(mod_InfiTools.nWoodPickaxe, "Bloody Wooden Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.nWoodPickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Block.planks, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_InfiTools.sWoodPickaxe.setIconCoord(6, 0);
        ModLoader.addName(mod_InfiTools.sWoodPickaxe, "Slimy Wooden Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.sWoodPickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Block.planks, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_InfiTools.cWoodPickaxe.setIconCoord(7, 0);
        ModLoader.addName(mod_InfiTools.cWoodPickaxe, "Spiny Wooden Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.cWoodPickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Block.planks, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_InfiTools.fWoodPickaxe.setIconCoord(8, 0);
        ModLoader.addName(mod_InfiTools.fWoodPickaxe, "Flaky Wooden Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.fWoodPickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Block.planks, Character.valueOf('|'), mod_InfiTools.flintRod
                });
        mod_InfiTools.brWoodPickaxe.setIconCoord(9, 0);
        ModLoader.addName(mod_InfiTools.brWoodPickaxe, "Baked Wooden Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.brWoodPickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Block.planks, Character.valueOf('|'), mod_InfiTools.brickRod
                });
        ModLoader.addRecipe(new ItemStack(Item.pickaxeStone, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Block.stone, Character.valueOf('|'), Item.stick
                });
        mod_InfiTools.stStonePickaxe.setIconCoord(1, 1);
        ModLoader.addName(mod_InfiTools.stStonePickaxe, "Heavy Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.stStonePickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.stStonePickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Block.stone, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_InfiTools.saStonePickaxe.setIconCoord(2, 1);
        ModLoader.addName(mod_InfiTools.saStonePickaxe, "Sandy Stone Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.saStonePickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), mod_InfiTools.sandstoneRod
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.saStonePickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Block.stone, Character.valueOf('|'), mod_InfiTools.sandstoneRod
                });
        mod_InfiTools.bStonePickaxe.setIconCoord(3, 1);
        ModLoader.addName(mod_InfiTools.bStonePickaxe, "Necrotic Stone Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bStonePickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bStonePickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Block.stone, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bStonePickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bStonePickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Block.stone, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_InfiTools.pStonePickaxe.setIconCoord(4, 1);
        ModLoader.addName(mod_InfiTools.pStonePickaxe, "Fibery Stone Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.pStonePickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), mod_InfiTools.paperRod
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.pStonePickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Block.stone, Character.valueOf('|'), mod_InfiTools.paperRod
                });
        mod_InfiTools.mStonePickaxe.setIconCoord(5, 1);
        ModLoader.addName(mod_InfiTools.mStonePickaxe, "Mossy Stone Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.mStonePickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), mod_InfiTools.mossyRod
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.mStonePickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Block.stone, Character.valueOf('|'), mod_InfiTools.mossyRod
                });
        mod_InfiTools.nStonePickaxe.setIconCoord(6, 1);
        ModLoader.addName(mod_InfiTools.nStonePickaxe, "Bloody Stone Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.nStonePickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.nStonePickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Block.stone, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_InfiTools.iceStonePickaxe.setIconCoord(7, 1);
        ModLoader.addName(mod_InfiTools.iceStonePickaxe, "Icy Stone Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.iceStonePickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), mod_InfiTools.iceRod
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.iceStonePickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Block.stone, Character.valueOf('|'), mod_InfiTools.iceRod
                });
        mod_InfiTools.sStonePickaxe.setIconCoord(8, 1);
        ModLoader.addName(mod_InfiTools.sStonePickaxe, "Slimy Stone Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.sStonePickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.sStonePickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Block.stone, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_InfiTools.cStonePickaxe.setIconCoord(9, 1);
        ModLoader.addName(mod_InfiTools.cStonePickaxe, "Spiny Stone Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.cStonePickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.cStonePickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Block.stone, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_InfiTools.fStonePickaxe.setIconCoord(1, 1);
        ModLoader.addName(mod_InfiTools.fStonePickaxe, "Flaky Stone Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.fStonePickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), mod_InfiTools.flintRod
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.fStonePickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Block.stone, Character.valueOf('|'), mod_InfiTools.flintRod
                });
        mod_InfiTools.brStonePickaxe.setIconCoord(1, 1);
        ModLoader.addName(mod_InfiTools.brStonePickaxe, "Baked Stone Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.brStonePickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), mod_InfiTools.brickRod
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.brStonePickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Block.stone, Character.valueOf('|'), mod_InfiTools.brickRod
                });
        mod_InfiTools.stIronPickaxe.setIconCoord(1, 2);
        ModLoader.addName(mod_InfiTools.stIronPickaxe, "Stony Iron Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.stIronPickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_InfiTools.iIronPickaxe.setIconCoord(2, 2);
        ModLoader.addName(mod_InfiTools.iIronPickaxe, "Ironic Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.iIronPickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), mod_InfiTools.ironRod
                });
        mod_InfiTools.dIronPickaxe.setIconCoord(3, 2);
        ModLoader.addName(mod_InfiTools.dIronPickaxe, "Jeweled Iron Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.dIronPickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), mod_InfiTools.diamondRod
                });
        mod_InfiTools.gIronPickaxe.setIconCoord(4, 2);
        ModLoader.addName(mod_InfiTools.gIronPickaxe, "Expensive Iron Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.gIronPickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), mod_InfiTools.goldRod
                });
        mod_InfiTools.rIronPickaxe.setIconCoord(5, 2);
        ModLoader.addName(mod_InfiTools.rIronPickaxe, "Red Iron Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.rIronPickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), mod_InfiTools.redstoneRod
                });
        mod_InfiTools.oIronPickaxe.setIconCoord(6, 2);
        ModLoader.addName(mod_InfiTools.oIronPickaxe, "Glassy Iron Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.oIronPickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), mod_InfiTools.obsidianRod
                });
        mod_InfiTools.bIronPickaxe.setIconCoord(7, 2);
        ModLoader.addName(mod_InfiTools.bIronPickaxe, "Necrotic Iron Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bIronPickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bIronPickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_InfiTools.nIronPickaxe.setIconCoord(8, 2);
        ModLoader.addName(mod_InfiTools.nIronPickaxe, "Bloody Iron Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.nIronPickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_InfiTools.glIronPickaxe.setIconCoord(9, 2);
        ModLoader.addName(mod_InfiTools.glIronPickaxe, "Glowing Iron Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.glIronPickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_InfiTools.iceIronPickaxe.setIconCoord(10, 2);
        ModLoader.addName(mod_InfiTools.iceIronPickaxe, "Icy Iron Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.iceIronPickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), mod_InfiTools.iceRod
                });
        mod_InfiTools.sIronPickaxe.setIconCoord(11, 2);
        ModLoader.addName(mod_InfiTools.sIronPickaxe, "Slimy Iron Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.sIronPickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_InfiTools.blIronPickaxe.setIconCoord(12, 2);
        ModLoader.addName(mod_InfiTools.blIronPickaxe, "Blazing Iron Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.blIronPickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), Item.blazeRod
                });
        mod_InfiTools.stDiamondPickaxe.setIconCoord(1, 3);
        ModLoader.addName(mod_InfiTools.stDiamondPickaxe, "Stony Diamond Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.stDiamondPickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_InfiTools.iDiamondPickaxe.setIconCoord(2, 3);
        ModLoader.addName(mod_InfiTools.iDiamondPickaxe, "Hard Diamond Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.iDiamondPickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), mod_InfiTools.ironRod
                });
        mod_InfiTools.dDiamondPickaxe.setIconCoord(3, 3);
        ModLoader.addName(mod_InfiTools.dDiamondPickaxe, "Diamondium Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.dDiamondPickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), mod_InfiTools.diamondRod
                });
        mod_InfiTools.gDiamondPickaxe.setIconCoord(4, 3);
        ModLoader.addName(mod_InfiTools.gDiamondPickaxe, "Expensive Diamond Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.gDiamondPickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), mod_InfiTools.goldRod
                });
        mod_InfiTools.rDiamondPickaxe.setIconCoord(5, 3);
        ModLoader.addName(mod_InfiTools.rDiamondPickaxe, "Red Diamond Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.rDiamondPickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), mod_InfiTools.redstoneRod
                });
        mod_InfiTools.oDiamondPickaxe.setIconCoord(6, 3);
        ModLoader.addName(mod_InfiTools.oDiamondPickaxe, "Glassy Diamond Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.oDiamondPickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), mod_InfiTools.obsidianRod
                });
        mod_InfiTools.bDiamondPickaxe.setIconCoord(7, 3);
        ModLoader.addName(mod_InfiTools.bDiamondPickaxe, "Necrotic Diamond Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bDiamondPickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bDiamondPickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_InfiTools.mDiamondPickaxe.setIconCoord(8, 3);
        ModLoader.addName(mod_InfiTools.mDiamondPickaxe, "Mossy Diamond Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.mDiamondPickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), mod_InfiTools.mossyRod
                });
        mod_InfiTools.nDiamondPickaxe.setIconCoord(9, 3);
        ModLoader.addName(mod_InfiTools.nDiamondPickaxe, "Bloody Diamond Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.nDiamondPickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_InfiTools.glDiamondPickaxe.setIconCoord(10, 3);
        ModLoader.addName(mod_InfiTools.glDiamondPickaxe, "Glowing Diamond Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.glDiamondPickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_InfiTools.blDiamondPickaxe.setIconCoord(11, 3);
        ModLoader.addName(mod_InfiTools.blDiamondPickaxe, "Blazing Diamond Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.blDiamondPickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), Item.blazeRod
                });
        mod_InfiTools.stGoldPickaxe.setIconCoord(1, 4);
        ModLoader.addName(mod_InfiTools.stGoldPickaxe, "Stony Gold Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.stGoldPickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_InfiTools.gGoldPickaxe.setIconCoord(2, 4);
        ModLoader.addName(mod_InfiTools.gGoldPickaxe, "Expensive Useless Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.gGoldPickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), mod_InfiTools.goldRod
                });
        mod_InfiTools.oGoldPickaxe.setIconCoord(3, 4);
        ModLoader.addName(mod_InfiTools.oGoldPickaxe, "Glassy Gold Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.oGoldPickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), mod_InfiTools.obsidianRod
                });
        mod_InfiTools.saGoldPickaxe.setIconCoord(4, 4);
        ModLoader.addName(mod_InfiTools.saGoldPickaxe, "Sandy Gold Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.saGoldPickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), mod_InfiTools.sandstoneRod
                });
        mod_InfiTools.bGoldPickaxe.setIconCoord(5, 4);
        ModLoader.addName(mod_InfiTools.bGoldPickaxe, "Necrotic Gold Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bGoldPickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bGoldPickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_InfiTools.mGoldPickaxe.setIconCoord(6, 4);
        ModLoader.addName(mod_InfiTools.mGoldPickaxe, "Mossy Gold Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.mGoldPickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), mod_InfiTools.mossyRod
                });
        mod_InfiTools.nGoldPickaxe.setIconCoord(7, 4);
        ModLoader.addName(mod_InfiTools.nGoldPickaxe, "Bloody Gold Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.nGoldPickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_InfiTools.glGoldPickaxe.setIconCoord(8, 4);
        ModLoader.addName(mod_InfiTools.glGoldPickaxe, "Glowing Gold Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.glGoldPickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_InfiTools.iceGoldPickaxe.setIconCoord(9, 4);
        ModLoader.addName(mod_InfiTools.iceGoldPickaxe, "Icy Gold Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.iceGoldPickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), mod_InfiTools.iceRod
                });
        mod_InfiTools.sGoldPickaxe.setIconCoord(10, 4);
        ModLoader.addName(mod_InfiTools.sGoldPickaxe, "Slimy Gold Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.sGoldPickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_InfiTools.fGoldPickaxe.setIconCoord(11, 4);
        ModLoader.addName(mod_InfiTools.fGoldPickaxe, "Flaky Gold Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.fGoldPickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), mod_InfiTools.flintRod
                });
        mod_InfiTools.wRedstonePickaxe.setIconCoord(0, 5);
        ModLoader.addName(mod_InfiTools.wRedstonePickaxe, "Redstone Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.wRedstonePickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), Item.stick
                });
        mod_InfiTools.stRedstonePickaxe.setIconCoord(1, 5);
        ModLoader.addName(mod_InfiTools.stRedstonePickaxe, "Stony Redstone Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.stRedstonePickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_InfiTools.iRedstonePickaxe.setIconCoord(2, 5);
        ModLoader.addName(mod_InfiTools.iRedstonePickaxe, "Hard Redstone Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.iRedstonePickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), mod_InfiTools.ironRod
                });
        mod_InfiTools.dRedstonePickaxe.setIconCoord(3, 5);
        ModLoader.addName(mod_InfiTools.dRedstonePickaxe, "Jeweled Redstone Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.dRedstonePickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), mod_InfiTools.diamondRod
                });
        mod_InfiTools.rRedstonePickaxe.setIconCoord(4, 5);
        ModLoader.addName(mod_InfiTools.rRedstonePickaxe, "Redredred Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.rRedstonePickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), mod_InfiTools.redstoneRod
                });
        mod_InfiTools.oRedstonePickaxe.setIconCoord(5, 5);
        ModLoader.addName(mod_InfiTools.oRedstonePickaxe, "Glassy Redstone Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.oRedstonePickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), mod_InfiTools.obsidianRod
                });
        mod_InfiTools.bRedstonePickaxe.setIconCoord(6, 5);
        ModLoader.addName(mod_InfiTools.bRedstonePickaxe, "Necrotic Redstone Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bRedstonePickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bRedstonePickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_InfiTools.mRedstonePickaxe.setIconCoord(7, 5);
        ModLoader.addName(mod_InfiTools.mRedstonePickaxe, "Mossy Redstone Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.mRedstonePickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), mod_InfiTools.mossyRod
                });
        mod_InfiTools.glRedstonePickaxe.setIconCoord(8, 5);
        ModLoader.addName(mod_InfiTools.glRedstonePickaxe, "Glowing Redstone Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.glRedstonePickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_InfiTools.sRedstonePickaxe.setIconCoord(9, 5);
        ModLoader.addName(mod_InfiTools.sRedstonePickaxe, "Slimy Redstone Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.sRedstonePickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_InfiTools.blRedstonePickaxe.setIconCoord(10, 5);
        ModLoader.addName(mod_InfiTools.blRedstonePickaxe, "Blazing Redstone Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.blRedstonePickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), Item.blazeRod
                });
        mod_InfiTools.wObsidianPickaxe.setIconCoord(0, 6);
        ModLoader.addName(mod_InfiTools.wObsidianPickaxe, "Obsidian Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.wObsidianPickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), Item.stick
                });
        mod_InfiTools.stObsidianPickaxe.setIconCoord(1, 6);
        ModLoader.addName(mod_InfiTools.stObsidianPickaxe, "Stony Obsidian Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.stObsidianPickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_InfiTools.iObsidianPickaxe.setIconCoord(2, 6);
        ModLoader.addName(mod_InfiTools.iObsidianPickaxe, "Hard Obsidian Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.iObsidianPickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), mod_InfiTools.ironRod
                });
        mod_InfiTools.dObsidianPickaxe.setIconCoord(3, 6);
        ModLoader.addName(mod_InfiTools.dObsidianPickaxe, "Jeweled Obsidian Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.dObsidianPickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), mod_InfiTools.diamondRod
                });
        mod_InfiTools.gObsidianPickaxe.setIconCoord(4, 6);
        ModLoader.addName(mod_InfiTools.gObsidianPickaxe, "Expensive Obsidian Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.gObsidianPickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), mod_InfiTools.goldRod
                });
        mod_InfiTools.rObsidianPickaxe.setIconCoord(5, 6);
        ModLoader.addName(mod_InfiTools.rObsidianPickaxe, "Red Obsidian Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.rObsidianPickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), mod_InfiTools.redstoneRod
                });
        mod_InfiTools.oObsidianPickaxe.setIconCoord(6, 6);
        ModLoader.addName(mod_InfiTools.oObsidianPickaxe, "Wicked Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.oObsidianPickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), mod_InfiTools.obsidianRod
                });
        mod_InfiTools.bObsidianPickaxe.setIconCoord(7, 6);
        ModLoader.addName(mod_InfiTools.bObsidianPickaxe, "Necrotic Obsidian Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bObsidianPickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bObsidianPickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_InfiTools.nObsidianPickaxe.setIconCoord(8, 6);
        ModLoader.addName(mod_InfiTools.nObsidianPickaxe, "Bloody Obsidian Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.nObsidianPickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_InfiTools.glObsidianPickaxe.setIconCoord(9, 6);
        ModLoader.addName(mod_InfiTools.glObsidianPickaxe, "Glowing Obsidian Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.glObsidianPickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_InfiTools.sObsidianPickaxe.setIconCoord(10, 6);
        ModLoader.addName(mod_InfiTools.sObsidianPickaxe, "Slimy Obsidian Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.sObsidianPickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_InfiTools.fObsidianPickaxe.setIconCoord(11, 6);
        ModLoader.addName(mod_InfiTools.fObsidianPickaxe, "Flaky Obsidian Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.fObsidianPickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), mod_InfiTools.flintRod
                });
        mod_InfiTools.blObsidianPickaxe.setIconCoord(12, 6);
        ModLoader.addName(mod_InfiTools.blObsidianPickaxe, "Blazing Obsidian Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.blObsidianPickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), Item.blazeRod
                });
        mod_InfiTools.wSandstonePickaxe.setIconCoord(0, 7);
        ModLoader.addName(mod_InfiTools.wSandstonePickaxe, "Sandstone Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.wSandstonePickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), Item.stick
                });
        mod_InfiTools.stSandstonePickaxe.setIconCoord(1, 7);
        ModLoader.addName(mod_InfiTools.stSandstonePickaxe, "Stony Sandstone Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.stSandstonePickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_InfiTools.saSandstonePickaxe.setIconCoord(2, 7);
        ModLoader.addName(mod_InfiTools.saSandstonePickaxe, "Sandtick");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.saSandstonePickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), mod_InfiTools.sandstoneRod
                });
        mod_InfiTools.bSandstonePickaxe.setIconCoord(3, 7);
        ModLoader.addName(mod_InfiTools.bSandstonePickaxe, "Necrotic Sandstone Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bSandstonePickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bSandstonePickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_InfiTools.pSandstonePickaxe.setIconCoord(4, 7);
        ModLoader.addName(mod_InfiTools.pSandstonePickaxe, "Fibery Sandstone Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.pSandstonePickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), mod_InfiTools.paperRod
                });
        mod_InfiTools.nSandstonePickaxe.setIconCoord(5, 7);
        ModLoader.addName(mod_InfiTools.nSandstonePickaxe, "Bloody Sandstone Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.nSandstonePickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_InfiTools.iceSandstonePickaxe.setIconCoord(6, 7);
        ModLoader.addName(mod_InfiTools.iceSandstonePickaxe, "Icy Sandstone Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.iceSandstonePickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), mod_InfiTools.iceRod
                });
        mod_InfiTools.sSandstonePickaxe.setIconCoord(7, 7);
        ModLoader.addName(mod_InfiTools.sSandstonePickaxe, "Slimy Sandstone Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.sSandstonePickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_InfiTools.cSandstonePickaxe.setIconCoord(8, 7);
        ModLoader.addName(mod_InfiTools.cSandstonePickaxe, "Spiny Sandstone Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.cSandstonePickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_InfiTools.fSandstonePickaxe.setIconCoord(9, 7);
        ModLoader.addName(mod_InfiTools.fSandstonePickaxe, "Flaky Sandstone Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.fSandstonePickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), mod_InfiTools.flintRod
                });
        mod_InfiTools.brSandstonePickaxe.setIconCoord(10, 7);
        ModLoader.addName(mod_InfiTools.brSandstonePickaxe, "Baked Sandstone Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.brSandstonePickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), mod_InfiTools.brickRod
                });
        mod_InfiTools.wBonePickaxe.setIconCoord(0, 8);
        ModLoader.addName(mod_InfiTools.wBonePickaxe, "Bone Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.wBonePickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Item.bone, Character.valueOf('|'), Item.stick
                });
        mod_InfiTools.stBonePickaxe.setIconCoord(1, 8);
        ModLoader.addName(mod_InfiTools.stBonePickaxe, "Stony Bone Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.stBonePickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_InfiTools.iBonePickaxe.setIconCoord(2, 8);
        ModLoader.addName(mod_InfiTools.iBonePickaxe, "Hard Bone Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.iBonePickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.ironRod
                });
        mod_InfiTools.dBonePickaxe.setIconCoord(3, 8);
        ModLoader.addName(mod_InfiTools.dBonePickaxe, "Jeweled Bone Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.dBonePickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.diamondRod
                });
        mod_InfiTools.rBonePickaxe.setIconCoord(4, 8);
        ModLoader.addName(mod_InfiTools.rBonePickaxe, "Red Bone Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.rBonePickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.redstoneRod
                });
        mod_InfiTools.oBonePickaxe.setIconCoord(5, 8);
        ModLoader.addName(mod_InfiTools.oBonePickaxe, "Glassy Bone Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.oBonePickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.obsidianRod
                });
        mod_InfiTools.bBonePickaxe.setIconCoord(6, 8);
        ModLoader.addName(mod_InfiTools.bBonePickaxe, "Reanimated Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bBonePickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Item.bone, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bBonePickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_InfiTools.mBonePickaxe.setIconCoord(7, 8);
        ModLoader.addName(mod_InfiTools.mBonePickaxe, "Mossy Bone Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.mBonePickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.mossyRod
                });
        mod_InfiTools.nBonePickaxe.setIconCoord(8, 8);
        ModLoader.addName(mod_InfiTools.nBonePickaxe, "Netherrack Bone Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.nBonePickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_InfiTools.glBonePickaxe.setIconCoord(9, 8);
        ModLoader.addName(mod_InfiTools.glBonePickaxe, "Glowstone Bone Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.glBonePickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_InfiTools.sBonePickaxe.setIconCoord(10, 8);
        ModLoader.addName(mod_InfiTools.sBonePickaxe, "Slimy Bone Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.sBonePickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_InfiTools.cBonePickaxe.setIconCoord(11, 8);
        ModLoader.addName(mod_InfiTools.cBonePickaxe, "Spiny Bone Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.cBonePickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_InfiTools.fBonePickaxe.setIconCoord(12, 8);
        ModLoader.addName(mod_InfiTools.fBonePickaxe, "Flaky Bone Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.fBonePickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.flintRod
                });
        mod_InfiTools.brBonePickaxe.setIconCoord(13, 8);
        ModLoader.addName(mod_InfiTools.brBonePickaxe, "Baked Bone Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.brBonePickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.brickRod
                });
        mod_InfiTools.blBonePickaxe.setIconCoord(14, 8);
        ModLoader.addName(mod_InfiTools.blBonePickaxe, "Blazing Bone Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.blBonePickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Item.bone, Character.valueOf('|'), Item.blazeRod
                });
        mod_InfiTools.wPaperPickaxe.setIconCoord(0, 9);
        ModLoader.addName(mod_InfiTools.wPaperPickaxe, "Paper Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.wPaperPickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), mod_InfiTools.paperStack, Character.valueOf('|'), Item.stick
                });
        mod_InfiTools.saPaperPickaxe.setIconCoord(1, 9);
        ModLoader.addName(mod_InfiTools.saPaperPickaxe, "Stony Paper Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.saPaperPickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), mod_InfiTools.paperStack, Character.valueOf('|'), mod_InfiTools.sandstoneRod
                });
        mod_InfiTools.bPaperPickaxe.setIconCoord(2, 9);
        ModLoader.addName(mod_InfiTools.bPaperPickaxe, "Necrotic Paper Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bPaperPickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), mod_InfiTools.paperStack, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bPaperPickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), mod_InfiTools.paperStack, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_InfiTools.pPaperPickaxe.setIconCoord(3, 9);
        ModLoader.addName(mod_InfiTools.pPaperPickaxe, "Chimney Sweep Replacment");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.pPaperPickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), mod_InfiTools.paperStack, Character.valueOf('|'), mod_InfiTools.paperRod
                });
        mod_InfiTools.sPaperPickaxe.setIconCoord(4, 9);
        ModLoader.addName(mod_InfiTools.sPaperPickaxe, "Slimy Paper Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.sPaperPickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), mod_InfiTools.paperStack, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_InfiTools.cPaperPickaxe.setIconCoord(5, 9);
        ModLoader.addName(mod_InfiTools.cPaperPickaxe, "Spiny Paper Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.cPaperPickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), mod_InfiTools.paperStack, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_InfiTools.brPaperPickaxe.setIconCoord(6, 9);
        ModLoader.addName(mod_InfiTools.brPaperPickaxe, "Baked Paper Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.brPaperPickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), mod_InfiTools.paperStack, Character.valueOf('|'), mod_InfiTools.brickRod
                });
        mod_InfiTools.stMossyPickaxe.setIconCoord(0, 10);
        ModLoader.addName(mod_InfiTools.stMossyPickaxe, "Stony Moss-Covered Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.stMossyPickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), mod_InfiTools.mossBallCrafted, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_InfiTools.dMossyPickaxe.setIconCoord(1, 10);
        ModLoader.addName(mod_InfiTools.dMossyPickaxe, "Jeweled Moss-Covered Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.dMossyPickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), mod_InfiTools.mossBallCrafted, Character.valueOf('|'), mod_InfiTools.diamondRod
                });
        mod_InfiTools.rMossyPickaxe.setIconCoord(2, 10);
        ModLoader.addName(mod_InfiTools.rMossyPickaxe, "Red Moss-Covered Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.rMossyPickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), mod_InfiTools.mossBallCrafted, Character.valueOf('|'), mod_InfiTools.redstoneRod
                });
        mod_InfiTools.bMossyPickaxe.setIconCoord(3, 10);
        ModLoader.addName(mod_InfiTools.bMossyPickaxe, "Necrotic Moss-Covered Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bMossyPickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), mod_InfiTools.mossBallCrafted, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bMossyPickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), mod_InfiTools.mossBallCrafted, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_InfiTools.mMossyPickaxe.setIconCoord(4, 10);
        ModLoader.addName(mod_InfiTools.mMossyPickaxe, "Living Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.mMossyPickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), mod_InfiTools.mossBallCrafted, Character.valueOf('|'), mod_InfiTools.mossyRod
                });
        mod_InfiTools.glMossyPickaxe.setIconCoord(5, 10);
        ModLoader.addName(mod_InfiTools.glMossyPickaxe, "Glowing Moss-Covered Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.glMossyPickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), mod_InfiTools.mossBallCrafted, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_InfiTools.wNetherrackPickaxe.setIconCoord(0, 11);
        ModLoader.addName(mod_InfiTools.wNetherrackPickaxe, "Netherrack Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.wNetherrackPickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), Item.stick
                });
        mod_InfiTools.stNetherrackPickaxe.setIconCoord(1, 11);
        ModLoader.addName(mod_InfiTools.stNetherrackPickaxe, "Stony Netherrack Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.stNetherrackPickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_InfiTools.iNetherrackPickaxe.setIconCoord(2, 11);
        ModLoader.addName(mod_InfiTools.iNetherrackPickaxe, "Hard Netherrack Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.iNetherrackPickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.ironRod
                });
        mod_InfiTools.rNetherrackPickaxe.setIconCoord(3, 11);
        ModLoader.addName(mod_InfiTools.rNetherrackPickaxe, "Red Netherrack Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.rNetherrackPickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.redstoneRod
                });
        mod_InfiTools.oNetherrackPickaxe.setIconCoord(4, 11);
        ModLoader.addName(mod_InfiTools.oNetherrackPickaxe, "Glassy Netherrack Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.oNetherrackPickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.obsidianRod
                });
        mod_InfiTools.saNetherrackPickaxe.setIconCoord(5, 11);
        ModLoader.addName(mod_InfiTools.saNetherrackPickaxe, "Sandy Netherrack Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.saNetherrackPickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.sandstoneRod
                });
        mod_InfiTools.bNetherrackPickaxe.setIconCoord(6, 11);
        ModLoader.addName(mod_InfiTools.bNetherrackPickaxe, "Necrotic Netherrack Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bNetherrackPickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bNetherrackPickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_InfiTools.mNetherrackPickaxe.setIconCoord(7, 11);
        ModLoader.addName(mod_InfiTools.mNetherrackPickaxe, "Mossy Netherrack Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.mNetherrackPickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.mossyRod
                });
        mod_InfiTools.nNetherrackPickaxe.setIconCoord(8, 11);
        ModLoader.addName(mod_InfiTools.nNetherrackPickaxe, "Blood Pick");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.nNetherrackPickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_InfiTools.glNetherrackPickaxe.setIconCoord(9, 11);
        ModLoader.addName(mod_InfiTools.glNetherrackPickaxe, "Glowing Netherrack Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.glNetherrackPickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_InfiTools.iceNetherrackPickaxe.setIconCoord(10, 11);
        ModLoader.addName(mod_InfiTools.iceNetherrackPickaxe, "Icy Netherrack Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.iceNetherrackPickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.iceRod
                });
        mod_InfiTools.sNetherrackPickaxe.setIconCoord(11, 11);
        ModLoader.addName(mod_InfiTools.sNetherrackPickaxe, "Slimy Netherrack Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.sNetherrackPickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_InfiTools.cNetherrackPickaxe.setIconCoord(12, 11);
        ModLoader.addName(mod_InfiTools.cNetherrackPickaxe, "Spiny Netherrack Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.cNetherrackPickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_InfiTools.fNetherrackPickaxe.setIconCoord(13, 11);
        ModLoader.addName(mod_InfiTools.fNetherrackPickaxe, "Flaky Netherrack Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.fNetherrackPickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_InfiTools.brNetherrackPickaxe.setIconCoord(14, 11);
        ModLoader.addName(mod_InfiTools.brNetherrackPickaxe, "Baked Netherrack Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.brNetherrackPickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_InfiTools.blNetherrackPickaxe.setIconCoord(15, 11);
        ModLoader.addName(mod_InfiTools.blNetherrackPickaxe, "Blazing Netherrack Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.blNetherrackPickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_InfiTools.wGlowstonePickaxe.setIconCoord(0, 12);
        ModLoader.addName(mod_InfiTools.wGlowstonePickaxe, "Glowstone Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.wGlowstonePickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), Item.stick
                });
        mod_InfiTools.stGlowstonePickaxe.setIconCoord(1, 12);
        ModLoader.addName(mod_InfiTools.stGlowstonePickaxe, "Stony Glowstone Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.stGlowstonePickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_InfiTools.iGlowstonePickaxe.setIconCoord(2, 12);
        ModLoader.addName(mod_InfiTools.iGlowstonePickaxe, "Hard Glowstone Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.iGlowstonePickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.ironRod
                });
        mod_InfiTools.dGlowstonePickaxe.setIconCoord(3, 12);
        ModLoader.addName(mod_InfiTools.dGlowstonePickaxe, "Jeweled Glowstone Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.dGlowstonePickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.diamondRod
                });
        mod_InfiTools.rGlowstonePickaxe.setIconCoord(4, 12);
        ModLoader.addName(mod_InfiTools.rGlowstonePickaxe, "Red Glowstone Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.rGlowstonePickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.redstoneRod
                });
        mod_InfiTools.oGlowstonePickaxe.setIconCoord(5, 12);
        ModLoader.addName(mod_InfiTools.oGlowstonePickaxe, "Glassy Glowstone Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.oGlowstonePickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.obsidianRod
                });
        mod_InfiTools.bGlowstonePickaxe.setIconCoord(6, 12);
        ModLoader.addName(mod_InfiTools.bGlowstonePickaxe, "Necrotic Glowstone Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bGlowstonePickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bGlowstonePickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_InfiTools.mGlowstonePickaxe.setIconCoord(7, 12);
        ModLoader.addName(mod_InfiTools.mGlowstonePickaxe, "Mossy Glowstone Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.mGlowstonePickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.mossyRod
                });
        mod_InfiTools.nGlowstonePickaxe.setIconCoord(8, 12);
        ModLoader.addName(mod_InfiTools.nGlowstonePickaxe, "Bloody Glowstone Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.nGlowstonePickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_InfiTools.glGlowstonePickaxe.setIconCoord(9, 12);
        ModLoader.addName(mod_InfiTools.glGlowstonePickaxe, "Bright Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.glGlowstonePickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_InfiTools.iceGlowstonePickaxe.setIconCoord(10, 12);
        ModLoader.addName(mod_InfiTools.iceGlowstonePickaxe, "Icy Glowstone Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.iceGlowstonePickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.iceRod
                });
        mod_InfiTools.lGlowstonePickaxe.setIconCoord(11, 12);
        ModLoader.addName(mod_InfiTools.lGlowstonePickaxe, "Fiery Glowstone Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.lGlowstonePickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_InfiTools.sGlowstonePickaxe.setIconCoord(12, 12);
        ModLoader.addName(mod_InfiTools.sGlowstonePickaxe, "Slimy Glowstone Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.sGlowstonePickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_InfiTools.blGlowstonePickaxe.setIconCoord(13, 12);
        ModLoader.addName(mod_InfiTools.blGlowstonePickaxe, "Blazing Glowstone Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.blGlowstonePickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), Item.blazeRod
                });
        mod_InfiTools.wIcePickaxe.setIconCoord(0, 13);
        ModLoader.addName(mod_InfiTools.wIcePickaxe, "Ice Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.wIcePickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Block.ice, Character.valueOf('|'), Item.stick
                });
        mod_InfiTools.stIcePickaxe.setIconCoord(1, 13);
        ModLoader.addName(mod_InfiTools.stIcePickaxe, "Stony Ice Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.stIcePickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_InfiTools.iIcePickaxe.setIconCoord(2, 13);
        ModLoader.addName(mod_InfiTools.iIcePickaxe, "Hard Ice Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.iIcePickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.ironRod
                });
        mod_InfiTools.dIcePickaxe.setIconCoord(3, 13);
        ModLoader.addName(mod_InfiTools.dIcePickaxe, "Jeweled Ice Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.dIcePickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.diamondRod
                });
        mod_InfiTools.gIcePickaxe.setIconCoord(4, 13);
        ModLoader.addName(mod_InfiTools.gIcePickaxe, "Expensive Ice Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.gIcePickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.goldRod
                });
        mod_InfiTools.rIcePickaxe.setIconCoord(5, 13);
        ModLoader.addName(mod_InfiTools.rIcePickaxe, "Red Ice Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.rIcePickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.redstoneRod
                });
        mod_InfiTools.oIcePickaxe.setIconCoord(6, 13);
        ModLoader.addName(mod_InfiTools.oIcePickaxe, "Glassy Ice Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.oIcePickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.obsidianRod
                });
        mod_InfiTools.saIcePickaxe.setIconCoord(7, 13);
        ModLoader.addName(mod_InfiTools.saIcePickaxe, "Sandy Ice Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.saIcePickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.sandstoneRod
                });
        mod_InfiTools.bIcePickaxe.setIconCoord(8, 13);
        ModLoader.addName(mod_InfiTools.bIcePickaxe, "Necrotic Ice Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bIcePickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Block.ice, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bIcePickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_InfiTools.glIcePickaxe.setIconCoord(9, 13);
        ModLoader.addName(mod_InfiTools.glIcePickaxe, "Glowing Ice Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.glIcePickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_InfiTools.iceIcePickaxe.setIconCoord(10, 13);
        ModLoader.addName(mod_InfiTools.iceIcePickaxe, "Freezing Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.iceIcePickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.iceRod
                });
        mod_InfiTools.sIcePickaxe.setIconCoord(11, 13);
        ModLoader.addName(mod_InfiTools.sIcePickaxe, "Slimy Ice Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.sIcePickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_InfiTools.cIcePickaxe.setIconCoord(12, 13);
        ModLoader.addName(mod_InfiTools.cIcePickaxe, "Spiny Ice Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.cIcePickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_InfiTools.fIcePickaxe.setIconCoord(13, 13);
        ModLoader.addName(mod_InfiTools.fIcePickaxe, "Flaky Ice Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.fIcePickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.flintRod
                });
        mod_InfiTools.brIcePickaxe.setIconCoord(14, 13);
        ModLoader.addName(mod_InfiTools.brIcePickaxe, "Baked Ice Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.brIcePickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.brickRod
                });
        mod_InfiTools.dLavaPickaxe.setIconCoord(0, 14);
        ModLoader.addName(mod_InfiTools.dLavaPickaxe, "Jeweled Lava Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.dLavaPickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), mod_InfiTools.lavaCrystal, Character.valueOf('|'), mod_InfiTools.diamondRod
                });
        mod_InfiTools.rLavaPickaxe.setIconCoord(1, 14);
        ModLoader.addName(mod_InfiTools.rLavaPickaxe, "Red Lava Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.rLavaPickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), mod_InfiTools.lavaCrystal, Character.valueOf('|'), mod_InfiTools.redstoneRod
                });
        mod_InfiTools.bLavaPickaxe.setIconCoord(2, 14);
        ModLoader.addName(mod_InfiTools.bLavaPickaxe, "Necrotic Lava Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bLavaPickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), mod_InfiTools.lavaCrystal, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bLavaPickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), mod_InfiTools.lavaCrystal, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_InfiTools.nLavaPickaxe.setIconCoord(3, 14);
        ModLoader.addName(mod_InfiTools.nLavaPickaxe, "Bloody Lava Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.nLavaPickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), mod_InfiTools.lavaCrystal, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_InfiTools.glLavaPickaxe.setIconCoord(4, 14);
        ModLoader.addName(mod_InfiTools.glLavaPickaxe, "Glowing Lava Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.glLavaPickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), mod_InfiTools.lavaCrystal, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_InfiTools.lLavaPickaxe.setIconCoord(5, 14);
        ModLoader.addName(mod_InfiTools.lLavaPickaxe, "Oresmelter");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.lLavaPickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), mod_InfiTools.lavaCrystal, Character.valueOf('|'), mod_InfiTools.lavaRod
                });
        mod_InfiTools.blLavaPickaxe.setIconCoord(6, 14);
        ModLoader.addName(mod_InfiTools.blLavaPickaxe, "Blazing Lava Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.blLavaPickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), mod_InfiTools.lavaCrystal, Character.valueOf('|'), Item.blazeRod
                });
        mod_InfiTools.wSlimePickaxe.setIconCoord(0, 15);
        ModLoader.addName(mod_InfiTools.wSlimePickaxe, "Slime Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.wSlimePickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), Item.stick
                });
        mod_InfiTools.stSlimePickaxe.setIconCoord(1, 15);
        ModLoader.addName(mod_InfiTools.stSlimePickaxe, "Stony Slime Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.stSlimePickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_InfiTools.iSlimePickaxe.setIconCoord(2, 15);
        ModLoader.addName(mod_InfiTools.iSlimePickaxe, "Hard Slime Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.iSlimePickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.ironRod
                });
        mod_InfiTools.dSlimePickaxe.setIconCoord(3, 15);
        ModLoader.addName(mod_InfiTools.dSlimePickaxe, "Jeweled Slime Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.dSlimePickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.diamondRod
                });
        mod_InfiTools.gSlimePickaxe.setIconCoord(4, 15);
        ModLoader.addName(mod_InfiTools.gSlimePickaxe, "Expensive Slime Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.gSlimePickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.goldRod
                });
        mod_InfiTools.rSlimePickaxe.setIconCoord(5, 15);
        ModLoader.addName(mod_InfiTools.rSlimePickaxe, "Red Slime Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.rSlimePickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.redstoneRod
                });
        mod_InfiTools.oSlimePickaxe.setIconCoord(6, 15);
        ModLoader.addName(mod_InfiTools.oSlimePickaxe, "Glassy Slime Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.oSlimePickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.obsidianRod
                });
        mod_InfiTools.saSlimePickaxe.setIconCoord(7, 15);
        ModLoader.addName(mod_InfiTools.saSlimePickaxe, "Sandy Slime Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.saSlimePickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.sandstoneRod
                });
        mod_InfiTools.bSlimePickaxe.setIconCoord(8, 15);
        ModLoader.addName(mod_InfiTools.bSlimePickaxe, "Necrotic Slime Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bSlimePickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bSlimePickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_InfiTools.pSlimePickaxe.setIconCoord(9, 15);
        ModLoader.addName(mod_InfiTools.pSlimePickaxe, "Fibery Slime Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.pSlimePickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.paperRod
                });
        mod_InfiTools.mSlimePickaxe.setIconCoord(10, 15);
        ModLoader.addName(mod_InfiTools.mSlimePickaxe, "Mossy Slime Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.mSlimePickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.mossyRod
                });
        mod_InfiTools.nSlimePickaxe.setIconCoord(11, 15);
        ModLoader.addName(mod_InfiTools.nSlimePickaxe, "Bloody Slime Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.nSlimePickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_InfiTools.glSlimePickaxe.setIconCoord(12, 15);
        ModLoader.addName(mod_InfiTools.glSlimePickaxe, "Glowing Slime Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.glSlimePickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_InfiTools.iceSlimePickaxe.setIconCoord(13, 15);
        ModLoader.addName(mod_InfiTools.iceSlimePickaxe, "Icy Slime Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.iceSlimePickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.iceRod
                });
        mod_InfiTools.lSlimePickaxe.setIconCoord(14, 15);
        ModLoader.addName(mod_InfiTools.lSlimePickaxe, "Fiery Slime Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.lSlimePickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.lavaRod
                });
        mod_InfiTools.sSlimePickaxe.setIconCoord(15, 15);
        ModLoader.addName(mod_InfiTools.sSlimePickaxe, "Toy Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.sSlimePickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_InfiTools.cSlimePickaxe.setIconCoord(12, 14);
        ModLoader.addName(mod_InfiTools.cSlimePickaxe, "Spiny Slime Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.cSlimePickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_InfiTools.fSlimePickaxe.setIconCoord(13, 14);
        ModLoader.addName(mod_InfiTools.fSlimePickaxe, "Flaky Slime Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.fSlimePickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.flintRod
                });
        mod_InfiTools.brSlimePickaxe.setIconCoord(14, 14);
        ModLoader.addName(mod_InfiTools.brSlimePickaxe, "Baked Slime Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.brSlimePickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.brickRod
                });
        mod_InfiTools.blSlimePickaxe.setIconCoord(15, 14);
        ModLoader.addName(mod_InfiTools.blSlimePickaxe, "Blazing Slime Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.blSlimePickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), Item.blazeRod
                });
        mod_InfiTools.wCactusPickaxe.setIconCoord(10, 0);
        ModLoader.addName(mod_InfiTools.wCactusPickaxe, "Cactus Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.wCactusPickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Block.cactus, Character.valueOf('|'), Item.stick
                });
        mod_InfiTools.stCactusPickaxe.setIconCoord(11, 0);
        ModLoader.addName(mod_InfiTools.stCactusPickaxe, "Stony Cactus Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.stCactusPickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Block.cactus, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_InfiTools.saCactusPickaxe.setIconCoord(12, 0);
        ModLoader.addName(mod_InfiTools.saCactusPickaxe, "Sandy Cactus Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.saCactusPickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Block.cactus, Character.valueOf('|'), mod_InfiTools.sandstoneRod
                });
        mod_InfiTools.bCactusPickaxe.setIconCoord(13, 0);
        ModLoader.addName(mod_InfiTools.bCactusPickaxe, "Necrotic Cactus Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bCactusPickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Block.cactus, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bCactusPickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Block.cactus, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_InfiTools.pCactusPickaxe.setIconCoord(14, 0);
        ModLoader.addName(mod_InfiTools.pCactusPickaxe, "Fibery Cactus Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.pCactusPickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Block.cactus, Character.valueOf('|'), mod_InfiTools.paperRod
                });
        mod_InfiTools.nCactusPickaxe.setIconCoord(15, 0);
        ModLoader.addName(mod_InfiTools.nCactusPickaxe, "Bloody Cactus Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.nCactusPickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Block.cactus, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_InfiTools.sCactusPickaxe.setIconCoord(12, 1);
        ModLoader.addName(mod_InfiTools.sCactusPickaxe, "Slimy Cactus Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.sCactusPickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Block.cactus, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_InfiTools.cCactusPickaxe.setIconCoord(13, 1);
        ModLoader.addName(mod_InfiTools.cCactusPickaxe, "Spined Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.cCactusPickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Block.cactus, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_InfiTools.fCactusPickaxe.setIconCoord(14, 1);
        ModLoader.addName(mod_InfiTools.fCactusPickaxe, "Flaky Cactus Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.fCactusPickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Block.cactus, Character.valueOf('|'), mod_InfiTools.flintRod
                });
        mod_InfiTools.brCactusPickaxe.setIconCoord(15, 1);
        ModLoader.addName(mod_InfiTools.brCactusPickaxe, "Baked Cactus Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.brCactusPickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Block.cactus, Character.valueOf('|'), mod_InfiTools.brickRod
                });
        mod_InfiTools.wFlintPickaxe.setIconCoord(13, 2);
        ModLoader.addName(mod_InfiTools.wFlintPickaxe, "Flint Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.wFlintPickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Item.flint, Character.valueOf('|'), Item.stick
                });
        mod_InfiTools.stFlintPickaxe.setIconCoord(14, 2);
        ModLoader.addName(mod_InfiTools.stFlintPickaxe, "Stony Flint Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.stFlintPickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_InfiTools.iFlintPickaxe.setIconCoord(15, 2);
        ModLoader.addName(mod_InfiTools.iFlintPickaxe, "Hard Flint Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.iFlintPickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.ironRod
                });
        mod_InfiTools.gFlintPickaxe.setIconCoord(12, 3);
        ModLoader.addName(mod_InfiTools.gFlintPickaxe, "Expensive Flint Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.gFlintPickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.goldRod
                });
        mod_InfiTools.oFlintPickaxe.setIconCoord(13, 3);
        ModLoader.addName(mod_InfiTools.oFlintPickaxe, "Glassy Flint Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.oFlintPickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.obsidianRod
                });
        mod_InfiTools.saFlintPickaxe.setIconCoord(14, 3);
        ModLoader.addName(mod_InfiTools.saFlintPickaxe, "Sandy Flint Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.saFlintPickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.sandstoneRod
                });
        mod_InfiTools.bFlintPickaxe.setIconCoord(15, 3);
        ModLoader.addName(mod_InfiTools.bFlintPickaxe, "Necrotic Flint Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bFlintPickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Item.flint, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bFlintPickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_InfiTools.nFlintPickaxe.setIconCoord(12, 4);
        ModLoader.addName(mod_InfiTools.nFlintPickaxe, "Bloody Flint Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.nFlintPickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_InfiTools.iceFlintPickaxe.setIconCoord(13, 4);
        ModLoader.addName(mod_InfiTools.iceFlintPickaxe, "Icy Flint Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.iceFlintPickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.iceRod
                });
        mod_InfiTools.sFlintPickaxe.setIconCoord(14, 4);
        ModLoader.addName(mod_InfiTools.sFlintPickaxe, "Slimy Flint Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.sFlintPickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_InfiTools.cFlintPickaxe.setIconCoord(15, 4);
        ModLoader.addName(mod_InfiTools.cFlintPickaxe, "Spiny Flint Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.cFlintPickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_InfiTools.fFlintPickaxe.setIconCoord(11, 5);
        ModLoader.addName(mod_InfiTools.fFlintPickaxe, "Shunted Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.fFlintPickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.flintRod
                });
        mod_InfiTools.brFlintPickaxe.setIconCoord(12, 5);
        ModLoader.addName(mod_InfiTools.brFlintPickaxe, "Baked Flint Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.brFlintPickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.brickRod
                });
        mod_InfiTools.blFlintPickaxe.setIconCoord(13, 5);
        ModLoader.addName(mod_InfiTools.blFlintPickaxe, "Blazing Flint Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.blFlintPickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Item.flint, Character.valueOf('|'), Item.blazeRod
                });
        mod_InfiTools.wBrickPickaxe.setIconCoord(14, 5);
        ModLoader.addName(mod_InfiTools.wBrickPickaxe, "Brick Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.wBrickPickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Item.brick, Character.valueOf('|'), Item.stick
                });
        mod_InfiTools.stBrickPickaxe.setIconCoord(15, 5);
        ModLoader.addName(mod_InfiTools.stBrickPickaxe, "Stony Brick Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.stBrickPickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Item.brick, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_InfiTools.saBrickPickaxe.setIconCoord(13, 6);
        ModLoader.addName(mod_InfiTools.saBrickPickaxe, "Sandstone Brick Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.saBrickPickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Item.brick, Character.valueOf('|'), mod_InfiTools.sandstoneRod
                });
        mod_InfiTools.bBrickPickaxe.setIconCoord(14, 6);
        ModLoader.addName(mod_InfiTools.bBrickPickaxe, "Necrotic Brick Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bBrickPickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Item.brick, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bBrickPickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Item.brick, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_InfiTools.pBrickPickaxe.setIconCoord(15, 6);
        ModLoader.addName(mod_InfiTools.pBrickPickaxe, "Fibery Brick Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.pBrickPickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Item.brick, Character.valueOf('|'), mod_InfiTools.paperRod
                });
        mod_InfiTools.nBrickPickaxe.setIconCoord(11, 7);
        ModLoader.addName(mod_InfiTools.nBrickPickaxe, "Bloody Brick Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.nBrickPickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Item.brick, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_InfiTools.iceBrickPickaxe.setIconCoord(12, 7);
        ModLoader.addName(mod_InfiTools.iceBrickPickaxe, "Icy Brick Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.iceBrickPickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Item.brick, Character.valueOf('|'), mod_InfiTools.iceRod
                });
        mod_InfiTools.sBrickPickaxe.setIconCoord(13, 7);
        ModLoader.addName(mod_InfiTools.sBrickPickaxe, "Slimy Brick Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.sBrickPickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Item.brick, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_InfiTools.cBrickPickaxe.setIconCoord(14, 7);
        ModLoader.addName(mod_InfiTools.cBrickPickaxe, "Spiny Brick Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.cBrickPickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Item.brick, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_InfiTools.fBrickPickaxe.setIconCoord(15, 7);
        ModLoader.addName(mod_InfiTools.fBrickPickaxe, "Flaky Brick Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.fBrickPickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Item.brick, Character.valueOf('|'), mod_InfiTools.flintRod
                });
        mod_InfiTools.brBrickPickaxe.setIconCoord(15, 8);
        ModLoader.addName(mod_InfiTools.brBrickPickaxe, "Look-alike Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.brBrickPickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), Item.brick, Character.valueOf('|'), mod_InfiTools.brickRod
                });
        mod_InfiTools.dBlazePickaxe.setIconCoord(7, 9);
        ModLoader.addName(mod_InfiTools.dBlazePickaxe, "Jeweled Blaze Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.dBlazePickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), mod_InfiTools.blazeCrystal, Character.valueOf('|'), mod_InfiTools.diamondRod
                });
        mod_InfiTools.rBlazePickaxe.setIconCoord(8, 9);
        ModLoader.addName(mod_InfiTools.rBlazePickaxe, "Red Blaze Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.rBlazePickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), mod_InfiTools.blazeCrystal, Character.valueOf('|'), mod_InfiTools.redstoneRod
                });
        mod_InfiTools.bBlazePickaxe.setIconCoord(9, 9);
        ModLoader.addName(mod_InfiTools.bBlazePickaxe, "Necrotic Blaze Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bBlazePickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), mod_InfiTools.blazeCrystal, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bBlazePickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), mod_InfiTools.blazeCrystal, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_InfiTools.nBlazePickaxe.setIconCoord(10, 9);
        ModLoader.addName(mod_InfiTools.nBlazePickaxe, "Bloody Blaze Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.nBlazePickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), mod_InfiTools.blazeCrystal, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_InfiTools.glBlazePickaxe.setIconCoord(11, 9);
        ModLoader.addName(mod_InfiTools.glBlazePickaxe, "Glowing Blaze Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.glBlazePickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), mod_InfiTools.blazeCrystal, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_InfiTools.lBlazePickaxe.setIconCoord(12, 9);
        ModLoader.addName(mod_InfiTools.lBlazePickaxe, "Fiery Blaze Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.lBlazePickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), mod_InfiTools.blazeCrystal, Character.valueOf('|'), mod_InfiTools.lavaRod
                });
        mod_InfiTools.fBlazePickaxe.setIconCoord(13, 9);
        ModLoader.addName(mod_InfiTools.fBlazePickaxe, "Flaky Blaze Pickaxe");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.fBlazePickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), mod_InfiTools.blazeCrystal, Character.valueOf('|'), mod_InfiTools.flintRod
                });
        mod_InfiTools.blBlazePickaxe.setIconCoord(14, 9);
        ModLoader.addName(mod_InfiTools.blBlazePickaxe, "Nether Commander");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.blBlazePickaxe, 1), new Object[]
                {
                    "mmm", " | ", " | ", Character.valueOf('m'), mod_InfiTools.blazeCrystal, Character.valueOf('|'), Item.blazeRod
                });
        MinecraftForge.setToolClass(mod_InfiTools.stWoodPickaxe, "pickaxe", mod_InfiTools.wLevel);
        MinecraftForge.setToolClass(mod_InfiTools.saWoodPickaxe, "pickaxe", mod_InfiTools.wLevel);
        MinecraftForge.setToolClass(mod_InfiTools.bWoodPickaxe, "pickaxe", mod_InfiTools.wLevel);
        MinecraftForge.setToolClass(mod_InfiTools.pWoodPickaxe, "pickaxe", mod_InfiTools.wLevel);
        MinecraftForge.setToolClass(mod_InfiTools.nWoodPickaxe, "pickaxe", mod_InfiTools.wLevel);
        MinecraftForge.setToolClass(mod_InfiTools.sWoodPickaxe, "pickaxe", mod_InfiTools.wLevel);
        MinecraftForge.setToolClass(mod_InfiTools.cWoodPickaxe, "pickaxe", mod_InfiTools.wLevel);
        MinecraftForge.setToolClass(mod_InfiTools.brWoodPickaxe, "pickaxe", mod_InfiTools.wLevel);
        MinecraftForge.setToolClass(mod_InfiTools.stStonePickaxe, "pickaxe", mod_InfiTools.stLevel);
        MinecraftForge.setToolClass(mod_InfiTools.saStonePickaxe, "pickaxe", mod_InfiTools.stLevel);
        MinecraftForge.setToolClass(mod_InfiTools.bStonePickaxe, "pickaxe", mod_InfiTools.stLevel);
        MinecraftForge.setToolClass(mod_InfiTools.mStonePickaxe, "pickaxe", mod_InfiTools.stLevel);
        MinecraftForge.setToolClass(mod_InfiTools.nStonePickaxe, "pickaxe", mod_InfiTools.stLevel);
        MinecraftForge.setToolClass(mod_InfiTools.iceStonePickaxe, "pickaxe", mod_InfiTools.stLevel);
        MinecraftForge.setToolClass(mod_InfiTools.sStonePickaxe, "pickaxe", mod_InfiTools.stLevel);
        MinecraftForge.setToolClass(mod_InfiTools.cStonePickaxe, "pickaxe", mod_InfiTools.stLevel);
        MinecraftForge.setToolClass(mod_InfiTools.fStonePickaxe, "pickaxe", mod_InfiTools.stLevel);
        MinecraftForge.setToolClass(mod_InfiTools.brStonePickaxe, "pickaxe", mod_InfiTools.stLevel);
        MinecraftForge.setToolClass(mod_InfiTools.stIronPickaxe, "pickaxe", mod_InfiTools.iLevel);
        MinecraftForge.setToolClass(mod_InfiTools.iIronPickaxe, "pickaxe", mod_InfiTools.iLevel);
        MinecraftForge.setToolClass(mod_InfiTools.dIronPickaxe, "pickaxe", mod_InfiTools.iLevel);
        MinecraftForge.setToolClass(mod_InfiTools.gIronPickaxe, "pickaxe", mod_InfiTools.iLevel);
        MinecraftForge.setToolClass(mod_InfiTools.rIronPickaxe, "pickaxe", mod_InfiTools.iLevel);
        MinecraftForge.setToolClass(mod_InfiTools.oIronPickaxe, "pickaxe", mod_InfiTools.iLevel);
        MinecraftForge.setToolClass(mod_InfiTools.bIronPickaxe, "pickaxe", mod_InfiTools.iLevel);
        MinecraftForge.setToolClass(mod_InfiTools.nIronPickaxe, "pickaxe", mod_InfiTools.iLevel);
        MinecraftForge.setToolClass(mod_InfiTools.glIronPickaxe, "pickaxe", mod_InfiTools.iLevel);
        MinecraftForge.setToolClass(mod_InfiTools.iceIronPickaxe, "pickaxe", mod_InfiTools.iLevel);
        MinecraftForge.setToolClass(mod_InfiTools.sIronPickaxe, "pickaxe", mod_InfiTools.iLevel);
        MinecraftForge.setToolClass(mod_InfiTools.blIronPickaxe, "pickaxe", mod_InfiTools.iLevel);
        MinecraftForge.setToolClass(mod_InfiTools.stDiamondPickaxe, "pickaxe", mod_InfiTools.dLevel);
        MinecraftForge.setToolClass(mod_InfiTools.iDiamondPickaxe, "pickaxe", mod_InfiTools.dLevel);
        MinecraftForge.setToolClass(mod_InfiTools.dDiamondPickaxe, "pickaxe", mod_InfiTools.dLevel);
        MinecraftForge.setToolClass(mod_InfiTools.gDiamondPickaxe, "pickaxe", mod_InfiTools.dLevel);
        MinecraftForge.setToolClass(mod_InfiTools.rDiamondPickaxe, "pickaxe", mod_InfiTools.dLevel);
        MinecraftForge.setToolClass(mod_InfiTools.oDiamondPickaxe, "pickaxe", mod_InfiTools.dLevel);
        MinecraftForge.setToolClass(mod_InfiTools.bDiamondPickaxe, "pickaxe", mod_InfiTools.dLevel);
        MinecraftForge.setToolClass(mod_InfiTools.mDiamondPickaxe, "pickaxe", mod_InfiTools.dLevel);
        MinecraftForge.setToolClass(mod_InfiTools.nDiamondPickaxe, "pickaxe", mod_InfiTools.dLevel);
        MinecraftForge.setToolClass(mod_InfiTools.glDiamondPickaxe, "pickaxe", mod_InfiTools.dLevel);
        MinecraftForge.setToolClass(mod_InfiTools.blDiamondPickaxe, "pickaxe", mod_InfiTools.dLevel);
        MinecraftForge.setToolClass(mod_InfiTools.stGoldPickaxe, "pickaxe", mod_InfiTools.gLevel);
        MinecraftForge.setToolClass(mod_InfiTools.gGoldPickaxe, "pickaxe", mod_InfiTools.gLevel);
        MinecraftForge.setToolClass(mod_InfiTools.oGoldPickaxe, "pickaxe", mod_InfiTools.gLevel);
        MinecraftForge.setToolClass(mod_InfiTools.saGoldPickaxe, "pickaxe", mod_InfiTools.gLevel);
        MinecraftForge.setToolClass(mod_InfiTools.bGoldPickaxe, "pickaxe", mod_InfiTools.gLevel);
        MinecraftForge.setToolClass(mod_InfiTools.mGoldPickaxe, "pickaxe", mod_InfiTools.gLevel);
        MinecraftForge.setToolClass(mod_InfiTools.nGoldPickaxe, "pickaxe", mod_InfiTools.gLevel);
        MinecraftForge.setToolClass(mod_InfiTools.glGoldPickaxe, "pickaxe", mod_InfiTools.gLevel);
        MinecraftForge.setToolClass(mod_InfiTools.iceGoldPickaxe, "pickaxe", mod_InfiTools.gLevel);
        MinecraftForge.setToolClass(mod_InfiTools.sGoldPickaxe, "pickaxe", mod_InfiTools.gLevel);
        MinecraftForge.setToolClass(mod_InfiTools.fGoldPickaxe, "pickaxe", mod_InfiTools.gLevel);
        MinecraftForge.setToolClass(mod_InfiTools.wRedstonePickaxe, "pickaxe", mod_InfiTools.rLevel);
        MinecraftForge.setToolClass(mod_InfiTools.stRedstonePickaxe, "pickaxe", mod_InfiTools.rLevel);
        MinecraftForge.setToolClass(mod_InfiTools.iRedstonePickaxe, "pickaxe", mod_InfiTools.rLevel);
        MinecraftForge.setToolClass(mod_InfiTools.dRedstonePickaxe, "pickaxe", mod_InfiTools.rLevel);
        MinecraftForge.setToolClass(mod_InfiTools.rRedstonePickaxe, "pickaxe", mod_InfiTools.rLevel);
        MinecraftForge.setToolClass(mod_InfiTools.oRedstonePickaxe, "pickaxe", mod_InfiTools.rLevel);
        MinecraftForge.setToolClass(mod_InfiTools.bRedstonePickaxe, "pickaxe", mod_InfiTools.rLevel);
        MinecraftForge.setToolClass(mod_InfiTools.mRedstonePickaxe, "pickaxe", mod_InfiTools.rLevel);
        MinecraftForge.setToolClass(mod_InfiTools.glRedstonePickaxe, "pickaxe", mod_InfiTools.rLevel);
        MinecraftForge.setToolClass(mod_InfiTools.sRedstonePickaxe, "pickaxe", mod_InfiTools.rLevel);
        MinecraftForge.setToolClass(mod_InfiTools.blRedstonePickaxe, "pickaxe", mod_InfiTools.rLevel);
        MinecraftForge.setToolClass(mod_InfiTools.wObsidianPickaxe, "pickaxe", mod_InfiTools.oLevel);
        MinecraftForge.setToolClass(mod_InfiTools.stObsidianPickaxe, "pickaxe", mod_InfiTools.oLevel);
        MinecraftForge.setToolClass(mod_InfiTools.iObsidianPickaxe, "pickaxe", mod_InfiTools.oLevel);
        MinecraftForge.setToolClass(mod_InfiTools.dObsidianPickaxe, "pickaxe", mod_InfiTools.oLevel);
        MinecraftForge.setToolClass(mod_InfiTools.gObsidianPickaxe, "pickaxe", mod_InfiTools.oLevel);
        MinecraftForge.setToolClass(mod_InfiTools.rObsidianPickaxe, "pickaxe", mod_InfiTools.oLevel);
        MinecraftForge.setToolClass(mod_InfiTools.oObsidianPickaxe, "pickaxe", mod_InfiTools.oLevel);
        MinecraftForge.setToolClass(mod_InfiTools.bObsidianPickaxe, "pickaxe", mod_InfiTools.oLevel);
        MinecraftForge.setToolClass(mod_InfiTools.nObsidianPickaxe, "pickaxe", mod_InfiTools.oLevel);
        MinecraftForge.setToolClass(mod_InfiTools.glObsidianPickaxe, "pickaxe", mod_InfiTools.oLevel);
        MinecraftForge.setToolClass(mod_InfiTools.sObsidianPickaxe, "pickaxe", mod_InfiTools.oLevel);
        MinecraftForge.setToolClass(mod_InfiTools.fObsidianPickaxe, "pickaxe", mod_InfiTools.oLevel);
        MinecraftForge.setToolClass(mod_InfiTools.blObsidianPickaxe, "pickaxe", mod_InfiTools.oLevel);
        MinecraftForge.setToolClass(mod_InfiTools.wSandstonePickaxe, "pickaxe", mod_InfiTools.saLevel);
        MinecraftForge.setToolClass(mod_InfiTools.stSandstonePickaxe, "pickaxe", mod_InfiTools.saLevel);
        MinecraftForge.setToolClass(mod_InfiTools.saSandstonePickaxe, "pickaxe", mod_InfiTools.saLevel);
        MinecraftForge.setToolClass(mod_InfiTools.bSandstonePickaxe, "pickaxe", mod_InfiTools.saLevel);
        MinecraftForge.setToolClass(mod_InfiTools.pSandstonePickaxe, "pickaxe", mod_InfiTools.saLevel);
        MinecraftForge.setToolClass(mod_InfiTools.nSandstonePickaxe, "pickaxe", mod_InfiTools.saLevel);
        MinecraftForge.setToolClass(mod_InfiTools.iceSandstonePickaxe, "pickaxe", mod_InfiTools.saLevel);
        MinecraftForge.setToolClass(mod_InfiTools.sSandstonePickaxe, "pickaxe", mod_InfiTools.saLevel);
        MinecraftForge.setToolClass(mod_InfiTools.cSandstonePickaxe, "pickaxe", mod_InfiTools.saLevel);
        MinecraftForge.setToolClass(mod_InfiTools.fSandstonePickaxe, "pickaxe", mod_InfiTools.saLevel);
        MinecraftForge.setToolClass(mod_InfiTools.brSandstonePickaxe, "pickaxe", mod_InfiTools.saLevel);
        MinecraftForge.setToolClass(mod_InfiTools.wBonePickaxe, "pickaxe", mod_InfiTools.bLevel);
        MinecraftForge.setToolClass(mod_InfiTools.stBonePickaxe, "pickaxe", mod_InfiTools.bLevel);
        MinecraftForge.setToolClass(mod_InfiTools.iBonePickaxe, "pickaxe", mod_InfiTools.bLevel);
        MinecraftForge.setToolClass(mod_InfiTools.dBonePickaxe, "pickaxe", mod_InfiTools.bLevel);
        MinecraftForge.setToolClass(mod_InfiTools.rBonePickaxe, "pickaxe", mod_InfiTools.bLevel);
        MinecraftForge.setToolClass(mod_InfiTools.oBonePickaxe, "pickaxe", mod_InfiTools.bLevel);
        MinecraftForge.setToolClass(mod_InfiTools.bBonePickaxe, "pickaxe", mod_InfiTools.bLevel);
        MinecraftForge.setToolClass(mod_InfiTools.mBonePickaxe, "pickaxe", mod_InfiTools.bLevel);
        MinecraftForge.setToolClass(mod_InfiTools.nBonePickaxe, "pickaxe", mod_InfiTools.bLevel);
        MinecraftForge.setToolClass(mod_InfiTools.glBonePickaxe, "pickaxe", mod_InfiTools.bLevel);
        MinecraftForge.setToolClass(mod_InfiTools.sBonePickaxe, "pickaxe", mod_InfiTools.bLevel);
        MinecraftForge.setToolClass(mod_InfiTools.cBonePickaxe, "pickaxe", mod_InfiTools.bLevel);
        MinecraftForge.setToolClass(mod_InfiTools.fBonePickaxe, "pickaxe", mod_InfiTools.bLevel);
        MinecraftForge.setToolClass(mod_InfiTools.brBonePickaxe, "pickaxe", mod_InfiTools.bLevel);
        MinecraftForge.setToolClass(mod_InfiTools.blBonePickaxe, "pickaxe", mod_InfiTools.bLevel);
        MinecraftForge.setToolClass(mod_InfiTools.wPaperPickaxe, "pickaxe", mod_InfiTools.pLevel);
        MinecraftForge.setToolClass(mod_InfiTools.saPaperPickaxe, "pickaxe", mod_InfiTools.pLevel);
        MinecraftForge.setToolClass(mod_InfiTools.bPaperPickaxe, "pickaxe", mod_InfiTools.pLevel);
        MinecraftForge.setToolClass(mod_InfiTools.pPaperPickaxe, "pickaxe", mod_InfiTools.pLevel);
        MinecraftForge.setToolClass(mod_InfiTools.sPaperPickaxe, "pickaxe", mod_InfiTools.pLevel);
        MinecraftForge.setToolClass(mod_InfiTools.cPaperPickaxe, "pickaxe", mod_InfiTools.pLevel);
        MinecraftForge.setToolClass(mod_InfiTools.brPaperPickaxe, "pickaxe", mod_InfiTools.pLevel);
        MinecraftForge.setToolClass(mod_InfiTools.stMossyPickaxe, "pickaxe", mod_InfiTools.mLevel);
        MinecraftForge.setToolClass(mod_InfiTools.dMossyPickaxe, "pickaxe", mod_InfiTools.mLevel);
        MinecraftForge.setToolClass(mod_InfiTools.rMossyPickaxe, "pickaxe", mod_InfiTools.mLevel);
        MinecraftForge.setToolClass(mod_InfiTools.bMossyPickaxe, "pickaxe", mod_InfiTools.mLevel);
        MinecraftForge.setToolClass(mod_InfiTools.mMossyPickaxe, "pickaxe", mod_InfiTools.mLevel);
        MinecraftForge.setToolClass(mod_InfiTools.glMossyPickaxe, "pickaxe", mod_InfiTools.mLevel);
        MinecraftForge.setToolClass(mod_InfiTools.wNetherrackPickaxe, "pickaxe", mod_InfiTools.nLevel);
        MinecraftForge.setToolClass(mod_InfiTools.stNetherrackPickaxe, "pickaxe", mod_InfiTools.nLevel);
        MinecraftForge.setToolClass(mod_InfiTools.iNetherrackPickaxe, "pickaxe", mod_InfiTools.nLevel);
        MinecraftForge.setToolClass(mod_InfiTools.rNetherrackPickaxe, "pickaxe", mod_InfiTools.nLevel);
        MinecraftForge.setToolClass(mod_InfiTools.oNetherrackPickaxe, "pickaxe", mod_InfiTools.nLevel);
        MinecraftForge.setToolClass(mod_InfiTools.saNetherrackPickaxe, "pickaxe", mod_InfiTools.nLevel);
        MinecraftForge.setToolClass(mod_InfiTools.bNetherrackPickaxe, "pickaxe", mod_InfiTools.nLevel);
        MinecraftForge.setToolClass(mod_InfiTools.mNetherrackPickaxe, "pickaxe", mod_InfiTools.nLevel);
        MinecraftForge.setToolClass(mod_InfiTools.nNetherrackPickaxe, "pickaxe", mod_InfiTools.nLevel);
        MinecraftForge.setToolClass(mod_InfiTools.glNetherrackPickaxe, "pickaxe", mod_InfiTools.nLevel);
        MinecraftForge.setToolClass(mod_InfiTools.iceNetherrackPickaxe, "pickaxe", mod_InfiTools.nLevel);
        MinecraftForge.setToolClass(mod_InfiTools.sNetherrackPickaxe, "pickaxe", mod_InfiTools.nLevel);
        MinecraftForge.setToolClass(mod_InfiTools.cNetherrackPickaxe, "pickaxe", mod_InfiTools.nLevel);
        MinecraftForge.setToolClass(mod_InfiTools.fNetherrackPickaxe, "pickaxe", mod_InfiTools.nLevel);
        MinecraftForge.setToolClass(mod_InfiTools.brNetherrackPickaxe, "pickaxe", mod_InfiTools.nLevel);
        MinecraftForge.setToolClass(mod_InfiTools.blNetherrackPickaxe, "pickaxe", mod_InfiTools.nLevel);
        MinecraftForge.setToolClass(mod_InfiTools.wGlowstonePickaxe, "pickaxe", mod_InfiTools.glLevel);
        MinecraftForge.setToolClass(mod_InfiTools.stGlowstonePickaxe, "pickaxe", mod_InfiTools.glLevel);
        MinecraftForge.setToolClass(mod_InfiTools.iGlowstonePickaxe, "pickaxe", mod_InfiTools.glLevel);
        MinecraftForge.setToolClass(mod_InfiTools.dGlowstonePickaxe, "pickaxe", mod_InfiTools.glLevel);
        MinecraftForge.setToolClass(mod_InfiTools.rGlowstonePickaxe, "pickaxe", mod_InfiTools.glLevel);
        MinecraftForge.setToolClass(mod_InfiTools.oGlowstonePickaxe, "pickaxe", mod_InfiTools.glLevel);
        MinecraftForge.setToolClass(mod_InfiTools.bGlowstonePickaxe, "pickaxe", mod_InfiTools.glLevel);
        MinecraftForge.setToolClass(mod_InfiTools.mGlowstonePickaxe, "pickaxe", mod_InfiTools.glLevel);
        MinecraftForge.setToolClass(mod_InfiTools.nGlowstonePickaxe, "pickaxe", mod_InfiTools.glLevel);
        MinecraftForge.setToolClass(mod_InfiTools.glGlowstonePickaxe, "pickaxe", mod_InfiTools.glLevel);
        MinecraftForge.setToolClass(mod_InfiTools.iceGlowstonePickaxe, "pickaxe", mod_InfiTools.glLevel);
        MinecraftForge.setToolClass(mod_InfiTools.lGlowstonePickaxe, "pickaxe", mod_InfiTools.glLevel);
        MinecraftForge.setToolClass(mod_InfiTools.sGlowstonePickaxe, "pickaxe", mod_InfiTools.glLevel);
        MinecraftForge.setToolClass(mod_InfiTools.blGlowstonePickaxe, "pickaxe", mod_InfiTools.glLevel);
        MinecraftForge.setToolClass(mod_InfiTools.wIcePickaxe, "pickaxe", mod_InfiTools.iceLevel);
        MinecraftForge.setToolClass(mod_InfiTools.stIcePickaxe, "pickaxe", mod_InfiTools.iceLevel);
        MinecraftForge.setToolClass(mod_InfiTools.iIcePickaxe, "pickaxe", mod_InfiTools.iceLevel);
        MinecraftForge.setToolClass(mod_InfiTools.dIcePickaxe, "pickaxe", mod_InfiTools.iceLevel);
        MinecraftForge.setToolClass(mod_InfiTools.gIcePickaxe, "pickaxe", mod_InfiTools.iceLevel);
        MinecraftForge.setToolClass(mod_InfiTools.rIcePickaxe, "pickaxe", mod_InfiTools.iceLevel);
        MinecraftForge.setToolClass(mod_InfiTools.oIcePickaxe, "pickaxe", mod_InfiTools.iceLevel);
        MinecraftForge.setToolClass(mod_InfiTools.saIcePickaxe, "pickaxe", mod_InfiTools.iceLevel);
        MinecraftForge.setToolClass(mod_InfiTools.bIcePickaxe, "pickaxe", mod_InfiTools.iceLevel);
        MinecraftForge.setToolClass(mod_InfiTools.glIcePickaxe, "pickaxe", mod_InfiTools.iceLevel);
        MinecraftForge.setToolClass(mod_InfiTools.iceIcePickaxe, "pickaxe", mod_InfiTools.iceLevel);
        MinecraftForge.setToolClass(mod_InfiTools.sIcePickaxe, "pickaxe", mod_InfiTools.iceLevel);
        MinecraftForge.setToolClass(mod_InfiTools.cIcePickaxe, "pickaxe", mod_InfiTools.iceLevel);
        MinecraftForge.setToolClass(mod_InfiTools.fIcePickaxe, "pickaxe", mod_InfiTools.iceLevel);
        MinecraftForge.setToolClass(mod_InfiTools.brIcePickaxe, "pickaxe", mod_InfiTools.iceLevel);
        MinecraftForge.setToolClass(mod_InfiTools.dLavaPickaxe, "pickaxe", mod_InfiTools.lLevel);
        MinecraftForge.setToolClass(mod_InfiTools.rLavaPickaxe, "pickaxe", mod_InfiTools.lLevel);
        MinecraftForge.setToolClass(mod_InfiTools.bLavaPickaxe, "pickaxe", mod_InfiTools.lLevel);
        MinecraftForge.setToolClass(mod_InfiTools.nLavaPickaxe, "pickaxe", mod_InfiTools.lLevel);
        MinecraftForge.setToolClass(mod_InfiTools.glLavaPickaxe, "pickaxe", mod_InfiTools.lLevel);
        MinecraftForge.setToolClass(mod_InfiTools.lLavaPickaxe, "pickaxe", mod_InfiTools.lLevel);
        MinecraftForge.setToolClass(mod_InfiTools.blLavaPickaxe, "pickaxe", mod_InfiTools.lLevel);
        MinecraftForge.setToolClass(mod_InfiTools.wSlimePickaxe, "pickaxe", mod_InfiTools.sLevel);
        MinecraftForge.setToolClass(mod_InfiTools.stSlimePickaxe, "pickaxe", mod_InfiTools.sLevel);
        MinecraftForge.setToolClass(mod_InfiTools.iSlimePickaxe, "pickaxe", mod_InfiTools.sLevel);
        MinecraftForge.setToolClass(mod_InfiTools.dSlimePickaxe, "pickaxe", mod_InfiTools.sLevel);
        MinecraftForge.setToolClass(mod_InfiTools.gSlimePickaxe, "pickaxe", mod_InfiTools.sLevel);
        MinecraftForge.setToolClass(mod_InfiTools.rSlimePickaxe, "pickaxe", mod_InfiTools.sLevel);
        MinecraftForge.setToolClass(mod_InfiTools.oSlimePickaxe, "pickaxe", mod_InfiTools.sLevel);
        MinecraftForge.setToolClass(mod_InfiTools.saSlimePickaxe, "pickaxe", mod_InfiTools.sLevel);
        MinecraftForge.setToolClass(mod_InfiTools.bSlimePickaxe, "pickaxe", mod_InfiTools.sLevel);
        MinecraftForge.setToolClass(mod_InfiTools.pSlimePickaxe, "pickaxe", mod_InfiTools.sLevel);
        MinecraftForge.setToolClass(mod_InfiTools.mSlimePickaxe, "pickaxe", mod_InfiTools.sLevel);
        MinecraftForge.setToolClass(mod_InfiTools.nSlimePickaxe, "pickaxe", mod_InfiTools.sLevel);
        MinecraftForge.setToolClass(mod_InfiTools.glSlimePickaxe, "pickaxe", mod_InfiTools.sLevel);
        MinecraftForge.setToolClass(mod_InfiTools.iceSlimePickaxe, "pickaxe", mod_InfiTools.sLevel);
        MinecraftForge.setToolClass(mod_InfiTools.lSlimePickaxe, "pickaxe", mod_InfiTools.sLevel);
        MinecraftForge.setToolClass(mod_InfiTools.sSlimePickaxe, "pickaxe", mod_InfiTools.sLevel);
        MinecraftForge.setToolClass(mod_InfiTools.cSlimePickaxe, "pickaxe", mod_InfiTools.sLevel);
        MinecraftForge.setToolClass(mod_InfiTools.fSlimePickaxe, "pickaxe", mod_InfiTools.sLevel);
        MinecraftForge.setToolClass(mod_InfiTools.brSlimePickaxe, "pickaxe", mod_InfiTools.sLevel);
        MinecraftForge.setToolClass(mod_InfiTools.blSlimePickaxe, "pickaxe", mod_InfiTools.sLevel);
        MinecraftForge.setToolClass(mod_InfiTools.wCactusPickaxe, "pickaxe", mod_InfiTools.cLevel);
        MinecraftForge.setToolClass(mod_InfiTools.stCactusPickaxe, "pickaxe", mod_InfiTools.cLevel);
        MinecraftForge.setToolClass(mod_InfiTools.saCactusPickaxe, "pickaxe", mod_InfiTools.cLevel);
        MinecraftForge.setToolClass(mod_InfiTools.bCactusPickaxe, "pickaxe", mod_InfiTools.cLevel);
        MinecraftForge.setToolClass(mod_InfiTools.pCactusPickaxe, "pickaxe", mod_InfiTools.cLevel);
        MinecraftForge.setToolClass(mod_InfiTools.nCactusPickaxe, "pickaxe", mod_InfiTools.cLevel);
        MinecraftForge.setToolClass(mod_InfiTools.sCactusPickaxe, "pickaxe", mod_InfiTools.cLevel);
        MinecraftForge.setToolClass(mod_InfiTools.cCactusPickaxe, "pickaxe", mod_InfiTools.cLevel);
        MinecraftForge.setToolClass(mod_InfiTools.fCactusPickaxe, "pickaxe", mod_InfiTools.cLevel);
        MinecraftForge.setToolClass(mod_InfiTools.brCactusPickaxe, "pickaxe", mod_InfiTools.cLevel);
        MinecraftForge.setToolClass(mod_InfiTools.wFlintPickaxe, "pickaxe", mod_InfiTools.fLevel);
        MinecraftForge.setToolClass(mod_InfiTools.stFlintPickaxe, "pickaxe", mod_InfiTools.fLevel);
        MinecraftForge.setToolClass(mod_InfiTools.iFlintPickaxe, "pickaxe", mod_InfiTools.fLevel);
        MinecraftForge.setToolClass(mod_InfiTools.gFlintPickaxe, "pickaxe", mod_InfiTools.fLevel);
        MinecraftForge.setToolClass(mod_InfiTools.oFlintPickaxe, "pickaxe", mod_InfiTools.fLevel);
        MinecraftForge.setToolClass(mod_InfiTools.saFlintPickaxe, "pickaxe", mod_InfiTools.fLevel);
        MinecraftForge.setToolClass(mod_InfiTools.bFlintPickaxe, "pickaxe", mod_InfiTools.fLevel);
        MinecraftForge.setToolClass(mod_InfiTools.nFlintPickaxe, "pickaxe", mod_InfiTools.fLevel);
        MinecraftForge.setToolClass(mod_InfiTools.iceFlintPickaxe, "pickaxe", mod_InfiTools.fLevel);
        MinecraftForge.setToolClass(mod_InfiTools.sFlintPickaxe, "pickaxe", mod_InfiTools.fLevel);
        MinecraftForge.setToolClass(mod_InfiTools.cFlintPickaxe, "pickaxe", mod_InfiTools.fLevel);
        MinecraftForge.setToolClass(mod_InfiTools.fFlintPickaxe, "pickaxe", mod_InfiTools.fLevel);
        MinecraftForge.setToolClass(mod_InfiTools.brFlintPickaxe, "pickaxe", mod_InfiTools.fLevel);
        MinecraftForge.setToolClass(mod_InfiTools.blFlintPickaxe, "pickaxe", mod_InfiTools.fLevel);
        MinecraftForge.setToolClass(mod_InfiTools.wBrickPickaxe, "pickaxe", mod_InfiTools.brLevel);
        MinecraftForge.setToolClass(mod_InfiTools.stBrickPickaxe, "pickaxe", mod_InfiTools.brLevel);
        MinecraftForge.setToolClass(mod_InfiTools.saBrickPickaxe, "pickaxe", mod_InfiTools.brLevel);
        MinecraftForge.setToolClass(mod_InfiTools.bBrickPickaxe, "pickaxe", mod_InfiTools.brLevel);
        MinecraftForge.setToolClass(mod_InfiTools.pBrickPickaxe, "pickaxe", mod_InfiTools.brLevel);
        MinecraftForge.setToolClass(mod_InfiTools.nBrickPickaxe, "pickaxe", mod_InfiTools.brLevel);
        MinecraftForge.setToolClass(mod_InfiTools.iceBrickPickaxe, "pickaxe", mod_InfiTools.brLevel);
        MinecraftForge.setToolClass(mod_InfiTools.sBrickPickaxe, "pickaxe", mod_InfiTools.brLevel);
        MinecraftForge.setToolClass(mod_InfiTools.cBrickPickaxe, "pickaxe", mod_InfiTools.brLevel);
        MinecraftForge.setToolClass(mod_InfiTools.fBrickPickaxe, "pickaxe", mod_InfiTools.brLevel);
        MinecraftForge.setToolClass(mod_InfiTools.brBrickPickaxe, "pickaxe", mod_InfiTools.brLevel);
        MinecraftForge.setToolClass(mod_InfiTools.dBlazePickaxe, "pickaxe", mod_InfiTools.blLevel);
        MinecraftForge.setToolClass(mod_InfiTools.rBlazePickaxe, "pickaxe", mod_InfiTools.blLevel);
        MinecraftForge.setToolClass(mod_InfiTools.bBlazePickaxe, "pickaxe", mod_InfiTools.blLevel);
        MinecraftForge.setToolClass(mod_InfiTools.nBlazePickaxe, "pickaxe", mod_InfiTools.blLevel);
        MinecraftForge.setToolClass(mod_InfiTools.glBlazePickaxe, "pickaxe", mod_InfiTools.blLevel);
        MinecraftForge.setToolClass(mod_InfiTools.lBlazePickaxe, "pickaxe", mod_InfiTools.blLevel);
        MinecraftForge.setToolClass(mod_InfiTools.fBlazePickaxe, "pickaxe", mod_InfiTools.blLevel);
        MinecraftForge.setToolClass(mod_InfiTools.blBlazePickaxe, "pickaxe", mod_InfiTools.blLevel);
        return 0;
    }
}
