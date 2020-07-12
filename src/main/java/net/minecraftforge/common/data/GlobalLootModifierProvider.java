package net.minecraftforge.common.data;

import com.google.common.collect.Lists;
import com.google.gson.*;
import cpw.mods.modlauncher.api.LamdbaExceptionUtils;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import net.minecraft.loot.ConditionArraySerializer;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.loot.conditions.LootConditionManager;
import net.minecraft.loot.functions.ILootFunction;
import net.minecraft.loot.functions.LootFunctionManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public abstract class GlobalLootModifierProvider implements IDataProvider
{
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
   private final DataGenerator gen;
   private final String modid;
   private final Map<String, Tuple<GlobalLootModifierSerializer<?>, ILootCondition[]>> modifiers = new HashMap<>();
   private final Map<String, Consumer<JsonObject>> extraProperties = new HashMap<>();
   private boolean replace = false;

   public GlobalLootModifierProvider(DataGenerator gen, String modid)
   {
      this.gen = gen;
      this.modid = modid;
   }

   protected void replacing()
   {
      this.replace = true;
   }

   /**
    * Call {@link #addModifier} here
    */
   protected abstract void start();

   @Override
   public void act(DirectoryCache cache) throws IOException
   {
      start();

      Path forge = gen.getOutputFolder().resolve("data/forge/loot_modifiers/global_loot_modifiers.json");
      String modpath = "data/" + modid + "/loot_modifiers/";
      List<ResourceLocation> entries = new ArrayList<>();

      modifiers.forEach(LamdbaExceptionUtils.rethrowBiConsumer((name, pair) ->
      {
         entries.add(new ResourceLocation(modid, name));
         Path modifierPath = gen.getOutputFolder().resolve(modpath + name + ".json");

         JsonObject json = new JsonObject();
         json.addProperty("type", pair.getA().getRegistryName().toString());
         json.add("conditions", ConditionArraySerializer.field_235679_a_.func_235681_a_(pair.getB()));

         Consumer<JsonObject> properties = extraProperties.get(name);
         if(properties != null)
            properties.accept(json);

         IDataProvider.save(GSON, cache, json, modifierPath);
      }));

      JsonObject forgeJson = new JsonObject();
      forgeJson.addProperty("replace", this.replace);
      forgeJson.add("entries", GSON.toJsonTree(entries.stream().map(ResourceLocation::toString).collect(Collectors.toList())));

      IDataProvider.save(GSON, cache, forgeJson, forge);
   }

   public void addModifier(String modifier, GlobalLootModifierSerializer<?> serializer, ILootCondition... conditions)
   {
      addModifier(modifier, serializer, null, conditions);
   }

   public void addModifier(String modifier, GlobalLootModifierSerializer<?> serializer, @Nullable Consumer<JsonObject> extraProperties, ILootCondition... conditions)
   {
      this.modifiers.put(modifier, new Tuple<>(serializer, conditions));
      this.extraProperties.put(modifier, extraProperties);
   }

   @Override
   public String getName()
   {
      return "Global Loot Modifiers" + modid;
   }
}
