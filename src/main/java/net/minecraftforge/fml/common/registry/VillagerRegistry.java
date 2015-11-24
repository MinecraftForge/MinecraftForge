/*
 * Forge Mod Loader
 * Copyright (c) 2012-2013 cpw.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 *
 * Contributors:
 *     cpw - implementation
 */

package net.minecraftforge.fml.common.registry;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.apache.commons.lang3.Validate;

import net.minecraft.block.Block;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.passive.EntityVillager.*;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ObjectIntIdentityMap;
import net.minecraft.util.RegistryNamespacedDefaultedByKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.registry.VillagerRegistry.VillagerProfession;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

/**
 * Registry for villager trading control
 *
 * @author cpw
 *
 */
public class VillagerRegistry
{
    private static final VillagerRegistry INSTANCE = new VillagerRegistry();

    private Map<Class<?>, IVillageCreationHandler> villageCreationHandlers = Maps.newHashMap();
    private List<Integer> newVillagerIds = Lists.newArrayList();
    @SideOnly(Side.CLIENT)
    private Map<Integer, ResourceLocation> newVillagers;

    private VillagerRegistry()
    {
        init();
    }

    /**
     * Allow access to the {@link net.minecraft.world.gen.structure.StructureVillagePieces} array controlling new village
     * creation so you can insert your own new village pieces
     *
     * @author cpw
     *
     */
    public interface IVillageCreationHandler
    {
        /**
         * Called when {@link net.minecraft.world.gen.structure.MapGenVillage} is creating a new village
         *
         * @param random
         * @param i
         */
        StructureVillagePieces.PieceWeight getVillagePieceWeight(Random random, int i);

        /**
         * The class of the root structure component to add to the village
         */
        Class<?> getComponentClass();


        /**
         * Build an instance of the village component {@link net.minecraft.world.gen.structure.StructureVillagePieces}
         * @param villagePiece
         * @param startPiece
         * @param pieces
         * @param random
         * @param p1
         * @param p2
         * @param p3
         * @param facing
         * @param p5
         */
        Object buildComponent(StructureVillagePieces.PieceWeight villagePiece, StructureVillagePieces.Start startPiece, @SuppressWarnings("rawtypes") List pieces, Random random, int p1,
                int p2, int p3, EnumFacing facing, int p5);
    }

    public static VillagerRegistry instance()
    {
        return INSTANCE;
    }

    /**
     * Register your villager id
     * @param id
     */
    @Deprecated // Doesn't work at all.
    public void registerVillagerId(int id)
    {
    }
    /**
     * Register a new skin for a villager type
     *
     * @param villagerId
     * @param villagerSkin
     */
    @SideOnly(Side.CLIENT)
    @Deprecated  // Doesn't work at all.
    private void registerVillagerSkin(int villagerId, ResourceLocation villagerSkin)
    {
    }

    /**
     * Register a new village creation handler
     *
     * @param handler
     */
    public void registerVillageCreationHandler(IVillageCreationHandler handler)
    {
        villageCreationHandlers.put(handler.getComponentClass(), handler);
    }

    /**
     * Callback to setup new villager types
     *
     * @param villagerType
     * @param defaultSkin
     */
    @SideOnly(Side.CLIENT)
    @Deprecated // Doesn't work at all.
    public static ResourceLocation getVillagerSkin(int villagerType, ResourceLocation defaultSkin)
    {
        return defaultSkin;
    }

    /**
     * Callback to setup new villager types
     *
     * @param villagerType
     * @param defaultSkin
     */
    @SideOnly(Side.CLIENT)
    public static ResourceLocation getVillagerSkin(EntityVillager villager, ResourceLocation defaultSkin)
    {
        //Using this instead of getProfession(EntityVillager) as that method will always at least return the farmer.
        VillagerProfession profession = INSTANCE.professions.getObjectById(villager.getProfession());
        if(profession!=null)
        {
            return profession.texture;
        }
        return defaultSkin;
    }

    /**
     * Returns a list of all added villager types
     *
     * @return newVillagerIds
     */
    @Deprecated // Doesn't work at all.
    public static Collection<Integer> getRegisteredVillagers()
    {
        return Collections.unmodifiableCollection(instance().newVillagerIds);
    }

    public static void addExtraVillageComponents(@SuppressWarnings("rawtypes") ArrayList components, Random random, int i)
    {
        @SuppressWarnings("unchecked")
        List<StructureVillagePieces.PieceWeight> parts = components;
        for (IVillageCreationHandler handler : instance().villageCreationHandlers.values())
        {
            parts.add(handler.getVillagePieceWeight(random, i));
        }
    }

