package net.minecraftforge.debug.client;

import com.mojang.logging.LogUtils;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.gametest.GameTestNamespace;
import net.minecraftforge.test.BaseTestMod;

@GameTestNamespace("forge")
@Mod(RenderTooltipTest.MODID)
public class RenderTooltipTest extends BaseTestMod {
    public static final String MODID = "render_tooltip_test";
    public RenderTooltipTest(FMLJavaModLoadingContext context) {
        super(context, false, false);
        // todo how?
    }
}
