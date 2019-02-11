/*
 * Minecraft Forge
 * Copyright (c) 2016-2019.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.oredict;

public class OreDictionary
{
/*

    private static void initVanillaEntries()
    {
        if (!hasInit)
        {
            // tree- and wood-related things
            registerOre("treeLeaves",  new ItemStack(Blocks.LEAVES, 1, WILDCARD_VALUE));
            registerOre("treeLeaves",  new ItemStack(Blocks.LEAVES2, 1, WILDCARD_VALUE));
            registerOre("vine",        Blocks.VINE);

            // crops
            registerOre("cropWheat",   Items.WHEAT);
            registerOre("cropPotato",  Items.POTATO);
            registerOre("cropCarrot",  Items.CARROT);
            registerOre("cropNetherWart", Items.NETHER_WART);
            registerOre("sugarcane",   Items.REEDS);
            registerOre("blockCactus", Blocks.CACTUS);

            // misc materials
            registerOre("paper",       new ItemStack(Items.PAPER));

            // mob drops
            registerOre("slimeball",   Items.SLIME_BALL);
            registerOre("enderpearl",  Items.ENDER_PEARL);
            registerOre("bone",        Items.BONE);
            registerOre("gunpowder",   Items.GUNPOWDER);
            registerOre("string",      Items.STRING);
            registerOre("netherStar",  Items.NETHER_STAR);
            registerOre("leather",     Items.LEATHER);
            registerOre("feather",     Items.FEATHER);
            registerOre("egg",         Items.EGG);

            // blocks
            registerOre("dirt",        Blocks.DIRT);
            registerOre("grass",       Blocks.GRASS);
            registerOre("gravel",      Blocks.GRAVEL);
            registerOre("sandstone",   new ItemStack(Blocks.SANDSTONE, 1, WILDCARD_VALUE));
            registerOre("sandstone",   new ItemStack(Blocks.RED_SANDSTONE, 1, WILDCARD_VALUE));
            registerOre("netherrack",  Blocks.NETHERRACK);
            registerOre("obsidian",    Blocks.OBSIDIAN);
            registerOre("glowstone",   Blocks.GLOWSTONE);
            registerOre("endstone",    Blocks.END_STONE);
            registerOre("torch",       Blocks.TORCH);
            registerOre("workbench",   Blocks.CRAFTING_TABLE);
            registerOre("blockSlime",    Blocks.SLIME_BLOCK);
            registerOre("blockPrismarine", new ItemStack(Blocks.PRISMARINE, 1, BlockPrismarine.EnumType.ROUGH.getMetadata()));
            registerOre("blockPrismarineBrick", new ItemStack(Blocks.PRISMARINE, 1, BlockPrismarine.EnumType.BRICKS.getMetadata()));
            registerOre("blockPrismarineDark", new ItemStack(Blocks.PRISMARINE, 1, BlockPrismarine.EnumType.DARK.getMetadata()));
            registerOre("blockGlassColorless", Blocks.GLASS);
            registerOre("blockGlass",    Blocks.GLASS);
            registerOre("blockGlass",    new ItemStack(Blocks.STAINED_GLASS, 1, WILDCARD_VALUE));
            //blockGlass{Color} is added below with dyes
            registerOre("paneGlassColorless", Blocks.GLASS_PANE);
            registerOre("paneGlass",     Blocks.GLASS_PANE);
            registerOre("paneGlass",     new ItemStack(Blocks.STAINED_GLASS_PANE, 1, WILDCARD_VALUE));
            //paneGlass{Color} is added below with dyes
        }

        // Build our list of items to replace with ore tags
        Map<ItemStack, String> replacements = new HashMap<ItemStack, String>();

        // wood-related things
        replacements.put(new ItemStack(Items.STICK), "stickWood");
        replacements.put(new ItemStack(Blocks.PLANKS, 1, 0), "plankWood");
        replacements.put(new ItemStack(Blocks.PLANKS, 1, 1), "plankWood");
        replacements.put(new ItemStack(Blocks.PLANKS, 1, 2), "plankWood");
        replacements.put(new ItemStack(Blocks.PLANKS, 1, 3), "plankWood");
        replacements.put(new ItemStack(Blocks.PLANKS, 1, 4), "plankWood");
        replacements.put(new ItemStack(Blocks.PLANKS, 1, 5), "plankWood");
        replacements.put(new ItemStack(Blocks.PLANKS, 1, WILDCARD_VALUE), "plankWood");
        replacements.put(new ItemStack(Blocks.WOODEN_SLAB, 1, WILDCARD_VALUE), "slabWood");

        // ingots/nuggets
        replacements.put(new ItemStack(Items.GOLD_INGOT), "ingotGold");
        replacements.put(new ItemStack(Items.IRON_INGOT), "ingotIron");

        // gems and dusts
        replacements.put(new ItemStack(Items.DIAMOND), "gemDiamond");
        replacements.put(new ItemStack(Items.EMERALD), "gemEmerald");
        replacements.put(new ItemStack(Items.PRISMARINE_SHARD), "gemPrismarine");
        replacements.put(new ItemStack(Items.PRISMARINE_CRYSTALS), "dustPrismarine");
        replacements.put(new ItemStack(Items.REDSTONE), "dustRedstone");
        replacements.put(new ItemStack(Items.GLOWSTONE_DUST), "dustGlowstone");

        // crops
        replacements.put(new ItemStack(Items.REEDS), "sugarcane");
        replacements.put(new ItemStack(Blocks.CACTUS), "blockCactus");

        // misc materials
        replacements.put(new ItemStack(Items.PAPER), "paper");

        // mob drops
        replacements.put(new ItemStack(Items.SLIME_BALL), "slimeball");
        replacements.put(new ItemStack(Items.STRING), "string");
        replacements.put(new ItemStack(Items.LEATHER), "leather");
        replacements.put(new ItemStack(Items.ENDER_PEARL), "enderpearl");
        replacements.put(new ItemStack(Items.GUNPOWDER), "gunpowder");
        replacements.put(new ItemStack(Items.NETHER_STAR), "netherStar");
        replacements.put(new ItemStack(Items.FEATHER), "feather");
        replacements.put(new ItemStack(Items.BONE), "bone");
        replacements.put(new ItemStack(Items.EGG), "egg");

        // blocks
        replacements.put(new ItemStack(Blocks.STONE), "stone");
        replacements.put(new ItemStack(Blocks.COBBLESTONE), "cobblestone");
        replacements.put(new ItemStack(Blocks.COBBLESTONE, 1, WILDCARD_VALUE), "cobblestone");
        replacements.put(new ItemStack(Blocks.GLOWSTONE), "glowstone");
        replacements.put(new ItemStack(Blocks.GLASS), "blockGlassColorless");
        replacements.put(new ItemStack(Blocks.PRISMARINE), "prismarine");
        replacements.put(new ItemStack(Blocks.STONE, 1, 1), "stoneGranite");
        replacements.put(new ItemStack(Blocks.STONE, 1, 2), "stoneGranitePolished");
        replacements.put(new ItemStack(Blocks.STONE, 1, 3), "stoneDiorite");
        replacements.put(new ItemStack(Blocks.STONE, 1, 4), "stoneDioritePolished");
        replacements.put(new ItemStack(Blocks.STONE, 1, 5), "stoneAndesite");
        replacements.put(new ItemStack(Blocks.STONE, 1, 6), "stoneAndesitePolished");

        // chests
        replacements.put(new ItemStack(Blocks.CHEST), "chestWood");
        replacements.put(new ItemStack(Blocks.ENDER_CHEST), "chestEnder");
        replacements.put(new ItemStack(Blocks.TRAPPED_CHEST), "chestTrapped");

        // Register dyes
        String[] dyes =
        {
            "Black",
            "Red",
            "Green",
            "Brown",
            "Blue",
            "Purple",
            "Cyan",
            "LightGray",
            "Gray",
            "Pink",
            "Lime",
            "Yellow",
            "LightBlue",
            "Magenta",
            "Orange",
            "White"
        };

        for(int i = 0; i < 16; i++)
        {
            ItemStack block = new ItemStack(Blocks.STAINED_GLASS, 1, 15 - i);
            ItemStack pane = new ItemStack(Blocks.STAINED_GLASS_PANE, 1, 15 - i);
            if (!hasInit)
            {
                registerOre("blockGlass" + dyes[i], block);
                registerOre("paneGlass"  + dyes[i], pane);
            }
            replacements.put(dye,   "dye" + dyes[i]);
            replacements.put(block, "blockGlass" + dyes[i]);
            replacements.put(pane,  "paneGlass" + dyes[i]);
        }
        hasInit = true;

        ItemStack[] replaceStacks = replacements.keySet().toArray(new ItemStack[replacements.keySet().size()]);

        // Ignore recipes for the following items
        ItemStack[] exclusions = new ItemStack[]
        {
            new ItemStack(Blocks.LAPIS_BLOCK),
            new ItemStack(Items.COOKIE),
            new ItemStack(Blocks.STONEBRICK),
            new ItemStack(Blocks.STONE_SLAB, 1, WILDCARD_VALUE),
            new ItemStack(Blocks.STONE_STAIRS),
            new ItemStack(Blocks.COBBLESTONE_WALL),
            new ItemStack(Blocks.OAK_FENCE),
            new ItemStack(Blocks.OAK_FENCE_GATE),
            new ItemStack(Blocks.OAK_STAIRS),
            new ItemStack(Blocks.SPRUCE_FENCE),
            new ItemStack(Blocks.SPRUCE_FENCE_GATE),
            new ItemStack(Blocks.SPRUCE_STAIRS),
            new ItemStack(Blocks.BIRCH_FENCE_GATE),
            new ItemStack(Blocks.BIRCH_FENCE),
            new ItemStack(Blocks.BIRCH_STAIRS),
            new ItemStack(Blocks.JUNGLE_FENCE),
            new ItemStack(Blocks.JUNGLE_FENCE_GATE),
            new ItemStack(Blocks.JUNGLE_STAIRS),
            new ItemStack(Blocks.ACACIA_FENCE),
            new ItemStack(Blocks.ACACIA_FENCE_GATE),
            new ItemStack(Blocks.ACACIA_STAIRS),
            new ItemStack(Blocks.DARK_OAK_FENCE),
            new ItemStack(Blocks.DARK_OAK_FENCE_GATE),
            new ItemStack(Blocks.DARK_OAK_STAIRS),
            new ItemStack(Blocks.WOODEN_SLAB, 1, WILDCARD_VALUE),
            new ItemStack(Blocks.GLASS_PANE),
            new ItemStack(Blocks.BONE_BLOCK), // Bone Block, to prevent conversion of dyes into bone meal.
            new ItemStack(Items.BOAT),
            new ItemStack(Items.SPRUCE_BOAT),
            new ItemStack(Items.BIRCH_BOAT),
            new ItemStack(Items.JUNGLE_BOAT),
            new ItemStack(Items.ACACIA_BOAT),
            new ItemStack(Items.DARK_OAK_BOAT),
            new ItemStack(Items.OAK_DOOR),
            new ItemStack(Items.SPRUCE_DOOR),
            new ItemStack(Items.BIRCH_DOOR),
            new ItemStack(Items.JUNGLE_DOOR),
            new ItemStack(Items.ACACIA_DOOR),
            new ItemStack(Items.DARK_OAK_DOOR),
            ItemStack.EMPTY //So the above can have a comma and we don't have to keep editing extra lines.
        };

        LOGGER.info("Starts to replace vanilla recipe ingredients with ore ingredients.");
        int replaced = 0;
        // Search vanilla recipes for recipes to replace
        for(IRecipe obj : CraftingManager.REGISTRY)
        {
            if(obj.getClass() == ShapedRecipes.class || obj.getClass() == ShapelessRecipes.class)
            {
                ItemStack output = obj.getRecipeOutput();
                if (!output.isEmpty() && containsMatch(false, new ItemStack[]{ output }, exclusions))
                {
                    continue;
                }

                Set<Ingredient> replacedIngs = new HashSet<>();
                NonNullList<Ingredient> lst = obj.getIngredients();
                for (int x = 0; x < lst.size(); x++)
                {
                    Ingredient ing = lst.get(x);
                    ItemStack[] ingredients = ing.getMatchingStacks();
                    String oreName = null;
                    boolean skip = false;

                    for (ItemStack stack : ingredients)
                    {
                        boolean matches = false;
                        for (Entry<ItemStack, String> ent : replacements.entrySet())
                        {
                            if (itemMatches(ent.getKey(), stack, true))
                            {
                                matches = true;
                                if (oreName != null && !oreName.equals(ent.getValue()))
                                {
                                    LOGGER.info("Invalid recipe found with multiple oredict ingredients in the same ingredient..."); //TODO: Write a dumper?
                                    skip = true;
                                    break;
                                }
                                else if (oreName == null)
                                {
                                    oreName = ent.getValue();
                                    break;
                                }
                            }
                        }
                        if (!matches && oreName != null)
                        {
                            //TODO: Properly fix this, Broken recipe example: Beds
                            //FMLLog.info("Invalid recipe found with ingredient that partially matches ore entries..."); //TODO: Write a dumper?
                            skip = true;
                        }
                        if (skip)
                            break;
                    }
                    if (!skip && oreName != null)
                    {
                        //Replace!
                        lst.set(x, new OreIngredient(oreName));
                        replaced++;
                        if(DEBUG && replacedIngs.add(ing))
                        {
                            String recipeName = obj.getRegistryName().getResourcePath();
                            LOGGER.debug("Replaced {} of the recipe \'{}\' with \"{}\".", ing.getMatchingStacks(), recipeName, oreName);
                        }
                    }
                }
            }
        }

        LOGGER.info("Replaced {} ore ingredients", replaced);
    }
    */
}
