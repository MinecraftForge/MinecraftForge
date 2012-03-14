package net.minecraft.src.hybrids;

import net.minecraft.src.forge.MinecraftForge;
import net.minecraft.src.*;

public class InfiRecipeHammers
{
    public InfiRecipeHammers()
    {
    }

    public static int recipeStorm()
    {
        mod_InfiHybrids.wWoodHammer.setIconCoord(0, 0);
        ModLoader.addName(mod_InfiHybrids.wWoodHammer, "Wooden Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.wWoodHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Block.planks, Character.valueOf('|'), Item.stick
                });
        mod_InfiHybrids.stWoodHammer.setIconCoord(1, 0);
        ModLoader.addName(mod_InfiHybrids.stWoodHammer, "Stony Wooden Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.stWoodHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Block.planks, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_InfiHybrids.saWoodHammer.setIconCoord(2, 0);
        ModLoader.addName(mod_InfiHybrids.saWoodHammer, "Sandy Wooden Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.saWoodHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Block.planks, Character.valueOf('|'), mod_InfiTools.sandstoneRod
                });
        mod_InfiHybrids.bWoodHammer.setIconCoord(3, 0);
        ModLoader.addName(mod_InfiHybrids.bWoodHammer, "Necrotic Wooden Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.bWoodHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Block.planks, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.bWoodHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Block.planks, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_InfiHybrids.pWoodHammer.setIconCoord(4, 0);
        ModLoader.addName(mod_InfiHybrids.pWoodHammer, "Fibery Wooden Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.pWoodHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Block.planks, Character.valueOf('|'), mod_InfiTools.paperRod
                });
        mod_InfiHybrids.nWoodHammer.setIconCoord(5, 0);
        ModLoader.addName(mod_InfiHybrids.nWoodHammer, "Bloody Wooden Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.nWoodHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Block.planks, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_InfiHybrids.sWoodHammer.setIconCoord(6, 0);
        ModLoader.addName(mod_InfiHybrids.sWoodHammer, "Slimy Wooden Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.sWoodHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Block.planks, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_InfiHybrids.cWoodHammer.setIconCoord(7, 0);
        ModLoader.addName(mod_InfiHybrids.cWoodHammer, "Spiny Wooden Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.cWoodHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Block.planks, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_InfiHybrids.fWoodHammer.setIconCoord(8, 0);
        ModLoader.addName(mod_InfiHybrids.fWoodHammer, "Flaky Wooden Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.fWoodHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Block.planks, Character.valueOf('|'), mod_InfiTools.flintRod
                });
        mod_InfiHybrids.brWoodHammer.setIconCoord(9, 0);
        ModLoader.addName(mod_InfiHybrids.brWoodHammer, "Baked Wooden Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.brWoodHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Block.planks, Character.valueOf('|'), mod_InfiTools.brickRod
                });
        mod_InfiHybrids.wStoneHammer.setIconCoord(0, 1);
        ModLoader.addName(mod_InfiHybrids.wStoneHammer, "Stone Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.wStoneHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), Item.stick
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.wStoneHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Block.stone, Character.valueOf('|'), Item.stick
                });
        mod_InfiHybrids.stStoneHammer.setIconCoord(1, 1);
        ModLoader.addName(mod_InfiHybrids.stStoneHammer, "Heavy Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.stStoneHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.stStoneHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Block.stone, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_InfiHybrids.saStoneHammer.setIconCoord(2, 1);
        ModLoader.addName(mod_InfiHybrids.saStoneHammer, "Sandy Stone Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.saStoneHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), mod_InfiTools.sandstoneRod
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.saStoneHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Block.stone, Character.valueOf('|'), mod_InfiTools.sandstoneRod
                });
        mod_InfiHybrids.bStoneHammer.setIconCoord(3, 1);
        ModLoader.addName(mod_InfiHybrids.bStoneHammer, "Necrotic Stone Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.bStoneHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.bStoneHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Block.stone, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.bStoneHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.bStoneHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Block.stone, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_InfiHybrids.pStoneHammer.setIconCoord(4, 1);
        ModLoader.addName(mod_InfiHybrids.pStoneHammer, "Fibery Stone Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.pStoneHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), mod_InfiTools.paperRod
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.pStoneHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Block.stone, Character.valueOf('|'), mod_InfiTools.paperRod
                });
        mod_InfiHybrids.mStoneHammer.setIconCoord(5, 1);
        ModLoader.addName(mod_InfiHybrids.mStoneHammer, "Mossy Stone Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.mStoneHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), mod_InfiTools.mossyRod
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.mStoneHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Block.stone, Character.valueOf('|'), mod_InfiTools.mossyRod
                });
        mod_InfiHybrids.nStoneHammer.setIconCoord(6, 1);
        ModLoader.addName(mod_InfiHybrids.nStoneHammer, "Bloody Stone Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.nStoneHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.nStoneHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Block.stone, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_InfiHybrids.iceStoneHammer.setIconCoord(7, 1);
        ModLoader.addName(mod_InfiHybrids.iceStoneHammer, "Icy Stone Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.iceStoneHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), mod_InfiTools.iceRod
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.iceStoneHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Block.stone, Character.valueOf('|'), mod_InfiTools.iceRod
                });
        mod_InfiHybrids.sStoneHammer.setIconCoord(8, 1);
        ModLoader.addName(mod_InfiHybrids.sStoneHammer, "Slimy Stone Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.sStoneHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.sStoneHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Block.stone, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_InfiHybrids.cStoneHammer.setIconCoord(9, 1);
        ModLoader.addName(mod_InfiHybrids.cStoneHammer, "Spiny Stone Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.cStoneHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.cStoneHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Block.stone, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_InfiHybrids.fStoneHammer.setIconCoord(10, 1);
        ModLoader.addName(mod_InfiHybrids.fStoneHammer, "Flaky Stone Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.fStoneHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), mod_InfiTools.flintRod
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.fStoneHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Block.stone, Character.valueOf('|'), mod_InfiTools.flintRod
                });
        mod_InfiHybrids.brStoneHammer.setIconCoord(11, 1);
        ModLoader.addName(mod_InfiHybrids.brStoneHammer, "Baked Stone Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.brStoneHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), mod_InfiTools.brickRod
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.brStoneHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Block.stone, Character.valueOf('|'), mod_InfiTools.brickRod
                });
        mod_InfiHybrids.wIronHammer.setIconCoord(0, 2);
        ModLoader.addName(mod_InfiHybrids.wIronHammer, "Iron Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.wIronHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), Item.stick
                });
        mod_InfiHybrids.stIronHammer.setIconCoord(1, 2);
        ModLoader.addName(mod_InfiHybrids.stIronHammer, "Stony Iron Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.stIronHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_InfiHybrids.iIronHammer.setIconCoord(2, 2);
        ModLoader.addName(mod_InfiHybrids.iIronHammer, "Ironic Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.iIronHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), mod_InfiTools.ironRod
                });
        mod_InfiHybrids.dIronHammer.setIconCoord(3, 2);
        ModLoader.addName(mod_InfiHybrids.dIronHammer, "Jeweled Iron Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.dIronHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), mod_InfiTools.diamondRod
                });
        mod_InfiHybrids.gIronHammer.setIconCoord(4, 2);
        ModLoader.addName(mod_InfiHybrids.gIronHammer, "Expensive Iron Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.gIronHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), mod_InfiTools.goldRod
                });
        mod_InfiHybrids.rIronHammer.setIconCoord(5, 2);
        ModLoader.addName(mod_InfiHybrids.rIronHammer, "Red Iron Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.rIronHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), mod_InfiTools.redstoneRod
                });
        mod_InfiHybrids.oIronHammer.setIconCoord(6, 2);
        ModLoader.addName(mod_InfiHybrids.oIronHammer, "Glassy Iron Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.oIronHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), mod_InfiTools.obsidianRod
                });
        mod_InfiHybrids.bIronHammer.setIconCoord(7, 2);
        ModLoader.addName(mod_InfiHybrids.bIronHammer, "Necrotic Iron Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.bIronHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.bIronHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_InfiHybrids.nIronHammer.setIconCoord(8, 2);
        ModLoader.addName(mod_InfiHybrids.nIronHammer, "Bloody Iron Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.nIronHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_InfiHybrids.glIronHammer.setIconCoord(9, 2);
        ModLoader.addName(mod_InfiHybrids.glIronHammer, "Glowing Iron Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.glIronHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_InfiHybrids.iceIronHammer.setIconCoord(10, 2);
        ModLoader.addName(mod_InfiHybrids.iceIronHammer, "Icy Iron Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.iceIronHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), mod_InfiTools.iceRod
                });
        mod_InfiHybrids.sIronHammer.setIconCoord(11, 2);
        ModLoader.addName(mod_InfiHybrids.sIronHammer, "Slimy Iron Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.sIronHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_InfiHybrids.blIronHammer.setIconCoord(12, 2);
        ModLoader.addName(mod_InfiHybrids.blIronHammer, "Blazing Iron Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.blIronHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), Item.blazeRod
                });
        mod_InfiHybrids.wDiamondHammer.setIconCoord(0, 3);
        ModLoader.addName(mod_InfiHybrids.wDiamondHammer, "Diamond Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.wDiamondHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), Item.stick
                });
        mod_InfiHybrids.stDiamondHammer.setIconCoord(1, 3);
        ModLoader.addName(mod_InfiHybrids.stDiamondHammer, "Stony Diamond Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.stDiamondHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_InfiHybrids.iDiamondHammer.setIconCoord(2, 3);
        ModLoader.addName(mod_InfiHybrids.iDiamondHammer, "Hard Diamond Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.iDiamondHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), mod_InfiTools.ironRod
                });
        mod_InfiHybrids.dDiamondHammer.setIconCoord(3, 3);
        ModLoader.addName(mod_InfiHybrids.dDiamondHammer, "Diamondium Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.dDiamondHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), mod_InfiTools.diamondRod
                });
        mod_InfiHybrids.gDiamondHammer.setIconCoord(4, 3);
        ModLoader.addName(mod_InfiHybrids.gDiamondHammer, "Expensive Diamond Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.gDiamondHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), mod_InfiTools.goldRod
                });
        mod_InfiHybrids.rDiamondHammer.setIconCoord(5, 3);
        ModLoader.addName(mod_InfiHybrids.rDiamondHammer, "Red Diamond Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.rDiamondHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), mod_InfiTools.redstoneRod
                });
        mod_InfiHybrids.oDiamondHammer.setIconCoord(6, 3);
        ModLoader.addName(mod_InfiHybrids.oDiamondHammer, "Glassy Diamond Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.oDiamondHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), mod_InfiTools.obsidianRod
                });
        mod_InfiHybrids.bDiamondHammer.setIconCoord(7, 3);
        ModLoader.addName(mod_InfiHybrids.bDiamondHammer, "Necrotic Diamond Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.bDiamondHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.bDiamondHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_InfiHybrids.mDiamondHammer.setIconCoord(8, 3);
        ModLoader.addName(mod_InfiHybrids.mDiamondHammer, "Mossy Diamond Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.mDiamondHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), mod_InfiTools.mossyRod
                });
        mod_InfiHybrids.nDiamondHammer.setIconCoord(9, 3);
        ModLoader.addName(mod_InfiHybrids.nDiamondHammer, "Bloody Diamond Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.nDiamondHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_InfiHybrids.glDiamondHammer.setIconCoord(10, 3);
        ModLoader.addName(mod_InfiHybrids.glDiamondHammer, "Glowing Diamond Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.glDiamondHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_InfiHybrids.blDiamondHammer.setIconCoord(11, 3);
        ModLoader.addName(mod_InfiHybrids.blDiamondHammer, "Blazing Diamond Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.blDiamondHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), Item.blazeRod
                });
        mod_InfiHybrids.wGoldHammer.setIconCoord(0, 4);
        ModLoader.addName(mod_InfiHybrids.wGoldHammer, "Stony Gold Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.wGoldHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), Item.stick
                });
        mod_InfiHybrids.stGoldHammer.setIconCoord(1, 4);
        ModLoader.addName(mod_InfiHybrids.stGoldHammer, "Stony Gold Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.stGoldHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_InfiHybrids.gGoldHammer.setIconCoord(2, 4);
        ModLoader.addName(mod_InfiHybrids.gGoldHammer, "Expensive Useless Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.gGoldHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), mod_InfiTools.goldRod
                });
        mod_InfiHybrids.oGoldHammer.setIconCoord(3, 4);
        ModLoader.addName(mod_InfiHybrids.oGoldHammer, "Glassy Gold Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.oGoldHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), mod_InfiTools.obsidianRod
                });
        mod_InfiHybrids.saGoldHammer.setIconCoord(4, 4);
        ModLoader.addName(mod_InfiHybrids.saGoldHammer, "Sandy Gold Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.saGoldHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), mod_InfiTools.sandstoneRod
                });
        mod_InfiHybrids.bGoldHammer.setIconCoord(5, 4);
        ModLoader.addName(mod_InfiHybrids.bGoldHammer, "Necrotic Gold Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.bGoldHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.bGoldHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_InfiHybrids.mGoldHammer.setIconCoord(6, 4);
        ModLoader.addName(mod_InfiHybrids.mGoldHammer, "Mossy Gold Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.mGoldHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), mod_InfiTools.mossyRod
                });
        mod_InfiHybrids.nGoldHammer.setIconCoord(7, 4);
        ModLoader.addName(mod_InfiHybrids.nGoldHammer, "Bloody Gold Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.nGoldHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_InfiHybrids.glGoldHammer.setIconCoord(8, 4);
        ModLoader.addName(mod_InfiHybrids.glGoldHammer, "Glowing Gold Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.glGoldHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_InfiHybrids.iceGoldHammer.setIconCoord(9, 4);
        ModLoader.addName(mod_InfiHybrids.iceGoldHammer, "Icy Gold Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.iceGoldHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), mod_InfiTools.iceRod
                });
        mod_InfiHybrids.sGoldHammer.setIconCoord(10, 4);
        ModLoader.addName(mod_InfiHybrids.sGoldHammer, "Slimy Gold Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.sGoldHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_InfiHybrids.fGoldHammer.setIconCoord(11, 4);
        ModLoader.addName(mod_InfiHybrids.fGoldHammer, "Flaky Gold Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.fGoldHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), mod_InfiTools.flintRod
                });
        mod_InfiHybrids.wRedstoneHammer.setIconCoord(0, 5);
        ModLoader.addName(mod_InfiHybrids.wRedstoneHammer, "Redstone Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.wRedstoneHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), Item.stick
                });
        mod_InfiHybrids.stRedstoneHammer.setIconCoord(1, 5);
        ModLoader.addName(mod_InfiHybrids.stRedstoneHammer, "Stony Redstone Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.stRedstoneHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_InfiHybrids.iRedstoneHammer.setIconCoord(2, 5);
        ModLoader.addName(mod_InfiHybrids.iRedstoneHammer, "Hard Redstone Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.iRedstoneHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), mod_InfiTools.ironRod
                });
        mod_InfiHybrids.dRedstoneHammer.setIconCoord(3, 5);
        ModLoader.addName(mod_InfiHybrids.dRedstoneHammer, "Jeweled Redstone Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.dRedstoneHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), mod_InfiTools.diamondRod
                });
        mod_InfiHybrids.rRedstoneHammer.setIconCoord(4, 5);
        ModLoader.addName(mod_InfiHybrids.rRedstoneHammer, "Redredred Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.rRedstoneHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), mod_InfiTools.redstoneRod
                });
        mod_InfiHybrids.oRedstoneHammer.setIconCoord(5, 5);
        ModLoader.addName(mod_InfiHybrids.oRedstoneHammer, "Glassy Redstone Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.oRedstoneHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), mod_InfiTools.obsidianRod
                });
        mod_InfiHybrids.bRedstoneHammer.setIconCoord(6, 5);
        ModLoader.addName(mod_InfiHybrids.bRedstoneHammer, "Necrotic Redstone Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.bRedstoneHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.bRedstoneHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_InfiHybrids.mRedstoneHammer.setIconCoord(7, 5);
        ModLoader.addName(mod_InfiHybrids.mRedstoneHammer, "Mossy Redstone Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.mRedstoneHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), mod_InfiTools.mossyRod
                });
        mod_InfiHybrids.glRedstoneHammer.setIconCoord(8, 5);
        ModLoader.addName(mod_InfiHybrids.glRedstoneHammer, "Glowing Redstone Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.glRedstoneHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_InfiHybrids.sRedstoneHammer.setIconCoord(9, 5);
        ModLoader.addName(mod_InfiHybrids.sRedstoneHammer, "Slimy Redstone Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.sRedstoneHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_InfiHybrids.blRedstoneHammer.setIconCoord(10, 5);
        ModLoader.addName(mod_InfiHybrids.blRedstoneHammer, "Blazing Redstone Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.blRedstoneHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), Item.blazeRod
                });
        mod_InfiHybrids.wObsidianHammer.setIconCoord(0, 6);
        ModLoader.addName(mod_InfiHybrids.wObsidianHammer, "Obsidian Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.wObsidianHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), Item.stick
                });
        mod_InfiHybrids.stObsidianHammer.setIconCoord(1, 6);
        ModLoader.addName(mod_InfiHybrids.stObsidianHammer, "Stony Obsidian Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.stObsidianHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_InfiHybrids.iObsidianHammer.setIconCoord(2, 6);
        ModLoader.addName(mod_InfiHybrids.iObsidianHammer, "Hard Obsidian Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.iObsidianHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), mod_InfiTools.ironRod
                });
        mod_InfiHybrids.dObsidianHammer.setIconCoord(3, 6);
        ModLoader.addName(mod_InfiHybrids.dObsidianHammer, "Jeweled Obsidian Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.dObsidianHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), mod_InfiTools.diamondRod
                });
        mod_InfiHybrids.gObsidianHammer.setIconCoord(4, 6);
        ModLoader.addName(mod_InfiHybrids.gObsidianHammer, "Expensive Obsidian Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.gObsidianHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), mod_InfiTools.goldRod
                });
        mod_InfiHybrids.rObsidianHammer.setIconCoord(5, 6);
        ModLoader.addName(mod_InfiHybrids.rObsidianHammer, "Red Obsidian Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.rObsidianHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), mod_InfiTools.redstoneRod
                });
        mod_InfiHybrids.oObsidianHammer.setIconCoord(6, 6);
        ModLoader.addName(mod_InfiHybrids.oObsidianHammer, "Wicked Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.oObsidianHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), mod_InfiTools.obsidianRod
                });
        mod_InfiHybrids.bObsidianHammer.setIconCoord(7, 6);
        ModLoader.addName(mod_InfiHybrids.bObsidianHammer, "Necrotic Obsidian Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.bObsidianHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.bObsidianHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_InfiHybrids.nObsidianHammer.setIconCoord(8, 6);
        ModLoader.addName(mod_InfiHybrids.nObsidianHammer, "Bloody Obsidian Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.nObsidianHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_InfiHybrids.glObsidianHammer.setIconCoord(9, 6);
        ModLoader.addName(mod_InfiHybrids.glObsidianHammer, "Glowing Obsidian Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.glObsidianHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_InfiHybrids.sObsidianHammer.setIconCoord(10, 6);
        ModLoader.addName(mod_InfiHybrids.sObsidianHammer, "Slimy Obsidian Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.sObsidianHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_InfiHybrids.fObsidianHammer.setIconCoord(11, 6);
        ModLoader.addName(mod_InfiHybrids.fObsidianHammer, "Flaky Obsidian Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.fObsidianHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), mod_InfiTools.flintRod
                });
        mod_InfiHybrids.blObsidianHammer.setIconCoord(12, 6);
        ModLoader.addName(mod_InfiHybrids.blObsidianHammer, "Blazing Obsidian Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.blObsidianHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), Item.blazeRod
                });
        mod_InfiHybrids.wSandstoneHammer.setIconCoord(0, 7);
        ModLoader.addName(mod_InfiHybrids.wSandstoneHammer, "Sandstone Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.wSandstoneHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), Item.stick
                });
        mod_InfiHybrids.stSandstoneHammer.setIconCoord(1, 7);
        ModLoader.addName(mod_InfiHybrids.stSandstoneHammer, "Stony Sandstone Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.stSandstoneHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_InfiHybrids.saSandstoneHammer.setIconCoord(2, 7);
        ModLoader.addName(mod_InfiHybrids.saSandstoneHammer, "Sandpound");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.saSandstoneHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), mod_InfiTools.sandstoneRod
                });
        mod_InfiHybrids.bSandstoneHammer.setIconCoord(3, 7);
        ModLoader.addName(mod_InfiHybrids.bSandstoneHammer, "Necrotic Sandstone Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.bSandstoneHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.bSandstoneHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_InfiHybrids.pSandstoneHammer.setIconCoord(4, 7);
        ModLoader.addName(mod_InfiHybrids.pSandstoneHammer, "Fibery Sandstone Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.pSandstoneHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), mod_InfiTools.paperRod
                });
        mod_InfiHybrids.nSandstoneHammer.setIconCoord(5, 7);
        ModLoader.addName(mod_InfiHybrids.nSandstoneHammer, "Bloody Sandstone Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.nSandstoneHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_InfiHybrids.iceSandstoneHammer.setIconCoord(6, 7);
        ModLoader.addName(mod_InfiHybrids.iceSandstoneHammer, "Icy Sandstone Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.iceSandstoneHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), mod_InfiTools.iceRod
                });
        mod_InfiHybrids.sSandstoneHammer.setIconCoord(7, 7);
        ModLoader.addName(mod_InfiHybrids.sSandstoneHammer, "Slimy Sandstone Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.sSandstoneHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_InfiHybrids.cSandstoneHammer.setIconCoord(8, 7);
        ModLoader.addName(mod_InfiHybrids.cSandstoneHammer, "Spiny Sandstone Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.cSandstoneHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_InfiHybrids.fSandstoneHammer.setIconCoord(9, 7);
        ModLoader.addName(mod_InfiHybrids.fSandstoneHammer, "Flaky Sandstone Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.fSandstoneHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), mod_InfiTools.flintRod
                });
        mod_InfiHybrids.brSandstoneHammer.setIconCoord(10, 7);
        ModLoader.addName(mod_InfiHybrids.brSandstoneHammer, "Baked Sandstone Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.brSandstoneHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), mod_InfiTools.brickRod
                });
        mod_InfiHybrids.wBoneHammer.setIconCoord(0, 8);
        ModLoader.addName(mod_InfiHybrids.wBoneHammer, "Bone Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.wBoneHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Item.bone, Character.valueOf('|'), Item.stick
                });
        mod_InfiHybrids.stBoneHammer.setIconCoord(1, 8);
        ModLoader.addName(mod_InfiHybrids.stBoneHammer, "Stony Bone Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.stBoneHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_InfiHybrids.iBoneHammer.setIconCoord(2, 8);
        ModLoader.addName(mod_InfiHybrids.iBoneHammer, "Hard Bone Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.iBoneHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.ironRod
                });
        mod_InfiHybrids.dBoneHammer.setIconCoord(3, 8);
        ModLoader.addName(mod_InfiHybrids.dBoneHammer, "Jeweled Bone Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.dBoneHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.diamondRod
                });
        mod_InfiHybrids.rBoneHammer.setIconCoord(4, 8);
        ModLoader.addName(mod_InfiHybrids.rBoneHammer, "Red Bone Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.rBoneHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.redstoneRod
                });
        mod_InfiHybrids.oBoneHammer.setIconCoord(5, 8);
        ModLoader.addName(mod_InfiHybrids.oBoneHammer, "Glassy Bone Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.oBoneHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.obsidianRod
                });
        mod_InfiHybrids.bBoneHammer.setIconCoord(6, 8);
        ModLoader.addName(mod_InfiHybrids.bBoneHammer, "Reanimated Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.bBoneHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Item.bone, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.bBoneHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_InfiHybrids.mBoneHammer.setIconCoord(7, 8);
        ModLoader.addName(mod_InfiHybrids.mBoneHammer, "Mossy Bone Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.mBoneHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.mossyRod
                });
        mod_InfiHybrids.nBoneHammer.setIconCoord(8, 8);
        ModLoader.addName(mod_InfiHybrids.nBoneHammer, "Netherrack Bone Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.nBoneHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_InfiHybrids.glBoneHammer.setIconCoord(9, 8);
        ModLoader.addName(mod_InfiHybrids.glBoneHammer, "Glowstone Bone Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.glBoneHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_InfiHybrids.sBoneHammer.setIconCoord(10, 8);
        ModLoader.addName(mod_InfiHybrids.sBoneHammer, "Slimy Bone Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.sBoneHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_InfiHybrids.cBoneHammer.setIconCoord(11, 8);
        ModLoader.addName(mod_InfiHybrids.cBoneHammer, "Spiny Bone Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.cBoneHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_InfiHybrids.fBoneHammer.setIconCoord(12, 8);
        ModLoader.addName(mod_InfiHybrids.fBoneHammer, "Flaky Bone Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.fBoneHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.flintRod
                });
        mod_InfiHybrids.brBoneHammer.setIconCoord(13, 8);
        ModLoader.addName(mod_InfiHybrids.brBoneHammer, "Baked Bone Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.brBoneHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.brickRod
                });
        mod_InfiHybrids.blBoneHammer.setIconCoord(14, 8);
        ModLoader.addName(mod_InfiHybrids.blBoneHammer, "Blazing Bone Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.blBoneHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Item.bone, Character.valueOf('|'), Item.blazeRod
                });
        mod_InfiHybrids.wPaperHammer.setIconCoord(0, 9);
        ModLoader.addName(mod_InfiHybrids.wPaperHammer, "Paper Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.wPaperHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), mod_InfiTools.paperStack, Character.valueOf('|'), Item.stick
                });
        mod_InfiHybrids.saPaperHammer.setIconCoord(1, 9);
        ModLoader.addName(mod_InfiHybrids.saPaperHammer, "Stony Paper Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.saPaperHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), mod_InfiTools.paperStack, Character.valueOf('|'), mod_InfiTools.sandstoneRod
                });
        mod_InfiHybrids.bPaperHammer.setIconCoord(2, 9);
        ModLoader.addName(mod_InfiHybrids.bPaperHammer, "Necrotic Paper Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.bPaperHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), mod_InfiTools.paperStack, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.bPaperHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), mod_InfiTools.paperStack, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_InfiHybrids.pPaperHammer.setIconCoord(3, 9);
        ModLoader.addName(mod_InfiHybrids.pPaperHammer, "Paper Mache");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.pPaperHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), mod_InfiTools.paperStack, Character.valueOf('|'), mod_InfiTools.paperRod
                });
        mod_InfiHybrids.sPaperHammer.setIconCoord(4, 9);
        ModLoader.addName(mod_InfiHybrids.sPaperHammer, "Slimy Paper Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.sPaperHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), mod_InfiTools.paperStack, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_InfiHybrids.cPaperHammer.setIconCoord(5, 9);
        ModLoader.addName(mod_InfiHybrids.cPaperHammer, "Spiny Paper Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.cPaperHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), mod_InfiTools.paperStack, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_InfiHybrids.brPaperHammer.setIconCoord(6, 9);
        ModLoader.addName(mod_InfiHybrids.brPaperHammer, "Baked Paper Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.brPaperHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), mod_InfiTools.paperStack, Character.valueOf('|'), mod_InfiTools.brickRod
                });
        mod_InfiHybrids.stMossyHammer.setIconCoord(0, 10);
        ModLoader.addName(mod_InfiHybrids.stMossyHammer, "Stony Moss-Covered Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.stMossyHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), mod_InfiTools.mossBallCrafted, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_InfiHybrids.dMossyHammer.setIconCoord(1, 10);
        ModLoader.addName(mod_InfiHybrids.dMossyHammer, "Jeweled Moss-Covered Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.dMossyHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), mod_InfiTools.mossBallCrafted, Character.valueOf('|'), mod_InfiTools.diamondRod
                });
        mod_InfiHybrids.rMossyHammer.setIconCoord(2, 10);
        ModLoader.addName(mod_InfiHybrids.rMossyHammer, "Red Moss-Covered Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.rMossyHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), mod_InfiTools.mossBallCrafted, Character.valueOf('|'), mod_InfiTools.redstoneRod
                });
        mod_InfiHybrids.bMossyHammer.setIconCoord(3, 10);
        ModLoader.addName(mod_InfiHybrids.bMossyHammer, "Necrotic Moss-Covered Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.bMossyHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), mod_InfiTools.mossBallCrafted, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.bMossyHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), mod_InfiTools.mossBallCrafted, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_InfiHybrids.mMossyHammer.setIconCoord(4, 10);
        ModLoader.addName(mod_InfiHybrids.mMossyHammer, "Living Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.mMossyHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), mod_InfiTools.mossBallCrafted, Character.valueOf('|'), mod_InfiTools.mossyRod
                });
        mod_InfiHybrids.glMossyHammer.setIconCoord(5, 10);
        ModLoader.addName(mod_InfiHybrids.glMossyHammer, "Glowing Moss-Covered Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.glMossyHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), mod_InfiTools.mossBallCrafted, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_InfiHybrids.wNetherrackHammer.setIconCoord(0, 11);
        ModLoader.addName(mod_InfiHybrids.wNetherrackHammer, "Netherrack Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.wNetherrackHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), Item.stick
                });
        mod_InfiHybrids.stNetherrackHammer.setIconCoord(1, 11);
        ModLoader.addName(mod_InfiHybrids.stNetherrackHammer, "Stony Netherrack Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.stNetherrackHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_InfiHybrids.iNetherrackHammer.setIconCoord(2, 11);
        ModLoader.addName(mod_InfiHybrids.iNetherrackHammer, "Hard Netherrack Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.iNetherrackHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.ironRod
                });
        mod_InfiHybrids.rNetherrackHammer.setIconCoord(3, 11);
        ModLoader.addName(mod_InfiHybrids.rNetherrackHammer, "Red Netherrack Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.rNetherrackHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.redstoneRod
                });
        mod_InfiHybrids.oNetherrackHammer.setIconCoord(4, 11);
        ModLoader.addName(mod_InfiHybrids.oNetherrackHammer, "Glassy Netherrack Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.oNetherrackHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.obsidianRod
                });
        mod_InfiHybrids.saNetherrackHammer.setIconCoord(5, 11);
        ModLoader.addName(mod_InfiHybrids.saNetherrackHammer, "Sandy Netherrack Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.saNetherrackHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.sandstoneRod
                });
        mod_InfiHybrids.bNetherrackHammer.setIconCoord(6, 11);
        ModLoader.addName(mod_InfiHybrids.bNetherrackHammer, "Necrotic Netherrack Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.bNetherrackHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.bNetherrackHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_InfiHybrids.mNetherrackHammer.setIconCoord(7, 11);
        ModLoader.addName(mod_InfiHybrids.mNetherrackHammer, "Mossy Netherrack Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.mNetherrackHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.mossyRod
                });
        mod_InfiHybrids.nNetherrackHammer.setIconCoord(8, 11);
        ModLoader.addName(mod_InfiHybrids.nNetherrackHammer, "Meat Mallet");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.nNetherrackHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_InfiHybrids.glNetherrackHammer.setIconCoord(9, 11);
        ModLoader.addName(mod_InfiHybrids.glNetherrackHammer, "Glowing Netherrack Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.glNetherrackHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_InfiHybrids.iceNetherrackHammer.setIconCoord(10, 11);
        ModLoader.addName(mod_InfiHybrids.iceNetherrackHammer, "Icy Netherrack Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.iceNetherrackHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.iceRod
                });
        mod_InfiHybrids.sNetherrackHammer.setIconCoord(11, 11);
        ModLoader.addName(mod_InfiHybrids.sNetherrackHammer, "Slimy Netherrack Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.sNetherrackHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_InfiHybrids.cNetherrackHammer.setIconCoord(12, 11);
        ModLoader.addName(mod_InfiHybrids.cNetherrackHammer, "Spiny Netherrack Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.cNetherrackHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_InfiHybrids.fNetherrackHammer.setIconCoord(13, 11);
        ModLoader.addName(mod_InfiHybrids.fNetherrackHammer, "Flaky Netherrack Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.fNetherrackHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_InfiHybrids.brNetherrackHammer.setIconCoord(14, 11);
        ModLoader.addName(mod_InfiHybrids.brNetherrackHammer, "Baked Netherrack Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.brNetherrackHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_InfiHybrids.blNetherrackHammer.setIconCoord(15, 11);
        ModLoader.addName(mod_InfiHybrids.blNetherrackHammer, "Blazing Netherrack Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.blNetherrackHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_InfiHybrids.wGlowstoneHammer.setIconCoord(0, 12);
        ModLoader.addName(mod_InfiHybrids.wGlowstoneHammer, "Glowstone Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.wGlowstoneHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), Item.stick
                });
        mod_InfiHybrids.stGlowstoneHammer.setIconCoord(1, 12);
        ModLoader.addName(mod_InfiHybrids.stGlowstoneHammer, "Stony Glowstone Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.stGlowstoneHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_InfiHybrids.iGlowstoneHammer.setIconCoord(2, 12);
        ModLoader.addName(mod_InfiHybrids.iGlowstoneHammer, "Hard Glowstone Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.iGlowstoneHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.ironRod
                });
        mod_InfiHybrids.dGlowstoneHammer.setIconCoord(3, 12);
        ModLoader.addName(mod_InfiHybrids.dGlowstoneHammer, "Jeweled Glowstone Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.dGlowstoneHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.diamondRod
                });
        mod_InfiHybrids.rGlowstoneHammer.setIconCoord(4, 12);
        ModLoader.addName(mod_InfiHybrids.rGlowstoneHammer, "Red Glowstone Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.rGlowstoneHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.redstoneRod
                });
        mod_InfiHybrids.oGlowstoneHammer.setIconCoord(5, 12);
        ModLoader.addName(mod_InfiHybrids.oGlowstoneHammer, "Glassy Glowstone Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.oGlowstoneHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.obsidianRod
                });
        mod_InfiHybrids.bGlowstoneHammer.setIconCoord(6, 12);
        ModLoader.addName(mod_InfiHybrids.bGlowstoneHammer, "Necrotic Glowstone Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.bGlowstoneHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.bGlowstoneHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_InfiHybrids.mGlowstoneHammer.setIconCoord(7, 12);
        ModLoader.addName(mod_InfiHybrids.mGlowstoneHammer, "Mossy Glowstone Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.mGlowstoneHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.mossyRod
                });
        mod_InfiHybrids.nGlowstoneHammer.setIconCoord(8, 12);
        ModLoader.addName(mod_InfiHybrids.nGlowstoneHammer, "Bloody Glowstone Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.nGlowstoneHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_InfiHybrids.glGlowstoneHammer.setIconCoord(9, 12);
        ModLoader.addName(mod_InfiHybrids.glGlowstoneHammer, "Bright Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.glGlowstoneHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_InfiHybrids.iceGlowstoneHammer.setIconCoord(10, 12);
        ModLoader.addName(mod_InfiHybrids.iceGlowstoneHammer, "Icy Glowstone Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.iceGlowstoneHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.iceRod
                });
        mod_InfiHybrids.lGlowstoneHammer.setIconCoord(11, 12);
        ModLoader.addName(mod_InfiHybrids.lGlowstoneHammer, "Fiery Glowstone Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.lGlowstoneHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_InfiHybrids.sGlowstoneHammer.setIconCoord(12, 12);
        ModLoader.addName(mod_InfiHybrids.sGlowstoneHammer, "Slimy Glowstone Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.sGlowstoneHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_InfiHybrids.blGlowstoneHammer.setIconCoord(13, 12);
        ModLoader.addName(mod_InfiHybrids.blGlowstoneHammer, "Blazing Glowstone Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.blGlowstoneHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), Item.blazeRod
                });
        mod_InfiHybrids.wIceHammer.setIconCoord(0, 13);
        ModLoader.addName(mod_InfiHybrids.wIceHammer, "Ice Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.wIceHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Block.ice, Character.valueOf('|'), Item.stick
                });
        mod_InfiHybrids.stIceHammer.setIconCoord(1, 13);
        ModLoader.addName(mod_InfiHybrids.stIceHammer, "Stony Ice Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.stIceHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_InfiHybrids.iIceHammer.setIconCoord(2, 13);
        ModLoader.addName(mod_InfiHybrids.iIceHammer, "Hard Ice Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.iIceHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.ironRod
                });
        mod_InfiHybrids.dIceHammer.setIconCoord(3, 13);
        ModLoader.addName(mod_InfiHybrids.dIceHammer, "Jeweled Ice Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.dIceHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.diamondRod
                });
        mod_InfiHybrids.gIceHammer.setIconCoord(4, 13);
        ModLoader.addName(mod_InfiHybrids.gIceHammer, "Expensive Ice Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.gIceHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.goldRod
                });
        mod_InfiHybrids.rIceHammer.setIconCoord(5, 13);
        ModLoader.addName(mod_InfiHybrids.rIceHammer, "Red Ice Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.rIceHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.redstoneRod
                });
        mod_InfiHybrids.oIceHammer.setIconCoord(6, 13);
        ModLoader.addName(mod_InfiHybrids.oIceHammer, "Glassy Ice Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.oIceHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.obsidianRod
                });
        mod_InfiHybrids.saIceHammer.setIconCoord(7, 13);
        ModLoader.addName(mod_InfiHybrids.saIceHammer, "Sandy Ice Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.saIceHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.sandstoneRod
                });
        mod_InfiHybrids.bIceHammer.setIconCoord(8, 13);
        ModLoader.addName(mod_InfiHybrids.bIceHammer, "Necrotic Ice Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.bIceHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Block.ice, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.bIceHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_InfiHybrids.glIceHammer.setIconCoord(9, 13);
        ModLoader.addName(mod_InfiHybrids.glIceHammer, "Glowing Ice Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.glIceHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_InfiHybrids.iceIceHammer.setIconCoord(10, 13);
        ModLoader.addName(mod_InfiHybrids.iceIceHammer, "Frost Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.iceIceHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.iceRod
                });
        mod_InfiHybrids.sIceHammer.setIconCoord(11, 13);
        ModLoader.addName(mod_InfiHybrids.sIceHammer, "Slimy Ice Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.sIceHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_InfiHybrids.cIceHammer.setIconCoord(12, 13);
        ModLoader.addName(mod_InfiHybrids.cIceHammer, "Spiny Ice Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.cIceHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_InfiHybrids.fIceHammer.setIconCoord(13, 13);
        ModLoader.addName(mod_InfiHybrids.fIceHammer, "Flaky Ice Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.fIceHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.flintRod
                });
        mod_InfiHybrids.brIceHammer.setIconCoord(14, 13);
        ModLoader.addName(mod_InfiHybrids.brIceHammer, "Baked Ice Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.brIceHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.brickRod
                });
        mod_InfiHybrids.dLavaHammer.setIconCoord(0, 14);
        ModLoader.addName(mod_InfiHybrids.dLavaHammer, "Jeweled Lava Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.dLavaHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), mod_InfiTools.lavaCrystal, Character.valueOf('|'), mod_InfiTools.diamondRod
                });
        mod_InfiHybrids.rLavaHammer.setIconCoord(1, 14);
        ModLoader.addName(mod_InfiHybrids.rLavaHammer, "Red Lava Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.rLavaHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), mod_InfiTools.lavaCrystal, Character.valueOf('|'), mod_InfiTools.redstoneRod
                });
        mod_InfiHybrids.bLavaHammer.setIconCoord(2, 14);
        ModLoader.addName(mod_InfiHybrids.bLavaHammer, "Necrotic Lava Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.bLavaHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), mod_InfiTools.lavaCrystal, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.bLavaHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), mod_InfiTools.lavaCrystal, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_InfiHybrids.nLavaHammer.setIconCoord(3, 14);
        ModLoader.addName(mod_InfiHybrids.nLavaHammer, "Bloody Lava Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.nLavaHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), mod_InfiTools.lavaCrystal, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_InfiHybrids.glLavaHammer.setIconCoord(4, 14);
        ModLoader.addName(mod_InfiHybrids.glLavaHammer, "Glowing Lava Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.glLavaHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), mod_InfiTools.lavaCrystal, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_InfiHybrids.lLavaHammer.setIconCoord(5, 14);
        ModLoader.addName(mod_InfiHybrids.lLavaHammer, "Stonetorch");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.lLavaHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), mod_InfiTools.lavaCrystal, Character.valueOf('|'), mod_InfiTools.lavaRod
                });
        mod_InfiHybrids.blLavaHammer.setIconCoord(6, 14);
        ModLoader.addName(mod_InfiHybrids.blLavaHammer, "Blazing Lava Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.blLavaHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), mod_InfiTools.lavaCrystal, Character.valueOf('|'), Item.blazeRod
                });
        mod_InfiHybrids.wSlimeHammer.setIconCoord(0, 15);
        ModLoader.addName(mod_InfiHybrids.wSlimeHammer, "Slime Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.wSlimeHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), Item.stick
                });
        mod_InfiHybrids.stSlimeHammer.setIconCoord(1, 15);
        ModLoader.addName(mod_InfiHybrids.stSlimeHammer, "Stony Slime Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.stSlimeHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_InfiHybrids.iSlimeHammer.setIconCoord(2, 15);
        ModLoader.addName(mod_InfiHybrids.iSlimeHammer, "Hard Slime Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.iSlimeHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.ironRod
                });
        mod_InfiHybrids.dSlimeHammer.setIconCoord(3, 15);
        ModLoader.addName(mod_InfiHybrids.dSlimeHammer, "Jeweled Slime Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.dSlimeHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.diamondRod
                });
        mod_InfiHybrids.gSlimeHammer.setIconCoord(4, 15);
        ModLoader.addName(mod_InfiHybrids.gSlimeHammer, "Expensive Slime Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.gSlimeHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.goldRod
                });
        mod_InfiHybrids.rSlimeHammer.setIconCoord(5, 15);
        ModLoader.addName(mod_InfiHybrids.rSlimeHammer, "Red Slime Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.rSlimeHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.redstoneRod
                });
        mod_InfiHybrids.oSlimeHammer.setIconCoord(6, 15);
        ModLoader.addName(mod_InfiHybrids.oSlimeHammer, "Glassy Slime Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.oSlimeHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.obsidianRod
                });
        mod_InfiHybrids.saSlimeHammer.setIconCoord(7, 15);
        ModLoader.addName(mod_InfiHybrids.saSlimeHammer, "Sandy Slime Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.saSlimeHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.sandstoneRod
                });
        mod_InfiHybrids.bSlimeHammer.setIconCoord(8, 15);
        ModLoader.addName(mod_InfiHybrids.bSlimeHammer, "Necrotic Slime Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.bSlimeHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.bSlimeHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_InfiHybrids.pSlimeHammer.setIconCoord(9, 15);
        ModLoader.addName(mod_InfiHybrids.pSlimeHammer, "Fibery Slime Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.pSlimeHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.paperRod
                });
        mod_InfiHybrids.mSlimeHammer.setIconCoord(10, 15);
        ModLoader.addName(mod_InfiHybrids.mSlimeHammer, "Mossy Slime Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.mSlimeHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.mossyRod
                });
        mod_InfiHybrids.nSlimeHammer.setIconCoord(11, 15);
        ModLoader.addName(mod_InfiHybrids.nSlimeHammer, "Bloody Slime Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.nSlimeHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_InfiHybrids.glSlimeHammer.setIconCoord(12, 15);
        ModLoader.addName(mod_InfiHybrids.glSlimeHammer, "Glowing Slime Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.glSlimeHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_InfiHybrids.iceSlimeHammer.setIconCoord(13, 15);
        ModLoader.addName(mod_InfiHybrids.iceSlimeHammer, "Icy Slime Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.iceSlimeHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.iceRod
                });
        mod_InfiHybrids.lSlimeHammer.setIconCoord(14, 15);
        ModLoader.addName(mod_InfiHybrids.lSlimeHammer, "Fiery Slime Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.lSlimeHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.lavaRod
                });
        mod_InfiHybrids.sSlimeHammer.setIconCoord(15, 15);
        ModLoader.addName(mod_InfiHybrids.sSlimeHammer, "Green Mallet");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.sSlimeHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_InfiHybrids.cSlimeHammer.setIconCoord(12, 14);
        ModLoader.addName(mod_InfiHybrids.cSlimeHammer, "Spiny Slime Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.cSlimeHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_InfiHybrids.fSlimeHammer.setIconCoord(13, 14);
        ModLoader.addName(mod_InfiHybrids.fSlimeHammer, "Flaky Slime Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.fSlimeHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.flintRod
                });
        mod_InfiHybrids.brSlimeHammer.setIconCoord(14, 14);
        ModLoader.addName(mod_InfiHybrids.brSlimeHammer, "Baked Slime Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.brSlimeHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.brickRod
                });
        mod_InfiHybrids.blSlimeHammer.setIconCoord(15, 14);
        ModLoader.addName(mod_InfiHybrids.blSlimeHammer, "Blazing Slime Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.blSlimeHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), Item.blazeRod
                });
        mod_InfiHybrids.wCactusHammer.setIconCoord(10, 0);
        ModLoader.addName(mod_InfiHybrids.wCactusHammer, "Cactus Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.wCactusHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Block.cactus, Character.valueOf('|'), Item.stick
                });
        mod_InfiHybrids.stCactusHammer.setIconCoord(11, 0);
        ModLoader.addName(mod_InfiHybrids.stCactusHammer, "Stony Cactus Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.stCactusHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Block.cactus, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_InfiHybrids.saCactusHammer.setIconCoord(12, 0);
        ModLoader.addName(mod_InfiHybrids.saCactusHammer, "Sandy Cactus Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.saCactusHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Block.cactus, Character.valueOf('|'), mod_InfiTools.sandstoneRod
                });
        mod_InfiHybrids.bCactusHammer.setIconCoord(13, 0);
        ModLoader.addName(mod_InfiHybrids.bCactusHammer, "Necrotic Cactus Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.bCactusHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Block.cactus, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.bCactusHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Block.cactus, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_InfiHybrids.pCactusHammer.setIconCoord(14, 0);
        ModLoader.addName(mod_InfiHybrids.pCactusHammer, "Fibery Cactus Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.pCactusHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Block.cactus, Character.valueOf('|'), mod_InfiTools.paperRod
                });
        mod_InfiHybrids.nCactusHammer.setIconCoord(15, 0);
        ModLoader.addName(mod_InfiHybrids.nCactusHammer, "Bloody Cactus Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.nCactusHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Block.cactus, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_InfiHybrids.sCactusHammer.setIconCoord(12, 1);
        ModLoader.addName(mod_InfiHybrids.sCactusHammer, "Slimy Cactus Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.sCactusHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Block.cactus, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_InfiHybrids.cCactusHammer.setIconCoord(13, 1);
        ModLoader.addName(mod_InfiHybrids.cCactusHammer, "Spined Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.cCactusHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Block.cactus, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_InfiHybrids.fCactusHammer.setIconCoord(14, 1);
        ModLoader.addName(mod_InfiHybrids.fCactusHammer, "Flaky Cactus Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.fCactusHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Block.cactus, Character.valueOf('|'), mod_InfiTools.flintRod
                });
        mod_InfiHybrids.brCactusHammer.setIconCoord(15, 1);
        ModLoader.addName(mod_InfiHybrids.brCactusHammer, "Baked Cactus Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.brCactusHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Block.cactus, Character.valueOf('|'), mod_InfiTools.brickRod
                });
        mod_InfiHybrids.wFlintHammer.setIconCoord(13, 2);
        ModLoader.addName(mod_InfiHybrids.wFlintHammer, "Flint Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.wFlintHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Item.flint, Character.valueOf('|'), Item.stick
                });
        mod_InfiHybrids.stFlintHammer.setIconCoord(14, 2);
        ModLoader.addName(mod_InfiHybrids.stFlintHammer, "Stony Flint Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.stFlintHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_InfiHybrids.iFlintHammer.setIconCoord(15, 2);
        ModLoader.addName(mod_InfiHybrids.iFlintHammer, "Hard Flint Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.iFlintHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.ironRod
                });
        mod_InfiHybrids.gFlintHammer.setIconCoord(12, 3);
        ModLoader.addName(mod_InfiHybrids.gFlintHammer, "Expensive Flint Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.gFlintHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.goldRod
                });
        mod_InfiHybrids.oFlintHammer.setIconCoord(13, 3);
        ModLoader.addName(mod_InfiHybrids.oFlintHammer, "Glassy Flint Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.oFlintHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.obsidianRod
                });
        mod_InfiHybrids.saFlintHammer.setIconCoord(14, 3);
        ModLoader.addName(mod_InfiHybrids.saFlintHammer, "Sandy Flint Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.saFlintHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.sandstoneRod
                });
        mod_InfiHybrids.bFlintHammer.setIconCoord(15, 3);
        ModLoader.addName(mod_InfiHybrids.bFlintHammer, "Necrotic Flint Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.bFlintHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Item.flint, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.bFlintHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_InfiHybrids.nFlintHammer.setIconCoord(12, 4);
        ModLoader.addName(mod_InfiHybrids.nFlintHammer, "Bloody Flint Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.nFlintHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_InfiHybrids.iceFlintHammer.setIconCoord(13, 4);
        ModLoader.addName(mod_InfiHybrids.iceFlintHammer, "Icy Flint Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.iceFlintHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.iceRod
                });
        mod_InfiHybrids.sFlintHammer.setIconCoord(14, 4);
        ModLoader.addName(mod_InfiHybrids.sFlintHammer, "Slimy Flint Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.sFlintHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_InfiHybrids.cFlintHammer.setIconCoord(15, 4);
        ModLoader.addName(mod_InfiHybrids.cFlintHammer, "Spiny Flint Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.cFlintHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_InfiHybrids.fFlintHammer.setIconCoord(11, 5);
        ModLoader.addName(mod_InfiHybrids.fFlintHammer, "Flaky Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.fFlintHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.flintRod
                });
        mod_InfiHybrids.brFlintHammer.setIconCoord(12, 5);
        ModLoader.addName(mod_InfiHybrids.brFlintHammer, "Baked Flint Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.brFlintHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.brickRod
                });
        mod_InfiHybrids.blFlintHammer.setIconCoord(13, 5);
        ModLoader.addName(mod_InfiHybrids.blFlintHammer, "Blazing Flint Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.blFlintHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Item.flint, Character.valueOf('|'), Item.blazeRod
                });
        mod_InfiHybrids.wBrickHammer.setIconCoord(14, 5);
        ModLoader.addName(mod_InfiHybrids.wBrickHammer, "Brick Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.wBrickHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Item.brick, Character.valueOf('|'), Item.brick
                });
        mod_InfiHybrids.stBrickHammer.setIconCoord(15, 5);
        ModLoader.addName(mod_InfiHybrids.stBrickHammer, "Stony Brick Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.stBrickHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Item.brick, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_InfiHybrids.saBrickHammer.setIconCoord(13, 6);
        ModLoader.addName(mod_InfiHybrids.saBrickHammer, "Sandstone Brick Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.saBrickHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Item.brick, Character.valueOf('|'), mod_InfiTools.sandstoneRod
                });
        mod_InfiHybrids.bBrickHammer.setIconCoord(14, 6);
        ModLoader.addName(mod_InfiHybrids.bBrickHammer, "Necrotic Brick Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.bBrickHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Item.brick, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.bBrickHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Item.brick, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_InfiHybrids.pBrickHammer.setIconCoord(15, 6);
        ModLoader.addName(mod_InfiHybrids.pBrickHammer, "Fibery Brick Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.pBrickHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Item.brick, Character.valueOf('|'), mod_InfiTools.paperRod
                });
        mod_InfiHybrids.nBrickHammer.setIconCoord(11, 7);
        ModLoader.addName(mod_InfiHybrids.nBrickHammer, "Bloody Brick Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.nBrickHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Item.brick, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_InfiHybrids.iceBrickHammer.setIconCoord(12, 7);
        ModLoader.addName(mod_InfiHybrids.iceBrickHammer, "Icy Brick Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.iceBrickHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Item.brick, Character.valueOf('|'), mod_InfiTools.iceRod
                });
        mod_InfiHybrids.sBrickHammer.setIconCoord(13, 7);
        ModLoader.addName(mod_InfiHybrids.sBrickHammer, "Slimy Brick Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.sBrickHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Item.brick, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_InfiHybrids.cBrickHammer.setIconCoord(14, 7);
        ModLoader.addName(mod_InfiHybrids.cBrickHammer, "Spiny Brick Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.cBrickHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Item.brick, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_InfiHybrids.fBrickHammer.setIconCoord(15, 7);
        ModLoader.addName(mod_InfiHybrids.fBrickHammer, "Flaky Brick Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.fBrickHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Item.brick, Character.valueOf('|'), mod_InfiTools.flintRod
                });
        mod_InfiHybrids.brBrickHammer.setIconCoord(15, 8);
        ModLoader.addName(mod_InfiHybrids.brBrickHammer, "Look-alike Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.brBrickHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), Item.brick, Character.valueOf('|'), mod_InfiTools.brickRod
                });
        mod_InfiHybrids.dBlazeHammer.setIconCoord(7, 9);
        ModLoader.addName(mod_InfiHybrids.dBlazeHammer, "Jeweled Blaze Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.dBlazeHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), mod_InfiTools.blazeCrystal, Character.valueOf('|'), mod_InfiTools.diamondRod
                });
        mod_InfiHybrids.rBlazeHammer.setIconCoord(8, 9);
        ModLoader.addName(mod_InfiHybrids.rBlazeHammer, "Red Blaze Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.rBlazeHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), mod_InfiTools.blazeCrystal, Character.valueOf('|'), mod_InfiTools.redstoneRod
                });
        mod_InfiHybrids.bBlazeHammer.setIconCoord(9, 9);
        ModLoader.addName(mod_InfiHybrids.bBlazeHammer, "Necrotic Blaze Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.bBlazeHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), mod_InfiTools.blazeCrystal, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.bBlazeHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), mod_InfiTools.blazeCrystal, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_InfiHybrids.nBlazeHammer.setIconCoord(10, 9);
        ModLoader.addName(mod_InfiHybrids.nBlazeHammer, "Bloody Blaze Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.nBlazeHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), mod_InfiTools.blazeCrystal, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_InfiHybrids.glBlazeHammer.setIconCoord(11, 9);
        ModLoader.addName(mod_InfiHybrids.glBlazeHammer, "Glowing Blaze Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.glBlazeHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), mod_InfiTools.blazeCrystal, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_InfiHybrids.lBlazeHammer.setIconCoord(12, 9);
        ModLoader.addName(mod_InfiHybrids.lBlazeHammer, "Fiery Blaze Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.lBlazeHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), mod_InfiTools.blazeCrystal, Character.valueOf('|'), Item.blazeRod
                });
        mod_InfiHybrids.fBlazeHammer.setIconCoord(13, 9);
        ModLoader.addName(mod_InfiHybrids.fBlazeHammer, "Flaky Blaze Hammer");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.fBlazeHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), mod_InfiTools.blazeCrystal, Character.valueOf('|'), Item.blazeRod
                });
        mod_InfiHybrids.blBlazeHammer.setIconCoord(14, 9);
        ModLoader.addName(mod_InfiHybrids.blBlazeHammer, "Netherbash");
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.blBlazeHammer, 1), new Object[]
                {
                    "mm", "mm", "| ", Character.valueOf('m'), mod_InfiTools.blazeCrystal, Character.valueOf('|'), Item.blazeRod
                });
        MinecraftForge.setToolClass(mod_InfiHybrids.wWoodHammer, "pickaxe", mod_InfiTools.wLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.stWoodHammer, "pickaxe", mod_InfiTools.wLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.saWoodHammer, "pickaxe", mod_InfiTools.wLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.bWoodHammer, "pickaxe", mod_InfiTools.wLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.pWoodHammer, "pickaxe", mod_InfiTools.wLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.nWoodHammer, "pickaxe", mod_InfiTools.wLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.sWoodHammer, "pickaxe", mod_InfiTools.wLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.cWoodHammer, "pickaxe", mod_InfiTools.wLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.brWoodHammer, "pickaxe", mod_InfiTools.wLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.wStoneHammer, "pickaxe", mod_InfiTools.stLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.stStoneHammer, "pickaxe", mod_InfiTools.stLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.saStoneHammer, "pickaxe", mod_InfiTools.stLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.bStoneHammer, "pickaxe", mod_InfiTools.stLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.mStoneHammer, "pickaxe", mod_InfiTools.stLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.nStoneHammer, "pickaxe", mod_InfiTools.stLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.iceStoneHammer, "pickaxe", mod_InfiTools.stLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.sStoneHammer, "pickaxe", mod_InfiTools.stLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.cStoneHammer, "pickaxe", mod_InfiTools.stLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.fStoneHammer, "pickaxe", mod_InfiTools.stLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.brStoneHammer, "pickaxe", mod_InfiTools.stLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.wIronHammer, "pickaxe", mod_InfiTools.iLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.stIronHammer, "pickaxe", mod_InfiTools.iLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.iIronHammer, "pickaxe", mod_InfiTools.iLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.dIronHammer, "pickaxe", mod_InfiTools.iLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.gIronHammer, "pickaxe", mod_InfiTools.iLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.rIronHammer, "pickaxe", mod_InfiTools.iLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.oIronHammer, "pickaxe", mod_InfiTools.iLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.bIronHammer, "pickaxe", mod_InfiTools.iLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.nIronHammer, "pickaxe", mod_InfiTools.iLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.glIronHammer, "pickaxe", mod_InfiTools.iLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.iceIronHammer, "pickaxe", mod_InfiTools.iLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.sIronHammer, "pickaxe", mod_InfiTools.iLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.blIronHammer, "pickaxe", mod_InfiTools.iLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.wDiamondHammer, "pickaxe", mod_InfiTools.dLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.stDiamondHammer, "pickaxe", mod_InfiTools.dLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.iDiamondHammer, "pickaxe", mod_InfiTools.dLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.dDiamondHammer, "pickaxe", mod_InfiTools.dLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.gDiamondHammer, "pickaxe", mod_InfiTools.dLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.rDiamondHammer, "pickaxe", mod_InfiTools.dLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.oDiamondHammer, "pickaxe", mod_InfiTools.dLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.bDiamondHammer, "pickaxe", mod_InfiTools.dLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.mDiamondHammer, "pickaxe", mod_InfiTools.dLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.nDiamondHammer, "pickaxe", mod_InfiTools.dLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.glDiamondHammer, "pickaxe", mod_InfiTools.dLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.blDiamondHammer, "pickaxe", mod_InfiTools.dLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.wGoldHammer, "pickaxe", mod_InfiTools.gLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.stGoldHammer, "pickaxe", mod_InfiTools.gLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.gGoldHammer, "pickaxe", mod_InfiTools.gLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.oGoldHammer, "pickaxe", mod_InfiTools.gLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.saGoldHammer, "pickaxe", mod_InfiTools.gLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.bGoldHammer, "pickaxe", mod_InfiTools.gLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.mGoldHammer, "pickaxe", mod_InfiTools.gLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.nGoldHammer, "pickaxe", mod_InfiTools.gLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.glGoldHammer, "pickaxe", mod_InfiTools.gLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.iceGoldHammer, "pickaxe", mod_InfiTools.gLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.sGoldHammer, "pickaxe", mod_InfiTools.gLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.fGoldHammer, "pickaxe", mod_InfiTools.gLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.wRedstoneHammer, "pickaxe", mod_InfiTools.rLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.stRedstoneHammer, "pickaxe", mod_InfiTools.rLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.iRedstoneHammer, "pickaxe", mod_InfiTools.rLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.dRedstoneHammer, "pickaxe", mod_InfiTools.rLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.rRedstoneHammer, "pickaxe", mod_InfiTools.rLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.oRedstoneHammer, "pickaxe", mod_InfiTools.rLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.bRedstoneHammer, "pickaxe", mod_InfiTools.rLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.mRedstoneHammer, "pickaxe", mod_InfiTools.rLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.glRedstoneHammer, "pickaxe", mod_InfiTools.rLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.sRedstoneHammer, "pickaxe", mod_InfiTools.rLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.blRedstoneHammer, "pickaxe", mod_InfiTools.rLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.wObsidianHammer, "pickaxe", mod_InfiTools.oLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.stObsidianHammer, "pickaxe", mod_InfiTools.oLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.iObsidianHammer, "pickaxe", mod_InfiTools.oLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.dObsidianHammer, "pickaxe", mod_InfiTools.oLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.gObsidianHammer, "pickaxe", mod_InfiTools.oLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.rObsidianHammer, "pickaxe", mod_InfiTools.oLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.oObsidianHammer, "pickaxe", mod_InfiTools.oLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.bObsidianHammer, "pickaxe", mod_InfiTools.oLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.nObsidianHammer, "pickaxe", mod_InfiTools.oLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.glObsidianHammer, "pickaxe", mod_InfiTools.oLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.sObsidianHammer, "pickaxe", mod_InfiTools.oLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.fObsidianHammer, "pickaxe", mod_InfiTools.oLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.blObsidianHammer, "pickaxe", mod_InfiTools.oLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.wSandstoneHammer, "pickaxe", mod_InfiTools.saLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.stSandstoneHammer, "pickaxe", mod_InfiTools.saLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.saSandstoneHammer, "pickaxe", mod_InfiTools.saLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.bSandstoneHammer, "pickaxe", mod_InfiTools.saLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.pSandstoneHammer, "pickaxe", mod_InfiTools.saLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.nSandstoneHammer, "pickaxe", mod_InfiTools.saLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.iceSandstoneHammer, "pickaxe", mod_InfiTools.saLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.sSandstoneHammer, "pickaxe", mod_InfiTools.saLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.cSandstoneHammer, "pickaxe", mod_InfiTools.saLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.fSandstoneHammer, "pickaxe", mod_InfiTools.saLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.brSandstoneHammer, "pickaxe", mod_InfiTools.saLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.wBoneHammer, "pickaxe", mod_InfiTools.bLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.stBoneHammer, "pickaxe", mod_InfiTools.bLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.iBoneHammer, "pickaxe", mod_InfiTools.bLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.dBoneHammer, "pickaxe", mod_InfiTools.bLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.rBoneHammer, "pickaxe", mod_InfiTools.bLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.oBoneHammer, "pickaxe", mod_InfiTools.bLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.bBoneHammer, "pickaxe", mod_InfiTools.bLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.mBoneHammer, "pickaxe", mod_InfiTools.bLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.nBoneHammer, "pickaxe", mod_InfiTools.bLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.glBoneHammer, "pickaxe", mod_InfiTools.bLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.sBoneHammer, "pickaxe", mod_InfiTools.bLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.cBoneHammer, "pickaxe", mod_InfiTools.bLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.fBoneHammer, "pickaxe", mod_InfiTools.bLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.brBoneHammer, "pickaxe", mod_InfiTools.bLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.blBoneHammer, "pickaxe", mod_InfiTools.bLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.wPaperHammer, "pickaxe", mod_InfiTools.pLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.saPaperHammer, "pickaxe", mod_InfiTools.pLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.bPaperHammer, "pickaxe", mod_InfiTools.pLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.pPaperHammer, "pickaxe", mod_InfiTools.pLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.sPaperHammer, "pickaxe", mod_InfiTools.pLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.cPaperHammer, "pickaxe", mod_InfiTools.pLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.brPaperHammer, "pickaxe", mod_InfiTools.pLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.stMossyHammer, "pickaxe", mod_InfiTools.mLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.dMossyHammer, "pickaxe", mod_InfiTools.mLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.rMossyHammer, "pickaxe", mod_InfiTools.mLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.bMossyHammer, "pickaxe", mod_InfiTools.mLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.mMossyHammer, "pickaxe", mod_InfiTools.mLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.glMossyHammer, "pickaxe", mod_InfiTools.mLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.wNetherrackHammer, "pickaxe", mod_InfiTools.nLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.stNetherrackHammer, "pickaxe", mod_InfiTools.nLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.iNetherrackHammer, "pickaxe", mod_InfiTools.nLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.rNetherrackHammer, "pickaxe", mod_InfiTools.nLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.oNetherrackHammer, "pickaxe", mod_InfiTools.nLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.saNetherrackHammer, "pickaxe", mod_InfiTools.nLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.bNetherrackHammer, "pickaxe", mod_InfiTools.nLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.mNetherrackHammer, "pickaxe", mod_InfiTools.nLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.nNetherrackHammer, "pickaxe", mod_InfiTools.nLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.glNetherrackHammer, "pickaxe", mod_InfiTools.nLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.iceNetherrackHammer, "pickaxe", mod_InfiTools.nLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.sNetherrackHammer, "pickaxe", mod_InfiTools.nLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.cNetherrackHammer, "pickaxe", mod_InfiTools.nLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.fNetherrackHammer, "pickaxe", mod_InfiTools.nLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.brNetherrackHammer, "pickaxe", mod_InfiTools.nLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.blNetherrackHammer, "pickaxe", mod_InfiTools.nLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.wGlowstoneHammer, "pickaxe", mod_InfiTools.glLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.stGlowstoneHammer, "pickaxe", mod_InfiTools.glLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.iGlowstoneHammer, "pickaxe", mod_InfiTools.glLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.dGlowstoneHammer, "pickaxe", mod_InfiTools.glLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.rGlowstoneHammer, "pickaxe", mod_InfiTools.glLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.oGlowstoneHammer, "pickaxe", mod_InfiTools.glLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.bGlowstoneHammer, "pickaxe", mod_InfiTools.glLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.mGlowstoneHammer, "pickaxe", mod_InfiTools.glLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.nGlowstoneHammer, "pickaxe", mod_InfiTools.glLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.glGlowstoneHammer, "pickaxe", mod_InfiTools.glLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.iceGlowstoneHammer, "pickaxe", mod_InfiTools.glLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.lGlowstoneHammer, "pickaxe", mod_InfiTools.glLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.sGlowstoneHammer, "pickaxe", mod_InfiTools.glLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.blGlowstoneHammer, "pickaxe", mod_InfiTools.glLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.wIceHammer, "pickaxe", mod_InfiTools.iceLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.stIceHammer, "pickaxe", mod_InfiTools.iceLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.iIceHammer, "pickaxe", mod_InfiTools.iceLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.dIceHammer, "pickaxe", mod_InfiTools.iceLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.gIceHammer, "pickaxe", mod_InfiTools.iceLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.rIceHammer, "pickaxe", mod_InfiTools.iceLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.oIceHammer, "pickaxe", mod_InfiTools.iceLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.saIceHammer, "pickaxe", mod_InfiTools.iceLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.bIceHammer, "pickaxe", mod_InfiTools.iceLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.glIceHammer, "pickaxe", mod_InfiTools.iceLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.iceIceHammer, "pickaxe", mod_InfiTools.iceLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.sIceHammer, "pickaxe", mod_InfiTools.iceLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.cIceHammer, "pickaxe", mod_InfiTools.iceLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.fIceHammer, "pickaxe", mod_InfiTools.iceLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.brIceHammer, "pickaxe", mod_InfiTools.iceLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.dLavaHammer, "pickaxe", mod_InfiTools.lLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.rLavaHammer, "pickaxe", mod_InfiTools.lLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.bLavaHammer, "pickaxe", mod_InfiTools.lLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.nLavaHammer, "pickaxe", mod_InfiTools.lLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.glLavaHammer, "pickaxe", mod_InfiTools.lLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.lLavaHammer, "pickaxe", mod_InfiTools.lLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.blLavaHammer, "pickaxe", mod_InfiTools.lLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.wSlimeHammer, "pickaxe", mod_InfiTools.sLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.stSlimeHammer, "pickaxe", mod_InfiTools.sLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.iSlimeHammer, "pickaxe", mod_InfiTools.sLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.dSlimeHammer, "pickaxe", mod_InfiTools.sLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.gSlimeHammer, "pickaxe", mod_InfiTools.sLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.rSlimeHammer, "pickaxe", mod_InfiTools.sLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.oSlimeHammer, "pickaxe", mod_InfiTools.sLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.saSlimeHammer, "pickaxe", mod_InfiTools.sLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.bSlimeHammer, "pickaxe", mod_InfiTools.sLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.pSlimeHammer, "pickaxe", mod_InfiTools.sLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.mSlimeHammer, "pickaxe", mod_InfiTools.sLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.nSlimeHammer, "pickaxe", mod_InfiTools.sLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.glSlimeHammer, "pickaxe", mod_InfiTools.sLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.iceSlimeHammer, "pickaxe", mod_InfiTools.sLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.lSlimeHammer, "pickaxe", mod_InfiTools.sLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.sSlimeHammer, "pickaxe", mod_InfiTools.sLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.cSlimeHammer, "pickaxe", mod_InfiTools.sLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.fSlimeHammer, "pickaxe", mod_InfiTools.sLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.brSlimeHammer, "pickaxe", mod_InfiTools.sLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.blSlimeHammer, "pickaxe", mod_InfiTools.sLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.wCactusHammer, "pickaxe", mod_InfiTools.cLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.stCactusHammer, "pickaxe", mod_InfiTools.cLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.saCactusHammer, "pickaxe", mod_InfiTools.cLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.bCactusHammer, "pickaxe", mod_InfiTools.cLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.pCactusHammer, "pickaxe", mod_InfiTools.cLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.nCactusHammer, "pickaxe", mod_InfiTools.cLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.sCactusHammer, "pickaxe", mod_InfiTools.cLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.cCactusHammer, "pickaxe", mod_InfiTools.cLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.fCactusHammer, "pickaxe", mod_InfiTools.cLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.brCactusHammer, "pickaxe", mod_InfiTools.cLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.wFlintHammer, "pickaxe", mod_InfiTools.fLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.stFlintHammer, "pickaxe", mod_InfiTools.fLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.iFlintHammer, "pickaxe", mod_InfiTools.fLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.gFlintHammer, "pickaxe", mod_InfiTools.fLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.oFlintHammer, "pickaxe", mod_InfiTools.fLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.saFlintHammer, "pickaxe", mod_InfiTools.fLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.bFlintHammer, "pickaxe", mod_InfiTools.fLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.nFlintHammer, "pickaxe", mod_InfiTools.fLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.iceFlintHammer, "pickaxe", mod_InfiTools.fLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.sFlintHammer, "pickaxe", mod_InfiTools.fLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.cFlintHammer, "pickaxe", mod_InfiTools.fLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.fFlintHammer, "pickaxe", mod_InfiTools.fLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.brFlintHammer, "pickaxe", mod_InfiTools.fLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.blFlintHammer, "pickaxe", mod_InfiTools.fLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.wBrickHammer, "pickaxe", mod_InfiTools.brLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.stBrickHammer, "pickaxe", mod_InfiTools.brLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.saBrickHammer, "pickaxe", mod_InfiTools.brLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.bBrickHammer, "pickaxe", mod_InfiTools.brLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.pBrickHammer, "pickaxe", mod_InfiTools.brLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.nBrickHammer, "pickaxe", mod_InfiTools.brLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.iceBrickHammer, "pickaxe", mod_InfiTools.brLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.sBrickHammer, "pickaxe", mod_InfiTools.brLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.cBrickHammer, "pickaxe", mod_InfiTools.brLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.fBrickHammer, "pickaxe", mod_InfiTools.brLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.brBrickHammer, "pickaxe", mod_InfiTools.brLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.dBlazeHammer, "pickaxe", mod_InfiTools.blLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.rBlazeHammer, "pickaxe", mod_InfiTools.blLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.bBlazeHammer, "pickaxe", mod_InfiTools.blLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.nBlazeHammer, "pickaxe", mod_InfiTools.blLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.glBlazeHammer, "pickaxe", mod_InfiTools.blLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.lBlazeHammer, "pickaxe", mod_InfiTools.blLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.fBlazeHammer, "pickaxe", mod_InfiTools.blLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.blBlazeHammer, "pickaxe", mod_InfiTools.blLevel);
        return 0;
    }
}
