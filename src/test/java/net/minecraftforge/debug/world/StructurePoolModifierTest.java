package net.minecraftforge.debug.world;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.datafixers.util.Pair;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.levelgen.feature.structures.LegacySinglePoolElement;
import net.minecraft.world.level.levelgen.feature.structures.StructurePoolElement;
import net.minecraft.world.level.levelgen.feature.structures.StructureTemplatePool;
import net.minecraftforge.common.data.StructurePoolModifierProvider;
import net.minecraftforge.common.world.IStructurePoolModifier;
import net.minecraftforge.common.world.StructurePoolModifierSerializer;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Mod(StructurePoolModifierTest.MODID)
public class StructurePoolModifierTest {

    public static final String MODID = "structure_pool_modifier_test";

    private static final DeferredRegister<StructurePoolModifierSerializer<?>> SPM = DeferredRegister.create(ForgeRegistries.POOL_MODIFIER_SERIALIZERS, MODID);

    private static final RegistryObject<PoolAdder.Serializer> POOL_ADDER = SPM.register("pool_adder", PoolAdder.Serializer::new);

    public StructurePoolModifierTest() {
        SPM.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class EventHandlers {
        @SubscribeEvent
        public static void runData(GatherDataEvent event) {
            event.getGenerator().addProvider(new StructurePoolModifierTest.DataProvider(event.getGenerator(), MODID));
        }
    }

    private static class DataProvider extends StructurePoolModifierProvider {

        public DataProvider(DataGenerator gen, String modid) {
            super(gen, modid);
        }

        @Override
        protected void start() {
            this.add("add_test_to_pillage", POOL_ADDER.get(), new PoolAdder(ImmutableMap.of(
                    new ResourceLocation("pillager_outpost/towers"),
                    ImmutableList.of(Pair.of(
                            new ResourceLocation("structure_pool_modifier_test", "test"),
                            10
                    ))
            )));
        }

    }

    private static class PoolAdder implements IStructurePoolModifier {

        private final Map<ResourceLocation, List<Pair<ResourceLocation, Integer>>> additionEntries;

        public PoolAdder(Map<ResourceLocation, List<Pair<ResourceLocation, Integer>>> additionEntries) {
            this.additionEntries = additionEntries;
        }

        @Override
        public void apply(ResourceLocation name, List<StructurePoolElement> elements) {
            List<Pair<ResourceLocation, Integer>> entriesForName = this.additionEntries.get(name);
            if (entriesForName != null) {
                for (Pair<ResourceLocation, Integer> templatePair : entriesForName) {
                    Function<StructureTemplatePool.Projection, LegacySinglePoolElement> elementFunction = StructurePoolElement.legacy(templatePair.getFirst().toString());
                    StructurePoolElement element = elementFunction.apply(StructureTemplatePool.Projection.RIGID);
                    for (int i = 0; i < templatePair.getSecond(); i++) {
                        elements.add(element);
                    }
                }
            }
        }

        public static class Serializer extends StructurePoolModifierSerializer<PoolAdder> {

            @Override
            public PoolAdder read(ResourceLocation location, JsonObject object) {
                ImmutableMap.Builder<ResourceLocation, List<Pair<ResourceLocation, Integer>>> builder = ImmutableMap.builder();
                JsonObject additionsMap = object.getAsJsonObject("additions");
                for (Map.Entry<String, JsonElement> entry : additionsMap.entrySet()) {
                    ImmutableList.Builder<Pair<ResourceLocation, Integer>> listBuilder = ImmutableList.builder();
                    ResourceLocation poolLocation = new ResourceLocation(entry.getKey());
                    JsonArray poolAdditions = entry.getValue().getAsJsonArray();
                    for (JsonElement addition : poolAdditions) {
                        JsonObject additionObj = addition.getAsJsonObject();
                        ResourceLocation templateLocation = new ResourceLocation(additionObj.get("template").getAsString());
                        int weight = additionObj.get("weight").getAsInt();
                        listBuilder.add(Pair.of(templateLocation, weight));
                    }
                    builder.put(poolLocation, listBuilder.build());
                }
                Map<ResourceLocation, List<Pair<ResourceLocation, Integer>>> additionEntries = builder.build();
                return new PoolAdder(additionEntries);
            }

            @Override
            public JsonObject write(PoolAdder instance) {
                JsonObject additions = new JsonObject();
                for(Map.Entry<ResourceLocation, List<Pair<ResourceLocation, Integer>>> entry : instance.additionEntries.entrySet()) {
                    ResourceLocation addTo = entry.getKey();
                    List<Pair<ResourceLocation, Integer>> templates = entry.getValue();
                    JsonArray list = new JsonArray();
                    for(Pair<ResourceLocation,Integer> template:templates) {
                        JsonObject obj = new JsonObject();
                        obj.add("template", new JsonPrimitive(template.getFirst().toString()));
                        obj.add("weight", new JsonPrimitive(template.getSecond()));
                        list.add(obj);
                    }
                    additions.add(addTo.toString(), list);
                }
                JsonObject result = new JsonObject();
                result.add("type", new JsonPrimitive(this.getRegistryName().toString()));
                result.add("additions", additions);
                return result;
            }

        }
    }
}
