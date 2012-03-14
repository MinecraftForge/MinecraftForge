package net.minecraft.src.balkon;

import net.minecraft.src.*;

public class BalkRecipeKnives
{
    public BalkRecipeKnives()
    {
    }

    public static int recipeStorm()
    {
        mod_BalkonInfiWeapons.stWoodKnife.setIconCoord(1, 0);
        ModLoader.addName(mod_BalkonInfiWeapons.stWoodKnife, "Stony Wooden Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.stWoodKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Block.planks, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_BalkonInfiWeapons.saWoodKnife.setIconCoord(2, 0);
        ModLoader.addName(mod_BalkonInfiWeapons.saWoodKnife, "Sandy Wooden Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.saWoodKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Block.planks, Character.valueOf('|'), mod_InfiTools.sandstoneRod
                });
        mod_BalkonInfiWeapons.bWoodKnife.setIconCoord(3, 0);
        ModLoader.addName(mod_BalkonInfiWeapons.bWoodKnife, "Necrotic Wooden Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bWoodKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Block.planks, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bWoodKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Block.planks, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_BalkonInfiWeapons.pWoodKnife.setIconCoord(4, 0);
        ModLoader.addName(mod_BalkonInfiWeapons.pWoodKnife, "Fibery Wooden Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.pWoodKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Block.planks, Character.valueOf('|'), mod_InfiTools.paperRod
                });
        mod_BalkonInfiWeapons.nWoodKnife.setIconCoord(5, 0);
        ModLoader.addName(mod_BalkonInfiWeapons.nWoodKnife, "Bloody Wooden Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.nWoodKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Block.planks, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_BalkonInfiWeapons.sWoodKnife.setIconCoord(6, 0);
        ModLoader.addName(mod_BalkonInfiWeapons.sWoodKnife, "Slimy Wooden Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.sWoodKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Block.planks, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_BalkonInfiWeapons.cWoodKnife.setIconCoord(7, 0);
        ModLoader.addName(mod_BalkonInfiWeapons.cWoodKnife, "Spiny Wooden Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.cWoodKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Block.planks, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_BalkonInfiWeapons.fWoodKnife.setIconCoord(8, 0);
        ModLoader.addName(mod_BalkonInfiWeapons.fWoodKnife, "Flaky Wooden Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.fWoodKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Block.planks, Character.valueOf('|'), mod_InfiTools.flintRod
                });
        mod_BalkonInfiWeapons.brWoodKnife.setIconCoord(9, 0);
        ModLoader.addName(mod_BalkonInfiWeapons.brWoodKnife, "Baked Wooden Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.brWoodKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Block.planks, Character.valueOf('|'), mod_InfiTools.brickRod
                });
        mod_BalkonInfiWeapons.stStoneKnife.setIconCoord(1, 1);
        ModLoader.addName(mod_BalkonInfiWeapons.stStoneKnife, "Heavy Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.stStoneKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.stStoneKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Block.stone, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_BalkonInfiWeapons.saStoneKnife.setIconCoord(2, 1);
        ModLoader.addName(mod_BalkonInfiWeapons.saStoneKnife, "Sandy Stone Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.saStoneKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), mod_InfiTools.sandstoneRod
                });
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.saStoneKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Block.stone, Character.valueOf('|'), mod_InfiTools.sandstoneRod
                });
        mod_BalkonInfiWeapons.bStoneKnife.setIconCoord(3, 1);
        ModLoader.addName(mod_BalkonInfiWeapons.bStoneKnife, "Necrotic Stone Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bStoneKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bStoneKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Block.stone, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bStoneKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bStoneKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Block.stone, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_BalkonInfiWeapons.pStoneKnife.setIconCoord(4, 1);
        ModLoader.addName(mod_BalkonInfiWeapons.pStoneKnife, "Fibery Stone Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.pStoneKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), mod_InfiTools.paperRod
                });
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.pStoneKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Block.stone, Character.valueOf('|'), mod_InfiTools.paperRod
                });
        mod_BalkonInfiWeapons.mStoneKnife.setIconCoord(5, 1);
        ModLoader.addName(mod_BalkonInfiWeapons.mStoneKnife, "Mossy Stone Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.mStoneKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), mod_InfiTools.mossyRod
                });
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.mStoneKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Block.stone, Character.valueOf('|'), mod_InfiTools.mossyRod
                });
        mod_BalkonInfiWeapons.nStoneKnife.setIconCoord(6, 1);
        ModLoader.addName(mod_BalkonInfiWeapons.nStoneKnife, "Bloody Stone Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.nStoneKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.nStoneKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Block.stone, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_BalkonInfiWeapons.iceStoneKnife.setIconCoord(7, 1);
        ModLoader.addName(mod_BalkonInfiWeapons.iceStoneKnife, "Icy Stone Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.iceStoneKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), mod_InfiTools.iceRod
                });
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.iceStoneKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Block.stone, Character.valueOf('|'), mod_InfiTools.iceRod
                });
        mod_BalkonInfiWeapons.sStoneKnife.setIconCoord(8, 1);
        ModLoader.addName(mod_BalkonInfiWeapons.sStoneKnife, "Slimy Stone Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.sStoneKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.sStoneKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Block.stone, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_BalkonInfiWeapons.cStoneKnife.setIconCoord(9, 1);
        ModLoader.addName(mod_BalkonInfiWeapons.cStoneKnife, "Spiny Stone Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.cStoneKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.cStoneKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Block.stone, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_BalkonInfiWeapons.fStoneKnife.setIconCoord(10, 1);
        ModLoader.addName(mod_BalkonInfiWeapons.fStoneKnife, "Flaky Stone Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.fStoneKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), mod_InfiTools.flintRod
                });
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.fStoneKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Block.stone, Character.valueOf('|'), mod_InfiTools.flintRod
                });
        mod_BalkonInfiWeapons.brStoneKnife.setIconCoord(11, 1);
        ModLoader.addName(mod_BalkonInfiWeapons.brStoneKnife, "Baked Stone Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.brStoneKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), mod_InfiTools.brickRod
                });
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.brStoneKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Block.stone, Character.valueOf('|'), mod_InfiTools.brickRod
                });
        mod_BalkonInfiWeapons.stIronKnife.setIconCoord(1, 2);
        ModLoader.addName(mod_BalkonInfiWeapons.stIronKnife, "Stony Iron Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.stIronKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_BalkonInfiWeapons.iIronKnife.setIconCoord(2, 2);
        ModLoader.addName(mod_BalkonInfiWeapons.iIronKnife, "Ironic Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.iIronKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), mod_InfiTools.ironRod
                });
        mod_BalkonInfiWeapons.dIronKnife.setIconCoord(3, 2);
        ModLoader.addName(mod_BalkonInfiWeapons.dIronKnife, "Jeweled Iron Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.dIronKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), mod_InfiTools.diamondRod
                });
        mod_BalkonInfiWeapons.gIronKnife.setIconCoord(4, 2);
        ModLoader.addName(mod_BalkonInfiWeapons.gIronKnife, "Expensive Iron Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.gIronKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), mod_InfiTools.goldRod
                });
        mod_BalkonInfiWeapons.rIronKnife.setIconCoord(5, 2);
        ModLoader.addName(mod_BalkonInfiWeapons.rIronKnife, "Red Iron Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.rIronKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), mod_InfiTools.redstoneRod
                });
        mod_BalkonInfiWeapons.oIronKnife.setIconCoord(6, 2);
        ModLoader.addName(mod_BalkonInfiWeapons.oIronKnife, "Glassy Iron Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.oIronKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), mod_InfiTools.obsidianRod
                });
        mod_BalkonInfiWeapons.bIronKnife.setIconCoord(7, 2);
        ModLoader.addName(mod_BalkonInfiWeapons.bIronKnife, "Necrotic Iron Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bIronKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bIronKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_BalkonInfiWeapons.nIronKnife.setIconCoord(8, 2);
        ModLoader.addName(mod_BalkonInfiWeapons.nIronKnife, "Bloody Iron Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.nIronKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_BalkonInfiWeapons.glIronKnife.setIconCoord(9, 2);
        ModLoader.addName(mod_BalkonInfiWeapons.glIronKnife, "Glowing Iron Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.glIronKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_BalkonInfiWeapons.iceIronKnife.setIconCoord(10, 2);
        ModLoader.addName(mod_BalkonInfiWeapons.iceIronKnife, "Icy Iron Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.iceIronKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), mod_InfiTools.iceRod
                });
        mod_BalkonInfiWeapons.sIronKnife.setIconCoord(11, 2);
        ModLoader.addName(mod_BalkonInfiWeapons.sIronKnife, "Slimy Iron Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.sIronKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_BalkonInfiWeapons.blIronKnife.setIconCoord(12, 2);
        ModLoader.addName(mod_BalkonInfiWeapons.blIronKnife, "Blazing Iron Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.blIronKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), Item.blazeRod
                });
        mod_BalkonInfiWeapons.stDiamondKnife.setIconCoord(1, 3);
        ModLoader.addName(mod_BalkonInfiWeapons.stDiamondKnife, "Stony Diamond Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.stDiamondKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_BalkonInfiWeapons.iDiamondKnife.setIconCoord(2, 3);
        ModLoader.addName(mod_BalkonInfiWeapons.iDiamondKnife, "Hard Diamond Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.iDiamondKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), mod_InfiTools.ironRod
                });
        mod_BalkonInfiWeapons.dDiamondKnife.setIconCoord(3, 3);
        ModLoader.addName(mod_BalkonInfiWeapons.dDiamondKnife, "Diamondium Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.dDiamondKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), mod_InfiTools.diamondRod
                });
        mod_BalkonInfiWeapons.gDiamondKnife.setIconCoord(4, 3);
        ModLoader.addName(mod_BalkonInfiWeapons.gDiamondKnife, "Expensive Diamond Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.gDiamondKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), mod_InfiTools.goldRod
                });
        mod_BalkonInfiWeapons.rDiamondKnife.setIconCoord(5, 3);
        ModLoader.addName(mod_BalkonInfiWeapons.rDiamondKnife, "Red Diamond Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.rDiamondKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), mod_InfiTools.redstoneRod
                });
        mod_BalkonInfiWeapons.oDiamondKnife.setIconCoord(6, 3);
        ModLoader.addName(mod_BalkonInfiWeapons.oDiamondKnife, "Glassy Diamond Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.oDiamondKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), mod_InfiTools.obsidianRod
                });
        mod_BalkonInfiWeapons.bDiamondKnife.setIconCoord(7, 3);
        ModLoader.addName(mod_BalkonInfiWeapons.bDiamondKnife, "Necrotic Diamond Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bDiamondKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bDiamondKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_BalkonInfiWeapons.mDiamondKnife.setIconCoord(8, 3);
        ModLoader.addName(mod_BalkonInfiWeapons.mDiamondKnife, "Mossy Diamond Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.mDiamondKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), mod_InfiTools.mossyRod
                });
        mod_BalkonInfiWeapons.nDiamondKnife.setIconCoord(9, 3);
        ModLoader.addName(mod_BalkonInfiWeapons.nDiamondKnife, "Bloody Diamond Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.nDiamondKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_BalkonInfiWeapons.glDiamondKnife.setIconCoord(10, 3);
        ModLoader.addName(mod_BalkonInfiWeapons.glDiamondKnife, "Glowing Diamond Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.glDiamondKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_BalkonInfiWeapons.blDiamondKnife.setIconCoord(11, 3);
        ModLoader.addName(mod_BalkonInfiWeapons.blDiamondKnife, "Blazing Diamond Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.blDiamondKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), Item.blazeRod
                });
        mod_BalkonInfiWeapons.stGoldKnife.setIconCoord(1, 4);
        ModLoader.addName(mod_BalkonInfiWeapons.stGoldKnife, "Stony Gold Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.stGoldKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_BalkonInfiWeapons.gGoldKnife.setIconCoord(2, 4);
        ModLoader.addName(mod_BalkonInfiWeapons.gGoldKnife, "Expensive Useless Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.gGoldKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), mod_InfiTools.goldRod
                });
        mod_BalkonInfiWeapons.oGoldKnife.setIconCoord(3, 4);
        ModLoader.addName(mod_BalkonInfiWeapons.oGoldKnife, "Glassy Gold Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.oGoldKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), mod_InfiTools.obsidianRod
                });
        mod_BalkonInfiWeapons.saGoldKnife.setIconCoord(4, 4);
        ModLoader.addName(mod_BalkonInfiWeapons.saGoldKnife, "Sandy Gold Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.saGoldKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), mod_InfiTools.sandstoneRod
                });
        mod_BalkonInfiWeapons.bGoldKnife.setIconCoord(5, 4);
        ModLoader.addName(mod_BalkonInfiWeapons.bGoldKnife, "Necrotic Gold Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bGoldKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bGoldKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_BalkonInfiWeapons.mGoldKnife.setIconCoord(6, 4);
        ModLoader.addName(mod_BalkonInfiWeapons.mGoldKnife, "Mossy Gold Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.mGoldKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), mod_InfiTools.mossyRod
                });
        mod_BalkonInfiWeapons.nGoldKnife.setIconCoord(7, 4);
        ModLoader.addName(mod_BalkonInfiWeapons.nGoldKnife, "Bloody Gold Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.nGoldKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_BalkonInfiWeapons.glGoldKnife.setIconCoord(8, 4);
        ModLoader.addName(mod_BalkonInfiWeapons.glGoldKnife, "Glowing Gold Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.glGoldKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_BalkonInfiWeapons.iceGoldKnife.setIconCoord(9, 4);
        ModLoader.addName(mod_BalkonInfiWeapons.iceGoldKnife, "Icy Gold Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.iceGoldKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), mod_InfiTools.iceRod
                });
        mod_BalkonInfiWeapons.sGoldKnife.setIconCoord(10, 4);
        ModLoader.addName(mod_BalkonInfiWeapons.sGoldKnife, "Slimy Gold Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.sGoldKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_BalkonInfiWeapons.fGoldKnife.setIconCoord(11, 4);
        ModLoader.addName(mod_BalkonInfiWeapons.fGoldKnife, "Flaky Gold Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.fGoldKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), mod_InfiTools.flintRod
                });
        mod_BalkonInfiWeapons.wRedstoneKnife.setIconCoord(0, 5);
        ModLoader.addName(mod_BalkonInfiWeapons.wRedstoneKnife, "Redstone Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.wRedstoneKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), Item.stick
                });
        mod_BalkonInfiWeapons.stRedstoneKnife.setIconCoord(1, 5);
        ModLoader.addName(mod_BalkonInfiWeapons.stRedstoneKnife, "Stony Redstone Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.stRedstoneKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_BalkonInfiWeapons.iRedstoneKnife.setIconCoord(2, 5);
        ModLoader.addName(mod_BalkonInfiWeapons.iRedstoneKnife, "Hard Redstone Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.iRedstoneKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), mod_InfiTools.ironRod
                });
        mod_BalkonInfiWeapons.dRedstoneKnife.setIconCoord(3, 5);
        ModLoader.addName(mod_BalkonInfiWeapons.dRedstoneKnife, "Jeweled Redstone Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.dRedstoneKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), mod_InfiTools.diamondRod
                });
        mod_BalkonInfiWeapons.rRedstoneKnife.setIconCoord(4, 5);
        ModLoader.addName(mod_BalkonInfiWeapons.rRedstoneKnife, "Redredred Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.rRedstoneKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), mod_InfiTools.redstoneRod
                });
        mod_BalkonInfiWeapons.oRedstoneKnife.setIconCoord(5, 5);
        ModLoader.addName(mod_BalkonInfiWeapons.oRedstoneKnife, "Glassy Redstone Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.oRedstoneKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), mod_InfiTools.obsidianRod
                });
        mod_BalkonInfiWeapons.bRedstoneKnife.setIconCoord(6, 5);
        ModLoader.addName(mod_BalkonInfiWeapons.bRedstoneKnife, "Necrotic Redstone Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bRedstoneKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bRedstoneKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_BalkonInfiWeapons.mRedstoneKnife.setIconCoord(7, 5);
        ModLoader.addName(mod_BalkonInfiWeapons.mRedstoneKnife, "Mossy Redstone Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.mRedstoneKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), mod_InfiTools.mossyRod
                });
        mod_BalkonInfiWeapons.glRedstoneKnife.setIconCoord(8, 5);
        ModLoader.addName(mod_BalkonInfiWeapons.glRedstoneKnife, "Glowing Redstone Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.glRedstoneKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_BalkonInfiWeapons.sRedstoneKnife.setIconCoord(9, 5);
        ModLoader.addName(mod_BalkonInfiWeapons.sRedstoneKnife, "Slimy Redstone Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.sRedstoneKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_BalkonInfiWeapons.blRedstoneKnife.setIconCoord(10, 5);
        ModLoader.addName(mod_BalkonInfiWeapons.blRedstoneKnife, "Blazing Redstone Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.blRedstoneKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), Item.blazeRod
                });
        mod_BalkonInfiWeapons.wObsidianKnife.setIconCoord(0, 6);
        ModLoader.addName(mod_BalkonInfiWeapons.wObsidianKnife, "Obsidian Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.wObsidianKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), Item.stick
                });
        mod_BalkonInfiWeapons.stObsidianKnife.setIconCoord(1, 6);
        ModLoader.addName(mod_BalkonInfiWeapons.stObsidianKnife, "Stony Obsidian Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.stObsidianKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_BalkonInfiWeapons.iObsidianKnife.setIconCoord(2, 6);
        ModLoader.addName(mod_BalkonInfiWeapons.iObsidianKnife, "Hard Obsidian Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.iObsidianKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), mod_InfiTools.ironRod
                });
        mod_BalkonInfiWeapons.dObsidianKnife.setIconCoord(3, 6);
        ModLoader.addName(mod_BalkonInfiWeapons.dObsidianKnife, "Jeweled Obsidian Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.dObsidianKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), mod_InfiTools.diamondRod
                });
        mod_BalkonInfiWeapons.gObsidianKnife.setIconCoord(4, 6);
        ModLoader.addName(mod_BalkonInfiWeapons.gObsidianKnife, "Expensive Obsidian Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.gObsidianKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), mod_InfiTools.goldRod
                });
        mod_BalkonInfiWeapons.rObsidianKnife.setIconCoord(5, 6);
        ModLoader.addName(mod_BalkonInfiWeapons.rObsidianKnife, "Red Obsidian Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.rObsidianKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), mod_InfiTools.redstoneRod
                });
        mod_BalkonInfiWeapons.oObsidianKnife.setIconCoord(6, 6);
        ModLoader.addName(mod_BalkonInfiWeapons.oObsidianKnife, "Wicked Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.oObsidianKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), mod_InfiTools.obsidianRod
                });
        mod_BalkonInfiWeapons.bObsidianKnife.setIconCoord(7, 6);
        ModLoader.addName(mod_BalkonInfiWeapons.bObsidianKnife, "Necrotic Obsidian Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bObsidianKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bObsidianKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_BalkonInfiWeapons.nObsidianKnife.setIconCoord(8, 6);
        ModLoader.addName(mod_BalkonInfiWeapons.nObsidianKnife, "Bloody Obsidian Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.nObsidianKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_BalkonInfiWeapons.glObsidianKnife.setIconCoord(9, 6);
        ModLoader.addName(mod_BalkonInfiWeapons.glObsidianKnife, "Glowing Obsidian Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.glObsidianKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_BalkonInfiWeapons.sObsidianKnife.setIconCoord(10, 6);
        ModLoader.addName(mod_BalkonInfiWeapons.sObsidianKnife, "Slimy Obsidian Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.sObsidianKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_BalkonInfiWeapons.fObsidianKnife.setIconCoord(11, 6);
        ModLoader.addName(mod_BalkonInfiWeapons.fObsidianKnife, "Flaky Obsidian Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.fObsidianKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), mod_InfiTools.flintRod
                });
        mod_BalkonInfiWeapons.blObsidianKnife.setIconCoord(12, 6);
        ModLoader.addName(mod_BalkonInfiWeapons.blObsidianKnife, "Blazing Obsidian Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.blObsidianKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), Item.blazeRod
                });
        mod_BalkonInfiWeapons.wSandstoneKnife.setIconCoord(0, 7);
        ModLoader.addName(mod_BalkonInfiWeapons.wSandstoneKnife, "Sandstone Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.wSandstoneKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), Item.stick
                });
        mod_BalkonInfiWeapons.stSandstoneKnife.setIconCoord(1, 7);
        ModLoader.addName(mod_BalkonInfiWeapons.stSandstoneKnife, "Stony Sandstone Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.stSandstoneKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_BalkonInfiWeapons.saSandstoneKnife.setIconCoord(2, 7);
        ModLoader.addName(mod_BalkonInfiWeapons.saSandstoneKnife, "Sandcut");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.saSandstoneKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), mod_InfiTools.sandstoneRod
                });
        mod_BalkonInfiWeapons.bSandstoneKnife.setIconCoord(3, 7);
        ModLoader.addName(mod_BalkonInfiWeapons.bSandstoneKnife, "Necrotic Sandstone Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bSandstoneKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bSandstoneKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_BalkonInfiWeapons.pSandstoneKnife.setIconCoord(4, 7);
        ModLoader.addName(mod_BalkonInfiWeapons.pSandstoneKnife, "Fibery Sandstone Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.pSandstoneKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), mod_InfiTools.paperRod
                });
        mod_BalkonInfiWeapons.nSandstoneKnife.setIconCoord(5, 7);
        ModLoader.addName(mod_BalkonInfiWeapons.nSandstoneKnife, "Bloody Sandstone Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.nSandstoneKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_BalkonInfiWeapons.iceSandstoneKnife.setIconCoord(6, 7);
        ModLoader.addName(mod_BalkonInfiWeapons.iceSandstoneKnife, "Icy Sandstone Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.iceSandstoneKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), mod_InfiTools.iceRod
                });
        mod_BalkonInfiWeapons.sSandstoneKnife.setIconCoord(7, 7);
        ModLoader.addName(mod_BalkonInfiWeapons.sSandstoneKnife, "Slimy Sandstone Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.sSandstoneKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_BalkonInfiWeapons.cSandstoneKnife.setIconCoord(8, 7);
        ModLoader.addName(mod_BalkonInfiWeapons.cSandstoneKnife, "Spiny Sandstone Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.cSandstoneKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_BalkonInfiWeapons.fSandstoneKnife.setIconCoord(9, 7);
        ModLoader.addName(mod_BalkonInfiWeapons.fSandstoneKnife, "Flaky Sandstone Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.fSandstoneKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), mod_InfiTools.flintRod
                });
        mod_BalkonInfiWeapons.brSandstoneKnife.setIconCoord(10, 7);
        ModLoader.addName(mod_BalkonInfiWeapons.brSandstoneKnife, "Baked Sandstone Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.brSandstoneKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), mod_InfiTools.brickRod
                });
        mod_BalkonInfiWeapons.wBoneKnife.setIconCoord(0, 8);
        ModLoader.addName(mod_BalkonInfiWeapons.wBoneKnife, "Bone Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.wBoneKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Item.bone, Character.valueOf('|'), Item.stick
                });
        mod_BalkonInfiWeapons.stBoneKnife.setIconCoord(1, 8);
        ModLoader.addName(mod_BalkonInfiWeapons.stBoneKnife, "Stony Bone Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.stBoneKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_BalkonInfiWeapons.iBoneKnife.setIconCoord(2, 8);
        ModLoader.addName(mod_BalkonInfiWeapons.iBoneKnife, "Hard Bone Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.iBoneKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.ironRod
                });
        mod_BalkonInfiWeapons.dBoneKnife.setIconCoord(3, 8);
        ModLoader.addName(mod_BalkonInfiWeapons.dBoneKnife, "Jeweled Bone Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.dBoneKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.diamondRod
                });
        mod_BalkonInfiWeapons.rBoneKnife.setIconCoord(4, 8);
        ModLoader.addName(mod_BalkonInfiWeapons.rBoneKnife, "Red Bone Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.rBoneKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.redstoneRod
                });
        mod_BalkonInfiWeapons.oBoneKnife.setIconCoord(5, 8);
        ModLoader.addName(mod_BalkonInfiWeapons.oBoneKnife, "Glassy Bone Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.oBoneKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.obsidianRod
                });
        mod_BalkonInfiWeapons.bBoneKnife.setIconCoord(6, 8);
        ModLoader.addName(mod_BalkonInfiWeapons.bBoneKnife, "Reanimated Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bBoneKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Item.bone, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bBoneKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_BalkonInfiWeapons.mBoneKnife.setIconCoord(7, 8);
        ModLoader.addName(mod_BalkonInfiWeapons.mBoneKnife, "Mossy Bone Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.mBoneKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.mossyRod
                });
        mod_BalkonInfiWeapons.nBoneKnife.setIconCoord(8, 8);
        ModLoader.addName(mod_BalkonInfiWeapons.nBoneKnife, "Netherrack Bone Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.nBoneKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_BalkonInfiWeapons.glBoneKnife.setIconCoord(9, 8);
        ModLoader.addName(mod_BalkonInfiWeapons.glBoneKnife, "Glowstone Bone Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.glBoneKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_BalkonInfiWeapons.sBoneKnife.setIconCoord(10, 8);
        ModLoader.addName(mod_BalkonInfiWeapons.sBoneKnife, "Slimy Bone Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.sBoneKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_BalkonInfiWeapons.cBoneKnife.setIconCoord(11, 8);
        ModLoader.addName(mod_BalkonInfiWeapons.cBoneKnife, "Spiny Bone Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.cBoneKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_BalkonInfiWeapons.fBoneKnife.setIconCoord(12, 8);
        ModLoader.addName(mod_BalkonInfiWeapons.fBoneKnife, "Flaky Bone Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.fBoneKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.flintRod
                });
        mod_BalkonInfiWeapons.brBoneKnife.setIconCoord(13, 8);
        ModLoader.addName(mod_BalkonInfiWeapons.brBoneKnife, "Baked Bone Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.brBoneKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.brickRod
                });
        mod_BalkonInfiWeapons.blBoneKnife.setIconCoord(14, 8);
        ModLoader.addName(mod_BalkonInfiWeapons.blBoneKnife, "Blazing Bone Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.blBoneKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Item.bone, Character.valueOf('|'), Item.blazeRod
                });
        mod_BalkonInfiWeapons.wPaperKnife.setIconCoord(0, 9);
        ModLoader.addName(mod_BalkonInfiWeapons.wPaperKnife, "Paper Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.wPaperKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), mod_InfiTools.paperStack, Character.valueOf('|'), Item.stick
                });
        mod_BalkonInfiWeapons.saPaperKnife.setIconCoord(1, 9);
        ModLoader.addName(mod_BalkonInfiWeapons.saPaperKnife, "Stony Paper Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.saPaperKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), mod_InfiTools.paperStack, Character.valueOf('|'), mod_InfiTools.sandstoneRod
                });
        mod_BalkonInfiWeapons.bPaperKnife.setIconCoord(2, 9);
        ModLoader.addName(mod_BalkonInfiWeapons.bPaperKnife, "Necrotic Paper Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bPaperKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), mod_InfiTools.paperStack, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bPaperKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), mod_InfiTools.paperStack, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_BalkonInfiWeapons.pPaperKnife.setIconCoord(3, 9);
        ModLoader.addName(mod_BalkonInfiWeapons.pPaperKnife, "Paper Cutter");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.pPaperKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), mod_InfiTools.paperStack, Character.valueOf('|'), mod_InfiTools.paperRod
                });
        mod_BalkonInfiWeapons.sPaperKnife.setIconCoord(4, 9);
        ModLoader.addName(mod_BalkonInfiWeapons.sPaperKnife, "Slimy Paper Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.sPaperKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), mod_InfiTools.paperStack, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_BalkonInfiWeapons.cPaperKnife.setIconCoord(5, 9);
        ModLoader.addName(mod_BalkonInfiWeapons.cPaperKnife, "Spiny Paper Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.cPaperKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), mod_InfiTools.paperStack, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_BalkonInfiWeapons.brPaperKnife.setIconCoord(6, 9);
        ModLoader.addName(mod_BalkonInfiWeapons.brPaperKnife, "Baked Paper Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.brPaperKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), mod_InfiTools.paperStack, Character.valueOf('|'), mod_InfiTools.brickRod
                });
        mod_BalkonInfiWeapons.stMossyKnife.setIconCoord(0, 10);
        ModLoader.addName(mod_BalkonInfiWeapons.stMossyKnife, "Stony Moss-Covered Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.stMossyKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), mod_InfiTools.mossBallCrafted, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_BalkonInfiWeapons.dMossyKnife.setIconCoord(1, 10);
        ModLoader.addName(mod_BalkonInfiWeapons.dMossyKnife, "Jeweled Moss-Covered Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.dMossyKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), mod_InfiTools.mossBallCrafted, Character.valueOf('|'), mod_InfiTools.diamondRod
                });
        mod_BalkonInfiWeapons.rMossyKnife.setIconCoord(2, 10);
        ModLoader.addName(mod_BalkonInfiWeapons.rMossyKnife, "Red Moss-Covered Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.rMossyKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), mod_InfiTools.mossBallCrafted, Character.valueOf('|'), mod_InfiTools.redstoneRod
                });
        mod_BalkonInfiWeapons.bMossyKnife.setIconCoord(3, 10);
        ModLoader.addName(mod_BalkonInfiWeapons.bMossyKnife, "Necrotic Moss-Covered Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bMossyKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), mod_InfiTools.mossBallCrafted, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bMossyKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), mod_InfiTools.mossBallCrafted, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_BalkonInfiWeapons.mMossyKnife.setIconCoord(4, 10);
        ModLoader.addName(mod_BalkonInfiWeapons.mMossyKnife, "Living Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.mMossyKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), mod_InfiTools.mossBallCrafted, Character.valueOf('|'), mod_InfiTools.mossyRod
                });
        mod_BalkonInfiWeapons.glMossyKnife.setIconCoord(5, 10);
        ModLoader.addName(mod_BalkonInfiWeapons.glMossyKnife, "Glowing Moss-Covered Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.glMossyKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), mod_InfiTools.mossBallCrafted, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_BalkonInfiWeapons.wNetherrackKnife.setIconCoord(0, 11);
        ModLoader.addName(mod_BalkonInfiWeapons.wNetherrackKnife, "Netherrack Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.wNetherrackKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), Item.stick
                });
        mod_BalkonInfiWeapons.stNetherrackKnife.setIconCoord(1, 11);
        ModLoader.addName(mod_BalkonInfiWeapons.stNetherrackKnife, "Stony Netherrack Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.stNetherrackKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_BalkonInfiWeapons.iNetherrackKnife.setIconCoord(2, 11);
        ModLoader.addName(mod_BalkonInfiWeapons.iNetherrackKnife, "Hard Netherrack Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.iNetherrackKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.ironRod
                });
        mod_BalkonInfiWeapons.rNetherrackKnife.setIconCoord(3, 11);
        ModLoader.addName(mod_BalkonInfiWeapons.rNetherrackKnife, "Red Netherrack Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.rNetherrackKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.redstoneRod
                });
        mod_BalkonInfiWeapons.oNetherrackKnife.setIconCoord(4, 11);
        ModLoader.addName(mod_BalkonInfiWeapons.oNetherrackKnife, "Glassy Netherrack Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.oNetherrackKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.obsidianRod
                });
        mod_BalkonInfiWeapons.saNetherrackKnife.setIconCoord(5, 11);
        ModLoader.addName(mod_BalkonInfiWeapons.saNetherrackKnife, "Sandy Netherrack Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.saNetherrackKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.sandstoneRod
                });
        mod_BalkonInfiWeapons.bNetherrackKnife.setIconCoord(6, 11);
        ModLoader.addName(mod_BalkonInfiWeapons.bNetherrackKnife, "Necrotic Netherrack Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bNetherrackKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bNetherrackKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_BalkonInfiWeapons.mNetherrackKnife.setIconCoord(7, 11);
        ModLoader.addName(mod_BalkonInfiWeapons.mNetherrackKnife, "Mossy Netherrack Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.mNetherrackKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.mossyRod
                });
        mod_BalkonInfiWeapons.nNetherrackKnife.setIconCoord(8, 11);
        ModLoader.addName(mod_BalkonInfiWeapons.nNetherrackKnife, "Bloody Blade");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.nNetherrackKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_BalkonInfiWeapons.glNetherrackKnife.setIconCoord(9, 11);
        ModLoader.addName(mod_BalkonInfiWeapons.glNetherrackKnife, "Glowing Netherrack Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.glNetherrackKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_BalkonInfiWeapons.iceNetherrackKnife.setIconCoord(10, 11);
        ModLoader.addName(mod_BalkonInfiWeapons.iceNetherrackKnife, "Icy Netherrack Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.iceNetherrackKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.iceRod
                });
        mod_BalkonInfiWeapons.sNetherrackKnife.setIconCoord(11, 11);
        ModLoader.addName(mod_BalkonInfiWeapons.sNetherrackKnife, "Slimy Netherrack Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.sNetherrackKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_BalkonInfiWeapons.cNetherrackKnife.setIconCoord(12, 11);
        ModLoader.addName(mod_BalkonInfiWeapons.cNetherrackKnife, "Spiny Netherrack Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.cNetherrackKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_BalkonInfiWeapons.fNetherrackKnife.setIconCoord(13, 11);
        ModLoader.addName(mod_BalkonInfiWeapons.fNetherrackKnife, "Flaky Netherrack Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.fNetherrackKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_BalkonInfiWeapons.brNetherrackKnife.setIconCoord(14, 11);
        ModLoader.addName(mod_BalkonInfiWeapons.brNetherrackKnife, "Baked Netherrack Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.brNetherrackKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_BalkonInfiWeapons.blNetherrackKnife.setIconCoord(15, 11);
        ModLoader.addName(mod_BalkonInfiWeapons.blNetherrackKnife, "Blazing Netherrack Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.blNetherrackKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_BalkonInfiWeapons.wGlowstoneKnife.setIconCoord(0, 12);
        ModLoader.addName(mod_BalkonInfiWeapons.wGlowstoneKnife, "Glowstone Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.wGlowstoneKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), Item.stick
                });
        mod_BalkonInfiWeapons.stGlowstoneKnife.setIconCoord(1, 12);
        ModLoader.addName(mod_BalkonInfiWeapons.stGlowstoneKnife, "Stony Glowstone Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.stGlowstoneKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_BalkonInfiWeapons.iGlowstoneKnife.setIconCoord(2, 12);
        ModLoader.addName(mod_BalkonInfiWeapons.iGlowstoneKnife, "Hard Glowstone Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.iGlowstoneKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.ironRod
                });
        mod_BalkonInfiWeapons.dGlowstoneKnife.setIconCoord(3, 12);
        ModLoader.addName(mod_BalkonInfiWeapons.dGlowstoneKnife, "Jeweled Glowstone Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.dGlowstoneKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.diamondRod
                });
        mod_BalkonInfiWeapons.rGlowstoneKnife.setIconCoord(4, 12);
        ModLoader.addName(mod_BalkonInfiWeapons.rGlowstoneKnife, "Red Glowstone Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.rGlowstoneKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.redstoneRod
                });
        mod_BalkonInfiWeapons.oGlowstoneKnife.setIconCoord(5, 12);
        ModLoader.addName(mod_BalkonInfiWeapons.oGlowstoneKnife, "Glassy Glowstone Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.oGlowstoneKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.obsidianRod
                });
        mod_BalkonInfiWeapons.bGlowstoneKnife.setIconCoord(6, 12);
        ModLoader.addName(mod_BalkonInfiWeapons.bGlowstoneKnife, "Necrotic Glowstone Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bGlowstoneKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bGlowstoneKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_BalkonInfiWeapons.mGlowstoneKnife.setIconCoord(7, 12);
        ModLoader.addName(mod_BalkonInfiWeapons.mGlowstoneKnife, "Mossy Glowstone Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.mGlowstoneKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.mossyRod
                });
        mod_BalkonInfiWeapons.nGlowstoneKnife.setIconCoord(8, 12);
        ModLoader.addName(mod_BalkonInfiWeapons.nGlowstoneKnife, "Bloody Glowstone Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.nGlowstoneKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_BalkonInfiWeapons.glGlowstoneKnife.setIconCoord(9, 12);
        ModLoader.addName(mod_BalkonInfiWeapons.glGlowstoneKnife, "Bright Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.glGlowstoneKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_BalkonInfiWeapons.iceGlowstoneKnife.setIconCoord(10, 12);
        ModLoader.addName(mod_BalkonInfiWeapons.iceGlowstoneKnife, "Icy Glowstone Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.iceGlowstoneKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.iceRod
                });
        mod_BalkonInfiWeapons.lGlowstoneKnife.setIconCoord(11, 12);
        ModLoader.addName(mod_BalkonInfiWeapons.lGlowstoneKnife, "Fiery Glowstone Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.lGlowstoneKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_BalkonInfiWeapons.sGlowstoneKnife.setIconCoord(12, 12);
        ModLoader.addName(mod_BalkonInfiWeapons.sGlowstoneKnife, "Slimy Glowstone Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.sGlowstoneKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_BalkonInfiWeapons.blGlowstoneKnife.setIconCoord(13, 12);
        ModLoader.addName(mod_BalkonInfiWeapons.blGlowstoneKnife, "Blazing Glowstone Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.blGlowstoneKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), Item.blazeRod
                });
        mod_BalkonInfiWeapons.wIceKnife.setIconCoord(0, 13);
        ModLoader.addName(mod_BalkonInfiWeapons.wIceKnife, "Ice Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.wIceKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Block.ice, Character.valueOf('|'), Item.stick
                });
        mod_BalkonInfiWeapons.stIceKnife.setIconCoord(1, 13);
        ModLoader.addName(mod_BalkonInfiWeapons.stIceKnife, "Stony Ice Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.stIceKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_BalkonInfiWeapons.iIceKnife.setIconCoord(2, 13);
        ModLoader.addName(mod_BalkonInfiWeapons.iIceKnife, "Hard Ice Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.iIceKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.ironRod
                });
        mod_BalkonInfiWeapons.dIceKnife.setIconCoord(3, 13);
        ModLoader.addName(mod_BalkonInfiWeapons.dIceKnife, "Jeweled Ice Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.dIceKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.diamondRod
                });
        mod_BalkonInfiWeapons.gIceKnife.setIconCoord(4, 13);
        ModLoader.addName(mod_BalkonInfiWeapons.gIceKnife, "Expensive Ice Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.gIceKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.goldRod
                });
        mod_BalkonInfiWeapons.rIceKnife.setIconCoord(5, 13);
        ModLoader.addName(mod_BalkonInfiWeapons.rIceKnife, "Red Ice Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.rIceKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.redstoneRod
                });
        mod_BalkonInfiWeapons.oIceKnife.setIconCoord(6, 13);
        ModLoader.addName(mod_BalkonInfiWeapons.oIceKnife, "Glassy Ice Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.oIceKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.obsidianRod
                });
        mod_BalkonInfiWeapons.saIceKnife.setIconCoord(7, 13);
        ModLoader.addName(mod_BalkonInfiWeapons.saIceKnife, "Sandy Ice Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.saIceKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.sandstoneRod
                });
        mod_BalkonInfiWeapons.bIceKnife.setIconCoord(8, 13);
        ModLoader.addName(mod_BalkonInfiWeapons.bIceKnife, "Necrotic Ice Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bIceKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Block.ice, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bIceKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_BalkonInfiWeapons.glIceKnife.setIconCoord(9, 13);
        ModLoader.addName(mod_BalkonInfiWeapons.glIceKnife, "Glowing Ice Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.glIceKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_BalkonInfiWeapons.iceIceKnife.setIconCoord(10, 13);
        ModLoader.addName(mod_BalkonInfiWeapons.iceIceKnife, "Frostsickle");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.iceIceKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.iceRod
                });
        mod_BalkonInfiWeapons.sIceKnife.setIconCoord(11, 13);
        ModLoader.addName(mod_BalkonInfiWeapons.sIceKnife, "Slimy Ice Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.sIceKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_BalkonInfiWeapons.cIceKnife.setIconCoord(12, 13);
        ModLoader.addName(mod_BalkonInfiWeapons.cIceKnife, "Spiny Ice Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.cIceKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_BalkonInfiWeapons.fIceKnife.setIconCoord(13, 13);
        ModLoader.addName(mod_BalkonInfiWeapons.fIceKnife, "Flaky Ice Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.fIceKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.flintRod
                });
        mod_BalkonInfiWeapons.brIceKnife.setIconCoord(14, 13);
        ModLoader.addName(mod_BalkonInfiWeapons.brIceKnife, "Baked Ice Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.brIceKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.brickRod
                });
        mod_BalkonInfiWeapons.dLavaKnife.setIconCoord(0, 14);
        ModLoader.addName(mod_BalkonInfiWeapons.dLavaKnife, "Jeweled Lava Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.dLavaKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), mod_InfiTools.lavaCrystal, Character.valueOf('|'), mod_InfiTools.diamondRod
                });
        mod_BalkonInfiWeapons.rLavaKnife.setIconCoord(1, 14);
        ModLoader.addName(mod_BalkonInfiWeapons.rLavaKnife, "Red Lava Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.rLavaKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), mod_InfiTools.lavaCrystal, Character.valueOf('|'), mod_InfiTools.redstoneRod
                });
        mod_BalkonInfiWeapons.bLavaKnife.setIconCoord(2, 14);
        ModLoader.addName(mod_BalkonInfiWeapons.bLavaKnife, "Necrotic Lava Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bLavaKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), mod_InfiTools.lavaCrystal, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bLavaKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), mod_InfiTools.lavaCrystal, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_BalkonInfiWeapons.nLavaKnife.setIconCoord(3, 14);
        ModLoader.addName(mod_BalkonInfiWeapons.nLavaKnife, "Bloody Lava Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.nLavaKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), mod_InfiTools.lavaCrystal, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_BalkonInfiWeapons.glLavaKnife.setIconCoord(4, 14);
        ModLoader.addName(mod_BalkonInfiWeapons.glLavaKnife, "Glowing Lava Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.glLavaKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), mod_InfiTools.lavaCrystal, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_BalkonInfiWeapons.lLavaKnife.setIconCoord(5, 14);
        ModLoader.addName(mod_BalkonInfiWeapons.lLavaKnife, "Fleshsinge");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.lLavaKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), mod_InfiTools.lavaCrystal, Character.valueOf('|'), mod_InfiTools.lavaRod
                });
        mod_BalkonInfiWeapons.blLavaKnife.setIconCoord(6, 14);
        ModLoader.addName(mod_BalkonInfiWeapons.blLavaKnife, "Blazing Lava Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.blLavaKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), mod_InfiTools.lavaCrystal, Character.valueOf('|'), Item.blazeRod
                });
        mod_BalkonInfiWeapons.wSlimeKnife.setIconCoord(0, 15);
        ModLoader.addName(mod_BalkonInfiWeapons.wSlimeKnife, "Slime Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.wSlimeKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), Item.stick
                });
        mod_BalkonInfiWeapons.stSlimeKnife.setIconCoord(1, 15);
        ModLoader.addName(mod_BalkonInfiWeapons.stSlimeKnife, "Stony Slime Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.stSlimeKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_BalkonInfiWeapons.iSlimeKnife.setIconCoord(2, 15);
        ModLoader.addName(mod_BalkonInfiWeapons.iSlimeKnife, "Hard Slime Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.iSlimeKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.ironRod
                });
        mod_BalkonInfiWeapons.dSlimeKnife.setIconCoord(3, 15);
        ModLoader.addName(mod_BalkonInfiWeapons.dSlimeKnife, "Jeweled Slime Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.dSlimeKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.diamondRod
                });
        mod_BalkonInfiWeapons.gSlimeKnife.setIconCoord(4, 15);
        ModLoader.addName(mod_BalkonInfiWeapons.gSlimeKnife, "Expensive Slime Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.gSlimeKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.goldRod
                });
        mod_BalkonInfiWeapons.rSlimeKnife.setIconCoord(5, 15);
        ModLoader.addName(mod_BalkonInfiWeapons.rSlimeKnife, "Red Slime Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.rSlimeKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.redstoneRod
                });
        mod_BalkonInfiWeapons.oSlimeKnife.setIconCoord(6, 15);
        ModLoader.addName(mod_BalkonInfiWeapons.oSlimeKnife, "Glassy Slime Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.oSlimeKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.obsidianRod
                });
        mod_BalkonInfiWeapons.saSlimeKnife.setIconCoord(7, 15);
        ModLoader.addName(mod_BalkonInfiWeapons.saSlimeKnife, "Sandy Slime Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.saSlimeKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.sandstoneRod
                });
        mod_BalkonInfiWeapons.bSlimeKnife.setIconCoord(8, 15);
        ModLoader.addName(mod_BalkonInfiWeapons.bSlimeKnife, "Necrotic Slime Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bSlimeKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bSlimeKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_BalkonInfiWeapons.pSlimeKnife.setIconCoord(9, 15);
        ModLoader.addName(mod_BalkonInfiWeapons.pSlimeKnife, "Fibery Slime Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.pSlimeKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.paperRod
                });
        mod_BalkonInfiWeapons.mSlimeKnife.setIconCoord(10, 15);
        ModLoader.addName(mod_BalkonInfiWeapons.mSlimeKnife, "Mossy Slime Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.mSlimeKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.mossyRod
                });
        mod_BalkonInfiWeapons.nSlimeKnife.setIconCoord(11, 15);
        ModLoader.addName(mod_BalkonInfiWeapons.nSlimeKnife, "Bloody Slime Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.nSlimeKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_BalkonInfiWeapons.glSlimeKnife.setIconCoord(12, 15);
        ModLoader.addName(mod_BalkonInfiWeapons.glSlimeKnife, "Glowing Slime Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.glSlimeKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_BalkonInfiWeapons.iceSlimeKnife.setIconCoord(13, 15);
        ModLoader.addName(mod_BalkonInfiWeapons.iceSlimeKnife, "Icy Slime Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.iceSlimeKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.iceRod
                });
        mod_BalkonInfiWeapons.lSlimeKnife.setIconCoord(14, 15);
        ModLoader.addName(mod_BalkonInfiWeapons.lSlimeKnife, "Fiery Slime Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.lSlimeKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.lavaRod
                });
        mod_BalkonInfiWeapons.sSlimeKnife.setIconCoord(15, 15);
        ModLoader.addName(mod_BalkonInfiWeapons.sSlimeKnife, "Green Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.sSlimeKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_BalkonInfiWeapons.cSlimeKnife.setIconCoord(12, 14);
        ModLoader.addName(mod_BalkonInfiWeapons.cSlimeKnife, "Spiny Slime Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.cSlimeKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_BalkonInfiWeapons.fSlimeKnife.setIconCoord(13, 14);
        ModLoader.addName(mod_BalkonInfiWeapons.fSlimeKnife, "Flaky Slime Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.fSlimeKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.flintRod
                });
        mod_BalkonInfiWeapons.brSlimeKnife.setIconCoord(14, 14);
        ModLoader.addName(mod_BalkonInfiWeapons.brSlimeKnife, "Baked Slime Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.brSlimeKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.brickRod
                });
        mod_BalkonInfiWeapons.blSlimeKnife.setIconCoord(15, 14);
        ModLoader.addName(mod_BalkonInfiWeapons.blSlimeKnife, "Blazing Slime Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.blSlimeKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), Item.blazeRod
                });
        mod_BalkonInfiWeapons.wCactusKnife.setIconCoord(10, 0);
        ModLoader.addName(mod_BalkonInfiWeapons.wCactusKnife, "Cactus Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.wCactusKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Block.cactus, Character.valueOf('|'), Item.stick
                });
        mod_BalkonInfiWeapons.stCactusKnife.setIconCoord(11, 0);
        ModLoader.addName(mod_BalkonInfiWeapons.stCactusKnife, "Stony Cactus Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.stCactusKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Block.cactus, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_BalkonInfiWeapons.saCactusKnife.setIconCoord(12, 0);
        ModLoader.addName(mod_BalkonInfiWeapons.saCactusKnife, "Sandy Cactus Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.saCactusKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Block.cactus, Character.valueOf('|'), mod_InfiTools.sandstoneRod
                });
        mod_BalkonInfiWeapons.bCactusKnife.setIconCoord(13, 0);
        ModLoader.addName(mod_BalkonInfiWeapons.bCactusKnife, "Necrotic Cactus Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bCactusKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Block.cactus, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bCactusKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Block.cactus, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_BalkonInfiWeapons.pCactusKnife.setIconCoord(14, 0);
        ModLoader.addName(mod_BalkonInfiWeapons.pCactusKnife, "Fibery Cactus Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.pCactusKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Block.cactus, Character.valueOf('|'), mod_InfiTools.paperRod
                });
        mod_BalkonInfiWeapons.nCactusKnife.setIconCoord(15, 0);
        ModLoader.addName(mod_BalkonInfiWeapons.nCactusKnife, "Bloody Cactus Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.nCactusKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Block.cactus, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_BalkonInfiWeapons.sCactusKnife.setIconCoord(12, 1);
        ModLoader.addName(mod_BalkonInfiWeapons.sCactusKnife, "Slimy Cactus Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.sCactusKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Block.cactus, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_BalkonInfiWeapons.cCactusKnife.setIconCoord(13, 1);
        ModLoader.addName(mod_BalkonInfiWeapons.cCactusKnife, "Cactus Spine");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.cCactusKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Block.cactus, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_BalkonInfiWeapons.fCactusKnife.setIconCoord(14, 1);
        ModLoader.addName(mod_BalkonInfiWeapons.fCactusKnife, "Flaky Cactus Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.fCactusKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Block.cactus, Character.valueOf('|'), mod_InfiTools.flintRod
                });
        mod_BalkonInfiWeapons.brCactusKnife.setIconCoord(15, 1);
        ModLoader.addName(mod_BalkonInfiWeapons.brCactusKnife, "Baked Cactus Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.brCactusKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Block.cactus, Character.valueOf('|'), mod_InfiTools.brickRod
                });
        mod_BalkonInfiWeapons.wFlintKnife.setIconCoord(13, 2);
        ModLoader.addName(mod_BalkonInfiWeapons.wFlintKnife, "Flint Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.wFlintKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Item.flint, Character.valueOf('|'), Item.stick
                });
        mod_BalkonInfiWeapons.stFlintKnife.setIconCoord(14, 2);
        ModLoader.addName(mod_BalkonInfiWeapons.stFlintKnife, "Stony Flint Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.stFlintKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_BalkonInfiWeapons.iFlintKnife.setIconCoord(15, 2);
        ModLoader.addName(mod_BalkonInfiWeapons.iFlintKnife, "Hard Flint Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.iFlintKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.ironRod
                });
        mod_BalkonInfiWeapons.gFlintKnife.setIconCoord(12, 3);
        ModLoader.addName(mod_BalkonInfiWeapons.gFlintKnife, "Expensive Flint Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.gFlintKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.goldRod
                });
        mod_BalkonInfiWeapons.oFlintKnife.setIconCoord(13, 3);
        ModLoader.addName(mod_BalkonInfiWeapons.oFlintKnife, "Glassy Flint Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.oFlintKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.obsidianRod
                });
        mod_BalkonInfiWeapons.saFlintKnife.setIconCoord(14, 3);
        ModLoader.addName(mod_BalkonInfiWeapons.saFlintKnife, "Sandy Flint Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.saFlintKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.sandstoneRod
                });
        mod_BalkonInfiWeapons.bFlintKnife.setIconCoord(15, 3);
        ModLoader.addName(mod_BalkonInfiWeapons.bFlintKnife, "Necrotic Flint Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bFlintKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Item.flint, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bFlintKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_BalkonInfiWeapons.nFlintKnife.setIconCoord(12, 4);
        ModLoader.addName(mod_BalkonInfiWeapons.nFlintKnife, "Bloody Flint Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.nFlintKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_BalkonInfiWeapons.iceFlintKnife.setIconCoord(13, 4);
        ModLoader.addName(mod_BalkonInfiWeapons.iceFlintKnife, "Icy Flint Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.iceFlintKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.iceRod
                });
        mod_BalkonInfiWeapons.sFlintKnife.setIconCoord(14, 4);
        ModLoader.addName(mod_BalkonInfiWeapons.sFlintKnife, "Slimy Flint Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.sFlintKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_BalkonInfiWeapons.cFlintKnife.setIconCoord(15, 4);
        ModLoader.addName(mod_BalkonInfiWeapons.cFlintKnife, "Spiny Flint Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.cFlintKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_BalkonInfiWeapons.fFlintKnife.setIconCoord(11, 5);
        ModLoader.addName(mod_BalkonInfiWeapons.fFlintKnife, "Simple Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.fFlintKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.flintRod
                });
        mod_BalkonInfiWeapons.brFlintKnife.setIconCoord(12, 5);
        ModLoader.addName(mod_BalkonInfiWeapons.brFlintKnife, "Baked Flint Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.brFlintKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.brickRod
                });
        mod_BalkonInfiWeapons.blFlintKnife.setIconCoord(13, 5);
        ModLoader.addName(mod_BalkonInfiWeapons.blFlintKnife, "Blazing Flint Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.blFlintKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Item.flint, Character.valueOf('|'), Item.blazeRod
                });
        mod_BalkonInfiWeapons.wBrickKnife.setIconCoord(14, 5);
        ModLoader.addName(mod_BalkonInfiWeapons.wBrickKnife, "Brick Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.wBrickKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Item.brick, Character.valueOf('|'), Item.brick
                });
        mod_BalkonInfiWeapons.stBrickKnife.setIconCoord(15, 5);
        ModLoader.addName(mod_BalkonInfiWeapons.stBrickKnife, "Stony Brick Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.stBrickKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Item.brick, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_BalkonInfiWeapons.saBrickKnife.setIconCoord(13, 6);
        ModLoader.addName(mod_BalkonInfiWeapons.saBrickKnife, "Sandstone Brick Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.saBrickKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Item.brick, Character.valueOf('|'), mod_InfiTools.sandstoneRod
                });
        mod_BalkonInfiWeapons.bBrickKnife.setIconCoord(14, 6);
        ModLoader.addName(mod_BalkonInfiWeapons.bBrickKnife, "Necrotic Brick Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bBrickKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Item.brick, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bBrickKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Item.brick, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_BalkonInfiWeapons.pBrickKnife.setIconCoord(15, 6);
        ModLoader.addName(mod_BalkonInfiWeapons.pBrickKnife, "Fibery Brick Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.pBrickKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Item.brick, Character.valueOf('|'), mod_InfiTools.paperRod
                });
        mod_BalkonInfiWeapons.nBrickKnife.setIconCoord(11, 7);
        ModLoader.addName(mod_BalkonInfiWeapons.nBrickKnife, "Bloody Brick Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.nBrickKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Item.brick, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_BalkonInfiWeapons.iceBrickKnife.setIconCoord(12, 7);
        ModLoader.addName(mod_BalkonInfiWeapons.iceBrickKnife, "Icy Brick Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.iceBrickKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Item.brick, Character.valueOf('|'), mod_InfiTools.iceRod
                });
        mod_BalkonInfiWeapons.sBrickKnife.setIconCoord(13, 7);
        ModLoader.addName(mod_BalkonInfiWeapons.sBrickKnife, "Slimy Brick Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.sBrickKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Item.brick, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_BalkonInfiWeapons.cBrickKnife.setIconCoord(14, 7);
        ModLoader.addName(mod_BalkonInfiWeapons.cBrickKnife, "Spiny Brick Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.cBrickKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Item.brick, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_BalkonInfiWeapons.fBrickKnife.setIconCoord(15, 7);
        ModLoader.addName(mod_BalkonInfiWeapons.fBrickKnife, "Flaky Brick Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.fBrickKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Item.brick, Character.valueOf('|'), mod_InfiTools.flintRod
                });
        mod_BalkonInfiWeapons.brBrickKnife.setIconCoord(15, 8);
        ModLoader.addName(mod_BalkonInfiWeapons.brBrickKnife, "Look-alike Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.brBrickKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), Item.brick, Character.valueOf('|'), mod_InfiTools.brickRod
                });
        mod_BalkonInfiWeapons.dBlazeKnife.setIconCoord(7, 9);
        ModLoader.addName(mod_BalkonInfiWeapons.dBlazeKnife, "Jeweled Blaze Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.dBlazeKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), mod_InfiTools.blazeCrystal, Character.valueOf('|'), mod_InfiTools.diamondRod
                });
        mod_BalkonInfiWeapons.rBlazeKnife.setIconCoord(8, 9);
        ModLoader.addName(mod_BalkonInfiWeapons.rBlazeKnife, "Red Blaze Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.rBlazeKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), mod_InfiTools.blazeCrystal, Character.valueOf('|'), mod_InfiTools.redstoneRod
                });
        mod_BalkonInfiWeapons.bBlazeKnife.setIconCoord(9, 9);
        ModLoader.addName(mod_BalkonInfiWeapons.bBlazeKnife, "Necrotic Blaze Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bBlazeKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), mod_InfiTools.blazeCrystal, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bBlazeKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), mod_InfiTools.blazeCrystal, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_BalkonInfiWeapons.nBlazeKnife.setIconCoord(10, 9);
        ModLoader.addName(mod_BalkonInfiWeapons.nBlazeKnife, "Bloody Blaze Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.nBlazeKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), mod_InfiTools.blazeCrystal, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_BalkonInfiWeapons.glBlazeKnife.setIconCoord(11, 9);
        ModLoader.addName(mod_BalkonInfiWeapons.glBlazeKnife, "Glowing Blaze Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.glBlazeKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), mod_InfiTools.blazeCrystal, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_BalkonInfiWeapons.lBlazeKnife.setIconCoord(12, 9);
        ModLoader.addName(mod_BalkonInfiWeapons.lBlazeKnife, "Fiery Blaze Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.lBlazeKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), mod_InfiTools.blazeCrystal, Character.valueOf('|'), Item.blazeRod
                });
        mod_BalkonInfiWeapons.fBlazeKnife.setIconCoord(13, 9);
        ModLoader.addName(mod_BalkonInfiWeapons.fBlazeKnife, "Flaky Blaze Knife");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.fBlazeKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), mod_InfiTools.blazeCrystal, Character.valueOf('|'), Item.blazeRod
                });
        mod_BalkonInfiWeapons.blBlazeKnife.setIconCoord(14, 9);
        ModLoader.addName(mod_BalkonInfiWeapons.blBlazeKnife, "Pocket Runner");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.blBlazeKnife, 1), new Object[]
                {
                    "m|", Character.valueOf('m'), mod_InfiTools.blazeCrystal, Character.valueOf('|'), Item.blazeRod
                });
        return 0;
    }
}
