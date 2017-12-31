package net.minecraftforge.debug;

import net.minecraft.command.FunctionObject;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.FunctionLoadEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = FunctionLoadEventTest.MOD_ID, name = "FunctionLoadEvent test mod", version = "1.0.0", acceptableRemoteVersions = "*")
public class FunctionLoadEventTest
{
    static final String MOD_ID = "function_load_event_test";
    private static final boolean ENABLED = false;
    private static final ResourceLocation TEST_FUNCTION_NAME = new ResourceLocation(MOD_ID, "test");
    private static final String TEST_COMMAND = "say Test";

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        if (ENABLED)
        {
            MinecraftForge.EVENT_BUS.register(FunctionLoadEventTest.class);
        }
    }

    @SubscribeEvent
    public static void onFunctionLoadEvent(FunctionLoadEvent event)
    {
        FunctionObject function = new FunctionObject(new FunctionObject.Entry[]{new FunctionObject.CommandEntry(TEST_COMMAND)});
        event.addFunction(TEST_FUNCTION_NAME, function);
    }
}
