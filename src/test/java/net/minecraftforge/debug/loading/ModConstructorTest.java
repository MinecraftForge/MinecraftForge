package net.minecraftforge.debug.loading;


import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLModContainer;
import net.minecraftforge.test.BaseTestMod;

@Mod(ModConstructorTest.MODID)
public class ModConstructorTest extends BaseTestMod {
    static final String MODID = "modconstructortest_with_bus";

    public ModConstructorTest(FMLModContainer container) {
        var bus = container.getEventBus();
        bus.addListener(this::onEvent);
    }

    public void onEvent(FMLCommonSetupEvent event) {
    }
}
