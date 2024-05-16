package net.minecraftforge.debug.gameplay.item;

import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.test.BaseTestMod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@GameTestHolder("forge." + LazyCapTest.MOD_ID)
@Mod(LazyCapTest.MOD_ID)
public class LazyCapTest extends BaseTestMod {
    public static final String MOD_ID = "lazy_cap_test";

    public LazyCapTest() {

        
        Items.DIAMOND_AXE.register(s -> {
            return LazyOptional.of(MyEnergyStorage::new);
        }, ForgeCapabilities.ENERGY, Direction.DOWN, Direction.UP);


        var bus = MinecraftForge.EVENT_BUS;
        bus.addListener(this::onTick);
    }


    public static class MyEnergyStorage extends EnergyStorage {
        public MyEnergyStorage() {
            super(10000);
            System.out.println("New Instance created");
        }
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
                        var lo = hand.getCapability(ForgeCapabilities.ENERGY);
                        if (lo.isPresent()) {
                            System.out.println("Cap Energy Is Present for Direction Undefined");
                        } else {
                            System.out.println("Cap Energy Is not Present for Direction Undefined");
                        }

                        var lo2 = hand.getCapability(ForgeCapabilities.ENERGY, Direction.DOWN);
                        if (lo2.isPresent()) {
                            System.out.println("Cap Energy Is Present for Direction DOWN");
                        } else {
                            System.out.println("Cap Energy Is not Present for Direction DOWN");
                        }

                        var lo3 = hand.getCapability(ForgeCapabilities.ENERGY, Direction.UP);
                        if (lo3.isPresent()) {
                            System.out.println("Cap Energy Is Present for Direction UP");
                        } else {
                            System.out.println("Cap Energy Is not Present for Direction UP");
                        }


                        if (lo2.isPresent() && lo3.isPresent()) {
                            lo2.ifPresent(i1 -> {
                                lo3.ifPresent(i2 -> {
                                    if (i1 == i2) {
                                        System.out.println("Both UP and DOWN are same Energy Storage Instance...");
                                    }
                                });
                            });
                        }
                    }
                }
            }
        }
    }
}
