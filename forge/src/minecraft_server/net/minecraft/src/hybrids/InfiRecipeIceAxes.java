package net.minecraft.src.hybrids;

import net.minecraft.src.forge.MinecraftForge;
import net.minecraft.src.*;

public class InfiRecipeIceAxes
{
    public InfiRecipeIceAxes()
    {
    }

    public static int recipeStorm()
    {
        mod_InfiHybrids.wWoodIceAxe.setIconCoord(0, 0);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.wWoodIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Block.planks, Character.valueOf('|'), Item.stick, Character.valueOf('s'), Item.stick
                });
        mod_InfiHybrids.stWoodIceAxe.setIconCoord(1, 0);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.stWoodIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Block.planks, Character.valueOf('|'), mod_InfiTools.stoneRod, Character.valueOf('s'), Item.stick
                });
        mod_InfiHybrids.saWoodIceAxe.setIconCoord(2, 0);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.saWoodIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Block.planks, Character.valueOf('|'), mod_InfiTools.sandstoneRod, Character.valueOf('s'), Item.stick
                });
        mod_InfiHybrids.bWoodIceAxe.setIconCoord(3, 0);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.bWoodIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Block.planks, Character.valueOf('|'), Item.bone, Character.valueOf('s'), Item.stick
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.bWoodIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Block.planks, Character.valueOf('|'), mod_InfiTools.boneRod, Character.valueOf('s'), Item.stick
                });
        mod_InfiHybrids.pWoodIceAxe.setIconCoord(4, 0);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.pWoodIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Block.planks, Character.valueOf('|'), mod_InfiTools.paperRod, Character.valueOf('s'), Item.stick
                });
        mod_InfiHybrids.nWoodIceAxe.setIconCoord(5, 0);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.nWoodIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Block.planks, Character.valueOf('|'), mod_InfiTools.netherrackRod, Character.valueOf('s'), Item.stick
                });
        mod_InfiHybrids.sWoodIceAxe.setIconCoord(6, 0);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.sWoodIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Block.planks, Character.valueOf('|'), mod_InfiTools.slimeRod, Character.valueOf('s'), Item.stick
                });
        mod_InfiHybrids.cWoodIceAxe.setIconCoord(7, 0);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.cWoodIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Block.planks, Character.valueOf('|'), mod_InfiTools.cactusRod, Character.valueOf('s'), Item.stick
                });
        mod_InfiHybrids.fWoodIceAxe.setIconCoord(8, 0);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.fWoodIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Block.planks, Character.valueOf('|'), mod_InfiTools.flintRod, Character.valueOf('s'), Item.stick
                });
        mod_InfiHybrids.brWoodIceAxe.setIconCoord(9, 0);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.brWoodIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Block.planks, Character.valueOf('|'), mod_InfiTools.brickRod, Character.valueOf('s'), Item.stick
                });
        mod_InfiHybrids.wStoneIceAxe.setIconCoord(0, 1);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.wStoneIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), Item.stick, Character.valueOf('s'), mod_InfiTools.stoneShard
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.wStoneIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Block.stone, Character.valueOf('|'), Item.stick, Character.valueOf('s'), mod_InfiTools.stoneShard
                });
        mod_InfiHybrids.stStoneIceAxe.setIconCoord(1, 1);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.stStoneIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), mod_InfiTools.stoneRod, Character.valueOf('s'), mod_InfiTools.stoneShard
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.stStoneIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Block.stone, Character.valueOf('|'), mod_InfiTools.stoneRod, Character.valueOf('s'), mod_InfiTools.stoneShard
                });
        mod_InfiHybrids.saStoneIceAxe.setIconCoord(2, 1);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.saStoneIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), mod_InfiTools.sandstoneRod, Character.valueOf('s'), mod_InfiTools.stoneShard
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.saStoneIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Block.stone, Character.valueOf('|'), mod_InfiTools.sandstoneRod, Character.valueOf('s'), mod_InfiTools.stoneShard
                });
        mod_InfiHybrids.bStoneIceAxe.setIconCoord(3, 1);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.bStoneIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), Item.bone, Character.valueOf('s'), mod_InfiTools.stoneShard
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.bStoneIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Block.stone, Character.valueOf('|'), Item.bone, Character.valueOf('s'), mod_InfiTools.stoneShard
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.bStoneIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), mod_InfiTools.boneRod, Character.valueOf('s'), mod_InfiTools.stoneShard
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.bStoneIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Block.stone, Character.valueOf('|'), mod_InfiTools.boneRod, Character.valueOf('s'), mod_InfiTools.stoneShard
                });
        mod_InfiHybrids.pStoneIceAxe.setIconCoord(4, 1);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.pStoneIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), mod_InfiTools.paperRod, Character.valueOf('s'), mod_InfiTools.stoneShard
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.pStoneIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Block.stone, Character.valueOf('|'), mod_InfiTools.paperRod, Character.valueOf('s'), mod_InfiTools.stoneShard
                });
        mod_InfiHybrids.mStoneIceAxe.setIconCoord(5, 1);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.mStoneIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), mod_InfiTools.mossyRod, Character.valueOf('s'), mod_InfiTools.stoneShard
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.mStoneIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Block.stone, Character.valueOf('|'), mod_InfiTools.mossyRod, Character.valueOf('s'), mod_InfiTools.stoneShard
                });
        mod_InfiHybrids.nStoneIceAxe.setIconCoord(6, 1);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.nStoneIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), mod_InfiTools.netherrackRod, Character.valueOf('s'), mod_InfiTools.stoneShard
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.nStoneIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Block.stone, Character.valueOf('|'), mod_InfiTools.netherrackRod, Character.valueOf('s'), mod_InfiTools.stoneShard
                });
        mod_InfiHybrids.iceStoneIceAxe.setIconCoord(7, 1);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.iceStoneIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), mod_InfiTools.iceRod, Character.valueOf('s'), mod_InfiTools.stoneShard
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.iceStoneIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Block.stone, Character.valueOf('|'), mod_InfiTools.iceRod, Character.valueOf('s'), mod_InfiTools.stoneShard
                });
        mod_InfiHybrids.sStoneIceAxe.setIconCoord(8, 1);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.sStoneIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), mod_InfiTools.slimeRod, Character.valueOf('s'), mod_InfiTools.stoneShard
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.sStoneIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Block.stone, Character.valueOf('|'), mod_InfiTools.slimeRod, Character.valueOf('s'), mod_InfiTools.stoneShard
                });
        mod_InfiHybrids.cStoneIceAxe.setIconCoord(9, 1);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.cStoneIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), mod_InfiTools.cactusRod, Character.valueOf('s'), mod_InfiTools.stoneShard
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.cStoneIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Block.stone, Character.valueOf('|'), mod_InfiTools.cactusRod, Character.valueOf('s'), mod_InfiTools.stoneShard
                });
        mod_InfiHybrids.fStoneIceAxe.setIconCoord(10, 1);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.fStoneIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), mod_InfiTools.flintRod, Character.valueOf('s'), mod_InfiTools.stoneShard
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.fStoneIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Block.stone, Character.valueOf('|'), mod_InfiTools.flintRod, Character.valueOf('s'), mod_InfiTools.stoneShard
                });
        mod_InfiHybrids.brStoneIceAxe.setIconCoord(11, 1);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.brStoneIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), mod_InfiTools.brickRod, Character.valueOf('s'), mod_InfiTools.stoneShard
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.brStoneIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Block.stone, Character.valueOf('|'), mod_InfiTools.brickRod, Character.valueOf('s'), mod_InfiTools.stoneShard
                });
        mod_InfiHybrids.wIronIceAxe.setIconCoord(0, 2);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.wIronIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), Item.stick, Character.valueOf('s'), mod_InfiTools.ironChunks
                });
        mod_InfiHybrids.stIronIceAxe.setIconCoord(1, 2);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.stIronIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), mod_InfiTools.stoneRod, Character.valueOf('s'), mod_InfiTools.ironChunks
                });
        mod_InfiHybrids.iIronIceAxe.setIconCoord(2, 2);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.iIronIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), mod_InfiTools.ironRod, Character.valueOf('s'), mod_InfiTools.ironChunks
                });
        mod_InfiHybrids.dIronIceAxe.setIconCoord(3, 2);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.dIronIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), mod_InfiTools.diamondRod, Character.valueOf('s'), mod_InfiTools.ironChunks
                });
        mod_InfiHybrids.gIronIceAxe.setIconCoord(4, 2);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.gIronIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), mod_InfiTools.goldRod, Character.valueOf('s'), mod_InfiTools.ironChunks
                });
        mod_InfiHybrids.rIronIceAxe.setIconCoord(5, 2);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.rIronIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), mod_InfiTools.redstoneRod, Character.valueOf('s'), mod_InfiTools.ironChunks
                });
        mod_InfiHybrids.oIronIceAxe.setIconCoord(6, 2);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.oIronIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), mod_InfiTools.obsidianRod, Character.valueOf('s'), mod_InfiTools.ironChunks
                });
        mod_InfiHybrids.bIronIceAxe.setIconCoord(7, 2);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.bIronIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), Item.bone, Character.valueOf('s'), mod_InfiTools.ironChunks
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.bIronIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), mod_InfiTools.boneRod, Character.valueOf('s'), mod_InfiTools.ironChunks
                });
        mod_InfiHybrids.nIronIceAxe.setIconCoord(8, 2);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.nIronIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), mod_InfiTools.netherrackRod, Character.valueOf('s'), mod_InfiTools.ironChunks
                });
        mod_InfiHybrids.glIronIceAxe.setIconCoord(9, 2);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.glIronIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), mod_InfiTools.glowstoneRod, Character.valueOf('s'), mod_InfiTools.ironChunks
                });
        mod_InfiHybrids.iceIronIceAxe.setIconCoord(10, 2);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.iceIronIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), mod_InfiTools.iceRod, Character.valueOf('s'), mod_InfiTools.ironChunks
                });
        mod_InfiHybrids.sIronIceAxe.setIconCoord(11, 2);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.sIronIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), mod_InfiTools.slimeRod, Character.valueOf('s'), mod_InfiTools.ironChunks
                });
        mod_InfiHybrids.blIronIceAxe.setIconCoord(12, 2);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.blIronIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), Item.blazeRod, Character.valueOf('s'), mod_InfiTools.ironChunks
                });
        mod_InfiHybrids.wDiamondIceAxe.setIconCoord(0, 3);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.wDiamondIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), Item.stick, Character.valueOf('s'), mod_InfiTools.diamondShard
                });
        mod_InfiHybrids.stDiamondIceAxe.setIconCoord(1, 3);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.stDiamondIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), mod_InfiTools.stoneRod, Character.valueOf('s'), mod_InfiTools.diamondShard
                });
        mod_InfiHybrids.iDiamondIceAxe.setIconCoord(2, 3);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.iDiamondIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), mod_InfiTools.ironRod, Character.valueOf('s'), mod_InfiTools.diamondShard
                });
        mod_InfiHybrids.dDiamondIceAxe.setIconCoord(3, 3);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.dDiamondIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), mod_InfiTools.diamondRod, Character.valueOf('s'), mod_InfiTools.diamondShard
                });
        mod_InfiHybrids.gDiamondIceAxe.setIconCoord(4, 3);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.gDiamondIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), mod_InfiTools.goldRod, Character.valueOf('s'), mod_InfiTools.diamondShard
                });
        mod_InfiHybrids.rDiamondIceAxe.setIconCoord(5, 3);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.rDiamondIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), mod_InfiTools.redstoneRod, Character.valueOf('s'), mod_InfiTools.diamondShard
                });
        mod_InfiHybrids.oDiamondIceAxe.setIconCoord(6, 3);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.oDiamondIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), mod_InfiTools.obsidianRod, Character.valueOf('s'), mod_InfiTools.diamondShard
                });
        mod_InfiHybrids.bDiamondIceAxe.setIconCoord(7, 3);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.bDiamondIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), Item.bone, Character.valueOf('s'), mod_InfiTools.diamondShard
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.bDiamondIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), mod_InfiTools.boneRod, Character.valueOf('s'), mod_InfiTools.diamondShard
                });
        mod_InfiHybrids.mDiamondIceAxe.setIconCoord(8, 3);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.mDiamondIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), mod_InfiTools.mossyRod, Character.valueOf('s'), mod_InfiTools.diamondShard
                });
        mod_InfiHybrids.nDiamondIceAxe.setIconCoord(9, 3);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.nDiamondIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), mod_InfiTools.netherrackRod, Character.valueOf('s'), mod_InfiTools.diamondShard
                });
        mod_InfiHybrids.glDiamondIceAxe.setIconCoord(10, 3);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.glDiamondIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), mod_InfiTools.glowstoneRod, Character.valueOf('s'), mod_InfiTools.diamondShard
                });
        mod_InfiHybrids.blDiamondIceAxe.setIconCoord(11, 3);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.blDiamondIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), Item.blazeRod, Character.valueOf('s'), mod_InfiTools.diamondShard
                });
        mod_InfiHybrids.wGoldIceAxe.setIconCoord(0, 4);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.wGoldIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), Item.stick, Character.valueOf('s'), mod_InfiTools.goldChunks
                });
        mod_InfiHybrids.stGoldIceAxe.setIconCoord(1, 4);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.stGoldIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), mod_InfiTools.stoneRod, Character.valueOf('s'), mod_InfiTools.goldChunks
                });
        mod_InfiHybrids.gGoldIceAxe.setIconCoord(2, 4);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.gGoldIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), mod_InfiTools.goldRod, Character.valueOf('s'), mod_InfiTools.goldChunks
                });
        mod_InfiHybrids.oGoldIceAxe.setIconCoord(3, 4);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.oGoldIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), mod_InfiTools.obsidianRod, Character.valueOf('s'), mod_InfiTools.goldChunks
                });
        mod_InfiHybrids.saGoldIceAxe.setIconCoord(4, 4);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.saGoldIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), mod_InfiTools.sandstoneRod, Character.valueOf('s'), mod_InfiTools.goldChunks
                });
        mod_InfiHybrids.bGoldIceAxe.setIconCoord(5, 4);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.bGoldIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), Item.bone, Character.valueOf('s'), mod_InfiTools.goldChunks
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.bGoldIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), mod_InfiTools.boneRod, Character.valueOf('s'), mod_InfiTools.goldChunks
                });
        mod_InfiHybrids.mGoldIceAxe.setIconCoord(6, 4);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.mGoldIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), mod_InfiTools.mossyRod, Character.valueOf('s'), mod_InfiTools.goldChunks
                });
        mod_InfiHybrids.nGoldIceAxe.setIconCoord(7, 4);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.nGoldIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), mod_InfiTools.netherrackRod, Character.valueOf('s'), mod_InfiTools.goldChunks
                });
        mod_InfiHybrids.glGoldIceAxe.setIconCoord(8, 4);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.glGoldIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), mod_InfiTools.glowstoneRod, Character.valueOf('s'), mod_InfiTools.goldChunks
                });
        mod_InfiHybrids.iceGoldIceAxe.setIconCoord(9, 4);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.iceGoldIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), mod_InfiTools.iceRod, Character.valueOf('s'), mod_InfiTools.goldChunks
                });
        mod_InfiHybrids.sGoldIceAxe.setIconCoord(10, 4);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.sGoldIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), mod_InfiTools.slimeRod, Character.valueOf('s'), mod_InfiTools.goldChunks
                });
        mod_InfiHybrids.fGoldIceAxe.setIconCoord(11, 4);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.fGoldIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), mod_InfiTools.flintRod, Character.valueOf('s'), mod_InfiTools.goldChunks
                });
        mod_InfiHybrids.wRedstoneIceAxe.setIconCoord(0, 5);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.wRedstoneIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), Item.stick, Character.valueOf('s'), mod_InfiTools.redstoneFragment
                });
        mod_InfiHybrids.stRedstoneIceAxe.setIconCoord(1, 5);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.stRedstoneIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), mod_InfiTools.stoneRod, Character.valueOf('s'), mod_InfiTools.redstoneFragment
                });
        mod_InfiHybrids.iRedstoneIceAxe.setIconCoord(2, 5);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.iRedstoneIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), mod_InfiTools.ironRod, Character.valueOf('s'), mod_InfiTools.redstoneFragment
                });
        mod_InfiHybrids.dRedstoneIceAxe.setIconCoord(3, 5);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.dRedstoneIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), mod_InfiTools.diamondRod, Character.valueOf('s'), mod_InfiTools.redstoneFragment
                });
        mod_InfiHybrids.rRedstoneIceAxe.setIconCoord(4, 5);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.rRedstoneIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), mod_InfiTools.redstoneRod, Character.valueOf('s'), mod_InfiTools.redstoneFragment
                });
        mod_InfiHybrids.oRedstoneIceAxe.setIconCoord(5, 5);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.oRedstoneIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), mod_InfiTools.obsidianRod, Character.valueOf('s'), mod_InfiTools.redstoneFragment
                });
        mod_InfiHybrids.bRedstoneIceAxe.setIconCoord(6, 5);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.bRedstoneIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), Item.bone, Character.valueOf('s'), mod_InfiTools.redstoneFragment
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.bRedstoneIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), mod_InfiTools.boneRod, Character.valueOf('s'), mod_InfiTools.redstoneFragment
                });
        mod_InfiHybrids.mRedstoneIceAxe.setIconCoord(7, 5);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.mRedstoneIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), mod_InfiTools.mossyRod, Character.valueOf('s'), mod_InfiTools.redstoneFragment
                });
        mod_InfiHybrids.glRedstoneIceAxe.setIconCoord(8, 5);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.glRedstoneIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), mod_InfiTools.glowstoneRod, Character.valueOf('s'), mod_InfiTools.redstoneFragment
                });
        mod_InfiHybrids.sRedstoneIceAxe.setIconCoord(9, 5);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.sRedstoneIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), mod_InfiTools.slimeRod, Character.valueOf('s'), mod_InfiTools.redstoneFragment
                });
        mod_InfiHybrids.blRedstoneIceAxe.setIconCoord(10, 5);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.blRedstoneIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), Item.blazeRod, Character.valueOf('s'), mod_InfiTools.redstoneFragment
                });
        mod_InfiHybrids.wObsidianIceAxe.setIconCoord(0, 6);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.wObsidianIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), Item.stick, Character.valueOf('s'), mod_InfiTools.obsidianShard
                });
        mod_InfiHybrids.stObsidianIceAxe.setIconCoord(1, 6);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.stObsidianIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), mod_InfiTools.stoneRod, Character.valueOf('s'), mod_InfiTools.obsidianShard
                });
        mod_InfiHybrids.iObsidianIceAxe.setIconCoord(2, 6);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.iObsidianIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), mod_InfiTools.ironRod, Character.valueOf('s'), mod_InfiTools.obsidianShard
                });
        mod_InfiHybrids.dObsidianIceAxe.setIconCoord(3, 6);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.dObsidianIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), mod_InfiTools.diamondRod, Character.valueOf('s'), mod_InfiTools.obsidianShard
                });
        mod_InfiHybrids.gObsidianIceAxe.setIconCoord(4, 6);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.gObsidianIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), mod_InfiTools.goldRod, Character.valueOf('s'), mod_InfiTools.obsidianShard
                });
        mod_InfiHybrids.rObsidianIceAxe.setIconCoord(5, 6);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.rObsidianIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), mod_InfiTools.redstoneRod, Character.valueOf('s'), mod_InfiTools.obsidianShard
                });
        mod_InfiHybrids.oObsidianIceAxe.setIconCoord(6, 6);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.oObsidianIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), mod_InfiTools.obsidianRod, Character.valueOf('s'), mod_InfiTools.obsidianShard
                });
        mod_InfiHybrids.bObsidianIceAxe.setIconCoord(7, 6);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.bObsidianIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), Item.bone, Character.valueOf('s'), mod_InfiTools.obsidianShard
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.bObsidianIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), mod_InfiTools.boneRod, Character.valueOf('s'), mod_InfiTools.obsidianShard
                });
        mod_InfiHybrids.nObsidianIceAxe.setIconCoord(8, 6);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.nObsidianIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), mod_InfiTools.netherrackRod, Character.valueOf('s'), mod_InfiTools.obsidianShard
                });
        mod_InfiHybrids.glObsidianIceAxe.setIconCoord(9, 6);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.glObsidianIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), mod_InfiTools.glowstoneRod, Character.valueOf('s'), mod_InfiTools.obsidianShard
                });
        mod_InfiHybrids.sObsidianIceAxe.setIconCoord(10, 6);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.sObsidianIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), mod_InfiTools.slimeRod, Character.valueOf('s'), mod_InfiTools.obsidianShard
                });
        mod_InfiHybrids.fObsidianIceAxe.setIconCoord(11, 6);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.fObsidianIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), mod_InfiTools.flintRod, Character.valueOf('s'), mod_InfiTools.obsidianShard
                });
        mod_InfiHybrids.blObsidianIceAxe.setIconCoord(12, 6);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.blObsidianIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), Item.blazeRod, Character.valueOf('s'), mod_InfiTools.obsidianShard
                });
        mod_InfiHybrids.wSandstoneIceAxe.setIconCoord(0, 7);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.wSandstoneIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), Item.stick, Character.valueOf('s'), mod_InfiTools.sandstoneShard
                });
        mod_InfiHybrids.stSandstoneIceAxe.setIconCoord(1, 7);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.stSandstoneIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), mod_InfiTools.stoneRod, Character.valueOf('s'), mod_InfiTools.sandstoneShard
                });
        mod_InfiHybrids.saSandstoneIceAxe.setIconCoord(2, 7);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.saSandstoneIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), mod_InfiTools.sandstoneRod, Character.valueOf('s'), mod_InfiTools.sandstoneShard
                });
        mod_InfiHybrids.bSandstoneIceAxe.setIconCoord(3, 7);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.bSandstoneIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), Item.bone, Character.valueOf('s'), mod_InfiTools.sandstoneShard
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.bSandstoneIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), mod_InfiTools.boneRod, Character.valueOf('s'), mod_InfiTools.sandstoneShard
                });
        mod_InfiHybrids.pSandstoneIceAxe.setIconCoord(4, 7);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.pSandstoneIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), mod_InfiTools.paperRod, Character.valueOf('s'), mod_InfiTools.sandstoneShard
                });
        mod_InfiHybrids.nSandstoneIceAxe.setIconCoord(5, 7);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.nSandstoneIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), mod_InfiTools.netherrackRod, Character.valueOf('s'), mod_InfiTools.sandstoneShard
                });
        mod_InfiHybrids.iceSandstoneIceAxe.setIconCoord(6, 7);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.iceSandstoneIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), mod_InfiTools.iceRod, Character.valueOf('s'), mod_InfiTools.sandstoneShard
                });
        mod_InfiHybrids.sSandstoneIceAxe.setIconCoord(7, 7);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.sSandstoneIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), mod_InfiTools.slimeRod, Character.valueOf('s'), mod_InfiTools.sandstoneShard
                });
        mod_InfiHybrids.cSandstoneIceAxe.setIconCoord(8, 7);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.cSandstoneIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), mod_InfiTools.cactusRod, Character.valueOf('s'), mod_InfiTools.sandstoneShard
                });
        mod_InfiHybrids.fSandstoneIceAxe.setIconCoord(9, 7);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.fSandstoneIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), mod_InfiTools.flintRod, Character.valueOf('s'), mod_InfiTools.sandstoneShard
                });
        mod_InfiHybrids.brSandstoneIceAxe.setIconCoord(10, 7);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.brSandstoneIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), mod_InfiTools.brickRod, Character.valueOf('s'), mod_InfiTools.sandstoneShard
                });
        mod_InfiHybrids.wBoneIceAxe.setIconCoord(0, 8);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.wBoneIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Item.bone, Character.valueOf('|'), Item.stick, Character.valueOf('s'), mod_InfiTools.sandstoneShard, Character.valueOf('s'),
                    new ItemStack(Item.dyePowder, 1, 15)
                });
        mod_InfiHybrids.stBoneIceAxe.setIconCoord(1, 8);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.stBoneIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.stoneRod, Character.valueOf('s'), new ItemStack(Item.dyePowder, 1, 15)
                });
        mod_InfiHybrids.iBoneIceAxe.setIconCoord(2, 8);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.iBoneIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.ironRod, Character.valueOf('s'), new ItemStack(Item.dyePowder, 1, 15)
                });
        mod_InfiHybrids.dBoneIceAxe.setIconCoord(3, 8);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.dBoneIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.diamondRod, Character.valueOf('s'), new ItemStack(Item.dyePowder, 1, 15)
                });
        mod_InfiHybrids.rBoneIceAxe.setIconCoord(4, 8);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.rBoneIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.redstoneRod, Character.valueOf('s'), new ItemStack(Item.dyePowder, 1, 15)
                });
        mod_InfiHybrids.oBoneIceAxe.setIconCoord(5, 8);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.oBoneIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.obsidianRod, Character.valueOf('s'), new ItemStack(Item.dyePowder, 1, 15)
                });
        mod_InfiHybrids.bBoneIceAxe.setIconCoord(6, 8);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.bBoneIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Item.bone, Character.valueOf('|'), Item.bone, Character.valueOf('s'), new ItemStack(Item.dyePowder, 1, 15)
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.bBoneIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.boneRod, Character.valueOf('s'), new ItemStack(Item.dyePowder, 1, 15)
                });
        mod_InfiHybrids.mBoneIceAxe.setIconCoord(7, 8);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.mBoneIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.mossyRod, Character.valueOf('s'), new ItemStack(Item.dyePowder, 1, 15)
                });
        mod_InfiHybrids.nBoneIceAxe.setIconCoord(8, 8);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.nBoneIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.netherrackRod, Character.valueOf('s'), new ItemStack(Item.dyePowder, 1, 15)
                });
        mod_InfiHybrids.glBoneIceAxe.setIconCoord(9, 8);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.glBoneIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.glowstoneRod, Character.valueOf('s'), new ItemStack(Item.dyePowder, 1, 15)
                });
        mod_InfiHybrids.sBoneIceAxe.setIconCoord(10, 8);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.sBoneIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.slimeRod, Character.valueOf('s'), new ItemStack(Item.dyePowder, 1, 15)
                });
        mod_InfiHybrids.cBoneIceAxe.setIconCoord(11, 8);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.cBoneIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.cactusRod, Character.valueOf('s'), new ItemStack(Item.dyePowder, 1, 15)
                });
        mod_InfiHybrids.fBoneIceAxe.setIconCoord(12, 8);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.fBoneIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.flintRod, Character.valueOf('s'), new ItemStack(Item.dyePowder, 1, 15)
                });
        mod_InfiHybrids.brBoneIceAxe.setIconCoord(13, 8);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.brBoneIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.brickRod, Character.valueOf('s'), new ItemStack(Item.dyePowder, 1, 15)
                });
        mod_InfiHybrids.blBoneIceAxe.setIconCoord(14, 8);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.blBoneIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Item.bone, Character.valueOf('|'), Item.blazeRod, Character.valueOf('s'), new ItemStack(Item.dyePowder, 1, 15)
                });
        mod_InfiHybrids.wPaperIceAxe.setIconCoord(0, 9);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.wPaperIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), mod_InfiTools.paperStack, Character.valueOf('|'), Item.stick, Character.valueOf('s'), Item.paper
                });
        mod_InfiHybrids.saPaperIceAxe.setIconCoord(1, 9);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.saPaperIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), mod_InfiTools.paperStack, Character.valueOf('|'), mod_InfiTools.sandstoneRod, Character.valueOf('s'), Item.paper
                });
        mod_InfiHybrids.bPaperIceAxe.setIconCoord(2, 9);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.bPaperIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), mod_InfiTools.paperStack, Character.valueOf('|'), Item.bone, Character.valueOf('s'), Item.paper
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.bPaperIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), mod_InfiTools.paperStack, Character.valueOf('|'), mod_InfiTools.boneRod, Character.valueOf('s'), Item.paper
                });
        mod_InfiHybrids.pPaperIceAxe.setIconCoord(3, 9);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.pPaperIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), mod_InfiTools.paperStack, Character.valueOf('|'), mod_InfiTools.paperRod, Character.valueOf('s'), Item.paper
                });
        mod_InfiHybrids.sPaperIceAxe.setIconCoord(4, 9);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.sPaperIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), mod_InfiTools.paperStack, Character.valueOf('|'), mod_InfiTools.slimeRod, Character.valueOf('s'), Item.paper
                });
        mod_InfiHybrids.cPaperIceAxe.setIconCoord(5, 9);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.cPaperIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), mod_InfiTools.paperStack, Character.valueOf('|'), mod_InfiTools.cactusRod, Character.valueOf('s'), Item.paper
                });
        mod_InfiHybrids.brPaperIceAxe.setIconCoord(6, 9);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.brPaperIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), mod_InfiTools.paperStack, Character.valueOf('|'), mod_InfiTools.brickRod, Character.valueOf('s'), Item.paper
                });
        mod_InfiHybrids.stMossyIceAxe.setIconCoord(0, 10);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.stMossyIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), mod_InfiTools.mossBallCrafted, Character.valueOf('|'), mod_InfiTools.stoneRod, Character.valueOf('s'), mod_InfiTools.mossBallGiant
                });
        mod_InfiHybrids.dMossyIceAxe.setIconCoord(1, 10);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.dMossyIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), mod_InfiTools.mossBallCrafted, Character.valueOf('|'), mod_InfiTools.diamondRod, Character.valueOf('s'), mod_InfiTools.mossBallGiant
                });
        mod_InfiHybrids.rMossyIceAxe.setIconCoord(2, 10);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.rMossyIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), mod_InfiTools.mossBallCrafted, Character.valueOf('|'), mod_InfiTools.redstoneRod, Character.valueOf('s'), mod_InfiTools.mossBallGiant
                });
        mod_InfiHybrids.bMossyIceAxe.setIconCoord(3, 10);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.bMossyIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), mod_InfiTools.mossBallCrafted, Character.valueOf('|'), Item.bone, Character.valueOf('s'), mod_InfiTools.mossBallGiant
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.bMossyIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), mod_InfiTools.mossBallCrafted, Character.valueOf('|'), mod_InfiTools.boneRod, Character.valueOf('s'), mod_InfiTools.mossBallGiant
                });
        mod_InfiHybrids.mMossyIceAxe.setIconCoord(4, 10);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.mMossyIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), mod_InfiTools.mossBallCrafted, Character.valueOf('|'), mod_InfiTools.mossyRod, Character.valueOf('s'), mod_InfiTools.mossBallGiant
                });
        mod_InfiHybrids.glMossyIceAxe.setIconCoord(5, 10);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.glMossyIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), mod_InfiTools.mossBallCrafted, Character.valueOf('|'), mod_InfiTools.glowstoneRod, Character.valueOf('s'), mod_InfiTools.mossBallGiant
                });
        mod_InfiHybrids.wNetherrackIceAxe.setIconCoord(0, 11);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.wNetherrackIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), Item.stick, Character.valueOf('s'), mod_InfiTools.netherrackShard
                });
        mod_InfiHybrids.stNetherrackIceAxe.setIconCoord(1, 11);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.stNetherrackIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.stoneRod, Character.valueOf('s'), mod_InfiTools.netherrackShard
                });
        mod_InfiHybrids.iNetherrackIceAxe.setIconCoord(2, 11);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.iNetherrackIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.ironRod, Character.valueOf('s'), mod_InfiTools.netherrackShard
                });
        mod_InfiHybrids.rNetherrackIceAxe.setIconCoord(3, 11);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.rNetherrackIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.redstoneRod, Character.valueOf('s'), mod_InfiTools.netherrackShard
                });
        mod_InfiHybrids.oNetherrackIceAxe.setIconCoord(4, 11);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.oNetherrackIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.obsidianRod, Character.valueOf('s'), mod_InfiTools.netherrackShard
                });
        mod_InfiHybrids.saNetherrackIceAxe.setIconCoord(5, 11);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.saNetherrackIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.sandstoneRod, Character.valueOf('s'), mod_InfiTools.netherrackShard
                });
        mod_InfiHybrids.bNetherrackIceAxe.setIconCoord(6, 11);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.bNetherrackIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), Item.bone, Character.valueOf('s'), mod_InfiTools.netherrackShard
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.bNetherrackIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.boneRod, Character.valueOf('s'), mod_InfiTools.netherrackShard
                });
        mod_InfiHybrids.mNetherrackIceAxe.setIconCoord(7, 11);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.mNetherrackIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.mossyRod, Character.valueOf('s'), mod_InfiTools.netherrackShard
                });
        mod_InfiHybrids.nNetherrackIceAxe.setIconCoord(8, 11);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.nNetherrackIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.netherrackRod, Character.valueOf('s'), mod_InfiTools.netherrackShard
                });
        mod_InfiHybrids.glNetherrackIceAxe.setIconCoord(9, 11);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.glNetherrackIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.glowstoneRod, Character.valueOf('s'), mod_InfiTools.netherrackShard
                });
        mod_InfiHybrids.iceNetherrackIceAxe.setIconCoord(10, 11);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.iceNetherrackIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.iceRod, Character.valueOf('s'), mod_InfiTools.netherrackShard
                });
        mod_InfiHybrids.sNetherrackIceAxe.setIconCoord(11, 11);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.sNetherrackIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.slimeRod, Character.valueOf('s'), mod_InfiTools.netherrackShard
                });
        mod_InfiHybrids.cNetherrackIceAxe.setIconCoord(12, 11);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.cNetherrackIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.cactusRod, Character.valueOf('s'), mod_InfiTools.netherrackShard
                });
        mod_InfiHybrids.fNetherrackIceAxe.setIconCoord(13, 11);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.fNetherrackIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.cactusRod, Character.valueOf('s'), mod_InfiTools.netherrackShard
                });
        mod_InfiHybrids.brNetherrackIceAxe.setIconCoord(14, 11);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.brNetherrackIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.cactusRod, Character.valueOf('s'), mod_InfiTools.netherrackShard
                });
        mod_InfiHybrids.blNetherrackIceAxe.setIconCoord(15, 11);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.blNetherrackIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.cactusRod, Character.valueOf('s'), mod_InfiTools.netherrackShard
                });
        mod_InfiHybrids.wGlowstoneIceAxe.setIconCoord(0, 12);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.wGlowstoneIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), Item.stick, Character.valueOf('s'), mod_InfiTools.glowstoneFragment
                });
        mod_InfiHybrids.stGlowstoneIceAxe.setIconCoord(1, 12);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.stGlowstoneIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.stoneRod, Character.valueOf('s'), mod_InfiTools.glowstoneFragment
                });
        mod_InfiHybrids.iGlowstoneIceAxe.setIconCoord(2, 12);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.iGlowstoneIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.ironRod, Character.valueOf('s'), mod_InfiTools.glowstoneFragment
                });
        mod_InfiHybrids.dGlowstoneIceAxe.setIconCoord(3, 12);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.dGlowstoneIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.diamondRod, Character.valueOf('s'), mod_InfiTools.glowstoneFragment
                });
        mod_InfiHybrids.rGlowstoneIceAxe.setIconCoord(4, 12);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.rGlowstoneIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.redstoneRod, Character.valueOf('s'), mod_InfiTools.glowstoneFragment
                });
        mod_InfiHybrids.oGlowstoneIceAxe.setIconCoord(5, 12);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.oGlowstoneIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.obsidianRod, Character.valueOf('s'), mod_InfiTools.glowstoneFragment
                });
        mod_InfiHybrids.bGlowstoneIceAxe.setIconCoord(6, 12);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.bGlowstoneIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), Item.bone, Character.valueOf('s'), mod_InfiTools.glowstoneFragment
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.bGlowstoneIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.boneRod, Character.valueOf('s'), mod_InfiTools.glowstoneFragment
                });
        mod_InfiHybrids.mGlowstoneIceAxe.setIconCoord(7, 12);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.mGlowstoneIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.mossyRod, Character.valueOf('s'), mod_InfiTools.glowstoneFragment
                });
        mod_InfiHybrids.nGlowstoneIceAxe.setIconCoord(8, 12);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.nGlowstoneIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.netherrackRod, Character.valueOf('s'), mod_InfiTools.glowstoneFragment
                });
        mod_InfiHybrids.glGlowstoneIceAxe.setIconCoord(9, 12);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.glGlowstoneIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.glowstoneRod, Character.valueOf('s'), mod_InfiTools.glowstoneFragment
                });
        mod_InfiHybrids.iceGlowstoneIceAxe.setIconCoord(10, 12);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.iceGlowstoneIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.iceRod, Character.valueOf('s'), mod_InfiTools.glowstoneFragment
                });
        mod_InfiHybrids.lGlowstoneIceAxe.setIconCoord(11, 12);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.lGlowstoneIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.glowstoneRod, Character.valueOf('s'), mod_InfiTools.glowstoneFragment
                });
        mod_InfiHybrids.sGlowstoneIceAxe.setIconCoord(12, 12);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.sGlowstoneIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.slimeRod, Character.valueOf('s'), mod_InfiTools.glowstoneFragment
                });
        mod_InfiHybrids.blGlowstoneIceAxe.setIconCoord(13, 12);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.blGlowstoneIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), Item.blazeRod, Character.valueOf('s'), mod_InfiTools.glowstoneFragment
                });
        mod_InfiHybrids.wIceIceAxe.setIconCoord(0, 13);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.wIceIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Block.ice, Character.valueOf('|'), Item.stick, Character.valueOf('s'), mod_InfiTools.iceShard
                });
        mod_InfiHybrids.stIceIceAxe.setIconCoord(1, 13);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.stIceIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.stoneRod, Character.valueOf('s'), mod_InfiTools.iceShard
                });
        mod_InfiHybrids.iIceIceAxe.setIconCoord(2, 13);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.iIceIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.ironRod, Character.valueOf('s'), mod_InfiTools.iceShard
                });
        mod_InfiHybrids.dIceIceAxe.setIconCoord(3, 13);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.dIceIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.diamondRod, Character.valueOf('s'), mod_InfiTools.iceShard
                });
        mod_InfiHybrids.gIceIceAxe.setIconCoord(4, 13);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.gIceIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.goldRod, Character.valueOf('s'), mod_InfiTools.iceShard
                });
        mod_InfiHybrids.rIceIceAxe.setIconCoord(5, 13);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.rIceIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.redstoneRod, Character.valueOf('s'), mod_InfiTools.iceShard
                });
        mod_InfiHybrids.oIceIceAxe.setIconCoord(6, 13);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.oIceIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.obsidianRod, Character.valueOf('s'), mod_InfiTools.iceShard
                });
        mod_InfiHybrids.saIceIceAxe.setIconCoord(7, 13);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.saIceIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.sandstoneRod, Character.valueOf('s'), mod_InfiTools.iceShard
                });
        mod_InfiHybrids.bIceIceAxe.setIconCoord(8, 13);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.bIceIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Block.ice, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.bIceIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.boneRod, Character.valueOf('s'), mod_InfiTools.iceShard
                });
        mod_InfiHybrids.glIceIceAxe.setIconCoord(9, 13);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.glIceIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.glowstoneRod, Character.valueOf('s'), mod_InfiTools.iceShard
                });
        mod_InfiHybrids.iceIceIceAxe.setIconCoord(10, 13);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.iceIceIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.iceRod, Character.valueOf('s'), mod_InfiTools.iceShard
                });
        mod_InfiHybrids.sIceIceAxe.setIconCoord(11, 13);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.sIceIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.slimeRod, Character.valueOf('s'), mod_InfiTools.iceShard
                });
        mod_InfiHybrids.cIceIceAxe.setIconCoord(12, 13);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.cIceIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.cactusRod, Character.valueOf('s'), mod_InfiTools.iceShard
                });
        mod_InfiHybrids.fIceIceAxe.setIconCoord(13, 13);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.fIceIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.flintRod, Character.valueOf('s'), mod_InfiTools.iceShard
                });
        mod_InfiHybrids.brIceIceAxe.setIconCoord(14, 13);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.brIceIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.brickRod, Character.valueOf('s'), mod_InfiTools.iceShard
                });
        mod_InfiHybrids.dLavaIceAxe.setIconCoord(0, 14);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.dLavaIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), mod_InfiTools.lavaCrystal, Character.valueOf('|'), mod_InfiTools.diamondRod, Character.valueOf('s'), mod_InfiTools.lavaFragment
                });
        mod_InfiHybrids.rLavaIceAxe.setIconCoord(1, 14);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.rLavaIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), mod_InfiTools.lavaCrystal, Character.valueOf('|'), mod_InfiTools.redstoneRod, Character.valueOf('s'), mod_InfiTools.lavaFragment
                });
        mod_InfiHybrids.bLavaIceAxe.setIconCoord(2, 14);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.bLavaIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), mod_InfiTools.lavaCrystal, Character.valueOf('|'), Item.bone, Character.valueOf('s'), mod_InfiTools.lavaFragment
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.bLavaIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), mod_InfiTools.lavaCrystal, Character.valueOf('|'), mod_InfiTools.boneRod, Character.valueOf('s'), mod_InfiTools.lavaFragment
                });
        mod_InfiHybrids.nLavaIceAxe.setIconCoord(3, 14);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.nLavaIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), mod_InfiTools.lavaCrystal, Character.valueOf('|'), mod_InfiTools.netherrackRod, Character.valueOf('s'), mod_InfiTools.lavaFragment
                });
        mod_InfiHybrids.glLavaIceAxe.setIconCoord(4, 14);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.glLavaIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), mod_InfiTools.lavaCrystal, Character.valueOf('|'), mod_InfiTools.glowstoneRod, Character.valueOf('s'), mod_InfiTools.lavaFragment
                });
        mod_InfiHybrids.lLavaIceAxe.setIconCoord(5, 14);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.lLavaIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), mod_InfiTools.lavaCrystal, Character.valueOf('|'), mod_InfiTools.lavaRod, Character.valueOf('s'), mod_InfiTools.lavaFragment
                });
        mod_InfiHybrids.blLavaIceAxe.setIconCoord(6, 14);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.blLavaIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), mod_InfiTools.lavaCrystal, Character.valueOf('|'), Item.blazeRod, Character.valueOf('s'), mod_InfiTools.lavaFragment
                });
        mod_InfiHybrids.wSlimeIceAxe.setIconCoord(0, 15);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.wSlimeIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), Item.stick, Character.valueOf('s'), mod_InfiTools.slimeFragment
                });
        mod_InfiHybrids.stSlimeIceAxe.setIconCoord(1, 15);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.stSlimeIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.stoneRod, Character.valueOf('s'), mod_InfiTools.slimeFragment
                });
        mod_InfiHybrids.iSlimeIceAxe.setIconCoord(2, 15);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.iSlimeIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.ironRod, Character.valueOf('s'), mod_InfiTools.slimeFragment
                });
        mod_InfiHybrids.dSlimeIceAxe.setIconCoord(3, 15);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.dSlimeIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.diamondRod, Character.valueOf('s'), mod_InfiTools.slimeFragment
                });
        mod_InfiHybrids.gSlimeIceAxe.setIconCoord(4, 15);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.gSlimeIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.goldRod, Character.valueOf('s'), mod_InfiTools.slimeFragment
                });
        mod_InfiHybrids.rSlimeIceAxe.setIconCoord(5, 15);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.rSlimeIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.redstoneRod, Character.valueOf('s'), mod_InfiTools.slimeFragment
                });
        mod_InfiHybrids.oSlimeIceAxe.setIconCoord(6, 15);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.oSlimeIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.obsidianRod, Character.valueOf('s'), mod_InfiTools.slimeFragment
                });
        mod_InfiHybrids.saSlimeIceAxe.setIconCoord(7, 15);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.saSlimeIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.sandstoneRod, Character.valueOf('s'), mod_InfiTools.slimeFragment
                });
        mod_InfiHybrids.bSlimeIceAxe.setIconCoord(8, 15);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.bSlimeIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), Item.bone, Character.valueOf('s'), mod_InfiTools.slimeFragment
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.bSlimeIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.boneRod, Character.valueOf('s'), mod_InfiTools.slimeFragment
                });
        mod_InfiHybrids.pSlimeIceAxe.setIconCoord(9, 15);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.pSlimeIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.paperRod, Character.valueOf('s'), mod_InfiTools.slimeFragment
                });
        mod_InfiHybrids.mSlimeIceAxe.setIconCoord(10, 15);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.mSlimeIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.mossyRod, Character.valueOf('s'), mod_InfiTools.slimeFragment
                });
        mod_InfiHybrids.nSlimeIceAxe.setIconCoord(11, 15);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.nSlimeIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.netherrackRod, Character.valueOf('s'), mod_InfiTools.slimeFragment
                });
        mod_InfiHybrids.glSlimeIceAxe.setIconCoord(12, 15);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.glSlimeIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.glowstoneRod, Character.valueOf('s'), mod_InfiTools.slimeFragment
                });
        mod_InfiHybrids.iceSlimeIceAxe.setIconCoord(13, 15);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.iceSlimeIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.iceRod, Character.valueOf('s'), mod_InfiTools.slimeFragment
                });
        mod_InfiHybrids.lSlimeIceAxe.setIconCoord(14, 15);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.lSlimeIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.lavaRod, Character.valueOf('s'), mod_InfiTools.slimeFragment
                });
        mod_InfiHybrids.sSlimeIceAxe.setIconCoord(15, 15);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.sSlimeIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.slimeRod, Character.valueOf('s'), mod_InfiTools.slimeFragment
                });
        mod_InfiHybrids.cSlimeIceAxe.setIconCoord(12, 14);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.cSlimeIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.cactusRod, Character.valueOf('s'), mod_InfiTools.slimeFragment
                });
        mod_InfiHybrids.fSlimeIceAxe.setIconCoord(13, 14);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.fSlimeIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.flintRod, Character.valueOf('s'), mod_InfiTools.slimeFragment
                });
        mod_InfiHybrids.brSlimeIceAxe.setIconCoord(14, 14);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.brSlimeIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.brickRod, Character.valueOf('s'), mod_InfiTools.slimeFragment
                });
        mod_InfiHybrids.blSlimeIceAxe.setIconCoord(15, 14);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.blSlimeIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), Item.blazeRod, Character.valueOf('s'), mod_InfiTools.slimeFragment
                });
        mod_InfiHybrids.wCactusIceAxe.setIconCoord(10, 0);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.wCactusIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Block.cactus, Character.valueOf('|'), Item.stick, Character.valueOf('s'), new ItemStack(Item.dyePowder, 1, 2)
                });
        mod_InfiHybrids.stCactusIceAxe.setIconCoord(11, 0);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.stCactusIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Block.cactus, Character.valueOf('|'), mod_InfiTools.stoneRod, Character.valueOf('s'), new ItemStack(Item.dyePowder, 1, 2)
                });
        mod_InfiHybrids.saCactusIceAxe.setIconCoord(12, 0);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.saCactusIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Block.cactus, Character.valueOf('|'), mod_InfiTools.sandstoneRod, Character.valueOf('s'), new ItemStack(Item.dyePowder, 1, 2)
                });
        mod_InfiHybrids.bCactusIceAxe.setIconCoord(13, 0);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.bCactusIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Block.cactus, Character.valueOf('|'), Item.bone, Character.valueOf('s'), new ItemStack(Item.dyePowder, 1, 2)
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.bCactusIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Block.cactus, Character.valueOf('|'), mod_InfiTools.boneRod, Character.valueOf('s'), new ItemStack(Item.dyePowder, 1, 2)
                });
        mod_InfiHybrids.pCactusIceAxe.setIconCoord(14, 0);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.pCactusIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Block.cactus, Character.valueOf('|'), mod_InfiTools.paperRod, Character.valueOf('s'), new ItemStack(Item.dyePowder, 1, 2)
                });
        mod_InfiHybrids.nCactusIceAxe.setIconCoord(15, 0);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.nCactusIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Block.cactus, Character.valueOf('|'), mod_InfiTools.netherrackRod, Character.valueOf('s'), new ItemStack(Item.dyePowder, 1, 2)
                });
        mod_InfiHybrids.sCactusIceAxe.setIconCoord(12, 1);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.sCactusIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Block.cactus, Character.valueOf('|'), mod_InfiTools.slimeRod, Character.valueOf('s'), new ItemStack(Item.dyePowder, 1, 2)
                });
        mod_InfiHybrids.cCactusIceAxe.setIconCoord(13, 1);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.cCactusIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Block.cactus, Character.valueOf('|'), mod_InfiTools.cactusRod, Character.valueOf('s'), new ItemStack(Item.dyePowder, 1, 2)
                });
        mod_InfiHybrids.fCactusIceAxe.setIconCoord(14, 1);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.fCactusIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Block.cactus, Character.valueOf('|'), mod_InfiTools.flintRod, Character.valueOf('s'), new ItemStack(Item.dyePowder, 1, 2)
                });
        mod_InfiHybrids.brCactusIceAxe.setIconCoord(15, 1);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.brCactusIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Block.cactus, Character.valueOf('|'), mod_InfiTools.brickRod, Character.valueOf('s'), new ItemStack(Item.dyePowder, 1, 2)
                });
        mod_InfiHybrids.wFlintIceAxe.setIconCoord(13, 2);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.wFlintIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Item.flint, Character.valueOf('|'), Item.stick, Character.valueOf('s'), mod_InfiTools.flintShard
                });
        mod_InfiHybrids.stFlintIceAxe.setIconCoord(14, 2);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.stFlintIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.stoneRod, Character.valueOf('s'), mod_InfiTools.flintShard
                });
        mod_InfiHybrids.iFlintIceAxe.setIconCoord(15, 2);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.iFlintIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.ironRod, Character.valueOf('s'), mod_InfiTools.flintShard
                });
        mod_InfiHybrids.gFlintIceAxe.setIconCoord(12, 3);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.gFlintIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.goldRod, Character.valueOf('s'), mod_InfiTools.flintShard
                });
        mod_InfiHybrids.oFlintIceAxe.setIconCoord(13, 3);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.oFlintIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.obsidianRod, Character.valueOf('s'), mod_InfiTools.flintShard
                });
        mod_InfiHybrids.saFlintIceAxe.setIconCoord(14, 3);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.saFlintIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.sandstoneRod, Character.valueOf('s'), mod_InfiTools.flintShard
                });
        mod_InfiHybrids.bFlintIceAxe.setIconCoord(15, 3);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.bFlintIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Item.flint, Character.valueOf('|'), Item.bone, Character.valueOf('s'), mod_InfiTools.flintShard
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.bFlintIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.boneRod, Character.valueOf('s'), mod_InfiTools.flintShard
                });
        mod_InfiHybrids.nFlintIceAxe.setIconCoord(12, 4);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.nFlintIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.netherrackRod, Character.valueOf('s'), mod_InfiTools.flintShard
                });
        mod_InfiHybrids.iceFlintIceAxe.setIconCoord(13, 4);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.iceFlintIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.iceRod, Character.valueOf('s'), mod_InfiTools.flintShard
                });
        mod_InfiHybrids.sFlintIceAxe.setIconCoord(14, 4);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.sFlintIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.slimeRod, Character.valueOf('s'), mod_InfiTools.flintShard
                });
        mod_InfiHybrids.cFlintIceAxe.setIconCoord(15, 4);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.cFlintIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.cactusRod, Character.valueOf('s'), mod_InfiTools.flintShard
                });
        mod_InfiHybrids.fFlintIceAxe.setIconCoord(11, 5);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.fFlintIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.flintRod, Character.valueOf('s'), mod_InfiTools.flintShard
                });
        mod_InfiHybrids.brFlintIceAxe.setIconCoord(12, 5);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.brFlintIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.brickRod, Character.valueOf('s'), mod_InfiTools.flintShard
                });
        mod_InfiHybrids.blFlintIceAxe.setIconCoord(13, 5);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.blFlintIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Item.flint, Character.valueOf('|'), Item.blazeRod, Character.valueOf('s'), mod_InfiTools.flintShard
                });
        mod_InfiHybrids.wBrickIceAxe.setIconCoord(14, 5);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.wBrickIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Item.brick, Character.valueOf('|'), Item.brick, Character.valueOf('s'), mod_InfiTools.miniBrick
                });
        mod_InfiHybrids.stBrickIceAxe.setIconCoord(15, 5);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.stBrickIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Item.brick, Character.valueOf('|'), mod_InfiTools.stoneRod, Character.valueOf('s'), mod_InfiTools.miniBrick
                });
        mod_InfiHybrids.saBrickIceAxe.setIconCoord(13, 6);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.saBrickIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Item.brick, Character.valueOf('|'), mod_InfiTools.sandstoneRod, Character.valueOf('s'), mod_InfiTools.miniBrick
                });
        mod_InfiHybrids.bBrickIceAxe.setIconCoord(14, 6);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.bBrickIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Item.brick, Character.valueOf('|'), Item.bone, Character.valueOf('s'), mod_InfiTools.miniBrick
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.bBrickIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Item.brick, Character.valueOf('|'), mod_InfiTools.boneRod, Character.valueOf('s'), mod_InfiTools.miniBrick
                });
        mod_InfiHybrids.pBrickIceAxe.setIconCoord(15, 6);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.pBrickIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Item.brick, Character.valueOf('|'), mod_InfiTools.paperRod, Character.valueOf('s'), mod_InfiTools.miniBrick
                });
        mod_InfiHybrids.nBrickIceAxe.setIconCoord(11, 7);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.nBrickIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Item.brick, Character.valueOf('|'), mod_InfiTools.netherrackRod, Character.valueOf('s'), mod_InfiTools.miniBrick
                });
        mod_InfiHybrids.iceBrickIceAxe.setIconCoord(12, 7);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.iceBrickIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Item.brick, Character.valueOf('|'), mod_InfiTools.iceRod, Character.valueOf('s'), mod_InfiTools.miniBrick
                });
        mod_InfiHybrids.sBrickIceAxe.setIconCoord(13, 7);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.sBrickIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Item.brick, Character.valueOf('|'), mod_InfiTools.slimeRod, Character.valueOf('s'), mod_InfiTools.miniBrick
                });
        mod_InfiHybrids.cBrickIceAxe.setIconCoord(14, 7);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.cBrickIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Item.brick, Character.valueOf('|'), mod_InfiTools.cactusRod, Character.valueOf('s'), mod_InfiTools.miniBrick
                });
        mod_InfiHybrids.fBrickIceAxe.setIconCoord(15, 7);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.fBrickIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Item.brick, Character.valueOf('|'), mod_InfiTools.flintRod, Character.valueOf('s'), mod_InfiTools.miniBrick
                });
        mod_InfiHybrids.brBrickIceAxe.setIconCoord(15, 8);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.brBrickIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), Item.brick, Character.valueOf('|'), mod_InfiTools.brickRod, Character.valueOf('s'), mod_InfiTools.miniBrick
                });
        mod_InfiHybrids.dBlazeIceAxe.setIconCoord(7, 9);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.dBlazeIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), mod_InfiTools.blazeCrystal, Character.valueOf('|'), mod_InfiTools.diamondRod, Character.valueOf('s'), mod_InfiTools.blazeFragment
                });
        mod_InfiHybrids.rBlazeIceAxe.setIconCoord(8, 9);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.rBlazeIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), mod_InfiTools.blazeCrystal, Character.valueOf('|'), mod_InfiTools.redstoneRod, Character.valueOf('s'), mod_InfiTools.blazeFragment
                });
        mod_InfiHybrids.bBlazeIceAxe.setIconCoord(9, 9);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.bBlazeIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), mod_InfiTools.blazeCrystal, Character.valueOf('|'), Item.bone, Character.valueOf('s'), mod_InfiTools.blazeFragment
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.bBlazeIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), mod_InfiTools.blazeCrystal, Character.valueOf('|'), mod_InfiTools.boneRod, Character.valueOf('s'), mod_InfiTools.blazeFragment
                });
        mod_InfiHybrids.nBlazeIceAxe.setIconCoord(10, 9);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.nBlazeIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), mod_InfiTools.blazeCrystal, Character.valueOf('|'), mod_InfiTools.netherrackRod, Character.valueOf('s'), mod_InfiTools.blazeFragment
                });
        mod_InfiHybrids.glBlazeIceAxe.setIconCoord(11, 9);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.glBlazeIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), mod_InfiTools.blazeCrystal, Character.valueOf('|'), mod_InfiTools.glowstoneRod, Character.valueOf('s'), mod_InfiTools.blazeFragment
                });
        mod_InfiHybrids.lBlazeIceAxe.setIconCoord(12, 9);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.lBlazeIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), mod_InfiTools.blazeCrystal, Character.valueOf('|'), Item.blazeRod, Character.valueOf('s'), mod_InfiTools.blazeFragment
                });
        mod_InfiHybrids.fBlazeIceAxe.setIconCoord(13, 9);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.fBlazeIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), mod_InfiTools.blazeCrystal, Character.valueOf('|'), Item.blazeRod, Character.valueOf('s'), mod_InfiTools.blazeFragment
                });
        mod_InfiHybrids.blBlazeIceAxe.setIconCoord(14, 9);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiHybrids.blBlazeIceAxe, 1), new Object[]
                {
                    "mms", " | ", " | ", Character.valueOf('m'), mod_InfiTools.blazeCrystal, Character.valueOf('|'), Item.blazeRod, Character.valueOf('s'), mod_InfiTools.blazeFragment
                });
        MinecraftForge.setToolClass(mod_InfiHybrids.wWoodIceAxe, "pickaxe", mod_InfiTools.wLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.stWoodIceAxe, "pickaxe", mod_InfiTools.wLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.saWoodIceAxe, "pickaxe", mod_InfiTools.wLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.bWoodIceAxe, "pickaxe", mod_InfiTools.wLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.pWoodIceAxe, "pickaxe", mod_InfiTools.wLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.nWoodIceAxe, "pickaxe", mod_InfiTools.wLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.sWoodIceAxe, "pickaxe", mod_InfiTools.wLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.cWoodIceAxe, "pickaxe", mod_InfiTools.wLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.brWoodIceAxe, "pickaxe", mod_InfiTools.wLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.wStoneIceAxe, "pickaxe", mod_InfiTools.stLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.stStoneIceAxe, "pickaxe", mod_InfiTools.stLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.saStoneIceAxe, "pickaxe", mod_InfiTools.stLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.bStoneIceAxe, "pickaxe", mod_InfiTools.stLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.mStoneIceAxe, "pickaxe", mod_InfiTools.stLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.nStoneIceAxe, "pickaxe", mod_InfiTools.stLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.iceStoneIceAxe, "pickaxe", mod_InfiTools.stLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.sStoneIceAxe, "pickaxe", mod_InfiTools.stLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.cStoneIceAxe, "pickaxe", mod_InfiTools.stLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.fStoneIceAxe, "pickaxe", mod_InfiTools.stLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.brStoneIceAxe, "pickaxe", mod_InfiTools.stLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.wIronIceAxe, "pickaxe", mod_InfiTools.iLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.stIronIceAxe, "pickaxe", mod_InfiTools.iLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.iIronIceAxe, "pickaxe", mod_InfiTools.iLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.dIronIceAxe, "pickaxe", mod_InfiTools.iLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.gIronIceAxe, "pickaxe", mod_InfiTools.iLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.rIronIceAxe, "pickaxe", mod_InfiTools.iLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.oIronIceAxe, "pickaxe", mod_InfiTools.iLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.bIronIceAxe, "pickaxe", mod_InfiTools.iLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.nIronIceAxe, "pickaxe", mod_InfiTools.iLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.glIronIceAxe, "pickaxe", mod_InfiTools.iLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.iceIronIceAxe, "pickaxe", mod_InfiTools.iLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.sIronIceAxe, "pickaxe", mod_InfiTools.iLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.blIronIceAxe, "pickaxe", mod_InfiTools.iLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.wDiamondIceAxe, "pickaxe", mod_InfiTools.dLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.stDiamondIceAxe, "pickaxe", mod_InfiTools.dLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.iDiamondIceAxe, "pickaxe", mod_InfiTools.dLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.dDiamondIceAxe, "pickaxe", mod_InfiTools.dLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.gDiamondIceAxe, "pickaxe", mod_InfiTools.dLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.rDiamondIceAxe, "pickaxe", mod_InfiTools.dLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.oDiamondIceAxe, "pickaxe", mod_InfiTools.dLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.bDiamondIceAxe, "pickaxe", mod_InfiTools.dLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.mDiamondIceAxe, "pickaxe", mod_InfiTools.dLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.nDiamondIceAxe, "pickaxe", mod_InfiTools.dLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.glDiamondIceAxe, "pickaxe", mod_InfiTools.dLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.blDiamondIceAxe, "pickaxe", mod_InfiTools.dLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.wGoldIceAxe, "pickaxe", mod_InfiTools.gLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.stGoldIceAxe, "pickaxe", mod_InfiTools.gLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.gGoldIceAxe, "pickaxe", mod_InfiTools.gLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.oGoldIceAxe, "pickaxe", mod_InfiTools.gLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.saGoldIceAxe, "pickaxe", mod_InfiTools.gLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.bGoldIceAxe, "pickaxe", mod_InfiTools.gLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.mGoldIceAxe, "pickaxe", mod_InfiTools.gLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.nGoldIceAxe, "pickaxe", mod_InfiTools.gLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.glGoldIceAxe, "pickaxe", mod_InfiTools.gLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.iceGoldIceAxe, "pickaxe", mod_InfiTools.gLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.sGoldIceAxe, "pickaxe", mod_InfiTools.gLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.fGoldIceAxe, "pickaxe", mod_InfiTools.gLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.wRedstoneIceAxe, "pickaxe", mod_InfiTools.rLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.stRedstoneIceAxe, "pickaxe", mod_InfiTools.rLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.iRedstoneIceAxe, "pickaxe", mod_InfiTools.rLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.dRedstoneIceAxe, "pickaxe", mod_InfiTools.rLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.rRedstoneIceAxe, "pickaxe", mod_InfiTools.rLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.oRedstoneIceAxe, "pickaxe", mod_InfiTools.rLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.bRedstoneIceAxe, "pickaxe", mod_InfiTools.rLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.mRedstoneIceAxe, "pickaxe", mod_InfiTools.rLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.glRedstoneIceAxe, "pickaxe", mod_InfiTools.rLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.sRedstoneIceAxe, "pickaxe", mod_InfiTools.rLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.blRedstoneIceAxe, "pickaxe", mod_InfiTools.rLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.wObsidianIceAxe, "pickaxe", mod_InfiTools.oLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.stObsidianIceAxe, "pickaxe", mod_InfiTools.oLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.iObsidianIceAxe, "pickaxe", mod_InfiTools.oLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.dObsidianIceAxe, "pickaxe", mod_InfiTools.oLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.gObsidianIceAxe, "pickaxe", mod_InfiTools.oLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.rObsidianIceAxe, "pickaxe", mod_InfiTools.oLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.oObsidianIceAxe, "pickaxe", mod_InfiTools.oLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.bObsidianIceAxe, "pickaxe", mod_InfiTools.oLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.nObsidianIceAxe, "pickaxe", mod_InfiTools.oLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.glObsidianIceAxe, "pickaxe", mod_InfiTools.oLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.sObsidianIceAxe, "pickaxe", mod_InfiTools.oLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.fObsidianIceAxe, "pickaxe", mod_InfiTools.oLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.blObsidianIceAxe, "pickaxe", mod_InfiTools.oLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.wSandstoneIceAxe, "pickaxe", mod_InfiTools.saLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.stSandstoneIceAxe, "pickaxe", mod_InfiTools.saLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.saSandstoneIceAxe, "pickaxe", mod_InfiTools.saLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.bSandstoneIceAxe, "pickaxe", mod_InfiTools.saLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.pSandstoneIceAxe, "pickaxe", mod_InfiTools.saLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.nSandstoneIceAxe, "pickaxe", mod_InfiTools.saLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.iceSandstoneIceAxe, "pickaxe", mod_InfiTools.saLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.sSandstoneIceAxe, "pickaxe", mod_InfiTools.saLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.cSandstoneIceAxe, "pickaxe", mod_InfiTools.saLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.fSandstoneIceAxe, "pickaxe", mod_InfiTools.saLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.brSandstoneIceAxe, "pickaxe", mod_InfiTools.saLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.wBoneIceAxe, "pickaxe", mod_InfiTools.bLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.stBoneIceAxe, "pickaxe", mod_InfiTools.bLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.iBoneIceAxe, "pickaxe", mod_InfiTools.bLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.dBoneIceAxe, "pickaxe", mod_InfiTools.bLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.rBoneIceAxe, "pickaxe", mod_InfiTools.bLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.oBoneIceAxe, "pickaxe", mod_InfiTools.bLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.bBoneIceAxe, "pickaxe", mod_InfiTools.bLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.mBoneIceAxe, "pickaxe", mod_InfiTools.bLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.nBoneIceAxe, "pickaxe", mod_InfiTools.bLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.glBoneIceAxe, "pickaxe", mod_InfiTools.bLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.sBoneIceAxe, "pickaxe", mod_InfiTools.bLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.cBoneIceAxe, "pickaxe", mod_InfiTools.bLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.fBoneIceAxe, "pickaxe", mod_InfiTools.bLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.brBoneIceAxe, "pickaxe", mod_InfiTools.bLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.blBoneIceAxe, "pickaxe", mod_InfiTools.bLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.wPaperIceAxe, "pickaxe", mod_InfiTools.pLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.saPaperIceAxe, "pickaxe", mod_InfiTools.pLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.bPaperIceAxe, "pickaxe", mod_InfiTools.pLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.pPaperIceAxe, "pickaxe", mod_InfiTools.pLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.sPaperIceAxe, "pickaxe", mod_InfiTools.pLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.cPaperIceAxe, "pickaxe", mod_InfiTools.pLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.brPaperIceAxe, "pickaxe", mod_InfiTools.pLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.stMossyIceAxe, "pickaxe", mod_InfiTools.mLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.dMossyIceAxe, "pickaxe", mod_InfiTools.mLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.rMossyIceAxe, "pickaxe", mod_InfiTools.mLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.bMossyIceAxe, "pickaxe", mod_InfiTools.mLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.mMossyIceAxe, "pickaxe", mod_InfiTools.mLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.glMossyIceAxe, "pickaxe", mod_InfiTools.mLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.wNetherrackIceAxe, "pickaxe", mod_InfiTools.nLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.stNetherrackIceAxe, "pickaxe", mod_InfiTools.nLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.iNetherrackIceAxe, "pickaxe", mod_InfiTools.nLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.rNetherrackIceAxe, "pickaxe", mod_InfiTools.nLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.oNetherrackIceAxe, "pickaxe", mod_InfiTools.nLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.saNetherrackIceAxe, "pickaxe", mod_InfiTools.nLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.bNetherrackIceAxe, "pickaxe", mod_InfiTools.nLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.mNetherrackIceAxe, "pickaxe", mod_InfiTools.nLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.nNetherrackIceAxe, "pickaxe", mod_InfiTools.nLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.glNetherrackIceAxe, "pickaxe", mod_InfiTools.nLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.iceNetherrackIceAxe, "pickaxe", mod_InfiTools.nLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.sNetherrackIceAxe, "pickaxe", mod_InfiTools.nLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.cNetherrackIceAxe, "pickaxe", mod_InfiTools.nLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.fNetherrackIceAxe, "pickaxe", mod_InfiTools.nLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.brNetherrackIceAxe, "pickaxe", mod_InfiTools.nLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.blNetherrackIceAxe, "pickaxe", mod_InfiTools.nLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.wGlowstoneIceAxe, "pickaxe", mod_InfiTools.glLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.stGlowstoneIceAxe, "pickaxe", mod_InfiTools.glLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.iGlowstoneIceAxe, "pickaxe", mod_InfiTools.glLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.dGlowstoneIceAxe, "pickaxe", mod_InfiTools.glLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.rGlowstoneIceAxe, "pickaxe", mod_InfiTools.glLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.oGlowstoneIceAxe, "pickaxe", mod_InfiTools.glLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.bGlowstoneIceAxe, "pickaxe", mod_InfiTools.glLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.mGlowstoneIceAxe, "pickaxe", mod_InfiTools.glLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.nGlowstoneIceAxe, "pickaxe", mod_InfiTools.glLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.glGlowstoneIceAxe, "pickaxe", mod_InfiTools.glLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.iceGlowstoneIceAxe, "pickaxe", mod_InfiTools.glLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.lGlowstoneIceAxe, "pickaxe", mod_InfiTools.glLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.sGlowstoneIceAxe, "pickaxe", mod_InfiTools.glLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.blGlowstoneIceAxe, "pickaxe", mod_InfiTools.glLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.wIceIceAxe, "pickaxe", mod_InfiTools.iceLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.stIceIceAxe, "pickaxe", mod_InfiTools.iceLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.iIceIceAxe, "pickaxe", mod_InfiTools.iceLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.dIceIceAxe, "pickaxe", mod_InfiTools.iceLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.gIceIceAxe, "pickaxe", mod_InfiTools.iceLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.rIceIceAxe, "pickaxe", mod_InfiTools.iceLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.oIceIceAxe, "pickaxe", mod_InfiTools.iceLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.saIceIceAxe, "pickaxe", mod_InfiTools.iceLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.bIceIceAxe, "pickaxe", mod_InfiTools.iceLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.glIceIceAxe, "pickaxe", mod_InfiTools.iceLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.iceIceIceAxe, "pickaxe", mod_InfiTools.iceLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.sIceIceAxe, "pickaxe", mod_InfiTools.iceLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.cIceIceAxe, "pickaxe", mod_InfiTools.iceLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.fIceIceAxe, "pickaxe", mod_InfiTools.iceLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.brIceIceAxe, "pickaxe", mod_InfiTools.iceLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.dLavaIceAxe, "pickaxe", mod_InfiTools.lLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.rLavaIceAxe, "pickaxe", mod_InfiTools.lLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.bLavaIceAxe, "pickaxe", mod_InfiTools.lLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.nLavaIceAxe, "pickaxe", mod_InfiTools.lLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.glLavaIceAxe, "pickaxe", mod_InfiTools.lLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.lLavaIceAxe, "pickaxe", mod_InfiTools.lLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.blLavaIceAxe, "pickaxe", mod_InfiTools.lLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.wSlimeIceAxe, "pickaxe", mod_InfiTools.sLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.stSlimeIceAxe, "pickaxe", mod_InfiTools.sLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.iSlimeIceAxe, "pickaxe", mod_InfiTools.sLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.dSlimeIceAxe, "pickaxe", mod_InfiTools.sLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.gSlimeIceAxe, "pickaxe", mod_InfiTools.sLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.rSlimeIceAxe, "pickaxe", mod_InfiTools.sLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.oSlimeIceAxe, "pickaxe", mod_InfiTools.sLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.saSlimeIceAxe, "pickaxe", mod_InfiTools.sLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.bSlimeIceAxe, "pickaxe", mod_InfiTools.sLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.pSlimeIceAxe, "pickaxe", mod_InfiTools.sLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.mSlimeIceAxe, "pickaxe", mod_InfiTools.sLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.nSlimeIceAxe, "pickaxe", mod_InfiTools.sLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.glSlimeIceAxe, "pickaxe", mod_InfiTools.sLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.iceSlimeIceAxe, "pickaxe", mod_InfiTools.sLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.lSlimeIceAxe, "pickaxe", mod_InfiTools.sLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.sSlimeIceAxe, "pickaxe", mod_InfiTools.sLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.cSlimeIceAxe, "pickaxe", mod_InfiTools.sLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.fSlimeIceAxe, "pickaxe", mod_InfiTools.sLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.brSlimeIceAxe, "pickaxe", mod_InfiTools.sLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.blSlimeIceAxe, "pickaxe", mod_InfiTools.sLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.wCactusIceAxe, "pickaxe", mod_InfiTools.cLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.stCactusIceAxe, "pickaxe", mod_InfiTools.cLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.saCactusIceAxe, "pickaxe", mod_InfiTools.cLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.bCactusIceAxe, "pickaxe", mod_InfiTools.cLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.pCactusIceAxe, "pickaxe", mod_InfiTools.cLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.nCactusIceAxe, "pickaxe", mod_InfiTools.cLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.sCactusIceAxe, "pickaxe", mod_InfiTools.cLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.cCactusIceAxe, "pickaxe", mod_InfiTools.cLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.fCactusIceAxe, "pickaxe", mod_InfiTools.cLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.brCactusIceAxe, "pickaxe", mod_InfiTools.cLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.wFlintIceAxe, "pickaxe", mod_InfiTools.fLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.stFlintIceAxe, "pickaxe", mod_InfiTools.fLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.iFlintIceAxe, "pickaxe", mod_InfiTools.fLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.gFlintIceAxe, "pickaxe", mod_InfiTools.fLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.oFlintIceAxe, "pickaxe", mod_InfiTools.fLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.saFlintIceAxe, "pickaxe", mod_InfiTools.fLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.bFlintIceAxe, "pickaxe", mod_InfiTools.fLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.nFlintIceAxe, "pickaxe", mod_InfiTools.fLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.iceFlintIceAxe, "pickaxe", mod_InfiTools.fLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.sFlintIceAxe, "pickaxe", mod_InfiTools.fLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.cFlintIceAxe, "pickaxe", mod_InfiTools.fLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.fFlintIceAxe, "pickaxe", mod_InfiTools.fLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.brFlintIceAxe, "pickaxe", mod_InfiTools.fLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.blFlintIceAxe, "pickaxe", mod_InfiTools.fLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.wBrickIceAxe, "pickaxe", mod_InfiTools.brLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.stBrickIceAxe, "pickaxe", mod_InfiTools.brLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.saBrickIceAxe, "pickaxe", mod_InfiTools.brLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.bBrickIceAxe, "pickaxe", mod_InfiTools.brLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.pBrickIceAxe, "pickaxe", mod_InfiTools.brLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.nBrickIceAxe, "pickaxe", mod_InfiTools.brLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.iceBrickIceAxe, "pickaxe", mod_InfiTools.brLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.sBrickIceAxe, "pickaxe", mod_InfiTools.brLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.cBrickIceAxe, "pickaxe", mod_InfiTools.brLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.fBrickIceAxe, "pickaxe", mod_InfiTools.brLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.brBrickIceAxe, "pickaxe", mod_InfiTools.brLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.dBlazeIceAxe, "pickaxe", mod_InfiTools.blLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.rBlazeIceAxe, "pickaxe", mod_InfiTools.blLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.bBlazeIceAxe, "pickaxe", mod_InfiTools.blLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.nBlazeIceAxe, "pickaxe", mod_InfiTools.blLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.glBlazeIceAxe, "pickaxe", mod_InfiTools.blLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.lBlazeIceAxe, "pickaxe", mod_InfiTools.blLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.fBlazeIceAxe, "pickaxe", mod_InfiTools.blLevel);
        MinecraftForge.setToolClass(mod_InfiHybrids.blBlazeIceAxe, "pickaxe", mod_InfiTools.blLevel);
        return 0;
    }
}
