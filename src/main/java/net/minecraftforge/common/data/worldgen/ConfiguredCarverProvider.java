package net.minecraftforge.common.data.worldgen;

import cpw.mods.modlauncher.api.LamdbaExceptionUtils;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.carver.ConfiguredCarver;
import net.minecraft.world.gen.carver.ICarverConfig;
import net.minecraft.world.gen.carver.WorldCarver;
import net.minecraft.world.gen.feature.ProbabilityConfig;
import net.minecraftforge.client.model.generators.ExistingFileHelper;
import net.minecraftforge.common.data.CodecBackedProvider;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static net.minecraft.world.gen.carver.WorldCarver.CAVE;

/**
 * Since the possibilities are straightforward, this DataProvider doesn't need to be implemented and can be instantiated directly
 * {@code
 *      new ConfiguredCarverProvider(gen, "my_mod").put(name1, new Builder<>(ConfigType).setCarver(carver).setConfig(config)).put(...);
 * }
 */
public class ConfiguredCarverProvider extends CodecBackedProvider<ConfiguredCarver<?>> {
    private final DataGenerator generator;
    private final String modid;
    protected final Map<ResourceLocation, Builder<?>> builders = new HashMap<>();

    public ConfiguredCarverProvider(DataGenerator generator, ExistingFileHelper helper, String modid) {
        super(ConfiguredCarver.field_236235_a_, helper); //TODO This codec is dispatched for the vanilla CARVER registry, and won't affect any mod added carvers.
        this.generator = generator;
        this.modid = modid;
    }

    public ConfiguredCarverProvider put(ResourceLocation location, Builder<?> builder) {
        builders.put(location, builder);
        return this;
    }

    @Override
    public void act(DirectoryCache cache)  {
        Path path = generator.getOutputFolder();

        builders.forEach(LamdbaExceptionUtils.rethrowBiConsumer((name, builder) ->
                this.save(builder.build(), cache, path.resolve("data/" + name.getNamespace() + "/worldgen/configured_carver/" + name.getPath() + ".json"))
        ));

        this.fileHelper.reloadResources();
    }

    @Override
    public String getName() {
        return "Configured Carvers: " + modid;
    }

    /**
     * Only ProbabilityConfigs are used in vanilla, so this helper is here for that.
     */
    public static Builder<ProbabilityConfig> getDefault() {
        return new Builder<ProbabilityConfig>().setCarver(CAVE).setConfig(new ProbabilityConfig(0.14285715F));
    }

    public static class Builder<C extends ICarverConfig> {
        private C config;
        private WorldCarver<C> carver;

        public ConfiguredCarver<?> build() {
            Objects.requireNonNull(config);
            Objects.requireNonNull(carver);
            return carver.func_242761_a(config);
        }

        public Builder<C> setCarver(WorldCarver<C> carver) { //has to be registered.
            this.carver = carver;
            return this;
        }

        public Builder<C> setConfig(C config) {
            this.config = config;
            return this;
        }
    }
}
