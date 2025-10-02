package net.minecraftforge.debug.client;

import net.minecraft.client.Minecraft;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.client.event.AddGuiOverlayLayersEvent;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.gametest.GameTest;
import net.minecraftforge.gametest.GameTestNamespace;
import net.minecraftforge.test.BaseTestMod;

@GameTestNamespace("forge")
@Mod(RenderTooltipTest.MODID)
public class RenderTooltipTest extends BaseTestMod {
    public static final String MODID = "render_tooltip_test";
    private static boolean testMode = false;
    private static ItemStack itemStack = Items.STICK.getDefaultInstance();
    private static ItemStack lastItemstackSeenInEvent = null;
    public RenderTooltipTest(FMLJavaModLoadingContext context) {
        super(context, false, false);
        AddGuiOverlayLayersEvent.getBus(context.getModBusGroup()).addListener(event -> {
           event.getLayeredDraw().addWithCondition(rl("render_tooltip_test"), (gg, dt) -> {
               gg.setTooltipForNextFrame(Minecraft.getInstance().font, itemStack, 50, 50);
           }, () -> testMode) ;
        });

        RenderTooltipEvent.Pre.BUS.addListener(event -> {
           lastItemstackSeenInEvent = event.getItemStack();
        });
    }

    @GameTest
    public static void stack_present_in_pre(GameTestHelper helper) {
        testMode = true;
        helper.runAfterDelay(5, () -> {
            testMode = false;
            helper.assertTrue(lastItemstackSeenInEvent == itemStack, "Itemstack from last tooltip was not correct.");
            helper.succeed();
        });
    }
}
