package net.minecraftforge.debug;

import net.minecraft.client.Minecraft;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;

@Mod("safe_referent_test")
public class SafeReferentTest
{

    private static final boolean ENABLED = true;

    public SafeReferentTest()
    {
        if (ENABLED) DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> ClientOnlyClass::new);
    }

    public static class ClientOnlyClass
    {
        public Level getClientLevel()
        {
            return Minecraft.getInstance().level;
        }
    }
}
