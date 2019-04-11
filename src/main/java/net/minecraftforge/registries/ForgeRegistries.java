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

package net.minecraftforge.registries;

import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityType;
import net.minecraft.init.Bootstrap;
import net.minecraft.item.Item;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionType;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.ModDimension;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.VillagerRegistry;
import net.minecraftforge.fml.common.registry.VillagerRegistry.VillagerProfession;

/**
 * A class that exposes static references to all vanilla and Forge registries.
 * Created to have a central place to access the registries directly if modders need.
 * It is still advised that if you are registering things to go through {@link GameRegistry} register methods, but queries and iterations can use this.
 */
public class ForgeRegistries
{
    static { init(); } // This must be above the fields so we guarantee it's run before findRegistry is called. Yay static inializers

    public static final IForgeRegistry<Block>               BLOCKS       = RegistryManager.ACTIVE.getRegistry(Block.class);
    public static final IForgeRegistry<Item>                ITEMS        = RegistryManager.ACTIVE.getRegistry(Item.class);
    public static final IForgeRegistry<Potion>              POTIONS      = RegistryManager.ACTIVE.getRegistry(Potion.class);
    public static final IForgeRegistry<Biome>               BIOMES       = RegistryManager.ACTIVE.getRegistry(Biome.class);
    public static final IForgeRegistry<SoundEvent>          SOUND_EVENTS = RegistryManager.ACTIVE.getRegistry(SoundEvent.class);
    public static final IForgeRegistry<PotionType>          POTION_TYPES = RegistryManager.ACTIVE.getRegistry(PotionType.class);
    public static final IForgeRegistry<Enchantment>         ENCHANTMENTS = RegistryManager.ACTIVE.getRegistry(Enchantment.class);
    public static final IForgeRegistry<VillagerProfession>  VILLAGER_PROFESSIONS = RegistryManager.ACTIVE.getRegistry(VillagerProfession.class);
    public static final IForgeRegistry<EntityType<?>>       ENTITIES       = RegistryManager.ACTIVE.getRegistry(EntityType.class); //Untyped casys needed to fix javac issues.
    public static final IForgeRegistry<TileEntityType<?>>   TILE_ENTITIES  = RegistryManager.ACTIVE.getRegistry(TileEntityType.class);
    public static final IForgeRegistry<ModDimension>        MOD_DIMENSIONS = RegistryManager.ACTIVE.getRegistry(ModDimension.class);
    public static final IForgeRegistry<DataSerializerEntry> DATA_SERIALIZERS = RegistryManager.ACTIVE.getRegistry(DataSerializerEntry.class);

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
