package net.minecraftforge.debug.entity;

import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraftforge.event.entity.EntityAttributeSetupEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;

@Mod("add_entity_attribute_test")
public class AddEntityAttributeTest {
    public static final boolean ENABLE = true;
    private static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(Attribute.class, "add_entity_attribute_test");
    public static final RegistryObject<Attribute> TEST_ATTR = ATTRIBUTES.register("test_attr", () -> new RangedAttribute("forge.test_attr", 1.0D, 0.0D, 1024.0D).func_233753_a_(true));

    public AddEntityAttributeTest()
    {
        if (ENABLE) {
            ATTRIBUTES.register(FMLJavaModLoadingContext.get().getModEventBus());
            FMLJavaModLoadingContext.get().getModEventBus().register(this);
        }
    }

    @SubscribeEvent
    public void entityAttributeSetup(EntityAttributeSetupEvent event)
    {
        event.getEntityAttributes().forEach((type, map) -> map.func_233814_a_(TEST_ATTR.get()));
    }
}
