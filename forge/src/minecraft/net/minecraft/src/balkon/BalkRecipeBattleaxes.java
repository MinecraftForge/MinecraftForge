package net.minecraft.src.balkon;

import net.minecraft.src.*;

public class BalkRecipeBattleaxes
{
    public BalkRecipeBattleaxes()
    {
    }

    public static int recipeStorm()
    {
        mod_BalkonInfiWeapons.stWoodBattleaxe.setIconCoord(1, 0);
        ModLoader.addName(mod_BalkonInfiWeapons.stWoodBattleaxe, "Stony Wooden Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.stWoodBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Block.planks, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_BalkonInfiWeapons.saWoodBattleaxe.setIconCoord(2, 0);
        ModLoader.addName(mod_BalkonInfiWeapons.saWoodBattleaxe, "Sandy Wooden Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.saWoodBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Block.planks, Character.valueOf('|'), mod_InfiTools.sandstoneRod
                });
        mod_BalkonInfiWeapons.bWoodBattleaxe.setIconCoord(3, 0);
        ModLoader.addName(mod_BalkonInfiWeapons.bWoodBattleaxe, "Necrotic Wooden Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bWoodBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Block.planks, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bWoodBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Block.planks, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_BalkonInfiWeapons.pWoodBattleaxe.setIconCoord(4, 0);
        ModLoader.addName(mod_BalkonInfiWeapons.pWoodBattleaxe, "Fibery Wooden Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.pWoodBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Block.planks, Character.valueOf('|'), mod_InfiTools.paperRod
                });
        mod_BalkonInfiWeapons.nWoodBattleaxe.setIconCoord(5, 0);
        ModLoader.addName(mod_BalkonInfiWeapons.nWoodBattleaxe, "Bloody Wooden Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.nWoodBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Block.planks, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_BalkonInfiWeapons.sWoodBattleaxe.setIconCoord(6, 0);
        ModLoader.addName(mod_BalkonInfiWeapons.sWoodBattleaxe, "Slimy Wooden Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.sWoodBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Block.planks, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_BalkonInfiWeapons.cWoodBattleaxe.setIconCoord(7, 0);
        ModLoader.addName(mod_BalkonInfiWeapons.cWoodBattleaxe, "Spiny Wooden Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.cWoodBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Block.planks, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_BalkonInfiWeapons.fWoodBattleaxe.setIconCoord(8, 0);
        ModLoader.addName(mod_BalkonInfiWeapons.fWoodBattleaxe, "Flaky Wooden Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.fWoodBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Block.planks, Character.valueOf('|'), mod_InfiTools.flintRod
                });
        mod_BalkonInfiWeapons.brWoodBattleaxe.setIconCoord(9, 0);
        ModLoader.addName(mod_BalkonInfiWeapons.brWoodBattleaxe, "Baked Wooden Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.brWoodBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Block.planks, Character.valueOf('|'), mod_InfiTools.brickRod
                });
        mod_BalkonInfiWeapons.stStoneBattleaxe.setIconCoord(1, 1);
        ModLoader.addName(mod_BalkonInfiWeapons.stStoneBattleaxe, "Heavy Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.stStoneBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.stStoneBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Block.stone, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_BalkonInfiWeapons.saStoneBattleaxe.setIconCoord(2, 1);
        ModLoader.addName(mod_BalkonInfiWeapons.saStoneBattleaxe, "Sandy Stone Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.saStoneBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), mod_InfiTools.sandstoneRod
                });
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.saStoneBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Block.stone, Character.valueOf('|'), mod_InfiTools.sandstoneRod
                });
        mod_BalkonInfiWeapons.bStoneBattleaxe.setIconCoord(3, 1);
        ModLoader.addName(mod_BalkonInfiWeapons.bStoneBattleaxe, "Necrotic Stone Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bStoneBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bStoneBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Block.stone, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bStoneBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bStoneBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Block.stone, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_BalkonInfiWeapons.pStoneBattleaxe.setIconCoord(4, 1);
        ModLoader.addName(mod_BalkonInfiWeapons.pStoneBattleaxe, "Fibery Stone Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.pStoneBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), mod_InfiTools.paperRod
                });
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.pStoneBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Block.stone, Character.valueOf('|'), mod_InfiTools.paperRod
                });
        mod_BalkonInfiWeapons.mStoneBattleaxe.setIconCoord(5, 1);
        ModLoader.addName(mod_BalkonInfiWeapons.mStoneBattleaxe, "Mossy Stone Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.mStoneBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), mod_InfiTools.mossyRod
                });
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.mStoneBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Block.stone, Character.valueOf('|'), mod_InfiTools.mossyRod
                });
        mod_BalkonInfiWeapons.nStoneBattleaxe.setIconCoord(6, 1);
        ModLoader.addName(mod_BalkonInfiWeapons.nStoneBattleaxe, "Bloody Stone Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.nStoneBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.nStoneBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Block.stone, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_BalkonInfiWeapons.iceStoneBattleaxe.setIconCoord(7, 1);
        ModLoader.addName(mod_BalkonInfiWeapons.iceStoneBattleaxe, "Icy Stone Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.iceStoneBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), mod_InfiTools.iceRod
                });
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.iceStoneBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Block.stone, Character.valueOf('|'), mod_InfiTools.iceRod
                });
        mod_BalkonInfiWeapons.sStoneBattleaxe.setIconCoord(8, 1);
        ModLoader.addName(mod_BalkonInfiWeapons.sStoneBattleaxe, "Slimy Stone Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.sStoneBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.sStoneBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Block.stone, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_BalkonInfiWeapons.cStoneBattleaxe.setIconCoord(9, 1);
        ModLoader.addName(mod_BalkonInfiWeapons.cStoneBattleaxe, "Spiny Stone Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.cStoneBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.cStoneBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Block.stone, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_BalkonInfiWeapons.fStoneBattleaxe.setIconCoord(10, 1);
        ModLoader.addName(mod_BalkonInfiWeapons.fStoneBattleaxe, "Flaky Stone Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.fStoneBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), mod_InfiTools.flintRod
                });
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.fStoneBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Block.stone, Character.valueOf('|'), mod_InfiTools.flintRod
                });
        mod_BalkonInfiWeapons.brStoneBattleaxe.setIconCoord(11, 1);
        ModLoader.addName(mod_BalkonInfiWeapons.brStoneBattleaxe, "Baked Stone Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.brStoneBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), mod_InfiTools.brickRod
                });
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.brStoneBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Block.stone, Character.valueOf('|'), mod_InfiTools.brickRod
                });
        mod_BalkonInfiWeapons.stIronBattleaxe.setIconCoord(1, 2);
        ModLoader.addName(mod_BalkonInfiWeapons.stIronBattleaxe, "Stony Iron Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.stIronBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_BalkonInfiWeapons.iIronBattleaxe.setIconCoord(2, 2);
        ModLoader.addName(mod_BalkonInfiWeapons.iIronBattleaxe, "Ironic Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.iIronBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), mod_InfiTools.ironRod
                });
        mod_BalkonInfiWeapons.dIronBattleaxe.setIconCoord(3, 2);
        ModLoader.addName(mod_BalkonInfiWeapons.dIronBattleaxe, "Jeweled Iron Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.dIronBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), mod_InfiTools.diamondRod
                });
        mod_BalkonInfiWeapons.gIronBattleaxe.setIconCoord(4, 2);
        ModLoader.addName(mod_BalkonInfiWeapons.gIronBattleaxe, "Expensive Iron Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.gIronBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), mod_InfiTools.goldRod
                });
        mod_BalkonInfiWeapons.rIronBattleaxe.setIconCoord(5, 2);
        ModLoader.addName(mod_BalkonInfiWeapons.rIronBattleaxe, "Red Iron Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.rIronBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), mod_InfiTools.redstoneRod
                });
        mod_BalkonInfiWeapons.oIronBattleaxe.setIconCoord(6, 2);
        ModLoader.addName(mod_BalkonInfiWeapons.oIronBattleaxe, "Glassy Iron Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.oIronBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), mod_InfiTools.obsidianRod
                });
        mod_BalkonInfiWeapons.bIronBattleaxe.setIconCoord(7, 2);
        ModLoader.addName(mod_BalkonInfiWeapons.bIronBattleaxe, "Necrotic Iron Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bIronBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bIronBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_BalkonInfiWeapons.nIronBattleaxe.setIconCoord(8, 2);
        ModLoader.addName(mod_BalkonInfiWeapons.nIronBattleaxe, "Bloody Iron Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.nIronBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_BalkonInfiWeapons.glIronBattleaxe.setIconCoord(9, 2);
        ModLoader.addName(mod_BalkonInfiWeapons.glIronBattleaxe, "Glowing Iron Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.glIronBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_BalkonInfiWeapons.iceIronBattleaxe.setIconCoord(10, 2);
        ModLoader.addName(mod_BalkonInfiWeapons.iceIronBattleaxe, "Icy Iron Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.iceIronBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), mod_InfiTools.iceRod
                });
        mod_BalkonInfiWeapons.sIronBattleaxe.setIconCoord(11, 2);
        ModLoader.addName(mod_BalkonInfiWeapons.sIronBattleaxe, "Slimy Iron Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.sIronBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_BalkonInfiWeapons.blIronBattleaxe.setIconCoord(12, 2);
        ModLoader.addName(mod_BalkonInfiWeapons.blIronBattleaxe, "Blazing Iron Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.blIronBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), Item.blazeRod
                });
        mod_BalkonInfiWeapons.stDiamondBattleaxe.setIconCoord(1, 3);
        ModLoader.addName(mod_BalkonInfiWeapons.stDiamondBattleaxe, "Stony Diamond Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.stDiamondBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_BalkonInfiWeapons.iDiamondBattleaxe.setIconCoord(2, 3);
        ModLoader.addName(mod_BalkonInfiWeapons.iDiamondBattleaxe, "Hard Diamond Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.iDiamondBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), mod_InfiTools.ironRod
                });
        mod_BalkonInfiWeapons.dDiamondBattleaxe.setIconCoord(3, 3);
        ModLoader.addName(mod_BalkonInfiWeapons.dDiamondBattleaxe, "Diamondium Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.dDiamondBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), mod_InfiTools.diamondRod
                });
        mod_BalkonInfiWeapons.gDiamondBattleaxe.setIconCoord(4, 3);
        ModLoader.addName(mod_BalkonInfiWeapons.gDiamondBattleaxe, "Expensive Diamond Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.gDiamondBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), mod_InfiTools.goldRod
                });
        mod_BalkonInfiWeapons.rDiamondBattleaxe.setIconCoord(5, 3);
        ModLoader.addName(mod_BalkonInfiWeapons.rDiamondBattleaxe, "Red Diamond Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.rDiamondBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), mod_InfiTools.redstoneRod
                });
        mod_BalkonInfiWeapons.oDiamondBattleaxe.setIconCoord(6, 3);
        ModLoader.addName(mod_BalkonInfiWeapons.oDiamondBattleaxe, "Glassy Diamond Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.oDiamondBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), mod_InfiTools.obsidianRod
                });
        mod_BalkonInfiWeapons.bDiamondBattleaxe.setIconCoord(7, 3);
        ModLoader.addName(mod_BalkonInfiWeapons.bDiamondBattleaxe, "Necrotic Diamond Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bDiamondBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bDiamondBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_BalkonInfiWeapons.mDiamondBattleaxe.setIconCoord(8, 3);
        ModLoader.addName(mod_BalkonInfiWeapons.mDiamondBattleaxe, "Mossy Diamond Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.mDiamondBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), mod_InfiTools.mossyRod
                });
        mod_BalkonInfiWeapons.nDiamondBattleaxe.setIconCoord(9, 3);
        ModLoader.addName(mod_BalkonInfiWeapons.nDiamondBattleaxe, "Bloody Diamond Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.nDiamondBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_BalkonInfiWeapons.glDiamondBattleaxe.setIconCoord(10, 3);
        ModLoader.addName(mod_BalkonInfiWeapons.glDiamondBattleaxe, "Glowing Diamond Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.glDiamondBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_BalkonInfiWeapons.blDiamondBattleaxe.setIconCoord(11, 3);
        ModLoader.addName(mod_BalkonInfiWeapons.blDiamondBattleaxe, "Blazing Diamond Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.blDiamondBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), Item.blazeRod
                });
        mod_BalkonInfiWeapons.stGoldBattleaxe.setIconCoord(1, 4);
        ModLoader.addName(mod_BalkonInfiWeapons.stGoldBattleaxe, "Stony Gold Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.stGoldBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_BalkonInfiWeapons.gGoldBattleaxe.setIconCoord(2, 4);
        ModLoader.addName(mod_BalkonInfiWeapons.gGoldBattleaxe, "Expensive Useless Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.gGoldBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), mod_InfiTools.goldRod
                });
        mod_BalkonInfiWeapons.oGoldBattleaxe.setIconCoord(3, 4);
        ModLoader.addName(mod_BalkonInfiWeapons.oGoldBattleaxe, "Glassy Gold Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.oGoldBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), mod_InfiTools.obsidianRod
                });
        mod_BalkonInfiWeapons.saGoldBattleaxe.setIconCoord(4, 4);
        ModLoader.addName(mod_BalkonInfiWeapons.saGoldBattleaxe, "Sandy Gold Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.saGoldBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), mod_InfiTools.sandstoneRod
                });
        mod_BalkonInfiWeapons.bGoldBattleaxe.setIconCoord(5, 4);
        ModLoader.addName(mod_BalkonInfiWeapons.bGoldBattleaxe, "Necrotic Gold Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bGoldBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bGoldBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_BalkonInfiWeapons.mGoldBattleaxe.setIconCoord(6, 4);
        ModLoader.addName(mod_BalkonInfiWeapons.mGoldBattleaxe, "Mossy Gold Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.mGoldBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), mod_InfiTools.mossyRod
                });
        mod_BalkonInfiWeapons.nGoldBattleaxe.setIconCoord(7, 4);
        ModLoader.addName(mod_BalkonInfiWeapons.nGoldBattleaxe, "Bloody Gold Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.nGoldBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_BalkonInfiWeapons.glGoldBattleaxe.setIconCoord(8, 4);
        ModLoader.addName(mod_BalkonInfiWeapons.glGoldBattleaxe, "Glowing Gold Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.glGoldBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_BalkonInfiWeapons.iceGoldBattleaxe.setIconCoord(9, 4);
        ModLoader.addName(mod_BalkonInfiWeapons.iceGoldBattleaxe, "Icy Gold Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.iceGoldBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), mod_InfiTools.iceRod
                });
        mod_BalkonInfiWeapons.sGoldBattleaxe.setIconCoord(10, 4);
        ModLoader.addName(mod_BalkonInfiWeapons.sGoldBattleaxe, "Slimy Gold Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.sGoldBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_BalkonInfiWeapons.fGoldBattleaxe.setIconCoord(11, 4);
        ModLoader.addName(mod_BalkonInfiWeapons.fGoldBattleaxe, "Flaky Gold Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.fGoldBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), mod_InfiTools.flintRod
                });
        mod_BalkonInfiWeapons.wRedstoneBattleaxe.setIconCoord(0, 5);
        ModLoader.addName(mod_BalkonInfiWeapons.wRedstoneBattleaxe, "Redstone Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.wRedstoneBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), Item.stick
                });
        mod_BalkonInfiWeapons.stRedstoneBattleaxe.setIconCoord(1, 5);
        ModLoader.addName(mod_BalkonInfiWeapons.stRedstoneBattleaxe, "Stony Redstone Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.stRedstoneBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_BalkonInfiWeapons.iRedstoneBattleaxe.setIconCoord(2, 5);
        ModLoader.addName(mod_BalkonInfiWeapons.iRedstoneBattleaxe, "Hard Redstone Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.iRedstoneBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), mod_InfiTools.ironRod
                });
        mod_BalkonInfiWeapons.dRedstoneBattleaxe.setIconCoord(3, 5);
        ModLoader.addName(mod_BalkonInfiWeapons.dRedstoneBattleaxe, "Jeweled Redstone Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.dRedstoneBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), mod_InfiTools.diamondRod
                });
        mod_BalkonInfiWeapons.rRedstoneBattleaxe.setIconCoord(4, 5);
        ModLoader.addName(mod_BalkonInfiWeapons.rRedstoneBattleaxe, "Redredred Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.rRedstoneBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), mod_InfiTools.redstoneRod
                });
        mod_BalkonInfiWeapons.oRedstoneBattleaxe.setIconCoord(5, 5);
        ModLoader.addName(mod_BalkonInfiWeapons.oRedstoneBattleaxe, "Glassy Redstone Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.oRedstoneBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), mod_InfiTools.obsidianRod
                });
        mod_BalkonInfiWeapons.bRedstoneBattleaxe.setIconCoord(6, 5);
        ModLoader.addName(mod_BalkonInfiWeapons.bRedstoneBattleaxe, "Necrotic Redstone Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bRedstoneBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bRedstoneBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_BalkonInfiWeapons.mRedstoneBattleaxe.setIconCoord(7, 5);
        ModLoader.addName(mod_BalkonInfiWeapons.mRedstoneBattleaxe, "Mossy Redstone Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.mRedstoneBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), mod_InfiTools.mossyRod
                });
        mod_BalkonInfiWeapons.glRedstoneBattleaxe.setIconCoord(8, 5);
        ModLoader.addName(mod_BalkonInfiWeapons.glRedstoneBattleaxe, "Glowing Redstone Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.glRedstoneBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_BalkonInfiWeapons.sRedstoneBattleaxe.setIconCoord(9, 5);
        ModLoader.addName(mod_BalkonInfiWeapons.sRedstoneBattleaxe, "Slimy Redstone Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.sRedstoneBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_BalkonInfiWeapons.blRedstoneBattleaxe.setIconCoord(10, 5);
        ModLoader.addName(mod_BalkonInfiWeapons.blRedstoneBattleaxe, "Blazing Redstone Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.blRedstoneBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), Item.blazeRod
                });
        mod_BalkonInfiWeapons.wObsidianBattleaxe.setIconCoord(0, 6);
        ModLoader.addName(mod_BalkonInfiWeapons.wObsidianBattleaxe, "Obsidian Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.wObsidianBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), Item.stick
                });
        mod_BalkonInfiWeapons.stObsidianBattleaxe.setIconCoord(1, 6);
        ModLoader.addName(mod_BalkonInfiWeapons.stObsidianBattleaxe, "Stony Obsidian Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.stObsidianBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_BalkonInfiWeapons.iObsidianBattleaxe.setIconCoord(2, 6);
        ModLoader.addName(mod_BalkonInfiWeapons.iObsidianBattleaxe, "Hard Obsidian Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.iObsidianBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), mod_InfiTools.ironRod
                });
        mod_BalkonInfiWeapons.dObsidianBattleaxe.setIconCoord(3, 6);
        ModLoader.addName(mod_BalkonInfiWeapons.dObsidianBattleaxe, "Jeweled Obsidian Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.dObsidianBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), mod_InfiTools.diamondRod
                });
        mod_BalkonInfiWeapons.gObsidianBattleaxe.setIconCoord(4, 6);
        ModLoader.addName(mod_BalkonInfiWeapons.gObsidianBattleaxe, "Expensive Obsidian Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.gObsidianBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), mod_InfiTools.goldRod
                });
        mod_BalkonInfiWeapons.rObsidianBattleaxe.setIconCoord(5, 6);
        ModLoader.addName(mod_BalkonInfiWeapons.rObsidianBattleaxe, "Red Obsidian Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.rObsidianBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), mod_InfiTools.redstoneRod
                });
        mod_BalkonInfiWeapons.oObsidianBattleaxe.setIconCoord(6, 6);
        ModLoader.addName(mod_BalkonInfiWeapons.oObsidianBattleaxe, "Wicked Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.oObsidianBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), mod_InfiTools.obsidianRod
                });
        mod_BalkonInfiWeapons.bObsidianBattleaxe.setIconCoord(7, 6);
        ModLoader.addName(mod_BalkonInfiWeapons.bObsidianBattleaxe, "Necrotic Obsidian Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bObsidianBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bObsidianBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_BalkonInfiWeapons.nObsidianBattleaxe.setIconCoord(8, 6);
        ModLoader.addName(mod_BalkonInfiWeapons.nObsidianBattleaxe, "Bloody Obsidian Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.nObsidianBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_BalkonInfiWeapons.glObsidianBattleaxe.setIconCoord(9, 6);
        ModLoader.addName(mod_BalkonInfiWeapons.glObsidianBattleaxe, "Glowing Obsidian Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.glObsidianBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_BalkonInfiWeapons.sObsidianBattleaxe.setIconCoord(10, 6);
        ModLoader.addName(mod_BalkonInfiWeapons.sObsidianBattleaxe, "Slimy Obsidian Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.sObsidianBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_BalkonInfiWeapons.fObsidianBattleaxe.setIconCoord(11, 6);
        ModLoader.addName(mod_BalkonInfiWeapons.fObsidianBattleaxe, "Flaky Obsidian Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.fObsidianBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), mod_InfiTools.flintRod
                });
        mod_BalkonInfiWeapons.blObsidianBattleaxe.setIconCoord(12, 6);
        ModLoader.addName(mod_BalkonInfiWeapons.blObsidianBattleaxe, "Blazing Obsidian Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.blObsidianBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), Item.blazeRod
                });
        mod_BalkonInfiWeapons.wSandstoneBattleaxe.setIconCoord(0, 7);
        ModLoader.addName(mod_BalkonInfiWeapons.wSandstoneBattleaxe, "Sandstone Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.wSandstoneBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), Item.stick
                });
        mod_BalkonInfiWeapons.stSandstoneBattleaxe.setIconCoord(1, 7);
        ModLoader.addName(mod_BalkonInfiWeapons.stSandstoneBattleaxe, "Stony Sandstone Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.stSandstoneBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_BalkonInfiWeapons.saSandstoneBattleaxe.setIconCoord(2, 7);
        ModLoader.addName(mod_BalkonInfiWeapons.saSandstoneBattleaxe, "Sandsmite");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.saSandstoneBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), mod_InfiTools.sandstoneRod
                });
        mod_BalkonInfiWeapons.bSandstoneBattleaxe.setIconCoord(3, 7);
        ModLoader.addName(mod_BalkonInfiWeapons.bSandstoneBattleaxe, "Necrotic Sandstone Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bSandstoneBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bSandstoneBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_BalkonInfiWeapons.pSandstoneBattleaxe.setIconCoord(4, 7);
        ModLoader.addName(mod_BalkonInfiWeapons.pSandstoneBattleaxe, "Fibery Sandstone Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.pSandstoneBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), mod_InfiTools.paperRod
                });
        mod_BalkonInfiWeapons.nSandstoneBattleaxe.setIconCoord(5, 7);
        ModLoader.addName(mod_BalkonInfiWeapons.nSandstoneBattleaxe, "Bloody Sandstone Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.nSandstoneBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_BalkonInfiWeapons.iceSandstoneBattleaxe.setIconCoord(6, 7);
        ModLoader.addName(mod_BalkonInfiWeapons.iceSandstoneBattleaxe, "Icy Sandstone Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.iceSandstoneBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), mod_InfiTools.iceRod
                });
        mod_BalkonInfiWeapons.sSandstoneBattleaxe.setIconCoord(7, 7);
        ModLoader.addName(mod_BalkonInfiWeapons.sSandstoneBattleaxe, "Slimy Sandstone Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.sSandstoneBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_BalkonInfiWeapons.cSandstoneBattleaxe.setIconCoord(8, 7);
        ModLoader.addName(mod_BalkonInfiWeapons.cSandstoneBattleaxe, "Spiny Sandstone Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.cSandstoneBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_BalkonInfiWeapons.fSandstoneBattleaxe.setIconCoord(9, 7);
        ModLoader.addName(mod_BalkonInfiWeapons.fSandstoneBattleaxe, "Flaky Sandstone Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.fSandstoneBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), mod_InfiTools.flintRod
                });
        mod_BalkonInfiWeapons.brSandstoneBattleaxe.setIconCoord(10, 7);
        ModLoader.addName(mod_BalkonInfiWeapons.brSandstoneBattleaxe, "Baked Sandstone Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.brSandstoneBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), mod_InfiTools.brickRod
                });
        mod_BalkonInfiWeapons.wBoneBattleaxe.setIconCoord(0, 8);
        ModLoader.addName(mod_BalkonInfiWeapons.wBoneBattleaxe, "Bone Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.wBoneBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Item.bone, Character.valueOf('|'), Item.stick
                });
        mod_BalkonInfiWeapons.stBoneBattleaxe.setIconCoord(1, 8);
        ModLoader.addName(mod_BalkonInfiWeapons.stBoneBattleaxe, "Stony Bone Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.stBoneBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_BalkonInfiWeapons.iBoneBattleaxe.setIconCoord(2, 8);
        ModLoader.addName(mod_BalkonInfiWeapons.iBoneBattleaxe, "Hard Bone Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.iBoneBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.ironRod
                });
        mod_BalkonInfiWeapons.dBoneBattleaxe.setIconCoord(3, 8);
        ModLoader.addName(mod_BalkonInfiWeapons.dBoneBattleaxe, "Jeweled Bone Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.dBoneBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.diamondRod
                });
        mod_BalkonInfiWeapons.rBoneBattleaxe.setIconCoord(4, 8);
        ModLoader.addName(mod_BalkonInfiWeapons.rBoneBattleaxe, "Red Bone Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.rBoneBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.redstoneRod
                });
        mod_BalkonInfiWeapons.oBoneBattleaxe.setIconCoord(5, 8);
        ModLoader.addName(mod_BalkonInfiWeapons.oBoneBattleaxe, "Glassy Bone Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.oBoneBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.obsidianRod
                });
        mod_BalkonInfiWeapons.bBoneBattleaxe.setIconCoord(6, 8);
        ModLoader.addName(mod_BalkonInfiWeapons.bBoneBattleaxe, "Reanimated Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bBoneBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Item.bone, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bBoneBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_BalkonInfiWeapons.mBoneBattleaxe.setIconCoord(7, 8);
        ModLoader.addName(mod_BalkonInfiWeapons.mBoneBattleaxe, "Mossy Bone Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.mBoneBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.mossyRod
                });
        mod_BalkonInfiWeapons.nBoneBattleaxe.setIconCoord(8, 8);
        ModLoader.addName(mod_BalkonInfiWeapons.nBoneBattleaxe, "Netherrack Bone Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.nBoneBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_BalkonInfiWeapons.glBoneBattleaxe.setIconCoord(9, 8);
        ModLoader.addName(mod_BalkonInfiWeapons.glBoneBattleaxe, "Glowstone Bone Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.glBoneBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_BalkonInfiWeapons.sBoneBattleaxe.setIconCoord(10, 8);
        ModLoader.addName(mod_BalkonInfiWeapons.sBoneBattleaxe, "Slimy Bone Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.sBoneBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_BalkonInfiWeapons.cBoneBattleaxe.setIconCoord(11, 8);
        ModLoader.addName(mod_BalkonInfiWeapons.cBoneBattleaxe, "Spiny Bone Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.cBoneBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_BalkonInfiWeapons.fBoneBattleaxe.setIconCoord(12, 8);
        ModLoader.addName(mod_BalkonInfiWeapons.fBoneBattleaxe, "Flaky Bone Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.fBoneBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.flintRod
                });
        mod_BalkonInfiWeapons.brBoneBattleaxe.setIconCoord(13, 8);
        ModLoader.addName(mod_BalkonInfiWeapons.brBoneBattleaxe, "Baked Bone Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.brBoneBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.brickRod
                });
        mod_BalkonInfiWeapons.blBoneBattleaxe.setIconCoord(14, 8);
        ModLoader.addName(mod_BalkonInfiWeapons.blBoneBattleaxe, "Blazing Bone Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.blBoneBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Item.bone, Character.valueOf('|'), Item.blazeRod
                });
        mod_BalkonInfiWeapons.wPaperBattleaxe.setIconCoord(0, 9);
        ModLoader.addName(mod_BalkonInfiWeapons.wPaperBattleaxe, "Paper Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.wPaperBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), mod_InfiTools.paperStack, Character.valueOf('|'), Item.stick
                });
        mod_BalkonInfiWeapons.saPaperBattleaxe.setIconCoord(1, 9);
        ModLoader.addName(mod_BalkonInfiWeapons.saPaperBattleaxe, "Stony Paper Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.saPaperBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), mod_InfiTools.paperStack, Character.valueOf('|'), mod_InfiTools.sandstoneRod
                });
        mod_BalkonInfiWeapons.bPaperBattleaxe.setIconCoord(2, 9);
        ModLoader.addName(mod_BalkonInfiWeapons.bPaperBattleaxe, "Necrotic Paper Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bPaperBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), mod_InfiTools.paperStack, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bPaperBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), mod_InfiTools.paperStack, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_BalkonInfiWeapons.pPaperBattleaxe.setIconCoord(3, 9);
        ModLoader.addName(mod_BalkonInfiWeapons.pPaperBattleaxe, "Flimsy Whacker");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.pPaperBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), mod_InfiTools.paperStack, Character.valueOf('|'), mod_InfiTools.paperRod
                });
        mod_BalkonInfiWeapons.sPaperBattleaxe.setIconCoord(4, 9);
        ModLoader.addName(mod_BalkonInfiWeapons.sPaperBattleaxe, "Slimy Paper Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.sPaperBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), mod_InfiTools.paperStack, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_BalkonInfiWeapons.cPaperBattleaxe.setIconCoord(5, 9);
        ModLoader.addName(mod_BalkonInfiWeapons.cPaperBattleaxe, "Spiny Paper Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.cPaperBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), mod_InfiTools.paperStack, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_BalkonInfiWeapons.brPaperBattleaxe.setIconCoord(6, 9);
        ModLoader.addName(mod_BalkonInfiWeapons.brPaperBattleaxe, "Baked Paper Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.brPaperBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), mod_InfiTools.paperStack, Character.valueOf('|'), mod_InfiTools.brickRod
                });
        mod_BalkonInfiWeapons.stMossyBattleaxe.setIconCoord(0, 10);
        ModLoader.addName(mod_BalkonInfiWeapons.stMossyBattleaxe, "Stony Moss-Covered Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.stMossyBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), mod_InfiTools.mossBallCrafted, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_BalkonInfiWeapons.dMossyBattleaxe.setIconCoord(1, 10);
        ModLoader.addName(mod_BalkonInfiWeapons.dMossyBattleaxe, "Jeweled Moss-Covered Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.dMossyBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), mod_InfiTools.mossBallCrafted, Character.valueOf('|'), mod_InfiTools.diamondRod
                });
        mod_BalkonInfiWeapons.rMossyBattleaxe.setIconCoord(2, 10);
        ModLoader.addName(mod_BalkonInfiWeapons.rMossyBattleaxe, "Red Moss-Covered Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.rMossyBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), mod_InfiTools.mossBallCrafted, Character.valueOf('|'), mod_InfiTools.redstoneRod
                });
        mod_BalkonInfiWeapons.bMossyBattleaxe.setIconCoord(3, 10);
        ModLoader.addName(mod_BalkonInfiWeapons.bMossyBattleaxe, "Necrotic Moss-Covered Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bMossyBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), mod_InfiTools.mossBallCrafted, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bMossyBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), mod_InfiTools.mossBallCrafted, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_BalkonInfiWeapons.mMossyBattleaxe.setIconCoord(4, 10);
        ModLoader.addName(mod_BalkonInfiWeapons.mMossyBattleaxe, "Living Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.mMossyBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), mod_InfiTools.mossBallCrafted, Character.valueOf('|'), mod_InfiTools.mossyRod
                });
        mod_BalkonInfiWeapons.glMossyBattleaxe.setIconCoord(5, 10);
        ModLoader.addName(mod_BalkonInfiWeapons.glMossyBattleaxe, "Glowing Moss-Covered Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.glMossyBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), mod_InfiTools.mossBallCrafted, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_BalkonInfiWeapons.wNetherrackBattleaxe.setIconCoord(0, 11);
        ModLoader.addName(mod_BalkonInfiWeapons.wNetherrackBattleaxe, "Netherrack Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.wNetherrackBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), Item.stick
                });
        mod_BalkonInfiWeapons.stNetherrackBattleaxe.setIconCoord(1, 11);
        ModLoader.addName(mod_BalkonInfiWeapons.stNetherrackBattleaxe, "Stony Netherrack Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.stNetherrackBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_BalkonInfiWeapons.iNetherrackBattleaxe.setIconCoord(2, 11);
        ModLoader.addName(mod_BalkonInfiWeapons.iNetherrackBattleaxe, "Hard Netherrack Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.iNetherrackBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.ironRod
                });
        mod_BalkonInfiWeapons.rNetherrackBattleaxe.setIconCoord(3, 11);
        ModLoader.addName(mod_BalkonInfiWeapons.rNetherrackBattleaxe, "Red Netherrack Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.rNetherrackBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.redstoneRod
                });
        mod_BalkonInfiWeapons.oNetherrackBattleaxe.setIconCoord(4, 11);
        ModLoader.addName(mod_BalkonInfiWeapons.oNetherrackBattleaxe, "Glassy Netherrack Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.oNetherrackBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.obsidianRod
                });
        mod_BalkonInfiWeapons.saNetherrackBattleaxe.setIconCoord(5, 11);
        ModLoader.addName(mod_BalkonInfiWeapons.saNetherrackBattleaxe, "Sandy Netherrack Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.saNetherrackBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.sandstoneRod
                });
        mod_BalkonInfiWeapons.bNetherrackBattleaxe.setIconCoord(6, 11);
        ModLoader.addName(mod_BalkonInfiWeapons.bNetherrackBattleaxe, "Necrotic Netherrack Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bNetherrackBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bNetherrackBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_BalkonInfiWeapons.mNetherrackBattleaxe.setIconCoord(7, 11);
        ModLoader.addName(mod_BalkonInfiWeapons.mNetherrackBattleaxe, "Mossy Netherrack Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.mNetherrackBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.mossyRod
                });
        mod_BalkonInfiWeapons.nNetherrackBattleaxe.setIconCoord(8, 11);
        ModLoader.addName(mod_BalkonInfiWeapons.nNetherrackBattleaxe, "Bloody Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.nNetherrackBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_BalkonInfiWeapons.glNetherrackBattleaxe.setIconCoord(9, 11);
        ModLoader.addName(mod_BalkonInfiWeapons.glNetherrackBattleaxe, "Glowing Netherrack Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.glNetherrackBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_BalkonInfiWeapons.iceNetherrackBattleaxe.setIconCoord(10, 11);
        ModLoader.addName(mod_BalkonInfiWeapons.iceNetherrackBattleaxe, "Icy Netherrack Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.iceNetherrackBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.iceRod
                });
        mod_BalkonInfiWeapons.sNetherrackBattleaxe.setIconCoord(11, 11);
        ModLoader.addName(mod_BalkonInfiWeapons.sNetherrackBattleaxe, "Slimy Netherrack Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.sNetherrackBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_BalkonInfiWeapons.cNetherrackBattleaxe.setIconCoord(12, 11);
        ModLoader.addName(mod_BalkonInfiWeapons.cNetherrackBattleaxe, "Spiny Netherrack Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.cNetherrackBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_BalkonInfiWeapons.fNetherrackBattleaxe.setIconCoord(13, 11);
        ModLoader.addName(mod_BalkonInfiWeapons.fNetherrackBattleaxe, "Flaky Netherrack Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.fNetherrackBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_BalkonInfiWeapons.brNetherrackBattleaxe.setIconCoord(14, 11);
        ModLoader.addName(mod_BalkonInfiWeapons.brNetherrackBattleaxe, "Baked Netherrack Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.brNetherrackBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_BalkonInfiWeapons.blNetherrackBattleaxe.setIconCoord(15, 11);
        ModLoader.addName(mod_BalkonInfiWeapons.blNetherrackBattleaxe, "Blazing Netherrack Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.blNetherrackBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_BalkonInfiWeapons.wGlowstoneBattleaxe.setIconCoord(0, 12);
        ModLoader.addName(mod_BalkonInfiWeapons.wGlowstoneBattleaxe, "Glowstone Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.wGlowstoneBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), Item.stick
                });
        mod_BalkonInfiWeapons.stGlowstoneBattleaxe.setIconCoord(1, 12);
        ModLoader.addName(mod_BalkonInfiWeapons.stGlowstoneBattleaxe, "Stony Glowstone Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.stGlowstoneBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_BalkonInfiWeapons.iGlowstoneBattleaxe.setIconCoord(2, 12);
        ModLoader.addName(mod_BalkonInfiWeapons.iGlowstoneBattleaxe, "Hard Glowstone Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.iGlowstoneBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.ironRod
                });
        mod_BalkonInfiWeapons.dGlowstoneBattleaxe.setIconCoord(3, 12);
        ModLoader.addName(mod_BalkonInfiWeapons.dGlowstoneBattleaxe, "Jeweled Glowstone Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.dGlowstoneBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.diamondRod
                });
        mod_BalkonInfiWeapons.rGlowstoneBattleaxe.setIconCoord(4, 12);
        ModLoader.addName(mod_BalkonInfiWeapons.rGlowstoneBattleaxe, "Red Glowstone Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.rGlowstoneBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.redstoneRod
                });
        mod_BalkonInfiWeapons.oGlowstoneBattleaxe.setIconCoord(5, 12);
        ModLoader.addName(mod_BalkonInfiWeapons.oGlowstoneBattleaxe, "Glassy Glowstone Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.oGlowstoneBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.obsidianRod
                });
        mod_BalkonInfiWeapons.bGlowstoneBattleaxe.setIconCoord(6, 12);
        ModLoader.addName(mod_BalkonInfiWeapons.bGlowstoneBattleaxe, "Necrotic Glowstone Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bGlowstoneBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bGlowstoneBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_BalkonInfiWeapons.mGlowstoneBattleaxe.setIconCoord(7, 12);
        ModLoader.addName(mod_BalkonInfiWeapons.mGlowstoneBattleaxe, "Mossy Glowstone Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.mGlowstoneBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.mossyRod
                });
        mod_BalkonInfiWeapons.nGlowstoneBattleaxe.setIconCoord(8, 12);
        ModLoader.addName(mod_BalkonInfiWeapons.nGlowstoneBattleaxe, "Bloody Glowstone Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.nGlowstoneBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_BalkonInfiWeapons.glGlowstoneBattleaxe.setIconCoord(9, 12);
        ModLoader.addName(mod_BalkonInfiWeapons.glGlowstoneBattleaxe, "Bright Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.glGlowstoneBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_BalkonInfiWeapons.iceGlowstoneBattleaxe.setIconCoord(10, 12);
        ModLoader.addName(mod_BalkonInfiWeapons.iceGlowstoneBattleaxe, "Icy Glowstone Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.iceGlowstoneBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.iceRod
                });
        mod_BalkonInfiWeapons.lGlowstoneBattleaxe.setIconCoord(11, 12);
        ModLoader.addName(mod_BalkonInfiWeapons.lGlowstoneBattleaxe, "Fiery Glowstone Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.lGlowstoneBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_BalkonInfiWeapons.sGlowstoneBattleaxe.setIconCoord(12, 12);
        ModLoader.addName(mod_BalkonInfiWeapons.sGlowstoneBattleaxe, "Slimy Glowstone Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.sGlowstoneBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_BalkonInfiWeapons.blGlowstoneBattleaxe.setIconCoord(13, 12);
        ModLoader.addName(mod_BalkonInfiWeapons.blGlowstoneBattleaxe, "Blazing Glowstone Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.blGlowstoneBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), Item.blazeRod
                });
        mod_BalkonInfiWeapons.wIceBattleaxe.setIconCoord(0, 13);
        ModLoader.addName(mod_BalkonInfiWeapons.wIceBattleaxe, "Ice Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.wIceBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Block.ice, Character.valueOf('|'), Item.stick
                });
        mod_BalkonInfiWeapons.stIceBattleaxe.setIconCoord(1, 13);
        ModLoader.addName(mod_BalkonInfiWeapons.stIceBattleaxe, "Stony Ice Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.stIceBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_BalkonInfiWeapons.iIceBattleaxe.setIconCoord(2, 13);
        ModLoader.addName(mod_BalkonInfiWeapons.iIceBattleaxe, "Hard Ice Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.iIceBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.ironRod
                });
        mod_BalkonInfiWeapons.dIceBattleaxe.setIconCoord(3, 13);
        ModLoader.addName(mod_BalkonInfiWeapons.dIceBattleaxe, "Jeweled Ice Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.dIceBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.diamondRod
                });
        mod_BalkonInfiWeapons.gIceBattleaxe.setIconCoord(4, 13);
        ModLoader.addName(mod_BalkonInfiWeapons.gIceBattleaxe, "Expensive Ice Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.gIceBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.goldRod
                });
        mod_BalkonInfiWeapons.rIceBattleaxe.setIconCoord(5, 13);
        ModLoader.addName(mod_BalkonInfiWeapons.rIceBattleaxe, "Red Ice Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.rIceBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.redstoneRod
                });
        mod_BalkonInfiWeapons.oIceBattleaxe.setIconCoord(6, 13);
        ModLoader.addName(mod_BalkonInfiWeapons.oIceBattleaxe, "Glassy Ice Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.oIceBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.obsidianRod
                });
        mod_BalkonInfiWeapons.saIceBattleaxe.setIconCoord(7, 13);
        ModLoader.addName(mod_BalkonInfiWeapons.saIceBattleaxe, "Sandy Ice Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.saIceBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.sandstoneRod
                });
        mod_BalkonInfiWeapons.bIceBattleaxe.setIconCoord(8, 13);
        ModLoader.addName(mod_BalkonInfiWeapons.bIceBattleaxe, "Necrotic Ice Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bIceBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Block.ice, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bIceBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_BalkonInfiWeapons.glIceBattleaxe.setIconCoord(9, 13);
        ModLoader.addName(mod_BalkonInfiWeapons.glIceBattleaxe, "Glowing Ice Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.glIceBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_BalkonInfiWeapons.iceIceBattleaxe.setIconCoord(10, 13);
        ModLoader.addName(mod_BalkonInfiWeapons.iceIceBattleaxe, "Frosted Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.iceIceBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.iceRod
                });
        mod_BalkonInfiWeapons.sIceBattleaxe.setIconCoord(11, 13);
        ModLoader.addName(mod_BalkonInfiWeapons.sIceBattleaxe, "Slimy Ice Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.sIceBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_BalkonInfiWeapons.cIceBattleaxe.setIconCoord(12, 13);
        ModLoader.addName(mod_BalkonInfiWeapons.cIceBattleaxe, "Spiny Ice Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.cIceBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_BalkonInfiWeapons.fIceBattleaxe.setIconCoord(13, 13);
        ModLoader.addName(mod_BalkonInfiWeapons.fIceBattleaxe, "Flaky Ice Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.fIceBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.flintRod
                });
        mod_BalkonInfiWeapons.brIceBattleaxe.setIconCoord(14, 13);
        ModLoader.addName(mod_BalkonInfiWeapons.brIceBattleaxe, "Baked Ice Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.brIceBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.brickRod
                });
        mod_BalkonInfiWeapons.dLavaBattleaxe.setIconCoord(0, 14);
        ModLoader.addName(mod_BalkonInfiWeapons.dLavaBattleaxe, "Jeweled Lava Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.dLavaBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), mod_InfiTools.lavaCrystal, Character.valueOf('|'), mod_InfiTools.diamondRod
                });
        mod_BalkonInfiWeapons.rLavaBattleaxe.setIconCoord(1, 14);
        ModLoader.addName(mod_BalkonInfiWeapons.rLavaBattleaxe, "Red Lava Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.rLavaBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), mod_InfiTools.lavaCrystal, Character.valueOf('|'), mod_InfiTools.redstoneRod
                });
        mod_BalkonInfiWeapons.bLavaBattleaxe.setIconCoord(2, 14);
        ModLoader.addName(mod_BalkonInfiWeapons.bLavaBattleaxe, "Necrotic Lava Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bLavaBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), mod_InfiTools.lavaCrystal, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bLavaBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), mod_InfiTools.lavaCrystal, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_BalkonInfiWeapons.nLavaBattleaxe.setIconCoord(3, 14);
        ModLoader.addName(mod_BalkonInfiWeapons.nLavaBattleaxe, "Bloody Lava Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.nLavaBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), mod_InfiTools.lavaCrystal, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_BalkonInfiWeapons.glLavaBattleaxe.setIconCoord(4, 14);
        ModLoader.addName(mod_BalkonInfiWeapons.glLavaBattleaxe, "Glowing Lava Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.glLavaBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), mod_InfiTools.lavaCrystal, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_BalkonInfiWeapons.lLavaBattleaxe.setIconCoord(5, 14);
        ModLoader.addName(mod_BalkonInfiWeapons.lLavaBattleaxe, "Lavascar");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.lLavaBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), mod_InfiTools.lavaCrystal, Character.valueOf('|'), mod_InfiTools.lavaRod
                });
        mod_BalkonInfiWeapons.blLavaBattleaxe.setIconCoord(6, 14);
        ModLoader.addName(mod_BalkonInfiWeapons.blLavaBattleaxe, "Blazing Lava Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.blLavaBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), mod_InfiTools.lavaCrystal, Character.valueOf('|'), Item.blazeRod
                });
        mod_BalkonInfiWeapons.wSlimeBattleaxe.setIconCoord(0, 15);
        ModLoader.addName(mod_BalkonInfiWeapons.wSlimeBattleaxe, "Slime Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.wSlimeBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), Item.stick
                });
        mod_BalkonInfiWeapons.stSlimeBattleaxe.setIconCoord(1, 15);
        ModLoader.addName(mod_BalkonInfiWeapons.stSlimeBattleaxe, "Stony Slime Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.stSlimeBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_BalkonInfiWeapons.iSlimeBattleaxe.setIconCoord(2, 15);
        ModLoader.addName(mod_BalkonInfiWeapons.iSlimeBattleaxe, "Hard Slime Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.iSlimeBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.ironRod
                });
        mod_BalkonInfiWeapons.dSlimeBattleaxe.setIconCoord(3, 15);
        ModLoader.addName(mod_BalkonInfiWeapons.dSlimeBattleaxe, "Jeweled Slime Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.dSlimeBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.diamondRod
                });
        mod_BalkonInfiWeapons.gSlimeBattleaxe.setIconCoord(4, 15);
        ModLoader.addName(mod_BalkonInfiWeapons.gSlimeBattleaxe, "Expensive Slime Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.gSlimeBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.goldRod
                });
        mod_BalkonInfiWeapons.rSlimeBattleaxe.setIconCoord(5, 15);
        ModLoader.addName(mod_BalkonInfiWeapons.rSlimeBattleaxe, "Red Slime Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.rSlimeBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.redstoneRod
                });
        mod_BalkonInfiWeapons.oSlimeBattleaxe.setIconCoord(6, 15);
        ModLoader.addName(mod_BalkonInfiWeapons.oSlimeBattleaxe, "Glassy Slime Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.oSlimeBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.obsidianRod
                });
        mod_BalkonInfiWeapons.saSlimeBattleaxe.setIconCoord(7, 15);
        ModLoader.addName(mod_BalkonInfiWeapons.saSlimeBattleaxe, "Sandy Slime Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.saSlimeBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.sandstoneRod
                });
        mod_BalkonInfiWeapons.bSlimeBattleaxe.setIconCoord(8, 15);
        ModLoader.addName(mod_BalkonInfiWeapons.bSlimeBattleaxe, "Necrotic Slime Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bSlimeBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bSlimeBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_BalkonInfiWeapons.pSlimeBattleaxe.setIconCoord(9, 15);
        ModLoader.addName(mod_BalkonInfiWeapons.pSlimeBattleaxe, "Fibery Slime Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.pSlimeBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.paperRod
                });
        mod_BalkonInfiWeapons.mSlimeBattleaxe.setIconCoord(10, 15);
        ModLoader.addName(mod_BalkonInfiWeapons.mSlimeBattleaxe, "Mossy Slime Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.mSlimeBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.mossyRod
                });
        mod_BalkonInfiWeapons.nSlimeBattleaxe.setIconCoord(11, 15);
        ModLoader.addName(mod_BalkonInfiWeapons.nSlimeBattleaxe, "Bloody Slime Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.nSlimeBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_BalkonInfiWeapons.glSlimeBattleaxe.setIconCoord(12, 15);
        ModLoader.addName(mod_BalkonInfiWeapons.glSlimeBattleaxe, "Glowing Slime Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.glSlimeBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_BalkonInfiWeapons.iceSlimeBattleaxe.setIconCoord(13, 15);
        ModLoader.addName(mod_BalkonInfiWeapons.iceSlimeBattleaxe, "Icy Slime Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.iceSlimeBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.iceRod
                });
        mod_BalkonInfiWeapons.lSlimeBattleaxe.setIconCoord(14, 15);
        ModLoader.addName(mod_BalkonInfiWeapons.lSlimeBattleaxe, "Fiery Slime Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.lSlimeBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.lavaRod
                });
        mod_BalkonInfiWeapons.sSlimeBattleaxe.setIconCoord(15, 15);
        ModLoader.addName(mod_BalkonInfiWeapons.sSlimeBattleaxe, "Green Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.sSlimeBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_BalkonInfiWeapons.cSlimeBattleaxe.setIconCoord(12, 14);
        ModLoader.addName(mod_BalkonInfiWeapons.cSlimeBattleaxe, "Spiny Slime Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.cSlimeBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_BalkonInfiWeapons.fSlimeBattleaxe.setIconCoord(13, 14);
        ModLoader.addName(mod_BalkonInfiWeapons.fSlimeBattleaxe, "Flaky Slime Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.fSlimeBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.flintRod
                });
        mod_BalkonInfiWeapons.brSlimeBattleaxe.setIconCoord(14, 14);
        ModLoader.addName(mod_BalkonInfiWeapons.brSlimeBattleaxe, "Baked Slime Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.brSlimeBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.brickRod
                });
        mod_BalkonInfiWeapons.blSlimeBattleaxe.setIconCoord(15, 14);
        ModLoader.addName(mod_BalkonInfiWeapons.blSlimeBattleaxe, "Blazing Slime Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.blSlimeBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), Item.blazeRod
                });
        mod_BalkonInfiWeapons.wCactusBattleaxe.setIconCoord(10, 0);
        ModLoader.addName(mod_BalkonInfiWeapons.wCactusBattleaxe, "Cactus Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.wCactusBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Block.cactus, Character.valueOf('|'), Item.stick
                });
        mod_BalkonInfiWeapons.stCactusBattleaxe.setIconCoord(11, 0);
        ModLoader.addName(mod_BalkonInfiWeapons.stCactusBattleaxe, "Stony Cactus Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.stCactusBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Block.cactus, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_BalkonInfiWeapons.saCactusBattleaxe.setIconCoord(12, 0);
        ModLoader.addName(mod_BalkonInfiWeapons.saCactusBattleaxe, "Sandy Cactus Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.saCactusBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Block.cactus, Character.valueOf('|'), mod_InfiTools.sandstoneRod
                });
        mod_BalkonInfiWeapons.bCactusBattleaxe.setIconCoord(13, 0);
        ModLoader.addName(mod_BalkonInfiWeapons.bCactusBattleaxe, "Necrotic Cactus Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bCactusBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Block.cactus, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bCactusBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Block.cactus, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_BalkonInfiWeapons.pCactusBattleaxe.setIconCoord(14, 0);
        ModLoader.addName(mod_BalkonInfiWeapons.pCactusBattleaxe, "Fibery Cactus Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.pCactusBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Block.cactus, Character.valueOf('|'), mod_InfiTools.paperRod
                });
        mod_BalkonInfiWeapons.nCactusBattleaxe.setIconCoord(15, 0);
        ModLoader.addName(mod_BalkonInfiWeapons.nCactusBattleaxe, "Bloody Cactus Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.nCactusBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Block.cactus, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_BalkonInfiWeapons.sCactusBattleaxe.setIconCoord(12, 1);
        ModLoader.addName(mod_BalkonInfiWeapons.sCactusBattleaxe, "Slimy Cactus Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.sCactusBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Block.cactus, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_BalkonInfiWeapons.cCactusBattleaxe.setIconCoord(13, 1);
        ModLoader.addName(mod_BalkonInfiWeapons.cCactusBattleaxe, "Spined Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.cCactusBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Block.cactus, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_BalkonInfiWeapons.fCactusBattleaxe.setIconCoord(14, 1);
        ModLoader.addName(mod_BalkonInfiWeapons.fCactusBattleaxe, "Flaky Cactus Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.fCactusBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Block.cactus, Character.valueOf('|'), mod_InfiTools.flintRod
                });
        mod_BalkonInfiWeapons.brCactusBattleaxe.setIconCoord(15, 1);
        ModLoader.addName(mod_BalkonInfiWeapons.brCactusBattleaxe, "Baked Cactus Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.brCactusBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Block.cactus, Character.valueOf('|'), mod_InfiTools.brickRod
                });
        mod_BalkonInfiWeapons.wFlintBattleaxe.setIconCoord(13, 2);
        ModLoader.addName(mod_BalkonInfiWeapons.wFlintBattleaxe, "Flint Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.wFlintBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Item.flint, Character.valueOf('|'), Item.stick
                });
        mod_BalkonInfiWeapons.stFlintBattleaxe.setIconCoord(14, 2);
        ModLoader.addName(mod_BalkonInfiWeapons.stFlintBattleaxe, "Stony Flint Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.stFlintBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_BalkonInfiWeapons.iFlintBattleaxe.setIconCoord(15, 2);
        ModLoader.addName(mod_BalkonInfiWeapons.iFlintBattleaxe, "Hard Flint Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.iFlintBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.ironRod
                });
        mod_BalkonInfiWeapons.gFlintBattleaxe.setIconCoord(12, 3);
        ModLoader.addName(mod_BalkonInfiWeapons.gFlintBattleaxe, "Expensive Flint Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.gFlintBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.goldRod
                });
        mod_BalkonInfiWeapons.oFlintBattleaxe.setIconCoord(13, 3);
        ModLoader.addName(mod_BalkonInfiWeapons.oFlintBattleaxe, "Glassy Flint Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.oFlintBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.obsidianRod
                });
        mod_BalkonInfiWeapons.saFlintBattleaxe.setIconCoord(14, 3);
        ModLoader.addName(mod_BalkonInfiWeapons.saFlintBattleaxe, "Sandy Flint Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.saFlintBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.sandstoneRod
                });
        mod_BalkonInfiWeapons.bFlintBattleaxe.setIconCoord(15, 3);
        ModLoader.addName(mod_BalkonInfiWeapons.bFlintBattleaxe, "Necrotic Flint Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bFlintBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Item.flint, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bFlintBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_BalkonInfiWeapons.nFlintBattleaxe.setIconCoord(12, 4);
        ModLoader.addName(mod_BalkonInfiWeapons.nFlintBattleaxe, "Bloody Flint Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.nFlintBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_BalkonInfiWeapons.iceFlintBattleaxe.setIconCoord(13, 4);
        ModLoader.addName(mod_BalkonInfiWeapons.iceFlintBattleaxe, "Icy Flint Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.iceFlintBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.iceRod
                });
        mod_BalkonInfiWeapons.sFlintBattleaxe.setIconCoord(14, 4);
        ModLoader.addName(mod_BalkonInfiWeapons.sFlintBattleaxe, "Slimy Flint Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.sFlintBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_BalkonInfiWeapons.cFlintBattleaxe.setIconCoord(15, 4);
        ModLoader.addName(mod_BalkonInfiWeapons.cFlintBattleaxe, "Spiny Flint Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.cFlintBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_BalkonInfiWeapons.fFlintBattleaxe.setIconCoord(11, 5);
        ModLoader.addName(mod_BalkonInfiWeapons.fFlintBattleaxe, "Rough-hewn Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.fFlintBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.flintRod
                });
        mod_BalkonInfiWeapons.brFlintBattleaxe.setIconCoord(12, 5);
        ModLoader.addName(mod_BalkonInfiWeapons.brFlintBattleaxe, "Baked Flint Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.brFlintBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.brickRod
                });
        mod_BalkonInfiWeapons.blFlintBattleaxe.setIconCoord(13, 5);
        ModLoader.addName(mod_BalkonInfiWeapons.blFlintBattleaxe, "Blazing Flint Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.blFlintBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Item.flint, Character.valueOf('|'), Item.blazeRod
                });
        mod_BalkonInfiWeapons.wBrickBattleaxe.setIconCoord(14, 5);
        ModLoader.addName(mod_BalkonInfiWeapons.wBrickBattleaxe, "Brick Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.wBrickBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Item.brick, Character.valueOf('|'), Item.brick
                });
        mod_BalkonInfiWeapons.stBrickBattleaxe.setIconCoord(15, 5);
        ModLoader.addName(mod_BalkonInfiWeapons.stBrickBattleaxe, "Stony Brick Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.stBrickBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Item.brick, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_BalkonInfiWeapons.saBrickBattleaxe.setIconCoord(13, 6);
        ModLoader.addName(mod_BalkonInfiWeapons.saBrickBattleaxe, "Sandstone Brick Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.saBrickBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Item.brick, Character.valueOf('|'), mod_InfiTools.sandstoneRod
                });
        mod_BalkonInfiWeapons.bBrickBattleaxe.setIconCoord(14, 6);
        ModLoader.addName(mod_BalkonInfiWeapons.bBrickBattleaxe, "Necrotic Brick Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bBrickBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Item.brick, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bBrickBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Item.brick, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_BalkonInfiWeapons.pBrickBattleaxe.setIconCoord(15, 6);
        ModLoader.addName(mod_BalkonInfiWeapons.pBrickBattleaxe, "Fibery Brick Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.pBrickBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Item.brick, Character.valueOf('|'), mod_InfiTools.paperRod
                });
        mod_BalkonInfiWeapons.nBrickBattleaxe.setIconCoord(11, 7);
        ModLoader.addName(mod_BalkonInfiWeapons.nBrickBattleaxe, "Bloody Brick Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.nBrickBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Item.brick, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_BalkonInfiWeapons.iceBrickBattleaxe.setIconCoord(12, 7);
        ModLoader.addName(mod_BalkonInfiWeapons.iceBrickBattleaxe, "Icy Brick Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.iceBrickBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Item.brick, Character.valueOf('|'), mod_InfiTools.iceRod
                });
        mod_BalkonInfiWeapons.sBrickBattleaxe.setIconCoord(13, 7);
        ModLoader.addName(mod_BalkonInfiWeapons.sBrickBattleaxe, "Slimy Brick Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.sBrickBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Item.brick, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_BalkonInfiWeapons.cBrickBattleaxe.setIconCoord(14, 7);
        ModLoader.addName(mod_BalkonInfiWeapons.cBrickBattleaxe, "Spiny Brick Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.cBrickBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Item.brick, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_BalkonInfiWeapons.fBrickBattleaxe.setIconCoord(15, 7);
        ModLoader.addName(mod_BalkonInfiWeapons.fBrickBattleaxe, "Flaky Brick Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.fBrickBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Item.brick, Character.valueOf('|'), mod_InfiTools.flintRod
                });
        mod_BalkonInfiWeapons.brBrickBattleaxe.setIconCoord(15, 8);
        ModLoader.addName(mod_BalkonInfiWeapons.brBrickBattleaxe, "Look-alike Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.brBrickBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), Item.brick, Character.valueOf('|'), mod_InfiTools.brickRod
                });
        mod_BalkonInfiWeapons.dBlazeBattleaxe.setIconCoord(7, 9);
        ModLoader.addName(mod_BalkonInfiWeapons.dBlazeBattleaxe, "Jeweled Blaze Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.dBlazeBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), mod_InfiTools.blazeCrystal, Character.valueOf('|'), mod_InfiTools.diamondRod
                });
        mod_BalkonInfiWeapons.rBlazeBattleaxe.setIconCoord(8, 9);
        ModLoader.addName(mod_BalkonInfiWeapons.rBlazeBattleaxe, "Red Blaze Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.rBlazeBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), mod_InfiTools.blazeCrystal, Character.valueOf('|'), mod_InfiTools.redstoneRod
                });
        mod_BalkonInfiWeapons.bBlazeBattleaxe.setIconCoord(9, 9);
        ModLoader.addName(mod_BalkonInfiWeapons.bBlazeBattleaxe, "Necrotic Blaze Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bBlazeBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), mod_InfiTools.blazeCrystal, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bBlazeBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), mod_InfiTools.blazeCrystal, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_BalkonInfiWeapons.nBlazeBattleaxe.setIconCoord(10, 9);
        ModLoader.addName(mod_BalkonInfiWeapons.nBlazeBattleaxe, "Bloody Blaze Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.nBlazeBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), mod_InfiTools.blazeCrystal, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_BalkonInfiWeapons.glBlazeBattleaxe.setIconCoord(11, 9);
        ModLoader.addName(mod_BalkonInfiWeapons.glBlazeBattleaxe, "Glowing Blaze Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.glBlazeBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), mod_InfiTools.blazeCrystal, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_BalkonInfiWeapons.lBlazeBattleaxe.setIconCoord(12, 9);
        ModLoader.addName(mod_BalkonInfiWeapons.lBlazeBattleaxe, "Fiery Blaze Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.lBlazeBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), mod_InfiTools.blazeCrystal, Character.valueOf('|'), Item.blazeRod
                });
        mod_BalkonInfiWeapons.fBlazeBattleaxe.setIconCoord(13, 9);
        ModLoader.addName(mod_BalkonInfiWeapons.fBlazeBattleaxe, "Flaky Blaze Battleaxe");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.fBlazeBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), mod_InfiTools.blazeCrystal, Character.valueOf('|'), Item.blazeRod
                });
        mod_BalkonInfiWeapons.blBlazeBattleaxe.setIconCoord(14, 9);
        ModLoader.addName(mod_BalkonInfiWeapons.blBlazeBattleaxe, "Creeper Crasher");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.blBlazeBattleaxe, 1), new Object[]
                {
                    "mmm", "m|m", " | ", Character.valueOf('m'), mod_InfiTools.blazeCrystal, Character.valueOf('|'), Item.blazeRod
                });
        return 0;
    }
}
