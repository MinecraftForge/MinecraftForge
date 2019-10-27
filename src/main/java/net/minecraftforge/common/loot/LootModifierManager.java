package net.minecraftforge.common.loot;

import java.util.Collection;

//import static net.minecraft.client.resources.JsonReloadListener.LOGGER;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import net.minecraft.client.resources.JsonReloadListener;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.conditions.ILootCondition;
import net.minecraft.world.storage.loot.conditions.LootConditionManager;
import net.minecraft.world.storage.loot.functions.ILootFunction;
import net.minecraft.world.storage.loot.functions.LootFunctionManager;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.thread.EffectiveSide;
import net.minecraftforge.registries.ForgeRegistries;

public class LootModifierManager extends JsonReloadListener {
	private static LootModifierManager instance;
	public static final Logger LOGGER = LogManager.getLogger();
	private static final Gson GSON_INSTANCE = (new GsonBuilder()).registerTypeHierarchyAdapter(ILootFunction.class, new LootFunctionManager.Serializer()).registerTypeHierarchyAdapter(ILootCondition.class, new LootConditionManager.Serializer()).create();

	private Map<ResourceLocation, IGlobalLootModifier> registeredLootModifiers = ImmutableMap.of();

	public LootModifierManager() {
		super(GSON_INSTANCE, "loot_modifiers");
		instance = this;
	}

	public static LootModifierManager getInstance() {
		return instance;
	}

	@Override
	protected void apply(Map<ResourceLocation, JsonObject> splashList, IResourceManager resourceManagerIn, IProfiler profilerIn) {
		Builder<ResourceLocation, IGlobalLootModifier> builder = ImmutableMap.builder();
		splashList.forEach((location, object) -> {
			try {
				IGlobalLootModifier modifier = deserializeModifier(location, object);
				builder.put(location, modifier);
			} catch (Exception exception) {
				LOGGER.error("Couldn't parse loot modifier {}", location, exception);
			}
		});
		ImmutableMap<ResourceLocation, IGlobalLootModifier> immutablemap = builder.build();
		this.registeredLootModifiers = immutablemap;
	}

	private IGlobalLootModifier deserializeModifier(ResourceLocation location, JsonObject object) {
		ILootCondition[] ailootcondition = GSON_INSTANCE.fromJson(object.get("conditions"), ILootCondition[].class);
		return ForgeRegistries.LOOT_MODIFIER_SERIALIZERS.getValue(location).read(location, object, ailootcondition);
	}

	public static IGlobalLootModifierSerializer<?> getSerializerForName(ResourceLocation resourcelocation) {
		return ForgeRegistries.LOOT_MODIFIER_SERIALIZERS.getValue(resourcelocation);
	}

	public Collection<IGlobalLootModifier> getAllLootMods() {
		return registeredLootModifiers.values();
	}

}
