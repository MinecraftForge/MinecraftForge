package net.minecraftforge.debug.entity.player;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.UUID;
/**
 * Tests if the various patches made to utilize values associated with Forge.REACH_DISTANCE works
 * The Longsword adds a modifier to the user's reach distance
 * The goal of these patches are to allow modders to more easily add equipment or potion effects that affect the attack reach of players
 */
@Mod(PlayerAttackReachTest.MODID)
public class PlayerAttackReachTest {

    static final String MODID = "player_attack_reach_test";

    static final float REACH_VALUE = 2.0F; // Change this value for testing, but note that the attribute is capped at 1024.0D

    static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

    static RegistryObject<Item> LONGSWORD = ITEMS.register("longsword", () ->
            new LongswordItem(ItemTier.IRON, 3, -2.4F, REACH_VALUE, (new Item.Properties()).tab(ItemGroup.TAB_COMBAT))
    );

    public PlayerAttackReachTest()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ITEMS.register(modEventBus);
    }

    static class LongswordItem extends SwordItem {
        private final float reach;
        private final Multimap<Attribute, AttributeModifier> defaultModifiers = ArrayListMultimap.create(); // initialize as empty
        protected static final UUID BASE_REACH_DISTANCE_UUID = UUID.fromString("f1797f2f-f02c-44f9-951f-27c9e5446916");

        LongswordItem(IItemTier itemTier, int attackDamageIn, float attackSpeedIn, float reachIn, Properties properties) {
            super(itemTier, attackDamageIn, attackSpeedIn, properties);
            this.reach = reachIn;
        }

        @Override
        public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlotType equipmentSlotType) {
            if(equipmentSlotType == EquipmentSlotType.MAINHAND){
                if(this.defaultModifiers.isEmpty()){
                    Multimap<Attribute, AttributeModifier> oldAttributeModifiers = super.getDefaultAttributeModifiers(equipmentSlotType);
                    this.defaultModifiers.putAll(oldAttributeModifiers);
                    this.defaultModifiers.put(ForgeMod.REACH_DISTANCE.get(), new AttributeModifier(BASE_REACH_DISTANCE_UUID, "Weapon modifier", (double)this.reach, AttributeModifier.Operation.ADDITION));
                }
                return this.defaultModifiers;
            }
            else return super.getDefaultAttributeModifiers(equipmentSlotType);
        }
    }
}
