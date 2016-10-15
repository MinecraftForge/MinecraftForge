package net.minecraftforge.test;

import net.minecraftforge.client.CloudRenderer;
import net.minecraftforge.common.ForgeModContainer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

@Mod(modid="fastcloudsperftest", name="Fast Clouds Performance Test", version="0.0.0", clientSideOnly = true)
public class FastCloudsPerfTest
{
    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        MinecraftForge.EVENT_BUS.register(this);
    }

    private boolean originalSetting;
    private int timer = 0;
    private static final int TESTS_START = 2;
    private static final int TESTS_END = 6;
    private int tests = 0;

    @SubscribeEvent
    public void onWorldTick(TickEvent.ClientTickEvent event)
    {
        if (event.phase == TickEvent.Phase.END)
        {
            if (--timer < 0 && tests < TESTS_END)
            {
                if (tests >= TESTS_START && CloudRenderer.frames > 0)
                {
                    FMLLog.info(
                            (ForgeModContainer.forgeCloudsEnabled ? "Forge clouds took " : "Vanilla clouds took ")
                            + (CloudRenderer.time / 1e+6F / CloudRenderer.frames)
                            + "ms per frame average");
                }
                else if (tests == 0)
                {
                    originalSetting = ForgeModContainer.forgeCloudsEnabled;
                }

                CloudRenderer.time = 0;
                CloudRenderer.frames = 0;
                ForgeModContainer.forgeCloudsEnabled = !ForgeModContainer.forgeCloudsEnabled;
                timer = 200;
                tests++;
            }
            else if (tests == TESTS_END)
            {
                ForgeModContainer.forgeCloudsEnabled = originalSetting;
                FMLLog.info("Switched back to %s",
                        ForgeModContainer.forgeCloudsEnabled ? "Forge clouds" : "vanilla clouds");
                tests++;
            }
        }
    }
}