    public static Object getVillageComponent(StructureVillagePieces.PieceWeight villagePiece, StructureVillagePieces.Start startPiece, @SuppressWarnings("rawtypes") List pieces, Random random,
            int p1, int p2, int p3, EnumFacing facing, int p5)
    {
        return instance().villageCreationHandlers.get(villagePiece.villagePieceClass).buildComponent(villagePiece, startPiece, pieces, random, p1, p2, p3, facing, p5);
    }

    public void register(VillagerProfession prof)
    {
        register(prof, -1);
    }
    private void register(VillagerProfession prof, int id)
    {
        professions.add(id, prof.name.toString(), prof, availabilityMap);
        availabilityMap.set(professions.getId(prof));
    }

    private boolean hasInit = false;
    public FMLControlledNamespacedRegistry<VillagerProfession> professions = GameData.createRegistry("villagerprofessions", VillagerProfession.class, 0, 1024);
    private BitSet availabilityMap = new BitSet();

    private void init()
    {
        if (hasInit)
            return;

        VillagerProfession prof = new VillagerProfession("minecraft:farmer", "minecraft:textures/entity/villager/farmer.png");
        {
            register(prof, 0);
            (new VillagerCareer(prof, "farmer"    )).init(VanillaTrades.trades[0][0]);
            (new VillagerCareer(prof, "fisherman" )).init(VanillaTrades.trades[0][1]);
            (new VillagerCareer(prof, "shepherd"  )).init(VanillaTrades.trades[0][2]);
            (new VillagerCareer(prof, "fletcher"  )).init(VanillaTrades.trades[0][3]);
        }
        prof = new VillagerProfession("minecraft:librarian", "minecraft:textures/entity/villager/librarian.png");
        {
            register(prof, 1);
            (new VillagerCareer(prof, "librarian")).init(VanillaTrades.trades[1][0]);
        }
        prof = new VillagerProfession("minecraft:priest", "minecraft:textures/entity/villager/priest.png");
        {
            register(prof, 2);
            (new VillagerCareer(prof, "cleric")).init(VanillaTrades.trades[2][0]);
        }
        prof = new VillagerProfession("minecraft:smith", "minecraft:textures/entity/villager/smith.png");
        {
            register(prof, 3);
            (new VillagerCareer(prof, "armor" )).init(VanillaTrades.trades[3][0]);
            (new VillagerCareer(prof, "weapon")).init(VanillaTrades.trades[3][1]);
            (new VillagerCareer(prof, "tool"  )).init(VanillaTrades.trades[3][2]);
        }
        prof = new VillagerProfession("minecraft:butcher", "minecraft:textures/entity/villager/butcher.png");
        {
            register(prof, 4);
            (new VillagerCareer(prof, "butcher")).init(VanillaTrades.trades[4][0]);
            (new VillagerCareer(prof, "leather")).init(VanillaTrades.trades[4][1]);
        }
    }

    public static class VillagerProfession
    {
        private ResourceLocation name;
        public ResourceLocation texture;
        private List<VillagerCareer> careers = Lists.newArrayList();
        private RegistryDelegate<VillagerProfession> delegate = GameData.getRegistry("villagerprofessions", VillagerProfession.class).getDelegate(this, VillagerProfession.class);

        public VillagerProfession(String name, String texture)
        {
            this.name = new ResourceLocation(name);
            this.texture = new ResourceLocation(texture);
            ((RegistryDelegate.Delegate<VillagerProfession>)delegate).setName(name);
        }

        private void register(VillagerCareer career)
        {
            Validate.isTrue(!careers.contains(career), "Attempted to register career that is already registered.");
            Validate.isTrue(career.profession == this, "Attempted to register career for the wrong profession.");
            career.id = careers.size();
            careers.add(career);
        }

        public int getId()
        {
            return INSTANCE.professions.getId(this);
        }

        public int getCareerCount()
        {
            return careers.size();
        }

        public String getName()
        {
            return name.getResourcePath();
        }
    }

    public static class VillagerCareer
    {
        private VillagerProfession profession;
        private String name;
        private int id;
        private HashMap<Integer, ArrayList<ITradeList>> levelTrades = new HashMap<Integer, ArrayList<ITradeList>>();

        public VillagerCareer(VillagerProfession parent, String name)
        {
            this.profession = parent;
            this.name = name;
            parent.register(this);
        }

        private VillagerCareer init(EntityVillager.ITradeList[][] traids)
        {
            for(int i = 0; i<traids.length; i++)
            {
                ArrayList<ITradeList> tradesForLevel =  Lists.newArrayList(traids[i]);
                levelTrades.put(i, tradesForLevel);
            }
            return this;
        }

