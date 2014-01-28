package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.World;

public class WorldGeneratorBonusChest extends WorldGenerator
{
    // JAVADOC FIELD $$ field_76546_a
    private final WeightedRandomChestContent[] theBonusChestGenerator;
    // JAVADOC FIELD $$ field_76545_b
    private final int itemsToGenerateInBonusChest;
    private static final String __OBFID = "CL_00000403";

    public WorldGeneratorBonusChest(WeightedRandomChestContent[] par1ArrayOfWeightedRandomChestContent, int par2)
    {
        this.theBonusChestGenerator = par1ArrayOfWeightedRandomChestContent;
        this.itemsToGenerateInBonusChest = par2;
    }

    public boolean generate(World par1World, Random par2Random, int par3, int par4, int par5)
    {
        Block block;

        do
        {
            block = par1World.func_147439_a(par3, par4, par5);
            if (!block.isAir(par1World, par3, par4, par5) && !block.isLeaves(par1World, par3, par4, par5)) break;
            par4--;
        } while (par4 > 1);

        if (par4 < 1)
        {
            return false;
        }
        else
        {
            ++par4;

            for (int l = 0; l < 4; ++l)
            {
                int i1 = par3 + par2Random.nextInt(4) - par2Random.nextInt(4);
                int j1 = par4 + par2Random.nextInt(3) - par2Random.nextInt(3);
                int k1 = par5 + par2Random.nextInt(4) - par2Random.nextInt(4);

                if (par1World.func_147437_c(i1, j1, k1) && World.func_147466_a(par1World, i1, j1 - 1, k1))
                {
                    par1World.func_147465_d(i1, j1, k1, Blocks.chest, 0, 2);
                    TileEntityChest tileentitychest = (TileEntityChest)par1World.func_147438_o(i1, j1, k1);

                    if (tileentitychest != null && tileentitychest != null)
                    {
                        WeightedRandomChestContent.generateChestContents(par2Random, this.theBonusChestGenerator, tileentitychest, this.itemsToGenerateInBonusChest);
                    }

                    if (par1World.func_147437_c(i1 - 1, j1, k1) && World.func_147466_a(par1World, i1 - 1, j1 - 1, k1))
                    {
                        par1World.func_147465_d(i1 - 1, j1, k1, Blocks.torch, 0, 2);
                    }

                    if (par1World.func_147437_c(i1 + 1, j1, k1) && World.func_147466_a(par1World, i1 - 1, j1 - 1, k1))
                    {
                        par1World.func_147465_d(i1 + 1, j1, k1, Blocks.torch, 0, 2);
                    }

                    if (par1World.func_147437_c(i1, j1, k1 - 1) && World.func_147466_a(par1World, i1 - 1, j1 - 1, k1))
                    {
                        par1World.func_147465_d(i1, j1, k1 - 1, Blocks.torch, 0, 2);
                    }

                    if (par1World.func_147437_c(i1, j1, k1 + 1) && World.func_147466_a(par1World, i1 - 1, j1 - 1, k1))
                    {
                        par1World.func_147465_d(i1, j1, k1 + 1, Blocks.torch, 0, 2);
                    }

                    return true;
                }
            }

            return false;
        }
    }
}