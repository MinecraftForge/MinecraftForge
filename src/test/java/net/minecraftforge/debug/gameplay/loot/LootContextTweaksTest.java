/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
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

package net.minecraftforge.debug.gameplay.loot;

import java.util.Random;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;

import net.minecraft.init.Biomes;
import net.minecraft.init.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootEntryItem;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraft.world.storage.loot.RandomValueRange;
import net.minecraft.world.storage.loot.conditions.EntityHasProperty;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.conditions.LootConditionManager;
import net.minecraft.world.storage.loot.functions.LootFunction;
import net.minecraft.world.storage.loot.functions.SetCount;
import net.minecraft.world.storage.loot.properties.EntityOnFire;
import net.minecraft.world.storage.loot.properties.EntityProperty;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

@EventBusSubscriber
@Mod(modid = LootContextTweaksTest.MODID, name = "LootContextTweaksTest", version = "1.0", acceptableRemoteVersions = "*")
public class LootContextTweaksTest 
{
    public static final String MODID = "loot_context_tweaks_test";
    public static final boolean ENABLED = false;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        if (!ENABLED) return; 
        LootConditionManager.registerCondition(new InBiome.Serialiser());
    }

    @SubscribeEvent
    public static void onLootTableLoad(LootTableLoadEvent event)
    {
        if (!ENABLED) return;
        if (event.getName().equals(LootTableList.GAMEPLAY_FISHING))
        {
            LootPool main = event.getTable().getPool("main");
            main.addEntry(new LootEntryItem(Items.ACACIA_BOAT, 100, 1, new LootFunction[0], new LootCondition[] {new InBiome(Biomes.SAVANNA)}, "fishing_test"));
        }
        else if (event.getName().equals(LootTableList.CHESTS_SIMPLE_DUNGEON))
        {
            LootPool main = event.getTable().getPool("main");
            LootCondition onFire = new EntityHasProperty(new EntityProperty[] {new EntityOnFire(true)}, LootContext.EntityTarget.KILLER_PLAYER);
            main.addEntry(new LootEntryItem(Items.BLAZE_POWDER, 100, 1, new LootFunction[] {new SetCount(new LootCondition[0], new RandomValueRange(64))}, new LootCondition[] {onFire}, "minecart_test"));
        }
    }

    private static class InBiome implements LootCondition
    {
        private final Biome requiredBiome;

        public InBiome(Biome requiredBiome) 
        {
            this.requiredBiome = requiredBiome;
        }

        @Override
        public boolean testCondition(Random rand, LootContext context) 
        {
            if (context.getLootedEntity() == null) return false;
            Biome biome = context.getWorld().getBiome(context.getLootedEntity().getPosition());
            return biome == requiredBiome;
        }

        private static class Serialiser extends LootCondition.Serializer<InBiome>
        {
            protected Serialiser() 
            {
                super(new ResourceLocation(MODID, "in_biome"), InBiome.class);
            }

            @Override
            public void serialize(JsonObject json, InBiome value, JsonSerializationContext context) 
            {
                json.addProperty("biome", value.requiredBiome.getRegistryName().toString());
            }

            @Override
            public InBiome deserialize(JsonObject json, JsonDeserializationContext context) 
            {
                if (!json.has("biome")) throw new JsonSyntaxException("Missing biome tag, expected to find a biome registry name");
                ResourceLocation biomeResLoc = new ResourceLocation(json.get("biome").getAsString());
                Biome biome = ForgeRegistries.BIOMES.getValue(biomeResLoc);
                if (biome == null) throw new JsonSyntaxException("Invalid biome tag. " + biomeResLoc + " does not exist in the biome registry.");
                return new InBiome(biome);
            }
        }
    }
}
