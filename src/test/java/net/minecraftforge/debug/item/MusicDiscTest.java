package net.minecraftforge.debug.item;

import net.minecraft.item.Item;
import net.minecraft.item.MusicDiscItem;
import net.minecraft.item.Rarity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@Mod(MusicDiscTest.MOD_ID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = MusicDiscTest.MOD_ID)
public class MusicDiscTest
{
    static final String MOD_ID = "music_disc_test";

    private static final DeferredRegister<Item> ITEMS = new DeferredRegister<>(ForgeRegistries.ITEMS, MOD_ID);
    private static final DeferredRegister<SoundEvent> SOUND_EVENTS = new DeferredRegister<>(ForgeRegistries.SOUND_EVENTS, MOD_ID);

    public MusicDiscTest()
    {
        final IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        ITEMS.register(modBus);
        SOUND_EVENTS.register(modBus);
    }

    static
    {
        final RegistryObject<SoundEvent> testEvent = SOUND_EVENTS.register("test_sound_event", () -> new SoundEvent(new ResourceLocation(MOD_ID, "test_sound_event")));
        ITEMS.register("test_music_disc", () -> new MusicDiscItem(1, testEvent, new Item.Properties().maxStackSize(1).rarity(Rarity.EPIC)));
    }
}
