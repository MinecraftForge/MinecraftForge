package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.World;
import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.common.DungeonHooks;
import static net.minecraftforge.common.ChestGenHooks.DUNGEON_CHEST;;

public class WorldGenDungeons extends WorldGenerator
{
    public static final WeightedRandomChestContent[] field_111189_a = new WeightedRandomChestContent[] {new WeightedRandomChestContent(Items.saddle, 0, 1, 1, 10), new WeightedRandomChestContent(Items.iron_ingot, 0, 1, 4, 10), new WeightedRandomChestContent(Items.bread, 0, 1, 1, 10), new WeightedRandomChestContent(Items.wheat, 0, 1, 4, 10), new WeightedRandomChestContent(Items.gunpowder, 0, 1, 4, 10), new WeightedRandomChestContent(Items.string, 0, 1, 4, 10), new WeightedRandomChestContent(Items.bucket, 0, 1, 1, 10), new WeightedRandomChestContent(Items.golden_apple, 0, 1, 1, 1), new WeightedRandomChestContent(Items.redstone, 0, 1, 4, 10), new WeightedRandomChestContent(Items.record_13, 0, 1, 1, 10), new WeightedRandomChestContent(Items.record_cat, 0, 1, 1, 10), new WeightedRandomChestContent(Items.name_tag, 0, 1, 1, 10), new WeightedRandomChestContent(Items.golden_horse_armor, 0, 1, 1, 2), new WeightedRandomChestContent(Items.iron_horse_armor, 0, 1, 1, 5), new WeightedRandomChestContent(Items.diamond_horse_armor, 0, 1, 1, 1)};
    private static final String __OBFID = "CL_00000425";

    public boolean generate(World par1World, Random par2Random, int par3, int par4, int par5)
    {
        byte b0 = 3;
        int l = par2Random.nextInt(2) + 2;
        int i1 = par2Random.nextInt(2) + 2;
        int j1 = 0;
        int k1;
        int l1;
        int i2;

        for (k1 = par3 - l - 1; k1 <= par3 + l + 1; ++k1)
        {
            for (l1 = par4 - 1; l1 <= par4 + b0 + 1; ++l1)
            {
                for (i2 = par5 - i1 - 1; i2 <= par5 + i1 + 1; ++i2)
                {
                    Material material = par1World.func_147439_a(k1, l1, i2).func_149688_o();

                    if (l1 == par4 - 1 && !material.isSolid())
                    {
                        return false;
                    }

                    if (l1 == par4 + b0 + 1 && !material.isSolid())
                    {
                        return false;
                    }

                    if ((k1 == par3 - l - 1 || k1 == par3 + l + 1 || i2 == par5 - i1 - 1 || i2 == par5 + i1 + 1) && l1 == par4 && par1World.func_147437_c(k1, l1, i2) && par1World.func_147437_c(k1, l1 + 1, i2))
                    {
                        ++j1;
                    }
                }
            }
        }

        if (j1 >= 1 && j1 <= 5)
        {
            for (k1 = par3 - l - 1; k1 <= par3 + l + 1; ++k1)
            {
                for (l1 = par4 + b0; l1 >= par4 - 1; --l1)
                {
                    for (i2 = par5 - i1 - 1; i2 <= par5 + i1 + 1; ++i2)
                    {
                        if (k1 != par3 - l - 1 && l1 != par4 - 1 && i2 != par5 - i1 - 1 && k1 != par3 + l + 1 && l1 != par4 + b0 + 1 && i2 != par5 + i1 + 1)
                        {
                            par1World.func_147468_f(k1, l1, i2);
                        }
                        else if (l1 >= 0 && !par1World.func_147439_a(k1, l1 - 1, i2).func_149688_o().isSolid())
                        {
                            par1World.func_147468_f(k1, l1, i2);
                        }
                        else if (par1World.func_147439_a(k1, l1, i2).func_149688_o().isSolid())
                        {
                            if (l1 == par4 - 1 && par2Random.nextInt(4) != 0)
                            {
                                par1World.func_147465_d(k1, l1, i2, Blocks.mossy_cobblestone, 0, 2);
                            }
                            else
                            {
                                par1World.func_147465_d(k1, l1, i2, Blocks.cobblestone, 0, 2);
                            }
                        }
                    }
                }
            }

            k1 = 0;

            while (k1 < 2)
            {
                l1 = 0;

                while (true)
                {
                    if (l1 < 3)
                    {
                        label101:
                        {
                            i2 = par3 + par2Random.nextInt(l * 2 + 1) - l;
                            int j2 = par5 + par2Random.nextInt(i1 * 2 + 1) - i1;

                            if (par1World.func_147437_c(i2, par4, j2))
                            {
                                int k2 = 0;

                                if (par1World.func_147439_a(i2 - 1, par4, j2).func_149688_o().isSolid())
                                {
                                    ++k2;
                                }

                                if (par1World.func_147439_a(i2 + 1, par4, j2).func_149688_o().isSolid())
                                {
                                    ++k2;
                                }

                                if (par1World.func_147439_a(i2, par4, j2 - 1).func_149688_o().isSolid())
                                {
                                    ++k2;
                                }

                                if (par1World.func_147439_a(i2, par4, j2 + 1).func_149688_o().isSolid())
                                {
                                    ++k2;
                                }

                                if (k2 == 1)
                                {
                                    par1World.func_147465_d(i2, par4, j2, Blocks.chest, 0, 2);
                                    TileEntityChest tileentitychest = (TileEntityChest)par1World.func_147438_o(i2, par4, j2);

                                    if (tileentitychest != null)
                                    {
                                        WeightedRandomChestContent.generateChestContents(par2Random, ChestGenHooks.getItems(DUNGEON_CHEST, par2Random), tileentitychest, ChestGenHooks.getCount(DUNGEON_CHEST, par2Random));
                                    }

                                    break label101;
                                }
                            }

                            ++l1;
                            continue;
                        }
                    }

                    ++k1;
                    break;
                }
            }

            par1World.func_147465_d(par3, par4, par5, Blocks.mob_spawner, 0, 2);
            TileEntityMobSpawner tileentitymobspawner = (TileEntityMobSpawner)par1World.func_147438_o(par3, par4, par5);

            if (tileentitymobspawner != null)
            {
                tileentitymobspawner.func_145881_a().setMobID(this.pickMobSpawner(par2Random));
            }
            else
            {
                System.err.println("Failed to fetch mob spawner entity at (" + par3 + ", " + par4 + ", " + par5 + ")");
            }

            return true;
        }
        else
        {
            return false;
        }
    }

    // JAVADOC METHOD $$ func_76543_b
    private String pickMobSpawner(Random par1Random)
    {
        return DungeonHooks.getRandomDungeonMob(par1Random);
    }
}