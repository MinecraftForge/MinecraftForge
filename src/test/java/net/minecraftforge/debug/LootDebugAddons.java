package net.minecraftforge.debug;

import com.google.common.base.Charsets;
import com.google.common.base.Function;
import net.minecraft.entity.item.EntityMinecartChest;
import net.minecraft.entity.item.EntityMinecartHopper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.loot.LootRegistry;
import net.minecraftforge.event.entity.minecart.MinecartInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

@Mod(modid = "forgelootdebugaddons")
public class LootDebugAddons {

    private static final String json = "{\"pools\":[{\"rolls\": 1, \"entries\": [{\"type\": \"item\", \"name\": \"minecraft:golden_apple\", \"weight\": 1}]}]}";

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent evt) {
        LootRegistry.registerOverride(new Function<ResourceLocation, InputStream>() {
            @Override
            public InputStream apply(ResourceLocation input) {
                // In a hypothetical situation, we would have addons. And this mod would be able to control how exactly it gets
                // from resourcelocation "forgelootdebugaddons:*" to an InputStream
                return new ByteArrayInputStream(json.getBytes(Charsets.UTF_8));
            }
        });

        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void openHopperCart(MinecartInteractEvent evt) {
        if (!evt.minecart.worldObj.isRemote && evt.minecart instanceof EntityMinecartHopper
                && "LOOTTEST".equals(evt.minecart.getCustomNameTag())) {
            ((EntityMinecartHopper) evt.minecart).setLootTable(new ResourceLocation("forgelootdebugaddons", "handledbymyoverride"), 0);
        }
    }

}
