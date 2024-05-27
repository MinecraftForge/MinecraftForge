package net.minecraftforge.debug.loading;


import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.test.BaseTestMod;

@Mod(ModConstructorTest.MODID)
public class ModConstructorTest extends BaseTestMod {
    static final String MODID = "modconstructortest_with_bus";

    public ModConstructorTest(IEventBus bus) {
        bus.addListener(this::onEvent);
    }

    public void onEvent(FMLCommonSetupEvent event) {
    }
}
