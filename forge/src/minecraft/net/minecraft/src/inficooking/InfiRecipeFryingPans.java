package net.minecraft.src.inficooking;

import net.minecraft.src.*;

public class InfiRecipeFryingPans
{
    public InfiRecipeFryingPans()
    {
    }

    public static int recipeStorm()
    {
        mod_InfiCooking.wWoodFryingPan.setIconCoord(0, 0);
        ModLoader.addName(mod_InfiCooking.wWoodFryingPan, "Wooden Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.wWoodFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Block.planks, Character.valueOf('|'), Item.stick
                });
        mod_InfiCooking.stWoodFryingPan.setIconCoord(1, 0);
        ModLoader.addName(mod_InfiCooking.stWoodFryingPan, "Stony Wooden Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.stWoodFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Block.planks, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_InfiCooking.saWoodFryingPan.setIconCoord(2, 0);
        ModLoader.addName(mod_InfiCooking.saWoodFryingPan, "Sandy Wooden Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.saWoodFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Block.planks, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_InfiCooking.bWoodFryingPan.setIconCoord(3, 0);
        ModLoader.addName(mod_InfiCooking.bWoodFryingPan, "Necrotic Wooden Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.bWoodFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Block.planks, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.bWoodFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Block.planks, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_InfiCooking.pWoodFryingPan.setIconCoord(4, 0);
        ModLoader.addName(mod_InfiCooking.pWoodFryingPan, "Fibery Wooden Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.pWoodFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Block.planks, Character.valueOf('|'), mod_InfiTools.paperRod
                });
        mod_InfiCooking.nWoodFryingPan.setIconCoord(5, 0);
        ModLoader.addName(mod_InfiCooking.nWoodFryingPan, "Bloody Wooden Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.nWoodFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Block.planks, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_InfiCooking.sWoodFryingPan.setIconCoord(6, 0);
        ModLoader.addName(mod_InfiCooking.sWoodFryingPan, "Slimy Wooden Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.sWoodFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Block.planks, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_InfiCooking.cWoodFryingPan.setIconCoord(7, 0);
        ModLoader.addName(mod_InfiCooking.cWoodFryingPan, "Spiny Wooden Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.cWoodFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Block.planks, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_InfiCooking.fWoodFryingPan.setIconCoord(8, 0);
        ModLoader.addName(mod_InfiCooking.fWoodFryingPan, "Flaky Wooden Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.fWoodFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Block.planks, Character.valueOf('|'), mod_InfiTools.flintRod
                });
        mod_InfiCooking.brWoodFryingPan.setIconCoord(9, 0);
        ModLoader.addName(mod_InfiCooking.brWoodFryingPan, "Baked Wooden Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.brWoodFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Block.planks, Character.valueOf('|'), mod_InfiTools.brickRod
                });
        mod_InfiCooking.wStoneFryingPan.setIconCoord(0, 1);
        ModLoader.addName(mod_InfiCooking.wStoneFryingPan, "Stone Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.wStoneFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), Item.stick
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.wStoneFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Block.stone, Character.valueOf('|'), Item.stick
                });
        mod_InfiCooking.stStoneFryingPan.setIconCoord(1, 1);
        ModLoader.addName(mod_InfiCooking.stStoneFryingPan, "Heavy Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.stStoneFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.stStoneFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Block.stone, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_InfiCooking.saStoneFryingPan.setIconCoord(2, 1);
        ModLoader.addName(mod_InfiCooking.saStoneFryingPan, "Sandy Stone Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.saStoneFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), mod_InfiTools.sandstoneRod
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.saStoneFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Block.stone, Character.valueOf('|'), mod_InfiTools.sandstoneRod
                });
        mod_InfiCooking.bStoneFryingPan.setIconCoord(3, 1);
        ModLoader.addName(mod_InfiCooking.bStoneFryingPan, "Necrotic Stone Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.bStoneFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.bStoneFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Block.stone, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.bStoneFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.bStoneFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Block.stone, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_InfiCooking.pStoneFryingPan.setIconCoord(4, 1);
        ModLoader.addName(mod_InfiCooking.pStoneFryingPan, "Fibery Stone Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.pStoneFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), mod_InfiTools.paperRod
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.pStoneFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Block.stone, Character.valueOf('|'), mod_InfiTools.paperRod
                });
        mod_InfiCooking.mStoneFryingPan.setIconCoord(5, 1);
        ModLoader.addName(mod_InfiCooking.mStoneFryingPan, "Mossy Stone Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.mStoneFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), mod_InfiTools.mossyRod
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.mStoneFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Block.stone, Character.valueOf('|'), mod_InfiTools.mossyRod
                });
        mod_InfiCooking.nStoneFryingPan.setIconCoord(6, 1);
        ModLoader.addName(mod_InfiCooking.nStoneFryingPan, "Bloody Stone Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.nStoneFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.nStoneFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Block.stone, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_InfiCooking.iceStoneFryingPan.setIconCoord(7, 1);
        ModLoader.addName(mod_InfiCooking.iceStoneFryingPan, "Icy Stone Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.iceStoneFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), mod_InfiTools.iceRod
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.iceStoneFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Block.stone, Character.valueOf('|'), mod_InfiTools.iceRod
                });
        mod_InfiCooking.sStoneFryingPan.setIconCoord(8, 1);
        ModLoader.addName(mod_InfiCooking.sStoneFryingPan, "Slimy Stone Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.sStoneFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.sStoneFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Block.stone, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_InfiCooking.cStoneFryingPan.setIconCoord(9, 1);
        ModLoader.addName(mod_InfiCooking.cStoneFryingPan, "Spiny Stone Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.cStoneFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.cStoneFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Block.stone, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_InfiCooking.fStoneFryingPan.setIconCoord(10, 1);
        ModLoader.addName(mod_InfiCooking.fStoneFryingPan, "Flaky Stone Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.fStoneFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), mod_InfiTools.flintRod
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.fStoneFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Block.stone, Character.valueOf('|'), mod_InfiTools.flintRod
                });
        mod_InfiCooking.brStoneFryingPan.setIconCoord(11, 1);
        ModLoader.addName(mod_InfiCooking.brStoneFryingPan, "Baked Stone Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.brStoneFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), mod_InfiTools.brickRod
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.brStoneFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Block.stone, Character.valueOf('|'), mod_InfiTools.brickRod
                });
        mod_InfiCooking.wIronFryingPan.setIconCoord(0, 2);
        ModLoader.addName(mod_InfiCooking.wIronFryingPan, "Iron Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.wIronFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), Item.stick
                });
        mod_InfiCooking.stIronFryingPan.setIconCoord(1, 2);
        ModLoader.addName(mod_InfiCooking.stIronFryingPan, "Stony Iron Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.stIronFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_InfiCooking.iIronFryingPan.setIconCoord(2, 2);
        ModLoader.addName(mod_InfiCooking.iIronFryingPan, "Ironic Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.iIronFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), mod_InfiTools.ironRod
                });
        mod_InfiCooking.dIronFryingPan.setIconCoord(3, 2);
        ModLoader.addName(mod_InfiCooking.dIronFryingPan, "Jeweled Iron Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.dIronFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), mod_InfiTools.diamondRod
                });
        mod_InfiCooking.gIronFryingPan.setIconCoord(4, 2);
        ModLoader.addName(mod_InfiCooking.gIronFryingPan, "Expensive Iron Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.gIronFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), mod_InfiTools.goldRod
                });
        mod_InfiCooking.rIronFryingPan.setIconCoord(5, 2);
        ModLoader.addName(mod_InfiCooking.rIronFryingPan, "Red Iron Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.rIronFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), mod_InfiTools.redstoneRod
                });
        mod_InfiCooking.oIronFryingPan.setIconCoord(6, 2);
        ModLoader.addName(mod_InfiCooking.oIronFryingPan, "Glassy Iron Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.oIronFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), mod_InfiTools.obsidianRod
                });
        mod_InfiCooking.bIronFryingPan.setIconCoord(7, 2);
        ModLoader.addName(mod_InfiCooking.bIronFryingPan, "Necrotic Iron Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.bIronFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.bIronFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_InfiCooking.nIronFryingPan.setIconCoord(8, 2);
        ModLoader.addName(mod_InfiCooking.nIronFryingPan, "Bloody Iron Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.nIronFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_InfiCooking.glIronFryingPan.setIconCoord(9, 2);
        ModLoader.addName(mod_InfiCooking.glIronFryingPan, "Glowing Iron Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.glIronFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_InfiCooking.iceIronFryingPan.setIconCoord(10, 2);
        ModLoader.addName(mod_InfiCooking.iceIronFryingPan, "Icy Iron Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.iceIronFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), mod_InfiTools.iceRod
                });
        mod_InfiCooking.sIronFryingPan.setIconCoord(11, 2);
        ModLoader.addName(mod_InfiCooking.sIronFryingPan, "Slimy Iron Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.sIronFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_InfiCooking.blIronFryingPan.setIconCoord(12, 2);
        ModLoader.addName(mod_InfiCooking.blIronFryingPan, "Blazing Iron Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.blIronFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), Item.blazeRod
                });
        mod_InfiCooking.wDiamondFryingPan.setIconCoord(0, 3);
        ModLoader.addName(mod_InfiCooking.wDiamondFryingPan, "Diamond Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.wDiamondFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), Item.stick
                });
        mod_InfiCooking.stDiamondFryingPan.setIconCoord(1, 3);
        ModLoader.addName(mod_InfiCooking.stDiamondFryingPan, "Stony Diamond Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.stDiamondFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_InfiCooking.iDiamondFryingPan.setIconCoord(2, 3);
        ModLoader.addName(mod_InfiCooking.iDiamondFryingPan, "Hard Diamond Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.iDiamondFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), mod_InfiTools.ironRod
                });
        mod_InfiCooking.dDiamondFryingPan.setIconCoord(3, 3);
        ModLoader.addName(mod_InfiCooking.dDiamondFryingPan, "Diamondium Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.dDiamondFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), mod_InfiTools.diamondRod
                });
        mod_InfiCooking.gDiamondFryingPan.setIconCoord(4, 3);
        ModLoader.addName(mod_InfiCooking.gDiamondFryingPan, "Expensive Diamond Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.gDiamondFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), mod_InfiTools.goldRod
                });
        mod_InfiCooking.rDiamondFryingPan.setIconCoord(5, 3);
        ModLoader.addName(mod_InfiCooking.rDiamondFryingPan, "Red Diamond Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.rDiamondFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), mod_InfiTools.redstoneRod
                });
        mod_InfiCooking.oDiamondFryingPan.setIconCoord(6, 3);
        ModLoader.addName(mod_InfiCooking.oDiamondFryingPan, "Glassy Diamond Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.oDiamondFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), mod_InfiTools.obsidianRod
                });
        mod_InfiCooking.bDiamondFryingPan.setIconCoord(7, 3);
        ModLoader.addName(mod_InfiCooking.bDiamondFryingPan, "Necrotic Diamond Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.bDiamondFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.bDiamondFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_InfiCooking.mDiamondFryingPan.setIconCoord(8, 3);
        ModLoader.addName(mod_InfiCooking.mDiamondFryingPan, "Mossy Diamond Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.mDiamondFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), mod_InfiTools.mossyRod
                });
        mod_InfiCooking.nDiamondFryingPan.setIconCoord(9, 3);
        ModLoader.addName(mod_InfiCooking.nDiamondFryingPan, "Bloody Diamond Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.nDiamondFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_InfiCooking.glDiamondFryingPan.setIconCoord(10, 3);
        ModLoader.addName(mod_InfiCooking.glDiamondFryingPan, "Glowing Diamond Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.glDiamondFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_InfiCooking.blDiamondFryingPan.setIconCoord(11, 3);
        ModLoader.addName(mod_InfiCooking.blDiamondFryingPan, "Blazing Diamond Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.blDiamondFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), Item.blazeRod
                });
        mod_InfiCooking.wGoldFryingPan.setIconCoord(0, 4);
        ModLoader.addName(mod_InfiCooking.wGoldFryingPan, "Stony Gold Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.wGoldFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), Item.stick
                });
        mod_InfiCooking.stGoldFryingPan.setIconCoord(1, 4);
        ModLoader.addName(mod_InfiCooking.stGoldFryingPan, "Stony Gold Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.stGoldFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_InfiCooking.gGoldFryingPan.setIconCoord(2, 4);
        ModLoader.addName(mod_InfiCooking.gGoldFryingPan, "Expensive Useless Cookware");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.gGoldFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), mod_InfiTools.goldRod
                });
        mod_InfiCooking.oGoldFryingPan.setIconCoord(3, 4);
        ModLoader.addName(mod_InfiCooking.oGoldFryingPan, "Glassy Gold Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.oGoldFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), mod_InfiTools.obsidianRod
                });
        mod_InfiCooking.saGoldFryingPan.setIconCoord(4, 4);
        ModLoader.addName(mod_InfiCooking.saGoldFryingPan, "Sandy Gold Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.saGoldFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), mod_InfiTools.sandstoneRod
                });
        mod_InfiCooking.bGoldFryingPan.setIconCoord(5, 4);
        ModLoader.addName(mod_InfiCooking.bGoldFryingPan, "Necrotic Gold Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.bGoldFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.bGoldFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_InfiCooking.mGoldFryingPan.setIconCoord(6, 4);
        ModLoader.addName(mod_InfiCooking.mGoldFryingPan, "Mossy Gold Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.mGoldFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), mod_InfiTools.mossyRod
                });
        mod_InfiCooking.nGoldFryingPan.setIconCoord(7, 4);
        ModLoader.addName(mod_InfiCooking.nGoldFryingPan, "Bloody Gold Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.nGoldFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_InfiCooking.glGoldFryingPan.setIconCoord(8, 4);
        ModLoader.addName(mod_InfiCooking.glGoldFryingPan, "Glowing Gold Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.glGoldFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_InfiCooking.iceGoldFryingPan.setIconCoord(9, 4);
        ModLoader.addName(mod_InfiCooking.iceGoldFryingPan, "Icy Gold Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.iceGoldFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), mod_InfiTools.iceRod
                });
        mod_InfiCooking.sGoldFryingPan.setIconCoord(10, 4);
        ModLoader.addName(mod_InfiCooking.sGoldFryingPan, "Slimy Gold Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.sGoldFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_InfiCooking.fGoldFryingPan.setIconCoord(11, 4);
        ModLoader.addName(mod_InfiCooking.fGoldFryingPan, "Flaky Gold Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.fGoldFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), mod_InfiTools.flintRod
                });
        mod_InfiCooking.wRedstoneFryingPan.setIconCoord(0, 5);
        ModLoader.addName(mod_InfiCooking.wRedstoneFryingPan, "Redstone Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.wRedstoneFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), Item.stick
                });
        mod_InfiCooking.stRedstoneFryingPan.setIconCoord(1, 5);
        ModLoader.addName(mod_InfiCooking.stRedstoneFryingPan, "Stony Redstone Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.stRedstoneFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_InfiCooking.iRedstoneFryingPan.setIconCoord(2, 5);
        ModLoader.addName(mod_InfiCooking.iRedstoneFryingPan, "Hard Redstone Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.iRedstoneFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), mod_InfiTools.ironRod
                });
        mod_InfiCooking.dRedstoneFryingPan.setIconCoord(3, 5);
        ModLoader.addName(mod_InfiCooking.dRedstoneFryingPan, "Jeweled Redstone Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.dRedstoneFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), mod_InfiTools.diamondRod
                });
        mod_InfiCooking.rRedstoneFryingPan.setIconCoord(4, 5);
        ModLoader.addName(mod_InfiCooking.rRedstoneFryingPan, "Redredred Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.rRedstoneFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), mod_InfiTools.redstoneRod
                });
        mod_InfiCooking.oRedstoneFryingPan.setIconCoord(5, 5);
        ModLoader.addName(mod_InfiCooking.oRedstoneFryingPan, "Glassy Redstone Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.oRedstoneFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), mod_InfiTools.obsidianRod
                });
        mod_InfiCooking.bRedstoneFryingPan.setIconCoord(6, 5);
        ModLoader.addName(mod_InfiCooking.bRedstoneFryingPan, "Necrotic Redstone Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.bRedstoneFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.bRedstoneFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_InfiCooking.mRedstoneFryingPan.setIconCoord(7, 5);
        ModLoader.addName(mod_InfiCooking.mRedstoneFryingPan, "Mossy Redstone Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.mRedstoneFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), mod_InfiTools.mossyRod
                });
        mod_InfiCooking.glRedstoneFryingPan.setIconCoord(8, 5);
        ModLoader.addName(mod_InfiCooking.glRedstoneFryingPan, "Glowing Redstone Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.glRedstoneFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_InfiCooking.sRedstoneFryingPan.setIconCoord(9, 5);
        ModLoader.addName(mod_InfiCooking.sRedstoneFryingPan, "Slimy Redstone Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.sRedstoneFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_InfiCooking.blRedstoneFryingPan.setIconCoord(10, 5);
        ModLoader.addName(mod_InfiCooking.blRedstoneFryingPan, "Blazing Redstone Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.blRedstoneFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), Item.blazeRod
                });
        mod_InfiCooking.wObsidianFryingPan.setIconCoord(0, 6);
        ModLoader.addName(mod_InfiCooking.wObsidianFryingPan, "Obsidian Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.wObsidianFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), Item.stick
                });
        mod_InfiCooking.stObsidianFryingPan.setIconCoord(1, 6);
        ModLoader.addName(mod_InfiCooking.stObsidianFryingPan, "Stony Obsidian Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.stObsidianFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_InfiCooking.iObsidianFryingPan.setIconCoord(2, 6);
        ModLoader.addName(mod_InfiCooking.iObsidianFryingPan, "Hard Obsidian Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.iObsidianFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), mod_InfiTools.ironRod
                });
        mod_InfiCooking.dObsidianFryingPan.setIconCoord(3, 6);
        ModLoader.addName(mod_InfiCooking.dObsidianFryingPan, "Jeweled Obsidian Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.dObsidianFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), mod_InfiTools.diamondRod
                });
        mod_InfiCooking.gObsidianFryingPan.setIconCoord(4, 6);
        ModLoader.addName(mod_InfiCooking.gObsidianFryingPan, "Expensive Obsidian Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.gObsidianFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), mod_InfiTools.goldRod
                });
        mod_InfiCooking.rObsidianFryingPan.setIconCoord(5, 6);
        ModLoader.addName(mod_InfiCooking.rObsidianFryingPan, "Red Obsidian Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.rObsidianFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), mod_InfiTools.redstoneRod
                });
        mod_InfiCooking.oObsidianFryingPan.setIconCoord(6, 6);
        ModLoader.addName(mod_InfiCooking.oObsidianFryingPan, "Wicked Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.oObsidianFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), mod_InfiTools.obsidianRod
                });
        mod_InfiCooking.bObsidianFryingPan.setIconCoord(7, 6);
        ModLoader.addName(mod_InfiCooking.bObsidianFryingPan, "Necrotic Obsidian Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.bObsidianFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.bObsidianFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_InfiCooking.nObsidianFryingPan.setIconCoord(8, 6);
        ModLoader.addName(mod_InfiCooking.nObsidianFryingPan, "Bloody Obsidian Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.nObsidianFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_InfiCooking.glObsidianFryingPan.setIconCoord(9, 6);
        ModLoader.addName(mod_InfiCooking.glObsidianFryingPan, "Glowing Obsidian Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.glObsidianFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_InfiCooking.sObsidianFryingPan.setIconCoord(10, 6);
        ModLoader.addName(mod_InfiCooking.sObsidianFryingPan, "Slimy Obsidian Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.sObsidianFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_InfiCooking.fObsidianFryingPan.setIconCoord(11, 6);
        ModLoader.addName(mod_InfiCooking.fObsidianFryingPan, "Flaky Obsidian Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.fObsidianFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), mod_InfiTools.flintRod
                });
        mod_InfiCooking.blObsidianFryingPan.setIconCoord(12, 6);
        ModLoader.addName(mod_InfiCooking.blObsidianFryingPan, "Blazing Obsidian Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.blObsidianFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), Item.blazeRod
                });
        mod_InfiCooking.wSandstoneFryingPan.setIconCoord(0, 7);
        ModLoader.addName(mod_InfiCooking.wSandstoneFryingPan, "Sandstone Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.wSandstoneFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), Item.stick
                });
        mod_InfiCooking.stSandstoneFryingPan.setIconCoord(1, 7);
        ModLoader.addName(mod_InfiCooking.stSandstoneFryingPan, "Stony Sandstone Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.stSandstoneFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_InfiCooking.saSandstoneFryingPan.setIconCoord(2, 7);
        ModLoader.addName(mod_InfiCooking.saSandstoneFryingPan, "Sandtongue");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.saSandstoneFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), mod_InfiTools.sandstoneRod
                });
        mod_InfiCooking.bSandstoneFryingPan.setIconCoord(3, 7);
        ModLoader.addName(mod_InfiCooking.bSandstoneFryingPan, "Necrotic Sandstone Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.bSandstoneFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.bSandstoneFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_InfiCooking.pSandstoneFryingPan.setIconCoord(4, 7);
        ModLoader.addName(mod_InfiCooking.pSandstoneFryingPan, "Fibery Sandstone Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.pSandstoneFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), mod_InfiTools.paperRod
                });
        mod_InfiCooking.nSandstoneFryingPan.setIconCoord(5, 7);
        ModLoader.addName(mod_InfiCooking.nSandstoneFryingPan, "Bloody Sandstone Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.nSandstoneFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_InfiCooking.iceSandstoneFryingPan.setIconCoord(6, 7);
        ModLoader.addName(mod_InfiCooking.iceSandstoneFryingPan, "Icy Sandstone Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.iceSandstoneFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), mod_InfiTools.iceRod
                });
        mod_InfiCooking.sSandstoneFryingPan.setIconCoord(7, 7);
        ModLoader.addName(mod_InfiCooking.sSandstoneFryingPan, "Slimy Sandstone Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.sSandstoneFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_InfiCooking.cSandstoneFryingPan.setIconCoord(8, 7);
        ModLoader.addName(mod_InfiCooking.cSandstoneFryingPan, "Spiny Sandstone Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.cSandstoneFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_InfiCooking.fSandstoneFryingPan.setIconCoord(9, 7);
        ModLoader.addName(mod_InfiCooking.fSandstoneFryingPan, "Flaky Sandstone Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.fSandstoneFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), mod_InfiTools.flintRod
                });
        mod_InfiCooking.brSandstoneFryingPan.setIconCoord(10, 7);
        ModLoader.addName(mod_InfiCooking.brSandstoneFryingPan, "Baked Sandstone Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.brSandstoneFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), mod_InfiTools.brickRod
                });
        mod_InfiCooking.wBoneFryingPan.setIconCoord(0, 8);
        ModLoader.addName(mod_InfiCooking.wBoneFryingPan, "Bone Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.wBoneFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Item.bone, Character.valueOf('|'), Item.stick
                });
        mod_InfiCooking.stBoneFryingPan.setIconCoord(1, 8);
        ModLoader.addName(mod_InfiCooking.stBoneFryingPan, "Stony Bone Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.stBoneFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_InfiCooking.iBoneFryingPan.setIconCoord(2, 8);
        ModLoader.addName(mod_InfiCooking.iBoneFryingPan, "Hard Bone Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.iBoneFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.ironRod
                });
        mod_InfiCooking.dBoneFryingPan.setIconCoord(3, 8);
        ModLoader.addName(mod_InfiCooking.dBoneFryingPan, "Jeweled Bone Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.dBoneFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.diamondRod
                });
        mod_InfiCooking.rBoneFryingPan.setIconCoord(4, 8);
        ModLoader.addName(mod_InfiCooking.rBoneFryingPan, "Red Bone Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.rBoneFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.redstoneRod
                });
        mod_InfiCooking.oBoneFryingPan.setIconCoord(5, 8);
        ModLoader.addName(mod_InfiCooking.oBoneFryingPan, "Glassy Bone Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.oBoneFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.obsidianRod
                });
        mod_InfiCooking.bBoneFryingPan.setIconCoord(6, 8);
        ModLoader.addName(mod_InfiCooking.bBoneFryingPan, "Reanimated Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.bBoneFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Item.bone, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.bBoneFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_InfiCooking.mBoneFryingPan.setIconCoord(7, 8);
        ModLoader.addName(mod_InfiCooking.mBoneFryingPan, "Mossy Bone Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.mBoneFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.mossyRod
                });
        mod_InfiCooking.nBoneFryingPan.setIconCoord(8, 8);
        ModLoader.addName(mod_InfiCooking.nBoneFryingPan, "Netherrack Bone Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.nBoneFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_InfiCooking.glBoneFryingPan.setIconCoord(9, 8);
        ModLoader.addName(mod_InfiCooking.glBoneFryingPan, "Glowstone Bone Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.glBoneFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_InfiCooking.sBoneFryingPan.setIconCoord(10, 8);
        ModLoader.addName(mod_InfiCooking.sBoneFryingPan, "Slimy Bone Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.sBoneFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_InfiCooking.cBoneFryingPan.setIconCoord(11, 8);
        ModLoader.addName(mod_InfiCooking.cBoneFryingPan, "Spiny Bone Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.cBoneFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_InfiCooking.fBoneFryingPan.setIconCoord(12, 8);
        ModLoader.addName(mod_InfiCooking.fBoneFryingPan, "Flaky Bone Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.fBoneFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.flintRod
                });
        mod_InfiCooking.brBoneFryingPan.setIconCoord(13, 8);
        ModLoader.addName(mod_InfiCooking.brBoneFryingPan, "Baked Bone Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.brBoneFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.brickRod
                });
        mod_InfiCooking.blBoneFryingPan.setIconCoord(14, 8);
        ModLoader.addName(mod_InfiCooking.blBoneFryingPan, "Blazing Bone Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.blBoneFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Item.bone, Character.valueOf('|'), Item.blazeRod
                });
        mod_InfiCooking.wPaperFryingPan.setIconCoord(0, 9);
        ModLoader.addName(mod_InfiCooking.wPaperFryingPan, "Paper Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.wPaperFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), mod_InfiTools.paperStack, Character.valueOf('|'), Item.stick
                });
        mod_InfiCooking.saPaperFryingPan.setIconCoord(1, 9);
        ModLoader.addName(mod_InfiCooking.saPaperFryingPan, "Stony Paper Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.saPaperFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), mod_InfiTools.paperStack, Character.valueOf('|'), mod_InfiTools.sandstoneRod
                });
        mod_InfiCooking.bPaperFryingPan.setIconCoord(2, 9);
        ModLoader.addName(mod_InfiCooking.bPaperFryingPan, "Necrotic Paper Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.bPaperFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), mod_InfiTools.paperStack, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.bPaperFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), mod_InfiTools.paperStack, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_InfiCooking.pPaperFryingPan.setIconCoord(3, 9);
        ModLoader.addName(mod_InfiCooking.pPaperFryingPan, "Fibrious Whack-a-Creeper");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.pPaperFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), mod_InfiTools.paperStack, Character.valueOf('|'), mod_InfiTools.paperRod
                });
        mod_InfiCooking.sPaperFryingPan.setIconCoord(4, 9);
        ModLoader.addName(mod_InfiCooking.sPaperFryingPan, "Slimy Paper Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.sPaperFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), mod_InfiTools.paperStack, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_InfiCooking.cPaperFryingPan.setIconCoord(5, 9);
        ModLoader.addName(mod_InfiCooking.cPaperFryingPan, "Spiny Paper Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.cPaperFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), mod_InfiTools.paperStack, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_InfiCooking.brPaperFryingPan.setIconCoord(6, 9);
        ModLoader.addName(mod_InfiCooking.brPaperFryingPan, "Baked Paper Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.brPaperFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), mod_InfiTools.paperStack, Character.valueOf('|'), mod_InfiTools.brickRod
                });
        mod_InfiCooking.stMossyFryingPan.setIconCoord(0, 10);
        ModLoader.addName(mod_InfiCooking.stMossyFryingPan, "Stony Moss-Covered Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.stMossyFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), mod_InfiTools.mossBallCrafted, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_InfiCooking.dMossyFryingPan.setIconCoord(1, 10);
        ModLoader.addName(mod_InfiCooking.dMossyFryingPan, "Jeweled Moss-Covered Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.dMossyFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), mod_InfiTools.mossBallCrafted, Character.valueOf('|'), mod_InfiTools.diamondRod
                });
        mod_InfiCooking.rMossyFryingPan.setIconCoord(2, 10);
        ModLoader.addName(mod_InfiCooking.rMossyFryingPan, "Red Moss-Covered Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.rMossyFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), mod_InfiTools.mossBallCrafted, Character.valueOf('|'), mod_InfiTools.redstoneRod
                });
        mod_InfiCooking.bMossyFryingPan.setIconCoord(3, 10);
        ModLoader.addName(mod_InfiCooking.bMossyFryingPan, "Necrotic Moss-Covered Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.bMossyFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), mod_InfiTools.mossBallCrafted, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.bMossyFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), mod_InfiTools.mossBallCrafted, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_InfiCooking.mMossyFryingPan.setIconCoord(4, 10);
        ModLoader.addName(mod_InfiCooking.mMossyFryingPan, "Living Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.mMossyFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), mod_InfiTools.mossBallCrafted, Character.valueOf('|'), mod_InfiTools.mossyRod
                });
        mod_InfiCooking.glMossyFryingPan.setIconCoord(5, 10);
        ModLoader.addName(mod_InfiCooking.glMossyFryingPan, "Glowing Moss-Covered Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.glMossyFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), mod_InfiTools.mossBallCrafted, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_InfiCooking.wNetherrackFryingPan.setIconCoord(0, 11);
        ModLoader.addName(mod_InfiCooking.wNetherrackFryingPan, "Netherrack Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.wNetherrackFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), Item.stick
                });
        mod_InfiCooking.stNetherrackFryingPan.setIconCoord(1, 11);
        ModLoader.addName(mod_InfiCooking.stNetherrackFryingPan, "Stony Netherrack Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.stNetherrackFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_InfiCooking.iNetherrackFryingPan.setIconCoord(2, 11);
        ModLoader.addName(mod_InfiCooking.iNetherrackFryingPan, "Hard Netherrack Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.iNetherrackFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.ironRod
                });
        mod_InfiCooking.rNetherrackFryingPan.setIconCoord(3, 11);
        ModLoader.addName(mod_InfiCooking.rNetherrackFryingPan, "Red Netherrack Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.rNetherrackFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.redstoneRod
                });
        mod_InfiCooking.oNetherrackFryingPan.setIconCoord(4, 11);
        ModLoader.addName(mod_InfiCooking.oNetherrackFryingPan, "Glassy Netherrack Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.oNetherrackFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.obsidianRod
                });
        mod_InfiCooking.saNetherrackFryingPan.setIconCoord(5, 11);
        ModLoader.addName(mod_InfiCooking.saNetherrackFryingPan, "Sandy Netherrack Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.saNetherrackFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.sandstoneRod
                });
        mod_InfiCooking.bNetherrackFryingPan.setIconCoord(6, 11);
        ModLoader.addName(mod_InfiCooking.bNetherrackFryingPan, "Necrotic Netherrack Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.bNetherrackFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.bNetherrackFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_InfiCooking.mNetherrackFryingPan.setIconCoord(7, 11);
        ModLoader.addName(mod_InfiCooking.mNetherrackFryingPan, "Mossy Netherrack Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.mNetherrackFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.mossyRod
                });
        mod_InfiCooking.nNetherrackFryingPan.setIconCoord(8, 11);
        ModLoader.addName(mod_InfiCooking.nNetherrackFryingPan, "Blood Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.nNetherrackFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_InfiCooking.glNetherrackFryingPan.setIconCoord(9, 11);
        ModLoader.addName(mod_InfiCooking.glNetherrackFryingPan, "Glowing Netherrack Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.glNetherrackFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_InfiCooking.iceNetherrackFryingPan.setIconCoord(10, 11);
        ModLoader.addName(mod_InfiCooking.iceNetherrackFryingPan, "Icy Netherrack Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.iceNetherrackFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.iceRod
                });
        mod_InfiCooking.sNetherrackFryingPan.setIconCoord(11, 11);
        ModLoader.addName(mod_InfiCooking.sNetherrackFryingPan, "Slimy Netherrack Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.sNetherrackFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_InfiCooking.cNetherrackFryingPan.setIconCoord(12, 11);
        ModLoader.addName(mod_InfiCooking.cNetherrackFryingPan, "Spiny Netherrack Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.cNetherrackFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_InfiCooking.fNetherrackFryingPan.setIconCoord(13, 11);
        ModLoader.addName(mod_InfiCooking.fNetherrackFryingPan, "Flaky Netherrack Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.fNetherrackFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_InfiCooking.brNetherrackFryingPan.setIconCoord(14, 11);
        ModLoader.addName(mod_InfiCooking.brNetherrackFryingPan, "Baked Netherrack Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.brNetherrackFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_InfiCooking.blNetherrackFryingPan.setIconCoord(15, 11);
        ModLoader.addName(mod_InfiCooking.blNetherrackFryingPan, "Blazing Netherrack Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.blNetherrackFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_InfiCooking.wGlowstoneFryingPan.setIconCoord(0, 12);
        ModLoader.addName(mod_InfiCooking.wGlowstoneFryingPan, "Glowstone Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.wGlowstoneFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), Item.stick
                });
        mod_InfiCooking.stGlowstoneFryingPan.setIconCoord(1, 12);
        ModLoader.addName(mod_InfiCooking.stGlowstoneFryingPan, "Stony Glowstone Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.stGlowstoneFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_InfiCooking.iGlowstoneFryingPan.setIconCoord(2, 12);
        ModLoader.addName(mod_InfiCooking.iGlowstoneFryingPan, "Hard Glowstone Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.iGlowstoneFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.ironRod
                });
        mod_InfiCooking.dGlowstoneFryingPan.setIconCoord(3, 12);
        ModLoader.addName(mod_InfiCooking.dGlowstoneFryingPan, "Jeweled Glowstone Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.dGlowstoneFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.diamondRod
                });
        mod_InfiCooking.rGlowstoneFryingPan.setIconCoord(4, 12);
        ModLoader.addName(mod_InfiCooking.rGlowstoneFryingPan, "Red Glowstone Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.rGlowstoneFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.redstoneRod
                });
        mod_InfiCooking.oGlowstoneFryingPan.setIconCoord(5, 12);
        ModLoader.addName(mod_InfiCooking.oGlowstoneFryingPan, "Glassy Glowstone Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.oGlowstoneFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.obsidianRod
                });
        mod_InfiCooking.bGlowstoneFryingPan.setIconCoord(6, 12);
        ModLoader.addName(mod_InfiCooking.bGlowstoneFryingPan, "Necrotic Glowstone Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.bGlowstoneFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.bGlowstoneFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_InfiCooking.mGlowstoneFryingPan.setIconCoord(7, 12);
        ModLoader.addName(mod_InfiCooking.mGlowstoneFryingPan, "Mossy Glowstone Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.mGlowstoneFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.mossyRod
                });
        mod_InfiCooking.nGlowstoneFryingPan.setIconCoord(8, 12);
        ModLoader.addName(mod_InfiCooking.nGlowstoneFryingPan, "Bloody Glowstone Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.nGlowstoneFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_InfiCooking.glGlowstoneFryingPan.setIconCoord(9, 12);
        ModLoader.addName(mod_InfiCooking.glGlowstoneFryingPan, "Bright Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.glGlowstoneFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_InfiCooking.iceGlowstoneFryingPan.setIconCoord(10, 12);
        ModLoader.addName(mod_InfiCooking.iceGlowstoneFryingPan, "Icy Glowstone Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.iceGlowstoneFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.iceRod
                });
        mod_InfiCooking.lGlowstoneFryingPan.setIconCoord(11, 12);
        ModLoader.addName(mod_InfiCooking.lGlowstoneFryingPan, "Fiery Glowstone Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.lGlowstoneFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_InfiCooking.sGlowstoneFryingPan.setIconCoord(12, 12);
        ModLoader.addName(mod_InfiCooking.sGlowstoneFryingPan, "Slimy Glowstone Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.sGlowstoneFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_InfiCooking.blGlowstoneFryingPan.setIconCoord(13, 12);
        ModLoader.addName(mod_InfiCooking.blGlowstoneFryingPan, "Blazing Glowstone Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.blGlowstoneFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), Item.blazeRod
                });
        mod_InfiCooking.wIceFryingPan.setIconCoord(0, 13);
        ModLoader.addName(mod_InfiCooking.wIceFryingPan, "Ice Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.wIceFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Block.ice, Character.valueOf('|'), Item.stick
                });
        mod_InfiCooking.stIceFryingPan.setIconCoord(1, 13);
        ModLoader.addName(mod_InfiCooking.stIceFryingPan, "Stony Ice Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.stIceFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_InfiCooking.iIceFryingPan.setIconCoord(2, 13);
        ModLoader.addName(mod_InfiCooking.iIceFryingPan, "Hard Ice Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.iIceFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.ironRod
                });
        mod_InfiCooking.dIceFryingPan.setIconCoord(3, 13);
        ModLoader.addName(mod_InfiCooking.dIceFryingPan, "Jeweled Ice Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.dIceFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.diamondRod
                });
        mod_InfiCooking.gIceFryingPan.setIconCoord(4, 13);
        ModLoader.addName(mod_InfiCooking.gIceFryingPan, "Expensive Ice Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.gIceFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.goldRod
                });
        mod_InfiCooking.rIceFryingPan.setIconCoord(5, 13);
        ModLoader.addName(mod_InfiCooking.rIceFryingPan, "Red Ice Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.rIceFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.redstoneRod
                });
        mod_InfiCooking.oIceFryingPan.setIconCoord(6, 13);
        ModLoader.addName(mod_InfiCooking.oIceFryingPan, "Glassy Ice Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.oIceFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.obsidianRod
                });
        mod_InfiCooking.saIceFryingPan.setIconCoord(7, 13);
        ModLoader.addName(mod_InfiCooking.saIceFryingPan, "Sandy Ice Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.saIceFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.sandstoneRod
                });
        mod_InfiCooking.bIceFryingPan.setIconCoord(8, 13);
        ModLoader.addName(mod_InfiCooking.bIceFryingPan, "Necrotic Ice Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.bIceFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Block.ice, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.bIceFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_InfiCooking.glIceFryingPan.setIconCoord(9, 13);
        ModLoader.addName(mod_InfiCooking.glIceFryingPan, "Glowing Ice Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.glIceFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_InfiCooking.iceIceFryingPan.setIconCoord(10, 13);
        ModLoader.addName(mod_InfiCooking.iceIceFryingPan, "Freezing Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.iceIceFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.iceRod
                });
        mod_InfiCooking.sIceFryingPan.setIconCoord(11, 13);
        ModLoader.addName(mod_InfiCooking.sIceFryingPan, "Slimy Ice Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.sIceFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_InfiCooking.cIceFryingPan.setIconCoord(12, 13);
        ModLoader.addName(mod_InfiCooking.cIceFryingPan, "Spiny Ice Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.cIceFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_InfiCooking.fIceFryingPan.setIconCoord(13, 13);
        ModLoader.addName(mod_InfiCooking.fIceFryingPan, "Flaky Ice Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.fIceFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.flintRod
                });
        mod_InfiCooking.brIceFryingPan.setIconCoord(14, 13);
        ModLoader.addName(mod_InfiCooking.brIceFryingPan, "Baked Ice Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.brIceFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.brickRod
                });
        mod_InfiCooking.dLavaFryingPan.setIconCoord(0, 14);
        ModLoader.addName(mod_InfiCooking.dLavaFryingPan, "Jeweled Lava Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.dLavaFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), mod_InfiTools.lavaCrystal, Character.valueOf('|'), mod_InfiTools.diamondRod
                });
        mod_InfiCooking.rLavaFryingPan.setIconCoord(1, 14);
        ModLoader.addName(mod_InfiCooking.rLavaFryingPan, "Red Lava Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.rLavaFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), mod_InfiTools.lavaCrystal, Character.valueOf('|'), mod_InfiTools.redstoneRod
                });
        mod_InfiCooking.bLavaFryingPan.setIconCoord(2, 14);
        ModLoader.addName(mod_InfiCooking.bLavaFryingPan, "Necrotic Lava Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.bLavaFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), mod_InfiTools.lavaCrystal, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.bLavaFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), mod_InfiTools.lavaCrystal, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_InfiCooking.nLavaFryingPan.setIconCoord(3, 14);
        ModLoader.addName(mod_InfiCooking.nLavaFryingPan, "Bloody Lava Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.nLavaFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), mod_InfiTools.lavaCrystal, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_InfiCooking.glLavaFryingPan.setIconCoord(4, 14);
        ModLoader.addName(mod_InfiCooking.glLavaFryingPan, "Glowing Lava Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.glLavaFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), mod_InfiTools.lavaCrystal, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_InfiCooking.lLavaFryingPan.setIconCoord(5, 14);
        ModLoader.addName(mod_InfiCooking.lLavaFryingPan, "Grub Melter");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.lLavaFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), mod_InfiTools.lavaCrystal, Character.valueOf('|'), mod_InfiTools.lavaRod
                });
        mod_InfiCooking.blLavaFryingPan.setIconCoord(6, 14);
        ModLoader.addName(mod_InfiCooking.blLavaFryingPan, "Blazing Lava Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.blLavaFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), mod_InfiTools.lavaCrystal, Character.valueOf('|'), Item.blazeRod
                });
        mod_InfiCooking.wSlimeFryingPan.setIconCoord(0, 15);
        ModLoader.addName(mod_InfiCooking.wSlimeFryingPan, "Slime Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.wSlimeFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), Item.stick
                });
        mod_InfiCooking.stSlimeFryingPan.setIconCoord(1, 15);
        ModLoader.addName(mod_InfiCooking.stSlimeFryingPan, "Stony Slime Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.stSlimeFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_InfiCooking.iSlimeFryingPan.setIconCoord(2, 15);
        ModLoader.addName(mod_InfiCooking.iSlimeFryingPan, "Hard Slime Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.iSlimeFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.ironRod
                });
        mod_InfiCooking.dSlimeFryingPan.setIconCoord(3, 15);
        ModLoader.addName(mod_InfiCooking.dSlimeFryingPan, "Jeweled Slime Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.dSlimeFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.diamondRod
                });
        mod_InfiCooking.gSlimeFryingPan.setIconCoord(4, 15);
        ModLoader.addName(mod_InfiCooking.gSlimeFryingPan, "Expensive Slime Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.gSlimeFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.goldRod
                });
        mod_InfiCooking.rSlimeFryingPan.setIconCoord(5, 15);
        ModLoader.addName(mod_InfiCooking.rSlimeFryingPan, "Red Slime Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.rSlimeFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.redstoneRod
                });
        mod_InfiCooking.oSlimeFryingPan.setIconCoord(6, 15);
        ModLoader.addName(mod_InfiCooking.oSlimeFryingPan, "Glassy Slime Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.oSlimeFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.obsidianRod
                });
        mod_InfiCooking.saSlimeFryingPan.setIconCoord(7, 15);
        ModLoader.addName(mod_InfiCooking.saSlimeFryingPan, "Sandy Slime Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.saSlimeFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.sandstoneRod
                });
        mod_InfiCooking.bSlimeFryingPan.setIconCoord(8, 15);
        ModLoader.addName(mod_InfiCooking.bSlimeFryingPan, "Necrotic Slime Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.bSlimeFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.bSlimeFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_InfiCooking.pSlimeFryingPan.setIconCoord(9, 15);
        ModLoader.addName(mod_InfiCooking.pSlimeFryingPan, "Fibery Slime Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.pSlimeFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.paperRod
                });
        mod_InfiCooking.mSlimeFryingPan.setIconCoord(10, 15);
        ModLoader.addName(mod_InfiCooking.mSlimeFryingPan, "Mossy Slime Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.mSlimeFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.mossyRod
                });
        mod_InfiCooking.nSlimeFryingPan.setIconCoord(11, 15);
        ModLoader.addName(mod_InfiCooking.nSlimeFryingPan, "Bloody Slime Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.nSlimeFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_InfiCooking.glSlimeFryingPan.setIconCoord(12, 15);
        ModLoader.addName(mod_InfiCooking.glSlimeFryingPan, "Glowing Slime Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.glSlimeFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_InfiCooking.iceSlimeFryingPan.setIconCoord(13, 15);
        ModLoader.addName(mod_InfiCooking.iceSlimeFryingPan, "Icy Slime Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.iceSlimeFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.iceRod
                });
        mod_InfiCooking.lSlimeFryingPan.setIconCoord(14, 15);
        ModLoader.addName(mod_InfiCooking.lSlimeFryingPan, "Fiery Slime Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.lSlimeFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.lavaRod
                });
        mod_InfiCooking.sSlimeFryingPan.setIconCoord(15, 15);
        ModLoader.addName(mod_InfiCooking.sSlimeFryingPan, "Blunt Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.sSlimeFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_InfiCooking.cSlimeFryingPan.setIconCoord(12, 14);
        ModLoader.addName(mod_InfiCooking.cSlimeFryingPan, "Spiny Slime Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.cSlimeFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_InfiCooking.fSlimeFryingPan.setIconCoord(13, 14);
        ModLoader.addName(mod_InfiCooking.fSlimeFryingPan, "Flaky Slime Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.fSlimeFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.flintRod
                });
        mod_InfiCooking.brSlimeFryingPan.setIconCoord(14, 14);
        ModLoader.addName(mod_InfiCooking.brSlimeFryingPan, "Baked Slime Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.brSlimeFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.brickRod
                });
        mod_InfiCooking.blSlimeFryingPan.setIconCoord(15, 14);
        ModLoader.addName(mod_InfiCooking.blSlimeFryingPan, "Blazing Slime Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.blSlimeFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), Item.blazeRod
                });
        mod_InfiCooking.wCactusFryingPan.setIconCoord(10, 0);
        ModLoader.addName(mod_InfiCooking.wCactusFryingPan, "Cactus Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.wCactusFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Block.cactus, Character.valueOf('|'), Item.stick
                });
        mod_InfiCooking.stCactusFryingPan.setIconCoord(11, 0);
        ModLoader.addName(mod_InfiCooking.stCactusFryingPan, "Stony Cactus Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.stCactusFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Block.cactus, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_InfiCooking.saCactusFryingPan.setIconCoord(12, 0);
        ModLoader.addName(mod_InfiCooking.saCactusFryingPan, "Sandy Cactus Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.saCactusFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Block.cactus, Character.valueOf('|'), mod_InfiTools.sandstoneRod
                });
        mod_InfiCooking.bCactusFryingPan.setIconCoord(13, 0);
        ModLoader.addName(mod_InfiCooking.bCactusFryingPan, "Necrotic Cactus Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.bCactusFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Block.cactus, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.bCactusFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Block.cactus, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_InfiCooking.pCactusFryingPan.setIconCoord(14, 0);
        ModLoader.addName(mod_InfiCooking.pCactusFryingPan, "Fibery Cactus Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.pCactusFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Block.cactus, Character.valueOf('|'), mod_InfiTools.paperRod
                });
        mod_InfiCooking.nCactusFryingPan.setIconCoord(15, 0);
        ModLoader.addName(mod_InfiCooking.nCactusFryingPan, "Bloody Cactus Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.nCactusFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Block.cactus, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_InfiCooking.sCactusFryingPan.setIconCoord(12, 1);
        ModLoader.addName(mod_InfiCooking.sCactusFryingPan, "Slimy Cactus Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.sCactusFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Block.cactus, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_InfiCooking.cCactusFryingPan.setIconCoord(13, 1);
        ModLoader.addName(mod_InfiCooking.cCactusFryingPan, "Spined Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.cCactusFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Block.cactus, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_InfiCooking.fCactusFryingPan.setIconCoord(14, 1);
        ModLoader.addName(mod_InfiCooking.fCactusFryingPan, "Flaky Cactus Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.fCactusFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Block.cactus, Character.valueOf('|'), mod_InfiTools.flintRod
                });
        mod_InfiCooking.brCactusFryingPan.setIconCoord(15, 1);
        ModLoader.addName(mod_InfiCooking.brCactusFryingPan, "Baked Cactus Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.brCactusFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Block.cactus, Character.valueOf('|'), mod_InfiTools.brickRod
                });
        mod_InfiCooking.wFlintFryingPan.setIconCoord(13, 2);
        ModLoader.addName(mod_InfiCooking.wFlintFryingPan, "Flint Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.wFlintFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Item.flint, Character.valueOf('|'), Item.stick
                });
        mod_InfiCooking.stFlintFryingPan.setIconCoord(14, 2);
        ModLoader.addName(mod_InfiCooking.stFlintFryingPan, "Stony Flint Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.stFlintFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_InfiCooking.iFlintFryingPan.setIconCoord(15, 2);
        ModLoader.addName(mod_InfiCooking.iFlintFryingPan, "Hard Flint Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.iFlintFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.ironRod
                });
        mod_InfiCooking.gFlintFryingPan.setIconCoord(12, 3);
        ModLoader.addName(mod_InfiCooking.gFlintFryingPan, "Expensive Flint Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.gFlintFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.goldRod
                });
        mod_InfiCooking.oFlintFryingPan.setIconCoord(13, 3);
        ModLoader.addName(mod_InfiCooking.oFlintFryingPan, "Glassy Flint Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.oFlintFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.obsidianRod
                });
        mod_InfiCooking.saFlintFryingPan.setIconCoord(14, 3);
        ModLoader.addName(mod_InfiCooking.saFlintFryingPan, "Sandy Flint Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.saFlintFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.sandstoneRod
                });
        mod_InfiCooking.bFlintFryingPan.setIconCoord(15, 3);
        ModLoader.addName(mod_InfiCooking.bFlintFryingPan, "Necrotic Flint Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.bFlintFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Item.flint, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.bFlintFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_InfiCooking.nFlintFryingPan.setIconCoord(12, 4);
        ModLoader.addName(mod_InfiCooking.nFlintFryingPan, "Bloody Flint Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.nFlintFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_InfiCooking.iceFlintFryingPan.setIconCoord(13, 4);
        ModLoader.addName(mod_InfiCooking.iceFlintFryingPan, "Icy Flint Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.iceFlintFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.iceRod
                });
        mod_InfiCooking.sFlintFryingPan.setIconCoord(14, 4);
        ModLoader.addName(mod_InfiCooking.sFlintFryingPan, "Slimy Flint Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.sFlintFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_InfiCooking.cFlintFryingPan.setIconCoord(15, 4);
        ModLoader.addName(mod_InfiCooking.cFlintFryingPan, "Spiny Flint Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.cFlintFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_InfiCooking.fFlintFryingPan.setIconCoord(11, 5);
        ModLoader.addName(mod_InfiCooking.fFlintFryingPan, "Shale Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.fFlintFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.flintRod
                });
        mod_InfiCooking.brFlintFryingPan.setIconCoord(12, 5);
        ModLoader.addName(mod_InfiCooking.brFlintFryingPan, "Baked Flint Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.brFlintFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.brickRod
                });
        mod_InfiCooking.blFlintFryingPan.setIconCoord(13, 5);
        ModLoader.addName(mod_InfiCooking.blFlintFryingPan, "Blazing Flint Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.blFlintFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Item.flint, Character.valueOf('|'), Item.blazeRod
                });
        mod_InfiCooking.wBrickFryingPan.setIconCoord(14, 5);
        ModLoader.addName(mod_InfiCooking.wBrickFryingPan, "Brick Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.wBrickFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Item.brick, Character.valueOf('|'), Item.stick
                });
        mod_InfiCooking.stBrickFryingPan.setIconCoord(15, 5);
        ModLoader.addName(mod_InfiCooking.stBrickFryingPan, "Stony Brick Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.stBrickFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Item.brick, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_InfiCooking.saBrickFryingPan.setIconCoord(13, 6);
        ModLoader.addName(mod_InfiCooking.saBrickFryingPan, "Sandstone Brick Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.saBrickFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Item.brick, Character.valueOf('|'), mod_InfiTools.sandstoneRod
                });
        mod_InfiCooking.bBrickFryingPan.setIconCoord(14, 6);
        ModLoader.addName(mod_InfiCooking.bBrickFryingPan, "Necrotic Brick Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.bBrickFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Item.brick, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.bBrickFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Item.brick, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_InfiCooking.pBrickFryingPan.setIconCoord(15, 6);
        ModLoader.addName(mod_InfiCooking.pBrickFryingPan, "Fibery Brick Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.pBrickFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Item.brick, Character.valueOf('|'), mod_InfiTools.paperRod
                });
        mod_InfiCooking.nBrickFryingPan.setIconCoord(11, 7);
        ModLoader.addName(mod_InfiCooking.nBrickFryingPan, "Bloody Brick Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.nBrickFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Item.brick, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_InfiCooking.iceBrickFryingPan.setIconCoord(12, 7);
        ModLoader.addName(mod_InfiCooking.iceBrickFryingPan, "Icy Brick Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.iceBrickFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Item.brick, Character.valueOf('|'), mod_InfiTools.iceRod
                });
        mod_InfiCooking.sBrickFryingPan.setIconCoord(13, 7);
        ModLoader.addName(mod_InfiCooking.sBrickFryingPan, "Slimy Brick Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.sBrickFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Item.brick, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_InfiCooking.cBrickFryingPan.setIconCoord(14, 7);
        ModLoader.addName(mod_InfiCooking.cBrickFryingPan, "Spiny Brick Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.cBrickFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Item.brick, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_InfiCooking.fBrickFryingPan.setIconCoord(15, 7);
        ModLoader.addName(mod_InfiCooking.fBrickFryingPan, "Flaky Brick Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.fBrickFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Item.brick, Character.valueOf('|'), mod_InfiTools.flintRod
                });
        mod_InfiCooking.brBrickFryingPan.setIconCoord(15, 8);
        ModLoader.addName(mod_InfiCooking.brBrickFryingPan, "Look-alike Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.brBrickFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), Item.brick, Character.valueOf('|'), mod_InfiTools.brickRod
                });
        mod_InfiCooking.dBlazeFryingPan.setIconCoord(7, 9);
        ModLoader.addName(mod_InfiCooking.dBlazeFryingPan, "Jeweled Blaze Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.dBlazeFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), mod_InfiTools.blazeCrystal, Character.valueOf('|'), mod_InfiTools.diamondRod
                });
        mod_InfiCooking.rBlazeFryingPan.setIconCoord(8, 9);
        ModLoader.addName(mod_InfiCooking.rBlazeFryingPan, "Red Blaze Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.rBlazeFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), mod_InfiTools.blazeCrystal, Character.valueOf('|'), mod_InfiTools.redstoneRod
                });
        mod_InfiCooking.bBlazeFryingPan.setIconCoord(9, 9);
        ModLoader.addName(mod_InfiCooking.bBlazeFryingPan, "Necrotic Blaze Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.bBlazeFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), mod_InfiTools.blazeCrystal, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.bBlazeFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), mod_InfiTools.blazeCrystal, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_InfiCooking.nBlazeFryingPan.setIconCoord(10, 9);
        ModLoader.addName(mod_InfiCooking.nBlazeFryingPan, "Bloody Blaze Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.nBlazeFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), mod_InfiTools.blazeCrystal, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_InfiCooking.glBlazeFryingPan.setIconCoord(11, 9);
        ModLoader.addName(mod_InfiCooking.glBlazeFryingPan, "Glowing Blaze Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.glBlazeFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), mod_InfiTools.blazeCrystal, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_InfiCooking.lBlazeFryingPan.setIconCoord(12, 9);
        ModLoader.addName(mod_InfiCooking.lBlazeFryingPan, "Fiery Blaze Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.lBlazeFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), mod_InfiTools.blazeCrystal, Character.valueOf('|'), mod_InfiTools.lavaRod
                });
        mod_InfiCooking.fBlazeFryingPan.setIconCoord(13, 9);
        ModLoader.addName(mod_InfiCooking.fBlazeFryingPan, "Flaky Blaze Frying Pan");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.fBlazeFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), mod_InfiTools.blazeCrystal, Character.valueOf('|'), mod_InfiTools.flintRod
                });
        mod_InfiCooking.blBlazeFryingPan.setIconCoord(14, 9);
        ModLoader.addName(mod_InfiCooking.blBlazeFryingPan, "Nether Splint");
        ModLoader.addRecipe(new ItemStack(mod_InfiCooking.blBlazeFryingPan, 1), new Object[]
                {
                    " m ", "m m", "|m ", Character.valueOf('m'), mod_InfiTools.blazeCrystal, Character.valueOf('|'), Item.blazeRod
                });
        return 0;
    }
}