        public void addTrade(ITradeList trade, int careerLevel)
        {
            ArrayList<ITradeList> tradesForLevel = levelTrades.get(careerLevel);
            if(tradesForLevel==null)
            {
                tradesForLevel = new ArrayList<ITradeList>();
                levelTrades.put(careerLevel, tradesForLevel);
            }
            tradesForLevel.add(trade);
        }

        private void populateBuyingList(MerchantRecipeList recipeList, int careerLevel, Random random)
        {
            while(careerLevel - 1 >= 0)
            {
                ArrayList<ITradeList> tradesForLevel = levelTrades.get(careerLevel - 1);
                if(tradesForLevel!=null)
                {
                    for(ITradeList trade: tradesForLevel)
                    {
                        trade.modifyMerchantRecipeList(recipeList, random);
                    }
                    return;
                }
                careerLevel--;
            }
        }

        @Override
        public boolean equals(Object o)
        {
            if (o == this) return true;
            if (!(o instanceof VillagerCareer)) return false;
            VillagerCareer oc = (VillagerCareer)o;
            return name.equals(oc.name) && profession == oc.profession;
        }

        public int getId()
        {
            return id + 1;
        }

        public String getName()
        {
            return name;
        }
    }

    /**
     * Hook called when spawning a Villager, sets it's profession to a random registered profession.
     *
     * @param entity The new entity
     * @param rand The world's RNG
     */
    public static void setRandomProfession(EntityVillager entity, Random rand)
    {
        ArrayList entries = new ArrayList(INSTANCE.professions.getKeys());
        int prof = rand.nextInt(entries.size());
        VillagerProfession profession = INSTANCE.professions.getObject(entries.get(prof));
        entity.setProfession(profession.getId());
    }
    
    public static void populateBuyingList(MerchantRecipeList recipeList, EntityVillager entity, int careerLevel, Random random)
    {
        VillagerProfession profession = INSTANCE.professions.getObjectById(entity.getProfession());
        //Career is at least 1.
        if(entity.getCareer() > profession.careers.size())
        {
            return;
        }
        VillagerCareer career = profession.careers.get(entity.getCareer() - 1);
        career.populateBuyingList(recipeList, careerLevel, random);
    }

    /** INTERNEL METHOD! DONT USE IT!! ONLY FOR FORGE!!! */
    public static int getNumberOfCareers(int professionID)
    {
        return getProfession(professionID).getCareerCount();
    }

    /** INTERNEL METHOD! DONT USE IT!! ONLY FOR FORGE!!! */
    public static VillagerProfession getProfession(int professionID)
    {
        VillagerProfession profession = INSTANCE.professions.getObjectById(professionID);
        if(profession == null)
        {
            return INSTANCE.professions.getObjectById(0);
        }
        return profession;
    }

    public static VillagerProfession getProfession(String professionID)
    {
        VillagerProfession profession = INSTANCE.professions.getObject(professionID);
        if(profession == null)
        {
            return INSTANCE.professions.getObjectById(0);
        }
        return profession;
    }

    public static VillagerProfession getProfession(EntityVillager villager)
    {
        VillagerProfession profession = INSTANCE.professions.getObjectById(villager.getProfession());
        if(profession == null)
        {
            return INSTANCE.professions.getObjectById(0);
        }
        return profession;
    }

    public static String getVillagerDisplay(EntityVillager entity)
    {
        VillagerProfession profession = getProfession(entity.getProfession());
        VillagerCareer career = profession.careers.get(entity.getCareer() - 1);
        return career.name;
    }

    public static int getProfessionCount()
    {
    	return INSTANCE.professions.getKeys().size();
    }

    //TODO: Figure out a good generic system for this. Put on hold for Patches.

