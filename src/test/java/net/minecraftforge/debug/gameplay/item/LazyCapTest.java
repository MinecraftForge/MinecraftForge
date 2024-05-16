package net.minecraftforge.debug.gameplay.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.test.BaseTestMod;

@GameTestHolder("forge." + LazyCapTest.MOD_ID)
@Mod(LazyCapTest.MOD_ID)
public class LazyCapTest extends BaseTestMod {
    public static final String MOD_ID = "lazy_cap_test";

    public LazyCapTest() {
        Items.DIAMOND_AXE.register(ForgeCapabilities.ENERGY, (s, i) -> {
            return LazyOptional.of(() -> new EnergyStorage(1000) {
                @Override
                public int receiveEnergy(int maxReceive, boolean simulate) {
                    System.out.println("Rec -> " + maxReceive + " for item stack " + i);
                    return super.receiveEnergy(maxReceive, simulate);
                }
            });
        });
        var bus = MinecraftForge.EVENT_BUS;
        bus.addListener(this::onTick);
    }

    public static int ticks = 0;
    public void onTick(TickEvent.PlayerTickEvent event) {
        if (event.side == LogicalSide.SERVER) {
            var plr = event.player;
            if (plr != null) {
                ticks++;
                if (ticks % 5 == 0) {
                var hand = plr.getItemInHand(InteractionHand.MAIN_HAND);
                if (!hand.isEmpty()) {
                    hand.getCapability(ForgeCapabilities.ENERGY).ifPresent(e -> {
                        e.receiveEnergy(10, false); // Call the function!
                    });
                }
                }
            }
        }
    }
}
