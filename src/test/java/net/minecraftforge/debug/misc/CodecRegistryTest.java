package net.minecraftforge.debug.misc;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("codecregistrytest")
public class CodecRegistryTest
{

    private static final boolean ENABLED = true;
    private static final Logger LOGGER = LogManager.getLogger();
    private Codec<Item> blockItemCodec = null;

    public CodecRegistryTest()
    {
        if(ENABLED)
        {
            FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);
            MinecraftForge.EVENT_BUS.addListener(this::testing);
        }
    }

    public void commonSetup(FMLCommonSetupEvent event)
    {
        blockItemCodec = ForgeRegistries.BLOCKS.flatXmap(block ->
                {
                    Item item = ForgeRegistries.ITEMS.getValue(block.getRegistryName());
                    return item != Items.AIR ?
                            DataResult.success(item) :
                            DataResult.error("No BlockItem for " + block.getRegistryName());
                },
                item -> item instanceof BlockItem ?
                        DataResult.success(((BlockItem) item).getBlock()) :
                        DataResult.error("No Block from item " + item.getRegistryName())
        ).fieldOf("test").codec();
    }

    public void testing(PlayerInteractEvent.RightClickItem event)
    {
        DynamicOps<JsonElement> dynamicOps = event.getWorld().isRemote ? JsonOps.INSTANCE : JsonOps.COMPRESSED;
        blockItemCodec.encodeStart(dynamicOps, event.getItemStack().getItem()).get()
                .ifLeft(json -> LOGGER.info("Encoded " + json.toString()))
                .ifRight(partial -> LOGGER.info("Error message : " + partial.message()));
    }
}
