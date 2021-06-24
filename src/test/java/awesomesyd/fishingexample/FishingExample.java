package awesomesyd.fishingexample;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@Mod("fishing_example")
public class FishingExample{
    public static final String MODID="fishing_example";
    public static final Logger LOGGER = LogManager.getLogger();
    public FishingExample() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        Register.ENCHANTMENTS.register(bus);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)// Event bus for receiving Registry Events)
    public static class Register {
        public static final DeferredRegister<Enchantment> ENCHANTMENTS = DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, MODID);   
        static final EquipmentSlotType[] hands = new EquipmentSlotType[] { EquipmentSlotType.MAINHAND, EquipmentSlotType.OFFHAND };
        public static final RegistryObject<BarbedEnchantment> BARBED = ENCHANTMENTS.register("barbed",
                ()->new BarbedEnchantment(Enchantment.Rarity.COMMON, EnchantmentType.FISHING_ROD, hands));
        public static final RegistryObject<BlockReelEnchantment> BLOCK_REEL = ENCHANTMENTS.register("block_reel",
                ()->new BlockReelEnchantment(Enchantment.Rarity.RARE, EnchantmentType.FISHING_ROD, hands));
        public static final RegistryObject<GrappleEnchantment> GRAPPLE = ENCHANTMENTS.register("grapple",
                ()->new GrappleEnchantment(Enchantment.Rarity.UNCOMMON, EnchantmentType.FISHING_ROD, hands));
        public static final RegistryObject<YoinkEnchantment> YOINK = ENCHANTMENTS.register("yoink",
                ()->new YoinkEnchantment(Enchantment.Rarity.VERY_RARE, EnchantmentType.FISHING_ROD, hands));
        public static final RegistryObject<YankEnchantment> YANK = ENCHANTMENTS.register("yank",
                ()->new YankEnchantment(Enchantment.Rarity.UNCOMMON, EnchantmentType.FISHING_ROD, hands));
        public static final RegistryObject<FishPunchEnchantment> FISH_PUNCH = ENCHANTMENTS.register("fish_punch",
                ()->new FishPunchEnchantment(Enchantment.Rarity.UNCOMMON, EnchantmentType.FISHING_ROD, hands));
        }
}