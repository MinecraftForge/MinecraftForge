package net.minecraftforge.debug.gameplay.villager;

import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.npc.VillagerType;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.test.BaseTestMod;

@GameTestHolder("forge." + VillagerTypeTestMod.MOD_ID)
@Mod(VillagerTypeTestMod.MOD_ID)
public class VillagerTypeTestMod extends BaseTestMod {
    public static final String MOD_ID = "villager_type_test";

    private static final DeferredRegister<VillagerType> VILLAGER_TYPES = DeferredRegister.create(Registries.VILLAGER_TYPE, MOD_ID);

    private static final RegistryObject<VillagerType> TEST_VILLAGER_TYPE = VILLAGER_TYPES.register("test_villager_type", () -> new VillagerType("test_villager_type"));

    public VillagerTypeTestMod(FMLJavaModLoadingContext context) {
        super(context);
        context.getModEventBus().addListener(this::onCommonSetup);
    }

    public void onCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> VillagerType.registerBiomeType(Biomes.PLAINS, TEST_VILLAGER_TYPE.get()));
    }

    @GameTest(template = "forge:empty3x3x3")
    public static void biome_type(GameTestHelper helper) {
        RegistryAccess access = helper.getLevel().registryAccess();
        VillagerType type = access.lookupOrThrow(Registries.VILLAGER_TYPE).getValue(TEST_VILLAGER_TYPE.getId());
        if (type == null)
            helper.fail("Failed to find test_villager_type");
        helper.assertValueEqual(type, TEST_VILLAGER_TYPE.get(), "Loaded entry does not contain expected value");

        Holder<Biome> biome = access.lookupOrThrow(Registries.BIOME).getOrThrow(Biomes.PLAINS);
        helper.assertValueEqual(type, VillagerType.byBiome(biome), "VillagerType.byBiome did not return the expected value");

        helper.succeed();
    }
}
