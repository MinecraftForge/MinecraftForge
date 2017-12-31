package net.minecraftforge.debug;

import org.apache.logging.log4j.Logger;

import com.google.gson.JsonParseException;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementManager;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AdvancementLoadEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = AdvancementLoadEventTest.MOD_ID, name = "AdvancementLoadEvent test mod", version = "1.0.0", acceptableRemoteVersions = "*")
public class AdvancementLoadEventTest
{
    static final String MOD_ID = "advancement_load_event_test";
    private static final boolean ENABLED = false;
    private static final ResourceLocation TEST_ADVANCEMENT_NAME = new ResourceLocation(MOD_ID, "test/root");
//    private static final ResourceLocation TEST_ADVANCEMENT_NAME = new ResourceLocation("story/root"); //Uncomment and comment line above to test overriding a built-in advancement
    private static final String TEST_ADVANCEMENT_JSON = 
              "{\n"
            + "  \"display\": {\n"
            + "    \"icon\": {\n"
            + "      \"item\": \"minecraft:stone\"\n"
            + "    },\n"
            + "    \"title\": \"Test Advancement\",\n"
            + "    \"description\":  \"Test Advancement Description\",\n"
            + "    \"background\": \"minecraft:textures/gui/advancements/backgrounds/stone.png\"\n"
            + "  },\n"
            + "  \"criteria\": {\n"
            + "    \"stone\": {\n"
            + "      \"trigger\": \"minecraft:inventory_changed\",\n"
            + "      \"conditions\": {\n"
            + "        \"items\": [\n"
            + "          {\n"
            + "            \"item\": \"minecraft:stone\""
            + "          }\n"
            + "        ]\n"
            + "      }\n"
            + "    }\n"
            + "  }\n"
            + "}";

    private static Logger logger;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        logger = event.getModLog();
        if (ENABLED)
        {
            MinecraftForge.EVENT_BUS.register(AdvancementLoadEventTest.class);
        }
    }

    @SubscribeEvent
    public static void onAdvancementLoadEvent(AdvancementLoadEvent event)
    {
        try
        {
            Advancement.Builder builder = JsonUtils.fromJson(AdvancementManager.GSON, TEST_ADVANCEMENT_JSON, Advancement.Builder.class, false);
            event.addAdvancement(TEST_ADVANCEMENT_NAME, builder);
        }
        catch (JsonParseException jsonparseexception)
        {
            logger.error("Parsing error loading test advancement", jsonparseexception);
        }
    }
}
