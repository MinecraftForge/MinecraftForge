package net.minecraft.src.balkon;

import net.minecraft.src.*;

public class BalkRecipeHalberds
{
    public BalkRecipeHalberds()
    {
    }

    public static int recipeStorm()
    {
        mod_BalkonInfiWeapons.stWoodHalberd.setIconCoord(1, 0);
        ModLoader.addName(mod_BalkonInfiWeapons.stWoodHalberd, "Stony Wooden Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.stWoodHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Block.planks, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_BalkonInfiWeapons.saWoodHalberd.setIconCoord(2, 0);
        ModLoader.addName(mod_BalkonInfiWeapons.saWoodHalberd, "Sandy Wooden Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.saWoodHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Block.planks, Character.valueOf('|'), mod_InfiTools.sandstoneRod
                });
        mod_BalkonInfiWeapons.bWoodHalberd.setIconCoord(3, 0);
        ModLoader.addName(mod_BalkonInfiWeapons.bWoodHalberd, "Necrotic Wooden Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bWoodHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Block.planks, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bWoodHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Block.planks, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_BalkonInfiWeapons.pWoodHalberd.setIconCoord(4, 0);
        ModLoader.addName(mod_BalkonInfiWeapons.pWoodHalberd, "Fibery Wooden Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.pWoodHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Block.planks, Character.valueOf('|'), mod_InfiTools.paperRod
                });
        mod_BalkonInfiWeapons.nWoodHalberd.setIconCoord(5, 0);
        ModLoader.addName(mod_BalkonInfiWeapons.nWoodHalberd, "Bloody Wooden Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.nWoodHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Block.planks, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_BalkonInfiWeapons.sWoodHalberd.setIconCoord(6, 0);
        ModLoader.addName(mod_BalkonInfiWeapons.sWoodHalberd, "Slimy Wooden Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.sWoodHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Block.planks, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_BalkonInfiWeapons.cWoodHalberd.setIconCoord(7, 0);
        ModLoader.addName(mod_BalkonInfiWeapons.cWoodHalberd, "Spiny Wooden Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.cWoodHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Block.planks, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_BalkonInfiWeapons.fWoodHalberd.setIconCoord(8, 0);
        ModLoader.addName(mod_BalkonInfiWeapons.fWoodHalberd, "Flaky Wooden Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.fWoodHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Block.planks, Character.valueOf('|'), mod_InfiTools.flintRod
                });
        mod_BalkonInfiWeapons.brWoodHalberd.setIconCoord(9, 0);
        ModLoader.addName(mod_BalkonInfiWeapons.brWoodHalberd, "Baked Wooden Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.brWoodHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Block.planks, Character.valueOf('|'), mod_InfiTools.brickRod
                });
        mod_BalkonInfiWeapons.stStoneHalberd.setIconCoord(1, 1);
        ModLoader.addName(mod_BalkonInfiWeapons.stStoneHalberd, "Heavy Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.stStoneHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.stStoneHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Block.stone, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_BalkonInfiWeapons.saStoneHalberd.setIconCoord(2, 1);
        ModLoader.addName(mod_BalkonInfiWeapons.saStoneHalberd, "Sandy Stone Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.saStoneHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), mod_InfiTools.sandstoneRod
                });
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.saStoneHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Block.stone, Character.valueOf('|'), mod_InfiTools.sandstoneRod
                });
        mod_BalkonInfiWeapons.bStoneHalberd.setIconCoord(3, 1);
        ModLoader.addName(mod_BalkonInfiWeapons.bStoneHalberd, "Necrotic Stone Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bStoneHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bStoneHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Block.stone, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bStoneHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bStoneHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Block.stone, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_BalkonInfiWeapons.pStoneHalberd.setIconCoord(4, 1);
        ModLoader.addName(mod_BalkonInfiWeapons.pStoneHalberd, "Fibery Stone Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.pStoneHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), mod_InfiTools.paperRod
                });
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.pStoneHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Block.stone, Character.valueOf('|'), mod_InfiTools.paperRod
                });
        mod_BalkonInfiWeapons.mStoneHalberd.setIconCoord(5, 1);
        ModLoader.addName(mod_BalkonInfiWeapons.mStoneHalberd, "Mossy Stone Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.mStoneHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), mod_InfiTools.mossyRod
                });
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.mStoneHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Block.stone, Character.valueOf('|'), mod_InfiTools.mossyRod
                });
        mod_BalkonInfiWeapons.nStoneHalberd.setIconCoord(6, 1);
        ModLoader.addName(mod_BalkonInfiWeapons.nStoneHalberd, "Bloody Stone Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.nStoneHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.nStoneHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Block.stone, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_BalkonInfiWeapons.iceStoneHalberd.setIconCoord(7, 1);
        ModLoader.addName(mod_BalkonInfiWeapons.iceStoneHalberd, "Icy Stone Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.iceStoneHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), mod_InfiTools.iceRod
                });
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.iceStoneHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Block.stone, Character.valueOf('|'), mod_InfiTools.iceRod
                });
        mod_BalkonInfiWeapons.sStoneHalberd.setIconCoord(8, 1);
        ModLoader.addName(mod_BalkonInfiWeapons.sStoneHalberd, "Slimy Stone Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.sStoneHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.sStoneHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Block.stone, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_BalkonInfiWeapons.cStoneHalberd.setIconCoord(9, 1);
        ModLoader.addName(mod_BalkonInfiWeapons.cStoneHalberd, "Spiny Stone Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.cStoneHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.cStoneHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Block.stone, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_BalkonInfiWeapons.fStoneHalberd.setIconCoord(10, 1);
        ModLoader.addName(mod_BalkonInfiWeapons.fStoneHalberd, "Flaky Stone Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.fStoneHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), mod_InfiTools.flintRod
                });
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.fStoneHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Block.stone, Character.valueOf('|'), mod_InfiTools.flintRod
                });
        mod_BalkonInfiWeapons.brStoneHalberd.setIconCoord(11, 1);
        ModLoader.addName(mod_BalkonInfiWeapons.brStoneHalberd, "Baked Stone Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.brStoneHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), mod_InfiTools.brickRod
                });
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.brStoneHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Block.stone, Character.valueOf('|'), mod_InfiTools.brickRod
                });
        mod_BalkonInfiWeapons.stIronHalberd.setIconCoord(1, 2);
        ModLoader.addName(mod_BalkonInfiWeapons.stIronHalberd, "Stony Iron Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.stIronHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_BalkonInfiWeapons.iIronHalberd.setIconCoord(2, 2);
        ModLoader.addName(mod_BalkonInfiWeapons.iIronHalberd, "Ironic Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.iIronHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), mod_InfiTools.ironRod
                });
        mod_BalkonInfiWeapons.dIronHalberd.setIconCoord(3, 2);
        ModLoader.addName(mod_BalkonInfiWeapons.dIronHalberd, "Jeweled Iron Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.dIronHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), mod_InfiTools.diamondRod
                });
        mod_BalkonInfiWeapons.gIronHalberd.setIconCoord(4, 2);
        ModLoader.addName(mod_BalkonInfiWeapons.gIronHalberd, "Expensive Iron Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.gIronHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), mod_InfiTools.goldRod
                });
        mod_BalkonInfiWeapons.rIronHalberd.setIconCoord(5, 2);
        ModLoader.addName(mod_BalkonInfiWeapons.rIronHalberd, "Red Iron Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.rIronHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), mod_InfiTools.redstoneRod
                });
        mod_BalkonInfiWeapons.oIronHalberd.setIconCoord(6, 2);
        ModLoader.addName(mod_BalkonInfiWeapons.oIronHalberd, "Glassy Iron Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.oIronHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), mod_InfiTools.obsidianRod
                });
        mod_BalkonInfiWeapons.bIronHalberd.setIconCoord(7, 2);
        ModLoader.addName(mod_BalkonInfiWeapons.bIronHalberd, "Necrotic Iron Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bIronHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bIronHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_BalkonInfiWeapons.nIronHalberd.setIconCoord(8, 2);
        ModLoader.addName(mod_BalkonInfiWeapons.nIronHalberd, "Bloody Iron Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.nIronHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_BalkonInfiWeapons.glIronHalberd.setIconCoord(9, 2);
        ModLoader.addName(mod_BalkonInfiWeapons.glIronHalberd, "Glowing Iron Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.glIronHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_BalkonInfiWeapons.iceIronHalberd.setIconCoord(10, 2);
        ModLoader.addName(mod_BalkonInfiWeapons.iceIronHalberd, "Icy Iron Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.iceIronHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), mod_InfiTools.iceRod
                });
        mod_BalkonInfiWeapons.sIronHalberd.setIconCoord(11, 2);
        ModLoader.addName(mod_BalkonInfiWeapons.sIronHalberd, "Slimy Iron Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.sIronHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_BalkonInfiWeapons.blIronHalberd.setIconCoord(12, 2);
        ModLoader.addName(mod_BalkonInfiWeapons.blIronHalberd, "Blazing Iron Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.blIronHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), Item.blazeRod
                });
        mod_BalkonInfiWeapons.stDiamondHalberd.setIconCoord(1, 3);
        ModLoader.addName(mod_BalkonInfiWeapons.stDiamondHalberd, "Stony Diamond Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.stDiamondHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_BalkonInfiWeapons.iDiamondHalberd.setIconCoord(2, 3);
        ModLoader.addName(mod_BalkonInfiWeapons.iDiamondHalberd, "Hard Diamond Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.iDiamondHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), mod_InfiTools.ironRod
                });
        mod_BalkonInfiWeapons.dDiamondHalberd.setIconCoord(3, 3);
        ModLoader.addName(mod_BalkonInfiWeapons.dDiamondHalberd, "Diamondium Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.dDiamondHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), mod_InfiTools.diamondRod
                });
        mod_BalkonInfiWeapons.gDiamondHalberd.setIconCoord(4, 3);
        ModLoader.addName(mod_BalkonInfiWeapons.gDiamondHalberd, "Expensive Diamond Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.gDiamondHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), mod_InfiTools.goldRod
                });
        mod_BalkonInfiWeapons.rDiamondHalberd.setIconCoord(5, 3);
        ModLoader.addName(mod_BalkonInfiWeapons.rDiamondHalberd, "Red Diamond Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.rDiamondHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), mod_InfiTools.redstoneRod
                });
        mod_BalkonInfiWeapons.oDiamondHalberd.setIconCoord(6, 3);
        ModLoader.addName(mod_BalkonInfiWeapons.oDiamondHalberd, "Glassy Diamond Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.oDiamondHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), mod_InfiTools.obsidianRod
                });
        mod_BalkonInfiWeapons.bDiamondHalberd.setIconCoord(7, 3);
        ModLoader.addName(mod_BalkonInfiWeapons.bDiamondHalberd, "Necrotic Diamond Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bDiamondHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bDiamondHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_BalkonInfiWeapons.mDiamondHalberd.setIconCoord(8, 3);
        ModLoader.addName(mod_BalkonInfiWeapons.mDiamondHalberd, "Mossy Diamond Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.mDiamondHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), mod_InfiTools.mossyRod
                });
        mod_BalkonInfiWeapons.nDiamondHalberd.setIconCoord(9, 3);
        ModLoader.addName(mod_BalkonInfiWeapons.nDiamondHalberd, "Bloody Diamond Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.nDiamondHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_BalkonInfiWeapons.glDiamondHalberd.setIconCoord(10, 3);
        ModLoader.addName(mod_BalkonInfiWeapons.glDiamondHalberd, "Glowing Diamond Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.glDiamondHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_BalkonInfiWeapons.blDiamondHalberd.setIconCoord(11, 3);
        ModLoader.addName(mod_BalkonInfiWeapons.blDiamondHalberd, "Blazing Diamond Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.blDiamondHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), Item.blazeRod
                });
        mod_BalkonInfiWeapons.stGoldHalberd.setIconCoord(1, 4);
        ModLoader.addName(mod_BalkonInfiWeapons.stGoldHalberd, "Stony Gold Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.stGoldHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_BalkonInfiWeapons.gGoldHalberd.setIconCoord(2, 4);
        ModLoader.addName(mod_BalkonInfiWeapons.gGoldHalberd, "Expensive Useless Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.gGoldHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), mod_InfiTools.goldRod
                });
        mod_BalkonInfiWeapons.oGoldHalberd.setIconCoord(3, 4);
        ModLoader.addName(mod_BalkonInfiWeapons.oGoldHalberd, "Glassy Gold Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.oGoldHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), mod_InfiTools.obsidianRod
                });
        mod_BalkonInfiWeapons.saGoldHalberd.setIconCoord(4, 4);
        ModLoader.addName(mod_BalkonInfiWeapons.saGoldHalberd, "Sandy Gold Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.saGoldHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), mod_InfiTools.sandstoneRod
                });
        mod_BalkonInfiWeapons.bGoldHalberd.setIconCoord(5, 4);
        ModLoader.addName(mod_BalkonInfiWeapons.bGoldHalberd, "Necrotic Gold Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bGoldHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bGoldHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_BalkonInfiWeapons.mGoldHalberd.setIconCoord(6, 4);
        ModLoader.addName(mod_BalkonInfiWeapons.mGoldHalberd, "Mossy Gold Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.mGoldHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), mod_InfiTools.mossyRod
                });
        mod_BalkonInfiWeapons.nGoldHalberd.setIconCoord(7, 4);
        ModLoader.addName(mod_BalkonInfiWeapons.nGoldHalberd, "Bloody Gold Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.nGoldHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_BalkonInfiWeapons.glGoldHalberd.setIconCoord(8, 4);
        ModLoader.addName(mod_BalkonInfiWeapons.glGoldHalberd, "Glowing Gold Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.glGoldHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_BalkonInfiWeapons.iceGoldHalberd.setIconCoord(9, 4);
        ModLoader.addName(mod_BalkonInfiWeapons.iceGoldHalberd, "Icy Gold Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.iceGoldHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), mod_InfiTools.iceRod
                });
        mod_BalkonInfiWeapons.sGoldHalberd.setIconCoord(10, 4);
        ModLoader.addName(mod_BalkonInfiWeapons.sGoldHalberd, "Slimy Gold Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.sGoldHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_BalkonInfiWeapons.fGoldHalberd.setIconCoord(11, 4);
        ModLoader.addName(mod_BalkonInfiWeapons.fGoldHalberd, "Flaky Gold Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.fGoldHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), mod_InfiTools.flintRod
                });
        mod_BalkonInfiWeapons.wRedstoneHalberd.setIconCoord(0, 5);
        ModLoader.addName(mod_BalkonInfiWeapons.wRedstoneHalberd, "Redstone Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.wRedstoneHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), Item.stick
                });
        mod_BalkonInfiWeapons.stRedstoneHalberd.setIconCoord(1, 5);
        ModLoader.addName(mod_BalkonInfiWeapons.stRedstoneHalberd, "Stony Redstone Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.stRedstoneHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_BalkonInfiWeapons.iRedstoneHalberd.setIconCoord(2, 5);
        ModLoader.addName(mod_BalkonInfiWeapons.iRedstoneHalberd, "Hard Redstone Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.iRedstoneHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), mod_InfiTools.ironRod
                });
        mod_BalkonInfiWeapons.dRedstoneHalberd.setIconCoord(3, 5);
        ModLoader.addName(mod_BalkonInfiWeapons.dRedstoneHalberd, "Jeweled Redstone Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.dRedstoneHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), mod_InfiTools.diamondRod
                });
        mod_BalkonInfiWeapons.rRedstoneHalberd.setIconCoord(4, 5);
        ModLoader.addName(mod_BalkonInfiWeapons.rRedstoneHalberd, "Redredred Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.rRedstoneHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), mod_InfiTools.redstoneRod
                });
        mod_BalkonInfiWeapons.oRedstoneHalberd.setIconCoord(5, 5);
        ModLoader.addName(mod_BalkonInfiWeapons.oRedstoneHalberd, "Glassy Redstone Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.oRedstoneHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), mod_InfiTools.obsidianRod
                });
        mod_BalkonInfiWeapons.bRedstoneHalberd.setIconCoord(6, 5);
        ModLoader.addName(mod_BalkonInfiWeapons.bRedstoneHalberd, "Necrotic Redstone Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bRedstoneHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bRedstoneHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_BalkonInfiWeapons.mRedstoneHalberd.setIconCoord(7, 5);
        ModLoader.addName(mod_BalkonInfiWeapons.mRedstoneHalberd, "Mossy Redstone Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.mRedstoneHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), mod_InfiTools.mossyRod
                });
        mod_BalkonInfiWeapons.glRedstoneHalberd.setIconCoord(8, 5);
        ModLoader.addName(mod_BalkonInfiWeapons.glRedstoneHalberd, "Glowing Redstone Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.glRedstoneHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_BalkonInfiWeapons.sRedstoneHalberd.setIconCoord(9, 5);
        ModLoader.addName(mod_BalkonInfiWeapons.sRedstoneHalberd, "Slimy Redstone Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.sRedstoneHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_BalkonInfiWeapons.blRedstoneHalberd.setIconCoord(10, 5);
        ModLoader.addName(mod_BalkonInfiWeapons.blRedstoneHalberd, "Blazing Redstone Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.blRedstoneHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), Item.blazeRod
                });
        mod_BalkonInfiWeapons.wObsidianHalberd.setIconCoord(0, 6);
        ModLoader.addName(mod_BalkonInfiWeapons.wObsidianHalberd, "Obsidian Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.wObsidianHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), Item.stick
                });
        mod_BalkonInfiWeapons.stObsidianHalberd.setIconCoord(1, 6);
        ModLoader.addName(mod_BalkonInfiWeapons.stObsidianHalberd, "Stony Obsidian Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.stObsidianHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_BalkonInfiWeapons.iObsidianHalberd.setIconCoord(2, 6);
        ModLoader.addName(mod_BalkonInfiWeapons.iObsidianHalberd, "Hard Obsidian Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.iObsidianHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), mod_InfiTools.ironRod
                });
        mod_BalkonInfiWeapons.dObsidianHalberd.setIconCoord(3, 6);
        ModLoader.addName(mod_BalkonInfiWeapons.dObsidianHalberd, "Jeweled Obsidian Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.dObsidianHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), mod_InfiTools.diamondRod
                });
        mod_BalkonInfiWeapons.gObsidianHalberd.setIconCoord(4, 6);
        ModLoader.addName(mod_BalkonInfiWeapons.gObsidianHalberd, "Expensive Obsidian Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.gObsidianHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), mod_InfiTools.goldRod
                });
        mod_BalkonInfiWeapons.rObsidianHalberd.setIconCoord(5, 6);
        ModLoader.addName(mod_BalkonInfiWeapons.rObsidianHalberd, "Red Obsidian Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.rObsidianHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), mod_InfiTools.redstoneRod
                });
        mod_BalkonInfiWeapons.oObsidianHalberd.setIconCoord(6, 6);
        ModLoader.addName(mod_BalkonInfiWeapons.oObsidianHalberd, "Wicked Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.oObsidianHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), mod_InfiTools.obsidianRod
                });
        mod_BalkonInfiWeapons.bObsidianHalberd.setIconCoord(7, 6);
        ModLoader.addName(mod_BalkonInfiWeapons.bObsidianHalberd, "Necrotic Obsidian Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bObsidianHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bObsidianHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_BalkonInfiWeapons.nObsidianHalberd.setIconCoord(8, 6);
        ModLoader.addName(mod_BalkonInfiWeapons.nObsidianHalberd, "Bloody Obsidian Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.nObsidianHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_BalkonInfiWeapons.glObsidianHalberd.setIconCoord(9, 6);
        ModLoader.addName(mod_BalkonInfiWeapons.glObsidianHalberd, "Glowing Obsidian Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.glObsidianHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_BalkonInfiWeapons.sObsidianHalberd.setIconCoord(10, 6);
        ModLoader.addName(mod_BalkonInfiWeapons.sObsidianHalberd, "Slimy Obsidian Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.sObsidianHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_BalkonInfiWeapons.fObsidianHalberd.setIconCoord(11, 6);
        ModLoader.addName(mod_BalkonInfiWeapons.fObsidianHalberd, "Flaky Obsidian Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.fObsidianHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), mod_InfiTools.flintRod
                });
        mod_BalkonInfiWeapons.blObsidianHalberd.setIconCoord(12, 6);
        ModLoader.addName(mod_BalkonInfiWeapons.blObsidianHalberd, "Blazing Obsidian Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.blObsidianHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), Item.blazeRod
                });
        mod_BalkonInfiWeapons.wSandstoneHalberd.setIconCoord(0, 7);
        ModLoader.addName(mod_BalkonInfiWeapons.wSandstoneHalberd, "Sandstone Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.wSandstoneHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), Item.stick
                });
        mod_BalkonInfiWeapons.stSandstoneHalberd.setIconCoord(1, 7);
        ModLoader.addName(mod_BalkonInfiWeapons.stSandstoneHalberd, "Stony Sandstone Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.stSandstoneHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_BalkonInfiWeapons.saSandstoneHalberd.setIconCoord(2, 7);
        ModLoader.addName(mod_BalkonInfiWeapons.saSandstoneHalberd, "Sandstreak");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.saSandstoneHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), mod_InfiTools.sandstoneRod
                });
        mod_BalkonInfiWeapons.bSandstoneHalberd.setIconCoord(3, 7);
        ModLoader.addName(mod_BalkonInfiWeapons.bSandstoneHalberd, "Necrotic Sandstone Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bSandstoneHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bSandstoneHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_BalkonInfiWeapons.pSandstoneHalberd.setIconCoord(4, 7);
        ModLoader.addName(mod_BalkonInfiWeapons.pSandstoneHalberd, "Fibery Sandstone Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.pSandstoneHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), mod_InfiTools.paperRod
                });
        mod_BalkonInfiWeapons.nSandstoneHalberd.setIconCoord(5, 7);
        ModLoader.addName(mod_BalkonInfiWeapons.nSandstoneHalberd, "Bloody Sandstone Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.nSandstoneHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_BalkonInfiWeapons.iceSandstoneHalberd.setIconCoord(6, 7);
        ModLoader.addName(mod_BalkonInfiWeapons.iceSandstoneHalberd, "Icy Sandstone Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.iceSandstoneHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), mod_InfiTools.iceRod
                });
        mod_BalkonInfiWeapons.sSandstoneHalberd.setIconCoord(7, 7);
        ModLoader.addName(mod_BalkonInfiWeapons.sSandstoneHalberd, "Slimy Sandstone Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.sSandstoneHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_BalkonInfiWeapons.cSandstoneHalberd.setIconCoord(8, 7);
        ModLoader.addName(mod_BalkonInfiWeapons.cSandstoneHalberd, "Spiny Sandstone Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.cSandstoneHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_BalkonInfiWeapons.fSandstoneHalberd.setIconCoord(9, 7);
        ModLoader.addName(mod_BalkonInfiWeapons.fSandstoneHalberd, "Flaky Sandstone Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.fSandstoneHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), mod_InfiTools.flintRod
                });
        mod_BalkonInfiWeapons.brSandstoneHalberd.setIconCoord(10, 7);
        ModLoader.addName(mod_BalkonInfiWeapons.brSandstoneHalberd, "Baked Sandstone Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.brSandstoneHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), mod_InfiTools.brickRod
                });
        mod_BalkonInfiWeapons.wBoneHalberd.setIconCoord(0, 8);
        ModLoader.addName(mod_BalkonInfiWeapons.wBoneHalberd, "Bone Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.wBoneHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Item.bone, Character.valueOf('|'), Item.stick
                });
        mod_BalkonInfiWeapons.stBoneHalberd.setIconCoord(1, 8);
        ModLoader.addName(mod_BalkonInfiWeapons.stBoneHalberd, "Stony Bone Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.stBoneHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_BalkonInfiWeapons.iBoneHalberd.setIconCoord(2, 8);
        ModLoader.addName(mod_BalkonInfiWeapons.iBoneHalberd, "Hard Bone Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.iBoneHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.ironRod
                });
        mod_BalkonInfiWeapons.dBoneHalberd.setIconCoord(3, 8);
        ModLoader.addName(mod_BalkonInfiWeapons.dBoneHalberd, "Jeweled Bone Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.dBoneHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.diamondRod
                });
        mod_BalkonInfiWeapons.rBoneHalberd.setIconCoord(4, 8);
        ModLoader.addName(mod_BalkonInfiWeapons.rBoneHalberd, "Red Bone Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.rBoneHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.redstoneRod
                });
        mod_BalkonInfiWeapons.oBoneHalberd.setIconCoord(5, 8);
        ModLoader.addName(mod_BalkonInfiWeapons.oBoneHalberd, "Glassy Bone Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.oBoneHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.obsidianRod
                });
        mod_BalkonInfiWeapons.bBoneHalberd.setIconCoord(6, 8);
        ModLoader.addName(mod_BalkonInfiWeapons.bBoneHalberd, "Reanimated Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bBoneHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Item.bone, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bBoneHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_BalkonInfiWeapons.mBoneHalberd.setIconCoord(7, 8);
        ModLoader.addName(mod_BalkonInfiWeapons.mBoneHalberd, "Mossy Bone Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.mBoneHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.mossyRod
                });
        mod_BalkonInfiWeapons.nBoneHalberd.setIconCoord(8, 8);
        ModLoader.addName(mod_BalkonInfiWeapons.nBoneHalberd, "Netherrack Bone Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.nBoneHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_BalkonInfiWeapons.glBoneHalberd.setIconCoord(9, 8);
        ModLoader.addName(mod_BalkonInfiWeapons.glBoneHalberd, "Glowstone Bone Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.glBoneHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_BalkonInfiWeapons.sBoneHalberd.setIconCoord(10, 8);
        ModLoader.addName(mod_BalkonInfiWeapons.sBoneHalberd, "Slimy Bone Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.sBoneHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_BalkonInfiWeapons.cBoneHalberd.setIconCoord(11, 8);
        ModLoader.addName(mod_BalkonInfiWeapons.cBoneHalberd, "Spiny Bone Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.cBoneHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_BalkonInfiWeapons.fBoneHalberd.setIconCoord(12, 8);
        ModLoader.addName(mod_BalkonInfiWeapons.fBoneHalberd, "Flaky Bone Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.fBoneHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.flintRod
                });
        mod_BalkonInfiWeapons.brBoneHalberd.setIconCoord(13, 8);
        ModLoader.addName(mod_BalkonInfiWeapons.brBoneHalberd, "Baked Bone Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.brBoneHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.brickRod
                });
        mod_BalkonInfiWeapons.blBoneHalberd.setIconCoord(14, 8);
        ModLoader.addName(mod_BalkonInfiWeapons.blBoneHalberd, "Blazing Bone Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.blBoneHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Item.bone, Character.valueOf('|'), Item.blazeRod
                });
        mod_BalkonInfiWeapons.wPaperHalberd.setIconCoord(0, 9);
        ModLoader.addName(mod_BalkonInfiWeapons.wPaperHalberd, "Paper Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.wPaperHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), mod_InfiTools.paperStack, Character.valueOf('|'), Item.stick
                });
        mod_BalkonInfiWeapons.saPaperHalberd.setIconCoord(1, 9);
        ModLoader.addName(mod_BalkonInfiWeapons.saPaperHalberd, "Stony Paper Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.saPaperHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), mod_InfiTools.paperStack, Character.valueOf('|'), mod_InfiTools.sandstoneRod
                });
        mod_BalkonInfiWeapons.bPaperHalberd.setIconCoord(2, 9);
        ModLoader.addName(mod_BalkonInfiWeapons.bPaperHalberd, "Necrotic Paper Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bPaperHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), mod_InfiTools.paperStack, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bPaperHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), mod_InfiTools.paperStack, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_BalkonInfiWeapons.pPaperHalberd.setIconCoord(3, 9);
        ModLoader.addName(mod_BalkonInfiWeapons.pPaperHalberd, "Paper Prodder");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.pPaperHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), mod_InfiTools.paperStack, Character.valueOf('|'), mod_InfiTools.paperRod
                });
        mod_BalkonInfiWeapons.sPaperHalberd.setIconCoord(4, 9);
        ModLoader.addName(mod_BalkonInfiWeapons.sPaperHalberd, "Slimy Paper Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.sPaperHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), mod_InfiTools.paperStack, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_BalkonInfiWeapons.cPaperHalberd.setIconCoord(5, 9);
        ModLoader.addName(mod_BalkonInfiWeapons.cPaperHalberd, "Spiny Paper Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.cPaperHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), mod_InfiTools.paperStack, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_BalkonInfiWeapons.brPaperHalberd.setIconCoord(6, 9);
        ModLoader.addName(mod_BalkonInfiWeapons.brPaperHalberd, "Baked Paper Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.brPaperHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), mod_InfiTools.paperStack, Character.valueOf('|'), mod_InfiTools.brickRod
                });
        mod_BalkonInfiWeapons.stMossyHalberd.setIconCoord(0, 10);
        ModLoader.addName(mod_BalkonInfiWeapons.stMossyHalberd, "Stony Moss-Covered Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.stMossyHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), mod_InfiTools.mossBallCrafted, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_BalkonInfiWeapons.dMossyHalberd.setIconCoord(1, 10);
        ModLoader.addName(mod_BalkonInfiWeapons.dMossyHalberd, "Jeweled Moss-Covered Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.dMossyHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), mod_InfiTools.mossBallCrafted, Character.valueOf('|'), mod_InfiTools.diamondRod
                });
        mod_BalkonInfiWeapons.rMossyHalberd.setIconCoord(2, 10);
        ModLoader.addName(mod_BalkonInfiWeapons.rMossyHalberd, "Red Moss-Covered Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.rMossyHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), mod_InfiTools.mossBallCrafted, Character.valueOf('|'), mod_InfiTools.redstoneRod
                });
        mod_BalkonInfiWeapons.bMossyHalberd.setIconCoord(3, 10);
        ModLoader.addName(mod_BalkonInfiWeapons.bMossyHalberd, "Necrotic Moss-Covered Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bMossyHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), mod_InfiTools.mossBallCrafted, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bMossyHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), mod_InfiTools.mossBallCrafted, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_BalkonInfiWeapons.mMossyHalberd.setIconCoord(4, 10);
        ModLoader.addName(mod_BalkonInfiWeapons.mMossyHalberd, "Living Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.mMossyHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), mod_InfiTools.mossBallCrafted, Character.valueOf('|'), mod_InfiTools.mossyRod
                });
        mod_BalkonInfiWeapons.glMossyHalberd.setIconCoord(5, 10);
        ModLoader.addName(mod_BalkonInfiWeapons.glMossyHalberd, "Glowing Moss-Covered Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.glMossyHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), mod_InfiTools.mossBallCrafted, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_BalkonInfiWeapons.wNetherrackHalberd.setIconCoord(0, 11);
        ModLoader.addName(mod_BalkonInfiWeapons.wNetherrackHalberd, "Netherrack Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.wNetherrackHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), Item.stick
                });
        mod_BalkonInfiWeapons.stNetherrackHalberd.setIconCoord(1, 11);
        ModLoader.addName(mod_BalkonInfiWeapons.stNetherrackHalberd, "Stony Netherrack Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.stNetherrackHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_BalkonInfiWeapons.iNetherrackHalberd.setIconCoord(2, 11);
        ModLoader.addName(mod_BalkonInfiWeapons.iNetherrackHalberd, "Hard Netherrack Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.iNetherrackHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.ironRod
                });
        mod_BalkonInfiWeapons.rNetherrackHalberd.setIconCoord(3, 11);
        ModLoader.addName(mod_BalkonInfiWeapons.rNetherrackHalberd, "Red Netherrack Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.rNetherrackHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.redstoneRod
                });
        mod_BalkonInfiWeapons.oNetherrackHalberd.setIconCoord(4, 11);
        ModLoader.addName(mod_BalkonInfiWeapons.oNetherrackHalberd, "Glassy Netherrack Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.oNetherrackHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.obsidianRod
                });
        mod_BalkonInfiWeapons.saNetherrackHalberd.setIconCoord(5, 11);
        ModLoader.addName(mod_BalkonInfiWeapons.saNetherrackHalberd, "Sandy Netherrack Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.saNetherrackHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.sandstoneRod
                });
        mod_BalkonInfiWeapons.bNetherrackHalberd.setIconCoord(6, 11);
        ModLoader.addName(mod_BalkonInfiWeapons.bNetherrackHalberd, "Necrotic Netherrack Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bNetherrackHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bNetherrackHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_BalkonInfiWeapons.mNetherrackHalberd.setIconCoord(7, 11);
        ModLoader.addName(mod_BalkonInfiWeapons.mNetherrackHalberd, "Mossy Netherrack Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.mNetherrackHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.mossyRod
                });
        mod_BalkonInfiWeapons.nNetherrackHalberd.setIconCoord(8, 11);
        ModLoader.addName(mod_BalkonInfiWeapons.nNetherrackHalberd, "Bloody Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.nNetherrackHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_BalkonInfiWeapons.glNetherrackHalberd.setIconCoord(9, 11);
        ModLoader.addName(mod_BalkonInfiWeapons.glNetherrackHalberd, "Glowing Netherrack Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.glNetherrackHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_BalkonInfiWeapons.iceNetherrackHalberd.setIconCoord(10, 11);
        ModLoader.addName(mod_BalkonInfiWeapons.iceNetherrackHalberd, "Icy Netherrack Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.iceNetherrackHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.iceRod
                });
        mod_BalkonInfiWeapons.sNetherrackHalberd.setIconCoord(11, 11);
        ModLoader.addName(mod_BalkonInfiWeapons.sNetherrackHalberd, "Slimy Netherrack Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.sNetherrackHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_BalkonInfiWeapons.cNetherrackHalberd.setIconCoord(12, 11);
        ModLoader.addName(mod_BalkonInfiWeapons.cNetherrackHalberd, "Spiny Netherrack Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.cNetherrackHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_BalkonInfiWeapons.fNetherrackHalberd.setIconCoord(13, 11);
        ModLoader.addName(mod_BalkonInfiWeapons.fNetherrackHalberd, "Flaky Netherrack Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.fNetherrackHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_BalkonInfiWeapons.brNetherrackHalberd.setIconCoord(14, 11);
        ModLoader.addName(mod_BalkonInfiWeapons.brNetherrackHalberd, "Baked Netherrack Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.brNetherrackHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_BalkonInfiWeapons.blNetherrackHalberd.setIconCoord(15, 11);
        ModLoader.addName(mod_BalkonInfiWeapons.blNetherrackHalberd, "Blazing Netherrack Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.blNetherrackHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_BalkonInfiWeapons.wGlowstoneHalberd.setIconCoord(0, 12);
        ModLoader.addName(mod_BalkonInfiWeapons.wGlowstoneHalberd, "Glowstone Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.wGlowstoneHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), Item.stick
                });
        mod_BalkonInfiWeapons.stGlowstoneHalberd.setIconCoord(1, 12);
        ModLoader.addName(mod_BalkonInfiWeapons.stGlowstoneHalberd, "Stony Glowstone Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.stGlowstoneHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_BalkonInfiWeapons.iGlowstoneHalberd.setIconCoord(2, 12);
        ModLoader.addName(mod_BalkonInfiWeapons.iGlowstoneHalberd, "Hard Glowstone Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.iGlowstoneHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.ironRod
                });
        mod_BalkonInfiWeapons.dGlowstoneHalberd.setIconCoord(3, 12);
        ModLoader.addName(mod_BalkonInfiWeapons.dGlowstoneHalberd, "Jeweled Glowstone Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.dGlowstoneHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.diamondRod
                });
        mod_BalkonInfiWeapons.rGlowstoneHalberd.setIconCoord(4, 12);
        ModLoader.addName(mod_BalkonInfiWeapons.rGlowstoneHalberd, "Red Glowstone Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.rGlowstoneHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.redstoneRod
                });
        mod_BalkonInfiWeapons.oGlowstoneHalberd.setIconCoord(5, 12);
        ModLoader.addName(mod_BalkonInfiWeapons.oGlowstoneHalberd, "Glassy Glowstone Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.oGlowstoneHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.obsidianRod
                });
        mod_BalkonInfiWeapons.bGlowstoneHalberd.setIconCoord(6, 12);
        ModLoader.addName(mod_BalkonInfiWeapons.bGlowstoneHalberd, "Necrotic Glowstone Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bGlowstoneHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bGlowstoneHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_BalkonInfiWeapons.mGlowstoneHalberd.setIconCoord(7, 12);
        ModLoader.addName(mod_BalkonInfiWeapons.mGlowstoneHalberd, "Mossy Glowstone Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.mGlowstoneHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.mossyRod
                });
        mod_BalkonInfiWeapons.nGlowstoneHalberd.setIconCoord(8, 12);
        ModLoader.addName(mod_BalkonInfiWeapons.nGlowstoneHalberd, "Bloody Glowstone Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.nGlowstoneHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_BalkonInfiWeapons.glGlowstoneHalberd.setIconCoord(9, 12);
        ModLoader.addName(mod_BalkonInfiWeapons.glGlowstoneHalberd, "Bright Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.glGlowstoneHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_BalkonInfiWeapons.iceGlowstoneHalberd.setIconCoord(10, 12);
        ModLoader.addName(mod_BalkonInfiWeapons.iceGlowstoneHalberd, "Icy Glowstone Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.iceGlowstoneHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.iceRod
                });
        mod_BalkonInfiWeapons.lGlowstoneHalberd.setIconCoord(11, 12);
        ModLoader.addName(mod_BalkonInfiWeapons.lGlowstoneHalberd, "Fiery Glowstone Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.lGlowstoneHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_BalkonInfiWeapons.sGlowstoneHalberd.setIconCoord(12, 12);
        ModLoader.addName(mod_BalkonInfiWeapons.sGlowstoneHalberd, "Slimy Glowstone Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.sGlowstoneHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_BalkonInfiWeapons.blGlowstoneHalberd.setIconCoord(13, 12);
        ModLoader.addName(mod_BalkonInfiWeapons.blGlowstoneHalberd, "Blazing Glowstone Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.blGlowstoneHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), Item.blazeRod
                });
        mod_BalkonInfiWeapons.wIceHalberd.setIconCoord(0, 13);
        ModLoader.addName(mod_BalkonInfiWeapons.wIceHalberd, "Ice Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.wIceHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Block.ice, Character.valueOf('|'), Item.stick
                });
        mod_BalkonInfiWeapons.stIceHalberd.setIconCoord(1, 13);
        ModLoader.addName(mod_BalkonInfiWeapons.stIceHalberd, "Stony Ice Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.stIceHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_BalkonInfiWeapons.iIceHalberd.setIconCoord(2, 13);
        ModLoader.addName(mod_BalkonInfiWeapons.iIceHalberd, "Hard Ice Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.iIceHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.ironRod
                });
        mod_BalkonInfiWeapons.dIceHalberd.setIconCoord(3, 13);
        ModLoader.addName(mod_BalkonInfiWeapons.dIceHalberd, "Jeweled Ice Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.dIceHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.diamondRod
                });
        mod_BalkonInfiWeapons.gIceHalberd.setIconCoord(4, 13);
        ModLoader.addName(mod_BalkonInfiWeapons.gIceHalberd, "Expensive Ice Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.gIceHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.goldRod
                });
        mod_BalkonInfiWeapons.rIceHalberd.setIconCoord(5, 13);
        ModLoader.addName(mod_BalkonInfiWeapons.rIceHalberd, "Red Ice Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.rIceHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.redstoneRod
                });
        mod_BalkonInfiWeapons.oIceHalberd.setIconCoord(6, 13);
        ModLoader.addName(mod_BalkonInfiWeapons.oIceHalberd, "Glassy Ice Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.oIceHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.obsidianRod
                });
        mod_BalkonInfiWeapons.saIceHalberd.setIconCoord(7, 13);
        ModLoader.addName(mod_BalkonInfiWeapons.saIceHalberd, "Sandy Ice Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.saIceHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.sandstoneRod
                });
        mod_BalkonInfiWeapons.bIceHalberd.setIconCoord(8, 13);
        ModLoader.addName(mod_BalkonInfiWeapons.bIceHalberd, "Necrotic Ice Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bIceHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Block.ice, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bIceHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_BalkonInfiWeapons.glIceHalberd.setIconCoord(9, 13);
        ModLoader.addName(mod_BalkonInfiWeapons.glIceHalberd, "Glowing Ice Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.glIceHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_BalkonInfiWeapons.iceIceHalberd.setIconCoord(10, 13);
        ModLoader.addName(mod_BalkonInfiWeapons.iceIceHalberd, "Ice Striation");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.iceIceHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.iceRod
                });
        mod_BalkonInfiWeapons.sIceHalberd.setIconCoord(11, 13);
        ModLoader.addName(mod_BalkonInfiWeapons.sIceHalberd, "Slimy Ice Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.sIceHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_BalkonInfiWeapons.cIceHalberd.setIconCoord(12, 13);
        ModLoader.addName(mod_BalkonInfiWeapons.cIceHalberd, "Spiny Ice Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.cIceHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_BalkonInfiWeapons.fIceHalberd.setIconCoord(13, 13);
        ModLoader.addName(mod_BalkonInfiWeapons.fIceHalberd, "Flaky Ice Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.fIceHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.flintRod
                });
        mod_BalkonInfiWeapons.brIceHalberd.setIconCoord(14, 13);
        ModLoader.addName(mod_BalkonInfiWeapons.brIceHalberd, "Baked Ice Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.brIceHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.brickRod
                });
        mod_BalkonInfiWeapons.dLavaHalberd.setIconCoord(0, 14);
        ModLoader.addName(mod_BalkonInfiWeapons.dLavaHalberd, "Jeweled Lava Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.dLavaHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), mod_InfiTools.lavaCrystal, Character.valueOf('|'), mod_InfiTools.diamondRod
                });
        mod_BalkonInfiWeapons.rLavaHalberd.setIconCoord(1, 14);
        ModLoader.addName(mod_BalkonInfiWeapons.rLavaHalberd, "Red Lava Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.rLavaHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), mod_InfiTools.lavaCrystal, Character.valueOf('|'), mod_InfiTools.redstoneRod
                });
        mod_BalkonInfiWeapons.bLavaHalberd.setIconCoord(2, 14);
        ModLoader.addName(mod_BalkonInfiWeapons.bLavaHalberd, "Necrotic Lava Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bLavaHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), mod_InfiTools.lavaCrystal, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bLavaHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), mod_InfiTools.lavaCrystal, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_BalkonInfiWeapons.nLavaHalberd.setIconCoord(3, 14);
        ModLoader.addName(mod_BalkonInfiWeapons.nLavaHalberd, "Bloody Lava Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.nLavaHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), mod_InfiTools.lavaCrystal, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_BalkonInfiWeapons.glLavaHalberd.setIconCoord(4, 14);
        ModLoader.addName(mod_BalkonInfiWeapons.glLavaHalberd, "Glowing Lava Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.glLavaHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), mod_InfiTools.lavaCrystal, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_BalkonInfiWeapons.lLavaHalberd.setIconCoord(5, 14);
        ModLoader.addName(mod_BalkonInfiWeapons.lLavaHalberd, "Heatpierce");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.lLavaHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), mod_InfiTools.lavaCrystal, Character.valueOf('|'), mod_InfiTools.lavaRod
                });
        mod_BalkonInfiWeapons.blLavaHalberd.setIconCoord(6, 14);
        ModLoader.addName(mod_BalkonInfiWeapons.blLavaHalberd, "Blazing Lava Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.blLavaHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), mod_InfiTools.lavaCrystal, Character.valueOf('|'), Item.blazeRod
                });
        mod_BalkonInfiWeapons.wSlimeHalberd.setIconCoord(0, 15);
        ModLoader.addName(mod_BalkonInfiWeapons.wSlimeHalberd, "Slime Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.wSlimeHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), Item.stick
                });
        mod_BalkonInfiWeapons.stSlimeHalberd.setIconCoord(1, 15);
        ModLoader.addName(mod_BalkonInfiWeapons.stSlimeHalberd, "Stony Slime Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.stSlimeHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_BalkonInfiWeapons.iSlimeHalberd.setIconCoord(2, 15);
        ModLoader.addName(mod_BalkonInfiWeapons.iSlimeHalberd, "Hard Slime Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.iSlimeHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.ironRod
                });
        mod_BalkonInfiWeapons.dSlimeHalberd.setIconCoord(3, 15);
        ModLoader.addName(mod_BalkonInfiWeapons.dSlimeHalberd, "Jeweled Slime Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.dSlimeHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.diamondRod
                });
        mod_BalkonInfiWeapons.gSlimeHalberd.setIconCoord(4, 15);
        ModLoader.addName(mod_BalkonInfiWeapons.gSlimeHalberd, "Expensive Slime Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.gSlimeHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.goldRod
                });
        mod_BalkonInfiWeapons.rSlimeHalberd.setIconCoord(5, 15);
        ModLoader.addName(mod_BalkonInfiWeapons.rSlimeHalberd, "Red Slime Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.rSlimeHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.redstoneRod
                });
        mod_BalkonInfiWeapons.oSlimeHalberd.setIconCoord(6, 15);
        ModLoader.addName(mod_BalkonInfiWeapons.oSlimeHalberd, "Glassy Slime Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.oSlimeHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.obsidianRod
                });
        mod_BalkonInfiWeapons.saSlimeHalberd.setIconCoord(7, 15);
        ModLoader.addName(mod_BalkonInfiWeapons.saSlimeHalberd, "Sandy Slime Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.saSlimeHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.sandstoneRod
                });
        mod_BalkonInfiWeapons.bSlimeHalberd.setIconCoord(8, 15);
        ModLoader.addName(mod_BalkonInfiWeapons.bSlimeHalberd, "Necrotic Slime Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bSlimeHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bSlimeHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_BalkonInfiWeapons.pSlimeHalberd.setIconCoord(9, 15);
        ModLoader.addName(mod_BalkonInfiWeapons.pSlimeHalberd, "Fibery Slime Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.pSlimeHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.paperRod
                });
        mod_BalkonInfiWeapons.mSlimeHalberd.setIconCoord(10, 15);
        ModLoader.addName(mod_BalkonInfiWeapons.mSlimeHalberd, "Mossy Slime Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.mSlimeHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.mossyRod
                });
        mod_BalkonInfiWeapons.nSlimeHalberd.setIconCoord(11, 15);
        ModLoader.addName(mod_BalkonInfiWeapons.nSlimeHalberd, "Bloody Slime Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.nSlimeHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_BalkonInfiWeapons.glSlimeHalberd.setIconCoord(12, 15);
        ModLoader.addName(mod_BalkonInfiWeapons.glSlimeHalberd, "Glowing Slime Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.glSlimeHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_BalkonInfiWeapons.iceSlimeHalberd.setIconCoord(13, 15);
        ModLoader.addName(mod_BalkonInfiWeapons.iceSlimeHalberd, "Icy Slime Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.iceSlimeHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.iceRod
                });
        mod_BalkonInfiWeapons.lSlimeHalberd.setIconCoord(14, 15);
        ModLoader.addName(mod_BalkonInfiWeapons.lSlimeHalberd, "Fiery Slime Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.lSlimeHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.lavaRod
                });
        mod_BalkonInfiWeapons.sSlimeHalberd.setIconCoord(15, 15);
        ModLoader.addName(mod_BalkonInfiWeapons.sSlimeHalberd, "Toy Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.sSlimeHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_BalkonInfiWeapons.cSlimeHalberd.setIconCoord(12, 14);
        ModLoader.addName(mod_BalkonInfiWeapons.cSlimeHalberd, "Spiny Slime Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.cSlimeHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_BalkonInfiWeapons.fSlimeHalberd.setIconCoord(13, 14);
        ModLoader.addName(mod_BalkonInfiWeapons.fSlimeHalberd, "Flaky Slime Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.fSlimeHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.flintRod
                });
        mod_BalkonInfiWeapons.brSlimeHalberd.setIconCoord(14, 14);
        ModLoader.addName(mod_BalkonInfiWeapons.brSlimeHalberd, "Baked Slime Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.brSlimeHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.brickRod
                });
        mod_BalkonInfiWeapons.blSlimeHalberd.setIconCoord(15, 14);
        ModLoader.addName(mod_BalkonInfiWeapons.blSlimeHalberd, "Blazing Slime Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.blSlimeHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), Item.blazeRod
                });
        mod_BalkonInfiWeapons.wCactusHalberd.setIconCoord(10, 0);
        ModLoader.addName(mod_BalkonInfiWeapons.wCactusHalberd, "Cactus Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.wCactusHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Block.cactus, Character.valueOf('|'), Item.stick
                });
        mod_BalkonInfiWeapons.stCactusHalberd.setIconCoord(11, 0);
        ModLoader.addName(mod_BalkonInfiWeapons.stCactusHalberd, "Stony Cactus Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.stCactusHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Block.cactus, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_BalkonInfiWeapons.saCactusHalberd.setIconCoord(12, 0);
        ModLoader.addName(mod_BalkonInfiWeapons.saCactusHalberd, "Sandy Cactus Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.saCactusHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Block.cactus, Character.valueOf('|'), mod_InfiTools.sandstoneRod
                });
        mod_BalkonInfiWeapons.bCactusHalberd.setIconCoord(13, 0);
        ModLoader.addName(mod_BalkonInfiWeapons.bCactusHalberd, "Necrotic Cactus Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bCactusHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Block.cactus, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bCactusHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Block.cactus, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_BalkonInfiWeapons.pCactusHalberd.setIconCoord(14, 0);
        ModLoader.addName(mod_BalkonInfiWeapons.pCactusHalberd, "Fibery Cactus Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.pCactusHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Block.cactus, Character.valueOf('|'), mod_InfiTools.paperRod
                });
        mod_BalkonInfiWeapons.nCactusHalberd.setIconCoord(15, 0);
        ModLoader.addName(mod_BalkonInfiWeapons.nCactusHalberd, "Bloody Cactus Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.nCactusHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Block.cactus, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_BalkonInfiWeapons.sCactusHalberd.setIconCoord(12, 1);
        ModLoader.addName(mod_BalkonInfiWeapons.sCactusHalberd, "Slimy Cactus Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.sCactusHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Block.cactus, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_BalkonInfiWeapons.cCactusHalberd.setIconCoord(13, 1);
        ModLoader.addName(mod_BalkonInfiWeapons.cCactusHalberd, "Spined Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.cCactusHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Block.cactus, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_BalkonInfiWeapons.fCactusHalberd.setIconCoord(14, 1);
        ModLoader.addName(mod_BalkonInfiWeapons.fCactusHalberd, "Flaky Cactus Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.fCactusHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Block.cactus, Character.valueOf('|'), mod_InfiTools.flintRod
                });
        mod_BalkonInfiWeapons.brCactusHalberd.setIconCoord(15, 1);
        ModLoader.addName(mod_BalkonInfiWeapons.brCactusHalberd, "Baked Cactus Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.brCactusHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Block.cactus, Character.valueOf('|'), mod_InfiTools.brickRod
                });
        mod_BalkonInfiWeapons.wFlintHalberd.setIconCoord(13, 2);
        ModLoader.addName(mod_BalkonInfiWeapons.wFlintHalberd, "Flint Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.wFlintHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Item.flint, Character.valueOf('|'), Item.stick
                });
        mod_BalkonInfiWeapons.stFlintHalberd.setIconCoord(14, 2);
        ModLoader.addName(mod_BalkonInfiWeapons.stFlintHalberd, "Stony Flint Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.stFlintHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_BalkonInfiWeapons.iFlintHalberd.setIconCoord(15, 2);
        ModLoader.addName(mod_BalkonInfiWeapons.iFlintHalberd, "Hard Flint Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.iFlintHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.ironRod
                });
        mod_BalkonInfiWeapons.gFlintHalberd.setIconCoord(12, 3);
        ModLoader.addName(mod_BalkonInfiWeapons.gFlintHalberd, "Expensive Flint Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.gFlintHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.goldRod
                });
        mod_BalkonInfiWeapons.oFlintHalberd.setIconCoord(13, 3);
        ModLoader.addName(mod_BalkonInfiWeapons.oFlintHalberd, "Glassy Flint Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.oFlintHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.obsidianRod
                });
        mod_BalkonInfiWeapons.saFlintHalberd.setIconCoord(14, 3);
        ModLoader.addName(mod_BalkonInfiWeapons.saFlintHalberd, "Sandy Flint Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.saFlintHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.sandstoneRod
                });
        mod_BalkonInfiWeapons.bFlintHalberd.setIconCoord(15, 3);
        ModLoader.addName(mod_BalkonInfiWeapons.bFlintHalberd, "Necrotic Flint Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bFlintHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Item.flint, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bFlintHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_BalkonInfiWeapons.nFlintHalberd.setIconCoord(12, 4);
        ModLoader.addName(mod_BalkonInfiWeapons.nFlintHalberd, "Bloody Flint Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.nFlintHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_BalkonInfiWeapons.iceFlintHalberd.setIconCoord(13, 4);
        ModLoader.addName(mod_BalkonInfiWeapons.iceFlintHalberd, "Icy Flint Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.iceFlintHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.iceRod
                });
        mod_BalkonInfiWeapons.sFlintHalberd.setIconCoord(14, 4);
        ModLoader.addName(mod_BalkonInfiWeapons.sFlintHalberd, "Slimy Flint Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.sFlintHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_BalkonInfiWeapons.cFlintHalberd.setIconCoord(15, 4);
        ModLoader.addName(mod_BalkonInfiWeapons.cFlintHalberd, "Spiny Flint Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.cFlintHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_BalkonInfiWeapons.fFlintHalberd.setIconCoord(11, 5);
        ModLoader.addName(mod_BalkonInfiWeapons.fFlintHalberd, "Senseless Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.fFlintHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.flintRod
                });
        mod_BalkonInfiWeapons.brFlintHalberd.setIconCoord(12, 5);
        ModLoader.addName(mod_BalkonInfiWeapons.brFlintHalberd, "Baked Flint Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.brFlintHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.brickRod
                });
        mod_BalkonInfiWeapons.blFlintHalberd.setIconCoord(13, 5);
        ModLoader.addName(mod_BalkonInfiWeapons.blFlintHalberd, "Blazing Flint Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.blFlintHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Item.flint, Character.valueOf('|'), Item.blazeRod
                });
        mod_BalkonInfiWeapons.wBrickHalberd.setIconCoord(14, 5);
        ModLoader.addName(mod_BalkonInfiWeapons.wBrickHalberd, "Brick Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.wBrickHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Item.brick, Character.valueOf('|'), Item.brick
                });
        mod_BalkonInfiWeapons.stBrickHalberd.setIconCoord(15, 5);
        ModLoader.addName(mod_BalkonInfiWeapons.stBrickHalberd, "Stony Brick Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.stBrickHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Item.brick, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_BalkonInfiWeapons.saBrickHalberd.setIconCoord(13, 6);
        ModLoader.addName(mod_BalkonInfiWeapons.saBrickHalberd, "Sandstone Brick Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.saBrickHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Item.brick, Character.valueOf('|'), mod_InfiTools.sandstoneRod
                });
        mod_BalkonInfiWeapons.bBrickHalberd.setIconCoord(14, 6);
        ModLoader.addName(mod_BalkonInfiWeapons.bBrickHalberd, "Necrotic Brick Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bBrickHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Item.brick, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bBrickHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Item.brick, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_BalkonInfiWeapons.pBrickHalberd.setIconCoord(15, 6);
        ModLoader.addName(mod_BalkonInfiWeapons.pBrickHalberd, "Fibery Brick Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.pBrickHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Item.brick, Character.valueOf('|'), mod_InfiTools.paperRod
                });
        mod_BalkonInfiWeapons.nBrickHalberd.setIconCoord(11, 7);
        ModLoader.addName(mod_BalkonInfiWeapons.nBrickHalberd, "Bloody Brick Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.nBrickHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Item.brick, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_BalkonInfiWeapons.iceBrickHalberd.setIconCoord(12, 7);
        ModLoader.addName(mod_BalkonInfiWeapons.iceBrickHalberd, "Icy Brick Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.iceBrickHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Item.brick, Character.valueOf('|'), mod_InfiTools.iceRod
                });
        mod_BalkonInfiWeapons.sBrickHalberd.setIconCoord(13, 7);
        ModLoader.addName(mod_BalkonInfiWeapons.sBrickHalberd, "Slimy Brick Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.sBrickHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Item.brick, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_BalkonInfiWeapons.cBrickHalberd.setIconCoord(14, 7);
        ModLoader.addName(mod_BalkonInfiWeapons.cBrickHalberd, "Spiny Brick Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.cBrickHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Item.brick, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_BalkonInfiWeapons.fBrickHalberd.setIconCoord(15, 7);
        ModLoader.addName(mod_BalkonInfiWeapons.fBrickHalberd, "Flaky Brick Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.fBrickHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Item.brick, Character.valueOf('|'), mod_InfiTools.flintRod
                });
        mod_BalkonInfiWeapons.brBrickHalberd.setIconCoord(15, 8);
        ModLoader.addName(mod_BalkonInfiWeapons.brBrickHalberd, "Look-alike Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.brBrickHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), Item.brick, Character.valueOf('|'), mod_InfiTools.brickRod
                });
        mod_BalkonInfiWeapons.dBlazeHalberd.setIconCoord(7, 9);
        ModLoader.addName(mod_BalkonInfiWeapons.dBlazeHalberd, "Jeweled Blaze Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.dBlazeHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), mod_InfiTools.blazeCrystal, Character.valueOf('|'), mod_InfiTools.diamondRod
                });
        mod_BalkonInfiWeapons.rBlazeHalberd.setIconCoord(8, 9);
        ModLoader.addName(mod_BalkonInfiWeapons.rBlazeHalberd, "Red Blaze Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.rBlazeHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), mod_InfiTools.blazeCrystal, Character.valueOf('|'), mod_InfiTools.redstoneRod
                });
        mod_BalkonInfiWeapons.bBlazeHalberd.setIconCoord(9, 9);
        ModLoader.addName(mod_BalkonInfiWeapons.bBlazeHalberd, "Necrotic Blaze Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bBlazeHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), mod_InfiTools.blazeCrystal, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bBlazeHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), mod_InfiTools.blazeCrystal, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_BalkonInfiWeapons.nBlazeHalberd.setIconCoord(10, 9);
        ModLoader.addName(mod_BalkonInfiWeapons.nBlazeHalberd, "Bloody Blaze Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.nBlazeHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), mod_InfiTools.blazeCrystal, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_BalkonInfiWeapons.glBlazeHalberd.setIconCoord(11, 9);
        ModLoader.addName(mod_BalkonInfiWeapons.glBlazeHalberd, "Glowing Blaze Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.glBlazeHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), mod_InfiTools.blazeCrystal, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_BalkonInfiWeapons.lBlazeHalberd.setIconCoord(12, 9);
        ModLoader.addName(mod_BalkonInfiWeapons.lBlazeHalberd, "Fiery Blaze Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.lBlazeHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), mod_InfiTools.blazeCrystal, Character.valueOf('|'), Item.blazeRod
                });
        mod_BalkonInfiWeapons.fBlazeHalberd.setIconCoord(13, 9);
        ModLoader.addName(mod_BalkonInfiWeapons.fBlazeHalberd, "Flaky Blaze Halberd");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.fBlazeHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), mod_InfiTools.blazeCrystal, Character.valueOf('|'), Item.blazeRod
                });
        mod_BalkonInfiWeapons.blBlazeHalberd.setIconCoord(14, 9);
        ModLoader.addName(mod_BalkonInfiWeapons.blBlazeHalberd, "Blazing Pigsticker");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.blBlazeHalberd, 1), new Object[]
                {
                    " mm", " |m", "|  ", Character.valueOf('m'), mod_InfiTools.blazeCrystal, Character.valueOf('|'), Item.blazeRod
                });
        return 0;
    }
}
