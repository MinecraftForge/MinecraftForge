package net.minecraft.src.hybrids;

import net.minecraft.src.*;

public class InfiRecipeMattocks
{
    public InfiRecipeMattocks()
    {
    }

    public static int recipeStorm()
    {
        mod_InfiHybrids.wWoodMattock.setIconCoord(0, 0);
        ModLoader.addName(mod_InfiHybrids.wWoodMattock, "Wooden Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.wWoodMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Block.planks, Character.valueOf('|'), Item.stick
                });
        mod_InfiHybrids.stWoodMattock.setIconCoord(1, 0);
        ModLoader.addName(mod_InfiHybrids.stWoodMattock, "Stony Wooden Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.stWoodMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Block.planks, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_InfiHybrids.saWoodMattock.setIconCoord(2, 0);
        ModLoader.addName(mod_InfiHybrids.saWoodMattock, "Sandy Wooden Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.saWoodMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Block.planks, Character.valueOf('|'), mod_InfiTools.sandstoneRod
                });
        mod_InfiHybrids.bWoodMattock.setIconCoord(3, 0);
        ModLoader.addName(mod_InfiHybrids.bWoodMattock, "Necrotic Wooden Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.bWoodMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Block.planks, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.bWoodMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Block.planks, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_InfiHybrids.pWoodMattock.setIconCoord(4, 0);
        ModLoader.addName(mod_InfiHybrids.pWoodMattock, "Fibery Wooden Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.pWoodMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Block.planks, Character.valueOf('|'), mod_InfiTools.paperRod
                });
        mod_InfiHybrids.nWoodMattock.setIconCoord(5, 0);
        ModLoader.addName(mod_InfiHybrids.nWoodMattock, "Bloody Wooden Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.nWoodMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Block.planks, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_InfiHybrids.sWoodMattock.setIconCoord(6, 0);
        ModLoader.addName(mod_InfiHybrids.sWoodMattock, "Slimy Wooden Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.sWoodMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Block.planks, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_InfiHybrids.cWoodMattock.setIconCoord(7, 0);
        ModLoader.addName(mod_InfiHybrids.cWoodMattock, "Spiny Wooden Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.cWoodMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Block.planks, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_InfiHybrids.fWoodMattock.setIconCoord(8, 0);
        ModLoader.addName(mod_InfiHybrids.fWoodMattock, "Flaky Wooden Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.fWoodMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Block.planks, Character.valueOf('|'), mod_InfiTools.flintRod
                });
        mod_InfiHybrids.brWoodMattock.setIconCoord(9, 0);
        ModLoader.addName(mod_InfiHybrids.brWoodMattock, "Baked Wooden Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.brWoodMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Block.planks, Character.valueOf('|'), mod_InfiTools.brickRod
                });
        mod_InfiHybrids.wStoneMattock.setIconCoord(0, 1);
        ModLoader.addName(mod_InfiHybrids.wStoneMattock, "Stone Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.wStoneMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), Item.stick
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.wStoneMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Block.stone, Character.valueOf('|'), Item.stick
                });
        mod_InfiHybrids.stStoneMattock.setIconCoord(1, 1);
        ModLoader.addName(mod_InfiHybrids.stStoneMattock, "Heavy Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.stStoneMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.stStoneMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Block.stone, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_InfiHybrids.saStoneMattock.setIconCoord(2, 1);
        ModLoader.addName(mod_InfiHybrids.saStoneMattock, "Sandy Stone Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.saStoneMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), mod_InfiTools.sandstoneRod
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.saStoneMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Block.stone, Character.valueOf('|'), mod_InfiTools.sandstoneRod
                });
        mod_InfiHybrids.bStoneMattock.setIconCoord(3, 1);
        ModLoader.addName(mod_InfiHybrids.bStoneMattock, "Necrotic Stone Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.bStoneMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.bStoneMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Block.stone, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.bStoneMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.bStoneMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Block.stone, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_InfiHybrids.pStoneMattock.setIconCoord(4, 1);
        ModLoader.addName(mod_InfiHybrids.pStoneMattock, "Fibery Stone Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.pStoneMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), mod_InfiTools.paperRod
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.pStoneMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Block.stone, Character.valueOf('|'), mod_InfiTools.paperRod
                });
        mod_InfiHybrids.mStoneMattock.setIconCoord(5, 1);
        ModLoader.addName(mod_InfiHybrids.mStoneMattock, "Mossy Stone Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.mStoneMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), mod_InfiTools.mossyRod
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.mStoneMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Block.stone, Character.valueOf('|'), mod_InfiTools.mossyRod
                });
        mod_InfiHybrids.nStoneMattock.setIconCoord(6, 1);
        ModLoader.addName(mod_InfiHybrids.nStoneMattock, "Bloody Stone Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.nStoneMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.nStoneMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Block.stone, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_InfiHybrids.iceStoneMattock.setIconCoord(7, 1);
        ModLoader.addName(mod_InfiHybrids.iceStoneMattock, "Icy Stone Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.iceStoneMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), mod_InfiTools.iceRod
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.iceStoneMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Block.stone, Character.valueOf('|'), mod_InfiTools.iceRod
                });
        mod_InfiHybrids.sStoneMattock.setIconCoord(8, 1);
        ModLoader.addName(mod_InfiHybrids.sStoneMattock, "Slimy Stone Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.sStoneMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.sStoneMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Block.stone, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_InfiHybrids.cStoneMattock.setIconCoord(9, 1);
        ModLoader.addName(mod_InfiHybrids.cStoneMattock, "Spiny Stone Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.cStoneMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.cStoneMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Block.stone, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_InfiHybrids.fStoneMattock.setIconCoord(10, 1);
        ModLoader.addName(mod_InfiHybrids.fStoneMattock, "Flaky Stone Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.fStoneMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), mod_InfiTools.flintRod
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.fStoneMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Block.stone, Character.valueOf('|'), mod_InfiTools.flintRod
                });
        mod_InfiHybrids.brStoneMattock.setIconCoord(11, 1);
        ModLoader.addName(mod_InfiHybrids.brStoneMattock, "Baked Stone Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.brStoneMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), mod_InfiTools.brickRod
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.brStoneMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Block.stone, Character.valueOf('|'), mod_InfiTools.brickRod
                });
        mod_InfiHybrids.wIronMattock.setIconCoord(0, 2);
        ModLoader.addName(mod_InfiHybrids.wIronMattock, "Iron Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.wIronMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), Item.stick
                });
        mod_InfiHybrids.stIronMattock.setIconCoord(1, 2);
        ModLoader.addName(mod_InfiHybrids.stIronMattock, "Stony Iron Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.stIronMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_InfiHybrids.iIronMattock.setIconCoord(2, 2);
        ModLoader.addName(mod_InfiHybrids.iIronMattock, "Ironic Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.iIronMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), mod_InfiTools.ironRod
                });
        mod_InfiHybrids.dIronMattock.setIconCoord(3, 2);
        ModLoader.addName(mod_InfiHybrids.dIronMattock, "Jeweled Iron Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.dIronMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), mod_InfiTools.diamondRod
                });
        mod_InfiHybrids.gIronMattock.setIconCoord(4, 2);
        ModLoader.addName(mod_InfiHybrids.gIronMattock, "Expensive Iron Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.gIronMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), mod_InfiTools.goldRod
                });
        mod_InfiHybrids.rIronMattock.setIconCoord(5, 2);
        ModLoader.addName(mod_InfiHybrids.rIronMattock, "Red Iron Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.rIronMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), mod_InfiTools.redstoneRod
                });
        mod_InfiHybrids.oIronMattock.setIconCoord(6, 2);
        ModLoader.addName(mod_InfiHybrids.oIronMattock, "Glassy Iron Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.oIronMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), mod_InfiTools.obsidianRod
                });
        mod_InfiHybrids.bIronMattock.setIconCoord(7, 2);
        ModLoader.addName(mod_InfiHybrids.bIronMattock, "Necrotic Iron Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.bIronMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.bIronMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_InfiHybrids.nIronMattock.setIconCoord(8, 2);
        ModLoader.addName(mod_InfiHybrids.nIronMattock, "Bloody Iron Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.nIronMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_InfiHybrids.glIronMattock.setIconCoord(9, 2);
        ModLoader.addName(mod_InfiHybrids.glIronMattock, "Glowing Iron Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.glIronMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_InfiHybrids.iceIronMattock.setIconCoord(10, 2);
        ModLoader.addName(mod_InfiHybrids.iceIronMattock, "Icy Iron Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.iceIronMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), mod_InfiTools.iceRod
                });
        mod_InfiHybrids.sIronMattock.setIconCoord(11, 2);
        ModLoader.addName(mod_InfiHybrids.sIronMattock, "Slimy Iron Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.sIronMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_InfiHybrids.blIronMattock.setIconCoord(12, 2);
        ModLoader.addName(mod_InfiHybrids.blIronMattock, "Blazing Iron Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.blIronMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), Item.blazeRod
                });
        mod_InfiHybrids.wDiamondMattock.setIconCoord(0, 3);
        ModLoader.addName(mod_InfiHybrids.wDiamondMattock, "Diamond Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.wDiamondMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), Item.stick
                });
        mod_InfiHybrids.stDiamondMattock.setIconCoord(1, 3);
        ModLoader.addName(mod_InfiHybrids.stDiamondMattock, "Stony Diamond Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.stDiamondMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_InfiHybrids.iDiamondMattock.setIconCoord(2, 3);
        ModLoader.addName(mod_InfiHybrids.iDiamondMattock, "Hard Diamond Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.iDiamondMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), mod_InfiTools.ironRod
                });
        mod_InfiHybrids.dDiamondMattock.setIconCoord(3, 3);
        ModLoader.addName(mod_InfiHybrids.dDiamondMattock, "Diamondium Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.dDiamondMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), mod_InfiTools.diamondRod
                });
        mod_InfiHybrids.gDiamondMattock.setIconCoord(4, 3);
        ModLoader.addName(mod_InfiHybrids.gDiamondMattock, "Expensive Diamond Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.gDiamondMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), mod_InfiTools.goldRod
                });
        mod_InfiHybrids.rDiamondMattock.setIconCoord(5, 3);
        ModLoader.addName(mod_InfiHybrids.rDiamondMattock, "Red Diamond Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.rDiamondMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), mod_InfiTools.redstoneRod
                });
        mod_InfiHybrids.oDiamondMattock.setIconCoord(6, 3);
        ModLoader.addName(mod_InfiHybrids.oDiamondMattock, "Glassy Diamond Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.oDiamondMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), mod_InfiTools.obsidianRod
                });
        mod_InfiHybrids.bDiamondMattock.setIconCoord(7, 3);
        ModLoader.addName(mod_InfiHybrids.bDiamondMattock, "Necrotic Diamond Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.bDiamondMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.bDiamondMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_InfiHybrids.mDiamondMattock.setIconCoord(8, 3);
        ModLoader.addName(mod_InfiHybrids.mDiamondMattock, "Mossy Diamond Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.mDiamondMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), mod_InfiTools.mossyRod
                });
        mod_InfiHybrids.nDiamondMattock.setIconCoord(9, 3);
        ModLoader.addName(mod_InfiHybrids.nDiamondMattock, "Bloody Diamond Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.nDiamondMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_InfiHybrids.glDiamondMattock.setIconCoord(10, 3);
        ModLoader.addName(mod_InfiHybrids.glDiamondMattock, "Glowing Diamond Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.glDiamondMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_InfiHybrids.blDiamondMattock.setIconCoord(11, 3);
        ModLoader.addName(mod_InfiHybrids.blDiamondMattock, "Blazing Diamond Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.blDiamondMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), Item.blazeRod
                });
        mod_InfiHybrids.wGoldMattock.setIconCoord(0, 4);
        ModLoader.addName(mod_InfiHybrids.wGoldMattock, "Stony Gold Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.wGoldMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), Item.stick
                });
        mod_InfiHybrids.stGoldMattock.setIconCoord(1, 4);
        ModLoader.addName(mod_InfiHybrids.stGoldMattock, "Stony Gold Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.stGoldMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_InfiHybrids.gGoldMattock.setIconCoord(2, 4);
        ModLoader.addName(mod_InfiHybrids.gGoldMattock, "Expensive Useless Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.gGoldMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), mod_InfiTools.goldRod
                });
        mod_InfiHybrids.oGoldMattock.setIconCoord(3, 4);
        ModLoader.addName(mod_InfiHybrids.oGoldMattock, "Glassy Gold Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.oGoldMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), mod_InfiTools.obsidianRod
                });
        mod_InfiHybrids.saGoldMattock.setIconCoord(4, 4);
        ModLoader.addName(mod_InfiHybrids.saGoldMattock, "Sandy Gold Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.saGoldMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), mod_InfiTools.sandstoneRod
                });
        mod_InfiHybrids.bGoldMattock.setIconCoord(5, 4);
        ModLoader.addName(mod_InfiHybrids.bGoldMattock, "Necrotic Gold Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.bGoldMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.bGoldMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_InfiHybrids.mGoldMattock.setIconCoord(6, 4);
        ModLoader.addName(mod_InfiHybrids.mGoldMattock, "Mossy Gold Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.mGoldMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), mod_InfiTools.mossyRod
                });
        mod_InfiHybrids.nGoldMattock.setIconCoord(7, 4);
        ModLoader.addName(mod_InfiHybrids.nGoldMattock, "Bloody Gold Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.nGoldMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_InfiHybrids.glGoldMattock.setIconCoord(8, 4);
        ModLoader.addName(mod_InfiHybrids.glGoldMattock, "Glowing Gold Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.glGoldMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_InfiHybrids.iceGoldMattock.setIconCoord(9, 4);
        ModLoader.addName(mod_InfiHybrids.iceGoldMattock, "Icy Gold Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.iceGoldMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), mod_InfiTools.iceRod
                });
        mod_InfiHybrids.sGoldMattock.setIconCoord(10, 4);
        ModLoader.addName(mod_InfiHybrids.sGoldMattock, "Slimy Gold Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.sGoldMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_InfiHybrids.fGoldMattock.setIconCoord(11, 4);
        ModLoader.addName(mod_InfiHybrids.fGoldMattock, "Flaky Gold Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.fGoldMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), mod_InfiTools.flintRod
                });
        mod_InfiHybrids.wRedstoneMattock.setIconCoord(0, 5);
        ModLoader.addName(mod_InfiHybrids.wRedstoneMattock, "Redstone Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.wRedstoneMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), Item.stick
                });
        mod_InfiHybrids.stRedstoneMattock.setIconCoord(1, 5);
        ModLoader.addName(mod_InfiHybrids.stRedstoneMattock, "Stony Redstone Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.stRedstoneMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_InfiHybrids.iRedstoneMattock.setIconCoord(2, 5);
        ModLoader.addName(mod_InfiHybrids.iRedstoneMattock, "Hard Redstone Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.iRedstoneMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), mod_InfiTools.ironRod
                });
        mod_InfiHybrids.dRedstoneMattock.setIconCoord(3, 5);
        ModLoader.addName(mod_InfiHybrids.dRedstoneMattock, "Jeweled Redstone Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.dRedstoneMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), mod_InfiTools.diamondRod
                });
        mod_InfiHybrids.rRedstoneMattock.setIconCoord(4, 5);
        ModLoader.addName(mod_InfiHybrids.rRedstoneMattock, "Redredred Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.rRedstoneMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), mod_InfiTools.redstoneRod
                });
        mod_InfiHybrids.oRedstoneMattock.setIconCoord(5, 5);
        ModLoader.addName(mod_InfiHybrids.oRedstoneMattock, "Glassy Redstone Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.oRedstoneMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), mod_InfiTools.obsidianRod
                });
        mod_InfiHybrids.bRedstoneMattock.setIconCoord(6, 5);
        ModLoader.addName(mod_InfiHybrids.bRedstoneMattock, "Necrotic Redstone Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.bRedstoneMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.bRedstoneMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_InfiHybrids.mRedstoneMattock.setIconCoord(7, 5);
        ModLoader.addName(mod_InfiHybrids.mRedstoneMattock, "Mossy Redstone Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.mRedstoneMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), mod_InfiTools.mossyRod
                });
        mod_InfiHybrids.glRedstoneMattock.setIconCoord(8, 5);
        ModLoader.addName(mod_InfiHybrids.glRedstoneMattock, "Glowing Redstone Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.glRedstoneMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_InfiHybrids.sRedstoneMattock.setIconCoord(9, 5);
        ModLoader.addName(mod_InfiHybrids.sRedstoneMattock, "Slimy Redstone Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.sRedstoneMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_InfiHybrids.blRedstoneMattock.setIconCoord(10, 5);
        ModLoader.addName(mod_InfiHybrids.blRedstoneMattock, "Blazing Redstone Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.blRedstoneMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), Item.blazeRod
                });
        mod_InfiHybrids.wObsidianMattock.setIconCoord(0, 6);
        ModLoader.addName(mod_InfiHybrids.wObsidianMattock, "Obsidian Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.wObsidianMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), Item.stick
                });
        mod_InfiHybrids.stObsidianMattock.setIconCoord(1, 6);
        ModLoader.addName(mod_InfiHybrids.stObsidianMattock, "Stony Obsidian Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.stObsidianMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_InfiHybrids.iObsidianMattock.setIconCoord(2, 6);
        ModLoader.addName(mod_InfiHybrids.iObsidianMattock, "Hard Obsidian Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.iObsidianMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), mod_InfiTools.ironRod
                });
        mod_InfiHybrids.dObsidianMattock.setIconCoord(3, 6);
        ModLoader.addName(mod_InfiHybrids.dObsidianMattock, "Jeweled Obsidian Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.dObsidianMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), mod_InfiTools.diamondRod
                });
        mod_InfiHybrids.gObsidianMattock.setIconCoord(4, 6);
        ModLoader.addName(mod_InfiHybrids.gObsidianMattock, "Expensive Obsidian Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.gObsidianMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), mod_InfiTools.goldRod
                });
        mod_InfiHybrids.rObsidianMattock.setIconCoord(5, 6);
        ModLoader.addName(mod_InfiHybrids.rObsidianMattock, "Red Obsidian Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.rObsidianMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), mod_InfiTools.redstoneRod
                });
        mod_InfiHybrids.oObsidianMattock.setIconCoord(6, 6);
        ModLoader.addName(mod_InfiHybrids.oObsidianMattock, "Wicked Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.oObsidianMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), mod_InfiTools.obsidianRod
                });
        mod_InfiHybrids.bObsidianMattock.setIconCoord(7, 6);
        ModLoader.addName(mod_InfiHybrids.bObsidianMattock, "Necrotic Obsidian Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.bObsidianMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.bObsidianMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_InfiHybrids.nObsidianMattock.setIconCoord(8, 6);
        ModLoader.addName(mod_InfiHybrids.nObsidianMattock, "Bloody Obsidian Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.nObsidianMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_InfiHybrids.glObsidianMattock.setIconCoord(9, 6);
        ModLoader.addName(mod_InfiHybrids.glObsidianMattock, "Glowing Obsidian Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.glObsidianMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_InfiHybrids.sObsidianMattock.setIconCoord(10, 6);
        ModLoader.addName(mod_InfiHybrids.sObsidianMattock, "Slimy Obsidian Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.sObsidianMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_InfiHybrids.fObsidianMattock.setIconCoord(11, 6);
        ModLoader.addName(mod_InfiHybrids.fObsidianMattock, "Flaky Obsidian Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.fObsidianMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), mod_InfiTools.flintRod
                });
        mod_InfiHybrids.blObsidianMattock.setIconCoord(12, 6);
        ModLoader.addName(mod_InfiHybrids.blObsidianMattock, "Blazing Obsidian Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.blObsidianMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), Item.blazeRod
                });
        mod_InfiHybrids.wSandstoneMattock.setIconCoord(0, 7);
        ModLoader.addName(mod_InfiHybrids.wSandstoneMattock, "Sandstone Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.wSandstoneMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), Item.stick
                });
        mod_InfiHybrids.stSandstoneMattock.setIconCoord(1, 7);
        ModLoader.addName(mod_InfiHybrids.stSandstoneMattock, "Stony Sandstone Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.stSandstoneMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_InfiHybrids.saSandstoneMattock.setIconCoord(2, 7);
        ModLoader.addName(mod_InfiHybrids.saSandstoneMattock, "Sandscrape");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.saSandstoneMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), mod_InfiTools.sandstoneRod
                });
        mod_InfiHybrids.bSandstoneMattock.setIconCoord(3, 7);
        ModLoader.addName(mod_InfiHybrids.bSandstoneMattock, "Necrotic Sandstone Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.bSandstoneMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.bSandstoneMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_InfiHybrids.pSandstoneMattock.setIconCoord(4, 7);
        ModLoader.addName(mod_InfiHybrids.pSandstoneMattock, "Fibery Sandstone Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.pSandstoneMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), mod_InfiTools.paperRod
                });
        mod_InfiHybrids.nSandstoneMattock.setIconCoord(5, 7);
        ModLoader.addName(mod_InfiHybrids.nSandstoneMattock, "Bloody Sandstone Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.nSandstoneMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_InfiHybrids.iceSandstoneMattock.setIconCoord(6, 7);
        ModLoader.addName(mod_InfiHybrids.iceSandstoneMattock, "Icy Sandstone Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.iceSandstoneMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), mod_InfiTools.iceRod
                });
        mod_InfiHybrids.sSandstoneMattock.setIconCoord(7, 7);
        ModLoader.addName(mod_InfiHybrids.sSandstoneMattock, "Slimy Sandstone Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.sSandstoneMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_InfiHybrids.cSandstoneMattock.setIconCoord(8, 7);
        ModLoader.addName(mod_InfiHybrids.cSandstoneMattock, "Spiny Sandstone Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.cSandstoneMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_InfiHybrids.fSandstoneMattock.setIconCoord(9, 7);
        ModLoader.addName(mod_InfiHybrids.fSandstoneMattock, "Flaky Sandstone Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.fSandstoneMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), mod_InfiTools.flintRod
                });
        mod_InfiHybrids.brSandstoneMattock.setIconCoord(10, 7);
        ModLoader.addName(mod_InfiHybrids.brSandstoneMattock, "Baked Sandstone Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.brSandstoneMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), mod_InfiTools.brickRod
                });
        mod_InfiHybrids.wBoneMattock.setIconCoord(0, 8);
        ModLoader.addName(mod_InfiHybrids.wBoneMattock, "Bone Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.wBoneMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Item.bone, Character.valueOf('|'), Item.stick
                });
        mod_InfiHybrids.stBoneMattock.setIconCoord(1, 8);
        ModLoader.addName(mod_InfiHybrids.stBoneMattock, "Stony Bone Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.stBoneMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_InfiHybrids.iBoneMattock.setIconCoord(2, 8);
        ModLoader.addName(mod_InfiHybrids.iBoneMattock, "Hard Bone Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.iBoneMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.ironRod
                });
        mod_InfiHybrids.dBoneMattock.setIconCoord(3, 8);
        ModLoader.addName(mod_InfiHybrids.dBoneMattock, "Jeweled Bone Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.dBoneMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.diamondRod
                });
        mod_InfiHybrids.rBoneMattock.setIconCoord(4, 8);
        ModLoader.addName(mod_InfiHybrids.rBoneMattock, "Red Bone Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.rBoneMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.redstoneRod
                });
        mod_InfiHybrids.oBoneMattock.setIconCoord(5, 8);
        ModLoader.addName(mod_InfiHybrids.oBoneMattock, "Glassy Bone Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.oBoneMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.obsidianRod
                });
        mod_InfiHybrids.bBoneMattock.setIconCoord(6, 8);
        ModLoader.addName(mod_InfiHybrids.bBoneMattock, "Reanimated Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.bBoneMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Item.bone, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.bBoneMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_InfiHybrids.mBoneMattock.setIconCoord(7, 8);
        ModLoader.addName(mod_InfiHybrids.mBoneMattock, "Mossy Bone Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.mBoneMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.mossyRod
                });
        mod_InfiHybrids.nBoneMattock.setIconCoord(8, 8);
        ModLoader.addName(mod_InfiHybrids.nBoneMattock, "Netherrack Bone Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.nBoneMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_InfiHybrids.glBoneMattock.setIconCoord(9, 8);
        ModLoader.addName(mod_InfiHybrids.glBoneMattock, "Glowstone Bone Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.glBoneMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_InfiHybrids.sBoneMattock.setIconCoord(10, 8);
        ModLoader.addName(mod_InfiHybrids.sBoneMattock, "Slimy Bone Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.sBoneMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_InfiHybrids.cBoneMattock.setIconCoord(11, 8);
        ModLoader.addName(mod_InfiHybrids.cBoneMattock, "Spiny Bone Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.cBoneMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_InfiHybrids.fBoneMattock.setIconCoord(12, 8);
        ModLoader.addName(mod_InfiHybrids.fBoneMattock, "Flaky Bone Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.fBoneMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.flintRod
                });
        mod_InfiHybrids.brBoneMattock.setIconCoord(13, 8);
        ModLoader.addName(mod_InfiHybrids.brBoneMattock, "Baked Bone Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.brBoneMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.brickRod
                });
        mod_InfiHybrids.blBoneMattock.setIconCoord(14, 8);
        ModLoader.addName(mod_InfiHybrids.blBoneMattock, "Blazing Bone Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.blBoneMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Item.bone, Character.valueOf('|'), Item.blazeRod
                });
        mod_InfiHybrids.wPaperMattock.setIconCoord(0, 9);
        ModLoader.addName(mod_InfiHybrids.wPaperMattock, "Paper Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.wPaperMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), mod_InfiTools.paperStack, Character.valueOf('|'), Item.stick
                });
        mod_InfiHybrids.saPaperMattock.setIconCoord(1, 9);
        ModLoader.addName(mod_InfiHybrids.saPaperMattock, "Stony Paper Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.saPaperMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), mod_InfiTools.paperStack, Character.valueOf('|'), mod_InfiTools.sandstoneRod
                });
        mod_InfiHybrids.bPaperMattock.setIconCoord(2, 9);
        ModLoader.addName(mod_InfiHybrids.bPaperMattock, "Necrotic Paper Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.bPaperMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), mod_InfiTools.paperStack, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.bPaperMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), mod_InfiTools.paperStack, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_InfiHybrids.pPaperMattock.setIconCoord(3, 9);
        ModLoader.addName(mod_InfiHybrids.pPaperMattock, "Dirty Digger");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.pPaperMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), mod_InfiTools.paperStack, Character.valueOf('|'), mod_InfiTools.paperRod
                });
        mod_InfiHybrids.sPaperMattock.setIconCoord(4, 9);
        ModLoader.addName(mod_InfiHybrids.sPaperMattock, "Slimy Paper Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.sPaperMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), mod_InfiTools.paperStack, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_InfiHybrids.cPaperMattock.setIconCoord(5, 9);
        ModLoader.addName(mod_InfiHybrids.cPaperMattock, "Spiny Paper Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.cPaperMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), mod_InfiTools.paperStack, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_InfiHybrids.brPaperMattock.setIconCoord(6, 9);
        ModLoader.addName(mod_InfiHybrids.brPaperMattock, "Baked Paper Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.brPaperMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), mod_InfiTools.paperStack, Character.valueOf('|'), mod_InfiTools.brickRod
                });
        mod_InfiHybrids.stMossyMattock.setIconCoord(0, 10);
        ModLoader.addName(mod_InfiHybrids.stMossyMattock, "Stony Moss-Covered Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.stMossyMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), mod_InfiTools.mossBallCrafted, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_InfiHybrids.dMossyMattock.setIconCoord(1, 10);
        ModLoader.addName(mod_InfiHybrids.dMossyMattock, "Jeweled Moss-Covered Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.dMossyMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), mod_InfiTools.mossBallCrafted, Character.valueOf('|'), mod_InfiTools.diamondRod
                });
        mod_InfiHybrids.rMossyMattock.setIconCoord(2, 10);
        ModLoader.addName(mod_InfiHybrids.rMossyMattock, "Red Moss-Covered Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.rMossyMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), mod_InfiTools.mossBallCrafted, Character.valueOf('|'), mod_InfiTools.redstoneRod
                });
        mod_InfiHybrids.bMossyMattock.setIconCoord(3, 10);
        ModLoader.addName(mod_InfiHybrids.bMossyMattock, "Necrotic Moss-Covered Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.bMossyMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), mod_InfiTools.mossBallCrafted, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.bMossyMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), mod_InfiTools.mossBallCrafted, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_InfiHybrids.mMossyMattock.setIconCoord(4, 10);
        ModLoader.addName(mod_InfiHybrids.mMossyMattock, "Living Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.mMossyMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), mod_InfiTools.mossBallCrafted, Character.valueOf('|'), mod_InfiTools.mossyRod
                });
        mod_InfiHybrids.glMossyMattock.setIconCoord(5, 10);
        ModLoader.addName(mod_InfiHybrids.glMossyMattock, "Glowing Moss-Covered Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.glMossyMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), mod_InfiTools.mossBallCrafted, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_InfiHybrids.wNetherrackMattock.setIconCoord(0, 11);
        ModLoader.addName(mod_InfiHybrids.wNetherrackMattock, "Netherrack Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.wNetherrackMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), Item.stick
                });
        mod_InfiHybrids.stNetherrackMattock.setIconCoord(1, 11);
        ModLoader.addName(mod_InfiHybrids.stNetherrackMattock, "Stony Netherrack Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.stNetherrackMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_InfiHybrids.iNetherrackMattock.setIconCoord(2, 11);
        ModLoader.addName(mod_InfiHybrids.iNetherrackMattock, "Hard Netherrack Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.iNetherrackMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.ironRod
                });
        mod_InfiHybrids.rNetherrackMattock.setIconCoord(3, 11);
        ModLoader.addName(mod_InfiHybrids.rNetherrackMattock, "Red Netherrack Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.rNetherrackMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.redstoneRod
                });
        mod_InfiHybrids.oNetherrackMattock.setIconCoord(4, 11);
        ModLoader.addName(mod_InfiHybrids.oNetherrackMattock, "Glassy Netherrack Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.oNetherrackMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.obsidianRod
                });
        mod_InfiHybrids.saNetherrackMattock.setIconCoord(5, 11);
        ModLoader.addName(mod_InfiHybrids.saNetherrackMattock, "Sandy Netherrack Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.saNetherrackMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.sandstoneRod
                });
        mod_InfiHybrids.bNetherrackMattock.setIconCoord(6, 11);
        ModLoader.addName(mod_InfiHybrids.bNetherrackMattock, "Necrotic Netherrack Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.bNetherrackMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.bNetherrackMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_InfiHybrids.mNetherrackMattock.setIconCoord(7, 11);
        ModLoader.addName(mod_InfiHybrids.mNetherrackMattock, "Mossy Netherrack Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.mNetherrackMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.mossyRod
                });
        mod_InfiHybrids.nNetherrackMattock.setIconCoord(8, 11);
        ModLoader.addName(mod_InfiHybrids.nNetherrackMattock, "Blood Scraper");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.nNetherrackMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_InfiHybrids.glNetherrackMattock.setIconCoord(9, 11);
        ModLoader.addName(mod_InfiHybrids.glNetherrackMattock, "Glowing Netherrack Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.glNetherrackMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_InfiHybrids.iceNetherrackMattock.setIconCoord(10, 11);
        ModLoader.addName(mod_InfiHybrids.iceNetherrackMattock, "Icy Netherrack Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.iceNetherrackMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.iceRod
                });
        mod_InfiHybrids.sNetherrackMattock.setIconCoord(11, 11);
        ModLoader.addName(mod_InfiHybrids.sNetherrackMattock, "Slimy Netherrack Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.sNetherrackMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_InfiHybrids.cNetherrackMattock.setIconCoord(12, 11);
        ModLoader.addName(mod_InfiHybrids.cNetherrackMattock, "Spiny Netherrack Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.cNetherrackMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_InfiHybrids.fNetherrackMattock.setIconCoord(13, 11);
        ModLoader.addName(mod_InfiHybrids.fNetherrackMattock, "Flaky Netherrack Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.fNetherrackMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_InfiHybrids.brNetherrackMattock.setIconCoord(14, 11);
        ModLoader.addName(mod_InfiHybrids.brNetherrackMattock, "Baked Netherrack Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.brNetherrackMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_InfiHybrids.blNetherrackMattock.setIconCoord(15, 11);
        ModLoader.addName(mod_InfiHybrids.blNetherrackMattock, "Blazing Netherrack Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.blNetherrackMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_InfiHybrids.wGlowstoneMattock.setIconCoord(0, 12);
        ModLoader.addName(mod_InfiHybrids.wGlowstoneMattock, "Glowstone Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.wGlowstoneMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), Item.stick
                });
        mod_InfiHybrids.stGlowstoneMattock.setIconCoord(1, 12);
        ModLoader.addName(mod_InfiHybrids.stGlowstoneMattock, "Stony Glowstone Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.stGlowstoneMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_InfiHybrids.iGlowstoneMattock.setIconCoord(2, 12);
        ModLoader.addName(mod_InfiHybrids.iGlowstoneMattock, "Hard Glowstone Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.iGlowstoneMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.ironRod
                });
        mod_InfiHybrids.dGlowstoneMattock.setIconCoord(3, 12);
        ModLoader.addName(mod_InfiHybrids.dGlowstoneMattock, "Jeweled Glowstone Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.dGlowstoneMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.diamondRod
                });
        mod_InfiHybrids.rGlowstoneMattock.setIconCoord(4, 12);
        ModLoader.addName(mod_InfiHybrids.rGlowstoneMattock, "Red Glowstone Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.rGlowstoneMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.redstoneRod
                });
        mod_InfiHybrids.oGlowstoneMattock.setIconCoord(5, 12);
        ModLoader.addName(mod_InfiHybrids.oGlowstoneMattock, "Glassy Glowstone Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.oGlowstoneMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.obsidianRod
                });
        mod_InfiHybrids.bGlowstoneMattock.setIconCoord(6, 12);
        ModLoader.addName(mod_InfiHybrids.bGlowstoneMattock, "Necrotic Glowstone Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.bGlowstoneMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.bGlowstoneMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_InfiHybrids.mGlowstoneMattock.setIconCoord(7, 12);
        ModLoader.addName(mod_InfiHybrids.mGlowstoneMattock, "Mossy Glowstone Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.mGlowstoneMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.mossyRod
                });
        mod_InfiHybrids.nGlowstoneMattock.setIconCoord(8, 12);
        ModLoader.addName(mod_InfiHybrids.nGlowstoneMattock, "Bloody Glowstone Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.nGlowstoneMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_InfiHybrids.glGlowstoneMattock.setIconCoord(9, 12);
        ModLoader.addName(mod_InfiHybrids.glGlowstoneMattock, "Bright Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.glGlowstoneMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_InfiHybrids.iceGlowstoneMattock.setIconCoord(10, 12);
        ModLoader.addName(mod_InfiHybrids.iceGlowstoneMattock, "Icy Glowstone Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.iceGlowstoneMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.iceRod
                });
        mod_InfiHybrids.lGlowstoneMattock.setIconCoord(11, 12);
        ModLoader.addName(mod_InfiHybrids.lGlowstoneMattock, "Fiery Glowstone Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.lGlowstoneMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_InfiHybrids.sGlowstoneMattock.setIconCoord(12, 12);
        ModLoader.addName(mod_InfiHybrids.sGlowstoneMattock, "Slimy Glowstone Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.sGlowstoneMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_InfiHybrids.blGlowstoneMattock.setIconCoord(13, 12);
        ModLoader.addName(mod_InfiHybrids.blGlowstoneMattock, "Blazing Glowstone Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.blGlowstoneMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), Item.blazeRod
                });
        mod_InfiHybrids.wIceMattock.setIconCoord(0, 13);
        ModLoader.addName(mod_InfiHybrids.wIceMattock, "Ice Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.wIceMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Block.ice, Character.valueOf('|'), Item.stick
                });
        mod_InfiHybrids.stIceMattock.setIconCoord(1, 13);
        ModLoader.addName(mod_InfiHybrids.stIceMattock, "Stony Ice Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.stIceMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_InfiHybrids.iIceMattock.setIconCoord(2, 13);
        ModLoader.addName(mod_InfiHybrids.iIceMattock, "Hard Ice Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.iIceMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.ironRod
                });
        mod_InfiHybrids.dIceMattock.setIconCoord(3, 13);
        ModLoader.addName(mod_InfiHybrids.dIceMattock, "Jeweled Ice Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.dIceMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.diamondRod
                });
        mod_InfiHybrids.gIceMattock.setIconCoord(4, 13);
        ModLoader.addName(mod_InfiHybrids.gIceMattock, "Expensive Ice Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.gIceMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.goldRod
                });
        mod_InfiHybrids.rIceMattock.setIconCoord(5, 13);
        ModLoader.addName(mod_InfiHybrids.rIceMattock, "Red Ice Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.rIceMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.redstoneRod
                });
        mod_InfiHybrids.oIceMattock.setIconCoord(6, 13);
        ModLoader.addName(mod_InfiHybrids.oIceMattock, "Glassy Ice Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.oIceMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.obsidianRod
                });
        mod_InfiHybrids.saIceMattock.setIconCoord(7, 13);
        ModLoader.addName(mod_InfiHybrids.saIceMattock, "Sandy Ice Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.saIceMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.sandstoneRod
                });
        mod_InfiHybrids.bIceMattock.setIconCoord(8, 13);
        ModLoader.addName(mod_InfiHybrids.bIceMattock, "Necrotic Ice Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.bIceMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Block.ice, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.bIceMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_InfiHybrids.glIceMattock.setIconCoord(9, 13);
        ModLoader.addName(mod_InfiHybrids.glIceMattock, "Glowing Ice Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.glIceMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_InfiHybrids.iceIceMattock.setIconCoord(10, 13);
        ModLoader.addName(mod_InfiHybrids.iceIceMattock, "Frozen Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.iceIceMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.iceRod
                });
        mod_InfiHybrids.sIceMattock.setIconCoord(11, 13);
        ModLoader.addName(mod_InfiHybrids.sIceMattock, "Slimy Ice Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.sIceMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_InfiHybrids.cIceMattock.setIconCoord(12, 13);
        ModLoader.addName(mod_InfiHybrids.cIceMattock, "Spiny Ice Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.cIceMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_InfiHybrids.fIceMattock.setIconCoord(13, 13);
        ModLoader.addName(mod_InfiHybrids.fIceMattock, "Flaky Ice Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.fIceMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.flintRod
                });
        mod_InfiHybrids.brIceMattock.setIconCoord(14, 13);
        ModLoader.addName(mod_InfiHybrids.brIceMattock, "Baked Ice Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.brIceMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.brickRod
                });
        mod_InfiHybrids.dLavaMattock.setIconCoord(0, 14);
        ModLoader.addName(mod_InfiHybrids.dLavaMattock, "Jeweled Lava Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.dLavaMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), mod_InfiTools.lavaCrystal, Character.valueOf('|'), mod_InfiTools.diamondRod
                });
        mod_InfiHybrids.rLavaMattock.setIconCoord(1, 14);
        ModLoader.addName(mod_InfiHybrids.rLavaMattock, "Red Lava Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.rLavaMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), mod_InfiTools.lavaCrystal, Character.valueOf('|'), mod_InfiTools.redstoneRod
                });
        mod_InfiHybrids.bLavaMattock.setIconCoord(2, 14);
        ModLoader.addName(mod_InfiHybrids.bLavaMattock, "Necrotic Lava Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.bLavaMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), mod_InfiTools.lavaCrystal, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.bLavaMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), mod_InfiTools.lavaCrystal, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_InfiHybrids.nLavaMattock.setIconCoord(3, 14);
        ModLoader.addName(mod_InfiHybrids.nLavaMattock, "Bloody Lava Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.nLavaMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), mod_InfiTools.lavaCrystal, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_InfiHybrids.glLavaMattock.setIconCoord(4, 14);
        ModLoader.addName(mod_InfiHybrids.glLavaMattock, "Glowing Lava Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.glLavaMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), mod_InfiTools.lavaCrystal, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_InfiHybrids.lLavaMattock.setIconCoord(5, 14);
        ModLoader.addName(mod_InfiHybrids.lLavaMattock, "Land Scorcher");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.lLavaMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), mod_InfiTools.lavaCrystal, Character.valueOf('|'), mod_InfiTools.lavaRod
                });
        mod_InfiHybrids.blLavaMattock.setIconCoord(6, 14);
        ModLoader.addName(mod_InfiHybrids.blLavaMattock, "Blazing Lava Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.blLavaMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), mod_InfiTools.lavaCrystal, Character.valueOf('|'), Item.blazeRod
                });
        mod_InfiHybrids.wSlimeMattock.setIconCoord(0, 15);
        ModLoader.addName(mod_InfiHybrids.wSlimeMattock, "Slime Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.wSlimeMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), Item.stick
                });
        mod_InfiHybrids.stSlimeMattock.setIconCoord(1, 15);
        ModLoader.addName(mod_InfiHybrids.stSlimeMattock, "Stony Slime Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.stSlimeMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_InfiHybrids.iSlimeMattock.setIconCoord(2, 15);
        ModLoader.addName(mod_InfiHybrids.iSlimeMattock, "Hard Slime Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.iSlimeMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.ironRod
                });
        mod_InfiHybrids.dSlimeMattock.setIconCoord(3, 15);
        ModLoader.addName(mod_InfiHybrids.dSlimeMattock, "Jeweled Slime Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.dSlimeMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.diamondRod
                });
        mod_InfiHybrids.gSlimeMattock.setIconCoord(4, 15);
        ModLoader.addName(mod_InfiHybrids.gSlimeMattock, "Expensive Slime Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.gSlimeMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.goldRod
                });
        mod_InfiHybrids.rSlimeMattock.setIconCoord(5, 15);
        ModLoader.addName(mod_InfiHybrids.rSlimeMattock, "Red Slime Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.rSlimeMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.redstoneRod
                });
        mod_InfiHybrids.oSlimeMattock.setIconCoord(6, 15);
        ModLoader.addName(mod_InfiHybrids.oSlimeMattock, "Glassy Slime Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.oSlimeMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.obsidianRod
                });
        mod_InfiHybrids.saSlimeMattock.setIconCoord(7, 15);
        ModLoader.addName(mod_InfiHybrids.saSlimeMattock, "Sandy Slime Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.saSlimeMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.sandstoneRod
                });
        mod_InfiHybrids.bSlimeMattock.setIconCoord(8, 15);
        ModLoader.addName(mod_InfiHybrids.bSlimeMattock, "Necrotic Slime Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.bSlimeMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.bSlimeMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_InfiHybrids.pSlimeMattock.setIconCoord(9, 15);
        ModLoader.addName(mod_InfiHybrids.pSlimeMattock, "Fibery Slime Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.pSlimeMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.paperRod
                });
        mod_InfiHybrids.mSlimeMattock.setIconCoord(10, 15);
        ModLoader.addName(mod_InfiHybrids.mSlimeMattock, "Mossy Slime Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.mSlimeMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.mossyRod
                });
        mod_InfiHybrids.nSlimeMattock.setIconCoord(11, 15);
        ModLoader.addName(mod_InfiHybrids.nSlimeMattock, "Bloody Slime Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.nSlimeMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_InfiHybrids.glSlimeMattock.setIconCoord(12, 15);
        ModLoader.addName(mod_InfiHybrids.glSlimeMattock, "Glowing Slime Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.glSlimeMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_InfiHybrids.iceSlimeMattock.setIconCoord(13, 15);
        ModLoader.addName(mod_InfiHybrids.iceSlimeMattock, "Icy Slime Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.iceSlimeMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.iceRod
                });
        mod_InfiHybrids.lSlimeMattock.setIconCoord(14, 15);
        ModLoader.addName(mod_InfiHybrids.lSlimeMattock, "Fiery Slime Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.lSlimeMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.lavaRod
                });
        mod_InfiHybrids.sSlimeMattock.setIconCoord(15, 15);
        ModLoader.addName(mod_InfiHybrids.sSlimeMattock, "Slippery Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.sSlimeMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_InfiHybrids.cSlimeMattock.setIconCoord(12, 14);
        ModLoader.addName(mod_InfiHybrids.cSlimeMattock, "Spiny Slime Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.cSlimeMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_InfiHybrids.fSlimeMattock.setIconCoord(13, 14);
        ModLoader.addName(mod_InfiHybrids.fSlimeMattock, "Flaky Slime Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.fSlimeMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.flintRod
                });
        mod_InfiHybrids.brSlimeMattock.setIconCoord(14, 14);
        ModLoader.addName(mod_InfiHybrids.brSlimeMattock, "Baked Slime Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.brSlimeMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.brickRod
                });
        mod_InfiHybrids.blSlimeMattock.setIconCoord(15, 14);
        ModLoader.addName(mod_InfiHybrids.blSlimeMattock, "Blazing Slime Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.blSlimeMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), Item.blazeRod
                });
        mod_InfiHybrids.wCactusMattock.setIconCoord(10, 0);
        ModLoader.addName(mod_InfiHybrids.wCactusMattock, "Cactus Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.wCactusMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Block.cactus, Character.valueOf('|'), Item.stick
                });
        mod_InfiHybrids.stCactusMattock.setIconCoord(11, 0);
        ModLoader.addName(mod_InfiHybrids.stCactusMattock, "Stony Cactus Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.stCactusMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Block.cactus, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_InfiHybrids.saCactusMattock.setIconCoord(12, 0);
        ModLoader.addName(mod_InfiHybrids.saCactusMattock, "Sandy Cactus Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.saCactusMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Block.cactus, Character.valueOf('|'), mod_InfiTools.sandstoneRod
                });
        mod_InfiHybrids.bCactusMattock.setIconCoord(13, 0);
        ModLoader.addName(mod_InfiHybrids.bCactusMattock, "Necrotic Cactus Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.bCactusMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Block.cactus, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.bCactusMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Block.cactus, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_InfiHybrids.pCactusMattock.setIconCoord(14, 0);
        ModLoader.addName(mod_InfiHybrids.pCactusMattock, "Fibery Cactus Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.pCactusMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Block.cactus, Character.valueOf('|'), mod_InfiTools.paperRod
                });
        mod_InfiHybrids.nCactusMattock.setIconCoord(15, 0);
        ModLoader.addName(mod_InfiHybrids.nCactusMattock, "Bloody Cactus Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.nCactusMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Block.cactus, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_InfiHybrids.sCactusMattock.setIconCoord(12, 1);
        ModLoader.addName(mod_InfiHybrids.sCactusMattock, "Slimy Cactus Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.sCactusMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Block.cactus, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_InfiHybrids.cCactusMattock.setIconCoord(13, 1);
        ModLoader.addName(mod_InfiHybrids.cCactusMattock, "Spined Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.cCactusMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Block.cactus, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_InfiHybrids.fCactusMattock.setIconCoord(14, 1);
        ModLoader.addName(mod_InfiHybrids.fCactusMattock, "Flaky Cactus Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.fCactusMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Block.cactus, Character.valueOf('|'), mod_InfiTools.flintRod
                });
        mod_InfiHybrids.brCactusMattock.setIconCoord(15, 1);
        ModLoader.addName(mod_InfiHybrids.brCactusMattock, "Baked Cactus Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.brCactusMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Block.cactus, Character.valueOf('|'), mod_InfiTools.brickRod
                });
        mod_InfiHybrids.wFlintMattock.setIconCoord(13, 2);
        ModLoader.addName(mod_InfiHybrids.wFlintMattock, "Flint Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.wFlintMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Item.flint, Character.valueOf('|'), Item.stick
                });
        mod_InfiHybrids.stFlintMattock.setIconCoord(14, 2);
        ModLoader.addName(mod_InfiHybrids.stFlintMattock, "Stony Flint Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.stFlintMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_InfiHybrids.iFlintMattock.setIconCoord(15, 2);
        ModLoader.addName(mod_InfiHybrids.iFlintMattock, "Hard Flint Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.iFlintMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.ironRod
                });
        mod_InfiHybrids.gFlintMattock.setIconCoord(12, 3);
        ModLoader.addName(mod_InfiHybrids.gFlintMattock, "Expensive Flint Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.gFlintMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.goldRod
                });
        mod_InfiHybrids.oFlintMattock.setIconCoord(13, 3);
        ModLoader.addName(mod_InfiHybrids.oFlintMattock, "Glassy Flint Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.oFlintMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.obsidianRod
                });
        mod_InfiHybrids.saFlintMattock.setIconCoord(14, 3);
        ModLoader.addName(mod_InfiHybrids.saFlintMattock, "Sandy Flint Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.saFlintMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.sandstoneRod
                });
        mod_InfiHybrids.bFlintMattock.setIconCoord(15, 3);
        ModLoader.addName(mod_InfiHybrids.bFlintMattock, "Necrotic Flint Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.bFlintMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Item.flint, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.bFlintMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_InfiHybrids.nFlintMattock.setIconCoord(12, 4);
        ModLoader.addName(mod_InfiHybrids.nFlintMattock, "Bloody Flint Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.nFlintMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_InfiHybrids.iceFlintMattock.setIconCoord(13, 4);
        ModLoader.addName(mod_InfiHybrids.iceFlintMattock, "Icy Flint Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.iceFlintMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.iceRod
                });
        mod_InfiHybrids.sFlintMattock.setIconCoord(14, 4);
        ModLoader.addName(mod_InfiHybrids.sFlintMattock, "Slimy Flint Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.sFlintMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_InfiHybrids.cFlintMattock.setIconCoord(15, 4);
        ModLoader.addName(mod_InfiHybrids.cFlintMattock, "Spiny Flint Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.cFlintMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_InfiHybrids.fFlintMattock.setIconCoord(11, 5);
        ModLoader.addName(mod_InfiHybrids.fFlintMattock, "Shale Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.fFlintMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.flintRod
                });
        mod_InfiHybrids.brFlintMattock.setIconCoord(12, 5);
        ModLoader.addName(mod_InfiHybrids.brFlintMattock, "Baked Flint Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.brFlintMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.brickRod
                });
        mod_InfiHybrids.blFlintMattock.setIconCoord(13, 5);
        ModLoader.addName(mod_InfiHybrids.blFlintMattock, "Blazing Flint Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.blFlintMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Item.flint, Character.valueOf('|'), Item.blazeRod
                });
        mod_InfiHybrids.wBrickMattock.setIconCoord(14, 5);
        ModLoader.addName(mod_InfiHybrids.wBrickMattock, "Brick Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.wBrickMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Item.brick, Character.valueOf('|'), Item.brick
                });
        mod_InfiHybrids.stBrickMattock.setIconCoord(15, 5);
        ModLoader.addName(mod_InfiHybrids.stBrickMattock, "Stony Brick Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.stBrickMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Item.brick, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_InfiHybrids.saBrickMattock.setIconCoord(13, 6);
        ModLoader.addName(mod_InfiHybrids.saBrickMattock, "Sandstone Brick Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.saBrickMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Item.brick, Character.valueOf('|'), mod_InfiTools.sandstoneRod
                });
        mod_InfiHybrids.bBrickMattock.setIconCoord(14, 6);
        ModLoader.addName(mod_InfiHybrids.bBrickMattock, "Necrotic Brick Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.bBrickMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Item.brick, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.bBrickMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Item.brick, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_InfiHybrids.pBrickMattock.setIconCoord(15, 6);
        ModLoader.addName(mod_InfiHybrids.pBrickMattock, "Fibery Brick Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.pBrickMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Item.brick, Character.valueOf('|'), mod_InfiTools.paperRod
                });
        mod_InfiHybrids.nBrickMattock.setIconCoord(11, 7);
        ModLoader.addName(mod_InfiHybrids.nBrickMattock, "Bloody Brick Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.nBrickMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Item.brick, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_InfiHybrids.iceBrickMattock.setIconCoord(12, 7);
        ModLoader.addName(mod_InfiHybrids.iceBrickMattock, "Icy Brick Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.iceBrickMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Item.brick, Character.valueOf('|'), mod_InfiTools.iceRod
                });
        mod_InfiHybrids.sBrickMattock.setIconCoord(13, 7);
        ModLoader.addName(mod_InfiHybrids.sBrickMattock, "Slimy Brick Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.sBrickMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Item.brick, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_InfiHybrids.cBrickMattock.setIconCoord(14, 7);
        ModLoader.addName(mod_InfiHybrids.cBrickMattock, "Spiny Brick Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.cBrickMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Item.brick, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_InfiHybrids.fBrickMattock.setIconCoord(15, 7);
        ModLoader.addName(mod_InfiHybrids.fBrickMattock, "Flaky Brick Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.fBrickMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Item.brick, Character.valueOf('|'), mod_InfiTools.flintRod
                });
        mod_InfiHybrids.brBrickMattock.setIconCoord(15, 8);
        ModLoader.addName(mod_InfiHybrids.brBrickMattock, "Look-alike Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.brBrickMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), Item.brick, Character.valueOf('|'), mod_InfiTools.brickRod
                });
        mod_InfiHybrids.dBlazeMattock.setIconCoord(7, 9);
        ModLoader.addName(mod_InfiHybrids.dBlazeMattock, "Jeweled Blaze Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.dBlazeMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), mod_InfiTools.blazeCrystal, Character.valueOf('|'), mod_InfiTools.diamondRod
                });
        mod_InfiHybrids.rBlazeMattock.setIconCoord(8, 9);
        ModLoader.addName(mod_InfiHybrids.rBlazeMattock, "Red Blaze Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.rBlazeMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), mod_InfiTools.blazeCrystal, Character.valueOf('|'), mod_InfiTools.redstoneRod
                });
        mod_InfiHybrids.bBlazeMattock.setIconCoord(9, 9);
        ModLoader.addName(mod_InfiHybrids.bBlazeMattock, "Necrotic Blaze Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.bBlazeMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), mod_InfiTools.blazeCrystal, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.bBlazeMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), mod_InfiTools.blazeCrystal, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_InfiHybrids.nBlazeMattock.setIconCoord(10, 9);
        ModLoader.addName(mod_InfiHybrids.nBlazeMattock, "Bloody Blaze Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.nBlazeMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), mod_InfiTools.blazeCrystal, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_InfiHybrids.glBlazeMattock.setIconCoord(11, 9);
        ModLoader.addName(mod_InfiHybrids.glBlazeMattock, "Glowing Blaze Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.glBlazeMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), mod_InfiTools.blazeCrystal, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_InfiHybrids.lBlazeMattock.setIconCoord(12, 9);
        ModLoader.addName(mod_InfiHybrids.lBlazeMattock, "Fiery Blaze Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.lBlazeMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), mod_InfiTools.blazeCrystal, Character.valueOf('|'), Item.blazeRod
                });
        mod_InfiHybrids.fBlazeMattock.setIconCoord(13, 9);
        ModLoader.addName(mod_InfiHybrids.fBlazeMattock, "Flaky Blaze Mattock");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.fBlazeMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), mod_InfiTools.blazeCrystal, Character.valueOf('|'), Item.blazeRod
                });
        mod_InfiHybrids.blBlazeMattock.setIconCoord(14, 9);
        ModLoader.addName(mod_InfiHybrids.blBlazeMattock, "Otherworldly Multi-tool");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.blBlazeMattock, 1), new Object[]
                {
                    "mmm", "m| ", " | ", Character.valueOf('m'), mod_InfiTools.blazeCrystal, Character.valueOf('|'), Item.blazeRod
                });
        return 0;
    }
}
