/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
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

import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.init.Bootstrap;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionType;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.common.registry.VillagerRegistry.VillagerProfession;
import net.minecraftforge.registries.DataSerializerEntry;
import net.minecraftforge.registries.GameData;
import net.minecraftforge.registries.IForgeRegistry;

/**
 * A class that exposes static references to all vanilla and Forge registries.
 * Created to have a central place to access the registries directly if modders need.
 * It is still advised that if you are registering things to go through {@link GameRegistry} register methods, but queries and iterations can use this.
 */
public class ForgeRegistries
{
    static { init(); } // This must be above the fields so we guarantee it's run before findRegistry is called. Yay static inializers

    public static final IForgeRegistry<Block>        BLOCKS       = GameRegistry.findRegistry(Block.class);
    public static final IForgeRegistry<Item>         ITEMS        = GameRegistry.findRegistry(Item.class);
    public static final IForgeRegistry<Potion>       POTIONS      = GameRegistry.findRegistry(Potion.class);
    public static final IForgeRegistry<Biome>        BIOMES       = GameRegistry.findRegistry(Biome.class);
    public static final IForgeRegistry<SoundEvent>   SOUND_EVENTS = GameRegistry.findRegistry(SoundEvent.class);
    public static final IForgeRegistry<PotionType>   POTION_TYPES = GameRegistry.findRegistry(PotionType.class);
    public static final IForgeRegistry<Enchantment>  ENCHANTMENTS = GameRegistry.findRegistry(Enchantment.class);
    public static final IForgeRegistry<VillagerProfession> VILLAGER_PROFESSIONS = GameRegistry.findRegistry(VillagerProfession.class);
    public static final IForgeRegistry<EntityEntry>  ENTITIES     = GameRegistry.findRegistry(EntityEntry.class);
    public static final IForgeRegistry<IRecipe>      RECIPES      = GameRegistry.findRegistry(IRecipe.class);
    public static final IForgeRegistry<DataSerializerEntry> DATA_SERIALIZERS = GameRegistry.findRegistry(DataSerializerEntry.class);


    /**
     * This function is just to make sure static inializers in other classes have run and setup their registries before we query them.
     */
    private static void init()
    {
        GameData.init();
        VillagerRegistry.instance();
        Bootstrap.register();
    }

}
