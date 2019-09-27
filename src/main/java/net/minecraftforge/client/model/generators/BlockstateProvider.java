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

package net.minecraftforge.client.model.generators;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.gson.*;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import net.minecraft.state.IProperty;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static net.minecraftforge.client.model.generators.VariantBlockstate.*;

public abstract class BlockstateProvider implements IDataProvider {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();

    private final DataGenerator gen;

    public BlockstateProvider(DataGenerator gen) {
        this.gen = gen;
    }

    private Set<ResourceLocation> generatedStates;
    private DirectoryCache cache;

    @Override
    public void act(@Nonnull DirectoryCache cache) throws IOException {
        generatedStates = new HashSet<>();
        this.cache = cache;
        registerStates(this::createVariantModel, this::createMultipartModel);
    }

    private void createVariantModel(VariantBlockstate out) {
        Block block = out.getOwner();
        ResourceLocation blockName = Preconditions.checkNotNull(block.getRegistryName());
        Preconditions.checkArgument(generatedStates.add(blockName));
        JsonObject variants = new JsonObject();
        for (Map.Entry<PartialBlockstate, ConfiguredModelList> entry : out.getModels().entrySet()) {
                variants.add(entry.getKey().toString(), entry.getValue().toJSON());
        }
        JsonObject main = new JsonObject();
        main.add("variants", variants);
        BlockstateProvider.this.saveBlockState(main, block);
    }

    private void createMultipartModel(Block block, List<MultiPart> parts) {
        JsonArray variants = new JsonArray();
        for (MultiPart part : parts) {
            Preconditions.checkArgument(part.canApplyTo(block));
            variants.add(part.toJson());
        }
        JsonObject main = new JsonObject();
        main.add("multipart", variants);
        saveBlockState(main, block);
    }

    protected abstract void registerStates(Consumer<VariantBlockstate> variantBased, BiConsumer<Block, List<MultiPart>> multipartBased);

    private void saveBlockState(JsonObject stateJson, Block owner) {
        ResourceLocation blockName = Preconditions.checkNotNull(owner.getRegistryName());
        Path mainOutput = gen.getOutputFolder();
        String pathSuffix = "assets/" + blockName.getNamespace() + "/blockstates/" + blockName.getPath() + ".json";
        Path outputPath = mainOutput.resolve(pathSuffix);
        try {
            IDataProvider.save(GSON, cache, stateJson, outputPath);
        } catch (IOException e) {
            LOGGER.error("Couldn't save blockstate to {}", outputPath, e);
        }
    }

    @Nonnull
    @Override
    public String getName() {
        return "Block States";
    }

    public static final class ConfiguredModel {
        public final ModelFile name;
        public final int rotationX;
        public final int rotationY;
        public final boolean uvLock;
        public final int weight;

        public ConfiguredModel(ModelFile name, int rotationX, int rotationY, boolean uvLock, int weight) {
            this.name = name;
            this.rotationX = rotationX;
            this.rotationY = rotationY;
            this.uvLock = uvLock;
            this.weight = weight;
        }

        public ConfiguredModel(ModelFile name, int rotationX, int rotationY, boolean uvLock) {
            this(name, rotationX, rotationY, uvLock, 0);
        }

        public ConfiguredModel(ModelFile name) {
            this(name, 0, 0, false, 0);
        }

        public JsonObject toJSON(boolean includeWeight) {
            JsonObject modelJson = new JsonObject();
            modelJson.addProperty("model", name.getLocation().toString());
            if (rotationX != 0)
                modelJson.addProperty("x", rotationX);
            if (rotationY != 0)
                modelJson.addProperty("y", rotationY);
            if (uvLock && (rotationX != 0 || rotationY != 0))
                modelJson.addProperty("uvlock", uvLock);
            if (includeWeight)
                modelJson.addProperty("weight", weight);
            return modelJson;
        }
    }

    public static class ConfiguredModelList {
        private final List<ConfiguredModel> models;

        public ConfiguredModelList(List<ConfiguredModel> models) {
            Preconditions.checkArgument(!models.isEmpty());
            this.models = models;
        }

        public ConfiguredModelList(ConfiguredModel model) {
            this(ImmutableList.of(model));
        }

        public ConfiguredModelList(ConfiguredModel... models) {
            this(Arrays.asList(models));
        }

        public JsonElement toJSON() {
            if (models.size()==1) {
                return models.get(0).toJSON(false);
            } else {
                JsonArray ret = new JsonArray();
                for (ConfiguredModel m:models) {
                    ret.add(m.toJSON(true));
                }
                return ret;
            }
        }
    }

    public static final class PropertyWithValues<T extends Comparable<T>> {
        public final IProperty<T> prop;
        public final List<T> values;

        public PropertyWithValues(IProperty<T> prop, T... values) {
            this.prop = prop;
            this.values = Arrays.asList(values);
        }
    }

    public static final class MultiPart {
        public final ConfiguredModelList models;
        public final boolean useOr;
        public final List<PropertyWithValues> conditions;

        public MultiPart(ConfiguredModelList models, boolean useOr, PropertyWithValues... conditionsArray) {
            conditions = Arrays.asList(conditionsArray);
            Preconditions.checkArgument(conditions.size() == conditions.stream()
                    .map(pwv -> pwv.prop)
                    .distinct()
                    .count());
            Preconditions.checkArgument(conditions.stream().noneMatch(pwv -> pwv.values.isEmpty()));
            this.models = models;
            this.useOr = useOr;
        }

        public JsonObject toJson() {
            JsonObject out = new JsonObject();
            if (!conditions.isEmpty()) {
                JsonObject when = new JsonObject();
                for (PropertyWithValues<?> prop : conditions) {
                    StringBuilder activeString = new StringBuilder();
                    for (Object val : prop.values) {
                        if (activeString.length() > 0)
                            activeString.append("|");
                        activeString.append(val.toString());
                    }
                    when.addProperty(prop.prop.getName(), activeString.toString());
                }
                if (useOr) {
                    JsonObject innerWhen = when;
                    when = new JsonObject();
                    when.add("OR", innerWhen);
                }
                out.add("when", when);
            }
            out.add("apply", models.toJSON());
            return out;
        }

        public boolean canApplyTo(Block b) {
            for (PropertyWithValues<?> p : conditions)
                if (!b.getStateContainer().getProperties().contains(p.prop))
                    return false;
            return true;
        }
    }
}