    private static class VanillaTrades
    {
        //This field is moved from EntityVillager over to here.
        //Moved to inner class to stop static initializer issues.
        //It is nasty I know but it's vanilla.
        private static final ITradeList[][][][] trades =
        {
            {
                {
                    {
                        new EmeraldForItems(Items.wheat, new PriceInfo(18, 22)),
                        new EmeraldForItems(Items.potato, new PriceInfo(15, 19)),
                        new EmeraldForItems(Items.carrot, new PriceInfo(15, 19)),
                        new ListItemForEmeralds(Items.bread, new PriceInfo(-4, -2))
                    },
                    {
                        new EmeraldForItems(Item.getItemFromBlock(Blocks.pumpkin), new PriceInfo(8, 13)),
                        new ListItemForEmeralds(Items.pumpkin_pie, new PriceInfo(-3, -2))
                    },
                    {
                        new EmeraldForItems(Item.getItemFromBlock(Blocks.melon_block), new PriceInfo(7, 12)),
                        new ListItemForEmeralds(Items.apple, new PriceInfo(-5, -7))
                    },
                    {
                        new ListItemForEmeralds(Items.cookie, new PriceInfo(-6, -10)),
                        new ListItemForEmeralds(Items.cake, new PriceInfo(1, 1))
                    }
                },
                {
                    {
                        new EmeraldForItems(Items.string, new PriceInfo(15, 20)),
                        new EmeraldForItems(Items.coal, new PriceInfo(16, 24)),
                        new ItemAndEmeraldToItem(Items.fish, new PriceInfo(6, 6), Items.cooked_fish, new PriceInfo(6, 6))
                    },
                    {
                        new ListEnchantedItemForEmeralds(Items.fishing_rod, new PriceInfo(7, 8))
                    }
                },
                {
                    {
                        new EmeraldForItems(Item.getItemFromBlock(Blocks.wool), new PriceInfo(16, 22)),
                        new ListItemForEmeralds(Items.shears, new PriceInfo(3, 4))
                    },
                    {
                        new ListItemForEmeralds(new ItemStack(Blocks.wool, 1, 0), new PriceInfo(1, 2)),
                        new ListItemForEmeralds(new ItemStack(Blocks.wool, 1, 1), new PriceInfo(1, 2)),
                        new ListItemForEmeralds(new ItemStack(Blocks.wool, 1, 2), new PriceInfo(1, 2)),
                        new ListItemForEmeralds(new ItemStack(Blocks.wool, 1, 3), new PriceInfo(1, 2)),
                        new ListItemForEmeralds(new ItemStack(Blocks.wool, 1, 4), new PriceInfo(1, 2)),
                        new ListItemForEmeralds(new ItemStack(Blocks.wool, 1, 5), new PriceInfo(1, 2)),
                        new ListItemForEmeralds(new ItemStack(Blocks.wool, 1, 6), new PriceInfo(1, 2)),
                        new ListItemForEmeralds(new ItemStack(Blocks.wool, 1, 7), new PriceInfo(1, 2)),
                        new ListItemForEmeralds(new ItemStack(Blocks.wool, 1, 8), new PriceInfo(1, 2)),
                        new ListItemForEmeralds(new ItemStack(Blocks.wool, 1, 9), new PriceInfo(1, 2)),
                        new ListItemForEmeralds(new ItemStack(Blocks.wool, 1, 10), new PriceInfo(1, 2)),
                        new ListItemForEmeralds(new ItemStack(Blocks.wool, 1, 11), new PriceInfo(1, 2)),
                        new ListItemForEmeralds(new ItemStack(Blocks.wool, 1, 12), new PriceInfo(1, 2)),
                        new ListItemForEmeralds(new ItemStack(Blocks.wool, 1, 13), new PriceInfo(1, 2)),
                        new ListItemForEmeralds(new ItemStack(Blocks.wool, 1, 14), new PriceInfo(1, 2)),
                        new ListItemForEmeralds(new ItemStack(Blocks.wool, 1, 15), new PriceInfo(1, 2))
                    }
                },
                {
                    {
                        new EmeraldForItems(Items.string, new PriceInfo(15, 20)),
                        new ListItemForEmeralds(Items.arrow, new PriceInfo(-12, -8))
                    },
                    {
                        new ListItemForEmeralds(Items.bow, new PriceInfo(2, 3)),
                        new ItemAndEmeraldToItem(Item.getItemFromBlock(Blocks.gravel), new PriceInfo(10, 10), Items.flint, new PriceInfo(6, 10))
                    }
                }
            },
            {
                {
                    {
                        new EmeraldForItems(Items.paper, new PriceInfo(24, 36)),
                        new ListEnchantedBookForEmeralds()
                    },
                    {
                        new EmeraldForItems(Items.book, new PriceInfo(8, 10)),
                        new ListItemForEmeralds(Items.compass, new PriceInfo(10, 12)),
                        new ListItemForEmeralds(Item.getItemFromBlock(Blocks.bookshelf), new PriceInfo(3, 4))
                    },
                    {
                        new EmeraldForItems(Items.written_book, new PriceInfo(2, 2)),
                        new ListItemForEmeralds(Items.clock, new PriceInfo(10, 12)),
                        new ListItemForEmeralds(Item.getItemFromBlock(Blocks.glass), new PriceInfo(-5, -3))
                    },
                    {
                        new ListEnchantedBookForEmeralds()
                    },
                    {
                        new ListEnchantedBookForEmeralds()
                    },
                    {
                        new ListItemForEmeralds(Items.name_tag, new PriceInfo(20, 22))
                    }
                }
            },
            {
                {
                    {
                        new EmeraldForItems(Items.rotten_flesh, new PriceInfo(36, 40)),
                        new EmeraldForItems(Items.gold_ingot, new PriceInfo(8, 10))
                    },
                    {
                        new ListItemForEmeralds(Items.redstone, new PriceInfo(-4, -1)),
                        new ListItemForEmeralds(new ItemStack(Items.dye, 1, EnumDyeColor.BLUE.getDyeDamage()),
                        new PriceInfo(-2, -1))
                    },
                    {
                        new ListItemForEmeralds(Items.ender_eye, new PriceInfo(7, 11)),
                        new ListItemForEmeralds(Item.getItemFromBlock(Blocks.glowstone), new PriceInfo(-3, -1))
                    },
                    {
                        new ListItemForEmeralds(Items.experience_bottle, new PriceInfo(3, 11))
                    }
                }
            },
            {
                {
                    {
                        new EmeraldForItems(Items.coal, new PriceInfo(16, 24)),
                        new ListItemForEmeralds(Items.iron_helmet, new PriceInfo(4, 6))
                    },
                    {
                        new EmeraldForItems(Items.iron_ingot, new PriceInfo(7, 9)),
                        new ListItemForEmeralds(Items.iron_chestplate, new PriceInfo(10, 14))
                    },
                    {
                        new EmeraldForItems(Items.diamond, new PriceInfo(3, 4)),
                        new ListEnchantedItemForEmeralds(Items.diamond_chestplate, new PriceInfo(16, 19))
                    },
                    {
                        new ListItemForEmeralds(Items.chainmail_boots, new PriceInfo(5, 7)),
                        new ListItemForEmeralds(Items.chainmail_leggings, new PriceInfo(9, 11)),
                        new ListItemForEmeralds(Items.chainmail_helmet, new PriceInfo(5, 7)),
                        new ListItemForEmeralds(Items.chainmail_chestplate, new PriceInfo(11, 15))
                    }
                },
                {
                    {
                        new EmeraldForItems(Items.coal, new PriceInfo(16, 24)),
                        new ListItemForEmeralds(Items.iron_axe, new PriceInfo(6, 8))
                    },
                    {
                        new EmeraldForItems(Items.iron_ingot, new PriceInfo(7, 9)),
                        new ListEnchantedItemForEmeralds(Items.iron_sword, new PriceInfo(9, 10))
                    },
                    {
                        new EmeraldForItems(Items.diamond, new PriceInfo(3, 4)),
                        new ListEnchantedItemForEmeralds(Items.diamond_sword, new PriceInfo(12, 15)),
                        new ListEnchantedItemForEmeralds(Items.diamond_axe, new PriceInfo(9, 12))
                    }
                },
                {
                    {
                        new EmeraldForItems(Items.coal, new PriceInfo(16, 24)),
                        new ListEnchantedItemForEmeralds(Items.iron_shovel, new PriceInfo(5, 7))
                    },
                    {
                        new EmeraldForItems(Items.iron_ingot, new PriceInfo(7, 9)),
                        new ListEnchantedItemForEmeralds(Items.iron_pickaxe, new PriceInfo(9, 11))
                    },
                    {
                        new EmeraldForItems(Items.diamond, new PriceInfo(3, 4)),
                        new ListEnchantedItemForEmeralds(Items.diamond_pickaxe, new PriceInfo(12, 15))
                    }
                }
            },
            {
                {
                    {
                        new EmeraldForItems(Items.porkchop, new PriceInfo(14, 18)),
                        new EmeraldForItems(Items.chicken, new PriceInfo(14, 18))
                    },
                    {
                        new EmeraldForItems(Items.coal, new PriceInfo(16, 24)),
                        new ListItemForEmeralds(Items.cooked_porkchop, new PriceInfo(-7, -5)),
                        new ListItemForEmeralds(Items.cooked_chicken, new PriceInfo(-8, -6))
                    }
                },
                {
                    {
                        new EmeraldForItems(Items.leather, new PriceInfo(9, 12)),
                        new ListItemForEmeralds(Items.leather_leggings, new PriceInfo(2, 4))
                    },
                    {
                        new ListEnchantedItemForEmeralds(Items.leather_chestplate, new PriceInfo(7, 12))
                    },
                    {
                        new ListItemForEmeralds(Items.saddle, new PriceInfo(8, 10))
                    }
                }
            }
        };
    }
}
