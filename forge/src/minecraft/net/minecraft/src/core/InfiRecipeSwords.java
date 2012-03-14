package net.minecraft.src.core;

import net.minecraft.src.*;

public class InfiRecipeSwords
{
    public InfiRecipeSwords()
    {
    }

    Object getTypeItem(Object obj)
    {
        return null;
    }

    public static void recipeStorm()
    {
        mod_InfiTools.stWoodSword.setIconCoord(1, 0);
        ModLoader.addName(mod_InfiTools.stWoodSword, "Stony Wooden Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.stWoodSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.planks, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_InfiTools.saWoodSword.setIconCoord(2, 0);
        ModLoader.addName(mod_InfiTools.saWoodSword, "Sandy Wooden Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.saWoodSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.planks, Character.valueOf('|'), mod_InfiTools.sandstoneRod
                });
        mod_InfiTools.bWoodSword.setIconCoord(3, 0);
        ModLoader.addName(mod_InfiTools.bWoodSword, "Necrotic Wooden Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bWoodSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.planks, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bWoodSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.planks, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_InfiTools.pWoodSword.setIconCoord(4, 0);
        ModLoader.addName(mod_InfiTools.pWoodSword, "Fibery Wooden Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.pWoodSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.planks, Character.valueOf('|'), mod_InfiTools.paperRod
                });
        mod_InfiTools.nWoodSword.setIconCoord(5, 0);
        ModLoader.addName(mod_InfiTools.nWoodSword, "Bloody Wooden Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.nWoodSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.planks, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_InfiTools.sWoodSword.setIconCoord(6, 0);
        ModLoader.addName(mod_InfiTools.sWoodSword, "Slimy Wooden Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.sWoodSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.planks, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_InfiTools.cWoodSword.setIconCoord(7, 0);
        ModLoader.addName(mod_InfiTools.cWoodSword, "Spiny Wooden Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.cWoodSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.planks, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_InfiTools.fWoodSword.setIconCoord(8, 0);
        ModLoader.addName(mod_InfiTools.fWoodSword, "Flaky Wooden Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.fWoodSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.planks, Character.valueOf('|'), mod_InfiTools.flintRod
                });
        mod_InfiTools.brWoodSword.setIconCoord(9, 0);
        ModLoader.addName(mod_InfiTools.brWoodSword, "Baked Wooden Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.brWoodSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.planks, Character.valueOf('|'), mod_InfiTools.brickRod
                });
        ModLoader.addRecipe(new ItemStack(Item.swordStone, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.stone, Character.valueOf('|'), Item.stick
                });
        mod_InfiTools.stStoneSword.setIconCoord(1, 1);
        ModLoader.addName(mod_InfiTools.stStoneSword, "Heavy Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.stStoneSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.stStoneSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.stone, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_InfiTools.saStoneSword.setIconCoord(2, 1);
        ModLoader.addName(mod_InfiTools.saStoneSword, "Sandy Stone Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.saStoneSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), mod_InfiTools.sandstoneRod
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.saStoneSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.stone, Character.valueOf('|'), mod_InfiTools.sandstoneRod
                });
        mod_InfiTools.bStoneSword.setIconCoord(3, 1);
        ModLoader.addName(mod_InfiTools.bStoneSword, "Necrotic Stone Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bStoneSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bStoneSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.stone, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bStoneSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bStoneSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.stone, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_InfiTools.pStoneSword.setIconCoord(4, 1);
        ModLoader.addName(mod_InfiTools.pStoneSword, "Fibery Stone Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.pStoneSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), mod_InfiTools.paperRod
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.pStoneSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.stone, Character.valueOf('|'), mod_InfiTools.paperRod
                });
        mod_InfiTools.mStoneSword.setIconCoord(5, 1);
        ModLoader.addName(mod_InfiTools.mStoneSword, "Mossy Stone Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.mStoneSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), mod_InfiTools.mossyRod
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.mStoneSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.stone, Character.valueOf('|'), mod_InfiTools.mossyRod
                });
        mod_InfiTools.nStoneSword.setIconCoord(6, 1);
        ModLoader.addName(mod_InfiTools.nStoneSword, "Bloody Stone Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.nStoneSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.nStoneSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.stone, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_InfiTools.iceStoneSword.setIconCoord(7, 1);
        ModLoader.addName(mod_InfiTools.iceStoneSword, "Icy Stone Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.iceStoneSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), mod_InfiTools.iceRod
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.iceStoneSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.stone, Character.valueOf('|'), mod_InfiTools.iceRod
                });
        mod_InfiTools.sStoneSword.setIconCoord(8, 1);
        ModLoader.addName(mod_InfiTools.sStoneSword, "Slimy Stone Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.sStoneSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.sStoneSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.stone, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_InfiTools.cStoneSword.setIconCoord(9, 1);
        ModLoader.addName(mod_InfiTools.cStoneSword, "Spiny Stone Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.cStoneSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.cStoneSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.stone, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_InfiTools.fStoneSword.setIconCoord(1, 1);
        ModLoader.addName(mod_InfiTools.fStoneSword, "Flaky Stone Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.fStoneSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), mod_InfiTools.flintRod
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.fStoneSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.stone, Character.valueOf('|'), mod_InfiTools.flintRod
                });
        mod_InfiTools.brStoneSword.setIconCoord(1, 1);
        ModLoader.addName(mod_InfiTools.brStoneSword, "Baked Stone Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.brStoneSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), mod_InfiTools.brickRod
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.brStoneSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.stone, Character.valueOf('|'), mod_InfiTools.brickRod
                });
        mod_InfiTools.stIronSword.setIconCoord(1, 2);
        ModLoader.addName(mod_InfiTools.stIronSword, "Stony Iron Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.stIronSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_InfiTools.iIronSword.setIconCoord(2, 2);
        ModLoader.addName(mod_InfiTools.iIronSword, "Ironic Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.iIronSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), mod_InfiTools.ironRod
                });
        mod_InfiTools.dIronSword.setIconCoord(3, 2);
        ModLoader.addName(mod_InfiTools.dIronSword, "Jeweled Iron Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.dIronSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), mod_InfiTools.diamondRod
                });
        mod_InfiTools.gIronSword.setIconCoord(4, 2);
        ModLoader.addName(mod_InfiTools.gIronSword, "Expensive Iron Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.gIronSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), mod_InfiTools.goldRod
                });
        mod_InfiTools.rIronSword.setIconCoord(5, 2);
        ModLoader.addName(mod_InfiTools.rIronSword, "Red Iron Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.rIronSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), mod_InfiTools.redstoneRod
                });
        mod_InfiTools.oIronSword.setIconCoord(6, 2);
        ModLoader.addName(mod_InfiTools.oIronSword, "Glassy Iron Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.oIronSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), mod_InfiTools.obsidianRod
                });
        mod_InfiTools.bIronSword.setIconCoord(7, 2);
        ModLoader.addName(mod_InfiTools.bIronSword, "Necrotic Iron Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bIronSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bIronSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_InfiTools.nIronSword.setIconCoord(8, 2);
        ModLoader.addName(mod_InfiTools.nIronSword, "Bloody Iron Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.nIronSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_InfiTools.glIronSword.setIconCoord(9, 2);
        ModLoader.addName(mod_InfiTools.glIronSword, "Glowing Iron Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.glIronSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_InfiTools.iceIronSword.setIconCoord(10, 2);
        ModLoader.addName(mod_InfiTools.iceIronSword, "Icy Iron Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.iceIronSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), mod_InfiTools.iceRod
                });
        mod_InfiTools.sIronSword.setIconCoord(11, 2);
        ModLoader.addName(mod_InfiTools.sIronSword, "Slimy Iron Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.sIronSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_InfiTools.blIronSword.setIconCoord(12, 2);
        ModLoader.addName(mod_InfiTools.blIronSword, "Blazing Iron Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.blIronSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), Item.blazeRod
                });
        mod_InfiTools.stDiamondSword.setIconCoord(1, 3);
        ModLoader.addName(mod_InfiTools.stDiamondSword, "Stony Diamond Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.stDiamondSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_InfiTools.iDiamondSword.setIconCoord(2, 3);
        ModLoader.addName(mod_InfiTools.iDiamondSword, "Hard Diamond Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.iDiamondSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), mod_InfiTools.ironRod
                });
        mod_InfiTools.dDiamondSword.setIconCoord(3, 3);
        ModLoader.addName(mod_InfiTools.dDiamondSword, "Diamondium Blade");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.dDiamondSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), mod_InfiTools.diamondRod
                });
        mod_InfiTools.gDiamondSword.setIconCoord(4, 3);
        ModLoader.addName(mod_InfiTools.gDiamondSword, "Expensive Diamond Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.gDiamondSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), mod_InfiTools.goldRod
                });
        mod_InfiTools.rDiamondSword.setIconCoord(5, 3);
        ModLoader.addName(mod_InfiTools.rDiamondSword, "Red Diamond Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.rDiamondSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), mod_InfiTools.redstoneRod
                });
        mod_InfiTools.oDiamondSword.setIconCoord(6, 3);
        ModLoader.addName(mod_InfiTools.oDiamondSword, "Glassy Diamond Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.oDiamondSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), mod_InfiTools.obsidianRod
                });
        mod_InfiTools.bDiamondSword.setIconCoord(7, 3);
        ModLoader.addName(mod_InfiTools.bDiamondSword, "Necrotic Diamond Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bDiamondSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bDiamondSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_InfiTools.mDiamondSword.setIconCoord(8, 3);
        ModLoader.addName(mod_InfiTools.mDiamondSword, "Mossy Diamond Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.mDiamondSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), mod_InfiTools.mossyRod
                });
        mod_InfiTools.nDiamondSword.setIconCoord(9, 3);
        ModLoader.addName(mod_InfiTools.nDiamondSword, "Bloody Diamond Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.nDiamondSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_InfiTools.glDiamondSword.setIconCoord(10, 3);
        ModLoader.addName(mod_InfiTools.glDiamondSword, "Glowing Diamond Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.glDiamondSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_InfiTools.blDiamondSword.setIconCoord(11, 3);
        ModLoader.addName(mod_InfiTools.blDiamondSword, "Blazing Diamond Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.blDiamondSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), Item.blazeRod
                });
        mod_InfiTools.stGoldSword.setIconCoord(1, 4);
        ModLoader.addName(mod_InfiTools.stGoldSword, "Stony Gold Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.stGoldSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_InfiTools.gGoldSword.setIconCoord(2, 4);
        ModLoader.addName(mod_InfiTools.gGoldSword, "Expensive Useless Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.gGoldSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), mod_InfiTools.goldRod
                });
        mod_InfiTools.oGoldSword.setIconCoord(3, 4);
        ModLoader.addName(mod_InfiTools.oGoldSword, "Glassy Gold Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.oGoldSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), mod_InfiTools.obsidianRod
                });
        mod_InfiTools.saGoldSword.setIconCoord(4, 4);
        ModLoader.addName(mod_InfiTools.saGoldSword, "Sandy Gold Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.saGoldSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), mod_InfiTools.sandstoneRod
                });
        mod_InfiTools.bGoldSword.setIconCoord(5, 4);
        ModLoader.addName(mod_InfiTools.bGoldSword, "Necrotic Gold Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bGoldSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bGoldSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_InfiTools.mGoldSword.setIconCoord(6, 4);
        ModLoader.addName(mod_InfiTools.mGoldSword, "Mossy Gold Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.mGoldSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), mod_InfiTools.mossyRod
                });
        mod_InfiTools.nGoldSword.setIconCoord(7, 4);
        ModLoader.addName(mod_InfiTools.nGoldSword, "Bloody Gold Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.nGoldSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_InfiTools.glGoldSword.setIconCoord(8, 4);
        ModLoader.addName(mod_InfiTools.glGoldSword, "Glowing Gold Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.glGoldSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_InfiTools.iceGoldSword.setIconCoord(9, 4);
        ModLoader.addName(mod_InfiTools.iceGoldSword, "Icy Gold Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.iceGoldSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), mod_InfiTools.iceRod
                });
        mod_InfiTools.sGoldSword.setIconCoord(10, 4);
        ModLoader.addName(mod_InfiTools.sGoldSword, "Slimy Gold Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.sGoldSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_InfiTools.fGoldSword.setIconCoord(11, 4);
        ModLoader.addName(mod_InfiTools.fGoldSword, "Flaky Gold Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.fGoldSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), mod_InfiTools.flintRod
                });
        mod_InfiTools.wRedstoneSword.setIconCoord(0, 5);
        ModLoader.addName(mod_InfiTools.wRedstoneSword, "Redstone Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.wRedstoneSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), Item.stick
                });
        mod_InfiTools.stRedstoneSword.setIconCoord(1, 5);
        ModLoader.addName(mod_InfiTools.stRedstoneSword, "Stony Redstone Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.stRedstoneSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_InfiTools.iRedstoneSword.setIconCoord(2, 5);
        ModLoader.addName(mod_InfiTools.iRedstoneSword, "Hard Redstone Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.iRedstoneSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), mod_InfiTools.ironRod
                });
        mod_InfiTools.dRedstoneSword.setIconCoord(3, 5);
        ModLoader.addName(mod_InfiTools.dRedstoneSword, "Jeweled Redstone Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.dRedstoneSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), mod_InfiTools.diamondRod
                });
        mod_InfiTools.rRedstoneSword.setIconCoord(4, 5);
        ModLoader.addName(mod_InfiTools.rRedstoneSword, "Redredred Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.rRedstoneSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), mod_InfiTools.redstoneRod
                });
        mod_InfiTools.oRedstoneSword.setIconCoord(5, 5);
        ModLoader.addName(mod_InfiTools.oRedstoneSword, "Glassy Redstone Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.oRedstoneSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), mod_InfiTools.obsidianRod
                });
        mod_InfiTools.bRedstoneSword.setIconCoord(6, 5);
        ModLoader.addName(mod_InfiTools.bRedstoneSword, "Necrotic Redstone Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bRedstoneSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bRedstoneSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_InfiTools.mRedstoneSword.setIconCoord(7, 5);
        ModLoader.addName(mod_InfiTools.mRedstoneSword, "Mossy Redstone Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.mRedstoneSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), mod_InfiTools.mossyRod
                });
        mod_InfiTools.glRedstoneSword.setIconCoord(8, 5);
        ModLoader.addName(mod_InfiTools.glRedstoneSword, "Glowing Redstone Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.glRedstoneSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_InfiTools.sRedstoneSword.setIconCoord(9, 5);
        ModLoader.addName(mod_InfiTools.sRedstoneSword, "Slimy Redstone Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.sRedstoneSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_InfiTools.blRedstoneSword.setIconCoord(10, 5);
        ModLoader.addName(mod_InfiTools.blRedstoneSword, "Blazing Redstone Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.blRedstoneSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), Item.blazeRod
                });
        mod_InfiTools.wObsidianSword.setIconCoord(0, 6);
        ModLoader.addName(mod_InfiTools.wObsidianSword, "Obsidian Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.wObsidianSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), Item.stick
                });
        mod_InfiTools.stObsidianSword.setIconCoord(1, 6);
        ModLoader.addName(mod_InfiTools.stObsidianSword, "Stony Obsidian Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.stObsidianSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_InfiTools.iObsidianSword.setIconCoord(2, 6);
        ModLoader.addName(mod_InfiTools.iObsidianSword, "Hard Obsidian Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.iObsidianSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), mod_InfiTools.ironRod
                });
        mod_InfiTools.dObsidianSword.setIconCoord(3, 6);
        ModLoader.addName(mod_InfiTools.dObsidianSword, "Jeweled Obsidian Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.dObsidianSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), mod_InfiTools.diamondRod
                });
        mod_InfiTools.gObsidianSword.setIconCoord(4, 6);
        ModLoader.addName(mod_InfiTools.gObsidianSword, "Expensive Obsidian Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.gObsidianSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), mod_InfiTools.goldRod
                });
        mod_InfiTools.rObsidianSword.setIconCoord(5, 6);
        ModLoader.addName(mod_InfiTools.rObsidianSword, "Red Obsidian Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.rObsidianSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), mod_InfiTools.redstoneRod
                });
        mod_InfiTools.oObsidianSword.setIconCoord(6, 6);
        ModLoader.addName(mod_InfiTools.oObsidianSword, "Wicked Shard");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.oObsidianSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), mod_InfiTools.obsidianRod
                });
        mod_InfiTools.bObsidianSword.setIconCoord(7, 6);
        ModLoader.addName(mod_InfiTools.bObsidianSword, "Necrotic Obsidian Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bObsidianSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bObsidianSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_InfiTools.nObsidianSword.setIconCoord(8, 6);
        ModLoader.addName(mod_InfiTools.nObsidianSword, "Bloody Obsidian Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.nObsidianSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_InfiTools.glObsidianSword.setIconCoord(9, 6);
        ModLoader.addName(mod_InfiTools.glObsidianSword, "Glowing Obsidian Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.glObsidianSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_InfiTools.sObsidianSword.setIconCoord(10, 6);
        ModLoader.addName(mod_InfiTools.sObsidianSword, "Slimy Obsidian Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.sObsidianSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_InfiTools.fObsidianSword.setIconCoord(11, 6);
        ModLoader.addName(mod_InfiTools.fObsidianSword, "Flaky Obsidian Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.fObsidianSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), mod_InfiTools.flintRod
                });
        mod_InfiTools.blObsidianSword.setIconCoord(12, 6);
        ModLoader.addName(mod_InfiTools.blObsidianSword, "Blazing Obsidian Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.blObsidianSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), Item.blazeRod
                });
        mod_InfiTools.wSandstoneSword.setIconCoord(0, 7);
        ModLoader.addName(mod_InfiTools.wSandstoneSword, "Sandstone Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.wSandstoneSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), Item.stick
                });
        mod_InfiTools.stSandstoneSword.setIconCoord(1, 7);
        ModLoader.addName(mod_InfiTools.stSandstoneSword, "Stony Sandstone Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.stSandstoneSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_InfiTools.saSandstoneSword.setIconCoord(2, 7);
        ModLoader.addName(mod_InfiTools.saSandstoneSword, "Sandmite");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.saSandstoneSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), mod_InfiTools.sandstoneRod
                });
        mod_InfiTools.bSandstoneSword.setIconCoord(3, 7);
        ModLoader.addName(mod_InfiTools.bSandstoneSword, "Necrotic Sandstone Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bSandstoneSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bSandstoneSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_InfiTools.pSandstoneSword.setIconCoord(4, 7);
        ModLoader.addName(mod_InfiTools.pSandstoneSword, "Fibery Sandstone Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.pSandstoneSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), mod_InfiTools.paperRod
                });
        mod_InfiTools.nSandstoneSword.setIconCoord(5, 7);
        ModLoader.addName(mod_InfiTools.nSandstoneSword, "Bloody Sandstone Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.nSandstoneSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_InfiTools.iceSandstoneSword.setIconCoord(6, 7);
        ModLoader.addName(mod_InfiTools.iceSandstoneSword, "Icy Sandstone Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.iceSandstoneSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), mod_InfiTools.iceRod
                });
        mod_InfiTools.sSandstoneSword.setIconCoord(7, 7);
        ModLoader.addName(mod_InfiTools.sSandstoneSword, "Slimy Sandstone Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.sSandstoneSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_InfiTools.cSandstoneSword.setIconCoord(8, 7);
        ModLoader.addName(mod_InfiTools.cSandstoneSword, "Spiny Sandstone Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.cSandstoneSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_InfiTools.fSandstoneSword.setIconCoord(9, 7);
        ModLoader.addName(mod_InfiTools.fSandstoneSword, "Flaky Sandstone Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.fSandstoneSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), mod_InfiTools.flintRod
                });
        mod_InfiTools.brSandstoneSword.setIconCoord(10, 7);
        ModLoader.addName(mod_InfiTools.brSandstoneSword, "Baked Sandstone Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.brSandstoneSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), mod_InfiTools.brickRod
                });
        mod_InfiTools.wBoneSword.setIconCoord(0, 8);
        ModLoader.addName(mod_InfiTools.wBoneSword, "Bone Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.wBoneSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Item.bone, Character.valueOf('|'), Item.stick
                });
        mod_InfiTools.stBoneSword.setIconCoord(1, 8);
        ModLoader.addName(mod_InfiTools.stBoneSword, "Stony Bone Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.stBoneSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_InfiTools.iBoneSword.setIconCoord(2, 8);
        ModLoader.addName(mod_InfiTools.iBoneSword, "Hard Bone Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.iBoneSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.ironRod
                });
        mod_InfiTools.dBoneSword.setIconCoord(3, 8);
        ModLoader.addName(mod_InfiTools.dBoneSword, "Jeweled Bone Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.dBoneSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.diamondRod
                });
        mod_InfiTools.rBoneSword.setIconCoord(4, 8);
        ModLoader.addName(mod_InfiTools.rBoneSword, "Red Bone Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.rBoneSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.redstoneRod
                });
        mod_InfiTools.oBoneSword.setIconCoord(5, 8);
        ModLoader.addName(mod_InfiTools.oBoneSword, "Glassy Bone Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.oBoneSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.obsidianRod
                });
        mod_InfiTools.bBoneSword.setIconCoord(6, 8);
        ModLoader.addName(mod_InfiTools.bBoneSword, "Reanimated Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bBoneSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Item.bone, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bBoneSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_InfiTools.mBoneSword.setIconCoord(7, 8);
        ModLoader.addName(mod_InfiTools.mBoneSword, "Mossy Bone Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.mBoneSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.mossyRod
                });
        mod_InfiTools.nBoneSword.setIconCoord(8, 8);
        ModLoader.addName(mod_InfiTools.nBoneSword, "Netherrack Bone Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.nBoneSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_InfiTools.glBoneSword.setIconCoord(9, 8);
        ModLoader.addName(mod_InfiTools.glBoneSword, "Glowstone Bone Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.glBoneSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_InfiTools.sBoneSword.setIconCoord(10, 8);
        ModLoader.addName(mod_InfiTools.sBoneSword, "Slimy Bone Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.sBoneSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_InfiTools.cBoneSword.setIconCoord(11, 8);
        ModLoader.addName(mod_InfiTools.cBoneSword, "Spiny Bone Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.cBoneSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_InfiTools.fBoneSword.setIconCoord(12, 8);
        ModLoader.addName(mod_InfiTools.fBoneSword, "Flaky Bone Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.fBoneSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.flintRod
                });
        mod_InfiTools.brBoneSword.setIconCoord(13, 8);
        ModLoader.addName(mod_InfiTools.brBoneSword, "Baked Bone Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.brBoneSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.brickRod
                });
        mod_InfiTools.blBoneSword.setIconCoord(14, 8);
        ModLoader.addName(mod_InfiTools.blBoneSword, "Blazing Bone Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.blBoneSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Item.bone, Character.valueOf('|'), Item.blazeRod
                });
        mod_InfiTools.wPaperSword.setIconCoord(0, 9);
        ModLoader.addName(mod_InfiTools.wPaperSword, "Paper Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.wPaperSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), mod_InfiTools.paperStack, Character.valueOf('|'), Item.stick
                });
        mod_InfiTools.saPaperSword.setIconCoord(1, 9);
        ModLoader.addName(mod_InfiTools.saPaperSword, "Stony Paper Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.saPaperSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), mod_InfiTools.paperStack, Character.valueOf('|'), mod_InfiTools.sandstoneRod
                });
        mod_InfiTools.bPaperSword.setIconCoord(2, 9);
        ModLoader.addName(mod_InfiTools.bPaperSword, "Necrotic Paper Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bPaperSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), mod_InfiTools.paperStack, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bPaperSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), mod_InfiTools.paperStack, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_InfiTools.pPaperSword.setIconCoord(3, 9);
        ModLoader.addName(mod_InfiTools.pPaperSword, "Pinata Stick");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.pPaperSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), mod_InfiTools.paperStack, Character.valueOf('|'), mod_InfiTools.paperRod
                });
        mod_InfiTools.sPaperSword.setIconCoord(4, 9);
        ModLoader.addName(mod_InfiTools.sPaperSword, "Slimy Paper Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.sPaperSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), mod_InfiTools.paperStack, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_InfiTools.cPaperSword.setIconCoord(5, 9);
        ModLoader.addName(mod_InfiTools.cPaperSword, "Spiny Paper Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.cPaperSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), mod_InfiTools.paperStack, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_InfiTools.brPaperSword.setIconCoord(6, 9);
        ModLoader.addName(mod_InfiTools.brPaperSword, "Baked Paper Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.brPaperSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), mod_InfiTools.paperStack, Character.valueOf('|'), mod_InfiTools.brickRod
                });
        mod_InfiTools.stMossySword.setIconCoord(0, 10);
        ModLoader.addName(mod_InfiTools.stMossySword, "Stony Moss-Covered Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.stMossySword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), mod_InfiTools.mossBallCrafted, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_InfiTools.dMossySword.setIconCoord(1, 10);
        ModLoader.addName(mod_InfiTools.dMossySword, "Jeweled Moss-Covered Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.dMossySword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), mod_InfiTools.mossBallCrafted, Character.valueOf('|'), mod_InfiTools.diamondRod
                });
        mod_InfiTools.rMossySword.setIconCoord(2, 10);
        ModLoader.addName(mod_InfiTools.rMossySword, "Red Moss-Covered Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.rMossySword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), mod_InfiTools.mossBallCrafted, Character.valueOf('|'), mod_InfiTools.redstoneRod
                });
        mod_InfiTools.bMossySword.setIconCoord(3, 10);
        ModLoader.addName(mod_InfiTools.bMossySword, "Necrotic Moss-Covered Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bMossySword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), mod_InfiTools.mossBallCrafted, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bMossySword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), mod_InfiTools.mossBallCrafted, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_InfiTools.mMossySword.setIconCoord(4, 10);
        ModLoader.addName(mod_InfiTools.mMossySword, "Living Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.mMossySword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), mod_InfiTools.mossBallCrafted, Character.valueOf('|'), mod_InfiTools.mossyRod
                });
        mod_InfiTools.glMossySword.setIconCoord(5, 10);
        ModLoader.addName(mod_InfiTools.glMossySword, "Glowing Moss-Covered Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.glMossySword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), mod_InfiTools.mossBallCrafted, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_InfiTools.wNetherrackSword.setIconCoord(0, 11);
        ModLoader.addName(mod_InfiTools.wNetherrackSword, "Netherrack Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.wNetherrackSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), Item.stick
                });
        mod_InfiTools.stNetherrackSword.setIconCoord(1, 11);
        ModLoader.addName(mod_InfiTools.stNetherrackSword, "Stony Netherrack Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.stNetherrackSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_InfiTools.iNetherrackSword.setIconCoord(2, 11);
        ModLoader.addName(mod_InfiTools.iNetherrackSword, "Hard Netherrack Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.iNetherrackSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.ironRod
                });
        mod_InfiTools.rNetherrackSword.setIconCoord(3, 11);
        ModLoader.addName(mod_InfiTools.rNetherrackSword, "Red Netherrack Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.rNetherrackSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.redstoneRod
                });
        mod_InfiTools.oNetherrackSword.setIconCoord(4, 11);
        ModLoader.addName(mod_InfiTools.oNetherrackSword, "Glassy Netherrack Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.oNetherrackSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.obsidianRod
                });
        mod_InfiTools.saNetherrackSword.setIconCoord(5, 11);
        ModLoader.addName(mod_InfiTools.saNetherrackSword, "Sandy Netherrack Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.saNetherrackSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.sandstoneRod
                });
        mod_InfiTools.bNetherrackSword.setIconCoord(6, 11);
        ModLoader.addName(mod_InfiTools.bNetherrackSword, "Necrotic Netherrack Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bNetherrackSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bNetherrackSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_InfiTools.mNetherrackSword.setIconCoord(7, 11);
        ModLoader.addName(mod_InfiTools.mNetherrackSword, "Mossy Netherrack Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.mNetherrackSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.mossyRod
                });
        mod_InfiTools.nNetherrackSword.setIconCoord(8, 11);
        ModLoader.addName(mod_InfiTools.nNetherrackSword, "Blood Blade");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.nNetherrackSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_InfiTools.glNetherrackSword.setIconCoord(9, 11);
        ModLoader.addName(mod_InfiTools.glNetherrackSword, "Glowing Netherrack Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.glNetherrackSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_InfiTools.iceNetherrackSword.setIconCoord(10, 11);
        ModLoader.addName(mod_InfiTools.iceNetherrackSword, "Icy Netherrack Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.iceNetherrackSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.iceRod
                });
        mod_InfiTools.sNetherrackSword.setIconCoord(11, 11);
        ModLoader.addName(mod_InfiTools.sNetherrackSword, "Slimy Netherrack Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.sNetherrackSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_InfiTools.cNetherrackSword.setIconCoord(12, 11);
        ModLoader.addName(mod_InfiTools.cNetherrackSword, "Spiny Netherrack Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.cNetherrackSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_InfiTools.fNetherrackSword.setIconCoord(13, 11);
        ModLoader.addName(mod_InfiTools.fNetherrackSword, "Flaky Netherrack Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.fNetherrackSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_InfiTools.brNetherrackSword.setIconCoord(14, 11);
        ModLoader.addName(mod_InfiTools.brNetherrackSword, "Baked Netherrack Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.brNetherrackSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_InfiTools.blNetherrackSword.setIconCoord(15, 11);
        ModLoader.addName(mod_InfiTools.blNetherrackSword, "Blazing Netherrack Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.blNetherrackSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), Item.blazeRod
                });
        mod_InfiTools.wGlowstoneSword.setIconCoord(0, 12);
        ModLoader.addName(mod_InfiTools.wGlowstoneSword, "Glowstone Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.wGlowstoneSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), Item.stick
                });
        mod_InfiTools.stGlowstoneSword.setIconCoord(1, 12);
        ModLoader.addName(mod_InfiTools.stGlowstoneSword, "Stony Glowstone Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.stGlowstoneSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_InfiTools.iGlowstoneSword.setIconCoord(2, 12);
        ModLoader.addName(mod_InfiTools.iGlowstoneSword, "Hard Glowstone Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.iGlowstoneSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.ironRod
                });
        mod_InfiTools.dGlowstoneSword.setIconCoord(3, 12);
        ModLoader.addName(mod_InfiTools.dGlowstoneSword, "Jeweled Glowstone Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.dGlowstoneSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.diamondRod
                });
        mod_InfiTools.rGlowstoneSword.setIconCoord(4, 12);
        ModLoader.addName(mod_InfiTools.rGlowstoneSword, "Red Glowstone Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.rGlowstoneSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.redstoneRod
                });
        mod_InfiTools.oGlowstoneSword.setIconCoord(5, 12);
        ModLoader.addName(mod_InfiTools.oGlowstoneSword, "Glassy Glowstone Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.oGlowstoneSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.obsidianRod
                });
        mod_InfiTools.bGlowstoneSword.setIconCoord(6, 12);
        ModLoader.addName(mod_InfiTools.bGlowstoneSword, "Necrotic Glowstone Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bGlowstoneSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bGlowstoneSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_InfiTools.mGlowstoneSword.setIconCoord(7, 12);
        ModLoader.addName(mod_InfiTools.mGlowstoneSword, "Mossy Glowstone Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.mGlowstoneSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.mossyRod
                });
        mod_InfiTools.nGlowstoneSword.setIconCoord(8, 12);
        ModLoader.addName(mod_InfiTools.nGlowstoneSword, "Bloody Glowstone Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.nGlowstoneSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_InfiTools.glGlowstoneSword.setIconCoord(9, 12);
        ModLoader.addName(mod_InfiTools.glGlowstoneSword, "Bright Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.glGlowstoneSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_InfiTools.iceGlowstoneSword.setIconCoord(10, 12);
        ModLoader.addName(mod_InfiTools.iceGlowstoneSword, "Icy Glowstone Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.iceGlowstoneSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.iceRod
                });
        mod_InfiTools.lGlowstoneSword.setIconCoord(11, 12);
        ModLoader.addName(mod_InfiTools.lGlowstoneSword, "Fiery Glowstone Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.lGlowstoneSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_InfiTools.sGlowstoneSword.setIconCoord(12, 12);
        ModLoader.addName(mod_InfiTools.sGlowstoneSword, "Slimy Glowstone Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.sGlowstoneSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_InfiTools.blGlowstoneSword.setIconCoord(13, 12);
        ModLoader.addName(mod_InfiTools.blGlowstoneSword, "Blazing Glowstone Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.blGlowstoneSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), Item.blazeRod
                });
        mod_InfiTools.wIceSword.setIconCoord(0, 13);
        ModLoader.addName(mod_InfiTools.wIceSword, "Ice Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.wIceSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.ice, Character.valueOf('|'), Item.stick
                });
        mod_InfiTools.stIceSword.setIconCoord(1, 13);
        ModLoader.addName(mod_InfiTools.stIceSword, "Stony Ice Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.stIceSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_InfiTools.iIceSword.setIconCoord(2, 13);
        ModLoader.addName(mod_InfiTools.iIceSword, "Hard Ice Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.iIceSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.ironRod
                });
        mod_InfiTools.dIceSword.setIconCoord(3, 13);
        ModLoader.addName(mod_InfiTools.dIceSword, "Jeweled Ice Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.dIceSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.diamondRod
                });
        mod_InfiTools.gIceSword.setIconCoord(4, 13);
        ModLoader.addName(mod_InfiTools.gIceSword, "Expensive Ice Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.gIceSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.goldRod
                });
        mod_InfiTools.rIceSword.setIconCoord(5, 13);
        ModLoader.addName(mod_InfiTools.rIceSword, "Red Ice Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.rIceSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.redstoneRod
                });
        mod_InfiTools.oIceSword.setIconCoord(6, 13);
        ModLoader.addName(mod_InfiTools.oIceSword, "Glassy Ice Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.oIceSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.obsidianRod
                });
        mod_InfiTools.saIceSword.setIconCoord(7, 13);
        ModLoader.addName(mod_InfiTools.saIceSword, "Sandy Ice Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.saIceSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.sandstoneRod
                });
        mod_InfiTools.bIceSword.setIconCoord(8, 13);
        ModLoader.addName(mod_InfiTools.bIceSword, "Necrotic Ice Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bIceSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.ice, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bIceSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_InfiTools.glIceSword.setIconCoord(9, 13);
        ModLoader.addName(mod_InfiTools.glIceSword, "Glowing Ice Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.glIceSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_InfiTools.iceIceSword.setIconCoord(10, 13);
        ModLoader.addName(mod_InfiTools.iceIceSword, "Freezing Blade");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.iceIceSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.iceRod
                });
        mod_InfiTools.sIceSword.setIconCoord(11, 13);
        ModLoader.addName(mod_InfiTools.sIceSword, "Slimy Ice Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.sIceSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_InfiTools.cIceSword.setIconCoord(12, 13);
        ModLoader.addName(mod_InfiTools.cIceSword, "Spiny Ice Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.cIceSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_InfiTools.fIceSword.setIconCoord(13, 13);
        ModLoader.addName(mod_InfiTools.fIceSword, "Flaky Ice Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.fIceSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.flintRod
                });
        mod_InfiTools.brIceSword.setIconCoord(14, 13);
        ModLoader.addName(mod_InfiTools.brIceSword, "Baked Ice Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.brIceSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.brickRod
                });
        mod_InfiTools.dLavaSword.setIconCoord(0, 14);
        ModLoader.addName(mod_InfiTools.dLavaSword, "Jeweled Lava Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.dLavaSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), mod_InfiTools.lavaCrystal, Character.valueOf('|'), mod_InfiTools.diamondRod
                });
        mod_InfiTools.rLavaSword.setIconCoord(1, 14);
        ModLoader.addName(mod_InfiTools.rLavaSword, "Red Lava Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.rLavaSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), mod_InfiTools.lavaCrystal, Character.valueOf('|'), mod_InfiTools.redstoneRod
                });
        mod_InfiTools.bLavaSword.setIconCoord(2, 14);
        ModLoader.addName(mod_InfiTools.bLavaSword, "Necrotic Lava Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bLavaSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), mod_InfiTools.lavaCrystal, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bLavaSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), mod_InfiTools.lavaCrystal, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_InfiTools.nLavaSword.setIconCoord(3, 14);
        ModLoader.addName(mod_InfiTools.nLavaSword, "Bloody Lava Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.nLavaSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), mod_InfiTools.lavaCrystal, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_InfiTools.glLavaSword.setIconCoord(4, 14);
        ModLoader.addName(mod_InfiTools.glLavaSword, "Glowing Lava Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.glLavaSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), mod_InfiTools.lavaCrystal, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_InfiTools.lLavaSword.setIconCoord(5, 14);
        ModLoader.addName(mod_InfiTools.lLavaSword, "Flamebrand");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.lLavaSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), mod_InfiTools.lavaCrystal, Character.valueOf('|'), mod_InfiTools.lavaRod
                });
        mod_InfiTools.blLavaSword.setIconCoord(6, 14);
        ModLoader.addName(mod_InfiTools.blLavaSword, "Blazing Lava Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.blLavaSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), mod_InfiTools.lavaCrystal, Character.valueOf('|'), Item.blazeRod
                });
        mod_InfiTools.wSlimeSword.setIconCoord(0, 15);
        ModLoader.addName(mod_InfiTools.wSlimeSword, "Slime Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.wSlimeSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), Item.stick
                });
        mod_InfiTools.stSlimeSword.setIconCoord(1, 15);
        ModLoader.addName(mod_InfiTools.stSlimeSword, "Stony Slime Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.stSlimeSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_InfiTools.iSlimeSword.setIconCoord(2, 15);
        ModLoader.addName(mod_InfiTools.iSlimeSword, "Hard Slime Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.iSlimeSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.ironRod
                });
        mod_InfiTools.dSlimeSword.setIconCoord(3, 15);
        ModLoader.addName(mod_InfiTools.dSlimeSword, "Jeweled Slime Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.dSlimeSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.diamondRod
                });
        mod_InfiTools.gSlimeSword.setIconCoord(4, 15);
        ModLoader.addName(mod_InfiTools.gSlimeSword, "Expensive Slime Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.gSlimeSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.goldRod
                });
        mod_InfiTools.rSlimeSword.setIconCoord(5, 15);
        ModLoader.addName(mod_InfiTools.rSlimeSword, "Red Slime Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.rSlimeSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.redstoneRod
                });
        mod_InfiTools.oSlimeSword.setIconCoord(6, 15);
        ModLoader.addName(mod_InfiTools.oSlimeSword, "Glassy Slime Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.oSlimeSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.obsidianRod
                });
        mod_InfiTools.saSlimeSword.setIconCoord(7, 15);
        ModLoader.addName(mod_InfiTools.saSlimeSword, "Sandy Slime Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.saSlimeSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.sandstoneRod
                });
        mod_InfiTools.bSlimeSword.setIconCoord(8, 15);
        ModLoader.addName(mod_InfiTools.bSlimeSword, "Necrotic Slime Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bSlimeSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bSlimeSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_InfiTools.pSlimeSword.setIconCoord(9, 15);
        ModLoader.addName(mod_InfiTools.pSlimeSword, "Fibery Slime Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.pSlimeSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.paperRod
                });
        mod_InfiTools.mSlimeSword.setIconCoord(10, 15);
        ModLoader.addName(mod_InfiTools.mSlimeSword, "Mossy Slime Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.mSlimeSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.mossyRod
                });
        mod_InfiTools.nSlimeSword.setIconCoord(11, 15);
        ModLoader.addName(mod_InfiTools.nSlimeSword, "Bloody Slime Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.nSlimeSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_InfiTools.glSlimeSword.setIconCoord(12, 15);
        ModLoader.addName(mod_InfiTools.glSlimeSword, "Glowing Slime Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.glSlimeSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_InfiTools.iceSlimeSword.setIconCoord(13, 15);
        ModLoader.addName(mod_InfiTools.iceSlimeSword, "Icy Slime Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.iceSlimeSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.iceRod
                });
        mod_InfiTools.lSlimeSword.setIconCoord(14, 15);
        ModLoader.addName(mod_InfiTools.lSlimeSword, "Fiery Slime Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.lSlimeSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.lavaRod
                });
        mod_InfiTools.sSlimeSword.setIconCoord(15, 15);
        ModLoader.addName(mod_InfiTools.sSlimeSword, "Slimewhacker");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.sSlimeSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_InfiTools.cSlimeSword.setIconCoord(12, 14);
        ModLoader.addName(mod_InfiTools.cSlimeSword, "Spiny Slime Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.cSlimeSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_InfiTools.fSlimeSword.setIconCoord(13, 14);
        ModLoader.addName(mod_InfiTools.fSlimeSword, "Flaky Slime Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.fSlimeSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.flintRod
                });
        mod_InfiTools.brSlimeSword.setIconCoord(14, 14);
        ModLoader.addName(mod_InfiTools.brSlimeSword, "Baked Slime Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.brSlimeSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.brickRod
                });
        mod_InfiTools.blSlimeSword.setIconCoord(15, 14);
        ModLoader.addName(mod_InfiTools.blSlimeSword, "Blazing Slime Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.blSlimeSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), Item.blazeRod
                });
        mod_InfiTools.wCactusSword.setIconCoord(10, 0);
        ModLoader.addName(mod_InfiTools.wCactusSword, "Cactus Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.wCactusSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.cactus, Character.valueOf('|'), Item.stick
                });
        mod_InfiTools.stCactusSword.setIconCoord(11, 0);
        ModLoader.addName(mod_InfiTools.stCactusSword, "Stony Cactus Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.stCactusSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.cactus, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_InfiTools.saCactusSword.setIconCoord(12, 0);
        ModLoader.addName(mod_InfiTools.saCactusSword, "Sandy Cactus Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.saCactusSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.cactus, Character.valueOf('|'), mod_InfiTools.sandstoneRod
                });
        mod_InfiTools.bCactusSword.setIconCoord(13, 0);
        ModLoader.addName(mod_InfiTools.bCactusSword, "Necrotic Cactus Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bCactusSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.cactus, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bCactusSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.cactus, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_InfiTools.pCactusSword.setIconCoord(14, 0);
        ModLoader.addName(mod_InfiTools.pCactusSword, "Fibery Cactus Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.pCactusSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.cactus, Character.valueOf('|'), mod_InfiTools.paperRod
                });
        mod_InfiTools.nCactusSword.setIconCoord(15, 0);
        ModLoader.addName(mod_InfiTools.nCactusSword, "Bloody Cactus Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.nCactusSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.cactus, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_InfiTools.sCactusSword.setIconCoord(12, 1);
        ModLoader.addName(mod_InfiTools.sCactusSword, "Slimy Cactus Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.sCactusSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.cactus, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_InfiTools.cCactusSword.setIconCoord(13, 1);
        ModLoader.addName(mod_InfiTools.cCactusSword, "Poking Stick");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.cCactusSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.cactus, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_InfiTools.fCactusSword.setIconCoord(14, 1);
        ModLoader.addName(mod_InfiTools.fCactusSword, "Flaky Cactus Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.fCactusSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.cactus, Character.valueOf('|'), mod_InfiTools.flintRod
                });
        mod_InfiTools.brCactusSword.setIconCoord(15, 1);
        ModLoader.addName(mod_InfiTools.brCactusSword, "Baked Cactus Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.brCactusSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.cactus, Character.valueOf('|'), mod_InfiTools.brickRod
                });
        mod_InfiTools.wFlintSword.setIconCoord(13, 2);
        ModLoader.addName(mod_InfiTools.wFlintSword, "Flint Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.wFlintSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Item.flint, Character.valueOf('|'), Item.stick
                });
        mod_InfiTools.stFlintSword.setIconCoord(14, 2);
        ModLoader.addName(mod_InfiTools.stFlintSword, "Stony Flint Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.stFlintSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_InfiTools.iFlintSword.setIconCoord(15, 2);
        ModLoader.addName(mod_InfiTools.iFlintSword, "Hard Flint Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.iFlintSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.ironRod
                });
        mod_InfiTools.gFlintSword.setIconCoord(12, 3);
        ModLoader.addName(mod_InfiTools.gFlintSword, "Expensive Flint Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.gFlintSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.goldRod
                });
        mod_InfiTools.oFlintSword.setIconCoord(13, 3);
        ModLoader.addName(mod_InfiTools.oFlintSword, "Glassy Flint Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.oFlintSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.obsidianRod
                });
        mod_InfiTools.saFlintSword.setIconCoord(14, 3);
        ModLoader.addName(mod_InfiTools.saFlintSword, "Sandy Flint Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.saFlintSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.sandstoneRod
                });
        mod_InfiTools.bFlintSword.setIconCoord(15, 3);
        ModLoader.addName(mod_InfiTools.bFlintSword, "Necrotic Flint Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bFlintSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Item.flint, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bFlintSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_InfiTools.nFlintSword.setIconCoord(12, 4);
        ModLoader.addName(mod_InfiTools.nFlintSword, "Bloody Flint Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.nFlintSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_InfiTools.iceFlintSword.setIconCoord(13, 4);
        ModLoader.addName(mod_InfiTools.iceFlintSword, "Icy Flint Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.iceFlintSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.iceRod
                });
        mod_InfiTools.sFlintSword.setIconCoord(14, 4);
        ModLoader.addName(mod_InfiTools.sFlintSword, "Slimy Flint Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.sFlintSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_InfiTools.cFlintSword.setIconCoord(15, 4);
        ModLoader.addName(mod_InfiTools.cFlintSword, "Spiny Flint Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.cFlintSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_InfiTools.fFlintSword.setIconCoord(11, 5);
        ModLoader.addName(mod_InfiTools.fFlintSword, "Flaky Shaver");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.fFlintSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.flintRod
                });
        mod_InfiTools.brFlintSword.setIconCoord(12, 5);
        ModLoader.addName(mod_InfiTools.brFlintSword, "Baked Flint Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.brFlintSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.brickRod
                });
        mod_InfiTools.blFlintSword.setIconCoord(13, 5);
        ModLoader.addName(mod_InfiTools.blFlintSword, "Blazing Flint Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.blFlintSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Item.flint, Character.valueOf('|'), Item.blazeRod
                });
        mod_InfiTools.wBrickSword.setIconCoord(14, 5);
        ModLoader.addName(mod_InfiTools.wBrickSword, "Brick Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.wBrickSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Item.brick, Character.valueOf('|'), Item.stick
                });
        mod_InfiTools.stBrickSword.setIconCoord(15, 5);
        ModLoader.addName(mod_InfiTools.stBrickSword, "Stony Brick Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.stBrickSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Item.brick, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_InfiTools.saBrickSword.setIconCoord(13, 6);
        ModLoader.addName(mod_InfiTools.saBrickSword, "Sandstone Brick Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.saBrickSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Item.brick, Character.valueOf('|'), mod_InfiTools.sandstoneRod
                });
        mod_InfiTools.bBrickSword.setIconCoord(14, 6);
        ModLoader.addName(mod_InfiTools.bBrickSword, "Necrotic Brick Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bBrickSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Item.brick, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bBrickSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Item.brick, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_InfiTools.pBrickSword.setIconCoord(15, 6);
        ModLoader.addName(mod_InfiTools.pBrickSword, "Fibery Brick Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.pBrickSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Item.brick, Character.valueOf('|'), mod_InfiTools.paperRod
                });
        mod_InfiTools.nBrickSword.setIconCoord(11, 7);
        ModLoader.addName(mod_InfiTools.nBrickSword, "Bloody Brick Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.nBrickSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Item.brick, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_InfiTools.iceBrickSword.setIconCoord(12, 7);
        ModLoader.addName(mod_InfiTools.iceBrickSword, "Icy Brick Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.iceBrickSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Item.brick, Character.valueOf('|'), mod_InfiTools.iceRod
                });
        mod_InfiTools.sBrickSword.setIconCoord(13, 7);
        ModLoader.addName(mod_InfiTools.sBrickSword, "Slimy Brick Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.sBrickSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Item.brick, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_InfiTools.cBrickSword.setIconCoord(14, 7);
        ModLoader.addName(mod_InfiTools.cBrickSword, "Spiny Brick Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.cBrickSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Item.brick, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_InfiTools.fBrickSword.setIconCoord(15, 7);
        ModLoader.addName(mod_InfiTools.fBrickSword, "Flaky Brick Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.fBrickSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Item.brick, Character.valueOf('|'), mod_InfiTools.flintRod
                });
        mod_InfiTools.brBrickSword.setIconCoord(15, 8);
        ModLoader.addName(mod_InfiTools.brBrickSword, "Look-alike Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.brBrickSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Item.brick, Character.valueOf('|'), mod_InfiTools.brickRod
                });
        mod_InfiTools.dBlazeSword.setIconCoord(7, 9);
        ModLoader.addName(mod_InfiTools.dBlazeSword, "Jeweled Blaze Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.dBlazeSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), mod_InfiTools.blazeCrystal, Character.valueOf('|'), mod_InfiTools.diamondRod
                });
        mod_InfiTools.rBlazeSword.setIconCoord(8, 9);
        ModLoader.addName(mod_InfiTools.rBlazeSword, "Red Blaze Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.rBlazeSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), mod_InfiTools.blazeCrystal, Character.valueOf('|'), mod_InfiTools.redstoneRod
                });
        mod_InfiTools.bBlazeSword.setIconCoord(9, 9);
        ModLoader.addName(mod_InfiTools.bBlazeSword, "Necrotic Blaze Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bBlazeSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), mod_InfiTools.blazeCrystal, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bBlazeSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), mod_InfiTools.blazeCrystal, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_InfiTools.nBlazeSword.setIconCoord(10, 9);
        ModLoader.addName(mod_InfiTools.nBlazeSword, "Bloody Blaze Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.nBlazeSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), mod_InfiTools.blazeCrystal, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_InfiTools.glBlazeSword.setIconCoord(11, 9);
        ModLoader.addName(mod_InfiTools.glBlazeSword, "Glowing Blaze Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.glBlazeSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), mod_InfiTools.blazeCrystal, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_InfiTools.lBlazeSword.setIconCoord(12, 9);
        ModLoader.addName(mod_InfiTools.lBlazeSword, "Fiery Blaze Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.lBlazeSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), mod_InfiTools.blazeCrystal, Character.valueOf('|'), mod_InfiTools.lavaRod
                });
        mod_InfiTools.fBlazeSword.setIconCoord(13, 9);
        ModLoader.addName(mod_InfiTools.fBlazeSword, "Flaky Blaze Sword");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.fBlazeSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), mod_InfiTools.blazeCrystal, Character.valueOf('|'), mod_InfiTools.flintRod
                });
        mod_InfiTools.blBlazeSword.setIconCoord(14, 9);
        ModLoader.addName(mod_InfiTools.blBlazeSword, "Worldsmite");
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.blBlazeSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), mod_InfiTools.blazeCrystal, Character.valueOf('|'), Item.blazeRod
                });
    }
}
