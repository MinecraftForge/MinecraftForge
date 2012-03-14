package net.minecraft.src.balkon;

import net.minecraft.src.*;

public class BalkRecipeFlails
{
    public BalkRecipeFlails()
    {
    }

    public static int recipeStorm()
    {
        mod_BalkonInfiWeapons.stWoodFlail.setIconCoord(1, 0);
        ModLoader.addName(mod_BalkonInfiWeapons.stWoodFlail, "Stony Wooden Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.stWoodFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Block.planks, Character.valueOf('|'), mod_InfiTools.stoneRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.saWoodFlail.setIconCoord(2, 0);
        ModLoader.addName(mod_BalkonInfiWeapons.saWoodFlail, "Sandy Wooden Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.saWoodFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Block.planks, Character.valueOf('|'), mod_InfiTools.sandstoneRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.bWoodFlail.setIconCoord(3, 0);
        ModLoader.addName(mod_BalkonInfiWeapons.bWoodFlail, "Necrotic Wooden Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bWoodFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Block.planks, Character.valueOf('|'), Item.bone, Character.valueOf('s'), Item.stick
                });
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bWoodFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Block.planks, Character.valueOf('|'), mod_InfiTools.boneRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.pWoodFlail.setIconCoord(4, 0);
        ModLoader.addName(mod_BalkonInfiWeapons.pWoodFlail, "Fibery Wooden Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.pWoodFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Block.planks, Character.valueOf('|'), Item.paper, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.nWoodFlail.setIconCoord(5, 0);
        ModLoader.addName(mod_BalkonInfiWeapons.nWoodFlail, "Bloody Wooden Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.nWoodFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Block.planks, Character.valueOf('|'), mod_InfiTools.netherrackRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.sWoodFlail.setIconCoord(6, 0);
        ModLoader.addName(mod_BalkonInfiWeapons.sWoodFlail, "Slimy Wooden Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.sWoodFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Block.planks, Character.valueOf('|'), mod_InfiTools.slimeRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.cWoodFlail.setIconCoord(7, 0);
        ModLoader.addName(mod_BalkonInfiWeapons.cWoodFlail, "Spiny Wooden Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.cWoodFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Block.planks, Character.valueOf('|'), mod_InfiTools.cactusRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.fWoodFlail.setIconCoord(8, 0);
        ModLoader.addName(mod_BalkonInfiWeapons.fWoodFlail, "Flaky Wooden Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.fWoodFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Block.planks, Character.valueOf('|'), mod_InfiTools.flintRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.brWoodFlail.setIconCoord(9, 0);
        ModLoader.addName(mod_BalkonInfiWeapons.brWoodFlail, "Baked Wooden Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.brWoodFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Block.planks, Character.valueOf('|'), mod_InfiTools.brickRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.stStoneFlail.setIconCoord(1, 1);
        ModLoader.addName(mod_BalkonInfiWeapons.stStoneFlail, "Heavy Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.stStoneFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), mod_InfiTools.stoneRod, Character.valueOf('s'), Item.silk
                });
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.stStoneFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Block.stone, Character.valueOf('|'), mod_InfiTools.stoneRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.saStoneFlail.setIconCoord(2, 1);
        ModLoader.addName(mod_BalkonInfiWeapons.saStoneFlail, "Sandy Stone Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.saStoneFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), mod_InfiTools.sandstoneRod, Character.valueOf('s'), Item.silk
                });
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.saStoneFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Block.stone, Character.valueOf('|'), mod_InfiTools.sandstoneRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.bStoneFlail.setIconCoord(3, 1);
        ModLoader.addName(mod_BalkonInfiWeapons.bStoneFlail, "Necrotic Stone Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bStoneFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), Item.bone, Character.valueOf('s'), Item.silk
                });
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bStoneFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Block.stone, Character.valueOf('|'), Item.bone, Character.valueOf('s'), Item.silk
                });
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bStoneFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), mod_InfiTools.boneRod, Character.valueOf('s'), Item.silk
                });
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bStoneFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Block.stone, Character.valueOf('|'), mod_InfiTools.boneRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.pStoneFlail.setIconCoord(4, 1);
        ModLoader.addName(mod_BalkonInfiWeapons.pStoneFlail, "Fibery Stone Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.pStoneFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), mod_InfiTools.paperRod, Character.valueOf('s'), Item.silk
                });
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.pStoneFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Block.stone, Character.valueOf('|'), mod_InfiTools.paperRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.mStoneFlail.setIconCoord(5, 1);
        ModLoader.addName(mod_BalkonInfiWeapons.mStoneFlail, "Mossy Stone Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.mStoneFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), mod_InfiTools.mossyRod, Character.valueOf('s'), Item.silk
                });
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.mStoneFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Block.stone, Character.valueOf('|'), mod_InfiTools.mossyRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.nStoneFlail.setIconCoord(6, 1);
        ModLoader.addName(mod_BalkonInfiWeapons.nStoneFlail, "Bloody Stone Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.nStoneFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), mod_InfiTools.netherrackRod, Character.valueOf('s'), Item.silk
                });
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.nStoneFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Block.stone, Character.valueOf('|'), mod_InfiTools.netherrackRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.iceStoneFlail.setIconCoord(7, 1);
        ModLoader.addName(mod_BalkonInfiWeapons.iceStoneFlail, "Icy Stone Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.iceStoneFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), mod_InfiTools.iceRod, Character.valueOf('s'), Item.silk
                });
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.iceStoneFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Block.stone, Character.valueOf('|'), mod_InfiTools.iceRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.sStoneFlail.setIconCoord(8, 1);
        ModLoader.addName(mod_BalkonInfiWeapons.sStoneFlail, "Slimy Stone Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.sStoneFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), mod_InfiTools.slimeRod, Character.valueOf('s'), Item.silk
                });
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.sStoneFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Block.stone, Character.valueOf('|'), mod_InfiTools.slimeRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.cStoneFlail.setIconCoord(9, 1);
        ModLoader.addName(mod_BalkonInfiWeapons.cStoneFlail, "Spiny Stone Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.cStoneFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), mod_InfiTools.cactusRod, Character.valueOf('s'), Item.silk
                });
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.cStoneFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Block.stone, Character.valueOf('|'), mod_InfiTools.cactusRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.fStoneFlail.setIconCoord(10, 1);
        ModLoader.addName(mod_BalkonInfiWeapons.fStoneFlail, "Flaky Stone Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.fStoneFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), mod_InfiTools.flintRod, Character.valueOf('s'), Item.silk
                });
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.fStoneFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Block.stone, Character.valueOf('|'), mod_InfiTools.flintRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.brStoneFlail.setIconCoord(11, 1);
        ModLoader.addName(mod_BalkonInfiWeapons.brStoneFlail, "Baked Stone Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.brStoneFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), mod_InfiTools.brickRod, Character.valueOf('s'), Item.silk
                });
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.brStoneFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Block.stone, Character.valueOf('|'), mod_InfiTools.brickRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.stIronFlail.setIconCoord(1, 2);
        ModLoader.addName(mod_BalkonInfiWeapons.stIronFlail, "Stony Iron Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.stIronFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), mod_InfiTools.stoneRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.iIronFlail.setIconCoord(2, 2);
        ModLoader.addName(mod_BalkonInfiWeapons.iIronFlail, "Ironic Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.iIronFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), mod_InfiTools.ironRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.dIronFlail.setIconCoord(3, 2);
        ModLoader.addName(mod_BalkonInfiWeapons.dIronFlail, "Jeweled Iron Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.dIronFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), mod_InfiTools.diamondRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.gIronFlail.setIconCoord(4, 2);
        ModLoader.addName(mod_BalkonInfiWeapons.gIronFlail, "Expensive Iron Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.gIronFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), mod_InfiTools.goldRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.rIronFlail.setIconCoord(5, 2);
        ModLoader.addName(mod_BalkonInfiWeapons.rIronFlail, "Red Iron Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.rIronFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), mod_InfiTools.redstoneRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.oIronFlail.setIconCoord(6, 2);
        ModLoader.addName(mod_BalkonInfiWeapons.oIronFlail, "Glassy Iron Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.oIronFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), mod_InfiTools.obsidianRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.bIronFlail.setIconCoord(7, 2);
        ModLoader.addName(mod_BalkonInfiWeapons.bIronFlail, "Necrotic Iron Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bIronFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), Item.bone, Character.valueOf('s'), Item.silk
                });
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bIronFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), mod_InfiTools.boneRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.nIronFlail.setIconCoord(8, 2);
        ModLoader.addName(mod_BalkonInfiWeapons.nIronFlail, "Bloody Iron Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.nIronFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), mod_InfiTools.netherrackRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.glIronFlail.setIconCoord(9, 2);
        ModLoader.addName(mod_BalkonInfiWeapons.glIronFlail, "Glowing Iron Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.glIronFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), mod_InfiTools.glowstoneRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.iceIronFlail.setIconCoord(10, 2);
        ModLoader.addName(mod_BalkonInfiWeapons.iceIronFlail, "Icy Iron Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.iceIronFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), mod_InfiTools.iceRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.sIronFlail.setIconCoord(11, 2);
        ModLoader.addName(mod_BalkonInfiWeapons.sIronFlail, "Slimy Iron Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.sIronFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), mod_InfiTools.slimeRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.blIronFlail.setIconCoord(12, 2);
        ModLoader.addName(mod_BalkonInfiWeapons.blIronFlail, "Blazing Iron Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.blIronFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), Item.blazeRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.stDiamondFlail.setIconCoord(1, 3);
        ModLoader.addName(mod_BalkonInfiWeapons.stDiamondFlail, "Stony Diamond Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.stDiamondFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), mod_InfiTools.stoneRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.iDiamondFlail.setIconCoord(2, 3);
        ModLoader.addName(mod_BalkonInfiWeapons.iDiamondFlail, "Hard Diamond Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.iDiamondFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), mod_InfiTools.ironRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.dDiamondFlail.setIconCoord(3, 3);
        ModLoader.addName(mod_BalkonInfiWeapons.dDiamondFlail, "Diamondium Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.dDiamondFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), mod_InfiTools.diamondRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.gDiamondFlail.setIconCoord(4, 3);
        ModLoader.addName(mod_BalkonInfiWeapons.gDiamondFlail, "Expensive Diamond Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.gDiamondFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), mod_InfiTools.goldRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.rDiamondFlail.setIconCoord(5, 3);
        ModLoader.addName(mod_BalkonInfiWeapons.rDiamondFlail, "Red Diamond Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.rDiamondFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), mod_InfiTools.redstoneRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.oDiamondFlail.setIconCoord(6, 3);
        ModLoader.addName(mod_BalkonInfiWeapons.oDiamondFlail, "Glassy Diamond Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.oDiamondFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), mod_InfiTools.obsidianRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.bDiamondFlail.setIconCoord(7, 3);
        ModLoader.addName(mod_BalkonInfiWeapons.bDiamondFlail, "Necrotic Diamond Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bDiamondFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), Item.bone, Character.valueOf('s'), Item.silk
                });
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bDiamondFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), mod_InfiTools.boneRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.mDiamondFlail.setIconCoord(8, 3);
        ModLoader.addName(mod_BalkonInfiWeapons.mDiamondFlail, "Mossy Diamond Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.mDiamondFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), mod_InfiTools.mossyRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.nDiamondFlail.setIconCoord(9, 3);
        ModLoader.addName(mod_BalkonInfiWeapons.nDiamondFlail, "Bloody Diamond Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.nDiamondFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), mod_InfiTools.netherrackRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.glDiamondFlail.setIconCoord(10, 3);
        ModLoader.addName(mod_BalkonInfiWeapons.glDiamondFlail, "Glowing Diamond Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.glDiamondFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), mod_InfiTools.glowstoneRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.blDiamondFlail.setIconCoord(11, 3);
        ModLoader.addName(mod_BalkonInfiWeapons.blDiamondFlail, "Blazing Diamond Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.blDiamondFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), Item.blazeRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.stGoldFlail.setIconCoord(1, 4);
        ModLoader.addName(mod_BalkonInfiWeapons.stGoldFlail, "Stony Gold Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.stGoldFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), mod_InfiTools.stoneRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.gGoldFlail.setIconCoord(2, 4);
        ModLoader.addName(mod_BalkonInfiWeapons.gGoldFlail, "Expensive Useless Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.gGoldFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), mod_InfiTools.goldRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.oGoldFlail.setIconCoord(3, 4);
        ModLoader.addName(mod_BalkonInfiWeapons.oGoldFlail, "Glassy Gold Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.oGoldFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), mod_InfiTools.obsidianRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.saGoldFlail.setIconCoord(4, 4);
        ModLoader.addName(mod_BalkonInfiWeapons.saGoldFlail, "Sandy Gold Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.saGoldFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), mod_InfiTools.sandstoneRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.bGoldFlail.setIconCoord(5, 4);
        ModLoader.addName(mod_BalkonInfiWeapons.bGoldFlail, "Necrotic Gold Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bGoldFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), Item.bone, Character.valueOf('s'), Item.silk
                });
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bGoldFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), mod_InfiTools.boneRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.mGoldFlail.setIconCoord(6, 4);
        ModLoader.addName(mod_BalkonInfiWeapons.mGoldFlail, "Mossy Gold Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.mGoldFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), mod_InfiTools.mossyRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.nGoldFlail.setIconCoord(7, 4);
        ModLoader.addName(mod_BalkonInfiWeapons.nGoldFlail, "Bloody Gold Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.nGoldFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), mod_InfiTools.netherrackRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.glGoldFlail.setIconCoord(8, 4);
        ModLoader.addName(mod_BalkonInfiWeapons.glGoldFlail, "Glowing Gold Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.glGoldFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), mod_InfiTools.glowstoneRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.iceGoldFlail.setIconCoord(9, 4);
        ModLoader.addName(mod_BalkonInfiWeapons.iceGoldFlail, "Icy Gold Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.iceGoldFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), mod_InfiTools.iceRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.sGoldFlail.setIconCoord(10, 4);
        ModLoader.addName(mod_BalkonInfiWeapons.sGoldFlail, "Slimy Gold Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.sGoldFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), mod_InfiTools.slimeRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.fGoldFlail.setIconCoord(11, 4);
        ModLoader.addName(mod_BalkonInfiWeapons.fGoldFlail, "Flaky Gold Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.fGoldFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), mod_InfiTools.flintRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.wRedstoneFlail.setIconCoord(0, 5);
        ModLoader.addName(mod_BalkonInfiWeapons.wRedstoneFlail, "Redstone Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.wRedstoneFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), Item.stick, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.stRedstoneFlail.setIconCoord(1, 5);
        ModLoader.addName(mod_BalkonInfiWeapons.stRedstoneFlail, "Stony Redstone Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.stRedstoneFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), mod_InfiTools.stoneRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.iRedstoneFlail.setIconCoord(2, 5);
        ModLoader.addName(mod_BalkonInfiWeapons.iRedstoneFlail, "Hard Redstone Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.iRedstoneFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), mod_InfiTools.ironRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.dRedstoneFlail.setIconCoord(3, 5);
        ModLoader.addName(mod_BalkonInfiWeapons.dRedstoneFlail, "Jeweled Redstone Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.dRedstoneFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), mod_InfiTools.diamondRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.rRedstoneFlail.setIconCoord(4, 5);
        ModLoader.addName(mod_BalkonInfiWeapons.rRedstoneFlail, "Redredred Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.rRedstoneFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), mod_InfiTools.redstoneRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.oRedstoneFlail.setIconCoord(5, 5);
        ModLoader.addName(mod_BalkonInfiWeapons.oRedstoneFlail, "Glassy Redstone Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.oRedstoneFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), mod_InfiTools.obsidianRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.bRedstoneFlail.setIconCoord(6, 5);
        ModLoader.addName(mod_BalkonInfiWeapons.bRedstoneFlail, "Necrotic Redstone Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bRedstoneFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), Item.bone, Character.valueOf('s'), Item.silk
                });
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bRedstoneFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), mod_InfiTools.boneRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.mRedstoneFlail.setIconCoord(7, 5);
        ModLoader.addName(mod_BalkonInfiWeapons.mRedstoneFlail, "Mossy Redstone Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.mRedstoneFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), mod_InfiTools.mossyRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.glRedstoneFlail.setIconCoord(8, 5);
        ModLoader.addName(mod_BalkonInfiWeapons.glRedstoneFlail, "Glowing Redstone Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.glRedstoneFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), mod_InfiTools.glowstoneRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.sRedstoneFlail.setIconCoord(9, 5);
        ModLoader.addName(mod_BalkonInfiWeapons.sRedstoneFlail, "Slimy Redstone Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.sRedstoneFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), mod_InfiTools.slimeRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.blRedstoneFlail.setIconCoord(10, 5);
        ModLoader.addName(mod_BalkonInfiWeapons.blRedstoneFlail, "Blazing Redstone Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.blRedstoneFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), Item.blazeRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.wObsidianFlail.setIconCoord(0, 6);
        ModLoader.addName(mod_BalkonInfiWeapons.wObsidianFlail, "Obsidian Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.wObsidianFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), Item.stick, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.stObsidianFlail.setIconCoord(1, 6);
        ModLoader.addName(mod_BalkonInfiWeapons.stObsidianFlail, "Stony Obsidian Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.stObsidianFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), mod_InfiTools.stoneRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.iObsidianFlail.setIconCoord(2, 6);
        ModLoader.addName(mod_BalkonInfiWeapons.iObsidianFlail, "Hard Obsidian Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.iObsidianFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), mod_InfiTools.ironRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.dObsidianFlail.setIconCoord(3, 6);
        ModLoader.addName(mod_BalkonInfiWeapons.dObsidianFlail, "Jeweled Obsidian Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.dObsidianFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), mod_InfiTools.diamondRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.gObsidianFlail.setIconCoord(4, 6);
        ModLoader.addName(mod_BalkonInfiWeapons.gObsidianFlail, "Expensive Obsidian Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.gObsidianFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), mod_InfiTools.goldRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.rObsidianFlail.setIconCoord(5, 6);
        ModLoader.addName(mod_BalkonInfiWeapons.rObsidianFlail, "Red Obsidian Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.rObsidianFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), mod_InfiTools.redstoneRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.oObsidianFlail.setIconCoord(6, 6);
        ModLoader.addName(mod_BalkonInfiWeapons.oObsidianFlail, "Wicked Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.oObsidianFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), mod_InfiTools.obsidianRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.bObsidianFlail.setIconCoord(7, 6);
        ModLoader.addName(mod_BalkonInfiWeapons.bObsidianFlail, "Necrotic Obsidian Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bObsidianFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), Item.bone, Character.valueOf('s'), Item.silk
                });
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bObsidianFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), mod_InfiTools.boneRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.nObsidianFlail.setIconCoord(8, 6);
        ModLoader.addName(mod_BalkonInfiWeapons.nObsidianFlail, "Bloody Obsidian Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.nObsidianFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), mod_InfiTools.netherrackRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.glObsidianFlail.setIconCoord(9, 6);
        ModLoader.addName(mod_BalkonInfiWeapons.glObsidianFlail, "Glowing Obsidian Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.glObsidianFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), mod_InfiTools.glowstoneRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.sObsidianFlail.setIconCoord(10, 6);
        ModLoader.addName(mod_BalkonInfiWeapons.sObsidianFlail, "Slimy Obsidian Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.sObsidianFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), mod_InfiTools.slimeRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.fObsidianFlail.setIconCoord(11, 6);
        ModLoader.addName(mod_BalkonInfiWeapons.fObsidianFlail, "Flaky Obsidian Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.fObsidianFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), mod_InfiTools.flintRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.blObsidianFlail.setIconCoord(12, 6);
        ModLoader.addName(mod_BalkonInfiWeapons.blObsidianFlail, "Blazing Obsidian Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.blObsidianFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), Item.blazeRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.wSandstoneFlail.setIconCoord(0, 7);
        ModLoader.addName(mod_BalkonInfiWeapons.wSandstoneFlail, "Sandstone Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.wSandstoneFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), Item.stick, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.stSandstoneFlail.setIconCoord(1, 7);
        ModLoader.addName(mod_BalkonInfiWeapons.stSandstoneFlail, "Stony Sandstone Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.stSandstoneFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), mod_InfiTools.stoneRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.saSandstoneFlail.setIconCoord(2, 7);
        ModLoader.addName(mod_BalkonInfiWeapons.saSandstoneFlail, "Sandswing");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.saSandstoneFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), mod_InfiTools.sandstoneRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.bSandstoneFlail.setIconCoord(3, 7);
        ModLoader.addName(mod_BalkonInfiWeapons.bSandstoneFlail, "Necrotic Sandstone Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bSandstoneFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), Item.bone, Character.valueOf('s'), Item.silk
                });
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bSandstoneFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), mod_InfiTools.boneRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.pSandstoneFlail.setIconCoord(4, 7);
        ModLoader.addName(mod_BalkonInfiWeapons.pSandstoneFlail, "Fibery Sandstone Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.pSandstoneFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), mod_InfiTools.paperRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.nSandstoneFlail.setIconCoord(5, 7);
        ModLoader.addName(mod_BalkonInfiWeapons.nSandstoneFlail, "Bloody Sandstone Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.nSandstoneFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), mod_InfiTools.netherrackRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.iceSandstoneFlail.setIconCoord(6, 7);
        ModLoader.addName(mod_BalkonInfiWeapons.iceSandstoneFlail, "Icy Sandstone Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.iceSandstoneFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), mod_InfiTools.iceRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.sSandstoneFlail.setIconCoord(7, 7);
        ModLoader.addName(mod_BalkonInfiWeapons.sSandstoneFlail, "Slimy Sandstone Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.sSandstoneFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), mod_InfiTools.slimeRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.cSandstoneFlail.setIconCoord(8, 7);
        ModLoader.addName(mod_BalkonInfiWeapons.cSandstoneFlail, "Spiny Sandstone Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.cSandstoneFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), mod_InfiTools.cactusRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.fSandstoneFlail.setIconCoord(9, 7);
        ModLoader.addName(mod_BalkonInfiWeapons.fSandstoneFlail, "Flaky Sandstone Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.fSandstoneFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), mod_InfiTools.flintRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.brSandstoneFlail.setIconCoord(10, 7);
        ModLoader.addName(mod_BalkonInfiWeapons.brSandstoneFlail, "Baked Sandstone Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.brSandstoneFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), mod_InfiTools.brickRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.wBoneFlail.setIconCoord(0, 8);
        ModLoader.addName(mod_BalkonInfiWeapons.wBoneFlail, "Bone Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.wBoneFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Item.bone, Character.valueOf('|'), Item.stick, Character.valueOf('s'), Item.silk, Character.valueOf('s'),
                    Item.silk
                });
        mod_BalkonInfiWeapons.stBoneFlail.setIconCoord(1, 8);
        ModLoader.addName(mod_BalkonInfiWeapons.stBoneFlail, "Stony Bone Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.stBoneFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.stoneRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.iBoneFlail.setIconCoord(2, 8);
        ModLoader.addName(mod_BalkonInfiWeapons.iBoneFlail, "Hard Bone Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.iBoneFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.ironRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.dBoneFlail.setIconCoord(3, 8);
        ModLoader.addName(mod_BalkonInfiWeapons.dBoneFlail, "Jeweled Bone Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.dBoneFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.diamondRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.rBoneFlail.setIconCoord(4, 8);
        ModLoader.addName(mod_BalkonInfiWeapons.rBoneFlail, "Red Bone Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.rBoneFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.redstoneRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.oBoneFlail.setIconCoord(5, 8);
        ModLoader.addName(mod_BalkonInfiWeapons.oBoneFlail, "Glassy Bone Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.oBoneFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.obsidianRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.bBoneFlail.setIconCoord(6, 8);
        ModLoader.addName(mod_BalkonInfiWeapons.bBoneFlail, "Reanimated Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bBoneFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Item.bone, Character.valueOf('|'), Item.bone, Character.valueOf('s'), Item.silk
                });
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bBoneFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.boneRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.mBoneFlail.setIconCoord(7, 8);
        ModLoader.addName(mod_BalkonInfiWeapons.mBoneFlail, "Mossy Bone Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.mBoneFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.mossyRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.nBoneFlail.setIconCoord(8, 8);
        ModLoader.addName(mod_BalkonInfiWeapons.nBoneFlail, "Netherrack Bone Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.nBoneFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.netherrackRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.glBoneFlail.setIconCoord(9, 8);
        ModLoader.addName(mod_BalkonInfiWeapons.glBoneFlail, "Glowstone Bone Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.glBoneFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.glowstoneRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.sBoneFlail.setIconCoord(10, 8);
        ModLoader.addName(mod_BalkonInfiWeapons.sBoneFlail, "Slimy Bone Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.sBoneFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.slimeRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.cBoneFlail.setIconCoord(11, 8);
        ModLoader.addName(mod_BalkonInfiWeapons.cBoneFlail, "Spiny Bone Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.cBoneFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.cactusRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.fBoneFlail.setIconCoord(12, 8);
        ModLoader.addName(mod_BalkonInfiWeapons.fBoneFlail, "Flaky Bone Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.fBoneFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.flintRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.brBoneFlail.setIconCoord(13, 8);
        ModLoader.addName(mod_BalkonInfiWeapons.brBoneFlail, "Baked Bone Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.brBoneFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.brickRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.blBoneFlail.setIconCoord(14, 8);
        ModLoader.addName(mod_BalkonInfiWeapons.blBoneFlail, "Blazing Bone Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.blBoneFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Item.bone, Character.valueOf('|'), Item.blazeRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.wPaperFlail.setIconCoord(0, 9);
        ModLoader.addName(mod_BalkonInfiWeapons.wPaperFlail, "Paper Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.wPaperFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), mod_InfiTools.paperStack, Character.valueOf('|'), Item.stick, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.saPaperFlail.setIconCoord(1, 9);
        ModLoader.addName(mod_BalkonInfiWeapons.saPaperFlail, "Stony Paper Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.saPaperFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), mod_InfiTools.paperStack, Character.valueOf('|'), mod_InfiTools.sandstoneRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.bPaperFlail.setIconCoord(2, 9);
        ModLoader.addName(mod_BalkonInfiWeapons.bPaperFlail, "Necrotic Paper Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bPaperFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), mod_InfiTools.paperStack, Character.valueOf('|'), Item.bone, Character.valueOf('s'), Item.silk
                });
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bPaperFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), mod_InfiTools.paperStack, Character.valueOf('|'), mod_InfiTools.boneRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.pPaperFlail.setIconCoord(3, 9);
        ModLoader.addName(mod_BalkonInfiWeapons.pPaperFlail, "Papyrus Picker");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.pPaperFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), mod_InfiTools.paperStack, Character.valueOf('|'), mod_InfiTools.paperRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.sPaperFlail.setIconCoord(4, 9);
        ModLoader.addName(mod_BalkonInfiWeapons.sPaperFlail, "Slimy Paper Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.sPaperFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), mod_InfiTools.paperStack, Character.valueOf('|'), mod_InfiTools.slimeRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.cPaperFlail.setIconCoord(5, 9);
        ModLoader.addName(mod_BalkonInfiWeapons.cPaperFlail, "Spiny Paper Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.cPaperFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), mod_InfiTools.paperStack, Character.valueOf('|'), mod_InfiTools.cactusRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.brPaperFlail.setIconCoord(6, 9);
        ModLoader.addName(mod_BalkonInfiWeapons.brPaperFlail, "Baked Paper Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.brPaperFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), mod_InfiTools.paperStack, Character.valueOf('|'), mod_InfiTools.brickRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.stMossyFlail.setIconCoord(0, 10);
        ModLoader.addName(mod_BalkonInfiWeapons.stMossyFlail, "Stony Moss-Covered Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.stMossyFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), mod_InfiTools.mossBallCrafted, Character.valueOf('|'), mod_InfiTools.stoneRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.dMossyFlail.setIconCoord(1, 10);
        ModLoader.addName(mod_BalkonInfiWeapons.dMossyFlail, "Jeweled Moss-Covered Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.dMossyFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), mod_InfiTools.mossBallCrafted, Character.valueOf('|'), mod_InfiTools.diamondRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.rMossyFlail.setIconCoord(2, 10);
        ModLoader.addName(mod_BalkonInfiWeapons.rMossyFlail, "Red Moss-Covered Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.rMossyFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), mod_InfiTools.mossBallCrafted, Character.valueOf('|'), mod_InfiTools.redstoneRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.bMossyFlail.setIconCoord(3, 10);
        ModLoader.addName(mod_BalkonInfiWeapons.bMossyFlail, "Necrotic Moss-Covered Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bMossyFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), mod_InfiTools.mossBallCrafted, Character.valueOf('|'), Item.bone, Character.valueOf('s'), Item.silk
                });
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bMossyFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), mod_InfiTools.mossBallCrafted, Character.valueOf('|'), mod_InfiTools.boneRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.mMossyFlail.setIconCoord(4, 10);
        ModLoader.addName(mod_BalkonInfiWeapons.mMossyFlail, "Living Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.mMossyFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), mod_InfiTools.mossBallCrafted, Character.valueOf('|'), mod_InfiTools.mossyRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.glMossyFlail.setIconCoord(5, 10);
        ModLoader.addName(mod_BalkonInfiWeapons.glMossyFlail, "Glowing Moss-Covered Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.glMossyFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), mod_InfiTools.mossBallCrafted, Character.valueOf('|'), mod_InfiTools.glowstoneRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.wNetherrackFlail.setIconCoord(0, 11);
        ModLoader.addName(mod_BalkonInfiWeapons.wNetherrackFlail, "Netherrack Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.wNetherrackFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), Item.stick, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.stNetherrackFlail.setIconCoord(1, 11);
        ModLoader.addName(mod_BalkonInfiWeapons.stNetherrackFlail, "Stony Netherrack Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.stNetherrackFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.stoneRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.iNetherrackFlail.setIconCoord(2, 11);
        ModLoader.addName(mod_BalkonInfiWeapons.iNetherrackFlail, "Hard Netherrack Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.iNetherrackFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.ironRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.rNetherrackFlail.setIconCoord(3, 11);
        ModLoader.addName(mod_BalkonInfiWeapons.rNetherrackFlail, "Red Netherrack Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.rNetherrackFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.redstoneRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.oNetherrackFlail.setIconCoord(4, 11);
        ModLoader.addName(mod_BalkonInfiWeapons.oNetherrackFlail, "Glassy Netherrack Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.oNetherrackFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.obsidianRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.saNetherrackFlail.setIconCoord(5, 11);
        ModLoader.addName(mod_BalkonInfiWeapons.saNetherrackFlail, "Sandy Netherrack Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.saNetherrackFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.sandstoneRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.bNetherrackFlail.setIconCoord(6, 11);
        ModLoader.addName(mod_BalkonInfiWeapons.bNetherrackFlail, "Necrotic Netherrack Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bNetherrackFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), Item.bone, Character.valueOf('s'), Item.silk
                });
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bNetherrackFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.boneRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.mNetherrackFlail.setIconCoord(7, 11);
        ModLoader.addName(mod_BalkonInfiWeapons.mNetherrackFlail, "Mossy Netherrack Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.mNetherrackFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.mossyRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.nNetherrackFlail.setIconCoord(8, 11);
        ModLoader.addName(mod_BalkonInfiWeapons.nNetherrackFlail, "Blood Ball");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.nNetherrackFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.netherrackRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.glNetherrackFlail.setIconCoord(9, 11);
        ModLoader.addName(mod_BalkonInfiWeapons.glNetherrackFlail, "Glowing Netherrack Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.glNetherrackFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.glowstoneRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.iceNetherrackFlail.setIconCoord(10, 11);
        ModLoader.addName(mod_BalkonInfiWeapons.iceNetherrackFlail, "Icy Netherrack Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.iceNetherrackFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.iceRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.sNetherrackFlail.setIconCoord(11, 11);
        ModLoader.addName(mod_BalkonInfiWeapons.sNetherrackFlail, "Slimy Netherrack Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.sNetherrackFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.slimeRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.cNetherrackFlail.setIconCoord(12, 11);
        ModLoader.addName(mod_BalkonInfiWeapons.cNetherrackFlail, "Spiny Netherrack Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.cNetherrackFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.cactusRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.fNetherrackFlail.setIconCoord(13, 11);
        ModLoader.addName(mod_BalkonInfiWeapons.fNetherrackFlail, "Flaky Netherrack Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.fNetherrackFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.cactusRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.brNetherrackFlail.setIconCoord(14, 11);
        ModLoader.addName(mod_BalkonInfiWeapons.brNetherrackFlail, "Baked Netherrack Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.brNetherrackFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.cactusRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.blNetherrackFlail.setIconCoord(15, 11);
        ModLoader.addName(mod_BalkonInfiWeapons.blNetherrackFlail, "Blazing Netherrack Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.blNetherrackFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.cactusRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.wGlowstoneFlail.setIconCoord(0, 12);
        ModLoader.addName(mod_BalkonInfiWeapons.wGlowstoneFlail, "Glowstone Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.wGlowstoneFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), Item.stick, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.stGlowstoneFlail.setIconCoord(1, 12);
        ModLoader.addName(mod_BalkonInfiWeapons.stGlowstoneFlail, "Stony Glowstone Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.stGlowstoneFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.stoneRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.iGlowstoneFlail.setIconCoord(2, 12);
        ModLoader.addName(mod_BalkonInfiWeapons.iGlowstoneFlail, "Hard Glowstone Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.iGlowstoneFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.ironRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.dGlowstoneFlail.setIconCoord(3, 12);
        ModLoader.addName(mod_BalkonInfiWeapons.dGlowstoneFlail, "Jeweled Glowstone Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.dGlowstoneFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.diamondRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.rGlowstoneFlail.setIconCoord(4, 12);
        ModLoader.addName(mod_BalkonInfiWeapons.rGlowstoneFlail, "Red Glowstone Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.rGlowstoneFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.redstoneRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.oGlowstoneFlail.setIconCoord(5, 12);
        ModLoader.addName(mod_BalkonInfiWeapons.oGlowstoneFlail, "Glassy Glowstone Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.oGlowstoneFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.obsidianRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.bGlowstoneFlail.setIconCoord(6, 12);
        ModLoader.addName(mod_BalkonInfiWeapons.bGlowstoneFlail, "Necrotic Glowstone Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bGlowstoneFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), Item.bone, Character.valueOf('s'), Item.silk
                });
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bGlowstoneFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.boneRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.mGlowstoneFlail.setIconCoord(7, 12);
        ModLoader.addName(mod_BalkonInfiWeapons.mGlowstoneFlail, "Mossy Glowstone Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.mGlowstoneFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.mossyRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.nGlowstoneFlail.setIconCoord(8, 12);
        ModLoader.addName(mod_BalkonInfiWeapons.nGlowstoneFlail, "Bloody Glowstone Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.nGlowstoneFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.netherrackRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.glGlowstoneFlail.setIconCoord(9, 12);
        ModLoader.addName(mod_BalkonInfiWeapons.glGlowstoneFlail, "Bright Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.glGlowstoneFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.glowstoneRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.iceGlowstoneFlail.setIconCoord(10, 12);
        ModLoader.addName(mod_BalkonInfiWeapons.iceGlowstoneFlail, "Icy Glowstone Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.iceGlowstoneFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.iceRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.lGlowstoneFlail.setIconCoord(11, 12);
        ModLoader.addName(mod_BalkonInfiWeapons.lGlowstoneFlail, "Fiery Glowstone Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.lGlowstoneFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.glowstoneRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.sGlowstoneFlail.setIconCoord(12, 12);
        ModLoader.addName(mod_BalkonInfiWeapons.sGlowstoneFlail, "Slimy Glowstone Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.sGlowstoneFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.slimeRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.blGlowstoneFlail.setIconCoord(13, 12);
        ModLoader.addName(mod_BalkonInfiWeapons.blGlowstoneFlail, "Blazing Glowstone Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.blGlowstoneFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), Item.blazeRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.wIceFlail.setIconCoord(0, 13);
        ModLoader.addName(mod_BalkonInfiWeapons.wIceFlail, "Ice Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.wIceFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Block.ice, Character.valueOf('|'), Item.stick, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.stIceFlail.setIconCoord(1, 13);
        ModLoader.addName(mod_BalkonInfiWeapons.stIceFlail, "Stony Ice Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.stIceFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.stoneRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.iIceFlail.setIconCoord(2, 13);
        ModLoader.addName(mod_BalkonInfiWeapons.iIceFlail, "Hard Ice Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.iIceFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.ironRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.dIceFlail.setIconCoord(3, 13);
        ModLoader.addName(mod_BalkonInfiWeapons.dIceFlail, "Jeweled Ice Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.dIceFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.diamondRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.gIceFlail.setIconCoord(4, 13);
        ModLoader.addName(mod_BalkonInfiWeapons.gIceFlail, "Expensive Ice Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.gIceFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.goldRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.rIceFlail.setIconCoord(5, 13);
        ModLoader.addName(mod_BalkonInfiWeapons.rIceFlail, "Red Ice Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.rIceFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.redstoneRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.oIceFlail.setIconCoord(6, 13);
        ModLoader.addName(mod_BalkonInfiWeapons.oIceFlail, "Glassy Ice Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.oIceFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.obsidianRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.saIceFlail.setIconCoord(7, 13);
        ModLoader.addName(mod_BalkonInfiWeapons.saIceFlail, "Sandy Ice Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.saIceFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.sandstoneRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.bIceFlail.setIconCoord(8, 13);
        ModLoader.addName(mod_BalkonInfiWeapons.bIceFlail, "Necrotic Ice Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bIceFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Block.ice, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bIceFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.boneRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.glIceFlail.setIconCoord(9, 13);
        ModLoader.addName(mod_BalkonInfiWeapons.glIceFlail, "Glowing Ice Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.glIceFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.glowstoneRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.iceIceFlail.setIconCoord(10, 13);
        ModLoader.addName(mod_BalkonInfiWeapons.iceIceFlail, "Condensed Ice Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.iceIceFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.iceRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.sIceFlail.setIconCoord(11, 13);
        ModLoader.addName(mod_BalkonInfiWeapons.sIceFlail, "Slimy Ice Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.sIceFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.slimeRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.cIceFlail.setIconCoord(12, 13);
        ModLoader.addName(mod_BalkonInfiWeapons.cIceFlail, "Spiny Ice Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.cIceFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.cactusRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.fIceFlail.setIconCoord(13, 13);
        ModLoader.addName(mod_BalkonInfiWeapons.fIceFlail, "Flaky Ice Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.fIceFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.flintRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.brIceFlail.setIconCoord(14, 13);
        ModLoader.addName(mod_BalkonInfiWeapons.brIceFlail, "Baked Ice Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.brIceFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.brickRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.dLavaFlail.setIconCoord(0, 14);
        ModLoader.addName(mod_BalkonInfiWeapons.dLavaFlail, "Jeweled Lava Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.dLavaFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), mod_InfiTools.lavaCrystal, Character.valueOf('|'), mod_InfiTools.diamondRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.rLavaFlail.setIconCoord(1, 14);
        ModLoader.addName(mod_BalkonInfiWeapons.rLavaFlail, "Red Lava Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.rLavaFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), mod_InfiTools.lavaCrystal, Character.valueOf('|'), mod_InfiTools.redstoneRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.bLavaFlail.setIconCoord(2, 14);
        ModLoader.addName(mod_BalkonInfiWeapons.bLavaFlail, "Necrotic Lava Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bLavaFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), mod_InfiTools.lavaCrystal, Character.valueOf('|'), Item.bone, Character.valueOf('s'), Item.silk
                });
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bLavaFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), mod_InfiTools.lavaCrystal, Character.valueOf('|'), mod_InfiTools.boneRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.nLavaFlail.setIconCoord(3, 14);
        ModLoader.addName(mod_BalkonInfiWeapons.nLavaFlail, "Bloody Lava Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.nLavaFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), mod_InfiTools.lavaCrystal, Character.valueOf('|'), mod_InfiTools.netherrackRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.glLavaFlail.setIconCoord(4, 14);
        ModLoader.addName(mod_BalkonInfiWeapons.glLavaFlail, "Glowing Lava Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.glLavaFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), mod_InfiTools.lavaCrystal, Character.valueOf('|'), mod_InfiTools.glowstoneRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.lLavaFlail.setIconCoord(5, 14);
        ModLoader.addName(mod_BalkonInfiWeapons.lLavaFlail, "Flaming Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.lLavaFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), mod_InfiTools.lavaCrystal, Character.valueOf('|'), mod_InfiTools.lavaRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.blLavaFlail.setIconCoord(6, 14);
        ModLoader.addName(mod_BalkonInfiWeapons.blLavaFlail, "Blazing Lava Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.blLavaFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), mod_InfiTools.lavaCrystal, Character.valueOf('|'), Item.blazeRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.wSlimeFlail.setIconCoord(0, 15);
        ModLoader.addName(mod_BalkonInfiWeapons.wSlimeFlail, "Slime Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.wSlimeFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), Item.stick, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.stSlimeFlail.setIconCoord(1, 15);
        ModLoader.addName(mod_BalkonInfiWeapons.stSlimeFlail, "Stony Slime Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.stSlimeFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.stoneRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.iSlimeFlail.setIconCoord(2, 15);
        ModLoader.addName(mod_BalkonInfiWeapons.iSlimeFlail, "Hard Slime Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.iSlimeFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.ironRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.dSlimeFlail.setIconCoord(3, 15);
        ModLoader.addName(mod_BalkonInfiWeapons.dSlimeFlail, "Jeweled Slime Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.dSlimeFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.diamondRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.gSlimeFlail.setIconCoord(4, 15);
        ModLoader.addName(mod_BalkonInfiWeapons.gSlimeFlail, "Expensive Slime Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.gSlimeFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.goldRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.rSlimeFlail.setIconCoord(5, 15);
        ModLoader.addName(mod_BalkonInfiWeapons.rSlimeFlail, "Red Slime Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.rSlimeFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.redstoneRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.oSlimeFlail.setIconCoord(6, 15);
        ModLoader.addName(mod_BalkonInfiWeapons.oSlimeFlail, "Glassy Slime Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.oSlimeFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.obsidianRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.saSlimeFlail.setIconCoord(7, 15);
        ModLoader.addName(mod_BalkonInfiWeapons.saSlimeFlail, "Sandy Slime Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.saSlimeFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.sandstoneRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.bSlimeFlail.setIconCoord(8, 15);
        ModLoader.addName(mod_BalkonInfiWeapons.bSlimeFlail, "Necrotic Slime Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bSlimeFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), Item.bone, Character.valueOf('s'), Item.silk
                });
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bSlimeFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.boneRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.pSlimeFlail.setIconCoord(9, 15);
        ModLoader.addName(mod_BalkonInfiWeapons.pSlimeFlail, "Fibery Slime Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.pSlimeFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.paperRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.mSlimeFlail.setIconCoord(10, 15);
        ModLoader.addName(mod_BalkonInfiWeapons.mSlimeFlail, "Mossy Slime Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.mSlimeFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.mossyRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.nSlimeFlail.setIconCoord(11, 15);
        ModLoader.addName(mod_BalkonInfiWeapons.nSlimeFlail, "Bloody Slime Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.nSlimeFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.netherrackRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.glSlimeFlail.setIconCoord(12, 15);
        ModLoader.addName(mod_BalkonInfiWeapons.glSlimeFlail, "Glowing Slime Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.glSlimeFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.glowstoneRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.iceSlimeFlail.setIconCoord(13, 15);
        ModLoader.addName(mod_BalkonInfiWeapons.iceSlimeFlail, "Icy Slime Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.iceSlimeFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.iceRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.lSlimeFlail.setIconCoord(14, 15);
        ModLoader.addName(mod_BalkonInfiWeapons.lSlimeFlail, "Fiery Slime Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.lSlimeFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.lavaRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.sSlimeFlail.setIconCoord(15, 15);
        ModLoader.addName(mod_BalkonInfiWeapons.sSlimeFlail, "Rubberized Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.sSlimeFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.slimeRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.cSlimeFlail.setIconCoord(12, 14);
        ModLoader.addName(mod_BalkonInfiWeapons.cSlimeFlail, "Spiny Slime Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.cSlimeFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.cactusRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.fSlimeFlail.setIconCoord(13, 14);
        ModLoader.addName(mod_BalkonInfiWeapons.fSlimeFlail, "Flaky Slime Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.fSlimeFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.flintRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.brSlimeFlail.setIconCoord(14, 14);
        ModLoader.addName(mod_BalkonInfiWeapons.brSlimeFlail, "Baked Slime Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.brSlimeFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.brickRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.blSlimeFlail.setIconCoord(15, 14);
        ModLoader.addName(mod_BalkonInfiWeapons.blSlimeFlail, "Blazing Slime Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.blSlimeFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), Item.blazeRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.wCactusFlail.setIconCoord(10, 0);
        ModLoader.addName(mod_BalkonInfiWeapons.wCactusFlail, "Cactus Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.wCactusFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Block.cactus, Character.valueOf('|'), Item.stick, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.stCactusFlail.setIconCoord(11, 0);
        ModLoader.addName(mod_BalkonInfiWeapons.stCactusFlail, "Stony Cactus Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.stCactusFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Block.cactus, Character.valueOf('|'), mod_InfiTools.stoneRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.saCactusFlail.setIconCoord(12, 0);
        ModLoader.addName(mod_BalkonInfiWeapons.saCactusFlail, "Sandy Cactus Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.saCactusFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Block.cactus, Character.valueOf('|'), mod_InfiTools.sandstoneRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.bCactusFlail.setIconCoord(13, 0);
        ModLoader.addName(mod_BalkonInfiWeapons.bCactusFlail, "Necrotic Cactus Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bCactusFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Block.cactus, Character.valueOf('|'), Item.bone, Character.valueOf('s'), Item.silk
                });
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bCactusFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Block.cactus, Character.valueOf('|'), mod_InfiTools.boneRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.pCactusFlail.setIconCoord(14, 0);
        ModLoader.addName(mod_BalkonInfiWeapons.pCactusFlail, "Fibery Cactus Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.pCactusFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Block.cactus, Character.valueOf('|'), mod_InfiTools.paperRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.nCactusFlail.setIconCoord(15, 0);
        ModLoader.addName(mod_BalkonInfiWeapons.nCactusFlail, "Bloody Cactus Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.nCactusFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Block.cactus, Character.valueOf('|'), mod_InfiTools.netherrackRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.sCactusFlail.setIconCoord(12, 1);
        ModLoader.addName(mod_BalkonInfiWeapons.sCactusFlail, "Slimy Cactus Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.sCactusFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Block.cactus, Character.valueOf('|'), mod_InfiTools.slimeRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.cCactusFlail.setIconCoord(13, 1);
        ModLoader.addName(mod_BalkonInfiWeapons.cCactusFlail, "Spined Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.cCactusFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Block.cactus, Character.valueOf('|'), mod_InfiTools.cactusRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.fCactusFlail.setIconCoord(14, 1);
        ModLoader.addName(mod_BalkonInfiWeapons.fCactusFlail, "Flaky Cactus Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.fCactusFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Block.cactus, Character.valueOf('|'), mod_InfiTools.flintRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.brCactusFlail.setIconCoord(15, 1);
        ModLoader.addName(mod_BalkonInfiWeapons.brCactusFlail, "Baked Cactus Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.brCactusFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Block.cactus, Character.valueOf('|'), mod_InfiTools.brickRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.wFlintFlail.setIconCoord(13, 2);
        ModLoader.addName(mod_BalkonInfiWeapons.wFlintFlail, "Flint Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.wFlintFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Item.flint, Character.valueOf('|'), Item.stick, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.stFlintFlail.setIconCoord(14, 2);
        ModLoader.addName(mod_BalkonInfiWeapons.stFlintFlail, "Stony Flint Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.stFlintFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.stoneRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.iFlintFlail.setIconCoord(15, 2);
        ModLoader.addName(mod_BalkonInfiWeapons.iFlintFlail, "Hard Flint Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.iFlintFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.ironRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.gFlintFlail.setIconCoord(12, 3);
        ModLoader.addName(mod_BalkonInfiWeapons.gFlintFlail, "Expensive Flint Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.gFlintFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.goldRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.oFlintFlail.setIconCoord(13, 3);
        ModLoader.addName(mod_BalkonInfiWeapons.oFlintFlail, "Glassy Flint Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.oFlintFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.obsidianRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.saFlintFlail.setIconCoord(14, 3);
        ModLoader.addName(mod_BalkonInfiWeapons.saFlintFlail, "Sandy Flint Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.saFlintFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.sandstoneRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.bFlintFlail.setIconCoord(15, 3);
        ModLoader.addName(mod_BalkonInfiWeapons.bFlintFlail, "Necrotic Flint Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bFlintFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Item.flint, Character.valueOf('|'), Item.bone, Character.valueOf('s'), Item.silk
                });
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bFlintFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.boneRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.nFlintFlail.setIconCoord(12, 4);
        ModLoader.addName(mod_BalkonInfiWeapons.nFlintFlail, "Bloody Flint Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.nFlintFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.netherrackRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.iceFlintFlail.setIconCoord(13, 4);
        ModLoader.addName(mod_BalkonInfiWeapons.iceFlintFlail, "Icy Flint Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.iceFlintFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.iceRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.sFlintFlail.setIconCoord(14, 4);
        ModLoader.addName(mod_BalkonInfiWeapons.sFlintFlail, "Slimy Flint Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.sFlintFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.slimeRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.cFlintFlail.setIconCoord(15, 4);
        ModLoader.addName(mod_BalkonInfiWeapons.cFlintFlail, "Spiny Flint Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.cFlintFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.cactusRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.fFlintFlail.setIconCoord(11, 5);
        ModLoader.addName(mod_BalkonInfiWeapons.fFlintFlail, "Shale Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.fFlintFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.flintRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.brFlintFlail.setIconCoord(12, 5);
        ModLoader.addName(mod_BalkonInfiWeapons.brFlintFlail, "Baked Flint Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.brFlintFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.brickRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.blFlintFlail.setIconCoord(13, 5);
        ModLoader.addName(mod_BalkonInfiWeapons.blFlintFlail, "Blazing Flint Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.blFlintFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Item.flint, Character.valueOf('|'), Item.blazeRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.wBrickFlail.setIconCoord(14, 5);
        ModLoader.addName(mod_BalkonInfiWeapons.wBrickFlail, "Brick Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.wBrickFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Item.brick, Character.valueOf('|'), Item.brick, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.stBrickFlail.setIconCoord(15, 5);
        ModLoader.addName(mod_BalkonInfiWeapons.stBrickFlail, "Stony Brick Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.stBrickFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Item.brick, Character.valueOf('|'), mod_InfiTools.stoneRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.saBrickFlail.setIconCoord(13, 6);
        ModLoader.addName(mod_BalkonInfiWeapons.saBrickFlail, "Sandstone Brick Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.saBrickFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Item.brick, Character.valueOf('|'), mod_InfiTools.sandstoneRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.bBrickFlail.setIconCoord(14, 6);
        ModLoader.addName(mod_BalkonInfiWeapons.bBrickFlail, "Necrotic Brick Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bBrickFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Item.brick, Character.valueOf('|'), Item.bone, Character.valueOf('s'), Item.silk
                });
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bBrickFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Item.brick, Character.valueOf('|'), mod_InfiTools.boneRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.pBrickFlail.setIconCoord(15, 6);
        ModLoader.addName(mod_BalkonInfiWeapons.pBrickFlail, "Fibery Brick Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.pBrickFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Item.brick, Character.valueOf('|'), mod_InfiTools.paperRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.nBrickFlail.setIconCoord(11, 7);
        ModLoader.addName(mod_BalkonInfiWeapons.nBrickFlail, "Bloody Brick Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.nBrickFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Item.brick, Character.valueOf('|'), mod_InfiTools.netherrackRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.iceBrickFlail.setIconCoord(12, 7);
        ModLoader.addName(mod_BalkonInfiWeapons.iceBrickFlail, "Icy Brick Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.iceBrickFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Item.brick, Character.valueOf('|'), mod_InfiTools.iceRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.sBrickFlail.setIconCoord(13, 7);
        ModLoader.addName(mod_BalkonInfiWeapons.sBrickFlail, "Slimy Brick Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.sBrickFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Item.brick, Character.valueOf('|'), mod_InfiTools.slimeRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.cBrickFlail.setIconCoord(14, 7);
        ModLoader.addName(mod_BalkonInfiWeapons.cBrickFlail, "Spiny Brick Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.cBrickFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Item.brick, Character.valueOf('|'), mod_InfiTools.cactusRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.fBrickFlail.setIconCoord(15, 7);
        ModLoader.addName(mod_BalkonInfiWeapons.fBrickFlail, "Flaky Brick Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.fBrickFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Item.brick, Character.valueOf('|'), mod_InfiTools.flintRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.brBrickFlail.setIconCoord(15, 8);
        ModLoader.addName(mod_BalkonInfiWeapons.brBrickFlail, "Look-alike Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.brBrickFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), Item.brick, Character.valueOf('|'), mod_InfiTools.brickRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.dBlazeFlail.setIconCoord(7, 9);
        ModLoader.addName(mod_BalkonInfiWeapons.dBlazeFlail, "Jeweled Blaze Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.dBlazeFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), mod_InfiTools.blazeCrystal, Character.valueOf('|'), mod_InfiTools.diamondRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.rBlazeFlail.setIconCoord(8, 9);
        ModLoader.addName(mod_BalkonInfiWeapons.rBlazeFlail, "Red Blaze Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.rBlazeFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), mod_InfiTools.blazeCrystal, Character.valueOf('|'), mod_InfiTools.redstoneRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.bBlazeFlail.setIconCoord(9, 9);
        ModLoader.addName(mod_BalkonInfiWeapons.bBlazeFlail, "Necrotic Blaze Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bBlazeFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), mod_InfiTools.blazeCrystal, Character.valueOf('|'), Item.bone, Character.valueOf('s'), Item.silk
                });
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bBlazeFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), mod_InfiTools.blazeCrystal, Character.valueOf('|'), mod_InfiTools.boneRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.nBlazeFlail.setIconCoord(10, 9);
        ModLoader.addName(mod_BalkonInfiWeapons.nBlazeFlail, "Bloody Blaze Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.nBlazeFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), mod_InfiTools.blazeCrystal, Character.valueOf('|'), mod_InfiTools.netherrackRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.glBlazeFlail.setIconCoord(11, 9);
        ModLoader.addName(mod_BalkonInfiWeapons.glBlazeFlail, "Glowing Blaze Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.glBlazeFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), mod_InfiTools.blazeCrystal, Character.valueOf('|'), mod_InfiTools.glowstoneRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.lBlazeFlail.setIconCoord(12, 9);
        ModLoader.addName(mod_BalkonInfiWeapons.lBlazeFlail, "Fiery Blaze Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.lBlazeFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), mod_InfiTools.blazeCrystal, Character.valueOf('|'), Item.blazeRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.fBlazeFlail.setIconCoord(13, 9);
        ModLoader.addName(mod_BalkonInfiWeapons.fBlazeFlail, "Flaky Blaze Flail");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.fBlazeFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), mod_InfiTools.blazeCrystal, Character.valueOf('|'), Item.blazeRod, Character.valueOf('s'), Item.silk
                });
        mod_BalkonInfiWeapons.blBlazeFlail.setIconCoord(14, 9);
        ModLoader.addName(mod_BalkonInfiWeapons.blBlazeFlail, "Blasting Ball");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.blBlazeFlail, 1), new Object[]
                {
                    "  s", " |s", "| m", Character.valueOf('m'), mod_InfiTools.blazeCrystal, Character.valueOf('|'), Item.blazeRod, Character.valueOf('s'), Item.silk
                });
        return 0;
    }
}
