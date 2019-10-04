package net.minecraftforge.common.loot;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonSyntaxException;

import net.minecraft.client.resources.JsonReloadListener;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.conditions.ILootCondition;
import net.minecraft.world.storage.loot.conditions.LootConditionManager;
import net.minecraft.world.storage.loot.functions.ILootFunction;
import net.minecraft.world.storage.loot.functions.LootFunctionManager;

public class LootModifierManager extends JsonReloadListener {
	private static final Set<ResourceLocation> LOOT_MODIFIERS = Sets.newHashSet();
	private static final Set<ResourceLocation> READ_ONLY_LOOT_MODIFIERS = Collections.unmodifiableSet(LOOT_MODIFIERS);
	private static final Map<ResourceLocation, IGlobalLootModification.Serializer<?>> NAME_TO_SERIALIZER_MAP = Maps.newHashMap();
	private static final Map<Class<? extends IGlobalLootModification>, IGlobalLootModification.Serializer<?>> CLASS_TO_SERIALIZER_MAP = Maps.newHashMap();

	public static final Logger LOGGER = LogManager.getLogger();
	private static final Gson GSON_INSTANCE = (new GsonBuilder()).registerTypeHierarchyAdapter(LootModifier.class, new LootModifierManager.Serializer()).registerTypeHierarchyAdapter(ILootFunction.class, new LootFunctionManager.Serializer()).registerTypeHierarchyAdapter(ILootCondition.class, new LootConditionManager.Serializer()).create();
	private Map<ResourceLocation, LootModifier> registeredLootModifiers = ImmutableMap.of();
	private static LootModifierManager instance;
	public LootModifierManager() {
		super(GSON_INSTANCE, "loot_modifiers");
		instance = this;
	}
	public static LootModifierManager getInstance() {
		return instance;
	}

	public static <T extends IGlobalLootModification> void registerFunction(IGlobalLootModification.Serializer<? extends T> serializer) {
		ResourceLocation resourcelocation = serializer.getFunctionName();
		Class<T> oclass = (Class<T>)serializer.getFunctionClass();
		if (NAME_TO_SERIALIZER_MAP.containsKey(resourcelocation)) {
			throw new IllegalArgumentException("Can't re-register item function name " + resourcelocation);
		} else if (CLASS_TO_SERIALIZER_MAP.containsKey(oclass)) {
			throw new IllegalArgumentException("Can't re-register item function class " + oclass.getName());
		} else {
			NAME_TO_SERIALIZER_MAP.put(resourcelocation, serializer);
			CLASS_TO_SERIALIZER_MAP.put(oclass, serializer);
		}
		LOOT_MODIFIERS.add(resourcelocation);
	}

	public LootModifier getLootModifierFromLocation(ResourceLocation ressources) {
		return registeredLootModifiers.getOrDefault(ressources, LootModifier.EMPTY_LOOT_MOD);
	}

	public static IGlobalLootModification.Serializer<?> getSerializerForName(ResourceLocation location) {
		IGlobalLootModification.Serializer<?> serializer = NAME_TO_SERIALIZER_MAP.get(location);
		if (serializer == null) {
			throw new IllegalArgumentException("Unknown loot item modifier '" + location + "'");
		} else {
			return serializer;
		}
	}

	public static <T extends IGlobalLootModification> IGlobalLootModification.Serializer<T> getSerializerFor(T functionClass) {
		IGlobalLootModification.Serializer<T> serializer = (IGlobalLootModification.Serializer<T>)CLASS_TO_SERIALIZER_MAP.get(functionClass.getClass());
		if (serializer == null) {
			throw new IllegalArgumentException("Unknown loot item modifier " + functionClass);
		} else {
			return serializer;
		}
	}

	@Override
	protected void apply(Map<ResourceLocation, JsonObject> splashList, IResourceManager resourceManagerIn, IProfiler profilerIn) {
		Builder<ResourceLocation, LootModifier> builder = ImmutableMap.builder();
		splashList.forEach((location, object) -> {
			try {
				LootModifier lootmod = GSON_INSTANCE.fromJson(object, LootModifier.class);
				builder.put(lootmod.registryName, lootmod);
			} catch (Exception exception) {
				LOGGER.error("Couldn't parse loot modifier {}", location, exception);
			}
		});
		builder.put(LootModifier.EMPTY, LootModifier.EMPTY_LOOT_MOD);
		ImmutableMap<ResourceLocation, LootModifier> immutablemap = builder.build();
		this.registeredLootModifiers = immutablemap;
	}

	public static class Serializer implements JsonDeserializer<IGlobalLootModification>, JsonSerializer<IGlobalLootModification> {

		@Override
		public JsonElement serialize(IGlobalLootModification src, Type typeOfSrc, JsonSerializationContext context) {
			IGlobalLootModification.Serializer<IGlobalLootModification> serializer = LootModifierManager.getSerializerFor(src);
			JsonObject jsonobject = new JsonObject();
			jsonobject.addProperty("function", serializer.getFunctionName().toString());
			serializer.serialize(jsonobject, src, context);
			return jsonobject;
		}

		@Override
		public IGlobalLootModification deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			JsonObject jsonobject = JSONUtils.getJsonObject(json, "modifier");
			//ILootCondition[] ailootcondition = JSONUtils.deserializeClass(jsonobject, "conditions", new ILootCondition[0], context, ILootCondition[].class);
			ResourceLocation resourcelocation = new ResourceLocation(JSONUtils.getString(jsonobject, "function"));

			IGlobalLootModification.Serializer<?> serializer;
			try {
				serializer = LootModifierManager.getSerializerForName(resourcelocation);
			} catch (IllegalArgumentException var8) {
				throw new JsonSyntaxException("Unknown modifier '" + resourcelocation + "'");
			}

			return serializer.deserialize(jsonobject, context);
		}
	}

	public Set<ResourceLocation> getAllLootMods() {
		return READ_ONLY_LOOT_MODIFIERS;
	}
}
