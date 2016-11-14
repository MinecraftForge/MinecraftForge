/*
 * Minecraft Forge
 * Copyright (c) 2016.
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

package net.minecraftforge.fml.common.registry;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.Validate;

import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.monster.ZombieType;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.passive.EntityVillager.ITradeList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import net.minecraft.world.gen.structure.StructureVillagePieces.PieceWeight;
import net.minecraft.world.gen.structure.StructureVillagePieces.Village;
/**
 * Registry for villager trading control
 */
public class VillagerRegistry
{
    public static final ResourceLocation PROFESSIONS = new ResourceLocation("minecraft:villagerprofessions");
    private static final VillagerRegistry INSTANCE = new VillagerRegistry();

    private Map<Class<?>, IVillageCreationHandler> villageCreationHandlers = Maps.newHashMap();

    private VillagerRegistry()
    {
        init();
    }

    /**
     * Allow access to the {@link net.minecraft.world.gen.structure.StructureVillagePieces} array controlling new village
     * creation so you can insert your own new village pieces
     *
     * @author cpw
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
         *
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
        Village buildComponent(StructureVillagePieces.PieceWeight villagePiece, StructureVillagePieces.Start startPiece, List<StructureComponent> pieces, Random random, int p1,
                               int p2, int p3, EnumFacing facing, int p5);
    }

    public static VillagerRegistry instance()
    {
        return INSTANCE;
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

    public static void addExtraVillageComponents(List<PieceWeight> list, Random random, int i)
    {
        List<StructureVillagePieces.PieceWeight> parts = list;
        for (IVillageCreationHandler handler : instance().villageCreationHandlers.values())
        {
            parts.add(handler.getVillagePieceWeight(random, i));
        }
    }

    public static Village getVillageComponent(StructureVillagePieces.PieceWeight villagePiece, StructureVillagePieces.Start startPiece, List<StructureComponent> pieces, Random random,
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
        professions.register(id, prof.name, prof);
    }

    private boolean hasInit = false;
    private FMLControlledNamespacedRegistry<VillagerProfession> professions = PersistentRegistryManager.createRegistry(PROFESSIONS, VillagerProfession.class, null, 0, 1024, true, null, null, null, null);
    public IForgeRegistry<VillagerProfession> getRegistry() { return this.professions; }


    private void init()
    {
        if (hasInit)
        {
            return;
        }

        VillagerProfession prof = new VillagerProfession("minecraft:farmer",
                "minecraft:textures/entity/villager/farmer.png",
                "minecraft:textures/entity/zombie_villager/zombie_farmer.png");
        {
            register(prof, 0);
            (new VillagerCareer(prof, "farmer")).init(VanillaTrades.trades[0][0]);
            (new VillagerCareer(prof, "fisherman")).init(VanillaTrades.trades[0][1]);
            (new VillagerCareer(prof, "shepherd")).init(VanillaTrades.trades[0][2]);
            (new VillagerCareer(prof, "fletcher")).init(VanillaTrades.trades[0][3]);
        }
        prof = new VillagerProfession("minecraft:librarian",
                "minecraft:textures/entity/villager/librarian.png",
                "minecraft:textures/entity/zombie_villager/zombie_librarian.png");
        {
            register(prof, 1);
            (new VillagerCareer(prof, "librarian")).init(VanillaTrades.trades[1][0]);
        }
        prof = new VillagerProfession("minecraft:priest",
                "minecraft:textures/entity/villager/priest.png",
                "minecraft:textures/entity/zombie_villager/zombie_priest.png");
        {
            register(prof, 2);
            (new VillagerCareer(prof, "cleric")).init(VanillaTrades.trades[2][0]);
        }
        prof = new VillagerProfession("minecraft:smith",
                "minecraft:textures/entity/villager/smith.png",
                "minecraft:textures/entity/zombie_villager/zombie_smith.png");
        {
            register(prof, 3);
            (new VillagerCareer(prof, "armor")).init(VanillaTrades.trades[3][0]);
            (new VillagerCareer(prof, "weapon")).init(VanillaTrades.trades[3][1]);
            (new VillagerCareer(prof, "tool")).init(VanillaTrades.trades[3][2]);
        }
        prof = new VillagerProfession("minecraft:butcher",
                "minecraft:textures/entity/villager/butcher.png",
                "minecraft:textures/entity/zombie_villager/zombie_butcher.png");
        {
            register(prof, 4);
            (new VillagerCareer(prof, "butcher")).init(VanillaTrades.trades[4][0]);
            (new VillagerCareer(prof, "leather")).init(VanillaTrades.trades[4][1]);
        }
    }

    public static class VillagerProfession extends IForgeRegistryEntry.Impl<VillagerProfession>
    {
        private ResourceLocation name;
        private ResourceLocation texture;
        private ResourceLocation zombie;
        private List<VillagerCareer> careers = Lists.newArrayList();

        @Deprecated //Use Zombie texture
        public VillagerProfession(String name, String texture)
        {
            this (name, texture, "minecraft:textures/entity/zombie_villager/zombie_villager.png");
        }
        public VillagerProfession(String name, String texture, String zombie)
        {
            this.name = new ResourceLocation(name);
            this.texture = new ResourceLocation(texture);
            this.zombie = new ResourceLocation(zombie);
            this.setRegistryName(this.name);
        }

        private void register(VillagerCareer career)
        {
            Validate.isTrue(!careers.contains(career), "Attempted to register career that is already registered.");
            Validate.isTrue(career.profession == this, "Attempted to register career for the wrong profession.");
            career.id = careers.size();
            careers.add(career);
        }

        public ResourceLocation getSkin() { return this.texture; }
        public ResourceLocation getZombieSkin() { return this.zombie; }
        public VillagerCareer getCareer(int id)
        {
            for (VillagerCareer car : this.careers)
            {
                if (car.id == id)
                    return car;
            }
            return this.careers.get(0);
        }

        public int getRandomCareer(Random rand)
        {
            return this.careers.get(rand.nextInt(this.careers.size())).id;
        }
    }

    public static class VillagerCareer
    {
        private VillagerProfession profession;
        private String name;
        private int id;
        private List<List<ITradeList>> trades = Lists.newArrayList();

        public VillagerCareer(VillagerProfession parent, String name)
        {
            this.profession = parent;
            this.name = name;
            parent.register(this);
        }

        public String getName()
        {
            return this.name;
        }


        public VillagerCareer addTrade(int level, ITradeList... trades)
        {
            if (level <= 0)
                throw new IllegalArgumentException("Levels start at 1");

            List<ITradeList> levelTrades = level <= this.trades.size() ? this.trades.get(level - 1) : null;
            if (levelTrades == null)
            {
                while (this.trades.size() < level)
                {
                    levelTrades = Lists.newArrayList();
                    this.trades.add(levelTrades);
                }
            }
            if (levelTrades == null) //Not sure how this could happen, but screw it
            {
                levelTrades = Lists.newArrayList();
                this.trades.set(level - 1, levelTrades);
            }
            for (ITradeList t : trades)
                levelTrades.add(t);
            return this;
        }


        public List<ITradeList> getTrades(int level)
        {
            return level >= 0 && level < this.trades.size() ? Collections.unmodifiableList(this.trades.get(level)) : null;
        }
        private VillagerCareer init(EntityVillager.ITradeList[][] trades)
        {
            for (int x = 0; x < trades.length; x++)
                this.trades.add(Lists.newArrayList(trades[x]));
            return this;
        }

        @Override
        public boolean equals(Object o)
        {
            if (o == this)
            {
                return true;
            }
            if (!(o instanceof VillagerCareer))
            {
                return false;
            }
            VillagerCareer oc = (VillagerCareer)o;
            return name.equals(oc.name) && profession == oc.profession;
        }
    }

    /**
     * Hook called when spawning a Villager, sets it's profession to a random registered profession.
     *
     * @param entity The new entity
     * @param rand   The world's RNG
     */
    public static void setRandomProfession(EntityVillager entity, Random rand)
    {
        List<VillagerProfession> entries = INSTANCE.professions.getValues();
        entity.setProfession(entries.get(rand.nextInt(entries.size())));
    }
    public static void setRandomProfession(EntityZombie entity, Random rand)
    {
        List<VillagerProfession> entries = INSTANCE.professions.getValues();
        entity.setVillagerType(entries.get(rand.nextInt(entries.size())));
    }







