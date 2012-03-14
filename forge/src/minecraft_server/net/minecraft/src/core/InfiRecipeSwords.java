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
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.stWoodSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.planks, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_InfiTools.saWoodSword.setIconCoord(2, 0);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.saWoodSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.planks, Character.valueOf('|'), mod_InfiTools.sandstoneRod
                });
        mod_InfiTools.bWoodSword.setIconCoord(3, 0);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bWoodSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.planks, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bWoodSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.planks, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_InfiTools.pWoodSword.setIconCoord(4, 0);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.pWoodSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.planks, Character.valueOf('|'), mod_InfiTools.paperRod
                });
        mod_InfiTools.nWoodSword.setIconCoord(5, 0);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.nWoodSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.planks, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_InfiTools.sWoodSword.setIconCoord(6, 0);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.sWoodSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.planks, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_InfiTools.cWoodSword.setIconCoord(7, 0);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.cWoodSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.planks, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_InfiTools.fWoodSword.setIconCoord(8, 0);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.fWoodSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.planks, Character.valueOf('|'), mod_InfiTools.flintRod
                });
        mod_InfiTools.brWoodSword.setIconCoord(9, 0);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.brWoodSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.planks, Character.valueOf('|'), mod_InfiTools.brickRod
                });
        ModLoader.addRecipe(new ItemStack(Item.swordStone, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.stone, Character.valueOf('|'), Item.stick
                });
        mod_InfiTools.stStoneSword.setIconCoord(1, 1);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.stStoneSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.stStoneSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.stone, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_InfiTools.saStoneSword.setIconCoord(2, 1);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.saStoneSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), mod_InfiTools.sandstoneRod
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.saStoneSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.stone, Character.valueOf('|'), mod_InfiTools.sandstoneRod
                });
        mod_InfiTools.bStoneSword.setIconCoord(3, 1);
        
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
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.pStoneSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), mod_InfiTools.paperRod
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.pStoneSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.stone, Character.valueOf('|'), mod_InfiTools.paperRod
                });
        mod_InfiTools.mStoneSword.setIconCoord(5, 1);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.mStoneSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), mod_InfiTools.mossyRod
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.mStoneSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.stone, Character.valueOf('|'), mod_InfiTools.mossyRod
                });
        mod_InfiTools.nStoneSword.setIconCoord(6, 1);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.nStoneSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.nStoneSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.stone, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_InfiTools.iceStoneSword.setIconCoord(7, 1);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.iceStoneSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), mod_InfiTools.iceRod
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.iceStoneSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.stone, Character.valueOf('|'), mod_InfiTools.iceRod
                });
        mod_InfiTools.sStoneSword.setIconCoord(8, 1);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.sStoneSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.sStoneSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.stone, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_InfiTools.cStoneSword.setIconCoord(9, 1);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.cStoneSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.cStoneSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.stone, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_InfiTools.fStoneSword.setIconCoord(1, 1);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.fStoneSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), mod_InfiTools.flintRod
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.fStoneSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.stone, Character.valueOf('|'), mod_InfiTools.flintRod
                });
        mod_InfiTools.brStoneSword.setIconCoord(1, 1);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.brStoneSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), mod_InfiTools.brickRod
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.brStoneSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.stone, Character.valueOf('|'), mod_InfiTools.brickRod
                });
        mod_InfiTools.stIronSword.setIconCoord(1, 2);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.stIronSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_InfiTools.iIronSword.setIconCoord(2, 2);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.iIronSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), mod_InfiTools.ironRod
                });
        mod_InfiTools.dIronSword.setIconCoord(3, 2);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.dIronSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), mod_InfiTools.diamondRod
                });
        mod_InfiTools.gIronSword.setIconCoord(4, 2);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.gIronSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), mod_InfiTools.goldRod
                });
        mod_InfiTools.rIronSword.setIconCoord(5, 2);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.rIronSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), mod_InfiTools.redstoneRod
                });
        mod_InfiTools.oIronSword.setIconCoord(6, 2);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.oIronSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), mod_InfiTools.obsidianRod
                });
        mod_InfiTools.bIronSword.setIconCoord(7, 2);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bIronSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bIronSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_InfiTools.nIronSword.setIconCoord(8, 2);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.nIronSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_InfiTools.glIronSword.setIconCoord(9, 2);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.glIronSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_InfiTools.iceIronSword.setIconCoord(10, 2);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.iceIronSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), mod_InfiTools.iceRod
                });
        mod_InfiTools.sIronSword.setIconCoord(11, 2);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.sIronSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_InfiTools.blIronSword.setIconCoord(12, 2);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.blIronSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), Item.blazeRod
                });
        mod_InfiTools.stDiamondSword.setIconCoord(1, 3);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.stDiamondSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_InfiTools.iDiamondSword.setIconCoord(2, 3);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.iDiamondSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), mod_InfiTools.ironRod
                });
        mod_InfiTools.dDiamondSword.setIconCoord(3, 3);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.dDiamondSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), mod_InfiTools.diamondRod
                });
        mod_InfiTools.gDiamondSword.setIconCoord(4, 3);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.gDiamondSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), mod_InfiTools.goldRod
                });
        mod_InfiTools.rDiamondSword.setIconCoord(5, 3);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.rDiamondSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), mod_InfiTools.redstoneRod
                });
        mod_InfiTools.oDiamondSword.setIconCoord(6, 3);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.oDiamondSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), mod_InfiTools.obsidianRod
                });
        mod_InfiTools.bDiamondSword.setIconCoord(7, 3);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bDiamondSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bDiamondSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_InfiTools.mDiamondSword.setIconCoord(8, 3);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.mDiamondSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), mod_InfiTools.mossyRod
                });
        mod_InfiTools.nDiamondSword.setIconCoord(9, 3);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.nDiamondSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_InfiTools.glDiamondSword.setIconCoord(10, 3);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.glDiamondSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_InfiTools.blDiamondSword.setIconCoord(11, 3);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.blDiamondSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), Item.blazeRod
                });
        mod_InfiTools.stGoldSword.setIconCoord(1, 4);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.stGoldSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_InfiTools.gGoldSword.setIconCoord(2, 4);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.gGoldSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), mod_InfiTools.goldRod
                });
        mod_InfiTools.oGoldSword.setIconCoord(3, 4);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.oGoldSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), mod_InfiTools.obsidianRod
                });
        mod_InfiTools.saGoldSword.setIconCoord(4, 4);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.saGoldSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), mod_InfiTools.sandstoneRod
                });
        mod_InfiTools.bGoldSword.setIconCoord(5, 4);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bGoldSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bGoldSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_InfiTools.mGoldSword.setIconCoord(6, 4);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.mGoldSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), mod_InfiTools.mossyRod
                });
        mod_InfiTools.nGoldSword.setIconCoord(7, 4);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.nGoldSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_InfiTools.glGoldSword.setIconCoord(8, 4);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.glGoldSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_InfiTools.iceGoldSword.setIconCoord(9, 4);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.iceGoldSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), mod_InfiTools.iceRod
                });
        mod_InfiTools.sGoldSword.setIconCoord(10, 4);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.sGoldSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_InfiTools.fGoldSword.setIconCoord(11, 4);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.fGoldSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), mod_InfiTools.flintRod
                });
        mod_InfiTools.wRedstoneSword.setIconCoord(0, 5);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.wRedstoneSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), Item.stick
                });
        mod_InfiTools.stRedstoneSword.setIconCoord(1, 5);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.stRedstoneSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_InfiTools.iRedstoneSword.setIconCoord(2, 5);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.iRedstoneSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), mod_InfiTools.ironRod
                });
        mod_InfiTools.dRedstoneSword.setIconCoord(3, 5);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.dRedstoneSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), mod_InfiTools.diamondRod
                });
        mod_InfiTools.rRedstoneSword.setIconCoord(4, 5);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.rRedstoneSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), mod_InfiTools.redstoneRod
                });
        mod_InfiTools.oRedstoneSword.setIconCoord(5, 5);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.oRedstoneSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), mod_InfiTools.obsidianRod
                });
        mod_InfiTools.bRedstoneSword.setIconCoord(6, 5);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bRedstoneSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bRedstoneSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_InfiTools.mRedstoneSword.setIconCoord(7, 5);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.mRedstoneSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), mod_InfiTools.mossyRod
                });
        mod_InfiTools.glRedstoneSword.setIconCoord(8, 5);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.glRedstoneSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_InfiTools.sRedstoneSword.setIconCoord(9, 5);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.sRedstoneSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_InfiTools.blRedstoneSword.setIconCoord(10, 5);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.blRedstoneSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), Item.blazeRod
                });
        mod_InfiTools.wObsidianSword.setIconCoord(0, 6);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.wObsidianSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), Item.stick
                });
        mod_InfiTools.stObsidianSword.setIconCoord(1, 6);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.stObsidianSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_InfiTools.iObsidianSword.setIconCoord(2, 6);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.iObsidianSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), mod_InfiTools.ironRod
                });
        mod_InfiTools.dObsidianSword.setIconCoord(3, 6);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.dObsidianSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), mod_InfiTools.diamondRod
                });
        mod_InfiTools.gObsidianSword.setIconCoord(4, 6);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.gObsidianSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), mod_InfiTools.goldRod
                });
        mod_InfiTools.rObsidianSword.setIconCoord(5, 6);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.rObsidianSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), mod_InfiTools.redstoneRod
                });
        mod_InfiTools.oObsidianSword.setIconCoord(6, 6);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.oObsidianSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), mod_InfiTools.obsidianRod
                });
        mod_InfiTools.bObsidianSword.setIconCoord(7, 6);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bObsidianSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bObsidianSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_InfiTools.nObsidianSword.setIconCoord(8, 6);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.nObsidianSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_InfiTools.glObsidianSword.setIconCoord(9, 6);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.glObsidianSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_InfiTools.sObsidianSword.setIconCoord(10, 6);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.sObsidianSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_InfiTools.fObsidianSword.setIconCoord(11, 6);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.fObsidianSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), mod_InfiTools.flintRod
                });
        mod_InfiTools.blObsidianSword.setIconCoord(12, 6);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.blObsidianSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), Item.blazeRod
                });
        mod_InfiTools.wSandstoneSword.setIconCoord(0, 7);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.wSandstoneSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), Item.stick
                });
        mod_InfiTools.stSandstoneSword.setIconCoord(1, 7);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.stSandstoneSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_InfiTools.saSandstoneSword.setIconCoord(2, 7);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.saSandstoneSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), mod_InfiTools.sandstoneRod
                });
        mod_InfiTools.bSandstoneSword.setIconCoord(3, 7);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bSandstoneSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bSandstoneSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_InfiTools.pSandstoneSword.setIconCoord(4, 7);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.pSandstoneSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), mod_InfiTools.paperRod
                });
        mod_InfiTools.nSandstoneSword.setIconCoord(5, 7);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.nSandstoneSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_InfiTools.iceSandstoneSword.setIconCoord(6, 7);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.iceSandstoneSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), mod_InfiTools.iceRod
                });
        mod_InfiTools.sSandstoneSword.setIconCoord(7, 7);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.sSandstoneSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_InfiTools.cSandstoneSword.setIconCoord(8, 7);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.cSandstoneSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_InfiTools.fSandstoneSword.setIconCoord(9, 7);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.fSandstoneSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), mod_InfiTools.flintRod
                });
        mod_InfiTools.brSandstoneSword.setIconCoord(10, 7);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.brSandstoneSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), mod_InfiTools.brickRod
                });
        mod_InfiTools.wBoneSword.setIconCoord(0, 8);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.wBoneSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Item.bone, Character.valueOf('|'), Item.stick
                });
        mod_InfiTools.stBoneSword.setIconCoord(1, 8);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.stBoneSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_InfiTools.iBoneSword.setIconCoord(2, 8);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.iBoneSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.ironRod
                });
        mod_InfiTools.dBoneSword.setIconCoord(3, 8);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.dBoneSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.diamondRod
                });
        mod_InfiTools.rBoneSword.setIconCoord(4, 8);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.rBoneSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.redstoneRod
                });
        mod_InfiTools.oBoneSword.setIconCoord(5, 8);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.oBoneSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.obsidianRod
                });
        mod_InfiTools.bBoneSword.setIconCoord(6, 8);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bBoneSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Item.bone, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bBoneSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_InfiTools.mBoneSword.setIconCoord(7, 8);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.mBoneSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.mossyRod
                });
        mod_InfiTools.nBoneSword.setIconCoord(8, 8);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.nBoneSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_InfiTools.glBoneSword.setIconCoord(9, 8);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.glBoneSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_InfiTools.sBoneSword.setIconCoord(10, 8);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.sBoneSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_InfiTools.cBoneSword.setIconCoord(11, 8);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.cBoneSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_InfiTools.fBoneSword.setIconCoord(12, 8);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.fBoneSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.flintRod
                });
        mod_InfiTools.brBoneSword.setIconCoord(13, 8);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.brBoneSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.brickRod
                });
        mod_InfiTools.blBoneSword.setIconCoord(14, 8);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.blBoneSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Item.bone, Character.valueOf('|'), Item.blazeRod
                });
        mod_InfiTools.wPaperSword.setIconCoord(0, 9);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.wPaperSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), mod_InfiTools.paperStack, Character.valueOf('|'), Item.stick
                });
        mod_InfiTools.saPaperSword.setIconCoord(1, 9);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.saPaperSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), mod_InfiTools.paperStack, Character.valueOf('|'), mod_InfiTools.sandstoneRod
                });
        mod_InfiTools.bPaperSword.setIconCoord(2, 9);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bPaperSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), mod_InfiTools.paperStack, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bPaperSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), mod_InfiTools.paperStack, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_InfiTools.pPaperSword.setIconCoord(3, 9);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.pPaperSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), mod_InfiTools.paperStack, Character.valueOf('|'), mod_InfiTools.paperRod
                });
        mod_InfiTools.sPaperSword.setIconCoord(4, 9);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.sPaperSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), mod_InfiTools.paperStack, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_InfiTools.cPaperSword.setIconCoord(5, 9);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.cPaperSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), mod_InfiTools.paperStack, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_InfiTools.brPaperSword.setIconCoord(6, 9);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.brPaperSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), mod_InfiTools.paperStack, Character.valueOf('|'), mod_InfiTools.brickRod
                });
        mod_InfiTools.stMossySword.setIconCoord(0, 10);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.stMossySword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), mod_InfiTools.mossBallCrafted, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_InfiTools.dMossySword.setIconCoord(1, 10);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.dMossySword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), mod_InfiTools.mossBallCrafted, Character.valueOf('|'), mod_InfiTools.diamondRod
                });
        mod_InfiTools.rMossySword.setIconCoord(2, 10);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.rMossySword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), mod_InfiTools.mossBallCrafted, Character.valueOf('|'), mod_InfiTools.redstoneRod
                });
        mod_InfiTools.bMossySword.setIconCoord(3, 10);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bMossySword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), mod_InfiTools.mossBallCrafted, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bMossySword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), mod_InfiTools.mossBallCrafted, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_InfiTools.mMossySword.setIconCoord(4, 10);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.mMossySword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), mod_InfiTools.mossBallCrafted, Character.valueOf('|'), mod_InfiTools.mossyRod
                });
        mod_InfiTools.glMossySword.setIconCoord(5, 10);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.glMossySword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), mod_InfiTools.mossBallCrafted, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_InfiTools.wNetherrackSword.setIconCoord(0, 11);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.wNetherrackSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), Item.stick
                });
        mod_InfiTools.stNetherrackSword.setIconCoord(1, 11);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.stNetherrackSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_InfiTools.iNetherrackSword.setIconCoord(2, 11);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.iNetherrackSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.ironRod
                });
        mod_InfiTools.rNetherrackSword.setIconCoord(3, 11);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.rNetherrackSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.redstoneRod
                });
        mod_InfiTools.oNetherrackSword.setIconCoord(4, 11);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.oNetherrackSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.obsidianRod
                });
        mod_InfiTools.saNetherrackSword.setIconCoord(5, 11);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.saNetherrackSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.sandstoneRod
                });
        mod_InfiTools.bNetherrackSword.setIconCoord(6, 11);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bNetherrackSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bNetherrackSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_InfiTools.mNetherrackSword.setIconCoord(7, 11);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.mNetherrackSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.mossyRod
                });
        mod_InfiTools.nNetherrackSword.setIconCoord(8, 11);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.nNetherrackSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_InfiTools.glNetherrackSword.setIconCoord(9, 11);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.glNetherrackSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_InfiTools.iceNetherrackSword.setIconCoord(10, 11);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.iceNetherrackSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.iceRod
                });
        mod_InfiTools.sNetherrackSword.setIconCoord(11, 11);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.sNetherrackSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_InfiTools.cNetherrackSword.setIconCoord(12, 11);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.cNetherrackSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_InfiTools.fNetherrackSword.setIconCoord(13, 11);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.fNetherrackSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_InfiTools.brNetherrackSword.setIconCoord(14, 11);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.brNetherrackSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_InfiTools.blNetherrackSword.setIconCoord(15, 11);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.blNetherrackSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), Item.blazeRod
                });
        mod_InfiTools.wGlowstoneSword.setIconCoord(0, 12);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.wGlowstoneSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), Item.stick
                });
        mod_InfiTools.stGlowstoneSword.setIconCoord(1, 12);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.stGlowstoneSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_InfiTools.iGlowstoneSword.setIconCoord(2, 12);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.iGlowstoneSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.ironRod
                });
        mod_InfiTools.dGlowstoneSword.setIconCoord(3, 12);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.dGlowstoneSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.diamondRod
                });
        mod_InfiTools.rGlowstoneSword.setIconCoord(4, 12);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.rGlowstoneSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.redstoneRod
                });
        mod_InfiTools.oGlowstoneSword.setIconCoord(5, 12);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.oGlowstoneSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.obsidianRod
                });
        mod_InfiTools.bGlowstoneSword.setIconCoord(6, 12);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bGlowstoneSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bGlowstoneSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_InfiTools.mGlowstoneSword.setIconCoord(7, 12);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.mGlowstoneSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.mossyRod
                });
        mod_InfiTools.nGlowstoneSword.setIconCoord(8, 12);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.nGlowstoneSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_InfiTools.glGlowstoneSword.setIconCoord(9, 12);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.glGlowstoneSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_InfiTools.iceGlowstoneSword.setIconCoord(10, 12);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.iceGlowstoneSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.iceRod
                });
        mod_InfiTools.lGlowstoneSword.setIconCoord(11, 12);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.lGlowstoneSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_InfiTools.sGlowstoneSword.setIconCoord(12, 12);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.sGlowstoneSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_InfiTools.blGlowstoneSword.setIconCoord(13, 12);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.blGlowstoneSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), Item.blazeRod
                });
        mod_InfiTools.wIceSword.setIconCoord(0, 13);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.wIceSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.ice, Character.valueOf('|'), Item.stick
                });
        mod_InfiTools.stIceSword.setIconCoord(1, 13);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.stIceSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_InfiTools.iIceSword.setIconCoord(2, 13);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.iIceSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.ironRod
                });
        mod_InfiTools.dIceSword.setIconCoord(3, 13);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.dIceSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.diamondRod
                });
        mod_InfiTools.gIceSword.setIconCoord(4, 13);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.gIceSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.goldRod
                });
        mod_InfiTools.rIceSword.setIconCoord(5, 13);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.rIceSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.redstoneRod
                });
        mod_InfiTools.oIceSword.setIconCoord(6, 13);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.oIceSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.obsidianRod
                });
        mod_InfiTools.saIceSword.setIconCoord(7, 13);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.saIceSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.sandstoneRod
                });
        mod_InfiTools.bIceSword.setIconCoord(8, 13);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bIceSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.ice, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bIceSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_InfiTools.glIceSword.setIconCoord(9, 13);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.glIceSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_InfiTools.iceIceSword.setIconCoord(10, 13);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.iceIceSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.iceRod
                });
        mod_InfiTools.sIceSword.setIconCoord(11, 13);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.sIceSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_InfiTools.cIceSword.setIconCoord(12, 13);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.cIceSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_InfiTools.fIceSword.setIconCoord(13, 13);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.fIceSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.flintRod
                });
        mod_InfiTools.brIceSword.setIconCoord(14, 13);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.brIceSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.brickRod
                });
        mod_InfiTools.dLavaSword.setIconCoord(0, 14);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.dLavaSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), mod_InfiTools.lavaCrystal, Character.valueOf('|'), mod_InfiTools.diamondRod
                });
        mod_InfiTools.rLavaSword.setIconCoord(1, 14);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.rLavaSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), mod_InfiTools.lavaCrystal, Character.valueOf('|'), mod_InfiTools.redstoneRod
                });
        mod_InfiTools.bLavaSword.setIconCoord(2, 14);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bLavaSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), mod_InfiTools.lavaCrystal, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bLavaSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), mod_InfiTools.lavaCrystal, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_InfiTools.nLavaSword.setIconCoord(3, 14);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.nLavaSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), mod_InfiTools.lavaCrystal, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_InfiTools.glLavaSword.setIconCoord(4, 14);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.glLavaSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), mod_InfiTools.lavaCrystal, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_InfiTools.lLavaSword.setIconCoord(5, 14);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.lLavaSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), mod_InfiTools.lavaCrystal, Character.valueOf('|'), mod_InfiTools.lavaRod
                });
        mod_InfiTools.blLavaSword.setIconCoord(6, 14);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.blLavaSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), mod_InfiTools.lavaCrystal, Character.valueOf('|'), Item.blazeRod
                });
        mod_InfiTools.wSlimeSword.setIconCoord(0, 15);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.wSlimeSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), Item.stick
                });
        mod_InfiTools.stSlimeSword.setIconCoord(1, 15);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.stSlimeSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_InfiTools.iSlimeSword.setIconCoord(2, 15);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.iSlimeSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.ironRod
                });
        mod_InfiTools.dSlimeSword.setIconCoord(3, 15);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.dSlimeSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.diamondRod
                });
        mod_InfiTools.gSlimeSword.setIconCoord(4, 15);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.gSlimeSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.goldRod
                });
        mod_InfiTools.rSlimeSword.setIconCoord(5, 15);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.rSlimeSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.redstoneRod
                });
        mod_InfiTools.oSlimeSword.setIconCoord(6, 15);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.oSlimeSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.obsidianRod
                });
        mod_InfiTools.saSlimeSword.setIconCoord(7, 15);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.saSlimeSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.sandstoneRod
                });
        mod_InfiTools.bSlimeSword.setIconCoord(8, 15);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bSlimeSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bSlimeSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_InfiTools.pSlimeSword.setIconCoord(9, 15);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.pSlimeSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.paperRod
                });
        mod_InfiTools.mSlimeSword.setIconCoord(10, 15);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.mSlimeSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.mossyRod
                });
        mod_InfiTools.nSlimeSword.setIconCoord(11, 15);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.nSlimeSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_InfiTools.glSlimeSword.setIconCoord(12, 15);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.glSlimeSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_InfiTools.iceSlimeSword.setIconCoord(13, 15);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.iceSlimeSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.iceRod
                });
        mod_InfiTools.lSlimeSword.setIconCoord(14, 15);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.lSlimeSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.lavaRod
                });
        mod_InfiTools.sSlimeSword.setIconCoord(15, 15);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.sSlimeSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_InfiTools.cSlimeSword.setIconCoord(12, 14);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.cSlimeSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_InfiTools.fSlimeSword.setIconCoord(13, 14);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.fSlimeSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.flintRod
                });
        mod_InfiTools.brSlimeSword.setIconCoord(14, 14);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.brSlimeSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.brickRod
                });
        mod_InfiTools.blSlimeSword.setIconCoord(15, 14);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.blSlimeSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), Item.blazeRod
                });
        mod_InfiTools.wCactusSword.setIconCoord(10, 0);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.wCactusSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.cactus, Character.valueOf('|'), Item.stick
                });
        mod_InfiTools.stCactusSword.setIconCoord(11, 0);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.stCactusSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.cactus, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_InfiTools.saCactusSword.setIconCoord(12, 0);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.saCactusSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.cactus, Character.valueOf('|'), mod_InfiTools.sandstoneRod
                });
        mod_InfiTools.bCactusSword.setIconCoord(13, 0);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bCactusSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.cactus, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bCactusSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.cactus, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_InfiTools.pCactusSword.setIconCoord(14, 0);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.pCactusSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.cactus, Character.valueOf('|'), mod_InfiTools.paperRod
                });
        mod_InfiTools.nCactusSword.setIconCoord(15, 0);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.nCactusSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.cactus, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_InfiTools.sCactusSword.setIconCoord(12, 1);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.sCactusSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.cactus, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_InfiTools.cCactusSword.setIconCoord(13, 1);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.cCactusSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.cactus, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_InfiTools.fCactusSword.setIconCoord(14, 1);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.fCactusSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.cactus, Character.valueOf('|'), mod_InfiTools.flintRod
                });
        mod_InfiTools.brCactusSword.setIconCoord(15, 1);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.brCactusSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Block.cactus, Character.valueOf('|'), mod_InfiTools.brickRod
                });
        mod_InfiTools.wFlintSword.setIconCoord(13, 2);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.wFlintSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Item.flint, Character.valueOf('|'), Item.stick
                });
        mod_InfiTools.stFlintSword.setIconCoord(14, 2);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.stFlintSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_InfiTools.iFlintSword.setIconCoord(15, 2);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.iFlintSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.ironRod
                });
        mod_InfiTools.gFlintSword.setIconCoord(12, 3);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.gFlintSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.goldRod
                });
        mod_InfiTools.oFlintSword.setIconCoord(13, 3);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.oFlintSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.obsidianRod
                });
        mod_InfiTools.saFlintSword.setIconCoord(14, 3);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.saFlintSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.sandstoneRod
                });
        mod_InfiTools.bFlintSword.setIconCoord(15, 3);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bFlintSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Item.flint, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bFlintSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_InfiTools.nFlintSword.setIconCoord(12, 4);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.nFlintSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_InfiTools.iceFlintSword.setIconCoord(13, 4);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.iceFlintSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.iceRod
                });
        mod_InfiTools.sFlintSword.setIconCoord(14, 4);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.sFlintSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_InfiTools.cFlintSword.setIconCoord(15, 4);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.cFlintSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_InfiTools.fFlintSword.setIconCoord(11, 5);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.fFlintSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.flintRod
                });
        mod_InfiTools.brFlintSword.setIconCoord(12, 5);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.brFlintSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.brickRod
                });
        mod_InfiTools.blFlintSword.setIconCoord(13, 5);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.blFlintSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Item.flint, Character.valueOf('|'), Item.blazeRod
                });
        mod_InfiTools.wBrickSword.setIconCoord(14, 5);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.wBrickSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Item.brick, Character.valueOf('|'), Item.stick
                });
        mod_InfiTools.stBrickSword.setIconCoord(15, 5);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.stBrickSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Item.brick, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_InfiTools.saBrickSword.setIconCoord(13, 6);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.saBrickSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Item.brick, Character.valueOf('|'), mod_InfiTools.sandstoneRod
                });
        mod_InfiTools.bBrickSword.setIconCoord(14, 6);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bBrickSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Item.brick, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bBrickSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Item.brick, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_InfiTools.pBrickSword.setIconCoord(15, 6);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.pBrickSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Item.brick, Character.valueOf('|'), mod_InfiTools.paperRod
                });
        mod_InfiTools.nBrickSword.setIconCoord(11, 7);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.nBrickSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Item.brick, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_InfiTools.iceBrickSword.setIconCoord(12, 7);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.iceBrickSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Item.brick, Character.valueOf('|'), mod_InfiTools.iceRod
                });
        mod_InfiTools.sBrickSword.setIconCoord(13, 7);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.sBrickSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Item.brick, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_InfiTools.cBrickSword.setIconCoord(14, 7);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.cBrickSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Item.brick, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_InfiTools.fBrickSword.setIconCoord(15, 7);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.fBrickSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Item.brick, Character.valueOf('|'), mod_InfiTools.flintRod
                });
        mod_InfiTools.brBrickSword.setIconCoord(15, 8);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.brBrickSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), Item.brick, Character.valueOf('|'), mod_InfiTools.brickRod
                });
        mod_InfiTools.dBlazeSword.setIconCoord(7, 9);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.dBlazeSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), mod_InfiTools.blazeCrystal, Character.valueOf('|'), mod_InfiTools.diamondRod
                });
        mod_InfiTools.rBlazeSword.setIconCoord(8, 9);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.rBlazeSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), mod_InfiTools.blazeCrystal, Character.valueOf('|'), mod_InfiTools.redstoneRod
                });
        mod_InfiTools.bBlazeSword.setIconCoord(9, 9);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bBlazeSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), mod_InfiTools.blazeCrystal, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bBlazeSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), mod_InfiTools.blazeCrystal, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_InfiTools.nBlazeSword.setIconCoord(10, 9);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.nBlazeSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), mod_InfiTools.blazeCrystal, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_InfiTools.glBlazeSword.setIconCoord(11, 9);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.glBlazeSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), mod_InfiTools.blazeCrystal, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_InfiTools.lBlazeSword.setIconCoord(12, 9);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.lBlazeSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), mod_InfiTools.blazeCrystal, Character.valueOf('|'), mod_InfiTools.lavaRod
                });
        mod_InfiTools.fBlazeSword.setIconCoord(13, 9);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.fBlazeSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), mod_InfiTools.blazeCrystal, Character.valueOf('|'), mod_InfiTools.flintRod
                });
        mod_InfiTools.blBlazeSword.setIconCoord(14, 9);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.blBlazeSword, 1), new Object[]
                {
                    "m", "m", "|", Character.valueOf('m'), mod_InfiTools.blazeCrystal, Character.valueOf('|'), Item.blazeRod
                });
    }
}
