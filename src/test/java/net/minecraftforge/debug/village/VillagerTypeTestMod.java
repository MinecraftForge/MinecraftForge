package net.minecraftforge.debug.village;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.npc.VillagerType;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.test.BaseTestMod;

@Mod(VillagerTypeTestMod.MOD_ID)
public class VillagerTypeTestMod extends BaseTestMod {

    public static final String MOD_ID = "villager_type_test_mod";

    private static final DeferredRegister<VillagerType> VILLAGER_TYPES = DeferredRegister.create(Registries.VILLAGER_TYPE, MOD_ID);

    private static final RegistryObject<VillagerType> TEST_VILLAGER_TYPE = VILLAGER_TYPES.register("test_villager_type", () -> new VillagerType("test_villager_type"));

    public VillagerTypeTestMod(FMLJavaModLoadingContext context) {
        super(context, false);
        VILLAGER_TYPES.register(context.getModEventBus());
    }
}
