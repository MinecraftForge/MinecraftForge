package net.minecraft.src.balkon;

import net.minecraft.src.*;

public class BalkRecipeWarhammers
{
    public BalkRecipeWarhammers()
    {
    }

    public static int recipeStorm()
    {
        mod_BalkonInfiWeapons.stWoodWarhammer.setIconCoord(1, 0);
        ModLoader.addName(mod_BalkonInfiWeapons.stWoodWarhammer, "Stony Wooden Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.stWoodWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Block.planks, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_BalkonInfiWeapons.saWoodWarhammer.setIconCoord(2, 0);
        ModLoader.addName(mod_BalkonInfiWeapons.saWoodWarhammer, "Sandy Wooden Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.saWoodWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Block.planks, Character.valueOf('|'), mod_InfiTools.sandstoneRod
                });
        mod_BalkonInfiWeapons.bWoodWarhammer.setIconCoord(3, 0);
        ModLoader.addName(mod_BalkonInfiWeapons.bWoodWarhammer, "Necrotic Wooden Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bWoodWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Block.planks, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bWoodWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Block.planks, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_BalkonInfiWeapons.pWoodWarhammer.setIconCoord(4, 0);
        ModLoader.addName(mod_BalkonInfiWeapons.pWoodWarhammer, "Fibery Wooden Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.pWoodWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Block.planks, Character.valueOf('|'), mod_InfiTools.paperRod
                });
        mod_BalkonInfiWeapons.nWoodWarhammer.setIconCoord(5, 0);
        ModLoader.addName(mod_BalkonInfiWeapons.nWoodWarhammer, "Bloody Wooden Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.nWoodWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Block.planks, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_BalkonInfiWeapons.sWoodWarhammer.setIconCoord(6, 0);
        ModLoader.addName(mod_BalkonInfiWeapons.sWoodWarhammer, "Slimy Wooden Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.sWoodWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Block.planks, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_BalkonInfiWeapons.cWoodWarhammer.setIconCoord(7, 0);
        ModLoader.addName(mod_BalkonInfiWeapons.cWoodWarhammer, "Spiny Wooden Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.cWoodWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Block.planks, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_BalkonInfiWeapons.fWoodWarhammer.setIconCoord(8, 0);
        ModLoader.addName(mod_BalkonInfiWeapons.fWoodWarhammer, "Flaky Wooden Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.fWoodWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Block.planks, Character.valueOf('|'), mod_InfiTools.flintRod
                });
        mod_BalkonInfiWeapons.brWoodWarhammer.setIconCoord(9, 0);
        ModLoader.addName(mod_BalkonInfiWeapons.brWoodWarhammer, "Baked Wooden Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.brWoodWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Block.planks, Character.valueOf('|'), mod_InfiTools.brickRod
                });
        mod_BalkonInfiWeapons.stStoneWarhammer.setIconCoord(1, 1);
        ModLoader.addName(mod_BalkonInfiWeapons.stStoneWarhammer, "Heavy Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.stStoneWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.stStoneWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Block.stone, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_BalkonInfiWeapons.saStoneWarhammer.setIconCoord(2, 1);
        ModLoader.addName(mod_BalkonInfiWeapons.saStoneWarhammer, "Sandy Stone Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.saStoneWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), mod_InfiTools.sandstoneRod
                });
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.saStoneWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Block.stone, Character.valueOf('|'), mod_InfiTools.sandstoneRod
                });
        mod_BalkonInfiWeapons.bStoneWarhammer.setIconCoord(3, 1);
        ModLoader.addName(mod_BalkonInfiWeapons.bStoneWarhammer, "Necrotic Stone Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bStoneWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bStoneWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Block.stone, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bStoneWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bStoneWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Block.stone, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_BalkonInfiWeapons.pStoneWarhammer.setIconCoord(4, 1);
        ModLoader.addName(mod_BalkonInfiWeapons.pStoneWarhammer, "Fibery Stone Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.pStoneWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), mod_InfiTools.paperRod
                });
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.pStoneWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Block.stone, Character.valueOf('|'), mod_InfiTools.paperRod
                });
        mod_BalkonInfiWeapons.mStoneWarhammer.setIconCoord(5, 1);
        ModLoader.addName(mod_BalkonInfiWeapons.mStoneWarhammer, "Mossy Stone Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.mStoneWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), mod_InfiTools.mossyRod
                });
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.mStoneWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Block.stone, Character.valueOf('|'), mod_InfiTools.mossyRod
                });
        mod_BalkonInfiWeapons.nStoneWarhammer.setIconCoord(6, 1);
        ModLoader.addName(mod_BalkonInfiWeapons.nStoneWarhammer, "Bloody Stone Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.nStoneWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.nStoneWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Block.stone, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_BalkonInfiWeapons.iceStoneWarhammer.setIconCoord(7, 1);
        ModLoader.addName(mod_BalkonInfiWeapons.iceStoneWarhammer, "Icy Stone Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.iceStoneWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), mod_InfiTools.iceRod
                });
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.iceStoneWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Block.stone, Character.valueOf('|'), mod_InfiTools.iceRod
                });
        mod_BalkonInfiWeapons.sStoneWarhammer.setIconCoord(8, 1);
        ModLoader.addName(mod_BalkonInfiWeapons.sStoneWarhammer, "Slimy Stone Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.sStoneWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.sStoneWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Block.stone, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_BalkonInfiWeapons.cStoneWarhammer.setIconCoord(9, 1);
        ModLoader.addName(mod_BalkonInfiWeapons.cStoneWarhammer, "Spiny Stone Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.cStoneWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.cStoneWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Block.stone, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_BalkonInfiWeapons.fStoneWarhammer.setIconCoord(10, 1);
        ModLoader.addName(mod_BalkonInfiWeapons.fStoneWarhammer, "Flaky Stone Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.fStoneWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), mod_InfiTools.flintRod
                });
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.fStoneWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Block.stone, Character.valueOf('|'), mod_InfiTools.flintRod
                });
        mod_BalkonInfiWeapons.brStoneWarhammer.setIconCoord(11, 1);
        ModLoader.addName(mod_BalkonInfiWeapons.brStoneWarhammer, "Baked Stone Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.brStoneWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), mod_InfiTools.brickRod
                });
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.brStoneWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Block.stone, Character.valueOf('|'), mod_InfiTools.brickRod
                });
        mod_BalkonInfiWeapons.stIronWarhammer.setIconCoord(1, 2);
        ModLoader.addName(mod_BalkonInfiWeapons.stIronWarhammer, "Stony Iron Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.stIronWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_BalkonInfiWeapons.iIronWarhammer.setIconCoord(2, 2);
        ModLoader.addName(mod_BalkonInfiWeapons.iIronWarhammer, "Ironic Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.iIronWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), mod_InfiTools.ironRod
                });
        mod_BalkonInfiWeapons.dIronWarhammer.setIconCoord(3, 2);
        ModLoader.addName(mod_BalkonInfiWeapons.dIronWarhammer, "Jeweled Iron Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.dIronWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), mod_InfiTools.diamondRod
                });
        mod_BalkonInfiWeapons.gIronWarhammer.setIconCoord(4, 2);
        ModLoader.addName(mod_BalkonInfiWeapons.gIronWarhammer, "Expensive Iron Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.gIronWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), mod_InfiTools.goldRod
                });
        mod_BalkonInfiWeapons.rIronWarhammer.setIconCoord(5, 2);
        ModLoader.addName(mod_BalkonInfiWeapons.rIronWarhammer, "Red Iron Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.rIronWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), mod_InfiTools.redstoneRod
                });
        mod_BalkonInfiWeapons.oIronWarhammer.setIconCoord(6, 2);
        ModLoader.addName(mod_BalkonInfiWeapons.oIronWarhammer, "Glassy Iron Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.oIronWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), mod_InfiTools.obsidianRod
                });
        mod_BalkonInfiWeapons.bIronWarhammer.setIconCoord(7, 2);
        ModLoader.addName(mod_BalkonInfiWeapons.bIronWarhammer, "Necrotic Iron Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bIronWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bIronWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_BalkonInfiWeapons.nIronWarhammer.setIconCoord(8, 2);
        ModLoader.addName(mod_BalkonInfiWeapons.nIronWarhammer, "Bloody Iron Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.nIronWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_BalkonInfiWeapons.glIronWarhammer.setIconCoord(9, 2);
        ModLoader.addName(mod_BalkonInfiWeapons.glIronWarhammer, "Glowing Iron Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.glIronWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_BalkonInfiWeapons.iceIronWarhammer.setIconCoord(10, 2);
        ModLoader.addName(mod_BalkonInfiWeapons.iceIronWarhammer, "Icy Iron Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.iceIronWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), mod_InfiTools.iceRod
                });
        mod_BalkonInfiWeapons.sIronWarhammer.setIconCoord(11, 2);
        ModLoader.addName(mod_BalkonInfiWeapons.sIronWarhammer, "Slimy Iron Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.sIronWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_BalkonInfiWeapons.blIronWarhammer.setIconCoord(12, 2);
        ModLoader.addName(mod_BalkonInfiWeapons.blIronWarhammer, "Blazing Iron Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.blIronWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), Item.blazeRod
                });
        mod_BalkonInfiWeapons.wDiamondWarhammer.setIconCoord(0, 3);
        ModLoader.addName(mod_BalkonInfiWeapons.wDiamondWarhammer, "Diamond Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.wDiamondWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), Item.stick
                });
        mod_BalkonInfiWeapons.stDiamondWarhammer.setIconCoord(1, 3);
        ModLoader.addName(mod_BalkonInfiWeapons.stDiamondWarhammer, "Stony Diamond Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.stDiamondWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_BalkonInfiWeapons.iDiamondWarhammer.setIconCoord(2, 3);
        ModLoader.addName(mod_BalkonInfiWeapons.iDiamondWarhammer, "Hard Diamond Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.iDiamondWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), mod_InfiTools.ironRod
                });
        mod_BalkonInfiWeapons.dDiamondWarhammer.setIconCoord(3, 3);
        ModLoader.addName(mod_BalkonInfiWeapons.dDiamondWarhammer, "Diamondium Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.dDiamondWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), mod_InfiTools.diamondRod
                });
        mod_BalkonInfiWeapons.gDiamondWarhammer.setIconCoord(4, 3);
        ModLoader.addName(mod_BalkonInfiWeapons.gDiamondWarhammer, "Expensive Diamond Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.gDiamondWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), mod_InfiTools.goldRod
                });
        mod_BalkonInfiWeapons.rDiamondWarhammer.setIconCoord(5, 3);
        ModLoader.addName(mod_BalkonInfiWeapons.rDiamondWarhammer, "Red Diamond Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.rDiamondWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), mod_InfiTools.redstoneRod
                });
        mod_BalkonInfiWeapons.oDiamondWarhammer.setIconCoord(6, 3);
        ModLoader.addName(mod_BalkonInfiWeapons.oDiamondWarhammer, "Glassy Diamond Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.oDiamondWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), mod_InfiTools.obsidianRod
                });
        mod_BalkonInfiWeapons.bDiamondWarhammer.setIconCoord(7, 3);
        ModLoader.addName(mod_BalkonInfiWeapons.bDiamondWarhammer, "Necrotic Diamond Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bDiamondWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bDiamondWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_BalkonInfiWeapons.mDiamondWarhammer.setIconCoord(8, 3);
        ModLoader.addName(mod_BalkonInfiWeapons.mDiamondWarhammer, "Mossy Diamond Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.mDiamondWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), mod_InfiTools.mossyRod
                });
        mod_BalkonInfiWeapons.nDiamondWarhammer.setIconCoord(9, 3);
        ModLoader.addName(mod_BalkonInfiWeapons.nDiamondWarhammer, "Bloody Diamond Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.nDiamondWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_BalkonInfiWeapons.glDiamondWarhammer.setIconCoord(10, 3);
        ModLoader.addName(mod_BalkonInfiWeapons.glDiamondWarhammer, "Glowing Diamond Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.glDiamondWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_BalkonInfiWeapons.blDiamondWarhammer.setIconCoord(11, 3);
        ModLoader.addName(mod_BalkonInfiWeapons.blDiamondWarhammer, "Blazing Diamond Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.blDiamondWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), Item.blazeRod
                });
        mod_BalkonInfiWeapons.wGoldWarhammer.setIconCoord(0, 4);
        ModLoader.addName(mod_BalkonInfiWeapons.wGoldWarhammer, "Stony Gold Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.wGoldWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), Item.stick
                });
        mod_BalkonInfiWeapons.stGoldWarhammer.setIconCoord(1, 4);
        ModLoader.addName(mod_BalkonInfiWeapons.stGoldWarhammer, "Stony Gold Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.stGoldWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_BalkonInfiWeapons.gGoldWarhammer.setIconCoord(2, 4);
        ModLoader.addName(mod_BalkonInfiWeapons.gGoldWarhammer, "Expensive Useless Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.gGoldWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), mod_InfiTools.goldRod
                });
        mod_BalkonInfiWeapons.oGoldWarhammer.setIconCoord(3, 4);
        ModLoader.addName(mod_BalkonInfiWeapons.oGoldWarhammer, "Glassy Gold Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.oGoldWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), mod_InfiTools.obsidianRod
                });
        mod_BalkonInfiWeapons.saGoldWarhammer.setIconCoord(4, 4);
        ModLoader.addName(mod_BalkonInfiWeapons.saGoldWarhammer, "Sandy Gold Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.saGoldWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), mod_InfiTools.sandstoneRod
                });
        mod_BalkonInfiWeapons.bGoldWarhammer.setIconCoord(5, 4);
        ModLoader.addName(mod_BalkonInfiWeapons.bGoldWarhammer, "Necrotic Gold Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bGoldWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bGoldWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_BalkonInfiWeapons.mGoldWarhammer.setIconCoord(6, 4);
        ModLoader.addName(mod_BalkonInfiWeapons.mGoldWarhammer, "Mossy Gold Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.mGoldWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), mod_InfiTools.mossyRod
                });
        mod_BalkonInfiWeapons.nGoldWarhammer.setIconCoord(7, 4);
        ModLoader.addName(mod_BalkonInfiWeapons.nGoldWarhammer, "Bloody Gold Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.nGoldWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_BalkonInfiWeapons.glGoldWarhammer.setIconCoord(8, 4);
        ModLoader.addName(mod_BalkonInfiWeapons.glGoldWarhammer, "Glowing Gold Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.glGoldWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_BalkonInfiWeapons.iceGoldWarhammer.setIconCoord(9, 4);
        ModLoader.addName(mod_BalkonInfiWeapons.iceGoldWarhammer, "Icy Gold Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.iceGoldWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), mod_InfiTools.iceRod
                });
        mod_BalkonInfiWeapons.sGoldWarhammer.setIconCoord(10, 4);
        ModLoader.addName(mod_BalkonInfiWeapons.sGoldWarhammer, "Slimy Gold Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.sGoldWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_BalkonInfiWeapons.fGoldWarhammer.setIconCoord(11, 4);
        ModLoader.addName(mod_BalkonInfiWeapons.fGoldWarhammer, "Flaky Gold Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.fGoldWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), mod_InfiTools.flintRod
                });
        mod_BalkonInfiWeapons.wRedstoneWarhammer.setIconCoord(0, 5);
        ModLoader.addName(mod_BalkonInfiWeapons.wRedstoneWarhammer, "Redstone Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.wRedstoneWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), Item.stick
                });
        mod_BalkonInfiWeapons.stRedstoneWarhammer.setIconCoord(1, 5);
        ModLoader.addName(mod_BalkonInfiWeapons.stRedstoneWarhammer, "Stony Redstone Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.stRedstoneWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_BalkonInfiWeapons.iRedstoneWarhammer.setIconCoord(2, 5);
        ModLoader.addName(mod_BalkonInfiWeapons.iRedstoneWarhammer, "Hard Redstone Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.iRedstoneWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), mod_InfiTools.ironRod
                });
        mod_BalkonInfiWeapons.dRedstoneWarhammer.setIconCoord(3, 5);
        ModLoader.addName(mod_BalkonInfiWeapons.dRedstoneWarhammer, "Jeweled Redstone Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.dRedstoneWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), mod_InfiTools.diamondRod
                });
        mod_BalkonInfiWeapons.rRedstoneWarhammer.setIconCoord(4, 5);
        ModLoader.addName(mod_BalkonInfiWeapons.rRedstoneWarhammer, "Redredred Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.rRedstoneWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), mod_InfiTools.redstoneRod
                });
        mod_BalkonInfiWeapons.oRedstoneWarhammer.setIconCoord(5, 5);
        ModLoader.addName(mod_BalkonInfiWeapons.oRedstoneWarhammer, "Glassy Redstone Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.oRedstoneWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), mod_InfiTools.obsidianRod
                });
        mod_BalkonInfiWeapons.bRedstoneWarhammer.setIconCoord(6, 5);
        ModLoader.addName(mod_BalkonInfiWeapons.bRedstoneWarhammer, "Necrotic Redstone Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bRedstoneWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bRedstoneWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_BalkonInfiWeapons.mRedstoneWarhammer.setIconCoord(7, 5);
        ModLoader.addName(mod_BalkonInfiWeapons.mRedstoneWarhammer, "Mossy Redstone Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.mRedstoneWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), mod_InfiTools.mossyRod
                });
        mod_BalkonInfiWeapons.glRedstoneWarhammer.setIconCoord(8, 5);
        ModLoader.addName(mod_BalkonInfiWeapons.glRedstoneWarhammer, "Glowing Redstone Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.glRedstoneWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_BalkonInfiWeapons.sRedstoneWarhammer.setIconCoord(9, 5);
        ModLoader.addName(mod_BalkonInfiWeapons.sRedstoneWarhammer, "Slimy Redstone Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.sRedstoneWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_BalkonInfiWeapons.blRedstoneWarhammer.setIconCoord(10, 5);
        ModLoader.addName(mod_BalkonInfiWeapons.blRedstoneWarhammer, "Blazing Redstone Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.blRedstoneWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), Item.blazeRod
                });
        mod_BalkonInfiWeapons.wObsidianWarhammer.setIconCoord(0, 6);
        ModLoader.addName(mod_BalkonInfiWeapons.wObsidianWarhammer, "Obsidian Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.wObsidianWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), Item.stick
                });
        mod_BalkonInfiWeapons.stObsidianWarhammer.setIconCoord(1, 6);
        ModLoader.addName(mod_BalkonInfiWeapons.stObsidianWarhammer, "Stony Obsidian Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.stObsidianWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_BalkonInfiWeapons.iObsidianWarhammer.setIconCoord(2, 6);
        ModLoader.addName(mod_BalkonInfiWeapons.iObsidianWarhammer, "Hard Obsidian Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.iObsidianWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), mod_InfiTools.ironRod
                });
        mod_BalkonInfiWeapons.dObsidianWarhammer.setIconCoord(3, 6);
        ModLoader.addName(mod_BalkonInfiWeapons.dObsidianWarhammer, "Jeweled Obsidian Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.dObsidianWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), mod_InfiTools.diamondRod
                });
        mod_BalkonInfiWeapons.gObsidianWarhammer.setIconCoord(4, 6);
        ModLoader.addName(mod_BalkonInfiWeapons.gObsidianWarhammer, "Expensive Obsidian Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.gObsidianWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), mod_InfiTools.goldRod
                });
        mod_BalkonInfiWeapons.rObsidianWarhammer.setIconCoord(5, 6);
        ModLoader.addName(mod_BalkonInfiWeapons.rObsidianWarhammer, "Red Obsidian Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.rObsidianWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), mod_InfiTools.redstoneRod
                });
        mod_BalkonInfiWeapons.oObsidianWarhammer.setIconCoord(6, 6);
        ModLoader.addName(mod_BalkonInfiWeapons.oObsidianWarhammer, "Wicked Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.oObsidianWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), mod_InfiTools.obsidianRod
                });
        mod_BalkonInfiWeapons.bObsidianWarhammer.setIconCoord(7, 6);
        ModLoader.addName(mod_BalkonInfiWeapons.bObsidianWarhammer, "Necrotic Obsidian Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bObsidianWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bObsidianWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_BalkonInfiWeapons.nObsidianWarhammer.setIconCoord(8, 6);
        ModLoader.addName(mod_BalkonInfiWeapons.nObsidianWarhammer, "Bloody Obsidian Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.nObsidianWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_BalkonInfiWeapons.glObsidianWarhammer.setIconCoord(9, 6);
        ModLoader.addName(mod_BalkonInfiWeapons.glObsidianWarhammer, "Glowing Obsidian Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.glObsidianWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_BalkonInfiWeapons.sObsidianWarhammer.setIconCoord(10, 6);
        ModLoader.addName(mod_BalkonInfiWeapons.sObsidianWarhammer, "Slimy Obsidian Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.sObsidianWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_BalkonInfiWeapons.fObsidianWarhammer.setIconCoord(11, 6);
        ModLoader.addName(mod_BalkonInfiWeapons.fObsidianWarhammer, "Flaky Obsidian Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.fObsidianWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), mod_InfiTools.flintRod
                });
        mod_BalkonInfiWeapons.blObsidianWarhammer.setIconCoord(12, 6);
        ModLoader.addName(mod_BalkonInfiWeapons.blObsidianWarhammer, "Blazing Obsidian Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.blObsidianWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), Item.blazeRod
                });
        mod_BalkonInfiWeapons.wSandstoneWarhammer.setIconCoord(0, 7);
        ModLoader.addName(mod_BalkonInfiWeapons.wSandstoneWarhammer, "Sandstone Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.wSandstoneWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), Item.stick
                });
        mod_BalkonInfiWeapons.stSandstoneWarhammer.setIconCoord(1, 7);
        ModLoader.addName(mod_BalkonInfiWeapons.stSandstoneWarhammer, "Stony Sandstone Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.stSandstoneWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_BalkonInfiWeapons.saSandstoneWarhammer.setIconCoord(2, 7);
        ModLoader.addName(mod_BalkonInfiWeapons.saSandstoneWarhammer, "Sandpound Two");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.saSandstoneWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), mod_InfiTools.sandstoneRod
                });
        mod_BalkonInfiWeapons.bSandstoneWarhammer.setIconCoord(3, 7);
        ModLoader.addName(mod_BalkonInfiWeapons.bSandstoneWarhammer, "Necrotic Sandstone Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bSandstoneWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bSandstoneWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_BalkonInfiWeapons.pSandstoneWarhammer.setIconCoord(4, 7);
        ModLoader.addName(mod_BalkonInfiWeapons.pSandstoneWarhammer, "Fibery Sandstone Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.pSandstoneWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), mod_InfiTools.paperRod
                });
        mod_BalkonInfiWeapons.nSandstoneWarhammer.setIconCoord(5, 7);
        ModLoader.addName(mod_BalkonInfiWeapons.nSandstoneWarhammer, "Bloody Sandstone Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.nSandstoneWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_BalkonInfiWeapons.iceSandstoneWarhammer.setIconCoord(6, 7);
        ModLoader.addName(mod_BalkonInfiWeapons.iceSandstoneWarhammer, "Icy Sandstone Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.iceSandstoneWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), mod_InfiTools.iceRod
                });
        mod_BalkonInfiWeapons.sSandstoneWarhammer.setIconCoord(7, 7);
        ModLoader.addName(mod_BalkonInfiWeapons.sSandstoneWarhammer, "Slimy Sandstone Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.sSandstoneWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_BalkonInfiWeapons.cSandstoneWarhammer.setIconCoord(8, 7);
        ModLoader.addName(mod_BalkonInfiWeapons.cSandstoneWarhammer, "Spiny Sandstone Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.cSandstoneWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_BalkonInfiWeapons.fSandstoneWarhammer.setIconCoord(9, 7);
        ModLoader.addName(mod_BalkonInfiWeapons.fSandstoneWarhammer, "Flaky Sandstone Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.fSandstoneWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), mod_InfiTools.flintRod
                });
        mod_BalkonInfiWeapons.brSandstoneWarhammer.setIconCoord(10, 7);
        ModLoader.addName(mod_BalkonInfiWeapons.brSandstoneWarhammer, "Baked Sandstone Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.brSandstoneWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), mod_InfiTools.brickRod
                });
        mod_BalkonInfiWeapons.wBoneWarhammer.setIconCoord(0, 8);
        ModLoader.addName(mod_BalkonInfiWeapons.wBoneWarhammer, "Bone Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.wBoneWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Item.bone, Character.valueOf('|'), Item.stick
                });
        mod_BalkonInfiWeapons.stBoneWarhammer.setIconCoord(1, 8);
        ModLoader.addName(mod_BalkonInfiWeapons.stBoneWarhammer, "Stony Bone Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.stBoneWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_BalkonInfiWeapons.iBoneWarhammer.setIconCoord(2, 8);
        ModLoader.addName(mod_BalkonInfiWeapons.iBoneWarhammer, "Hard Bone Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.iBoneWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.ironRod
                });
        mod_BalkonInfiWeapons.dBoneWarhammer.setIconCoord(3, 8);
        ModLoader.addName(mod_BalkonInfiWeapons.dBoneWarhammer, "Jeweled Bone Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.dBoneWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.diamondRod
                });
        mod_BalkonInfiWeapons.rBoneWarhammer.setIconCoord(4, 8);
        ModLoader.addName(mod_BalkonInfiWeapons.rBoneWarhammer, "Red Bone Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.rBoneWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.redstoneRod
                });
        mod_BalkonInfiWeapons.oBoneWarhammer.setIconCoord(5, 8);
        ModLoader.addName(mod_BalkonInfiWeapons.oBoneWarhammer, "Glassy Bone Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.oBoneWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.obsidianRod
                });
        mod_BalkonInfiWeapons.bBoneWarhammer.setIconCoord(6, 8);
        ModLoader.addName(mod_BalkonInfiWeapons.bBoneWarhammer, "Reanimated Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bBoneWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Item.bone, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bBoneWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_BalkonInfiWeapons.mBoneWarhammer.setIconCoord(7, 8);
        ModLoader.addName(mod_BalkonInfiWeapons.mBoneWarhammer, "Mossy Bone Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.mBoneWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.mossyRod
                });
        mod_BalkonInfiWeapons.nBoneWarhammer.setIconCoord(8, 8);
        ModLoader.addName(mod_BalkonInfiWeapons.nBoneWarhammer, "Netherrack Bone Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.nBoneWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_BalkonInfiWeapons.glBoneWarhammer.setIconCoord(9, 8);
        ModLoader.addName(mod_BalkonInfiWeapons.glBoneWarhammer, "Glowstone Bone Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.glBoneWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_BalkonInfiWeapons.sBoneWarhammer.setIconCoord(10, 8);
        ModLoader.addName(mod_BalkonInfiWeapons.sBoneWarhammer, "Slimy Bone Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.sBoneWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_BalkonInfiWeapons.cBoneWarhammer.setIconCoord(11, 8);
        ModLoader.addName(mod_BalkonInfiWeapons.cBoneWarhammer, "Spiny Bone Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.cBoneWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_BalkonInfiWeapons.fBoneWarhammer.setIconCoord(12, 8);
        ModLoader.addName(mod_BalkonInfiWeapons.fBoneWarhammer, "Flaky Bone Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.fBoneWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.flintRod
                });
        mod_BalkonInfiWeapons.brBoneWarhammer.setIconCoord(13, 8);
        ModLoader.addName(mod_BalkonInfiWeapons.brBoneWarhammer, "Baked Bone Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.brBoneWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.brickRod
                });
        mod_BalkonInfiWeapons.blBoneWarhammer.setIconCoord(14, 8);
        ModLoader.addName(mod_BalkonInfiWeapons.blBoneWarhammer, "Blazing Bone Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.blBoneWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Item.bone, Character.valueOf('|'), Item.blazeRod
                });
        mod_BalkonInfiWeapons.wPaperWarhammer.setIconCoord(0, 9);
        ModLoader.addName(mod_BalkonInfiWeapons.wPaperWarhammer, "Paper Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.wPaperWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), mod_InfiTools.paperStack, Character.valueOf('|'), Item.stick
                });
        mod_BalkonInfiWeapons.saPaperWarhammer.setIconCoord(1, 9);
        ModLoader.addName(mod_BalkonInfiWeapons.saPaperWarhammer, "Stony Paper Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.saPaperWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), mod_InfiTools.paperStack, Character.valueOf('|'), mod_InfiTools.sandstoneRod
                });
        mod_BalkonInfiWeapons.bPaperWarhammer.setIconCoord(2, 9);
        ModLoader.addName(mod_BalkonInfiWeapons.bPaperWarhammer, "Necrotic Paper Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bPaperWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), mod_InfiTools.paperStack, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bPaperWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), mod_InfiTools.paperStack, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_BalkonInfiWeapons.pPaperWarhammer.setIconCoord(3, 9);
        ModLoader.addName(mod_BalkonInfiWeapons.pPaperWarhammer, "Large Paper Hammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.pPaperWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), mod_InfiTools.paperStack, Character.valueOf('|'), mod_InfiTools.paperRod
                });
        mod_BalkonInfiWeapons.sPaperWarhammer.setIconCoord(4, 9);
        ModLoader.addName(mod_BalkonInfiWeapons.sPaperWarhammer, "Slimy Paper Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.sPaperWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), mod_InfiTools.paperStack, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_BalkonInfiWeapons.cPaperWarhammer.setIconCoord(5, 9);
        ModLoader.addName(mod_BalkonInfiWeapons.cPaperWarhammer, "Spiny Paper Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.cPaperWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), mod_InfiTools.paperStack, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_BalkonInfiWeapons.brPaperWarhammer.setIconCoord(6, 9);
        ModLoader.addName(mod_BalkonInfiWeapons.brPaperWarhammer, "Baked Paper Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.brPaperWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), mod_InfiTools.paperStack, Character.valueOf('|'), mod_InfiTools.brickRod
                });
        mod_BalkonInfiWeapons.stMossyWarhammer.setIconCoord(0, 10);
        ModLoader.addName(mod_BalkonInfiWeapons.stMossyWarhammer, "Stony Moss-Covered Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.stMossyWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), mod_InfiTools.mossBallCrafted, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_BalkonInfiWeapons.dMossyWarhammer.setIconCoord(1, 10);
        ModLoader.addName(mod_BalkonInfiWeapons.dMossyWarhammer, "Jeweled Moss-Covered Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.dMossyWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), mod_InfiTools.mossBallCrafted, Character.valueOf('|'), mod_InfiTools.diamondRod
                });
        mod_BalkonInfiWeapons.rMossyWarhammer.setIconCoord(2, 10);
        ModLoader.addName(mod_BalkonInfiWeapons.rMossyWarhammer, "Red Moss-Covered Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.rMossyWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), mod_InfiTools.mossBallCrafted, Character.valueOf('|'), mod_InfiTools.redstoneRod
                });
        mod_BalkonInfiWeapons.bMossyWarhammer.setIconCoord(3, 10);
        ModLoader.addName(mod_BalkonInfiWeapons.bMossyWarhammer, "Necrotic Moss-Covered Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bMossyWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), mod_InfiTools.mossBallCrafted, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bMossyWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), mod_InfiTools.mossBallCrafted, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_BalkonInfiWeapons.mMossyWarhammer.setIconCoord(4, 10);
        ModLoader.addName(mod_BalkonInfiWeapons.mMossyWarhammer, "Living Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.mMossyWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), mod_InfiTools.mossBallCrafted, Character.valueOf('|'), mod_InfiTools.mossyRod
                });
        mod_BalkonInfiWeapons.glMossyWarhammer.setIconCoord(5, 10);
        ModLoader.addName(mod_BalkonInfiWeapons.glMossyWarhammer, "Glowing Moss-Covered Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.glMossyWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), mod_InfiTools.mossBallCrafted, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_BalkonInfiWeapons.wNetherrackWarhammer.setIconCoord(0, 11);
        ModLoader.addName(mod_BalkonInfiWeapons.wNetherrackWarhammer, "Netherrack Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.wNetherrackWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), Item.stick
                });
        mod_BalkonInfiWeapons.stNetherrackWarhammer.setIconCoord(1, 11);
        ModLoader.addName(mod_BalkonInfiWeapons.stNetherrackWarhammer, "Stony Netherrack Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.stNetherrackWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_BalkonInfiWeapons.iNetherrackWarhammer.setIconCoord(2, 11);
        ModLoader.addName(mod_BalkonInfiWeapons.iNetherrackWarhammer, "Hard Netherrack Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.iNetherrackWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.ironRod
                });
        mod_BalkonInfiWeapons.rNetherrackWarhammer.setIconCoord(3, 11);
        ModLoader.addName(mod_BalkonInfiWeapons.rNetherrackWarhammer, "Red Netherrack Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.rNetherrackWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.redstoneRod
                });
        mod_BalkonInfiWeapons.oNetherrackWarhammer.setIconCoord(4, 11);
        ModLoader.addName(mod_BalkonInfiWeapons.oNetherrackWarhammer, "Glassy Netherrack Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.oNetherrackWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.obsidianRod
                });
        mod_BalkonInfiWeapons.saNetherrackWarhammer.setIconCoord(5, 11);
        ModLoader.addName(mod_BalkonInfiWeapons.saNetherrackWarhammer, "Sandy Netherrack Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.saNetherrackWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.sandstoneRod
                });
        mod_BalkonInfiWeapons.bNetherrackWarhammer.setIconCoord(6, 11);
        ModLoader.addName(mod_BalkonInfiWeapons.bNetherrackWarhammer, "Necrotic Netherrack Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bNetherrackWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bNetherrackWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_BalkonInfiWeapons.mNetherrackWarhammer.setIconCoord(7, 11);
        ModLoader.addName(mod_BalkonInfiWeapons.mNetherrackWarhammer, "Mossy Netherrack Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.mNetherrackWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.mossyRod
                });
        mod_BalkonInfiWeapons.nNetherrackWarhammer.setIconCoord(8, 11);
        ModLoader.addName(mod_BalkonInfiWeapons.nNetherrackWarhammer, "Meat Pounder");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.nNetherrackWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_BalkonInfiWeapons.glNetherrackWarhammer.setIconCoord(9, 11);
        ModLoader.addName(mod_BalkonInfiWeapons.glNetherrackWarhammer, "Glowing Netherrack Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.glNetherrackWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_BalkonInfiWeapons.iceNetherrackWarhammer.setIconCoord(10, 11);
        ModLoader.addName(mod_BalkonInfiWeapons.iceNetherrackWarhammer, "Icy Netherrack Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.iceNetherrackWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.iceRod
                });
        mod_BalkonInfiWeapons.sNetherrackWarhammer.setIconCoord(11, 11);
        ModLoader.addName(mod_BalkonInfiWeapons.sNetherrackWarhammer, "Slimy Netherrack Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.sNetherrackWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_BalkonInfiWeapons.cNetherrackWarhammer.setIconCoord(12, 11);
        ModLoader.addName(mod_BalkonInfiWeapons.cNetherrackWarhammer, "Spiny Netherrack Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.cNetherrackWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_BalkonInfiWeapons.fNetherrackWarhammer.setIconCoord(13, 11);
        ModLoader.addName(mod_BalkonInfiWeapons.fNetherrackWarhammer, "Flaky Netherrack Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.fNetherrackWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_BalkonInfiWeapons.brNetherrackWarhammer.setIconCoord(14, 11);
        ModLoader.addName(mod_BalkonInfiWeapons.brNetherrackWarhammer, "Baked Netherrack Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.brNetherrackWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_BalkonInfiWeapons.blNetherrackWarhammer.setIconCoord(15, 11);
        ModLoader.addName(mod_BalkonInfiWeapons.blNetherrackWarhammer, "Blazing Netherrack Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.blNetherrackWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_BalkonInfiWeapons.wGlowstoneWarhammer.setIconCoord(0, 12);
        ModLoader.addName(mod_BalkonInfiWeapons.wGlowstoneWarhammer, "Glowstone Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.wGlowstoneWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), Item.stick
                });
        mod_BalkonInfiWeapons.stGlowstoneWarhammer.setIconCoord(1, 12);
        ModLoader.addName(mod_BalkonInfiWeapons.stGlowstoneWarhammer, "Stony Glowstone Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.stGlowstoneWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_BalkonInfiWeapons.iGlowstoneWarhammer.setIconCoord(2, 12);
        ModLoader.addName(mod_BalkonInfiWeapons.iGlowstoneWarhammer, "Hard Glowstone Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.iGlowstoneWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.ironRod
                });
        mod_BalkonInfiWeapons.dGlowstoneWarhammer.setIconCoord(3, 12);
        ModLoader.addName(mod_BalkonInfiWeapons.dGlowstoneWarhammer, "Jeweled Glowstone Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.dGlowstoneWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.diamondRod
                });
        mod_BalkonInfiWeapons.rGlowstoneWarhammer.setIconCoord(4, 12);
        ModLoader.addName(mod_BalkonInfiWeapons.rGlowstoneWarhammer, "Red Glowstone Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.rGlowstoneWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.redstoneRod
                });
        mod_BalkonInfiWeapons.oGlowstoneWarhammer.setIconCoord(5, 12);
        ModLoader.addName(mod_BalkonInfiWeapons.oGlowstoneWarhammer, "Glassy Glowstone Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.oGlowstoneWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.obsidianRod
                });
        mod_BalkonInfiWeapons.bGlowstoneWarhammer.setIconCoord(6, 12);
        ModLoader.addName(mod_BalkonInfiWeapons.bGlowstoneWarhammer, "Necrotic Glowstone Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bGlowstoneWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bGlowstoneWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_BalkonInfiWeapons.mGlowstoneWarhammer.setIconCoord(7, 12);
        ModLoader.addName(mod_BalkonInfiWeapons.mGlowstoneWarhammer, "Mossy Glowstone Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.mGlowstoneWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.mossyRod
                });
        mod_BalkonInfiWeapons.nGlowstoneWarhammer.setIconCoord(8, 12);
        ModLoader.addName(mod_BalkonInfiWeapons.nGlowstoneWarhammer, "Bloody Glowstone Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.nGlowstoneWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_BalkonInfiWeapons.glGlowstoneWarhammer.setIconCoord(9, 12);
        ModLoader.addName(mod_BalkonInfiWeapons.glGlowstoneWarhammer, "Bright Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.glGlowstoneWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_BalkonInfiWeapons.iceGlowstoneWarhammer.setIconCoord(10, 12);
        ModLoader.addName(mod_BalkonInfiWeapons.iceGlowstoneWarhammer, "Icy Glowstone Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.iceGlowstoneWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.iceRod
                });
        mod_BalkonInfiWeapons.lGlowstoneWarhammer.setIconCoord(11, 12);
        ModLoader.addName(mod_BalkonInfiWeapons.lGlowstoneWarhammer, "Fiery Glowstone Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.lGlowstoneWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_BalkonInfiWeapons.sGlowstoneWarhammer.setIconCoord(12, 12);
        ModLoader.addName(mod_BalkonInfiWeapons.sGlowstoneWarhammer, "Slimy Glowstone Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.sGlowstoneWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_BalkonInfiWeapons.blGlowstoneWarhammer.setIconCoord(13, 12);
        ModLoader.addName(mod_BalkonInfiWeapons.blGlowstoneWarhammer, "Blazing Glowstone Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.blGlowstoneWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), Item.blazeRod
                });
        mod_BalkonInfiWeapons.wIceWarhammer.setIconCoord(0, 13);
        ModLoader.addName(mod_BalkonInfiWeapons.wIceWarhammer, "Ice Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.wIceWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Block.ice, Character.valueOf('|'), Item.stick
                });
        mod_BalkonInfiWeapons.stIceWarhammer.setIconCoord(1, 13);
        ModLoader.addName(mod_BalkonInfiWeapons.stIceWarhammer, "Stony Ice Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.stIceWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_BalkonInfiWeapons.iIceWarhammer.setIconCoord(2, 13);
        ModLoader.addName(mod_BalkonInfiWeapons.iIceWarhammer, "Hard Ice Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.iIceWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.ironRod
                });
        mod_BalkonInfiWeapons.dIceWarhammer.setIconCoord(3, 13);
        ModLoader.addName(mod_BalkonInfiWeapons.dIceWarhammer, "Jeweled Ice Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.dIceWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.diamondRod
                });
        mod_BalkonInfiWeapons.gIceWarhammer.setIconCoord(4, 13);
        ModLoader.addName(mod_BalkonInfiWeapons.gIceWarhammer, "Expensive Ice Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.gIceWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.goldRod
                });
        mod_BalkonInfiWeapons.rIceWarhammer.setIconCoord(5, 13);
        ModLoader.addName(mod_BalkonInfiWeapons.rIceWarhammer, "Red Ice Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.rIceWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.redstoneRod
                });
        mod_BalkonInfiWeapons.oIceWarhammer.setIconCoord(6, 13);
        ModLoader.addName(mod_BalkonInfiWeapons.oIceWarhammer, "Glassy Ice Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.oIceWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.obsidianRod
                });
        mod_BalkonInfiWeapons.saIceWarhammer.setIconCoord(7, 13);
        ModLoader.addName(mod_BalkonInfiWeapons.saIceWarhammer, "Sandy Ice Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.saIceWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.sandstoneRod
                });
        mod_BalkonInfiWeapons.bIceWarhammer.setIconCoord(8, 13);
        ModLoader.addName(mod_BalkonInfiWeapons.bIceWarhammer, "Necrotic Ice Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bIceWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Block.ice, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bIceWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_BalkonInfiWeapons.glIceWarhammer.setIconCoord(9, 13);
        ModLoader.addName(mod_BalkonInfiWeapons.glIceWarhammer, "Glowing Ice Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.glIceWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_BalkonInfiWeapons.iceIceWarhammer.setIconCoord(10, 13);
        ModLoader.addName(mod_BalkonInfiWeapons.iceIceWarhammer, "Compressed Snowflake");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.iceIceWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.iceRod
                });
        mod_BalkonInfiWeapons.sIceWarhammer.setIconCoord(11, 13);
        ModLoader.addName(mod_BalkonInfiWeapons.sIceWarhammer, "Slimy Ice Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.sIceWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_BalkonInfiWeapons.cIceWarhammer.setIconCoord(12, 13);
        ModLoader.addName(mod_BalkonInfiWeapons.cIceWarhammer, "Spiny Ice Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.cIceWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_BalkonInfiWeapons.fIceWarhammer.setIconCoord(13, 13);
        ModLoader.addName(mod_BalkonInfiWeapons.fIceWarhammer, "Flaky Ice Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.fIceWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.flintRod
                });
        mod_BalkonInfiWeapons.brIceWarhammer.setIconCoord(14, 13);
        ModLoader.addName(mod_BalkonInfiWeapons.brIceWarhammer, "Baked Ice Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.brIceWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.brickRod
                });
        mod_BalkonInfiWeapons.dLavaWarhammer.setIconCoord(0, 14);
        ModLoader.addName(mod_BalkonInfiWeapons.dLavaWarhammer, "Jeweled Lava Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.dLavaWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), mod_InfiTools.lavaCrystal, Character.valueOf('|'), mod_InfiTools.diamondRod
                });
        mod_BalkonInfiWeapons.rLavaWarhammer.setIconCoord(1, 14);
        ModLoader.addName(mod_BalkonInfiWeapons.rLavaWarhammer, "Red Lava Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.rLavaWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), mod_InfiTools.lavaCrystal, Character.valueOf('|'), mod_InfiTools.redstoneRod
                });
        mod_BalkonInfiWeapons.bLavaWarhammer.setIconCoord(2, 14);
        ModLoader.addName(mod_BalkonInfiWeapons.bLavaWarhammer, "Necrotic Lava Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bLavaWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), mod_InfiTools.lavaCrystal, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bLavaWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), mod_InfiTools.lavaCrystal, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_BalkonInfiWeapons.nLavaWarhammer.setIconCoord(3, 14);
        ModLoader.addName(mod_BalkonInfiWeapons.nLavaWarhammer, "Bloody Lava Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.nLavaWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), mod_InfiTools.lavaCrystal, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_BalkonInfiWeapons.glLavaWarhammer.setIconCoord(4, 14);
        ModLoader.addName(mod_BalkonInfiWeapons.glLavaWarhammer, "Glowing Lava Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.glLavaWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), mod_InfiTools.lavaCrystal, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_BalkonInfiWeapons.lLavaWarhammer.setIconCoord(5, 14);
        ModLoader.addName(mod_BalkonInfiWeapons.lLavaWarhammer, "Bedrock Thumper");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.lLavaWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), mod_InfiTools.lavaCrystal, Character.valueOf('|'), mod_InfiTools.lavaRod
                });
        mod_BalkonInfiWeapons.blLavaWarhammer.setIconCoord(6, 14);
        ModLoader.addName(mod_BalkonInfiWeapons.blLavaWarhammer, "Blazing Lava Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.blLavaWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), mod_InfiTools.lavaCrystal, Character.valueOf('|'), Item.blazeRod
                });
        mod_BalkonInfiWeapons.wSlimeWarhammer.setIconCoord(0, 15);
        ModLoader.addName(mod_BalkonInfiWeapons.wSlimeWarhammer, "Slime Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.wSlimeWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), Item.stick
                });
        mod_BalkonInfiWeapons.stSlimeWarhammer.setIconCoord(1, 15);
        ModLoader.addName(mod_BalkonInfiWeapons.stSlimeWarhammer, "Stony Slime Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.stSlimeWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_BalkonInfiWeapons.iSlimeWarhammer.setIconCoord(2, 15);
        ModLoader.addName(mod_BalkonInfiWeapons.iSlimeWarhammer, "Hard Slime Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.iSlimeWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.ironRod
                });
        mod_BalkonInfiWeapons.dSlimeWarhammer.setIconCoord(3, 15);
        ModLoader.addName(mod_BalkonInfiWeapons.dSlimeWarhammer, "Jeweled Slime Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.dSlimeWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.diamondRod
                });
        mod_BalkonInfiWeapons.gSlimeWarhammer.setIconCoord(4, 15);
        ModLoader.addName(mod_BalkonInfiWeapons.gSlimeWarhammer, "Expensive Slime Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.gSlimeWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.goldRod
                });
        mod_BalkonInfiWeapons.rSlimeWarhammer.setIconCoord(5, 15);
        ModLoader.addName(mod_BalkonInfiWeapons.rSlimeWarhammer, "Red Slime Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.rSlimeWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.redstoneRod
                });
        mod_BalkonInfiWeapons.oSlimeWarhammer.setIconCoord(6, 15);
        ModLoader.addName(mod_BalkonInfiWeapons.oSlimeWarhammer, "Glassy Slime Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.oSlimeWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.obsidianRod
                });
        mod_BalkonInfiWeapons.saSlimeWarhammer.setIconCoord(7, 15);
        ModLoader.addName(mod_BalkonInfiWeapons.saSlimeWarhammer, "Sandy Slime Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.saSlimeWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.sandstoneRod
                });
        mod_BalkonInfiWeapons.bSlimeWarhammer.setIconCoord(8, 15);
        ModLoader.addName(mod_BalkonInfiWeapons.bSlimeWarhammer, "Necrotic Slime Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bSlimeWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bSlimeWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_BalkonInfiWeapons.pSlimeWarhammer.setIconCoord(9, 15);
        ModLoader.addName(mod_BalkonInfiWeapons.pSlimeWarhammer, "Fibery Slime Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.pSlimeWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.paperRod
                });
        mod_BalkonInfiWeapons.mSlimeWarhammer.setIconCoord(10, 15);
        ModLoader.addName(mod_BalkonInfiWeapons.mSlimeWarhammer, "Mossy Slime Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.mSlimeWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.mossyRod
                });
        mod_BalkonInfiWeapons.nSlimeWarhammer.setIconCoord(11, 15);
        ModLoader.addName(mod_BalkonInfiWeapons.nSlimeWarhammer, "Bloody Slime Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.nSlimeWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_BalkonInfiWeapons.glSlimeWarhammer.setIconCoord(12, 15);
        ModLoader.addName(mod_BalkonInfiWeapons.glSlimeWarhammer, "Glowing Slime Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.glSlimeWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_BalkonInfiWeapons.iceSlimeWarhammer.setIconCoord(13, 15);
        ModLoader.addName(mod_BalkonInfiWeapons.iceSlimeWarhammer, "Icy Slime Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.iceSlimeWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.iceRod
                });
        mod_BalkonInfiWeapons.lSlimeWarhammer.setIconCoord(14, 15);
        ModLoader.addName(mod_BalkonInfiWeapons.lSlimeWarhammer, "Fiery Slime Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.lSlimeWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.lavaRod
                });
        mod_BalkonInfiWeapons.sSlimeWarhammer.setIconCoord(15, 15);
        ModLoader.addName(mod_BalkonInfiWeapons.sSlimeWarhammer, "Croquet Mallet");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.sSlimeWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_BalkonInfiWeapons.cSlimeWarhammer.setIconCoord(12, 14);
        ModLoader.addName(mod_BalkonInfiWeapons.cSlimeWarhammer, "Spiny Slime Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.cSlimeWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_BalkonInfiWeapons.fSlimeWarhammer.setIconCoord(13, 14);
        ModLoader.addName(mod_BalkonInfiWeapons.fSlimeWarhammer, "Flaky Slime Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.fSlimeWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.flintRod
                });
        mod_BalkonInfiWeapons.brSlimeWarhammer.setIconCoord(14, 14);
        ModLoader.addName(mod_BalkonInfiWeapons.brSlimeWarhammer, "Baked Slime Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.brSlimeWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.brickRod
                });
        mod_BalkonInfiWeapons.blSlimeWarhammer.setIconCoord(15, 14);
        ModLoader.addName(mod_BalkonInfiWeapons.blSlimeWarhammer, "Blazing Slime Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.blSlimeWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), Item.blazeRod
                });
        mod_BalkonInfiWeapons.wCactusWarhammer.setIconCoord(10, 0);
        ModLoader.addName(mod_BalkonInfiWeapons.wCactusWarhammer, "Cactus Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.wCactusWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Block.cactus, Character.valueOf('|'), Item.stick
                });
        mod_BalkonInfiWeapons.stCactusWarhammer.setIconCoord(11, 0);
        ModLoader.addName(mod_BalkonInfiWeapons.stCactusWarhammer, "Stony Cactus Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.stCactusWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Block.cactus, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_BalkonInfiWeapons.saCactusWarhammer.setIconCoord(12, 0);
        ModLoader.addName(mod_BalkonInfiWeapons.saCactusWarhammer, "Sandy Cactus Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.saCactusWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Block.cactus, Character.valueOf('|'), mod_InfiTools.sandstoneRod
                });
        mod_BalkonInfiWeapons.bCactusWarhammer.setIconCoord(13, 0);
        ModLoader.addName(mod_BalkonInfiWeapons.bCactusWarhammer, "Necrotic Cactus Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bCactusWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Block.cactus, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bCactusWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Block.cactus, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_BalkonInfiWeapons.pCactusWarhammer.setIconCoord(14, 0);
        ModLoader.addName(mod_BalkonInfiWeapons.pCactusWarhammer, "Fibery Cactus Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.pCactusWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Block.cactus, Character.valueOf('|'), mod_InfiTools.paperRod
                });
        mod_BalkonInfiWeapons.nCactusWarhammer.setIconCoord(15, 0);
        ModLoader.addName(mod_BalkonInfiWeapons.nCactusWarhammer, "Bloody Cactus Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.nCactusWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Block.cactus, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_BalkonInfiWeapons.sCactusWarhammer.setIconCoord(12, 1);
        ModLoader.addName(mod_BalkonInfiWeapons.sCactusWarhammer, "Slimy Cactus Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.sCactusWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Block.cactus, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_BalkonInfiWeapons.cCactusWarhammer.setIconCoord(13, 1);
        ModLoader.addName(mod_BalkonInfiWeapons.cCactusWarhammer, "Spined Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.cCactusWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Block.cactus, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_BalkonInfiWeapons.fCactusWarhammer.setIconCoord(14, 1);
        ModLoader.addName(mod_BalkonInfiWeapons.fCactusWarhammer, "Flaky Cactus Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.fCactusWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Block.cactus, Character.valueOf('|'), mod_InfiTools.flintRod
                });
        mod_BalkonInfiWeapons.brCactusWarhammer.setIconCoord(15, 1);
        ModLoader.addName(mod_BalkonInfiWeapons.brCactusWarhammer, "Baked Cactus Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.brCactusWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Block.cactus, Character.valueOf('|'), mod_InfiTools.brickRod
                });
        mod_BalkonInfiWeapons.wFlintWarhammer.setIconCoord(13, 2);
        ModLoader.addName(mod_BalkonInfiWeapons.wFlintWarhammer, "Flint Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.wFlintWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Item.flint, Character.valueOf('|'), Item.stick
                });
        mod_BalkonInfiWeapons.stFlintWarhammer.setIconCoord(14, 2);
        ModLoader.addName(mod_BalkonInfiWeapons.stFlintWarhammer, "Stony Flint Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.stFlintWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_BalkonInfiWeapons.iFlintWarhammer.setIconCoord(15, 2);
        ModLoader.addName(mod_BalkonInfiWeapons.iFlintWarhammer, "Hard Flint Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.iFlintWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.ironRod
                });
        mod_BalkonInfiWeapons.gFlintWarhammer.setIconCoord(12, 3);
        ModLoader.addName(mod_BalkonInfiWeapons.gFlintWarhammer, "Expensive Flint Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.gFlintWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.goldRod
                });
        mod_BalkonInfiWeapons.oFlintWarhammer.setIconCoord(13, 3);
        ModLoader.addName(mod_BalkonInfiWeapons.oFlintWarhammer, "Glassy Flint Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.oFlintWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.obsidianRod
                });
        mod_BalkonInfiWeapons.saFlintWarhammer.setIconCoord(14, 3);
        ModLoader.addName(mod_BalkonInfiWeapons.saFlintWarhammer, "Sandy Flint Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.saFlintWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.sandstoneRod
                });
        mod_BalkonInfiWeapons.bFlintWarhammer.setIconCoord(15, 3);
        ModLoader.addName(mod_BalkonInfiWeapons.bFlintWarhammer, "Necrotic Flint Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bFlintWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Item.flint, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bFlintWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_BalkonInfiWeapons.nFlintWarhammer.setIconCoord(12, 4);
        ModLoader.addName(mod_BalkonInfiWeapons.nFlintWarhammer, "Bloody Flint Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.nFlintWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_BalkonInfiWeapons.iceFlintWarhammer.setIconCoord(13, 4);
        ModLoader.addName(mod_BalkonInfiWeapons.iceFlintWarhammer, "Icy Flint Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.iceFlintWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.iceRod
                });
        mod_BalkonInfiWeapons.sFlintWarhammer.setIconCoord(14, 4);
        ModLoader.addName(mod_BalkonInfiWeapons.sFlintWarhammer, "Slimy Flint Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.sFlintWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_BalkonInfiWeapons.cFlintWarhammer.setIconCoord(15, 4);
        ModLoader.addName(mod_BalkonInfiWeapons.cFlintWarhammer, "Spiny Flint Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.cFlintWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_BalkonInfiWeapons.fFlintWarhammer.setIconCoord(11, 5);
        ModLoader.addName(mod_BalkonInfiWeapons.fFlintWarhammer, "Flaky Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.fFlintWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.flintRod
                });
        mod_BalkonInfiWeapons.brFlintWarhammer.setIconCoord(12, 5);
        ModLoader.addName(mod_BalkonInfiWeapons.brFlintWarhammer, "Rough-hewn Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.brFlintWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.brickRod
                });
        mod_BalkonInfiWeapons.blFlintWarhammer.setIconCoord(13, 5);
        ModLoader.addName(mod_BalkonInfiWeapons.blFlintWarhammer, "Blazing Flint Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.blFlintWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Item.flint, Character.valueOf('|'), Item.blazeRod
                });
        mod_BalkonInfiWeapons.wBrickWarhammer.setIconCoord(14, 5);
        ModLoader.addName(mod_BalkonInfiWeapons.wBrickWarhammer, "Brick Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.wBrickWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Item.brick, Character.valueOf('|'), Item.brick
                });
        mod_BalkonInfiWeapons.stBrickWarhammer.setIconCoord(15, 5);
        ModLoader.addName(mod_BalkonInfiWeapons.stBrickWarhammer, "Stony Brick Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.stBrickWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Item.brick, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_BalkonInfiWeapons.saBrickWarhammer.setIconCoord(13, 6);
        ModLoader.addName(mod_BalkonInfiWeapons.saBrickWarhammer, "Sandstone Brick Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.saBrickWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Item.brick, Character.valueOf('|'), mod_InfiTools.sandstoneRod
                });
        mod_BalkonInfiWeapons.bBrickWarhammer.setIconCoord(14, 6);
        ModLoader.addName(mod_BalkonInfiWeapons.bBrickWarhammer, "Necrotic Brick Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bBrickWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Item.brick, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bBrickWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Item.brick, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_BalkonInfiWeapons.pBrickWarhammer.setIconCoord(15, 6);
        ModLoader.addName(mod_BalkonInfiWeapons.pBrickWarhammer, "Fibery Brick Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.pBrickWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Item.brick, Character.valueOf('|'), mod_InfiTools.paperRod
                });
        mod_BalkonInfiWeapons.nBrickWarhammer.setIconCoord(11, 7);
        ModLoader.addName(mod_BalkonInfiWeapons.nBrickWarhammer, "Bloody Brick Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.nBrickWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Item.brick, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_BalkonInfiWeapons.iceBrickWarhammer.setIconCoord(12, 7);
        ModLoader.addName(mod_BalkonInfiWeapons.iceBrickWarhammer, "Icy Brick Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.iceBrickWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Item.brick, Character.valueOf('|'), mod_InfiTools.iceRod
                });
        mod_BalkonInfiWeapons.sBrickWarhammer.setIconCoord(13, 7);
        ModLoader.addName(mod_BalkonInfiWeapons.sBrickWarhammer, "Slimy Brick Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.sBrickWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Item.brick, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_BalkonInfiWeapons.cBrickWarhammer.setIconCoord(14, 7);
        ModLoader.addName(mod_BalkonInfiWeapons.cBrickWarhammer, "Spiny Brick Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.cBrickWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Item.brick, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_BalkonInfiWeapons.fBrickWarhammer.setIconCoord(15, 7);
        ModLoader.addName(mod_BalkonInfiWeapons.fBrickWarhammer, "Flaky Brick Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.fBrickWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Item.brick, Character.valueOf('|'), mod_InfiTools.flintRod
                });
        mod_BalkonInfiWeapons.brBrickWarhammer.setIconCoord(15, 8);
        ModLoader.addName(mod_BalkonInfiWeapons.brBrickWarhammer, "Look-alike Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.brBrickWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), Item.brick, Character.valueOf('|'), mod_InfiTools.brickRod
                });
        mod_BalkonInfiWeapons.dBlazeWarhammer.setIconCoord(7, 9);
        ModLoader.addName(mod_BalkonInfiWeapons.dBlazeWarhammer, "Jeweled Blaze Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.dBlazeWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), mod_InfiTools.blazeCrystal, Character.valueOf('|'), mod_InfiTools.diamondRod
                });
        mod_BalkonInfiWeapons.rBlazeWarhammer.setIconCoord(8, 9);
        ModLoader.addName(mod_BalkonInfiWeapons.rBlazeWarhammer, "Red Blaze Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.rBlazeWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), mod_InfiTools.blazeCrystal, Character.valueOf('|'), mod_InfiTools.redstoneRod
                });
        mod_BalkonInfiWeapons.bBlazeWarhammer.setIconCoord(9, 9);
        ModLoader.addName(mod_BalkonInfiWeapons.bBlazeWarhammer, "Necrotic Blaze Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bBlazeWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), mod_InfiTools.blazeCrystal, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.bBlazeWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), mod_InfiTools.blazeCrystal, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_BalkonInfiWeapons.nBlazeWarhammer.setIconCoord(10, 9);
        ModLoader.addName(mod_BalkonInfiWeapons.nBlazeWarhammer, "Bloody Blaze Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.nBlazeWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), mod_InfiTools.blazeCrystal, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_BalkonInfiWeapons.glBlazeWarhammer.setIconCoord(11, 9);
        ModLoader.addName(mod_BalkonInfiWeapons.glBlazeWarhammer, "Glowing Blaze Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.glBlazeWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), mod_InfiTools.blazeCrystal, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_BalkonInfiWeapons.lBlazeWarhammer.setIconCoord(12, 9);
        ModLoader.addName(mod_BalkonInfiWeapons.lBlazeWarhammer, "Fiery Blaze Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.lBlazeWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), mod_InfiTools.blazeCrystal, Character.valueOf('|'), Item.blazeRod
                });
        mod_BalkonInfiWeapons.fBlazeWarhammer.setIconCoord(13, 9);
        ModLoader.addName(mod_BalkonInfiWeapons.fBlazeWarhammer, "Flaky Blaze Warhammer");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.fBlazeWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), mod_InfiTools.blazeCrystal, Character.valueOf('|'), Item.blazeRod
                });
        mod_BalkonInfiWeapons.blBlazeWarhammer.setIconCoord(14, 9);
        ModLoader.addName(mod_BalkonInfiWeapons.blBlazeWarhammer, "Underworld Smite");
        ModLoader.addRecipe(new ItemStack(mod_BalkonInfiWeapons.blBlazeWarhammer, 1), new Object[]
                {
                    "m|m", "m|m", " | ", Character.valueOf('m'), mod_InfiTools.blazeCrystal, Character.valueOf('|'), Item.blazeRod
                });
        return 0;
    }
}