    //Below this is INTERNAL USE ONLY DO NOT USE MODDERS
    public static void onSetProfession(EntityVillager entity, VillagerProfession prof)
    {
        int network = INSTANCE.professions.getId(prof);
        if (network == -1 || prof != INSTANCE.professions.getObjectById(network))
        {
            throw new RuntimeException("Attempted to set villager profession to unregistered profession: " + network + " " + prof);
        }

        if (network != entity.getProfession())
            entity.setProfession(network);
    }
    public static void onSetProfession(EntityVillager entity, int network)
    {
        VillagerProfession prof = INSTANCE.professions.getObjectById(network);
        if (prof == null || INSTANCE.professions.getId(prof) != network)
        {
            throw new RuntimeException("Attempted to set villager profession to unregistered profession: " + network + " " + prof);
        }

        if (prof != entity.getProfessionForge())
            entity.setProfession(prof);
    }

    @SuppressWarnings("deprecation")
    public static void onSetProfession(EntityZombie entity, VillagerProfession prof)
    {
        if (prof == null)
        {
            if (entity.getZombieType() != ZombieType.NORMAL && entity.getZombieType() != ZombieType.HUSK)
                entity.setZombieType(ZombieType.NORMAL);
            return;
        }

        int network = INSTANCE.professions.getId(prof);
        if (network == -1 || prof != INSTANCE.professions.getObjectById(network))
        {
            throw new RuntimeException("Attempted to set villager profession to unregistered profession: " + network + " " + prof);
        }

        if (network >= 0 && network < 5) // Vanilla
        {
            if (entity.getZombieType() == null || entity.getZombieType().getId() != network + 1)
            {
                entity.setZombieType(ZombieType.getVillagerByOrdinal(network));
            }
        }
        else if (entity.getZombieType() != null)
            entity.setZombieType(ZombieType.NORMAL);
    }
    public static void onSetProfession(EntityZombie entity, ZombieType type, int network)
    {
        if (type == ZombieType.NORMAL || type == ZombieType.HUSK)
        {
            if (entity.getVillagerTypeForge() != null)
                entity.setVillagerType(null);
            return;
        }
        int realID = network - 1;
        if (type == null) //Forge type?
            realID = network * -1; // Forge encoded as -ID
        VillagerProfession prof = INSTANCE.professions.getObjectById(realID);
        if (prof == null && network != 0 || INSTANCE.professions.getId(prof) != realID)
        {
            throw new RuntimeException("Attempted to set villager profession to unregistered profession: " + realID + " " + prof);
        }

        if (prof != entity.getVillagerTypeForge())
            entity.setVillagerType(prof);
    }

    @Deprecated public static VillagerProfession getById(int network){ return INSTANCE.professions.getObjectById(network); }
    @Deprecated public static int getId(VillagerProfession prof){ return INSTANCE.professions.getId(prof); }

    //TODO: Figure out a good generic system for this. Put on hold for Patches.

    private static class VanillaTrades
    {
        //This field is moved from EntityVillager over to here.
        //Moved to inner class to stop static initializer issues.
        //It is nasty I know but it's vanilla.
        private static final ITradeList[][][][] trades = EntityVillager.GET_TRADES_DONT_USE();
    }
}
