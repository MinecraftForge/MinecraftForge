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

package net.minecraftforge.common.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import cpw.mods.modlauncher.api.LamdbaExceptionUtils;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import net.minecraft.loot.ConditionArraySerializer;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Provider for forge's GlobalLootModifier system. See {@link LootModifier} and {@link GlobalLootModifierSerializer}.
 *
 * This provider only requires implementing {@link #start()} and calling {@link #addModifier} from it.
 */
public abstract class GlobalLootModifierProvider implements IDataProvider
{
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

    /**
     * Sets the "replace" key in global_loot_modifiers to true.
     */
    protected void replacing()
    {
        this.replace = true;
    }

    /**
     * Call {@link #addModifier} here, which will pass in the necessary information to write the jsons.
     */
    protected abstract void start();

    @Override
    public void act(DirectoryCache cache) throws IOException
    {
        start();

        Path forgePath = gen.getOutputFolder().resolve("data/forge/loot_modifiers/global_loot_modifiers.json");
        String modPath = "data/" + modid + "/loot_modifiers/";
        List<ResourceLocation> entries = new ArrayList<>();

        modifiers.forEach(LamdbaExceptionUtils.rethrowBiConsumer((name, pair) ->
        {
            entries.add(new ResourceLocation(modid, name));
            Path modifierPath = gen.getOutputFolder().resolve(modPath + name + ".json");

            JsonObject json = new JsonObject();
            json.addProperty("type", pair.getA().getRegistryName().toString());
            json.add("conditions", ConditionArraySerializer.field_235679_a_.func_235681_a_(pair.getB()));

            extraProperties.get(name).accept(json);

            IDataProvider.save(GSON, cache, json, modifierPath);
        }));

        JsonObject forgeJson = new JsonObject();
        forgeJson.addProperty("replace", this.replace);
        forgeJson.add("entries", GSON.toJsonTree(entries.stream().map(ResourceLocation::toString).collect(Collectors.toList())));

        IDataProvider.save(GSON, cache, forgeJson, forgePath);
    }

    /**
     * Passes in the data needed to create the file without any extra objects.
     *
     * @param modifier      The name of the modifier, which will be the file name.
     * @param serializer    The serializer of this modifier.
     * @param conditions    The loot conditions before {@link LootModifier#doApply} is called.
     */
    public void addModifier(String modifier, GlobalLootModifierSerializer<?> serializer, ILootCondition... conditions)
    {
        addModifier(modifier, serializer, j -> {}, conditions);
    }

    /**
     * Passes in the data needed to create the file with any extra objects necessary.
     * They are then available through {@link GlobalLootModifierSerializer#read}
     *
     * @param modifier          The name of the modifier, which will be the file name.
     * @param serializer        The serializer of this modifier.
     * @param extraProperties   The consumer that will be used to write any extra objects and their value.
     * @param conditions        The loot conditions before {@link LootModifier#doApply} is called.
     */
    public void addModifier(String modifier, GlobalLootModifierSerializer<?> serializer, Consumer<JsonObject> extraProperties, ILootCondition... conditions)
    {
        this.modifiers.put(modifier, new Tuple<>(serializer, conditions));
        this.extraProperties.put(modifier, extraProperties);
    }

    @Override
    public String getName()
    {
        return "Global Loot Modifiers : " + modid;
    }
}
