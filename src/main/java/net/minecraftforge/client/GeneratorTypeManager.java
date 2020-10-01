package net.minecraftforge.client;

import net.minecraft.client.gui.screen.BiomeGeneratorTypeScreens;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.DimensionSettings;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Manages Vanilla and Modded instances of {@link BiomeGeneratorTypeScreens}.
 * Mods should register their GeneratorTypes here so that they show up as options
 * in the world creation menus.
 * <p>
 * Access to the contents of this manager is synchronized so can be safely used
 * during {@link net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent}.
 */
public class GeneratorTypeManager {

    private static final GeneratorTypeManager INSTANCE = new GeneratorTypeManager();

    private BiomeGeneratorTypeScreens defaultGeneratorType;
    private final List<BiomeGeneratorTypeScreens> generatorTypes = new ArrayList<>();
    private final Map<BiomeGeneratorTypeScreens, BiomeGeneratorTypeScreens.IFactory> editScreens = new HashMap<>();

    private GeneratorTypeManager() {
        this.defaultGeneratorType = BiomeGeneratorTypeScreens.field_239066_a_;
        VanillaGeneratorTypeAccess.forEachType(generatorTypes::add);
        VanillaGeneratorTypeAccess.forEachEditScreen(editScreens::put);
    }

    /**
     * Get the number of registered GeneratorTypes
     *
     * @return The number of registered GeneratorTypes
     */
    public synchronized int getCount() {
        return generatorTypes.size();
    }

    /**
     * Get the position of the given GeneratorType in the list of registered GeneratorTypes
     *
     * @param generatorType The GeneratorType to find the position of
     * @return The index of the given GeneratorType or -1 if it has not been registered
     */
    public synchronized int getIndex(BiomeGeneratorTypeScreens generatorType) {
        return generatorTypes.indexOf(generatorType);
    }

    /**
     * Test whether the given GeneratorType has an associated EditScreenFactory
     *
     * @param generatorType The GeneratorType to test
     * @return true if an EditScreenFactory has been registered to the GeneratorType
     */
    public synchronized boolean hasEditScreen(BiomeGeneratorTypeScreens generatorType) {
        return editScreens.containsKey(generatorType);
    }

    /**
     * Get the GeneratorType for a given position in the list of registered GeneratorTypes
     *
     * @param index The index to look up
     * @return The GeneratorType at the given position
     * @throws IndexOutOfBoundsException if the ordinal is out of range
     */
    public synchronized BiomeGeneratorTypeScreens getGeneratorTypeAt(int index) {
        return generatorTypes.get(index);
    }

    @Nonnull
    public synchronized BiomeGeneratorTypeScreens getDefaultGeneratorType() {
        return defaultGeneratorType;
    }

    /**
     * Get the EditScreenFactory registered to the given GeneratorType
     *
     * @param generatorType The GeneratorType to get an EditScreenFactory for
     * @return The EditScreenFactory or null
     */
    @Nullable
    public synchronized BiomeGeneratorTypeScreens.IFactory getEditScreenFactory(BiomeGeneratorTypeScreens generatorType) {
        return editScreens.get(generatorType);
    }

    /**
     * Register a GeneratorType with the GeneratorTypeManager
     *
     * @param generatorType The GeneratorType to be added
     * @return true if the GeneratorType was successfully added
     */
    public synchronized boolean register(BiomeGeneratorTypeScreens generatorType) {
        if (!generatorTypes.contains(generatorType)) {
            generatorTypes.add(generatorType);
            return true;
        }
        return false;
    }

    /**
     * Register a GeneratorType and associated EditScreenFactory with the GeneratorTypeManager
     *
     * @param generatorType The GeneratorType to be added
     * @param editScreen    The EditScreenFactory to be associated with given GeneratorType
     * @return true if the GeneratorType and EditScreenFactory was successfully added
     */
    public synchronized boolean register(BiomeGeneratorTypeScreens generatorType, BiomeGeneratorTypeScreens.IFactory editScreen) {
        if (register(generatorType)) {
            editScreens.put(generatorType, editScreen);
            return true;
        }
        return false;
    }

    /**
     * Set the EditScreenFactory for a given GeneratorType, overriding any previously set factory
     *
     * @param generatorType The GeneratorType to associate with the given EditScreenFactory
     * @param editScreen    The EditScreenFactory to associate with the given GeneratorType
     * @return true if the EditScreenFactory was successfully set for the GeneratorType
     */
    public synchronized boolean setEditScreen(BiomeGeneratorTypeScreens generatorType, BiomeGeneratorTypeScreens.IFactory editScreen) {
        if (generatorTypes.contains(generatorType)) {
            editScreens.put(generatorType, editScreen);
            return true;
        }
        return false;
    }

    /**
     * Set the default GeneratorType
     *
     * @param generatorType The GeneratorType to use as the default
     */
    public synchronized void setDefaultGeneratorType(@Nonnull BiomeGeneratorTypeScreens generatorType) {
        this.defaultGeneratorType = generatorType;
    }

    public static GeneratorTypeManager get() {
        return INSTANCE;
    }

    // Used to access the list/maps of GeneratorTypes in BiomeGeneratorTypeScreens so that they can be
    // copied to the manager.
    private static class VanillaGeneratorTypeAccess extends BiomeGeneratorTypeScreens {

        private VanillaGeneratorTypeAccess() {
            super("nope");
        }

        @Override
        protected ChunkGenerator func_241869_a(Registry<Biome> biomes, Registry<DimensionSettings> settings, long seed) {
            throw new UnsupportedOperationException();
        }

        private static void forEachType(Consumer<BiomeGeneratorTypeScreens> consumer) {
            BiomeGeneratorTypeScreens.field_239068_c_.forEach(consumer);
        }

        private static void forEachEditScreen(BiConsumer<BiomeGeneratorTypeScreens, BiomeGeneratorTypeScreens.IFactory> consumer) {
            BiomeGeneratorTypeScreens.field_239069_d_.forEach((typeOp, screen) -> typeOp.ifPresent(type -> consumer.accept(type, screen)));
        }
    }
}
